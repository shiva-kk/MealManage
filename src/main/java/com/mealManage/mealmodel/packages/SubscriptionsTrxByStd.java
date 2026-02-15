package com.mealManage.mealmodel.packages;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.user.StudentUser;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "SubscriptionsTrxByStd", indexes = {
	    @Index(columnList="studentUser_userId"),
	    @Index(columnList="startDate"),
	    @Index(columnList="endDate")})
/**This entity used for capture the package payments info by student**/
public class SubscriptionsTrxByStd implements Serializable{
	
	private static final long serialVersionUID = 4218829217317756016L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	@ManyToOne
	@NotNull
	private StudentUser studentUser;
	@NotNull
	private Double paidAmt; //in school country currency
	private Date startDate;
	private Date endDate;
	@ManyToOne
	@NotNull
	private SchoolPackage schoolPackage;
	/*@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "subTrxId", nullable = false, updatable = false)*/
	
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
	 * @return the paidAmt
	 */
	public Double getPaidAmt() {
		return paidAmt;
	}
	/**
	 * @param paidAmt the paidAmt to set
	 */
	public void setPaidAmt(Double paidAmt) {
		this.paidAmt = paidAmt;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the schoolPackage
	 */
	public SchoolPackage getSchoolPackage() {
		return schoolPackage;
	}
	/**
	 * @param schoolPackage the schoolPackage to set
	 */
	public void setSchoolPackage(SchoolPackage schoolPackage) {
		this.schoolPackage = schoolPackage;
	}	
}
