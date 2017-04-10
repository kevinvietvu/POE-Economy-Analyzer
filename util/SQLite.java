package application.util;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;

import application.model.CurrencyRate;
import application.model.CurrencyTypes;

import java.sql.SQLException;


public class SQLite {
	String defaultSQL = "jdbc:sqlite:test.db";
	String database = "";
	HashMap<String, CurrencyTypes[]> tables = new HashMap<String, CurrencyTypes[]>();
	ArrayList<String> dates = null;
	ArrayList<Double> values = null;
	CurrencyTypes type = null;
	
	//  select time(time(), 'localtime'); DISPLAYS HH:MM:SS in your local time SELECT
	//  datetime('now','localtime'); DISPLAYS DATE AND TIME

	public SQLite(String database) {
		this.database = database;
		CurrencyTypes.setCurrencyMap();
		CurrencyTypes[] wantAlchs = {
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Alchemys, CurrencyTypes.Chaos) };
		CurrencyTypes[] wantChaos = {
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chaos, CurrencyTypes.Alchemys),
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chaos, CurrencyTypes.Fusings),
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chaos, CurrencyTypes.Exalted),
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chaos, CurrencyTypes.Chromes),
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chaos, CurrencyTypes.Chisels) };
		CurrencyTypes[] wantExalts = {
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Exalted, CurrencyTypes.Chaos) };
		CurrencyTypes[] wantChisels = {
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chisels, CurrencyTypes.Chaos) };
		CurrencyTypes[] wantChromes = {
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chromes, CurrencyTypes.Chaos) };
		CurrencyTypes[] wantFusings = {
				new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Fusings, CurrencyTypes.Chaos) };
		tables.put("WANTAlchemys", wantAlchs);
		tables.put("WANTChaos", wantChaos);
		tables.put("WANTChisels", wantChisels);
		tables.put("WANTChromes", wantChromes);
		tables.put("WANTExalted", wantExalts);
		tables.put("WANTFusings", wantFusings);
	}

	// if DB doesn't exist it will create one
	public Connection createConnection() {
		// SQLite connection string
		// String url = "jdbc:sqlite:test2.db"; this makes a DB inside the
		// directly of poeTradeGUI
		String url = "jdbc:sqlite:" + database;
		Connection conn = null;
		try {
			// create a connection to the database
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	/**
	 * Create a new table in the a given database
	 *
	 */
	public void createNewTable(String tableName) {
		// SQL statement for creating a new table
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(\n" + " date TEXT,\n" + " have TEXT,\n"
				+ " wantQuantity REAL,\n" + " haveQuantity REAL\n" + ");";
		try (Connection conn = createConnection(); Statement stmt = conn.createStatement()) {
			// create a new table
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Create all of the tables needed for this database
	 */
	public void createNewTables() {
		for (String key : tables.keySet()) {
			createNewTable(key);
		}
	}

	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void insert(String table, String haveCurrency, double wantQuantity, double haveQuantity) {
		// always provide the columns or the implicit primary key is added in as
		// a extra column when inserting
		String sql = "INSERT INTO " + table + "(date, have, wantQuantity, haveQuantity) VALUES(?,?,?,?)";
		try (Connection conn = createConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, getDateTime());
			pstmt.setString(2, haveCurrency);
			pstmt.setDouble(3, wantQuantity);
			pstmt.setDouble(4, haveQuantity);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void bulkInsert() {
		Scraper s = new Scraper();
		for (String key : tables.keySet()) {
			CurrencyTypes[] types = tables.get(key);
			for (int i = 0; i < types.length; i++) {
				CurrencyRate[] data = s.getData(types[i]);
				CurrencyRate type = s.getBestValue(data);
				CurrencyTypes ratio = type.getCurrencyTypes();
				insert(key, CurrencyTypes.getCurrencyMap().get(ratio.getCurrencyHave()),
						Double.parseDouble(type.getWant()), Double.parseDouble(type.getHave()));
			}
		}
	}

	/**
	 * select all rows in the warehouses table
	 */
	public void printRows(String table, String have) {
		String sql = "SELECT * FROM " + table;
		String sqlselect = sql + " WHERE HAVE = " + "\"" + have + "\"";
		ResultSet rs = null;

		try {

			Connection conn = createConnection();
			Statement stmt = conn.createStatement();

			if (table.contains("WANTChaos"))
				rs = stmt.executeQuery(sqlselect);
			else
				rs = stmt.executeQuery(sql);

			// loop through the result set
			while (rs.next()) {
				System.out.println(table + "\t| " + rs.getString("date") + "\t| Have : " + rs.getString("have") + "\t| "
						+ rs.getDouble("wantQuantity") + "\t| " + rs.getDouble("haveQuantity"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void printAll() {
		for (String key : tables.keySet()) {
			if (key.equals("WANTChaos")) {
				for (String chaoskey : tables.keySet()) {
					if (!chaoskey.equals("WANTChaos")) {
						printRows(key, chaoskey.substring(4));
						System.out.println(
								"-------------------------------------------------------------------------------------");
					}
				}
			} else {
				printRows(key, "");
				System.out.println(
						"-------------------------------------------------------------------------------------");
			}
		}
	}

	public void getResultSet(String table, String have) {
		dates = new ArrayList<String>();
		values = new ArrayList<Double>();
		ResultSet rs = null;
		String sql = "SELECT * FROM " + table;
		String sqlselect = sql + " WHERE HAVE = " + "\"" + have + "\"";
		int wantName = CurrencyTypes.getReverseCurrencyMap().get(table.substring(4));
		int haveName = CurrencyTypes.getReverseCurrencyMap().get(have);
		try {
			Connection conn = createConnection();
			Statement stmt = conn.createStatement();

			if (table.contains("WANTChaos"))
				rs = stmt.executeQuery(sqlselect);
			else
				rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Double wantQ = rs.getDouble("wantQuantity");
				Double haveQ = rs.getDouble("haveQuantity");
				haveName = CurrencyTypes.getReverseCurrencyMap().get(rs.getString("have"));

				if (wantQ > haveQ)
					values.add(wantQ);
				else
					values.add(haveQ);

				dates.add(rs.getString("date"));
			}
			type = new CurrencyTypes(CurrencyTypes.League, wantName, haveName);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public CurrencyTypes getType() {
		return type;
	}

	public ArrayList<String> getDates() {
		return dates;
	}

	public ArrayList<Double> getValues() {
		return values;
	}

	/*
	 * Gets number of rows in a certain table
	 */
	private int getRowCount(String table) {
		String sql = "SELECT Count(*) FROM " + table;
		int count = 0;
		try (Connection conn = createConnection(); Statement stmt = conn.createStatement();) {
			ResultSet rs = stmt.executeQuery(sql);
			count = rs.getInt(1);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return count;
	}

	public void update(int id, String name, double capacity) {
		String sql = "UPDATE warehouses SET name = ? , " + "capacity = ? " + "WHERE id = ?";

		try (Connection conn = createConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, name);
			pstmt.setDouble(2, capacity);
			pstmt.setInt(3, id);
			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteRow(String table) {
		String sql = "DELETE FROM " + table;

		try (Connection conn = this.createConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			// execute the delete statement
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteRowFromAll() {
		for (String key : tables.keySet()) {
			deleteRow(key);
		}
	}

	public static void main(String args[]) {
		SQLite database = new SQLite("Legacy.db");
		database.createConnection();
		database.bulkInsert();
		// database.createNewTables();
		// database.deleteRowFromAll();
		database.printAll();
	}
}
