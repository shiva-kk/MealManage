package com.mealManage.response;

import java.util.Date;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface EventsResp {
	
	public String getFirstName();
	public String getLastName();
	public String getStudentId();
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonIgnore
	public Date getTransactionDateTime();
	@Transient
	public String getTrxDateTime();
	public Double getTransactionAmount();
	public String getNote();
	public String getTransactionDescription();
	public String getCreatedBy();
	public String getTransferId();
	@JsonIgnore
	public Long getRecId();
	@JsonIgnore
	public String getEventName();
	@JsonIgnore
	public String getEventType();
	public Long getUserId();
	@JsonIgnore
	public String getLongDesc();
	@JsonIgnore
	public Double getAmount();
	@Transient
	public void setTrxDateTime(String trxDateTime);
}
