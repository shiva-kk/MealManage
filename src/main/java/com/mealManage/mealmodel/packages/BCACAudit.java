package com.mealManage.mealmodel.packages;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "BCACAudit", indexes = {@Index(columnList="subTrxByStd_recId")})
/**This entity used for all the School Packages info**/
public class BCACAudit  extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 4274550184052803838L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long bcacAuditID;
	@ManyToOne
	@NotNull
	private SubscriptionsTrxByStd subTrxByStd;
	private Date checkIn;
	private Date checkOut;
	private String pickupBy;
	
	/**
	 * @return the bcacAuditID
	 */
	public Long getBcacAuditID() {
		return bcacAuditID;
	}
	/**
	 * @param bcacAuditID the bcacAuditID to set
	 */
	public void setBcacAuditID(Long bcacAuditID) {
		this.bcacAuditID = bcacAuditID;
	}
	/**
	 * @return the subTrxByStd
	 */
	public SubscriptionsTrxByStd getSubTrxByStd() {
		return subTrxByStd;
	}
	/**
	 * @param subTrxByStd the subTrxByStd to set
	 */
	public void setSubTrxByStd(SubscriptionsTrxByStd subTrxByStd) {
		this.subTrxByStd = subTrxByStd;
	}
	/**
	 * @return the checkIn
	 */
	public Date getCheckIn() {
		return checkIn;
	}
	/**
	 * @param checkIn the checkIn to set
	 */
	public void setCheckIn(Date checkIn) {
		this.checkIn = checkIn;
	}
	/**
	 * @return the checkOut
	 */
	public Date getCheckOut() {
		return checkOut;
	}
	/**
	 * @param checkOut the checkOut to set
	 */
	public void setCheckOut(Date checkOut) {
		this.checkOut = checkOut;
	}
	/**
	 * @return the pickupBy
	 */
	public String getPickupBy() {
		return pickupBy;
	}
	/**
	 * @param pickupBy the pickupBy to set
	 */
	public void setPickupBy(String pickupBy) {
		this.pickupBy = pickupBy;
	}
	
}
