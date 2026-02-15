package com.mealManage.response;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CaterersResp {
	
	@JsonIgnore
	private Long mealSchoolId;
	@JsonIgnore
	private Date date;
	@JsonIgnore
	private String category;
	private String itemName;
	private Long count;
	@JsonIgnore
	private Long stdRecId;
	@JsonIgnore
	private String mealDate;
	@JsonIgnore
	private String grade;
	public CaterersResp() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param mealSchoolId
	 * @param date
	 * @param category
	 * @param itemName
	 * @param count
	 */
	public CaterersResp(Object[] obj) {
		super();
		this.itemName = obj[0]!=null?(String)obj[0]:null;
		this.count = obj[2]!=null?((BigInteger)obj[2]).longValue():null;
		this.category = obj[3]!=null?(String)obj[3]:null;
		this.mealSchoolId = obj[4]!=null?((BigInteger)obj[4]).longValue():null;
		this.date = obj[1]!=null? (Timestamp)obj[1]:null;
	}

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
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
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
	 * @return the count
	 */
	public Long getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Long count) {
		this.count = count;
	}

	/**
	 * @return the stdRecId
	 */
	public Long getStdRecId() {
		return stdRecId;
	}

	/**
	 * @param stdRecId the stdRecId to set
	 */
	public void setStdRecId(Long stdRecId) {
		this.stdRecId = stdRecId;
	}

	/**
	 * @return the mealDate
	 */
	public String getMealDate() {
		return mealDate;
	}

	/**
	 * @param mealDate the mealDate to set
	 */
	public void setMealDate(String mealDate) {
		this.mealDate = mealDate;
	}

	/**
	 * @return the grade
	 */
	public String getGrade() {
		return grade;
	}

	/**
	 * @param grade the grade to set
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
}
