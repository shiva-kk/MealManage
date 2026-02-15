package com.mealManage.domain;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import com.mealManage.mealmodel.transaction.EventInfo;
import com.mealManage.mealmodel.transaction.SchoolPayGatewayInfo;

public class SchoolDetailsInfo {
	
	private Long mealSchoolId;
	private String subdomain;
	private String schoolName;
	private String logoLink;
	private String contactPName;
	private String contactPPhone;
	private String contactPEmail;
	private String schoolTimezone;
	private Boolean isPaymentEnabled = false;
	private String stripeAccountId;
	private List<Map<String, Object>> schoolYears;
	private Boolean isBreakfastAvailable = false;
	private String currencySymbol;
	private String isdCode;
	private Map<String, Object> schoolOtherInfo;	
	private List<GradesInfo> gradesMap;
	private List<EventInfo> events;
	@Transient
	private Map<String, String> moduleAccess;
	private String tierName;
	private Set<SchoolPayGatewayInfo> paymentGateways;
	private List<BigInteger> pkgRegisteredStds;
	private String dateFormat;
	private String countryCode;
	private boolean isTrxFeeOnSchool = false;
	private String phoneValidation;
	private List<Integer> nonSchoolDays;
	/**
	 * @return the mealSchoolId
	 */
	public Long getMealSchoolId() {
		return mealSchoolId;
	}
	/**
	 * @param mealSchoolId the mealSchoolId to set
	 */
	public void setMealSchoolId(Long mealSchoolId) {
		this.mealSchoolId = mealSchoolId;
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
	 * @return the logoLink
	 */
	public String getLogoLink() {
		return logoLink;
	}
	/**
	 * @param logoLink the logoLink to set
	 */
	public void setLogoLink(String logoLink) {
		this.logoLink = logoLink;
	}
	/**
	 * @return the contactPName
	 */
	public String getContactPName() {
		return contactPName;
	}
	/**
	 * @param contactPName the contactPName to set
	 */
	public void setContactPName(String contactPName) {
		this.contactPName = contactPName;
	}
	/**
	 * @return the contactPPhone
	 */
	public String getContactPPhone() {
		return contactPPhone;
	}
	/**
	 * @param contactPPhone the contactPPhone to set
	 */
	public void setContactPPhone(String contactPPhone) {
		this.contactPPhone = contactPPhone;
	}
	/**
	 * @return the contactPEmail
	 */
	public String getContactPEmail() {
		return contactPEmail;
	}
	/**
	 * @param contactPEmail the contactPEmail to set
	 */
	public void setContactPEmail(String contactPEmail) {
		this.contactPEmail = contactPEmail;
	}
	
	/**
	 * @return the schoolTimezone
	 */
	public String getSchoolTimezone() {
		return schoolTimezone;
	}
	/**
	 * @param schoolTimezone the schoolTimezone to set
	 */
	public void setSchoolTimezone(String schoolTimezone) {
		this.schoolTimezone = schoolTimezone;
	}
	/**
	 * @return the isPaymentEnabled
	 */
	public Boolean getIsPaymentEnabled() {
		return isPaymentEnabled;
	}
	/**
	 * @param isPaymentEnabled the isPaymentEnabled to set
	 */
	public void setIsPaymentEnabled(Boolean isPaymentEnabled) {
		this.isPaymentEnabled = isPaymentEnabled;
	}
	/**
	 * @return the stripeAccountId
	 */
	public String getStripeAccountId() {
		return stripeAccountId;
	}
	/**
	 * @param stripeAccountId the stripeAccountId to set
	 */
	public void setStripeAccountId(String stripeAccountId) {
		this.stripeAccountId = stripeAccountId;
	}
	/**
	 * @return the schoolYears
	 */
	public List<Map<String, Object>> getSchoolYears() {
		return schoolYears;
	}
	/**
	 * @param schoolYears the schoolYears to set
	 */
	public void setSchoolYears(List<Map<String, Object>> schoolYears) {
		this.schoolYears = schoolYears;
	}
	/**
	 * @return the isBreakfastAvailable
	 */
	public Boolean getIsBreakfastAvailable() {
		return isBreakfastAvailable;
	}
	/**
	 * @param isBreakfastAvailable the isBreakfastAvailable to set
	 */
	public void setIsBreakfastAvailable(Boolean isBreakfastAvailable) {
		this.isBreakfastAvailable = isBreakfastAvailable;
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
	 * @return the events
	 */
	public List<EventInfo> getEvents() {
		return events;
	}
	/**
	 * @param events the events to set
	 */
	public void setEvents(List<EventInfo> events) {
		this.events = events;
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
	 * @return the pkgRegisteredStds
	 */
	public List<BigInteger> getPkgRegisteredStds() {
		return pkgRegisteredStds;
	}
	/**
	 * @param pkgRegisteredStds the pkgRegisteredStds to set
	 */
	public void setPkgRegisteredStds(List<BigInteger> pkgRegisteredStds) {
		this.pkgRegisteredStds = pkgRegisteredStds;
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
	
}
