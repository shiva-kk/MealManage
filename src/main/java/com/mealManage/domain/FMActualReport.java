package com.mealManage.domain;

public class FMActualReport {
	
	private String studentFName;
	private String studentLName;
	private String gradeName;
	private String studentId;
	private boolean isFreeMeal;
	private boolean isReducedPrice;
	private String teacherName;
	private String schoolName;
	private String parentEmail;
	
	/**
	 * @return the studentFName
	 */
	public String getStudentFName() {
		if(studentFName != null)
			return studentFName = studentFName.trim();
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
	 * @return the gradeName
	 */
	public String getGradeName() {
		return gradeName;
	}
	/**
	 * @param gradeName the gradeName to set
	 */
	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
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
	 * @return the isFreeMeal
	 */
	public boolean isFreeMeal() {
		return isFreeMeal;
	}
	/**
	 * @param isFreeMeal the isFreeMeal to set
	 */
	public void setFreeMeal(boolean isFreeMeal) {
		this.isFreeMeal = isFreeMeal;
	}
	/**
	 * @return the isReducedPrice
	 */
	public boolean isReducedPrice() {
		return isReducedPrice;
	}
	/**
	 * @param isReducedPrice the isReducedPrice to set
	 */
	public void setReducedPrice(boolean isReducedPrice) {
		this.isReducedPrice = isReducedPrice;
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
	
}
