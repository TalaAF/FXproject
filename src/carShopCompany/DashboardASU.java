package carShopCompany;

import java.sql.*;
import java.util.ArrayList;

public class DashboardASU {
    
    public static class ActivityRecord {
        private String type;
        private Timestamp date;
        private String description;

        public ActivityRecord(String type, Timestamp date, String description) {
            this.type = type;
            this.date = date;
            this.description = description;
        }

        public String getType() { return type; }
        public Timestamp getDate() { return date; }
        public String getDescription() { return description; }
    }

    public static ArrayList<ActivityRecord> getRecentActivities(int limit) {
        ArrayList<ActivityRecord> activities = new ArrayList<>();
        
        String query = "SELECT * FROM (" +
                      "SELECT 'Order' as type, o.OrderDate as date, " +
                      "CONCAT('New order: ', c.Make, ' ', c.Model) as description " +
                      "FROM Orders o " +
                      "JOIN Cars c ON o.CarID = c.CarID " +
                      "UNION ALL " +
                      "SELECT 'Service' as type, ServiceDate as date, " +
                      "CONCAT('Service: ', ServiceDescription) as description " +
                      "FROM Services" +
                      ") as combined_activities " +
                      "ORDER BY date DESC " +
                      "LIMIT ?";
        
        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                activities.add(new ActivityRecord(
                    rs.getString("type"),
                    rs.getTimestamp("date"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return activities;
    }

    public static int getTotalPendingServices() {
        String query = "SELECT COUNT(*) as count FROM Services WHERE ServiceDate >= CURRENT_DATE";
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

    public static double getTotalRevenue(int days) {
        String query = "SELECT SUM(TotalPrice) as total " +
                      "FROM Orders " +
                      "WHERE OrderDate >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)";
                      
        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, days);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}