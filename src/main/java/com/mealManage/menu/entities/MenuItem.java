package com.mealManage.menu.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.meal.PosLocation;
import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;

/**
 * @author Thulasiram Yachamaneni
 */
@Entity
@Table(name = "menu_items", uniqueConstraints={
        @UniqueConstraint(columnNames = {"name", "mealSchool_schoolId", "active","category"})},indexes = { 
        	    @Index(columnList = "category"),
        	    @Index(columnList="name"),
        	    @Index(columnList="mealSchool_schoolId")})
public class MenuItem extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -8178557562036885041L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "mealSchool_schoolId")
    @JsonBackReference
    private MealSchool schoolDetails;
    private String name;
    private String ingredients;
    @Transient
    private String imageBase64Content;
    private String imageUrl;
    private Boolean active;
    private String shortDescription;
    private String longDescription;
    @Enumerated(EnumType.STRING)
	@Column(name="category", nullable = false)
    private MealType category;
    private String categoryType;
    private String allergens;
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
    @Transient
    private Double itemPrice = 0.0;
    private Boolean isNutrAvailable;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
	        name = "menuItem_posLocation", 
	        joinColumns = { @JoinColumn(name = "menuId") }, 
	        inverseJoinColumns = { @JoinColumn(name = "locationId") }
	    )
	private Set<PosLocation> locations = new HashSet<>();
    
    /*private Double price = 0.0;
    private Double reducedPrice = 0.0;*/
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
	 * @return the schoolDetails
	 */
	public MealSchool getSchoolDetails() {
		return schoolDetails;
	}
	/**
	 * @param schoolDetails the schoolDetails to set
	 */
	public void setSchoolDetails(MealSchool schoolDetails) {
		this.schoolDetails = schoolDetails;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the ingredients
	 */
	public String getIngredients() {
		return ingredients;
	}
	/**
	 * @param ingredients the ingredients to set
	 */
	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}
	/**
	 * @return the imageBase64Content
	 */
	public String getImageBase64Content() {
		return imageBase64Content;
	}
	/**
	 * @param imageBase64Content the imageBase64Content to set
	 */
	public void setImageBase64Content(String imageBase64Content) {
		this.imageBase64Content = imageBase64Content;
	}
	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}
	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}
	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}
	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	
	/**
	 * @return the category
	 */
	public MealType getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(MealType category) {
		this.category = category;
	}
	/**
	 * @return the categoryType
	 */
	public String getCategoryType() {
		return categoryType;
	}
	/**
	 * @param categoryType the categoryType to set
	 */
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
	/**
	 * @return the allergens
	 */
	public String getAllergens() {
		return allergens;
	}
	/**
	 * @param allergens the allergens to set
	 */
	public void setAllergens(String allergens) {
		this.allergens = allergens;
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
	/**
	 * @return the itemPrice
	 */
	public Double getItemPrice() {
		return itemPrice;
	}
	/**
	 * @param itemPrice the itemPrice to set
	 */
	public void setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
	}
	/**
	 * @return the isNutrAvailable
	 */
	public Boolean getIsNutrAvailable() {
		return isNutrAvailable;
	}
	/**
	 * @param isNutrAvailable the isNutrAvailable to set
	 */
	public void setIsNutrAvailable(Boolean isNutrAvailable) {
		this.isNutrAvailable = isNutrAvailable;
	}
	/**
	 * @return the locations
	 */
	public Set<PosLocation> getLocations() {
		return locations;
	}
	/**
	 * @param locations the locations to set
	 */
	public void setLocations(Set<PosLocation> locations) {
		this.locations = locations;
	}
	
}
