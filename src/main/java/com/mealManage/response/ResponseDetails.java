package com.mealManage.response;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.domain.GradesInfo;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.transaction.SchoolPayGatewayInfo;
import com.mealManage.mealmodel.user.ModuleTypeMapping;
import com.mealManage.menu.entities.SchoolSession;

public class ResponseDetails {
	
	private String firstName="";
	private String lastName="";
	private String mobileNo="";
	private String username="";
	private String schoolId="";
	private String authToken="";
	private String errorCode;
	private String message="";
	private String status="";
	private String error="";
	private Integer statusCode;
	private String ctds="";
	private String schoolName="";
	private String schoolAddress="";
	private String cityStateZip="";
	private String county="";
	private String schoolSubDomain="";
	private String role="";
	private Long userId;
	private Integer schoolYear;
	private String latestActiveMonth;
	private String isdCode;
	private String schoolTimeZone;
	private String currencySymbol;
	private Map<String, String> pageSize;
	private Map<String,Object> schoolOtherInfo;
	private List<GradesInfo> gradesMap;
	private Map<String, Map<String, List<ModuleTypeMapping>>> moduleDetails;
	private Boolean isContactDetailsReq;
	private String pin;
	/*private boolean isSupportSIS = false;
	private boolean isSupportBCPrg = false;
	private boolean isSupportFreeReducedPrg = false;
	private boolean isSchoolProvideBreakfast = false;
	private boolean isMenuByYear = false; //if it's true then menu by year else menu by month
	private boolean isSupportStaffLunch = false;
	private boolean isSupportInstantPayment = false;*/
	private Map<String, String> moduleAccess;
	private String tierName;
	private boolean isTrxFeeOnSchool = false;
	private Set<SchoolPayGatewayInfo> paymentGateways;
	private Long catererId;
	private Long districtId;
	private List<MealSchool> schools;
	private String dateFormat;
	private String countryCode;
	private String phoneValidation;
	@JsonIgnore
	private Boolean loginAsAdmin = false;
	private List<Integer> nonSchoolDays;
	private List<SchoolSession> schoolSessions;
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		if(firstName != null)
			return firstName.trim();
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		if(lastName != null)
			return lastName.trim();
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the mobileNo
	 */
	public String getMobileNo() {
		return mobileNo;
	}
	/**
	 * @param mobileNo the mobileNo to set
	 */
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}
	/**
	 * @param authToken the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
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
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
	/**
	 * @return the ctds
	 */
	public String getCtds() {
		return ctds;
	}
	/**
	 * @param ctds the ctds to set
	 */
	public void setCtds(String ctds) {
		this.ctds = ctds;
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
	 * @return the schoolAddress
	 */
	public String getSchoolAddress() {
		return schoolAddress;
	}
	/**
	 * @param schoolAddress the schoolAddress to set
	 */
	public void setSchoolAddress(String schoolAddress) {
		this.schoolAddress = schoolAddress;
	}
	/**
	 * @return the cityStateZip
	 */
	public String getCityStateZip() {
		return cityStateZip;
	}
	/**
	 * @param cityStateZip the cityStateZip to set
	 */
	public void setCityStateZip(String cityStateZip) {
		this.cityStateZip = cityStateZip;
	}
	/**
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}
	/**
	 * @param county the county to set
	 */
	public void setCounty(String county) {
		this.county = county;
	}
	/**
	 * @return the schoolSubDomain
	 */
	public String getSchoolSubDomain() {
		return schoolSubDomain;
	}
	/**
	 * @param schoolSubDomain the schoolSubDomain to set
	 */
	public void setSchoolSubDomain(String schoolSubDomain) {
		this.schoolSubDomain = schoolSubDomain;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
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
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
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
	 * @return the latestActiveMonth
	 */
	public String getLatestActiveMonth() {
		return latestActiveMonth;
	}
	/**
	 * @param latestActiveMonth the latestActiveMonth to set
	 */
	public void setLatestActiveMonth(String latestActiveMonth) {
		this.latestActiveMonth = latestActiveMonth;
	}
	/**
	 * @return the isdCode
	 */
	public String getIsdCode() {
		return isdCode;
	}
	/**
	 * @param isdCode the isdCode to set
	 */
	public void setIsdCode(String isdCode) {
		this.isdCode = isdCode;
	}
	/**
	 * @return the schoolTimeZone
	 */
	public String getSchoolTimeZone() {
		return schoolTimeZone;
	}
	/**
	 * @param schoolTimeZone the schoolTimeZone to set
	 */
	public void setSchoolTimeZone(String schoolTimeZone) {
		this.schoolTimeZone = schoolTimeZone;
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
	 * @return the pageSize
	 */
	public Map<String, String> getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Map<String, String> pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the schoolOtherInfo
	 */
	public Map<String, Object> getSchoolOtherInfo() {
		return schoolOtherInfo;
	}
	/**
	 * @param schoolOtherInfo the schoolOtherInfo to set
	 */
	public void setSchoolOtherInfo(Map<String, Object> schoolOtherInfo) {
		this.schoolOtherInfo = schoolOtherInfo;
	}
	/**
	 * @return the moduleDetails
	 */
	public Map<String, Map<String, List<ModuleTypeMapping>>> getModuleDetails() {
		return moduleDetails;
	}
	/**
	 * @param moduleDetails the moduleDetails to set
	 */
	public void setModuleDetails(Map<String, Map<String, List<ModuleTypeMapping>>> moduleDetails) {
		this.moduleDetails = moduleDetails;
	}
	/**
	 * @return the isContactDetailsReq
	 */
	public Boolean getIsContactDetailsReq() {
		return isContactDetailsReq;
	}
	/**
	 * @param isContactDetailsReq the isContactDetailsReq to set
	 */
	public void setIsContactDetailsReq(Boolean isContactDetailsReq) {
		this.isContactDetailsReq = isContactDetailsReq;
	}
	
	/**
	 * @return the moduleAccess
	 */
	public Map<String, String> getModuleAccess() {
		return moduleAccess;
	}
	/**
	 * @param moduleAccess the moduleAccess to set
	 */
	public void setModuleAccess(Map<String, String> moduleAccess) {
		this.moduleAccess = moduleAccess;
	}
	
	/**
	 * @return the tierName
	 */
	public String getTierName() {
		return tierName;
	}
	/**
	 * @param tierName the tierName to set
	 */
	public void setTierName(String tierName) {
		this.tierName = tierName;
	}
	/**
	 * @return the isTrxFeeOnSchool
	 */
	public boolean isTrxFeeOnSchool() {
		return isTrxFeeOnSchool;
	}
	/**
	 * @param isTrxFeeOnSchool the isTrxFeeOnSchool to set
	 */
	public void setTrxFeeOnSchool(boolean isTrxFeeOnSchool) {
		this.isTrxFeeOnSchool = isTrxFeeOnSchool;
	}
	/**
	 * @return the paymentGateways
	 */
	public Set<SchoolPayGatewayInfo> getPaymentGateways() {
		return paymentGateways;
	}
	/**
	 * @param paymentGateways the paymentGateways to set
	 */
	public void setPaymentGateways(Set<SchoolPayGatewayInfo> paymentGateways) {
		this.paymentGateways = paymentGateways;
	}
	/**
	 * @return the gradesMap
	 */
	public List<GradesInfo> getGradesMap() {
		return gradesMap;
	}
	/**
	 * @param gradesMap the gradesMap to set
	 */
	public void setGradesMap(List<GradesInfo> gradesMap) {
		this.gradesMap = gradesMap;
	}
	
	/**
	 * @return the catererId
	 */
	public Long getCatererId() {
		return catererId;
	}
	/**
	 * @param catererId the catererId to set
	 */
	public void setCatererId(Long catererId) {
		this.catererId = catererId;
	}
	/**
	 * @return the schools
	 */
	public List<MealSchool> getSchools() {
		return schools;
	}
	/**
	 * @param schools the schools to set
	 */
	public void setSchools(List<MealSchool> schools) {
		this.schools = schools;
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
	 * @return the districtId
	 */
	public Long getDistrictId() {
		return districtId;
	}
	/**
	 * @param districtId the districtId to set
	 */
	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}
	/**
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}
	/**
	 * @param pin the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}
	/**
	 * @return the loginAsAdmin
	 */
	public Boolean getLoginAsAdmin() {
		return loginAsAdmin;
	}
	/**
	 * @param loginAsAdmin the loginAsAdmin to set
	 */
	public void setLoginAsAdmin(Boolean loginAsAdmin) {
		this.loginAsAdmin = loginAsAdmin;
	}
	/**
	 * @return the nonSchoolDays
	 */
	public List<Integer> getNonSchoolDays() {
		return nonSchoolDays;
	}
	/**
	 * @param nonSchoolDays the nonSchoolDays to set
	 */
	public void setNonSchoolDays(List<Integer> nonSchoolDays) {
		this.nonSchoolDays = nonSchoolDays;
	}
	/**
	 * @return the schoolSessions
	 */
	public List<SchoolSession> getSchoolSessions() {
		return schoolSessions;
	}
	/**
	 * @param schoolSessions the schoolSessions to set
	 */
	public void setSchoolSessions(List<SchoolSession> schoolSessions) {
		this.schoolSessions = schoolSessions;
	}
	
}
