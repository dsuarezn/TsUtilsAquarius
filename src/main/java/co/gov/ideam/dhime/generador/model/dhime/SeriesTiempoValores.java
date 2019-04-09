package co.gov.ideam.dhime.generador.model.dhime;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table (name = "SERIESTIEMPOVALORES", schema = "CUSTOMHM")
public class SeriesTiempoValores {

	@Column(name="IDPARAMETRO")
	private String idparametro;
	@Column(name="ETIQUETA")
	private String etiqueta;
	@Column(name="IDESTACION")
	private Long idestacion;
	@Column(name="FECHALECTURA")
	private Date fechalectura;
	@Column(name="FECHACAPTURA")
	private Date fechacaptura;
	@Column(name="IDPERIODICIDAD")
	private Long idperiodicidad;
	@Column(name="VALOR")
	private BigDecimal valor;
	@Id
	@Column(name="IDSERIETIEMPOVALOR")
	private Long idserietiempovalor;
	@Column(name="USUARIO")
	private String usuario;
}
