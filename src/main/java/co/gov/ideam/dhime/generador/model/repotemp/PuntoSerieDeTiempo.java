package co.gov.ideam.dhime.generador.model.repotemp;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import co.gov.ideam.dhime.utils.DateTimeUtilsComponent;
import lombok.Data;


@Data
public class PuntoSerieDeTiempo {

	
	private Date timestamp;
	private Double dato;	
	private String calificador;	
	private Integer prioridad;
	private String puntoCorreccion;
	//Usados para diferencia de puntos
	private String diffTipoPunto;
	private Double diffDatoPunto;
	//USADOS PARA GENERACION DE RUTH
	private String fechaTrabajo;
	private String strTimestamp;
	private String strVariable;
	private String strDato;
	
	public PuntoSerieDeTiempo(){}
	
	public PuntoSerieDeTiempo(Date timestamp, Double dato, String calificador) {
		super();
		this.timestamp = timestamp;
		this.dato = dato;
		this.calificador = calificador;
	}
	
	public String getFormatedTimeStamp() {
		
			if(this.timestamp!=null){
				return DateTimeUtilsComponent.formatDateToCompareFormat(this.timestamp);
			}
			return null;
		
	}
		
	public String getShortFormatedTimeStamp() {
		
		if(this.timestamp!=null){
			return DateTimeUtilsComponent.formatDateToShortCompareFormat(this.timestamp);
		}
		return null;
	
}
	
}
