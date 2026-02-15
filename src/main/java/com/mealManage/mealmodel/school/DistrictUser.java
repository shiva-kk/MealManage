package com.mealManage.mealmodel.school;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.user.User;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "DistrictUser")
public class DistrictUser extends User implements Serializable  {

	private static final long serialVersionUID = 1L;
	@Column(unique=true, nullable=false)
    private String username;
    @NotNull
    private Boolean isPrimaryUser = false;
    @NotNull
    private Boolean isVerified = false;
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
    
}
