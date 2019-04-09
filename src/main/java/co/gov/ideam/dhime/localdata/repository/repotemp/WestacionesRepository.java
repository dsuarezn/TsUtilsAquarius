package co.gov.ideam.dhime.localdata.repository.repotemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.gov.ideam.dhime.generador.model.repotemp.EtiquetasTs;
import co.gov.ideam.dhime.generador.model.repotemp.Westaciones;


@Repository
public interface WestacionesRepository extends JpaRepository<Westaciones, Long>  {
 

}
