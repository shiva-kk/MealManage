package com.mealManage.mealmodel.packages;

import java.math.BigInteger;

/*This POJO used for pickup authorized response*/
public class PickupAuthorizedResp {
	
	private Long authorizedId;
	private String firstName;
	private String lastName;
	private String phoneNo;
	private String relation;
	private Long stdRecId;
	private Boolean isActive;
	public PickupAuthorizedResp() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param firstName
	 * @param lastName
	 * @param relation
	 * @param phone
	 * @param stdRecId
	 */
	public PickupAuthorizedResp(Object[] obj) {
		super();
		this.authorizedId = obj[0]!=null?((BigInteger)obj[0]).longValue():null;
		this.firstName = obj[1]!=null?(String)obj[1]:null;
		this.lastName = obj[2]!=null?(String)obj[2]:null;
		this.phoneNo = obj[3]!=null?(String)obj[3]:null;
		this.relation = obj[4]!=null?(String)obj[4]:null;
		this.stdRecId = obj[5]!=null?((BigInteger)obj[5]).longValue():null;
		this.isActive = obj[6]!=null?(Boolean)obj[6]:true;
	}
	
	/**
	 * @return the authorizedId
	 */
	public Long getAuthorizedId() {
		return authorizedId;
	}

	/**
	 * @param authorizedId the authorizedId to set
	 */
	public void setAuthorizedId(Long authorizedId) {
		this.authorizedId = authorizedId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
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
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}
	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	/**
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}

	/**
	 * @param phoneNo the phoneNo to set
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	/**
	 * @return the stdRecId
	 */
	public Long getStdRecId() {
		return stdRecId;
	}
	/**
	 * @param stdRecId the stdRecId to set
	 */
	public void setStdRecId(Long stdRecId) {
		this.stdRecId = stdRecId;
	}

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
}
