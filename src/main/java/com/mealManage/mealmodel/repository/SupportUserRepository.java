package com.mealManage.mealmodel.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.mealmodel.user.SupportUser;

import io.swagger.annotations.Api;

@Api(value = "supportUsers", description = "These API enabled for User support CRUD operation")
public interface SupportUserRepository extends JpaRepository<SupportUser, Long>{
	
	@RestResource(exported = false)
	/**This API disabled for delete the Support data**/
	public void delete(SupportUser supportUser);
	
	public Set<SupportUser> findByMealSchoolSchoolIdAndTktCurrentStatus(@Param("mealSchoolId") Long mealSchoolId, @Param("tktCurrentStatus") 
			Integer tktCurrentStatus);
	
	public Set<SupportUser> findByMealSchoolSchoolId(@Param("mealSchoolId") Long mealSchoolId);

}
