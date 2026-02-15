package com.mealManage.domain;

import java.util.Date;

public class MealSummaryUpdateReq {
	
	private Long mealSummaryId;
	private Date cutOffDateTime;
	private Boolean orderDateExtensionStatus;
	private Date autoReminderDate1;
	private Date autoReminderDate2;
	private Boolean isPublished;
	private String loggedUser;
	private String schoolYear;
	private Boolean isSideSelect;
	private Integer allowOrderNDaysBefore; //default it should null when having any value then allow parent to add/update on those days before
	private String cutOffType = "M"; //cutOff category Monthly >> 'M', Weekly >> 'W' and Rolling >> 'R'
	private String weeklyOrderCutOffDay; //Last day when parent can add/update order for next week.
	private String weeklyOrderCutOffTime; //Last day when parent can add/update order for next week.
	private boolean extraEnableForCaterer = false;
	/**
	 * @return the mealSummaryId
	 */
	public Long getMealSummaryId() {
		return mealSummaryId;
	}
	/**
	 * @param mealSummaryId the mealSummaryId to set
	 */
	public void setMealSummaryId(Long mealSummaryId) {
		this.mealSummaryId = mealSummaryId;
	}
	/**
	 * @return the cutOffDateTime
	 */
	public Date getCutOffDateTime() {
		return cutOffDateTime;
	}
	/**
	 * @param cutOffDateTime the cutOffDateTime to set
	 */
	public void setCutOffDateTime(Date cutOffDateTime) {
		this.cutOffDateTime = cutOffDateTime;
	}
	/**
	 * @return the orderDateExtensionStatus
	 */
	public Boolean getOrderDateExtensionStatus() {
		return orderDateExtensionStatus;
	}
	/**
	 * @param orderDateExtensionStatus the orderDateExtensionStatus to set
	 */
	public void setOrderDateExtensionStatus(Boolean orderDateExtensionStatus) {
		this.orderDateExtensionStatus = orderDateExtensionStatus;
	}
	/**
	 * @return the autoReminderDate1
	 */
	public Date getAutoReminderDate1() {
		return autoReminderDate1;
	}
	/**
	 * @param autoReminderDate1 the autoReminderDate1 to set
	 */
	public void setAutoReminderDate1(Date autoReminderDate1) {
		this.autoReminderDate1 = autoReminderDate1;
	}
	/**
	 * @return the autoReminderDate2
	 */
	public Date getAutoReminderDate2() {
		return autoReminderDate2;
	}
	/**
	 * @param autoReminderDate2 the autoReminderDate2 to set
	 */
	public void setAutoReminderDate2(Date autoReminderDate2) {
		this.autoReminderDate2 = autoReminderDate2;
	}
	/**
	 * @return the isPublished
	 */
	public Boolean getIsPublished() {
		return isPublished;
	}
	/**
	 * @param isPublished the isPublished to set
	 */
	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}
	/**
	 * @return the loggedUser
	 */
	public String getLoggedUser() {
		return loggedUser;
	}
	/**
	 * @param loggedUser the loggedUser to set
	 */
	public void setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
	}
	/**
	 * @return the schoolYear
	 */
	public String getSchoolYear() {
		return schoolYear;
	}
	/**
	 * @param schoolYear the schoolYear to set
	 */
	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}
	/**
	 * @return the isSideSelect
	 */
	public Boolean getIsSideSelect() {
		return isSideSelect;
	}
	/**
	 * @param isSideSelect the isSideSelect to set
	 */
	public void setIsSideSelect(Boolean isSideSelect) {
		this.isSideSelect = isSideSelect;
	}
	/**
	 * @return the allowOrderNDaysBefore
	 */
	public Integer getAllowOrderNDaysBefore() {
		return allowOrderNDaysBefore;
	}
	/**
	 * @param allowOrderNDaysBefore the allowOrderNDaysBefore to set
	 */
	public void setAllowOrderNDaysBefore(Integer allowOrderNDaysBefore) {
		this.allowOrderNDaysBefore = allowOrderNDaysBefore;
	}
	
	/**
	 * @return the cutOffType
	 */
	public String getCutOffType() {
		return cutOffType;
	}
	/**
	 * @param cutOffType the cutOffType to set
	 */
	public void setCutOffType(String cutOffType) {
		this.cutOffType = cutOffType;
	}
	/**
	 * @return the weeklyOrderCutOffDay
	 */
	public String getWeeklyOrderCutOffDay() {
		return weeklyOrderCutOffDay;
	}
	/**
	 * @param weeklyOrderCutOffDay the weeklyOrderCutOffDay to set
	 */
	public void setWeeklyOrderCutOffDay(String weeklyOrderCutOffDay) {
		this.weeklyOrderCutOffDay = weeklyOrderCutOffDay;
	}
	
	/**
	 * @return the weeklyOrderCutOffTime
	 */
	public String getWeeklyOrderCutOffTime() {
		return weeklyOrderCutOffTime;
	}
	/**
	 * @param weeklyOrderCutOffTime the weeklyOrderCutOffTime to set
	 */
	public void setWeeklyOrderCutOffTime(String weeklyOrderCutOffTime) {
		this.weeklyOrderCutOffTime = weeklyOrderCutOffTime;
	}
	/**
	 * @return the extraEnableForCaterer
	 */
	public boolean isExtraEnableForCaterer() {
		return extraEnableForCaterer;
	}
	/**
	 * @param extraEnableForCaterer the extraEnableForCaterer to set
	 */
	public void setExtraEnableForCaterer(boolean extraEnableForCaterer) {
		this.extraEnableForCaterer = extraEnableForCaterer;
	}
	
}
