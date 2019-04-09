package co.gov.ideam.dhime.generador.localgenerator;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.gov.ideam.dhime.generador.model.repotemp.EtiquetasTs;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.TablasAsociadas;
import co.gov.ideam.dhime.localdata.repository.repotemp.EtiquetasTsRepository;
import co.gov.ideam.dhime.localdata.repository.repotemp.SerieDeTiempoRepository;
import co.gov.ideam.dhime.localdata.repository.repotemp.TablasAsociadasRepository;

@Component
public class LocalSeriesQuerier {

	@Autowired
	SerieDeTiempoRepository serieDeTiempoRepository;

	@Autowired	
	TablasAsociadasRepository tablasAsociadasRepository;

	@Autowired
	EtiquetasTsRepository etiquetasTsRepository;
	
	public SerieDeTiempo getIdeamLocalTimeSeries(String codEstacion, String variable) throws ParseException{		
		List<TablasAsociadas> listaTablas=consultarTablasAsociadas(variable);
		EtiquetasTs etiquetas=consultarEtiquetasTs(variable);				
		return serieDeTiempoRepository.obtenerSerieDeTiempo(listaTablas, codEstacion, etiquetas);			
	}
	
	
	private List<TablasAsociadas> consultarTablasAsociadas(String variable){
		return tablasAsociadasRepository.findByVariable(variable);		
	}
	
	
	private EtiquetasTs consultarEtiquetasTs(String variable){
		return etiquetasTsRepository.findByNombre(variable).get(0);		
	}
	
	
}
