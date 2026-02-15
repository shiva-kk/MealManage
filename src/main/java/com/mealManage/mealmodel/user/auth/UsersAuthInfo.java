package com.mealManage.mealmodel.user.auth;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealManage.mealmodel.school.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "UserAuthInfo_v2", indexes = { 
	    @Index(columnList = "role")})
/**This entity having all the users authentication details**/
public class UsersAuthInfo extends BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userAuthInfoId", updatable = false, nullable = false)
	private Long userAuthInfoId;
	
	private static final long serialVersionUID = -1473611734737650176L;
	@Column(unique=true, nullable = false)
	private String username;
	@NotNull
	private String role;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password="";
	@JsonIgnore
	private String fToken;
	@JsonIgnore
	private Date fTokenTime;
	@JsonIgnore
	private String otp;
	private String mobile;
	private Boolean paymentReminderEnable = true;
	private Boolean lunchReminderEnable = true;
	private Boolean emailIsSubscribe = true;
	private String partner_id;
	private Long district_id;
	private String partnerName;
	
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
	public Date getfTokenTime() {
		return fTokenTime;
	}
	/**
	 * @param fTokenTime the fTokenTime to set
	 */
	public void setfTokenTime(Date fTokenTime) {
		this.fTokenTime = fTokenTime;
	}
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
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		if(mobile != null && mobile.contains("+"))
			this.mobile = "+"+mobile.replaceAll("[^a-zA-Z0-9]", "");
		else if(mobile != null)
			this.mobile = mobile.replaceAll("[^a-zA-Z0-9]", "");
		else
			this.mobile = mobile;
	}
	/**
	 * @return the paymentReminderEnable
	 */
	public Boolean getPaymentReminderEnable() {
		return paymentReminderEnable;
	}
	/**
	 * @param paymentReminderEnable the paymentReminderEnable to set
	 */
	public void setPaymentReminderEnable(Boolean paymentReminderEnable) {
		this.paymentReminderEnable = paymentReminderEnable;
	}
	/**
	 * @return the lunchReminderEnable
	 */
	public Boolean getLunchReminderEnable() {
		return lunchReminderEnable;
	}
	/**
	 * @param lunchReminderEnable the lunchReminderEnable to set
	 */
	public void setLunchReminderEnable(Boolean lunchReminderEnable) {
		this.lunchReminderEnable = lunchReminderEnable;
	}
	/**
	 * @return the emailIsSubscribe
	 */
	public Boolean getEmailIsSubscribe() {
		return emailIsSubscribe;
	}
	/**
	 * @param emailIsSubscribe the emailIsSubscribe to set
	 */
	public void setEmailIsSubscribe(Boolean emailIsSubscribe) {
		this.emailIsSubscribe = emailIsSubscribe;
	}
	/**
	 * @return the partner_id
	 */
	public String getPartner_id() {
		return partner_id;
	}
	/**
	 * @param partner_id the partner_id to set
	 */
	public void setPartner_id(String partner_id) {
		this.partner_id = partner_id;
	}
	/**
	 * @return the district_id
	 */
	public Long getDistrict_id() {
		return district_id;
	}
	/**
	 * @param district_id the district_id to set
	 */
	public void setDistrict_id(Long district_id) {
		this.district_id = district_id;
	}
	/**
	 * @return the partnerName
	 */
	public String getPartnerName() {
		return partnerName;
	}
	/**
	 * @param partnerName the partnerName to set
	 */
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}	
	
}
