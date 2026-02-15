package com.mealManage.menu.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class EligibilityCode implements Serializable{
	
	private static final long serialVersionUID = 120640370652653795L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "eligCodeId", updatable = false, nullable = false)
	private Long eligCodeId;
	@NotNull
	@Column(name="code",nullable=false, unique=true)
	private String code;
	private String codeDesc;
	private boolean isFreeElig = false;
	private boolean isRedElig = false;
	private boolean isActive = true;
	private boolean isMMApp = false;
	/**
	 * @return the eligCodeId
	 */
	public Long getEligCodeId() {
		return eligCodeId;
	}
	/**
	 * @param eligCodeId the eligCodeId to set
	 */
	public void setEligCodeId(Long eligCodeId) {
		this.eligCodeId = eligCodeId;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * @return the codeDesc
	 */
	public String getCodeDesc() {
		return codeDesc;
	}
	/**
	 * @param codeDesc the codeDesc to set
	 */
	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}
	/**
	 * @return the isFreeElig
	 */
	public boolean isFreeElig() {
		return isFreeElig;
	}
	/**
	 * @param isFreeElig the isFreeElig to set
	 */
	public void setFreeElig(boolean isFreeElig) {
		this.isFreeElig = isFreeElig;
	}
	/**
	 * @return the isRedElig
	 */
	public boolean isRedElig() {
		return isRedElig;
	}
	/**
	 * @param isRedElig the isRedElig to set
	 */
	public void setRedElig(boolean isRedElig) {
		this.isRedElig = isRedElig;
	}
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the isMMApp
	 */
	public boolean isMMApp() {
		return isMMApp;
	}
	/**
	 * @param isMMApp the isMMApp to set
	 */
	public void setMMApp(boolean isMMApp) {
		this.isMMApp = isMMApp;
	}
}
