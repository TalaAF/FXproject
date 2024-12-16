package carShopCompany;

import java.time.LocalDate;

public class Order {

	private int odrerID;
	private LocalDate date;
	private int carID;
	private int employeeID;
	private int quantity;
	private int customerID;
	private double totalPrice;
	
	public Order(int odrerID, LocalDate date, int carID, int employeeID, int quantity, int customerID, double totalPrice) {
		this.odrerID=odrerID;
		this.date=date;
		this.carID=carID;
		this.customerID=customerID;
		this.employeeID=employeeID;
		this.quantity=quantity;
		this.totalPrice=totalPrice;
	}

	public int getOdrerID() {
		return odrerID;
	}

	public void setOdrerID(int odrerID) {
		this.odrerID = odrerID;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getCarID() {
		return carID;
	}

	public void setCarID(int carID) {
		this.carID = carID;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
}
