package co.gov.ideam.dhime.localdata.repository.repotemp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.gov.ideam.dhime.generador.model.repotemp.EtiquetasTs;
import co.gov.ideam.dhime.generador.model.repotemp.Logmigcompara;
import co.gov.ideam.dhime.generador.model.repotemp.TablasAsociadas;

@Repository
public interface LogmigcomparaRepository extends JpaRepository<Logmigcompara, Long> {

}
