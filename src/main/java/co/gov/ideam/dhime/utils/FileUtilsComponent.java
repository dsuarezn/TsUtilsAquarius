package co.gov.ideam.dhime.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

@Component
public class FileUtilsComponent {

	@Value("${comparation.folder}")
	public String RUTA_DIRECTORIO_COMPARACION;
	
	@Value("${partition.folder}")
	public String RUTA_DIRECTORIO_PARTICION;
	
	
	public void crearArchivoData(String nombreArchivo, List<TimeSeriesPoint> contenido, String extension) throws IOException{
		File path =new java.io.File( RUTA_DIRECTORIO_COMPARACION );
		File file= new java.io.File(RUTA_DIRECTORIO_COMPARACION+"/"+nombreArchivo+"."+extension);
		if(!path.exists()){
			path.mkdir();
		}		
		if(!file.exists()){
			file.createNewFile();
		}		
	
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toURI()),StandardCharsets.UTF_8))
		{
			writer.append("Fecha|Valor"+"\n");
			for (TimeSeriesPoint punto : contenido) {
				writer.append(DateTimeUtilsComponent.formatDateToCompareFormat(Date.from(punto.getTimestamp().getDateTimeOffset()))+"|"+punto.getValue().getNumeric()+"\n");
			}
			writer.close();
		}							
	}
	
	public void crearArchivoComparacion(String nombreArchivo, List<PuntoSerieDeTiempo> contenido, String extension) throws IOException{
		File path =new java.io.File( RUTA_DIRECTORIO_COMPARACION );
		File file= new java.io.File(RUTA_DIRECTORIO_COMPARACION+"/"+nombreArchivo+"."+extension);
		if(!path.exists()){
			path.mkdir();
		}		
		if(!file.exists()){
			file.createNewFile();
		}		
	
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toURI()),StandardCharsets.UTF_8))
		{
			writer.append("FechaDato|ValorNuevo"+"\n");
			for (PuntoSerieDeTiempo punto : contenido) {
				writer.append(punto.getStrTimestamp()+"|"+punto.getDiffDatoPunto()+"\n");
			}
			writer.close();
		}							
	}
	
	public void crearArchivoEliminacionIntervalo(String nombreArchivo, Date fechaIni, Date fechaFin, String extension) throws IOException{
		File path =new java.io.File( RUTA_DIRECTORIO_COMPARACION );
		File file= new java.io.File(RUTA_DIRECTORIO_COMPARACION+"/"+nombreArchivo+"."+extension);
		if(!path.exists()){
			path.mkdir();
		}		
		if(!file.exists()){
			file.createNewFile();
		}		
	
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toURI()),StandardCharsets.UTF_8))
		{			
			writer.append("FechaIni="+DateTimeUtilsComponent.formatDateToCompareFormat(fechaIni)+"\n");
			writer.append("FechaFin="+DateTimeUtilsComponent.formatDateToCompareFormat(fechaFin)+"\n");
			writer.close();
		}							
	}
	
	public void crearArchivosFragmentados(List<String> listaPuntos, String cabecera) throws IOException{
		
		List<List<String>> listaPrincipal=new ArrayList<>();
		
		
		Integer conteo=1;
		List<String> listaInterna=new ArrayList<>();
		for (String linea : listaPuntos) {
			
			if(conteo==1){
				listaInterna.add(cabecera);
			}
			if(conteo<601){
				listaInterna.add(linea);
				conteo++;
			}	
			else{
				listaPrincipal.add(listaInterna);
				listaInterna=null;
				listaInterna=new ArrayList<>();
				
				conteo=1;
			}			
		}
		listaPrincipal.add(listaInterna);
		for (int i = 0; i < listaPrincipal.size(); i++) {
			File file= new java.io.File(RUTA_DIRECTORIO_PARTICION+"/SAMPLES_"+i+".csv");
			if(file.exists()){
				file.delete();				
			}
			file.createNewFile();
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toURI()),StandardCharsets.UTF_8))
			{				
				List<String> listaIntern=listaPrincipal.get(i);
				for (String linea : listaIntern) {
					writer.append(linea);
					writer.append("\n");
				}				
				writer.close();
			}
			
		}											
	}
	
public static void crearArchivoList(String folder, String filename, List<String> listaInformacion) throws IOException{
		
		File path =new java.io.File( folder );
		if(!path.exists()){
			if(path.mkdir()){
				File file= new java.io.File(path.getAbsolutePath()+"/"+filename);
				try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toURI()),StandardCharsets.UTF_8))
				{
					for (String parvalor : listaInformacion) {
						writer.append(parvalor+"\n");																
					}
					writer.close();
				}
			}			
		}else{
			File file= new java.io.File(path.getAbsolutePath()+"/"+filename);
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toURI()),StandardCharsets.UTF_8))
			{
				for (String parvalor : listaInformacion) {
					writer.append(parvalor+"\n");																
				}
				writer.close();
			}
		}
	}
	
	public void registrarEvento(String logfile, String data){
		try {
			File file= new java.io.File(CARPETA_COMPARACION+"/"+logfile);
			if(!file.exists()){
				file.createNewFile();
			}				
			Files.write(
				      Paths.get(CARPETA_COMPARACION+"/"+logfile), 
				      data.getBytes(), 
				      StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Value("${comparation.folder}")
	public String CARPETA_COMPARACION;
	
	public SerieDeTiempo leerSerieDeTiempo(Path filepath)
			  throws IOException, ParseException {
			    Path ruta=Paths.get(CARPETA_COMPARACION+filepath.getFileName());
			    try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(ruta)))) {
			        String line=null;
			        SerieDeTiempo serie=new SerieDeTiempo();
			        String [] split=filepath.getFileName().toString().split("@");
			        serie.setEtiqueta(split[0]);
			        serie.setEstacion(split[1].split("\\.")[0]);
			        List<PuntoSerieDeTiempo> listaPuntos=new ArrayList<>();
			        boolean firstline=true;
			        while ((line = br.readLine()) != null) {
			        	if(!line.contains("Estacion")){
			        		String [] pointData=line.split("\\|");//estacion;variable;fecha;datoold;datonew
			        		if(!firstline){
			        			try {
			        				PuntoSerieDeTiempo puntoSerie=new PuntoSerieDeTiempo();
						        	puntoSerie.setDato(Double.valueOf(pointData[1]));
						        	puntoSerie.setTimestamp(DateTimeUtilsComponent.parseDateToCompareFormat(pointData[0]));
						            listaPuntos.add(puntoSerie);
								} catch (Exception e) {
									e.printStackTrace();
								}
					        	
			        		}
			        		else{firstline=false;}
			        	}			        	
			        }
			        serie.setListaPuntos(listaPuntos);
			        return serie;
			    }			  
	}	
	
	public SerieDeTiempo leerSerieDeTiempoDVAG_CON(Path filepath)
			  throws IOException, ParseException {
			    Path ruta=Paths.get(CARPETA_COMPARACION+filepath.getFileName());
			    try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(ruta)))) {
			        String line=null;
			        SerieDeTiempo serie=new SerieDeTiempo();
			        String [] split=filepath.getFileName().toString().split("@");
			        serie.setEtiqueta(split[0]);
			        serie.setEstacion(split[1].split("\\.")[0]);
			        List<PuntoSerieDeTiempo> listaPuntos=new ArrayList<>();
			        boolean firstline=true;
			        while ((line = br.readLine()) != null) {
			        	if(!line.contains("Estacion")){
			        		String [] pointData=line.split("\\|");//estacion;variable;fecha;datoold;datonew
			        		if(!firstline){
			        			try {
			        				PuntoSerieDeTiempo puntoSerie=new PuntoSerieDeTiempo();
						        	puntoSerie.setDato(switchPoint(Double.valueOf(pointData[1])));						       
						        	puntoSerie.setTimestamp(DateTimeUtilsComponent.parseDateToCompareFormat(pointData[0]));
						            listaPuntos.add(puntoSerie);
								} catch (Exception e) {
									e.printStackTrace();
								}
					        	
			        		}
			        		else{firstline=false;}
			        	}			        	
			        }
			        serie.setListaPuntos(listaPuntos);
			        return serie;
			    }			  
	}	
	
	private Double switchPoint(Double valor){
		Double salidaValor=null;
		/**
		 *   GRADOS	DIRECCIÓN	SECTORES
				361	calma		0
				360	NORTE		1
				45	NORESTE		2
				90	ESTE		3
				135	SURESTE		4
				180	SUR			5
				225	SUROESTE	6
				270	OESTE		7
				315	NOROESTE	8				
				362	variable	9

		 * 
		 * 
		 */
		switch(valor.intValue()){
			case 0: salidaValor=361D; break;	
			case 1: salidaValor=360D; break;
			case 2: salidaValor=45D; break;
			case 3: salidaValor=90D; break;
			case 4: salidaValor=135D; break;
			case 5: salidaValor=180D; break;
			case 6: salidaValor=225D; break;
			case 7: salidaValor=270D; break;
			case 8: salidaValor=315D; break;
			case 9: salidaValor=362D; break;			
		}
		return salidaValor;
	}
	
	public SerieDeTiempo leerInfoSerieTiempo(Path filepath) throws IOException, ParseException {
			    Path ruta=Paths.get(CARPETA_COMPARACION+filepath.getFileName());
			    try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(ruta)))) {
			        String line=null;
			        SerieDeTiempo serie=new SerieDeTiempo();
			        String [] split=filepath.getFileName().toString().split("@");
			        serie.setEtiqueta(split[0]);
			        serie.setEstacion(split[1].split("\\.")[0]);
			        List<PuntoSerieDeTiempo> listaPuntos=new ArrayList<>();
			        boolean firstline=true;
			        Date intervaloInicio =null;
			        Date intervaloFin = null;
			        while ((line = br.readLine()) != null) {
			        	if(line.contains("FechaIni=")){
			        		String [] fechaData=line.split("=");		        		
		        			try {
		        				intervaloInicio=DateTimeUtilsComponent.parseDateToCompareFormat(fechaData[1]);						        							            
							} catch (Exception e) {
								e.printStackTrace();
							}					        			        					        	
			        	}	
			        	else if(line.contains("FechaFin=")){
			        		String [] fechaData=line.split("=");		        		
		        			try {
		        				intervaloFin=DateTimeUtilsComponent.parseDateToCompareFormat(fechaData[1]);						        							            
							} catch (Exception e) {
								e.printStackTrace();
							}					        			        					        	
			        	}
			        }
			        serie.setRangoInicio(intervaloInicio);
			        serie.setRangoFin(intervaloFin);
			        return serie;
			    }			  
	}	
}
