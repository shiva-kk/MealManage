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

import com.mealManage.mealmodel.transaction.EventInfo;
import com.mealManage.response.EventsResp;
import com.mealManage.response.ParentEmailWithToken;

public interface EventInfoRepo extends JpaRepository<EventInfo, Long> {
	
	public List<EventInfo> findByMealSchoolSchoolIdAndSchoolYear(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("schoolYear") Integer schoolYear);
	
	@Transactional
	@Modifying
	@Query("Update EventInfo e set e.isActive = false where e.recId = :eventId")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for delete (i.e. inActivate) the Event by record id**/
	public void delete(@Param("eventId") Long eventId);

	@Query("Select ev from EventInfo ev where ev.mealSchool.schoolId=:mealSchoolId and isActive=true and isPublished = true and endDate >= :currentDate and schoolYear=:schoolYear")
	public List<EventInfo> getSchoolQualifyEvents(@Param("mealSchoolId") Long mealSchoolId,@Param("currentDate") Date currentDate,@Param("schoolYear") Integer schoolYear);
	
	@Query(value="Select su.userId from StudentUser_v2 su inner join ParentUser_v2 pu on pu.userId = su.parentuser_userId "
			+ "where (pu.userName = :parentEmail or pu.parentAltEmail = :parentEmail) and su.mealSchool_schoolId=:mealSchoolId "
			+ "and su.schoolYear = :schoolYear and su.gradeName in (select eg.grades_name from EventInfo_Grades eg where eg.eventInfo_Id=:eventId)"
			+ " and su.isActive=true and su.isRegister=true and su.userId not in (Select swt.studentUser_userId from StudentWiseTransactions swt where swt.eventInfo_recId=:eventId)", nativeQuery=true)
	public List<BigInteger> notPaidStdRecIds(@Param("parentEmail") String parentEmail, @Param("mealSchoolId") Long mealSchoolId, 
			@Param("schoolYear") Integer schoolYear, @Param("eventId") Long eventId);
	
	@Query(value="Select pu.parentAltEmail as altEmailId, ua2.fToken as altToken, pu.userName as priEmailId,ua1.fToken as priToken from "
			+ "StudentUser_v2 su inner join ParentUser_v2 pu on pu.userId=su.parentuser_userId inner join UserAuthInfo_v2 ua1 on "
			+ "pu.userName=ua1.username left join UserAuthInfo_v2 ua2 on pu.parentAltEmail=ua2.username where su.mealSchool_schoolId=:mealSchoolId "
			+ "and su.schoolYear=:schoolYear and su.isActive=1 and su.isRegister=1 and pu.isActive=1 and pu.userName<>'NA' and su.gradeName in "
			+ "(select eg.grades_name from EventInfo_Grades eg where eg.eventInfo_Id=:eventId) group by pu.userName,pu.parentAltEmail", nativeQuery=true)
	public List<ParentEmailWithToken> getParentInfo(@Param("mealSchoolId") Long mealSchoolId, @Param("schoolYear") Integer schoolYear, 
			@Param("eventId") Long eventId);
	
	@Query(value="select e.amount,mta.createdBy,e.eventName,e.type as eventType,su.firstName,su.lastName,e.longDesc,mta.note,e.recId,su.studentId,swt.transactionAmount,mta.transactionDateTime,"
			+ "mta.transactionDescription,mta.transferId,su.userId from MasterTransactionsAudit mta Inner Join StudentWiseTransactions swt "
			+ "on mta.recId = swt.MasterTransactionsAudit_RecId Inner Join StudentUser_v2 su on swt.studentUser_userId=su.userId inner join EventInfo e on "
			+ "swt.eventInfo_recId=e.recId where mta.mealSchool_schoolId = :mealSchoolId and mta.transactionType = 'Event' and (mta.transactionDateTime "
			+ "between :startDate and :endDate) and su.schoolYear = :schoolYear", nativeQuery=true)
	public List<EventsResp> getEventsReport(@Param("mealSchoolId") Long mealSchoolId, @Param("startDate") String startDate, @Param("endDate") String endDate, 
			@Param("schoolYear") Integer schoolYear);
}