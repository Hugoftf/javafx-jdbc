package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrutyException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Product;
import model.service.CategoryService;
import model.service.ProductService;

public class ProductListControler implements Initializable, DataChangeListener {

	private ProductService productService;
	
	@FXML
	private TableView<Product> tableViewProduct;
	
	@FXML
	private TableColumn<Product, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Product, String> tableColumnName;
	
	@FXML
	private TableColumn<Product, Double> tableColumnPrice;
	
	@FXML
	private TableColumn<Product, java.util.Date> tableColumnManufacturing;
	
	@FXML
	private TableColumn<Product, String> tableColumnMade;
	
	
	@FXML
	private TableColumn<Product, Product> tableColumnEDIT;
	
	@FXML
	private TableColumn<Product, Product> tableColumnREMOVE;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Product> obsListProduct;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		
		Stage parentStage = Utils.currentStage(event);
		Product product = new Product();
		createDialogForm(product, "ProductForm.fxml", parentStage);
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}
	
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}


	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		tableColumnManufacturing.setCellValueFactory(new PropertyValueFactory<>("manufacturing"));
		Utils.formatTableColumnDate(tableColumnManufacturing, "dd/MM/yyyy");
		tableColumnMade.setCellValueFactory(new PropertyValueFactory<>("made"));
		Utils.formatTableColumnDouble(tableColumnPrice, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewProduct.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	public void updateTableView() {
		if (productService == null ) {
			throw new IllegalStateException("Service was null");
		}
		List<Product> list = productService.findAll();
		obsListProduct = FXCollections.observableArrayList(list);
		tableViewProduct.setItems(obsListProduct);
		initEditButtons();
		initRemoveButtons();
	}
	
	private void createDialogForm(Product product, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			ProductFormControler controler = loader.getController();
			controler.setProduct(product);
			controler.setServices(new ProductService(), new CategoryService());
			controler.loadAssociatedObjects();
			controler.subscribeDataChangeListener(this);
			controler.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Product Data: ");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			Alerts.showAlert("IOexception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}


	@Override
	public void onDataChanged() {
		updateTableView();
		
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Product product, boolean empty) {
				super.updateItem(product, empty);
				if (product == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(product, "/gui/ProductForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Product product, boolean empty) {
				super.updateItem(product, empty);
				if (product == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(product));
			}
		});
	}


	private void  removeEntity(Product product) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete");
	
		if (result.get() == ButtonType.OK) {
			if (productService == null) {
				throw new IllegalStateException("Product Service was null");
			}
			try {
				productService.remove(product);
				updateTableView();
			}
			catch (DbIntegrutyException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
