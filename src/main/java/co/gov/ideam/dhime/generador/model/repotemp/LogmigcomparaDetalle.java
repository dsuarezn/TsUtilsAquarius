package co.gov.ideam.dhime.generador.model.repotemp;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;

import java.math.BigDecimal;


/**
 * The persistent class for the LOGMIGCOMPARA_DETALLE database table.
 * 
 */
@Data
@Entity
@Table(name="LOGMIGCOMPARA_DETALLE")
public class LogmigcomparaDetalle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String idregistro;

	private String detalle;

	@Column(name="IDREG_MAESTRO")
	private String idregMaestro;

	@Column(name="TIPO_DETALLE")
	private String tipoDetalle;
	
	public LogmigcomparaDetalle() {
	}



}