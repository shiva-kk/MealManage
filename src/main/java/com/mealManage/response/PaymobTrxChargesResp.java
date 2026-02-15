package com.mealManage.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**This POJO class used for map the paymob transactions report charges response**/
public class PaymobTrxChargesResp {
	
	private String trxDateTime;
	@JsonIgnore
	private Date trxDtTm;
	private Double totalTrxAmt;
	private String transferId;
	private String chargeId;
	private Double appFee;
	private String userEmail;
	public PaymobTrxChargesResp() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param trxDateTime
	 * @param trxDtTm
	 * @param totalTrxAmt
	 * @param transferId
	 * @param chargeId
	 * @param appFee
	 * @param userEmail
	 */
	public PaymobTrxChargesResp(Object[] obj) {
		super();
		this.trxDtTm = obj[0]!=null?(Date)obj[0]:null;
		this.totalTrxAmt = Double.parseDouble(obj[1] != null ? obj[1].toString() : "0.00)");
		this.transferId = obj[2]!=null?(String)obj[2]:null;
		this.chargeId = obj[3]!=null?(String)obj[3]:null;
		this.appFee = Double.parseDouble(obj[4] != null ? obj[4].toString() : "0.00)");
		this.userEmail = obj[5]!=null?(String)obj[5]:null;
	}

	/**
	 * @return the trxDateTime
	 */
	public String getTrxDateTime() {
		return trxDateTime;
	}
	/**
	 * @param trxDateTime the trxDateTime to set
	 */
	public void setTrxDateTime(String trxDateTime) {
		this.trxDateTime = trxDateTime;
	}
	/**
	 * @return the trxDtTm
	 */
	public Date getTrxDtTm() {
		return trxDtTm;
	}
	/**
	 * @param trxDtTm the trxDtTm to set
	 */
	public void setTrxDtTm(Date trxDtTm) {
		this.trxDtTm = trxDtTm;
	}
	/**
	 * @return the totalTrxAmt
	 */
	public Double getTotalTrxAmt() {
		return totalTrxAmt;
	}
	/**
	 * @param totalTrxAmt the totalTrxAmt to set
	 */
	public void setTotalTrxAmt(Double totalTrxAmt) {
		this.totalTrxAmt = totalTrxAmt;
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
	 * @return the chargeId
	 */
	public String getChargeId() {
		return chargeId;
	}
	/**
	 * @param chargeId the chargeId to set
	 */
	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
	}
	/**
	 * @return the appFee
	 */
	public Double getAppFee() {
		return appFee;
	}
	/**
	 * @param appFee the appFee to set
	 */
	public void setAppFee(Double appFee) {
		this.appFee = appFee;
	}
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
	
}
