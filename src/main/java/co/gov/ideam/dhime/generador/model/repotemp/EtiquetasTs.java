package co.gov.ideam.dhime.generador.model.repotemp;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;


/**
 * The persistent class for the ETIQUETAS_TS database table.
 * 
 */
@Data
@Entity
@Table(name="ETIQUETAS_TS")
public class EtiquetasTs implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="DETALLE_FREC")
	private String detalleFrec;

	@Column(name="FRECUENCIA_TS")
	private String frecuenciaTs;

	private String nombre;

	@Column(name="TIPO_DATO")
	private String tipoDato;
	
	

	public EtiquetasTs() {
	}



}