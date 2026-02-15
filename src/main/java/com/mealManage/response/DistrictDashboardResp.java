package com.mealManage.response;

import java.util.Map;

/**This POJO class used for map the district dashboard data**/
public class DistrictDashboardResp {
	
	private Long mealSchoolId;
	private String schoolName;
	private String subdomain;
	private String schoolYear;
	private Map<String, Integer> ledger;
	private Map<String, String> financial;
	private Map<String, Integer> orders;
	
	/**
	 * @return the mealSchoolId
	 */
	public Long getMealSchoolId() {
		return mealSchoolId;
	}
	/**
	 * @param mealSchoolId the mealSchoolId to set
	 */
	public void setMealSchoolId(Long mealSchoolId) {
		this.mealSchoolId = mealSchoolId;
	}
	/**
	 * @return the schoolName
	 */
	public String getSchoolName() {
		return schoolName;
	}
	/**
	 * @param schoolName the schoolName to set
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	/**
	 * @return the schoolYear
	 */
	public String getSchoolYear() {
		return schoolYear;
	}
	/**
	 * @param schoolYear the schoolYear to set
	 */
	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}
	/**
	 * @return the ledger
	 */
	public Map<String, Integer> getLedger() {
		return ledger;
	}
	/**
	 * @param ledger the ledger to set
	 */
	public void setLedger(Map<String, Integer> ledger) {
		this.ledger = ledger;
	}
	
	/**
	 * @return the financial
	 */
	public Map<String, String> getFinancial() {
		return financial;
	}
	/**
	 * @param financial the financial to set
	 */
	public void setFinancial(Map<String, String> financial) {
		this.financial = financial;
	}
	/**
	 * @return the orders
	 */
	public Map<String, Integer> getOrders() {
		return orders;
	}
	/**
	 * @param orders the orders to set
	 */
	public void setOrders(Map<String, Integer> orders) {
		this.orders = orders;
	}
	/**
	 * @return the subdomain
	 */
	public String getSubdomain() {
		return subdomain;
	}
	/**
	 * @param subdomain the subdomain to set
	 */
	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}
	
}
