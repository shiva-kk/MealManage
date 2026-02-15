package com.mealManage.mealschedule.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mealManage.mealmodel.school.SchoolGrades;

/**
 * @author Thulasiram Yachamaneni
 */
public class MenuSummaryDetailDTO {

	private Long id;
	private Set<SchoolGrades> grades;
	private Date autoReminderDate1;
	private Date autoReminderDate2;
	private Boolean isPublished = false;
	private Boolean orderDateExtensionStatus = false;
	private String yearMonth;
	private Date cutOffDatetime;
	private String pdfLink;
	private List<Long> mealCalendarIds;
    private List<MenuDetailDTO> menuItemsList;
    private Boolean isStdFreeMealElig;
    private Boolean isStdReducedPriceElig;
    private Boolean isStdBeforeCare;
    private Boolean isSideSelect;
	private Integer allowOrderNDaysBefore; //default it should null when having any value then allow parent to add/update on those days before
	private String cutOffType = "M"; //cutOff category Monthly >> 'M', Weekly >> 'W' and Rolling >> 'R'
	private String weeklyOrderCutOffDay; //Last day when parent can add/update order for next week.
	private String weeklyOrderCutOffTime; //Last day when parent can add/update order for next week.
	private Boolean isExtraPreOrder;
	private Double itemPriceDisForMonthlyOrder;
	private Boolean isEligForDiscount;
	private List<MenuDetailDTO> holidayList;

    public MenuSummaryDetailDTO() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<SchoolGrades> getGrades() {
		return grades;
	}

	public void setGrades(Set<SchoolGrades> grades) {
		this.grades = grades;
	}

	public Date getAutoReminderDate1() {
		return autoReminderDate1;
	}

	public void setAutoReminderDate1(Date autoReminderDate1) {
		this.autoReminderDate1 = autoReminderDate1;
	}

	public Date getAutoReminderDate2() {
		return autoReminderDate2;
	}

	public void setAutoReminderDate2(Date autoReminderDate2) {
		this.autoReminderDate2 = autoReminderDate2;
	}

	public Boolean getPublished() {
		return isPublished;
	}

	public void setPublished(Boolean published) {
		isPublished = published;
	}

	public Boolean getOrderDateExtensionStatus() {
		return orderDateExtensionStatus;
	}

	public void setOrderDateExtensionStatus(Boolean orderDateExtensionStatus) {
		this.orderDateExtensionStatus = orderDateExtensionStatus;
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public String getPdfLink() {
		return pdfLink;
	}

	public void setPdfLink(String pdfLink) {
		this.pdfLink = pdfLink;
	}

	public List<MenuDetailDTO> getMenuItemsList() {
		return menuItemsList;
	}

	public void setMenuItemsList(List<MenuDetailDTO> menuItemsList) {
		this.menuItemsList = menuItemsList;
	}

	/**
	 * @return the cutOffDatetime
	 */
	public Date getCutOffDatetime() {
		return cutOffDatetime;
	}

	/**
	 * @param cutOffDatetime the cutOffDatetime to set
	 */
	public void setCutOffDatetime(Date cutOffDatetime) {
		this.cutOffDatetime = cutOffDatetime;
	}

	/**
	 * @return the mealCalendarIds
	 */
	public List<Long> getMealCalendarIds() {
		return mealCalendarIds;
	}

	/**
	 * @param mealCalendarIds the mealCalendarIds to set
	 */
	public void setMealCalendarIds(List<Long> mealCalendarIds) {
		this.mealCalendarIds = mealCalendarIds;
	}

	/**
	 * @return the isStdFreeMealElig
	 */
	public Boolean getIsStdFreeMealElig() {
		return isStdFreeMealElig;
	}

	/**
	 * @param isStdFreeMealElig the isStdFreeMealElig to set
	 */
	public void setIsStdFreeMealElig(Boolean isStdFreeMealElig) {
		this.isStdFreeMealElig = isStdFreeMealElig;
	}

	/**
	 * @return the isStdReducedPriceElig
	 */
	public Boolean getIsStdReducedPriceElig() {
		return isStdReducedPriceElig;
	}

	/**
	 * @param isStdReducedPriceElig the isStdReducedPriceElig to set
	 */
	public void setIsStdReducedPriceElig(Boolean isStdReducedPriceElig) {
		this.isStdReducedPriceElig = isStdReducedPriceElig;
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
	 * @return the isStdBeforeCare
	 */
	public Boolean getIsStdBeforeCare() {
		return isStdBeforeCare;
	}

	/**
	 * @param isStdBeforeCare the isStdBeforeCare to set
	 */
	public void setIsStdBeforeCare(Boolean isStdBeforeCare) {
		this.isStdBeforeCare = isStdBeforeCare;
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
	 * @return the isEligForDiscount
	 */
	public Boolean getIsEligForDiscount() {
		return isEligForDiscount;
	}

	/**
	 * @param isEligForDiscount the isEligForDiscount to set
	 */
	public void setIsEligForDiscount(Boolean isEligForDiscount) {
		this.isEligForDiscount = isEligForDiscount;
	}

	/**
	 * @return the holidayList
	 */
	public List<MenuDetailDTO> getHolidayList() {
		return holidayList;
	}

	/**
	 * @param holidayList the holidayList to set
	 */
	public void setHolidayList(List<MenuDetailDTO> holidayList) {
		this.holidayList = holidayList;
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
