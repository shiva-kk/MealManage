package com.mealManage.dao;

import com.mealManage.menu.entities.PlaceHolder;
import com.mealManage.model.Placeholder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional
public class PlaceHolderDaoImpl implements PlaceHolderDao {

    private static final Logger log = LoggerFactory.getLogger(PlaceHolderDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PlaceHolder getPlaceHolderById(int id) {
        PlaceHolder placeHolder = entityManager.find(PlaceHolder.class,id);
        log.info("invoking the getPlaceholder method by id {}",id);
        return placeHolder;
    }

    @Override
    public List<PlaceHolder> getAllPlaceHolders() {
        String query ="select * from pm_placeholders";
        TypedQuery<PlaceHolder> query1 = entityManager.createQuery(query, PlaceHolder.class);
        log.info("Invoking the getAllPlaceHolder method");
        return query1.getResultList();
    }

    @Override
    public String saveOrUpdate(Placeholder placeholder) {
        if (placeholder.getId() == 0) {
            entityManager.persist(placeholder);
        } else {
            placeholder = entityManager.merge(placeholder);
        }
        log.info("invoking the saveOrupdate method");
        return "saveOrUpdate placeholders successfully";
    }
}
