package com.mealManage.mealmodel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mealManage.mealmodel.user.StudentBkpFileHistory;

import io.swagger.annotations.Api;

@Api(value = "studentBkpFileHistories", description = "These API enabled for Student bkp history file")
public interface StudentBkpFileHistoryRepo extends JpaRepository<StudentBkpFileHistory, Long> {
	
	@Query(value = "Select * from StudentBkpHistory where mealSchoolId = :mealSchoolId and "
			+ "schoolYear = :schoolYear and bkpType=:type order by date desc limit :limitVal", nativeQuery=true)
	public List<StudentBkpFileHistory> studentBkpHstry(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("schoolYear") Integer schoolYear, @Param("type") String type, @Param("limitVal") Integer limitVal);

}