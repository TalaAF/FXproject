package carShopCompany;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application{

	 @Override
	    public void start(Stage primaryStage) {
//	        // Create the BorderPane with the car table view
//	        BorderPane root = CarTableView.buildCarTableView();
//
//	        
//	        // Create the scene
//	        Scene scene = new Scene(root, 1200, 900);
//
        
//	        // Set up the stage
//	        primaryStage.setTitle("Car Inventory Management System");
//	        primaryStage.setScene(scene);
//	        primaryStage.show();
		
		  LoginPage login = new LoginPage();
		    Scene scene = login.createLoginPage(primaryStage);
		   // scene.getStylesheets().add(getClass().getResource("/tableStyle.css").toExternalForm());
			    primaryStage.setMinWidth(1200);  // Set minimum width
			    primaryStage.setMinHeight(800);  // Set minimum height
			    primaryStage.setTitle("Login");
			    primaryStage.setScene(scene);
			    //primaryStage.setMaximized(true); // Optional: start maximized
//			    primaryStage.setOnCloseRequest(e -> dashboard.cleanup());
		    primaryStage.show();
			}
	    

	    public static void main(String[] args) {
	        launch(args);
	    }

}
