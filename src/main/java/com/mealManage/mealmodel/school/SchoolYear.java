package com.mealManage.mealmodel.school;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import com.mealManage.mealmodel.reimbursement.ReimbursementRatesInfo;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "MealSchool_SchoolYear", uniqueConstraints={
	    @UniqueConstraint(columnNames = {"mealSchool_schoolId", "schoolYear"})}, indexes = { 
	    @Index(columnList = "schoolYear"),
	    @Index(columnList="mealSchool_schoolId")})
/**This entity having all the school year related information along with session start and end date time**/
public class SchoolYear extends CateringEntity implements Serializable{
	
	private static final long serialVersionUID = -4588992033547381798L;
	
	@ManyToOne
	@NotNull
	private MealSchool  mealSchool;
	@NotNull
	private Integer schoolYear;
	@NotNull
	private Date sessionStartDateTime;
	@NotNull
	private Date sessionEndDateTime;
	@Column(precision = 5, scale = 2)
	@Type(type = "big_decimal")
	private BigDecimal attendanceFactor;
	private double emrgLunchRegPrice;
	private double emrgLunchRedPrice;
	private double emrgLunchFreePrice;
	private double emrgMilkRegPrice;
	private double emrgMilkRedPrice;
	private double emrgMilkFreePrice;
	@NotNull
	private Boolean isEmergeLunch = false;
	@NotNull
	private Boolean isEmergeLunchReimburse = false;
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	@JoinColumn(name = "schoolYear_Id", nullable = false, updatable = false)
	private Set<ReimbursementRatesInfo> reimbursementRatesInfos = new HashSet<ReimbursementRatesInfo>();
	@Transient
	private String schoolPdfBase64;
	private String schoolPdfUrl;
	private double breakfastReducedCents;
	private double lunchReducedCents;
	private Integer duePkgNotificationDays;
	private Date pkgDueNotificationLastRun;
	private Double endingBal;
	private boolean isPOSIdVerificationReq=false;
	private Date schoolActualStartDate;
	private Date reCertificationDueDate;
	private Boolean isFreeMeal;
	
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
	 * @return the schoolYear
	 */
	public Integer getSchoolYear() {
		return schoolYear;
	}
	/**
	 * @param schoolYear the schoolYear to set
	 */
	public void setSchoolYear(Integer schoolYear) {
		this.schoolYear = schoolYear;
	}
	/**
	 * @return the sessionStartDateTime
	 */
	public Date getSessionStartDateTime() {
		return sessionStartDateTime;
	}
	/**
	 * @param sessionStartDateTime the sessionStartDateTime to set
	 */
	public void setSessionStartDateTime(Date sessionStartDateTime) {
		this.sessionStartDateTime = sessionStartDateTime;
	}
	/**
	 * @return the sessionEndDateTime
	 */
	public Date getSessionEndDateTime() {
		return sessionEndDateTime;
	}
	/**
	 * @param sessionEndDateTime the sessionEndDateTime to set
	 */
	public void setSessionEndDateTime(Date sessionEndDateTime) {
		this.sessionEndDateTime = sessionEndDateTime;
	}
	/**
	 * @return the attendanceFactor
	 */
	public BigDecimal getAttendanceFactor() {
		return attendanceFactor;
	}
	/**
	 * @param attendanceFactor the attendanceFactor to set
	 */
	public void setAttendanceFactor(BigDecimal attendanceFactor) {
		this.attendanceFactor = attendanceFactor;
	}
	/**
	 * @return the emrgLunchRegPrice
	 */
	public Double getEmrgLunchRegPrice() {
		return emrgLunchRegPrice;
	}
	/**
	 * @param emrgLunchRegPrice the emrgLunchRegPrice to set
	 */
	public void setEmrgLunchRegPrice(Double emrgLunchRegPrice) {
		this.emrgLunchRegPrice = emrgLunchRegPrice;
	}
	/**
	 * @return the emrgLunchRedPrice
	 */
	public Double getEmrgLunchRedPrice() {
		return emrgLunchRedPrice;
	}
	/**
	 * @param emrgLunchRedPrice the emrgLunchRedPrice to set
	 */
	public void setEmrgLunchRedPrice(Double emrgLunchRedPrice) {
		this.emrgLunchRedPrice = emrgLunchRedPrice;
	}
	/**
	 * @return the emrgLunchFreePrice
	 */
	public Double getEmrgLunchFreePrice() {
		return emrgLunchFreePrice;
	}
	/**
	 * @param emrgLunchFreePrice the emrgLunchFreePrice to set
	 */
	public void setEmrgLunchFreePrice(Double emrgLunchFreePrice) {
		this.emrgLunchFreePrice = emrgLunchFreePrice;
	}
	/**
	 * @return the emrgMilkRegPrice
	 */
	public Double getEmrgMilkRegPrice() {
		return emrgMilkRegPrice;
	}
	/**
	 * @param emrgMilkRegPrice the emrgMilkRegPrice to set
	 */
	public void setEmrgMilkRegPrice(Double emrgMilkRegPrice) {
		this.emrgMilkRegPrice = emrgMilkRegPrice;
	}
	/**
	 * @return the emrgMilkRedPrice
	 */
	public Double getEmrgMilkRedPrice() {
		return emrgMilkRedPrice;
	}
	/**
	 * @param emrgMilkRedPrice the emrgMilkRedPrice to set
	 */
	public void setEmrgMilkRedPrice(Double emrgMilkRedPrice) {
		this.emrgMilkRedPrice = emrgMilkRedPrice;
	}
	/**
	 * @return the emrgMilkFreePrice
	 */
	public Double getEmrgMilkFreePrice() {
		return emrgMilkFreePrice;
	}
	/**
	 * @param emrgMilkFreePrice the emrgMilkFreePrice to set
	 */
	public void setEmrgMilkFreePrice(Double emrgMilkFreePrice) {
		this.emrgMilkFreePrice = emrgMilkFreePrice;
	}
	/**
	 * @return the reimbursementRatesInfos
	 */
	public Set<ReimbursementRatesInfo> getReimbursementRatesInfos() {
		return reimbursementRatesInfos;
	}
	/**
	 * @param reimbursementRatesInfos the reimbursementRatesInfos to set
	 */
	public void setReimbursementRatesInfos(Set<ReimbursementRatesInfo> reimbursementRatesInfos) {
		this.reimbursementRatesInfos = reimbursementRatesInfos;
	}
	/**
	 * @return the isEmergeLunch
	 */
	public boolean isEmergeLunch() {
		return isEmergeLunch;
	}
	/**
	 * @param isEmergeLunch the isEmergeLunch to set
	 */
	public void setEmergeLunch(boolean isEmergeLunch) {
		this.isEmergeLunch = isEmergeLunch;
	}
	/**
	 * @return the isEmergeLunchReimburse
	 */
	public Boolean getIsEmergeLunchReimburse() {
		return isEmergeLunchReimburse;
	}
	/**
	 * @param isEmergeLunchReimburse the isEmergeLunchReimburse to set
	 */
	public void setIsEmergeLunchReimburse(Boolean isEmergeLunchReimburse) {
		this.isEmergeLunchReimburse = isEmergeLunchReimburse;
	}
	
	/**
	 * @return the schoolPdfBase64
	 */
	public String getSchoolPdfBase64() {
		return schoolPdfBase64;
	}
	/**
	 * @param schoolPdfBase64 the schoolPdfBase64 to set
	 */
	public void setSchoolPdfBase64(String schoolPdfBase64) {
		this.schoolPdfBase64 = schoolPdfBase64;
	}
	/**
	 * @return the schoolPdfUrl
	 */
	public String getSchoolPdfUrl() {
		return schoolPdfUrl;
	}
	/**
	 * @param schoolPdfUrl the schoolPdfUrl to set
	 */
	public void setSchoolPdfUrl(String schoolPdfUrl) {
		this.schoolPdfUrl = schoolPdfUrl;
	}
	/**
	 * @return the breakfastReducedCents
	 */
	public double getBreakfastReducedCents() {
		return breakfastReducedCents;
	}
	/**
	 * @param breakfastReducedCents the breakfastReducedCents to set
	 */
	public void setBreakfastReducedCents(double breakfastReducedCents) {
		this.breakfastReducedCents = breakfastReducedCents;
	}
	/**
	 * @return the lunchReducedCents
	 */
	public double getLunchReducedCents() {
		return lunchReducedCents;
	}
	/**
	 * @param lunchReducedCents the lunchReducedCents to set
	 */
	public void setLunchReducedCents(double lunchReducedCents) {
		this.lunchReducedCents = lunchReducedCents;
	}
	/**
	 * @return the duePkgNotificationDays
	 */
	public Integer getDuePkgNotificationDays() {
		return duePkgNotificationDays;
	}
	/**
	 * @param duePkgNotificationDays the duePkgNotificationDays to set
	 */
	public void setDuePkgNotificationDays(Integer duePkgNotificationDays) {
		this.duePkgNotificationDays = duePkgNotificationDays;
	}
	/**
	 * @return the pkgDueNotificationLastRun
	 */
	public Date getPkgDueNotificationLastRun() {
		return pkgDueNotificationLastRun;
	}
	/**
	 * @param pkgDueNotificationLastRun the pkgDueNotificationLastRun to set
	 */
	public void setPkgDueNotificationLastRun(Date pkgDueNotificationLastRun) {
		this.pkgDueNotificationLastRun = pkgDueNotificationLastRun;
	}
	/**
	 * @return the endingBal
	 */
	public Double getEndingBal() {
		return endingBal;
	}
	/**
	 * @param endingBal the endingBal to set
	 */
	public void setEndingBal(Double endingBal) {
		this.endingBal = endingBal;
	}
	/**
	 * @return the isPOSIdVerificationReq
	 */
	public boolean isPOSIdVerificationReq() {
		return isPOSIdVerificationReq;
	}
	/**
	 * @param isPOSIdVerificationReq the isPOSIdVerificationReq to set
	 */
	public void setPOSIdVerificationReq(boolean isPOSIdVerificationReq) {
		this.isPOSIdVerificationReq = isPOSIdVerificationReq;
	}
	/**
	 * @return the schoolActualStartDate
	 */
	public Date getSchoolActualStartDate() {
		return schoolActualStartDate;
	}
	/**
	 * @param schoolActualStartDate the schoolActualStartDate to set
	 */
	public void setSchoolActualStartDate(Date schoolActualStartDate) {
		this.schoolActualStartDate = schoolActualStartDate;
	}
	/**
	 * @return the reCertificationDueDate
	 */
	public Date getReCertificationDueDate() {
		return reCertificationDueDate;
	}
	/**
	 * @param reCertificationDueDate the reCertificationDueDate to set
	 */
	public void setReCertificationDueDate(Date reCertificationDueDate) {
		this.reCertificationDueDate = reCertificationDueDate;
	}
	/**
	 * @return the isFreeMeal
	 */
	public Boolean getIsFreeMeal() {
		return isFreeMeal;
	}
	/**
	 * @param isFreeMeal the isFreeMeal to set
	 */
	public void setIsFreeMeal(Boolean isFreeMeal) {
		this.isFreeMeal = isFreeMeal;
	}
	
}
