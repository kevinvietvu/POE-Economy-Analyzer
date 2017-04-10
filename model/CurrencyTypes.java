package application.model;

import java.util.LinkedHashMap;

public class CurrencyTypes {
	public final static int Alterations = 1;
	public final static int Fusings = 2;
	public final static int Alchemys = 3;
	public final static int Chaos = 4;
	public final static int Gemcutters = 5;
	public final static int Exalted = 6;
	public final static int Chromes = 7;
	public final static int Jewellers = 8;
	public final static int Chances = 9;
	public final static int Chisels = 10;
	public final static int Scours = 11;
	public final static int Blessed = 12;
	public final static int Regrets = 13;
	public final static int Regals = 14;
	public final static int Divines = 15;
	public final static int Vaals = 16;
	public final static String League = "Legacy";
	//think about using a bimap instead of using two maps
	private static LinkedHashMap<Integer,String> currencyMap = new LinkedHashMap<Integer,String>();
	private static LinkedHashMap<String,Integer> reverseCurrencyMap = new LinkedHashMap<String,Integer>();
	private int tableIndex = 0;
	
	//"http://currency.poe.trade/search?league=Breach&online=x&want=3&have=4"
	private int currencyWant;
	private int currencyHave;
	
	 /**
	   * Constructor for types of currency  
	   * @param league The league the game is currently in
	   * @param currencyWant the type of currency that is wanted
	   * @param currencyHave the type of currency that you have
	   * @return No return value.
	   */ 
	public CurrencyTypes(String league, int currencyWant, int currencyHave)
	{	
		this.currencyWant = currencyWant;
		this.currencyHave = currencyHave;
	}
	
	 /**
	   * Gets the URL for the current currency data
	   * @param none
	   * @return the URL to the website.
	   */ 
	public String getURL() {
		String newUrl = "http://currency.poe.trade/search?league=" + League + "&online=x&want=" + currencyWant + "&have=" + currencyHave;
		return newUrl;
	}

	public int getCurrencyWant() {
		return currencyWant;
	}

	public void setCurrencyWant(int currencyWant) {
		this.currencyWant = currencyWant;
	}

	public int getCurrencyHave() {
		return currencyHave;
	}

	public void setCurrencyHave(int currencyHave) {
		this.currencyHave = currencyHave;
	}
	
	public int getTableIndex() {
		return tableIndex;
	}
	
	public void setTableIndex(int newIndex) {
		tableIndex = newIndex;
	}
	
	public boolean equals(CurrencyTypes other) {
		if (other != null && other.getCurrencyWant() == this.getCurrencyWant() && other.getCurrencyHave() == this.getCurrencyHave())
			return true;
		else 
			return false;
	}
	 /**
	   * Sets the mapping between Currency Name and Currency Number
	   * and vice versa
	   */ 
	public static void setCurrencyMap() {
    	currencyMap.put(CurrencyTypes.Alterations,"Alterations");
    	currencyMap.put(CurrencyTypes.Fusings, "Fusings");
    	currencyMap.put(CurrencyTypes.Alchemys, "Alchemys");
    	currencyMap.put(CurrencyTypes.Chaos, "Chaos");
    	currencyMap.put(CurrencyTypes.Gemcutters, "Gemcutters");
    	currencyMap.put(CurrencyTypes.Exalted, "Exalted");
    	currencyMap.put(CurrencyTypes.Chromes, "Chromes");
    	currencyMap.put(CurrencyTypes.Jewellers, "Jewellers");
    	currencyMap.put(CurrencyTypes.Chances, "Chances");
    	currencyMap.put(CurrencyTypes.Chisels, "Chisels");
    	currencyMap.put(CurrencyTypes.Scours, "Scours");
    	currencyMap.put(CurrencyTypes.Blessed, "Blessed");
    	currencyMap.put(CurrencyTypes.Regrets, "Regrets");
    	currencyMap.put(CurrencyTypes.Regals, "Regals");
    	currencyMap.put(CurrencyTypes.Divines, "Divines");
    	currencyMap.put(CurrencyTypes.Vaals, "Vaals");
    	
    	reverseCurrencyMap.put("Alterations", CurrencyTypes.Alterations);
    	reverseCurrencyMap.put("Fusings", CurrencyTypes.Fusings);
    	reverseCurrencyMap.put("Alchemys", CurrencyTypes.Alchemys);
    	reverseCurrencyMap.put("Chaos", CurrencyTypes.Chaos);
    	reverseCurrencyMap.put("Gemcutters", CurrencyTypes.Gemcutters);
    	reverseCurrencyMap.put("Exalted", CurrencyTypes.Exalted);
    	reverseCurrencyMap.put("Chromes", CurrencyTypes.Chromes);
    	reverseCurrencyMap.put("Jewellers", CurrencyTypes.Jewellers);
    	reverseCurrencyMap.put("Chances", CurrencyTypes.Chances);
    	reverseCurrencyMap.put("Chisels", CurrencyTypes.Chisels);
    	reverseCurrencyMap.put("Scours", CurrencyTypes.Scours);
    	reverseCurrencyMap.put("Blessed", CurrencyTypes.Blessed);
    	reverseCurrencyMap.put("Regrets", CurrencyTypes.Regrets);
    	reverseCurrencyMap.put("Regals", CurrencyTypes.Regals);
    	reverseCurrencyMap.put("Divines", CurrencyTypes.Divines);
    	reverseCurrencyMap.put("Vaals", CurrencyTypes.Vaals);
	}
	
	public static LinkedHashMap<Integer, String> getCurrencyMap() {
		return currencyMap;
	}
	
	public static LinkedHashMap<String, Integer> getReverseCurrencyMap() {
		return reverseCurrencyMap;
	}
	
}
