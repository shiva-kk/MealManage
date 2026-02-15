package com.mealManage.mealmodel.school;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name="LowBalanceSchoolSetting",uniqueConstraints={
	    @UniqueConstraint(columnNames = {"mealSchool_schoolId"})}, indexes = { 
	    @Index(columnList = "mealSchool_schoolId")})
/**This entity used for the school low balance settings details**/
public class LowBalanceSchoolSetting extends BaseEntity implements Serializable{

	private static final long serialVersionUID = -8768832565269321329L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	@OneToOne(cascade=CascadeType.ALL) 
	@NotNull
	private MealSchool  mealSchool;
	private Date lastRanDate;
	@JsonIgnore
	private String scheduledDays; 
	@NotNull
	private String runTime; //HH:mm (i.e. 23:00)
	@NotNull
	private Double lowBalMinCriteria=0.0;
	@NotNull
	private Double lowBalMaxCriteria=0.0;
	@NotNull
	private Boolean isExcludeZeroBal = false;
	@Size(max = 1024)
	private String messageBody;
	@NotNull
	private Boolean isPaused = false;
	@Transient
	private List<WeekDaysEnum> scheduledDayList; //It's used for fronted only not in DB
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
	 * @return the lastRanDate
	 */
	public Date getLastRanDate() {
		return lastRanDate;
	}
	/**
	 * @param lastRanDate the lastRanDate to set
	 */
	public void setLastRanDate(Date lastRanDate) {
		this.lastRanDate = lastRanDate;
	}
	/**
	 * @return the scheduledDays
	 */
	public String getScheduledDays() {
		return scheduledDays;
	}
	/**
	 * @param scheduledDays the scheduledDays to set
	 */
	public void setScheduledDays(String scheduledDays) {
		this.scheduledDays = scheduledDays;
		this.scheduledDayList =  Arrays.stream(scheduledDays.split(",")).map(WeekDaysEnum::valueOf)
			    .collect(Collectors.toList());;
	}
	/**
	 * @return the runTime
	 */
	public String getRunTime() {
		return runTime;
	}
	/**
	 * @param runTime the runTime to set
	 */
	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}
	/**
	 * @return the lowBalMinCriteria
	 */
	public Double getLowBalMinCriteria() {
		return lowBalMinCriteria;
	}
	/**
	 * @param lowBalMinCriteria the lowBalMinCriteria to set
	 */
	public void setLowBalMinCriteria(Double lowBalMinCriteria) {
		this.lowBalMinCriteria = lowBalMinCriteria;
	}
	/**
	 * @return the lowBalMaxCriteria
	 */
	public Double getLowBalMaxCriteria() {
		return lowBalMaxCriteria;
	}
	/**
	 * @param lowBalMaxCriteria the lowBalMaxCriteria to set
	 */
	public void setLowBalMaxCriteria(Double lowBalMaxCriteria) {
		this.lowBalMaxCriteria = lowBalMaxCriteria;
	}
	/**
	 * @return the isExcludeZeroBal
	 */
	public Boolean getIsExcludeZeroBal() {
		return isExcludeZeroBal;
	}
	/**
	 * @param isExcludeZeroBal the isExcludeZeroBal to set
	 */
	public void setIsExcludeZeroBal(Boolean isExcludeZeroBal) {
		this.isExcludeZeroBal = isExcludeZeroBal;
	}
	/**
	 * @return the messageBody
	 */
	public String getMessageBody() {
		return messageBody;
	}
	/**
	 * @param messageBody the messageBody to set
	 */
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	/**
	 * @return the isPaused
	 */
	public Boolean getIsPaused() {
		return isPaused;
	}
	/**
	 * @param isPaused the isPaused to set
	 */
	public void setIsPaused(Boolean isPaused) {
		this.isPaused = isPaused;
	}
	/**
	 * @return the scheduledDayList
	 */
	public List<WeekDaysEnum> getScheduledDayList() {
		this.scheduledDayList =  Arrays.stream(scheduledDays.split(",")).map(WeekDaysEnum::valueOf)
			    .collect(Collectors.toList());;
		return scheduledDayList;
	}
	/**
	 * @param scheduledDayList the scheduledDayList to set
	 */
	public void setScheduledDayList(List<WeekDaysEnum> scheduledDayList) {
		this.scheduledDayList = scheduledDayList;
		this.scheduledDays =  scheduledDayList.stream().map(a -> String.valueOf(a))
		           .collect(Collectors.joining(","));
	}
	
}
