package com.mealManage.mealmodel.repository;

import java.math.BigInteger;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.DistrictUser;

import io.swagger.annotations.Api;

@Api(value = "districts", description = "These APIs are used to manage District entities.")
public interface DistrictRepository extends JpaRepository<District, Long> {
	
	@Query(value="select c.id from District c inner join DistrictUser cu on c.id=cu.district_id where cu.username=:userName", nativeQuery=true)
	public Long getDistrictRecId(@Param("userName") String userName);
	
	@Query(value="select cu.userId from District c inner join DistrictUser cu on c.id=cu.district_id where cu.username=:userName", nativeQuery=true)
	public Long getDistrictUserId(@Param("userName") String userName);
	
	@Query(value="select cu.username from District c inner join DistrictUser cu on c.id=cu.district_id where cu.userId=:userId", nativeQuery=true)
	public String districtUserNameByUserId(@Param("userId") Long userId);
	
	@Query("Select d.countryCode from District d where d.id= :distId")
	public String getSchoolCountry(@Param("distId") Long distId);
	
	@Query("Select d.name from District d where d.id= :distId")
	public String getDistName(@Param("distId") Long distId);
	
	@Query("Select d.timezone from District d where d.id= :distId")
	public String getTimezone(@Param("distId") Long distId);
	
	@Transactional
	@Modifying
	@Query(value="Update DistrictUser c set c.isVerified = true where c.username=:username", nativeQuery=true)
	public void updateIsVerified(@Param("username") String username);
	
	public District findByDistrictUsersUsername(@Param("username") String username);
	
	public List<District> findByCountryCode(@Param("countryCode") String countryCode);
	
	@Query("SELECT cu from District c INNER JOIN c.districtUsers cu where cu.username= :username")
	/**This API used for get the District admin user by user name**/
	public DistrictUser districtUser(@Param("username") String username);
	
	@Transactional
	@Modifying
	@Query("Update District c set c.isActive = false where c.id = :id")
	@PreAuthorize("hasAuthority('ROLE_DISTRICT') or hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for delete (i.e. inActivate) the district user by record id**/
	public void delete(@Param("id") Long id);
	
	@Transactional
	@Modifying
	@Query(value="Update DistrictUser set isActive = :isActive where username = :username", nativeQuery=true)
	public void enableDisableUser(@Param("username") String username, @Param("isActive") Boolean isActive);
	
	@Query(value="SELECT m.schoolId, m.schoolName, sum(if(su.accBalance,su.accBalance,0)), sy.schoolYear, "
			+ "sum(if(su.isFreeMealEligible,1,0)) AS freeStd , sum(if(su.isReducePriceEligible,1,0)) AS redStd, "
			+ "sum(if(su.isFreeMealEligible,0,if(su.isReducePriceEligible, 0, 1))) AS regStd, count(su.userId),m.subdomain FROM MealSchool_v2 m "
			+ " LEFT JOIN MealSchool_SchoolYear sy ON m.schoolId = sy.mealSchool_schoolId AND (:currentDate BETWEEN "
			+ "sy.sessionStartDateTime AND sy.sessionEndDateTime) "
			+ "LEFT JOIN  StudentUser_v2 su ON m.schoolId=su.mealSchool_schoolId AND su.isActive = 1 AND su.schoolYear=sy.schoolYear "
			+ " WHERE m.districtId = :districtId"
			+ " GROUP BY m.schoolId, m.schoolName, sy.schoolYear", nativeQuery=true)
	public List<Object[]> getSchoolLedger(@Param("districtId") Long districtId, @Param("currentDate") String currentDate);
	
	@Query(value="SELECT m.schoolId, SUM(swt.transactionAmount) FROM MasterTransactionsAudit t INNER JOIN "
			+ "StudentWiseTransactions swt ON t.recId=swt.MasterTransactionsAudit_RecId INNER JOIN StudentUser_v2 su "
			+ "ON swt.studentUser_userId = su.userId INNER JOIN MealSchool_v2 m ON su.mealSchool_schoolId = "
			+ "m.schoolId INNER JOIN MealSchool_SchoolYear sy ON su.mealSchool_schoolId = sy.mealSchool_schoolId AND "
			+ "su.schoolYear = sy.schoolYear AND (:currentDate BETWEEN sy.sessionStartDateTime AND sy.sessionEndDateTime)"
			+ " WHERE t.transactionType IN ('Deposit','InstantPayment') AND t.paymentType is not null and "
			+ "t.paymentType != 'TransferCR' and t.paymentType != 'Wallet' AND swt.isPosted = 1 AND m.districtId = :districtId"
			+ " AND (t.transactionDateTime BETWEEN sy.sessionStartDateTime AND sy.sessionEndDateTime) GROUP BY m.schoolId", nativeQuery=true)
	public List<Object[]> getDeposits(@Param("districtId") Long districtId, @Param("currentDate") String currentDate);
	
	@Query(value="SELECT su.mealSchool_schoolId, sum(if(m.isEligibleForFreeMeal,1,0)) AS freeOrd,"
			+ " sum(if(m.isEligibleForReducedPrice,1,0)) AS redOrd, "
			+ " sum(if(m.isEligibleForFreeMeal,0,if(m.isEligibleForReducedPrice, 0, 1))) AS regStd,"
			+ " COUNT(m.schoolId) totOrd FROM MealOrdersAudit_v2 m INNER JOIN StudentUser_v2 su ON "
			+ "m.studentUser_userId = su.userId INNER JOIN MealSchool_v2 ms ON su.mealSchool_schoolId = ms.schoolId"
			+ " WHERE ms.districtId = :districtId AND su.isActive = 1 AND m.yearMonth = :yearMonth GROUP BY su.mealSchool_schoolId", nativeQuery=true)
	public List<Object[]> getOrders(@Param("districtId") Long districtId, @Param("yearMonth") String yearMonth);
	
	@Query(value="SELECT su.mealSchool_schoolId, m.schoolName, su.schoolYear, su.isFreeMealEligible , su.isReducePriceEligible,"
			+ "su.decisionReason, su.category, su.isActive,count(su.userId),su.reCertificateDate,su.recertPending,su.actualPrg FROM StudentUser_v2 su INNER JOIN MealSchool_v2 m ON"
			+ " su.mealSchool_schoolId=m.schoolId INNER JOIN MealSchool_SchoolYear sy ON su.schoolYear = sy.schoolYear "
			+ "AND m.schoolId = sy.mealSchool_schoolId AND (:currentDate BETWEEN sy.sessionStartDateTime AND "
			+ "sy.sessionEndDateTime) WHERE m.districtId = :districtId GROUP BY su.mealSchool_schoolId,"
			+ " m.schoolName, su.schoolYear, su.isFreeMealEligible , su.isReducePriceEligible,su.decisionReason, su.category, su.isActive,su.reCertificateDate,su.recertPending,su.actualPrg "
			+ "ORDER BY su.mealSchool_schoolId", nativeQuery=true)
	public List<Object[]> getEligSummary(@Param("districtId") Long districtId, @Param("currentDate") String currentDate);
	
	@Query(value="SELECT su.mealSchool_schoolId, m.schoolName, su.schoolYear, su.isFreeMealEligible , su.isReducePriceEligible,"
			+ "su.decisionReason, su.category, su.isActive,count(su.userId),su.reCertificateDate,su.recertPending,su.actualPrg FROM StudentUser_v2 su INNER JOIN MealSchool_v2 m ON"
			+ " su.mealSchool_schoolId=m.schoolId INNER JOIN MealSchool_SchoolYear sy ON su.schoolYear = sy.schoolYear "
			+ "AND m.schoolId = sy.mealSchool_schoolId AND (:currentDate BETWEEN sy.sessionStartDateTime AND "
			+ "sy.sessionEndDateTime) WHERE m.schoolId = :schoolId GROUP BY su.mealSchool_schoolId,"
			+ " m.schoolName, su.schoolYear, su.isFreeMealEligible , su.isReducePriceEligible,su.decisionReason, su.category, su.isActive,su.reCertificateDate,su.recertPending,su.actualPrg "
			+ "ORDER BY su.mealSchool_schoolId", nativeQuery=true)
	public List<Object[]> getEligSummaryBySchool(@Param("schoolId") Long schoolId, @Param("currentDate") String currentDate);
	
	@Query(value="SELECT schoolId from MealSchool_v2 where districtId=:districtId", nativeQuery=true)
	public List<BigInteger> getSchoolIds(@Param("districtId") Long districtId);

}
