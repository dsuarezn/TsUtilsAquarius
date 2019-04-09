package co.gov.ideam.dhime.generador.timeseries;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.validation.Path.CrossParameterNode;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aquaticinformatics.aquarius.sdk.timeseries.AquariusClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.AppendResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.AppendStatusCode;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.GetTimeSeriesAppendStatus;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.PostReflectedTimeSeries;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.PostTimeSeriesAppend;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.PostTimeSeriesOverwriteAppend;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.ReflectedTimeSeriesPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.TimeSeriesAppendStatus;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.TimeSeriesPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Interval;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataRawServiceRequest;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import co.gov.ideam.dhime.utils.LogUtilsComponent;
import net.servicestack.client.WebServiceException;

@Component
public class TimeSeriesUpdater {
	
	
	@Value("${aquarius.path}")
	public String AQUARIUS_PATH;
	
	@Value("${aquarius.user}")
	public String AQUARIUS_USER;
	
	@Value("${aquarius.password}")
	public String AQUARIUS_PASSWORD;
	
	@Autowired
	private LogUtilsComponent logUtilsComponent;
	
	@Autowired
	private TimeSeriesQuerier timeSeriesQuerier;
	
	private Log logger=null;
	
	public TimeSeriesUpdater() {}
	
//	@PostConstruct
//	public void updaterInit() {
//		System.out.println(AQUARIUS_PATH+"-"+AQUARIUS_USER+"-"+AQUARIUS_PASSWORD);
//		try (AquariusClient cli = AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
//			 this.adquisitionClient = cli;	
//			 logger=logUtilsComponent.getLogger(this.getClass());
//		}catch (WebServiceException e) {
//		    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
//		} catch (Exception e) {
//		    System.out.format("ERROR: That was weird! %s", e.getMessage());
//		    e.printStackTrace();
//		}
//	}
	
	public void timeSeriesPointAppender(SerieDeTiempo serie, Boolean waitPolling) throws WebServiceException, Exception{
		String uniqueId = timeSeriesQuerier.obtenerTimeSeriesUniqueId(serie.getEtiqueta(), serie.getEstacion());	
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
		
			
			ArrayList<TimeSeriesPoint> tspoints = new ArrayList<>();
			
			for (PuntoSerieDeTiempo puntoSdT : serie.getListaPuntos()) {
				TimeSeriesPoint puntots=new TimeSeriesPoint();
				puntots.setTime(puntoSdT.getTimestamp().toInstant());
				puntots.setValue(puntoSdT.getDato());
				tspoints.add(puntots);
			}
			PostTimeSeriesAppend append=new PostTimeSeriesAppend();
			append.setPoints(tspoints);
			append.setUniqueId(uniqueId);
			AppendResponse appendResponse = cliente.Acquisition.post(append);		
			
			if(waitPolling){
			    Instant pollStart = Instant.now();
			    TimeSeriesAppendStatus pollResponse = pollUntilComplete(appendResponse.AppendRequestIdentifier);
			    Duration elapsed = Duration.between(pollStart, Instant.now());
			    System.out.format("Appended %d points (deleting %d points) in %s\n", pollResponse.NumberOfPointsAppended, 
			    		pollResponse.NumberOfPointsDeleted, elapsed.toString());
			}
		}catch (WebServiceException e) {
			System.out.println("mensaje:"+e.getErrorMessage()+" code:"+e.getErrorCode());
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}		
	}
	
	public void timeSeriesIntervalEraser(SerieDeTiempo serie) throws WebServiceException, Exception{
		String uniqueId = timeSeriesQuerier.obtenerTimeSeriesUniqueIdFullDeletes(serie.getEtiqueta(), serie.getEstacion());
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
			if(uniqueId!=null){
				PostTimeSeriesOverwriteAppend append=new PostTimeSeriesOverwriteAppend();						
				append.setPoints(null);
				append.setUniqueId(uniqueId);
				Interval intervalo=new Interval();
				intervalo.Start=serie.getRangoInicio().toInstant().minus(1, ChronoUnit.MINUTES);
				intervalo.End=serie.getRangoFin().toInstant().plus(1, ChronoUnit.MINUTES);
				append.setTimeRange(intervalo);
				AppendResponse appendResponse = cliente.Acquisition.post(append);	
			}	
			else{
				System.out.println("No existe la serie "+serie.getEtiqueta()+" para la estación: "+serie.getEstacion());
			}
		}catch (WebServiceException e) {
			e.printStackTrace();
			throw e;		    
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;		    
		}	
}
	
	
	public void timeSeriesPointEraser(SerieDeTiempo serie) throws WebServiceException, Exception{
		String uniqueId = timeSeriesQuerier.obtenerTimeSeriesUniqueId(serie.getEtiqueta(), serie.getEstacion());
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {				
			if(serie.getListaPuntos()!=null){
				for (PuntoSerieDeTiempo puntoSdT : serie.getListaPuntos()) {
					ArrayList<TimeSeriesPoint> tspoints = new ArrayList<>();
					TimeSeriesPoint puntots=new TimeSeriesPoint();
					puntots.setTime(puntoSdT.getTimestamp().toInstant());
					puntots.setValue(null);
					tspoints.add(puntots);			
					PostTimeSeriesOverwriteAppend append=new PostTimeSeriesOverwriteAppend();						
					append.setPoints(tspoints);
					append.setUniqueId(uniqueId);
					Interval intervalo=new Interval();
					intervalo.Start=puntots.getTime().minus(1, ChronoUnit.MINUTES);
					intervalo.End=puntots.getTime().plus(1, ChronoUnit.MINUTES);
					append.setTimeRange(intervalo);
					AppendResponse appendResponse = cliente.Acquisition.post(append);
				}														
			}
		}catch (WebServiceException e) {
			e.printStackTrace();
			throw e;		    
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;		    
		}	
}
	
	
	public void timeSeriesPointUpdater(SerieDeTiempo serie) throws WebServiceException, Exception{
			String uniqueId = timeSeriesQuerier.obtenerTimeSeriesUniqueId(serie.getEtiqueta(), serie.getEstacion());
			try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {				
				if(serie.getListaPuntos()!=null){
					for (PuntoSerieDeTiempo puntoSdT : serie.getListaPuntos()) {
						ArrayList<TimeSeriesPoint> tspoints = new ArrayList<>();
						TimeSeriesPoint puntots=new TimeSeriesPoint();
						puntots.setTime(puntoSdT.getTimestamp().toInstant());
						puntots.setValue(puntoSdT.getDato());
						tspoints.add(puntots);
						
						PostTimeSeriesOverwriteAppend append=new PostTimeSeriesOverwriteAppend();						
						append.setPoints(tspoints);
						append.setUniqueId(uniqueId);
						Interval intervalo=new Interval();
						intervalo.Start=puntots.getTime().minus(1, ChronoUnit.MINUTES);
						intervalo.End=puntots.getTime().plus(1, ChronoUnit.MINUTES);
						append.setTimeRange(intervalo);
						AppendResponse appendResponse = cliente.Acquisition.post(append);
					}														
				}
			}catch (WebServiceException e) {
				e.printStackTrace();
				throw e;		    
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;		    
			}	
	}
	
	
	public void timeSeriesPointUpdaterReflected(SerieDeTiempo serie) throws WebServiceException, Exception{
		String uniqueId = timeSeriesQuerier.obtenerTimeSeriesUniqueId(serie.getEtiqueta(), serie.getEstacion());
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {				
			if(serie.getListaPuntos()!=null){
				for (PuntoSerieDeTiempo puntoSdT : serie.getListaPuntos()) {
					ArrayList<ReflectedTimeSeriesPoint> tspoints = new ArrayList<>();
					ReflectedTimeSeriesPoint puntots=new ReflectedTimeSeriesPoint();
					puntots.setTime(puntoSdT.getTimestamp().toInstant());
					puntots.setValue(puntoSdT.getDato());
					tspoints.add(puntots);
					
					PostReflectedTimeSeries append=new PostReflectedTimeSeries();						
					append.setPoints(tspoints);
					append.setUniqueId(uniqueId);
					Interval intervalo=new Interval();
					intervalo.Start=puntots.getTime().minus(1, ChronoUnit.MINUTES);
					intervalo.End=puntots.getTime().plus(1, ChronoUnit.MINUTES);
					append.setTimeRange(intervalo);
					AppendResponse appendResponse = cliente.Acquisition.post(append);
				}														
			}
		}catch (WebServiceException e) {
			e.printStackTrace();
			throw e;		    
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;		    
		}	
	}
	
	private TimeSeriesAppendStatus pollUntilComplete(String appendRequestIdentifier) throws WebServiceException, Exception {
	    	    	   
	    try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {

	    	TimeSeriesAppendStatus pollResponse = null;

		    // Start polling AQTS quickly
		    int pollDelayMilliseconds = 50;

		    while(true) {
		    	GetTimeSeriesAppendStatus status=new GetTimeSeriesAppendStatus();
		    	status.setAppendRequestIdentifier(appendRequestIdentifier);
		        pollResponse = cliente.Acquisition.get(status);

		        if (pollResponse.AppendStatus != AppendStatusCode.Pending)
		            break;

		        Thread.sleep(pollDelayMilliseconds);
		        pollDelayMilliseconds = Math.min(pollDelayMilliseconds * 2, 20_000);
		    }

		    if (pollResponse.AppendStatus != AppendStatusCode.Completed)
		        throw new UnsupportedOperationException("Unexpected append status=" + pollResponse.AppendStatus);

		    return pollResponse;
	    	
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
	    
	}
	
	
	
}
