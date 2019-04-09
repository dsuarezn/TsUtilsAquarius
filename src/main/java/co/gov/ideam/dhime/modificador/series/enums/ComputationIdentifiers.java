package co.gov.ideam.dhime.modificador.series.enums;

public enum ComputationIdentifiers {

	Decumulated("Decumulated"),
	Max("Max"),
	MaxAtEventTime("MaxAtEventTime"),
	Mean("Mean"),
	Median("Median"),
	Min("Min"),
	SelectedValue("SelectedValue"),
	Sum("Sum"),
	TidalHigh("TidalHigh"),
	TidalHigherLow("TidalHigherLow"),
	TidalLow("TidalLow"),
	TidalLowerHigh("TidalLowerHigh"),
	TotalAmount("TotalAmount");
	
	private final String identifier;

	ComputationIdentifiers(String identifier) {
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
	
	
}
