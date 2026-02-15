package com.mealManage.domain;

public class UserActivationNotification {
	
	private String email;
	private String token;
	private String schoolName;
	private String dates;
	private String cancellationNote;
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
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the schoolName
	 */
	public String getSchoolName() {
		return schoolName;
	}

	/**
	 * @param schoolName the schoolName to set
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	/**
	 * @return the dates
	 */
	public String getDates() {
		return dates;
	}

	/**
	 * @param dates the dates to set
	 */
	public void setDates(String dates) {
		this.dates = dates;
	}

	/**
	 * @return the cancellationNote
	 */
	public String getCancellationNote() {
		return cancellationNote;
	}

	/**
	 * @param cancellationNote the cancellationNote to set
	 */
	public void setCancellationNote(String cancellationNote) {
		this.cancellationNote = cancellationNote;
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

	@Override
     public int hashCode() {
         return (this.email.hashCode() + this.token.hashCode() + this.schoolName.hashCode());
     }

     @Override
     public boolean equals(Object obj) {
         if(obj instanceof UserActivationNotification) {
             UserActivationNotification temp = (UserActivationNotification) obj;
             if(this.email.equals(temp.email) && this.token.equals(temp.token) && this.schoolName.equals(temp.schoolName)) {
                 return true;
             }
         }
         return false;
     }
}
