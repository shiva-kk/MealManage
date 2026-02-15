package com.mealManage.mealmodel.packages;

import java.io.Serializable;

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "SchoolPackage", indexes = {@Index(columnList="mealSchool_schoolId"), @Index(columnList="schoolYear")})
/**This entity used for all the School Packages info**/
public class SchoolPackage extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 4274550184052803838L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long packageId;
	@NotNull
	private String packageName;
	@NotNull
	private String frequency; //##One Time / Weekly / Monthly
	@NotNull
	private Double price;
	@ManyToOne
	@NotNull
	private MealSchool mealSchool; 
	private Integer schoolYear;
	@NotNull
	private boolean isActive = true;
	@Enumerated(EnumType.STRING)
	@NotNull
	private PackageType type;
	@NotNull
	private double perAdditionalStdDiscountPerc = 0.0;
	private Double maxFeePerParent;
	/**
	 * @return the packageId
	 */
	public Long getPackageId() {
		return packageId;
	}
	/**
	 * @param packageId the packageId to set
	 */
	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}
	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}
	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
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
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the type
	 */
	public PackageType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(PackageType type) {
		this.type = type;
	}
	/**
	 * @return the perAdditionalStdDiscountPerc
	 */
	public double getPerAdditionalStdDiscountPerc() {
		return perAdditionalStdDiscountPerc;
	}
	/**
	 * @param perAdditionalStdDiscountPerc the perAdditionalStdDiscountPerc to set
	 */
	public void setPerAdditionalStdDiscountPerc(double perAdditionalStdDiscountPerc) {
		this.perAdditionalStdDiscountPerc = perAdditionalStdDiscountPerc;
	}
	/**
	 * @return the maxFeePerParent
	 */
	public Double getMaxFeePerParent() {
		return maxFeePerParent;
	}
	/**
	 * @param maxFeePerParent the maxFeePerParent to set
	 */
	public void setMaxFeePerParent(Double maxFeePerParent) {
		this.maxFeePerParent = maxFeePerParent;
	}
	
}
