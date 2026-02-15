package com.mealManage.response;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.mealManage.mealmodel.packages.PickupAuthorizedResp;

/**This POJO used for display the BCAC subscription packages with details**/
public class BCACSubscriptions {
	
	private String stdFName;
	private String stdLName;
	private String grade;
	private String packageName;
	private Date checkIn;
	private Date checkOut;
	private String pickupBy;
	private Long recId;
	private Long bcacAuditID;
	private Long stdRecId;
	private List<PickupAuthorizedResp> pickupPersons;
	private Long trxRecId;
	private Boolean isPaid;
	
	public BCACSubscriptions() {
		// TODO Auto-generated constructor stub
	}
	
	public BCACSubscriptions(Object[] obj) {
		super();
		this.stdFName = obj[0]!=null?(String)obj[0]:null;
		this.stdLName = obj[1]!=null?(String)obj[1]:null;
		this.grade = obj[2]!=null?(String)obj[2]:null;
		this.packageName = obj[3]!=null?(String)obj[3]:null;
		this.recId = obj[4]!=null?((BigInteger)obj[4]).longValue():null;
		this.bcacAuditID = obj[5]!=null?((BigInteger)obj[5]).longValue():null;
		this.checkIn =  obj[6]!=null?(Timestamp)obj[6]:null;
		this.checkOut =  obj[7]!=null?(Timestamp)obj[7]:null;
		this.pickupBy =  obj[8]!=null?(String)obj[8]:null;
		this.stdRecId = obj[9]!=null?((BigInteger)obj[9]).longValue():null;
		this.trxRecId = obj[10]!=null?((BigInteger)obj[10]).longValue():null;
		this.isPaid = obj[11] != null ? (Boolean)obj[11] : false;
	}
	/**
	 * @return the stdFName
	 */
	public String getStdFName() {
		return stdFName;
	}
	/**
	 * @param stdFName the stdFName to set
	 */
	public void setStdFName(String stdFName) {
		this.stdFName = stdFName;
	}
	/**
	 * @return the stdLName
	 */
	public String getStdLName() {
		return stdLName;
	}
	/**
	 * @param stdLName the stdLName to set
	 */
	public void setStdLName(String stdLName) {
		this.stdLName = stdLName;
	}
	/**
	 * @return the grade
	 */
	public String getGrade() {
		return grade;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(String grade) {
		this.grade = grade;
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
	/**
	 * @return the checkIn
	 */
	public Date getCheckIn() {
		return checkIn;
	}
	/**
	 * @param checkIn the checkIn to set
	 */
	public void setCheckIn(Date checkIn) {
		this.checkIn = checkIn;
	}
	/**
	 * @return the checkOut
	 */
	public Date getCheckOut() {
		return checkOut;
	}
	/**
	 * @param checkOut the checkOut to set
	 */
	public void setCheckOut(Date checkOut) {
		this.checkOut = checkOut;
	}
	/**
	 * @return the pickupBy
	 */
	public String getPickupBy() {
		return pickupBy;
	}
	/**
	 * @param pickupBy the pickupBy to set
	 */
	public void setPickupBy(String pickupBy) {
		this.pickupBy = pickupBy;
	}
	
	/**
	 * @return the recId
	 */
	public Long getRecId() {
		return recId;
	}

	/**
	 * @param recId the recId to set
	 */
	public void setRecId(Long recId) {
		this.recId = recId;
	}

	/**
	 * @return the bcacAuditID
	 */
	public Long getBcacAuditID() {
		return bcacAuditID;
	}
	/**
	 * @param bcacAuditID the bcacAuditID to set
	 */
	public void setBcacAuditID(Long bcacAuditID) {
		this.bcacAuditID = bcacAuditID;
	}

	public Long getStdRecId() {
		return stdRecId;
	}

	public void setStdRecId(Long stdRecId) {
		this.stdRecId = stdRecId;
	}

	/**
	 * @return the pickupPersons
	 */
	public List<PickupAuthorizedResp> getPickupPersons() {
		return pickupPersons;
	}

	/**
	 * @param pickupPersons the pickupPersons to set
	 */
	public void setPickupPersons(List<PickupAuthorizedResp> pickupPersons) {
		this.pickupPersons = pickupPersons;
	}
	
	/**
	 * @return the trxRecId
	 */
	public Long getTrxRecId() {
		return trxRecId;
	}

	/**
	 * @param trxRecId the trxRecId to set
	 */
	public void setTrxRecId(Long trxRecId) {
		this.trxRecId = trxRecId;
	}

	/**
	 * @return the isPaid
	 */
	public Boolean getIsPaid() {
		return isPaid;
	}

	/**
	 * @param isPaid the isPaid to set
	 */
	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}
	
}
