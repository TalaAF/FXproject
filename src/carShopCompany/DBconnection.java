package carShopCompany;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

	    private static final String URL = "jdbc:mysql://localhost:3306/carcompany";
	    private static final String USER = "root";
	    private static final String PASSWORD = "";

	    
	    public static Connection connect() {
	        try {
	            return DriverManager.getConnection(URL, USER, PASSWORD);
	        } catch (SQLException e) {
	            System.out.println("Database connection failed: " + e.getMessage());
	            return null;
	        }
	    }
}
