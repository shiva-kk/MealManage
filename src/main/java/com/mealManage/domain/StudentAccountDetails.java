package com.mealManage.domain;

import java.util.List;

/**This POJO class used for Account transaction history details**/
public class StudentAccountDetails {
	
	private String studentFName;
	private String studentLName;
	private String studentId;
	private String startDate;
	private String endDate;
	private List<AccountTransactionHistory> accountTransactionHistories;
	private String numberStreetApt;
	private String cityStateZip;
	private String note;
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
			studentLName.trim();
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
	 * @return the accountTransactionHistories
	 */
	public List<AccountTransactionHistory> getAccountTransactionHistories() {
		return accountTransactionHistories;
	}
	/**
	 * @param accountTransactionHistories the accountTransactionHistories to set
	 */
	public void setAccountTransactionHistories(List<AccountTransactionHistory> accountTransactionHistories) {
		this.accountTransactionHistories = accountTransactionHistories;
	}
	/**
	 * @return the numberStreetApt
	 */
	public String getNumberStreetApt() {
		return numberStreetApt;
	}
	/**
	 * @param numberStreetApt the numberStreetApt to set
	 */
	public void setNumberStreetApt(String numberStreetApt) {
		this.numberStreetApt = numberStreetApt;
	}
	/**
	 * @return the cityStateZip
	 */
	public String getCityStateZip() {
		return cityStateZip;
	}
	/**
	 * @param cityStateZip the cityStateZip to set
	 */
	public void setCityStateZip(String cityStateZip) {
		this.cityStateZip = cityStateZip;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
}
