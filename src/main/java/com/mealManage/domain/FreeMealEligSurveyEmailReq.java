package com.mealManage.domain;

/**This class used for build the request to send notification to parent user regarding meal & payment status update**/
public class FreeMealEligSurveyEmailReq {
	
	private String email;
	private SurveyRequest surveyMsg;
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
	 * @return the surveyMsg
	 */
	public SurveyRequest getSurveyMsg() {
		return surveyMsg;
	}
	/**
	 * @param surveyMsg the surveyMsg to set
	 */
	public void setSurveyMsg(SurveyRequest surveyMsg) {
		this.surveyMsg = surveyMsg;
	}
	
}
