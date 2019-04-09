package co.gov.ideam.dhime.comparador.webapp.dto.comparer;

import static co.gov.ideam.dhime.utils.DateTimeUtilsComponent.addHours;
import static co.gov.ideam.dhime.utils.SerieDeTiempoUtils.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

public class DateNotExistComparerModificadoLocal19Swap extends ComparerDto {

	@Override
	public SerieDeTiempo serieComparada(SerieDeTiempo serieBase, TimeSeriesDataServiceResponse serieComparer, SerieDeTiempo serieComparerLocal) {
		SerieDeTiempo serieAComparar;
		try {
			serieAComparar = obtenerMapSerieTiempo(serieComparerLocal);
		
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

			Integer notNullCount=0;
			if(formatedlgts.contains(" 19:")){					
				Double valor18=serieAComparar.getMapaPuntos().get(formatedts+" "+"18:00:00");	
				if(valor18!=null){
					punto.setTimestamp(addHours(punto.getTimestamp(), -1));
					punto.setStrTimestamp(punto.getFormatedTimeStamp());
					notNullCount++;
				}
				else{
					Double valor19=serieAComparar.getMapaPuntos().get(formatedlgts);
					if(valor19!=null){
						notNullCount++;
					}
				}					
			}
			else{
				Double valorPunto=serieAComparar.getMapaPuntos().get(formatedlgts);	
				if(valorPunto!=null){						
					notNullCount++;
				}
			}
			if(notNullCount>0){
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
