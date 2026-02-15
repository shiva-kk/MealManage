package com.mealManage.mealmodel.transaction;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "MasterTransactionsAudit", indexes = { 
	    @Index(columnList = "transactionType"),
	    @Index(columnList="mealSchool_schoolId"),
	    @Index(columnList="paymentType")})
/**This class used for transactions (i.e. deposit/withdraw history details)**/
public class MasterTransactionsAudit extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = 5684883929287186311L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "MasterTransactionsAudit_RecId", nullable = false, updatable = false)
	private Set<StudentWiseTransaction> studentWiseTransactions;
	@ManyToOne
	@NotNull
	private MealSchool mealSchool;
	@Enumerated(EnumType.STRING)
	@NotNull
	private TransactionType transactionType;
	private Date transactionDateTime = new Date();
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	@NotNull
	private Double totalTransactionAmount; //in school country currency
	private String transactionDescription; // user can pass transaction desc as optional.
	private String note; //provide from backend some transaction details it means what type of transaction done.
	@Enumerated(EnumType.STRING)
	private PurchaseItemType purchaseItemType;
	private String chargeId;
	private String transferId;
	private String checkNumb;
	@Transient
	private Long mealSchoolId;
	@Transient
	private String transactionToken;
	private boolean isItemTaken = true;	
	private Long transferSourceRecId;
	@Transient
	@JsonIgnore
	private String sourceTransferAccInfo;
	@Transient
	private String parentUserEmails;
	@Transient
	private Double transactionFees;
	@Transient
	private Double appFeeAmount;
	@Transient
	private Double payToAmt;
	@Transient
	@JsonIgnore
	private Double accBalance;
	//private Long orderId;
	private Boolean isPrePaid;
	private String paymentGateway;
	private Double appFee;
	@Transient
	private Double chargedAmt;
	private Long locationId;
	private boolean directPosDeposit=false;
	private boolean posDeposit=false;
	
	/**
	 * @return the recId
	 */
	public Long getRecId() {
		return recId;
	}
	/**
	 * @param recId the recId to set
	 */
	public void setRecId(Long recId) {
		this.recId = recId;
	}
	/**
	 * @return the studentWiseTransactions
	 */
	public Set<StudentWiseTransaction> getStudentWiseTransactions() {
		return studentWiseTransactions;
	}
	/**
	 * @param studentWiseTransactions the studentWiseTransactions to set
	 */
	public void setStudentWiseTransactions(Set<StudentWiseTransaction> studentWiseTransactions) {
		this.studentWiseTransactions = studentWiseTransactions;
	}
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
	 * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}
	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	/**
	 * @return the transactionDateTime
	 */
	public Date getTransactionDateTime() {
		return transactionDateTime;
	}
	/**
	 * @param transactionDateTime the transactionDateTime to set
	 */
	public void setTransactionDateTime(Date transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
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
	 * @return the totalTransactionAmount
	 */
	public Double getTotalTransactionAmount() {
		return totalTransactionAmount;
	}
	/**
	 * @param totalTransactionAmount the totalTransactionAmount to set
	 */
	public void setTotalTransactionAmount(Double totalTransactionAmount) {
		this.totalTransactionAmount = totalTransactionAmount;
	}
	/**
	 * @return the transactionDescription
	 */
	public String getTransactionDescription() {
		return transactionDescription;
	}
	/**
	 * @param transactionDescription the transactionDescription to set
	 */
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * @return the purchaseItemType
	 */
	public PurchaseItemType getPurchaseItemType() {
		return purchaseItemType;
	}
	/**
	 * @param purchaseItemType the purchaseItemType to set
	 */
	public void setPurchaseItemType(PurchaseItemType purchaseItemType) {
		this.purchaseItemType = purchaseItemType;
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
	 * @return the isItemTaken
	 */
	public boolean isItemTaken() {
		return isItemTaken;
	}
	/**
	 * @param isItemTaken the isItemTaken to set
	 */
	public void setItemTaken(boolean isItemTaken) {
		this.isItemTaken = isItemTaken;
	}
	/**
	 * @return the transferSourceRecId
	 */
	public Long getTransferSourceRecId() {
		return transferSourceRecId;
	}
	/**
	 * @param transferSourceRecId the transferSourceRecId to set
	 */
	public void setTransferSourceRecId(Long transferSourceRecId) {
		this.transferSourceRecId = transferSourceRecId;
	}
	/**
	 * @return the sourceTransferAccInfo
	 */
	public String getSourceTransferAccInfo() {
		return sourceTransferAccInfo;
	}
	/**
	 * @param sourceTransferAccInfo the sourceTransferAccInfo to set
	 */
	public void setSourceTransferAccInfo(String sourceTransferAccInfo) {
		this.sourceTransferAccInfo = sourceTransferAccInfo;
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
	 * @return the payToAmt
	 */
	public Double getPayToAmt() {
		return payToAmt;
	}
	/**
	 * @param payToAmt the payToAmt to set
	 */
	public void setPayToAmt(Double payToAmt) {
		this.payToAmt = payToAmt;
	}
	/**
	 * @return the accBalance
	 */
	public Double getAccBalance() {
		return accBalance;
	}
	/**
	 * @param accBalance the accBalance to set
	 */
	public void setAccBalance(Double accBalance) {
		this.accBalance = accBalance;
	}
	/**
	 * @return the isPrePaid
	 */
	public Boolean getIsPrePaid() {
		return isPrePaid;
	}
	/**
	 * @param isPrePaid the isPrePaid to set
	 */
	public void setIsPrePaid(Boolean isPrePaid) {
		this.isPrePaid = isPrePaid;
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
	 * @return the chargedAmt
	 */
	public Double getChargedAmt() {
		return chargedAmt;
	}
	/**
	 * @param chargedAmt the chargedAmt to set
	 */
	public void setChargedAmt(Double chargedAmt) {
		this.chargedAmt = chargedAmt;
	}
	/**
	 * @return the locationId
	 */
	public Long getLocationId() {
		return locationId;
	}
	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	/**
	 * @return the directPosDeposit
	 */
	public boolean isDirectPosDeposit() {
		return directPosDeposit;
	}
	/**
	 * @param directPosDeposit the directPosDeposit to set
	 */
	public void setDirectPosDeposit(boolean directPosDeposit) {
		this.directPosDeposit = directPosDeposit;
	}
	/**
	 * @return the posDeposit
	 */
	public boolean isPosDeposit() {
		return posDeposit;
	}
	/**
	 * @param posDeposit the posDeposit to set
	 */
	public void setPosDeposit(boolean posDeposit) {
		this.posDeposit = posDeposit;
	}	
	
}
