package com.mealManage.mealmodel.packages;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*This POJO class used for map package purchase deposit transactions*/
public class PackagePaymentsTrx {
	
	private String stdFName;
	private String stdLName;
	@JsonIgnore
	private Date trxDtTime;
	private String transactionDateTime;
	private String paymentType;
	private Double amount;
	private String transferId;
	private String userEmail;
	private String packageName;
	private String startDt;
	private String endDt;
	private String packageType;
	private String grade;
	public PackagePaymentsTrx() {
		// TODO Auto-generated constructor stub
	}
	
	public PackagePaymentsTrx(Object[] obj) {
		super();
		this.stdFName = obj[0]!=null?(String)obj[0]:null;
		this.stdLName = obj[1]!=null?(String)obj[1]:null;
		this.trxDtTime = obj[2]!=null?(Date)obj[2]:null;
		this.paymentType = obj[3]!=null?(String)obj[3]:null;
		this.amount = Double.parseDouble(obj[4] != null ? obj[4].toString() : "0.00");
		this.transferId = obj[5]!=null?(String)obj[5]:(obj[12]!=null?(String)obj[12]:"");
		this.userEmail = obj[6]!=null?(String)obj[6]:null;
		this.packageName = obj[7]!=null?(String)obj[7]:null;
		this.startDt = obj[8]!=null?(new SimpleDateFormat("MM/dd/yyyy").format((Date)obj[8])):"";
		this.endDt = obj[9]!=null?(new SimpleDateFormat("MM/dd/yyyy").format((Date)obj[9])):"";
		this.packageType = obj[10]!=null?(String)obj[10]:null;
		this.grade = obj[11]!=null?(String)obj[11]:null;
	}

	/**
	 * @return the stdFName
	 */
	public String getStdFName() {
		return stdFName;
	}
	/**
	 * @param stdFName the stdFName to set
	 */
	public void setStdFName(String stdFName) {
		this.stdFName = stdFName;
	}
	/**
	 * @return the stdLName
	 */
	public String getStdLName() {
		return stdLName;
	}
	/**
	 * @param stdLName the stdLName to set
	 */
	public void setStdLName(String stdLName) {
		this.stdLName = stdLName;
	}
	
	/**
	 * @return the trxDtTime
	 */
	public Date getTrxDtTime() {
		return trxDtTime;
	}

	/**
	 * @param trxDtTime the trxDtTime to set
	 */
	public void setTrxDtTime(Date trxDtTime) {
		this.trxDtTime = trxDtTime;
	}
	
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
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}
	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	/**
	 * @return the startDt
	 */
	public String getStartDt() {
		return startDt;
	}
	/**
	 * @param startDt the startDt to set
	 */
	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}
	/**
	 * @return the endDt
	 */
	public String getEndDt() {
		return endDt;
	}
	/**
	 * @param endDt the endDt to set
	 */
	public void setEndDt(String endDt) {
		this.endDt = endDt;
	}
	/**
	 * @return the packageType
	 */
	public String getPackageType() {
		return packageType;
	}
	/**
	 * @param packageType the packageType to set
	 */
	public void setPackageType(String packageType) {
		this.packageType = packageType;
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
	
}
