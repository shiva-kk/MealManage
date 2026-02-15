package com.mealManage.mealmodel.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.mealmodel.user.StudentEligibilityAudit;

public interface StudentEligibilityAuditRepo extends JpaRepository<StudentEligibilityAudit, Long> {
	
	public StudentEligibilityAudit findByStudentUserUserIdAndEffectiveEndDateAfter(@Param("userId") Long userId, @Param("currentDate") Date currentDate);
	
	@Query(value="Select su.userId from StudentUser_v2 su where su.schoolYear = :schoolYear and NOT EXISTS (select null from "
			+ "StudentEligibilityAudit o where o.studentUser_userId = su.userId)",nativeQuery=true)
	public List<Long> getStudentIds(@Param("schoolYear") Integer schoolYear);
	
	public List<StudentEligibilityAudit> findByStudentUserUserId(@Param("studentRecId") Long studentRecId);
	
	public List<StudentEligibilityAudit> findByStudentUserMealSchoolSchoolId(@Param("mealSchoolId") Long mealSchoolId);
	
	@RestResource(exported = false)
	public void delete(StudentEligibilityAudit studentEligibilityAudit);

}
