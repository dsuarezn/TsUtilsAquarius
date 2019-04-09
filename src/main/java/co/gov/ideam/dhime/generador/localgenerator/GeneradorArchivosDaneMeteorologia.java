package co.gov.ideam.dhime.generador.localgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.gov.ideam.dhime.generador.model.repotemp.CombinadorGenRuth;
import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import co.gov.ideam.dhime.localdata.repository.repotemp.CombinadorGenRuthRepository;
import co.gov.ideam.dhime.localdata.repository.repotemp.SerieDeTiempoRepository;
import co.gov.ideam.dhime.utils.LogUtilsComponent;

@Component
public class GeneradorArchivosDaneMeteorologia {
	
	@Autowired
	CombinadorGenRuthRepository combinadorGenRuthRepository;
	
	@Autowired
	SerieDeTiempoRepository serieDeTiempoRepository;
	
	@Autowired
	LogUtilsComponent logUtilsComponent;
	
	Log logger=null;
	
	public GeneradorArchivosDaneMeteorologia() {
		super();
		// TODO Auto-generated constructor stub
		Log logger=logUtilsComponent.getLogger(this.getClass());
	}


	public void generarDesdeDb() throws IOException{
		
		List<String> listaMeteorologicas=new ArrayList<>();
		
//		listaMeteorologicas.add("14015080");  //CodAnterior 14015020
//		listaMeteorologicas.add("29045190");  //CodAnterior 29045020
//		listaMeteorologicas.add("15065180");  //CodAnterior 15065010
//		listaMeteorologicas.add("28025502");  //CodAnterior 28035030			
//		listaMeteorologicas.add("23195502");  //CodAnterior 23195130			
//		listaMeteorologicas.add("27015330");  //CodAnterior 27015070
//		listaMeteorologicas.add("23085270");  //CodAnterior 23085200			
//		listaMeteorologicas.add("26125061");  //CodAnterior 26125060			
//		listaMeteorologicas.add("21205791");  //CodAnterior 21205790			  
//		listaMeteorologicas.add("52055230");  //CodAnterior 52055010			
//		listaMeteorologicas.add("48015050");  //CodAnterior 48015010
		
		
		
		listaMeteorologicas.add("17015010");
		listaMeteorologicas.add("17025020");
		listaMeteorologicas.add("15015050");
		listaMeteorologicas.add("23155030");
		listaMeteorologicas.add("16015010");
		listaMeteorologicas.add("26135040");
		listaMeteorologicas.add("21245040");			
		listaMeteorologicas.add("21115020");
		listaMeteorologicas.add("52045020");
		listaMeteorologicas.add("11045010");
		listaMeteorologicas.add("37055010");
		listaMeteorologicas.add("35035020");
		listaMeteorologicas.add("26035030");
		
		//Lista de los que son homologados
		listaMeteorologicas.add("14015020");
		listaMeteorologicas.add("29045020");
		listaMeteorologicas.add("15065010");
		listaMeteorologicas.add("28035030");
		listaMeteorologicas.add("23195130");
		listaMeteorologicas.add("27015070");
		listaMeteorologicas.add("23085200");
		listaMeteorologicas.add("26125060");
		listaMeteorologicas.add("21205790");
		listaMeteorologicas.add("52055010");
		listaMeteorologicas.add("48015010");
		
		//no paso
		//listaMeteorologicas.add("26075040");
		
		Map<String, String> homologadas=new HashMap<>();
		
		homologadas.put("14015020","14015080");
		homologadas.put("29045020","29045190");
		homologadas.put("15065010","15065180");
		homologadas.put("28035030","28025502");			
		homologadas.put("23195130","23195502");			
		homologadas.put("27015070","27015330");
		homologadas.put("23085200","23085270");			
		homologadas.put("26125060","26125061");			
		homologadas.put("21205790","21205791");			  
		homologadas.put("52055010","52055230");			
		homologadas.put("48015010","48015050");
		//sin codigo de homologacion
		homologadas.put("17015010", "17015010");
		homologadas.put("17025020", "17025020");
		homologadas.put("15015050", "15015050");
		homologadas.put("23155030", "23155030");
		homologadas.put("16015010", "16015010");
		homologadas.put("26135040", "26135040");
		homologadas.put("21245040", "21245040");
		homologadas.put("26075040", "26075040");
		homologadas.put("21115020", "21115020");
		homologadas.put("52045020", "52045020");
		homologadas.put("11045010", "11045010");
		homologadas.put("37055010", "37055010");
		homologadas.put("35035020", "35035020");
		homologadas.put("26035030", "26035030");
		
		
		List<CombinadorGenRuth> listaCombinaciones=	combinadorGenRuthRepository.findAll();	
		String folder="E:/ARCHIVOSRUTH/";		
		for (String estacion : listaMeteorologicas) {
			
			for (CombinadorGenRuth combinador : listaCombinaciones) {
				
				SerieDeTiempo serie=serieDeTiempoRepository.generarSerieUnidaRuth(combinador,estacion, homologadas.get(estacion));
				if(combinador.getVariable1()!=null && combinador.getVariable2()!=null){
					serie.setEtiqueta(combinador.getVariable1()+"-"+combinador.getVariable2());					
				}
				else if(combinador.getVariable1()!=null && combinador.getVariable2()==null){
					serie.setEtiqueta(combinador.getVariable1());					
				}							
				if(serie!=null){					
					crearArchivoSerieTiempoCombinacion(serie, serie.getEtiqueta(), folder, homologadas.get(serie.getEstacion()));
//					crearArchivoSerieTiempoCombinacion(serie, serie.getEtiqueta(), folder, serie.getEstacion());
				} 
			}			
		}
//		logger.info("Finalizo la generación de los archivos");
		System.out.println("Finalizo la generación de los archivos");
	}

	
	private void crearArchivoSerieTiempoCombinacion(SerieDeTiempo serie, String variable, String folder,String estacion) throws IOException{			
		File path =new java.io.File( folder );
		File file2= new java.io.File(folder+"/"+"conteo.csv");
		if(!path.exists()){
			path.mkdir();
		}
		
		if(!file2.exists()){
			file2.createNewFile();
		}
		
		File file= new java.io.File(folder+"/"+estacion+"@"+variable+".csv");
		
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toURI()),StandardCharsets.UTF_8))
		{
			writer.append("Estacion;Variable;FechaDato;Valor"+"\n");
			for (PuntoSerieDeTiempo punto : serie.getListaPuntos()) {
				writer.append(estacion+";"+punto.getStrVariable()+";"+punto.getStrTimestamp()+";"+punto.getStrDato()+"\n");
			}
			writer.close();
		}				
		String contentToAppend = estacion+"@"+variable+";"+serie.getListaPuntos().size()+"\n";
	    Files.write(Paths.get(file2.getPath()), contentToAppend.getBytes(), StandardOpenOption.APPEND);		
		
	    
	    
	}
	
	
}



