package com.mealManage.mealmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.domain.TierInfo;

public interface TierInfoRepo extends JpaRepository<TierInfo, Long>{
	
	@RestResource(exported = false)
	/**This API disabled for delete the tier data**/
	public void delete(TierInfo tierInfo);
	
	@SuppressWarnings("unchecked")
	@RestResource(exported = false)
	/**This API disabled for delete the tier data**/
	public TierInfo save(TierInfo tierInfo);

}
