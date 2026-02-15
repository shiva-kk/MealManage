package com.mealManage.response;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderCostInfo {
	
	private String studentId;
	private String grade;
	private String firstName;
	private String lastName;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date date;
	private Double cost;
	
	public OrderCostInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public OrderCostInfo(Object[] obj) {
		super();
		this.studentId = obj[0]!=null?(String)obj[0]:null;
		this.grade = obj[1]!=null?(String)obj[1]:null;
		this.firstName = obj[2]!=null?(String)obj[2]:null;
		this.lastName = obj[3]!=null?(String)obj[3]:null;
		this.date = obj[4]!=null?(Timestamp)obj[4]:null;
		this.cost = obj[5]!=null?((Double)obj[5]).doubleValue():null;	
	}

	/**
	 * @return the studentId
	 */
	public String getStudentId() {
		return studentId;
	}
	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	/**
	 * @return the grade
	 */
	public String getGrade() {
		return grade;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the cost
	 */
	public Double getCost() {
		return cost;
	}
	/**
	 * @param cost the cost to set
	 */
	public void setCost(Double cost) {
		this.cost = cost;
	}
}
