package com.mealManage.domain;

public class StudentsWithAllergies {
	
	private String stdFName;
	private String stdLName;
	private String grade;
	private String teacherName;
	private String allergies;
	/**
	 * @return the stdFName
	 */
	public String getStdFName() {
		if(stdFName != null)
			return stdFName.trim();
		return stdFName;
	}
	/**
	 * @param stdFName the stdFName to set
	 */
	public void setStdFName(String stdFName) {
		this.stdFName = stdFName;
	}
	/**
	 * @return the stdLName
	 */
	public String getStdLName() {
		if(stdLName != null)
			return stdLName.trim();
		return stdLName;
	}
	/**
	 * @param stdLName the stdLName to set
	 */
	public void setStdLName(String stdLName) {
		this.stdLName = stdLName;
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
	 * @return the allergies
	 */
	public String getAllergies() {
		return allergies;
	}
	/**
	 * @param allergies the allergies to set
	 */
	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

}
