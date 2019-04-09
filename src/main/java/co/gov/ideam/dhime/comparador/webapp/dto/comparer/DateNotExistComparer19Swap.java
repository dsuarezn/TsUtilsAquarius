package co.gov.ideam.dhime.comparador.webapp.dto.comparer;

import static co.gov.ideam.dhime.utils.SerieDeTiempoUtils.obtenerMapSerieTiempo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

import static co.gov.ideam.dhime.utils.DateTimeUtilsComponent.*;

public class DateNotExistComparer19Swap extends ComparerDto {

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
				String formatedlgts=punto.getFormatedTimeStamp();
				
				if(formatedlgts.equals("1995-07-13 19:00:00")){
					System.out.println(formatedlgts);
				}
				
				Integer notNullCount=0;
				for (String hour : posibleHours) {					
					Double valorPunto=serieAComparar.getMapaPuntos().get(formatedts+" "+hour);									
					if(valorPunto!=null){
						notNullCount++;
						break;
					}			
				}
				
				if(notNullCount>0){
					if(formatedlgts.contains(" 19:")){
						Double valor18=serieAComparar.getMapaPuntos().get(formatedts+" "+"18:00:00");	
						if(valor18!=null){
							punto.setTimestamp(addHours(punto.getTimestamp(), -1));
							punto.setStrTimestamp(punto.getFormatedTimeStamp());
						}
					}
					
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
