package com.mealManage.mealmodel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import com.mealManage.mealmodel.user.ParentDeviceInfo;
import io.swagger.annotations.Api;

@Api(value = "parentDeviceInfoes", description = "These API enabled for the Parent user device info")
//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
public interface ParentDeviceInfoRepository extends JpaRepository<ParentDeviceInfo, Long> {
	
	/**This method used in DAO for get the parent registered device info by parent user name**/
	public List<ParentDeviceInfo> findByUsername(@Param("username") String username);
	
	/**This method used in DAO for get the parent registered device info by device id and parent user name**/
	public ParentDeviceInfo findByDeviceDetailsAndUsername(@Param("deviceDetails") String deviceDetails,
			@Param("username") String username);
	
	@RestResource(exported = false)
	/**This API disabled for DELETE operation**/
	public void delete(ParentDeviceInfo parentDeviceInfo);
	
	@SuppressWarnings("unchecked")
	@RestResource(exported = false)
	/**This API disabled for INSERT/UPDATE operation**/
	public ParentDeviceInfo save(ParentDeviceInfo parentDeviceInfo);

}
