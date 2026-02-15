package com.mealManage.domain;

import com.mealManage.mealmodel.school.SchoolGrades;

public class GradesInfo {
	
	private Integer displayOrder;
	private SchoolGrades value;
	private String label;
	/**
	 * @return the displayOrder
	 */
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	/**
	 * @param displayOrder the displayOrder to set
	 */
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	/**
	 * @return the value
	 */
	public SchoolGrades getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(SchoolGrades value) {
		this.value = value;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
}
