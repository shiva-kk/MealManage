package com.mealManage.dao;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.MealSummaryUpdateReq;
import com.mealManage.domain.MealsRequest;
import com.mealManage.domain.MenuModificationReq;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.meal.SchoolMeal;
import com.mealManage.mealmodel.meal.SchoolMealSummary;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealschedule.entities.MealCalendarSummary;
import com.mealManage.response.MealCreateJson;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.ServiceResponse;

public interface ManageMenuDao {
	
	public ServiceResponse schoolMealsCreate(MealsRequest mealsRequest, String gradesName);
	
	public MealCreateJson mealExcelSummary(MultipartFile multipartFile, Long mealSchoolId, String yearMonth, 
			String loggedUser, Set<SchoolGrades> grades, List<SchoolHoliday> schoolHolidays, Long mealSummaryId);
	
	public ServiceResponse updateMealSummary(MealSummaryUpdateReq mealSummaryUpdateReq);
	
	public List<String> mealCreatedGrades(Long mealSchoolId, String yearMonth) throws Exception;

	public List<String> retrieveMenuScheduledGrades(Long mealSchoolId, String yearMonth, ItemTypeConstants mealType);

	public List<String> breakfastCreatedGrades(Long mealSchoolId, String yearMonth);
	
	public ServiceResponse restoreCancelledOrder(Long mealSchoolId, List<MealOrderDetails> mealOrderDetails);
	
	public Set<String> getStudentIds(String yearMonth, Long schoolMealId, Long mealSchoolId);
	
	public Set<String> getStudentIdsV2(String yearMonth, Long schoolMealId, Long mealSchoolId);
	
	public ServiceResponse menuModification(List<MealOrderDetails> mealOrderDetails, List<SchoolMeal> schoolMeals, 
			MenuModificationReq menuModificationReq);

	public ServiceResponse menuModificationV2(MealCalendarSummary summary);
	
	public Set<String> getMenuAddGrades(Long schoolMealId);

	public SchoolMealSummary prepareCalendarSummary(MealCalendarSummary summary);
	public Set<String> getMenuAddGradesV2(Long summaryId);

	ServiceResponse schoolMealsCreateV2(ItemTypeConstants menuType, MealsRequest mealsRequest, String finalGradesName) throws Exception;

    public ServiceResponse updateMealSummaryV2(MealSummaryUpdateReq mealSummaryUpdateReq);

    //public List<MenuDetailDTO> getMenuItemsForSummary(Long summaryId );


}
