package com.mealManage.domain;

import java.util.List;
import java.util.Map;

public class MealReminderRequest {
	
	private String schoolName;
	private String yearMonth;
	private Map<String, List<StudentDetailSendNotif>> emailStudentDetailsMap;
	private Map<String, String> emailLinkMap;
	private String adminEmail;
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
	 * @return the emailStudentDetailsMap
	 */
	public Map<String, List<StudentDetailSendNotif>> getEmailStudentDetailsMap() {
		return emailStudentDetailsMap;
	}
	/**
	 * @param emailStudentDetailsMap the emailStudentDetailsMap to set
	 */
	public void setEmailStudentDetailsMap(Map<String, List<StudentDetailSendNotif>> emailStudentDetailsMap) {
		this.emailStudentDetailsMap = emailStudentDetailsMap;
	}
	/**
	 * @return the emailLinkMap
	 */
	public Map<String, String> getEmailLinkMap() {
		return emailLinkMap;
	}
	/**
	 * @param emailLinkMap the emailLinkMap to set
	 */
	public void setEmailLinkMap(Map<String, String> emailLinkMap) {
		this.emailLinkMap = emailLinkMap;
	}
	/**
	 * @return the adminEmail
	 */
	public String getAdminEmail() {
		return adminEmail;
	}
	/**
	 * @param adminEmail the adminEmail to set
	 */
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}
	
}
