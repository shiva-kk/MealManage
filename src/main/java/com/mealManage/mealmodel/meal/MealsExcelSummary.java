package com.mealManage.mealmodel.meal;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "mealsexcelsummary", indexes = { 
			    @Index(columnList = "yearMonth"),
			    @Index(columnList="mealSchool_schoolId"),
			    @Index(columnList="itemType")} )
public class MealsExcelSummary extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = -6341040785067802404L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@ManyToOne
	@NotNull
	private MealSchool mealSchool;
	@NotNull
	private String yearMonth;
	private String excelLink;
	
	@ElementCollection(targetClass = SchoolGrades.class)
	@CollectionTable(name = "mealexcelsummary_grades",joinColumns = @JoinColumn(name = "excelsummary_Id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "grades_name")
	private Set<SchoolGrades> grades ;
	@Enumerated(EnumType.STRING)
	@NotNull
	private ItemTypeConstants itemType = ItemTypeConstants.Lunch; //default should be lunch
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
	 * @return the excelLink
	 */
	public String getExcelLink() {
		return excelLink;
	}
	/**
	 * @param excelLink the excelLink to set
	 */
	public void setExcelLink(String excelLink) {
		this.excelLink = excelLink;
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
	 * @return the itemType
	 */
	public ItemTypeConstants getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(ItemTypeConstants itemType) {
		this.itemType = itemType;
	}
	
}
