package co.gov.ideam.dhime.comparador.webapp.dto.comparer;

import static co.gov.ideam.dhime.utils.SerieDeTiempoUtils.obtenerMapSerieTiempo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

public class DateNotExistComparerModificadoLocalDvagCon extends ComparerDto {

	@Override
	public SerieDeTiempo serieComparada(SerieDeTiempo serieBase, TimeSeriesDataServiceResponse serieComparer, SerieDeTiempo serieComparerLocal) {
		try {
			SerieDeTiempo serieAComparar= obtenerMapSerieTiempo(serieComparerLocal);
			SerieDeTiempo nueva=new SerieDeTiempo();
			nueva.setEstacion(serieBase.getEstacion());
			nueva.setEtiqueta(serieBase.getEtiqueta());
			List<PuntoSerieDeTiempo> listaNuevaSerie=new ArrayList<>();
			nueva.setListaPuntos(listaNuevaSerie);
			
			for (PuntoSerieDeTiempo punto : serieBase.getListaPuntos()) {	
											
				//Busca el punto de la serie local en la de aquarius						
				String formatedlgts=punto.getFormatedTimeStamp();

				Integer notNullCount=0;
						
				Double valorPunto=serieAComparar.getMapaPuntos().get(formatedlgts);					
				
				//Se el punto cumple con las condiciones de las reglas de velocidad si se tiene en cuenta para comparar
				//si no no se trae y no se carga DVAG
				if(valorPunto!=null){
				  if(valorPunto>=0 && valorPunto<=80){					  
					  	if(valorPunto>=0 && valorPunto<=0.3){
					  		punto.setDiffDatoPunto(0D);
					  		notNullCount++;
					  	}
					  	else{					  	
					  		if(valorPunto>0.3 && punto.getDiffDatoPunto()>0){
					  			notNullCount++;
					  		}					  		
					  	}
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
