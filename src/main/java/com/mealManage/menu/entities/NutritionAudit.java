package com.mealManage.menu.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
@Entity
@Table(name="NutritionAudit")
public class NutritionAudit {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
	@NotNull
	private Long itemId;
	private Date effectiveStartDate;
	private Date effectiveEndDate;
	private String createdBy;
	private Date createdDate;
	private Integer calories;
    private Integer totalFat;
    private Integer saturatedFat;
    private Integer cholestral;
    private Integer sodium;
    private Integer totalCarbohydrate;
    private Integer dietaryFiber;
    private Integer sugars;
    private Integer protein;
    private Integer vitaminA;
    private Integer vitaminB6;
    private Integer vitaminB12;
    private Integer vitaminC;
    private Integer vitaminD;
    private Integer vitaminE;
    private Integer vitaminK;
    private Integer calcium;
    private Integer iron;
    private Integer potassium;
    private Integer thiamin;
    private Integer riboFlavin;
    private Integer niacin;
    private Integer folate;
    private Integer pantothenicAcid;
    private Integer phosphorous;
    private Integer magnesium;
    private Integer zinc;
    private Integer selenium;
    private Integer copper;
    private Integer manganese;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the itemId
	 */
	public Long getItemId() {
		return itemId;
	}
	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	/**
	 * @return the effectiveStartDate
	 */
	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}
	/**
	 * @param effectiveStartDate the effectiveStartDate to set
	 */
	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}
	/**
	 * @return the effectiveEndDate
	 */
	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}
	/**
	 * @param effectiveEndDate the effectiveEndDate to set
	 */
	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return the calories
	 */
	public Integer getCalories() {
		return calories;
	}
	/**
	 * @param calories the calories to set
	 */
	public void setCalories(Integer calories) {
		this.calories = calories;
	}
	/**
	 * @return the totalFat
	 */
	public Integer getTotalFat() {
		return totalFat;
	}
	/**
	 * @param totalFat the totalFat to set
	 */
	public void setTotalFat(Integer totalFat) {
		this.totalFat = totalFat;
	}
	/**
	 * @return the saturatedFat
	 */
	public Integer getSaturatedFat() {
		return saturatedFat;
	}
	/**
	 * @param saturatedFat the saturatedFat to set
	 */
	public void setSaturatedFat(Integer saturatedFat) {
		this.saturatedFat = saturatedFat;
	}
	/**
	 * @return the cholestral
	 */
	public Integer getCholestral() {
		return cholestral;
	}
	/**
	 * @param cholestral the cholestral to set
	 */
	public void setCholestral(Integer cholestral) {
		this.cholestral = cholestral;
	}
	/**
	 * @return the sodium
	 */
	public Integer getSodium() {
		return sodium;
	}
	/**
	 * @param sodium the sodium to set
	 */
	public void setSodium(Integer sodium) {
		this.sodium = sodium;
	}
	/**
	 * @return the totalCarbohydrate
	 */
	public Integer getTotalCarbohydrate() {
		return totalCarbohydrate;
	}
	/**
	 * @param totalCarbohydrate the totalCarbohydrate to set
	 */
	public void setTotalCarbohydrate(Integer totalCarbohydrate) {
		this.totalCarbohydrate = totalCarbohydrate;
	}
	/**
	 * @return the dietaryFiber
	 */
	public Integer getDietaryFiber() {
		return dietaryFiber;
	}
	/**
	 * @param dietaryFiber the dietaryFiber to set
	 */
	public void setDietaryFiber(Integer dietaryFiber) {
		this.dietaryFiber = dietaryFiber;
	}
	/**
	 * @return the sugars
	 */
	public Integer getSugars() {
		return sugars;
	}
	/**
	 * @param sugars the sugars to set
	 */
	public void setSugars(Integer sugars) {
		this.sugars = sugars;
	}
	/**
	 * @return the protein
	 */
	public Integer getProtein() {
		return protein;
	}
	/**
	 * @param protein the protein to set
	 */
	public void setProtein(Integer protein) {
		this.protein = protein;
	}
	/**
	 * @return the vitaminA
	 */
	public Integer getVitaminA() {
		return vitaminA;
	}
	/**
	 * @param vitaminA the vitaminA to set
	 */
	public void setVitaminA(Integer vitaminA) {
		this.vitaminA = vitaminA;
	}
	/**
	 * @return the vitaminB6
	 */
	public Integer getVitaminB6() {
		return vitaminB6;
	}
	/**
	 * @param vitaminB6 the vitaminB6 to set
	 */
	public void setVitaminB6(Integer vitaminB6) {
		this.vitaminB6 = vitaminB6;
	}
	/**
	 * @return the vitaminB12
	 */
	public Integer getVitaminB12() {
		return vitaminB12;
	}
	/**
	 * @param vitaminB12 the vitaminB12 to set
	 */
	public void setVitaminB12(Integer vitaminB12) {
		this.vitaminB12 = vitaminB12;
	}
	/**
	 * @return the vitaminC
	 */
	public Integer getVitaminC() {
		return vitaminC;
	}
	/**
	 * @param vitaminC the vitaminC to set
	 */
	public void setVitaminC(Integer vitaminC) {
		this.vitaminC = vitaminC;
	}
	/**
	 * @return the vitaminD
	 */
	public Integer getVitaminD() {
		return vitaminD;
	}
	/**
	 * @param vitaminD the vitaminD to set
	 */
	public void setVitaminD(Integer vitaminD) {
		this.vitaminD = vitaminD;
	}
	/**
	 * @return the vitaminE
	 */
	public Integer getVitaminE() {
		return vitaminE;
	}
	/**
	 * @param vitaminE the vitaminE to set
	 */
	public void setVitaminE(Integer vitaminE) {
		this.vitaminE = vitaminE;
	}
	/**
	 * @return the vitaminK
	 */
	public Integer getVitaminK() {
		return vitaminK;
	}
	/**
	 * @param vitaminK the vitaminK to set
	 */
	public void setVitaminK(Integer vitaminK) {
		this.vitaminK = vitaminK;
	}
	/**
	 * @return the calcium
	 */
	public Integer getCalcium() {
		return calcium;
	}
	/**
	 * @param calcium the calcium to set
	 */
	public void setCalcium(Integer calcium) {
		this.calcium = calcium;
	}
	/**
	 * @return the iron
	 */
	public Integer getIron() {
		return iron;
	}
	/**
	 * @param iron the iron to set
	 */
	public void setIron(Integer iron) {
		this.iron = iron;
	}
	/**
	 * @return the potassium
	 */
	public Integer getPotassium() {
		return potassium;
	}
	/**
	 * @param potassium the potassium to set
	 */
	public void setPotassium(Integer potassium) {
		this.potassium = potassium;
	}
	/**
	 * @return the thiamin
	 */
	public Integer getThiamin() {
		return thiamin;
	}
	/**
	 * @param thiamin the thiamin to set
	 */
	public void setThiamin(Integer thiamin) {
		this.thiamin = thiamin;
	}
	/**
	 * @return the riboFlavin
	 */
	public Integer getRiboFlavin() {
		return riboFlavin;
	}
	/**
	 * @param riboFlavin the riboFlavin to set
	 */
	public void setRiboFlavin(Integer riboFlavin) {
		this.riboFlavin = riboFlavin;
	}
	/**
	 * @return the niacin
	 */
	public Integer getNiacin() {
		return niacin;
	}
	/**
	 * @param niacin the niacin to set
	 */
	public void setNiacin(Integer niacin) {
		this.niacin = niacin;
	}
	/**
	 * @return the folate
	 */
	public Integer getFolate() {
		return folate;
	}
	/**
	 * @param folate the folate to set
	 */
	public void setFolate(Integer folate) {
		this.folate = folate;
	}
	/**
	 * @return the pantothenicAcid
	 */
	public Integer getPantothenicAcid() {
		return pantothenicAcid;
	}
	/**
	 * @param pantothenicAcid the pantothenicAcid to set
	 */
	public void setPantothenicAcid(Integer pantothenicAcid) {
		this.pantothenicAcid = pantothenicAcid;
	}
	/**
	 * @return the phosphorous
	 */
	public Integer getPhosphorous() {
		return phosphorous;
	}
	/**
	 * @param phosphorous the phosphorous to set
	 */
	public void setPhosphorous(Integer phosphorous) {
		this.phosphorous = phosphorous;
	}
	/**
	 * @return the magnesium
	 */
	public Integer getMagnesium() {
		return magnesium;
	}
	/**
	 * @param magnesium the magnesium to set
	 */
	public void setMagnesium(Integer magnesium) {
		this.magnesium = magnesium;
	}
	/**
	 * @return the zinc
	 */
	public Integer getZinc() {
		return zinc;
	}
	/**
	 * @param zinc the zinc to set
	 */
	public void setZinc(Integer zinc) {
		this.zinc = zinc;
	}
	/**
	 * @return the selenium
	 */
	public Integer getSelenium() {
		return selenium;
	}
	/**
	 * @param selenium the selenium to set
	 */
	public void setSelenium(Integer selenium) {
		this.selenium = selenium;
	}
	/**
	 * @return the copper
	 */
	public Integer getCopper() {
		return copper;
	}
	/**
	 * @param copper the copper to set
	 */
	public void setCopper(Integer copper) {
		this.copper = copper;
	}
	/**
	 * @return the manganese
	 */
	public Integer getManganese() {
		return manganese;
	}
	/**
	 * @param manganese the manganese to set
	 */
	public void setManganese(Integer manganese) {
		this.manganese = manganese;
	}
    
}
