package com.mealManage.response;

/**This POJO class used for map eligibility summary response**/
public class EligSummaryResp {
	
	private String schoolName;
	private Long mealSchoolId;
	private Integer schoolYear;
	private Integer incomeFree = 0;
	private Integer incomeRed = 0;
	private Integer incomePaid = 0;
	private Integer tempFree = 0;
	private Integer tempRed = 0;
	private Integer snap = 0;
	private Integer tanf = 0;
	private Integer fosterChild = 0;
	private Integer inst = 0;
	private Integer fdpir = 0;
	private Integer hdStart = 0;
	private Integer homeless = 0;
	private Integer migrant = 0;
	private Integer runway = 0;
	private Integer medicaidFree = 0;
	private Integer medicaidRed = 0;
	private Integer directCert = 0;
	private Integer customCase = 0;
	private Integer totalFree = 0;
	private Integer totalRed = 0;
	private Integer totalPaid = 0;
	private Integer totalInactive = 0;
	/**
	 * @return the schoolName
	 */
	public String getSchoolName() {
		return schoolName;
	}
	/**
	 * @param schoolName the schoolName to set
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
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
	 * @return the incomeFree
	 */
	public Integer getIncomeFree() {
		return incomeFree;
	}
	/**
	 * @param incomeFree the incomeFree to set
	 */
	public void setIncomeFree(Integer incomeFree) {
		this.incomeFree = incomeFree;
	}
	/**
	 * @return the incomeRed
	 */
	public Integer getIncomeRed() {
		return incomeRed;
	}
	/**
	 * @param incomeRed the incomeRed to set
	 */
	public void setIncomeRed(Integer incomeRed) {
		this.incomeRed = incomeRed;
	}
	/**
	 * @return the incomePaid
	 */
	public Integer getIncomePaid() {
		return incomePaid;
	}
	/**
	 * @param incomePaid the incomePaid to set
	 */
	public void setIncomePaid(Integer incomePaid) {
		this.incomePaid = incomePaid;
	}
	/**
	 * @return the tempFree
	 */
	public Integer getTempFree() {
		return tempFree;
	}
	/**
	 * @param tempFree the tempFree to set
	 */
	public void setTempFree(Integer tempFree) {
		this.tempFree = tempFree;
	}
	/**
	 * @return the tempRed
	 */
	public Integer getTempRed() {
		return tempRed;
	}
	/**
	 * @param tempRed the tempRed to set
	 */
	public void setTempRed(Integer tempRed) {
		this.tempRed = tempRed;
	}
	/**
	 * @return the snap
	 */
	public Integer getSnap() {
		return snap;
	}
	/**
	 * @param snap the snap to set
	 */
	public void setSnap(Integer snap) {
		this.snap = snap;
	}
	/**
	 * @return the tanf
	 */
	public Integer getTanf() {
		return tanf;
	}
	/**
	 * @param tanf the tanf to set
	 */
	public void setTanf(Integer tanf) {
		this.tanf = tanf;
	}
	/**
	 * @return the fosterChild
	 */
	public Integer getFosterChild() {
		return fosterChild;
	}
	/**
	 * @param fosterChild the fosterChild to set
	 */
	public void setFosterChild(Integer fosterChild) {
		this.fosterChild = fosterChild;
	}
	/**
	 * @return the inst
	 */
	public Integer getInst() {
		return inst;
	}
	/**
	 * @param inst the inst to set
	 */
	public void setInst(Integer inst) {
		this.inst = inst;
	}
	/**
	 * @return the fdpir
	 */
	public Integer getFdpir() {
		return fdpir;
	}
	/**
	 * @param fdpir the fdpir to set
	 */
	public void setFdpir(Integer fdpir) {
		this.fdpir = fdpir;
	}
	/**
	 * @return the hdStart
	 */
	public Integer getHdStart() {
		return hdStart;
	}
	/**
	 * @param hdStart the hdStart to set
	 */
	public void setHdStart(Integer hdStart) {
		this.hdStart = hdStart;
	}
	/**
	 * @return the homeless
	 */
	public Integer getHomeless() {
		return homeless;
	}
	/**
	 * @param homeless the homeless to set
	 */
	public void setHomeless(Integer homeless) {
		this.homeless = homeless;
	}
	/**
	 * @return the migrant
	 */
	public Integer getMigrant() {
		return migrant;
	}
	/**
	 * @param migrant the migrant to set
	 */
	public void setMigrant(Integer migrant) {
		this.migrant = migrant;
	}
	/**
	 * @return the runway
	 */
	public Integer getRunway() {
		return runway;
	}
	/**
	 * @param runway the runway to set
	 */
	public void setRunway(Integer runway) {
		this.runway = runway;
	}
	/**
	 * @return the medicaidFree
	 */
	public Integer getMedicaidFree() {
		return medicaidFree;
	}
	/**
	 * @param medicaidFree the medicaidFree to set
	 */
	public void setMedicaidFree(Integer medicaidFree) {
		this.medicaidFree = medicaidFree;
	}
	/**
	 * @return the medicaidRed
	 */
	public Integer getMedicaidRed() {
		return medicaidRed;
	}
	/**
	 * @param medicaidRed the medicaidRed to set
	 */
	public void setMedicaidRed(Integer medicaidRed) {
		this.medicaidRed = medicaidRed;
	}
	/**
	 * @return the directCert
	 */
	public Integer getDirectCert() {
		return directCert;
	}
	/**
	 * @param directCert the directCert to set
	 */
	public void setDirectCert(Integer directCert) {
		this.directCert = directCert;
	}
	/**
	 * @return the customCase
	 */
	public Integer getCustomCase() {
		return customCase;
	}
	/**
	 * @param customCase the customCase to set
	 */
	public void setCustomCase(Integer customCase) {
		this.customCase = customCase;
	}
	/**
	 * @return the totalFree
	 */
	public Integer getTotalFree() {
		return totalFree;
	}
	/**
	 * @param totalFree the totalFree to set
	 */
	public void setTotalFree(Integer totalFree) {
		this.totalFree = totalFree;
	}
	/**
	 * @return the totalRed
	 */
	public Integer getTotalRed() {
		return totalRed;
	}
	/**
	 * @param totalRed the totalRed to set
	 */
	public void setTotalRed(Integer totalRed) {
		this.totalRed = totalRed;
	}
	/**
	 * @return the totalPaid
	 */
	public Integer getTotalPaid() {
		return totalPaid;
	}
	/**
	 * @param totalPaid the totalPaid to set
	 */
	public void setTotalPaid(Integer totalPaid) {
		this.totalPaid = totalPaid;
	}
	/**
	 * @return the totalInactive
	 */
	public Integer getTotalInactive() {
		return totalInactive;
	}
	/**
	 * @param totalInactive the totalInactive to set
	 */
	public void setTotalInactive(Integer totalInactive) {
		this.totalInactive = totalInactive;
	}
	
}
