package com.mealManage.menu.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.mealManage.mealmodel.school.SchoolGrades;

@Entity
@Table(name = "SchoolSession", uniqueConstraints={@UniqueConstraint(columnNames =
{"mealSchoolId", "session"})})
public class SchoolSession implements Serializable{

	private static final long serialVersionUID = 2897683151472207824L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
    private Long id;
	private Long mealSchoolId;
	private String session;
    @ElementCollection(targetClass = SchoolGrades.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "SessionGradeMapping",joinColumns = @JoinColumn(name = "SchoolSession_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "grades_name")
	private Set<SchoolGrades> grades=new HashSet<>();
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
	 * @return the session
	 */
	public String getSession() {
		return session;
	}
	/**
	 * @param session the session to set
	 */
	public void setSession(String session) {
		this.session = session;
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
}
