package com.mealManage.response;

import java.util.List;
import java.util.Map;

public class StudentInfoWithMeal {
	
	private String studentLName;
	private String studentFName;
	private String studentId;
	private Map<String, List<String>> mealOrderedByDate;
	private Map<String, List<String>> sideOrderedByDate;
	private Map<String, List<String>> extraOrderedByDate;
	private String teacherName;
	private String grade;
	private String allergies;
	private String served;
	private Long stdRecId;
	private Long mealSchoolId;
	private Object orders;
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
		this.studentLName = studentLName;//.trim().toUpperCase();
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
		this.studentFName = studentFName;//.trim().toUpperCase();
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
	 * @return the mealOrderedByDate
	 */
	public Map<String, List<String>> getMealOrderedByDate() {
		return mealOrderedByDate;
	}
	/**
	 * @param mealOrderedByDate the mealOrderedByDate to set
	 */
	public void setMealOrderedByDate(Map<String, List<String>> mealOrderedByDate) {
		this.mealOrderedByDate = mealOrderedByDate;
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
	/**
	 * @return the served
	 */
	public String getServed() {
		return served;
	}
	/**
	 * @param served the served to set
	 */
	public void setServed(String served) {
		this.served = served;
	}
	/**
	 * @return the sideOrderedByDate
	 */
	public Map<String, List<String>> getSideOrderedByDate() {
		return sideOrderedByDate;
	}
	/**
	 * @param sideOrderedByDate the sideOrderedByDate to set
	 */
	public void setSideOrderedByDate(Map<String, List<String>> sideOrderedByDate) {
		this.sideOrderedByDate = sideOrderedByDate;
	}
	/**
	 * @return the extraOrderedByDate
	 */
	public Map<String, List<String>> getExtraOrderedByDate() {
		return extraOrderedByDate;
	}
	/**
	 * @param extraOrderedByDate the extraOrderedByDate to set
	 */
	public void setExtraOrderedByDate(Map<String, List<String>> extraOrderedByDate) {
		this.extraOrderedByDate = extraOrderedByDate;
	}
	/**
	 * @return the stdRecId
	 */
	public Long getStdRecId() {
		return stdRecId;
	}
	/**
	 * @param stdRecId the stdRecId to set
	 */
	public void setStdRecId(Long stdRecId) {
		this.stdRecId = stdRecId;
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
	 * @return the orders
	 */
	public Object getOrders() {
		return orders;
	}
	/**
	 * @param orders the orders to set
	 */
	public void setOrders(Object orders) {
		this.orders = orders;
	}
	
}
