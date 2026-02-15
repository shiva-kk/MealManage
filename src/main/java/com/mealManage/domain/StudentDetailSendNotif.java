package com.mealManage.domain;

public class StudentDetailSendNotif {
	
	private String studentFName;
	private String studentLName;
	private String studentId;
	private String grade;
	private String cutOffDateTime;
	/**
	 * @return the studentFName
	 */
	public String getStudentFName() {
		if(studentFName != null)
			return studentFName.trim();
		return studentFName;
	}
	/**
	 * @param studentFName the studentFName to set
	 */
	public void setStudentFName(String studentFName) {
		this.studentFName = studentFName;
	}
	/**
	 * @return the studentLName
	 */
	public String getStudentLName() {
		if(studentLName != null)
			return studentLName.trim();
		return studentLName;
	}
	/**
	 * @param studentLName the studentLName to set
	 */
	public void setStudentLName(String studentLName) {
		this.studentLName = studentLName;
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
	 * @return the grade
	 */
	public String getGrade() {
		return grade;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}
	/**
	 * @return the cutOffDateTime
	 */
	public String getCutOffDateTime() {
		return cutOffDateTime;
	}
	/**
	 * @param cutOffDateTime the cutOffDateTime to set
	 */
	public void setCutOffDateTime(String cutOffDateTime) {
		this.cutOffDateTime = cutOffDateTime;
	}

}
