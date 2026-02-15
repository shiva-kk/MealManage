package com.mealManage.service;

import com.mealManage.dao.SchoolDataDao;
import com.mealManage.domain.SchoolDataDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SchoolDataService {

    private final SchoolDataDao schoolDataDao;

    @Autowired
    public SchoolDataService(SchoolDataDao schoolDataDao) {
        this.schoolDataDao = schoolDataDao;
    }

    public List<SchoolDataDTO> getAllSchoolData() {
        try {
            return schoolDataDao.getAllSchoolData();
        } catch (Exception e) {
            
            e.printStackTrace();
            
            return Collections.emptyList();
        }
    }
}
