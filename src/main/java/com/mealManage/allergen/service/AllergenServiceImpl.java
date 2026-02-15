package com.mealManage.allergen.service;

import com.mealManage.allergen.entity.Allergen;
import com.mealManage.allergen.repositories.AllergenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Thulasiram Yachamaneni
 */
@Service
public class AllergenServiceImpl implements AllergenService {

    @Autowired
    private AllergenRepository allergenRepository;

    @Override
    public Set<Allergen> extractAllergen(String ingredients) {
        Set<Allergen> allergenList = new HashSet<>();
        //The ingredients is a "comma" separated string.
        //Convert the ingredients into list of strings
        List<String> ingredientsList = Stream.of(ingredients.split(",", -1))
                .collect(Collectors.toList());
        List<Allergen> dbAllergenSet = allergenRepository.findAll();
        ingredientsList.stream().forEach(ingredient -> {
            String finalIngredient = ingredient.trim();
            Optional<Allergen> matchedAllergen = dbAllergenSet.stream().filter(allergen -> allergen.getName().toLowerCase().contains(finalIngredient.toLowerCase())).findFirst();
            if(matchedAllergen.isPresent()){
                Allergen allergen = matchedAllergen.get();
                Allergen updatedAllergen = new Allergen();
                updatedAllergen.setName(ingredient);
                updatedAllergen.setCategory(allergen.getCategory());
                allergenList.add(updatedAllergen);
            }
        });
        //if it matches, add it to new allergen list and return
        return allergenList;
    }
}
