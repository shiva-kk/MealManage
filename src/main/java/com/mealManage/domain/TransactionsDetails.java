package com.mealManage.domain;

/**This POJO class used for the deposit transactions details**/
public class TransactionsDetails {
	
	private Long idNumb;
	private String studentLName;
	private String studentFName;
	private String transactionDate;
	private String transactionTime;
	private Double amount;
	private String transactionDesc;
	private String note;
	private String user;
	private String source;
	private String itemPurchased;
	private String grade;
	private String paymentType;
	private String checkNum;
	private String transferId;
	private String schoolName;
	private String category;
	private String location;
	private String type;
	/**
	 * @return the idNumb
	 */
	public Long getIdNumb() {
		return idNumb;
	}
	/**
	 * @param idNumb the idNumb to set
	 */
	public void setIdNumb(Long idNumb) {
		this.idNumb = idNumb;
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
	 * @return the transactionDate
	 */
	public String getTransactionDate() {
		return transactionDate;
	}
	/**
	 * @param transactionDate the transactionDate to set
	 */
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	/**
	 * @return the transactionTime
	 */
	public String getTransactionTime() {
		return transactionTime;
	}
	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * @return the transactionDesc
	 */
	public String getTransactionDesc() {
		return transactionDesc;
	}
	/**
	 * @param transactionDesc the transactionDesc to set
	 */
	public void setTransactionDesc(String transactionDesc) {
		this.transactionDesc = transactionDesc;
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
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the itemPurchased
	 */
	public String getItemPurchased() {
		return itemPurchased;
	}
	/**
	 * @param itemPurchased the itemPurchased to set
	 */
	public void setItemPurchased(String itemPurchased) {
		this.itemPurchased = itemPurchased;
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
	 * @return the checkNum
	 */
	public String getCheckNum() {
		return checkNum;
	}
	/**
	 * @param checkNum the checkNum to set
	 */
	public void setCheckNum(String checkNum) {
		this.checkNum = checkNum;
	}
	/**
	 * @return the transferId
	 */
	public String getTransferId() {
		return transferId;
	}
	/**
	 * @param transferId the transferId to set
	 */
	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}
	/**
	 * @return the paymentType
	 */
	public String getPaymentType() {
		return paymentType;
	}
	/**
	 * @param paymentType the paymentType to set
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
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
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
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
