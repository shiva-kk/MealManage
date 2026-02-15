package com.mealManage.mealmodel.meal;

import java.io.Serializable;

import javax.persistence.Column;
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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "PosLocation", uniqueConstraints={
        @UniqueConstraint(columnNames = {"location", "mealSchool_schoolId"})},indexes = { 
        	    @Index(columnList = "location"),
        	    @Index(columnList="mealSchool_schoolId")})
public class PosLocation extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = -8178557562036885041L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
	@ManyToOne
	@NotNull
    private MealSchool mealSchool;
    private String location;
    private String locationDesc;
    private Boolean isActive = true;
    @Transient
    private Long locationId;
    
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
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * @return the locationDesc
	 */
	public String getLocationDesc() {
		return locationDesc;
	}
	/**
	 * @param locationDesc the locationDesc to set
	 */
	public void setLocationDesc(String locationDesc) {
		this.locationDesc = locationDesc;
	}
	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the locationId
	 */
	public Long getLocationId() {
		return id;
	}
	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	
   
}
