package carShopCompany;

import java.time.LocalDate;

public class Employee {

	private int employeeID;
	private String firstName;
	private String lastName;
	private String position;
	private double salary;
	private LocalDate hireDate;
	
	public Employee(int employeeID, String firstName, String lastName,
			String position, double salary, LocalDate date) {
		this.employeeID=employeeID;
		this.firstName=firstName;
		this.lastName=lastName;
		this.position=position;
		this.salary=salary;
		this.hireDate=date;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public LocalDate getDate() {
		return hireDate;
	}

	public void setDate(LocalDate date) {
		this.hireDate = date;
	}
	
	
	
}
