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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mealManage.domain.PaymentGatewayForm;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table
/**This entity used for capture the school's payment gateway info**/
public class SchoolPayGatewayInfo implements Serializable{
	
	private static final long serialVersionUID = 4274550184052803838L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long schoolPayGatewayId;
	@ManyToOne(optional = false)
    @JoinColumn(name="payGatewayId")
	private PaymentGateway paymentGateway;
	@JsonIgnore
	@Column(length = 65535, columnDefinition = "text")
	private String gatewayInfo;
	@Transient
	private List<PaymentGatewayForm> formValues;
	@Transient
	private String payGatewayName;
	
	/**
	 * @return the schoolPayGatewayId
	 */
	public Long getSchoolPayGatewayId() {
		return schoolPayGatewayId;
	}
	/**
	 * @param schoolPayGatewayId the schoolPayGatewayId to set
	 */
	public void setSchoolPayGatewayId(Long schoolPayGatewayId) {
		this.schoolPayGatewayId = schoolPayGatewayId;
	}
	/**
	 * @return the paymentGateway
	 */
	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}
	/**
	 * @param paymentGateway the paymentGateway to set
	 */
	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
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
	/**
	 * @return the payGatewayName
	 */
	public String getPayGatewayName() {
		this.payGatewayName = paymentGateway.getName();
		return payGatewayName;
	}	
}
