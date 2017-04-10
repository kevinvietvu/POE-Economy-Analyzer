package application;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import application.util.Scraper;
import application.model.CurrencyRate;
import application.model.CurrencyTypes;
import application.view.OverviewController;
import application.view.TableEditDialogController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private final CurrencyTypes wantAlchHaveChaos = new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Alchemys, CurrencyTypes.Chaos);
	private final CurrencyTypes wantChaosHaveAlch = new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chaos, CurrencyTypes.Alchemys);
	private final CurrencyTypes wantExaltedHaveChaos = new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Exalted, CurrencyTypes.Chaos);
	private final CurrencyTypes wantChaosHaveExalted = new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chaos, CurrencyTypes.Exalted);
	private final CurrencyTypes wantFusingsHaveChaos = new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Fusings, CurrencyTypes.Chaos);
	private final CurrencyTypes wantChaosHaveFusings = new CurrencyTypes(CurrencyTypes.League, CurrencyTypes.Chaos, CurrencyTypes.Fusings);
    private Stage primaryStage;
    private BorderPane rootLayout;
    private OverviewController overviewController;
    private static Boolean test = true;
    
    /**
     * The data as an observable list of POE currency.
     */
    private ObservableList<CurrencyRate> currencyRateData;
    private ObservableList<CurrencyRate> currencyRateData2;
    private ObservableList<CurrencyRate> currencyRateData3;
    private ObservableList<CurrencyRate> currencyRateData4;
    private ObservableList<CurrencyRate> currencyRateData5;
    private ObservableList<CurrencyRate> currencyRateData6;
    
    private static final ArrayList<ObservableList<CurrencyRate>> allCurrencyRates = new ArrayList<ObservableList<CurrencyRate>>();
    
    /**
     * Constructor
     */
    public MainApp() {
    	if (test) {
    	CurrencyTypes.setCurrencyMap();
    	currencyRateData = FXCollections.observableArrayList(new Scraper().getData(wantAlchHaveChaos));
    	currencyRateData2 = FXCollections.observableArrayList(new Scraper().getData(wantChaosHaveAlch));
    	currencyRateData3 = FXCollections.observableArrayList(new Scraper().getData(wantExaltedHaveChaos));
    	currencyRateData4 = FXCollections.observableArrayList(new Scraper().getData(wantChaosHaveExalted));
    	currencyRateData5 = FXCollections.observableArrayList(new Scraper().getData(wantFusingsHaveChaos));
    	currencyRateData6 = FXCollections.observableArrayList(new Scraper().getData(wantChaosHaveFusings));
    	allCurrencyRates.add(currencyRateData);
    	allCurrencyRates.add(currencyRateData2);
    	allCurrencyRates.add(currencyRateData3);
    	allCurrencyRates.add(currencyRateData4);
    	allCurrencyRates.add(currencyRateData5);
    	allCurrencyRates.add(currencyRateData6);
    	}
    }
    
    public static void setCurrencyRateData(int index, CurrencyRate[] currencyRates) {
       allCurrencyRates.set(index, FXCollections.observableArrayList(currencyRates));
    }
    
    public static CurrencyTypes getCurrencyType(int index) {
    	return allCurrencyRates.get(index).get(0).getCurrencyTypes();
    }
    
	public static ArrayList<ObservableList<CurrencyRate>> getAllCurrencyRates() {
		return allCurrencyRates;
	}

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Poe Trading App");

        initRootLayout();

        showCurrencyOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the currency tables overview inside the root layout.
     */
    public void showCurrencyOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Overview.fxml"));
            AnchorPane overview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(overview);
            
            // Give the controller access to the main app.
            overviewController = loader.getController();
            overviewController.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Opens a dialog to edit details for a currency table. If the user
     * clicks OK, the changes are saved into the overview and true
     * is returned.
     * 
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showTableEditDialog(CurrencyTypes type) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TableEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Table");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            TableEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCurrencyTypeFields(type);       
            controller.setOverview(overviewController);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

}