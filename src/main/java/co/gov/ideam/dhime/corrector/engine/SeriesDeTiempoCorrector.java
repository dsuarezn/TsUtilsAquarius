package co.gov.ideam.dhime.corrector.engine;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Acquisition.PostTimeSeriesOverwriteAppend;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionServiceRequest;

import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import co.gov.ideam.dhime.generador.timeseries.TimeSeriesUpdater;
import co.gov.ideam.dhime.utils.FileUtilsComponent;
import net.servicestack.client.WebServiceException;

@Component
public class SeriesDeTiempoCorrector {
		
	@Autowired
	FileUtilsComponent fileUtilsComponent;
	
	@Value("${comparation.folder}")
	public String CARPETA_COMPARACION;
	
	@Value("${comparation.procesed.folder}")
	public String CARPETA_PROCESADOS;
	
	@Autowired
	private TimeSeriesUpdater timeSeriesUpdater;
	

	public void borrarPuntosSeriesConArchivos() throws IOException, ParseException, WebServiceException, Exception{
		List<Path> archivos=obtenerListaCvsSeriesDeTiempo(CARPETA_COMPARACION, "del");			
		for (Path path : archivos) {
			System.out.println("Procesando"+path.getFileName());
			SerieDeTiempo serie=cargarPuntosSerieDeTiempo(path);
			timeSeriesUpdater.timeSeriesPointEraser(serie);
			Files.move(Paths.get(CARPETA_COMPARACION+path.getFileName()),Paths.get(CARPETA_PROCESADOS+path.getFileName()) , StandardCopyOption.REPLACE_EXISTING);
			
			
		}
	}
	
	public void borrarIntervaloSeries() throws IOException, ParseException, WebServiceException, Exception{
		List<Path> archivos=obtenerListaCvsSeriesDeTiempo(CARPETA_COMPARACION, "delinterval");			
		for (Path path : archivos) {
			System.out.println("Procesando:"+path.getFileName());
			SerieDeTiempo serie=cargarInfoSerieDeTiempo(path);
			timeSeriesUpdater.timeSeriesIntervalEraser(serie);
			Files.move(Paths.get(CARPETA_COMPARACION+path.getFileName()),Paths.get(CARPETA_PROCESADOS+path.getFileName()) , StandardCopyOption.REPLACE_EXISTING);
						
		}
	}
	
	
	public void actualizarSeriesConArchivos() throws IOException, ParseException, WebServiceException, Exception{
		List<Path> archivos=obtenerListaCvsSeriesDeTiempo(CARPETA_COMPARACION, "udp");			
		for (Path path : archivos) {
			System.out.println("Procesando"+path.getFileName());
			SerieDeTiempo serie=cargarPuntosSerieDeTiempo(path);
			timeSeriesUpdater.timeSeriesPointUpdater(serie);
			Files.move(Paths.get(CARPETA_COMPARACION+path.getFileName()),Paths.get(CARPETA_PROCESADOS+path.getFileName()) , StandardCopyOption.REPLACE_EXISTING);
			
		}
	}
	
	public void agregarPuntosSerieConArchivos() throws IOException, WebServiceException, Exception, ParseException{
		List<Path> archivos=obtenerListaCvsSeriesDeTiempo(CARPETA_COMPARACION, "app");		
		for (Path path : archivos) {
			System.out.println("Procesando"+path.getFileName());
			SerieDeTiempo serie=cargarPuntosSerieDeTiempo(path);
			timeSeriesUpdater.timeSeriesPointAppender(serie, false);
			Files.move(Paths.get(CARPETA_COMPARACION+path.getFileName()),Paths.get(CARPETA_PROCESADOS+path.getFileName()) , StandardCopyOption.REPLACE_EXISTING);
			
		}
	}
	
	
	
	private List<Path> obtenerListaCvsSeriesDeTiempo(String ruta, String ext) throws IOException{		
		return Files.list(Paths.get(ruta))
	     .filter(s -> s.toString().endsWith(ext))
	     .map(Path::getFileName)
	     .sorted()
	     .collect(Collectors.toList());
	}
	
	private SerieDeTiempo cargarPuntosSerieDeTiempo(Path archivo) throws IOException, ParseException{		
		return fileUtilsComponent.leerSerieDeTiempo(archivo);			
	}
	
	private SerieDeTiempo cargarInfoSerieDeTiempo(Path archivo) throws IOException, ParseException{		
		return fileUtilsComponent.leerInfoSerieTiempo(archivo);				
	}
	

	
}
