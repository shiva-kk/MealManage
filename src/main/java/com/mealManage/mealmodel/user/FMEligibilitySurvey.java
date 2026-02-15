package com.mealManage.mealmodel.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "FMEligibilitySurvey", indexes = {
		@Index(columnList = "parentEmail")})
/**This entity used for store the parent user free meal eligibility survey data***/
public class FMEligibilitySurvey extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = 5684883929287186311L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "surveyId", updatable = false, nullable = false)
	private Long surveyId;
	@Column(nullable = false, unique = true)
	private String parentEmail;
	@Column(nullable = false)
	private int householdSize;
	@Column(nullable = false)
	private Double income;
	@Column(nullable = false)
	private String incomeType;
	private Boolean isFreeLunchEligible = false;
	private Boolean isReducedPriceEligible = false;
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
	        name = "FMEligibilitySurvey_MealSchool", 
	        joinColumns = { @JoinColumn(name = "surveyId") }, 
	        inverseJoinColumns = { @JoinColumn(name = "mealSchoolId") }
	    )
	private Set<MealSchool> mealSchools = new HashSet<>();
	/**This below parameter using for store the admin email ids temporary**/
	@Transient
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Set<String> adminEmails;
	/**
	 * @return the surveyId
	 */
	public Long getSurveyId() {
		return surveyId;
	}
	/**
	 * @param surveyId the surveyId to set
	 */
	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}
	/**
	 * @return the parentEmail
	 */
	public String getParentEmail() {
		return parentEmail;
	}
	/**
	 * @param parentEmail the parentEmail to set
	 */
	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}
	/**
	 * @return the householdSize
	 */
	public int getHouseholdSize() {
		return householdSize;
	}
	/**
	 * @param householdSize the householdSize to set
	 */
	public void setHouseholdSize(int householdSize) {
		this.householdSize = householdSize;
	}
	/**
	 * @return the income
	 */
	public Double getIncome() {
		return income;
	}
	/**
	 * @param income the income to set
	 */
	public void setIncome(Double income) {
		this.income = income;
	}
	/**
	 * @return the incomeType
	 */
	public String getIncomeType() {
		return incomeType;
	}
	/**
	 * @param incomeType the incomeType to set
	 */
	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}
	/**
	 * @return the mealSchools
	 */
	public Set<MealSchool> getMealSchools() {
		return mealSchools;
	}
	/**
	 * @param mealSchools the mealSchools to set
	 */
	public void setMealSchools(Set<MealSchool> mealSchools) {
		this.mealSchools = mealSchools;
	}
	/**
	 * @return the isFreeLunchEligible
	 */
	public Boolean getIsFreeLunchEligible() {
		return isFreeLunchEligible;
	}
	/**
	 * @param isFreeLunchEligible the isFreeLunchEligible to set
	 */
	public void setIsFreeLunchEligible(Boolean isFreeLunchEligible) {
		this.isFreeLunchEligible = isFreeLunchEligible;
	}
	/**
	 * @return the isReducedPriceEligible
	 */
	public Boolean getIsReducedPriceEligible() {
		return isReducedPriceEligible;
	}
	/**
	 * @param isReducedPriceEligible the isReducedPriceEligible to set
	 */
	public void setIsReducedPriceEligible(Boolean isReducedPriceEligible) {
		this.isReducedPriceEligible = isReducedPriceEligible;
	}
	/**
	 * @return the adminEmails
	 */
	public Set<String> getAdminEmails() {
		return adminEmails;
	}
	/**
	 * @param adminEmails the adminEmails to set
	 */
	public void setAdminEmails(Set<String> adminEmails) {
		this.adminEmails = adminEmails;
	}
	
}
