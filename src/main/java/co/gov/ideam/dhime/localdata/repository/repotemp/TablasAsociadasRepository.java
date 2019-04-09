package co.gov.ideam.dhime.localdata.repository.repotemp;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.gov.ideam.dhime.generador.model.repotemp.TablasAsociadas;

@Repository
public interface TablasAsociadasRepository extends JpaRepository<TablasAsociadas, Long> {

	
//	@Query("select NEW co.edu.udistrital.sga.preinscripcion.auto.domain.ProyectoCurricular(pc.craCod,pc.craNombre) FROM Accra pc where pc.craEmpNroIden=(select p.craEmpNroIden from Accra p where p.craCod = :codigoCarrera)")
//	 List<ProyectoCurricular> obtenerProyectosCoordinador(@Param("codigoCarrera") long codigoCarrera);
//	
	
	
	List<TablasAsociadas> findByVariable(String variable);
	
}
