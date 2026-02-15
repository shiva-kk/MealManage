package com.mealManage.domain;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.mealManage.mealmodel.meal.MealMenu;
import com.mealManage.mealmodel.school.SchoolGrades;

public class MealsRequest {
	
	private String loggedUser;
	@Enumerated(EnumType.STRING)
	private Set<SchoolGrades> gradesList;
	private List<MealMenu> mealMenus;
	private Long mealSchoolId;
	private String yearMonth;
	private Date cutOffDateTime;
	private Boolean reducedPriceStatus;
	private Boolean orderDateExtensionStatus = false;
	private Date autoReminderDate1;
	private Date autoReminderDate2;
	private Long schoolMealSummaryId;
	private Integer schoolYear;
	private Boolean isSideSelect;
	private Integer allowOrderNDaysBefore; //default it should null when having any value then allow parent to add/update on those days before
	private String cutOffType = "M"; //cutOff category Monthly >> 'M', Weekly >> 'W' and Rolling >> 'R'
	private String weeklyOrderCutOffDay; //Last day when parent can add/update order for next week.
	private String weeklyOrderCutOffTime; //Last day when parent can add/update order for next week.
	private Boolean isExtraPreOrder;
	private Double itemPriceDisForMonthlyOrder;
	private boolean extraEnableForCaterer = false;
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
	 * @return the gradesList
	 */
	public Set<SchoolGrades> getGradesList() {
		return gradesList;
	}
	/**
	 * @param gradesList the gradesList to set
	 */
	public void setGradesList(Set<SchoolGrades> gradesList) {
		this.gradesList = gradesList;
	}
	/**
	 * @return the mealMenus
	 */
	public List<MealMenu> getMealMenus() {
		return mealMenus;
	}
	/**
	 * @param mealMenus the mealMenus to set
	 */
	public void setMealMenus(List<MealMenu> mealMenus) {
		this.mealMenus = mealMenus;
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
	 * @return the yearMonth
	 */
	public String getYearMonth() {
		return yearMonth;
	}
	/**
	 * @param yearMonth the yearMonth to set
	 */
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
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
	 * @return the schoolMealSummaryId
	 */
	public Long getSchoolMealSummaryId() {
		return schoolMealSummaryId;
	}
	/**
	 * @param schoolMealSummaryId the schoolMealSummaryId to set
	 */
	public void setSchoolMealSummaryId(Long schoolMealSummaryId) {
		this.schoolMealSummaryId = schoolMealSummaryId;
	}
	/**
	 * @return the schoolYear
	 */
	public Integer getSchoolYear() {
		return schoolYear;
	}
	/**
	 * @param schoolYear the schoolYear to set
	 */
	public void setSchoolYear(Integer schoolYear) {
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
	 * @param allowOrderNDaysBefore the allowOrsderNDaysBefore to set
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
	 * @return the isExtraPreOrder
	 */
	public Boolean getIsExtraPreOrder() {
		return isExtraPreOrder;
	}
	/**
	 * @param isExtraPreOrder the isExtraPreOrder to set
	 */
	public void setIsExtraPreOrder(Boolean isExtraPreOrder) {
		this.isExtraPreOrder = isExtraPreOrder;
	}
	/**
	 * @return the itemPriceDisForMonthlyOrder
	 */
	public Double getItemPriceDisForMonthlyOrder() {
		return itemPriceDisForMonthlyOrder;
	}
	/**
	 * @param itemPriceDisForMonthlyOrder the itemPriceDisForMonthlyOrder to set
	 */
	public void setItemPriceDisForMonthlyOrder(Double itemPriceDisForMonthlyOrder) {
		this.itemPriceDisForMonthlyOrder = itemPriceDisForMonthlyOrder;
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
	
}
