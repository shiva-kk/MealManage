package com.mealManage.domain;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.mealmodel.transaction.PaymentType;

public class StudentMealOrdersV2 {
	
	private Long studentId;
	private Map<String, List<Long>> calendarIdsByMonth;
	private Double walletAmt = 0.0;
	private Double instantPayAmt = 0.0;
	private String transactionToken;
	private Double transactionFees;
	private Double appFeeAmount;
	private PaymentType paymentType;
	private String checkNum;
	@JsonIgnore
	private Double finalOrderAmt;
	
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
	 * @return the calendarIdsByMonth
	 */
	public Map<String, List<Long>> getCalendarIdsByMonth() {
		return calendarIdsByMonth;
	}
	/**
	 * @param calendarIdsByMonth the calendarIdsByMonth to set
	 */
	public void setCalendarIdsByMonth(Map<String, List<Long>> calendarIdsByMonth) {
		this.calendarIdsByMonth = calendarIdsByMonth;
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
	/**
	 * @return the finalOrderAmt
	 */
	public Double getFinalOrderAmt() {
		return finalOrderAmt;
	}
	/**
	 * @param finalOrderAmt the finalOrderAmt to set
	 */
	public void setFinalOrderAmt(Double finalOrderAmt) {
		this.finalOrderAmt = finalOrderAmt;
	}
	
}
