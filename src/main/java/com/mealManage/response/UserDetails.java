package com.mealManage.response;

public class UserDetails {
	private String username;
	private String password;
	private String role;
	private String fToken;
	private String fTokenTime;
	private String deviceId;
	private String otpOn;
	private String deviceIP;
	private String otp;
	private String oldPassword;
	private String parentMobile;
	private Boolean isMobileApp;
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
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
	/**
	 * @return the fTokenTime
	 */
	public String getfTokenTime() {
		return fTokenTime;
	}
	/**
	 * @param fTokenTime the fTokenTime to set
	 */
	public void setfTokenTime(String fTokenTime) {
		this.fTokenTime = fTokenTime;
	}
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the otpOn
	 */
	public String getOtpOn() {
		return otpOn;
	}
	/**
	 * @param otpOn the otpOn to set
	 */
	public void setOtpOn(String otpOn) {
		this.otpOn = otpOn;
	}
	/**
	 * @return the deviceIP
	 */
	public String getDeviceIP() {
		return deviceIP;
	}
	/**
	 * @param deviceIP the deviceIP to set
	 */
	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}
	/**
	 * @return the otp
	 */
	public String getOtp() {
		return otp;
	}
	/**
	 * @param otp the otp to set
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}
	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}
	/**
	 * @param oldPassword the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	/**
	 * @return the parentMobile
	 */
	public String getParentMobile() {
		return parentMobile;
	}
	/**
	 * @param parentMobile the parentMobile to set
	 */
	public void setParentMobile(String parentMobile) {
		if(parentMobile != null && parentMobile.contains("+"))
			this.parentMobile = "+"+parentMobile.replaceAll("[^a-zA-Z0-9]", "");
		else if(parentMobile != null)
			this.parentMobile = parentMobile.replaceAll("[^a-zA-Z0-9]", "");
		else
			this.parentMobile = parentMobile;
	}
	/**
	 * @return the isMobileApp
	 */
	public Boolean getIsMobileApp() {
		return isMobileApp;
	}
	/**
	 * @param isMobileApp the isMobileApp to set
	 */
	public void setIsMobileApp(Boolean isMobileApp) {
		this.isMobileApp = isMobileApp;
	}
	
}
