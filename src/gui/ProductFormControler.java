package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constrains;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Category;
import model.entities.Product;
import model.exception.ValidationException;
import model.service.CategoryService;
import model.service.ProductService;

public class ProductFormControler implements Initializable {

	private Product product;
	
	private ProductService productService;
	
	private CategoryService categoryService;
	
	private List<DataChangeListener> dataChangeListenerer =  new ArrayList<DataChangeListener>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtPrice;
	
	@FXML
	private TextField txtMade;
	
	@FXML
	private ComboBox<Category> comboBoxCategory;
	
	@FXML
	private DatePicker dpManufacturing;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorPrice;
	
	@FXML
	private Label labelErrorManufacturing;
	
	@FXML
	private Label labelErrorMade;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	private ObservableList<Category> obsList;
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public void setServices(ProductService productService, CategoryService categoryService) {
		this.productService = productService;
		this.categoryService = categoryService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListenerer.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent actionEvent) {
		if (product == null) {
			throw new IllegalStateException("Product was null");
		}
		if (productService == null) {
			throw new IllegalStateException("Product Service was null");
		}
		try {
		product = getFormData();
		productService.saveOrUpdate(product);
		notifyDataChangeListener();
		Utils.currentStage(actionEvent).close();
		}
		catch (DbException e) {
			Alerts.showAlert("Erro saving object", null, e.getMessage(), AlertType.ERROR);
		}
		catch (ValidationException e) {
			setErrorMensages(e.getErrors());
		}
	}
	
	private void notifyDataChangeListener() {
		for (DataChangeListener listener: dataChangeListenerer) {
			listener.onDataChanged();
		}
		
	}

	private Product getFormData() {
		Product product = new Product();
		
		ValidationException exception = new ValidationException("Validation error");
		
		product.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		else {
			product.setName(txtName.getText());
		}
		
		if (txtMade.getText() == null || txtMade.getText().trim().equals("")) {
			exception.addError("made", "Field can't be empty");
		}
		else {
			product.setMade(txtMade.getText());
		}
		
		if (dpManufacturing.getValue() == null ) {
			exception.addError("Manufacturing", "Field can't be empty");
		}
		else {
			Instant instant = Instant.from(dpManufacturing.getValue().atStartOfDay(ZoneId.systemDefault()));
			product.setManufacturing(Date.from(instant));
		}
		
		if (txtPrice.getText() == null || txtPrice.getText().trim().equals("")) {
			exception.addError("price", "Field can't be empty");
		}
		else {
			product.setPrice(Utils.tryParseToDouble(txtPrice.getText()));
		}
		
		product.setCategory(comboBoxCategory.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return product;
	}

	@FXML
	public void onBtCancelAction(ActionEvent actionEvent) {
		Utils.currentStage(actionEvent).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		Constrains.setTextFieldInteger(txtId);
		Constrains.setTextFieldMaxLength(txtName, 70);
		Constrains.setTextFieldDouble(txtPrice);
		Constrains.setTextFieldMaxLength(txtMade, 70);
		Utils.formatDatePicker(dpManufacturing, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
		
	}
	
	public void updateFormData() {
		if (product == null) {
			throw new IllegalStateException("Product was null");
		}
		txtId.setText(String.valueOf(product.getId()));
		txtName.setText(product.getName());
		Locale.setDefault(Locale.US);
		txtPrice.setText(String.format("%.2f", product.getPrice()));
		txtMade.setText(product.getMade());
		if (product.getManufacturing() != null) {
			dpManufacturing.setValue(LocalDate.ofInstant(product.getManufacturing().toInstant(), ZoneId.systemDefault()));
		}
		
		if (product.getCategory() == null ) {
			comboBoxCategory.getSelectionModel().selectFirst();
		}
		else {
			comboBoxCategory.setValue(product.getCategory());
		}
		
		
	}
	
	public void loadAssociatedObjects() {
		if (categoryService == null) {
			throw new IllegalStateException("Category Service was null");
		}
		List<Category> list = categoryService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxCategory.setItems(obsList);
	}
	
	private void setErrorMensages(Map<String, String> erros) {
		Set<String> fields = erros.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(erros.get("name"));
		}
		else { 
			labelErrorName.setText("");
		}
		
		if (fields.contains("made")) {
			labelErrorMade.setText(erros.get("made"));
		}
		else { 
			labelErrorMade.setText("");
		}
		
		if (fields.contains("price")) {
			labelErrorPrice.setText(erros.get("price"));
		}
		else { 
			labelErrorPrice.setText("");
		}
		
		if (fields.contains("Manufacturing")) {
			labelErrorManufacturing.setText(erros.get("Manufacturing"));
		}
		else { 
			labelErrorManufacturing.setText("");
		}
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Category>, ListCell<Category>> factory = lv -> new ListCell<Category>() {
			@Override
			protected void updateItem(Category item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxCategory.setCellFactory(factory);
		comboBoxCategory.setButtonCell(factory.call(null));
	} 

}
