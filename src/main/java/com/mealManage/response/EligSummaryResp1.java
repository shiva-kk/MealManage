package com.mealManage.response;

import java.math.BigInteger;
import java.util.Date;

public class EligSummaryResp1 {
	
	private Long mealSchoolId;
	private String schoolName;
	private Integer schoolYear;
	private Boolean isFreeMeal;
	private Boolean isReducedMeal;
	private String decisionReason;
	private String category;
	private Boolean isActive;
	private Integer stdCount;
	private Date reCertificateDate;  //used for eligibility re-certification date
	private String recertPending="N"; //If re-certification pending then it should be Y else N
	private String actualPrg;
	
	public EligSummaryResp1() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param mealSchoolId
	 * @param schoolName
	 * @param schoolYear
	 * @param isFreeMeal
	 * @param isReducedMeal
	 * @param decisionReason
	 * @param category
	 * @param isActive
	 * @param stdCount
	 */
	public EligSummaryResp1(Object[] obj) {
		super();
		this.mealSchoolId = obj[0]!=null?((BigInteger)obj[0]).longValue():null;
		this.schoolName = obj[1]!=null?(String)obj[1]:null;
		this.schoolYear = obj[2]!=null?(Integer)obj[2]:null;
		this.isFreeMeal = obj[3]!=null?(Boolean)obj[3]:false;
		this.isReducedMeal = obj[4]!=null?(Boolean)obj[4]:false;
		this.decisionReason = obj[5]!=null?(String)obj[5]:"";
		this.category = obj[6]!=null?(String)obj[6]:"";
		this.isActive = obj[7]!=null?(Boolean)obj[7]:false;
		this.stdCount = obj[8]!=null?((BigInteger)obj[8]).intValue():null;
		this.reCertificateDate = obj[9]!=null?(Date)obj[9]:null;
		this.recertPending = obj[10]!=null?(String)obj[10]:"N";
		this.actualPrg = obj[11]!=null?(String)obj[11]:"";
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
	 * @return the schoolName
	 */
	public String getSchoolName() {
		return schoolName;
	}
	/**
	 * @param schoolName the schoolName to set
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
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
	 * @return the isFreeMeal
	 */
	public Boolean getIsFreeMeal() {
		return isFreeMeal;
	}
	/**
	 * @param isFreeMeal the isFreeMeal to set
	 */
	public void setIsFreeMeal(Boolean isFreeMeal) {
		this.isFreeMeal = isFreeMeal;
	}
	/**
	 * @return the isReducedMeal
	 */
	public Boolean getIsReducedMeal() {
		return isReducedMeal;
	}
	/**
	 * @param isReducedMeal the isReducedMeal to set
	 */
	public void setIsReducedMeal(Boolean isReducedMeal) {
		this.isReducedMeal = isReducedMeal;
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
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the stdCount
	 */
	public Integer getStdCount() {
		return stdCount;
	}
	/**
	 * @param stdCount the stdCount to set
	 */
	public void setStdCount(Integer stdCount) {
		this.stdCount = stdCount;
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
	
}
