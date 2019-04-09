package co.gov.ideam.dhime.generador.model.repotemp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SerieDeTiempo {
	
	
	private String estacion;
	private String etiqueta;
	private Integer prioridad;
	private TablasAsociadas tabla;
	private String tsIdSerieTiempo;
	private Date rangoInicio;
	private Date rangoFin;
	
	private List<PuntoSerieDeTiempo> listaPuntos;
	private Map<String, Double> mapaPuntos;	

	public SerieDeTiempo(String estacion, String etiqueta, Integer prioridad, List<PuntoSerieDeTiempo> listaPuntos) {
		super();
		this.estacion = estacion;
		this.etiqueta = etiqueta;
		this.prioridad = prioridad;
		this.listaPuntos = listaPuntos;
	}

	public SerieDeTiempo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

}
