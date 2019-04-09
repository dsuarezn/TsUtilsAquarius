package co.gov.ideam.dhime.generador.model.repotemp;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TABLAS_ASOCIADAS database table.
 * 
 */
@Data
@Entity
@Table(name="TABLAS_ASOCIADAS")
public class TablasAsociadas implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String calificador;

	@Column(name="DETALLE_FREC")
	private String detalleFrec;

	@Column(name="FORMATO_TIMESTAMP")
	private String formatoTimestamp;

	private String frecuencia;

	private Integer precedencia;

	private String tabla;

	@Column(name="VARIABLE")
	private String variable;
	
	@Column(name="ESTRUCTURA_COLUMNAS")
	private String estructuraColumnas;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFin;
		
	private Integer habilitada;
	
	private String tipoSerie;


	public TablasAsociadas() {
	}


}