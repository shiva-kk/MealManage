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
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.StdCountByElig;
import com.mealManage.mealmodel.user.StudentUser;

import io.swagger.annotations.Api;

@Api(value = "studentUsers", description = "These API enabled for the Student & parent user")
//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
public interface StudentUserRepository extends JpaRepository<StudentUser, Long> {
	
	/*public List<StudentUser> findByParentuserUserNameAndIsActive(@Param("username") String username, @Param("isActive") Boolean isActive);
	
	public List<StudentUser> findByIsActive(@Param("isActive") Boolean isActive);*/
	
	/**This API used for get all the active/inactive student details by meal school id, isActive flag and school year***/
	public List<StudentUser> findByIsActiveAndMealSchoolSchoolIdAndSchoolYear(@Param("isActive") Boolean isActive, 
			@Param("schoolId") Long schoolId, @Param("schoolYear") Integer schoolYear);
	
	/*@Query("SELECT p FROM StudentUser s INNER JOIN s.parentuser p where p.userName = :username and s.isActive = :isActive")
	public ParentUser findByUsernameAndIsActive(@Param("username") String username, @Param("isActive") Boolean isActive);*/
	
	@Query("SELECT p FROM StudentUser s RIGHT JOIN s.parentuser p where p.userName = :username")
	/**This method used in DAO for get the parent user details by parent user name**/
	public ParentUser findByUsername(@Param("username") String username);

	/**This API used for get all student user details by meal school id, grade name, isActive status and school year**/
	public Set<StudentUser> findByMealSchoolSchoolIdAndGradeNameAndIsActiveAndSchoolYear(@Param("schoolId") Long schoolId, 
			@Param("gradeName") SchoolGrades gradeName, @Param("isActive") Boolean isActive, @Param("schoolYear") Integer schoolYear);
	
	@Query("SELECT p FROM StudentUser s INNER JOIN s.parentuser p where s.mealSchool.schoolId = :schoolId and "
			+ "s.gradeName = :gradeName and s.isActive = :isActive and s.schoolYear = :schoolYear")
	/**This method used in DAO for get all the parent user details by meal school id, grade name and isActive status**/
	public Set<ParentUser> findByMealSchoolAndGradeNameAndIsActiveAndSchoolYear(@Param("schoolId") Long schoolId, 
			@Param("gradeName") SchoolGrades gradeName, @Param("isActive") Boolean isActive, @Param("schoolYear") Integer schoolYear);
	
	@Query("SELECT p FROM StudentUser s INNER JOIN s.parentuser p where s.mealSchool.schoolId = :schoolId and "
			+ "s.isActive = :isActive and s.schoolYear = :schoolYear")
	/**This method used in DAO for get all the parent user details by meal school id and isActive status**/
	public Set<ParentUser> findByMealSchoolSchoolIdAndIsActiveAndYear(@Param("schoolId") Long schoolId, 
			@Param("isActive") Boolean isActive, @Param("schoolYear") Integer schoolYear);
	
	/*@Query("SELECT p FROM StudentUser s INNER JOIN s.parentuser p where s.id IN (:Ids)")
	public Set<ParentUser> findParentsByStudentIdIn(@Param("Ids") List<Long> Ids);*/
	
	@Query("SELECT p FROM StudentUser s INNER JOIN s.parentuser p where s.studentId IN (:studentIds) and "
			+ "s.mealSchool.schoolId = :schoolId and s.isActive = :isActive and s.schoolYear = :schoolYear")
	/**This method used in DAO for get all the parent user details by student id IN, meal school id and isActive status**/
	public Set<ParentUser> findByStudentIdInAndMealSchoolIdAndIsActiveAndYear(@Param("studentIds") List<String> studentIds, 
			@Param("schoolId") Long schoolId, @Param("isActive") Boolean isActive, @Param("schoolYear") Integer schoolYear);
	
	@Transactional
	@Modifying
	@Query("UPDATE StudentUser s SET s.isRegister = true where s.mealSchool.schoolId = :schoolId and s.gradeName = :gradeName "
			+ "and s.isRegister = false and s.isActive = true and s.schoolYear = :schoolYear")
	/**This method used for register the students by meal school id, grade name which having isRegister as false and isActive as true**/
	public void registerStudentBySchoolAndGradeAndYear(@Param("schoolId") Long schoolId, @Param("gradeName") SchoolGrades 
			gradeName, @Param("schoolYear") Integer schoolYear);
	
	@Transactional
	@Modifying
	@Query("UPDATE StudentUser s SET s.isRegister = true where s.mealSchool.schoolId = :schoolId and s.isRegister = false "
			+ "and s.isActive = true and s.schoolYear = :schoolYear")
	/**This method used for register the students by meal school id which having isRegister as false and isActive as true**/
	public void registerStudentBySchoolAndYear(@Param("schoolId") Long schoolId, @Param("schoolYear") Integer schoolYear);

	@Transactional
	@Modifying
	@Query("UPDATE StudentUser s SET s.isRegister = true where s.studentId IN (:studentIds) and s.mealSchool.schoolId = :schoolId "
			+ "and s.isRegister = false and s.isActive = true and s.schoolYear = :schoolYear")
	/**This method used for register the students by meal school id, student ids IN. which having isRegister as false and isActive as true**/
	public void registerStudentByStudentIdsAndSchoolIdAndYear(@Param("studentIds") List<String> studentIds, 
			@Param("schoolId") Long schoolId, @Param("schoolYear") Integer schoolYear);
	
	@Modifying
	@Query("Update StudentUser s set s.isActive = :isActive where s.userId = :studentRecId")
	/**This method used for activate/deactivate the student**/
	public void changeStdStatus(@Param("studentRecId") Long studentRecId, @Param("isActive") Boolean isActive);
	
	@Transactional
	@Modifying
	@Query("Update StudentUser s set s.isActive = false where s.userId = :id")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for delete (i.e. inActivate) the student user by student record id**/
	public void delete(@Param("id") Long id);
	
	@Query("SELECT s FROM StudentUser s INNER JOIN s.parentuser p where (p.userName = :username OR p.parentAltEmail = :username) "
			+ "and s.isActive = true and s.isRegister = true and s.mealSchool.isActive = true and s.schoolYear = :schoolYear")
	/**This API used for get all the student user details (i.e. All the registered student which having meal school & 
	 * student isActive status as true and school year)  by parent user name.**/
	public List<StudentUser> findByEmailAndYear(@Param("username") String username, @Param("schoolYear") Integer schoolYear);
	
	//public Set<StudentUser> findByUserIdInAndIsActive(@Param("studentUserIds") List<Long> studentUserIds, @Param("isActive") Boolean isActive);
	
	/**This method used in DAO for get the student user details by student record id and isActive status**/
	public StudentUser findByUserIdAndIsActive(@Param("studentUserId") Long studentUserId, @Param("isActive") Boolean isActive);
	
	/**This method used in DAO for get the student user details by student record id, isActive and isRegister status**/
	public StudentUser findByUserIdAndIsActiveAndIsRegister(@Param("studentUserId") Long studentUserId, 
			@Param("isActive") Boolean isActive, @Param("isRegister") Boolean isRegister);
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	/**This API used for SAVE the student user **/
	public StudentUser save(StudentUser studentUser);
	
	public Set<StudentUser> findByMealSchoolSchoolIdAndIsRegisterAndIsActiveAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("isRegister") Boolean isRegister, @Param("isActive") Boolean isActive, @Param("schoolYear") Integer schoolYear);
	
	public Set<StudentUser> findByMealSchoolSchoolIdAndIsActiveAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("isActive") Boolean isActive, @Param("schoolYear") Integer schoolYear);
	
	public Set<StudentUser> findByMealSchoolSchoolIdAndIsRegisterAndIsActiveAndGradeNameInAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("isRegister") Boolean isRegister, @Param("isActive") Boolean isActive, @Param("schoolGrades") List<SchoolGrades> schoolGrades,
			@Param("schoolYear") Integer schoolYear);
	
	public Set<StudentUser> findByMealSchoolSchoolIdAndIsActiveAndGradeNameInAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("isActive") Boolean isActive, @Param("schoolGrades") List<SchoolGrades> schoolGrades,
			@Param("schoolYear") Integer schoolYear);
	
	@Query("SELECT p FROM StudentUser s INNER JOIN s.parentuser p where p.userName = :email OR p.parentAltEmail = :email")
	/**This method used in DAO for get all the parent user details by parent user email id**/
	public Set<ParentUser> findUsersByEmail(@Param("email") String email);
	
	public StudentUser findByMealSchoolSchoolIdAndStudentIdAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("studentId") String studentId, @Param("schoolYear") Integer schoolYear);
	
	public StudentUser findByMealSchoolSchoolIdAndStudentIdAndSchoolYearAndIsActive(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("studentId") String studentId, @Param("schoolYear") Integer schoolYear, @Param("isActive") Boolean isActive);
	
	/**This method used for get all the meal school ids by parent email address**/
	@Query("SELECT ms FROM StudentUser s INNER JOIN s.parentuser p INNER JOIN s.mealSchool ms where "
			+ "(p.userName = :username OR p.parentAltEmail = :username) and s.isActive = true and s.isRegister = true and "
			+ "s.mealSchool.isActive = true group by ms.schoolId")
	public Set<MealSchool> schoolsByEmail(@Param("username") String username);
	
	@Query("SELECT s FROM StudentUser s INNER JOIN s.parentuser p where (p.userName = :username OR p.parentAltEmail = :username) "
			+ "and s.isActive = true and s.isRegister = true and s.mealSchool.isActive = true and s.schoolYear = :schoolYear "
			+ "and s.mealSchool.schoolId = :mealSchoolId")
	/**This API used for get all the student user details (i.e. All the registered student which having meal school & 
	 * student isActive status as true and school year)  by parent user name.**/
	public List<StudentUser> findByEmailAndYearAndSchool(@Param("username") String username, @Param("schoolYear") Integer schoolYear, 
			@Param("mealSchoolId") Long mealSchoolId);
	
	/**This API used for get the all student count**/
	@Query("SELECT count(s) FROM StudentUser s where s.isActive = true and"
			+ " s.schoolYear = :schoolYear and s.mealSchool.schoolId = :mealSchoolId and s.isFreeMealEligible = true and s.gradeName != 'staff'")
	public Integer freeStudentsCount(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	/**This API used for get the all student count**/
	@Query("SELECT count(s) FROM StudentUser s where s.isActive = true and"
			+ " s.schoolYear = :schoolYear and s.mealSchool.districtId = :districtId and s.isFreeMealEligible = true and s.gradeName != 'staff'")
	public Integer distFreeStudentsCount(@Param("districtId") Long districtId, @Param("schoolYear") Integer schoolYear);
	
	/**This API used for get the all student count**/
	@Query("SELECT count(s) FROM StudentUser s where s.isActive = true and s.schoolYear = :schoolYear"
			+ " and s.mealSchool.schoolId = :mealSchoolId and s.isReducePriceEligible = true and s.isFreeMealEligible = false and s.gradeName != 'staff'")
	public Integer reducedStudentsCount(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	/**This API used for get the all student count**/
	@Query("SELECT count(s) FROM StudentUser s where s.isActive = true and s.schoolYear = :schoolYear"
			+ " and s.mealSchool.districtId = :districtId and s.isReducePriceEligible = true and s.isFreeMealEligible = false and s.gradeName != 'staff'")
	public Integer distReducedStudentsCount(@Param("districtId") Long districtId, @Param("schoolYear") Integer schoolYear);
	
	/**This API used for get the all student count**/
	@Query("SELECT count(s) FROM StudentUser s where s.isActive = true and"
			+ " s.schoolYear = :schoolYear and s.mealSchool.schoolId = :mealSchoolId and s.gradeName != 'staff'")
	public Integer totalStudentsCount(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	/**This API used for get the all student count**/
	@Query("SELECT count(s) FROM StudentUser s where s.isActive = true and"
			+ " s.schoolYear = :schoolYear and s.mealSchool.districtId = :districtId and s.gradeName != 'staff'")
	public Integer distTotalStudentsCount(@Param("districtId") Long districtId, @Param("schoolYear") Integer schoolYear);
	
	public List<StudentUser> findByMealSchoolSchoolIdAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query(value="Select su.* from StudentUser_v2 su where su.schoolYear = :schoolYear and not EXISTS (select null from "
			+ "StudentEligibilityAudit o where o.studentUser_userId = su.userId)", nativeQuery=true)
	public List<StudentUser> getExistingStds(@Param("schoolYear") Integer schoolYear);
	
	@Query(value="Select su.* from StudentUser_v2 su where su.schoolYear = :schoolYear and not EXISTS (select null from "
			+ "StudentStatusAudit o where o.studentUser_userId = su.userId)", nativeQuery=true)
	public List<StudentUser> getExistingStdsForStatus(@Param("schoolYear") Integer schoolYear);
	
	@Query(value="Select count(elg.recId) as countVal, elg.currentEligStatus as elgStatus from StudentEligibilityAudit elg "
			+ "inner join StudentUser_v2 su on elg.studentUser_userId=su.userId and su.mealSchool_schoolId=:mealSchoolId "
			+ "inner join StudentStatusAudit sa on sa.studentUser_userId = su.userId "
			+ "where elg.schoolYear=:schoolYear and su.gradeName != 'staff' and (:auditDate between elg.effectiveStartDate and elg.effectiveEndDate) and"
			+ " (:auditDate between sa.effectiveStartDate and sa.effectiveEndDate) and sa.currentStatus=true group by elg.currentEligStatus", nativeQuery=true)
	public List<StdCountByElig> countByElg(@Param("auditDate") String auditDate, 
			@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	public List<StudentUser> findByUserIdIn(@Param("studentRecIds") List<Long> studentRecIds);
	
	@Query(value="Select su.studentId from StudentWiseTransactions swt inner join MasterTransactionsAudit mta on "
			+ "swt.MasterTransactionsAudit_RecId = mta.recId inner join StudentUser_v2 su on swt.studentUser_userId = su.userId "
			+ "where mta.mealSchool_schoolId = :mealSchoolId and su.schoolYear = :schoolYear and DATE(mta.transactionDateTime) = :startDate"
			+ " and mta.purchaseItemType = :menuType and swt.isPosted=true and mta.transactionType='Purchase'", nativeQuery=true)
	public List<String> getServedStdIds(@Param("mealSchoolId") Long mealSchoolId, @Param("startDate") Date startDate, @Param("schoolYear") Integer schoolYear,
			@Param("menuType") String menuType);
	
	@Query(value="SELECT MAX(CAST(su.pin as unsigned)) FROM StudentUser_v2 su WHERE su.mealSchool_schoolId=:mealSchoolId and su.schoolYear=:schoolYear", nativeQuery=true)
	public String getMaxPin(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query(value="SELECT su.pin FROM StudentUser_v2 su WHERE su.mealSchool_schoolId=:mealSchoolId and su.schoolYear=:schoolYear", nativeQuery=true)
	public List<String> getUsedPins(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query(value="SELECT su.userId FROM StudentUser_v2 su WHERE su.mealSchool_schoolId=:mealSchoolId AND su.gradeName=:grade AND su.pin IS NULL;", nativeQuery=true)
	public List<BigInteger> getIdsWithoutPin(@Param("mealSchoolId") Long mealSchoolId, @Param("grade") String grade);

	public List<StudentUser> findByStudentIdInAndMealSchoolSchoolIdAndSchoolYear(@Param("stdIds") Set<String> stdIds, @Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear);
	
	@Query(value="SELECT m.schoolName,SUM(su.accBalance) FROM MealSchool_v2 m LEFT JOIN StudentUser_v2 su ON m.schoolId=su.mealSchool_schoolId AND su.schoolYear=:schoolYear AND ((:isStaff AND su.gradeName = 'staff') "
			+ "OR (!:isStaff AND su.gradeName != 'staff')) AND su.accBalance>0 AND su.isActive=TRUE WHERE ((!:isDistrict AND m.schoolId = :id) OR (:isDistrict AND m.districtId=:id)) group by m.schoolName", nativeQuery=true)
	public List<Object[]> getPostiveBal(@Param("id") Long id, @Param("isDistrict") Boolean isDistrict, @Param("isStaff") Boolean isStaff, @Param("schoolYear") Integer schoolYear);
	
	@Query(value="SELECT m.schoolName,SUM(su.accBalance) FROM MealSchool_v2 m LEFT JOIN StudentUser_v2 su ON m.schoolId=su.mealSchool_schoolId AND su.schoolYear=:schoolYear AND ((:isStaff AND su.gradeName = 'staff') "
			+ "OR (!:isStaff AND su.gradeName != 'staff')) AND su.accBalance<0 AND su.isActive=TRUE WHERE ((!:isDistrict AND m.schoolId = :id) OR (:isDistrict AND m.districtId=:id)) group by m.schoolName", nativeQuery=true)
	public List<Object[]> getNegativeBal(@Param("id") Long id, @Param("isDistrict") Boolean isDistrict, @Param("isStaff") Boolean isStaff, @Param("schoolYear") Integer schoolYear);
	
	@Query(value="SELECT m.schoolName,SUM(swt.finalBalance) FROM MealSchool_v2 m LEFT JOIN MasterTransactionsAudit mta ON mta.mealSchool_schoolId=m.schoolId LEFT JOIN StudentWiseTransactions swt ON swt.MasterTransactionsAudit_RecId=mta.recId "
			+ " AND swt.finalBalance > 0 AND swt.recId IN (SELECT MAX(s.recId) FROM StudentWiseTransactions s INNER JOIN MasterTransactionsAudit m on s.MasterTransactionsAudit_RecId=m.recId INNER JOIN StudentUser_v2 su ON s.studentUser_userId=su.userId WHERE su.schoolYear=:schoolYear AND m.transactionDateTime <= :dateV and su.isActive=true AND ((:isStaff AND su.gradeName = 'staff') OR (!:isStaff AND su.gradeName != 'staff')) GROUP BY "
			+ "s.studentUser_userId) where ((!:isDistrict AND m.schoolId = :id) OR (:isDistrict AND m.districtId=:id)) group by m.schoolName", nativeQuery=true)
	public List<Object[]> getPDPostiveBal(@Param("id") Long id, @Param("isDistrict") Boolean isDistrict, @Param("isStaff") Boolean isStaff, @Param("schoolYear") Integer schoolYear, @Param("dateV") Date dateV);
	
	@Query(value="SELECT m.schoolName,SUM(swt.finalBalance) FROM MealSchool_v2 m LEFT JOIN MasterTransactionsAudit mta ON mta.mealSchool_schoolId=m.schoolId LEFT JOIN StudentWiseTransactions swt ON swt.MasterTransactionsAudit_RecId=mta.recId "
			+ " AND swt.finalBalance < 0 AND swt.recId IN (SELECT MAX(s.recId) FROM StudentWiseTransactions s INNER JOIN MasterTransactionsAudit m on s.MasterTransactionsAudit_RecId=m.recId INNER JOIN StudentUser_v2 su ON s.studentUser_userId=su.userId WHERE su.schoolYear=:schoolYear AND m.transactionDateTime <= :dateV and su.isActive=true AND ((:isStaff AND su.gradeName = 'staff') OR (!:isStaff AND su.gradeName != 'staff')) GROUP BY "
			+ "s.studentUser_userId) where ((!:isDistrict AND m.schoolId = :id) OR (:isDistrict AND m.districtId=:id)) group by m.schoolName", nativeQuery=true)
	public List<Object[]> getPDNegativeBal(@Param("id") Long id, @Param("isDistrict") Boolean isDistrict, @Param("isStaff") Boolean isStaff, @Param("schoolYear") Integer schoolYear,  @Param("dateV") Date dateV);
}
