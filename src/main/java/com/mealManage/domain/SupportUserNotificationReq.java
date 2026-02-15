package com.mealManage.domain;

import java.util.List;

/**This POJO class used for send the notification request to the node js email template API**/
public class SupportUserNotificationReq {
	
	private String userEmail;
	private String issueType;
	private String studentName;
	private String grade;
	private String studentId;
	private String schoolName;
	private String customMessage;
	private String orderIssueYearMonth;
	private List<String> adminEmails;
	private String mMAdminEmail;
	private String type;
	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}
	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	/**
	 * @return the issueType
	 */
	public String getIssueType() {
		return issueType;
	}
	/**
	 * @param issueType the issueType to set
	 */
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}
	
	/**
	 * @return the studentName
	 */
	public String getStudentName() {
		return studentName;
	}
	/**
	 * @param studentName the studentName to set
	 */
	public void setStudentName(String studentName) {
		this.studentName = studentName;
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
	 * @return the customMessage
	 */
	public String getCustomMessage() {
		return customMessage;
	}
	/**
	 * @param customMessage the customMessage to set
	 */
	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}
	/**
	 * @return the orderIssueYearMonth
	 */
	public String getOrderIssueYearMonth() {
		return orderIssueYearMonth;
	}
	/**
	 * @param orderIssueYearMonth the orderIssueYearMonth to set
	 */
	public void setOrderIssueYearMonth(String orderIssueYearMonth) {
		this.orderIssueYearMonth = orderIssueYearMonth;
	}
	/**
	 * @return the adminEmails
	 */
	public List<String> getAdminEmails() {
		return adminEmails;
	}
	/**
	 * @param adminEmails the adminEmails to set
	 */
	public void setAdminEmails(List<String> adminEmails) {
		this.adminEmails = adminEmails;
	}
	/**
	 * @return the mMAdminEmail
	 */
	public String getmMAdminEmail() {
		return mMAdminEmail;
	}
	/**
	 * @param mMAdminEmail the mMAdminEmail to set
	 */
	public void setmMAdminEmail(String mMAdminEmail) {
		this.mMAdminEmail = mMAdminEmail;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
