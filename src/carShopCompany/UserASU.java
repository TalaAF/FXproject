package carShopCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserASU {

    // Method to validate sign-in credentials
    public boolean validateUser(String username, String password) {
        String query = "SELECT * FROM user_account WHERE userName = ? AND password = ?";
        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            // If a result exists, the credentials are valid
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to sign up a new user (validate if the user is an employee)
    // Method to sign up a new user with enhanced validation
    public boolean signUpUser(User user, int employeeID, String firstName, String position) {
        // Validate if the employee exists and the firstName and position match
        String validateEmployeeQuery = "SELECT * FROM Employees WHERE employeeID = ? AND firstName = ? AND position = ?";
        String insertUserQuery = "INSERT INTO Users (userName, password) VALUES (?, ?)";

        try (Connection conn = DBconnection.connect();
             PreparedStatement validateStmt = conn.prepareStatement(validateEmployeeQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {

            // Validate that the employee exists with the correct firstName and position
            validateStmt.setInt(1, employeeID);
            validateStmt.setString(2, firstName);
            validateStmt.setString(3, position);
            ResultSet rs = validateStmt.executeQuery();

            if (!rs.next()) {
                return false; // Employee doesn't exist or the firstName/position is incorrect
            }

            // Insert new user into the Users table
            insertStmt.setString(1, user.getUserName());
            insertStmt.setString(2, user.getPassword());
            insertStmt.executeUpdate();

            return true; // Successfully signed up
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Sign-up failed
    }
}
