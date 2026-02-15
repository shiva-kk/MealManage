package com.mealManage.mealmodel.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealsExcelSummary;
import com.mealManage.mealmodel.school.SchoolGrades;

import io.swagger.annotations.Api;

@Api(value = "mealsExcelSummaries", description = "These API enabled for uploaded meal excel summary details")
public interface MealsExcelSummaryRepository extends JpaRepository<MealsExcelSummary, Long> {
	
	/**This API used for get all the upload meal excel file summary by meal school id and year month IN**/
	public Set<MealsExcelSummary> findByMealSchoolSchoolIdAndYearMonthInAndItemType(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonths") List<String> yearMonth, @Param("itemType") ItemTypeConstants itemType);
	
	/*public MealsExcelSummary findByMealSchoolSchoolIdAndYearMonthAndGradeNames(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonth") String yearMonth, @Param("gradeNames") String gradeNames);*/
	
	@Query("Select mes from MealsExcelSummary mes where mes.mealSchool.schoolId = :mealSchoolId "
			+ "and mes.yearMonth LIKE :year% and mes.itemType = :itemType")
	/**This API used for get all the uploaded meal excel file summary by meal school id and year**/
	public Set<MealsExcelSummary> findByMealSchoolSchoolIdAndYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("year") String year, @Param("itemType") ItemTypeConstants itemType);
	
	/**This method used in DAO for get all the uploaded meal excel file summary by meal school id, year month and grades**/
	public Set<MealsExcelSummary> findByMealSchoolSchoolIdAndYearMonthAndGradesInAndItemType(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonth") String yearMonth, @Param("grades") List<SchoolGrades> grades, 
			@Param("itemType") ItemTypeConstants itemType);
	
	/**This API used for get all the latest n upload meal excel files by meal school id**/
	@Query(value = "select * from mealsexcelsummary where mealSchool_schoolId = :mealSchoolId and itemType = :itemType "
			+ "order by createdOn desc limit :size", nativeQuery = true)
	public Set<MealsExcelSummary> mealExcelList(@Param("mealSchoolId") Long mealSchoolId, @Param("size") int size, 
			@Param("itemType") String itemType);

}
