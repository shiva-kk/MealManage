package com.mealManage.mealmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mealManage.mealmodel.school.CountryDetail;

import io.swagger.annotations.Api;

@Api(value = "countryDetails", description = "These API enabled for country details data")
public interface CountryDetailsRepository extends JpaRepository<CountryDetail, Long>{
	
	@RestResource(exported = false)
	/**This API disabled for delete the country details data**/
	public void delete(CountryDetail countryDetail);
	
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public CountryDetail save(CountryDetail countryDetail);

	public CountryDetail findByCountryCode(@Param("countryCode") String countryCode);
	
	@Query("Select currencySymbol from CountryDetail where countryCode= :countryCode")
	public String getCurrencySymbol(@Param("countryCode") String countryCode);
	
	@Query("Select currencyCode from CountryDetail where countryCode= :countryCode")
	public String getCurrencyCode(@Param("countryCode") String countryCode);
	
	@Query("Select dateFormat from CountryDetail where countryCode= :countryCode")
	public String getDateFormat(@Param("countryCode") String countryCode);
	
	@Query("Select isdCode from CountryDetail where countryCode= :countryCode")
	public String getIsdCode(@Param("countryCode") String countryCode);

}
