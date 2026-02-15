package com.mealManage.domain;

import java.util.Date;
import java.util.List;

import com.mealManage.mealmodel.transaction.PaymentType;

public class StudentMealOrders {
	
	private Long studentId;
	private List<Long> schoolMealIds;
	/*@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone = "IST")*/
	private Date cutOffDateTime;
	private String parentUserEmails;
	private Double walletAmt = 0.0;
	private Double instantPayAmt = 0.0;
	private String transactionToken;
	private Double transactionFees;
	private Double appFeeAmount;
	private PaymentType paymentType;
	private String checkNum;
	
	/**
	 * @return the studentId
	 */
	public Long getStudentId() {
		return studentId;
	}
	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
	/**
	 * @return the schoolMealIds
	 */
	public List<Long> getSchoolMealIds() {
		return schoolMealIds;
	}
	/**
	 * @param schoolMealIds the schoolMealIds to set
	 */
	public void setSchoolMealIds(List<Long> schoolMealIds) {
		this.schoolMealIds = schoolMealIds;
	}
	/**
	 * @return the cutOffDateTime
	 */
	public Date getCutOffDateTime() {
		return cutOffDateTime;
	}
	/**
	 * @param cutOffDateTime the cutOffDateTime to set
	 */
	public void setCutOffDateTime(Date cutOffDateTime) {
		this.cutOffDateTime = cutOffDateTime;
	}
	/**
	 * @return the parentUserEmails
	 */
	public String getParentUserEmails() {
		return parentUserEmails;
	}
	/**
	 * @param parentUserEmails the parentUserEmails to set
	 */
	public void setParentUserEmails(String parentUserEmails) {
		this.parentUserEmails = parentUserEmails;
	}
	/**
	 * @return the walletAmt
	 */
	public Double getWalletAmt() {
		return walletAmt;
	}
	/**
	 * @param walletAmt the walletAmt to set
	 */
	public void setWalletAmt(Double walletAmt) {
		this.walletAmt = walletAmt;
	}
	/**
	 * @return the instantPayAmt
	 */
	public Double getInstantPayAmt() {
		return instantPayAmt;
	}
	/**
	 * @param instantPayAmt the instantPayAmt to set
	 */
	public void setInstantPayAmt(Double instantPayAmt) {
		this.instantPayAmt = instantPayAmt;
	}
	/**
	 * @return the transactionToken
	 */
	public String getTransactionToken() {
		return transactionToken;
	}
	/**
	 * @param transactionToken the transactionToken to set
	 */
	public void setTransactionToken(String transactionToken) {
		this.transactionToken = transactionToken;
	}
	/**
	 * @return the transactionFees
	 */
	public Double getTransactionFees() {
		return transactionFees;
	}
	/**
	 * @param transactionFees the transactionFees to set
	 */
	public void setTransactionFees(Double transactionFees) {
		this.transactionFees = transactionFees;
	}
	/**
	 * @return the appFeeAmount
	 */
	public Double getAppFeeAmount() {
		return appFeeAmount;
	}
	/**
	 * @param appFeeAmount the appFeeAmount to set
	 */
	public void setAppFeeAmount(Double appFeeAmount) {
		this.appFeeAmount = appFeeAmount;
	}
	/**
	 * @return the paymentType
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}
	/**
	 * @param paymentType the paymentType to set
	 */
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
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
	
}
