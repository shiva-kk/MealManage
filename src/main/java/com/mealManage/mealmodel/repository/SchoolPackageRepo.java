package com.mealManage.mealmodel.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.mealManage.mealmodel.packages.SchoolPackage;

@Repository
public interface SchoolPackageRepo extends JpaRepository<SchoolPackage, Long>{
	
	@Transactional
	@Modifying
	@Query("Update SchoolPackage p set p.isActive = false where p.packageId = :packageId")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for delete (i.e. inActivate) the School Package by package id**/
	public void delete(@Param("packageId") Long packageId);
	
	public List<SchoolPackage> findByMealSchoolSchoolIdAndSchoolYearAndIsActive(@Param("mealSchoolId") Long mealSchoolId,
			@Param("schoolYear") Integer schoolYear, @Param("isActive") Boolean isActive);
	
	@Query(value="SELECT su.userId FROM SubscriptionsTrxByStd stx INNER JOIN StudentUser_v2 su ON stx.studentUser_userId = su.userId "
			+ "INNER JOIN SchoolPackage sp ON stx.schoolPackage_packageId = sp.packageId INNER JOIN ParentUser_v2 p on su.parentuser_userId = p.userId "
			+ "where (p.userName = :parentEmail OR p.parentAltEmail = :parentEmail) and su.isActive = true and su.isRegister = true "
			+ "and su.mealSchool_schoolId = :mealSchoolId AND su.schoolYear = :schoolYear AND sp.`type` = 'Enrollment'", nativeQuery=true)
	public List<BigInteger> getPackageRegisteredStds(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear, 
			@Param("parentEmail") String parentEmail);
	
	@Query(value="Select su.firstName, su.lastName, su.gradeName, p.packageName, s.recId as subStdId, bac.bcacAuditID, "
			+ "bac.checkIn, bac.checkOut, bac.pickupBy, su.userId, ps.trxId, ps.isPaid from PackageSubscriptionsTrx ps "
			+ "inner join SubscriptionsTrxByStd s on ps.trxId = s.PackageSubscriptionsTrx_trxId inner join "
			+ "SchoolPackage p on s.schoolPackage_packageId = p.packageId inner join StudentUser_v2 su "
			+ "on s.studentUser_userId = su.userId left join BCACAudit bac on s.recId=bac.subTrxByStd_recId "
			+ "and (DATE(bac.checkIn) = Date(:subscribeDate) or DATE(bac.checkOut) = Date(:subscribeDate)) where ps.mealSchool_schoolId = :mealSchoolId "
			+ "and DATE(:subscribeDate) between s.startDate and s.endDate", nativeQuery=true)
	public List<Object[]> getBCACSubscriptions(@Param("mealSchoolId") Long mealSchoolId, @Param("subscribeDate") String subscribeDate);
	
	@Query(value="Select pa.authorizedId, pa.firstName, pa.lastName, pa.phoneNo, pa.relation, pa.stdRecId,pa.isActive from PickupAuthorized pa "
			+ "INNER JOIN StudentUser_v2 su on pa.stdRecId = su.userId WHERE su.mealSchool_schoolId = :mealSchoolId and su.isActive = true and pa.isActive = true", nativeQuery=true)
	public List<Object[]> getAuthorizedPkp(@Param("mealSchoolId") Long mealSchoolId);
	
	@Query(value="Select pa.authorizedId, pa.firstName, pa.lastName, pa.phoneNo, pa.relation, pa.stdRecId,pa.isActive from "
			+ "PickupAuthorized pa INNER JOIN StudentUser_v2 su on pa.stdRecId = su.userId "
			+ "INNER JOIN ParentUser_v2 p ON su.parentuser_userId = p.userId where (p.userName = :parentEmail "
			+ "OR p.parentAltEmail = :parentEmail) and su.isActive = true and su.mealSchool_schoolId = :mealSchoolId and pa.isActive = true", nativeQuery=true)
	public List<Object[]> getAuthorizedPkpByParent(@Param("mealSchoolId") Long mealSchoolId, @Param("parentEmail") String parentEmail);
	
	@Query(value="SELECT su.userId AS stdRecId, stx.startDate, stx.endDate, sp.frequency, sp.packageId, sp.packageName "
			+ " FROM SubscriptionsTrxByStd stx INNER JOIN StudentUser_v2 su ON stx.studentUser_userId = su.userId "
			+ "INNER JOIN SchoolPackage sp ON stx.schoolPackage_packageId = sp.packageId INNER JOIN ParentUser_v2 p "
			+ "on su.parentuser_userId = p.userId where (p.userName = :parentEmail OR p.parentAltEmail = :parentEmail) "
			+ " and su.mealSchool_schoolId = :mealSchoolId and stx.endDate >= DATE(:currentDate) AND sp.type = 'Package'", nativeQuery=true)
	public List<Object[]> bcacSubsPackagesInfo(@Param("mealSchoolId") Long mealSchoolId, @Param("parentEmail") String parentEmail, 
			@Param("currentDate") String currentDate);
	
	@Query(value="SELECT sp.packageName, su.firstName, su.lastName, s.paidAmt, s.startDate, s.endDate, p.createdBy FROM PackageSubscriptionsTrx p "
			+ "INNER JOIN SubscriptionsTrxByStd s ON p.trxId=s.PackageSubscriptionsTrx_trxId INNER JOIN StudentUser_v2 su ON s.studentUser_userId = su.userId "
			+ "INNER JOIN SchoolPackage sp ON s.schoolPackage_packageId = sp.packageId WHERE p.trxId = :masterPkgTrxId AND p.isPaid = false;", nativeQuery=true)
	public List<Object[]> duePaymentPkgInfo(@Param("masterPkgTrxId") Long masterPkgTrxId);
	
	/*@Query(value="Select su.firstName, su.lastName, ps.createdOn, ps.paymentType, s.paidAmt, ps.transferId, ps.createdBy, "
			+ "p.packageName, s.startDate, s.endDate, p.`type` from PackageSubscriptionsTrx ps inner join "
			+ "SubscriptionsTrxByStd s on ps.trxId = s.PackageSubscriptionsTrx_trxId inner join SchoolPackage p on "
			+ "s.schoolPackage_packageId = p.packageId inner join StudentUser_v2 su on s.studentUser_userId = su.userId "
			+ "where ps.mealSchool_schoolId = :mealSchoolId AND su.schoolYear = :schoolYear and (ps.createdOn "
			+ "BETWEEN :startDt AND :endDt)", nativeQuery=true)
	public List<Object[]> packageDepositTrx(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear, 
			@Param("startDt") String startDt, @Param("endDt") String endDt);*/
	
	@Query(value="Select COUNT(s.recId) from PackageSubscriptionsTrx ps "
			+ "inner join SubscriptionsTrxByStd s on ps.trxId = s.PackageSubscriptionsTrx_trxId inner join SchoolPackage p "
			+ "on s.schoolPackage_packageId = p.packageId inner join StudentUser_v2 su on s.studentUser_userId = su.userId "
			+ "WHERE s.studentUser_userId = :studentRecId AND DATE(s.endDate) > :currDate", nativeQuery=true)
	public BigInteger checkFutureSubs(@Param("studentRecId") Long studentRecId, @Param("currDate") String currDate);
	
	@Query(value="SELECT p.packageName,COUNT(s.recId)  FROM PackageSubscriptionsTrx ps INNER JOIN SubscriptionsTrxByStd s "
			+ "ON ps.trxId = s.PackageSubscriptionsTrx_trxId INNER JOIN SchoolPackage p ON s.schoolPackage_packageId = p.packageId"
			+ " WHERE p.mealSchool_schoolId = :mealSchoolId AND :dateV BETWEEN s.startDate AND s.endDate GROUP BY p.packageName", nativeQuery=true)
	public List<Object[]> getCountByPkg(@Param("mealSchoolId") Long mealSchoolId, @Param("dateV") Date dateV);
	
	@Query(value="SELECT COUNT(s.recId) FROM PackageSubscriptionsTrx ps INNER JOIN SubscriptionsTrxByStd s ON ps.trxId = s.PackageSubscriptionsTrx_trxId "
			+ "INNER  JOIN SchoolPackage p ON s.schoolPackage_packageId = p.packageId WHERE p.mealSchool_schoolId = :mealSchoolId AND "
			+ "(:dateV BETWEEN s.startDate AND s.endDate) AND ps.isPaid = true", nativeQuery=true)
	public Integer getPaidCount(@Param("mealSchoolId") Long mealSchoolId, @Param("dateV") Date dateV);
	
	@Query(value="SELECT CONCAT(su.firstName,' ',su.lastName),p.packageName,ps.createdOn,s.startDate,s.endDate,ps.isPaid,ps.paymentType,s.paidAmt, ps.trxId "
			+ "FROM PackageSubscriptionsTrx ps INNER JOIN SubscriptionsTrxByStd s ON ps.trxId = s.PackageSubscriptionsTrx_trxId  INNER  JOIN "
			+ "SchoolPackage p ON s.schoolPackage_packageId = p.packageId INNER JOIN StudentUser_v2 su ON s.studentUser_userId=su.userId "
			+ "WHERE p.mealSchool_schoolId = :mealSchoolId AND ((s.startDate BETWEEN :startDate AND :endDate) OR "
			+ "(s.endDate BETWEEN :startDate AND :endDate))", nativeQuery=true)
	public List<Object[]> getEnrollments(@Param("mealSchoolId") Long mealSchoolId, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate);
	
	@Query(value="SELECT CONCAT(su.firstName,' ',su.lastName),p.packageName,ps.createdOn,s.startDate,s.endDate,ps.isPaid,ps.paymentType,s.paidAmt, ps.trxId "
			+ "FROM PackageSubscriptionsTrx ps INNER JOIN SubscriptionsTrxByStd s ON ps.trxId = s.PackageSubscriptionsTrx_trxId  INNER  JOIN "
			+ "SchoolPackage p ON s.schoolPackage_packageId = p.packageId INNER JOIN StudentUser_v2 su ON s.studentUser_userId=su.userId INNER JOIN "
			+ "ParentUser_v2 pu ON su.parentuser_userId = pu.userId WHERE p.mealSchool_schoolId = :mealSchoolId AND (pu.userName = :parentEmail OR "
			+ "pu.parentAltEmail = :parentEmail) AND (s.startDate IS NULL OR ((s.startDate BETWEEN :startDate AND :endDate) OR "
			+ "(s.endDate BETWEEN :startDate AND :endDate)))", nativeQuery=true)
	public List<Object[]> getEnrollmentsByParent(@Param("mealSchoolId") Long mealSchoolId, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate, @Param("parentEmail") String parentEmail);

}
