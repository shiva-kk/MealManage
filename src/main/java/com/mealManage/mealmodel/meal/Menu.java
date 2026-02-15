package com.mealManage.mealmodel.meal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;

@MappedSuperclass
public abstract class Menu extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MenuIdGenerator")
	@TableGenerator(table = "MENU_SEQUENCES", name = "MenuIdGenerator")
	private Long mealId;
	
	@Column(name="mealName")
	@NotNull
	private String title;
	
	@Column(name="mealPrice", nullable = false)
	private Double price = 0.0;
	@Column(name="mealLongDesc")
	private String desc;
	@Column(name="mealShortDesc")
	private String mealShortDesc;
	private Double reducedPrice;
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
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
	 * @return the mealShortDesc
	 */
	public String getMealShortDesc() {
		return mealShortDesc;
	}
	/**
	 * @param mealShortDesc the mealShortDesc to set
	 */
	public void setMealShortDesc(String mealShortDesc) {
		this.mealShortDesc = mealShortDesc;
	}
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
	
}
