package com.mealManage.mealmodel.school;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mealManage.mealmodel.transaction.SchoolPayGatewayInfo;
import com.mealManage.mealmodel.user.SchoolUser;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "MealSchool_v2",
uniqueConstraints = @UniqueConstraint(columnNames = "school_id"), indexes = { 
	    @Index(columnList = "isActive")})
/**This entity having all those school which are OnBoarded and having school Admin users. Also it's having one to one relation with School**/
public class MealSchool extends CateringEntity implements Serializable{
	
	private static final long serialVersionUID = -4588992033547381798L;
	@Column(unique=true, nullable=false)
	private String subdomain;
	@NotNull
	private String schoolName;
	private Boolean isActive=true;

	 /*@ElementCollection(targetClass = SchoolGrades.class)
	 @CollectionTable(name = "mealschool_grades",joinColumns = @JoinColumn(name = "mealschool_id"))
	 @Enumerated(EnumType.STRING)
	 @Column(name = "grades_id")
	 private Set<SchoolGrades> grades ;*/
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "school_id")
	@NotNull
	private School school;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "mealSchool_id", nullable = false, updatable = false)
	private Set<SchoolUser> schoolUsers;
	private String logoLink;
	private String contactPName;
	private String contactPPhone;
	private String contactPEmail;
	@NotNull
	//@Enumerated(EnumType.STRING)
	private String schoolTimezone;
	@NotNull
	private Boolean isPaymentEnabled = false;
	private String stripeAccountId;
	private String public_key;
	@JsonIgnore
	private String secret_key;
	@NotNull
	private boolean isStripeAcceptance=false;	
	private String contactPAddress;
	private String principalName;
	private String principalEmail;
	private String principalPhone;
	private String principalAddress;	
	@NotNull
	@Size(min=2,max=2)
	private String countryCode;
	/*@NotNull
	private boolean isSupportSIS = false;
	@NotNull
	private boolean isSupportBCPrg = false;
	@NotNull
	private boolean isSupportFreeReducedPrg = false;
	@NotNull
	private boolean isSchoolProvideBreakfast = false;
	@NotNull
	private boolean isMenuByYear = false; //if it's true then menu by year else menu by month
	@NotNull
	private boolean isSupportStaffLunch = false;
	@NotNull
	private boolean isSupportInstantPayment = false;*/
	private String tierName;
	@JsonIgnore
	@Column(length = 65000, columnDefinition = "text")
	private String moduleAccessVal;
	@Transient
	private Map<String, String> moduleAccess;
	@NotNull
	private boolean isTrxFeeOnSchool = false;
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	@JoinColumn(name = "mealSchool_Id", nullable = false, updatable = false)
	private Set<SchoolPayGatewayInfo> paymentGateways;
	private Long catererId;
	private Long districtId;
	@Transient
	private Integer activeSchoolYear;
	private String nonSchoolDays; //0-Sun,1-Mon,2-Tue,3-Wed,4-Thru,5-Fri,6-Sat
	
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
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the school
	 */
	public School getSchool() {
		return school;
	}

	/**
	 * @param school the school to set
	 */
	public void setSchool(School school) {
		this.school = school;
	}

	/**
	 * @return the schoolUsers
	 */
	public Set<SchoolUser> getSchoolUsers() {
		return schoolUsers;
	}

	/**
	 * @param schoolUsers the schoolUsers to set
	 */
	public void setSchoolUsers(Set<SchoolUser> schoolUsers) {
		this.schoolUsers = schoolUsers;
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
		if(contactPPhone != null && contactPPhone.contains("+"))
			this.contactPPhone = "+"+contactPPhone.replaceAll("[^a-zA-Z0-9]", "");
		else if(contactPPhone != null)
			this.contactPPhone = contactPPhone.replaceAll("[^a-zA-Z0-9]", "");
		else
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
	 * @return the public_key
	 */
	public String getPublic_key() {
		return public_key;
	}

	/**
	 * @param public_key the public_key to set
	 */
	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	/**
	 * @return the secret_key
	 */
	public String getSecret_key() {
		return secret_key;
	}

	/**
	 * @param secret_key the secret_key to set
	 */
	public void setSecret_key(String secret_key) {
		this.secret_key = secret_key;
	}

	/**
	 * @return the isStripeAcceptance
	 */
	public boolean isStripeAcceptance() {
		return isStripeAcceptance;
	}

	/**
	 * @param isStripeAcceptance the isStripeAcceptance to set
	 */
	public void setStripeAcceptance(boolean isStripeAcceptance) {
		this.isStripeAcceptance = isStripeAcceptance;
	}

	/**
	 * @return the contactPAddress
	 */
	public String getContactPAddress() {
		return contactPAddress;
	}

	/**
	 * @param contactPAddress the contactPAddress to set
	 */
	public void setContactPAddress(String contactPAddress) {
		this.contactPAddress = contactPAddress;
	}

	/**
	 * @return the principalName
	 */
	public String getPrincipalName() {
		return principalName;
	}

	/**
	 * @param principalName the principalName to set
	 */
	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	/**
	 * @return the principalEmail
	 */
	public String getPrincipalEmail() {
		return principalEmail;
	}

	/**
	 * @param principalEmail the principalEmail to set
	 */
	public void setPrincipalEmail(String principalEmail) {
		this.principalEmail = principalEmail;
	}

	/**
	 * @return the principalPhone
	 */
	public String getPrincipalPhone() {
		return principalPhone;
	}

	/**
	 * @param principalPhone the principalPhone to set
	 */
	public void setPrincipalPhone(String principalPhone) {
		this.principalPhone = principalPhone;
	}

	/**
	 * @return the principalAddress
	 */
	public String getPrincipalAddress() {
		return principalAddress;
	}

	/**
	 * @param principalAddress the principalAddress to set
	 */
	public void setPrincipalAddress(String principalAddress) {
		this.principalAddress = principalAddress;
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
	 * @param moduleAccessVal the moduleAccessVal to set
	 */
	public void setModuleAccessVal(String moduleAccessVal) {
		this.moduleAccessVal = moduleAccessVal;
	}

	/**
	 * @return the moduleAccess
	 * @throws Exception
	 */
	public Map<String, String> getModuleAccess() throws Exception {
		if(moduleAccessVal != null){
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			TypeReference<Map<String, String>> mapType = new TypeReference<Map<String, String>>() {};
			this.moduleAccess= objectMapper.readValue(moduleAccessVal, mapType);
		}
		return moduleAccess;
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
	 * @return the moduleAccessVal
	 * @throws Exception
	 */
	public String getModuleAccessVal() {
		return moduleAccessVal;
	}

	/**
	 * @param moduleAccess the moduleAccess to set
	 * @throws JsonProcessingException 
	 */
	public void setModuleAccess(Map<String, String> moduleAccess) throws JsonProcessingException {
		if(moduleAccess != null){
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			this.moduleAccessVal = objectMapper.writeValueAsString(moduleAccess);
		}
		this.moduleAccess = moduleAccess;
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
	 * @return the activeSchoolYear
	 */
	public Integer getActiveSchoolYear() {
		return activeSchoolYear;
	}

	/**
	 * @param activeSchoolYear the activeSchoolYear to set
	 */
	public void setActiveSchoolYear(Integer activeSchoolYear) {
		this.activeSchoolYear = activeSchoolYear;
	}

	/**
	 * @return the nonSchoolDays
	 */
	public String getNonSchoolDays() {
		return nonSchoolDays;
	}

	/**
	 * @param nonSchoolDays the nonSchoolDays to set
	 */
	public void setNonSchoolDays(String nonSchoolDays) {
		this.nonSchoolDays = nonSchoolDays;
	}	
}
