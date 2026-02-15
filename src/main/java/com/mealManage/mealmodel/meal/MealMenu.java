package com.mealManage.mealmodel.meal;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "MealMenu_v2", indexes = { 
	    @Index(columnList = "mealtype"),
	    @Index(columnList="mealName"),
	    @Index(columnList="mealPrice"),
	    @Index(columnList="mealDate")})
public class MealMenu extends Menu implements Serializable{
	
	private static final long serialVersionUID = -6341040785067802404L;

	private String mealImage;
	@Column(name="mealDate")
	//@JsonFormat(pattern="yyyy-MM-dd"/*, timezone = "IST"*/)
	private Date start;
	
	 /*@ElementCollection(targetClass = MealType.class)
	 @CollectionTable(name = "mealmenu_type",joinColumns = @JoinColumn(name = "mealmenu_id"))
	 @Enumerated(EnumType.STRING)
	 @Column(name = "mealtype_id")*/
	 @Enumerated(EnumType.STRING)
	 @Column(name="mealtype", nullable = false)
	 private MealType type;

	/**
	 * @return the mealImage
	 */
	public String getMealImage() {
		return mealImage;
	}

	/**
	 * @param mealImage the mealImage to set
	 */
	public void setMealImage(String mealImage) {
		this.mealImage = mealImage;
	}

	/**
	 * @return the type
	 */
	public MealType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(MealType type) {
		this.type = type;
	}

	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}
	

}
