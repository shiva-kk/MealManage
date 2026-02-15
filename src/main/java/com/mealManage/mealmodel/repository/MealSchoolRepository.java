package com.mealManage.mealmodel.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.user.SchoolUser;

import io.swagger.annotations.Api;

@Api(value = "mealSchools", description = "These API enabled for onboarded school CRUD operation")
//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
public interface  MealSchoolRepository extends JpaRepository<MealSchool,Long>{
	
	/**This API used for get the onBoarded School information by meal schoolId**/
	public MealSchool findBySchoolId(@Param("schoolId") Long id);
	
	/**This API used for get the onBoarded school information by subdomain**/
	public MealSchool findBySubdomain(@Param("subdomain") String subdomain);
	
	
	/**Find the onBoarded Active/Inactive school information **/
	public List<MealSchool> findByIsActive(@Param("isActive") Boolean isActive);
	
	//public MealSchool findBySchoolUsersUsername(@Param("username") String username);
	
	/*@Query("SELECT su from MealSchool s INNER JOIN s.schoolUsers su where s.school.schoolId= :schoolId")
	public List<SchoolUser> findBySchoolSchoolId(@Param("schoolId") Long schoolId);
	
	@Query("SELECT s.subdomain from MealSchool s INNER JOIN s.school sh where sh.schoolId= :schoolId")
	public String subdomain(@Param("schoolId") Long schoolId);*/
	
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN false ELSE true END FROM  MealSchool s WHERE s.subdomain = :subdomain")
	/**This API used for validate the subdomain**/
	public Boolean validateSubdomain(@Param("subdomain") String subdomain);
	
	/**This method used in service layer for get the meal school by school admin user**/
	public MealSchool findBySchoolUsersUsername(@Param("username") String username);
	
	@Query("SELECT su from MealSchool s INNER JOIN s.schoolUsers su where su.username= :username")
	/**This API used for get the school admin user by admin user name**/
	public SchoolUser schoolUser(@Param("username") String username);
	
	@Query("SELECT su from MealSchool s INNER JOIN s.schoolUsers su where su.userId= :userId")
	/**This API used for get the school admin user by admin user id**/
	public SchoolUser schoolUserByUserId(@Param("userId") Long userId);
	
	/*@Query("SELECT su from MealSchool s INNER JOIN s.schoolUsers su where su.userId= :schoolUserId")
	public SchoolUser schoolUserById(@Param("schoolUserId") Long schoolUserId);
	
	public Set<MealSchool> findBySchoolIdIn(@Param("mealSchoolIds") List<Long> mealSchoolIds);*/
	
	@Transactional
	@Modifying
	@Query("Update MealSchool s set s.isActive = false where s.schoolId = :id")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for inActivate the meal school (i.e. delete onboard school). It can be execute by super admin user role only**/
	public void delete(@Param("id") Long id);
	
	/*@Transactional
	@Modifying
	@Query(value="Update SchoolUser_v2 set pin = :pin where username = :username", nativeQuery=true)
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	*//**This API used for update the POS admin pin**//*
	public void updatePin(@Param("username") String username, @Param("pin") String pin);*/
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for create new onboard school or activate the meal school. It can be execute by super admin user role only**/
	public MealSchool save(MealSchool mealSchool);
	
	@Query("Select ms.schoolId from MealSchool ms INNER JOIN ms.schoolUsers su where su.username= :username")
	public Long findSchoolIdByUsername(@Param("username") String username);
	
	@Query("Select ms.schoolId as schoolId, su.firstName as firstName, su.lastName as lastName from MealSchool ms INNER JOIN ms.schoolUsers su where su.username= :username")
	public Map<String,String> findSchoolBasicInfoByUname(@Param("username") String username);
	
	@Query("Select ms.schoolName from MealSchool ms INNER JOIN ms.schoolUsers su where su.username= :username")
	public String findSchoolByUsername(@Param("username") String username);
	
	@Query("Select su.username from MealSchool ms INNER JOIN ms.schoolUsers su where ms.schoolId = :mealSchoolId and su.isVerified = true"
			+ " and su.isActive = true and su.isUnsubscribeGenNotif = 0 and su.isPrimaryUser = true")
	public List<String> allAdminEmails(@Param("mealSchoolId") Long mealSchoolId);
	
	@Query("Select ms.countryCode from MealSchool ms where ms.schoolId= :schoolId")
	public String getSchoolCountry(@Param("schoolId") Long schoolId);
	
	@Query("Select ms.schoolName from MealSchool ms where ms.schoolId= :schoolId")
	public String getSchoolName(@Param("schoolId") Long schoolId);
	
	public List<MealSchool> findByCatererId(@Param("catererId") Long catererId);
	
	public List<MealSchool> findByDistrictId(@Param("districtId") Long districtId);
	
	@Query("Select ms.schoolId from MealSchool ms where ms.districtId = :districtId")
	public List<Long> getSchoolIdsByDistrictId(@Param("districtId") Long districtId);
	
	@Query(value="SELECT l.location,SUM(s.prepaidAmt), SUM(s.ccAmt) FROM MasterTransactionsAudit m INNER JOIN "
			+ "StudentWiseTransactions s ON m.recId=s.MasterTransactionsAudit_RecId INNER JOIN PosLocation l "
			+ "ON m.locationId =l.id WHERE m.mealSchool_schoolId = :mealSchoolId AND  m.transactionType='Purchase' AND s.mealType = 'ALaCarte' "
			+ "and l.location is not null AND s.isPosted=true AND m.transactionDateTime between :startDate and :endDate GROUP BY l.location", nativeQuery=true)
	public List<Object[]> getRevenueByLoc(@Param("mealSchoolId") Long mealSchoolId, @Param("startDate") Date startDate, 
			@Param("endDate") Date endDate);
	
	@Query(value="SELECT m.schoolId,d.name,COUNT(s.userId) FROM MealSchool_v2 m LEFT JOIN StudentUser_v2 s ON "
			+ "m.schoolId = s.mealSchool_schoolId AND s.isActive=true AND s.schoolYear = (SELECT sy.schoolYear FROM MealSchool_SchoolYear sy"
			+ " WHERE sy.mealSchool_schoolId = m.schoolId AND NOW() BETWEEN sy.sessionStartDateTime AND sy.sessionEndDateTime)"
			+ " LEFT JOIN District d ON m.districtId=d.id WHERE m.isActive=true GROUP BY m.schoolId,d.name", nativeQuery=true)
	public List<Object[]> getStdCountBySchool();
	
	@Query(value="SELECT s.schoolTimezone FROM MealSchool_v2 s WHERE s.schoolId = :mealSchoolId", nativeQuery=true)
	public String getSchoolTimezone(@Param("mealSchoolId") Long mealSchoolId);
	
	/*@Transactional
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public MealSchool save(MealSchool mealSchool);*/
}
