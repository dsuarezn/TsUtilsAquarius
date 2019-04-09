package co.gov.ideam.dhime.comparador.webapp.dto;

import lombok.Data;

@Data
public class EstacionEtiqueta {
	
	public EstacionEtiqueta(String identificador, String estacion, String etiqueta) {
		super();
		this.identificador=identificador;
		this.estacion = estacion;
		this.etiqueta = etiqueta;
	}
	
	public EstacionEtiqueta(String estacion, String etiqueta) {
		super();
		this.estacion = estacion;
		this.etiqueta = etiqueta;
	}
	private String estacion;
	private String etiqueta;
	private String identificador;
}
