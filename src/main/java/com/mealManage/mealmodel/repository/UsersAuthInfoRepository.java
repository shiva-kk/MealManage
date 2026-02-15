package com.mealManage.mealmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.mealmodel.user.auth.UsersAuthInfo;

import io.swagger.annotations.Api;

@Api(value = "usersAuthInfoes", description = "These API enabled for the user auth info")
//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
public interface UsersAuthInfoRepository extends JpaRepository<UsersAuthInfo, Long>{
	
	/**This method used in DAO for get the User auth info details by user-name and user role**/
	public UsersAuthInfo findByUsernameAndRole(@Param("username") String username, @Param("role") String role);
	
	/**This method used in DAO for get the user auth info details by user name**/
	public UsersAuthInfo findByUsername(@Param("username") String username);
	
	/**This API used for validate the user name. It return true if user valid to create else return false (i.e. user already exist)**/
	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN false ELSE true END FROM  UsersAuthInfo u WHERE u.username = :username")
	public Boolean validateUserName(@Param("username") String username);
	
	@RestResource(exported = false)
	/**This API disabled for delete operation**/
	public void delete(UsersAuthInfo usersAuthInfo);
	
	@SuppressWarnings("unchecked")
	@RestResource(exported = false)
	/**This API disabled for SAVE operation**/
	public UsersAuthInfo save(UsersAuthInfo usersAuthInfo);
	
	/*@Transactional
	@Modifying
	@Query(value="Update SchoolUser_v2 set isPasscodeAuth=:isPasscodeAuth where username=:username", nativeQuery=true)
	public void updateIsPasscodeAuthStatus(@Param("isPasscodeAuth") Boolean isPasscodeAuth, @Param("username") String username);*/
}
