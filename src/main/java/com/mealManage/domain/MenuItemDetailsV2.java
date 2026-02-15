package com.mealManage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.school.SchoolGrades;

/**This POJO class used for menu items details**/
public class MenuItemDetailsV2 {
	
	private Long mealId;
	private String mealDesc;
	private Double mealPrice = 0.0;
	private Double reducedPrice = 0.0;
	private String mealName;
	@JsonIgnore
	private MealType mealType;
	private Boolean reducedPriceEligStatus = false;
	@JsonIgnore
	private SchoolGrades grade;
	/**
	 * @return the mealId
	 */
	public Long getMealId() {
		return mealId;
	}
	/**
	 * @param mealId the mealId to set
	 */
	public void setMealId(Long mealId) {
		this.mealId = mealId;
	}
	/**
	 * @return the mealDesc
	 */
	public String getMealDesc() {
		return mealDesc;
	}
	/**
	 * @param mealDesc the mealDesc to set
	 */
	public void setMealDesc(String mealDesc) {
		this.mealDesc = mealDesc;
	}
	/**
	 * @return the mealPrice
	 */
	public Double getMealPrice() {
		return mealPrice;
	}
	/**
	 * @param mealPrice the mealPrice to set
	 */
	public void setMealPrice(Double mealPrice) {
		this.mealPrice = mealPrice;
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
	 * @return the mealType
	 */
	public MealType getMealType() {
		return mealType;
	}
	/**
	 * @param mealType the mealType to set
	 */
	public void setMealType(MealType mealType) {
		this.mealType = mealType;
	}
	/**
	 * @return the reducedPriceEligStatus
	 */
	public Boolean getReducedPriceEligStatus() {
		return reducedPriceEligStatus;
	}
	/**
	 * @param reducedPriceEligStatus the reducedPriceEligStatus to set
	 */
	public void setReducedPriceEligStatus(Boolean reducedPriceEligStatus) {
		this.reducedPriceEligStatus = reducedPriceEligStatus;
	}
	/**
	 * @return the grade
	 */
	public SchoolGrades getGrade() {
		return grade;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(SchoolGrades grade) {
		this.grade = grade;
	}

}
