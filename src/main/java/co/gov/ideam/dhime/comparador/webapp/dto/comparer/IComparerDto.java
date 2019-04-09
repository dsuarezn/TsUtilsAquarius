package co.gov.ideam.dhime.comparador.webapp.dto.comparer;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

public interface IComparerDto {
	
	SerieDeTiempo serieComparada(SerieDeTiempo serieBase, TimeSeriesDataServiceResponse serieComparer, SerieDeTiempo serieComparerLocal);
}
