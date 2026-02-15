package com.mealManage.response;

import java.util.List;
import java.util.Map;

public class EditCheckResp {
	
	private Long mealSchoolId;
	private String schoolName;
	private Double attendanceFactor;
	private Map<Integer, Map<Integer, Integer>> stdCountByEligAndDt;
	private List<Integer> schoolHolidays;
	private String schoolTimezone;
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
	 * @return the attendanceFactor
	 */
	public Double getAttendanceFactor() {
		return attendanceFactor;
	}
	/**
	 * @param attendanceFactor the attendanceFactor to set
	 */
	public void setAttendanceFactor(Double attendanceFactor) {
		this.attendanceFactor = attendanceFactor;
	}
	/**
	 * @return the stdCountByEligAndDt
	 */
	public Map<Integer, Map<Integer, Integer>> getStdCountByEligAndDt() {
		return stdCountByEligAndDt;
	}
	/**
	 * @param stdCountByEligAndDt the stdCountByEligAndDt to set
	 */
	public void setStdCountByEligAndDt(Map<Integer, Map<Integer, Integer>> stdCountByEligAndDt) {
		this.stdCountByEligAndDt = stdCountByEligAndDt;
	}
	/**
	 * @return the schoolHolidays
	 */
	public List<Integer> getSchoolHolidays() {
		return schoolHolidays;
	}
	/**
	 * @param schoolHolidays the schoolHolidays to set
	 */
	public void setSchoolHolidays(List<Integer> schoolHolidays) {
		this.schoolHolidays = schoolHolidays;
	}
	/**
	 * @return the schoolTimezone
	 */
	public String getSchoolTimezone() {
		return schoolTimezone;
	}
	/**
	 * @param schoolTimezone the schoolTimezone to set
	 */
	public void setSchoolTimezone(String schoolTimezone) {
		this.schoolTimezone = schoolTimezone;
	}
}
