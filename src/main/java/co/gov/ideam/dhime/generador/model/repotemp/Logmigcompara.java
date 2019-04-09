package co.gov.ideam.dhime.generador.model.repotemp;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the LOGMIGCOMPARA database table.
 * 
 */
@Data
@Entity
public class Logmigcompara implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="IDREGISTRO")
	private String idregistro;

	@Column(name="TIPO_OPERACION")
	private String tipoOperacion;
	
	@Column(name="ARCHIVO_GENERADO")
	private String archivoGenerado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FECHA_OPERACION")
	private Date fechaOperacion;

	@Column(name="PORCENTAJE_SIMIL")
	private BigDecimal porcentajeSimil;

	public Logmigcompara() {
	}

	



}