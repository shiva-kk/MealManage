package com.mealManage.allergen.controller;

import com.mealManage.allergen.entity.Allergen;
import com.mealManage.allergen.service.AllergenService;
import com.mealManage.menu.entities.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thulasiram Yachamaneni
 */
@RestController
@RequestMapping("mealManage/allergens")
public class AllergenController {

    @Autowired
    private AllergenService allergenService;

    @PostMapping
    public Set<Allergen> processIngredients(@RequestBody MenuItem menuCatalog) throws Exception {
        Set<Allergen> allergenList = new HashSet<>();
        if(menuCatalog==null || (menuCatalog!=null && menuCatalog.getIngredients()==null)){
            throw new Exception("Meal detail or ingredients is mandatory");
        }
        allergenList = allergenService.extractAllergen(menuCatalog.getIngredients());
        return allergenList;
    }
}
