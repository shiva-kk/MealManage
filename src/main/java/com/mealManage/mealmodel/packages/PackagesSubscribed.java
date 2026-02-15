package com.mealManage.mealmodel.packages;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**This class used for Active Subscribed packages**/
public class PackagesSubscribed {
	
	private Long studentRecId;
	private Date startDt;
	private Date endDt;
	private String frequency;
	private Long packageId;
	private String packageName;
	public PackagesSubscribed() {
		// TODO Auto-generated constructor stub
	}
	
	public PackagesSubscribed(Object[] obj) {
		super();
		this.studentRecId = obj[0]!=null?((BigInteger)obj[0]).longValue():null;
		this.startDt = obj[1]!=null?(Timestamp)obj[1]:null;
		this.endDt = obj[2]!=null?(Timestamp)obj[2]:null;
		this.frequency = obj[3]!=null?(String)obj[3]:null;
		this.packageId = obj[4]!=null?((BigInteger)obj[4]).longValue():null;
		this.packageName = obj[5]!=null?(String)obj[5]:null;
	}

	/**
	 * @return the studentRecId
	 */
	public Long getStudentRecId() {
		return studentRecId;
	}
	/**
	 * @param studentRecId the studentRecId to set
	 */
	public void setStudentRecId(Long studentRecId) {
		this.studentRecId = studentRecId;
	}
	/**
	 * @return the startDt
	 */
	public Date getStartDt() {
		return startDt;
	}
	/**
	 * @param startDt the startDt to set
	 */
	public void setStartDt(Date startDt) {
		this.startDt = startDt;
	}
	/**
	 * @return the endDt
	 */
	public Date getEndDt() {
		return endDt;
	}
	/**
	 * @param endDt the endDt to set
	 */
	public void setEndDt(Date endDt) {
		this.endDt = endDt;
	}
	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	/**
	 * @return the packageId
	 */
	public Long getPackageId() {
		return packageId;
	}
	/**
	 * @param packageId the packageId to set
	 */
	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}
	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}
	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
}
