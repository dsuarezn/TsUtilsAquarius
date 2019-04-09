package co.gov.ideam.dhime.localdata.repository.repotemp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.hibernate.mapping.Subclass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.gov.ideam.dhime.generador.model.repotemp.CombinadorGenRuth;
import co.gov.ideam.dhime.generador.model.repotemp.EtiquetasTs;
import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.TablasAsociadas;
import co.gov.ideam.dhime.utils.DateTimeUtilsComponent;
import co.gov.ideam.dhime.utils.FileUtilsComponent;
import co.gov.ideam.dhime.utils.LogUtilsComponent;
import oracle.net.aso.q;


@Repository
public class SerieDeTiempoRepository {
		 
	 @Autowired
	 private JdbcTemplate jdbcTemplate;

	 @Autowired
	 LogUtilsComponent utilsComponent;
	 
	 @Autowired
	 private FileUtilsComponent fileUtilsComponent;
	 
	 Log logger=null;
	 
	 public SerieDeTiempoRepository() {
		super();
		// TODO Auto-generated constructor stub
		logger=utilsComponent.getLogger(this.getClass());
	}




	private SerieDeTiempo obtenerSerieDeTiempoPuntual(TablasAsociadas tablas, String codEstacion, EtiquetasTs etiquetas, String idMaestro) throws ParseException{
		String [] columnas = tablas.getEstructuraColumnas().split(",");
		Date now=new Date();
//		String idDetalle=utilsComponent.getFormatCodDetalle().format(now);		
		String query ="SELECT "+tablas.getEstructuraColumnas()+" FROM "+tablas.getTabla()+" WHERE "+columnas[0]+"='"+codEstacion+"' AND "+columnas[1]+"='"+etiquetas.getNombre()+"' ";
		String fechaq ="";
		if(tablas.getFechaInicio()!=null && tablas.getFechaFin()!=null){
			fechaq="AND TO_DATE(FECHA,'DD/MM/YYYY HH24:MI:SS') between TO_DATE('"+DateTimeUtilsComponent.formatFromDBIdeam(tablas.getFechaInicio())+"','DD/MM/YYYY HH24:MI:SS')" + " AND "+ "TO_DATE('"+DateTimeUtilsComponent.formatFromDBIdeam(tablas.getFechaFin())+"','DD/MM/YYYY HH24:MI:SS') ";			
		}
		else{
			if(tablas.getFechaInicio()!=null){
				fechaq="AND TO_DATE(FECHA,'DD/MM/YYYY HH24:MI:SS') >= TO_DATE('"+DateTimeUtilsComponent.formatFromDBIdeam(tablas.getFechaInicio())+"','DD/MM/YYYY HH24:MI:SS') ";							
			}
			else if(tablas.getFechaFin()!=null){
				fechaq="AND TO_DATE(FECHA,'DD/MM/YYYY HH24:MI:SS') <= TO_DATE('"+DateTimeUtilsComponent.formatFromDBIdeam(tablas.getFechaFin())+"','DD/MM/YYYY HH24:MI:SS') ";											
			}
		}
		String order=" ORDER BY "+columnas[2];
		List<PuntoSerieDeTiempo> listaPuntos=new ArrayList<>();
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//		System.out.println("inicio consulta:"+timestamp);
		System.out.println("Query ejecutado:"+query+fechaq+order);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query+fechaq+order);
		timestamp = new Timestamp(System.currentTimeMillis());
//		System.out.println("fin consulta inicio maper:"+timestamp);		
		
		for (Map row : rows) {
			PuntoSerieDeTiempo punto = new PuntoSerieDeTiempo();
			String fecha=(String) row.get(columnas[2]);	
			String dato=changeToString(row.get(columnas[3]));
			boolean bisiesto=false;
			boolean containsFechaBisiesta=false;
			if(fecha.contains("29/02/") || fecha.contains("30/02/")|| fecha.contains("31/02/")){
				containsFechaBisiesta=true;
				GregorianCalendar calendar = new GregorianCalendar();
				String [] datoanio=fecha.split(" ")[0].split("/");		
				Integer anio=Integer.parseInt(datoanio[2]);				
				bisiesto=calendar.isLeapYear(anio);
//				System.out.println("leapyear:"+anio+": "+bisiesto);
			}
			if(containsFechaBisiesta==true && bisiesto==false){
				continue;
			}
			
			punto.setStrTimestamp(fecha);
			punto.setTimestamp(DateTimeUtilsComponent.parseFromDBIdeam(fecha, bisiesto));
			punto.setDato((dato!=null?Double.parseDouble(dato.replace(",", ".")):null));
			if("Y".equals(tablas.getCalificador())){
				punto.setCalificador((row.get(columnas[4])!=null?changeToString(row.get(columnas[4])):null));
			}
			listaPuntos.add(punto);
			
			
		}
		
		timestamp = new Timestamp(System.currentTimeMillis());
//		System.out.println("fin mapper:"+timestamp);
		listaPuntos.sort((d1,d2)->d1.getTimestamp().compareTo(d2.getTimestamp()));
//		if(listaPuntos.size()==0){
////			utilsComponent.registrarEventoDetalle(idDetalle, idMaestro, utilsComponent.TIPODET_WARNQUERY, "SIN REGISTROS EN TABLA REGISTRADA: La consulta ("+query+") no arrojo resultados, la prioridad de la tabla es "+tablas.getPrecedencia());
//			return null;
//		}		
		
		listaPuntos=ajustarDetalleFrecuencia(listaPuntos, etiquetas.getFrecuenciaTs(), etiquetas.getDetalleFrec());
		SerieDeTiempo serie=new SerieDeTiempo(codEstacion, tablas.getVariable(), tablas.getPrecedencia(), listaPuntos);
		return serie;
	 }
	 
	 
	 
	 
	 public SerieDeTiempo obtenerSerieDeTiempo(List<TablasAsociadas> listaTablas, String codEstacion, EtiquetasTs etiquetas) throws ParseException{		 		 
		 Date now= new Date();
//		 String idMaestro=utilsComponent.getFormatCodMaestro().format(now);		
//		 utilsComponent.registrarEventoMaestroLog(idMaestro,utilsComponent.TIPOOP_QUERY+":"+codEstacion+":"+etiquetas.getNombre(), null, null,now);		 
		 SerieDeTiempo serieBase=null;
		 List<SerieDeTiempo> listaSeries=new ArrayList<>();		 
		 for (TablasAsociadas tablasAsociadas : listaTablas) {
//			SerieDeTiempo serie= obtenerSerieDeTiempoPuntual(tablasAsociadas, codEstacion, etiquetas, idMaestro);
			SerieDeTiempo serie= obtenerSerieDeTiempoPuntual(tablasAsociadas, codEstacion, etiquetas, null);
			if(serie!=null && serie.getListaPuntos().size()>0){
				listaSeries.add(serie);
			}
		 }
		 if(listaSeries.size()>0){
			 listaSeries.sort(Comparator.comparing(SerieDeTiempo::getPrioridad));	
			 serieBase=listaSeries.get(0);
			 for (int i = 1; i < listaSeries.size(); i++) {
				serieBase=joinSeries(serieBase, listaSeries.get(i));
//				System.out.println(serieBase.getListaPuntos().size());
			 }
		}	
		if(serieBase!=null && serieBase.getListaPuntos()!=null){
			serieBase.getListaPuntos().sort((o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));                
		}
		else{
			if(serieBase==null){
				fileUtilsComponent.registrarEvento("noDataFound.log", "No hay datos asociados a la estación "+codEstacion+" en ninguna tabla."+"\n");				
			}
		}
		
		return serieBase;
	 }
	 
	 
	 private List<PuntoSerieDeTiempo> ajustarDetalleFrecuencia(List<PuntoSerieDeTiempo> puntos, String frecuencia, String detalle){
		 return puntos;
	 }
	 
	 private SerieDeTiempo joinSeries(SerieDeTiempo serieBase, SerieDeTiempo serieIntegrar){
		 
		 Map<String, PuntoSerieDeTiempo> mapaSerieBase=generarMapaPuntos(serieBase.getListaPuntos());		 
		 Map<String, PuntoSerieDeTiempo> mapaSerieComplementaria=generarMapaPuntos(serieIntegrar.getListaPuntos());
		 

		 for (String key : mapaSerieComplementaria.keySet()) {

			if(mapaSerieBase.get(key)==null){
				mapaSerieBase.put(key, mapaSerieComplementaria.get(key));
			}					
		 }	
		 
		 SerieDeTiempo serieResultante=new SerieDeTiempo();
		 serieResultante.setEstacion(serieBase.getEstacion());
		 serieResultante.setEtiqueta(serieBase.getEtiqueta());
		 serieResultante.setPrioridad(serieBase.getPrioridad());
		 serieResultante.setListaPuntos(mapaSerieBase.values().stream().collect(Collectors.toList()));		 
		 
		 return serieResultante;
	 }
	 
	 
	 private Map<String, PuntoSerieDeTiempo> generarMapaPuntos(List<PuntoSerieDeTiempo> listaPuntos){
		 Map<String,PuntoSerieDeTiempo> mapaPuntos=new HashMap<>();
		 for (PuntoSerieDeTiempo punto : listaPuntos) {
			mapaPuntos.put(punto.getStrTimestamp(), punto);
		 }
		 return mapaPuntos;
	 }	
	 
	 public SerieDeTiempo generarSerieUnidaRuth(CombinadorGenRuth combinador, String estacion, String estacionOld){
		 if(combinador.getTabla1()!=null && combinador.getTabla2()!=null){
			 String [] columnas = combinador.getEstrGen().split(",");
			 String inestacion=null;
			 if(!estacion.equals(estacionOld)){
				 inestacion="'"+estacion+"','"+estacionOld+"'";
			 }
			 else{
				 inestacion="'"+estacion+"'";
			 }
			 String queryA ="SELECT "+columnas[0]+","+columnas[1]+",TO_DATE("+columnas[2]+",'DD/MM/YYYY HH24:MI:SS') "+columnas[2]+", TO_CHAR("+columnas[3]+") "+columnas[3]+", 'TABLA1' TABLA FROM "+combinador.getTabla1()+" WHERE "+columnas[0]+" in ("+inestacion+") AND "+columnas[1]+"='"+combinador.getVariable1()+"'";
			 String union=" UNION ALL ";		
		     String queryB ="SELECT "+columnas[0]+","+columnas[1]+",TO_DATE("+columnas[2]+",'DD/MM/YYYY HH24:MI:SS') "+columnas[2]+", TO_CHAR("+columnas[3]+") "+columnas[3]+", 'TABLA2' TABLA FROM "+combinador.getTabla2()+" WHERE "+columnas[0]+" in ("+inestacion+") AND "+columnas[1]+"='"+combinador.getVariable2()+"'";	     	    			
		     String query = "SELECT "+columnas[0]+","+columnas[1]+",TO_CHAR("+columnas[2]+",'YYYY-MM-DD HH24:MI:SS')"+columnas[2]+","+columnas[3]+ ", TABLA FROM ("+queryA+union+queryB+") ORDER BY "+columnas[2];
		     
		     logger.info("Consulta:"+query);
		     List<PuntoSerieDeTiempo> listaPuntos=null;
		     List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
			
		     Map<String, PuntoSerieDeTiempo> listaPuntosMapa = new LinkedHashMap<>();		     		    
				for (Map row : rows) {
					PuntoSerieDeTiempo punto = new PuntoSerieDeTiempo();
					String fecha=(String) row.get(columnas[2]);
					String fechaTrabajo=null;
					if("Diaria".equals(combinador.getPeriodicidad())){
						fechaTrabajo=((String)row.get(columnas[2])).substring(0, 10);
					}
					else if("Horaria".equals(combinador.getPeriodicidad())){
						fechaTrabajo=((String)row.get(columnas[2])).substring(0, 13);
					}					
					String dato=(String) row.get(columnas[3]);
					String variable=(String) row.get(columnas[1]);
					String tabla=(String) row.get("TABLA");
					punto.setStrTimestamp(fecha);
					punto.setStrDato(dato.replace(".", ","));
					punto.setStrVariable(variable);
					punto.setFechaTrabajo(fechaTrabajo);
					if("TABLA1".equals(tabla)){
						punto.setPrioridad(combinador.getPrdTabla1());
					}
					else if("TABLA2".equals(tabla)){
						punto.setPrioridad(combinador.getPrdTabla2());
					}
					
					PuntoSerieDeTiempo puntoAlmacenado=(PuntoSerieDeTiempo) listaPuntosMapa.get(fechaTrabajo);
					if(puntoAlmacenado==null){
						listaPuntosMapa.put(fechaTrabajo, punto);
					}else{
						if(puntoAlmacenado.getPrioridad()>punto.getPrioridad()){
							listaPuntosMapa.put(fechaTrabajo, punto);
						}
					}													
				}				
				listaPuntos = new ArrayList<PuntoSerieDeTiempo>(listaPuntosMapa.values()) ;
				
				SerieDeTiempo serie=new SerieDeTiempo();
				serie.setEstacion(estacion);
				serie.setListaPuntos(listaPuntos);
				return serie;
		 }
		 else if(combinador.getTabla1()!=null && combinador.getTabla2()==null){
			 String inestacion=null;
			 if(!estacion.equals(estacionOld)){
				 inestacion="'"+estacion+"','"+estacionOld+"'";
			 }
			 else{
				 inestacion="'"+estacion+"'";
			 }
			 String [] columnas = combinador.getEstrGen().split(",");
			 String queryA ="SELECT "+columnas[0]+","+columnas[1]+",TO_DATE("+columnas[2]+",'DD/MM/YYYY HH24:MI:SS') "+columnas[2]+", TO_CHAR("+columnas[3]+") "+columnas[3]+" FROM "+combinador.getTabla1()+" WHERE "+columnas[0]+" in ("+inestacion+") AND "+columnas[1]+"='"+combinador.getVariable1()+"'";
			 String query = "SELECT "+columnas[0]+","+columnas[1]+",TO_CHAR("+columnas[2]+",'YYYY-MM-DD HH24:MI:SS')"+columnas[2]+","+columnas[3]+ " FROM ("+queryA+") ORDER BY "+columnas[2];
		     
			 logger.info("Consulta:"+query);
		     
		     List<PuntoSerieDeTiempo> listaPuntos=new ArrayList<>();
		     List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
				
				for (Map row : rows) {
					PuntoSerieDeTiempo punto = new PuntoSerieDeTiempo();
					String fecha=(String) row.get(columnas[2]);	
					String dato=(String) row.get(columnas[3]);
					String variable=(String) row.get(columnas[1]);
					punto.setStrTimestamp(fecha);
					punto.setStrDato(dato);
					punto.setStrVariable(variable);
					listaPuntos.add(punto);
				}
				SerieDeTiempo serie=new SerieDeTiempo();
				serie.setEstacion(estacion);
				serie.setListaPuntos(listaPuntos);
				return serie;
		 }
		 else {
			 return null;
		 }
	 }
	 
	 public static String join(List<String> stationlist){
		    if (stationlist.isEmpty()) return "";
		    StringBuilder sb = null;
		    for (String element : stationlist) {
		        if (sb == null) {
		            sb = new StringBuilder();
		        } else {
		            sb.append(", ");
		        }
		        sb.append("'"+element+"'");
		    }
		    return sb.toString();
		}
	 
	 
	 public List<String> obtenerCsvDeTabla(CombinadorGenRuth combinador) throws ParseException{
			String [] columnas = combinador.getEstrGen().split(",");			
			String query ="SELECT "+combinador.getEstrGen()+" FROM "+combinador.getTabla1();		
			List<String> listaRegistros=new ArrayList<>();
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
			logger.info(query);
			
			for (Map row : rows) {
				
				String [] lsregistro=new String[columnas.length];
				for (int i = 0; i < columnas.length; i++) {
					lsregistro[i]=(row.get(columnas[i])==null?"":(String) row.get(columnas[i]));
				}
				String linea=Arrays.asList(lsregistro).stream().collect(Collectors.joining(","));
				listaRegistros.add(linea);
			}

			return listaRegistros;
		 }
	 
	 public <T extends Object> String changeToString(T object) {    
		    if (object instanceof Integer)		        
		    	return ((Integer)object).toString();
		    else if(object instanceof Double)
		    	return ((Double)object).toString();
		    else if(object instanceof Float)
		    	return ((Float)object).toString();		    
		    else if(object instanceof BigDecimal)
		    	return ((BigDecimal)object).toString();
		    else if(object instanceof String)
		    	return (object).toString();
			return null;
		}
	 
}
