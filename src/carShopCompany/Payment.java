package carShopCompany;

import java.time.LocalDate;

public class Payment {

	private int paymentID;
	private int orderID;
	private LocalDate date;
	private String paymentMethod;
	private double amount;
	
	public Payment(int paymentID, int orderID, LocalDate date, String paymentMethod, double amount) {
		this.paymentID=paymentID;
		this.orderID=orderID;
		this.date=date;
		this.amount=amount;
		this.paymentMethod=paymentMethod;
	}

	public int getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(int paymentID) {
		this.paymentID = paymentID;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
}
