package com.mealManage.mealmodel.transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "EventInfo", indexes = {@Index(columnList="mealSchool_schoolId")})
/**This entity used for all the events info**/
public class EventInfo extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 4274550184052803838L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long recId;
	private String eventName;
	private Date startDate;
	private Date endDate;
	private String type; //[i.e.Donation / Priced]
	private Double amount;
	@NotNull
	private Boolean isPublished = false;
	private String longDesc;
	@ManyToOne
	@NotNull
	private MealSchool mealSchool; 
	@ElementCollection(targetClass = SchoolGrades.class, fetch=FetchType.EAGER)
	@CollectionTable(name = "EventInfo_Grades",joinColumns = @JoinColumn(name = "eventInfo_Id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "grades_name")
	private Set<SchoolGrades> grades;
	private String eventImageUrl;
	@NotNull
	private Boolean isActive = true;
	private Integer schoolYear;
	private String configurableMsg;
	@Transient
	private List<BigInteger> notPaidStudentRecId;
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
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * @return the isPublished
	 */
	public Boolean getIsPublished() {
		return isPublished;
	}
	/**
	 * @param isPublished the isPublished to set
	 */
	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}
	
	/**
	 * @return the longDesc
	 */
	public String getLongDesc() {
		return longDesc;
	}
	/**
	 * @param longDesc the longDesc to set
	 */
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}
	/**
	 * @return the mealSchool
	 */
	public MealSchool getMealSchool() {
		return mealSchool;
	}
	/**
	 * @param mealSchool the mealSchool to set
	 */
	public void setMealSchool(MealSchool mealSchool) {
		this.mealSchool = mealSchool;
	}
	/**
	 * @return the grades
	 */
	public Set<SchoolGrades> getGrades() {
		return grades;
	}
	/**
	 * @param grades the grades to set
	 */
	public void setGrades(Set<SchoolGrades> grades) {
		this.grades = grades;
	}
	/**
	 * @return the eventImageUrl
	 */
	public String getEventImageUrl() {
		return eventImageUrl;
	}
	/**
	 * @param eventImageUrl the eventImageUrl to set
	 */
	public void setEventImageUrl(String eventImageUrl) {
		this.eventImageUrl = eventImageUrl;
	}
	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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
	 * @return the configurableMsg
	 */
	public String getConfigurableMsg() {
		return configurableMsg;
	}
	/**
	 * @param configurableMsg the configurableMsg to set
	 */
	public void setConfigurableMsg(String configurableMsg) {
		this.configurableMsg = configurableMsg;
	}
	/**
	 * @return the notPaidStudentRecId
	 */
	public List<BigInteger> getNotPaidStudentRecId() {
		return notPaidStudentRecId;
	}
	/**
	 * @param notPaidStudentRecId the notPaidStudentRecId to set
	 */
	public void setNotPaidStudentRecId(List<BigInteger> notPaidStudentRecId) {
		this.notPaidStudentRecId = notPaidStudentRecId;
	}	
}
