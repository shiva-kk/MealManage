package com.mealManage.response;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StudentCreateResp {
	
	private List<RejectedStudentsInfo> rejectedStudentsInfos;
	private String status;
	private String statusMessage;
	private Integer statusCode;
	private String errorMessage;
	private int successInsertCount;
	private int successUpdateCount;
	private int failedInsertCount;
	private int failedUpdateCount;
	private int skipRecCount;
	@JsonIgnore
	private Set<String> studentIds;
	private List<String> failedInsertStudentIds;
	private List<String> failedUpdateStudentIds;
	
	/**
	 * @return the rejectedStudentsInfos
	 */
	public List<RejectedStudentsInfo> getRejectedStudentsInfos() {
		return rejectedStudentsInfos;
	}
	/**
	 * @param rejectedStudentsInfos the rejectedStudentsInfos to set
	 */
	public void setRejectedStudentsInfos(List<RejectedStudentsInfo> rejectedStudentsInfos) {
		this.rejectedStudentsInfos = rejectedStudentsInfos;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the successInsertCount
	 */
	public int getSuccessInsertCount() {
		return successInsertCount;
	}
	/**
	 * @param successInsertCount the successInsertCount to set
	 */
	public void setSuccessInsertCount(int successInsertCount) {
		this.successInsertCount = successInsertCount;
	}
	/**
	 * @return the successUpdateCount
	 */
	public int getSuccessUpdateCount() {
		return successUpdateCount;
	}
	/**
	 * @param successUpdateCount the successUpdateCount to set
	 */
	public void setSuccessUpdateCount(int successUpdateCount) {
		this.successUpdateCount = successUpdateCount;
	}
	/**
	 * @return the failedInsertCount
	 */
	public int getFailedInsertCount() {
		return failedInsertCount;
	}
	/**
	 * @param failedInsertCount the failedInsertCount to set
	 */
	public void setFailedInsertCount(int failedInsertCount) {
		this.failedInsertCount = failedInsertCount;
	}
	/**
	 * @return the failedUpdateCount
	 */
	public int getFailedUpdateCount() {
		return failedUpdateCount;
	}
	/**
	 * @param failedUpdateCount the failedUpdateCount to set
	 */
	public void setFailedUpdateCount(int failedUpdateCount) {
		this.failedUpdateCount = failedUpdateCount;
	}
	/**
	 * @return the skipRecCount
	 */
	public int getSkipRecCount() {
		return skipRecCount;
	}
	/**
	 * @param skipRecCount the skipRecCount to set
	 */
	public void setSkipRecCount(int skipRecCount) {
		this.skipRecCount = skipRecCount;
	}
	/**
	 * @return the studentIds
	 */
	public Set<String> getStudentIds() {
		return studentIds;
	}
	/**
	 * @param studentIds the studentIds to set
	 */
	public void setStudentIds(Set<String> studentIds) {
		this.studentIds = studentIds;
	}
	/**
	 * @return the failedInsertStudentIds
	 */
	public List<String> getFailedInsertStudentIds() {
		return failedInsertStudentIds;
	}
	/**
	 * @param failedInsertStudentIds the failedInsertStudentIds to set
	 */
	public void setFailedInsertStudentIds(List<String> failedInsertStudentIds) {
		this.failedInsertStudentIds = failedInsertStudentIds;
	}
	/**
	 * @return the failedUpdateStudentIds
	 */
	public List<String> getFailedUpdateStudentIds() {
		return failedUpdateStudentIds;
	}
	/**
	 * @param failedUpdateStudentIds the failedUpdateStudentIds to set
	 */
	public void setFailedUpdateStudentIds(List<String> failedUpdateStudentIds) {
		this.failedUpdateStudentIds = failedUpdateStudentIds;
	}
	
}
