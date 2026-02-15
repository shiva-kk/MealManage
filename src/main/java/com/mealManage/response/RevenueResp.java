package com.mealManage.response;

import java.util.List;
import java.util.Map;

public class RevenueResp {
	
	private Map<String, Map<String, Integer>> reimbMeal;
	private Map<String, Map<String, Integer>> nonReimbMeal;
	private Map<String, Map<String, Map<String, Double>>> revenueFromMeal;
	private Map<String, Map<String, Map<String, Double>>> revenueFromAlaCarte;
	private Map<String, Map<String, Double>> depositFromPayment;
	private Map<String, Double> salesSummary;
	private List<Object[]> revenueByLoc;
	
	/**
	 * @return the reimbMeal
	 */
	public Map<String, Map<String, Integer>> getReimbMeal() {
		return reimbMeal;
	}
	/**
	 * @param reimbMeal the reimbMeal to set
	 */
	public void setReimbMeal(Map<String, Map<String, Integer>> reimbMeal) {
		this.reimbMeal = reimbMeal;
	}
	/**
	 * @return the nonReimbMeal
	 */
	public Map<String, Map<String, Integer>> getNonReimbMeal() {
		return nonReimbMeal;
	}
	/**
	 * @param nonReimbMeal the nonReimbMeal to set
	 */
	public void setNonReimbMeal(Map<String, Map<String, Integer>> nonReimbMeal) {
		this.nonReimbMeal = nonReimbMeal;
	}
	/**
	 * @return the revenueFromMeal
	 */
	public Map<String, Map<String, Map<String, Double>>> getRevenueFromMeal() {
		return revenueFromMeal;
	}
	/**
	 * @param revenueFromMeal the revenueFromMeal to set
	 */
	public void setRevenueFromMeal(Map<String, Map<String, Map<String, Double>>> revenueFromMeal) {
		this.revenueFromMeal = revenueFromMeal;
	}
	/**
	 * @return the revenueFromAlaCarte
	 */
	public Map<String, Map<String, Map<String, Double>>> getRevenueFromAlaCarte() {
		return revenueFromAlaCarte;
	}
	/**
	 * @param revenueFromAlaCarte the revenueFromAlaCarte to set
	 */
	public void setRevenueFromAlaCarte(Map<String, Map<String, Map<String, Double>>> revenueFromAlaCarte) {
		this.revenueFromAlaCarte = revenueFromAlaCarte;
	}
	/**
	 * @return the depositFromPayment
	 */
	public Map<String, Map<String, Double>> getDepositFromPayment() {
		return depositFromPayment;
	}
	/**
	 * @param depositFromPayment the depositFromPayment to set
	 */
	public void setDepositFromPayment(Map<String, Map<String, Double>> depositFromPayment) {
		this.depositFromPayment = depositFromPayment;
	}
	/**
	 * @return the salesSummary
	 */
	public Map<String, Double> getSalesSummary() {
		return salesSummary;
	}
	/**
	 * @param salesSummary the salesSummary to set
	 */
	public void setSalesSummary(Map<String, Double> salesSummary) {
		this.salesSummary = salesSummary;
	}
	/**
	 * @return the revenueByLoc
	 */
	public List<Object[]> getRevenueByLoc() {
		return revenueByLoc;
	}
	/**
	 * @param revenueByLoc the revenueByLoc to set
	 */
	public void setRevenueByLoc(List<Object[]> revenueByLoc) {
		this.revenueByLoc = revenueByLoc;
	}
	
}
