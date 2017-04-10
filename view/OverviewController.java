package application.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import application.util.Scraper;
import application.MainApp;
import application.model.CurrencyRate;
import application.model.CurrencyTypes;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.util.Pair;

public class OverviewController {

	@FXML
	private GridPane grid;

	@FXML
	private ChoiceBox<String> choiceBox;

	@FXML
	private Label label;

	@FXML
	private ToggleButton toggleButton;

	// Reference to the main application.
	private MainApp mainApp;
	private Boolean check = true;
	private IntegerProperty refreshTime = new SimpleIntegerProperty(0);
	private Timeline timer;
	private Integer countdown = 0;
	private CurrencyTypes type;
	private boolean[][] matrix = new boolean[4][4];

	ArrayList<ObservableList<CurrencyRate>> allCurrencyRates;

	/**
	 * The constructor is called before the initialize() method.
	 */
	public OverviewController() {
	}

	public GridPane getGridPane() {
		return grid;
	}

	/**
	 * Adds a new preset currency table to be viewed in the overview.
	 */
	@FXML
	private void addTable() {
		Pair<Integer, Integer> tableCoord = getCoords();
		if (tableCoord == null || tableCoord.getKey() == null || tableCoord.getValue() == null) {
			return;
		}
		type = new CurrencyTypes(CurrencyTypes.League, 4, 3);
		type.setTableIndex(grid.getChildren().size() - 1);
		TableView<CurrencyRate> tableView = new TableView<CurrencyRate>();
		tableView.setPrefSize(480, 250);
		grid.setGridLinesVisible(false);
		grid.add(tableView, tableCoord.getValue(), tableCoord.getKey());
		grid.setGridLinesVisible(true);
		ObservableList<CurrencyRate> currencyRates = FXCollections.observableArrayList(new Scraper().getData(type));
		allCurrencyRates.add(currencyRates);
		TableColumn<CurrencyRate, String> col = new TableColumn<CurrencyRate, String>();
		col.setCellValueFactory(cellData -> cellData.getValue().IGNProperty());
		TableColumn<CurrencyRate, String> col2 = new TableColumn<CurrencyRate, String>();
		col2.setCellValueFactory(cellData -> cellData.getValue().wantProperty());
		TableColumn<CurrencyRate, String> col3 = new TableColumn<CurrencyRate, String>();
		col3.setCellValueFactory(cellData -> cellData.getValue().haveProperty());
		tableView.getColumns().add(col);
		tableView.getColumns().add(col2);
		tableView.getColumns().add(col3);
		// Listen for selection changes for opening edit dialog
		tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				type = newValue.getCurrencyTypes();
				System.out.println(
						type.getTableIndex() + " : " + CurrencyTypes.getCurrencyMap().get(type.getCurrencyWant())
								+ " : " + CurrencyTypes.getCurrencyMap().get(type.getCurrencyHave()));
			}
		}

		);
		col2.setText("Want " + CurrencyTypes.getCurrencyMap().get(type.getCurrencyWant()));
		col3.setText("Have " + CurrencyTypes.getCurrencyMap().get(type.getCurrencyHave()));
		tableView.setItems(currencyRates);

	}

	/**
	 * Returns the x,y position of an empty pane within Grid Pane and marks the
	 * spot filled within the matrix as true
	 */
	private Pair<Integer, Integer> getCoords() {
		Pair<Integer, Integer> coords = null;
		outer: for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (!matrix[i][j]) {
					System.out.println(i + " : " + j);
					coords = new Pair<Integer, Integer>(i, j);
					matrix[i][j] = true;
					break outer;
				}
			}
		}
		return coords;
	}

	/**
	 * Initializes matrix to represent Grid Pane
	 */
	private void initializeMatrix() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				matrix[i][j] = false;
			}
		}
		matrix[0][0] = true;
		matrix[0][1] = true;
		matrix[1][0] = true;
		matrix[1][1] = true;
		matrix[2][0] = true;
		matrix[2][1] = true;
	}

	/**
	 * Removes Table from Grid Pane and marks spot empty within matrix as false
	 */
	@FXML
	private void removeTable() {
		outer: for (int i = matrix.length - 1; i >= 0; i--) {
			for (int j = matrix[0].length - 1; j >= 0; j--) {
				if (matrix[i][j]) {
					matrix[i][j] = false;
					break outer;
				}
			}
		}
		grid.setGridLinesVisible(false);
		grid.getChildren().remove(grid.getChildren().size() - 1);
		allCurrencyRates.remove(grid.getChildren().size());
		grid.setGridLinesVisible(true);
	}

	/**
	 * Called when the user clicks on the Refresh button.
	 */
	@FXML
	private void handleRefresh() {
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						// Background work
						for (int i = 0; i < allCurrencyRates.size(); i++) {
							if (grid.getChildren().get(i) instanceof TableView) {
								MainApp.setCurrencyRateData(i, new Scraper().getData(MainApp.getCurrencyType(i)));
							}
						}
						final CountDownLatch latch = new CountDownLatch(1);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									// FX work done here
									for (int i = 0; i < allCurrencyRates.size(); i++) {
										if (grid.getChildren().get(i) instanceof TableView) {
											@SuppressWarnings("unchecked")
											TableView<CurrencyRate> tableView = ((TableView<CurrencyRate>) grid
													.getChildren().get(i));
											tableView.setItems(allCurrencyRates.get(i));
										}
									}
								} finally {
									latch.countDown();
								}
							}
						});
						latch.await();
						// Keep with the background work
						return null;
					}
				};
			}
		};
		service.start();
	}

	@FXML
	public void webLinkOnTableClick() {
		for (int i = 0; i < grid.getChildren().size(); i++) {
			System.out.println(grid.getChildren().get(i));
		}
		if (check)
			check = false;
		else
			check = true;
	}

	public void openWebPage(String url) {
		try {
			if (check) {
				Desktop.getDesktop().browse(new URI(url));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}

	 /**
	   * Refresh the currency tables' data if auto refresh is toggled.
	   * Auto refresh time is dependent on user input from choice box
	   */ 
	@FXML
	public void autoRefresh() {
		if (refreshTime.getValue() == 0) {
			toggleButton.setSelected(false);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Auto Refresh");
			alert.setHeaderText("Please Select Auto Refresh Time");
			alert.showAndWait();
		} else if (!toggleButton.isSelected() && timer != null) {
			label.setText("Auto Refresh Off");
			timer.stop();
			timer = null;
			toggleButton.setSelected(false);
		} else {
			toggleButton.setSelected(true);
			countdown = refreshTime.get();
			label.setText("Auto Refreshing in " + countdown.toString() + "s");
			timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					countdown--;
					label.setText("Auto Refreshing in " + countdown.toString() + "s");
					if (countdown <= 0) {
						handleRefresh();
						countdown = refreshTime.get() + 1;
						timer.playFromStart();
					}
				}
			}));
			timer.setCycleCount(Timeline.INDEFINITE);
			timer.playFromStart();
		}
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@SuppressWarnings("unchecked")
	@FXML
	private void initialize() {
		// Initialize the currency table with the three columns.
		for (int i = 0; i < MainApp.getAllCurrencyRates().size(); i++) {
			// if statement not necessary unless you use
			if (grid.getChildren().get(i) instanceof TableView) {
				TableView<CurrencyRate> tableView = ((TableView<CurrencyRate>) grid.getChildren().get(i));
				TableColumn<CurrencyRate, String> col = (TableColumn<CurrencyRate, String>) tableView.getColumns()
						.get(0);
				col.setCellValueFactory(cellData -> cellData.getValue().IGNProperty());
				TableColumn<CurrencyRate, String> col2 = (TableColumn<CurrencyRate, String>) tableView.getColumns()
						.get(1);
				col2.setCellValueFactory(cellData -> cellData.getValue().wantProperty());
				TableColumn<CurrencyRate, String> col3 = (TableColumn<CurrencyRate, String>) tableView.getColumns()
						.get(2);
				col3.setCellValueFactory(cellData -> cellData.getValue().haveProperty());
				// sets table index with currency type
				MainApp.getCurrencyType(i).setTableIndex(i);
				// Listen for selection changes.
				tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						type = newValue.getCurrencyTypes();
						System.out.println(type.getTableIndex() + " : "
								+ CurrencyTypes.getCurrencyMap().get(type.getCurrencyWant()) + " : "
								+ CurrencyTypes.getCurrencyMap().get(type.getCurrencyHave()));
					}
				});
				col2.setText(
						"Want " + CurrencyTypes.getCurrencyMap().get(MainApp.getCurrencyType(i).getCurrencyWant()));
				col3.setText(
						"Have " + CurrencyTypes.getCurrencyMap().get(MainApp.getCurrencyType(i).getCurrencyHave()));
			}
		}

		// initializes choice box for the auto refresh time options
		int[] choices = new int[] { 30, 60, 300, 600 };
		choiceBox.setItems(FXCollections.observableArrayList("30 seconds", "1 minutes", "5 minutes", "10 minutes"));
		choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				refreshTime.set(choices[newValue.intValue()]);
			}
		});

		// Refresh button functionality
		refreshTime.addListener((observable, oldValue, newValue) -> {
			if (timer != null) {
				timer.stop();
				countdown = refreshTime.get();
				label.setText("Auto Refreshing in " + countdown.toString() + "s");
				timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						countdown--;
						label.setText("Auto Refreshing in " + countdown.toString() + "s");
						if (countdown <= 0) {
							handleRefresh();
							countdown = refreshTime.get() + 1;
							timer.playFromStart();
						}
					}
				}));
				timer.setCycleCount(Timeline.INDEFINITE);
				timer.playFromStart();
			}
		});

		// initialize 2d matrix to keep track of what is on the grid layout
		initializeMatrix();

	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit a currency table
	 */
	@FXML
	private void handleEditTable() {
		if (type != null) {
			boolean okClicked = mainApp.showTableEditDialog(type);
			if (okClicked)
				handleRefresh();
		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Table Selected");
			alert.setContentText("Please Select A Table");
			alert.showAndWait();
		}
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		allCurrencyRates = MainApp.getAllCurrencyRates();
		// Add observable list data to the table
		for (int i = 0; i < allCurrencyRates.size(); i++) {
			if (grid.getChildren().get(i) instanceof TableView) {
				@SuppressWarnings("unchecked")
				TableView<CurrencyRate> tableView = ((TableView<CurrencyRate>) grid.getChildren().get(i));
				tableView.setItems(allCurrencyRates.get(i));
			}
		}

	}
}