package co.gov.ideam.dhime.modificador.series;

import static co.gov.ideam.dhime.modificador.series.EjecucionesLocationsMethodsModif.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aquaticinformatics.aquarius.sdk.timeseries.AquariusClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.DeleteTimeSeries;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.ExtendedAttributeValue;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.GetMonitoringMethods;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.GetParameters;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.InterpolationType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.Parameter;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.PostBasicTimeSeries;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.PostStatisticalDerivedTimeSeries;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.PutTimeSeries;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionServiceRequest;

import co.gov.ideam.dhime.comparador.webapp.dto.EstacionEtiqueta;
import co.gov.ideam.dhime.comparador.webapp.dto.TSParametro;
import co.gov.ideam.dhime.comparador.webapp.dto.TSParametroEstadistica;
import co.gov.ideam.dhime.generador.timeseries.TimeSeriesQuerier;
import co.gov.ideam.dhime.modificador.series.enums.ComputationIdentifiers;
import co.gov.ideam.dhime.modificador.series.enums.ComputationPeriod;
import net.servicestack.client.WebServiceException;
import net.servicestack.func.Func;


@Component
public class TimeSeriesConfigModifier {
	
	@Autowired
	private TimeSeriesQuerier timeSeriesQuerier;
	

	@Value("${aquarius.path}")
	public String AQUARIUS_PATH;
	
	@Value("${aquarius.user}")
	public String AQUARIUS_USER;
	
	@Value("${aquarius.password}")
	public String AQUARIUS_PASSWORD;
		

	public TimeSeriesConfigModifier() {
		
	}
	
	private TimeSeriesQuerier getTimeSeriesQuerier(){
		return (timeSeriesQuerier!=null?timeSeriesQuerier:new TimeSeriesQuerier());
	}
	
	AquariusClient cliente = null;	
	
	@Test
	public void creacion(){		
		try (AquariusClient cli = AquariusClient.createConnectedClient("http://172.16.50.54/", "AppExternos", "dh1m31nt3r0p*")) {		
				cliente=cli;
				System.out.println("Se conecta y inicia creacion series");
				for (EstacionEtiqueta estacionEtiqueta : solicitudCreacionBasicasHIS_Q_MX_M()) {
					LocationDataServiceRequest locatdescReq = new LocationDataServiceRequest();
					locatdescReq.setLocationIdentifier(estacionEtiqueta.getEstacion());
					LocationDataServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
					if(locatdescResp!=null){
						String uid=locatdescResp.getUniqueId();
						try {
							System.out.println("Creando:"+estacionEtiqueta.getEstacion()+"::"+estacionEtiqueta.getEtiqueta());
							crearSerieDeTiempoBasica(estacionEtiqueta,listaDeTsParametrosBasicas().get(estacionEtiqueta.getEtiqueta()),uid,cliente);												
						} catch (WebServiceException e) {
						    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
						} catch (Exception e) {
						    System.out.format("ERROR: That was weird! %s", e.getMessage());
						    e.printStackTrace();
						}
					}
				}
				System.out.println("Termina creacion series");
			}catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} catch (Exception e) {
			    System.out.format("ERROR: That was weird! %s", e.getMessage());
			    e.printStackTrace();
			}					
	}
	
//	@Test
	public void creacionDerivadaEsadistica(){		
		try (AquariusClient cli = AquariusClient.createConnectedClient("http://172.16.50.54/", "AppExternos", "dh1m31nt3r0p*")) {		
				cliente=cli;
				System.out.println("Se conecta y inicia creacion series");
				for (EstacionEtiqueta estacionEtiqueta : listaEstacionEtiquetaPruebaDerivada()) {
					LocationDataServiceRequest locatdescReq = new LocationDataServiceRequest();
					locatdescReq.setLocationIdentifier(estacionEtiqueta.getEstacion());
					LocationDataServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
					if(locatdescResp!=null){
						String uid=locatdescResp.getUniqueId();
						try {
							System.out.println("Creando:"+estacionEtiqueta.getEstacion()+"::"+estacionEtiqueta.getEtiqueta());
							crearSerieDeTiempoDerivadaEstadistica(estacionEtiqueta,listaDeTsParametrosDerivadas().get(estacionEtiqueta.getEtiqueta()),uid,cliente);												
						} catch (WebServiceException e) {
						    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
						} catch (Exception e) {
						    System.out.format("ERROR: That was weird! %s", e.getMessage());
						    e.printStackTrace();
						}
					}
				}
				System.out.println("Termina creacion series");
			}catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} catch (Exception e) {
			    System.out.format("ERROR: That was weird! %s", e.getMessage());
			    e.printStackTrace();
			}					
	}
	
	public void eliminacion(){		
		try (AquariusClient cli = AquariusClient.createConnectedClient("http://172.16.50.54/", "AppExternos", "dh1m31nt3r0p*")) {		
				cliente=cli;
				System.out.println("Se conecta y inicia eliminacion series");
				for (EstacionEtiqueta estacionEtiqueta : listaEstacionEtiquetaSoloPa()) {
					LocationDataServiceRequest locatdescReq = new LocationDataServiceRequest();
					locatdescReq.setLocationIdentifier(estacionEtiqueta.getEstacion());
					LocationDataServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
					if(locatdescResp!=null){
						String uid=locatdescResp.getUniqueId();															
						deleteSeries(estacionEtiqueta, listaDeTsParametrosBasicas().get(estacionEtiqueta.getEtiqueta()), uid, cliente);
					}
				}
				System.out.println("Termina eliminacion series");
			}catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} catch (Exception e) {
			    System.out.format("ERROR: That was weird! %s", e.getMessage());
			    e.printStackTrace();
			}					
	}
	
	public static Map<String,TSParametro> listaDeTsParametrosBasicas(){
		Map<String,TSParametro> mapaTSParametros=new HashMap<String, TSParametro>();
		mapaTSParametros.put("BSHG_CON",new TSParametro("BRILLO SOLAR",60,InterpolationType.SucceedingConstant,ComputationPeriod.Hourly,"horas/sol","Brillo solar de 500 a 1800"));
		mapaTSParametros.put("DVAG_CON",new TSParametro("DIR VIENTO",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"deg","Dirección del viento de las 24 horas en grados"));
		mapaTSParametros.put("DVMXAG_CON",new TSParametro("DIR VIENTO",1440,InterpolationType.InstantaneousValues,ComputationPeriod.Daily,"deg","Dirección del viento en grados de la máxima velocidad en el día"));
		mapaTSParametros.put("EVTE_CON",new TSParametro("EVAPORACION",1440,InterpolationType.InstantaneousValues,ComputationPeriod.Daily,"mm","Evaporación total diaria"));
		mapaTSParametros.put("FA_CON",new TSParametro("FEN ATMOS",360,InterpolationType.DiscreteValues,ComputationPeriod.Hourly,"codigo","Fenómeno Atmosférico de las 700, 1300 y 1800"));
		mapaTSParametros.put("HRHG_CON",new TSParametro("HUM RELATIVA",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"%","Humedad relativa de las 24 horas (gráfica)"));
		mapaTSParametros.put("NB_CON",new TSParametro("NUBOSIDAD",360,InterpolationType.DiscreteValues,ComputationPeriod.Daily,"Ctg","Nubosidad de las 700, 1300 y 1800"));
		mapaTSParametros.put("PTPG_CON",new TSParametro("PRECIPITACION",60,InterpolationType.PrecedingTotals,ComputationPeriod.Hourly,"mm","Precipitación horaria"));
		mapaTSParametros.put("PTPM_CON",new TSParametro("PRECIPITACION",1440,InterpolationType.PrecedingTotals,ComputationPeriod.Daily,"mm","Día pluviométrico"));
		mapaTSParametros.put("RCAM_CON",new TSParametro("REC VIENTO",1440,InterpolationType.SucceedingConstant,ComputationPeriod.Daily,"km","Recorrido del viento diario"));
		mapaTSParametros.put("TSTG_CON",new TSParametro("TEMPERATURA",60,InterpolationType.InstantaneousValues,ComputationPeriod.Daily,"degC","Temperatura seca de las 24 hora (gráfica)"));
		mapaTSParametros.put("TSSM_CON",new TSParametro("TEMPERATURA",360,InterpolationType.SucceedingConstant,ComputationPeriod._3PerDay,"degC","Temperatura seca de las 700, 1300 y 1800"));
		
		mapaTSParametros.put("THSM_CON",new TSParametro("TEMPERATURA",360,InterpolationType.SucceedingConstant,ComputationPeriod._3PerDay,"degC","Temperatura húmeda de las 700, 1300 y 1800"));
		mapaTSParametros.put("THSM_CON_add",new TSParametro("TEMPERATURA",360,InterpolationType.SucceedingConstant,ComputationPeriod._3PerDay,"degC","Temperatura húmeda de las 700, 1300 y 1800"));
		
		mapaTSParametros.put("TMX_CON",new TSParametro("TEMPERATURA",1440,InterpolationType.SucceedingConstant,ComputationPeriod.Daily,"degC","Temperatura máxima diaria"));
		mapaTSParametros.put("TMN_CON",new TSParametro("TEMPERATURA",1440,InterpolationType.SucceedingConstant,ComputationPeriod.Daily,"degC","Temperatura mínima diaria"));
		mapaTSParametros.put("VVAG_CON",new TSParametro("VEL VIENTO",60,InterpolationType.InstantaneousValues,ComputationPeriod.Daily,"m/s","Velocidad del viento de las 24 horas"));
		mapaTSParametros.put("VVMXAG_CON",new TSParametro("VEL VIENTO",1440,InterpolationType.InstantaneousValues,ComputationPeriod.Daily,"m/s","Velocidad del viento máxima del día"));
		mapaTSParametros.put("NVLM_CON",new TSParametro("NIVEL",720,InterpolationType.InstantaneousValues,ComputationPeriod._2PerDay,"cm","Nivel del rio horario"));
		mapaTSParametros.put("HIS_TSSM_MEDIA_D",new TSParametro("TEMPERATURA",1440,InterpolationType.SucceedingConstant,ComputationPeriod.Daily,"degC","Temperatura seca media diaria histórica", "0"));
		mapaTSParametros.put("HRA2_AUT_60",new TSParametro("HUM RELATIVA",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"%","Humedad relativa del aire a 2 metros horaria"));
		mapaTSParametros.put("TA2_AUT_60",new TSParametro("TEMPERATURA",60,InterpolationType.SucceedingConstant,ComputationPeriod.Hourly,"degC","Temperatura del Aire a 2 metros"));
		mapaTSParametros.put("TAMX2_AUT_60",new TSParametro("TEMPERATURA",60,InterpolationType.SucceedingConstant,ComputationPeriod.Hourly,"degC","Temperatura máxima del Aire a 2 metros"));
		mapaTSParametros.put("TAMN2_AUT_60",new TSParametro("TEMPERATURA",60,InterpolationType.SucceedingConstant,ComputationPeriod.Hourly,"degC","Temperatura mínima del Aire a 2 metros"));
		mapaTSParametros.put("VV_AUT_10",new TSParametro("VEL VIENTO",10,InterpolationType.InstantaneousValues,ComputationPeriod._10Minutes,"m/s","Velocidad del viento cada 10 min"));
		mapaTSParametros.put("DV_AUT_10",new TSParametro("DIR VIENTO",10,InterpolationType.InstantaneousValues,ComputationPeriod._10Minutes,"deg","Dirección del viento cada 10 minutos"));
		mapaTSParametros.put("VVMX_AUT_60",new TSParametro("VEL VIENTO",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"m/s","Velocidad del viento máxima de la hora"));
		mapaTSParametros.put("DVMX_AUT_60",new TSParametro("DIR VIENTO",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"deg","Dirección del viento de máxima velocidad en la hora"));
		mapaTSParametros.put("NV_AUT_60",new TSParametro("NIVEL",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"cm","Nivel horario"));
		mapaTSParametros.put("NVMX_AUT_60",new TSParametro("NIVEL",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"cm","Nivel máximo horario"));
		mapaTSParametros.put("NVMN_AUT_60",new TSParametro("NIVEL",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"cm","Nivel mínimo horario"));
		mapaTSParametros.put("PT_AUT_10",new TSParametro("PRECIPITACION",10,InterpolationType.PrecedingTotals,ComputationPeriod._10Minutes,"mm","Precipitación acumulada 10 minutos"));
		mapaTSParametros.put("PA_AUT_60",new TSParametro("PRES ATMOS",60,InterpolationType.InstantaneousValues,ComputationPeriod.Hourly,"hPa","Presión Atmosférica Horaria"));
		
		mapaTSParametros.put("TR_QS_D",new TSParametro("TM",0,InterpolationType.InstantaneousValues,ComputationPeriod.Daily,"kg/s","Transporte de sedimentos medio diario /se obtiene con la curva EQ_QS-QL"));
		mapaTSParametros.put("CM_D",new TSParametro("CS",0,InterpolationType.InstantaneousValues,ComputationPeriod.Daily,"mg/l","Concentración media diaria / depende de CSPROMEDIO y EQ_CS-CM"));
		mapaTSParametros.put("RSGVAL_AUT_60",new TSParametro("RAD SOLAR",60,InterpolationType.SucceedingConstant,ComputationPeriod.Hourly,"Wh/m^2","Radiación solar global horaria validada"));
		mapaTSParametros.put("RSAG_CON",new TSParametro("RAD SOLAR",60,InterpolationType.SucceedingConstant,ComputationPeriod.Daily,"Wh/m^2","Radiación solar de 1000 a 1600"));
		
		mapaTSParametros.put("HIS_Q_MEDIA_M",new TSParametro("CAUDAL",60,InterpolationType.SucceedingConstant,ComputationPeriod.Monthly,"m^3/s","Caudal medio mensual historico"));
		
		mapaTSParametros.put("HIS_Q_MX_M",new TSParametro("CAUDAL",60,InterpolationType.SucceedingConstant,ComputationPeriod.Monthly,"m^3/s","Caudal máximo mensual historico"));
		
		
		return mapaTSParametros;
	}
	


	public static Map<String,TSParametroEstadistica> listaDeTsParametrosDerivadas(){
		Map<String,TSParametroEstadistica> mapaTSParametros=new HashMap<String, TSParametroEstadistica>();
		mapaTSParametros.put("BSHG_MEDIA_M_refl",new TSParametroEstadistica("BRILLO SOLAR",60,InterpolationType.SucceedingConstant,ComputationPeriod.Monthly,"horas/sol","Brillo solar de 500 a 1800","0",ComputationIdentifiers.Mean,"BSHG_MEDIA_D"));
		return mapaTSParametros;
		}
	

		
	
	
	private static final Duration NoGaps = Duration.ofSeconds(Long.MAX_VALUE);
	
	private static final List<Provisioning.InterpolationType> InterpolationTypesWithNoGaps = java.util.Arrays.asList(new Provisioning.InterpolationType[]{
	        Provisioning.InterpolationType.PrecedingTotals,
	        Provisioning.InterpolationType.InstantaneousTotals,
	        Provisioning.InterpolationType.DiscreteValues});
	
	
	private void deleteSeries(EstacionEtiqueta etiqueta, TSParametro parametro, String locationIdentifier, AquariusClient cliente){
		List<TimeSeriesDescription> listaSeries=listaSeriesGlobalDel.get(etiqueta.getEstacion());
		if(listaSeries==null){
			listaSeries=queryTimeSeriesDescriptionListFilterByLocationId(etiqueta.getEstacion(), cliente);
			listaSeriesGlobalDel.put(etiqueta.getEstacion(), (ArrayList<TimeSeriesDescription>) listaSeries);
		}
		String composicion=parametro.getParameter()+"."+etiqueta.getEtiqueta()+"@"+etiqueta.getEstacion();
		TimeSeriesDescription serie= Func.first(listaSeries.stream().filter(item->item.getIdentifier().contains(composicion)).collect(Collectors.toList()));
		
		System.out.println("UniqueId:"+serie.getUniqueId());
		if(serie!=null){
			DeleteTimeSeries request =new DeleteTimeSeries();
			request.setTimeSeriesUniqueId(serie.getUniqueId());			
			cliente.Provisioning.post(request);
		}		
	}
	
	private void updateSeries(EstacionEtiqueta etiqueta, TSParametro parametro, String locationIdentifier, AquariusClient cliente){
		List<TimeSeriesDescription> listaSeries=listaSeriesGlobalDel.get(etiqueta.getEstacion());
		if(listaSeries==null){
			listaSeries=queryTimeSeriesDescriptionListFilterByLocationId(etiqueta.getEstacion(), cliente);
			listaSeriesGlobalDel.put(etiqueta.getEstacion(), (ArrayList<TimeSeriesDescription>) listaSeries);
		}
		String composicion=parametro.getParameter()+"."+etiqueta.getEtiqueta()+"@"+etiqueta.getEstacion();
		TimeSeriesDescription serie= Func.first(listaSeries.stream().filter(item->item.getIdentifier().contains(composicion)).collect(Collectors.toList()));
		
		if(serie!=null){
			PutTimeSeries request =new PutTimeSeries();
			request.setTimeSeriesUniqueId(serie.getUniqueId());				
			cliente.Provisioning.post(request);
		}	
				
	}
	
	public class UpdateMethod extends PutTimeSeries{
		
	}
	

	Map<String, ArrayList<TimeSeriesDescription>> listaSeriesGlobalDel =new HashMap<>();
	
	public ArrayList<TimeSeriesDescription> queryTimeSeriesDescriptionListFilterByLocationId(String estacionid, AquariusClient cliente){
		TimeSeriesDescriptionServiceRequest tedRequest = new TimeSeriesDescriptionServiceRequest();
		tedRequest.setLocationIdentifier(estacionid);
		TimeSeriesDescriptionListServiceResponse tedResp=cliente.Publish.get(tedRequest);
		return tedResp.getTimeSeriesDescriptions();
	}
	
	private String crearSerieDeTiempoBasica(EstacionEtiqueta etiqueta, TSParametro parametro, String locationIdentifier, AquariusClient cliente) throws Exception {

	    	Provisioning.MonitoringMethod defaultMonitoringMethod = GetDefaultMonitoringMethod(cliente);
		    Parameter parameter = GetParameter(parametro.getParameter(),cliente);
		    Duration gmtConfig = Duration.ofHours(-5);
		    
		    PostBasicTimeSeries request = new PostBasicTimeSeries()
		            .setGapTolerance(GapToleranceForInterpolationType(parametro.getInterpolation(),parametro.getGapTolerance()));

		    // NOTE: The PostBasicTimeSeries request DTO is derived from TimeSeriesBase.
		    // As a result, properties defined on the base class have setXXX() methods that return the base class, not the derived class.
		    // This unfortunate Java limitation breaks a bit of the fluent nature of these methods.
		    List<ExtendedAttributeValue> listaAttExt=new ArrayList<>();
		    ExtendedAttributeValue webpatt=new ExtendedAttributeValue();
		    webpatt.setColumnIdentifier("WEBPORTAL_SYNC@TIMESERIES_EXTENSION");
		    webpatt.setValue(parametro.getPublicado()!=null?parametro.getPublicado():"1");
		    listaAttExt.add(webpatt);
		    request
		            .setLocationUniqueId(locationIdentifier)
		            .setParameter(parameter.getParameterId())		            
		            .setLabel(etiqueta.getEtiqueta())
		            .setUnit(parametro.getUnit())
		            .setInterpolationType(parametro.getInterpolation())
		            .setComputationPeriodIdentifier(parametro.getPeriodicidad().getIdentifier())
		            .setUtcOffset(gmtConfig)
		            .setDescription(parametro.getDescripcion())
		            .setPublish(true)
		            .setExtendedAttributeValues(listaAttExt)
		            .setMethod(defaultMonitoringMethod.MethodCode);
		    		
		    	

		    return cliente.Provisioning
		            .post(request)
		            .UniqueId;	    		
	    
	}
	
	
	private String crearSerieDeTiempoDerivadaEstadistica(EstacionEtiqueta etiqueta, TSParametroEstadistica parametro, String locationIdentifier,  AquariusClient cliente) throws Exception {

    	Provisioning.MonitoringMethod defaultMonitoringMethod = GetDefaultMonitoringMethod(cliente);
	    Parameter parameter = GetParameter(parametro.getParameter(),cliente);
	    Duration gmtConfig = Duration.ofHours(-5);
	    
//	    PostStatisticalDerivedTimeSeries request = new PostStatisticalDerivedTimeSeries();
	    
	    PostStatisticalDerivedTimeSeries request = new PostStatisticalDerivedTimeSeries();
	    
	    
//	            .setGapTolerance(GapToleranceForInterpolationType(parametro.getInterpolation(),parametro.getGapTolerance()));

	    // NOTE: The PostBasicTimeSeries request DTO is derived from TimeSeriesBase.
	    // As a result, properties defined on the base class have setXXX() methods that return the base class, not the derived class.
	    // This unfortunate Java limitation breaks a bit of the fluent nature of these methods.
	    List<ExtendedAttributeValue> listaAttExt=new ArrayList<>();
	    ExtendedAttributeValue webpatt=new ExtendedAttributeValue();
	    webpatt.setColumnIdentifier("WEBPORTAL_SYNC@TIMESERIES_EXTENSION");
	    webpatt.setValue(parametro.getPublicado()!=null?parametro.getPublicado():"1");
	    listaAttExt.add(webpatt);
	    
	    
	    String idSerieBase=timeSeriesQuerier.obtenerTimeSeriesUniqueId(cliente, parametro.getEtiquetaSerieBase(),etiqueta.getEstacion());
	    
	    
	    request.setTimeSeriesUniqueId(idSerieBase);
	    request.setLocationUniqueId(locationIdentifier);
	    request.setParameter(parameter.getParameterId());		            
	    request.setLabel(etiqueta.getEtiqueta());
	    request.setUnit(parametro.getUnit());
	    request.setInterpolationType(parametro.getInterpolation());       
	    request.setUtcOffset(gmtConfig);
	    request.setDescription(parametro.getDescripcion());
	    request.setPublish(Boolean.valueOf(parametro.getPublicado()));
	    request.setExtendedAttributeValues(listaAttExt);
	    request.setMethod(defaultMonitoringMethod.MethodCode);
	    request.setComputationIdentifier(parametro.getStatisticalComputationIdentifier().getIdentifier());
	    request.setComputationPeriodIdentifier(parametro.getComputationPeriod().getIdentifier());	
	    
	    
//	   request.setFormula(parametro.getFormula());
//	   request.setTimeSeriesUniqueIds(value);
	    		
	    	

	    return cliente.Provisioning
	            .post(request)
	            .UniqueId;	    		
    
}
	
	private Parameter GetParameter(String parameterIdentifier, AquariusClient cliente)
	{
	    return Func.first(
	            cliente.Provisioning.get(new GetParameters()).Results,
	            p -> p.Identifier.equals(parameterIdentifier));
	}
	
//	private Parameter GetComputationPeriod(String comPeriod, AquariusClient cliente)
//	{
//	    return Func.first(
//	            cliente.Provisioning.get(new GetParameters()).Results,
//	            p -> p.Identifier.equals(parameterIdentifier));
//	}
	
	
	private Duration GapToleranceForInterpolationType(Provisioning.InterpolationType interpolationType, Integer defaultGapToleranceMinutes)
	{
	    return defaultGapToleranceMinutes < 1 || Func.first(InterpolationTypesWithNoGaps, i -> i == interpolationType) != null
	            ? NoGaps
	            : Duration.ofMinutes(defaultGapToleranceMinutes);
	}
		
	
	
	
	
	private String GetLocationUniqueId(String locationIdentifier, AquariusClient cliente)
	{
	    return Func.first(
	            cliente.Publish
	                .get(new LocationDescriptionListServiceRequest().setLocationIdentifier(locationIdentifier))
	                .LocationDescriptions,
	            l -> l.Identifier.equals(locationIdentifier))
	    .UniqueId;
	}

	private Provisioning.MonitoringMethod GetDefaultMonitoringMethod(AquariusClient cliente)
	{
	    return Func.first(
	            cliente.Provisioning.get(new GetMonitoringMethods()).Results,
	            m -> m.ParameterUniqueId.equals(EMPTY_GUID));
	}
	
	private final String EMPTY_GUID = "00000000000000000000000000000000";
	
	
//	@Test
	public void creacionPrueba() throws Exception {
		    	    		
		    try (AquariusClient cli = AquariusClient.createConnectedClient("http://172.16.50.54/", "AppExternos", "dh1m31nt3r0p*")) {		
//		    try (AquariusClient cli = AquariusClient.createConnectedClient("http://172.16.50.54/", "admin", "dh1m3w3b2018*")) {
		    				    	
			    Duration gmtConfig = Duration.ofHours(-5);
			    
			    PostStatisticalDerivedTimeSeries request = new PostStatisticalDerivedTimeSeries();			    			  

			    List<ExtendedAttributeValue> listaAttExt=new ArrayList<>();
			    ExtendedAttributeValue webpatt=new ExtendedAttributeValue();
			    webpatt.setColumnIdentifier("WEBPORTAL_SYNC@TIMESERIES_EXTENSION");
			    webpatt.setValue("0");
			    listaAttExt.add(webpatt);
			    			     
			    request.setTimeSeriesUniqueId("460482a0dc8e4e78945e70bdafd823eb");
			    request.setLocationUniqueId("0c36d23e699749a980c00183d5fac13a");
			    request.setParameter("HG");		            
			    request.setLabel("PRUEBA2");
			    request.setUnit("m");
			    request.setInterpolationType(InterpolationType.SucceedingConstant);       
			    request.setUtcOffset(gmtConfig);
			    request.setDescription("Descripción de prueba");
			    request.setPublish(false);
			    request.setExtendedAttributeValues(listaAttExt);
			    request.setMethod("DefaultNone");
			    request.setComputationIdentifier("Mean");
			    request.setComputationPeriodIdentifier("Daily");	
		
			    cli.Provisioning.post(request);
			            		    	
				System.out.println("Termina creacion series");
			}catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} catch (Exception e) {
			    System.out.format("ERROR: That was weird! %s", e.getMessage());
			    e.printStackTrace();
			}		
		    
	}

}
