package com.mealManage.mealmodel.transaction;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mealManage.domain.PaymentGatewayForm;
import com.mealManage.mealmodel.school.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table
/**This entity used for capture the payment gateway info**/
public class PaymentGateway extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = 4274550184052803838L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(unique=true)
	private String name;
	@JsonIgnore
	@Column(length = 65535, columnDefinition = "text")
	private String gatewayInfo;
	@Transient
	private List<PaymentGatewayForm> formValues;
	
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the gatewayInfo
	 */
	public String getGatewayInfo() {
		return gatewayInfo;
	}
	/**
	 * @param gatewayInfo the gatewayInfo to set
	 */
	public void setGatewayInfo(String gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}
	/**
	 * @return the formValues
	 */
	public List<PaymentGatewayForm> getFormValues() {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    	try{
    		TypeReference<List<PaymentGatewayForm>> mapType = new TypeReference<List<PaymentGatewayForm>>() {};
    		List<PaymentGatewayForm> jsonToOtherInfoList = objectMapper.readValue(gatewayInfo, mapType);
    		this.formValues=jsonToOtherInfoList;
    	} catch (Exception e) {}
		return formValues;
	}
	/**
	 * @param formValues the formValues to set
	 */
	public void setFormValues(List<PaymentGatewayForm> formValues) {
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			String arrayToJson = objectMapper.writeValueAsString(formValues);
			this.gatewayInfo=arrayToJson;
		} catch (JsonProcessingException e) {}
		this.formValues = formValues;
	}
}
