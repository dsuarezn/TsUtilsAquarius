package co.gov.ideam.dhime.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import co.gov.ideam.dhime.generador.model.dhime.SeriesTiempoValores;
import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

public class SerieDeTiempoUtils {

	public static SerieDeTiempo obtenerMapSerieTiempo(TimeSeriesDataServiceResponse serieTiempots) throws ParseException{
		SerieDeTiempo serie=new SerieDeTiempo();
		serie.setEstacion(serieTiempots.getLocationIdentifier());
		serie.setEtiqueta(serieTiempots.getLabel());
//		serie.setListaPuntos(obtenerTimeSeriesSerie(serieTiempots.getPoints()));
		serie.setMapaPuntos(obtenerMapaPuntosSerie(serieTiempots.getPoints()));
		return serie;
	}
	
	public static Map<String, Double> obtenerMapaPuntosSerie(ArrayList<TimeSeriesPoint> puntos) throws ParseException{
		Map<String, Double>  mapalistaPuntos=new Hashtable<>();
		for (TimeSeriesPoint tsPoint : puntos) {
			Date fecha=Date.from(tsPoint.getTimestamp().getDateTimeOffset());
			String strdate=DateTimeUtilsComponent.formatDateToCompareFormat(fecha);

			mapalistaPuntos.put(strdate, tsPoint.getValue().getNumeric());			
		}
		return mapalistaPuntos;
	}
	
	public static SerieDeTiempo obtenerMapSerieTiempo(Long idestacion, String etiqueta, List<SeriesTiempoValores> listaPuntos) throws ParseException{
		SerieDeTiempo serie=new SerieDeTiempo();
		serie.setEstacion(idestacion.toString());
		serie.setEtiqueta(etiqueta);		
		serie.setMapaPuntos(obtenerMapaPuntosDhime(listaPuntos));
		return serie;
	}
	
	public static Map<String, Double> obtenerMapaPuntosDhime(List<SeriesTiempoValores> lista) throws ParseException{
		Map<String, Double>  mapalistaPuntos=new Hashtable<>();
		for (SeriesTiempoValores valor : lista) {
			mapalistaPuntos.put(DateTimeUtilsComponent.formatDateToCompareFormat(valor.getFechalectura()), valor.getValor().doubleValue());
		}
		return mapalistaPuntos;
	}
	
	public static SerieDeTiempo obtenerMapSerieTiempo(SerieDeTiempo serie) throws ParseException{
		serie.setMapaPuntos(obtenerMapaPuntosSerieIdeam(serie.getListaPuntos()));
		return serie;
	}
	
	public static Map<String, Double> obtenerMapaPuntosSerieIdeam(List<PuntoSerieDeTiempo> puntos) throws ParseException{
		Map<String, Double>  mapalistaPuntos=new Hashtable<>();
		for (PuntoSerieDeTiempo ideamPoint : puntos) {			
			String strdate=DateTimeUtilsComponent.formatDateToCompareFormat(ideamPoint.getTimestamp());
			mapalistaPuntos.put(strdate, ideamPoint.getDato());			
		}
		return mapalistaPuntos;
	}
	
}
