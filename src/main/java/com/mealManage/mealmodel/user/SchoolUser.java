package com.mealManage.mealmodel.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "SchoolUser_v2", indexes = {
	    @Index(columnList="isActive")})
/**This entity having the user name properties and extending to Users entity**/
public class SchoolUser extends User implements Serializable{
	
	private static final long serialVersionUID = 6034024176557072678L;
	
	@Column(unique=true, nullable=false)
	private String username;
	private Boolean isVerified = false;
	@NotNull
	private Boolean isPrimaryUser = false; //It should be true when admin account as the primary account.
	//It should be default false and when user want to unsubscribe for general notification then it should be true.
	@NotNull
	private Boolean isUnsubscribeGenNotif = false; 
	@NotNull
	private Boolean isPaymentRegister = false;
	private String pin;
	//private Boolean isPasscodeAuth;

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
	 * @return the isVerified
	 */
	public Boolean getIsVerified() {
		return isVerified;
	}

	/**
	 * @param isVerified the isVerified to set
	 */
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	/**
	 * @return the isPrimaryUser
	 */
	public Boolean getIsPrimaryUser() {
		return isPrimaryUser;
	}

	/**
	 * @param isPrimaryUser the isPrimaryUser to set
	 */
	public void setIsPrimaryUser(Boolean isPrimaryUser) {
		this.isPrimaryUser = isPrimaryUser;
	}

	/**
	 * @return the isUnsubscribeGenNotif
	 */
	public Boolean getIsUnsubscribeGenNotif() {
		return isUnsubscribeGenNotif;
	}

	/**
	 * @param isUnsubscribeGenNotif the isUnsubscribeGenNotif to set
	 */
	public void setIsUnsubscribeGenNotif(Boolean isUnsubscribeGenNotif) {
		this.isUnsubscribeGenNotif = isUnsubscribeGenNotif;
	}

	/**
	 * @return the isPaymentRegister
	 */
	public Boolean getIsPaymentRegister() {
		return isPaymentRegister;
	}

	/**
	 * @param isPaymentRegister the isPaymentRegister to set
	 */
	public void setIsPaymentRegister(Boolean isPaymentRegister) {
		this.isPaymentRegister = isPaymentRegister;
	}

	/**
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}

	/**
	 * @param pin the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}	
}
