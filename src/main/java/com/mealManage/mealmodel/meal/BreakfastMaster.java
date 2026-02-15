package com.mealManage.mealmodel.meal;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "BreakfastMaster", indexes = { @Index(columnList = "mealSchool_schoolId"), 
		@Index(columnList="yearMonth")})
public class BreakfastMaster extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 2897683151472207824L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recId", updatable = false, nullable = false)
	private Long recId;
	@NotNull
	private String yearMonth;
	@NotNull
	private String itemsPdfLink;
	@ManyToOne
	@NotNull
	private MealSchool mealSchool;
	@ElementCollection(targetClass = SchoolGrades.class)
	@CollectionTable(name = "BreakfastMaster_Grades",joinColumns = @JoinColumn(name = "breakfastMaster_Id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "grades_name")
	private Set<SchoolGrades> grades;
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	@JoinColumn(name = "breakfastMaster_Id", nullable = false, updatable = false)
	private Set<BreakfastItems> breakfastItems;
	@Transient
	private Long mealSchoolId;
	private Boolean reducedPriceStatus;
	
	/**
	 * @return the recId
	 */
	public Long getRecId() {
		return recId;
	}
	/**
	 * @param recId the recId to set
	 */
	public void setRecId(Long recId) {
		this.recId = recId;
	}
	/**
	 * @return the yearMonth
	 */
	public String getYearMonth() {
		return yearMonth;
	}
	/**
	 * @param yearMonth the yearMonth to set
	 */
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
	/**
	 * @return the itemsPdfLink
	 */
	public String getItemsPdfLink() {
		return itemsPdfLink;
	}
	/**
	 * @param itemsPdfLink the itemsPdfLink to set
	 */
	public void setItemsPdfLink(String itemsPdfLink) {
		this.itemsPdfLink = itemsPdfLink;
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
	 * @return the grades
	 */
	public Set<SchoolGrades> getGrades() {
		return grades;
	}
	/**
	 * @param grades the grades to set
	 */
	public void setGrades(Set<SchoolGrades> grades) {
		this.grades = grades;
	}
	/**
	 * @return the breakfastItems
	 */
	public Set<BreakfastItems> getBreakfastItems() {
		return breakfastItems;
	}
	/**
	 * @param breakfastItems the breakfastItems to set
	 */
	public void setBreakfastItems(Set<BreakfastItems> breakfastItems) {
		this.breakfastItems = breakfastItems;
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
	 * @return the reducedPriceStatus
	 */
	public Boolean getReducedPriceStatus() {
		return reducedPriceStatus;
	}
	/**
	 * @param reducedPriceStatus the reducedPriceStatus to set
	 */
	public void setReducedPriceStatus(Boolean reducedPriceStatus) {
		this.reducedPriceStatus = reducedPriceStatus;
	}

}
