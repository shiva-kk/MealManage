package com.mealManage.domain;

/**This class used for build the request to send notification to parent user regarding meal & payment status update**/
public class StatusUpdateNotificationReq {
	
	private String email;
	private String ordermsg;
	private String subjectMsg;
	private String adminEmail;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the ordermsg
	 */
	public String getOrdermsg() {
		return ordermsg;
	}
	/**
	 * @param ordermsg the ordermsg to set
	 */
	public void setOrdermsg(String ordermsg) {
		this.ordermsg = ordermsg;
	}	

	/**
	 * @return the subjectMsg
	 */
	public String getSubjectMsg() {
		return subjectMsg;
	}
	/**
	 * @param subjectMsg the subjectMsg to set
	 */
	public void setSubjectMsg(String subjectMsg) {
		this.subjectMsg = subjectMsg;
	}
	

	/**
	 * @return the adminEmail
	 */
	public String getAdminEmail() {
		return adminEmail;
	}
	/**
	 * @param adminEmail the adminEmail to set
	 */
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}
	/**This method used for make the unique combination**/
	@Override
    public int hashCode() {
        return (this.email.hashCode() + this.ordermsg.hashCode()+ this.subjectMsg.hashCode());
    }
	
	/**This method used to make sure that only there are one record for same email and message**/
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UserActivationNotification) {
            StatusUpdateNotificationReq temp = (StatusUpdateNotificationReq) obj;
            if(this.email.equals(temp.email) && this.ordermsg.equals(temp.ordermsg) && this.subjectMsg.equals(temp.subjectMsg)) {
                return true;
            }
        }
        return false;
    }

}
