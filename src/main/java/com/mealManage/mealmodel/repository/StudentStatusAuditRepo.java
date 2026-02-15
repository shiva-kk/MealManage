package com.mealManage.mealmodel.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.mealManage.mealmodel.user.StudentStatusAudit;

@Repository
/**This repository enable for Student status audit entity**/
public interface StudentStatusAuditRepo extends JpaRepository<StudentStatusAudit, Long>{
	
	@RestResource(exported = false)
	public void delete(StudentStatusAudit studentStatusAudit);
	
	public StudentStatusAudit findByStudentUserUserIdAndEffectiveEndDateAfter(@Param("userId") Long userId, @Param("endDate") Date endDate);
	
	@Query(value="Select su.userId from StudentUser_v2 su where su.schoolYear = :schoolYear and NOT EXISTS (select null from "
			+ "StudentStatusAudit ss where ss.studentUser_userId = su.userId)",nativeQuery=true)
	public List<Long> getStudentIds(@Param("schoolYear") Integer schoolYear);
	
	public List<StudentStatusAudit> findByStudentUserUserId(@Param("studentRecId") Long studentRecId);
	
	public List<StudentStatusAudit> findByStudentUserMealSchoolSchoolId(@Param("mealSchoolId") Long mealSchoolId);

}
