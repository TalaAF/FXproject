package carShopCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderASU {

    // Static method to retrieve all orders
    public static ArrayList<Order> getAllOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders";

        try (Connection conn = DBconnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("orderID"),
                    rs.getDate("date").toLocalDate(),
                    rs.getInt("carID"),
                    rs.getInt("employeeID"),
                    rs.getInt("quantity"),
                    rs.getInt("customerID"),
                    rs.getDouble("totalPrice")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Method to add a new order
    public static int addOrder(Order order) {
        String query = "INSERT INTO Orders (date, carID, employeeID, quantity, customerID, totalPrice) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedID = -1;

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, java.sql.Date.valueOf(order.getDate())); // Convert LocalDate to SQL Date
            pstmt.setInt(2, order.getCarID());
            pstmt.setInt(3, order.getEmployeeID());
            pstmt.setInt(4, order.getQuantity());
            pstmt.setInt(5, order.getCustomerID());
            pstmt.setDouble(6, order.getTotalPrice());
            pstmt.executeUpdate();

            // Retrieve the generated orderID
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

    // Method to update the quantity and total price of an order
    public void updateOrder(int orderID, Integer newQuantity, Double newTotalPrice, Integer newCarID) {
        StringBuilder query = new StringBuilder("UPDATE Orders SET ");
        boolean first = true;

        if (newQuantity != null) {
            query.append("quantity = ?");
            first = false;
        }
        if (newTotalPrice != null) {
            query.append(first ? "" : ", ").append("totalPrice = ?");
            first = false;
        }
        if (newCarID != null) {
            query.append(first ? "" : ", ").append("carID = ?");
        }
        query.append(" WHERE orderID = ?");

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            int index = 1;
            if (newQuantity != null) pstmt.setInt(index++, newQuantity);
            if (newTotalPrice != null) pstmt.setDouble(index++, newTotalPrice);
            if (newCarID != null) pstmt.setInt(index++, newCarID);
            pstmt.setInt(index, orderID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getRecentOrders(int days) {
        String query = "SELECT COUNT(*) as count FROM Orders " +
                      "WHERE OrderDate >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)";
        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, days);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Map<String, Double> getMonthlySales(int months) {
        Map<String, Double> salesData = new LinkedHashMap<>();
        String query = "SELECT \r\n"
        		+ "DATE_FORMAT(OrderDate, '%b') as month,\r\n"
        		+ "SUM(TotalPrice) as total\r\n"
        		+ "FROM Orders\r\n"
        		+ "WHERE OrderDate >= DATE_SUB(CURRENT_DATE, INTERVAL ? MONTH)\r\n"
        		+ "GROUP BY MONTH(OrderDate)\r\n"
        		+ "ORDER BY OrderDate"
            ;
        
        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, months);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                salesData.put(rs.getString("month"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesData;
    }
}
