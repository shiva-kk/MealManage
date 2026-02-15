package com.mealManage.response;

import java.util.Date;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;

@SuppressWarnings("deprecation")
@JsonSerialize(include = Inclusion.NON_NULL)
public class SchoolHoliday {
	
	private Long recId;
	private String holidayName;
	private String holidayDesc;
	private Date holidayDate;
	private String dateOfHoliday;
	private Long mealSchoolId;
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
	 * @return the holidayName
	 */
	public String getHolidayName() {
		return holidayName;
	}
	/**
	 * @param holidayName the holidayName to set
	 */
	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}
	/**
	 * @return the holidayDesc
	 */
	public String getHolidayDesc() {
		return holidayDesc;
	}
	/**
	 * @param holidayDesc the holidayDesc to set
	 */
	public void setHolidayDesc(String holidayDesc) {
		this.holidayDesc = holidayDesc;
	}
	/**
	 * @return the holidayDate
	 */
	public Date getHolidayDate() {
		return holidayDate;
	}
	/**
	 * @param holidayDate the holidayDate to set
	 */
	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
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
	 * @return the dateOfHoliday
	 */
	public String getDateOfHoliday() {
		return dateOfHoliday;
	}
	/**
	 * @param dateOfHoliday the dateOfHoliday to set
	 */
	public void setDateOfHoliday(String dateOfHoliday) {
		this.dateOfHoliday = dateOfHoliday;
	}
	
}
