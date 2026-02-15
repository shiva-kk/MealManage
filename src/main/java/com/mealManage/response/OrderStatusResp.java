package com.mealManage.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderStatusResp {
	
	private Boolean orderStatus;
	private Double totalPrice;
	private Boolean paymentStatus;
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
	private Date orderedDate;
	/**
	 * @return the orderStatus
	 */
	public Boolean getOrderStatus() {
		return orderStatus;
	}
	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(Boolean orderStatus) {
		this.orderStatus = orderStatus;
	}
	/**
	 * @return the totalPrice
	 */
	public Double getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**
	 * @return the paymentStatus
	 */
	public Boolean getPaymentStatus() {
		return paymentStatus;
	}
	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	/**
	 * @return the orderedDate
	 */
	public Date getOrderedDate() {
		return orderedDate;
	}
	/**
	 * @param orderedDate the orderedDate to set
	 */
	public void setOrderedDate(Date orderedDate) {
		this.orderedDate = orderedDate;
	}
}
