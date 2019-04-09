package co.gov.ideam.dhime.generador.localgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.gov.ideam.dhime.comparador.webapp.dto.CurvaNQInfo;
import co.gov.ideam.dhime.generador.model.repotemp.CurvasNqHoml;
import co.gov.ideam.dhime.localdata.repository.repotemp.CurvasNqHomlRepository;
import co.gov.ideam.dhime.utils.DateTimeUtilsComponent;
import co.gov.ideam.dhime.utils.LogUtilsComponent;


@Component
public class GeneradorArchivosCurvasNQ {
	
	
	@Autowired
	private LogUtilsComponent logUtilsComponent;
	
	@Autowired
	private CurvasNqHomlRepository curvasNqHomlRepository;
	
	
	private static String parentFolder="E:/ARCHIVOSCURVAS/";
	
	public void generarArchivosDesdeDB(){
		
		List<CurvaNQInfo> listaConfiguraciones=curvasNqHomlRepository.obtenerListaFechasAgrupadas();
		
		listaConfiguraciones=listaConfiguraciones.stream().filter(t->listaEstacionesFaltantesCurvaNQ().contains(t.getCodigo())).collect(Collectors.toList());
		
		System.out.println("tamanolista:"+listaConfiguraciones.size());
		Integer count=0;
		String currentCodigo=null;
		Optional<CurvaNQInfo> firstCurva=listaConfiguraciones.stream().findFirst();
		if(firstCurva.isPresent()){
			currentCodigo=firstCurva.get().getCodigo();
		}
		for (CurvaNQInfo curvaNQInfo : listaConfiguraciones) {
			
			List<CurvasNqHoml> datoTablaCal=curvasNqHomlRepository.findByCodigoAndFechaInicioVigenciaAndFechaFinVigencia(curvaNQInfo.getCodigo(), curvaNQInfo.getFechaInicioVigencia(), curvaNQInfo.getFechaFinVigencia());
						
			if(datoTablaCal!=null && datoTablaCal.size()>0){
				String dateini=(curvaNQInfo.getFechaInicioVigencia()!=null?DateTimeUtilsComponent.formatDateToShortCompareFormat(curvaNQInfo.getFechaInicioVigencia()):"error");
				String datefin=(curvaNQInfo.getFechaFinVigencia()!=null?DateTimeUtilsComponent.formatDateToShortCompareFormat(curvaNQInfo.getFechaFinVigencia()):"vigencia");				
				if(currentCodigo.equals(curvaNQInfo.getCodigo())){ count++; }
				else{ 
					count=1; 
					currentCodigo=curvaNQInfo.getCodigo();
				}
				
				try {
					crearArchivoTablaCalibracion(datoTablaCal, curvaNQInfo.getCodigo(), dateini,datefin, curvaNQInfo.getCodigo(), count);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}							
		}
		
	}
	
	private void crearArchivoTablaCalibracion(List<CurvasNqHoml> listaValoresTabla, String estacion, String fechaIni, String fechaFin, String folder, Integer index) throws IOException{						
		
		File pathRoot =new java.io.File(parentFolder);		
		if(!pathRoot.exists()){
			pathRoot.mkdir();
		}
		
		File pathEstacion =new java.io.File(parentFolder+"/"+folder);		
		if(!pathEstacion.exists()){
			pathEstacion.mkdir();
		}
		
		File file= new java.io.File(parentFolder+"/"+folder+"/"+estacion+"_"+index+"@"+fechaIni+"@"+fechaFin+".csv");		
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.toURI()),StandardCharsets.UTF_8))
		{			
			for (CurvasNqHoml punto : listaValoresTabla) {
				writer.append(punto.getNivel()+","+punto.getCaudal()+"\n");
			}
			writer.close();
		}	
	}
		

	public static List<String> listaEstacionesFaltantesCurvaNQ(){
		List<String> listaEstacionesEtiquetas=new ArrayList<>();
		
		listaEstacionesEtiquetas.add("35027500");
		
//		listaEstacionesEtiquetas.add("11147020");
//		listaEstacionesEtiquetas.add("12017020");
//		listaEstacionesEtiquetas.add("26197020");
//		listaEstacionesEtiquetas.add("11027030");
//		listaEstacionesEtiquetas.add("23087150");
//		listaEstacionesEtiquetas.add("27037030");
//		listaEstacionesEtiquetas.add("25027640");
//		listaEstacionesEtiquetas.add("12027010");

		
//		listaEstacionesEtiquetas.add("17027040");	
//		listaEstacionesEtiquetas.add("26057040");	
//		listaEstacionesEtiquetas.add("16027090");
//		listaEstacionesEtiquetas.add("12027040");
//		listaEstacionesEtiquetas.add("23067040");
//		listaEstacionesEtiquetas.add("21117110");
//		listaEstacionesEtiquetas.add("12017010");
//		listaEstacionesEtiquetas.add("21217230");
//		listaEstacionesEtiquetas.add("21167020");
//		listaEstacionesEtiquetas.add("21197110");
//		listaEstacionesEtiquetas.add("21217210");
//		listaEstacionesEtiquetas.add("15067060");
//		listaEstacionesEtiquetas.add("15017010");
//		listaEstacionesEtiquetas.add("24017640");
//		listaEstacionesEtiquetas.add("21237010");
//		listaEstacionesEtiquetas.add("22057040");
//		listaEstacionesEtiquetas.add("44037110");
//		listaEstacionesEtiquetas.add("28047010");
//		listaEstacionesEtiquetas.add("44037130");
//		listaEstacionesEtiquetas.add("24017610");
//		listaEstacionesEtiquetas.add("23187110");
//		listaEstacionesEtiquetas.add("47017701");
//		listaEstacionesEtiquetas.add("47017110");
//		listaEstacionesEtiquetas.add("52017001");
//		listaEstacionesEtiquetas.add("23157080");
//		listaEstacionesEtiquetas.add("23127060");
//		listaEstacionesEtiquetas.add("35027200");
//		listaEstacionesEtiquetas.add("35047030");
//		listaEstacionesEtiquetas.add("32067020");
//		listaEstacionesEtiquetas.add("35027210");
//		listaEstacionesEtiquetas.add("26167010");
//		listaEstacionesEtiquetas.add("35067100");
//		listaEstacionesEtiquetas.add("26177010");
//		listaEstacionesEtiquetas.add("26017020");
//		listaEstacionesEtiquetas.add("24057050");
//		listaEstacionesEtiquetas.add("24037460");
//		listaEstacionesEtiquetas.add("35097080");
//		listaEstacionesEtiquetas.add("26247030");
//		listaEstacionesEtiquetas.add("26217050");
//		listaEstacionesEtiquetas.add("29067060");
//		listaEstacionesEtiquetas.add("29067010");
//		listaEstacionesEtiquetas.add("35057010");
//		listaEstacionesEtiquetas.add("29067050");
		return listaEstacionesEtiquetas;
	}
	
}
