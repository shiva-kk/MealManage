package com.mealManage.mealmodel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.mealManage.menu.entities.SchoolSession;

public interface SchoolSessionRepo extends JpaRepository<SchoolSession, Long> {
	
	public List<SchoolSession> findByMealSchoolId(@Param("mealSchoolId") Long mealSchoolId);

}
