package com.mealManage.domain;

import com.mealManage.mealmodel.school.SchoolGrades;

public class LunchNotServedStudents {
	
	private Long studentRecId;
	private String mealName;
	private String studentFName;
	private String studentLName;
	private SchoolGrades schoolGrades;
	private String studentId;
	/**
	 * @return the studentRecId
	 */
	public Long getStudentRecId() {
		return studentRecId;
	}
	/**
	 * @param studentRecId the studentRecId to set
	 */
	public void setStudentRecId(Long studentRecId) {
		this.studentRecId = studentRecId;
	}
	/**
	 * @return the mealName
	 */
	public String getMealName() {
		return mealName;
	}
	/**
	 * @param mealName the mealName to set
	 */
	public void setMealName(String mealName) {
		this.mealName = mealName;
	}
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
	 * @return the schoolGrades
	 */
	public SchoolGrades getSchoolGrades() {
		return schoolGrades;
	}
	/**
	 * @param schoolGrades the schoolGrades to set
	 */
	public void setSchoolGrades(SchoolGrades schoolGrades) {
		this.schoolGrades = schoolGrades;
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

}
