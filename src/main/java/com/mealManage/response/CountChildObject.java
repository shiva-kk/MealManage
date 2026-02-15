package com.mealManage.response;

import java.util.Map;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;

@SuppressWarnings("deprecation")
@JsonSerialize(include = Inclusion.NON_NULL)
public class CountChildObject {
	
	private Long allGradesCount;
	private Map<String, Long> countByGrades;
	private Double allGradesPaidAmount;
	private Double allGradesNotPaidAmount;
	private Map<String, Double> notPaidAmountByGrades;
	private Map<String, Double> paidAmountByGrades;
	/**
	 * @return the allGradesCount
	 */
	public Long getAllGradesCount() {
		return allGradesCount;
	}
	/**
	 * @param allGradesCount the allGradesCount to set
	 */
	public void setAllGradesCount(Long allGradesCount) {
		this.allGradesCount = allGradesCount;
	}
	/**
	 * @return the countByGrades
	 */
	public Map<String, Long> getCountByGrades() {
		return countByGrades;
	}
	/**
	 * @param countByGrades the countByGrades to set
	 */
	public void setCountByGrades(Map<String, Long> countByGrades) {
		this.countByGrades = countByGrades;
	}
	/**
	 * @return the allGradesPaidAmount
	 */
	public Double getAllGradesPaidAmount() {
		return allGradesPaidAmount;
	}
	/**
	 * @param allGradesPaidAmount the allGradesPaidAmount to set
	 */
	public void setAllGradesPaidAmount(Double allGradesPaidAmount) {
		this.allGradesPaidAmount = allGradesPaidAmount;
	}
	/**
	 * @return the allGradesNotPaidAmount
	 */
	public Double getAllGradesNotPaidAmount() {
		return allGradesNotPaidAmount;
	}
	/**
	 * @param allGradesNotPaidAmount the allGradesNotPaidAmount to set
	 */
	public void setAllGradesNotPaidAmount(Double allGradesNotPaidAmount) {
		this.allGradesNotPaidAmount = allGradesNotPaidAmount;
	}
	/**
	 * @return the notPaidAmountByGrades
	 */
	public Map<String, Double> getNotPaidAmountByGrades() {
		return notPaidAmountByGrades;
	}
	/**
	 * @param notPaidAmountByGrades the notPaidAmountByGrades to set
	 */
	public void setNotPaidAmountByGrades(Map<String, Double> notPaidAmountByGrades) {
		this.notPaidAmountByGrades = notPaidAmountByGrades;
	}
	/**
	 * @return the paidAmountByGrades
	 */
	public Map<String, Double> getPaidAmountByGrades() {
		return paidAmountByGrades;
	}
	/**
	 * @param paidAmountByGrades the paidAmountByGrades to set
	 */
	public void setPaidAmountByGrades(Map<String, Double> paidAmountByGrades) {
		this.paidAmountByGrades = paidAmountByGrades;
	}

}
