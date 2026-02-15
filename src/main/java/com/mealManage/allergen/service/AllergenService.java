package com.mealManage.allergen.service;

import com.mealManage.allergen.entity.Allergen;

import java.util.Set;

/**
 * @author Thulasiram Yachamaneni
 */
public interface AllergenService {

    public Set<Allergen> extractAllergen(String ingredients);
    
}
