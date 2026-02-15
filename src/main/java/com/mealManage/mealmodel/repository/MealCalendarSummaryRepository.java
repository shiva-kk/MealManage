package com.mealManage.mealmodel.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealschedule.entities.MealCalendar;
import com.mealManage.mealschedule.entities.MealCalendarSummary;

import io.swagger.annotations.Api;

/**
 * @author Thulasiram Yachamaneni
 */

@Api(value = "mealCalendarSummaries", description = "These API enabled for MealCalendarSummary data")
public interface MealCalendarSummaryRepository extends JpaRepository<MealCalendarSummary, Long> {

    public Set<MealCalendarSummary> findBySchoolSchoolIdAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId,@Param("schoolYear")Integer schoolYear);

    public Set<MealCalendarSummary> findBySchoolSchoolId(@Param("mealSchoolId") Long mealSchoolId);
    
    @RestResource(exported = false)
	 /**This API disabled for DELETE operation**/
	 public void delete(MealCalendarSummary summary);

    public MealCalendarSummary findMealCalendarSummariesById(@Param("summaryId") Long summaryId);
    
    public Set<MealCalendarSummary> findBySchoolSchoolIdAndYearMonthAndMealType(@Param("mealSchoolId") Long mealSchoolId, @Param("yearMonth") String yearMonth, @Param("mealType") ItemTypeConstants mealType);
    
    /**This method used in DAO for get all the Meal Calendar by meal school id, grade name and year month**/
	public MealCalendarSummary findBySchoolSchoolIdAndMealTypeAndYearMonthAndGrades(@Param("mealSchoolid") Long mealSchoolid, @Param("mealType") ItemTypeConstants mealType,
			@Param("yearMonth") String yearMonth, @Param("gradeName") SchoolGrades gardeName);
	
	/**This method used for get all the meal calendars by calendar id & active & summary id**/
	@Query("SELECT c from MealCalendarSummary s INNER JOIN s.mealByDays c where c.id IN (:calendarIds) and c.isActive = :isActive and s.id = :summaryId")
	public Set<MealCalendar> getMealCalendarByIds(@Param("calendarIds") List<Long> calendarIds, @Param("isActive") Boolean isActive, @Param("summaryId") Long summaryId);

	@Query(value="SELECT DISTINCT DATE(c.date) from meal_calendar_summary s INNER JOIN meal_calendar c ON s.id = c.meal_calendar_summary_id INNER JOIN menu_items m "
			+ "ON c.menu_item_id = m.id where c.isActive = 1 and s.id = :summaryId AND m.category = :type", nativeQuery=true)
	public List<Date> getMainItemDates(@Param("summaryId") Long summaryId, @Param("type") String type);
	
	@Query(value="SELECT DISTINCT DATE(c.date) from meal_calendar_summary s INNER JOIN meal_calendar c ON s.id = c.meal_calendar_summary_id INNER JOIN menu_items m "
			+ "ON c.menu_item_id = m.id where c.isActive = 1 and s.id = :summaryId AND m.category = :type and c.id != :calendarId", nativeQuery=true)
	public List<Date> getMainItemDates1(@Param("summaryId") Long summaryId, @Param("type") String type, @Param("calendarId") Long calendarId);
	
	@Query(value="SELECT c.meal_calendar_summary_id from meal_calendar_summary s INNER JOIN meal_calendar c "
			+ "ON s.id = c.meal_calendar_summary_id where c.isActive = 1 and c.id = :calendarId", nativeQuery=true)
	public Long getSummaryId(@Param("calendarId") Long calendarId);
	
    @Query("SELECT ss FROM MealCalendarSummary ss where ss.school.schoolId = :mealSchoolId "
            + "and ss.yearMonth >= :startYearMonth and ss.yearMonth <= :endYearMonth and mealType = :itemType")
    /**This API used for get all the school meal summary (i.e. meal menu created pdf files) by meal school id and year month range**/
    public Set<MealCalendarSummary> findByBySchoolSchoolIdAndYearMonthRange(@Param("mealSchoolId") Long mealSchoolId, @Param("startYearMonth") String startYearMonth, 
    		@Param("endYearMonth") String endYearMonth, @Param("itemType") ItemTypeConstants itemType);

    /**This API used for get the latest active menu month**/
    @Query("SELECT max(ss.yearMonth) FROM MealCalendarSummary ss where ss.school.schoolId = :mealSchoolId and ss.isPublished = true and ss.yearMonth >= :schoolStartYearMonth"
            + " and ss.yearMonth <= :schoolEndYearMonth")
    public String latestActiveMonth(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolStartYearMonth") String schoolStartYearMonth,
                                    @Param("schoolEndYearMonth") String schoolEndYearMonth);
    
    @Query("Select bi from MealCalendarSummary bm Inner Join bm.mealByDays bi where bm.school.schoolId = :mealSchoolId"
    		+ " and bi.date = :date and bm.mealType = :itemType")
	/**This API used for get all the breakfast menu details by school id and date**/
	public Set<MealCalendar> getBreakfastItem(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date, @Param("itemType") ItemTypeConstants itemType);
    
    @Query(value="Select s.id,s.cutOffDateTime,s.orderDateExtensionStatus,s.isPublished,s.isSideSelect,s.isExtraPreOrder,s.itemPriceDisForMonthlyOrder from meal_calendar_summary s inner join meal_summary_grades g "
    		+ "on s.id=g.meal_calendar_summary_id where s.yearMonth=:yearMonth and s.mealSchool_schoolId=:mealSchoolId and s.mealType = :itemType "
    		+ "and g.grades_name = :grade limit 1", nativeQuery=true)
    public List<Object[]> getSummaryInfo(@Param("mealSchoolId") Long mealSchoolId, @Param("yearMonth") String yearMonth, @Param("grade") String grade,
                                         @Param("itemType") String itemType);
    
    @Query(value="Select s.id from meal_calendar_summary s inner join meal_summary_grades g on s.id=g.meal_calendar_summary_id "
    		+ "where s.mealSchool_schoolId = :mealSchoolId and s.mealType=:menuType and g.grades_name = :grade "
    		+ "and s.yearMonth >= :yearMonth and s.isPublished = true "
    		+ "and ((s.cutOffType = 'M' and (s.cutOffDateTime >= now() OR s.orderDateExtensionStatus = 1)) "
    		+ "or (s.cutOffType = 'R' and s.yearMonth >= date_format(DATE_ADD(now(), INTERVAL s.allowOrderNDaysBefore DAY), '%Y%m'))"
    		+ " or (s.cutOffType = 'W' and s.yearMonth >= :weekYearMonth))", nativeQuery=true)
    public Set<BigInteger> getAllMenus(@Param("mealSchoolId") Long mealSchoolId, @Param("menuType") String menuType, @Param("grade") String grade, 
    		@Param("yearMonth") String yearMonth, @Param("weekYearMonth") String weekYearMonth);
    
    @Query(value="Select s.id from meal_calendar_summary s inner join meal_summary_grades g on s.id=g.meal_calendar_summary_id "
    		+ "where s.mealSchool_schoolId = :mealSchoolId and s.mealType=:menuType and g.grades_name = :grade "
    		+ "and s.yearMonth >= :yearMonth and s.isPublished = true", nativeQuery=true)
    public Set<BigInteger> getAllMenus4Admin(@Param("mealSchoolId") Long mealSchoolId, @Param("menuType") String menuType, @Param("grade") String grade, 
    		@Param("yearMonth") String yearMonth);
    
    public List<MealCalendarSummary> findByIdIn(@Param("summaryIds") List<Long> summaryIds);

    /*@Query("SELECT new com.mealManage.mealschedule.entities.MealCalendarSummary(ss.id, ss.autoReminderDate1, ss.autoReminderDate2, ss.isPublished, ss.orderDateExtensionStatus, ss.yearMonth, " +
            " ss.schoolYear, ss.pdfLink, ss.reducedPriceStatus) FROM MealCalendarSummary ss inner join ss.grades g where ss.school.schoolId = :mealSchoolId "
            + "and ss.yearMonth >= :startYearMonth and ss.yearMonth <= :endYearMonth")
    *//**This API used for get all the school meal summary (i.e. meal menu created pdf files) by meal school id and year month range**//*
    public List<MealCalendarSummary> findBySchoolSchoolIdAndYearMonthRange(@Param("mealSchoolId") Long mealSchoolId,
                                                                           @Param("startYearMonth") String startYearMonth, @Param("endYearMonth") String endYearMonth);
*/
    

	 
	 @Query(value="SELECT c.id FROM meal_calendar_summary s INNER JOIN meal_calendar c ON s.id=c.meal_calendar_summary_id "
	 		+ "INNER JOIN menu_items m ON c.menu_item_id=m.id WHERE s.id=:summaryId AND c.date = :date AND m.category = 'HOLIDAY'", nativeQuery=true)
	 public Long getHolidayCalId(@Param("summaryId") Long summaryId, @Param("date") Date date);

}
