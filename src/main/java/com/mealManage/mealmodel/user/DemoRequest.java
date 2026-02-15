package com.mealManage.mealmodel.user;


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
import javax.persistence.Transient;

import com.mealManage.mealmodel.school.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "DemoRequest", indexes = {
		@Index(columnList = "firstName"),
	    @Index(columnList="lastName"),
	    @Index(columnList = "schoolName"),
	    @Index(columnList = "emailAddress")})
/**This entity used for Demo Request**/
public class DemoRequest extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 5684883929287186311L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "requestId", updatable = false, nullable = false)
	private Long requestId;
	@Column(nullable = false)
	private String firstName = "";
	@Column(nullable = false)
	private String lastName = "";
	@Transient
	private String name;
	private String schoolName;
	@Column(nullable = false)
	private String emailAddress;
	private String city;
	private String state;
	private String country;
	private String mobileNo;
	private String requestingFor;
	private Long statusCodeId;
	private String dateOfAction;
	private String followUpDate;
	private String comments;
	@Column(columnDefinition = "boolean default true")
	private Boolean active=true;
	@Transient
	private String updatedBy;
	@Transient
	private Date updatedOn;
	/**
	 * @return the requestId
	 */
	public Long getRequestId() {
		return requestId;
	}
	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		if(firstName != null)
			return firstName.trim();
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
	 * @return the name
	 */
	public String getName() {
		return firstName+" "+lastName;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		if(name.split(" ").length > 1){
			firstName = name.split(" ")[0].trim();
			lastName = name.replaceFirst(firstName, "").trim();
		}else{
			firstName = name.trim();
			lastName = "";
		}
		
	}
	/**
	 * @return the schoolName
	 */
	public String getSchoolName() {
		return schoolName;
	}
	/**
	 * @param schoolName the schoolName to set
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
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
	 * @return the requestingFor
	 */
	public String getRequestingFor() {
		return requestingFor;
	}
	/**
	 * @param requestingFor the requestingFor to set
	 */
	public void setRequestingFor(String requestingFor) {
		this.requestingFor = requestingFor;
	}

	public Long getStatusCodeId() {
		return statusCodeId;
	}

	public void setStatusCodeId(Long statusCodeId) {
		this.statusCodeId = statusCodeId;
	}

	public String getDateOfAction() {
		return dateOfAction;
	}

	public void setDateOfAction(String dateOfAction) {
		this.dateOfAction = dateOfAction;
	}

	public String getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(String followUpDate) {
		this.followUpDate = followUpDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getUpdatedBy() {
		return getModifiedBy() != null ? getModifiedBy() : (getCreatedBy() != null ? getCreatedBy() : (getFirstName()+" "+getLastName()));
	}

	public Date getUpdatedOn() {
		return getModifiedOn() != null ? getModifiedOn() : getCreatedOn();
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}