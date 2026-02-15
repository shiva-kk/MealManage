package com.mealManage.response;

import java.util.List;
import java.util.Map;

public class CatererReportResp {
	
	private String schoolName;
	private List<String> grades;
	private String startDate;
	private String endDate;
	private Map<String, Long> mealItemWithCounts;
	private Map<String, Map<String, Integer>> dateMealItemCountMap;
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
	 * @return the grades
	 */
	public List<String> getGrades() {
		return grades;
	}
	/**
	 * @param grades the grades to set
	 */
	public void setGrades(List<String> grades) {
		this.grades = grades;
	}
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
	 * @return the mealItemWithCounts
	 */
	public Map<String, Long> getMealItemWithCounts() {
		return mealItemWithCounts;
	}
	/**
	 * @param mealItemWithCounts the mealItemWithCounts to set
	 */
	public void setMealItemWithCounts(Map<String, Long> mealItemWithCounts) {
		this.mealItemWithCounts = mealItemWithCounts;
	}
	/**
	 * @return the dateMealItemCountMap
	 */
	public Map<String, Map<String, Integer>> getDateMealItemCountMap() {
		return dateMealItemCountMap;
	}
	/**
	 * @param dateMealItemCountMap the dateMealItemCountMap to set
	 */
	public void setDateMealItemCountMap(Map<String, Map<String, Integer>> dateMealItemCountMap) {
		this.dateMealItemCountMap = dateMealItemCountMap;
	}
}
