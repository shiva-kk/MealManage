package com.mealManage.mealmodel.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.school.LowBalanceSchoolSetting;

import io.swagger.annotations.Api;

@Api(value = "lowBalanceSchoolSettings", description = "These API enabled for the low balance school setting CRUD operation")
public interface LowBalanceSchoolSettingRepository extends JpaRepository<LowBalanceSchoolSetting, Long>{
	
	@RestResource(exported = false)
	/**This API disabled for delete the survey data**/
	public void delete(LowBalanceSchoolSetting lowBalanceSchoolSetting);
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for SAVE the low balance school setting **/
	public LowBalanceSchoolSetting save(LowBalanceSchoolSetting lowBalanceSchoolSetting);
	
	public Set<LowBalanceSchoolSetting> findByMealSchoolSchoolId(@Param("schoolId") Long schoolId);

}
