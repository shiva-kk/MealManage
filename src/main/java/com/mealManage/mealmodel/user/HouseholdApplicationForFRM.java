package com.mealManage.mealmodel.user;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.domain.HouseholdAppDeclinedReason;
import com.mealManage.domain.HouseholdAppOtherInfo;
import com.mealManage.domain.HouseholdIncompleteApp;
import com.mealManage.mealmodel.school.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "FRM_HouseholdApplication", indexes = {
		@Index(columnList = "status"), @Index(columnList = "mealSchoolId"), 
		@Index(columnList = "date"), @Index(columnList = "schoolYear"),
		@Index(columnList = "prmyParentEmail")})
/**This entity used for capture the household size and other info for free/reduced price program**/
public class HouseholdApplicationForFRM extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 120640370652653795L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "applicationId", updatable = false, nullable = false)
	private Long applicationId;
	private String date;
	private String status; //approved/denied/in-complete/cancelled
	private String prmyParentEmail;
	private Date incompleteDueDate;
	private Long mealSchoolId;
	private Date statusUpdateDate;
	@Column(length = 65535, columnDefinition = "text")
	@JsonIgnore
	private String otherInfo;
	private Integer schoolYear;
	private String studentRecIds;
	@Column(length = 1500, columnDefinition = "text")
	@JsonIgnore
	private String declinedReason;
	@Column(length = 5000, columnDefinition = "text")
	@JsonIgnore
	private String incompleteReason;
	@Transient
	private List<HouseholdAppOtherInfo> householdAppInfo;
	@Transient
	private List<HouseholdAppDeclinedReason> declinedReasonList;

	@Transient
	private List<HouseholdIncompleteApp> incompleteReasonList;
	private String cancelledNote;
	/**
	 * @return the applicationId
	 */
	public Long getApplicationId() {
		return applicationId;
	}
	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the prmyParentEmail
	 */
	public String getPrmyParentEmail() {
		return prmyParentEmail;
	}
	
	/**
	 * @return the incompleteDueDate
	 */
	public Date getIncompleteDueDate() {
		return incompleteDueDate;
	}
	/**
	 * @param incompleteDueDate the incompleteDueDate to set
	 */
	public void setIncompleteDueDate(Date incompleteDueDate) {
		this.incompleteDueDate = incompleteDueDate;
	}
	/**
	 * @param prmyParentEmail the prmyParentEmail to set
	 */
	public void setPrmyParentEmail(String prmyParentEmail) {
		this.prmyParentEmail = prmyParentEmail;
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
	 * @return the statusUpdateDate
	 */
	public Date getStatusUpdateDate() {
		return statusUpdateDate;
	}
	/**
	 * @param statusUpdateDate the statusUpdateDate to set
	 */
	public void setStatusUpdateDate(Date statusUpdateDate) {
		this.statusUpdateDate = statusUpdateDate;
	}
	/**
	 * @return the otherInfo
	 */
	public String getOtherInfo() {
		return otherInfo;
	}
	/**
	 * @param otherInfo the otherInfo to set
	 */
	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
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
	 * @return the studentRecIds
	 */
	public String getStudentRecIds() {
		return studentRecIds;
	}
	/**
	 * @param studentRecIds the studentRecIds to set
	 */
	public void setStudentRecIds(String studentRecIds) {
		this.studentRecIds = studentRecIds;
	}
	/**
	 * @return the householdAppInfo
	 */
	public List<HouseholdAppOtherInfo> getHouseholdAppInfo() {
		return householdAppInfo;
	}
	/**
	 * @param householdAppInfo the householdAppInfo to set
	 */
	public void setHouseholdAppInfo(List<HouseholdAppOtherInfo> householdAppInfo) {
		this.householdAppInfo = householdAppInfo;
	}
	/**
	 * @return the declinedReason
	 */
	public String getDeclinedReason() {
		return declinedReason;
	}
	/**
	 * @param declinedReason the declinedReason to set
	 */
	public void setDeclinedReason(String declinedReason) {
		this.declinedReason = declinedReason;
	}
	/**
	 * @return the declinedReasonList
	 */
	public List<HouseholdAppDeclinedReason> getDeclinedReasonList() {
		return declinedReasonList;
	}
	/**
	 * @param declinedReasonList the declinedReasonList to set
	 */
	public void setDeclinedReasonList(List<HouseholdAppDeclinedReason> declinedReasonList) {
		this.declinedReasonList = declinedReasonList;
	}
	/**
	 * @return the incompleteReason
	 */
	public String getIncompleteReason() {
		return incompleteReason;
	}
	/**
	 * @param incompleteReason the incompleteReason to set
	 */
	public void setIncompleteReason(String incompleteReason) {
		this.incompleteReason = incompleteReason;
	}
	/**
	 * @return the incompleteReasonList
	 */
	public List<HouseholdIncompleteApp> getIncompleteReasonList() {
		return incompleteReasonList;
	}
	/**
	 * @param incompleteReasonList the incompleteReasonList to set
	 */
	public void setIncompleteReasonList(List<HouseholdIncompleteApp> incompleteReasonList) {
		this.incompleteReasonList = incompleteReasonList;
	}
	/**
	 * @return the cancelledNote
	 */
	public String getCancelledNote() {
		return cancelledNote;
	}
	/**
	 * @param cancelledNote the cancelledNote to set
	 */
	public void setCancelledNote(String cancelledNote) {
		this.cancelledNote = cancelledNote;
	}
	
}
