package com.mealManage.response;

public class Enrollments {
	
	private String name;
	private String type;
	private String enrolledOn;
	private String startDate;
	private String endDate;
	private String payment;
	private String paymentType;
	private Double amount;
	private Long trxRecId;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the enrolledOn
	 */
	public String getEnrolledOn() {
		return enrolledOn;
	}
	/**
	 * @param enrolledOn the enrolledOn to set
	 */
	public void setEnrolledOn(String enrolledOn) {
		this.enrolledOn = enrolledOn;
	}
	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the payment
	 */
	public String getPayment() {
		return payment;
	}
	/**
	 * @param payment the payment to set
	 */
	public void setPayment(String payment) {
		this.payment = payment;
	}
	/**
	 * @return the paymentType
	 */
	public String getPaymentType() {
		return paymentType;
	}
	/**
	 * @param paymentType the paymentType to set
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * @return the trxRecId
	 */
	public Long getTrxRecId() {
		return trxRecId;
	}
	/**
	 * @param trxRecId the trxRecId to set
	 */
	public void setTrxRecId(Long trxRecId) {
		this.trxRecId = trxRecId;
	}
	
}
