package co.gov.ideam.dhime.generador.localgenerator;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.gov.ideam.dhime.generador.model.repotemp.CombinadorGenRuth;
import co.gov.ideam.dhime.localdata.repository.repotemp.CombinadorGenRuthRepository;
import co.gov.ideam.dhime.localdata.repository.repotemp.SerieDeTiempoRepository;
import co.gov.ideam.dhime.utils.FileUtilsComponent;
import co.gov.ideam.dhime.utils.LogUtilsComponent;

@Component
public class GeneradorArchivosMarcelaApiSamples {

	@Autowired
	private LogUtilsComponent logUtilsComponent;
	
	@Autowired
	private CombinadorGenRuthRepository combinadorGenRuthRepository;
	
	@Autowired
	private SerieDeTiempoRepository serieDeTiempoRepository;
	
	@Autowired
	private FileUtilsComponent fileUtilsComponent;
	
	private Log logger=null;

	public GeneradorArchivosMarcelaApiSamples() {
		super();
		// TODO Auto-generated constructor stub
		Log logger=logUtilsComponent.getLogger(this.getClass());	
	}
	
	public void generarArchivosMarcela() throws ParseException, IOException{
		List<CombinadorGenRuth> listaCombinador=null;	
		String encabezado="Observation ID,Location ID,Observed Property ID,Observed DateTime,Analyzed DateTime,Depth,Depth Unit,Data Classification,Result Value,Result Unit,Result Status,Result Grade,Medium,Sample ID,Collection Method,Field: Device ID,Field: Device Type,Field: Comment,Lab: Specimen Name,Lab: Analysis Method,Lab: Detection Condition,Lab: Limit Type,Lab: MDL,Lab: MRL,Lab: Quality Flag,Lab: Received DateTime,Lab: Prepared DateTime,Lab: Sample Fraction,Lab: From Laboratory,Lab: Sample ID,Lab: Dilution Factor,Lab: Comment,QC: Type,QC: Source Sample ID";
		listaCombinador=combinadorGenRuthRepository.findByForBroke("true");
		for (CombinadorGenRuth combinadorGenRuth : listaCombinador) {
			List<String> lista=serieDeTiempoRepository.obtenerCsvDeTabla(combinadorGenRuth);
			fileUtilsComponent.crearArchivosFragmentados(lista, encabezado);
		}		
		System.out.println("Finaliza generación de archivos");	
	}
}
