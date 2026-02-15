package com.mealManage.controller;


import com.mealManage.menu.entities.PlaceHolder;
import com.mealManage.model.Placeholder;
import com.mealManage.service.PlaceHolderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/*
  @Author sivak
 */
@RestController
@RequestMapping("mealManage/placeholder")
public class PlaceHolderController {

    private PlaceHolderService placeHolderService;

    /*
    get placeholder based on id
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlaceHolder> getPlaceHolder(@PathVariable int id){
        return new ResponseEntity<>(placeHolderService.getPlaceholder(id), HttpStatus.OK);
    }

    /*
       get all placeholders
     */
    @GetMapping("/placeholders")
    public ResponseEntity<List<PlaceHolder>> getAllPlaceHolder(){
        return new ResponseEntity<>(placeHolderService.getAllPlaceholders(),HttpStatus.OK);
    }

    /*
      save or update based on the id if id==0 then insert a
      new record else update the existing record
    */
    @PostMapping
    public ResponseEntity<String> saveOrUpdatePlaceders(@RequestBody Placeholder placeholder){
        return new ResponseEntity<>(placeHolderService.saveOrUpdatePlaceholder(placeholder),HttpStatus.OK);
    }

}
