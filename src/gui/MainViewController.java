package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.service.CategoryService;
import model.service.ProductService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemProduct;
	
	@FXML
	private MenuItem menuItemCategory;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemProduct() {
		loadView("/gui/ProductList.fxml", (ProductListControler controler) -> {
			controler.setProductService(new ProductService());
			controler.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemCategory() {
		loadView("/gui/CategoryList.fxml", (CategoryListControler controler) -> {
			controler.setCategoryService(new CategoryService());
			controler.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAbout() {
		loadView("/gui/About.fxml", x-> {});
	}
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initAction) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVbox.getChildren().get(0);
			mainVbox.getChildren().clear();
			mainVbox.getChildren().add(mainMenu);
			mainVbox.getChildren().addAll(newVbox.getChildren());
			
			T controler = loader.getController();
			initAction.accept(controler);
			
			
		} catch (IOException e) {
			Alerts.showAlert("IOexception", "Error loading view", e.getMessage(), AlertType.ERROR);
			
		}
		
	}
	
	

}
