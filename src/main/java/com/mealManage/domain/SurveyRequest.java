package com.mealManage.domain;

public class SurveyRequest {
	
	private String email;
	private Double income;
	private String incomeType;
	private int houseHoldSize;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the income
	 */
	public Double getIncome() {
		return income;
	}
	/**
	 * @param income the income to set
	 */
	public void setIncome(Double income) {
		this.income = income;
	}
	/**
	 * @return the incomeType
	 */
	public String getIncomeType() {
		return incomeType;
	}
	/**
	 * @param incomeType the incomeType to set
	 */
	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}
	/**
	 * @return the houseHoldSize
	 */
	public int getHouseHoldSize() {
		return houseHoldSize;
	}
	/**
	 * @param houseHoldSize the houseHoldSize to set
	 */
	public void setHouseHoldSize(int houseHoldSize) {
		this.houseHoldSize = houseHoldSize;
	}
	
}
