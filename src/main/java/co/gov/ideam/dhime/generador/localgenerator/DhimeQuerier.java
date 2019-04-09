package co.gov.ideam.dhime.generador.localgenerator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.gov.ideam.dhime.generador.model.dhime.SeriesTiempoValores;
import co.gov.ideam.dhime.localdata.repository.dhime.SeriesTiempoValoresRepository;

@Component
public class DhimeQuerier {

	@Autowired
	private SeriesTiempoValoresRepository seriesTiempoValoresRepository;
	
	
	public List<SeriesTiempoValores> obtenerListaValoresDhime(String etiqueta, Long estacionId){
		return seriesTiempoValoresRepository.findByEtiquetaAndIdestacion(etiqueta, estacionId);
	}

	
}
