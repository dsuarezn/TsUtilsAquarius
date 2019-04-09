package co.gov.ideam.dhime.comparador.webapp.dto;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Provisioning.InterpolationType;

import co.gov.ideam.dhime.modificador.series.enums.ComputationPeriod;
import lombok.Data;

@Data
public class TSParametro {
	
	private String methodCode;
	private InterpolationType interpolation;
	private String unit;
	private String parameter;
	private String label;
	private Integer gapTolerance;	
	private String descripcion;
	private String publicado;
	
	private ComputationPeriod periodicidad;
	
	public TSParametro(String parameter,Integer gapTolerance, InterpolationType interpolation,ComputationPeriod periodicidad, String unit, String desc) {
		super();		
		this.interpolation = interpolation;
		this.unit = unit;
		this.parameter = parameter;
		this.gapTolerance=gapTolerance;		
		this.periodicidad=periodicidad;
		this.descripcion=desc;
	}

	public TSParametro(String parameter,Integer gapTolerance, InterpolationType interpolation,ComputationPeriod periodicidad, String unit, String desc, String publicado) {
		super();		
		this.interpolation = interpolation;
		this.unit = unit;
		this.parameter = parameter;
		this.gapTolerance=gapTolerance;		
		this.periodicidad=periodicidad;
		this.descripcion=desc;
		this.publicado=publicado;
	}
	

	
}
