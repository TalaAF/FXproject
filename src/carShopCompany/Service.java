package carShopCompany;

import java.time.LocalDate;

public class Service {

	private int serviceID;
	private int carID;
	private int customerID;
	private LocalDate date;
	private String serviceDescription;
	private double cost;
	
	public Service(int serviceID, int carID, int customerID, LocalDate date, String serviceDescription, double cost) {
		this.serviceID=serviceID;
		this.carID=carID;
		this.date=date;
		this.cost=cost;
		this.customerID=customerID;
		this.serviceDescription=serviceDescription;
		
	}

	public int getServiceID() {
		return serviceID;
	}

	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}

	public int getCarID() {
		return carID;
	}

	public void setCarID(int carID) {
		this.carID = carID;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
}
