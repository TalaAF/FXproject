package carShopCompany;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginPage {


	
    public  Scene createLoginPage(Stage stage) {
        
    	//Font loraFont = Font.loadFont(getClass().getResourceAsStream("/resources/Lora-Medium.ttf"), 12);
    	StackPane stackPane = new StackPane();

        // Create an Image and set it
        Image image = new Image("/speed.jpg");  // Use your image path here
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(1433);  // Set image width
        imageView.setFitHeight(800);  // Set image height
        
        
        // Create the login form
        VBox loginForm = new VBox(20);
        loginForm.setPadding(new Insets(25));
        loginForm.setPrefWidth(465);  // Preferred width
        loginForm.setPrefHeight(485);
        loginForm.setMaxWidth(465);  
        loginForm.setMaxHeight(485);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85); -fx-background-radius: 10;");


        //spaces
        Region space1 = new Region();
        Region space2 = new Region();

        space1.setMinHeight(40); // Space between userNameField and passwordField
        space2.setMinHeight(15);
        // Add fields and buttons
        Label shopName=new Label("SpeedShift");
        //shopName.setFont(loraFont);
        shopName.setStyle("-fx-text-fill: #B22222;-fx-font-size: 15px;-fx-font-weight: bold;-fx-font-family: 'Verdana';");
        VBox welcomeMes=new VBox(5);
        welcomeMes.setAlignment(Pos.CENTER);
        Label welcome=new Label("Welcome Back");
        //welcome.setFont(loraFont);
        welcome.setStyle("-fx-text-fill: #FFFFFF;-fx-font-size: 22px;-fx-font-weight: bold;-fx-font-family: 'Verdana';");
        Label message=new Label("Enter your user name and password to access your account ");
        message.setStyle("-fx-text-fill: LightGrey;-fx-font-size: 12px;");
        welcomeMes.getChildren().addAll(welcome, message);
        VBox nameBox=new VBox(4);
        Label userNameLabel = new Label("Username");
        userNameLabel.setTextFill(Color.WHITE);
        userNameLabel.setFont(Font.font("Arial", 13));

        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter your username");
        userNameField.setMaxWidth(400);  
        userNameField.setMaxHeight(35);
        userNameField.setStyle("-fx-background-color: #333333;-fx-background-radius:8; -fx-padding: 10;-fx-text-fill: #FFFFFF;");
        nameBox.getChildren().addAll(userNameLabel, userNameField);
        
        VBox passBox=new VBox(5);
        Label passwordLabel = new Label("Password");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(Font.font("Arial", 14));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(400);  
        passwordField.setMaxHeight(35);
        passwordField.setStyle("-fx-background-radius: 8; -fx-padding: 10;-fx-background-color: #333333;-fx-text-fill: #FFFFFF;");
        
        passBox.getChildren().addAll(passwordLabel, passwordField);

        
        Button signInButton = new Button("Sign In");
        signInButton.setMaxWidth(400);  
        signInButton.setPrefHeight(36);
        signInButton.setStyle("-fx-font-family: 'Arial';-fx-background-color: #B22222; -fx-text-fill: white; -fx-background-radius: 8;-fx-font-weight: bold;-fx-font-size: 15px;");
        

        Hyperlink signUpLink = new Hyperlink("Don't have an account? Sign Up");
        signUpLink.setTextFill(Color.LIGHTGRAY);
        signUpLink.setFont(Font.font("Arial", 12));


        loginForm.getChildren().addAll(shopName,space1,welcomeMes,space2, nameBox, passBox,signInButton,signUpLink);

        stackPane.getChildren().addAll(imageView, loginForm);
        Scene scene = new Scene(stackPane, 1433, 750);
        

        // Button actions
        signInButton.setOnAction(e -> handleSignIn(userNameField.getText(), passwordField.getText(), stage));
        signUpLink.setOnAction(e -> showSignUpPage(stage));
        
        userNameField.setOnAction(e -> handleSignIn(userNameField.getText(), passwordField.getText(), stage));
        passwordField.setOnAction(e -> handleSignIn(userNameField.getText(), passwordField.getText(), stage));

        return scene;
    }

    private  void handleSignIn(String username, String password, Stage stage) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password must be filled.");
            return;
        }

        UserASU userASU = new UserASU();
        if (userASU.validateUser(username, password)) {
            showDashboardPage(stage);
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    private  void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private  void showSignUpPage(Stage stage) {
        SignUpPage signUpPage = new SignUpPage();
        Scene signUpScene = signUpPage.createSignUpScene(stage);
        stage.setScene(signUpScene);
    }

  private  void showDashboardPage(Stage primaryStage) {
        Dashboard dashboard = new Dashboard();
    Scene scene = new Scene(dashboard, 1200, 800);
    scene.getStylesheets().add(getClass().getResource("/tableStyle.css").toExternalForm());
	    
	    primaryStage.setTitle("Car Shop Management System");
	    primaryStage.setScene(scene);
	    primaryStage.setMaximized(true); // Optional: start maximized
	    primaryStage.setOnCloseRequest(e -> dashboard.cleanup());
//	    primaryStage.show();
 }
}
