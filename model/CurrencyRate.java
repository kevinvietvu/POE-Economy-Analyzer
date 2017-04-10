package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CurrencyRate {
	
    private final StringProperty want;
    private final StringProperty have;
    //IGN stands for in game name
    private final StringProperty IGN;
    private CurrencyTypes types;
	
    /**
     * Default constructor.
     */
    public CurrencyRate() {
        this(null, null, null, null);
    }

    /**
     * Constructor with some initial data.
     * 
     * @param want
     * @param have
     */
    public CurrencyRate(CurrencyTypes types, String IGN, String want, String have) {
    	this.types = types;
    	this.IGN = new SimpleStringProperty(IGN);
        this.want = new SimpleStringProperty(want);
        this.have = new SimpleStringProperty(have);
    }
    
    public CurrencyTypes getCurrencyTypes()
    {
    	return types;
    }
    
    public void setCurrencyTypes(CurrencyTypes newTypes)
    {
    	types = newTypes;
    }
    
    public String getWant() {
        return want.get();
    }

    public void setWant(String newWant) {
        this.want.set(newWant);
    }

    public StringProperty wantProperty() {
        return want;
    }

    public String getHave() {
        return have.get();
    }
    
    public void setHave(String newHave) {
        this.have.set(newHave);
    }

    public StringProperty haveProperty() {
        return have;
    }
    
    public String getIGN() {
        return IGN.get();
    }
    
    public void setIGN(String newIGN) {
        this.IGN.set(newIGN);
    }

    public StringProperty IGNProperty() {
        return IGN;
    }
}
