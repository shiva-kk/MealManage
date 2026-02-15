package com.mealManage.mealmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.mealManage.menu.entities.NutritionAudit;

import io.swagger.annotations.Api;

/**This repository enable JPA repository for Nutrition Audit**/
@Api(value = "nutritionAudits", description = "These API enabled for the Item nutritios audit")
public interface NutritionAuditRepo extends JpaRepository<NutritionAudit, Long> {
	
	public NutritionAudit findByItemIdAndEffectiveEndDateIsNull(@Param("itemId") Long itemId);

}
