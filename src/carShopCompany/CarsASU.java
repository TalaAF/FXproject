package carShopCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class CarsASU {

	private static final String PRIMARY_RED = "#B22222";
	 private static final String SURFACE_DARK = "#2A2A2A";
    // Static method to get all cars
    public static ArrayList<Car> getAllCars() {
        ArrayList<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM Cars";

        try (Connection conn = DBconnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Car car = new Car(
                    rs.getInt("carID"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("vin")
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    // Method to add a car
    public static int addCar(Car car) {
        String query = "INSERT INTO Cars (make, model, year, price, stock, vin) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedID = -1;

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, car.getMake());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setDouble(4, car.getPrice());
            pstmt.setInt(5, car.getStock());
            pstmt.setString(6, car.getVin());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedID;
    }

    // Update stock for a car
    public void updateCarStock(String vin, int newStock) {
        String query = "UPDATE Cars SET stock = ? WHERE vin = ?";
        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newStock);
            pstmt.setString(2, vin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean updateCar(Car car) {
        String query = "UPDATE Cars SET make = ?, model = ?, year = ?, price = ?, stock = ?, vin = ? WHERE carID = ?";
        boolean isUpdated = false;

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, car.getMake());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setDouble(4, car.getPrice());
            pstmt.setInt(5, car.getStock());
            pstmt.setString(6, car.getVin());
            pstmt.setInt(7, car.getCarID());

            int affectedRows = pstmt.executeUpdate();
            isUpdated = affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUpdated;
    }
    
    public static int getMaxCarID() {
        String query = "SELECT MAX(carID) AS maxID FROM Cars";
        int maxID = 0;

        try (Connection conn = DBconnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                maxID = rs.getInt("maxID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maxID;
    }
    
    public static int getTotalCars() {
        String query = "SELECT COUNT(*) as count FROM Cars";
        try (Connection conn = DBconnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getTotalInventoryValue() {
        String query = "SELECT SUM(price * stock) as total FROM Cars";
        try (Connection conn = DBconnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public static boolean isVinExists(String vin) {
        String query = "SELECT COUNT(*) as count FROM Cars WHERE vin = ?";
        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (conn != null) {  // Check if connection was successful
                pstmt.setString(1, vin);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            } else {
                showErrorAlert("Database Error", "Could not connect to database.");
            }
        } catch (SQLException e) {
            System.out.println("Error checking VIN: " + e.getMessage());
            showErrorAlert("Database Error", 
                "Error checking VIN in database. Please try again.");
        }
        return false;
    }
    
    private static void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        styleAlert(alert, title, content, PRIMARY_RED);
        alert.showAndWait();
    }
    
    private static void styleAlert(Alert alert, String title, String content, String color) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: " + SURFACE_DARK + ";" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2px;"
        );
        
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Style the button
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );
        
        // Style the content
        dialogPane.lookup(".content.label").setStyle(
            "-fx-text-fill: white; -fx-font-size: 14px;"
        );
    }
}
