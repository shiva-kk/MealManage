package com.mealManage.domain;

public class HouseholdIncompleteApp {
	
	private Long id;
	private String name;
	private String description;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
