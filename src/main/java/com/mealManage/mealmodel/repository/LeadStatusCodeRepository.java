package com.mealManage.mealmodel.repository;

import com.mealManage.mealmodel.user.LeadStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadStatusCodeRepository extends JpaRepository<LeadStatusCode, Long> {
}
