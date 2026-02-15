package com.mealManage.allergen.repositories;

import com.mealManage.allergen.entity.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergenRepository extends JpaRepository<Allergen, Long>{



}
