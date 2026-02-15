package com.mealManage.mealmodel.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "ParentUser_v2", indexes = {
		@Index(columnList = "parentAltEmail"),
	    @Index(columnList="isActive")})
/**This entity used for student's parent user***/
public class ParentUser extends User implements Serializable{
	
	private static final long serialVersionUID = 5684883929287186311L;
	@Column(unique=true, nullable = false)
	private String userName;
	private String parentAltEmail;
	private Boolean isParentRegistered = false;
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return (userName != null ? userName.trim() : userName);
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = (userName != null ? userName.trim() : userName);
	}
	/**
	 * @return the parentAltEmail
	 */
	public String getParentAltEmail() {
		return (parentAltEmail != null ? parentAltEmail.trim() : parentAltEmail);
	}
	/**
	 * @param parentAltEmail the parentAltEmail to set
	 */
	public void setParentAltEmail(String parentAltEmail) {
		this.parentAltEmail = (parentAltEmail != null ? parentAltEmail.trim() : parentAltEmail);
	}
	/**
	 * @return the isParentRegistered
	 */
	public Boolean getIsParentRegistered() {
		return isParentRegistered;
	}
	/**
	 * @param isParentRegistered the isParentRegistered to set
	 */
	public void setIsParentRegistered(Boolean isParentRegistered) {
		this.isParentRegistered = isParentRegistered;
	}
}
