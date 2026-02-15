package com.mealManage.mealmodel.user;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "ParentDeviceInfo_v2", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"deviceDetails", "username"}) })
/**This entity used for parent user device registration***/
public class ParentDeviceInfo extends BaseEntity implements Serializable{

	private static final long serialVersionUID = -1443649969445390578L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "parentDeviceId", updatable = false, nullable = false)
	private Long parentDeviceId;
	@NotNull
	private String deviceDetails;
	@NotNull
	private String username;
	private String deviceIP;
	/**
	 * @return the deviceDetails
	 */
	public String getDeviceDetails() {
		return deviceDetails;
	}
	/**
	 * @param deviceDetails the deviceDetails to set
	 */
	public void setDeviceDetails(String deviceDetails) {
		this.deviceDetails = deviceDetails;
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
	 * @return the deviceIP
	 */
	public String getDeviceIP() {
		return deviceIP;
	}
	/**
	 * @param deviceIP the deviceIP to set
	 */
	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}
	/**
	 * @return the parentDeviceId
	 */
	public Long getParentDeviceId() {
		return parentDeviceId;
	}
	/**
	 * @param parentDeviceId the parentDeviceId to set
	 */
	public void setParentDeviceId(Long parentDeviceId) {
		this.parentDeviceId = parentDeviceId;
	}
	
}
