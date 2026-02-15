package com.mealManage.service;

import com.mealManage.menu.entities.PlaceHolder;
import com.mealManage.model.Placeholder;

import java.util.List;

public interface PlaceHolderService {


    public PlaceHolder getPlaceholder(int id);


    public List<PlaceHolder> getAllPlaceholders();

    public String saveOrUpdatePlaceholder(Placeholder placeholder);

}
