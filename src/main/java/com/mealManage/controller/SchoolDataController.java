package com.mealManage.controller;


import com.mealManage.domain.SchoolDataDTO;
import com.mealManage.service.SchoolDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("mealManage/schooldata")
public class SchoolDataController {

    private final SchoolDataService schoolDataService;

    @Autowired
    public SchoolDataController(SchoolDataService schoolDataService) {
        this.schoolDataService = schoolDataService;
    }

    @GetMapping()
    public ResponseEntity<List<SchoolDataDTO>> getAllSchoolData() {
        List<SchoolDataDTO> schoolDataList = schoolDataService.getAllSchoolData();
        if (schoolDataList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(schoolDataList, HttpStatus.OK);
    }
}
