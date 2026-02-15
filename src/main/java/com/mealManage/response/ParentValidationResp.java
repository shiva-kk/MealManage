package com.mealManage.response;

public class ParentValidationResp {
	
	private String status;
	private Integer statusCode;
	private String mobileNumber;
	private String fToken;
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}
	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * @return the fToken
	 */
	public String getfToken() {
		return fToken;
	}
	/**
	 * @param fToken the fToken to set
	 */
	public void setfToken(String fToken) {
		this.fToken = fToken;
	}
	
}