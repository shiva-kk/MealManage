package com.mealManage.mealmodel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;

import io.swagger.annotations.Api;

@Api(value = "householdApplicationForFRMs", description = "These API enabled for demorequest data")
public interface HouseholdAppForFRMRepository extends JpaRepository<HouseholdApplicationForFRM, Long> {
	
	@RestResource(exported = false)
	/**This API disabled for delete the Free/Reduced meals eligibility application data**/
	public void delete(HouseholdApplicationForFRM householdApplicationForFRM);
	
	/**This method used for save the Free/Reduced meals eligibility application data**/
	@SuppressWarnings("unchecked")
	public HouseholdApplicationForFRM save(HouseholdApplicationForFRM householdApplicationForFRM);
	
	/**This method used for get all the household application info by mealSchoolId and schoolYear**/
	public List<HouseholdApplicationForFRM> findByMealSchoolIdAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	/**This method used for get all the household application info by districtId and schoolYear**/
	public List<HouseholdApplicationForFRM> findByMealSchoolIdInAndSchoolYear(@Param("mealSchoolIds") List<Long> mealSchoolIds, @Param("schoolYear") Integer schoolYear);
	
	@Query("Select houseApp from HouseholdApplicationForFRM houseApp where houseApp.mealSchoolId=:mealSchoolId and "
			+ "houseApp.schoolYear = :schoolYear and houseApp.prmyParentEmail in (:parentEmails) and houseApp.status IN ('pending','in-complete')")
	public List<HouseholdApplicationForFRM> findByMealSchoolIdAndSchoolYearAndPrmyParentEmailIn(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("schoolYear") Integer schoolYear, @Param("parentEmails") List<String> parentEmails);
	
	@Query("Select houseApp from HouseholdApplicationForFRM houseApp where houseApp.mealSchoolId=:mealSchoolId and "
			+ "houseApp.schoolYear = :schoolYear and houseApp.prmyParentEmail in (:parentEmails)")
	public List<HouseholdApplicationForFRM> getAppInfo(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("schoolYear") Integer schoolYear, @Param("parentEmails") List<String> parentEmails);
	
	/**This API used for get the all the applications count by mealSchoolId and schoolYear and applicationStatus**/
	public Long countByMealSchoolIdAndSchoolYearAndStatus(@Param("mealSchoolId") Long mealSchoolId,@Param("schoolYear") Integer schoolYear,@Param("status") String status);
	
	/*/**This API used for get the all the applications by mealSchoolId and application status and schoolYear**//*
	@Query("Select app from HouseholdApplicationForFRM app where app.studentUser.mealSchool.schoolId=:mealSchoolId and "
			+ "app.applicationStatus = :applicationStatus and app.studentUser.schoolYear = :schoolYear")
	public Set<HouseholdApplicationForFRM> findBySchoolAndYearAndStatus(
			@Param("mealSchoolId") Long mealSchoolId, @Param("applicationStatus") int applicationStatus, 
			@Param("schoolYear") Integer schoolYear);
	
	*//**This API used for get the all the applications by mealSchoolId and application status and schoolYear**//*
	@Query("Select app from HouseholdApplicationForFRM app where app.studentUser.mealSchool.schoolId=:mealSchoolId and "
			+ "app.studentUser.schoolYear = :schoolYear")
	public Set<HouseholdApplicationForFRM> findBySchoolAndYear(
			@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	*//**This API used for get all the submitted application between any date range for school and year**//*
	@Query("Select app from HouseholdApplicationForFRM app where app.studentUser.mealSchool.schoolId=:mealSchoolId and "
			+ "app.studentUser.schoolYear = :schoolYear and app.appSubmissionDate between :startDate and :endDate")
	public Set<HouseholdApplicationForFRM> applicationsByDateRange(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("schoolYear") Integer schoolYear, @Param("startDate")@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startDate,
			@Param("endDate")@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endDate);*/
	
	
}