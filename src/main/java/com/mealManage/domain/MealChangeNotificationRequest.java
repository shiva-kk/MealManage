package com.mealManage.domain;

public class MealChangeNotificationRequest {
	
	private Long mealSchoolId;
	private Long itemId;
	private String yearMonth;
	private String newItemName;
	private String customMessage;
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
	 * @return the itemId
	 */
	public Long getItemId() {
		return itemId;
	}
	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	/**
	 * @return the newItemName
	 */
	public String getNewItemName() {
		return newItemName;
	}
	/**
	 * @param newItemName the newItemName to set
	 */
	public void setNewItemName(String newItemName) {
		this.newItemName = newItemName;
	}
	/**
	 * @return the customMessage
	 */
	public String getCustomMessage() {
		return customMessage;
	}
	/**
	 * @param customMessage the customMessage to set
	 */
	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}
	/**
	 * @return the yearMonth
	 */
	public String getYearMonth() {
		return yearMonth;
	}
	/**
	 * @param yearMonth the yearMonth to set
	 */
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
	
}
