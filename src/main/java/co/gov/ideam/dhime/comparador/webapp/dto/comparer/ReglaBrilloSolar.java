package co.gov.ideam.dhime.comparador.webapp.dto.comparer;

import static co.gov.ideam.dhime.utils.SerieDeTiempoUtils.obtenerMapSerieTiempo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

public class ReglaBrilloSolar extends ComparerDto{

	@Override
	public SerieDeTiempo serieComparada(SerieDeTiempo serieBase, TimeSeriesDataServiceResponse serieComparer, SerieDeTiempo serieComparerLocal) {
			List<PuntoSerieDeTiempo> listaNuevaSerie=new ArrayList<>();
			Optional<String> horaFiltro=posibleHours.stream().findFirst();
			if(horaFiltro.isPresent()){
				listaNuevaSerie=serieBase.getListaPuntos().stream().filter(item->!(item.getStrTimestamp().contains(horaFiltro.get()) && item.getDiffDatoPunto()>1)).collect(Collectors.toList());
				serieBase.setListaPuntos(listaNuevaSerie);	
			}					
			return serieBase;
	}

}
