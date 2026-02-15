package com.mealManage.mealmodel.user;

import java.io.Serializable;

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

import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "SupportUser", indexes = {
		@Index(columnList = "userEmail"),
		@Index(columnList = "issueType"),
		@Index(columnList = "tktCurrentStatus"),
		@Index(columnList = "mealSchool_schoolId"),
		@Index(columnList = "studentUser_userId"),
		@Index(columnList = "orderIssueYearMonth")})
/**This entity used for store the user support data***/
public class SupportUser extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 5684883929287186311L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "supportReqId", updatable = false, nullable = false)
	private Long supportReqId;
	@Column(nullable = false)
	private String userEmail;
	@Column(nullable = false)
	private String issueType;
	@ManyToOne
	private StudentUser studentUser;
	@ManyToOne
	private MealSchool mealSchool;
	@Column(nullable = false)
	private String customMessage;
	private String orderIssueYearMonth;
	private String userName;
	private String contactNo;
	private Integer tktCurrentStatus = 0; //0 means Open, 1 means In-Progress, 2 means Close.
	private String studentName;
	private String studentGrade;
	private String comments;
	/**
	 * @return the supportReqId
	 */
	public Long getSupportReqId() {
		return supportReqId;
	}
	/**
	 * @param supportReqId the supportReqId to set
	 */
	public void setSupportReqId(Long supportReqId) {
		this.supportReqId = supportReqId;
	}
	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}
	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	/**
	 * @return the issueType
	 */
	public String getIssueType() {
		return issueType;
	}
	/**
	 * @param issueType the issueType to set
	 */
	public void setIssueType(String issueType) {
		this.issueType = issueType;
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
	 * @return the customMessage
	 */
	public String getCustomMessage() {
		return customMessage;
	}
	/**
	 * @param customMessage the customMessage to set
	 */
	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}
	/**
	 * @return the orderIssueYearMonth
	 */
	public String getOrderIssueYearMonth() {
		return orderIssueYearMonth;
	}
	/**
	 * @param orderIssueYearMonth the orderIssueYearMonth to set
	 */
	public void setOrderIssueYearMonth(String orderIssueYearMonth) {
		this.orderIssueYearMonth = orderIssueYearMonth;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the contactNo
	 */
	public String getContactNo() {
		return contactNo;
	}
	/**
	 * @param contactNo the contactNo to set
	 */
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	/**
	 * @return the tktCurrentStatus
	 */
	public Integer getTktCurrentStatus() {
		return tktCurrentStatus;
	}
	/**
	 * @param tktCurrentStatus the tktCurrentStatus to set
	 */
	public void setTktCurrentStatus(Integer tktCurrentStatus) {
		this.tktCurrentStatus = tktCurrentStatus;
	}
	/**
	 * @return the studentName
	 */
	public String getStudentName() {
		return studentName;
	}
	/**
	 * @param studentName the studentName to set
	 */
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	/**
	 * @return the studentGrade
	 */
	public String getStudentGrade() {
		return studentGrade;
	}
	/**
	 * @param studentGrade the studentGrade to set
	 */
	public void setStudentGrade(String studentGrade) {
		this.studentGrade = studentGrade;
	}
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
