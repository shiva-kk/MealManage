package com.mealManage.mealmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.mealschedule.entities.MealCalendar;

/**
 * @author Thulasiram Yachamaneni
 */
public interface MealCalendarRepository extends JpaRepository<MealCalendar, Long> {
	
	 @RestResource(exported = false)
	 /**This API disabled for DELETE operation**/
	 public void delete(MealCalendar calendar);


}