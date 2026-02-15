package com.mealManage.mealschedule.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;

/**
 * @author Thulasiram Yachamaneni
 */
@Entity
@Table(name = "meal_calendar_summary",indexes = { 
	    @Index(columnList = "mealSchool_schoolId"),
	    @Index(columnList="yearMonth"),
	    @Index(columnList="mealType")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MealCalendarSummary extends BaseEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @ElementCollection(targetClass = SchoolGrades.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "meal_summary_grades",joinColumns = @JoinColumn(name = "meal_calendar_summary_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "grades_name")
    private Set<SchoolGrades> grades=new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
    @JoinColumn(name = "meal_calendar_summary_id", nullable = false, updatable = false)

    /*@OneToMany(
            mappedBy = "mealCalendarSummary",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )*/
    @JsonIgnore
    private Set<MealCalendar> mealByDays;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="mealSchool_schoolId")
    private MealSchool school;
    private Date autoReminderDate1;
    private Date autoReminderDate2;
    private Boolean isPublished = false;
    private Boolean orderDateExtensionStatus = false;
    private String yearMonth;
    private Date startDate;
    private Date endDate;
    private Integer schoolYear;
    private String pdfLink;
    private Boolean reducedPriceStatus;
    @Enumerated(EnumType.STRING)
   	@Column(name="mealType", nullable = false)
    private ItemTypeConstants mealType;
	private Date cutOffDateTime;
	private Boolean isSideSelect;
	private Integer allowOrderNDaysBefore; //default it should null when having any value then allow parent to add/update on those days before
	/*@NotNull
	private boolean isWeeklyCutOff = false;*/
	@NotNull
	private String cutOffType = "M"; //cutOff category Monthly >> 'M', Weekly >> 'W' and Rolling >> 'R'
	private String weeklyOrderCutOffDay; //Last day when parent can add/update order for next week.
	private String weeklyOrderCutOffTime; //Last day when parent can add/update order for next week.
	private Boolean isExtraPreOrder = false;
	private Double itemPriceDisForMonthlyOrder;
	@NotNull
	private boolean extraEnableForCaterer = false;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the grades
	 */
	public Set<SchoolGrades> getGrades() {
		return grades;
	}
	/**
	 * @param grades the grades to set
	 */
	public void setGrades(Set<SchoolGrades> grades) {
		this.grades = grades;
	}

	/**
	 * @return the school
	 */
	public MealSchool getSchool() {
		return school;
	}
	/**
	 * @param school the school to set
	 */
	public void setSchool(MealSchool school) {
		this.school = school;
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
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
	 * @return the pdfLink
	 */
	public String getPdfLink() {
		return pdfLink;
	}
	/**
	 * @param pdfLink the pdfLink to set
	 */
	public void setPdfLink(String pdfLink) {
		this.pdfLink = pdfLink;
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
	 * @return the mealType
	 */
	public ItemTypeConstants getMealType() {
		return mealType;
	}
	/**
	 * @param mealType the mealType to set
	 */
	public void setMealType(ItemTypeConstants mealType) {
		this.mealType = mealType;
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
	 * @return the mealByDays
	 */
	public Set<MealCalendar> getMealByDays() {
		return mealByDays;
	}
	/**
	 * @param mealByDays the mealByDays to set
	 */
	public void setMealByDays(Set<MealCalendar> mealByDays) {
		this.mealByDays = mealByDays;
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
	
    /*public void addMealCalendar(MealCalendar mealCalendar) {
        mealByDays.add(mealCalendar);
        mealCalendar.setMealCalendarSummary(this);
    }

    public void removeMealCalendar(MealCalendar mealCalendar) {
        mealByDays.remove(mealCalendar);
        mealCalendar.setMealCalendarSummary(null);
    }*/
}


