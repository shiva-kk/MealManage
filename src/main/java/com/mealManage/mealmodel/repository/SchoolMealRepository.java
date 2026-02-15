package com.mealManage.mealmodel.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.meal.MealMenu;
import com.mealManage.mealmodel.meal.SchoolMeal;
import com.mealManage.mealmodel.meal.SchoolMealSummary;
import com.mealManage.mealmodel.school.SchoolGrades;

import io.swagger.annotations.Api;

@Api(value = "schoolMeals", description = "These API enabled for the school meals (i.e. Meal Menus)")
//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
public interface SchoolMealRepository extends JpaRepository<SchoolMeal, Long> {
	
	/*public Set<SchoolMeal> findBySchoolIdIn(@Param("schoolMealIds") List<Long> schoolMealIds);
	
	public Set<SchoolMeal> findByMealSchoolSchoolId(@Param("mealSchoolId") Long mealSchoolId);*/
	
	/**This method used in DAO for get the all school meals (i.e. Meal Menus) by meal school id, year month and grades IN**/
	public Set<SchoolMeal> findByMealSchoolSchoolIdAndYearMonthAndGradesInAndIsDelete(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonth") String yearMonth, @Param("gradesList") List<SchoolGrades> gradesList, @Param("isDelete") boolean isDelete);
	
	/**This method used in DAO for get the all school meals (i.e. Meal Menus) by meal school id, year month, grades IN and published status**/
	public Set<SchoolMeal> findByMealSchoolSchoolIdAndYearMonthAndGradesInAndSchoolMealSummaryIsPublishedAndIsDelete(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonth") String yearMonth, @Param("gradesList") List<SchoolGrades> gradesList, 
			@Param("isPublished") Boolean isPublished, @Param("isDelete") boolean isDelete);
	
	/*public SchoolMeal findBySchoolId(@Param("schoolMealId") Long schoolMealId);
	
	public Set<SchoolMeal> findByYearMonth(@Param("yearMonth") String yearMonth);
	
	public Set<SchoolMeal> findByYearMonthAndGradesIn(@Param("yearMonth") String yearMonth,
			@Param("gradesList") List<SchoolGrades> gradesList);*/
	
	/**This API used for get all the school meals (i.e. Meal Menus) by meal school id and year month**/
	public Set<SchoolMeal> findByYearMonthAndMealSchoolSchoolIdAndIsDelete(@Param("yearMonth") String yearMonth, 
			@Param("mealSchoolId") Long mealSchoolId, @Param("isDelete") boolean isDelete);
	
	/**This API used for get all the school meals (i.e. Meal Menus) by meal school id, year month and Grades IN**/
	public Set<SchoolMeal> findByYearMonthAndGradesInAndMealSchoolSchoolIdAndIsDelete(@Param("yearMonth") String yearMonth,
			@Param("gradesList") List<SchoolGrades> gradesList, @Param("mealSchoolId") Long mealSchoolId,
			@Param("isDelete") boolean isDelete);
	
	 @RestResource(exported = false)
	 /**This API disabled for DELETE operation**/
	 public void delete(SchoolMeal schoolMeal);
	 
	 @SuppressWarnings("unchecked")
	 @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	 /**This API used for INSERT/UPDATE operation of School Meals. It can be execute by admin or super admin user only**/
	 public SchoolMeal save(SchoolMeal schoolMeal);
		
	/**This method used in DAO for get all the school meals (i.e. meal menus) by school meal id IN, grade name and meal school id**/
	public Set<SchoolMeal> findBySchoolIdInAndGradesAndMealSchoolSchoolIdAndIsDelete(@Param("schoolMealIds") List<Long> schoolMealIds, 
			@Param("gradeName") SchoolGrades gardeName, @Param("mealSchoolId") Long mealSchoolId, @Param("isDelete") boolean isDelete);
	
	@Query("SELECT ss FROM SchoolMeal m INNER JOIN m.schoolMealSummary ss where m.mealSchool.schoolId = :mealSchoolId "
			+ "and ss.yearMonth IN (:yearMonths)")
	/**This API used for get all the School Meal summary (i.e. Meal Menu created pdf files) by meal school id and year month IN**/
	public Set<SchoolMealSummary> mealMenuPdfByMealSchoolIdAndYearMonths(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonths") List<String> yearMonths);
	
	@Query("SELECT ss FROM SchoolMeal m INNER JOIN m.schoolMealSummary ss where m.mealSchool.schoolId = :mealSchoolId "
			+ "and ss.yearMonth IN (:yearMonths) and :grade MEMBER OF m.grades)")
	/**This API used for get all the school meal summary (i.e. meal menu created pdf files) by meal school id, year month IN and grade**/
	public Set<SchoolMealSummary> mealMenuPdfByMealSchoolIdAndYearMonthsAndGrades(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonths") List<String> yearMonths, @Param("grade") SchoolGrades grade);
	
	@Query("SELECT ss FROM SchoolMeal m INNER JOIN m.schoolMealSummary ss where m.mealSchool.schoolId = :mealSchoolId "
			+ "and ss.yearMonth IN (:yearMonths) and ss.isPublished = 1 and :grade MEMBER OF m.grades)")
	/**This API used for get all the school meal summary (i.e. meal menu created pdf files) by meal school id, year month IN and grade**/
	public Set<SchoolMealSummary> mealMenuPdfByMealSchoolIdAndYearMonthsAndGradesAndPublished(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonths") List<String> yearMonths, @Param("grade") SchoolGrades grade);
	
	@Query("SELECT ss FROM SchoolMeal m INNER JOIN m.schoolMealSummary ss where m.mealSchool.schoolId = :mealSchoolId "
			+ "and ss.yearMonth LIKE :year%")
	/**This API used for get all the school meal summary (i.e. meal menu created pdf files) by meal school id and year**/
	public Set<SchoolMealSummary> mealMenuPdfByMealSchoolIdAndYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("year") String year);
			
	@Query("SELECT ss FROM SchoolMeal m INNER JOIN m.schoolMealSummary ss where m.mealSchool.schoolId = :mealSchoolId "
			+ "and ss.yearMonth >= :startYearMonth and ss.yearMonth <= :endYearMonth")
	/**This API used for get all the school meal summary (i.e. meal menu created pdf files) by meal school id and year month range**/
	public Set<SchoolMealSummary> mealMenuPdfByMealSchoolIdAndYearMonthRange(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("startYearMonth") String startYearMonth, @Param("endYearMonth") String endYearMonth);
	
	/**This method used for get all the Meal Menu items by meal school id and meal date**/
	@Query("Select m.title from SchoolMeal s INNER JOIN s.mealMenu m where s.mealSchool.schoolId = :mealSchoolId and (m.start "
			+ "between :startDate and :endDate) and m.type = 'MEAL' and s.isDelete = 0 group by m.title")
	public List<String> mealMenuBySchoolAndDate(@Param("mealSchoolId") Long mealSchoolId, @Param("startDate") @DateTimeFormat(
			pattern="yyyy-MM-dd") Date startDate, @Param("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate);
	
	/**This method used for get all the school meals by school meal summary id**/
	public Set<SchoolMeal> findBySchoolMealSummarySchoolIdAndIsDelete(@Param("schoolMealSummaryId") Long schoolMealSummaryId, 
			@Param("isDelete") boolean isDelete);
	
	@Query("SELECT ss FROM SchoolMeal m INNER JOIN m.schoolMealSummary ss where ss.schoolId = :mealSummaryId")
	/**This API used for get the school meal summary by meal summary id**/
	public Set<SchoolMealSummary> schoolMealSummaryById(@Param("mealSummaryId") Long mealSummaryId);
	
	/**This method used for get all the meal menu items details by meal summary id**/
	@Query("SELECT mm FROM SchoolMeal sm INNER JOIN sm.schoolMealSummary ss INNER JOIN sm.mealMenu mm "
			+ "where ss.schoolId = :mealSummaryId and sm.isDelete = 0")
	public List<MealMenu> getAllMealMenuDetails(@Param("mealSummaryId") Long mealSummaryId);
	
	/**This API used for get the latest active menu month**/
	@Query("SELECT max(ss.yearMonth) FROM SchoolMeal sm INNER JOIN sm.schoolMealSummary ss "
			+ "where ss.mealSchool.schoolId = :mealSchoolId and ss.isPublished = true and ss.yearMonth >= :schoolStartYearMonth"
			+ " and ss.yearMonth <= :schoolEndYearMonth")
	public String latestActiveMonth(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolStartYearMonth") String schoolStartYearMonth,
			 @Param("schoolEndYearMonth") String schoolEndYearMonth);
	
}
