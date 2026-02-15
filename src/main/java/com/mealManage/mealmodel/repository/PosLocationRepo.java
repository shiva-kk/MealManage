package com.mealManage.mealmodel.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.meal.PosLocation;

public interface PosLocationRepo extends JpaRepository<PosLocation, Long> {
	
	public List<PosLocation> findByMealSchoolSchoolId(@Param("mealSchoolId") Long mealSchoolId);
	
	public List<PosLocation> findByMealSchoolSchoolIdAndIsActive(@Param("mealSchoolId") Long mealSchoolId, 
			@Param("isActive") Boolean isActive);
	
	@Transactional
	@Modifying
	@Query("Update PosLocation s set s.isActive = false where s.id = :id")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	/**This API used for inActivate the POS location and admin user role only**/
	public void delete(@Param("id") Long id);
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for create new / update POS location**/
	public PosLocation save(PosLocation posLocation);

}
