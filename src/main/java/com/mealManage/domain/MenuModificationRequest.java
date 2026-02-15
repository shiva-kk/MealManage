package com.mealManage.domain;

import com.mealManage.mealmodel.meal.MealType;

public class MenuModificationRequest {

	private int operationCode; //0 means add, 1 means update, 2 means delete
	private String menuModificationDate;
	private Long mealCalendarId;
	private String itemName;
	private MealType mealType;
	private String desc;
	private double price;
	private double reducedPrice;
	private String deletionReason;
	
	
	/**
	 * @return the mealCalendarId
	 */
	public Long getMealCalendarId() {
		return mealCalendarId;
	}
	/**
	 * @param mealCalendarId the mealCalendarId to set
	 */
	public void setMealCalendarId(Long mealCalendarId) {
		this.mealCalendarId = mealCalendarId;
	}
	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}	
	/**
	 * @return the deletionReason
	 */
	public String getDeletionReason() {
		return deletionReason;
	}
	/**
	 * @param deletionReason the deletionReason to set
	 */
	public void setDeletionReason(String deletionReason) {
		this.deletionReason = deletionReason;
	}
	/**
	 * @return the operationCode
	 */
	public int getOperationCode() {
		return operationCode;
	}
	/**
	 * @param operationCode the operationCode to set
	 */
	public void setOperationCode(int operationCode) {
		this.operationCode = operationCode;
	}
	/**
	 * @return the reducedPrice
	 */
	public double getReducedPrice() {
		return reducedPrice;
	}
	/**
	 * @param reducedPrice the reducedPrice to set
	 */
	public void setReducedPrice(double reducedPrice) {
		this.reducedPrice = reducedPrice;
	}

	public String getMenuModificationDate() {
		return menuModificationDate;
	}

	public void setMenuModificationDate(String menuModificationDate) {
		this.menuModificationDate = menuModificationDate;
	}

	public MealType getMealType() {
		return mealType;
	}

	public void setMealType(MealType mealType) {
		this.mealType = mealType;
	}
}
