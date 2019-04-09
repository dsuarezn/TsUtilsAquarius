package co.gov.ideam.dhime.comparador.webapp.dto.comparer;

import java.util.List;

import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import lombok.Data;

@Data
public abstract class ComparerDto implements IComparerDto {

	protected String etiqueta;
	protected String etiquetaLocal;
	protected SerieDeTiempo serieCargada;
	protected List<String> posibleHours;

}
