package com.mealManage.mealmodel.school;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
import com.mealManage.domain.GradesInfo;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "CountryDetails",uniqueConstraints = @UniqueConstraint(columnNames = "countryCode"),
indexes = { @Index(columnList = "countryCode"), 
		@Index(columnList="countryName")})
public class CountryDetail extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -6341040785067802404L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@NotNull
	private String countryName;
	@NotNull
	@Size(min=2,max=2)
	private String countryCode;
	@NotNull
	private String isdCode;
	@Column(name = "currencySymbol", updatable = false, nullable = false)
	private String currencySymbol;
	@Transient
	private List<TimezoneDetails> timezoneDetails;
	@Column(length = 65535, columnDefinition = "text")
	private String timezoneInfo;
	private String phoneValidation;
	private String zipValidation;
	@Column(length = 65535, columnDefinition = "text")
	@JsonIgnore
	private String otherInfo;
	@Transient
	private Map<String, Object> otherInfoJson;
	@Column(length = 65535, columnDefinition = "text")
	@JsonIgnore
	private String gradesMapInfo;
	@Transient
	private List<GradesInfo> gradesMap;
	private String dateFormat;
	@JsonIgnore
	private String pageSizeStr;
	@Transient
	private Map<String, String> pageSize;
	private String currencyCode;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}
	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
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
	 * @return the currencySymbol
	 */
	public String getCurrencySymbol() {
		if(countryCode != null && countryCode.equalsIgnoreCase("IN"))
			return "₹";
		else
			return currencySymbol;
	}
	/**
	 * @param currencySymbol the currencySymbol to set
	 */
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	/**
	 * @return the timezoneDetails
	 */
	public List<TimezoneDetails> getTimezoneDetails() {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    	try{
    		TypeReference<List<TimezoneDetails>> mapType = new TypeReference<List<TimezoneDetails>>() {};
    		List<TimezoneDetails> jsonToOtherInfoList = objectMapper
					.readValue(timezoneInfo, mapType);
    		this.timezoneDetails=jsonToOtherInfoList;
    	} catch (Exception e) {}
		return timezoneDetails;
	}
	/**
	 * @param timezoneDetails the timezoneDetails to set
	 */
	public void setTimezoneDetails(List<TimezoneDetails> timezoneDetails) {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			String arrayToJson = objectMapper.writeValueAsString(timezoneDetails);
			this.timezoneInfo=arrayToJson;
		} catch (JsonProcessingException e) {}
		this.timezoneDetails = timezoneDetails;
	}
	/**
	 * @return the timezoneInfo
	 */
	public String getTimezoneInfo() {
		return timezoneInfo;
	}
	/**
	 * @param timezoneInfo the timezoneInfo to set
	 */
	public void setTimezoneInfo(String timezoneInfo) {
		this.timezoneInfo = timezoneInfo;
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
	 * @return the zipValidation
	 */
	public String getZipValidation() {
		return zipValidation;
	}
	/**
	 * @param zipValidation the zipValidation to set
	 */
	public void setZipValidation(String zipValidation) {
		this.zipValidation = zipValidation;
	}
	/**
	 * @return the otherInfo
	 */
	public String getOtherInfo() {
		return otherInfo;
	}
	/**
	 * @param otherInfo the otherInfo to set
	 */
	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}
	
	/**
	 * @return the gradesMapInfo
	 */
	public String getGradesMapInfo() {
		return gradesMapInfo;
	}
	/**
	 * @param gradesMapInfo the gradesMapInfo to set
	 */
	public void setGradesMapInfo(String gradesMapInfo) {
		this.gradesMapInfo = gradesMapInfo;
	}
	/**
	 * @return the otherInfoJson
	 */
	public Map<String, Object> getOtherInfoJson() {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    	try{
    		TypeReference<Map<String, Object>> mapType = new TypeReference<Map<String, Object>>() {};
    		Map<String, Object> jsonToOtherInfoList = objectMapper
					.readValue(otherInfo, mapType);
    		this.otherInfoJson=jsonToOtherInfoList;
    	} catch (Exception e) {}
		return otherInfoJson;
	}
	/**
	 * @param otherInfoJson the otherInfoJson to set
	 */
	public void setOtherInfoJson(Map<String, Object> otherInfoJson) {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			String arrayToJson = objectMapper.writeValueAsString(otherInfoJson);
			this.otherInfo=arrayToJson;
		} catch (JsonProcessingException e) {}
		this.otherInfoJson = otherInfoJson;
	}
	/**
	 * @return the otherInfoJson
	 */
	public List<GradesInfo> getGradesMap() {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    	try{
    		TypeReference<List<GradesInfo>> mapType = new TypeReference<List<GradesInfo>>() {};
    		List<GradesInfo> jsonToOtherInfoList = objectMapper
					.readValue(gradesMapInfo, mapType);
    		this.gradesMap=jsonToOtherInfoList;
    	} catch (Exception e) {}
		return gradesMap;
	}
	/**
	 * @param otherInfoJson the otherInfoJson to set
	 */
	public void setGradesMap(List<GradesInfo> gradesMap) {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			String arrayToJson = objectMapper.writeValueAsString(gradesMap);
			this.gradesMapInfo=arrayToJson;
		} catch (JsonProcessingException e) {}
		this.gradesMap = gradesMap;
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
	 * @return the pageSizeStr
	 */
	public String getPageSizeStr() {
		return pageSizeStr;
	}
	/**
	 * @param pageSizeStr the pageSizeStr to set
	 */
	public void setPageSizeStr(String pageSizeStr) {
		this.pageSizeStr = pageSizeStr;
	}
	
	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}
	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	/**
	 * @return the pageSize
	 */
	public Map<String, String> getPageSize() {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    	try{
    		TypeReference<Map<String, String>> mapType = new TypeReference<Map<String, String>>() {};
    		Map<String, String> jsonToOtherInfoList = objectMapper
					.readValue(pageSizeStr, mapType);
    		this.pageSize=jsonToOtherInfoList;
    	} catch (Exception e) {}
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Map<String, String> pageSize) {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			String arrayToJson = objectMapper.writeValueAsString(pageSize);
			this.pageSizeStr=arrayToJson;
		} catch (JsonProcessingException e) {}
		this.pageSize = pageSize;
	}
	
}
