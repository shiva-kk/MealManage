package com.mealManage.mealmodel.school;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "School_v2", indexes = { 
	    @Index(columnList = "schoolName"),
	    @Index(columnList="cityStateZip")})
/**This entity having all the school information along with districts data and school type**/
public class School extends CateringEntity implements Serializable{
	
	private static final long serialVersionUID = -4588992033547381798L;
	
	@Column(name="CTDS")
	private String ctds;
	private String schoolDistrictName;
	@NotNull
	private String city;
    private String county;
    @Column(name="State")
    private String state;
    @Column(name="SchoolDistrictsNo")
    private Integer schoolDistrictsNo;  
    @NotNull
	private String schoolName;
	private String schoolAddress;
	@NotNull
	private String cityStateZip;
	private String telephone;
	private String fax;
	
	//private Integer schoolDistrictId;
	/*@Enumerated(EnumType.STRING)
    @Column(length = 8)*/
	@ElementCollection(targetClass = SchoolType.class)
	@CollectionTable(name = "school_type",joinColumns = @JoinColumn(name = "schoolId"))
	@Enumerated(EnumType.STRING)
	@Column(name = "type_id")
	private Set<SchoolType> schoolType;
	
	/*@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "districtSchool_id")
    @RestResource(path = "districtSchools", rel="districtSchools")
    private DistrictSchool districtSchool;*/
	
	/**It's required to get all not boarded schools**/
	@OneToOne(mappedBy="school")
	private MealSchool mealSchool;
	@NotNull
	@Size(min=2,max=2)
	private String countryCode = "US";
	
	@Transient
	private Map<String, List<String>> typeGrades = new HashMap<String, List<String>>();

	/**
	 * @return the ctds
	 */
	public String getCtds() {
		return ctds;
	}

	/**
	 * @param ctds the ctds to set
	 */
	public void setCtds(String ctds) {
		this.ctds = ctds;
	}

	/**
	 * @return the schoolDistrictName
	 */
	public String getSchoolDistrictName() {
		return schoolDistrictName;
	}

	/**
	 * @param schoolDistrictName the schoolDistrictName to set
	 */
	public void setSchoolDistrictName(String schoolDistrictName) {
		this.schoolDistrictName = schoolDistrictName;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}

	/**
	 * @param county the county to set
	 */
	public void setCounty(String county) {
		this.county = county;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the schoolDistrictsNo
	 */
	public Integer getSchoolDistrictsNo() {
		return schoolDistrictsNo;
	}

	/**
	 * @param schoolDistrictsNo the schoolDistrictsNo to set
	 */
	public void setSchoolDistrictsNo(Integer schoolDistrictsNo) {
		this.schoolDistrictsNo = schoolDistrictsNo;
	}

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
	 * @return the schoolAddress
	 */
	public String getSchoolAddress() {
		return schoolAddress;
	}

	/**
	 * @param schoolAddress the schoolAddress to set
	 */
	public void setSchoolAddress(String schoolAddress) {
		this.schoolAddress = schoolAddress;
	}

	/**
	 * @return the cityStateZip
	 */
	public String getCityStateZip() {
		return cityStateZip;
	}

	/**
	 * @param cityStateZip the cityStateZip to set
	 */
	public void setCityStateZip(String cityStateZip) {
		this.cityStateZip = cityStateZip;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @return the schoolType
	 */
	public Set<SchoolType> getSchoolType() {
		for(SchoolType str : schoolType){
			typeGrades.put(str.name(), str.getValues());
		}
		return schoolType;
	}

	/**
	 * @param schoolType the schoolType to set
	 */
	public void setSchoolType(Set<SchoolType> schoolType) {
		this.schoolType = schoolType;
	}

	/**
	 * @return the typeGrades
	 */
	public Map<String, List<String>> getTypeGrades() {
		return typeGrades;
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
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
}
