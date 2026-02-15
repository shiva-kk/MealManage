package com.mealManage.mealmodel.user;

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
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "StudentBkpHistory", indexes = { 
	    @Index(columnList = "mealSchoolId"),
	    @Index(columnList="schoolYear")})
public class StudentBkpFileHistory implements Serializable{
	
	private static final long serialVersionUID = -4588992033547381798L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	private String bkpType; //Data Sync or Student Import
	private Long mealSchoolId;
	private Integer schoolYear;
	private Date date;
	private String createdBy;
	private String fileS3Link;
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
	 * @return the bkpType
	 */
	public String getBkpType() {
		return bkpType;
	}
	/**
	 * @param bkpType the bkpType to set
	 */
	public void setBkpType(String bkpType) {
		this.bkpType = bkpType;
	}
	/**
	 * @return the mealSchoolId
	 */
	public Long getMealSchoolId() {
		return mealSchoolId;
	}
	/**
	 * @param mealSchoolId the mealSchoolId to set
	 */
	public void setMealSchoolId(Long mealSchoolId) {
		this.mealSchoolId = mealSchoolId;
	}
	/**
	 * @return the schoolYear
	 */
	public Integer getSchoolYear() {
		return schoolYear;
	}
	/**
	 * @param schoolYear the schoolYear to set
	 */
	public void setSchoolYear(Integer schoolYear) {
		this.schoolYear = schoolYear;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the fileS3Link
	 */
	public String getFileS3Link() {
		return fileS3Link;
	}
	/**
	 * @param fileS3Link the fileS3Link to set
	 */
	public void setFileS3Link(String fileS3Link) {
		this.fileS3Link = fileS3Link;
	}
	
	
}
