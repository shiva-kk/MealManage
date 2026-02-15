package com.mealManage.mealmodel.meal;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
//@DiscriminatorColumn(name = "TYPE")
@Table(name = "BreakfastItems", indexes = { 
	    @Index(columnList = "itemType"),
	    @Index(columnList="itemName"),
	    @Index(columnList="breakfastDate")})
public class BreakfastItems implements Serializable{
	
	private static final long serialVersionUID = -6341040785067802404L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	private String mealImage;
	private Date breakfastDate;
	@JsonFormat(pattern="yyyy-MM-dd")
	@Transient
	private Date breakfastDateVal;
	@Enumerated(EnumType.STRING)
	@Column(name = "itemType", nullable = false)
	private MealType itemType;
	@NotNull
	private String itemName;
	@Column(name="price", nullable = false)
	private Double price = 0.0;
	//@Column(name="reducedPrice", nullable = false)
	private Double reducedPrice = 0.0;
	@Column(name="itemDesc")
	private String itemDesc;
	
	/**
	 * @return the recId
	 */
	public Long getRecId() {
		return recId;
	}
	/**
	 * @param recId the recId to set
	 */
	public void setRecId(Long recId) {
		this.recId = recId;
	}
	/**
	 * @return the mealImage
	 */
	public String getMealImage() {
		return mealImage;
	}
	/**
	 * @param mealImage the mealImage to set
	 */
	public void setMealImage(String mealImage) {
		this.mealImage = mealImage;
	}
	/**
	 * @return the breakfastDate
	 */
	public Date getBreakfastDate() {
		this.breakfastDateVal = breakfastDate;
		return breakfastDate;
	}
	/**
	 * @param breakfastDate the breakfastDate to set
	 */
	public void setBreakfastDate(Date breakfastDate) {
		this.breakfastDate = breakfastDate;
	}
	/**
	 * @return the itemType
	 */
	public MealType getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(MealType itemType) {
		this.itemType = itemType;
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
