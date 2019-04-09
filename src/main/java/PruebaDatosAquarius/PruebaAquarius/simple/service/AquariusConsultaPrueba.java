package PruebaDatosAquarius.PruebaAquarius.simple.service;

import java.util.ArrayList;

import org.junit.Test;

import com.aquaticinformatics.aquarius.sdk.timeseries.AquariusClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ApprovalListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ApprovalListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ApprovalMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttribute;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttributeFilter;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataCorrectedServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataRawServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;

import net.servicestack.client.WebServiceException;



//@Component
public class AquariusConsultaPrueba {
	
	AquariusClient cliente = null;


	
	@Test
	public void test() {
//		try (AquariusClient cli = AquariusClient.createConnectedClient("http://172.16.50.54/", "admdhime", "Ep33m*2018")) {
//		try (AquariusClient cli = AquariusClient.createConnectedClient("http://181.225.72.82/", "admin", "dh1m3w3b2018*")) {
		try (AquariusClient cli = AquariusClient.createConnectedClient("http://181.225.72.82/", "AppExternos", "4pp3xt3rn0")) {
//		try (AquariusClient cli = AquariusClient.createConnectedClient("http://172.16.50.32/", "Dhimecapacita", "dhime2018*")) {
			cliente=cli;
			System.out.println("Se ha conectado a aquarius de forma exitosa por el test");
//			queryGradeList();
//			queryApprovalList();
//			queryParamList();
//			queryUnitList();
//			queryLocationDescriptionList();
//			queryLocationDescriptionListFilteredByName();
//			queryLocationDescriptionListFilteredByAreaOperativa();
//			queryLocationDescriptionListFilteredByLocationFolder();
//			queryLocationDescriptionListFilteredByExtendedAttributes();
//			queryLocationDataList();
			queryTimeSeriesDescriptionListFilterByLocationId();
//			queryTimeSeriesDataById();
			
		}catch (WebServiceException e) {
		    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
		} catch (Exception e) {
		    System.out.format("ERROR: That was weird! %s", e.getMessage());
		    e.printStackTrace();
		}
	}
	
	public void queryApprovalList(){
		ApprovalListServiceRequest approvalReq = new ApprovalListServiceRequest();
		ApprovalListServiceResponse approvalResp=cliente.Publish.get(approvalReq);
		System.out.println("...................................................................");
		System.out.println("........................PRINTING APROVAL LIST......................");
		System.out.println("...................................................................");
		for (ApprovalMetadata elemento : approvalResp.getApprovals()) {
			System.out.println(elemento.getDisplayName());
		}
	}
	
	public void queryGradeList(){
		GradeListServiceRequest gradeReq = new GradeListServiceRequest();
		GradeListServiceResponse gradeResp=cliente.Publish.get(gradeReq);
		System.out.println("............................................................");
		System.out.println("...................PRINTING GRADE LIST......................");
		System.out.println("............................................................");
		for (GradeMetadata elemento : gradeResp.Grades) {
			System.out.println(elemento.getDisplayName());
		}
	}

	public void queryParamList(){
		ParameterListServiceRequest paramReq = new ParameterListServiceRequest();
		ParameterListServiceResponse paramResp=cliente.Publish.get(paramReq);
		System.out.println("..........................................................................................");
		System.out.println("................................................PARAMETER PARAM LIST......................");
		System.out.println("..........................................................................................");
		for (ParameterMetadata elemento : paramResp.Parameters) {
			System.out.println(elemento.getIdentifier()+"-"+ elemento.getDisplayName());
		}
	}
	
	public void queryUnitList(){
		UnitListServiceRequest unitReq = new UnitListServiceRequest();
		UnitListServiceResponse unitResp=cliente.Publish.get(unitReq);
		System.out.println(".....................................................");
		System.out.println("...............PARAMETER UNIT LIST...................");
		System.out.println(".....................................................");
		for (UnitMetadata elemento : unitResp.Units) {
			System.out.println(elemento.getDisplayName());
		}
	}
	
	public void queryLocationDescriptionList(){
		LocationDescriptionListServiceRequest locatdescReq = new LocationDescriptionListServiceRequest();
		LocationDescriptionListServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
		System.out.println("...........................................................................................");
		System.out.println("........................................LOCATIONDESCRIPTION UNIT LIST......................");
		System.out.println("...........................................................................................");
		for (LocationDescription elemento : locatdescResp.LocationDescriptions) {
			System.out.println("ID:"+elemento.getIdentifier()+"-Nombre:"+elemento.getName()+"-Folder:"+elemento.getPrimaryFolder()+"-SecFolder:"+elemento.getSecondaryFolders());
		}
	}
	
	public void queryLocationDescriptionListFilteredByName(){
		LocationDescriptionListServiceRequest locatdescReq = new LocationDescriptionListServiceRequest();
		locatdescReq.setLocationName("*YOLOMBO*");
		LocationDescriptionListServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
		System.out.println("...........................................................................................");
		System.out.println("....................LOCATIONDESCRIPTION FILTERED LIST BY LOCATIONNAME......................");
		System.out.println("...........................................................................................");
		for (LocationDescription elemento : locatdescResp.LocationDescriptions) {
			System.out.println("ID:"+elemento.getIdentifier()+"-Nombre:"+elemento.getName()+"-Folder:"+elemento.getPrimaryFolder()+
					"-SecFolder:"+elemento.getSecondaryFolders());
		}
	}
	

	
	
	public void queryLocationDescriptionListFilteredByLocationFolder(){
		LocationDescriptionListServiceRequest locatdescReq = new LocationDescriptionListServiceRequest();		
		//Se puede filtrar por el municipio, departamento, area operativa
		locatdescReq.setLocationFolder("*Cundinamarca*");
		LocationDescriptionListServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
		System.out.println("................................................................................................................");
		System.out.println("......................................LOCATIONDESCRIPTION FILTERED LIST BY LOCATION FOLDER......................");
		System.out.println("................................................................................................................");
		for (LocationDescription elemento : locatdescResp.LocationDescriptions) {
			System.out.println("ID:"+elemento.getIdentifier()+"-Nombre:"+elemento.getName()+"-Folder:"+elemento.getPrimaryFolder()+"-SecFolder:"+elemento.getSecondaryFolders());
		}
	}
	
	public void queryLocationDescriptionListFilteredByAreaOp(){
		LocationDescriptionListServiceRequest locatdescReq = new LocationDescriptionListServiceRequest();		
		//Se puede filtrar por el municipio, departamento, area operativa
		locatdescReq.setLocationFolder("*Area operativa 01*");
		LocationDescriptionListServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
		System.out.println("................................................................................................................");
		System.out.println("......................................LOCATIONDESCRIPTION FILTERED LIST BY LOCATION FOLDER......................");
		System.out.println("................................................................................................................");
		for (LocationDescription elemento : locatdescResp.LocationDescriptions) {
			System.out.println("ID:"+elemento.getIdentifier()+"-Nombre:"+elemento.getName()+"-Folder:"+elemento.getPrimaryFolder()+"-SecFolder:"+elemento.getSecondaryFolders());
		}
	}
	
	//TODO:Revisar propiedades para filtrar acá
	public void queryLocationDescriptionListFilteredByExtendedAttributes(){
		LocationDescriptionListServiceRequest locatdescReq = new LocationDescriptionListServiceRequest();		
		//Se puede filtrar por el municipio, departamento, area operativa
		ArrayList<ExtendedAttributeFilter> listaFiltros=new ArrayList<ExtendedAttributeFilter>();
		ExtendedAttributeFilter filter =new ExtendedAttributeFilter();
		filter.setFilterName("WEBPORTAL_SYNC");
		filter.setFilterValue("1.0");
		listaFiltros.add(filter);
		locatdescReq.setExtendedFilters(listaFiltros);
		LocationDescriptionListServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
		System.out.println("...................................................................................................................");
		System.out.println(".....................................LOCATIONDESCRIPTION FILTERED LIST BY EXTENDED ATTRIBUTES......................");
		System.out.println("...................................................................................................................");
		for (LocationDescription elemento : locatdescResp.LocationDescriptions) {
			System.out.println("ID:"+elemento.getIdentifier()+"-Nombre:"+elemento.getName()+"-Folder:"+elemento.getPrimaryFolder()+"-SecFolder:"+elemento.getSecondaryFolders());
		}
	}
	
	public void queryLocationDataList(){
		LocationDataServiceRequest locatdescReq = new LocationDataServiceRequest();
		locatdescReq.setLocationIdentifier("12010080");
		LocationDataServiceResponse locatdescResp=cliente.Publish.get(locatdescReq);
		System.out.println(".......................................................................................................");
		System.out.println("................................................LOCATIONDATA UNIT LIST.................................");
		System.out.println(".......................................................................................................");
		System.out.println("Nombre:"+locatdescResp.getLocationName()+"-(latitud:"+locatdescResp.getLatitude()+")-(longitud:"+locatdescResp.getLongitude()+")-(Attributos extendidos:"+obtenerAtributosExtendidos(locatdescResp.getExtendedAttributes())+")");		
	}
	
	
	private String obtenerAtributosExtendidos(ArrayList<ExtendedAttribute> atributos){
		StringBuffer buffer = new StringBuffer();
		for (ExtendedAttribute atributo : atributos) {
			buffer.append("(attr:"+atributo.Name+"-val:"+atributo.Value.toString()+")");
		}
		return buffer.toString();
	}
	
	private String obtenerDescriptionList(ArrayList<TimeSeriesDescription> descripciones){
		StringBuffer buffer = new StringBuffer();
		for (TimeSeriesDescription desc : descripciones) {
			buffer.append("UniqueIdSerie:"+desc.getUniqueId()+"-IdSerie:"+desc.getIdentifier()+"-Etiqueta:"+desc.getLabel()+"-Parametro:"+desc.getParameter()+"-LocationId:"+desc.getLocationIdentifier()+"-Parametro:"+desc.getParameter()+"\n");			
		}
		return buffer.toString();
	}
	

	public void queryTimeSeriesDescriptionListFilterByLocationId(){
		TimeSeriesDescriptionServiceRequest tedRequest = new TimeSeriesDescriptionServiceRequest();
		tedRequest.setLocationIdentifier("25025030");
		TimeSeriesDescriptionListServiceResponse tedResp=cliente.Publish.get(tedRequest);
		System.out.println(".......................................................................");
		System.out.println(".........TIME SERIES DESCRIPTION LIST FILTER BY LOCATION ID............");		
		System.out.println(".......................................................................");
		System.out.println(obtenerDescriptionList(tedResp.getTimeSeriesDescriptions()));	
	}
	
	
	//CONSULTA LA SERIE DE LA ETIQUETA 
		public void queryTimeSeriesRawDataById(){
			TimeSeriesDataRawServiceRequest dataRequest = new TimeSeriesDataRawServiceRequest();			
			dataRequest.setTimeSeriesUniqueId("7302753779344213860b3e108a347c9d");					
			TimeSeriesDataServiceResponse dataResp=cliente.Publish.get(dataRequest);
			System.out.println(".................................................................................................");
			System.out.println("...................................TIME SERIES DATA POINTS....................................");		
			System.out.println(".................................................................................................");
			System.out.println(obtenerTimeSeriesPoint(dataResp.getPoints()));
						
		}
		
		public void queryTimeSeriesCorrectedDataById(){
			TimeSeriesDataCorrectedServiceRequest dataRequest = new TimeSeriesDataCorrectedServiceRequest();			
			dataRequest.setTimeSeriesUniqueId("7302753779344213860b3e108a347c9d");					
			TimeSeriesDataServiceResponse dataResp=cliente.Publish.get(dataRequest);
			System.out.println(".................................................................................................");
			System.out.println("...................................TIME SERIES DATA POINTS....................................");		
			System.out.println(".................................................................................................");
			System.out.println(obtenerTimeSeriesPoint(dataResp.getPoints()));
						
		}
		
		private String obtenerTimeSeriesPoint(ArrayList<TimeSeriesPoint> puntos){
			StringBuffer buffer = new StringBuffer();
			for (TimeSeriesPoint punto : puntos) {
				buffer.append("("+punto.getTimestamp().getDateTimeOffset()+":"+punto.getValue().getNumeric()+")\n");
			}
			return buffer.toString();
		}
		
//		public void queryTimeSeriesCorrectedFromTimeSeries(){
//			CorrectionListServiceRequest dataRequest = new CorrectionListServiceRequest();			
//			dataRequest.setTimeSeriesUniqueId("7302753779344213860b3e108a347c9d");					
//			CorrectionListServiceResponse dataResp=cliente.Publish.get(dataRequest);
//			System.out.println(".................................................................................................");
//			System.out.println("...................................TIME SERIES CORRECTION LIST POINTS............................");		
//			System.out.println(".................................................................................................");
//			dataResp.getCorrections()
//			System.out.println(obtenerTimeSeriesCorrectionPoint(dataResp.getCorrections()));
//						
//		}
	

//		private String obtenerTimeSeriesCorrectionPoint(ArrayList<Correction> puntos){
//			StringBuffer buffer = new StringBuffer();
//			for (TimeSeriesPoint punto : puntos) {
//				buffer.append("("+punto.getTimestamp().getDateTimeOffset()+":"+punto.getValue().getNumeric()+")\n");
//			}
//			return buffer.toString();
//		}
		
		
	



}
