package co.gov.ideam.dhime.comparador.engine;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import org.junit.Test;

import com.aquaticinformatics.aquarius.sdk.timeseries.AquariusClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.AppendResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.PostTimeSeriesAppend;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.TimeSeriesAppendStatus;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.TimeSeriesPoint;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import net.servicestack.client.WebServiceException;

public class PruebaUnitariaIngresoInfo {
	
	
	@Test
	public void timeSeriesPointAppender() throws WebServiceException, Exception{
		String uniqueId = "34eff396859c4a5f9c87c14f26f18d1d";	
		try (AquariusClient cliente= AquariusClient.createConnectedClient("http://172.16.50.54/", "AppExternos", "dh1m31nt3r0p*")) {
		
			
			ArrayList<TimeSeriesPoint> points = new ArrayList<>();
		    points.add(new TimeSeriesPoint()
		            .setTime(Instant.parse("2018-01-11T00:00:00Z"))
		            .setValue(77.0));
		    points.add(new TimeSeriesPoint()
		            .setTime(Instant.parse("2018-01-12T00:00:00Z"))
		            .setValue(77.0));		    
		    points.add(new TimeSeriesPoint()
		            .setTime(Instant.parse("2018-01-13T00:00:00Z"))
		            .setValue(77.0));		    				
			
			PostTimeSeriesAppend append=new PostTimeSeriesAppend();
			append.setPoints(points);
			append.setUniqueId(uniqueId);
			AppendResponse appendResponse = cliente.Acquisition.post(append);		
			
		}catch (WebServiceException e) {
			System.out.println("mensaje:"+e.getErrorMessage()+" code:"+e.getErrorCode());
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
		
	}

}
