package co.gov.ideam.dhime.utils;

import java.util.Date;

import lombok.Data;

@Data
public class CorrectionDateRange{
	private Date fechaInicio;
	private Date fechaFin;
	private String user;
	private String comments;
	public boolean inDateRange(Date dateToCheck){
		return dateToCheck.compareTo(fechaInicio) >= 0 && dateToCheck.compareTo(fechaFin) <=0;
	}
}
