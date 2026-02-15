package com.mealManage.mealmodel.user;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**This Entity used for Student eligibility audit**/
@Entity
public class StudentEligibilityAudit {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long recId;
	@ManyToOne(cascade=CascadeType.ALL) 
	@NotNull
	private StudentUser studentUser;
	private Integer currentEligStatus; //0 for free meals eligibility, 1 for reduced price and 2 for regular price eligibility.
	private Integer previousEligStatus; //0 for free meals eligibility, 1 for reduced price and 2 for regular price eligibility.
	private Date effectiveStartDate;
	private Date effectiveEndDate;
	private String note; //Data Sync, File Import, Manage Screen
	private Integer schoolYear;
	private String lastModifiedBy;
	private String decisionReason;
	private String category;
	/**
	 * @return the recId
	 */
	public Long getRecId() {
		return recId;
	}
	/**
	 * @param recId the recId to set
	 */
	public void setRecId(Long recId) {
		this.recId = recId;
	}
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
	 * @return the currentEligStatus
	 */
	public Integer getCurrentEligStatus() {
		return currentEligStatus;
	}
	/**
	 * @param currentEligStatus the currentEligStatus to set
	 */
	public void setCurrentEligStatus(Integer currentEligStatus) {
		this.currentEligStatus = currentEligStatus;
	}
	/**
	 * @return the previousEligStatus
	 */
	public Integer getPreviousEligStatus() {
		return previousEligStatus;
	}
	/**
	 * @param previousEligStatus the previousEligStatus to set
	 */
	public void setPreviousEligStatus(Integer previousEligStatus) {
		this.previousEligStatus = previousEligStatus;
	}
	/**
	 * @return the effectiveStartDate
	 */
	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}
	/**
	 * @param effectiveStartDate the effectiveStartDate to set
	 */
	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}
	/**
	 * @return the effectiveEndDate
	 */
	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}
	/**
	 * @param effectiveEndDate the effectiveEndDate to set
	 */
	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
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
	 * @return the lastModifiedBy
	 */
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	/**
	 * @param lastModifiedBy the lastModifiedBy to set
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
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
	
}
