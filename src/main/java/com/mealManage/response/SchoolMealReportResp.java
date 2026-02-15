package com.mealManage.response;

import java.util.List;
import java.util.Map;

import com.mealManage.mealmodel.school.SchoolGrades;

public class SchoolMealReportResp {
	
	private String startDate;
	private String endDate;
	private List<SchoolGrades> grades;
	private Map<String, List<String>> allMealNameByDate;
	private List<StudentInfoWithMeal> studentWithMeal;
	private Map<String, Map<String, List<String>>> mealsByDateAndGrade;
	private Map<String, Map<String, List<String>>> mealsByGradeAndDate;
	private Boolean isAllergyEnabled;
	private Boolean havingExtraPreOrders;
	private String countryCode;
	
	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the grades
	 */
	public List<SchoolGrades> getGrades() {
		return grades;
	}
	/**
	 * @param grades the grades to set
	 */
	public void setGrades(List<SchoolGrades> grades) {
		this.grades = grades;
	}
	
	/**
	 * @return the allMealNameByDate
	 */
	public Map<String, List<String>> getAllMealNameByDate() {
		return allMealNameByDate;
	}
	/**
	 * @param allMealNameByDate the allMealNameByDate to set
	 */
	public void setAllMealNameByDate(Map<String, List<String>> allMealNameByDate) {
		this.allMealNameByDate = allMealNameByDate;
	}
	/**
	 * @return the studentWithMeal
	 */
	public List<StudentInfoWithMeal> getStudentWithMeal() {
		return studentWithMeal;
	}
	/**
	 * @param studentWithMeal the studentWithMeal to set
	 */
	public void setStudentWithMeal(List<StudentInfoWithMeal> studentWithMeal) {
		this.studentWithMeal = studentWithMeal;
	}
	/**
	 * @return the mealsByDateAndGrade
	 */
	public Map<String, Map<String, List<String>>> getMealsByDateAndGrade() {
		return mealsByDateAndGrade;
	}
	/**
	 * @param mealsByDateAndGrade the mealsByDateAndGrade to set
	 */
	public void setMealsByDateAndGrade(Map<String, Map<String, List<String>>> mealsByDateAndGrade) {
		this.mealsByDateAndGrade = mealsByDateAndGrade;
	}
	/**
	 * @return the mealsByGradeAndDate
	 */
	public Map<String, Map<String, List<String>>> getMealsByGradeAndDate() {
		return mealsByGradeAndDate;
	}
	/**
	 * @param mealsByGradeAndDate the mealsByGradeAndDate to set
	 */
	public void setMealsByGradeAndDate(Map<String, Map<String, List<String>>> mealsByGradeAndDate) {
		this.mealsByGradeAndDate = mealsByGradeAndDate;
	}
	/**
	 * @return the isAllergyEnabled
	 */
	public Boolean getIsAllergyEnabled() {
		return isAllergyEnabled;
	}
	/**
	 * @param isAllergyEnabled the isAllergyEnabled to set
	 */
	public void setIsAllergyEnabled(Boolean isAllergyEnabled) {
		this.isAllergyEnabled = isAllergyEnabled;
	}
	/**
	 * @return the havingExtraPreOrders
	 */
	public Boolean getHavingExtraPreOrders() {
		return havingExtraPreOrders;
	}
	/**
	 * @param havingExtraPreOrders the havingExtraPreOrders to set
	 */
	public void setHavingExtraPreOrders(Boolean havingExtraPreOrders) {
		this.havingExtraPreOrders = havingExtraPreOrders;
	}
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
}
