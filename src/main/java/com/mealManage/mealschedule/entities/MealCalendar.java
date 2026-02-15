package com.mealManage.mealschedule.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.menu.entities.MenuItem;

/**
 * @author Thulasiram Yachamaneni
 */

@Entity
@Table(name = "meal_calendar",indexes = { 
	    @Index(columnList = "date"),
	    @Index(columnList="menu_item_id"),
	    @Index(columnList="isActive")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MealCalendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private Date date;
    @ManyToOne(optional = false)
    @JoinColumn(name="menu_item_id")
    private MenuItem menuItem;
    private boolean isActive = true;
    private Double price = 0.0;
    private Double reducedPrice = 0.0;
	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="meal_calendar_summary_id")
    private MealCalendarSummary mealCalendarSummary;*/
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
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the menuItem
	 */
	public MenuItem getMenuItem() {
		return menuItem;
	}
	/**
	 * @param menuItem the menuItem to set
	 */
	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
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
	 * @return the reducedPrice
	 */
	public Double getReducedPrice() {
		return reducedPrice;
	}
	/**
	 * @param reducedPrice the reducedPrice to set
	 */
	public void setReducedPrice(Double reducedPrice) {
		this.reducedPrice = reducedPrice;
	}
	
}
