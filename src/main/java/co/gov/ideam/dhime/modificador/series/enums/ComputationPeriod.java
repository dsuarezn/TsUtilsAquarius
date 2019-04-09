package co.gov.ideam.dhime.modificador.series.enums;

public enum ComputationPeriod {
	Yearly("Yearly"),
	Daily("Daily"),
	Hourly("Hourly"),
	Minutes("Minutes"),
	Monthly("Monthly"),
	Points("Points"),
	WaterYear("WaterYear"),
	Weekly("Weekly"),
	_5Minutes("5Minutes"),
	_3PerDay("3PerDay"),
	_10Minutes("10Minutes"),
	_2Minutes("2Minutes"),
	Undetermined("TotalAmount"),
	_2PerDay("2PerDay");
	
	private final String identifier;

	ComputationPeriod(String identifier) {
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
	
	
}
