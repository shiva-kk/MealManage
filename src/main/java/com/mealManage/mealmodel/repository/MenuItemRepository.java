package com.mealManage.mealmodel.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.menu.entities.MenuItem;

import io.swagger.annotations.Api;

/**
 * @author Thulasiram Yachamaneni
 */
@Api(value = "menuItems", description = "These API enables caterer to save menus to catalog")
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
	
    @Query("SELECT menuItems FROM MenuItem menuItems where menuItems.schoolDetails.schoolId = :schoolId ")
    public List<MenuItem> findMenuItemsBySchool(@Param("schoolId")Long schoolId);
    
    @Query(value="SELECT m.* FROM menu_items m INNER JOIN meal_calendar c ON m.id=c.menu_item_id INNER JOIN meal_calendar_summary s "
    		+ "ON c.meal_calendar_summary_id = s.id WHERE m.active = :isActive AND m.mealSchool_schoolId = :schoolId AND s.yearMonth = :yearMonth GROUP BY m.id, m.name", nativeQuery=true)
    public List<MenuItem> findByActiveAndSchoolDetailsSchoolId(@Param("isActive") Boolean isActive, @Param("schoolId") Long schoolId, @Param("yearMonth") String yearMonth);

    public MenuItem findMenuItemsById(@Param("id")Long id);

    @Query("SELECT menuItems FROM MenuItem menuItems where menuItems.active = 1 and menuItems.schoolDetails.schoolId = :schoolId and menuItems.category IN (:types)")
    public List<MenuItem> findActiveMenuItemsBySchoolAndTypes(@Param("schoolId")Long schoolId, @Param("types") List<MealType> types);

    @RestResource(exported = false)
    /**This API disabled for delete the menuItem**/
    public void delete(MenuItem menuItem);
    
    @Query(value="SELECT m.id,m.name,GROUP_CONCAT(p.locationId) FROM menu_items m  inner JOIN  meal_calendar c ON m.id=c.menu_item_id INNER JOIN meal_calendar_summary s "
    		+ "ON c.meal_calendar_summary_id = s.id AND s.yearMonth = :yearMonth Left JOIN menuItem_posLocation p ON m.id=p.menuId WHERE m.mealSchool_schoolId=:mealSchoolId "
    		+ "AND m.category='EXTRA' and m.active = true GROUP BY m.id,m.name", nativeQuery=true)
    public List<Object[]> getItemPOSLocations(@Param("mealSchoolId") Long mealSchoolId, @Param("yearMonth") String yearMonth);
    
    @Query(value="select d.date, e.name, e.ingredients, e.shortDescription, e.longDescription, e.allergens, d.price, e.category, e.id, d.id as calendarId,d.reducedPrice,e.isNutrAvailable,d.meal_calendar_summary_id FROM meal_calendar d "
    		+ "INNER JOIN menu_items e on d.menu_item_id=e.id where d.isActive = 1 and d.meal_calendar_summary_id = :summaryId", nativeQuery=true)
    public List<Object[]> getMenuItemsBySummaryId(@Param("summaryId") Long summaryId);

    @Query("SELECT menuItems FROM MenuItem menuItems where menuItems.active = 1 and menuItems.schoolDetails.schoolId = :schoolId and menuItems.name = :name and menuItems.category =:type")
    public List<MenuItem> getMenuItemsByNameAndSchoolAndType(@Param("name") String name,@Param("schoolId") Long schoolId, @Param("type") MealType type);
    
    @Query(value="Select m.id, m.longDescription, m.name, m.category from menu_items m where m.mealSchool_schoolId=:mealSchoolId and m.category in (:types)", nativeQuery=true)
    public List<Object[]> getAllMasterItems(@Param("mealSchoolId") Long mealSchoolId, @Param("types") List<String> types);
    
    public List<MenuItem> findByIdIn(@Param("ids") Set<Long> ids);

    /*@Query("SELECT menuItems FROM MenuItem menuItems where menuItems.active = 1 and menuItems.schoolDetails.schoolId = :schoolId and menuItems.name = :name and menuItems.longDescription= :mealLongDesc")
    List<MenuItem> getMenuItemsByNameAndSchoolAndLongDescription(@Param("name") String name, @Param("schoolId") Long schoolId, @Param("mealLongDesc") String mealLongDesc);
*/
}
