package com.mealManage.domain;

import java.util.List;

import com.mealManage.mealmodel.school.SchoolGrades;

/**This POJO class used for build the request of menu order cancellation**/
public class MenuOrderCancellationReq {
	
	private String yearMonth;
	private List<SchoolGrades> gradeList;
	private List<Long> studentRecordIds;
	private String cancellationNote;
	private Boolean isGradeWise = false;
	private String loggedUser;
	//@JsonFormat(pattern="yyyy-MM-dd")
	private List<String> dateList;

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
	 * @return the gradeList
	 */
	public List<SchoolGrades> getGradeList() {
		return gradeList;
	}
	/**
	 * @param gradeList the gradeList to set
	 */
	public void setGradeList(List<SchoolGrades> gradeList) {
		this.gradeList = gradeList;
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
	 * @return the isGradeWise
	 */
	public Boolean getIsGradeWise() {
		return isGradeWise;
	}
	/**
	 * @param isGradeWise the isGradeWise to set
	 */
	public void setIsGradeWise(Boolean isGradeWise) {
		this.isGradeWise = isGradeWise;
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
	/**
	 * @return the dateList
	 */
	public List<String> getDateList() {
		return dateList;
	}
	/**
	 * @param dateList the dateList to set
	 */
	public void setDateList(List<String> dateList) {
		this.dateList = dateList;
	}
	
}
