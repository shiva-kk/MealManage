package com.mealManage.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.mealmodel.school.SchoolGrades;

/**This POJO class contains the required parameters related to restore cancelled menu**/
public class RestoreCancelledMenuReq {
	
	private SchoolGrades grade;
	private List<Long> studentRecordIds;
	private String restoreNote;
	private String restoreDate; //yyyy-MM-dd
	private String yearMonth;
	@JsonIgnore
	private String loggedUser;
	
	/**
	 * @return the grade
	 */
	public SchoolGrades getGrade() {
		return grade;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(SchoolGrades grade) {
		this.grade = grade;
	}
	/**
	 * @return the studentRecordIds
	 */
	public List<Long> getStudentRecordIds() {
		return studentRecordIds;
	}
	/**
	 * @param studentRecordIds the studentRecordIds to set
	 */
	public void setStudentRecordIds(List<Long> studentRecordIds) {
		this.studentRecordIds = studentRecordIds;
	}
	/**
	 * @return the restoreNote
	 */
	public String getRestoreNote() {
		return restoreNote;
	}
	/**
	 * @param restoreNote the restoreNote to set
	 */
	public void setRestoreNote(String restoreNote) {
		this.restoreNote = restoreNote;
	}
	/**
	 * @return the restoreDate
	 */
	public String getRestoreDate() {
		return restoreDate;
	}
	/**
	 * @param restoreDate the restoreDate to set
	 */
	public void setRestoreDate(String restoreDate) {
		this.restoreDate = restoreDate;
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
	 * @return the loggedUser
	 */
	public String getLoggedUser() {
		return loggedUser;
	}
	/**
	 * @param loggedUser the loggedUser to set
	 */
	public void setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
	}
	
}
