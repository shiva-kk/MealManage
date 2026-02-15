package com.mealManage.domain;

import java.util.Date;

public class EligCertReq {
	
	private String studentId;
	private String name;
	private String prgSource;
	private Date certDate;
	/**
	 * @return the studentId
	 */
	public String getStudentId() {
		return studentId;
	}
	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(String studentId) {
		this.studentId = studentId;
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
	 * @return the prgSource
	 */
	public String getPrgSource() {
		return prgSource;
	}
	/**
	 * @param prgSource the prgSource to set
	 */
	public void setPrgSource(String prgSource) {
		this.prgSource = prgSource;
	}
	/**
	 * @return the certDate
	 */
	public Date getCertDate() {
		return certDate;
	}
	/**
	 * @param certDate the certDate to set
	 */
	public void setCertDate(Date certDate) {
		this.certDate = certDate;
	}
	
}
