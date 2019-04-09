package co.gov.ideam.dhime.comparador.engine;


import static co.gov.ideam.dhime.comparador.engine.EjecucionesLocationsMethods.*;

import static co.gov.ideam.dhime.utils.SerieDeTiempoUtils.obtenerMapSerieTiempo;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import co.gov.ideam.dhime.comparador.webapp.dto.comparer.ComparerDto;
import co.gov.ideam.dhime.comparador.webapp.dto.comparer.DateExistComparer;
import co.gov.ideam.dhime.comparador.webapp.dto.comparer.DateNotExistComparer19Swap;
import co.gov.ideam.dhime.comparador.webapp.dto.comparer.DateNotExistComparerModificado19Swap;
import co.gov.ideam.dhime.comparador.webapp.dto.comparer.DateNotExistComparerModificadoLocal19Swap;
import co.gov.ideam.dhime.comparador.webapp.dto.comparer.DateNotExistComparerModificadoLocalVVagCon;
import co.gov.ideam.dhime.comparador.webapp.dto.comparer.DateNotExistComparerModificadoLocalDvagCon;
import co.gov.ideam.dhime.comparador.webapp.dto.comparer.ReglaBrilloSolar;
import co.gov.ideam.dhime.generador.localgenerator.DhimeQuerier;
import co.gov.ideam.dhime.generador.localgenerator.LocalSeriesQuerier;
import co.gov.ideam.dhime.generador.model.dhime.SeriesTiempoValores;
import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import co.gov.ideam.dhime.generador.timeseries.TimeSeriesQuerier;
import co.gov.ideam.dhime.utils.CorrectionDateRange;
import co.gov.ideam.dhime.utils.DateTimeUtilsComponent;
import co.gov.ideam.dhime.utils.FileUtilsComponent;
import net.servicestack.client.WebServiceException;

@Component
public class SeriesDeTiempoComparer {
	
	@Value("${comparation.folder}")
	public String CARPETA_COMPARACION;

	@Autowired
	private TimeSeriesQuerier timeSeriesQuerier;
	
	@Autowired
	private LocalSeriesQuerier localSeriesQuerier;
	
	@Autowired
	private FileUtilsComponent fileUtilsComponent;
	
	@Autowired
	private DhimeQuerier dhimeQuerier;
	
	private List<PuntoSerieDeTiempo> seriesDeTiempoComparer(SerieDeTiempo serieTimeSeries, SerieDeTiempo serieLocal) throws Exception{
		List<PuntoSerieDeTiempo> buffer=new ArrayList<>();
		System.out.println("PuntosSerieLocal:"+serieLocal.getListaPuntos().size());
		System.out.println("PuntosSerieTimeSeries:"+serieTimeSeries.getListaPuntos().size());
		Integer count=0;
		for (PuntoSerieDeTiempo punto : serieLocal.getListaPuntos()) {
			System.out.println("procesando:"+count++);
			//Busca el punto de la serie local en la de aquarius
			PuntoSerieDeTiempo puntots=null;
			List<PuntoSerieDeTiempo> listMatch=serieTimeSeries.getListaPuntos().stream().filter(t->punto.getFormatedTimeStamp().equals(t.getFormatedTimeStamp())).collect(Collectors.toList());
			if(listMatch.size()>1){
				throw new Exception();
			}
			else if(listMatch.size()==1){
				//Si el punto existe pero son diferentes, agrega el punto actual para actualizacion
				puntots=listMatch.get(0);
				if(!puntots.getDato().equals(punto.getDato())){
					PuntoSerieDeTiempo puntoDiferente=new PuntoSerieDeTiempo();
					puntoDiferente.setDiffTipoPunto("UPDATE");
					puntoDiferente.setDiffDatoPunto(punto.getDato());
					puntoDiferente.setTimestamp(punto.getTimestamp());
					puntoDiferente.setStrTimestamp(punto.getFormatedTimeStamp());
					buffer.add(puntoDiferente);									
				}
			}
			else if(listMatch.size()<1){
				//Si el punto no existe en la serie aquarius agrega el punto para insertarlo
					PuntoSerieDeTiempo puntoDiferente=new PuntoSerieDeTiempo();
					puntoDiferente.setDiffTipoPunto("APPEND");
					puntoDiferente.setDiffDatoPunto(punto.getDato());
					puntoDiferente.setTimestamp(punto.getTimestamp());
					puntoDiferente.setStrTimestamp(punto.getFormatedTimeStamp());
					buffer.add(puntoDiferente);																							
			}
		}
		return buffer;
	}
	
	private List<PuntoSerieDeTiempo> seriesDeTiempoMapComparer(SerieDeTiempo serieLocal,SerieDeTiempo serieTiempoTsMapa, SerieDeTiempo serieDhime,List<CorrectionDateRange> listaCorreciones) throws Exception{
		List<PuntoSerieDeTiempo> buffer=new ArrayList<>();
		System.out.println("PuntosSerieLocal:"+serieLocal.getListaPuntos().size());
		System.out.println("PuntosSerieTimeSeries:"+serieTiempoTsMapa.getMapaPuntos().size());
		Integer count=0;
		for (PuntoSerieDeTiempo punto : serieLocal.getListaPuntos()) {			
			//Busca el punto de la serie local en la de aquarius
			PuntoSerieDeTiempo puntots=null;
			String formatedts=punto.getFormatedTimeStamp();
			Double valorPunto=serieTiempoTsMapa.getMapaPuntos().get(formatedts);
			//Si el punto no esta en la serie de correciones 		
//			List<PuntoSerieDeTiempo> listMatch=serieTimeSeries.getListaPuntos().stream().filter(t->punto.getFormatedTimeStamp().equals(t.getFormatedTimeStamp())).collect(Collectors.toList());
			if(valorPunto!=null){
				//Si el punto existe pero son diferentes, agrega el punto actual para actualizacion
				puntots=new PuntoSerieDeTiempo();
				if(!valorPunto.equals(punto.getDato())){					
					//Si el punto esta dentro de las fechas de correccion o es un punto dhime
					//entonces no lo agrega
					List<CorrectionDateRange> puntosCorrecion=listaCorreciones.stream().filter(t->t.inDateRange(punto.getTimestamp())==true).collect(Collectors.toList());
					Double valorSerieDhime=serieDhime.getMapaPuntos().get(formatedts);
					if(puntosCorrecion.size()==0 && valorSerieDhime==null){
						PuntoSerieDeTiempo puntoDiferente=new PuntoSerieDeTiempo();
						puntoDiferente.setDiffTipoPunto("UPDATE");
						puntoDiferente.setDiffDatoPunto(punto.getDato());
						puntoDiferente.setTimestamp(punto.getTimestamp());
						puntoDiferente.setStrTimestamp(punto.getFormatedTimeStamp());
						buffer.add(puntoDiferente);									
					}					
				}
			}
			else {
				//Si el punto no existe en la serie aquarius agrega el punto para insertarlo
					PuntoSerieDeTiempo puntoDiferente=new PuntoSerieDeTiempo();
					puntoDiferente.setDiffTipoPunto("APPEND");
					puntoDiferente.setDiffDatoPunto(punto.getDato());
					puntoDiferente.setTimestamp(punto.getTimestamp());
					puntoDiferente.setStrTimestamp(punto.getFormatedTimeStamp());
					buffer.add(puntoDiferente);																							
			}
		}
		return buffer;
	}
	
//	public void comparadorSeriesTiempo(List<String> locationFilter, List<String> variableFilter, String folderContainFilter)throws WebServiceException, Exception{
//		List<LocationDescription> listaEstaciones=timeSeriesQuerier.obtenerListaLocations(folderContainFilter);
//		for (LocationDescription locationDescription : listaEstaciones) {			
//			
//				if(locationFilter.size()>0){
//					if(!locationFilter.contains(locationDescription.getIdentifier())){
//						continue;
//					}
//				}
//				
//				
//				List<TimeSeriesDescription> listaSeriesUbicacion=timeSeriesQuerier.queryTimeSeriesDescriptionListFilterByLocationId(locationDescription.getIdentifier());				
//				for (TimeSeriesDescription tsdescription : listaSeriesUbicacion) {
//					String variable=tsdescription.getIdentifier().split("@")[0].split("\\.")[1];
//					if(variableFilter.size()>0){
//						if(!variableFilter.contains(variable)){
//							continue;
//						}
//					}					
//					try {
//						SerieDeTiempo serieIdeam=localSeriesQuerier.getIdeamLocalTimeSeries(tsdescription.getLocationIdentifier(), variable);
//						String serieUniqueId=tsdescription.getUniqueId();
//						System.out.println(serieUniqueId);
//						TimeSeriesDataServiceResponse serieTs=null;
//						if(serieIdeam.getListaPuntos().size()>0){
//							Date initDate=serieIdeam.getListaPuntos().get(0).getTimestamp();
//							Date finDate=serieIdeam.getListaPuntos().get(serieIdeam.getListaPuntos().size()-1).getTimestamp();
//							System.out.println("Fecha Inicio:"+DateTimeUtilsComponent.formatDateToCompareFormat(initDate));
//							System.out.println("Fecha fin   :"+DateTimeUtilsComponent.formatDateToCompareFormat(finDate));
//							serieTs=timeSeriesQuerier.obtenerTimeSeriesCorrectedData(serieUniqueId, initDate, finDate);
//							List<SeriesTiempoValores> serieDhime=dhimeQuerier.obtenerListaValoresDhime(variable, Long.parseLong(locationDescription.getIdentifier()));
////							List<PuntoSerieDeTiempo> listaPuntosDiff=seriesDeTiempoComparer(obtenerSerieTiempo(serieTs),serieIdeam);
//							List<CorrectionDateRange> listasCorrecion=timeSeriesQuerier.obtenerListasDeCorrecion(serieTs.getUniqueId(), initDate, finDate);
//							List<PuntoSerieDeTiempo> listaPuntosDiff=seriesDeTiempoMapComparer(serieIdeam, obtenerMapSerieTiempo(serieTs), obtenerMapSerieTiempo(Long.parseLong(locationDescription.getIdentifier()), variable,serieDhime),listasCorrecion);
//							generarArchivosInsercionActualización(locationDescription.Identifier, variable, listaPuntosDiff);
//						}						
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				
//				
//				
//		}
//	}
	
	
	public void comparadorSeriesTiempo(String locationFilter, String variableFilter,String paramFilter, String folderContainFilter, String hourDeleteConfig, Boolean onlyGenDeletes, ComparerDto reglaComparacion)throws WebServiceException, Exception{
		List<LocationDescription> listaEstaciones=timeSeriesQuerier.obtenerListaLocations(locationFilter, folderContainFilter);
		for (LocationDescription locationDescription : listaEstaciones) {						
				List<TimeSeriesDescription> listaSeriesUbicacion=timeSeriesQuerier.queryTimeSeriesDescriptionListFilter(locationDescription.getIdentifier(),paramFilter);				
				List<TimeSeriesDescription> listaSeriesBack=listaSeriesUbicacion;
				for (TimeSeriesDescription tsdescription : listaSeriesUbicacion) {
					
					String variable=tsdescription.getIdentifier().split("@")[0].split("\\.")[1];
					
					if(variableFilter.equals(variable)){
								
						SerieDeTiempo serieIdeam=null;
						SerieDeTiempo serieIdeamComparar=null;
						String variableSecondSeries=null;
						String filterSerieUniqueId=null;
						
						try {
							
							if(!onlyGenDeletes){
								if(variable!=null){
									serieIdeam=localSeriesQuerier.getIdeamLocalTimeSeries(tsdescription.getLocationIdentifier(), variable);							
								}
								
								if(reglaComparacion!=null  && reglaComparacion.getEtiqueta()!=null){
									for (TimeSeriesDescription tsdescInt : listaSeriesBack) {
										variableSecondSeries=tsdescInt.getIdentifier().split("@")[0].split("\\.")[1];								
										if(reglaComparacion.getEtiqueta()!=null && reglaComparacion.getEtiqueta().equals(variableSecondSeries)){
											filterSerieUniqueId=tsdescInt.getUniqueId();								
											break;
										}							
									}
								}
								else if(reglaComparacion !=null && reglaComparacion.getEtiquetaLocal()!=null){
									serieIdeamComparar=localSeriesQuerier.getIdeamLocalTimeSeries(tsdescription.getLocationIdentifier(), reglaComparacion.getEtiquetaLocal());	
								}
															
							}
							if(serieIdeam!=null || onlyGenDeletes){
								String serieUniqueId=tsdescription.getUniqueId();
								System.out.println(serieUniqueId);
								TimeSeriesDataServiceResponse serieTs=null;
								TimeSeriesDataServiceResponse serieTsForFilter=null;
								TimeSeriesDataServiceResponse serieTsForDelete=null;
								List<PuntoSerieDeTiempo> listaPuntosDiff =new ArrayList<>();
								
								if(hourDeleteConfig!=null){
									serieTsForDelete=timeSeriesQuerier.obtenerTimeSeriesCorrectedData(serieUniqueId, null, null);												
								}
								List<SeriesTiempoValores> serieDhime=dhimeQuerier.obtenerListaValoresDhime(variable, Long.parseLong(locationDescription.getIdentifier()));
								
								List<PuntoSerieDeTiempo> listaPuntosParaBorrado=new ArrayList<>();
								if(hourDeleteConfig!=null){
									List<TimeSeriesPoint> listaPuntosEliminarTs=obtenerPuntosParaBorrarTs(serieTsForDelete,hourDeleteConfig);
									serieTsForDelete.getPoints().removeAll(listaPuntosEliminarTs);
									listaPuntosParaBorrado=obtenerPuntosBorrado(listaPuntosEliminarTs,hourDeleteConfig);								
								}
																				
								if(!onlyGenDeletes){
									if(serieIdeam!=null && serieIdeam.getListaPuntos()!=null && serieIdeam.getListaPuntos().size()>0){
										Date initDate=serieIdeam.getListaPuntos().get(0).getTimestamp();
										Date finDate=serieIdeam.getListaPuntos().get(serieIdeam.getListaPuntos().size()-1).getTimestamp();
			//							System.out.println("Fecha Inicio:"+DateTimeUtilsComponent.formatDateToCompareFormat(initDate));
			//							System.out.println("Fecha fin   :"+DateTimeUtilsComponent.formatDateToCompareFormat(finDate));
										serieTs=timeSeriesQuerier.obtenerTimeSeriesCorrectedData(serieUniqueId, initDate, finDate);													
										List<CorrectionDateRange> listasCorrecion=timeSeriesQuerier.obtenerListasDeCorrecion(serieTs.getUniqueId(), initDate, finDate);
										listaPuntosDiff=seriesDeTiempoMapComparer(serieIdeam, obtenerMapSerieTiempo(serieTs), obtenerMapSerieTiempo(Long.parseLong(locationDescription.getIdentifier()), variable,serieDhime),listasCorrecion);													
									}		
								}
								if(reglaComparacion!=null ){		
									if(filterSerieUniqueId !=null){
										SerieDeTiempo serieBase=new SerieDeTiempo();
										SerieDeTiempo serieResultante=null;
										serieBase.setEstacion(locationDescription.getIdentifier());
										serieBase.setEtiqueta(variable);
										serieBase.setListaPuntos(listaPuntosDiff);
										
										Date initDate=serieIdeam.getListaPuntos().get(0).getTimestamp();
										Date finDate=serieIdeam.getListaPuntos().get(serieIdeam.getListaPuntos().size()-1).getTimestamp();
										serieTsForFilter=timeSeriesQuerier.obtenerTimeSeriesCorrectedData(filterSerieUniqueId, initDate, finDate);																				
										
										serieResultante=reglaComparacion.serieComparada(serieBase, serieTsForFilter, null);
										listaPuntosDiff=serieResultante.getListaPuntos();
									}
									else{
										System.out.println("No se encontro la serie de comparación "+reglaComparacion.getEtiqueta()+" para la estacion "+locationDescription.getIdentifier());									
										SerieDeTiempo serieBase=new SerieDeTiempo();
										SerieDeTiempo serieResultante=null;
										serieBase.setEstacion(locationDescription.getIdentifier());
										serieBase.setEtiqueta(variable);
										serieBase.setListaPuntos(listaPuntosDiff);
										serieResultante=reglaComparacion.serieComparada(serieBase, null, serieIdeamComparar);
										listaPuntosDiff=serieResultante.getListaPuntos();
									}
								}
															
								if(listaPuntosParaBorrado.size()>0){
									listaPuntosDiff.addAll(listaPuntosParaBorrado);
								}
								generarArchivosInsercionActualización(locationDescription.Identifier, variable, listaPuntosDiff);
							}											
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}			
														
					
				}			
		}
	}
	

	public void generadorBackUpSerieTiempo(String locationFilter, String variableFilter,String paramFilter, String folderContainFilter, Date initDate, Date finDate, String horaNoIncluida)throws WebServiceException, Exception{
		List<LocationDescription> listaEstaciones=timeSeriesQuerier.obtenerListaLocations(locationFilter, folderContainFilter);
		for (LocationDescription locationDescription : listaEstaciones) {						
				List<TimeSeriesDescription> listaSeriesUbicacion=timeSeriesQuerier.queryTimeSeriesDescriptionListFilter(locationDescription.getIdentifier(),paramFilter);				
				for (TimeSeriesDescription tsdescription : listaSeriesUbicacion) {
					String variable=tsdescription.getIdentifier().split("@")[0].split("\\.")[1];	
					
					if(!variableFilter.equals(variable)){
						continue;
					}
					
					try {
						String serieUniqueId=tsdescription.getUniqueId();
						
						TimeSeriesDataServiceResponse serieTs=null;											
						serieTs=timeSeriesQuerier.obtenerTimeSeriesCorrectedData(serieUniqueId, initDate, finDate);	
						String estacionId=locationDescription.getIdentifier();
						System.out.println("creando: "+paramFilter+variable+"@"+estacionId);
						fileUtilsComponent.crearArchivoData(variable+"@"+estacionId,obtenerPuntosHoraFormateada(serieTs, horaNoIncluida),"data");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
		}
	}
	
	private List<TimeSeriesPoint> obtenerPuntosParaBorrarTs(TimeSeriesDataServiceResponse serieBorrar, String configHour){		
		List<TimeSeriesPoint> puntos=serieBorrar.getPoints().stream().filter(item->DateTimeUtilsComponent.formatDateToCompareFormat(Date.from(item.getTimestamp().getDateTimeOffset())).contains(configHour)).collect(Collectors.toList());
		return puntos;
	}
	
	
	private List<TimeSeriesPoint> obtenerPuntosHoraFormateada(TimeSeriesDataServiceResponse serieBorrar, String horaRemovida){	
		if(horaRemovida!=null){
			List<TimeSeriesPoint> listapuntos=serieBorrar.getPoints().stream().filter(item->!DateTimeUtilsComponent.formatDateToCompareFormat(Date.from(item.getTimestamp().getDateTimeOffset())).contains(" 00:00:00")).collect(Collectors.toList());				
			return listapuntos;
		}
		return serieBorrar.getPoints();
	}
	
	private List<PuntoSerieDeTiempo> obtenerPuntosBorrado(List<TimeSeriesPoint> puntos, String configHour){	
		
		List<PuntoSerieDeTiempo> buffer=new ArrayList<>();	
		System.out.println("Cantidad de puntos:"+puntos.size());
		for (TimeSeriesPoint punto : puntos) {		
			PuntoSerieDeTiempo puntoDiferente=new PuntoSerieDeTiempo();
			puntoDiferente.setDiffTipoPunto("ERASE");
			puntoDiferente.setDiffDatoPunto(punto.getValue().getNumeric());
			puntoDiferente.setTimestamp(Date.from(punto.getTimestamp().getDateTimeOffset()));
			puntoDiferente.setStrTimestamp(DateTimeUtilsComponent.formatDateToCompareFormat(Date.from(punto.getTimestamp().getDateTimeOffset())));
//			System.out.println("addborrar:"+puntoDiferente);		
			buffer.add(puntoDiferente);	
		}
		return buffer;		
	}
	
	
	
	
	private void generarArchivosInsercionActualización(String estacionId, String variable, List<PuntoSerieDeTiempo> listaPuntos) throws IOException{		
		List<PuntoSerieDeTiempo> puntosActualizar = listaPuntos.stream().filter(t -> "UPDATE".equals(t.getDiffTipoPunto())).collect(Collectors.toList());
		List<PuntoSerieDeTiempo> puntosAgregar = listaPuntos.stream().filter(t -> "APPEND".equals(t.getDiffTipoPunto())).collect(Collectors.toList());
		List<PuntoSerieDeTiempo> puntosBorrar = listaPuntos.stream().filter(t -> "ERASE".equals(t.getDiffTipoPunto())).collect(Collectors.toList());
		
		fileUtilsComponent.registrarEvento("compareLog.log",estacionId+" "+variable+" UPDATE | "+puntosActualizar.size()+"\n");	
		fileUtilsComponent.registrarEvento("compareLog.log",estacionId+" "+variable+" APPEND | "+puntosAgregar.size()+"\n");	
		fileUtilsComponent.registrarEvento("compareLog.log",estacionId+" "+variable+" DELETE | "+puntosBorrar.size()+"\n");	
		
		if(puntosActualizar.size()>0){
			fileUtilsComponent.crearArchivoComparacion(variable+"@"+estacionId,puntosActualizar,"udp");
		}
		if(puntosAgregar.size()>0){
			fileUtilsComponent.crearArchivoComparacion(variable+"@"+estacionId,puntosAgregar,"app");		
		}	
		if(puntosBorrar.size()>0){
			fileUtilsComponent.crearArchivoComparacion(variable+"@"+estacionId,puntosBorrar,"del");		
		}	
			
	}
	
	private void generarArchivosBorradoEntreFechas(String estacionId, String variable, Date fechaInicio, Date fechaFin) throws IOException{		
				
		fileUtilsComponent.crearArchivoEliminacionIntervalo(variable+"@"+estacionId,fechaInicio, fechaFin,"delinterval");				
			
	}
	
//	private SerieDeTiempo obtenerSerieTiempo(TimeSeriesDataServiceResponse serieTiempots){
//		SerieDeTiempo serie=new SerieDeTiempo();
//		serie.setEstacion(serieTiempots.getLocationIdentifier());
//		serie.setEtiqueta(serieTiempots.getLabel());
//		serie.setListaPuntos(obtenerTimeSeriesSerie(serieTiempots.getPoints()));
//		return serie;
//	}
	

	
	

	
	private List<PuntoSerieDeTiempo> obtenerTimeSeriesSerie(ArrayList<TimeSeriesPoint> puntos){
		List<PuntoSerieDeTiempo> listaPuntos=new ArrayList<>();
		for (TimeSeriesPoint tsPoint : puntos) {
			PuntoSerieDeTiempo punto=new PuntoSerieDeTiempo();
			punto.setDato(tsPoint.getValue().getNumeric());
			Date fecha=Date.from(tsPoint.getTimestamp().getDateTimeOffset());
			
			listaPuntos.add(punto);
		}
		return listaPuntos;
	}
	
	
	

	
	
	
	
	public String getAreaOperativa(){
		return "Area Operativa 11";
	}
	
	public String getConfigHoraBorrado(){
		return "00:00:00";
	}
	
	public void compararSeries()throws WebServiceException, Exception{
		
//		for (String paramVar : getVariableFilter()) {
//			String params [] = paramVar.split("\\.");
//			comparadorSeriesTiempo(null,params[1],params[0],getAreaOperativa(),getConfigHoraBorrado());
//		}
		
//		DateNotExistComparerModificadoLocal dtcomparer=new DateNotExistComparerModificadoLocal();
////		dtcomparer.setEtiqueta("TSSM_CON");
//		dtcomparer.setEtiquetaLocal("TSSM_CON");
//		
//		if(getLocationAllThsmCon().size()>0){
//			for (String location : getLocationAllThsmCon()) {
//				for (String paramVar : getVariableThsmCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, dtcomparer);
//				}
//			}
//		}
		
//		DateExistComparer dtcomparer=new DateExistComparer();
//		dtcomparer.setEtiqueta("TSSM_CON");
//		dtcomparer.setPosibleHours(Arrays.asList("07:00:00","13:00:00","18:00:00","19:00:00")); 		
//		if(getLocationHisTssmMediaD().size()>0){
//			for (String location : getLocationHisTssmMediaD()) {
//				for (String paramVar : getVariableHisTssmMediaD()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, dtcomparer);
//				}
//			}
//		}

//		if(getLocationRadiacion().size()>0){
//			for (String location : getLocationRadiacion()) {
//				for (String paramVar : getVariableRadiacionGlobal()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location, paramVar,"RAD SOLAR",null,null);
//				}
//			}
//		}
		
//		ReglaBrilloSolar bscomparer=new ReglaBrilloSolar();
//		bscomparer.setPosibleHours(Arrays.asList("18:00:00")); 
//		if(getLocationBshgCon().size()>0){
//			for (String location : getLocationBshgCon()) {
//				for (String paramVar : getVariableBrilloSolarBshgCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
//		if(getLocationPrecipitacion00Faltantes().size()>0){
//			for (String location : getLocationPrecipitacion00Faltantes()) {
//				for (String paramVar : getVariablePrecipitacionFilter()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location, params[1],params[0],null, getConfigHoraBorrado(),true,null);
//				}
//			}
//		}
		
		
//		if(getLocationPruebaPTPM_CON().size()>0){
//			for (String location : getLocationPruebaPTPM_CON()) {
//				for (String paramVar : getVariablePrecipitacionFilter()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
		
//		if(getLocationSolicitudMarcela9EneroTemperaturaMaxima().size()>0){
//			for (String location : getLocationSolicitudMarcela9EneroTemperaturaMaxima()) {
//				for (String paramVar : getVariableTmxCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
		
//		if(getLocationSolicitudMarcela29EneroFenAtmos().size()>0){
//			for (String location : getLocationSolicitudMarcela29EneroFenAtmos()) {
//				for (String paramVar : getVariableFenomenosAtmosfericosFaCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
//		if(getLocationSolicitudMarcela8FebreroTermografo().size()>0){
//			for (String location : getLocationSolicitudMarcela8FebreroTermografo()) {
//				for (String paramVar : getVariableTstgCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
//		if(getLocationSolicitudMarcelaTemyMaxMinima12Febr().size()>0){
//			for (String location : getLocationSolicitudMarcelaTemyMaxMinima12Febr()) {
//				for (String paramVar : getVariableTmxCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
//		
//		if(getLocationSolicitudMarcelaTemyMaxMinima12Febr().size()>0){
//			for (String location : getLocationSolicitudMarcelaTemyMaxMinima12Febr()) {
//				for (String paramVar : getVariableTmnCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
//		DateNotExistComparerModificadoLocalDvagCon reglaDvagCon=new DateNotExistComparerModificadoLocalDvagCon();
//		reglaDvagCon.setEtiquetaLocal("VVAG_CON");
//		if(getLocationDVAG_CON_all().size()>0){
//			for (String location : getLocationDVAG_CON_all()) {
//				for (String paramVar : getVariableDvagCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, reglaDvagCon);
//				}
//			}
//		}
//		
//		DateNotExistComparerModificadoLocalVVagCon reglaVvagCon=new DateNotExistComparerModificadoLocalVVagCon();
//		reglaVvagCon.setEtiquetaLocal("DVAG_CON");
//		if(getLocationVVAG_CON_all().size()>0){
//			for (String location : getLocationVVAG_CON_all()) {
//				for (String paramVar : getVariableVvagCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, reglaVvagCon);
//				}
//			}
//		}
//	
		
//		if(getLocationSolicitudMarcelaFACon12Febr().size()>0){
//			for (String location : getLocationSolicitudMarcelaFACon12Febr()) {
//				for (String paramVar : getVariableFenomenosAtmosfericosFaCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
//		
//		if(getLocationSolicitudMarcelaTsTgCon12Febr().size()>0){
//			for (String location : getLocationSolicitudMarcelaTsTgCon12Febr()) {
//				for (String paramVar : getVariableTstgCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
//		if(getLocationSolicitudMarcela20FebRSGVAL_AUT_60().size()>0){
//			for (String location : getLocationSolicitudMarcela20FebRSGVAL_AUT_60()) {
//				for (String paramVar : getVariableRadiacionGlobalVal()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
//		if(getLocationsSolicitudMarcelaRSGVAL_AUT_60_15MarzoVer2().size()>0){
//			for (String location : getLocationsSolicitudMarcelaRSGVAL_AUT_60_15MarzoVer2()) {
//				for (String paramVar : getVariableRadiacionGlobalVal()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
//		if(getLocationsSolicitudMarcelaEtiquetasHenry().size()>0){
//			for (String location : getLocationsSolicitudMarcelaEtiquetasHenry()) {
//				for (String paramVar : getVariablesHenryPropias()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
//		if(getLocationsSolicitudEvte_con().size()>0){
//			for (String location : getLocationsSolicitudEvte_con()) {
//				for (String paramVar : getVariableEvteCon()) {
//					String params [] = paramVar.split("\\.");
//					comparadorSeriesTiempo(location,params[1],params[0],null,null, false, null);
//				}
//			}
//		}
		
	}
	
public void generarArchivosBorradoCompleto()throws WebServiceException, Exception{
		
		if(getLocationTssmCon().size()>0){
			for (String location : getLocationTssmCon()) {
				for (String paramVar : getVariableTssmCon()) {
					String params [] = paramVar.split("\\.");
					Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("1900-01-01 00:00:00");
					Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2017-12-31 23:59:59");
					generarArchivosBorradoEntreFechas(location, paramVar,fechaIni,fechaFin);
				}
			}
		}
		
	}


public void generarBackUpArchivos()throws WebServiceException, Exception{
	
	if(getLocationsSolicitud_HIS_TSSM_MN_D().size()>0){
		for (String location : getLocationsSolicitud_HIS_TSSM_MN_D()) {
			for (String paramVar : getVariable_HIS_TSSM_MN_D()) {
				String params [] = paramVar.split("\\.");			
				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("1990-01-01 00:00:00");
				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
			}
		}
	}
	
	if(getLocationsSolicitud_HIS_TSSM_MX_D().size()>0){
		for (String location : getLocationsSolicitud_HIS_TSSM_MX_D()) {
			for (String paramVar : getVariable_HIS_TSSM_MX_D()) {
				String params [] = paramVar.split("\\.");			
				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("1990-01-01 00:00:00");
				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
			}
		}
	}
	
//	if(getLocationsSolicitudEvte_con().size()>0){
//		for (String location : getLocationsSolicitudEvte_con()) {
//			for (String paramVar : getVariableEvteCon()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("1990-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
		
//	if(getLocationBackupTiquetREQ2019_001640().size()>0){
//		for (String location : getLocationBackupTiquetREQ2019_001640()) {
//			for (String paramVar : getVariablePrecipitacionFilter()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("2017-03-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2018-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
	
//	if(getLocationBackupTiquetREQ2019_001518().size()>0){
//		for (String location : getLocationBackupTiquetREQ2019_001518()) {
//			for (String paramVar : getVariableBackupSolicitudREQ2019_001518()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("2017-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
	
//	if(getLocationBackupTiquetREQ2019_001520().size()>0){
//		for (String location : getLocationBackupTiquetREQ2019_001520()) {
//			for (String paramVar : getVariableBackupSolicitudREQ2019_001520()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("2017-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
	
//	if(getLocationBackupTiquetREQ2019_001528().size()>0){
//		for (String location : getLocationBackupTiquetREQ2019_001528()) {
//			for (String paramVar : getVariableBackupSolicitudREQ2019_001528()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("2017-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
	
//	if(getLocationVVAG_CON_all().size()>0){
//		for (String location : getLocationVVAG_CON_all()) {
//			for (String paramVar : getVariableVvagCon()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("1900-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
	
//	if(getLocationDVAG_CON_all().size()>0){
//		for (String location : getLocationDVAG_CON_all()) {
//			for (String paramVar : getVariableDvagCon()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("1900-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
	
//	if(getLocationSolicitudMarcela8FebreroTermografo().size()>0){
//		for (String location : getLocationSolicitudMarcela8FebreroTermografo()) {
//			for (String paramVar : getVariableTstgCon()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("1900-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2019-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
	
	
//	if(getLocationSolicitudInfoMarcela12Febr_EstPrec().size()>0){
//		for (String location : getLocationSolicitudInfoMarcela12Febr_EstPrec()) {
//			for (String paramVar : getVariablePrecipitacionMensual()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("2017-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2018-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
//	
//	if(getLocationSolicitudInfoMarcela12Febr_Temp().size()>0){
//		for (String location : getLocationSolicitudInfoMarcela12Febr_Temp()) {
//			for (String paramVar : getVariablesTemperaturasMensuales()) {
//				String params [] = paramVar.split("\\.");			
//				Date fechaIni=DateTimeUtilsComponent.parseDateToCompareFormat("2017-01-01 00:00:00");
//				Date fechaFin=DateTimeUtilsComponent.parseDateToCompareFormat("2018-12-31 23:59:59");
//				generadorBackUpSerieTiempo(location,params[1],params[0],null, fechaIni, fechaFin, null);
//			}
//		}
//	}
	
}
	
	


	
	  
	

}
