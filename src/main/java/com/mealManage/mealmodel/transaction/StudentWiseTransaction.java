package com.mealManage.mealmodel.transaction;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.menu.entities.MenuItem;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "StudentWiseTransactions", indexes = {
	    @Index(columnList="studentUser_userId")})
/**This entity having the user name properties and extending to Users entity**/
public class StudentWiseTransaction implements Serializable{
	
	private static final long serialVersionUID = 4218829217317756016L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	
	@ManyToOne
	@NotNull
	private StudentUser studentUser;
	private String studentFName;
	private String studentLName;
	@NotNull
	private Double transactionAmount; //in school country currency
	@NotNull
	private Double finalBalance; //in school country currency
	@Transient
	private Long studentRecId;
	@Enumerated(EnumType.STRING)
	private SchoolGrades grade;
	private Integer eligStatus; //0 for free meals eligibility, 1 for reduced price and 2 for regular price eligibility.
	@NotNull
	private Boolean isEmrgLunchServe = false;
	@NotNull
	private Boolean isPosted = true; //This flag false means purchase transaction soft deleted (in-active)
	@ManyToOne
	private EventInfo eventInfo;
	private String mealType; //Additional / ALaCarte / Regular
	private Double prepaidAmt;
	private Double ccAmt; //Cash/Check
	private Double chargedAmt;
	@ManyToMany(cascade = CascadeType.MERGE, fetch= FetchType.LAZY)
	@JoinTable(
	        name = "StudentWiseTransactions_menuItem", 
	        joinColumns = { @JoinColumn(name = "studentWiseTransactionId") }, 
	        inverseJoinColumns = { @JoinColumn(name = "itemId") }
	    )
	private Set<MenuItem> items = new HashSet<>();
	
	
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
	 * @return the studentUser
	 */
	public StudentUser getStudentUser() {
		return studentUser;
	}
	/**
	 * @param studentUser the studentUser to set
	 */
	public void setStudentUser(StudentUser studentUser) {
		this.studentUser = studentUser;
	}
	/**
	 * @return the studentFName
	 */
	public String getStudentFName() {
		if(studentFName != null)
			return studentFName.trim();
		return studentFName;
	}
	/**
	 * @param studentFName the studentFName to set
	 */
	public void setStudentFName(String studentFName) {
		this.studentFName = studentFName;
	}
	/**
	 * @return the studentLName
	 */
	public String getStudentLName() {
		if(studentLName != null)
			return studentLName.trim();
		return studentLName;
	}
	/**
	 * @param studentLName the studentLName to set
	 */
	public void setStudentLName(String studentLName) {
		this.studentLName = studentLName;
	}
	/**
	 * @return the transactionAmount
	 */
	public Double getTransactionAmount() {
		return transactionAmount;
	}
	/**
	 * @param transactionAmount the transactionAmount to set
	 */
	public void setTransactionAmount(Double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	/**
	 * @return the finalBalance
	 */
	public Double getFinalBalance() {
		return finalBalance;
	}
	/**
	 * @param finalBalance the finalBalance to set
	 */
	public void setFinalBalance(Double finalBalance) {
		this.finalBalance = finalBalance;
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
	 * @return the grade
	 */
	public SchoolGrades getGrade() {
		return grade;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(SchoolGrades grade) {
		this.grade = grade;
	}
	/**
	 * @return the eligStatus
	 */
	public Integer getEligStatus() {
		return eligStatus;
	}
	/**
	 * @param eligStatus the eligStatus to set
	 */
	public void setEligStatus(Integer eligStatus) {
		this.eligStatus = eligStatus;
	}
	/**
	 * @return the isEmrgLunchServe
	 */
	public boolean isEmrgLunchServe() {
		return isEmrgLunchServe;
	}
	/**
	 * @param isEmrgLunchServe the isEmrgLunchServe to set
	 */
	public void setEmrgLunchServe(boolean isEmrgLunchServe) {
		this.isEmrgLunchServe = isEmrgLunchServe;
	}
	/**
	 * @return the isPosted
	 */
	public Boolean getIsPosted() {
		return isPosted;
	}
	/**
	 * @param isPosted the isPosted to set
	 */
	public void setIsPosted(Boolean isPosted) {
		this.isPosted = isPosted;
	}
	/**
	 * @return the eventInfo
	 */
	public EventInfo getEventInfo() {
		return eventInfo;
	}
	/**
	 * @param eventInfo the eventInfo to set
	 */
	public void setEventInfo(EventInfo eventInfo) {
		this.eventInfo = eventInfo;
	}
	/**
	 * @return the mealType
	 */
	public String getMealType() {
		return mealType;
	}
	/**
	 * @param mealType the mealType to set
	 */
	public void setMealType(String mealType) {
		this.mealType = mealType;
	}
	/**
	 * @return the prepaidAmt
	 */
	public Double getPrepaidAmt() {
		return prepaidAmt;
	}
	/**
	 * @param prepaidAmt the prepaidAmt to set
	 */
	public void setPrepaidAmt(Double prepaidAmt) {
		this.prepaidAmt = prepaidAmt;
	}
	/**
	 * @return the ccAmt
	 */
	public Double getCcAmt() {
		return ccAmt;
	}
	/**
	 * @param ccAmt the ccAmt to set
	 */
	public void setCcAmt(Double ccAmt) {
		this.ccAmt = ccAmt;
	}
	/**
	 * @return the chargedAmt
	 */
	public Double getChargedAmt() {
		return chargedAmt;
	}
	/**
	 * @param chargedAmt the chargedAmt to set
	 */
	public void setChargedAmt(Double chargedAmt) {
		this.chargedAmt = chargedAmt;
	}
	/**
	 * @return the items
	 */
	public Set<MenuItem> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(Set<MenuItem> items) {
		this.items = items;
	}
	
}
