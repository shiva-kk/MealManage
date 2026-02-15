package com.mealManage.mealschedule.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mealManage.mealmodel.meal.MealType;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Thulasiram Yachamaneni
 */
public class MenuDetailDTO {

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date date;
    private String name;
    private String ingredients;
    private String shortDescription;
    private String allergens;
    private Double price;
	private Long id;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date start;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date end;
	private MealType type;
	private String desc ;
	private Double reducedPrice ;
	private Long mealCalendarId;
	private Boolean isNutrAvailable;
	private Long summaryId;

    
    public MenuDetailDTO() {
		// TODO Auto-generated constructor stub
	}
    
	public MenuDetailDTO(Object[] obj) {
		super();
		this.date = obj[0]!=null?(Timestamp)obj[0]:null;
		this.start= obj[0]!=null?(Timestamp)obj[0]:null;
		this.end= obj[0]!=null?(Timestamp)obj[0]:null;
		this.name = obj[1]!=null?(String)obj[1]:null;
		this.ingredients = obj[2]!=null?(String)obj[2]:null;
		this.shortDescription = obj[3]!=null?(String)obj[3]:null;
		this.desc = obj[4]!=null?(String)obj[4]:null;
		this.allergens = obj[5]!=null?(String)obj[5]:null;
		this.price = obj[6]!=null?(Double)obj[6]:null;
		this.type = obj[7]!=null?MealType.valueOf((String)obj[7]):null;
		this.id = obj[8]!=null?((BigInteger)obj[8]).longValue():null;
		this.mealCalendarId = obj[9]!=null?((BigInteger)obj[9]).longValue():null;
		this.reducedPrice = obj[10]!=null?(Double)obj[10]:null;
		this.isNutrAvailable = obj[11]!=null?(Boolean)obj[11]:null;
		this.summaryId = obj[12]!=null?((BigInteger)obj[12]).longValue():null;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the ingredients
	 */
	public String getIngredients() {
		return ingredients;
	}
	/**
	 * @param ingredients the ingredients to set
	 */
	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}
	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}
	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	/**
	 * @return the allergens
	 */
	public String getAllergens() {
		return allergens;
	}
	/**
	 * @param allergens the allergens to set
	 */
	public void setAllergens(String allergens) {
		this.allergens = allergens;
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


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public MealType getType() {
		return type;
	}

	public void setType(MealType type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Double getReducedPrice() {
		return reducedPrice;
	}

	public void setReducedPrice(Double reducedPrice) {
		this.reducedPrice = reducedPrice;
	}

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
	 * @return the isNutrAvailable
	 */
	public Boolean getIsNutrAvailable() {
		return isNutrAvailable;
	}

	/**
	 * @param isNutrAvailable the isNutrAvailable to set
	 */
	public void setIsNutrAvailable(Boolean isNutrAvailable) {
		this.isNutrAvailable = isNutrAvailable;
	}

	/**
	 * @return the summaryId
	 */
	public Long getSummaryId() {
		return summaryId;
	}

	/**
	 * @param summaryId the summaryId to set
	 */
	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}
	
}
