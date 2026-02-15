package com.mealManage.mealmodel.user;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;

@MappedSuperclass
/**This abstract class having all the properties which used for all type of users**/
public abstract class User extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "UserIdGenerator")
	@TableGenerator(table = "USER_SEQUENCES", name = "UserIdGenerator")
	private Long userId;
	
	private String firstName;
	private String lastName;
	private String mobileNo;
	@NotNull
	private String role;
	private Boolean isActive=true;
	@Transient
	private Long displayRecId;
	private String image;
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
	 * @return the mobileNo
	 */
	public String getMobileNo() {
		return mobileNo;
	}
	/**
	 * @param mobileNo the mobileNo to set
	 */
	public void setMobileNo(String mobileNo) {
		if(mobileNo != null){
			mobileNo = mobileNo.replaceAll("[^a-zA-Z0-9]", "");
			if(mobileNo.length() < 10)
				this.mobileNo = null;
			else if(mobileNo.length() > 10)
				this.mobileNo = "+"+mobileNo;
			else
				this.mobileNo = mobileNo;
		}else
			this.mobileNo = mobileNo;
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
	 * @return the displayRecId
	 */
	public Long getDisplayRecId() {
		return userId;
	}
	/**
	 * @param displayRecId the displayRecId to set
	 */
	public void setDisplayRecId(Long displayRecId) {
		this.displayRecId = userId;
	}
	/**
	 * @return the image
	 */
	public String getImage() {
		if(image == null || image.trim().isEmpty())
			image = "https://s3.amazonaws.com/mealmanage-prod/SampleFiles/studentIcon.png";
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
		
}
