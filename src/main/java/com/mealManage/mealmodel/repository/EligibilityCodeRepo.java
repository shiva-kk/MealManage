package com.mealManage.mealmodel.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.menu.entities.EligibilityCode;

public interface EligibilityCodeRepo extends JpaRepository<EligibilityCode, Long> {
	
	@Transactional
	@Modifying
	@Query("Update EligibilityCode set isActive = false where eligCodeId = :id")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for inActivate the eligibility code. It can be execute by super admin user role only**/
	public void delete(@Param("id") Long id);
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for create new Eligibility code. It can be execute by super admin user role only**/
	public EligibilityCode save(EligibilityCode eligibilityCode);
	
	public List<EligibilityCode> findByIsActive(@Param("isActive") Boolean isActive);	
}
