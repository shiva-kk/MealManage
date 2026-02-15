package com.mealManage.response;

import java.util.List;

public class MealItems {
	
	private List<MealJsonData> mealMenuItems;
	private List<MealJsonData> extra;
	private Boolean reducedPriceStatus;
	/**
	 * @return the mealMenuItems
	 */
	public List<MealJsonData> getMealMenuItems() {
		return mealMenuItems;
	}
	/**
	 * @param mealMenuItems the mealMenuItems to set
	 */
	public void setMealMenuItems(List<MealJsonData> mealMenuItems) {
		this.mealMenuItems = mealMenuItems;
	}
	/**
	 * @return the extra
	 */
	public List<MealJsonData> getExtra() {
		return extra;
	}
	/**
	 * @param extra the extra to set
	 */
	public void setExtra(List<MealJsonData> extra) {
		this.extra = extra;
	}
	/**
	 * @return the reducedPriceStatus
	 */
	public Boolean getReducedPriceStatus() {
		return reducedPriceStatus;
	}
	/**
	 * @param reducedPriceStatus the reducedPriceStatus to set
	 */
	public void setReducedPriceStatus(Boolean reducedPriceStatus) {
		this.reducedPriceStatus = reducedPriceStatus;
	}
	
}
