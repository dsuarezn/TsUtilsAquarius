package co.gov.ideam.dhime.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.gov.ideam.dhime.generador.model.repotemp.Logmigcompara;
import co.gov.ideam.dhime.generador.model.repotemp.LogmigcomparaDetalle;
import co.gov.ideam.dhime.localdata.repository.repotemp.LogmigcomparaDetalleRepository;
import co.gov.ideam.dhime.localdata.repository.repotemp.LogmigcomparaRepository;
import lombok.Data;

@Component
@Transactional
@Data
public class LogUtilsComponent {

	@Autowired
	private LogmigcomparaRepository logmigcomparaRepository;
	
	@Autowired
	private LogmigcomparaDetalleRepository logmigcomparaDetalleRepository;
	
	public final String TIPOOP_QUERY="CONSULTA";
	public final String TIPODET_INFOQUERY="INFO_CONSULTA";
	public final String TIPODET_WARNQUERY="WARNING_CONSULTA";
	
	private DateFormat formatCodMaestro =null;
	
	private DateFormat formatCodDetalle = null;
	
//	private DateFormat formatoFechaHoraEstandar =null;
	
	private Log logger=null;
	
	public LogUtilsComponent() {
		super();
		// TODO Auto-generated constructor stub
//		formatCodMaestro = new SimpleDateFormat("yyyyMMddHHmmss");		
//		formatCodDetalle = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//		formatoFechaHoraEstandar = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");	
		logger=this.getLogger(this.getClass());
		
	}

	public void registrarEventoMaestroLog(String idMaestro, String tipoOperacion, String rutaArchivoGen, Double porcentajeSimil, Date fechaOperacion){
		
		Logmigcompara regMaestro=new Logmigcompara();
		regMaestro.setIdregistro(idMaestro);
		regMaestro.setTipoOperacion(tipoOperacion);
		regMaestro.setArchivoGenerado(rutaArchivoGen);
//		regMaestro.setPorcentajeSimil(porcentajeSimil);
		regMaestro.setFechaOperacion(fechaOperacion);
		logmigcomparaRepository.save(regMaestro);
		logger.info("Se registra evento maestro de tipo:" + tipoOperacion );
		
	}
	
	public void registrarEventoDetalle(String idevento, String idmaestro, String tipoDetalle, String detalle){
		
		LogmigcomparaDetalle logdetalle=new LogmigcomparaDetalle();
		logdetalle.setTipoDetalle(tipoDetalle);
		logdetalle.setIdregistro(idevento);
		logdetalle.setIdregMaestro(idmaestro);
		logdetalle.setDetalle(detalle);
		logmigcomparaDetalleRepository.save(logdetalle);
		logger.info("Se registra evento detalle de tipo " + tipoDetalle +" op:"+ detalle );
		
	}
	
	public static Log getLogger(Class T){
		return LogFactory.getLog(T);
	}
	
	

}
