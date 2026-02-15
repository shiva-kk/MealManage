package com.mealManage.domain;

public class LowBalanceStudents {
	
	private String gradeName ;
	private String studentId;
	private Long userId;
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String userName;
	private String parentAltEmail;
	private String teacherName;
	private Boolean isReducePriceEligible=false;
	private Boolean isFreeMealEligible=false;
	private Double accBalance = 0.0;
	
	/**
	 * @return the gradeName
	 */
	public String getGradeName() {
		return gradeName;
	}
	/**
	 * @param gradeName the gradeName to set
	 */
	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
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
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		if(firstName != null)
			return  firstName.trim();
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		if(lastName != null)
			return lastName.trim();
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the mobileNo
	 */
	public String getMobileNo() {
		return mobileNo;
	}
	/**
	 * @param mobileNo the mobileNo to set
	 */
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the parentAltEmail
	 */
	public String getParentAltEmail() {
		return parentAltEmail;
	}
	/**
	 * @param parentAltEmail the parentAltEmail to set
	 */
	public void setParentAltEmail(String parentAltEmail) {
		this.parentAltEmail = parentAltEmail;
	}
	/**
	 * @return the teacherName
	 */
	public String getTeacherName() {
		return teacherName;
	}
	/**
	 * @param teacherName the teacherName to set
	 */
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	/**
	 * @return the isReducePriceEligible
	 */
	public Boolean getIsReducePriceEligible() {
		return isReducePriceEligible;
	}
	/**
	 * @param isReducePriceEligible the isReducePriceEligible to set
	 */
	public void setIsReducePriceEligible(Boolean isReducePriceEligible) {
		this.isReducePriceEligible = isReducePriceEligible;
	}
	/**
	 * @return the isFreeMealEligible
	 */
	public Boolean getIsFreeMealEligible() {
		return isFreeMealEligible;
	}
	/**
	 * @param isFreeMealEligible the isFreeMealEligible to set
	 */
	public void setIsFreeMealEligible(Boolean isFreeMealEligible) {
		this.isFreeMealEligible = isFreeMealEligible;
	}
	/**
	 * @return the accBalance
	 */
	public Double getAccBalance() {
		return accBalance;
	}
	/**
	 * @param accBalance the accBalance to set
	 */
	public void setAccBalance(Double accBalance) {
		this.accBalance = accBalance;
	}

}
