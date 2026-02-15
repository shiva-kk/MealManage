package com.mealManage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.mealmodel.meal.MealType;

public class OrderedMenuItemDetails {
	
	private Long menuId;
	private String itemName;
	private Double itemOriginalPrice = 0.0;
	private Double itemFinalPrice = 0.0;
	private Double reducedPrice;
	private String itemDesc;
	private MealType itemtype;
	@JsonIgnore
	private Boolean isReducePriceEligible=false;
	@JsonIgnore
	private Boolean isFreeMealEligible=false;
	@JsonIgnore
	private Boolean mealReducedPriceElig;
	/**
	 * @return the menuId
	 */
	public Long getMenuId() {
		return menuId;
	}
	/**
	 * @param menuId the menuId to set
	 */
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
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
	 * @return the itemOriginalPrice
	 */
	public Double getItemOriginalPrice() {
		return itemOriginalPrice;
	}
	/**
	 * @param itemOriginalPrice the itemOriginalPrice to set
	 */
	public void setItemOriginalPrice(Double itemOriginalPrice) {
		this.itemOriginalPrice = itemOriginalPrice;
	}
	/**
	 * @return the itemFinalPrice
	 */
	public Double getItemFinalPrice() {
		return itemFinalPrice;
	}
	/**
	 * @param itemFinalPrice the itemFinalPrice to set
	 */
	public void setItemFinalPrice(Double itemFinalPrice) {
		this.itemFinalPrice = itemFinalPrice;
	}
	
	/**
	 * @return the reducedPrice
	 */
	public Double getReducedPrice() {
		return reducedPrice;
	}
	/**
	 * @param reducedPrice the reducedPrice to set
	 */
	public void setReducedPrice(Double reducedPrice) {
		this.reducedPrice = reducedPrice;
	}
	/**
	 * @return the itemDesc
	 */
	public String getItemDesc() {
		return itemDesc;
	}
	/**
	 * @param itemDesc the itemDesc to set
	 */
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	/**
	 * @return the itemtype
	 */
	public MealType getItemtype() {
		return itemtype;
	}
	/**
	 * @param itemtype the itemtype to set
	 */
	public void setItemtype(MealType itemtype) {
		this.itemtype = itemtype;
	}
	/**
	 * @return the isReducePriceEligible
	 */
	public Boolean getIsReducePriceEligible() {
		return isReducePriceEligible;
	}
	/**
	 * @param isReducePriceEligible the isReducePriceEligible to set
	 */
	public void setIsReducePriceEligible(Boolean isReducePriceEligible) {
		this.isReducePriceEligible = isReducePriceEligible;
	}
	/**
	 * @return the isFreeMealEligible
	 */
	public Boolean getIsFreeMealEligible() {
		return isFreeMealEligible;
	}
	/**
	 * @param isFreeMealEligible the isFreeMealEligible to set
	 */
	public void setIsFreeMealEligible(Boolean isFreeMealEligible) {
		this.isFreeMealEligible = isFreeMealEligible;
	}
	/**
	 * @return the mealReducedPriceElig
	 */
	public Boolean getMealReducedPriceElig() {
		return mealReducedPriceElig;
	}
	/**
	 * @param mealReducedPriceElig the mealReducedPriceElig to set
	 */
	public void setMealReducedPriceElig(Boolean mealReducedPriceElig) {
		this.mealReducedPriceElig = mealReducedPriceElig;
	}
	
}
