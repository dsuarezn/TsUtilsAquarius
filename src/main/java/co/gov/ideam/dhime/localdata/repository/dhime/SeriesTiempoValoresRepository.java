package co.gov.ideam.dhime.localdata.repository.dhime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.gov.ideam.dhime.generador.model.dhime.SeriesTiempoValores;


@Repository
public interface SeriesTiempoValoresRepository extends JpaRepository<SeriesTiempoValores, Long>  {
 
	List<SeriesTiempoValores> findByEtiquetaAndIdestacion(String etiqueta, Long estacion);
	

}
