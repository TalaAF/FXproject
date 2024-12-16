package carShopCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServiceASU {

    // Static method to retrieve all services
    public static ArrayList<Service> getAllServices() {
        ArrayList<Service> services = new ArrayList<>();
        String query = "SELECT * FROM Services";

        try (Connection conn = DBconnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Service service = new Service(
                    rs.getInt("serviceID"),
                    rs.getInt("carID"),
                    rs.getInt("customerID"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("serviceDescription"),
                    rs.getDouble("cost")
                );
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    // Method to add a new service
    public int addService(Service service) {
        String query = "INSERT INTO Services (carID, customerID, date, serviceDescription, cost) VALUES (?, ?, ?, ?, ?)";
        int generatedID = -1;

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, service.getCarID());
            pstmt.setInt(2, service.getCustomerID());
            pstmt.setDate(3, java.sql.Date.valueOf(service.getDate())); // Convert LocalDate to SQL Date
            pstmt.setString(4, service.getServiceDescription());
            pstmt.setDouble(5, service.getCost());
            pstmt.executeUpdate();

            // Retrieve the generated serviceID
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

    // Method to update service details
    public void updateService(int serviceID, String newDescription, double newCost) {
        String query = "UPDATE Services SET serviceDescription = ?, cost = ? WHERE serviceID = ?";

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newDescription);
            pstmt.setDouble(2, newCost);
            pstmt.setInt(3, serviceID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static int getPendingServices() {
        String query = "SELECT COUNT(*) as count FROM Services " +
                      "WHERE ServiceDate >= CURRENT_DATE";
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
    
}
