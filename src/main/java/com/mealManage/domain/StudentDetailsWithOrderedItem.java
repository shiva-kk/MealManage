package com.mealManage.domain;

import java.util.List;
import java.util.Map;

import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.school.SchoolGrades;

/**This class contains students details with ordered item details**/
public class StudentDetailsWithOrderedItem {
	
	private Long studentRecId;
	private String firstName;
	private String lastName;
	private SchoolGrades gradeName;
	private String studentId;
	private Long mealSchoolId;
	private String allergies;
	private String teacherName;
	private String barCode;
	private Boolean isReducePriceEligible=false;
	private Boolean isFreeMealEligible=false;
	private Boolean isBeforeCare;
	private Boolean hasMilkCard;
	private Boolean isEnrollBCAndACPkt;
	private String schoolStudentId;
	private List<OrderedMenuItemDetails> menuItemDetails;
	private Double accBalance;
	private Boolean isThresholdBal = false; //if a/c balance less than equal min threshold that time it'll return true else false.
	private Map<MealType, List<MenuItemDetailsV2>> menuByType;
	private String pin;
	private String image;
	private Double thresholdAmt;
	private String additionalNotes;
	
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
	 * @return the firstName
	 */
	public String getFirstName() {
		if(firstName != null)
			return firstName.trim();
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		if(lastName != null)
			return lastName.trim();
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the gradeName
	 */
	public SchoolGrades getGradeName() {
		return gradeName;
	}
	/**
	 * @param gradeName the gradeName to set
	 */
	public void setGradeName(SchoolGrades gradeName) {
		this.gradeName = gradeName;
	}
	/**
	 * @return the studentId
	 */
	public String getStudentId() {
		return studentId;
	}
	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(String studentId) {
		this.studentId = studentId;
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
	 * @return the allergies
	 */
	public String getAllergies() {
		return allergies;
	}
	/**
	 * @param allergies the allergies to set
	 */
	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}
	/**
	 * @return the teacherName
	 */
	public String getTeacherName() {
		return teacherName;
	}
	/**
	 * @param teacherName the teacherName to set
	 */
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	/**
	 * @return the barCode
	 */
	public String getBarCode() {
		return barCode;
	}
	/**
	 * @param barCode the barCode to set
	 */
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	/**
	 * @return the isReducePriceEligible
	 */
	public Boolean getIsReducePriceEligible() {
		return isReducePriceEligible;
	}
	/**
	 * @param isReducePriceEligible the isReducePriceEligible to set
	 */
	public void setIsReducePriceEligible(Boolean isReducePriceEligible) {
		this.isReducePriceEligible = isReducePriceEligible;
	}
	/**
	 * @return the isFreeMealEligible
	 */
	public Boolean getIsFreeMealEligible() {
		return isFreeMealEligible;
	}
	/**
	 * @param isFreeMealEligible the isFreeMealEligible to set
	 */
	public void setIsFreeMealEligible(Boolean isFreeMealEligible) {
		this.isFreeMealEligible = isFreeMealEligible;
	}
	/**
	 * @return the menuItemDetails
	 */
	public List<OrderedMenuItemDetails> getMenuItemDetails() {
		return menuItemDetails;
	}
	/**
	 * @param menuItemDetails the menuItemDetails to set
	 */
	public void setMenuItemDetails(List<OrderedMenuItemDetails> menuItemDetails) {
		this.menuItemDetails = menuItemDetails;
	}
	/**
	 * @return the isBeforeCare
	 */
	public Boolean getIsBeforeCare() {
		return isBeforeCare;
	}
	/**
	 * @param isBeforeCare the isBeforeCare to set
	 */
	public void setIsBeforeCare(Boolean isBeforeCare) {
		this.isBeforeCare = isBeforeCare;
	}
	/**
	 * @return the hasMilkCard
	 */
	public Boolean getHasMilkCard() {
		return hasMilkCard;
	}
	/**
	 * @param hasMilkCard the hasMilkCard to set
	 */
	public void setHasMilkCard(Boolean hasMilkCard) {
		this.hasMilkCard = hasMilkCard;
	}
	/**
	 * @return the isEnrollBCAndACPkt
	 */
	public Boolean getIsEnrollBCAndACPkt() {
		return isEnrollBCAndACPkt;
	}
	/**
	 * @param isEnrollBCAndACPkt the isEnrollBCAndACPkt to set
	 */
	public void setIsEnrollBCAndACPkt(Boolean isEnrollBCAndACPkt) {
		this.isEnrollBCAndACPkt = isEnrollBCAndACPkt;
	}
	/**
	 * @return the schoolStudentId
	 */
	public String getSchoolStudentId() {
		return schoolStudentId;
	}
	/**
	 * @param schoolStudentId the schoolStudentId to set
	 */
	public void setSchoolStudentId(String schoolStudentId) {
		this.schoolStudentId = schoolStudentId;
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
	 * @return the isThresholdBal
	 */
	public Boolean getIsThresholdBal() {
		return isThresholdBal;
	}
	/**
	 * @param isThresholdBal the isThresholdBal to set
	 */
	public void setIsThresholdBal(Boolean isThresholdBal) {
		this.isThresholdBal = isThresholdBal;
	}
	/**
	 * @return the menuByType
	 */
	public Map<MealType, List<MenuItemDetailsV2>> getMenuByType() {
		return menuByType;
	}
	/**
	 * @param menuByType the menuByType to set
	 */
	public void setMenuByType(Map<MealType, List<MenuItemDetailsV2>> menuByType) {
		this.menuByType = menuByType;
	}
	/**
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}
	/**
	 * @param pin the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * @return the thresholdAmt
	 */
	public Double getThresholdAmt() {
		return thresholdAmt;
	}
	/**
	 * @param thresholdAmt the thresholdAmt to set
	 */
	public void setThresholdAmt(Double thresholdAmt) {
		this.thresholdAmt = thresholdAmt;
	}
	/**
	 * @return the additionalNotes
	 */
	public String getAdditionalNotes() {
		return additionalNotes;
	}
	/**
	 * @param additionalNotes the additionalNotes to set
	 */
	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}
	
}
