package com.mealManage.mealmodel.reimbursement;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@DiscriminatorColumn(name = "TYPE")
@Table(name = "School_ReimbursementRate", 
	//uniqueConstraints={@UniqueConstraint(columnNames = {"schoolYear_Id", "reimbursementType","reimbursementMealsType"})}, 
	indexes = { @Index(columnList = "reimbursementType"),
	    @Index(columnList="reimbursementMealsType")})
/**This entity having all the school year related information along with session start and end date time**/
public class ReimbursementRatesInfo  implements Serializable{

	private static final long serialVersionUID = -6135003277303572838L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	@Enumerated(EnumType.STRING)
	@NotNull
	private ReimbursementType reimbursementType;
	@Enumerated(EnumType.STRING)
	@NotNull
	private ReimbursementMealsType reimbursementMealsType;
	@Column(precision = 6, scale = 4)
	@NotNull
	@Type(type = "big_decimal")
	private BigDecimal redFedReimbRate;
	@Column(precision = 6, scale = 4)
	@NotNull
	@Type(type = "big_decimal")
	private BigDecimal freFedReimbRate;
	@Column(precision = 6, scale = 4)
	@NotNull
	@Type(type = "big_decimal")
	private BigDecimal totFedReimbRate;
	@Column(precision = 6, scale = 4)
	@NotNull
	@Type(type = "big_decimal")
	private BigDecimal redStateReimbRate;
	@Column(precision = 6, scale = 4)
	@NotNull
	@Type(type = "big_decimal")
	private BigDecimal freStateReimbRate;
	@Column(precision = 6, scale = 4)
	@NotNull
	@Type(type = "big_decimal")
	private BigDecimal totStateReimbRate;
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
	 * @return the reimbursementType
	 */
	public ReimbursementType getReimbursementType() {
		return reimbursementType;
	}
	/**
	 * @param reimbursementType the reimbursementType to set
	 */
	public void setReimbursementType(ReimbursementType reimbursementType) {
		this.reimbursementType = reimbursementType;
	}
	/**
	 * @return the reimbursementMealsType
	 */
	public ReimbursementMealsType getReimbursementMealsType() {
		return reimbursementMealsType;
	}
	/**
	 * @param reimbursementMealsType the reimbursementMealsType to set
	 */
	public void setReimbursementMealsType(ReimbursementMealsType reimbursementMealsType) {
		this.reimbursementMealsType = reimbursementMealsType;
	}
	/**
	 * @return the redFedReimbRate
	 */
	public BigDecimal getRedFedReimbRate() {
		return redFedReimbRate;
	}
	/**
	 * @param redFedReimbRate the redFedReimbRate to set
	 */
	public void setRedFedReimbRate(BigDecimal redFedReimbRate) {
		this.redFedReimbRate = redFedReimbRate;
	}
	/**
	 * @return the freFedReimbRate
	 */
	public BigDecimal getFreFedReimbRate() {
		return freFedReimbRate;
	}
	/**
	 * @param freFedReimbRate the freFedReimbRate to set
	 */
	public void setFreFedReimbRate(BigDecimal freFedReimbRate) {
		this.freFedReimbRate = freFedReimbRate;
	}
	/**
	 * @return the totFedReimbRate
	 */
	public BigDecimal getTotFedReimbRate() {
		return totFedReimbRate;
	}
	/**
	 * @param totFedReimbRate the totFedReimbRate to set
	 */
	public void setTotFedReimbRate(BigDecimal totFedReimbRate) {
		this.totFedReimbRate = totFedReimbRate;
	}
	/**
	 * @return the redStateReimbRate
	 */
	public BigDecimal getRedStateReimbRate() {
		return redStateReimbRate;
	}
	/**
	 * @param redStateReimbRate the redStateReimbRate to set
	 */
	public void setRedStateReimbRate(BigDecimal redStateReimbRate) {
		this.redStateReimbRate = redStateReimbRate;
	}
	/**
	 * @return the freStateReimbRate
	 */
	public BigDecimal getFreStateReimbRate() {
		return freStateReimbRate;
	}
	/**
	 * @param freStateReimbRate the freStateReimbRate to set
	 */
	public void setFreStateReimbRate(BigDecimal freStateReimbRate) {
		this.freStateReimbRate = freStateReimbRate;
	}
	/**
	 * @return the totStateReimbRate
	 */
	public BigDecimal getTotStateReimbRate() {
		return totStateReimbRate;
	}
	/**
	 * @param totStateReimbRate the totStateReimbRate to set
	 */
	public void setTotStateReimbRate(BigDecimal totStateReimbRate) {
		this.totStateReimbRate = totStateReimbRate;
	}
}
