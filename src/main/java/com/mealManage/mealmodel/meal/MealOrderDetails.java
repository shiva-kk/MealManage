package com.mealManage.mealmodel.meal;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.mealmodel.school.CateringEntity;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.mealschedule.entities.MealCalendar;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "MealOrdersAudit_v2", uniqueConstraints={
	    @UniqueConstraint(columnNames = {"yearMonth", "studentUser_userId","menuType"})}, indexes = { 
	    @Index(columnList = "yearMonth"),
	    @Index(columnList="studentUser_userId"),
	    @Index(columnList="paymentStatus"),
	    @Index(columnList="menuType")})
public class MealOrderDetails extends CateringEntity{
	
	@ManyToOne
	@NotNull
	private StudentUser studentUser;
	@NotNull
	private String yearMonth;
	@NotNull
	private Double totalPrice = 0.0;
	@NotNull
	private int Items_count = 0;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
	        name = "mealOrdersAudit_schoolMeals", 
	        joinColumns = { @JoinColumn(name = "orderId") }, 
	        inverseJoinColumns = { @JoinColumn(name = "schoolMealId") }
	    )
	private Set<SchoolMeal> schoolMeals = new HashSet<>();
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
	        name = "mealOrdersAudit_calendarMenu", 
	        joinColumns = { @JoinColumn(name = "orderId") }, 
	        inverseJoinColumns = { @JoinColumn(name = "mealCalendarId") }
	    )
	private Set<MealCalendar> mealCalendars = new HashSet<>();
	
	private String menuOrderedPdfLink;
	private Boolean paymentStatus = false;
	private Date cutOffDateTime = new Date();
	@Transient
	@JsonIgnore
	/**crudOperationVal value would be 0 if it's create first time, in update case it'll be 1 and in order cancellation case 
	 * it'll be 2. It's default value is 0. It would be 3 if cancelled item restored**/
	private Integer crudOperationVal = 0;
	@Transient
	@JsonIgnore
	private String cancellationNote;
	@Transient
	@JsonIgnore
	private String cancellationDates;
	@NotNull
	private Double orderAmount = 0.0; //This field capture the payable amount of order
	@NotNull
	private Boolean isEligibleForFreeMeal = false;
	@Enumerated(EnumType.STRING)
	@NotNull
	private ItemTypeConstants menuType = ItemTypeConstants.Lunch;
	@NotNull
	private Boolean isEligibleForReducedPrice = false;
	private Boolean isEligForDiscount;
	private Long instantPaymentId; //having reference id of MasterTransactionsAudit
	@Transient
	@JsonIgnore
	private boolean isPaymentIdChanged;
	@JsonIgnore
	private Double itemDiscount;
	
	/**
	 * @return the studentUser
	 */
	public StudentUser getStudentUser() {
		return studentUser;
	}
	/**
	 * @param studentUser the studentUser to set
	 */
	public void setStudentUser(StudentUser studentUser) {
		this.studentUser = studentUser;
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
	 * @return the totalPrice
	 */
	public Double getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**
	 * @return the items_count
	 */
	public int getItems_count() {
		return Items_count;
	}
	/**
	 * @param items_count the items_count to set
	 */
	public void setItems_count(int items_count) {
		Items_count = items_count;
	}
	/**
	 * @return the schoolMeals
	 */
	public Set<SchoolMeal> getSchoolMeals() {
		return schoolMeals;
	}
	/**
	 * @param schoolMeals the schoolMeals to set
	 */
	public void setSchoolMeals(Set<SchoolMeal> schoolMeals) {
		this.schoolMeals = schoolMeals;
	}
	/**
	 * @return the menuOrderedPdfLink
	 */
	public String getMenuOrderedPdfLink() {
		return menuOrderedPdfLink;
	}
	/**
	 * @param menuOrderedPdfLink the menuOrderedPdfLink to set
	 */
	public void setMenuOrderedPdfLink(String menuOrderedPdfLink) {
		this.menuOrderedPdfLink = menuOrderedPdfLink;
	}
	/**
	 * @return the paymentStatus
	 */
	public Boolean getPaymentStatus() {
		return paymentStatus;
	}
	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
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
	 * @return the crudOperationVal
	 */
	public Integer getCrudOperationVal() {
		return crudOperationVal;
	}
	/**
	 * @param crudOperationVal the crudOperationVal to set
	 */
	public void setCrudOperationVal(Integer crudOperationVal) {
		this.crudOperationVal = crudOperationVal;
	}
	/**
	 * @return the cancellationNote
	 */
	public String getCancellationNote() {
		return cancellationNote;
	}
	/**
	 * @param cancellationNote the cancellationNote to set
	 */
	public void setCancellationNote(String cancellationNote) {
		this.cancellationNote = cancellationNote;
	}
	/**
	 * @return the cancellationDates
	 */
	public String getCancellationDates() {
		return cancellationDates;
	}
	/**
	 * @param cancellationDates the cancellationDates to set
	 */
	public void setCancellationDates(String cancellationDates) {
		this.cancellationDates = cancellationDates;
	}
	/**
	 * @return the orderAmount
	 */
	public Double getOrderAmount() {
		return orderAmount;
	}
	/**
	 * @param orderAmount the orderAmount to set
	 */
	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}
	/**
	 * @return the isEligibleForFreeMeal
	 */
	public Boolean getIsEligibleForFreeMeal() {
		return isEligibleForFreeMeal;
	}
	/**
	 * @param isEligibleForFreeMeal the isEligibleForFreeMeal to set
	 */
	public void setIsEligibleForFreeMeal(Boolean isEligibleForFreeMeal) {
		this.isEligibleForFreeMeal = isEligibleForFreeMeal;
	}
	
	/**
	 * @return the menuType
	 */
	public ItemTypeConstants getMenuType() {
		return menuType;
	}
	/**
	 * @param menuType the menuType to set
	 */
	public void setMenuType(ItemTypeConstants menuType) {
		this.menuType = menuType;
	}
	/**
	 * @return the isEligibleForReducedPrice
	 */
	public Boolean getIsEligibleForReducedPrice() {
		return isEligibleForReducedPrice;
	}
	/**
	 * @param isEligibleForReducedPrice the isEligibleForReducedPrice to set
	 */
	public void setIsEligibleForReducedPrice(Boolean isEligibleForReducedPrice) {
		this.isEligibleForReducedPrice = isEligibleForReducedPrice;
	}
	/**
	 * @return the mealCalendars
	 */
	public Set<MealCalendar> getMealCalendars() {
		return mealCalendars;
	}
	/**
	 * @param mealCalendars the mealCalendars to set
	 */
	public void setMealCalendars(Set<MealCalendar> mealCalendars) {
		this.mealCalendars = mealCalendars;
	}
	/**
	 * @return the instantPaymentId
	 */
	public Long getInstantPaymentId() {
		return instantPaymentId;
	}
	/**
	 * @param instantPaymentId the instantPaymentId to set
	 */
	public void setInstantPaymentId(Long instantPaymentId) {
		this.instantPaymentId = instantPaymentId;
	}
	/**
	 * @return the isPaymentIdChanged
	 */
	public boolean isPaymentIdChanged() {
		return isPaymentIdChanged;
	}
	/**
	 * @param isPaymentIdChanged the isPaymentIdChanged to set
	 */
	public void setPaymentIdChanged(boolean isPaymentIdChanged) {
		this.isPaymentIdChanged = isPaymentIdChanged;
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
	 * @return the itemDiscount
	 */
	public Double getItemDiscount() {
		return itemDiscount;
	}
	/**
	 * @param itemDiscount the itemDiscount to set
	 */
	public void setItemDiscount(Double itemDiscount) {
		this.itemDiscount = itemDiscount;
	}
	
}
