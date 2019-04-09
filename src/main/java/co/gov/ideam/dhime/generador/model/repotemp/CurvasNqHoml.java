package co.gov.ideam.dhime.generador.model.repotemp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;


@Data
@Entity
@Table(name="CURVAS_NQ_HOML")
public class CurvasNqHoml implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_REG_CURVA")
	private long idRegCurva;
	
	
	@Column(name="CODIGO")
	private String codigo;
	
	@Column(name="NIVEL")
	private BigDecimal nivel;
	
	@Column(name="CAUDAL")
	private BigDecimal caudal;
	
	@Temporal(TemporalType.DATE)
	@Column(name="FECHA_INICIO_VIG")
	private Date fechaInicioVigencia;
	
	@Temporal(TemporalType.DATE)
	@Column(name="FECHA_FIN_VIG")
	private Date fechaFinVigencia;
	
	
}
