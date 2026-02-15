package com.mealManage.mealmodel.packages;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.transaction.PaymentType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "PackageSubscriptionsTrx", indexes = { 
	    @Index(columnList="mealSchool_schoolId")})
/**This class used for Package payment master transactions**/
public class PackageSubscriptionsTrx extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = 5684883929287186311L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trxId", updatable = false, nullable = false)
	private Long trxId;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "PackageSubscriptionsTrx_trxId", nullable = false, updatable = false)
	private Set<SubscriptionsTrxByStd> subscriptionsTrxByStds;
	@ManyToOne
	@NotNull
	private MealSchool mealSchool;
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	@NotNull
	private Double totalPaidAmt; //in school country currency
	private String chargeId;
	private String transferId;
	private String checkNumb;
	@Transient
	private String transactionToken;
	@Transient
	private String parentUserEmails;
	@Transient
	private Double transactionFees;
	@Transient
	private Double appFeeAmount;
	private String paymentGateway;
	private Double appFee;
	@Transient
	private List<PickupAuthorized> pickupAuthorizeds;
	private boolean isPaid = true;
	
	/**
	 * @return the mealSchool
	 */
	public MealSchool getMealSchool() {
		return mealSchool;
	}
	/**
	 * @param mealSchool the mealSchool to set
	 */
	public void setMealSchool(MealSchool mealSchool) {
		this.mealSchool = mealSchool;
	}
	/**
	 * @return the paymentType
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}
	/**
	 * @param paymentType the paymentType to set
	 */
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	/**
	 * @return the chargeId
	 */
	public String getChargeId() {
		return chargeId;
	}
	/**
	 * @param chargeId the chargeId to set
	 */
	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
	}
	/**
	 * @return the transferId
	 */
	public String getTransferId() {
		return transferId;
	}
	/**
	 * @param transferId the transferId to set
	 */
	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}
	
	/**
	 * @return the checkNumb
	 */
	public String getCheckNumb() {
		return checkNumb;
	}
	/**
	 * @param checkNumb the checkNumb to set
	 */
	public void setCheckNumb(String checkNumb) {
		this.checkNumb = checkNumb;
	}
	/**
	 * @return the transactionToken
	 */
	public String getTransactionToken() {
		return transactionToken;
	}
	/**
	 * @param transactionToken the transactionToken to set
	 */
	public void setTransactionToken(String transactionToken) {
		this.transactionToken = transactionToken;
	}
	/**
	 * @return the parentUserEmails
	 */
	public String getParentUserEmails() {
		return parentUserEmails;
	}
	/**
	 * @param parentUserEmails the parentUserEmails to set
	 */
	public void setParentUserEmails(String parentUserEmails) {
		this.parentUserEmails = parentUserEmails;
	}
	/**
	 * @return the transactionFees
	 */
	public Double getTransactionFees() {
		return transactionFees;
	}
	/**
	 * @param transactionFees the transactionFees to set
	 */
	public void setTransactionFees(Double transactionFees) {
		this.transactionFees = transactionFees;
	}
	/**
	 * @return the appFeeAmount
	 */
	public Double getAppFeeAmount() {
		return appFeeAmount;
	}
	/**
	 * @param appFeeAmount the appFeeAmount to set
	 */
	public void setAppFeeAmount(Double appFeeAmount) {
		this.appFeeAmount = appFeeAmount;
	}
	/**
	 * @return the paymentGateway
	 */
	public String getPaymentGateway() {
		return paymentGateway;
	}
	/**
	 * @param paymentGateway the paymentGateway to set
	 */
	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}
	/**
	 * @return the appFee
	 */
	public Double getAppFee() {
		return appFee;
	}
	/**
	 * @param appFee the appFee to set
	 */
	public void setAppFee(Double appFee) {
		this.appFee = appFee;
	}
	
	/**
	 * @return the trxId
	 */
	public Long getTrxId() {
		return trxId;
	}
	/**
	 * @param trxId the trxId to set
	 */
	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}
	/**
	 * @return the subscriptionsTrxByStds
	 */
	public Set<SubscriptionsTrxByStd> getSubscriptionsTrxByStds() {
		return subscriptionsTrxByStds;
	}
	/**
	 * @param subscriptionsTrxByStds the subscriptionsTrxByStds to set
	 */
	public void setSubscriptionsTrxByStds(Set<SubscriptionsTrxByStd> subscriptionsTrxByStds) {
		this.subscriptionsTrxByStds = subscriptionsTrxByStds;
	}
	/**
	 * @return the totalPaidAmt
	 */
	public Double getTotalPaidAmt() {
		return totalPaidAmt;
	}
	/**
	 * @param totalPaidAmt the totalPaidAmt to set
	 */
	public void setTotalPaidAmt(Double totalPaidAmt) {
		this.totalPaidAmt = totalPaidAmt;
	}
	/**
	 * @return the pickupAuthorizeds
	 */
	public List<PickupAuthorized> getPickupAuthorizeds() {
		return pickupAuthorizeds;
	}
	/**
	 * @param pickupAuthorizeds the pickupAuthorizeds to set
	 */
	public void setPickupAuthorizeds(List<PickupAuthorized> pickupAuthorizeds) {
		this.pickupAuthorizeds = pickupAuthorizeds;
	}
	/**
	 * @return the isPaid
	 */
	public boolean isPaid() {
		return isPaid;
	}
	/**
	 * @param isPaid the isPaid to set
	 */
	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
	
}
