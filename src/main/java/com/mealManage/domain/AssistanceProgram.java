package com.mealManage.domain;

public class AssistanceProgram {
	
	private Long id;
	private String name;
	private String accronym;
	private String caseNumber;
	private Boolean isApplicable;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the accronym
	 */
	public String getAccronym() {
		return accronym;
	}
	/**
	 * @param accronym the accronym to set
	 */
	public void setAccronym(String accronym) {
		this.accronym = accronym;
	}
	/**
	 * @return the caseNumber
	 */
	public String getCaseNumber() {
		return caseNumber;
	}
	/**
	 * @param caseNumber the caseNumber to set
	 */
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	/**
	 * @return the isApplicable
	 */
	public Boolean getIsApplicable() {
		return isApplicable;
	}
	/**
	 * @param isApplicable the isApplicable to set
	 */
	public void setIsApplicable(Boolean isApplicable) {
		this.isApplicable = isApplicable;
	}
}
