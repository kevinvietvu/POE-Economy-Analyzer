package application.util;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import application.model.CurrencyRate;
import application.model.CurrencyTypes;

public class Scraper {

	private CurrencyRate[] listOfData;
	private int cutoff = 7;

	public Scraper() {

	}

	public int getCutoff() {
		return cutoff;
	}

	public void setCutoff(int newCutoff) {
		cutoff = newCutoff;
	}

	 /**
	   * Scrapes Currency Rate data from URL
	   * @param types Type of Currency
	   * @return list of currency rates
	   */ 
	public CurrencyRate[] getData(CurrencyTypes types) {
		listOfData = new CurrencyRate[15];
		CurrencyRate rate;
		Document doc;
		try {
			// Connection c =
			// Jsoup.connect(types.urlConverter()).timeout(10000);
			// doc = c.get();
			doc = Jsoup.connect(types.getURL()).get();
			// or use doc = Jsoup.connect(types.urlConverter()).get();
			Elements rawRates = doc.getElementsByClass("displayoffer-middle");
			Elements rawNames = doc.select("a[href=#]");
			ArrayList<String> cleanNames = new ArrayList<>();

			for (int i = 0; i < rawNames.size(); i++) {
				if ((rawNames.get(i).text().contains("IGN"))) {
					String name = rawNames.get(i).text();
					name = name.replaceAll("IGN:\\s+", "");
					cleanNames.add(name);
				}
			}

			for (int i = 0; i < rawRates.size(); i++) {
				if (i > 14)
					break;
				String[] splitRate = rawRates.get(i).text().split(" ");
				rate = new CurrencyRate(types, cleanNames.get(i), splitRate[0], splitRate[2]);
				listOfData[i] = rate;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return listOfData;
	}

	/**
	 * Rounds all the data within the array to two decimal places
	 * @param data
	 * @return
	 */
	public CurrencyRate[] roundData(CurrencyRate[] data) {
		for (int i = 0; i < cutoff; i++) {
			data[i] = getRatio(data[i]);
		}
		return data;
	}
	
	/**
	 * Helper method for rounding data to two decimal places
	 * @param rate
	 * @return
	 */
	public static CurrencyRate getRatio(CurrencyRate rate) {
		double want = Double.parseDouble(rate.getWant());
		double have = Double.parseDouble(rate.getHave());
		if (want > have && have > 1) {
			want = want / have;
			have = have / have;
			// rounds to two decimal places
			want = Math.round(want * 10000.0) / 10000.0;
			have = Math.round(have * 10000.0) / 10000.0;
			rate.setWant(Double.toString(want));
			rate.setHave(Double.toString(have));
			return rate;
		} else if (have > want && want > 1) {
			have = have / want;
			want = want / want;
			want = Math.round(want * 10000.0) / 10000.0;
			have = Math.round(have * 10000.0) / 10000.0;
			rate.setWant(Double.toString(want));
			rate.setHave(Double.toString(have));
			return rate;
		} else if (want > 1 && have == want) {
			want = want / want;
			have = have / have;
			want = Math.round(want * 10000.0) / 10000.0;
			have = Math.round(have * 10000.0) / 10000.0;
			rate.setWant(Double.toString(want));
			rate.setHave(Double.toString(have));
			return rate;
		} else {
			return rate;
		}
	}

	/**
	 * Gets best value that isn't skewed from currency rates to be put into sqlite database
	 * @param data
	 * @return
	 */
	public CurrencyRate getBestValue(CurrencyRate[] data) {

		if (data.length < 1)
			return null;

		data = roundData(data);
		boolean wantFlag = false;
		// boolean haveFlag = false;
		double sum = 0;
		for (int i = 0; i < cutoff; i++) {
			CurrencyRate rate = data[i];
			double want = Double.parseDouble(rate.getWant());
			double have = Double.parseDouble(rate.getHave());
			if (want > have) {
				sum = sum + want;
				wantFlag = true;
			} else if (have > want) {
				wantFlag = false;
				sum = sum + have;
				// haveFlag = true;
			} else // if they are equal, just default add want value to sum
				sum = sum + want;
		}
		double avg = sum / cutoff;
		double weight = avg / 150;
		if (wantFlag)
			avg = avg - weight;
		else 
			avg = avg + weight;
		System.out.println("AVG : " + avg);
		int indexOfBestValue = 0;
		double minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < cutoff; i++) {
			CurrencyRate rate = data[i];
			if (wantFlag) {
				double want = Double.parseDouble(rate.getWant());
				double delta = Math.abs(avg - want);
				System.out.println("DELTA : " + delta);
				if (delta < minDelta) {
					minDelta = delta;
					indexOfBestValue = i;
				}
			} else {
				double have = Double.parseDouble(rate.getHave());
				double delta = Math.abs(avg - have);
				System.out.println("DELTA : " + delta);
				if (delta < minDelta) {
					minDelta = delta;
					indexOfBestValue = i;
				}
			}
		}
		return data[indexOfBestValue];
	}

	public static void main(String args[]) {
		CurrencyTypes.setCurrencyMap();
		Scraper s = new Scraper();
		CurrencyTypes type = new CurrencyTypes("Legacy", 3, 4);
		CurrencyRate[] data = s.getData(type);

		data = s.roundData(data);
		for (int i = 0; i < s.getCutoff(); i++)
			System.out.println(data[i].getWant() + " : " + data[i].getHave());

		CurrencyRate best = s.getBestValue(data);
		System.out.println(CurrencyTypes.getCurrencyMap().get(type.getCurrencyHave()) + " " + best.getWant() + " : "
				+ best.getHave());
	}
}
