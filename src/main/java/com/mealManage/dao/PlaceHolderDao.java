package com.mealManage.dao;

import com.mealManage.menu.entities.PlaceHolder;
import com.mealManage.model.Placeholder;

import java.util.List;

public interface PlaceHolderDao {


    public PlaceHolder getPlaceHolderById(int id);

    public List<PlaceHolder>  getAllPlaceHolders();

    public String saveOrUpdate(Placeholder placeholder);
}
