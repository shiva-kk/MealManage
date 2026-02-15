package com.mealManage.mealmodel.meal;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import com.mealManage.mealmodel.school.CateringEntity;
import com.mealManage.mealmodel.school.MealSchool;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "SchoolMealsSummary_v2", uniqueConstraints={@UniqueConstraint(columnNames =
		{"mealSchool_schoolId", "gradeNames", "yearMonth"})}, 
indexes = { @Index(columnList = "mealSchool_schoolId"), @Index(columnList="yearMonth")})
public class SchoolMealSummary extends CateringEntity implements Serializable{

	private static final long serialVersionUID = 2897683151472207824L;
	@NotNull
	private String yearMonth;
	@NotNull
	private Date cutOffDateTime;
	@NotNull
	private String mealsPdfLink;
	@ManyToOne
	@NotNull
	private MealSchool mealSchool;
	@NotNull
	private String gradeNames;
	private Boolean reducedPriceStatus;
	private Boolean orderDateExtensionStatus = false;
	private Date autoReminderDate1;
	@Transient
	private ItemTypeConstants menuType;
	private Date autoReminderDate2;
	private Boolean isPublished = false; //If it's true then only parent can order meal and admin can send reminder to parent
	@Transient
	private Boolean isExtraPreOrder;
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
	 * @return the mealsPdfLink
	 */
	public String getMealsPdfLink() {
		return mealsPdfLink;
	}
	/**
	 * @param mealsPdfLink the mealsPdfLink to set
	 */
	public void setMealsPdfLink(String mealsPdfLink) {
		this.mealsPdfLink = mealsPdfLink;
	}
	/**
	 * @return the mealSchool
	 */
	public MealSchool getMealSchool() {
		return mealSchool;
	}
	/**
	 * @param mealSchool the mealSchool to set
	 */
	public void setMealSchool(MealSchool mealSchool) {
		this.mealSchool = mealSchool;
	}
	/**
	 * @return the gradeNames
	 */
	public String getGradeNames() {
		return gradeNames;
	}
	/**
	 * @param gradeNames the gradeNames to set
	 */
	public void setGradeNames(String gradeNames) {
		this.gradeNames = gradeNames;
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
	
}
