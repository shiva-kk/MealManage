package com.mealManage.mealmodel.packages;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name="TYPE")
@Table(name="PickupAuthorized")
public class PickupAuthorized implements Serializable{

	private static final long serialVersionUID = 7409053341687813061L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="authorizedId", nullable = false, updatable = false)
	private Long authorizedId;
	private String firstName;
	private String lastName;
	private String phoneNo;
	private String relation;
	private Long stdRecId;
	private Boolean isActive;
	
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
