package com.mealManage.mealmodel.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "StudentUser_v2", uniqueConstraints={
	    @UniqueConstraint(columnNames = {"studentId", "mealSchool_schoolId", "schoolYear"}),
	    @UniqueConstraint(columnNames = {"pin", "mealSchool_schoolId", "schoolYear"})}, indexes = { 
		    @Index(columnList = "gradeName"),
		    @Index(columnList="isRegister"),
		    @Index(columnList="parentuser_userId"),
		    @Index(columnList="mealSchool_schoolId"),
		    @Index(columnList="isActive"),
		    @Index(columnList="schoolYear")} )
/**This entity used for Student user data**/
public class StudentUser extends User implements Serializable{
	
	private static final long serialVersionUID = 3660587614579880469L;
	
	 @Enumerated(EnumType.STRING)
	 @NotNull
	 private SchoolGrades gradeName ;
	
	//private String gradeName;
	private boolean isRegister = true;
	
	@NotNull
	private String studentId;
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@NotNull
  	private ParentUser parentuser;
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@NotNull
	private MealSchool  mealSchool;
	private String allergies;
	private String teacherName;
	private Boolean isReducePriceEligible=false;
	private Boolean isFreeMealEligible=false;
	@NotNull
	private Integer schoolYear;
	private String barcode;
	@NotNull
	private Double accBalance = 0.0;
	@Transient
	@JsonIgnore
	private String entryCode;
	private String numberStreetApt;
	private String cityStateZip;
	private boolean hasMilkCard = false;
	private boolean isBeforeCare = false;
	private Boolean isEnrollBCAndACPkt;
	private String schoolStudentId;
	private String defaultNotifyEmail; //it would have value like Primary/Alternate/Both 
	private String decisionReason;
	private String category;
	private String pin;
	private Date reCertificateDate;  //used for eligibility re-certification date
	private String recertPending="N"; //If re-certification pending then it should be Y else N
	private String actualPrg;
	private String additionalNotes;
	
	/**
	 * @return the gradeName
	 */
	public SchoolGrades getGradeName() {
		return gradeName;
	}
	/**
	 * @param gradeName the gradeName to set
	 */
	public void setGradeName(SchoolGrades gradeName) {
		this.gradeName = gradeName;
	}
	/**
	 * @return the parentuser
	 */
	public ParentUser getParentuser() {
		return parentuser;
	}
	/**
	 * @param parentuser the parentuser to set
	 */
	public void setParentuser(ParentUser parentuser) {
		this.parentuser = parentuser;
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
	 * @return the isRegister
	 */
	public boolean isRegister() {
		return isRegister;
	}
	/**
	 * @param isRegister the isRegister to set
	 */
	public void setRegister(boolean isRegister) {
		this.isRegister = isRegister;
	}
	/**
	 * @return the studentId
	 */
	public String getStudentId() {
		return studentId;
	}
	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	/**
	 * @return the allergies
	 */
	public String getAllergies() {
		return allergies;
	}
	/**
	 * @param allergies the allergies to set
	 */
	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}
	/**
	 * @return the teacherName
	 */
	public String getTeacherName() {
		return teacherName;
	}
	/**
	 * @param teacherName the teacherName to set
	 */
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	/**
	 * @return the isReducePriceEligible
	 */
	public Boolean getIsReducePriceEligible() {
		return isReducePriceEligible;
	}
	/**
	 * @param isReducePriceEligible the isReducePriceEligible to set
	 */
	public void setIsReducePriceEligible(Boolean isReducePriceEligible) {
		this.isReducePriceEligible = isReducePriceEligible;
	}
	/**
	 * @return the isFreeMealEligible
	 */
	public Boolean getIsFreeMealEligible() {
		return isFreeMealEligible;
	}
	/**
	 * @param isFreeMealEligible the isFreeMealEligible to set
	 */
	public void setIsFreeMealEligible(Boolean isFreeMealEligible) {
		this.isFreeMealEligible = isFreeMealEligible;
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
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}
	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	/**
	 * @return the accBalance
	 */
	public Double getAccBalance() {
		return accBalance;
	}
	/**
	 * @param accBalance the accBalance to set
	 */
	public void setAccBalance(Double accBalance) {
		this.accBalance = accBalance;
	}
	/**
	 * @return the entryCode
	 */
	public String getEntryCode() {
		return entryCode;
	}
	/**
	 * @param entryCode the entryCode to set
	 */
	public void setEntryCode(String entryCode) {
		this.entryCode = entryCode;
	}
	/**
	 * @return the numberStreetApt
	 */
	public String getNumberStreetApt() {
		return numberStreetApt;
	}
	/**
	 * @param numberStreetApt the numberStreetApt to set
	 */
	public void setNumberStreetApt(String numberStreetApt) {
		this.numberStreetApt = numberStreetApt;
	}
	/**
	 * @return the cityStateZip
	 */
	public String getCityStateZip() {
		return cityStateZip;
	}
	/**
	 * @param cityStateZip the cityStateZip to set
	 */
	public void setCityStateZip(String cityStateZip) {
		this.cityStateZip = cityStateZip;
	}
	/**
	 * @return the hasMilkCard
	 */
	public boolean isHasMilkCard() {
		return hasMilkCard;
	}
	/**
	 * @param hasMilkCard the hasMilkCard to set
	 */
	public void setHasMilkCard(boolean hasMilkCard) {
		this.hasMilkCard = hasMilkCard;
	}
	/**
	 * @return the isBeforeCare
	 */
	public boolean isBeforeCare() {
		return isBeforeCare;
	}
	/**
	 * @param isBeforeCare the isBeforeCare to set
	 */
	public void setBeforeCare(boolean isBeforeCare) {
		this.isBeforeCare = isBeforeCare;
	}
	/**
	 * @return the isEnrollBCAndACPkt
	 */
	public Boolean getIsEnrollBCAndACPkt() {
		return isEnrollBCAndACPkt;
	}
	/**
	 * @param isEnrollBCAndACPkt the isEnrollBCAndACPkt to set
	 */
	public void setIsEnrollBCAndACPkt(Boolean isEnrollBCAndACPkt) {
		this.isEnrollBCAndACPkt = isEnrollBCAndACPkt;
	}
	/**
	 * @return the schoolStudentId
	 */
	public String getSchoolStudentId() {
		return schoolStudentId;
	}
	/**
	 * @param schoolStudentId the schoolStudentId to set
	 */
	public void setSchoolStudentId(String schoolStudentId) {
		this.schoolStudentId = schoolStudentId;
	}
	/**
	 * @return the defaultNotifyEmail
	 */
	public String getDefaultNotifyEmail() {
		return defaultNotifyEmail;
	}
	/**
	 * @param defaultNotifyEmail the defaultNotifyEmail to set
	 */
	public void setDefaultNotifyEmail(String defaultNotifyEmail) {
		this.defaultNotifyEmail = defaultNotifyEmail;
	}
	/**
	 * @return the decisionReason
	 */
	public String getDecisionReason() {
		return decisionReason;
	}
	/**
	 * @param decisionReason the decisionReason to set
	 */
	public void setDecisionReason(String decisionReason) {
		this.decisionReason = decisionReason;
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
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}
	/**
	 * @param pin the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}
	/**
	 * @return the reCertificateDate
	 */
	public Date getReCertificateDate() {
		return reCertificateDate;
	}
	/**
	 * @param reCertificateDate the reCertificateDate to set
	 */
	public void setReCertificateDate(Date reCertificateDate) {
		this.reCertificateDate = reCertificateDate;
	}
	/**
	 * @return the recertPending
	 */
	public String getRecertPending() {
		return recertPending;
	}
	/**
	 * @param recertPending the recertPending to set
	 */
	public void setRecertPending(String recertPending) {
		this.recertPending = recertPending;
	}
	/**
	 * @return the actualPrg
	 */
	public String getActualPrg() {
		return actualPrg;
	}
	/**
	 * @param actualPrg the actualPrg to set
	 */
	public void setActualPrg(String actualPrg) {
		this.actualPrg = actualPrg;
	}
	/**
	 * @return the additionalNotes
	 */
	public String getAdditionalNotes() {
		return additionalNotes;
	}
	/**
	 * @param additionalNotes the additionalNotes to set
	 */
	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}
	
}
