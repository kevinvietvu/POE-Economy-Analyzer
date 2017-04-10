package application.view;


import java.util.Collection;

import org.controlsfx.control.textfield.TextFields;
import application.MainApp;
import application.model.CurrencyRate;
import application.model.CurrencyTypes;
import application.util.Scraper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TableEditDialogController {
	
	@FXML Label tableLabel;
	
	@FXML TextField wantField;
	
	@FXML TextField haveField;

    private Stage dialogStage;
    private boolean okClicked = false;
    private int tableIndex = 0;
    private CurrencyTypes type;
    private OverviewController overview;
    private Collection<String> correctInputs = CurrencyTypes.getCurrencyMap().values();

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }
    
    public CurrencyTypes getNewCurrencyType()
    {
		return type;
    }
    
    public void setOverview(OverviewController overview) {
    	this.overview = overview;
    }

    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    /**
     * Returns true if the user clicked Save, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks Save.
     * Sets the new Currency Table from user input.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
        	int want = CurrencyTypes.getReverseCurrencyMap().get(wantField.getText());
        	int have = CurrencyTypes.getReverseCurrencyMap().get(haveField.getText());
        	CurrencyTypes newType = new CurrencyTypes(CurrencyTypes.League, want, have);
        	newType.setTableIndex(type.getTableIndex());
        	System.out.println(newType.getCurrencyWant() + " : " + newType.getCurrencyHave());
			TableView<CurrencyRate> tableView = ((TableView<CurrencyRate>) overview.getGridPane().getChildren().get(type.getTableIndex()));
			TableColumn<CurrencyRate, String> col2 =  (TableColumn<CurrencyRate, String>) tableView.getColumns().get(1);
			col2.setCellValueFactory(cellData -> cellData.getValue().wantProperty());
			TableColumn<CurrencyRate, String> col3 =  (TableColumn<CurrencyRate, String>) tableView.getColumns().get(2);
	        col2.setText("Want " + CurrencyTypes.getCurrencyMap().get(newType.getCurrencyWant()));
	        col3.setText("Have " + CurrencyTypes.getCurrencyMap().get(newType.getCurrencyHave()));       
        	MainApp.setCurrencyRateData(tableIndex, new Scraper().getData(newType));
        	okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
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
             alert.initOwner(dialogStage);
             alert.setTitle("Invalid Fields");
             alert.setHeaderText("Please correct invalid fields");
             alert.setContentText(errorMessage);
             alert.showAndWait();
             return false;
         }
    }
    
    /**
     * Sets the CurrencyType to be edited in the dialog along with editing labels with CurrencyType information.
     * 
     * @param type
     */
    public void setCurrencyTypeFields(CurrencyTypes selectedType) {
    	this.type = selectedType;
    	tableIndex = type.getTableIndex();
    	if (tableIndex != -1)
    	{
    		tableLabel.setText("Table " + (tableIndex + 1));
    		wantField.setText(CurrencyTypes.getCurrencyMap().get(type.getCurrencyWant()));
    		haveField.setText(CurrencyTypes.getCurrencyMap().get(type.getCurrencyHave()));
    		//Adds autocomplete to textfields from controlsfx external library
    		TextFields.bindAutoCompletion(wantField, correctInputs);
    		TextFields.bindAutoCompletion(haveField, correctInputs);
    	}
    	else
    	{
    		tableLabel.setText("New Table");
    		wantField.setText("");
    		haveField.setText("");
    		//Adds autocomplete to textfields from controlsfx external library
    		TextFields.bindAutoCompletion(wantField, correctInputs);
    		TextFields.bindAutoCompletion(haveField, correctInputs);
    	}
    	
    }
}