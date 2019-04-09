package co.gov.ideam.dhime.comparador.webapp.dto.comparer;

import static co.gov.ideam.dhime.utils.SerieDeTiempoUtils.obtenerMapSerieTiempo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

public class DateExistComparer extends ComparerDto {

	@Override
	public SerieDeTiempo serieComparada(SerieDeTiempo serieBase, TimeSeriesDataServiceResponse serieComparer, SerieDeTiempo serieComparerLocal) {
		try {
			SerieDeTiempo serieAComparar=obtenerMapSerieTiempo(serieComparer);
			SerieDeTiempo nueva=new SerieDeTiempo();
			nueva.setEstacion(serieBase.getEstacion());
			nueva.setEtiqueta(serieBase.getEtiqueta());
			List<PuntoSerieDeTiempo> listaNuevaSerie=new ArrayList<>();
			nueva.setListaPuntos(listaNuevaSerie);
			
			for (PuntoSerieDeTiempo punto : serieBase.getListaPuntos()) {			
				//Busca el punto de la serie local en la de aquarius
				PuntoSerieDeTiempo puntots=null;
				String formatedts=punto.getShortFormatedTimeStamp();
				Integer nullCount=0;
				for (String hour : posibleHours) {					
					Double valorPunto=serieAComparar.getMapaPuntos().get(formatedts+" "+hour);									
					if(valorPunto==null){nullCount++;}			
				}
				
				if(nullCount==4){
					nueva.getListaPuntos().add(punto);					
				}
			}
			return nueva;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	
	

}
