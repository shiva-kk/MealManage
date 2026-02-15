package com.mealManage.mealmodel.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import com.mealManage.mealmodel.user.FMEligibilitySurvey;
import io.swagger.annotations.Api;

@Api(value = "fMEligibilitySurveys", description = "These API enabled for Free meal eligibility Survey CRUD operation")
public interface FMEligibilitySurveyRepository extends JpaRepository<FMEligibilitySurvey, Long> {
	
	@RestResource(exported = false)
	/**This API disabled for delete the survey data**/
	public void delete(FMEligibilitySurvey fmEligibilitySurvey);
	
	public FMEligibilitySurvey findByParentEmail(@Param("parentEmail") String parentEmail);
	
	public Set<FMEligibilitySurvey> findByMealSchoolsSchoolId(@Param("mealSchoolId") Long mealSchoolId);

}
