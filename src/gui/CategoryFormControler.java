package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constrains;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Category;
import model.exception.ValidationException;
import model.service.CategoryService;

public class CategoryFormControler implements Initializable {

	private Category category;
	
	private CategoryService categoryService;
	
	private List<DataChangeListener> dataChangeListenerer =  new ArrayList<DataChangeListener>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListenerer.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent actionEvent) {
		if (category == null) {
			throw new IllegalStateException("Category was null");
		}
		if (categoryService == null) {
			throw new IllegalStateException("Category Service was null");
		}
		try {
		category = getFormData();
		categoryService.saveOrUpdate(category);
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

	private Category getFormData() {
		Category category = new Category();
		
		ValidationException exception = new ValidationException("Validation error");
		
		category.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		category.setName(txtName.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return category;
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
		Constrains.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData() {
		if (category == null) {
			throw new IllegalStateException("Category was null");
		}
		txtId.setText(String.valueOf(category.getId()));
		txtName.setText(category.getName());
		
	}
	
	private void setErrorMensages(Map<String, String> erros) {
		Set<String> fields = erros.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(erros.get("name"));
		}
	}

}
