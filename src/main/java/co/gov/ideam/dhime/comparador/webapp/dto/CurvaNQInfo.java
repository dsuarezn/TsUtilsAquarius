package co.gov.ideam.dhime.comparador.webapp.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CurvaNQInfo {

	private String codigo;
	
	private Date fechaInicioVigencia;
	
	private Date fechaFinVigencia;

	public CurvaNQInfo(String codigo, Date fechaInicioVigencia, Date fechaFinVigencia) {
		super();
		this.codigo = codigo;
		this.fechaInicioVigencia = fechaInicioVigencia;
		this.fechaFinVigencia = fechaFinVigencia;
	}
	
	
	
}
