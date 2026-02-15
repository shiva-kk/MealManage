package com.mealManage.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.BreakfastModificationReq;
import com.mealManage.domain.MealSummaryUpdateReq;
import com.mealManage.domain.MealsRequest;
import com.mealManage.domain.MenuModificationReq;
import com.mealManage.domain.MenuModificationRequest;
import com.mealManage.domain.MenuOrderCancellationReq;
import com.mealManage.domain.RestoreCancelledMenuReq;
import com.mealManage.mealmodel.meal.BreakfastMaster;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.menu.entities.NutritionAudit;
import com.mealManage.response.ItemPosLocation;
import com.mealManage.response.ServiceResponse;
import com.mealManage.service.ManageMenuService;
import com.mealManage.service.MealManageAPIService;

/**
 * Manage menu items related APIs 
 */
@RestController
@RequestMapping("mealManage")
public class ManageMenuAPI {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ManageMenuService manageMenuService;
	@Autowired
	private MealManageAPIService mealManageAPIService;
	
	/**This API used for read the uploaded excel file data and return result in Json format. And also upload the file to S3 bucket for history**/
	@PostMapping("mealJson")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> mealJson(@RequestPart(value = "file") MultipartFile multiPartFile, 
			@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId, 
			@RequestParam(value="gradeNames", required = true) Set<SchoolGrades> gradeNames,
			@RequestParam(value="yearMonth", required=true) String yearMonth,
			@RequestParam(value="loggedUser", required=false) String loggedUser,
			@RequestParam(value="mealSummaryId", required=false) Long mealSummaryId){
		ServiceResponse serviceResponse = manageMenuService.mealJson(multiPartFile, mealSchoolId, gradeNames, yearMonth, loggedUser, mealSummaryId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for read the uploaded excel file data and return result in Json format. And also upload the file to S3 bucket for history**/
	@PostMapping("v2/menuJson")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> menuJson(@RequestPart(value = "file") MultipartFile multiPartFile, 
			@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId, 
			@RequestParam(value="gradeNames", required = true) Set<SchoolGrades> gradeNames,
			@RequestParam(value="yearMonth", required=true) String yearMonth,
			@RequestParam(value="mealSummaryId", required=false) Long mealSummaryId, @RequestParam(value="menuType", required=true) ItemTypeConstants menuType, 
			@RequestParam(value="isExtraPreOrder", required=false) Boolean isExtraPreOrder){
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = manageMenuService.menuJson(multiPartFile, mealSchoolId, gradeNames, yearMonth, mealSummaryId, menuType, isExtraPreOrder);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for create/update single or multiple school meals**/
	@PostMapping("schoolMealsCreate")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> schoolMealsCreate(@RequestBody MealsRequest mealsRequest){
		logger.info("Invoking the schoolMealsCreate API");
		ServiceResponse serviceResponse= manageMenuService.schoolMealsCreate(mealsRequest);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for update the meal info (i.e. cut-off date, order extension status, auto reminder date, meal publish status ...etc**/
	@PostMapping("updateMealSummary")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> updateMealSummary(@RequestBody MealSummaryUpdateReq mealSummaryUpdateReq){
		logger.info("Invoking the API for update the meal summary (i.e. cut-off date, order extension status, auto reminder date, meal publish status ...etc");
		ServiceResponse serviceResponse = manageMenuService.updateMealSummary(mealSummaryUpdateReq);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}

	/**This API used for update the meal info (i.e. cut-off date, order extension status, auto reminder date, meal publish status ...etc**/
	@PostMapping("v2/updateMealSummary")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> updateMealSummaryV2(@RequestBody MealSummaryUpdateReq mealSummaryUpdateReq){
		logger.info("Invoking the API for update the meal summary (i.e. cut-off date, order extension status, auto reminder date, meal publish status ...etc");
		ServiceResponse serviceResponse = manageMenuService.updateMealSummaryV2(mealSummaryUpdateReq);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This method used for return the grade for whom MEAL/BREAKFAST created**/
	@GetMapping("mealCreatedGrades")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public List<String> mealCreatedGrades(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId,
			@RequestParam(value="yearMonth", required=true) String yearMonth,
			@RequestParam(value="itemType", required=false) MealType itemType) throws Exception{
		logger.info("Invoking the API for get all the grades for meal has been created by school admin");
		return manageMenuService.mealCreatedGrades(mealSchoolId, yearMonth, itemType);
	}

	/**This method used for return the grade for whom MEAL/BREAKFAST created**/
	@GetMapping("v2/mealCreatedGrades")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public List<String> mealCreatedGradesV2(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId,
										  @RequestParam(value="yearMonth", required=true) String yearMonth,
										  @RequestParam(value="itemType", required=true) ItemTypeConstants itemType) throws Exception{
		logger.info("Invoking the API for get all the grades for meal has been created by school admin");
		return manageMenuService.mealCreatedGradesV2(mealSchoolId, yearMonth, itemType);
	}


	
	/**This API used for refresh the Menu pdf file from backend using summary id**/
	@GetMapping("refreshMenuPdf")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> refreshMenuPdf(@RequestParam(value="summaryId", required=true) Long summaryId,
			@RequestParam(value="itemType", required = false) MealType itemType){
		logger.info("Invoking API for refresh the menu file from backend using summary id: "+summaryId+" and itemType:"+itemType);
		ServiceResponse serviceResponse = manageMenuService.refreshMenuPdf(summaryId, itemType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for refresh the Menu pdf file from backend using meal calendar summary id**/
	@GetMapping("v2/refreshMenuPdf")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> refreshMenuPdfV2(@RequestParam(value="summaryId", required=true) Long summaryId,
			@RequestParam(value="itemType", required = false) MealType itemType){
		logger.info("Invoking API for refresh the menu file from backend using summary id: "+summaryId+" and itemType:"+itemType);
		ServiceResponse serviceResponse = manageMenuService.refreshMenuPdfV2(summaryId, itemType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for read the uploaded excel file data and return result in Json format for the breakfast menu.
	 *  And also upload the file to S3 bucket for history**/
	@PostMapping("breakfastJson")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> breakfastJsonBuild(@RequestPart(value = "file") MultipartFile multiPartFile, 
			@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId, 
			@RequestParam(value="gradeNames", required = true) Set<SchoolGrades> gradeNames,
			@RequestParam(value="yearMonth", required=true) String yearMonth,
			@RequestParam(value="masterRecId", required=false) Long masterRecId){
		ServiceResponse serviceResponse = manageMenuService.breakfastJsonBuild(multiPartFile, mealSchoolId, gradeNames, 
				yearMonth, masterRecId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for create/update the breakfast menu items**/
	@PostMapping("breakfastMenuManage")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> breakfastMenuManage(@RequestBody BreakfastMaster breakfastMaster){
		logger.info("Invoking the API for create the breakfast menu items");
		ServiceResponse serviceResponse= manageMenuService.breakfastMenuManage(breakfastMaster);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get all the breakfast menu in json format like as mealJson API using master record id**/
	@GetMapping("breakfastByMasterRecId")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public Map<String, Object> breakfastByMasterRecId(@RequestParam(value = "masterRecId", required = true) Long masterRecId, 
			@RequestParam(value="mealSchoolId", required=false) Long mealSchoolId, 
			@RequestParam(value="yearMonth", required=false) String yearMonth, @RequestParam(value="jsonFormat", required=false) Boolean jsonFormat) throws Exception{
		logger.info("Invoking the API for get all the breakfast menus by master record id");
		return manageMenuService.breakfastByMasterRecId(masterRecId, mealSchoolId, yearMonth, jsonFormat);
	}

	/**This API created for Cancel the Menu order by student wise or Grade wise for the specific month.**/
	@PostMapping("menuOrderCancel")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> menuOrderCancel(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestBody MenuOrderCancellationReq menuOrderCancellationReq){
		logger.info("API executing for cancel the menu order on the following criteria school id: "+mealSchoolId+", Year Month:"+
			menuOrderCancellationReq.getYearMonth()+", Logged User:"+menuOrderCancellationReq.getLoggedUser()+", Cancellation Note:"+
				menuOrderCancellationReq.getCancellationNote()+", Cancellation Dates:"+menuOrderCancellationReq.getDateList()+
				", Gardes:"+menuOrderCancellationReq.getGradeList()+", StudentRecIds"+menuOrderCancellationReq.getStudentRecordIds()+
				", is Grade wise?"+menuOrderCancellationReq.getIsGradeWise());
		ServiceResponse serviceResponse = mealManageAPIService.menuOrderCancel(mealSchoolId, menuOrderCancellationReq);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API created for Cancel the Menu order by student wise or Grade wise for the specific month.**/
	@PostMapping("v2/menuOrderCancel")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> menuOrderCancelV2(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestBody MenuOrderCancellationReq menuOrderCancellationReq, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType){
		logger.info("API executing for cancel the menu order on the following criteria school id: "+mealSchoolId+", Year Month:"+
			menuOrderCancellationReq.getYearMonth()+", Logged User:"+menuOrderCancellationReq.getLoggedUser()+", Cancellation Note:"+
				menuOrderCancellationReq.getCancellationNote()+", Cancellation Dates:"+menuOrderCancellationReq.getDateList()+
				", Gardes:"+menuOrderCancellationReq.getGradeList()+", StudentRecIds"+menuOrderCancellationReq.getStudentRecordIds()+
				", is Grade wise?"+menuOrderCancellationReq.getIsGradeWise());
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = mealManageAPIService.menuOrderCancelV2(mealSchoolId, menuOrderCancellationReq, menuType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API created for Restore the Cancel Menu order by student wise or Grade wise for the specific date.**/
	@PostMapping("restoreCancelledMenu")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> restoreCancelledMenu(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestBody RestoreCancelledMenuReq restoreCancelledMenuReq){
		logger.info("API executing for restore the cancelled menu order on the following criteria school id: "+mealSchoolId+", Restore Note:"+
				restoreCancelledMenuReq.getRestoreNote()+", Restore Dates:"+restoreCancelledMenuReq.getRestoreDate()+
				", Gardes:"+restoreCancelledMenuReq.getGrade()+", StudentRecIds"+restoreCancelledMenuReq.getStudentRecordIds()+
				", year month: "+restoreCancelledMenuReq.getYearMonth());
		ServiceResponse serviceResponse = manageMenuService.restoreCancelledMenu(mealSchoolId, restoreCancelledMenuReq);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API created for Restore the Cancel Menu order by student wise or Grade wise for the specific date.**/
	@PostMapping("v2/restoreCancelledMenu")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> restoreCancelledMenuV2(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestBody RestoreCancelledMenuReq restoreCancelledMenuReq, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType){
		logger.info("API executing for restore the cancelled menu order on the following criteria school id: "+mealSchoolId+", Restore Note:"+
				restoreCancelledMenuReq.getRestoreNote()+", Restore Dates:"+restoreCancelledMenuReq.getRestoreDate()+
				", Gardes:"+restoreCancelledMenuReq.getGrade()+", StudentRecIds"+restoreCancelledMenuReq.getStudentRecordIds()+
				", year month: "+restoreCancelledMenuReq.getYearMonth());
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = manageMenuService.restoreCancelledMenuV2(mealSchoolId, restoreCancelledMenuReq, menuType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API created for add/remove/edit the Lunch menu item**/
	@PostMapping("menuItemsModification")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> menuItemsModification(@RequestParam(value="mealSummaryId", required=true) Long mealSummaryId, 
			@RequestBody MenuModificationReq menuModificationReq){
		logger.info("Invoking API for add/remove/edit the Lunch menu items based on mealSummaryId: "+mealSummaryId);
		ServiceResponse serviceResponse = manageMenuService.menuItemsModification(mealSummaryId, menuModificationReq);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
		/**This API created for add/remove/edit the Lunch menu item**/
	@PostMapping("v2/menuItemsModification")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> menuItemsModificationV2(@RequestParam(value="mealSummaryId", required=true) Long mealSummaryId, @RequestBody MenuModificationRequest menuModificationRequest){
		logger.info("Invoking API for add/remove/edit the Lunch menu items based on mealSummaryId: "+mealSummaryId);
		ServiceResponse serviceResponse = manageMenuService.menuItemsModificationV2(mealSummaryId, menuModificationRequest);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}

	/**This API used for update the sides**/
	@PutMapping("updateSideMenu")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> updateSideMenu(@RequestParam Long mealSummaryId, 
			@RequestParam String sideName, @RequestParam Long schoolMealId){
		logger.info("Invoking API for edit the side menu");
		ServiceResponse serviceResponse = manageMenuService.updateSideMenu(mealSummaryId, sideName, schoolMealId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API created for add/remove/edit the Side menu item**/
	@PutMapping("breakfastModification")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> breakfastModification(@RequestParam(value="breakfastMasterId", 
		required=true) Long breakfastMasterId,	@RequestBody BreakfastModificationReq breakfastModificationReq){
		logger.info("Invoking API for add/remove/edit the breakfast menu items based on breakfastMasterId: "+breakfastMasterId);
		ServiceResponse serviceResponse = manageMenuService.breakfastModification(breakfastMasterId, breakfastModificationReq);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the latest active menu month**/
	@GetMapping("latestActiveMenuMonth")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public Map<String, String> latestActiveMenuMonth(@RequestParam Long mealSchoolId, @RequestParam Integer schoolYearVal){
		logger.info("Invoking API for get the latest active menu month for school: "+mealSchoolId+" and school year: "+schoolYearVal);
		return manageMenuService.latestActiveMonth(mealSchoolId, schoolYearVal);
	}

	/**This API used for get the latest active menu month**/
	@GetMapping("v2/latestActiveMenuMonth")
	//@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public Map<String, String> latestActiveMenuMonthV2(@RequestParam Long mealSchoolId, @RequestParam Integer schoolYearVal){
		logger.info("Invoking API for get the latest active menu month for school: "+mealSchoolId+" and school year: "+schoolYearVal);
		return manageMenuService.latestActiveMonthV2(mealSchoolId, schoolYearVal);
	}


	@GetMapping("mealSummaryDetails")
	//@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	/**This method used for get the menu item details by summary id. If summary id is 0 and other parameters there then we have get all details for order screen**/
	public Object getMealSummaryDetails(@RequestParam Long menuSummaryId, @RequestParam(value="mealSchoolId", required=false) Long mealSchoolId,
			@RequestParam(value="yearMonth", required=false) String yearMonth, @RequestParam(value="grade", required=false) String grade
	,@RequestParam(value="itemType", required=false) ItemTypeConstants itemType, @RequestParam(value="studentRecId", required=false) Long studentRecId) throws Exception{
		logger.info("Invoking API getMenuItemsForSummary for summaryId: "+menuSummaryId);
		return manageMenuService.getMealSummaryDetails(menuSummaryId, mealSchoolId, yearMonth, grade, itemType, studentRecId);
	}

	@PostMapping("v2/schoolMealsCreate")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> schoolMealsCreateV2(@RequestParam ItemTypeConstants itemType, @RequestBody MealsRequest mealsRequest){
		logger.info("Invoking the schoolMealsCreateV2 API");
		ServiceResponse serviceResponse= manageMenuService.schoolMealsCreateV2(itemType,mealsRequest);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	@GetMapping("allMasterItemsByCategory")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> allMasterItemsByCategory(@RequestParam Long mealSchoolId, @RequestParam ItemTypeConstants menuType){
		logger.info("Invoking API to get all the master items for create Calendar with mealSchoolId::"+mealSchoolId+" and type::"+menuType);
		ServiceResponse serviceResponse = manageMenuService.allMasterItemsByCategory(mealSchoolId, menuType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	@GetMapping("{mealSchoolId}/itemLocations")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> getItemLocations(@PathVariable("mealSchoolId") Long mealSchoolId){
		logger.info("Invoking API for get items mapped POS location");
		ServiceResponse serviceResponse = manageMenuService.getItemLocations(mealSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	@PostMapping("{mealSchoolId}/itemLocations")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> saveItemLocations(@PathVariable("mealSchoolId") Long mealSchoolId,
			@RequestBody List<ItemPosLocation> locations){
		logger.info("Invoking API for save items mapped POS location");
		ServiceResponse serviceResponse = manageMenuService.saveItemLocations(mealSchoolId, locations);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	@PostMapping("{mealSchoolId}/nutritionInfo")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> nutritionInfo(@PathVariable("mealSchoolId") Long mealSchoolId,
			@RequestBody List<NutritionAudit> nutritionAudits){
		logger.info("Invoking API for save the nutrition info");
		ServiceResponse serviceResponse = manageMenuService.nutritionInfo(mealSchoolId, nutritionAudits);				
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	
}
