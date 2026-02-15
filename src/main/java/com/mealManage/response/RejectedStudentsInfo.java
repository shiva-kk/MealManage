package com.mealManage.response;

import java.util.List;

import com.mealManage.mealmodel.user.StudentUser;

public class RejectedStudentsInfo {
	
	private List<StudentUser> studentUsers;
	private String status;
	private String errorMessage;
	/**
	 * @return the studentUsers
	 */
	public List<StudentUser> getStudentUsers() {
		return studentUsers;
	}
	/**
	 * @param studentUsers the studentUsers to set
	 */
	public void setStudentUsers(List<StudentUser> studentUsers) {
		this.studentUsers = studentUsers;
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

}
