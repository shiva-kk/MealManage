package com.mealManage.response;


/**This POJO class used for get/set the parent requested email details regarding self registration**/
public class SelfRegParentRequestedEmail {
	
	private long recId;
	private String emailId;
	private String requestedTime;
	private Boolean linkSendStatus;
	/**
	 * @return the recId
	 */
	public long getRecId() {
		return recId;
	}
	/**
	 * @param recId the recId to set
	 */
	public void setRecId(long recId) {
		this.recId = recId;
	}
	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	/**
	 * @return the requestedTime
	 */
	public String getRequestedTime() {
		return requestedTime;
	}
	/**
	 * @param requestedTime the requestedTime to set
	 */
	public void setRequestedTime(String requestedTime) {
		this.requestedTime = requestedTime;
	}
	/**
	 * @return the linkSendStatus
	 */
	public Boolean getLinkSendStatus() {
		return linkSendStatus;
	}
	/**
	 * @param linkSendStatus the linkSendStatus to set
	 */
	public void setLinkSendStatus(Boolean linkSendStatus) {
		this.linkSendStatus = linkSendStatus;
	}

}
