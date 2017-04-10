package application.view;

import javafx.fxml.FXML;

import org.controlsfx.control.textfield.TextFields;
import java.util.ArrayList;
import java.util.Collection;

import application.model.CurrencyTypes;
import application.util.SQLite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class GraphTabController {

	@FXML
	TextField wantField;

	@FXML
	TextField haveField;

	@FXML
	private LineChart<String, Number> lineChart;

	@FXML
	private CategoryAxis xAxis;

	private String tableName = "";

	private Collection<String> correctInputs = new ArrayList<String>();
	
	 /**
	   * Initializes Graph Tab when program starts.
	   */ 
	@FXML
	private void initialize() {
		correctInputs.add("Alchemys");
		correctInputs.add("Chaos");
		correctInputs.add("Exalted");
		correctInputs.add("Chisels");
		correctInputs.add("Chromes");
		correctInputs.add("Fusings");
		setTableName("WANTAlchemys");
		initialGraph();
		setGraphTypeFields();
	}
	
	 /**
	   * Sets a new Graph according to user input from text fields.
	   */ 
	@FXML
	private void setGraph() {
		if (isInputValid()) {
			ObservableList<XYChart.Series<String, Number>> currencyData = FXCollections.observableArrayList();
			ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();

			SQLite database = new SQLite(CurrencyTypes.League + ".db");
			database.createConnection();
			setTableName("WANT" + wantField.getText());
			System.out.println(haveField.getText());
			database.getResultSet(tableName,haveField.getText());
			CurrencyTypes type = database.getType();
			ArrayList<String> dates = database.getDates();
			ArrayList<Double> values = database.getValues();
			for (int i = 0; i < dates.size(); i++) {
				String date = dates.get(i).substring(0, 10);
				data.add(new XYChart.Data<String, Number>(date, values.get(i)));
			}

			String graphName = "Want : " + CurrencyTypes.getCurrencyMap().get(type.getCurrencyWant()) + " | Have : "
					+ CurrencyTypes.getCurrencyMap().get(type.getCurrencyHave());
			currencyData.add(new XYChart.Series<>(graphName, data));

			lineChart.setData(currencyData);
		}
	}
	
	/**
	  *  Creates initial Graph when program starts.
	  */
	private void initialGraph() {
		ObservableList<XYChart.Series<String, Number>> currencyData = FXCollections.observableArrayList();
		ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();

		SQLite database = new SQLite(CurrencyTypes.League + ".db");
		database.createConnection();
		database.getResultSet(tableName, "Chaos");
		CurrencyTypes type = database.getType();
		ArrayList<String> dates = database.getDates();
		ArrayList<Double> values = database.getValues();
		for (int i = 0; i < dates.size(); i++) {
			String date = dates.get(i).substring(0, 10);
			data.add(new XYChart.Data<String, Number>(date, values.get(i)));
		}

		String graphName = "Want : " + CurrencyTypes.getCurrencyMap().get(type.getCurrencyWant()) + " | Have : "
				+ CurrencyTypes.getCurrencyMap().get(type.getCurrencyHave());
		currencyData.add(new XYChart.Series<>(graphName, data));

		lineChart.setData(currencyData);
	}

	 /**
	   * Sets Text Fields in the Graph Tab.
	   */ 
	private void setGraphTypeFields() {
		wantField.setText("Want");
		haveField.setText("Have");
		// Adds autocomplete to textfields from controlsfx external library
		TextFields.bindAutoCompletion(wantField, correctInputs);
		TextFields.bindAutoCompletion(haveField, correctInputs);
	}

	 /**
	   * Checks if input is valid within text fields
	   */ 
	private boolean isInputValid() {
		String errorMessage = "";

		if (!correctInputs.contains(wantField.getText())) {
			errorMessage += "Want is not a valid currency type\n";
		}
		if (!correctInputs.contains(haveField.getText())) {
			errorMessage += "Have is not a valid currency type\n";
		}
		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errorMessage);
			alert.showAndWait();
			return false;
		}
	}

	private void setTableName(String tableName) {
		this.tableName = tableName;
	}
}