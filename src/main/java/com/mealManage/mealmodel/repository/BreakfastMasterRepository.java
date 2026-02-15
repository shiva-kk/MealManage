package com.mealManage.mealmodel.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

import com.mealManage.mealmodel.meal.BreakfastItems;
import com.mealManage.mealmodel.meal.BreakfastMaster;
import com.mealManage.mealmodel.meal.MealType;

import io.swagger.annotations.Api;

@Api(value = "breakfastMasters", description = "These API enabled for demorequest data")
public interface BreakfastMasterRepository extends JpaRepository<BreakfastMaster, Long>{
	
	@RestResource(exported = false)
	/**This API disabled for delete the breakfast master data**/
	public void delete(BreakfastMaster breakfastMaster);
	
	public BreakfastMaster findByRecId(@Param("recId") Long recId);
	
	public Set<BreakfastMaster> findByMealSchoolSchoolIdAndYearMonth(@Param("mealSchoolId") Long mealSchoolId, @Param("yearMonth") String yearMonth);
	
	@Query("SELECT bm FROM BreakfastMaster bm where bm.mealSchool.schoolId = :mealSchoolId "
			+ "and bm.yearMonth >= :startYearMonth and bm.yearMonth <= :endYearMonth")
	/**This API used for get all the breakfast menu details by meal school id and year month range**/
	public Set<BreakfastMaster> breakfastMenuPdfBySchoolAndYearMonthRange(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("startYearMonth") String startYearMonth, @Param("endYearMonth") String endYearMonth);
	
	@Query("Select bi from BreakfastMaster bm Inner Join bm.breakfastItems bi where bm.mealSchool.schoolId = :mealSchoolId and "
			+ "bi.breakfastDate = :date and bi.itemType IN (:itemTypes)")
	/**This API used for get all the breakfast menu details by school id and date**/
	public Set<BreakfastItems> findMenuBySchoolAndDate(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date, @Param("itemTypes") List<MealType> itemTypes);
	

}
