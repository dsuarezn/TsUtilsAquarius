package co.gov.ideam.dhime.comparador.webapp.controller;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import co.gov.ideam.dhime.comparador.engine.SeriesDeTiempoComparer;
import co.gov.ideam.dhime.comparador.webapp.dto.DataQueryWebDto;
import co.gov.ideam.dhime.corrector.engine.SeriesDeTiempoCorrector;
import co.gov.ideam.dhime.generador.localgenerator.GeneradorArchivosCurvasNQ;
import co.gov.ideam.dhime.generador.localgenerator.GeneradorArchivosDaneMeteorologia;
import co.gov.ideam.dhime.generador.localgenerator.GeneradorArchivosMarcelaApiSamples;
import co.gov.ideam.dhime.generador.localgenerator.LocalSeriesQuerier;
import co.gov.ideam.dhime.utils.LogUtilsComponent;
import net.servicestack.client.WebServiceException;

@Controller
public class LocalDataComparatorController {
	
	@Autowired
	private LocalSeriesQuerier localSeriesGenerator;
		
	@Autowired
	private LogUtilsComponent logUtilsComponent;
	
	@Autowired
	private GeneradorArchivosDaneMeteorologia generadorArchivosDaneMeteorologia;
	
	@Autowired
	private GeneradorArchivosMarcelaApiSamples generadorArchivosMarcelaApiSamples;
	
	@Autowired
	private SeriesDeTiempoComparer seriesDeTiempoComparer;
	
	@Autowired
	private SeriesDeTiempoCorrector seriesDeTiempoCorrector;
	
	@Autowired
	private GeneradorArchivosCurvasNQ generadorArchivosCurvasNQ;
	
	Log logger=null;
	
	public LocalDataComparatorController() {
		super();
		// TODO Auto-generated constructor stub
		logger=logUtilsComponent.getLogger(this.getClass());
		
	}

		// inject via application.properties
		@Value("${welcome.message:test}")
		private String message = "Hello World";

		@RequestMapping("/")
		public String welcome(Map<String, Object> model) {
			model.put("message", this.message);
			return "welcome";
		}
		
		@RequestMapping("/ejecutar")
		public String comparacion(Map<String, Object> model, DataQueryWebDto dto) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando comparación con codEstacion:"+dto.getCodEstacion()+" codVar:"+dto.getCodVar());
			try {
				if(dto.getCodEstacion()!=null && dto.getCodVar()!=null){
					localSeriesGenerator.getIdeamLocalTimeSeries(dto.getCodEstacion(), dto.getCodVar());
					logger.info("Se ha terminado la generación local de la serie");
				}				
			} catch (Exception e) {
				logger.error("ERROR AL GENERAR");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		
		@RequestMapping("/correa")
		public String genRuthFiles(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando generacion de archivos");
			try {
				generadorArchivosDaneMeteorologia.generarDesdeDb();				
			} catch (Exception e) {
				logger.error("ERROR AL GENERAR ARCHIVOS RUTH");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		
		@RequestMapping("/comparar")
		public String generarArchivosComparacion(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando generacion de archivos de comparación");
			try {
					seriesDeTiempoComparer.compararSeries();	
					logger.info("Finalizó generacion de archivos de comparación");
			} 
			catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} 
			catch (Exception e) {
				logger.error("ERROR AL COMPARAR ARCHIVOS");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		
		@RequestMapping("/borradoCompleto")
		public String generarArchivosRadiacionGlobal(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando generacion de archivos de borrado radiación global");
			try {
					seriesDeTiempoComparer.generarArchivosBorradoCompleto();	
					logger.info("Finalizó generacion de archivos de borrado radiación global");
			} 
			catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} 
			catch (Exception e) {
				logger.error("ERROR AL COMPARAR ARCHIVOS");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		
		@RequestMapping("/msierra")
		public String genMarcelaFiles(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando generacion de archivos");
			try {
				generadorArchivosMarcelaApiSamples.generarArchivosMarcela();			
			} catch (Exception e) {
				logger.error("ERROR AL GENERAR ARCHIVOS RUTH");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		
		
		
		
		@RequestMapping("/borrar")
		public String borrarPuntosSerie(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando borrado de puntos");
			try {
					seriesDeTiempoCorrector.borrarPuntosSeriesConArchivos();	
					logger.info("Finalizando borrado de puntos");
			} 
			catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} 
			catch (Exception e) {
				logger.error("ERROR AL COMPARAR ARCHIVOS");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		

		@RequestMapping("/borrarIntervalo")
		public String borrarIntervalo(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando borrado de puntos");
			try {
					seriesDeTiempoCorrector.borrarIntervaloSeries();	
					logger.info("Finalizando borrado de puntos");
			} 
			catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} 
			catch (Exception e) {
				logger.error("ERROR AL COMPARAR ARCHIVOS");
				e.printStackTrace();
			}
								
			return "welcome";
		}

		
		@RequestMapping("/actualizar")
		public String actualizarSerie(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando actualización de puntos");
			try {
					seriesDeTiempoCorrector.actualizarSeriesConArchivos();	
					logger.info("Finalizando actualización de puntos");
			} 
			catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} 
			catch (Exception e) {
				logger.error("ERROR AL COMPARAR ARCHIVOS");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		

		@RequestMapping("/agregar")
		public String agregarPuntosSerie(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando agregagado de puntos");
			try {
					seriesDeTiempoCorrector.agregarPuntosSerieConArchivos();	
					logger.info("Finalizó agregagado de puntos");
			} 
			catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} 
			catch (Exception e) {
				logger.error("ERROR AL COMPARAR ARCHIVOS");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		
		@RequestMapping("/backup")
		public String generarBackupPuntosSerie(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando backup de puntos");
			try {
					seriesDeTiempoComparer.generarBackUpArchivos();	
					logger.info("Finalizó backup de puntos");
			} 
			catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} 
			catch (Exception e) {
				logger.error("ERROR AL realizar backup ARCHIVOS");
				e.printStackTrace();
			}
								
			return "welcome";
		}
		
		
		
		
		@RequestMapping("/generarArchivosCurvas")
		public String generarArchivosCurvas(Map<String, Object> model) {
			Log logger=logUtilsComponent.getLogger(this.getClass());
			logger.info("Iniciando generacion archivos curvas");
			try {
					generadorArchivosCurvasNQ.generarArchivosDesdeDB();;	
					logger.info("Finalizó generacion archivos curvas");
			} 
			catch (WebServiceException e) {
			    System.out.format("AQTS ERROR: + %d (%s) %s\n", e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			} 
			catch (Exception e) {
				logger.error("ERROR AL generar archivos de curvas");
				e.printStackTrace();
			}
								
			return "welcome";
		}
	
}
