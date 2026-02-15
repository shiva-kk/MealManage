package com.mealManage.mealmodel.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.caterer.CatererUser;

import io.swagger.annotations.Api;

@Api(value = "caterers", description = "These APIs are used to manage Caterer entities.")
public interface CatererRepository extends JpaRepository<Caterer, Long> {
	
	@Query(value="select c.id from Caterer c inner join CatererUser cu on c.id=cu.caterer_id where cu.username=:userName", nativeQuery=true)
	public Long getCatererRecId(@Param("userName") String userName);
	
	@Query(value="select cu.userId from Caterer c inner join CatererUser cu on c.id=cu.caterer_id where cu.username=:userName", nativeQuery=true)
	public Long getCatererUserId(@Param("userName") String userName);
	
	@Query(value="select cu.username from Caterer c inner join CatererUser cu on c.id=cu.caterer_id where cu.userId=:userId", nativeQuery=true)
	public String catererUserNameByUserId(@Param("userId") Long userId);
	
	@Transactional
	@Modifying
	@Query(value="Update CatererUser c set c.isVerified = true where c.username=:username", nativeQuery=true)
	public void updateIsVerified(@Param("username") String username);
	
	public Caterer findByCatererUsersUsername(@Param("username") String username);
	
	@Query("SELECT cu from Caterer c INNER JOIN c.catererUsers cu where cu.username= :username")
	/**This API used for get the Caterer admin user by user name**/
	public CatererUser catererUser(@Param("username") String username);
	
	@Transactional
	@Modifying
	@Query("Update Caterer c set c.isActive = false where c.id = :id")
	@PreAuthorize("hasAuthority('ROLE_CATERER') or hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for delete (i.e. inActivate) the student user by student record id**/
	public void delete(@Param("id") Long id);
	
	@Transactional
	@Modifying
	@Query(value="Update CatererUser set isActive = :isActive where username = :username", nativeQuery=true)
	public void enableDisableUser(@Param("username") String username, @Param("isActive") Boolean isActive);
	
	@Query(value="SELECT c.id,COUNT(m.schoolId) FROM MealSchool_v2 m LEFT JOIN Caterer c ON m.catererId=c.id "
			+ "WHERE c.isActive=true AND m.isActive=true GROUP BY c.id", nativeQuery=true)
	public List<Object[]> getSchoolsCountBYCaterer();
}
