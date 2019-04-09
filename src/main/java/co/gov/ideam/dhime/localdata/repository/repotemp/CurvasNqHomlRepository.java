package co.gov.ideam.dhime.localdata.repository.repotemp;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.gov.ideam.dhime.comparador.webapp.dto.CurvaNQInfo;
import co.gov.ideam.dhime.generador.model.repotemp.CurvasNqHoml;

public interface CurvasNqHomlRepository extends JpaRepository<CurvasNqHoml, Long>  {
	 
	
	@Query("select NEW co.gov.ideam.dhime.comparador.webapp.dto.CurvaNQInfo(cv.codigo, cv.fechaInicioVigencia, cv.fechaFinVigencia) from CurvasNqHoml cv group by codigo, fechaInicioVigencia, fechaFinVigencia order by codigo, fechaInicioVigencia, fechaFinVigencia")
	List<CurvaNQInfo> obtenerListaFechasAgrupadas();
	
	
	List<CurvasNqHoml> findByCodigoAndFechaInicioVigenciaAndFechaFinVigencia(String codigo, Date fechaInicioVigencia, Date fechaFinVigencia);	

}


