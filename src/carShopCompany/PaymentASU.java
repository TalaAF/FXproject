package carShopCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class PaymentASU {

    // Static method to get all payments
    public static ArrayList<Payment> getAllPayments() {
        ArrayList<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM Payments";

        try (Connection conn = DBconnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Payment payment = new Payment(
                    rs.getInt("paymentID"),
                    rs.getInt("orderID"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("paymentMethod"),
                    rs.getDouble("amount")
                );
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // Method to add a payment
    public static int addPayment(Payment payment) {
        String query = "INSERT INTO Payments (orderID, date, paymentMethod, amount) VALUES (?, ?, ?, ?)";
        int generatedID = -1;

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, payment.getOrderID());
            pstmt.setDate(2, java.sql.Date.valueOf(payment.getDate())); // Convert LocalDate to SQL Date
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setDouble(4, payment.getAmount());
            pstmt.executeUpdate();

            // Retrieve the generated paymentID
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

    // Method to update a payment's details
    public void updatePayment(int paymentID, String newPaymentMethod, double newAmount) {
        String query = "UPDATE Payments SET paymentMethod = ?, amount = ? WHERE paymentID = ?";

        try (Connection conn = DBconnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPaymentMethod);
            pstmt.setDouble(2, newAmount);
            pstmt.setInt(3, paymentID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
