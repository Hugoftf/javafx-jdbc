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
import model.entities.Category;
import model.service.CategoryService;

public class CategoryListControler implements Initializable, DataChangeListener {

	private CategoryService categoryService;
	
	@FXML
	private TableView<Category> tableViewCategory;
	
	@FXML
	private TableColumn<Category, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Category, String> tableColumnName;
	
	
	@FXML
	private TableColumn<Category, Category> tableColumnEDIT;
	
	@FXML
	private TableColumn<Category, Category> tableColumnREMOVE;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Category> obsListCategory;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		
		Stage parentStage = Utils.currentStage(event);
		Category category = new Category();
		createDialogForm(category, "CategoryForm.fxml", parentStage);
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}
	
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}


	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCategory.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	public void updateTableView() {
		if (categoryService == null ) {
			throw new IllegalStateException("Service was null");
		}
		List<Category> list = categoryService.findAll();
		obsListCategory = FXCollections.observableArrayList(list);
		tableViewCategory.setItems(obsListCategory);
		initEditButtons();
		initRemoveButtons();
	}
	
	private void createDialogForm(Category category, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			CategoryFormControler controler = loader.getController();
			controler.setCategory(category);
			controler.setCategoryService(new CategoryService());
			controler.subscribeDataChangeListener(this);
			controler.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Category Data: ");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Category, Category>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Category category, boolean empty) {
				super.updateItem(category, empty);
				if (category == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(category, "/gui/CategoryForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Category, Category>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Category category, boolean empty) {
				super.updateItem(category, empty);
				if (category == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(category));
			}
		});
	}


	private void  removeEntity(Category category) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete");
	
		if (result.get() == ButtonType.OK) {
			if (categoryService == null) {
				throw new IllegalStateException("Category Service was null");
			}
			try {
				categoryService.remove(category);
				updateTableView();
			}
			catch (DbIntegrutyException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
