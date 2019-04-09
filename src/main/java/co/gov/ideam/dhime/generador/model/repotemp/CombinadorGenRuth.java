package co.gov.ideam.dhime.generador.model.repotemp;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;

import java.math.BigDecimal;


/**
 * The persistent class for the TABLAS_ASOCIADAS database table.
 * 
 */
@Data
@Entity
@Table(name="COMBINADOR_GEN_RUTH")
public class CombinadorGenRuth implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="VARIABLE_1")
	private String variable1;
	
	@Column(name="TABLA_1")
	private String Tabla1;
	
	@Column(name="ESTR_COL_1")
	private String EstrCol1;

	@Column(name="VARIABLE_2")
	private String variable2;
	
	@Column(name="TABLA_2")
	private String Tabla2;
	
	@Column(name="ESTR_COL_2")
	private String EstrCol2;
	
	@Column(name="FOR_BROKE")
	private String forBroke;	

	private String frecuencia;
	
	private String calificador;
	
	@Column(name="DETALLE_FREC")
	private String DetalleFrec;
	
	@Column(name="ESTR_GEN")
	private String EstrGen;
	
	@Column(name="PRD_TABLA_1")
	private Integer PrdTabla1;
	
	@Column(name="PRD_TABLA_2")
	private Integer PrdTabla2;
	
	@Column(name="PERIODICIDAD")
	private String periodicidad;
	
	public CombinadorGenRuth() {
	}


}