package com.mealManage.domain;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.mealManage.mealmodel.school.SchoolGrades;

/**This class used for the reduced / free lunch eligibility status update**/
public class FreeReducedLunchEligReq {
	
	private Long schoolId;
	@Enumerated(EnumType.STRING)
	private SchoolGrades gradeName;
	private List<String> studentIds;
	private Integer schoolYear;
	private Boolean isFreeLunch;
	private Boolean isBeforeCare;
	
	/**
	 * @return the schoolId
	 */
	public Long getSchoolId() {
		return schoolId;
	}
	/**
	 * @param schoolId the schoolId to set
	 */
	public void setSchoolId(Long schoolId) {
		this.schoolId = schoolId;
	}
	/**
	 * @return the gradeName
	 */
	public SchoolGrades getGradeName() {
		return gradeName;
	}
	/**
	 * @param gradeName the gradeName to set
	 */
	public void setGradeName(SchoolGrades gradeName) {
		this.gradeName = gradeName;
	}
	/**
	 * @return the studentIds
	 */
	public List<String> getStudentIds() {
		return studentIds;
	}
	/**
	 * @param studentIds the studentIds to set
	 */
	public void setStudentIds(List<String> studentIds) {
		this.studentIds = studentIds;
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
	 * @return the isFreeLunch
	 */
	public Boolean getIsFreeLunch() {
		return isFreeLunch;
	}
	/**
	 * @param isFreeLunch the isFreeLunch to set
	 */
	public void setIsFreeLunch(Boolean isFreeLunch) {
		this.isFreeLunch = isFreeLunch;
	}
	/**
	 * @return the isBeforeCare
	 */
	public Boolean getIsBeforeCare() {
		return isBeforeCare;
	}
	/**
	 * @param isBeforeCare the isBeforeCare to set
	 */
	public void setIsBeforeCare(Boolean isBeforeCare) {
		this.isBeforeCare = isBeforeCare;
	}	
	
}
