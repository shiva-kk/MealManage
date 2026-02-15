package com.mealManage.response;

import java.util.List;

public class ItemPosLocation {
	
	private Long itemId;
	private String itemName;
	private List<Long> locationIds;
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
	 * @return the locationId
	 */
	public List<Long> getLocationIds() {
		return locationIds;
	}
	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationIds(List<Long> locationIds) {
		this.locationIds = locationIds;
	}
}
