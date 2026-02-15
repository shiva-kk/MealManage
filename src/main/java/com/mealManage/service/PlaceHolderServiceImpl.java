package com.mealManage.service;

import com.mealManage.dao.PlaceHolderDao;
import com.mealManage.exception.DatabaseErrorException;
import com.mealManage.menu.entities.PlaceHolder;
import com.mealManage.model.Placeholder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceHolderServiceImpl implements PlaceHolderService {

    private static final Logger log = LoggerFactory.getLogger(PlaceHolderServiceImpl.class);

    @Autowired
    private PlaceHolderDao placeHolderDao;

    @Override
    public PlaceHolder getPlaceholder(int id) {
        try {
            return placeHolderDao.getPlaceHolderById(id);
        }catch (Exception e){
        log.error("failed to get the placeholder details, id:{}. Exception:", id, e);
        throw new DatabaseErrorException("Failed to retrieve placeholder details");
      }
    }


    @Override
    public List<PlaceHolder> getAllPlaceholders() {
        try {
        return placeHolderDao.getAllPlaceHolders();
        }catch (Exception e){
            log.error("failed to get the allPlaceholders, Exception:", e);
            throw new DatabaseErrorException("Failed to retrieve allplaceholders details");
        }
    }

    @Override
    public String saveOrUpdatePlaceholder(Placeholder placeholder) {
        try{
        return placeHolderDao.saveOrUpdate(placeholder);
    } catch (Exception e){
        log.error("failed to  saveOrUpdate placeholder details, Exception:", e);
        throw new DatabaseErrorException("Failed to saveOrUpdate placeholder details");
    }
    }
}
