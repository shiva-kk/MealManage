package com.mealManage.domain;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;

public class ParentsNotificationRequest {
	
	private Long schoolId;
	@Enumerated(EnumType.STRING)
	private SchoolGrades gradeName;
	private List<String> studentIds;
	private String notificationType;
	private Boolean sendStatus = false;
	private String yearMonth;
	private Integer schoolYear;
	private ItemTypeConstants menuType;
	
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
	 * @return the notificationType
	 */
	public String getNotificationType() {
		return notificationType;
	}
	/**
	 * @param notificationType the notificationType to set
	 */
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	/**
	 * @return the sendStatus
	 */
	public Boolean getSendStatus() {
		return sendStatus;
	}
	/**
	 * @param sendStatus the sendStatus to set
	 */
	public void setSendStatus(Boolean sendStatus) {
		this.sendStatus = sendStatus;
	}
	/**
	 * @return the yearMonth
	 */
	public String getYearMonth() {
		return yearMonth;
	}
	/**
	 * @param yearMonth the yearMonth to set
	 */
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
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
	 * @return the menuType
	 */
	public ItemTypeConstants getMenuType() {
		return menuType;
	}
	/**
	 * @param menuType the menuType to set
	 */
	public void setMenuType(ItemTypeConstants menuType) {
		this.menuType = menuType;
	}
	
}
