package com.mealManage.response;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DuePaymentResp {
	
	private String packageName;
	private String stdFName;
	private String stdLName;
	private Double pkgCost;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date pkgStartDt;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date pkgEndDt;
	private String parentEmail;
	
	public DuePaymentResp() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param packageName
	 * @param stdFName
	 * @param stdLName
	 * @param pkgCost
	 * @param pkgStartDt
	 * @param pkgEndDt
	 * @param masterPkgId
	 * @param parentEmail
	 */
	public DuePaymentResp(Object[] obj) {
		super();
		this.packageName = obj[0] != null ? (String)obj[0] : "";
		this.stdFName = obj[1] != null ? (String)obj[1] : "";
		this.stdLName = obj[2] != null ? (String)obj[2] : "";
		this.pkgCost = obj[3] != null ? (Double)obj[3] : 0;
		this.pkgStartDt = obj[4] != null ? (Timestamp) obj[4] : null;
		this.pkgEndDt = obj[5] != null ? (Timestamp) obj[5] : null;
		this.parentEmail = obj[6] != null ? (String) obj[6] : "";
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
	 * @return the pkgCost
	 */
	public Double getPkgCost() {
		return pkgCost;
	}

	/**
	 * @param pkgCost the pkgCost to set
	 */
	public void setPkgCost(Double pkgCost) {
		this.pkgCost = pkgCost;
	}

	/**
	 * @return the pkgStartDt
	 */
	public Date getPkgStartDt() {
		return pkgStartDt;
	}

	/**
	 * @param pkgStartDt the pkgStartDt to set
	 */
	public void setPkgStartDt(Date pkgStartDt) {
		this.pkgStartDt = pkgStartDt;
	}

	/**
	 * @return the pkgEndDt
	 */
	public Date getPkgEndDt() {
		return pkgEndDt;
	}

	/**
	 * @param pkgEndDt the pkgEndDt to set
	 */
	public void setPkgEndDt(Date pkgEndDt) {
		this.pkgEndDt = pkgEndDt;
	}

	/**
	 * @return the parentEmail
	 */
	public String getParentEmail() {
		return parentEmail;
	}

	/**
	 * @param parentEmail the parentEmail to set
	 */
	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}
	
}
