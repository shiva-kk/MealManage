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
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.JoinColumn;
import com.mealManage.mealmodel.school.CateringEntity;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "SchoolMeals_v2", indexes = { @Index(columnList = "mealSchool_schoolId"),
											@Index(columnList="yearMonth")})
public class SchoolMeal extends CateringEntity implements Serializable{

	private static final long serialVersionUID = 2897683151472207824L;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "mealMenu_Id", nullable = false, updatable = false)
	private MealMenu mealMenu;	
	
	@ManyToOne(fetch = FetchType.EAGER) 
	@NotNull
	private MealSchool mealSchool;
	
	@ManyToOne(fetch = FetchType.EAGER) 
	//@NotNull
	private SchoolMealSummary schoolMealSummary;	
	/*@ManyToMany
	@JoinTable(
	        name = "School_Meals", 
	        joinColumns = { @JoinColumn(name = "schoolmeal_id") }, 
	        inverseJoinColumns = { @JoinColumn(name = "mealschool_id") }
	    )
	 private Set<MealSchool> mealSchool = new HashSet<>();*/
	
	/*@ManyToMany
	@JoinTable(
	        name = "SchoolMeals_Students", 
	        joinColumns = { @JoinColumn(name = "schoolmeal_Id") }, 
	        inverseJoinColumns = { @JoinColumn(name = "studentRec_Id") }
	    )
	private Set<StudentUser> studentUser = new HashSet<>();*/
	
	@ElementCollection(targetClass = SchoolGrades.class)
	@CollectionTable(name = "schoolMeal_grades",joinColumns = @JoinColumn(name = "schoolmeal_Id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "grades_name")
	private Set<SchoolGrades> grades ;
	@NotNull
	private String yearMonth;
	@NotNull
	private Boolean isDelete = false;

	/**
	 * @return the mealMenu
	 */
	public MealMenu getMealMenu() {
		return mealMenu;
	}

	/**
	 * @param mealMenu the mealMenu to set
	 */
	public void setMealMenu(MealMenu mealMenu) {
		this.mealMenu = mealMenu;
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
	 * @return the studentUser
	 */
	/*public Set<StudentUser> getStudentUser() {
		return studentUser;
	}*/

	/**
	 * @param studentUser the studentUser to set
	 */
	/*public void setStudentUser(Set<StudentUser> studentUser) {
		this.studentUser = studentUser;
	}*/

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
	 * @return the schoolMealSummary
	 */
	public SchoolMealSummary getSchoolMealSummary() {
		return schoolMealSummary;
	}

	/**
	 * @param schoolMealSummary the schoolMealSummary to set
	 */
	public void setSchoolMealSummary(SchoolMealSummary schoolMealSummary) {
		this.schoolMealSummary = schoolMealSummary;
	}

	/**
	 * @return the isDelete
	 */
	public Boolean getIsDelete() {
		return isDelete;
	}

	/**
	 * @param isDelete the isDelete to set
	 */
	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}
	
}
