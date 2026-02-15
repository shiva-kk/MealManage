package com.mealManage.mealmodel.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.domain.PkgNotificationSetting;
import com.mealManage.mealmodel.reimbursement.ReimbursementMealsType;
import com.mealManage.mealmodel.reimbursement.ReimbursementRatesInfo;
import com.mealManage.mealmodel.school.SchoolYear;

import io.swagger.annotations.Api;

@Api(value = "schoolYears", description = "These API enabled for the School Year")
//@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
public interface SchoolYearRepository extends JpaRepository<SchoolYear, Long>{

	@RestResource(exported = false)
	/**This API disabled for delete operation of School Year**/
	public void delete(SchoolYear schoolYear);
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_DISTRICT')")
	/**This API used for create school year. It can be execute by super admin or admin user role only.**/
	public SchoolYear save(SchoolYear schoolYear);
	
	/**This API used for get the list of school year data by school year**/
	public List<SchoolYear> findByMealSchoolSchoolId(@Param("mealSchoolId") Long mealSchoolId);
	
	/**This API used for get the list of school year data by year**/
	public List<SchoolYear> findBySchoolYear(@Param("schoolYear") Integer schoolYear);
	
	/**This API used for get the school year by school and year**/
	public SchoolYear findByMealSchoolSchoolIdAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("schoolYear") Integer schoolYear);
	
	/**This API used for get the reimbursement rate info**/
	@Query("Select rr from SchoolYear sy inner join sy.reimbursementRatesInfos rr where sy.schoolId=:schoolId and "
			+ "rr.reimbursementMealsType = :itemType")
	public Set<ReimbursementRatesInfo> getReimburseRates(@Param("schoolId") Long schoolId, @Param("itemType") ReimbursementMealsType itemType);
	
	/**This method used for return the school year by date and meal school id**/
	@Query("Select s.schoolYear from SchoolYear s where s.mealSchool.schoolId = :mealSchoolId and :date between "
			+ "s.sessionStartDateTime and s.sessionEndDateTime")
	public Integer schoolYearBySchoolAndDate(@Param("mealSchoolId") Long mealSchoolId, @Param("date")@DateTimeFormat(pattern="yyyy-MM-dd") Date date);
	
	/**This method used for get the latest school year data for the specified school**/
	@Query("Select s from SchoolYear s where s.mealSchool.schoolId = :mealSchoolId order by s.schoolYear desc")
	public List<SchoolYear> latestSchoolYear(@Param("mealSchoolId") Long mealSchoolId);
	
	@Query("Select sy.schoolPdfUrl from SchoolYear sy where sy.mealSchool.schoolId=:mealSchoolId and sy.schoolYear=:schoolYear")
	public String schoolAppLink(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query("Select sy.isPOSIdVerificationReq from SchoolYear sy where sy.mealSchool.schoolId=:mealSchoolId and sy.schoolYear=:schoolYear")
	public Boolean posVerStatus(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query("Select sy.isFreeMeal from SchoolYear sy where sy.mealSchool.schoolId=:mealSchoolId and sy.schoolYear=:schoolYear")
	public Boolean isSchoolFreeMeal(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query("Select sy.sessionEndDateTime from SchoolYear sy where sy.mealSchool.schoolId=:mealSchoolId and sy.schoolYear=:schoolYear")
	public Date getSchoolYearEndDate(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query("Select sy.sessionStartDateTime from SchoolYear sy where sy.mealSchool.schoolId=:mealSchoolId and sy.schoolYear=:schoolYear")
	public Date getSchoolYearStartDate(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query("Select sy.duePkgNotificationDays as duePkgNotificationDays, sy.pkgDueNotificationLastRun as pkgDueNotificationLastRun from SchoolYear sy where sy.mealSchool.schoolId=:mealSchoolId and sy.schoolYear=:schoolYear")
	public PkgNotificationSetting getPkgDueNotificationDays(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query(value="Select schoolId from MealSchool_SchoolYear where mealSchool_schoolId=:mealSchoolId and schoolYear=:schoolYear", nativeQuery=true)
	public BigInteger getSchoolYearId(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Transactional
	@Modifying
	//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	@Query(value="Update MealSchool_SchoolYear set duePkgNotificationDays = :days where mealSchool_schoolId=:mealSchoolId and schoolYear=:schoolYear", nativeQuery=true)
	public void pkgDueNotificationDays(@Param("days") Integer days, @Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
}
