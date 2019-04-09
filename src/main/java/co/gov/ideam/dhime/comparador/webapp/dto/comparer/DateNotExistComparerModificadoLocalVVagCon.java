package co.gov.ideam.dhime.comparador.webapp.dto.comparer;

import static co.gov.ideam.dhime.utils.SerieDeTiempoUtils.obtenerMapSerieTiempo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import co.gov.ideam.dhime.generador.model.repotemp.PuntoSerieDeTiempo;
import co.gov.ideam.dhime.generador.model.repotemp.SerieDeTiempo;

public class DateNotExistComparerModificadoLocalVVagCon extends ComparerDto {

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
				String formatedlgts=punto.getFormatedTimeStamp();
	
				Integer notNullCount=0;
				
				Double valorDvagCon=serieAComparar.getMapaPuntos().get(formatedlgts);	
								
				if(valorDvagCon!=null){
				  if(punto.getDiffDatoPunto()>=0 && punto.getDiffDatoPunto()<=80){
					  
					  if(punto.getDiffDatoPunto()>=0 && punto.getDiffDatoPunto()<=0.3){					  
						valorDvagCon=0D;
					  }
					  
					  
					  if(valorDvagCon>0){						  
						  if(punto.getDiffDatoPunto()>0.3){
							  notNullCount++;
						  }  						  
					  }
					  else if(valorDvagCon==0){						
						  if(punto.getDiffDatoPunto()>=0 && punto.getDiffDatoPunto()<=0.3){		
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
