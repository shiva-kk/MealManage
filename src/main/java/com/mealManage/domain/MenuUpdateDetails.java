package com.mealManage.domain;

public class MenuUpdateDetails {
	
	private Long schoolMealId;
	private String mealName;
	private String mealLongDesc;
	/**
	 * @return the schoolMealId
	 */
	public Long getSchoolMealId() {
		return schoolMealId;
	}
	/**
	 * @param schoolMealId the schoolMealId to set
	 */
	public void setSchoolMealId(Long schoolMealId) {
		this.schoolMealId = schoolMealId;
	}
	/**
	 * @return the mealName
	 */
	public String getMealName() {
		return mealName;
	}
	/**
	 * @param mealName the mealName to set
	 */
	public void setMealName(String mealName) {
		this.mealName = mealName;
	}
	/**
	 * @return the mealLongDesc
	 */
	public String getMealLongDesc() {
		return mealLongDesc;
	}
	/**
	 * @param mealLongDesc the mealLongDesc to set
	 */
	public void setMealLongDesc(String mealLongDesc) {
		this.mealLongDesc = mealLongDesc;
	}

}
