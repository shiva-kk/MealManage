package com.mealManage.mealmodel.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.user.SuperAdminUser;

import io.swagger.annotations.Api;

@Api(value = "superAdminUsers", description = "These API enabled for the super admin user CRUD operation")
public interface SuperAdminUserRepository extends JpaRepository<SuperAdminUser, Long>{
	
	/**This method used in DAO for get the super admin user details by user name**/
	public SuperAdminUser findByUsername(@Param("username") String username);
	
	@Transactional
	@Modifying
	@Query("Update SuperAdminUser s set s.isActive = false where s.userId = :id")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for delete (i.e. in-activate) the super admin user by user id. It can be execute by super admin user only**/
	public void delete(@Param("id") Long id);
	

	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for CREATE/UPDATE the super admin user details. It can be execute by super admin user only**/
	public SuperAdminUser save(SuperAdminUser superAdminUser);
	
}
