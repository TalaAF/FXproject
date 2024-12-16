package carShopCompany;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SignUpPage {

    private Runnable onSignUpSuccess; // Callback for successful sign-up

    public void setOnSignUpSuccess(Runnable onSignUpSuccess) {
        this.onSignUpSuccess = onSignUpSuccess;
    }

    public Scene createSignUpScene(Stage stage) {

        StackPane stackPane = new StackPane();

        // Create an Image and set it
        Image image = new Image("/speed.jpg");  // Use your image path here
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(1433);  // Set image width
        imageView.setFitHeight(750);  // Set image height
        
        VBox signUpForm = new VBox(20);
        signUpForm.setPadding(new Insets(25));
        signUpForm.setPrefWidth(465);  // Preferred width
        signUpForm.setPrefHeight(620);
        signUpForm.setMaxWidth(465);  
        signUpForm.setMaxHeight(620);
        signUpForm.setAlignment(Pos.CENTER);
        signUpForm.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85); -fx-background-radius: 10;");

        Region space1 = new Region();
        Region space2 = new Region();

        space1.setMinHeight(26); // Space between userNameField and passwordField
        space2.setMinHeight(9);
        
        Label shopName=new Label("SpeedShift"); 
        shopName.setStyle("-fx-text-fill: #B22222;-fx-font-size: 15px;-fx-font-weight: bold;-fx-font-family: 'Verdana';");
        
        Label create=new Label("Create an account");
        create.setStyle("-fx-text-fill: #FFFFFF;-fx-font-size: 22px;-fx-font-weight: bold;-fx-font-family: 'Verdana';");
        // Add form fields
        HBox name=new HBox(9);
        VBox fName=new VBox(5);
        VBox lName=new VBox(5);
        VBox userN=new VBox(5);
        VBox id=new VBox(5);
        VBox pass=new VBox(5);
        
        Label firstNameLabel = new Label("First Name");
        firstNameLabel.setStyle("-fx-text-fill: #ffffff;-fx-font-size: 13px;-fx-font-family: Arial;");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");
        firstNameField.setPrefWidth(195);  
        firstNameField.setPrefHeight(35);
        firstNameField.setStyle("-fx-background-color: #333333;-fx-background-radius:8; -fx-padding: 10;-fx-text-fill: #FFFFFF;");
        fName.getChildren().addAll(firstNameLabel, firstNameField);
        
        Label lastNameLabel = new Label("Last Name");
        lastNameLabel.setStyle("-fx-text-fill: #ffffff;-fx-font-size: 13px;-fx-font-family: Arial;");
        TextField lastNameField = new TextField();  // Last name field added
        lastNameField.setPromptText("Enter your last name");
        lastNameField.setPrefWidth(195);  
        lastNameField.setPrefHeight(35);
        lastNameField.setStyle("-fx-background-color: #333333;-fx-background-radius:8; -fx-padding: 10;-fx-text-fill: #FFFFFF;");
        lName.getChildren().addAll(lastNameLabel, lastNameField);
        
        name.getChildren().addAll(fName, lName);
        
        Label employeeIDLabel = new Label("Employee ID");
        employeeIDLabel.setStyle("-fx-text-fill: #ffffff;-fx-font-size: 13px;-fx-font-family: Arial;");
        TextField employeeIDField = new TextField();
        employeeIDField.setPromptText("Enter your ID");
        employeeIDField.setMaxWidth(400);  
        employeeIDField.setMaxHeight(35);
        employeeIDField.setStyle("-fx-background-color: #333333;-fx-background-radius:8; -fx-padding: 10;-fx-text-fill: #FFFFFF;");
        id.getChildren().addAll(employeeIDLabel, employeeIDField);
        
        Label userNameLabel = new Label("Username");
        userNameLabel.setStyle("-fx-text-fill: #ffffff;-fx-font-size: 13px;-fx-font-family: Arial;");
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter your username");
        userNameField.setMaxWidth(400);  
        userNameField.setMaxHeight(35);
        userNameField.setStyle("-fx-background-color: #333333;-fx-background-radius:8; -fx-padding: 10;-fx-text-fill: #FFFFFF;");
        userN.getChildren().addAll(userNameLabel, userNameField);

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-text-fill: #ffffff;-fx-font-size: 13px;-fx-font-family: Arial;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(400);  
        passwordField.setMaxHeight(35);
        passwordField.setStyle("-fx-background-color: #333333;-fx-background-radius:8; -fx-padding: 10;-fx-text-fill: #FFFFFF;");
        pass.getChildren().addAll(passwordLabel, passwordField);

        

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setOnAction(e -> navigateToLoginPage(stage));
        loginLink.setTextFill(Color.LIGHTGRAY);

        Button signUpButton = new Button("Sign Up");
        signUpButton.setMaxWidth(400);  
        signUpButton.setPrefHeight(36);
        signUpButton.setMaxHeight(35);
        signUpButton.setStyle("-fx-font-family: 'Arial';-fx-background-color: #B22222; -fx-text-fill: white; -fx-background-radius: 8;-fx-font-weight: bold;-fx-font-size: 15px;");

        // Set the button action
        signUpButton.setOnAction(event -> handleSignUp(userNameField, passwordField, employeeIDField, firstNameField, lastNameField, stage));

        // Create the scene
        signUpForm.getChildren().addAll(shopName,space1,
        		create,space2,name,id,userN,pass,
                                         signUpButton,loginLink);

        stackPane.getChildren().addAll(imageView, signUpForm);
        
        return new Scene(stackPane, 1433, 750);
    }

    private void handleSignUp(TextField userNameField, PasswordField passwordField, 
                              TextField employeeIDField, TextField firstNameField, 
                              TextField lastNameField, Stage stage) {
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String employeeIDStr = employeeIDField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (userName.isEmpty() || password.isEmpty() || employeeIDStr.isEmpty() || 
            firstName.isEmpty() || lastName.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        try {
            int employeeID = Integer.parseInt(employeeIDStr);
            User user = new User(userName, password);
            UserASU userASU = new UserASU();

            if (userASU.signUpUser(user, employeeID, firstName, lastName)) {
                showAlert("Success", "User successfully signed up!");
                if (onSignUpSuccess != null) {
                    onSignUpSuccess.run(); // Navigate to the next page
                }
            } else {
                showAlert("Error", "Sign-up failed. Ensure you are a valid employee with the correct first name and last name.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Employee ID.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void navigateToLoginPage(Stage stage) {
        LoginPage loginPage = new LoginPage();
        Scene loginScene = loginPage.createLoginPage(stage);
        stage.setScene(loginScene);  // Switch to login page
    }
}
