package com.mealManage.domain;

public class BreakfastModificationReq {
	
	private Long itemId;
	private String itemName;
	private String desc;
	private double price;
	private double reducedPrice;
	private String breakfastDate;
	private String deletionReason;
	private int operationCode; //0 means add, 1 means update, 2 means delete
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
	 * @return the breakfastDate
	 */
	public String getBreakfastDate() {
		return breakfastDate;
	}
	/**
	 * @param breakfastDate the breakfastDate to set
	 */
	public void setBreakfastDate(String breakfastDate) {
		this.breakfastDate = breakfastDate;
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
	
}
