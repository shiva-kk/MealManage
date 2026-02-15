package com.mealManage.domain;

import java.util.List;

public class HouseholdAppOtherInfo {
	
	private String fname;
	private String lname;
	private String mname;
	private Boolean filledTheApplication;
	private String personType;
	private List<AssistanceProgram> assistancePrograms;
	private List<IncomeInfo> income;
	private ContactInfo contact;
	private Boolean noIncome;
	private Double totalIncome;
	private String ssn;
	private Boolean noSSN;
	private String grade;
	private Boolean liveUnderFosterCare;
	private Boolean homelessOrMigrantOrRunaway;
	private String ethenticity;
	private List<String> race;
	private Boolean isFreeMeal;
	private Long studentRecId;
	private String decisionReason;
	private String category;
	private String actualPrg;
	
	/**
	 * @return the fname
	 */
	public String getFname() {
		if(fname != null)
			return fname.trim();
		return fname;
	}
	/**
	 * @param fname the fname to set
	 */
	public void setFname(String fname) {
		this.fname = fname;
	}
	/**
	 * @return the lname
	 */
	public String getLname() {
		if(lname != null)
			return lname.trim();
		return lname;
	}
	/**
	 * @param lname the lname to set
	 */
	public void setLname(String lname) {
		this.lname = lname;
	}
	/**
	 * @return the mname
	 */
	public String getMname() {
		if(mname != null)
			return mname.trim();
		return mname;
	}
	/**
	 * @param mname the mname to set
	 */
	public void setMname(String mname) {
		this.mname = mname;
	}
	/**
	 * @return the filledTheApplication
	 */
	public Boolean getFilledTheApplication() {
		return filledTheApplication;
	}
	/**
	 * @param filledTheApplication the filledTheApplication to set
	 */
	public void setFilledTheApplication(Boolean filledTheApplication) {
		this.filledTheApplication = filledTheApplication;
	}
	/**
	 * @return the personType
	 */
	public String getPersonType() {
		return personType;
	}
	/**
	 * @param personType the personType to set
	 */
	public void setPersonType(String personType) {
		this.personType = personType;
	}
	/**
	 * @return the assistancePrograms
	 */
	public List<AssistanceProgram> getAssistancePrograms() {
		return assistancePrograms;
	}
	/**
	 * @param assistancePrograms the assistancePrograms to set
	 */
	public void setAssistancePrograms(List<AssistanceProgram> assistancePrograms) {
		this.assistancePrograms = assistancePrograms;
	}
	/**
	 * @return the income
	 */
	public List<IncomeInfo> getIncome() {
		return income;
	}
	/**
	 * @param income the income to set
	 */
	public void setIncome(List<IncomeInfo> income) {
		this.income = income;
	}
	/**
	 * @return the contact
	 */
	public ContactInfo getContact() {
		return contact;
	}
	/**
	 * @param contact the contact to set
	 */
	public void setContact(ContactInfo contact) {
		this.contact = contact;
	}
	/**
	 * @return the noIncome
	 */
	public Boolean getNoIncome() {
		return noIncome;
	}
	/**
	 * @param noIncome the noIncome to set
	 */
	public void setNoIncome(Boolean noIncome) {
		this.noIncome = noIncome;
	}
	
	/**
	 * @return the totalIncome
	 */
	public Double getTotalIncome() {
		return totalIncome;
	}
	/**
	 * @param totalIncome the totalIncome to set
	 */
	public void setTotalIncome(Double totalIncome) {
		this.totalIncome = totalIncome;
	}
	
	/**
	 * @return the ssn
	 */
	public String getSsn() {
		return ssn;
	}
	/**
	 * @param ssn the ssn to set
	 */
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	/**
	 * @return the noSSN
	 */
	public Boolean getNoSSN() {
		return noSSN;
	}
	/**
	 * @param noSSN the noSSN to set
	 */
	public void setNoSSN(Boolean noSSN) {
		this.noSSN = noSSN;
	}
	/**
	 * @return the grade
	 */
	public String getGrade() {
		return grade;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}
	/**
	 * @return the liveUnderFosterCare
	 */
	public Boolean getLiveUnderFosterCare() {
		return liveUnderFosterCare;
	}
	/**
	 * @param liveUnderFosterCare the liveUnderFosterCare to set
	 */
	public void setLiveUnderFosterCare(Boolean liveUnderFosterCare) {
		this.liveUnderFosterCare = liveUnderFosterCare;
	}
	/**
	 * @return the homelessOrMigrantOrRunaway
	 */
	public Boolean getHomelessOrMigrantOrRunaway() {
		return homelessOrMigrantOrRunaway;
	}
	/**
	 * @param homelessOrMigrantOrRunaway the homelessOrMigrantOrRunaway to set
	 */
	public void setHomelessOrMigrantOrRunaway(Boolean homelessOrMigrantOrRunaway) {
		this.homelessOrMigrantOrRunaway = homelessOrMigrantOrRunaway;
	}
	/**
	 * @return the ethenticity
	 */
	public String getEthenticity() {
		return ethenticity;
	}
	/**
	 * @param ethenticity the ethenticity to set
	 */
	public void setEthenticity(String ethenticity) {
		this.ethenticity = ethenticity;
	}
	/**
	 * @return the race
	 */
	public List<String> getRace() {
		return race;
	}
	/**
	 * @param race the race to set
	 */
	public void setRace(List<String> race) {
		this.race = race;
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
	/**
	 * @return the studentRecId
	 */
	public Long getStudentRecId() {
		return studentRecId;
	}
	/**
	 * @param studentRecId the studentRecId to set
	 */
	public void setStudentRecId(Long studentRecId) {
		this.studentRecId = studentRecId;
	}
	/**
	 * @return the decisionReason
	 */
	public String getDecisionReason() {
		return decisionReason;
	}
	/**
	 * @param decisionReason the decisionReason to set
	 */
	public void setDecisionReason(String decisionReason) {
		this.decisionReason = decisionReason;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the actualPrg
	 */
	public String getActualPrg() {
		return actualPrg;
	}
	/**
	 * @param actualPrg the actualPrg to set
	 */
	public void setActualPrg(String actualPrg) {
		this.actualPrg = actualPrg;
	}
	
}
