package com.mealManage.response;

public class IncomeResp {
	
	private Double cFullPrice;
	private Double cRedPrice;
	private Double ppFullPrice;
	private Double ppRedPrice;
	private Double cpFullPrice;
	private Double cpRedPrice;
	/*private Double rmStaffPrice;
	private Double amStd;
	private Double amStaff;
	private Double alaCStd;
	private Double alaCStaf;*/
	private Double ccAm;
	private Double ppAm;
	private Double cpAm;
	private Double ccAlc;
	private Double ppAlc;
	private Double cpAlc;
	//private Double alcDebit;
	private String Date;
	public IncomeResp() {
		// TODO Auto-generated constructor stub
	}
	
	public IncomeResp(Double cFullPrice, Double cRedPrice, Double ppFullPrice, Double ppRedPrice, Double cpFullPrice,
			Double cpRedPrice, Double ccAm, Double ppAm, Double cpAm, Double ccAlc, Double ppAlc, Double cpAlc, String date) {
		super();
		this.cFullPrice = cFullPrice;
		this.cRedPrice = cRedPrice;
		this.ppFullPrice = ppFullPrice;
		this.ppRedPrice = ppRedPrice;
		this.cpFullPrice = cpFullPrice;
		this.cpRedPrice = cpRedPrice;
		this.ccAm = ccAm;
		this.ppAm = ppAm;
		this.cpAm = cpAm;
		this.ccAlc = ccAlc;
		this.ppAlc = ppAlc;
		this.cpAlc = cpAlc;
		//this.alcDebit = alcDebit;
		Date = date;
	}

	/**
	 * @return the cFullPrice
	 */
	public Double getcFullPrice() {
		return cFullPrice;
	}
	/**
	 * @param cFullPrice the cFullPrice to set
	 */
	public void setcFullPrice(Double cFullPrice) {
		this.cFullPrice = cFullPrice;
	}
	/**
	 * @return the cRedPrice
	 */
	public Double getcRedPrice() {
		return cRedPrice;
	}
	/**
	 * @param cRedPrice the cRedPrice to set
	 */
	public void setcRedPrice(Double cRedPrice) {
		this.cRedPrice = cRedPrice;
	}
	/**
	 * @return the ppFullPrice
	 */
	public Double getPpFullPrice() {
		return ppFullPrice;
	}
	/**
	 * @param ppFullPrice the ppFullPrice to set
	 */
	public void setPpFullPrice(Double ppFullPrice) {
		this.ppFullPrice = ppFullPrice;
	}
	/**
	 * @return the ppRedPrice
	 */
	public Double getPpRedPrice() {
		return ppRedPrice;
	}
	/**
	 * @param ppRedPrice the ppRedPrice to set
	 */
	public void setPpRedPrice(Double ppRedPrice) {
		this.ppRedPrice = ppRedPrice;
	}
	/**
	 * @return the cpFullPrice
	 */
	public Double getCpFullPrice() {
		return cpFullPrice;
	}
	/**
	 * @param cpFullPrice the cpFullPrice to set
	 */
	public void setCpFullPrice(Double cpFullPrice) {
		this.cpFullPrice = cpFullPrice;
	}
	/**
	 * @return the cpRedPrice
	 */
	public Double getCpRedPrice() {
		return cpRedPrice;
	}
	/**
	 * @param cpRedPrice the cpRedPrice to set
	 */
	public void setCpRedPrice(Double cpRedPrice) {
		this.cpRedPrice = cpRedPrice;
	}
	
	

	/**
	 * @return the ccAm
	 */
	public Double getCcAm() {
		return ccAm;
	}

	/**
	 * @param ccAm the ccAm to set
	 */
	public void setCcAm(Double ccAm) {
		this.ccAm = ccAm;
	}

	/**
	 * @return the ppAm
	 */
	public Double getPpAm() {
		return ppAm;
	}

	/**
	 * @param ppAm the ppAm to set
	 */
	public void setPpAm(Double ppAm) {
		this.ppAm = ppAm;
	}

	/**
	 * @return the cpAm
	 */
	public Double getCpAm() {
		return cpAm;
	}

	/**
	 * @param cpAm the cpAm to set
	 */
	public void setCpAm(Double cpAm) {
		this.cpAm = cpAm;
	}

	/**
	 * @return the ccAlc
	 */
	public Double getCcAlc() {
		return ccAlc;
	}

	/**
	 * @param ccAlc the ccAlc to set
	 */
	public void setCcAlc(Double ccAlc) {
		this.ccAlc = ccAlc;
	}

	/**
	 * @return the ppAlc
	 */
	public Double getPpAlc() {
		return ppAlc;
	}

	/**
	 * @param ppAlc the ppAlc to set
	 */
	public void setPpAlc(Double ppAlc) {
		this.ppAlc = ppAlc;
	}

	/**
	 * @return the cpAlc
	 */
	public Double getCpAlc() {
		return cpAlc;
	}

	/**
	 * @param cpAlc the cpAlc to set
	 */
	public void setCpAlc(Double cpAlc) {
		this.cpAlc = cpAlc;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return Date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		Date = date;
	}
	
}
