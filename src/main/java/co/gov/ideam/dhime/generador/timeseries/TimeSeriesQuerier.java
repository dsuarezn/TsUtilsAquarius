package co.gov.ideam.dhime.generador.timeseries;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aquaticinformatics.aquarius.sdk.timeseries.AquariusClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.Parameter;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataCorrectedServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataRawServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import co.gov.ideam.dhime.utils.CorrectionDateRange;
import co.gov.ideam.dhime.utils.DateTimeUtilsComponent;
import co.gov.ideam.dhime.utils.LogUtilsComponent;
import net.servicestack.client.WebServiceException;
import net.servicestack.func.Func;

@Component
public class TimeSeriesQuerier {
	
	@Autowired
	private LogUtilsComponent logUtilsComponent; 
	
	private Log logger=null;
	
	@Value("${aquarius.path}")
	public String AQUARIUS_PATH;
	
	@Value("${aquarius.user}")
	public String AQUARIUS_USER;
	
	@Value("${aquarius.password}")
	public String AQUARIUS_PASSWORD;
	
	
	
	private Map<String,List<TimeSeriesDescription>> listaSeriesPorEstacion;
	
	public TimeSeriesQuerier() {	
		
		listaSeriesPorEstacion=new HashMap<>();
		

	}
	
//	@PostConstruct
//	public void querierInit(){
//		System.out.println(AQUARIUS_PATH+"-"+AQUARIUS_USER+"-"+AQUARIUS_PASSWORD);
//		try {
//			 this.publishClient= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD);
//			 logger=logUtilsComponent.getLogger(this.getClass());
//			 listaSeriesPorEstacion=new HashMap<>();
//		}catch (WebServiceException e) {
//		    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
//		} catch (Exception e) {
//		    System.out.format("ERROR: That was weird! %s", e.getMessage());
//		    e.printStackTrace();
//		}
//	}
	
	private SerieDeTiempo consultarSerieTiempoTs(String codEstacion, String variable, String tipoData, String parametro, Date inicio, Date Fin) throws WebServiceException, Exception{
			
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {

			TimeSeriesDescriptionServiceRequest tedRequest = new TimeSeriesDescriptionServiceRequest();
			tedRequest.setLocationIdentifier(codEstacion);		
			TimeSeriesDescriptionListServiceResponse tedResp=cliente.Publish.get(tedRequest);
			
			String composicion=parametro+"."+variable+"@"+codEstacion;
			List<TimeSeriesDescription> listaDesc= tedResp.getTimeSeriesDescriptions().stream().filter(item->item.getIdentifier().contains(composicion)).collect(Collectors.toList());
			SerieDeTiempo serie=new SerieDeTiempo(codEstacion, variable, 0, null);
			if(listaDesc.size()>0){
				TimeSeriesDataRawServiceRequest dataRequest = new TimeSeriesDataRawServiceRequest();			
				dataRequest.setTimeSeriesUniqueId(listaDesc.get(0).getUniqueId());
				dataRequest.setQueryFrom(inicio.toInstant());
				dataRequest.setQueryTo(inicio.toInstant());
				TimeSeriesDataServiceResponse dataResp=cliente.Publish.get(dataRequest);
				List<PuntoSerieDeTiempo> listaPuntos=new ArrayList<>();
				for (TimeSeriesPoint puntoTs : dataResp.getPoints()) {
					PuntoSerieDeTiempo punto=new PuntoSerieDeTiempo();
					punto.setDato(puntoTs.getValue().getNumeric());
					punto.setTimestamp(DateTimeUtilsComponent.parseTsDateOffsetToCurrentTimeZone(puntoTs.getTimestamp().getDateTimeOffset().toString()));						
					listaPuntos.add(punto);
				}
				serie.setListaPuntos(listaPuntos);			
			}
			return serie;
			
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
		
	}
	
	
	
	
	public String obtenerTimeSeriesUniqueId(String variable, String estacionId) throws WebServiceException, Exception{				
					
			try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
				if(listaSeriesPorEstacion.get(estacionId)==null){
					TimeSeriesDescriptionServiceRequest tedRequest = new TimeSeriesDescriptionServiceRequest();
					tedRequest.setLocationIdentifier(estacionId);				
					TimeSeriesDescriptionListServiceResponse tedResp=cliente.Publish.get(tedRequest);
					List<TimeSeriesDescription> listaDesc= tedResp.getTimeSeriesDescriptions();
					listaSeriesPorEstacion.put(estacionId, listaDesc);
					String composicion=variable+"@"+estacionId;				
					List<TimeSeriesDescription> listaFiltrada= listaDesc.stream().filter(item->item.getIdentifier().contains(composicion)).collect(Collectors.toList());
					if(listaFiltrada.size()>0){
						return listaFiltrada.get(0).getUniqueId();
					}
				}
				else{
					String composicion=variable+"@"+estacionId;	
					List<TimeSeriesDescription> listaFiltrada=listaSeriesPorEstacion.get(estacionId).stream().filter(item->item.getIdentifier().contains(composicion)).collect(Collectors.toList());
					if(listaFiltrada.size()>0){
						return listaFiltrada.get(0).getUniqueId();
					}
				}
				return null;
				
			}catch (WebServiceException e) {
				throw e;		    
			} catch (Exception ex) {
				throw ex;		    
			}
	}
	
	
	public static String obtenerTimeSeriesUniqueId(AquariusClient cliente, String variable, String estacionId) throws WebServiceException, Exception{				
				
				TimeSeriesDescriptionServiceRequest tedRequest = new TimeSeriesDescriptionServiceRequest();
				tedRequest.setLocationIdentifier(estacionId);				
				TimeSeriesDescriptionListServiceResponse tedResp=cliente.Publish.get(tedRequest);
				List<TimeSeriesDescription> listaDesc= tedResp.getTimeSeriesDescriptions();				
				String composicion=variable+"@"+estacionId;				
				List<TimeSeriesDescription> listaFiltrada= listaDesc.stream().filter(item->item.getIdentifier().contains(composicion)).collect(Collectors.toList());
				if(listaFiltrada.size()>0){
					return listaFiltrada.get(0).getUniqueId();
				}

			return null;
			
		
}
	
	public String obtenerTimeSeriesUniqueIdFullDeletes(String variable, String estacionId) throws WebServiceException, Exception{				
		
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
			TimeSeriesDescriptionServiceRequest tedRequest = new TimeSeriesDescriptionServiceRequest();
			tedRequest.setLocationIdentifier(estacionId);				
			TimeSeriesDescriptionListServiceResponse tedResp=cliente.Publish.get(tedRequest);
			List<TimeSeriesDescription> listaDesc= tedResp.getTimeSeriesDescriptions();
			String composicion=variable+"@"+estacionId;				
			List<TimeSeriesDescription> listaFiltrada= listaDesc.stream().filter(item->item.getIdentifier().contains(composicion)).collect(Collectors.toList());
			if(listaFiltrada.size()>0){
				return listaFiltrada.get(0).getUniqueId();
			}			
			return null;
			
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
}
	
	public ArrayList<LocationDescription> obtenerListaLocations(String locationId, String locationFolderFilter)throws WebServiceException, Exception{
		
		System.out.println(AQUARIUS_PATH+"-"+AQUARIUS_USER+"-"+AQUARIUS_PASSWORD);
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
			List<String> locationIdentifiers=new ArrayList<>();
			LocationDescriptionListServiceRequest locatdescReq = new LocationDescriptionListServiceRequest();	
			if(locationFolderFilter!=null && !locationFolderFilter.isEmpty() ){
				locatdescReq.setLocationFolder("*"+locationFolderFilter+"*");
			}
			if(locationId!=null && !locationId.isEmpty() ){
				locatdescReq.setLocationIdentifier("*"+locationId+"*");
			}
			LocationDescriptionListServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
			return locatdescResp.getLocationDescriptions();	
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}			
	}
	
public ArrayList<LocationDescription> obtenerListaLocations(String locationFolderFilter)throws WebServiceException, Exception{
		
		System.out.println(AQUARIUS_PATH+"-"+AQUARIUS_USER+"-"+AQUARIUS_PASSWORD);
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
			List<String> locationIdentifiers=new ArrayList<>();
			LocationDescriptionListServiceRequest locatdescReq = new LocationDescriptionListServiceRequest();	
			if(locationFolderFilter!=null && !locationFolderFilter.isEmpty() ){
				locatdescReq.setLocationFolder("*"+locationFolderFilter+"*");
			}			
			LocationDescriptionListServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
			return locatdescResp.getLocationDescriptions();	
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}			
	}


	public ArrayList<TimeSeriesDescription> queryTimeSeriesDescriptionListFilterByLocationId(String locationId)throws WebServiceException, Exception{						
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
			TimeSeriesDescriptionServiceRequest tedRequest = new TimeSeriesDescriptionServiceRequest();
			tedRequest.setLocationIdentifier(locationId);
			TimeSeriesDescriptionListServiceResponse tedResp=cliente.Publish.get(tedRequest);
			return tedResp.getTimeSeriesDescriptions();		
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
	}
	
	public ArrayList<TimeSeriesDescription> queryTimeSeriesDescriptionListFilter(String locationId, String parameter)throws WebServiceException, Exception{						
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
			TimeSeriesDescriptionServiceRequest tedRequest = new TimeSeriesDescriptionServiceRequest();
			tedRequest.setLocationIdentifier(locationId);
			tedRequest.setParameter(parameter);
			TimeSeriesDescriptionListServiceResponse tedResp=cliente.Publish.get(tedRequest);
			return tedResp.getTimeSeriesDescriptions();		
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
	}
	
	public TimeSeriesDataServiceResponse obtenerTimeSeriesRawData(String uniqueId, Date initDate, Date finDate)throws WebServiceException, Exception{	
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
			TimeSeriesDataRawServiceRequest dataRequest = new TimeSeriesDataRawServiceRequest();
			dataRequest.setTimeSeriesUniqueId(uniqueId);			
			dataRequest.setQueryFrom(initDate.toInstant());
			dataRequest.setQueryTo(finDate.toInstant());
			return cliente.Publish.get(dataRequest);	
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
	}
	
	public TimeSeriesDataServiceResponse obtenerTimeSeriesCorrectedData(String uniqueId, Date initDate, Date finDate)throws WebServiceException, Exception{	
		try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
			TimeSeriesDataCorrectedServiceRequest dataRequest = new TimeSeriesDataCorrectedServiceRequest();
			dataRequest.setTimeSeriesUniqueId(uniqueId);
			if(initDate!=null){
				dataRequest.setQueryFrom(initDate.toInstant());
			}
			if(finDate!=null){
				dataRequest.setQueryTo(finDate.toInstant());				
			}
			return cliente.Publish.get(dataRequest);	
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
	}
	
	public List<CorrectionDateRange> obtenerListasDeCorrecion(String uniqueId, Date initDate, Date finDate) throws WebServiceException, Exception{    	    
    	try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
    		CorrectionListServiceRequest listasCorregidos=new CorrectionListServiceRequest();
//        	String id="TEMPERATURA.SOPORTE - TMN_CON@26225030";
//        	String uid="34eff396859c4a5f9c87c14f26f18d1d";
        	listasCorregidos.setTimeSeriesUniqueId(uniqueId);
//        	listasCorregidos.setQueryFrom(initDate.toInstant());
//        	listasCorregidos.setQueryTo(finDate.toInstant());
        	CorrectionListServiceResponse respuesta=cliente.Publish.get(listasCorregidos);
        	return obtenerListaCorrectionDateRange(respuesta.getCorrections());
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
    }
    
    private List<CorrectionDateRange> obtenerListaCorrectionDateRange(List<Correction> correciones){
    	List<CorrectionDateRange> listaRangosCorr=new ArrayList<>();
    	for (Correction correccionts : correciones) {
    		CorrectionDateRange correcion=new CorrectionDateRange();
    		correcion.setComments(correccionts.Comment);
    		correcion.setFechaInicio(Date.from(correccionts.getStartTime()));
    		correcion.setFechaFin(Date.from(correccionts.getEndTime()));
    		correcion.setUser(correccionts.getUser());
    		listaRangosCorr.add(correcion);					
		}    	
    	return listaRangosCorr;
    }
    
    public String obtenerUniqueIdLocation(String locationIdentifier) throws Exception
    {
    	try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
    		return Func.first(
                    cliente.Publish
                        .get(new LocationDescriptionListServiceRequest().setLocationIdentifier(locationIdentifier))
                        .LocationDescriptions,
                    l -> l.Identifier.equals(locationIdentifier))
            .UniqueId;
		}catch (WebServiceException e) {
			throw e;		    
		} catch (Exception ex) {
			throw ex;		    
		}
    	
        
    }
   
    
//    public Parameter obtenerParameter(String paramString) throws Exception
//    {
//    	try (AquariusClient cliente= AquariusClient.createConnectedClient(AQUARIUS_PATH, AQUARIUS_USER, AQUARIUS_PASSWORD)) {
//    		
//    		return Func.first(
//                    cliente.Provisioning.get(new GetParameters()).Results,
//                    p -> p.Identifier.equals(parameterIdentifier));
//    		
//		}catch (WebServiceException e) {
//			throw e;		    
//		} catch (Exception ex) {
//			throw ex;		    
//		}
//    	
//        
//    }
    
   
	
	
}
