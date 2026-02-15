package com.mealManage.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.domain.GradesInfo;
import com.mealManage.domain.LowBalanceStudents;
import com.mealManage.domain.LunchNotServedStudents;
import com.mealManage.domain.MenuItemDetails;
import com.mealManage.domain.MenuItemDetailsV2;
import com.mealManage.domain.SchoolDetailsInfo;
import com.mealManage.domain.StudentAccountDetails;
import com.mealManage.domain.StudentDetailsWithOrderedItem;
import com.mealManage.domain.TransactionsDetails;
import com.mealManage.mealmodel.meal.BreakfastItems;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.packages.PickupAuthorizedResp;
import com.mealManage.mealmodel.school.SchoolGrades;

public class ServiceResponse {
	
	private String status;
	private Integer statusCode;
	private String errorCode;
	private String statusMessage;
	private String errorMessage;
	private String schoolId;
	private String resetPwdLink="";
	private String userName;
	private String mealJsonData;
	private String yearMonth;
	private String subdomain;
	private List<StudentDetailsWithOrderedItem> studentDetailsWithOrderedItems;
	private Map<SchoolGrades, List<MenuItemDetails>> menuItemsByGrade;
	private List<BreakfastItems> breakfastItems;
	private StudentAccountDetails studentAccountDetails;
	private List<TransactionsDetails> transactionReportsDetails;
	private List<LowBalanceStudents> lowBalanceStudentsList;
	private Map<String, String> mapKeyVal; 
	private List<SchoolDetailsInfo> schoolDetailsInfos;
	private List<LunchNotServedStudents> lunchNotServedStudentList;
	private String stripeAcLink;
	@JsonIgnore
	private String studentId;
	private Map<SchoolGrades, Map<MealType, List<MenuItemDetailsV2>>> lunchByTypeAndGrade;
	private Map<MealType, List<MenuItemDetailsV2>> breakfastByType;
	private Object response;
	private Map<Long, List<PickupAuthorizedResp>> resp1;
	private List<GradesInfo> gradesInfo;
	private String dateFormat;
	private String currencySymbol;
	private String countryCode;
	private Map<String, Double> costByGrade;
	private String phoneValidation;
	private Object summary;
	private Boolean isPOSIdVerificationReq;
	private Boolean freeMeal;
	
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
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
	 * @return the schoolId
	 */
	public String getSchoolId() {
		return schoolId;
	}
	/**
	 * @param schoolId the schoolId to set
	 */
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	/**
	 * @return the resetPwdLink
	 */
	public String getResetPwdLink() {
		return resetPwdLink;
	}
	/**
	 * @param resetPwdLink the resetPwdLink to set
	 */
	public void setResetPwdLink(String resetPwdLink) {
		this.resetPwdLink = resetPwdLink;
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
	 * @return the mealJsonData
	 */
	public String getMealJsonData() {
		return mealJsonData;
	}
	/**
	 * @param mealJsonData the mealJsonData to set
	 */
	public void setMealJsonData(String mealJsonData) {
		this.mealJsonData = mealJsonData;
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
	 * @return the subdomain
	 */
	public String getSubdomain() {
		return subdomain;
	}
	/**
	 * @param subdomain the subdomain to set
	 */
	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}
	/**
	 * @return the studentDetailsWithOrderedItems
	 */
	public List<StudentDetailsWithOrderedItem> getStudentDetailsWithOrderedItems() {
		return studentDetailsWithOrderedItems;
	}
	/**
	 * @param studentDetailsWithOrderedItems the studentDetailsWithOrderedItems to set
	 */
	public void setStudentDetailsWithOrderedItems(List<StudentDetailsWithOrderedItem> studentDetailsWithOrderedItems) {
		this.studentDetailsWithOrderedItems = studentDetailsWithOrderedItems;
	}
	/**
	 * @return the menuItemsByGrade
	 */
	public Map<SchoolGrades, List<MenuItemDetails>> getMenuItemsByGrade() {
		return menuItemsByGrade;
	}
	/**
	 * @param menuItemsByGrade the menuItemsByGrade to set
	 */
	public void setMenuItemsByGrade(Map<SchoolGrades, List<MenuItemDetails>> menuItemsByGrade) {
		this.menuItemsByGrade = menuItemsByGrade;
	}
	/**
	 * @return the studentAccountDetails
	 */
	public StudentAccountDetails getStudentAccountDetails() {
		return studentAccountDetails;
	}
	/**
	 * @param studentAccountDetails the studentAccountDetails to set
	 */
	public void setStudentAccountDetails(StudentAccountDetails studentAccountDetails) {
		this.studentAccountDetails = studentAccountDetails;
	}
	/**
	 * @return the transactionReportsDetails
	 */
	public List<TransactionsDetails> getTransactionReportsDetails() {
		return transactionReportsDetails;
	}
	/**
	 * @param transactionReportsDetails the transactionReportsDetails to set
	 */
	public void setTransactionReportsDetails(List<TransactionsDetails> transactionReportsDetails) {
		this.transactionReportsDetails = transactionReportsDetails;
	}
	/**
	 * @return the lowBalanceStudentsList
	 */
	public List<LowBalanceStudents> getLowBalanceStudentsList() {
		return lowBalanceStudentsList;
	}
	/**
	 * @param lowBalanceStudentsList the lowBalanceStudentsList to set
	 */
	public void setLowBalanceStudentsList(List<LowBalanceStudents> lowBalanceStudentsList) {
		this.lowBalanceStudentsList = lowBalanceStudentsList;
	}
	/**
	 * @return the mapKeyVal
	 */
	public Map<String, String> getMapKeyVal() {
		return mapKeyVal;
	}
	/**
	 * @param mapKeyVal the mapKeyVal to set
	 */
	public void setMapKeyVal(Map<String, String> mapKeyVal) {
		this.mapKeyVal = mapKeyVal;
	}
	/**
	 * @return the schoolDetailsInfos
	 */
	public List<SchoolDetailsInfo> getSchoolDetailsInfos() {
		return schoolDetailsInfos;
	}
	/**
	 * @param schoolDetailsInfos the schoolDetailsInfos to set
	 */
	public void setSchoolDetailsInfos(List<SchoolDetailsInfo> schoolDetailsInfos) {
		this.schoolDetailsInfos = schoolDetailsInfos;
	}
	/**
	 * @return the breakfastItems
	 */
	public List<BreakfastItems> getBreakfastItems() {
		return breakfastItems;
	}
	/**
	 * @param breakfastItems the breakfastItems to set
	 */
	public void setBreakfastItems(List<BreakfastItems> breakfastItems) {
		this.breakfastItems = breakfastItems;
	}
	/**
	 * @return the lunchNotServedStudentList
	 */
	public List<LunchNotServedStudents> getLunchNotServedStudentList() {
		return lunchNotServedStudentList;
	}
	/**
	 * @param lunchNotServedStudentList the lunchNotServedStudentList to set
	 */
	public void setLunchNotServedStudentList(List<LunchNotServedStudents> lunchNotServedStudentList) {
		this.lunchNotServedStudentList = lunchNotServedStudentList;
	}
	/**
	 * @return the stripeAcLink
	 */
	public String getStripeAcLink() {
		return stripeAcLink;
	}
	/**
	 * @param stripeAcLink the stripeAcLink to set
	 */
	public void setStripeAcLink(String stripeAcLink) {
		this.stripeAcLink = stripeAcLink;
	}
	/**
	 * @return the studentId
	 */
	public String getStudentId() {
		return studentId;
	}
	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	/**
	 * @return the lunchByTypeAndGrade
	 */
	public Map<SchoolGrades, Map<MealType, List<MenuItemDetailsV2>>> getLunchByTypeAndGrade() {
		return lunchByTypeAndGrade;
	}
	/**
	 * @param lunchByTypeAndGrade the lunchByTypeAndGrade to set
	 */
	public void setLunchByTypeAndGrade(Map<SchoolGrades, Map<MealType, List<MenuItemDetailsV2>>> lunchByTypeAndGrade) {
		this.lunchByTypeAndGrade = lunchByTypeAndGrade;
	}
	/**
	 * @return the breakfastByType
	 */
	public Map<MealType, List<MenuItemDetailsV2>> getBreakfastByType() {
		return breakfastByType;
	}
	/**
	 * @param breakfastByType the breakfastByType to set
	 */
	public void setBreakfastByType(Map<MealType, List<MenuItemDetailsV2>> breakfastByType) {
		this.breakfastByType = breakfastByType;
	}
	/**
	 * @return the response
	 */
	public Object getResponse() {
		return response;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(Object response) {
		this.response = response;
	}
	/**
	 * @return the resp1
	 */
	public Map<Long, List<PickupAuthorizedResp>> getResp1() {
		return resp1;
	}
	/**
	 * @param resp1 the resp1 to set
	 */
	public void setResp1(Map<Long, List<PickupAuthorizedResp>> resp1) {
		this.resp1 = resp1;
	}
	/**
	 * @return the gradesInfo
	 */
	public List<GradesInfo> getGradesInfo() {
		return gradesInfo;
	}
	/**
	 * @param gradesInfo the gradesInfo to set
	 */
	public void setGradesInfo(List<GradesInfo> gradesInfo) {
		this.gradesInfo = gradesInfo;
	}
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * @return the currencySymbol
	 */
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	/**
	 * @param currencySymbol the currencySymbol to set
	 */
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return the costByGrade
	 */
	public Map<String, Double> getCostByGrade() {
		return costByGrade;
	}
	/**
	 * @param costByGrade the costByGrade to set
	 */
	public void setCostByGrade(Map<String, Double> costByGrade) {
		this.costByGrade = costByGrade;
	}
	/**
	 * @return the phoneValidation
	 */
	public String getPhoneValidation() {
		return phoneValidation;
	}
	/**
	 * @param phoneValidation the phoneValidation to set
	 */
	public void setPhoneValidation(String phoneValidation) {
		this.phoneValidation = phoneValidation;
	}
	/**
	 * @return the summary
	 */
	public Object getSummary() {
		return summary;
	}
	/**
	 * @param summary the summary to set
	 */
	public void setSummary(Object summary) {
		this.summary = summary;
	}
	/**
	 * @return the isPOSIdVerificationReq
	 */
	public Boolean getIsPOSIdVerificationReq() {
		return isPOSIdVerificationReq;
	}
	/**
	 * @param isPOSIdVerificationReq the isPOSIdVerificationReq to set
	 */
	public void setIsPOSIdVerificationReq(Boolean isPOSIdVerificationReq) {
		this.isPOSIdVerificationReq = isPOSIdVerificationReq;
	}
	/**
	 * @return the freeMeal
	 */
	public Boolean getFreeMeal() {
		return freeMeal;
	}
	/**
	 * @param freeMeal the freeMeal to set
	 */
	public void setFreeMeal(Boolean freeMeal) {
		this.freeMeal = freeMeal;
	}
	
}
