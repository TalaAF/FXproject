package carShopCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeeASU {

    // Static method to retrieve all employees
    public static ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employees";

        try (Connection conn = DBconnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Employee employee = new Employee(
                    rs.getInt("employeeID"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("position"),
                    rs.getDouble("salary"),
                    rs.getDate("hireDate").toLocalDate()
                );
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    // Method to add a new employee
    public int addEmployee(Employee employee) {
        String query = "INSERT INTO Employees (firstName, lastName, position, salary, hireDate) VALUES (?, ?, ?, ?, ?)";
        int generatedID = -1;

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getPosition());
            pstmt.setDouble(4, employee.getSalary());
            pstmt.setDate(5, java.sql.Date.valueOf(employee.getDate())); // Convert LocalDate to SQL Date
            pstmt.executeUpdate();

            // Retrieve the generated employeeID
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

    // Method to update an employee's position and salary
    public void updateEmployee(int employeeID, String newPosition, double newSalary) {
        String query = "UPDATE Employees SET position = ?, salary = ? WHERE employeeID = ?";

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPosition);
            pstmt.setDouble(2, newSalary);
            pstmt.setInt(3, employeeID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
