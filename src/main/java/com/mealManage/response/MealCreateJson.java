package com.mealManage.response;

import java.util.List;

public class MealCreateJson {
	
	private MealItems mealItems;
	private List<MealJsonData> mealJsonDataList;
	private List<Integer> schoolDays;
	private String status;
	private String statusMessage;
	private Integer statusCode;
	private String errorMessage;
	/**
	 * @return the mealItems
	 */
	public MealItems getMealItems() {
		return mealItems;
	}
	/**
	 * @param mealItems the mealItems to set
	 */
	public void setMealItems(MealItems mealItems) {
		this.mealItems = mealItems;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the mealJsonDataList
	 */
	public List<MealJsonData> getMealJsonDataList() {
		return mealJsonDataList;
	}
	/**
	 * @param mealJsonDataList the mealJsonDataList to set
	 */
	public void setMealJsonDataList(List<MealJsonData> mealJsonDataList) {
		this.mealJsonDataList = mealJsonDataList;
	}
	/**
	 * @return the schoolDays
	 */
	public List<Integer> getSchoolDays() {
		return schoolDays;
	}
	/**
	 * @param schoolDays the schoolDays to set
	 */
	public void setSchoolDays(List<Integer> schoolDays) {
		this.schoolDays = schoolDays;
	}
	
}
