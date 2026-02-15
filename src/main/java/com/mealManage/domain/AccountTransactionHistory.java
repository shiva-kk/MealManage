package com.mealManage.domain;

public class AccountTransactionHistory {
	
	private String transactionDateTime;
	private String transactionType;
	private String paymentType;
	private String purchaseItemType;
	private Double transactionAmount;
	private Double finalBalance;
	private String note;
	/**
	 * @return the transactionDateTime
	 */
	public String getTransactionDateTime() {
		return transactionDateTime;
	}
	/**
	 * @param transactionDateTime the transactionDateTime to set
	 */
	public void setTransactionDateTime(String transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}
	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}
	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
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
	 * @return the purchaseItemType
	 */
	public String getPurchaseItemType() {
		return purchaseItemType;
	}
	/**
	 * @param purchaseItemType the purchaseItemType to set
	 */
	public void setPurchaseItemType(String purchaseItemType) {
		this.purchaseItemType = purchaseItemType;
	}
	/**
	 * @return the transactionAmount
	 */
	public Double getTransactionAmount() {
		return transactionAmount;
	}
	/**
	 * @param transactionAmount the transactionAmount to set
	 */
	public void setTransactionAmount(Double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	/**
	 * @return the finalBalance
	 */
	public Double getFinalBalance() {
		return finalBalance;
	}
	/**
	 * @param finalBalance the finalBalance to set
	 */
	public void setFinalBalance(Double finalBalance) {
		this.finalBalance = finalBalance;
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
