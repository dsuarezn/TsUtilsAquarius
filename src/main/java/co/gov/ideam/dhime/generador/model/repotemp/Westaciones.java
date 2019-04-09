package co.gov.ideam.dhime.generador.model.repotemp;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;


/**
 * The persistent class for the WESTACIONES database table.
 * 
 */
@Data
@Entity
@Table(name="WESTACIONES")
public class Westaciones implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_ESTACION")
	private String idEstacion;

	@Column(name="ID_HOMOLOGADO")
	private String idHomologado;

	public Westaciones() {
	}



}