package com.mealManage.mealmodel.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.school.School;

import io.swagger.annotations.Api;

@Api(value = "schools", description = "These API enabled for the School")
//@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
public interface SchoolRepository extends JpaRepository<School,Long> {
	
	/**This API used for get all the NotBoarded school details. It can be execute by admin or super admin user only **/
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	@Query("select s from School s left join s.mealSchool ms where ms.subdomain IS NULL")
	List<School> findNotBoardedSchool();
	
	@RestResource(exported = false)
	/**This API disabled for delete operation of School**/
	public void delete(School school);
	 
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	/**This API used for create new school. It can be execute by super admin user only. We'll disbale this API after development**/
	public School save(School school);

}
