package com.mealManage.mealmodel.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.school.SchoolGrades;

import io.swagger.annotations.Api;

@Api(value = "mealOrderDetailses", description = "These API enabled for meal Order details.")
public interface MealOrderDetailsRepository extends JpaRepository<MealOrderDetails, Long> {
	
	/**This API used for get the Meal Ordered details by Student record ID and year month**/
	public MealOrderDetails findByStudentUserUserIdAndYearMonthAndMenuType(@Param("studentRecId") Long studentRecId, @Param("yearMonth") String yearMonth, @Param("menuType") ItemTypeConstants menuType);
	
	/**This method used in DAO for check the meal ordered details by student record id, year month and payment status**/
	public MealOrderDetails findByStudentUserUserIdAndYearMonthAndPaymentStatus(@Param("studentRecId") Long studentRecId,
			@Param("yearMonth") String yearMonth, @Param("paymentStatus") Boolean paymentStatus);
	
	/*public List<MealOrderDetails> findByYearMonth(@Param("yearMonth") String yearMonth);*/
	
	@RestResource(exported = false)
	/**This API disabled for DELETE operation**/
	public void delete(MealOrderDetails mealOrderDetails);
	
	@SuppressWarnings("unchecked")
	@RestResource(exported = false)
	/**This API disabled for SAVE operation**/
	public MealOrderDetails save(MealOrderDetails mealOrderDetails);
	
	/**This API used for get all the meal ordered details by meal school id and year months IN**/
	public Set<MealOrderDetails> findBySchoolMealsMealSchoolSchoolIdAndYearMonthIn(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonths") List<String> yearMonths);
	
	/**This API used for get all the meal ordered details by meal school id, grades IN and year months IN**/
	public Set<MealOrderDetails> findBySchoolMealsMealSchoolSchoolIdAndStudentUserGradeNameInAndYearMonthIn(@Param("mealSchoolId") 
		Long mealSchoolId, @Param("schoolGrades") List<SchoolGrades> schoolGrades, @Param("yearMonths") List<String> yearMonths);
	
	/**This API used for get all the meal ordered details by meal school id, payment status and year months IN**/
	public Set<MealOrderDetails> findBySchoolMealsMealSchoolSchoolIdAndPaymentStatusAndYearMonthIn(@Param("mealSchoolId") Long mealSchoolId,
		@Param("paymentStatus") Boolean paymentStatus, @Param("yearMonths") List<String> yearMonths);
	
	/**This API used for get the meal ordered details by meal school id, grade name, payment status and year months IN**/
	public Set<MealOrderDetails> findBySchoolMealsMealSchoolSchoolIdAndStudentUserGradeNameInAndPaymentStatusAndYearMonthIn(@Param("mealSchoolId") 
		Long mealSchoolId, @Param("schoolGrades") List<SchoolGrades> schoolGrades, @Param("paymentStatus") Boolean paymentStatus, @Param("yearMonths") List<String> yearMonths);
	
	/**This API used for get all the meal ordered details by student record id and year months IN**/
	public Set<MealOrderDetails> findByStudentUserUserIdAndYearMonthIn(@Param("studentRecId") Long studentRecId, @Param("yearMonths") List<String> yearMonths);

	@Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM MealOrderDetails o where o.yearMonth = :yearMonth "
			+ "and o.studentUser.userId = :studentRecId and o.studentUser.isActive = true")
	public Boolean studentOrderStatus(@Param("studentRecId") Long studentRecId, @Param("yearMonth") String yearMonth);
	
	/*@Query("SELECT mod From MealOrderDetails mod where mod.studentUser.mealSchool.schoolId = :mealSchoolId and mod.yearMonth IN "
			+ "(:yearMonths) and mod.studentUser.isActive = true and mod.studentUser.isRegister = true")
	public Set<MealOrderDetails> orderedBySchoolAndYearMonths(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonths") List<String> yearMonths);
	
	@Query("SELECT mod From MealOrderDetails mod where mod.studentUser.mealSchool.schoolId = :mealSchoolId and mod.yearMonth IN "
			+ "(:yearMonths) and mod.studentUser.gradeName IN (:grades) and mod.studentUser.isActive = true and mod.studentUser.isRegister = true")
	public Set<MealOrderDetails> orderedBySchoolAndYearMonthsAndGrades(@Param("mealSchoolId")
		Long mealSchoolId, @Param("yearMonths") List<String> yearMonths, @Param("grades") List<SchoolGrades> grades);
	
	@Query("SELECT mod From MealOrderDetails mod where mod.studentUser.mealSchool.schoolId = :mealSchoolId and mod.yearMonth IN "
			+ "(:yearMonths) and mod.paymentStatus = :paymentStatus and mod.studentUser.isActive = true and mod.studentUser.isRegister = true")
	public Set<MealOrderDetails> orderedBySchoolAndYearMonthsAndPymtStatus(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonths") List<String> yearMonths, @Param("paymentStatus") Boolean paymentStatus);
	
	@Query("SELECT mod From MealOrderDetails mod where mod.studentUser.mealSchool.schoolId = :mealSchoolId and mod.yearMonth IN "
			+ "(:yearMonths) and mod.paymentStatus = :paymentStatus and mod.studentUser.gradeName IN (:grades) and "
			+ "mod.studentUser.isActive = true and mod.studentUser.isRegister = true")
	public Set<MealOrderDetails> orderedBySchoolAndYearMonthsAndGradesAndPymtStatus(@Param("mealSchoolId")
		Long mealSchoolId, @Param("yearMonths") List<String> yearMonths, @Param("grades") List<SchoolGrades> grades, @Param("paymentStatus") Boolean paymentStatus);*/
	
	/*@Query("Select su from MealOrderDetails mod right join mod.studentUser su where su.mealSchool.schoolId = :mealSchoolId "
			+ "and su.isActive =true and su.isRegister = true and NOT EXISTS (select m.studentUser.userId from MealOrderDetails m where m.studentUser.userId = su.userId and m.yearMonth = :yearMonth)")
	*//**This API used for get all the student details which not ordered meal yet by meal school id and year month**//*
	@Query("Select su from MealOrderDetails mod right join mod.studentUser su where su.mealSchool.schoolId = :mealSchoolId "
			+ "and su.isActive =true and su.isRegister = true and su.userId NOT IN (select m.studentUser.userId from MealOrderDetails m "
			+ "where m.studentUser.mealSchool.schoolId = :mealSchoolId and m.yearMonth = :yearMonth)")
	public Set<StudentUser> notOrderedBySchoolAndYearMonth(@Param("mealSchoolId") Long mealSchoolId, @Param("yearMonth") String yearMonth);
	
	@Query("Select su from MealOrderDetails mod right join mod.studentUser su where su.mealSchool.schoolId = :mealSchoolId "
			+ "and mod.studentUser.gradeName IN (:grades) and su.isActive =true and su.isRegister = true and "
			+ "NOT EXISTS (select m.studentUser.userId from MealOrderDetails m where m.studentUser.userId = su.userId "
			+ "and m.yearMonth = :yearMonth and m.studentUser.gradeName IN (:grades))")
	*//**This API used for get all the student details which not ordered meal yet by meal school id, year month and grades**//*
	public Set<StudentUser> notOrderedBySchoolAndYearMonthAndGrades(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonth") String yearMonth, @Param("grades") List<SchoolGrades> grades);*/
	
	@Query("Select mod from MealOrderDetails mod where mod.studentUser.userId = :studentRecId and mod.yearMonth LIKE :year%")
	/**This API used for get all the meal ordered details by student record id and year**/
	public Set<MealOrderDetails> findByStudentAndYear(@Param("studentRecId") Long studentRecId, @Param("year") String year);
	
	/*@Query("Select count(su) from MealOrderDetails mod right join mod.studentUser su where su.mealSchool.schoolId = :mealSchoolId "
			+ "and su.isActive =true and su.isRegister = true and NOT EXISTS (select m.studentUser.userId from MealOrderDetails m where m.studentUser.userId = su.userId and m.yearMonth = :yearMonth)")
	*//**This API used for get all the student details which not ordered meal yet by meal school id and year month**//*
	public Long notOrderedStudentCountBySchoolAndYearMonth(@Param("mealSchoolId") Long mealSchoolId, @Param("yearMonth") String yearMonth);
	
	@Query("Select count(su) from MealOrderDetails mod right join mod.studentUser su where su.mealSchool.schoolId = :mealSchoolId "
			+ "and mod.studentUser.gradeName IN (:grades) and su.isActive =true and su.isRegister = true and "
			+ "NOT EXISTS (select m.studentUser.userId from MealOrderDetails m where m.studentUser.userId = su.userId "
			+ "and m.yearMonth = :yearMonth and m.studentUser.gradeName IN (:grades))")
	*//**This API used for get all the student details which not ordered meal yet by meal school id, year month and grades**//*
	public Long notOrderedStudentCountBySchoolAndYearMonthAndGrades(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("yearMonth") String yearMonth, @Param("grades") List<SchoolGrades> grades);*/
	
	/**This method used for get all the menu orders by month, grades and school**/
	@Query("Select mod from MealOrderDetails mod where mod.studentUser.mealSchool.schoolId = :mealSchoolId and "
			+ "mod.studentUser.gradeName in (:gradeList) and mod.yearMonth = :yearMonth and mod.menuType = :menuType")
	public Set<MealOrderDetails> ordersByGradesAndMonth(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("gradeList") List<SchoolGrades> gradeList, @Param("yearMonth") String yearMonth, @Param("menuType") ItemTypeConstants menuType);
	
	/**This method used for get all the menu orders by month, student record ids and school**/
	@Query("Select mod from MealOrderDetails mod where mod.studentUser.mealSchool.schoolId = :mealSchoolId and "
			+ "mod.studentUser.userId in (:studentRecIds) and mod.yearMonth = :yearMonth and mod.menuType = :menuType")
	public Set<MealOrderDetails> ordersByStudentsAndMonth(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("studentRecIds") List<Long> studentRecIds, @Param("yearMonth") String yearMonth, @Param("menuType") ItemTypeConstants menuType);
	
	/**This method used for get the all menu ordered students details whose belong item need to be remove**/
	public Set<MealOrderDetails> findByStudentUserMealSchoolSchoolIdAndStudentUserStudentIdInAndYearMonthAndMenuType(@Param("mealSchoolId") 
		Long mealSchoolId, @Param("studentIds") Set<String> studentIds, @Param("yearMonth") String yearMonth, @Param("menuType") ItemTypeConstants menuType);
	
	@Query("Select mod.menuOrderedPdfLink, su.isFreeMealEligible, su.isReducePriceEligible, su.isBeforeCare, mod.itemDiscount, mod.isEligForDiscount from MealOrderDetails mod inner join mod.studentUser su where su.userId = :studentRecId and mod.yearMonth = :yearMonth and mod.menuType = :menuType")
	/**This API used for get menu order pdf link by student record id and yearMonth**/
	public List<Object[]> getOrderPdf(@Param("studentRecId") Long studentRecId, @Param("yearMonth") String yearMonth, @Param("menuType") ItemTypeConstants menuType);
	
	@Query(value="select d.date, e.name, e.ingredients, e.shortDescription, e.longDescription, e.allergens, d.price, e.category, "
			+ "e.id, d.id as calendarId,d.reducedPrice,e.isNutrAvailable,d.meal_calendar_summary_id FROM MealOrdersAudit_v2 mo inner join mealOrdersAudit_calendarMenu oc on "
			+ "mo.schoolId=oc.orderId inner join meal_calendar d on oc.mealCalendarId=d.id INNER JOIN menu_items e on "
			+ "d.menu_item_id=e.id where d.isActive = 1 and mo.studentUser_userId=:studentRecId and mo.yearMonth=:yearMonth and mo.menuType = :menuType", nativeQuery=true)
    public List<Object[]> getMenuItemsByStudentAndMonth(@Param("studentRecId") Long studentRecId, @Param("yearMonth") String yearMonth, @Param("menuType") String menuType);
    
    @Query(value="select d.date, e.name, e.ingredients, e.shortDescription, e.longDescription, e.allergens, d.price, e.category,"
    		+ "e.id, d.id as calendarId,d.reducedPrice,e.isNutrAvailable,d.meal_calendar_summary_id FROM meal_calendar d INNER JOIN "
    		+ "menu_items e ON d.menu_item_id=e.id where d.isActive = 1 AND d.meal_calendar_summary_id = :summaryId and e.category = 'HOLIDAY'", nativeQuery=true)
    public List<Object[]> getHolidayList(@Param("summaryId") Long summaryId);
    
    @Query(value="select d.id FROM MealOrdersAudit_v2 mo inner join mealOrdersAudit_calendarMenu oc on "
			+ "mo.schoolId=oc.orderId inner join meal_calendar d on oc.mealCalendarId=d.id INNER JOIN menu_items e on "
			+ "d.menu_item_id=e.id where d.isActive = 1 and mo.studentUser_userId=:studentRecId and mo.yearMonth=:yearMonth and mo.menuType = :menuType", nativeQuery=true)
    public List<BigInteger> getExistingCalendars(@Param("studentRecId") Long studentRecId, @Param("yearMonth") String yearMonth, @Param("menuType") String menuType);
    
    @Query(value="SELECT COUNT(e.id) FROM MealOrdersAudit_v2 mo inner join mealOrdersAudit_calendarMenu oc ON "
    		+ "mo.schoolId=oc.orderId inner join meal_calendar d on oc.mealCalendarId=d.id INNER JOIN menu_items e ON "
    		+ "d.menu_item_id=e.id where d.isActive = 1 and mo.studentUser_userId=:studentRecId AND DATE(d.date) > :currDate", nativeQuery=true)
	/**This API used for check the future orders count**/
	public BigInteger checkFutureOrders(@Param("studentRecId") Long studentRecId, @Param("currDate") String currDate);
}
