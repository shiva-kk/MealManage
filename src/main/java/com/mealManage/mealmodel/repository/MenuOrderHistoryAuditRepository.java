package com.mealManage.mealmodel.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MenuOrderHistoryAudit;
import com.mealManage.mealmodel.school.SchoolGrades;

import io.swagger.annotations.Api;

@Api(value = "menuOrderHistoryAudits", description = "These API enabled for Menu order history audit.")
public interface MenuOrderHistoryAuditRepository extends JpaRepository<MenuOrderHistoryAudit, Long>{
	
	@RestResource(exported = false)
	/**This API disabled for DELETE operation**/
	public void delete(MenuOrderHistoryAudit menuOrderHistoryAudit);
	
	@SuppressWarnings("unchecked")
	@RestResource(exported = false)
	/**This API disabled for SAVE operation**/
	public MenuOrderHistoryAudit save(MenuOrderHistoryAudit menuOrderHistoryAudit);
	
	/**This API used for get all the order history related to specific order**/
	public Set<MenuOrderHistoryAudit> findByOrderId(@Param("orderId") Long orderId);
	
	/**This method used for get all the menu orders history by month, grades and school**/
	@Query("Select mod from MenuOrderHistoryAudit mod where mod.studentUser.mealSchool.schoolId = :mealSchoolId and "
			+ "mod.studentUser.gradeName in (:gradeList) and mod.yearMonth = :yearMonth and mod.menuType = :menuType"
			+ " order by mod.createdOn desc")
	public Set<MenuOrderHistoryAudit> ordersByGradesAndMonth(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("gradeList") List<SchoolGrades> gradeList, @Param("yearMonth") String yearMonth, @Param("menuType") ItemTypeConstants menuType);
	
	/**This method used for get all the menu orders by month, student record ids and school**/
	@Query("Select mod from MenuOrderHistoryAudit mod where mod.studentUser.mealSchool.schoolId = :mealSchoolId and "
			+ "mod.studentUser.userId in (:studentRecIds) and mod.yearMonth = :yearMonth  and mod.menuType = :menuType"
			+ " order by mod.createdOn desc")
	public Set<MenuOrderHistoryAudit> ordersByStudentsAndMonth(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("studentRecIds") List<Long> studentRecIds, @Param("yearMonth") String yearMonth, @Param("menuType") ItemTypeConstants menuType);

}
