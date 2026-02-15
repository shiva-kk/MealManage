package com.mealManage.mealmodel.school;

import java.util.Date;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@MappedSuperclass
/**This base entity having all the properties which are same for all entities**/
public abstract class BaseEntity {
	/*@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ConfirmationCodeGenerator")
	@TableGenerator(table = "SEQUENCES", name = "ConfirmationCodeGenerator")
	private Long id;*/
    
	@Transient
	//@JsonIgnore
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String loggedUser;
	//@JsonIgnore
	private String createdBy;
	//@JsonIgnore
	private Date createdOn;
	@JsonIgnore
	private String modifiedBy;
	@JsonIgnore
	private Date modifiedOn;
	
	/**
	 * @return the loggedUser
	 */
	public String getLoggedUser() {
		return loggedUser;
	}
	/**
	 * @param loggedUser the loggedUser to set
	 */
	public void setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}
	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}
	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	/**
	 * @return the modifiedOn
	 */
	public Date getModifiedOn() {
		return modifiedOn;
	}
	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
}