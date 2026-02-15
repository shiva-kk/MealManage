package com.mealManage.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.BreakfastModificationReq;
import com.mealManage.domain.MealSummaryUpdateReq;
import com.mealManage.domain.MealsRequest;
import com.mealManage.domain.MenuModificationReq;
import com.mealManage.domain.MenuModificationRequest;
import com.mealManage.domain.RestoreCancelledMenuReq;
import com.mealManage.mealmodel.meal.BreakfastMaster;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.menu.entities.NutritionAudit;
import com.mealManage.response.ItemPosLocation;
import com.mealManage.response.ServiceResponse;

public interface ManageMenuService {
	
	public ServiceResponse mealJson(MultipartFile multipartFile, Long mealSchoolId, Set<SchoolGrades> gradeNames, String yearMonth, String loggedUser, Long mealSummaryId);
	
	public ServiceResponse schoolMealsCreate(MealsRequest mealsRequest);

	public ServiceResponse menuJson(MultipartFile file, Long mealSchoolId, Set<SchoolGrades> gradeNames, 
			String yearMonth, Long masterRecId, ItemTypeConstants menuType, Boolean isExtraPreOrder);

	public ServiceResponse updateMealSummary(MealSummaryUpdateReq mealSummaryUpdateReq);

	public ServiceResponse updateMealSummaryV2(MealSummaryUpdateReq mealSummaryUpdateReq);

	public List<String> mealCreatedGrades(Long mealSchoolId, String yearMonth, MealType itemType) throws Exception;
	
	public ServiceResponse refreshMenuPdf(Long summaryId, MealType itemtype);	
	
	public ServiceResponse refreshMenuPdfV2(Long summaryId, MealType itemtype);
	
	public ServiceResponse breakfastJsonBuild(MultipartFile file, Long mealSchoolId, Set<SchoolGrades> gradeNames, 
			String yearMonth, Long masterRecId);
	
	public ServiceResponse breakfastMenuManage(BreakfastMaster breakfastMaster);
	
	public Map<String, Object> breakfastByMasterRecId(Long masterRecId, Long mealSchoolId, String yearMonth, Boolean jsonFormat);
	
	public ServiceResponse restoreCancelledMenu(Long mealSchoolId, RestoreCancelledMenuReq restoreCancelledMenuReq);
	
	public ServiceResponse restoreCancelledMenuV2(Long mealSchoolId, RestoreCancelledMenuReq restoreCancelledMenuReq, ItemTypeConstants menuType);
	
	public ServiceResponse menuItemsModification(Long mealSummaryId, MenuModificationReq menuModificationReq);
	
	public ServiceResponse breakfastModification(Long breakfastMasterId, BreakfastModificationReq breakfastModificationReq);
	
	public ServiceResponse updateSideMenu(Long mealSummaryId, String sideName, Long schoolMealId);
	
	public Map<String, String> latestActiveMonth(Long mealSchoolId, Integer schoolYear);

	public Object getMealSummaryDetails(Long menuSummaryId, Long mealSchoolId, String yearMonth, String grade, ItemTypeConstants itemType, Long studentRecId) throws Exception;

	public ServiceResponse schoolMealsCreateV2(ItemTypeConstants menuType, MealsRequest mealsRequest);
	
	public ServiceResponse allMasterItemsByCategory(Long mealSchoolId, ItemTypeConstants menuType);
	
	public ServiceResponse getItemLocations(Long mealSchoolId);
	
	public ServiceResponse saveItemLocations(Long mealSchoolId, List<ItemPosLocation> locations);

    public List<String> mealCreatedGradesV2(Long mealSchoolId, String yearMonth, ItemTypeConstants itemType) throws Exception;


	public ServiceResponse menuItemsModificationV2(Long mealSummaryId, MenuModificationRequest menuModificationReq);

	Map<String, String> latestActiveMonthV2(Long mealSchoolId, Integer schoolYearVal);
	
	public ServiceResponse nutritionInfo(Long mealSchoolId, List<NutritionAudit> nutritionAudits);

}
