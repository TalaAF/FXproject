package carShopCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CustomerASU{

	
	 public ArrayList<Customer> getAllCustomers() {
	        ArrayList<Customer> customers = new ArrayList<>();
	        String query = "SELECT * FROM Customers";

	        try (Connection conn = DBconnection.connect();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            while (rs.next()) {
	            	Customer customer = new Customer(
	                	rs.getInt("customerID"),
	                	rs.getString("firstName"),
	                    rs.getString("lastName"),
	                    rs.getString("email"),
	                    rs.getString("phone"),
	                    rs.getString("address"),
	                    rs.getString("city"),
	                    rs.getString("state"),
	                    rs.getString("zipCode")
	                );
	            	customers.add(customer);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return customers;
	    }


	    // Add a new car to the Cars table
	    public void addCar(Employee employee) {
	        String query = "INSERT INTO Cars (make, model, year, price, stock, vin) VALUES (?, ?, ?, ?, ?, ?)";
	        try (Connection conn = DBconnection.connect();
	             PreparedStatement pstmt = conn.prepareStatement(query)) {
	        	pstmt.setInt(1, employee.getEmployeeID());
	        	pstmt.setString(2, employee.getFirstName());
	            pstmt.setString(3, employee.getLastName());
	            pstmt.setString(4, employee.getPosition());
	            pstmt.setDouble(5, employee.getSalary());
	            pstmt.setDate(6, java.sql.Date.valueOf(employee.getDate()));
	            
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // we will set later what this should do
	    public void updateEmployeeThing(String vin, int newStock) {
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
}
