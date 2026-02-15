package com.mealManage.response;

public class EmailSendResp {
	
	private Boolean paymentReminderEnable = true;
	private Boolean lunchReminderEnable = true;
	private Boolean emailIsSubscribe = true;
	/**
	 * @return the paymentReminderEnable
	 */
	public Boolean getPaymentReminderEnable() {
		return paymentReminderEnable;
	}
	/**
	 * @param paymentReminderEnable the paymentReminderEnable to set
	 */
	public void setPaymentReminderEnable(Boolean paymentReminderEnable) {
		this.paymentReminderEnable = paymentReminderEnable;
	}
	/**
	 * @return the lunchReminderEnable
	 */
	public Boolean getLunchReminderEnable() {
		return lunchReminderEnable;
	}
	/**
	 * @param lunchReminderEnable the lunchReminderEnable to set
	 */
	public void setLunchReminderEnable(Boolean lunchReminderEnable) {
		this.lunchReminderEnable = lunchReminderEnable;
	}
	/**
	 * @return the emailIsSubscribe
	 */
	public Boolean getEmailIsSubscribe() {
		return emailIsSubscribe;
	}
	/**
	 * @param emailIsSubscribe the emailIsSubscribe to set
	 */
	public void setEmailIsSubscribe(Boolean emailIsSubscribe) {
		this.emailIsSubscribe = emailIsSubscribe;
	}
}
