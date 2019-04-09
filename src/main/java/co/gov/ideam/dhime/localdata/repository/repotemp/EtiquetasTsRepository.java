package co.gov.ideam.dhime.localdata.repository.repotemp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.gov.ideam.dhime.generador.model.repotemp.EtiquetasTs;


@Repository
public interface EtiquetasTsRepository extends JpaRepository<EtiquetasTs, Long>  {
 
	List<EtiquetasTs> findByNombre(String nombre);
	

}
