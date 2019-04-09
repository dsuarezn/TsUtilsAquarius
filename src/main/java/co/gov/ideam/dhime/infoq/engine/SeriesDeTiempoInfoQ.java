package co.gov.ideam.dhime.infoq.engine;

import static co.gov.ideam.dhime.modificador.series.EjecucionesLocationsMethodsModif.listaEstacionEtiquetaBshgConFaltantes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.aquaticinformatics.aquarius.sdk.timeseries.AquariusClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;

import co.gov.ideam.dhime.comparador.webapp.dto.EstacionEtiqueta;
import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import co.gov.ideam.dhime.utils.DateTimeUtilsComponent;
import co.gov.ideam.dhime.utils.FileUtilsComponent;
import net.servicestack.client.WebServiceException;
import static co.gov.ideam.dhime.modificador.series.IdentificadoresSeries.listaIdentificadoresTssmConAll;

import static co.gov.ideam.dhime.utils.FileUtilsComponent.crearArchivoList;

public class SeriesDeTiempoInfoQ {
	
	@Test
	public void creacion(){		
		try (AquariusClient cli = AquariusClient.createConnectedClient("http://172.16.50.54/", "AppExternos", "dh1m31nt3r0p*")) {		
				System.out.println("Se conecta y inicia creacion series");
				List<String> correciones=new ArrayList<>();
				for (EstacionEtiqueta idEtq : listaIdentificadoresTssmConAll()) {
					System.out.println("Estación:"+idEtq.getEstacion());
					CorrectionListServiceRequest locatdescReq = new CorrectionListServiceRequest();
					locatdescReq.setTimeSeriesUniqueId(idEtq.getIdentificador());
					CorrectionListServiceResponse locatdescResp=cli.Publish.get(locatdescReq);
					if(locatdescResp!=null){						
						try {
							List<Correction> listaCorreciones=locatdescResp.getCorrections();							
							correciones.addAll(getInfoCorrection(listaCorreciones, idEtq.getEstacion(), idEtq.getEtiqueta()));							
						} catch (WebServiceException e) {
						    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
						} catch (Exception e) {
						    System.out.format("ERROR: That was weird! %s", e.getMessage());
						    e.printStackTrace();
						}
					}
				}				
				crearArchivoList("CORRECIONES","TSSM_CON",correciones);
				System.out.println("Termina creacion series");
			}catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} catch (Exception e) {
			    System.out.format("ERROR: That was weird! %s", e.getMessage());
			    e.printStackTrace();
			}					
	}
	
	private List<String> getInfoCorrection(List<Correction> listaCorreccion, String estacion, String variable){
		List<String> lista=new ArrayList<String>();		
		for (Correction correction : listaCorreccion) {
			String valor=DateTimeUtilsComponent.formatDateOffset(correction.getStartTime())+";"+DateTimeUtilsComponent.formatDateOffset(correction.getEndTime())+";"+correction.getType().name()+";"+estacion+";"+variable+";"+correction.getUser();
			lista.add(valor);
		}
		return lista;
	}
	

}
