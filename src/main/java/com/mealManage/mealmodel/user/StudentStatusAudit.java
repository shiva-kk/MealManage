package com.mealManage.mealmodel.user;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**This entity used for student status (i.e. Active/In-active) audit**/
@Entity
public class StudentStatusAudit {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long recId;
	@ManyToOne(cascade=CascadeType.ALL) 
	@NotNull
	private StudentUser studentUser;
	private Boolean currentStatus;
	private Boolean previousStatus;
	private Date effectiveStartDate;
	private Date effectiveEndDate;
	private String note; //Data Sync, Manage Screen
	private Integer schoolYear;
	private String lastModifiedBy;
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
	 * @return the currentStatus
	 */
	public Boolean getCurrentStatus() {
		return currentStatus;
	}
	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(Boolean currentStatus) {
		this.currentStatus = currentStatus;
	}
	/**
	 * @return the previousStatus
	 */
	public Boolean getPreviousStatus() {
		return previousStatus;
	}
	/**
	 * @param previousStatus the previousStatus to set
	 */
	public void setPreviousStatus(Boolean previousStatus) {
		this.previousStatus = previousStatus;
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
	
}
