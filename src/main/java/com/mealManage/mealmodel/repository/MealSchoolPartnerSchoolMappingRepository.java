package com.mealManage.mealmodel.repository;

import com.mealManage.mealmodel.school.MealSchoolPartnerSchoolMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealSchoolPartnerSchoolMappingRepository extends CrudRepository<MealSchoolPartnerSchoolMapping, Long> {

    Optional<MealSchoolPartnerSchoolMapping> findByPartnerSchoolId(String partnerSchoolId);

}