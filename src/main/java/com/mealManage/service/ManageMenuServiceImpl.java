package com.mealManage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mealManage.dao.ManageMenuDao;
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.domain.BreakfastModificationReq;
import com.mealManage.domain.MealSummaryUpdateReq;
import com.mealManage.domain.MealsRequest;
import com.mealManage.domain.MenuModificationReq;
import com.mealManage.domain.MenuModificationRequest;
import com.mealManage.domain.MenuUpdateDetails;
import com.mealManage.domain.RestoreCancelledMenuReq;
import com.mealManage.domain.StudentMealOrdersV2;
import com.mealManage.mealmodel.meal.BreakfastItems;
import com.mealManage.mealmodel.meal.BreakfastMaster;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealMenu;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.meal.MealsExcelSummary;
import com.mealManage.mealmodel.meal.MenuItemInfo;
import com.mealManage.mealmodel.meal.MenuOrderHistoryAudit;
import com.mealManage.mealmodel.meal.PosLocation;
import com.mealManage.mealmodel.meal.SchoolMeal;
import com.mealManage.mealmodel.meal.SchoolMealSummary;
import com.mealManage.mealmodel.repository.BreakfastMasterRepository;
import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.MealCalendarSummaryRepository;
import com.mealManage.mealmodel.repository.MealOrderDetailsRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.MealsExcelSummaryRepository;
import com.mealManage.mealmodel.repository.MenuItemRepository;
import com.mealManage.mealmodel.repository.MenuOrderHistoryAuditRepository;
import com.mealManage.mealmodel.repository.NutritionAuditRepo;
import com.mealManage.mealmodel.repository.PosLocationRepo;
import com.mealManage.mealmodel.repository.SchoolMealRepository;
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.CountryDetail;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.school.SchoolType;
import com.mealManage.mealmodel.school.SchoolYear;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.mealschedule.entities.MealCalendar;
import com.mealManage.mealschedule.entities.MealCalendarSummary;
import com.mealManage.mealschedule.model.MenuDetailDTO;
import com.mealManage.mealschedule.model.MenuSummaryDetailDTO;
import com.mealManage.menu.entities.MenuItem;
import com.mealManage.menu.entities.NutritionAudit;
import com.mealManage.response.ItemPosLocation;
import com.mealManage.response.MealCreateJson;
import com.mealManage.response.MealItems;
import com.mealManage.response.MealJsonData;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.ServiceResponse;
import com.mealManage.util.AWSUtility;
import com.mealManage.util.BreakfastMenuPdfUtility;
import com.mealManage.util.BreakfastMenuPdfUtilityV2;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.DateUtilityV2;
import com.mealManage.util.ExcelReadUtil;
import com.mealManage.util.GradeFormatBuild;
import com.mealManage.util.MealMenuPdfUtility;
import com.mealManage.util.OrderedMenuPdfUtility;

/**This class implement by ManageMenuService interface for menu related API's services**/
@Service
public class ManageMenuServiceImpl implements ManageMenuService {
	
	@Autowired
	private SchoolMealRepository schoolMealRepository;
	@Autowired
	private MealMenuPdfUtility mealMenuPdfUtility;
	@Autowired
	private ManageMenuDao manageMenuDao;
	@Autowired
	private DateUtilityV2 du;
	@Autowired
	private MealManageAPIService mealManageAPIService;
	@Autowired
	private BreakfastMasterRepository breakfastMasterRepository;
	@Autowired
	private MealCalendarSummaryRepository summaryRepo;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private MealsExcelSummaryRepository mealsExcelSummaryRepository;
	@Autowired
	private ExcelReadUtil excelReadUtil;
	@Autowired
	private AWSUtility awsUtility;
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	@Autowired
	private BreakfastMenuPdfUtility breakfastMenuPdfUtility;
	@Autowired
	private MealOrderDetailsRepository mealOrderDetailsRepository;
	@Autowired
	private MenuOrderHistoryAuditRepository menuOrderHistoryAuditRepository;
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	@Autowired
	private OrderedMenuPdfUtility orderedMenuPdfUtility;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private MealCalendarSummaryRepository mealCalendarSummaryRepository;
	@Autowired
	private PosLocationRepo posLocationRepo;
	@Autowired
	private NutritionAuditRepo nutritionAuditRepo;
	/*@Autowired
	private MealCalendarRepository mealCalendarRepository;*/
	@Autowired
	private BreakfastMenuPdfUtilityV2 breakfastMenuPdfUtilityV2;
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	/**This method used for read the uploaded excel file data and return the result into Json format**/
	@Override
	public ServiceResponse mealJson(MultipartFile multipartFile, Long mealSchoolId, Set<SchoolGrades> gradeNames, String yearMonth, String loggedUser, Long mealSummaryId){
		logger.info("Reading file and building data into Json format");
		ServiceResponse serviceResponse = new ServiceResponse();
		MealCreateJson mealCreateJson = null;
		String mealJsonData = null;
		try{
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			//String finalGradesName = getGradesFromSet(gradeNames);	
			Date monthStartDate = new SimpleDateFormat("yyyyMMdd").parse(yearMonth+""+01);
			List<SchoolHoliday> schoolHolidays = mealManageAPIService.schoolHolidays(mealSchoolId, monthStartDate, 
					lastDayOfMonth(monthStartDate));
			mealCreateJson = manageMenuDao.mealExcelSummary(multipartFile, mealSchoolId, yearMonth, loggedUser, gradeNames, schoolHolidays, mealSummaryId);
			mealJsonData = objectMapper.writeValueAsString(mealCreateJson.getMealItems());
			serviceResponse.setMealJsonData(mealJsonData.replace("\"new Date(", "new Date(").replace("')'\"", ")"));
			serviceResponse.setStatus(mealCreateJson.getStatus());
			serviceResponse.setStatusCode(mealCreateJson.getStatusCode());
			serviceResponse.setErrorMessage(mealCreateJson.getErrorMessage());
			serviceResponse.setStatusMessage(mealCreateJson.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to import the meal file due to "+e.getMessage());
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to import the meal menu file. Please check file and try again later!");
		}
		return serviceResponse;
	}

	/**This method used for create/update the single or multiple school meals**/
	@Override
	public ServiceResponse schoolMealsCreate(MealsRequest mealsRequest) {
		GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
		String finalGradesName = gradeFormatBuild.getGradesFromSet(mealsRequest.getGradesList());	
		return manageMenuDao.schoolMealsCreate(mealsRequest, finalGradesName);
	}

	/**This method used for update the meal summary i.e. cut-off date, order extension status, auto reminder date, meal publish status ....etc**/
	@Override
	public ServiceResponse updateMealSummary(MealSummaryUpdateReq mealSummaryUpdateReq) {	
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse = manageMenuDao.updateMealSummary(mealSummaryUpdateReq);
		}catch(Exception e){
			logger.error("Error occurred during execution of the updateMealSummary API for update the meal summary. "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update the Menu summary.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

    /**This method used for update the meal summary i.e. cut-off date, order extension status, auto reminder date, meal publish status ....etc**/
    @Override
    public ServiceResponse updateMealSummaryV2(MealSummaryUpdateReq mealSummaryUpdateReq) {
        ServiceResponse serviceResponse = new ServiceResponse();
        try{
            serviceResponse = manageMenuDao.updateMealSummaryV2(mealSummaryUpdateReq);
        }catch(Exception e){
            logger.error("Error occurred during execution of the updateMealSummary API for update the meal summary with id::"+mealSummaryUpdateReq.getMealSummaryId()+" due to "+e.getMessage());
            serviceResponse.setStatus("Failed");
            serviceResponse.setStatusCode(500);
            serviceResponse.setStatusMessage("Failed to update the requested info.");
            serviceResponse.setErrorMessage(e.getMessage());
        }
        return serviceResponse;
    }




    /**This method used for get all the grades for whom meal has been created
	 * @throws Exception **/
	@Override
	public List<String> mealCreatedGrades(Long mealSchoolId, String yearMonth, MealType itemType) throws Exception {
		List<String> grades = new ArrayList<String>();
		try{
			if(itemType != null && itemType.toString().equalsIgnoreCase(MealType.BREAKFAST.toString()))
				grades = manageMenuDao.breakfastCreatedGrades(mealSchoolId, yearMonth);
			else
				grades = manageMenuDao.mealCreatedGrades(mealSchoolId, yearMonth);
		}catch(Exception e){
			logger.error("Failed to get the created meal grades.");
			throw new Exception("Failed to get the created meal grades.");
		}
		return grades;
	}
	
	@Override
	public List<String> mealCreatedGradesV2(Long mealSchoolId, String yearMonth, ItemTypeConstants itemType) throws Exception {
		List<String> grades = new ArrayList<String>();
		try{
			grades = manageMenuDao.retrieveMenuScheduledGrades(mealSchoolId, yearMonth,itemType);
		}catch(Exception e){
			logger.error("Failed to get the created "+itemType+" grades for mealSchoolId::"+mealSchoolId+" and yearMonth::"+yearMonth+" due to "+e.getMessage());
			throw new Exception("Failed to get the created menu grades.");
		}
		return grades;
	}

	/**This method used for refresh the menu pdf file from backend**/
	@Override
	public ServiceResponse refreshMenuPdf(Long summaryId, MealType itemType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			if(itemType != null && itemType.toString().equalsIgnoreCase("Breakfast")){
				BreakfastMaster breakfastMaster = breakfastMasterRepository.findByRecId(summaryId);
				if(breakfastMaster !=  null){
					MealSchool mealSchool = mealSchoolRepository.findBySchoolId(breakfastMaster.getMealSchoolId());
					Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
					String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(breakfastMaster.getMealSchool().getSchoolId()));
					breakfastMenuPdfUtility.breakfastMenuPdf(breakfastMaster,currencySymbol, isItemized);
					serviceResponse.setStatus("Success");
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatusMessage("Breakfast Menu PDF updated successfully.");
				}else{
					serviceResponse.setStatusMessage("There are no entry with this id: "+summaryId+".");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatus("Failed");
				}
			}else{
				Set<SchoolMealSummary> schoolMealSummaries = schoolMealRepository.schoolMealSummaryById(summaryId);
				if(schoolMealSummaries !=  null && schoolMealSummaries.size() > 0){
					SchoolMealSummary schoolMealSummary = new ArrayList<>(schoolMealSummaries).get(0);
					MealSchool mealSchool = mealSchoolRepository.findBySchoolId(schoolMealSummary.getMealSchool().getSchoolId());
					Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
					String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(schoolMealSummary.getMealSchool().getSchoolId()));
					List<MealMenu> mealMenus = schoolMealRepository.getAllMealMenuDetails(schoolMealSummary.getSchoolId());
					mealMenuPdfUtility.mealMenuPdf(mealMenus, schoolMealSummary,currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
					serviceResponse.setStatus("Success");
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatusMessage("Lunch Menu PDF updated successfully.");
				}else{
					serviceResponse.setStatusMessage("There are no entry with this id: "+summaryId+".");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatus("Failed");
				}
			}
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to refresh the menu pdf file from backend due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to refresh the menu pdf file.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for refresh the menu pdf file from backend**/
	@Override
	public ServiceResponse refreshMenuPdfV2(Long summaryId, MealType itemType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			if(itemType != null && itemType.toString().equalsIgnoreCase("Breakfast")){
				MealCalendarSummary summary = summaryRepo.findMealCalendarSummariesById(summaryId);
				if(summary !=  null){
					MealSchool mealSchool = mealSchoolRepository.findBySchoolId(summary.getSchool().getSchoolId());
					Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
					String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(summary.getSchool().getSchoolId()));
					breakfastMenuPdfUtilityV2.breakfastMenuPdf(summary,currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
					serviceResponse.setStatus("Success");
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatusMessage("Breakfast Menu PDF updated successfully.");
				}else{
					serviceResponse.setStatusMessage("There are no entry with this id: "+summaryId+".");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatus("Failed");
				}
			}else{
				MealCalendarSummary summary = summaryRepo.findMealCalendarSummariesById(summaryId);
				if(summary !=  null){
					MealSchool mealSchool = mealSchoolRepository.findBySchoolId(summary.getSchool().getSchoolId());
					Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
					String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(summary.getSchool().getSchoolId()));
					List<MenuDetailDTO> menuLis = ((MenuSummaryDetailDTO)getMealSummaryDetails(summary.getId(), null, null, null, null, null)).getMenuItemsList();
					SchoolMealSummary schoolMealSummary = manageMenuDao.prepareCalendarSummary(summary);
					List<MealMenu> mealMenus = prepareMenu(menuLis);
					mealMenuPdfUtility.mealMenuPdf(mealMenus, schoolMealSummary,currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
					serviceResponse.setStatus("Success");
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatusMessage("Lunch Menu PDF updated successfully.");
				}else{
					serviceResponse.setStatusMessage("There are no entry with this id: "+summaryId+".");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatus("Failed");
				}
			}
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to refresh the menu pdf file from backend due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to refresh the menu pdf file.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for build the breakfast menu items in json format from file**/
	@Override
	@Transactional
	public ServiceResponse breakfastJsonBuild(MultipartFile file, Long mealSchoolId, Set<SchoolGrades> gradeNames,
			String yearMonth, Long masterRecId) {
		logger.info("Reading file and building data into Json format for breakfast items");
		ServiceResponse serviceResponse = new ServiceResponse();
		MealCreateJson mealCreateJson = null;
		String mealJsonData = null;
		try{
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			//String finalGradesName = getGradesFromSet(gradeNames);	
			Date monthStartDate = new SimpleDateFormat("yyyyMMdd").parse(yearMonth+""+01);
			List<SchoolHoliday> schoolHolidays = mealManageAPIService.schoolHolidays(mealSchoolId, monthStartDate, 
					lastDayOfMonth(monthStartDate));
			mealCreateJson = breakfastItemsJsonAndFile(file, mealSchoolId, yearMonth, gradeNames, schoolHolidays, masterRecId);
			mealJsonData = objectMapper.writeValueAsString(mealCreateJson.getMealItems());
			serviceResponse.setMealJsonData(mealJsonData.replace("\"new Date(", "new Date(").replace("')'\"", ")"));
			serviceResponse.setStatus(mealCreateJson.getStatus());
			serviceResponse.setStatusCode(mealCreateJson.getStatusCode());
			serviceResponse.setErrorMessage(mealCreateJson.getErrorMessage());
			serviceResponse.setStatusMessage(mealCreateJson.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to import the breakfast menu file due to "+e.getMessage());
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to import the breakfast menu file. Please check file and try again later!");
		}
		return serviceResponse;
	}
	
	/**This method used for create the breakfast menu items**/
	@Override
	@Transactional
	public ServiceResponse breakfastMenuManage(BreakfastMaster breakfastMaster) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse = breakfastMenuManageBuild(breakfastMaster);			
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to create the breakfast menu items.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get the breakfast menu details by master record id**/
	@Override
	public Map<String, Object> breakfastByMasterRecId(Long masterRecId, Long mealSchoolId, String yearMonth, Boolean jsonFormat) {
		Map<String, Object> breakfastDetailsMap = new HashMap<String, Object>();
		String mealJson = "";
		try{
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	    	BreakfastMaster breakfastMaster = new BreakfastMaster();
	    	if(masterRecId != null && masterRecId != 0)
	    		breakfastMaster = breakfastMasterRepository.findByRecId(masterRecId);
	    	else{
	    		List<BreakfastMaster> breakfastMastersList = new ArrayList<>(breakfastMasterRepository
	    				.findByMealSchoolSchoolIdAndYearMonth(mealSchoolId, yearMonth));
	    		if(breakfastMastersList.size() > 0)
	    			breakfastMaster = breakfastMastersList.get(0);
	    	}
	    	MealItems mealItems = buildBreakfastRequiredFormat(breakfastMaster);
	    	mealJson = objectMapper.writeValueAsString(mealItems);  	
	    	mealJson = mealJson.replace("\"new Date(", "new Date(").replace("')'\"", ")");
	    	if(jsonFormat == null || !jsonFormat)
	    		breakfastDetailsMap.put("jsonData", mealJson);
	    	else{
				breakfastDetailsMap.put("jsonData", breakfastMaster.getBreakfastItems());
	    	}
	    	breakfastDetailsMap.put("pdfUrl", breakfastMaster.getItemsPdfLink());
	    	breakfastDetailsMap.put("recId", breakfastMaster.getRecId().toString());
			logger.info("Get the breakfast menu details API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occurred during execution of the breakfastByMasterRecId API. "+e.getMessage());
		}
		return breakfastDetailsMap;
	}

	/**This method used for restore the cancelled ordered menu item**/
	@Override
	public ServiceResponse restoreCancelledMenu(Long mealSchoolId, RestoreCancelledMenuReq restoreCancelledMenuReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		Set<MealOrderDetails> mealOrderDetailsList = new HashSet<MealOrderDetails>();
		Set<MenuOrderHistoryAudit> menuOrderHstryList = new HashSet<MenuOrderHistoryAudit>();
		List<MealOrderDetails> mealOrderDetailsFinalList = null;
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			/**Check the date for whom menu need to cancel, that date should be greater than current date**/
			String currentDateVal = du.formatDateToString(new Date(), "yyyy-MM-dd", 
					mealSchool.getSchoolTimezone().toString());
			if(restoreCancelledMenuReq.getRestoreDate() != null){
				if (sdf.parse(currentDateVal).compareTo(sdf.parse(restoreCancelledMenuReq.getRestoreDate())) >= 0) {
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatusMessage("Order can not restore for previous & current date: "
							+ restoreCancelledMenuReq.getRestoreDate() + ". Please select valid date.");
					logger.info("Order can not restore for previous & current date: "
							+ restoreCancelledMenuReq.getRestoreDate() + ". Please select valid date.");
					return serviceResponse;
				}
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatusMessage("Please select valid date for which order need to be restore!");
				logger.info("Please select valid date for which order need to be restore!");
				return serviceResponse;
			}
			ItemTypeConstants menuType = ItemTypeConstants.Lunch;
			if(restoreCancelledMenuReq.getGrade() != null){
					mealOrderDetailsList = mealOrderDetailsRepository.ordersByGradesAndMonth(mealSchoolId, 
							Arrays.asList(restoreCancelledMenuReq.getGrade()), restoreCancelledMenuReq.getYearMonth(), menuType);
					menuOrderHstryList = menuOrderHistoryAuditRepository.ordersByGradesAndMonth(mealSchoolId, 
							Arrays.asList(restoreCancelledMenuReq.getGrade()), restoreCancelledMenuReq.getYearMonth(), menuType);
			}else {
				if(restoreCancelledMenuReq.getStudentRecordIds() != null && restoreCancelledMenuReq.getStudentRecordIds().size() > 0){
					mealOrderDetailsList = mealOrderDetailsRepository.ordersByStudentsAndMonth(mealSchoolId, 
							restoreCancelledMenuReq.getStudentRecordIds(), restoreCancelledMenuReq.getYearMonth(), menuType);
					menuOrderHstryList = menuOrderHistoryAuditRepository.ordersByStudentsAndMonth(mealSchoolId, 
							restoreCancelledMenuReq.getStudentRecordIds(), restoreCancelledMenuReq.getYearMonth(), menuType);
				}else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatusMessage("Please select atleast one Student for whom order need to be restore!");
					logger.info("Please select atleast one Student for whom order need to be restore!");
					return serviceResponse;
				}
			}
			if(SecurityContextHolder.getContext().getAuthentication() != null)
				restoreCancelledMenuReq.setLoggedUser(SecurityContextHolder.getContext().getAuthentication().getName());
			/**Get the final meal order details data**/
			mealOrderDetailsFinalList = buildRestoreMenuOrderUpdateData(mealOrderDetailsList, restoreCancelledMenuReq, 
					menuOrderHstryList);
			try{
				if(mealOrderDetailsFinalList != null && mealOrderDetailsFinalList.size() > 0)
					serviceResponse = manageMenuDao.restoreCancelledOrder(mealSchoolId, mealOrderDetailsFinalList);
				else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("There are no orders to be restore for the selected date.");
					logger.info("There are no orders to be restore for the selected date.");
					return serviceResponse;
				}
			}catch(Exception e){
				logger.info("Failed to restore the menu order for the specific dates "+restoreCancelledMenuReq.getRestoreDate()
					+" due to "+e.getMessage());
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Failed to restore the cancelled menu items and transaction has been rolled back!");
				serviceResponse.setErrorMessage(e.getMessage());
				return serviceResponse;
			}
			if(serviceResponse.getStatusCode() == 200){
				logger.info("Cancelled menu items has been restored successfully.");
				buildPdfAndSendToParent(mealSchool, new HashSet<MealOrderDetails>(mealOrderDetailsFinalList), restoreCancelledMenuReq,currencySymbol, isItemized, "MM/dd/yyyy");
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusMessage("Cancelled menu items has been restored successfully.");
			}else{
				logger.info("Failed to restore the cancelled menu items");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Failed to restore the cancelled menu items. Transaction has been rolled back!");
			}
		}catch(Exception e){
			logger.error("Failed to restore the cancelled menu items for the meal school id :"+mealSchoolId+" grades:"+
					restoreCancelledMenuReq.getGrade()+" student record ids: "+restoreCancelledMenuReq.getStudentRecordIds()+
					" month: "+restoreCancelledMenuReq.getYearMonth()+" due to error : "+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to restore the cancelled menu items. Please try again later!");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for restore the cancelled ordered menu item**/
	@Override
	public ServiceResponse restoreCancelledMenuV2(Long mealSchoolId, RestoreCancelledMenuReq restoreCancelledMenuReq, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		Set<MealOrderDetails> mealOrderDetailsList = new HashSet<MealOrderDetails>();
		Set<MenuOrderHistoryAudit> menuOrderHstryList = new HashSet<MenuOrderHistoryAudit>();
		List<MealOrderDetails> mealOrderDetailsFinalList = null;
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
			//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			/**Check the date for whom menu need to cancel, that date should be greater than current date**/
			String currentDateVal = du.formatDateToString(new Date(), "yyyy-MM-dd", 
					mealSchool.getSchoolTimezone().toString());
			if(restoreCancelledMenuReq.getRestoreDate() != null){
				if (sdf.parse(currentDateVal).compareTo(sdf.parse(restoreCancelledMenuReq.getRestoreDate())) >= 0) {
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatusMessage("Order can not restore for previous & current date: "
							+ restoreCancelledMenuReq.getRestoreDate() + ". Please select valid date.");
					logger.info("Order can not restore for previous & current date: "
							+ restoreCancelledMenuReq.getRestoreDate() + ". Please select valid date.");
					return serviceResponse;
				}
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatusMessage("Please select valid date for which order need to be restore!");
				logger.info("Please select valid date for which order need to be restore!");
				return serviceResponse;
			}
			
			if(restoreCancelledMenuReq.getGrade() != null){
					mealOrderDetailsList = mealOrderDetailsRepository.ordersByGradesAndMonth(mealSchoolId, 
							Arrays.asList(restoreCancelledMenuReq.getGrade()), restoreCancelledMenuReq.getYearMonth(), menuType);
					menuOrderHstryList = menuOrderHistoryAuditRepository.ordersByGradesAndMonth(mealSchoolId, 
							Arrays.asList(restoreCancelledMenuReq.getGrade()), restoreCancelledMenuReq.getYearMonth(), menuType);
			}else {
				if(restoreCancelledMenuReq.getStudentRecordIds() != null && restoreCancelledMenuReq.getStudentRecordIds().size() > 0){
					mealOrderDetailsList = mealOrderDetailsRepository.ordersByStudentsAndMonth(mealSchoolId, 
							restoreCancelledMenuReq.getStudentRecordIds(), restoreCancelledMenuReq.getYearMonth(), menuType);
					menuOrderHstryList = menuOrderHistoryAuditRepository.ordersByStudentsAndMonth(mealSchoolId, 
							restoreCancelledMenuReq.getStudentRecordIds(), restoreCancelledMenuReq.getYearMonth(), menuType);
				}else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatusMessage("Please select atleast one Student for whom order need to be restore!");
					logger.info("Please select atleast one Student for whom order need to be restore!");
					return serviceResponse;
				}
			}
			if(SecurityContextHolder.getContext().getAuthentication() != null)
				restoreCancelledMenuReq.setLoggedUser(SecurityContextHolder.getContext().getAuthentication().getName());
			String itemType = CommonUtil.getItemType(menuType);
			/**Get the final meal order details data**/
			mealOrderDetailsFinalList = buildRestoreMenuOrderUpdateDataV2(mealOrderDetailsList, restoreCancelledMenuReq, 
					menuOrderHstryList, mealSchool, itemType, isItemized);
			try{
				if(mealOrderDetailsFinalList != null && mealOrderDetailsFinalList.size() > 0)
					serviceResponse = manageMenuDao.restoreCancelledOrder(mealSchoolId, mealOrderDetailsFinalList);
				else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("There are no orders to be restore for the selected date.");
					logger.info("There are no orders to be restore for the selected date.");
					return serviceResponse;
				}
			}catch(Exception e){
				logger.info("Failed to restore the menu order for the specific dates "+restoreCancelledMenuReq.getRestoreDate()
					+" due to "+e.getMessage());
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Failed to restore the cancelled menu items and transaction has been rolled back!");
				serviceResponse.setErrorMessage(e.getMessage());
				return serviceResponse;
			}
			if(serviceResponse.getStatusCode() == 200){
				logger.info("Cancelled menu items has been restored successfully.");
				buildPdfAndSendToParent(mealSchool, new HashSet<MealOrderDetails>(mealOrderDetailsFinalList), restoreCancelledMenuReq,countryDetail.getCurrencySymbol(), isItemized, countryDetail.getDateFormat());
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusMessage("Cancelled menu items has been restored successfully.");
			}else{
				logger.info("Failed to restore the cancelled menu items");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Failed to restore the cancelled menu items. Transaction has been rolled back!");
			}
		}catch(Exception e){
			logger.error("Failed to restore the cancelled menu items for the meal school id :"+mealSchoolId+" grades:"+
					restoreCancelledMenuReq.getGrade()+" student record ids: "+restoreCancelledMenuReq.getStudentRecordIds()+
					" month: "+restoreCancelledMenuReq.getYearMonth()+" due to error : "+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to restore the cancelled menu items. Please try again later!");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for add/remove/edit the menu item**/
	@Override
	public ServiceResponse menuItemsModification(Long mealSummaryId, MenuModificationReq menuModificationReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			ItemTypeConstants menuType = ItemTypeConstants.Lunch;
			List<SchoolMeal> schoolMeallist = new ArrayList<SchoolMeal>(schoolMealRepository.findBySchoolMealSummarySchoolIdAndIsDelete(
					mealSummaryId, false));
			SchoolMealSummary schoolMealSummary = null;
			if(schoolMeallist == null || schoolMeallist.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("There are no menu available for the mealSummaryId: "+mealSummaryId+".");
				return serviceResponse;
			}
			String loggedUser = "";
			if(SecurityContextHolder.getContext() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			schoolMealSummary = schoolMeallist.get(0).getSchoolMealSummary();
			String yearMonth = schoolMealSummary.getYearMonth();
			List<MealOrderDetails> mealOrderDetailsFinalList = null;
			List<MealOrderDetails> mealOrderUpdateList = null;
			MealSchool mealSchool = schoolMealSummary.getMealSchool();
			Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
			List<SchoolMeal> schoolMealFinal = new ArrayList<SchoolMeal>();
			Long updatedMenuId = (long) 0;
			//Check the request that any items need to be remove
			if(menuModificationReq.getDeleteSchoolMealId() != null && menuModificationReq.getDeleteSchoolMealId() != 0 && 
					menuModificationReq.getCancellationDate() != null){
				Set<String> studentIds = manageMenuDao.getStudentIds(yearMonth, 
						menuModificationReq.getDeleteSchoolMealId(), schoolMealSummary.getMealSchool().getSchoolId());
				Set<MealOrderDetails> mealOrderDetailsRes = mealOrderDetailsRepository
						.findByStudentUserMealSchoolSchoolIdAndStudentUserStudentIdInAndYearMonthAndMenuType(
						schoolMealSummary.getMealSchool().getSchoolId(), studentIds, yearMonth, menuType);
				mealOrderDetailsFinalList = buildCancelMenuOrderUpdateData(mealOrderDetailsRes, menuModificationReq, loggedUser);
				/*schoolMeallist = schoolMeallist.stream().filter(p ->  (p.getMealMenu().getStart() == null 
						|| !menuModificationReq.getDeleteSchoolMealId().equals(p.getSchoolId()))).collect(
								Collectors.toCollection(ArrayList::new));*/
				List<SchoolMeal> schoolMealsForCanDt = schoolMeallist.stream().filter(p ->  p.getMealMenu().getStart() != null 
						&& (menuModificationReq.getCancellationDate().equals(sdf1.format(p.getMealMenu().getStart())))).collect(
										Collectors.toCollection(ArrayList::new));
				if(schoolMealsForCanDt !=  null && schoolMealsForCanDt.size() <= 2){
					for(SchoolMeal schoolMeal : schoolMealsForCanDt){
						schoolMeallist.remove(schoolMeal);
						schoolMeal.setIsDelete(true);
						schoolMealFinal.add(schoolMeal);
					}
					Set<SchoolGrades> schoolGradesAll = buildGradesFinalVal(schoolMeallist.get(0).getSchoolId());
					SchoolMeal schoolMeal = new SchoolMeal();
					schoolMeal.setLoggedUser(loggedUser);
					MealMenu mealMenu = new MealMenu();
					mealMenu.setCreatedBy(schoolMeal.getLoggedUser());
					mealMenu.setCreatedOn(new Date());
					mealMenu.setStart(mealMenu.getStart());
					mealMenu.setType(MealType.HOLIDAY);
					mealMenu.setTitle("No Lunch due to "+menuModificationReq.getMenuDeletionReason());
					mealMenu.setStart(sdf1.parse(menuModificationReq.getCancellationDate()));
					schoolMeal.setMealMenu(mealMenu);
					schoolMeal.setMealSchool(mealSchool);
					schoolMeal.setGrades(schoolGradesAll);
					schoolMeal.setYearMonth(yearMonth);
					schoolMeal.setSchoolMealSummary(schoolMealSummary);
					schoolMealFinal.add(schoolMeal);
					schoolMeallist.add(schoolMeal);
				}else{
					SchoolMeal schoolMealRemove = schoolMeallist.stream().filter(sm -> menuModificationReq.getDeleteSchoolMealId()
							.equals(sm.getSchoolId())).findAny().orElse(null);
					schoolMeallist.remove(schoolMealRemove);
					schoolMealRemove.setIsDelete(true);
					schoolMealFinal.add(schoolMealRemove);
				}
			}
			if(menuModificationReq.getMenuEdit() != null && menuModificationReq.getMenuEdit().size() > 0){
				SchoolMeal schoolMeal = null;
				for(MenuUpdateDetails menuUpdateDetails : menuModificationReq.getMenuEdit()){
					schoolMeal = schoolMeallist.stream().filter(sm -> menuUpdateDetails.getSchoolMealId()
							.equals(sm.getSchoolId())).findAny().orElse(null);
					schoolMeallist.remove(schoolMeal);
					MealMenu mm = schoolMeal.getMealMenu();
					if(menuUpdateDetails.getMealName() != null && !mm.getTitle().equalsIgnoreCase(menuUpdateDetails.getMealName()))
						updatedMenuId = menuUpdateDetails.getSchoolMealId();
					
					if(menuUpdateDetails.getMealName() != null)
						mm.setTitle(menuUpdateDetails.getMealName());
					if(menuUpdateDetails.getMealLongDesc() != null)
						mm.setDesc(menuUpdateDetails.getMealLongDesc());
					mm.setModifiedOn(new Date());
					mm.setModifiedBy(loggedUser);
					schoolMeal.setMealMenu(mm);
					schoolMealFinal.add(schoolMeal);	
					schoolMeallist.add(schoolMeal);
					
				}
			}
			if(menuModificationReq.getAddMenus() != null && menuModificationReq.getAddMenus().size() > 0){
				SchoolMeal schoolMeal = null;
				Set<SchoolGrades> schoolGradesAll = buildGradesFinalVal(schoolMeallist.get(0).getSchoolId());
				for(MealMenu mealMenu : menuModificationReq.getAddMenus()){
					schoolMeal = new SchoolMeal();
					schoolMeal.setLoggedUser(loggedUser);
					mealMenu.setCreatedBy(schoolMeal.getLoggedUser());
					mealMenu.setCreatedOn(new Date());
					mealMenu.setStart(mealMenu.getStart());
					schoolMeal.setMealMenu(mealMenu);
					schoolMeal.setMealSchool(mealSchool);
					schoolMeal.setGrades(schoolGradesAll);
					schoolMeal.setYearMonth(yearMonth);	
					schoolMeal.setSchoolMealSummary(schoolMealSummary);
					schoolMealFinal.add(schoolMeal);
					schoolMeallist.add(schoolMeal);
				}
			}
				serviceResponse = manageMenuDao.menuModification(mealOrderDetailsFinalList, schoolMealFinal, menuModificationReq);
				if(serviceResponse.getStatusCode() == 200){
					logger.info("Menu modification completed successfully.");
					//buildPdfAndSendToParentForMenuUpdate(mealSchool, new HashSet<MealOrderDetails>(mealOrderDetailsFinalList), loggedUser);
					String schoolName = mealSchool.getSchoolName();
					String logoLink = mealSchool.getLogoLink();
					String schoolTimezone = mealSchool.getSchoolTimezone().toString();
					Boolean priEmailIsSubscribe = null;
					Boolean altEmailIsSubscribe = null;
					if(updatedMenuId != null && updatedMenuId > 0){
						Set<String> studentIds = manageMenuDao.getStudentIds(yearMonth, updatedMenuId, schoolMealSummary.getMealSchool().getSchoolId());
						mealOrderUpdateList = new ArrayList<>(mealOrderDetailsRepository
								.findByStudentUserMealSchoolSchoolIdAndStudentUserStudentIdInAndYearMonthAndMenuType(
								schoolMealSummary.getMealSchool().getSchoolId(), studentIds, yearMonth, menuType));
					}
					if(mealOrderDetailsFinalList == null)
						mealOrderDetailsFinalList = new ArrayList<MealOrderDetails>();
					if(mealOrderUpdateList != null && mealOrderUpdateList.size() > 0 && mealOrderDetailsFinalList != null)
						mealOrderDetailsFinalList.addAll(mealOrderUpdateList);					
					if(mealOrderDetailsFinalList != null && mealOrderDetailsFinalList.size() > 0){
						for(MealOrderDetails mealOrderDetails : new HashSet<MealOrderDetails>(mealOrderDetailsFinalList)){
							priEmailIsSubscribe = null;
							altEmailIsSubscribe = null;
							ParentUser parentUser = mealOrderDetails.getStudentUser().getParentuser();
							if(parentUser.getUserName() != null){
								//usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getUserName());
								priEmailIsSubscribe = true; //usersAuthInfo.getEmailIsSubscribe();
							}
							if(parentUser.getParentAltEmail() != null && !parentUser.getParentAltEmail().trim().equalsIgnoreCase("")){
								//usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getParentAltEmail());
								altEmailIsSubscribe = true;//usersAuthInfo.getEmailIsSubscribe();
							}
							orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, schoolName, loggedUser, logoLink, 
												parentUser, priEmailIsSubscribe, altEmailIsSubscribe, schoolTimezone,currencySymbol, mealSchool.getContactPEmail(), null,
												isItemized, "MM/dd/yyyy",CommonUtil.getNonSchoolDays(mealSchool),false);
						}
					}
					//refreshMenuPdf(mealSummaryId, MealType.MEAL);
					//List<MealMenu> mealMenus = schoolMealRepository.getAllMealMenuDetails(mealSummaryId);
					List<MealMenu> mealMenus = new ArrayList<MealMenu>();
					for(SchoolMeal schoolMeal2 : schoolMeallist){
						mealMenus.add(schoolMeal2.getMealMenu());
					}
					mealMenuPdfUtility.mealMenuPdf(mealMenus, schoolMealSummary,currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
					serviceResponse.setStatus("Success");
					serviceResponse.setStatusMessage("Lunch Menu has been updated successfully.");
				}else{
					logger.info("Failed to modify the menu");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusMessage("Failed to update the Lunch menu.");
				}
		}catch(Exception e){
			logger.error("Failed to add/remove/edit the menu item due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to proceed the request. Please try again later!!");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for add/remove/edit the menu item**/
	@Override
	public ServiceResponse menuItemsModificationV2(Long mealSummaryId, MenuModificationRequest menuModificationReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			MealCalendarSummary schoolMealSummary = mealCalendarSummaryRepository.findOne(mealSummaryId);
			if(schoolMealSummary==null || schoolMealSummary.getMealByDays()==null|| schoolMealSummary.getMealByDays().size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("There are no menu available for the mealSummaryId: "+mealSummaryId+".");
				return serviceResponse;
			}
			String loggedUser = "";
			if(SecurityContextHolder.getContext() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			String yearMonth = schoolMealSummary.getYearMonth();
			MealCalendar matchedMealCalendar = null;
			Optional<MealCalendar> matchedMealCalendarOptional = null;
			List<MealOrderDetails> mealOrderDetailsFinalList = null;
			List<MealOrderDetails> mealOrderUpdateList = null;
			MealSchool mealSchool = schoolMealSummary.getSchool();
			Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
			//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			//List<MealCalendar> schoolMealFinal = new ArrayList<>();
			Long updatedMenuId = (long) 0;
			Set<MealCalendar> mealCalendarList = new HashSet<>(schoolMealSummary.getMealByDays());

			if(menuModificationReq.getOperationCode() == 0){
				MealCalendar calendar = addMealCalendar(menuModificationReq, schoolMealSummary, loggedUser, mealSchool);
				Long calId = mealCalendarSummaryRepository.getHolidayCalId(mealSummaryId, sdf1.parse(menuModificationReq.getMenuModificationDate()));
				if(calId != null){
					matchedMealCalendarOptional = mealCalendarList.stream().filter(
							mealCalendar -> calId.equals(mealCalendar.getId())).findFirst();
					 matchedMealCalendar = matchedMealCalendarOptional.get();
					mealCalendarList = updateMealCalendar(mealCalendarList,menuModificationReq,mealSchool, loggedUser, matchedMealCalendar);
				}else
					mealCalendarList.add(calendar);
			}else if(menuModificationReq.getOperationCode() == 1 && menuModificationReq.getMealCalendarId() != null
					&& menuModificationReq.getMealCalendarId() != 0){
				//edit menu item
				matchedMealCalendarOptional = mealCalendarList.stream().filter(
						mealCalendar -> menuModificationReq.getMealCalendarId().equals(mealCalendar.getId())).findFirst();
				 matchedMealCalendar = matchedMealCalendarOptional.get();
				 updatedMenuId = matchedMealCalendar.getId();
				mealCalendarList = updateMealCalendar(mealCalendarList,menuModificationReq,mealSchool, loggedUser, matchedMealCalendar);
			}else if(menuModificationReq.getOperationCode() == 2 && menuModificationReq.getMealCalendarId() != null
					&& menuModificationReq.getMealCalendarId() != 0){
				/*if(schoolMealSummary.getMealType().toString().equalsIgnoreCase(ItemTypeConstants.Lunch.toString()) || 
						schoolMealSummary.getMealType().toString().equalsIgnoreCase(ItemTypeConstants.Snack.toString())){*/
					Set<String> studentIds = manageMenuDao.getStudentIdsV2(yearMonth,
							menuModificationReq.getMealCalendarId(), schoolMealSummary.getSchool().getSchoolId());
					Set<MealOrderDetails> mealOrderDetailsRes = mealOrderDetailsRepository
							.findByStudentUserMealSchoolSchoolIdAndStudentUserStudentIdInAndYearMonthAndMenuType(
									schoolMealSummary.getSchool().getSchoolId(), studentIds, yearMonth, schoolMealSummary.getMealType());
					Boolean schoolInstantPayEnable = false;
					if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Instant Payment for Orders") != null && 
								mealSchool.getModuleAccess().get("Instant Payment for Orders").equalsIgnoreCase("Yes"))
						schoolInstantPayEnable = true;
					String itemType = CommonUtil.getItemType(schoolMealSummary.getMealType());
					mealOrderDetailsFinalList = buildCancelMenuOrderUpdateDataV2(mealOrderDetailsRes, menuModificationReq, loggedUser, 
							schoolInstantPayEnable, mealSchool, itemType, isItemized);
				//}				
				mealCalendarList = deleteMealCalendar(mealCalendarList, menuModificationReq,mealSchool, loggedUser);
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatusMessage("Please select valid operation for menu update.");
				return serviceResponse;
			}
			schoolMealSummary.getMealByDays().clear();
			schoolMealSummary.getMealByDays().addAll(new HashSet<>(mealCalendarList));
			schoolMealSummary.setModifiedBy(loggedUser);
			schoolMealSummary.setModifiedOn(new Date());
			mealCalendarSummaryRepository.save(schoolMealSummary);
			MealType mealType = MealType.MEAL;
			if(schoolMealSummary.getMealType().toString().equalsIgnoreCase(ItemTypeConstants.Breakfast.toString()))
				mealType = MealType.BREAKFAST;
			refreshMenuPdfV2(schoolMealSummary.getId(), mealType);
			if(/*(schoolMealSummary.getMealType().toString().equalsIgnoreCase(ItemTypeConstants.Lunch.toString()) || 
					schoolMealSummary.getMealType().toString().equalsIgnoreCase(ItemTypeConstants.Snack.toString()))
					&& */menuModificationReq.getOperationCode() > 0){
				String schoolName = mealSchool.getSchoolName();
				String logoLink = mealSchool.getLogoLink();
				String schoolTimezone = mealSchool.getSchoolTimezone().toString();
				Boolean priEmailIsSubscribe = null;
				Boolean altEmailIsSubscribe = null;
				if(updatedMenuId != null && updatedMenuId > 0){
					//TODO Assuming Order tables will store calendar ids
					Set<String> studentIds = manageMenuDao.getStudentIdsV2(yearMonth, updatedMenuId, schoolMealSummary.getSchool().getSchoolId());
					mealOrderUpdateList = new ArrayList<>(mealOrderDetailsRepository
							.findByStudentUserMealSchoolSchoolIdAndStudentUserStudentIdInAndYearMonthAndMenuType(
									schoolMealSummary.getSchool().getSchoolId(), studentIds, yearMonth, schoolMealSummary.getMealType()));
				}
				if(mealOrderDetailsFinalList == null)
					mealOrderDetailsFinalList = new ArrayList<MealOrderDetails>();
				if(mealOrderUpdateList != null && mealOrderUpdateList.size() > 0 && mealOrderDetailsFinalList != null)
					mealOrderDetailsFinalList.addAll(mealOrderUpdateList);
				if(mealOrderDetailsFinalList != null && mealOrderDetailsFinalList.size() > 0){
					for(MealOrderDetails mealOrderDetails : new HashSet<MealOrderDetails>(mealOrderDetailsFinalList)){
						priEmailIsSubscribe = null;
						altEmailIsSubscribe = null;
						ParentUser parentUser = mealOrderDetails.getStudentUser().getParentuser();
						if(parentUser.getUserName() != null){
							//usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getUserName());
							priEmailIsSubscribe = true; //usersAuthInfo.getEmailIsSubscribe();
						}
						if(parentUser.getParentAltEmail() != null && !parentUser.getParentAltEmail().trim().equalsIgnoreCase("")){
							//usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getParentAltEmail());
							altEmailIsSubscribe = true;//usersAuthInfo.getEmailIsSubscribe();
						}
						mealOrderDetailsRepository.save(mealOrderDetails);
						orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, schoolName, loggedUser, logoLink,
								parentUser, priEmailIsSubscribe, altEmailIsSubscribe, schoolTimezone,countryDetail.getCurrencySymbol(), mealSchool.getContactPEmail(), null, isItemized, 
								countryDetail.getDateFormat(),CommonUtil.getNonSchoolDays(mealSchool),false);
					}
				}
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Menu has been updated successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to update  menu due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update  menu.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**Delete the calendar item**/
	private Set<MealCalendar> deleteMealCalendar(Set<MealCalendar> mealCalendarList, MenuModificationRequest menuModificationReq,  MealSchool mealSchool, String loggedUser) throws ParseException {
		//FInd the menuItem
		Optional<MealCalendar> matchedMealCalendarOptional = mealCalendarList.stream().filter(
				mealCalendar -> menuModificationReq.getMealCalendarId().equals(mealCalendar.getId())).findFirst();
		MealCalendar matchedMealCalendar = matchedMealCalendarOptional.get();
		List<MealCalendar> schoolMealsForCanDt = mealCalendarList.stream().filter(mealCalendar -> mealCalendar.getDate()!=null
				&& mealCalendar.isActive() && (menuModificationReq.getMenuModificationDate().equals(sdf1.format(mealCalendar.getDate())))).collect(
				Collectors.toCollection(ArrayList::new));
		List<MealCalendar> schoolMeals = schoolMealsForCanDt.stream().filter(mealCalendar -> mealCalendar.getMenuItem()!=null
				&& mealCalendar.getMenuItem().getCategory().equals(MealType.MEAL)).collect(
				Collectors.toCollection(ArrayList::new));
		if(schoolMeals !=  null && schoolMeals.size() == 1 && matchedMealCalendar.getMenuItem().getCategory().toString().equalsIgnoreCase("MEAL")){
			Boolean isExtraEnable = false;
			for(MealCalendar schoolMeal : schoolMealsForCanDt){
				if(schoolMeal.getMenuItem().getCategory().toString().equalsIgnoreCase("EXTRA")){
					isExtraEnable = true;
				}else{
					schoolMeal.setActive(false);
					schoolMeal.setModifiedOn(new Date());
					schoolMeal.setModifiedBy(loggedUser);
				}				
			}
			if(!isExtraEnable){
				List<MenuItem> availableMenuItems = menuItemRepository.getMenuItemsByNameAndSchoolAndType("No Lunch due to "+menuModificationReq.getDeletionReason(),
						mealSchool.getSchoolId(), MealType.HOLIDAY);
				//if exist attach it to matched meal calendar
				MenuItem mennuItem = null;
				MealCalendar calendar = new MealCalendar();
				calendar.setActive(true);
				calendar.setDate(sdf1.parse(menuModificationReq.getMenuModificationDate()));
				if(availableMenuItems!=null && availableMenuItems.size()>0) {
					calendar.setMenuItem(availableMenuItems.get(0));
				}else {
					//Otherwise save and add it to matchedCalendar and save the
					mennuItem = createMenuItem(menuModificationReq, loggedUser, mealSchool, MealType.HOLIDAY);
					mennuItem.setName("No Lunch due to "+menuModificationReq.getDeletionReason());
					menuItemRepository.save(mennuItem);
					calendar.setMenuItem(mennuItem);
				}
				mealCalendarList.add(calendar);
			}			
		}else{
			matchedMealCalendar.setActive(false);
			matchedMealCalendar.setModifiedOn(new Date());
			matchedMealCalendar.setModifiedBy(loggedUser);
		}
		return mealCalendarList;
	}

	/**Update meal calendar**/
	private Set<MealCalendar> updateMealCalendar(Set<MealCalendar> mealCalendarList, MenuModificationRequest menuModificationReq, MealSchool mealSchool, String loggedUser, MealCalendar mealCalendar) {
		List<MenuItem> availableMenuItems = menuItemRepository.getMenuItemsByNameAndSchoolAndType(menuModificationReq.getItemName(),
				mealSchool.getSchoolId(), menuModificationReq.getMealType());
		//if exist attach it to matched meal calendar
		MenuItem mennuItem = null;
		if(availableMenuItems!=null && availableMenuItems.size()>0) {
			mealCalendar.setMenuItem(availableMenuItems.get(0));
		}else {
			//Otherwise save and add it to matchedCalendar and save the
			mennuItem = createMenuItem(menuModificationReq, loggedUser, mealSchool, menuModificationReq.getMealType());
			menuItemRepository.save(mennuItem);
			mealCalendar.setMenuItem(mennuItem);
		}
		mealCalendar.setPrice(menuModificationReq.getPrice());
		mealCalendar.setReducedPrice(menuModificationReq.getReducedPrice());
		return mealCalendarList;
	}

	/**Add meal calendar**/
	private MealCalendar addMealCalendar(MenuModificationRequest menuModificationReq, MealCalendarSummary schoolMealSummary, String loggedUser, MealSchool mealSchool) throws ParseException {
		List<MenuItem> availableMenuItems = menuItemRepository.getMenuItemsByNameAndSchoolAndType(menuModificationReq.getItemName(), schoolMealSummary.getSchool().getSchoolId(), menuModificationReq.getMealType());
		//if exist attach it to matched meal calendar
		MenuItem mennuItem = null;
		if(availableMenuItems!=null && availableMenuItems.size() > 0)
			mennuItem = availableMenuItems.get(0);
		else {
			//Otherwise save and add it to matchedCalendar and save the
			mennuItem = createMenuItem(menuModificationReq, loggedUser, mealSchool, menuModificationReq.getMealType());
			menuItemRepository.save(mennuItem);
		}
		//Create MealCalendar
		MealCalendar calendar = new MealCalendar();
		calendar.setMenuItem(mennuItem);
		calendar.setDate(sdf1.parse(menuModificationReq.getMenuModificationDate()));
		calendar.setActive(true);
		calendar.setCreatedBy(loggedUser);
		calendar.setCreatedOn(new Date());
		calendar.setPrice(menuModificationReq.getPrice());
		calendar.setReducedPrice(menuModificationReq.getReducedPrice());
		return calendar;
	}

	/**Add menu item**/
	private MenuItem createMenuItem(MenuModificationRequest menuModificationReq, String loggedUser, MealSchool mealSchool, MealType mealType) {
		MenuItem mennuItem = new MenuItem();
		mennuItem.setName(menuModificationReq.getItemName());
		mennuItem.setCategory(mealType);
		mennuItem.setLongDescription(menuModificationReq.getDesc());
		/*mennuItem.setPrice(menuModificationReq.getPrice());
		mennuItem.setReducedPrice(menuModificationReq.getReducedPrice());*/
		mennuItem.setActive(true);
		mennuItem.setSchoolDetails(mealSchool);
		mennuItem.setCreatedBy(loggedUser);
		mennuItem.setCreatedOn(new Date());
		return mennuItem;
	}
	
	/**This method used for edit the Side menu**/
	@Override
	public ServiceResponse updateSideMenu(Long mealSummaryId, String sideName, Long schoolMealId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<SchoolMeal> schoolMeallist = new ArrayList<SchoolMeal>(schoolMealRepository.findBySchoolMealSummarySchoolIdAndIsDelete(
					mealSummaryId, false));
			SchoolMealSummary schoolMealSummary = null;
			if(schoolMeallist == null || schoolMeallist.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("There are no side available for update.");
				return serviceResponse;
			}
			String loggedUser = "";
			if(SecurityContextHolder.getContext() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			schoolMealSummary = schoolMeallist.get(0).getSchoolMealSummary();
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(schoolMealSummary.getMealSchool().getSchoolId());
			Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
			SchoolMeal schoolMeal = schoolMeallist.stream().filter(sm -> schoolMealId.equals(sm.getSchoolId()))
					.findAny().orElse(null);
			schoolMeallist.remove(schoolMeal);
			MealMenu mm = schoolMeal.getMealMenu();
			if (sideName != null)
				mm.setTitle(sideName);
			mm.setModifiedOn(new Date());
			mm.setModifiedBy(loggedUser);
			schoolMeal.setMealMenu(mm);
			schoolMeallist.add(schoolMeal);
			schoolMealRepository.save(schoolMeal);
			List<MealMenu> mealMenus = new ArrayList<MealMenu>();
			for(SchoolMeal schoolMeal2 : schoolMeallist){
				mealMenus.add(schoolMeal2.getMealMenu());
			}
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(schoolMealSummary.getMealSchool().getSchoolId()));
			mealMenuPdfUtility.mealMenuPdf(mealMenus, schoolMealSummary,currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Side Menu has been updated successfully.");
			serviceResponse.setStatusCode(200);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to update side menu due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update side menu.");
			serviceResponse.setErrorMessage(e.getMessage());;
		}
		return serviceResponse;
	}

	/**This method used for breakfast menu modification**/
	@Override
	public ServiceResponse breakfastModification(Long breakfastMasterId, BreakfastModificationReq breakfastModificationReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String loggedUser = "";
			if(SecurityContextHolder.getContext() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			BreakfastMaster breakfastMaster = breakfastMasterRepository.findByRecId(breakfastMasterId);
			if(breakfastMaster == null){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("No entry found for update breakfast.");
				return serviceResponse;
			}
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(breakfastMaster.getMealSchoolId());
			Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(breakfastMaster.getMealSchool().getSchoolId()));
			List<BreakfastItems> breakfastItemList = new ArrayList<BreakfastItems>(breakfastMaster.getBreakfastItems());
			if(breakfastModificationReq.getOperationCode() == 0){ //add breakfast menu
				BreakfastItems breakfastItems = addBreakfastMenu(breakfastModificationReq);
				breakfastItemList.add(breakfastItems);
			}else if(breakfastModificationReq.getOperationCode() == 1 && breakfastModificationReq.getItemId() != null 
					&& breakfastModificationReq.getItemId() != 0){ //edit breakfast menu
				breakfastItemList = updateBreakfastMenu(breakfastItemList, breakfastModificationReq);
			}else if(breakfastModificationReq.getOperationCode() == 2 && breakfastModificationReq.getItemId() != null 
					&& breakfastModificationReq.getItemId() != 0 && breakfastModificationReq.getBreakfastDate() != null){
				breakfastItemList = deleteBreakfastMenu(breakfastItemList, breakfastModificationReq);
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatusMessage("Please select valid operation for menu update.");
				return serviceResponse;
			}
			breakfastMaster.getBreakfastItems().clear();
			//breakfastMaster.setBreakfastItems(new HashSet<BreakfastItems>(breakfastItemList));
			breakfastMaster.getBreakfastItems().addAll(new HashSet<BreakfastItems>(breakfastItemList));
			breakfastMaster.setModifiedBy(loggedUser);
			breakfastMaster.setModifiedOn(new Date());
			breakfastMasterRepository.save(breakfastMaster);
			breakfastMenuPdfUtility.breakfastMenuPdf(breakfastMaster,currencySymbol, isItemized);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Breakfast Menu has been updated successfully.");
		}catch(Exception e){
			logger.error("Failed to update breakfast menu due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update breakfast menu.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get the last day of month**/
	private Date lastDayOfMonth(Date monthStartDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(monthStartDate);
		cal.set(Calendar.DAY_OF_MONTH,
		cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**This method used for breakfast menu items build in json format
	 * @throws Exception **/
	private MealCreateJson breakfastItemsJsonAndFile(MultipartFile multipartFile, Long mealSchoolId, String yearMonth, 
			Set<SchoolGrades> grades, List<SchoolHoliday> schoolHolidays, Long masterRecId) throws Exception {
		MealCreateJson mealCreateJson = new MealCreateJson();
		Boolean gradeStatus = true;
		String filePath = "";
		List<String> previousGrades = new ArrayList<String>();
		//Set<BreakfastItems> breakfastItems = new HashSet<BreakfastItems>();
		List<String> breakfastCreatedGrades = manageMenuDao.breakfastCreatedGrades(mealSchoolId, yearMonth);
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
		Map<String, String> gradeKeyVal = mealManageAPIDao.gradeMapByCountry(mealSchool.getCountryCode());
		String gradeFailed = "";
		BreakfastMaster breakfastMaster = null;
		List<String> gradesList1 = new ArrayList<String>();
		if (masterRecId != null && masterRecId != 0)
			breakfastMaster = breakfastMasterRepository.findByRecId(masterRecId);
		if (breakfastMaster != null && breakfastMaster.getGrades().size() > 0){
			for(SchoolGrades grade : breakfastMaster.getGrades()){
				previousGrades.add(grade.toString());
			}
		}
		for (SchoolType type : mealSchool.getSchool().getSchoolType()) {
			gradesList1.addAll(type.getValues());
		}
		for (SchoolGrades grade : grades) {
			if (gradesList1.contains(grade.toString())
					&& ((masterRecId != null && masterRecId != 0
							&& (previousGrades.contains(grade.toString())
									|| !breakfastCreatedGrades.contains(grade.toString())))
							|| !breakfastCreatedGrades.contains(grade.toString()))) {

			} else{
				gradeStatus = false;
				gradeFailed = grade.toString();
				break;
			}
		}
		if (gradeStatus) {
			/*if(breakfastMaster != null)
				breakfastItems = breakfastMaster.getBreakfastItems();*/
			//if (breakfastItems.size() < 1) {
				Set<MealsExcelSummary> mealsExcelSummaries = mealsExcelSummaryRepository
						.findByMealSchoolSchoolIdAndYearMonthAndGradesInAndItemType(mealSchoolId, yearMonth,
								new ArrayList<>(grades), ItemTypeConstants.Breakfast);
				mealCreateJson = excelReadUtil.mealJson(multipartFile, yearMonth, schoolHolidays, "Breakfast", null, false, null);
				if (mealCreateJson.getStatusCode() != 200)
					return mealCreateJson;
				if (mealsExcelSummaries.size() > 1) {
					mealsExcelSummaryRepository.delete(mealsExcelSummaries);
					mealsExcelSummaries = new HashSet<MealsExcelSummary>();
				}

				MealsExcelSummary mealsExcelSummary = new MealsExcelSummary();
				String loggedUser = "";
				if(SecurityContextHolder.getContext().getAuthentication() != null)
					loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
				if (mealsExcelSummaries.size() < 1) {
					mealsExcelSummary.setCreatedBy(loggedUser);
					mealsExcelSummary.setCreatedOn(new Date());
					mealsExcelSummary.setMealSchool(mealSchool);
					mealsExcelSummary.setExcelLink("");
					mealsExcelSummary.setYearMonth(yearMonth);
					mealsExcelSummary.setItemType(ItemTypeConstants.Breakfast);
					mealsExcelSummaryRepository.save(mealsExcelSummary);
				}
				if (mealsExcelSummaries.size() == 1)
					mealsExcelSummary = new ArrayList<>(mealsExcelSummaries).get(0);

				File convFile = new File(mealSchoolId + "_" + yearMonth + "_" + mealsExcelSummary.getId() + "_Breakfast."
						+ FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
				FileOutputStream fos = new FileOutputStream(convFile);
				fos.write(multipartFile.getBytes());
				fos.close();
				filePath = convFile.getAbsolutePath();
				String finalFileLink = awsUtility.fileUploadPath(filePath, "excelMenuFilelink");

				if (mealsExcelSummary != null) {
					mealsExcelSummary.setModifiedBy(loggedUser);
					mealsExcelSummary.setModifiedOn(new Date());
					mealsExcelSummary.setExcelLink(finalFileLink);
					mealsExcelSummary.setGrades(grades);
					mealsExcelSummaryRepository.save(mealsExcelSummary);
				}
				awsUtility.uploadMenuExcel(filePath, "MealsExcelFile");
				logger.info("The Meals Excel file has been uploaded successfully in S3 bucket");
				mealCreateJson.setStatus("Success");
				mealCreateJson.setStatusCode(200);
				mealCreateJson.setStatusMessage("Menu file imported successfully.");

			//}
		} else {
			logger.info("Please try again with valid grades");
			mealCreateJson.setStatus("Failed");
			if (!gradesList1.contains(gradeFailed)) {
				mealCreateJson.setStatusMessage(
						"Selected Grade " + gradeKeyVal.get(gradeFailed) + " does not belong to this School.");
			} else if ((masterRecId == null || masterRecId == 0) && breakfastCreatedGrades.contains(gradeFailed)) {
				mealCreateJson.setStatusMessage(
						"Breakfast menu already created for the selected Grade " + gradeKeyVal.get(gradeFailed)+".");
			} else if (masterRecId != null && masterRecId != 0
					&& (!previousGrades.contains(gradeFailed) || breakfastCreatedGrades.contains(gradeFailed))) {
				mealCreateJson.setStatusMessage("Breakfast menu can not update for the selected Grade "
						+ gradeKeyVal.get(gradeFailed) + " as it is belong to other created breakfast menu.");
			}
			logger.info(mealCreateJson.getStatusMessage());
			mealCreateJson.setStatusCode(422);
		}

		return mealCreateJson;
	}
	
	/**This method used for menu items build in json format
	 * @throws Exception **/
	private MealCreateJson menuItemsJsonAndFile(MultipartFile multipartFile, Long mealSchoolId, String yearMonth, 
			Set<SchoolGrades> grades, List<SchoolHoliday> schoolHolidays, Long masterRecId, ItemTypeConstants menuType, Boolean isExtraPreOrder) throws Exception {
		MealCreateJson mealCreateJson = new MealCreateJson();
		Boolean gradeStatus = true;
		String filePath = "";
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
		Boolean isOrderItemized = CommonUtil.checkItemized(mealSchool);
		String countryCode = mealSchool.getCountryCode();
		List<String> previousGrades = new ArrayList<String>();
		//Set<BreakfastItems> breakfastItems = new HashSet<BreakfastItems>();
		List<String> menuCreatedGrades = manageMenuDao.retrieveMenuScheduledGrades(mealSchoolId, yearMonth, menuType);
		String gradeFailed = "";
		MealCalendarSummary summary = null;
		List<String> gradesList1 = new ArrayList<String>();
		if (masterRecId != null && masterRecId != 0)
			summary = summaryRepo.findOne(masterRecId);
		if (summary != null && summary.getGrades().size() > 0){
			for(SchoolGrades grade : summary.getGrades()){
				previousGrades.add(grade.toString());
			}
		}
		for (SchoolType type : mealSchool.getSchool().getSchoolType()) {
			gradesList1.addAll(type.getValues());
		}
		for (SchoolGrades grade : grades) {
			if (gradesList1.contains(grade.toString())
					&& ((masterRecId != null && masterRecId != 0
							&& (previousGrades.contains(grade.toString())
									|| !menuCreatedGrades.contains(grade.toString())))
							|| !menuCreatedGrades.contains(grade.toString()))) {

			} else{
				gradeStatus = false;
				gradeFailed = grade.toString();
				break;
			}
		}
		if (gradeStatus) {
				Set<MealsExcelSummary> mealsExcelSummaries = mealsExcelSummaryRepository
						.findByMealSchoolSchoolIdAndYearMonthAndGradesInAndItemType(mealSchoolId, yearMonth,
								new ArrayList<>(grades), menuType);
				mealCreateJson = excelReadUtil.mealJson(multipartFile, yearMonth, schoolHolidays, menuType.toString(), true, isOrderItemized, isExtraPreOrder);
				if (mealCreateJson.getStatusCode() != 200)
					return mealCreateJson;
				if (mealsExcelSummaries.size() > 1) {
					mealsExcelSummaryRepository.delete(mealsExcelSummaries);
					mealsExcelSummaries = new HashSet<MealsExcelSummary>();
				}
				MealsExcelSummary mealsExcelSummary = new MealsExcelSummary();
				String loggedUser = "";
				if(SecurityContextHolder.getContext().getAuthentication() != null)
					loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
				if (mealsExcelSummaries.size() < 1) {
					mealsExcelSummary.setCreatedBy(loggedUser);
					mealsExcelSummary.setCreatedOn(new Date());
					mealsExcelSummary.setMealSchool(mealSchool);
					mealsExcelSummary.setExcelLink("");
					mealsExcelSummary.setYearMonth(yearMonth);
					mealsExcelSummary.setItemType(menuType);
					mealsExcelSummaryRepository.save(mealsExcelSummary);
				}
				if (mealsExcelSummaries.size() == 1)
					mealsExcelSummary = new ArrayList<>(mealsExcelSummaries).get(0);

				File convFile = new File(mealSchoolId + "_" + yearMonth + "_" + mealsExcelSummary.getId() + "_"+menuType+"."
						+ FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
				FileOutputStream fos = new FileOutputStream(convFile);
				fos.write(multipartFile.getBytes());
				fos.close();
				filePath = convFile.getAbsolutePath();
				String finalFileLink = awsUtility.fileUploadPath(filePath, "excelMenuFilelink");

				if (mealsExcelSummary != null) {
					mealsExcelSummary.setModifiedBy(loggedUser);
					mealsExcelSummary.setModifiedOn(new Date());
					mealsExcelSummary.setExcelLink(finalFileLink);
					mealsExcelSummary.setGrades(grades);
					mealsExcelSummaryRepository.save(mealsExcelSummary);
				}
				awsUtility.uploadMenuExcel(filePath, "MealsExcelFile");
				logger.info("The Meals Excel file has been uploaded successfully in S3 bucket");
				mealCreateJson.setStatus("Success");
				mealCreateJson.setStatusCode(200);
				mealCreateJson.setStatusMessage("Menu file imported successfully.");

			//}
		} else {
			logger.info("Please try again with valid grades");
			mealCreateJson.setStatus("Failed");
			Map<String, String> gradeKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode);
			if (!gradesList1.contains(gradeFailed)) {
				mealCreateJson.setStatusMessage(
						"Selected Grade " + gradeKeyVal.get(gradeFailed) + " does not belong to this School.");
			} else if ((masterRecId == null || masterRecId == 0) && menuCreatedGrades.contains(gradeFailed)) {
				mealCreateJson.setStatusMessage(
						menuType+" menu already created for the selected Grade " + gradeKeyVal.get(gradeFailed)+".");
			} else if (masterRecId != null && masterRecId != 0
					&& (!previousGrades.contains(gradeFailed) || menuCreatedGrades.contains(gradeFailed))) {
				mealCreateJson.setStatusMessage(menuType+"menu can not update for the selected Grade "
						+ gradeKeyVal.get(gradeFailed) + " as it is belong to other created menu.");
			}
			logger.info(mealCreateJson.getStatusMessage());
			mealCreateJson.setStatusCode(422);
		}

		return mealCreateJson;
	}
	
	/**This method used for create/update the breakfast menu
	 * @throws Exception **/
	private ServiceResponse breakfastMenuManageBuild(BreakfastMaster breakfastMaster) throws Exception{
		ServiceResponse serviceResponse = new ServiceResponse();
		if(SecurityContextHolder.getContext().getAuthentication() != null)
			breakfastMaster.setLoggedUser(SecurityContextHolder.getContext().getAuthentication().getName());
		BreakfastMaster breakfastMasterOld = null;
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(breakfastMaster.getMealSchoolId());
		Boolean isItemized = CommonUtil.checkItemized(mealSchool);
		breakfastMaster.setMealSchool(mealSchool);
		List<String> gradesList1 = new ArrayList<String>();
		Map<String, String> gradeKeyVal = mealManageAPIDao.gradeMapByCountry(mealSchool.getCountryCode());
		Boolean gradeStatus = true;
		List<String> previousGrades = new ArrayList<String>();
		String gradeFailed = "";
		String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
		for(SchoolType type : mealSchool.getSchool().getSchoolType()){
			gradesList1.addAll(type.getValues());
		}
		if(breakfastMaster.getRecId() != null && breakfastMaster.getRecId() != 0)
			breakfastMasterOld = breakfastMasterRepository.findByRecId(breakfastMaster.getRecId());
		if (breakfastMasterOld != null && breakfastMasterOld.getGrades().size() > 0){
			for(SchoolGrades grade : breakfastMasterOld.getGrades()){
				previousGrades.add(grade.toString());
			}
		}
		List<String> mealCreatedGradeList = manageMenuDao.breakfastCreatedGrades(breakfastMaster.getMealSchoolId(), breakfastMaster.getYearMonth());
		//Date monthStartDate = new SimpleDateFormat("yyyyMMdd").parse(yearMonth+""+01);
		//Date monthEndDate = lastDayOfMonth(monthStartDate);
		for(SchoolGrades grade : breakfastMaster.getGrades()){
			if(gradesList1.contains(grade.toString()) && 
					((breakfastMaster.getRecId() != null && breakfastMaster.getRecId() != 0 
					&& (previousGrades.contains(grade.toString()) || !mealCreatedGradeList.contains(grade.toString()))) || 
							!mealCreatedGradeList.contains(grade.toString()))){
				
			}else{
				gradeStatus = false;
				gradeFailed = grade.toString();
				break;
			}
		}
		if (gradeStatus) {
			if (breakfastMaster.getRecId() != null && breakfastMaster.getRecId() != 0 && breakfastMasterOld != null) {
					breakfastMaster.setCreatedBy(breakfastMasterOld.getCreatedBy());
					breakfastMaster.setCreatedOn(breakfastMasterOld.getCreatedOn());
					/*breakfastMaster.setModifiedBy(breakfastMaster.getLoggedUser());
					breakfastMaster.setModifiedOn(new Date());*/
					String breakfastMenuPdfLink = mealMenuPdfUtility.breakfastMenuPdfFinalLink(breakfastMaster.getMealSchoolId(),
							breakfastMaster.getYearMonth(), breakfastMaster.getRecId(), true);
					breakfastMaster.setItemsPdfLink(breakfastMenuPdfLink);
					breakfastMasterRepository.save(breakfastMaster);
					breakfastMenuPdfUtility.breakfastMenuPdf(breakfastMaster,currencySymbol, isItemized);
					serviceResponse.setStatusMessage("Breakfast menu updated successfully.");
					logger.info("Breakfast Menu updated successfully");
			}else {
				/*breakfastMaster.setCreatedBy(breakfastMaster.getLoggedUser());
				breakfastMaster.setCreatedOn(new Date());*/
				String breakfastMenuPdfLink = "";
				breakfastMaster.setItemsPdfLink(breakfastMenuPdfLink);
				breakfastMasterRepository.save(breakfastMaster);
				breakfastMenuPdfLink = mealMenuPdfUtility.breakfastMenuPdfFinalLink(breakfastMaster.getMealSchoolId(),
						breakfastMaster.getYearMonth(), breakfastMaster.getRecId(), true);
				breakfastMaster.setItemsPdfLink(breakfastMenuPdfLink);
				breakfastMasterRepository.save(breakfastMaster);
				breakfastMenuPdfUtility.breakfastMenuPdf(breakfastMaster,currencySymbol, isItemized);
				serviceResponse.setStatusMessage("Breakfast menu created Successfully.");
				logger.info("Breakfast menu created Successfully");
			}
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
		}else{
			serviceResponse.setStatus("Failed");
			if(!gradesList1.contains(gradeFailed)){
				serviceResponse.setStatusMessage("Selected Grade "+gradeKeyVal.get(gradeFailed)+" does not belong to this School.");
			}else if((breakfastMaster.getRecId() == null || breakfastMaster.getRecId() == 0) 
					&& mealCreatedGradeList.contains(gradeFailed)){
				serviceResponse.setStatusMessage("Breakfast menu already created for the selected Grade "+gradeKeyVal.get(gradeFailed)+".");
			}else if(breakfastMaster.getRecId() != null && breakfastMaster.getRecId() != 0 && (!previousGrades.contains(gradeFailed) || 
					mealCreatedGradeList.contains(gradeFailed))){
				serviceResponse.setStatusMessage("Breakfast menu can not update for the selected Grade "+gradeKeyVal.get(gradeFailed)+" as it is belong to other Menu.");
			}
			logger.info(serviceResponse.getStatusMessage());
			serviceResponse.setStatusCode(422);
		}
		return serviceResponse;
	}

	/**This method used for build the breakfast menu items in json format**/
	private MealItems buildBreakfastRequiredFormat(BreakfastMaster breakfastMaster){
		MealItems mealItems = new MealItems();
		List<MealJsonData> otherItems = new ArrayList<MealJsonData>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateValue = null;
		String[] dtArray = null;
		MealJsonData mealJsonData = null;
		for (BreakfastItems breakfastItems : breakfastMaster.getBreakfastItems()) {
			mealJsonData = new MealJsonData();
			mealJsonData.setId(Integer.parseInt(breakfastItems.getRecId().toString()));
			mealJsonData.setPrice(breakfastItems.getPrice());
			mealJsonData.setReducedPrice(breakfastItems.getReducedPrice());
			mealJsonData.setDesc(breakfastItems.getItemDesc());
			mealJsonData.setTitle(breakfastItems.getItemName());
			mealJsonData.setType(breakfastItems.getItemType().toString());
			dtArray = sdf.format(breakfastItems.getBreakfastDate()).split("-");
			dateValue = dtArray[0] + ", " + (Integer.parseInt(dtArray[1]) - 1) + ", " + (Integer.parseInt(dtArray[2]));
			mealJsonData.setStart("new Date(" + dateValue + "')'");
			mealJsonData.setEnd("new Date(" + dateValue + "')'");
			otherItems.add(mealJsonData);
		}
		mealItems.setMealMenuItems(otherItems);
		return mealItems;
	}
	
	/**This method used for build the restore cancelled item details
	 * @throws Exception **/
	private List<MealOrderDetails> buildRestoreMenuOrderUpdateData(Set<MealOrderDetails> mealOrderDetailsList, 
			RestoreCancelledMenuReq restoreCancelledMenuReq, Set<MenuOrderHistoryAudit> menuOrderHstryList) throws Exception{
		List<MealOrderDetails> mealOrderDetailsFinalList = new ArrayList<MealOrderDetails>();
		List<SchoolMeal> schoolMealList = null;
		Double totalPrice = 0.0;
		Double orderAmount = 0.0;
		List<MenuOrderHistoryAudit> menuOrderHistoryAudits = null;
		MenuOrderHistoryAudit menuOrderHistoryAudit = null;
		List<SchoolMeal> cancelledMenus = null;
		Boolean processStatus = false; 
		for(MealOrderDetails mealOrderDetail : mealOrderDetailsList){
			if(mealOrderDetail.getSchoolMeals().stream().filter(p ->  p.getMealMenu().getStart() != null 
					&& restoreCancelledMenuReq.getRestoreDate().contains(sdf1.format(p.getMealMenu().getStart()))).collect(
							Collectors.toCollection(ArrayList::new)).size() < 1){
				schoolMealList = new ArrayList<>(mealOrderDetail.getSchoolMeals());
				menuOrderHistoryAudits = menuOrderHstryList.stream().filter(p ->  p.getOrderId() != null 
						&& p.getOrderId().equals(mealOrderDetail.getSchoolId())).collect(Collectors.toCollection(ArrayList::new));
				//Collections.sort(menuOrderHistoryAudits, (o1, o2) -> o1.getCreatedOn().compareTo(o2.getCreatedOn()));
				//menuOrderHistoryAudit = menuOrderHistoryAudits.get(0);
				menuOrderHistoryAudit = null;
				for(MenuOrderHistoryAudit menuOrderHistoryAudit2 : menuOrderHistoryAudits){
					if(processStatus){
						menuOrderHistoryAudit = menuOrderHistoryAudit2;
						processStatus = false;
						break;
					}
					if(menuOrderHistoryAudit2.getCancellationDates() != null && menuOrderHistoryAudit2.getCancellationDates()
							.equalsIgnoreCase(restoreCancelledMenuReq.getRestoreDate()) && menuOrderHistoryAudit2.getCrudOperationVal() == 2)
						processStatus = true;
				}
				cancelledMenus = menuOrderHistoryAudit.getSchoolMeals().stream().filter(p ->  p.getMealMenu().getStart() != null 
						&& restoreCancelledMenuReq.getRestoreDate().contains(sdf1.format(p.getMealMenu().getStart()))/* &&  
						p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL")*/).collect(
								Collectors.toCollection(ArrayList::new));
				if(cancelledMenus.size() > 0){
					schoolMealList.addAll(cancelledMenus);
					mealOrderDetail.setSchoolMeals(new HashSet<>(schoolMealList));
					mealOrderDetail.setLoggedUser(restoreCancelledMenuReq.getLoggedUser());
					mealOrderDetail.setCrudOperationVal(3); //restore the cancelled items
					mealOrderDetail.setCancellationNote(restoreCancelledMenuReq.getRestoreNote()+". Restored cancelled items for date: "+
							restoreCancelledMenuReq.getRestoreDate());
					mealOrderDetail.setItems_count((int) schoolMealList.stream().filter(p -> p.getMealMenu().getType() != null && 
							p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL")).count());
					totalPrice = schoolMealList.stream().filter(p -> p.getMealMenu().getType() != null && 
							p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL")).mapToDouble(o -> o.getMealMenu().getPrice()).sum();
					if(mealOrderDetail.getIsEligibleForFreeMeal())
						orderAmount = 0.0;
					else if(mealOrderDetail.getIsEligibleForReducedPrice())
						orderAmount = schoolMealList.stream().filter(p -> p.getMealMenu().getType() != null && 
								p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL") && p.getMealMenu().getReducedPrice() != null)
								.mapToDouble(o -> o.getMealMenu().getReducedPrice()).sum();
					else
						orderAmount = totalPrice;
					if(schoolMealList.get(0).getMealSchool().getModuleAccess().get("Instant Payment for Orders") != null && 
							schoolMealList.get(0).getMealSchool().getModuleAccess().get("Instant Payment for Orders").equalsIgnoreCase("Yes") 
							&& mealOrderDetail.getPaymentStatus() != null && mealOrderDetail.getPaymentStatus()){
						StudentMealOrdersV2 stdOrder = new StudentMealOrdersV2();
						stdOrder.setStudentId(mealOrderDetail.getStudentUser().getUserId());
						stdOrder.setWalletAmt(orderAmount - mealOrderDetail.getOrderAmount());
						mealManageAPIDao.addInstantPayTrx(restoreCancelledMenuReq.getLoggedUser(), 
								schoolMealList.get(0).getMealSchool(), stdOrder, null, mealOrderDetail.getMenuType());
					}
					mealOrderDetail.setTotalPrice(Double.parseDouble(String.format("%.2f", totalPrice)));
					mealOrderDetail.setOrderAmount(Double.parseDouble(String.format("%.2f", orderAmount)));
					mealOrderDetailsFinalList.add(mealOrderDetail);
				}	
			}
		}
		return mealOrderDetailsFinalList;
	}
	
	/**This method used for build the restore cancelled item details
	 * @throws Exception **/
	private List<MealOrderDetails> buildRestoreMenuOrderUpdateDataV2(Set<MealOrderDetails> mealOrderDetailsList, 
			RestoreCancelledMenuReq restoreCancelledMenuReq, Set<MenuOrderHistoryAudit> menuOrderHstryList, MealSchool mealSchool, String itemType, Boolean isItemized) throws Exception{
		List<MealOrderDetails> mealOrderDetailsFinalList = new ArrayList<MealOrderDetails>();
		List<MealCalendar> calendars = null;
		Double totalPrice = 0.0;
		Double orderAmount = 0.0;
		List<MenuOrderHistoryAudit> menuOrderHistoryAudits = null;
		MenuOrderHistoryAudit menuOrderHistoryAudit = null;
		List<MealCalendar> cancelledMenus = null;
		Boolean processStatus = false; 
		for(MealOrderDetails mealOrderDetail : mealOrderDetailsList){
			if(mealOrderDetail.getMealCalendars().stream().filter(p ->  p.getDate() != null 
					&& restoreCancelledMenuReq.getRestoreDate().contains(sdf1.format(p.getDate()))).collect(
							Collectors.toCollection(ArrayList::new)).size() < 1){
				calendars = new ArrayList<>(mealOrderDetail.getMealCalendars());
				menuOrderHistoryAudits = menuOrderHstryList.stream().filter(p ->  p.getOrderId() != null 
						&& p.getOrderId().equals(mealOrderDetail.getSchoolId())).collect(Collectors.toCollection(ArrayList::new));
				//Collections.sort(menuOrderHistoryAudits, (o1, o2) -> o1.getCreatedOn().compareTo(o2.getCreatedOn()));
				//menuOrderHistoryAudit = menuOrderHistoryAudits.get(0);
				menuOrderHistoryAudit = null;
				for(MenuOrderHistoryAudit menuOrderHistoryAudit2 : menuOrderHistoryAudits){
					if(processStatus){
						menuOrderHistoryAudit = menuOrderHistoryAudit2;
						processStatus = false;
						break;
					}
					if(menuOrderHistoryAudit2.getCancellationDates() != null && menuOrderHistoryAudit2.getCancellationDates()
							.equalsIgnoreCase(restoreCancelledMenuReq.getRestoreDate()) && menuOrderHistoryAudit2.getCrudOperationVal() == 2)
						processStatus = true;
				}
				if(menuOrderHistoryAudit != null)
					cancelledMenus = menuOrderHistoryAudit.getMealCalendars().stream().filter(p ->  p.getDate() != null 
						&& restoreCancelledMenuReq.getRestoreDate().contains(sdf1.format(p.getDate()))/* &&  
						p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL")*/).collect(
								Collectors.toCollection(ArrayList::new));
				else
					cancelledMenus = null;
				if(cancelledMenus != null && cancelledMenus.size() > 0){
					Long summaryId = mealCalendarSummaryRepository.getSummaryId(cancelledMenus.get(0).getId());
					List<Date> mainItemDates1 = mealCalendarSummaryRepository.getMainItemDates(summaryId, itemType);
					List<String> mainItemDates = mainItemDates1.stream().map(s -> sdf1.format(s)).collect(Collectors.toList());
					calendars.addAll(cancelledMenus);
					Set<String> orderItemDates = new HashSet<>();
					for(MealCalendar c : calendars){
						if(c.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType))
							orderItemDates.add(sdf1.format(c.getDate()));
					}
					mealOrderDetail.setMealCalendars(new HashSet<>(calendars));
					mealOrderDetail.setLoggedUser(restoreCancelledMenuReq.getLoggedUser());
					mealOrderDetail.setCrudOperationVal(3); //restore the cancelled items
					mealOrderDetail.setCancellationNote(restoreCancelledMenuReq.getRestoreNote()+". Restored cancelled items for date: "+
							restoreCancelledMenuReq.getRestoreDate());
					mealOrderDetail.setItems_count((int) calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
							p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType)).count());
					totalPrice = calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
							p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType)).mapToDouble(o -> o.getPrice()).sum();
					if(mealOrderDetail.getIsEligibleForFreeMeal()/* && (!isItemized || itemType.equalsIgnoreCase("Breakfast"))) || 
							(mealOrderDetail.getStudentUser().isBeforeCare() && itemType.equalsIgnoreCase("Breakfast"))*/)
						orderAmount = 0.0;
					else if(mealOrderDetail.getIsEligibleForReducedPrice()/* && (!isItemized || itemType.equalsIgnoreCase("Breakfast"))*/)
						orderAmount = calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
								p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType) && p.getReducedPrice() != null)
								.mapToDouble(o -> o.getReducedPrice()).sum();
					else if(mainItemDates.size() == orderItemDates.size() && mealOrderDetail.getItemDiscount() != null){
						orderAmount = calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
						p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType)).mapToDouble(o -> (o.getPrice()-mealOrderDetail.getItemDiscount())).sum();
						mealOrderDetail.setIsEligForDiscount(true);
					}else
						orderAmount = totalPrice;
					Double extraItemPrice = 0.0;
					extraItemPrice = calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
							p.getMenuItem().getCategory().toString().equalsIgnoreCase("EXTRA"))
							.mapToDouble(o -> o.getPrice()).sum();
					orderAmount = orderAmount+extraItemPrice;
					totalPrice = totalPrice+extraItemPrice;
					if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Instant Payment for Orders") != null && 
							mealSchool.getModuleAccess().get("Instant Payment for Orders").equalsIgnoreCase("Yes") 
							&& mealOrderDetail.getPaymentStatus() != null && mealOrderDetail.getPaymentStatus()){
						StudentMealOrdersV2 stdOrder = new StudentMealOrdersV2();
						stdOrder.setStudentId(mealOrderDetail.getStudentUser().getUserId());
						Double payeeAmt = orderAmount - mealOrderDetail.getOrderAmount();
						Double refundAmt = null;
						if(payeeAmt > 0)
							stdOrder.setWalletAmt(payeeAmt);
						else if(payeeAmt < 0)
							refundAmt = -(payeeAmt);
						mealManageAPIDao.addInstantPayTrx(restoreCancelledMenuReq.getLoggedUser(), 
								mealSchool, stdOrder, refundAmt,mealOrderDetail.getMenuType());
					}
					mealOrderDetail.setTotalPrice(Double.parseDouble(String.format("%.2f", totalPrice)));
					mealOrderDetail.setOrderAmount(Double.parseDouble(String.format("%.2f", orderAmount)));
					mealOrderDetailsFinalList.add(mealOrderDetail);
				}	
			}
		}
		return mealOrderDetailsFinalList;
	}
	
	/**This method used for build the menu ordered pdf file and send to the respective parent user**/
	private void buildPdfAndSendToParent(MealSchool mealSchool, Set<MealOrderDetails> mealOrderDetailsList, 
			RestoreCancelledMenuReq restoreCancelledMenuReq,String currencySymbol, Boolean isItemized, String dateFormat) throws Exception{
		String schoolName = mealSchool.getSchoolName();
		String logoLink = mealSchool.getLogoLink();
		String schoolTimezone = mealSchool.getSchoolTimezone().toString();
		Boolean priEmailIsSubscribe = null;
		Boolean altEmailIsSubscribe = null;
		UsersAuthInfo usersAuthInfo = null;
		for(MealOrderDetails mealOrderDetails : mealOrderDetailsList){
			ParentUser parentUser = mealOrderDetails.getStudentUser().getParentuser();
			if(parentUser.getUserName() != null){
				usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getUserName());
				priEmailIsSubscribe = usersAuthInfo.getEmailIsSubscribe();
			}
			if(parentUser.getParentAltEmail() != null && !parentUser.getParentAltEmail().trim().equalsIgnoreCase("")){
				usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getParentAltEmail());
				altEmailIsSubscribe = usersAuthInfo.getEmailIsSubscribe();
			}
			orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, schoolName, restoreCancelledMenuReq.getLoggedUser(), logoLink, 
								parentUser, priEmailIsSubscribe, altEmailIsSubscribe, schoolTimezone,currencySymbol, mealSchool.getContactPEmail(), 
								null, isItemized, dateFormat,CommonUtil.getNonSchoolDays(mealSchool),false);
		}
	}
	
	private List<MealOrderDetails> buildCancelMenuOrderUpdateDataV2(Set<MealOrderDetails> mealOrderDetailsList,
			MenuModificationRequest menuModificationReq, String loggedUser, Boolean schoolInstantPayEnable, MealSchool mealSchool, String itemType, Boolean isItemized) throws Exception {
		List<MealOrderDetails> mealOrderDetailsFinalList = new ArrayList<MealOrderDetails>();
		List<MealCalendar> mealCalendars = null;
		Double totalReducedPrice = 0.0;
		Double totalPrice = 0.0;
		Double extraItemPrice = 0.0;
		Double orderAmount = 0.0;
		if (mealOrderDetailsList != null && mealOrderDetailsList.size() > 0)
			for (MealOrderDetails mealOrderDetails : mealOrderDetailsList) {
				mealCalendars = new ArrayList<>(mealOrderDetails.getMealCalendars());
				mealCalendars = mealCalendars.stream()
						.filter(p -> p.getDate() != null && !menuModificationReq.getMealCalendarId().equals(p.getId()))
						.collect(Collectors.toCollection(ArrayList::new));
				Long summaryId = mealCalendarSummaryRepository.getSummaryId(menuModificationReq.getMealCalendarId());
				List<Date> mainItemDates1 = mealCalendarSummaryRepository.getMainItemDates1(summaryId, itemType, menuModificationReq.getMealCalendarId());
				List<String> mainItemDates = mainItemDates1.stream().map(s -> sdf1.format(s)).collect(Collectors.toList());
				Set<String> orderItemDates = new HashSet<>();
				for(MealCalendar c : mealCalendars){
					if(c.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType))
						orderItemDates.add(sdf1.format(c.getDate()));
				}
				mealOrderDetails.setMealCalendars(new HashSet<>(mealCalendars));
				mealOrderDetails.setLoggedUser(loggedUser);
				mealOrderDetails.setCrudOperationVal(2);
				mealOrderDetails.setCancellationNote(menuModificationReq.getDeletionReason());
				mealOrderDetails.setCancellationDates(menuModificationReq.getMenuModificationDate());
				mealOrderDetails.setLoggedUser(loggedUser);
				mealOrderDetails.setItems_count((int) mealCalendars.stream()
										.filter(p -> p.getMenuItem().getCategory() != null
												&& p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType)).count());
				totalPrice = mealCalendars.stream().filter(p -> p.getMenuItem().getCategory() != null
								&& p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType))
						.mapToDouble(o -> o.getPrice()).sum();
				extraItemPrice = mealCalendars.stream().filter(p -> p.getMenuItem().getCategory() != null
						&& p.getMenuItem().getCategory().toString().equalsIgnoreCase("EXTRA"))
				.mapToDouble(o -> o.getPrice()).sum();
				totalReducedPrice = mealCalendars.stream()
						.filter(p -> p.getMenuItem().getCategory() != null
								&& p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType)
								&& p.getReducedPrice() != null)
						.mapToDouble(o -> o.getReducedPrice()).sum();
				if (mealOrderDetails.getIsEligibleForFreeMeal())
					orderAmount = 0.0;
				else if (mealOrderDetails.getIsEligibleForReducedPrice())
					orderAmount = totalReducedPrice;
				else if(mainItemDates.size() == orderItemDates.size() && mealOrderDetails.getItemDiscount() != null){
					orderAmount = mealCalendars.stream().filter(p -> p.getMenuItem().getCategory() != null
							&& p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType))
					.mapToDouble(o -> (o.getPrice()-mealOrderDetails.getItemDiscount())).sum();
					mealOrderDetails.setIsEligForDiscount(true);
				}else{
					orderAmount = totalPrice;
					mealOrderDetails.setIsEligForDiscount(false);
				}
				orderAmount = (orderAmount+extraItemPrice);
				totalPrice = (totalPrice+extraItemPrice);
				mealOrderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", totalPrice)));
				if(schoolInstantPayEnable && orderAmount < mealOrderDetails.getOrderAmount() && 
						mealOrderDetails.getPaymentStatus() != null && mealOrderDetails.getPaymentStatus()){
						Double refundAmt = mealOrderDetails.getOrderAmount() - orderAmount;
						StudentMealOrdersV2 stdOrder = new StudentMealOrdersV2();
						stdOrder.setStudentId(mealOrderDetails.getStudentUser().getUserId());
						mealManageAPIDao.addInstantPayTrx(loggedUser, mealSchool, stdOrder, refundAmt, mealOrderDetails.getMenuType());
				}
				mealOrderDetails.setOrderAmount(Double.parseDouble(String.format("%.2f", orderAmount)));
				mealOrderDetailsFinalList.add(mealOrderDetails);
			}
		return mealOrderDetailsFinalList;
	}

	private List<MealOrderDetails> buildCancelMenuOrderUpdateData(Set<MealOrderDetails> mealOrderDetailsList, 
			MenuModificationReq menuModificationReq, String loggedUser){
		List<MealOrderDetails> mealOrderDetailsFinalList = new ArrayList<MealOrderDetails>();
		List<SchoolMeal> schoolMealList = null;
		Double totalReducedPrice = 0.0;
		Double totalPrice = 0.0;
		Double orderAmount = 0.0;
		if(mealOrderDetailsList != null && mealOrderDetailsList.size() > 0)
		for(MealOrderDetails mealOrderDetails : mealOrderDetailsList){
			schoolMealList = new ArrayList<>(mealOrderDetails.getSchoolMeals());
			schoolMealList = schoolMealList.stream().filter(p ->  p.getMealMenu().getStart() != null 
					&& (!menuModificationReq.getCancellationDate().equals(sdf1.format(p.getMealMenu().getStart())) || 
					p.getMealMenu().getType().toString().equalsIgnoreCase("HOLIDAY"))).collect(
							Collectors.toCollection(ArrayList::new));
				mealOrderDetails.setSchoolMeals(new HashSet<>(schoolMealList));
				//mealOrderDetails.setModifiedBy(menuOrderCancelReq.getLoggedUser());
				//mealOrderDetails.setModifiedOn(new Date());
				mealOrderDetails.setLoggedUser(loggedUser);
				mealOrderDetails.setCrudOperationVal(2);
				mealOrderDetails.setCancellationNote(menuModificationReq.getMenuDeletionReason());
				mealOrderDetails.setCancellationDates(menuModificationReq.getCancellationDate());
				mealOrderDetails.setLoggedUser(loggedUser);
				mealOrderDetails.setItems_count((int) schoolMealList.stream().filter(p -> p.getMealMenu().getType() != null && 
						p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL")).count());
				totalPrice = schoolMealList.stream().filter(p -> p.getMealMenu().getType() != null && 
						p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL")).mapToDouble(o -> o.getMealMenu().getPrice()).sum();
				totalReducedPrice = schoolMealList.stream().filter(p -> p.getMealMenu().getType() != null && 
						p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL") && p.getMealMenu().getReducedPrice() != null)
						.mapToDouble(o -> o.getMealMenu().getReducedPrice()).sum();
				if(mealOrderDetails.getIsEligibleForFreeMeal())
					orderAmount = 0.0;
				else if(mealOrderDetails.getIsEligibleForReducedPrice())
					orderAmount = totalReducedPrice;
				else
					orderAmount = totalPrice;
				mealOrderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", totalPrice)));
				mealOrderDetails.setOrderAmount(Double.parseDouble(String.format("%.2f", orderAmount)));
				mealOrderDetailsFinalList.add(mealOrderDetails);
		}
		return mealOrderDetailsFinalList;
	}
	
	/**This method used for build the grade**/
	private Set<SchoolGrades> buildGradesFinalVal(Long schoolMealId){
		Set<String> schoolGrades = manageMenuDao.getMenuAddGrades(schoolMealId);
		Set<SchoolGrades> schoolGradesAll = new HashSet<SchoolGrades>();
		for(String schoolGrade : schoolGrades){
			schoolGradesAll.add(SchoolGrades.valueOf(schoolGrade));
		}
		return schoolGradesAll;
	}

	/*private Set<SchoolGrades> buildGradesFinalValV2(Long schoolMealId){
		Set<String> schoolGrades = manageMenuDao.getMenuAddGradesV2(schoolMealId);
		Set<SchoolGrades> schoolGradesAll = new HashSet<SchoolGrades>();
		for(String schoolGrade : schoolGrades){
			schoolGradesAll.add(SchoolGrades.valueOf(schoolGrade));
		}
		return schoolGradesAll;
	}*/

	/**This method used for delete the breakfast menu
	 * @throws ParseException **/
	private List<BreakfastItems> deleteBreakfastMenu(List<BreakfastItems> breakfastItemList, 
			BreakfastModificationReq breakfastModificationReq) throws ParseException{
		List<BreakfastItems> breakfastForCanDt = breakfastItemList.stream().filter(p ->  p.getBreakfastDate() != null 
				&& (breakfastModificationReq.getBreakfastDate().equals(sdf1.format(p.getBreakfastDate())))).collect(
								Collectors.toCollection(ArrayList::new));
		if(breakfastForCanDt !=  null && breakfastForCanDt.size() <= 2){
			for(BreakfastItems breakfastItems : breakfastForCanDt){
				breakfastItemList.remove(breakfastItems);
			}
			BreakfastItems breakfastItems = new BreakfastItems();
			breakfastItems.setBreakfastDate(sdf1.parse(breakfastModificationReq.getBreakfastDate()));
			breakfastItems.setItemType(MealType.HOLIDAY);
			breakfastItems.setItemName("No Breakfast due to "+breakfastModificationReq.getDeletionReason());
			breakfastItemList.add(breakfastItems);
		}else{
			BreakfastItems breakfastItems = breakfastItemList.stream().filter(sm -> breakfastModificationReq.getItemId()
					.equals(sm.getRecId())).findAny().orElse(null);
			breakfastItemList.remove(breakfastItems);
		}
		return breakfastItemList;	
	}
	
	/**This method used for update the breakfast menu info**/
	private List<BreakfastItems> updateBreakfastMenu(List<BreakfastItems> breakfastItemList, 
		BreakfastModificationReq breakfastModificationReq) throws ParseException{
		BreakfastItems breakfastItems = breakfastItemList.stream().filter(sm -> breakfastModificationReq.getItemId()
				.equals(sm.getRecId())).findAny().orElse(null);
		breakfastItemList.remove(breakfastItems);
		if(breakfastItems.getItemType().toString().equalsIgnoreCase(MealType.BREAKFAST.toString()) && 
					breakfastModificationReq.getDesc() != null)
			breakfastItems.setItemDesc(breakfastModificationReq.getDesc());
		if(breakfastModificationReq.getItemName() != null)
			breakfastItems.setItemName(breakfastModificationReq.getItemName());
		breakfastItemList.add(breakfastItems);
		return breakfastItemList;	
	}
	
	/**This method used for add the breakfast menu info
	 * @throws ParseException **/
	private BreakfastItems addBreakfastMenu(BreakfastModificationReq breakfastModificationReq) throws ParseException{
		BreakfastItems breakfastItems = new BreakfastItems();
		breakfastItems.setBreakfastDate(sdf1.parse(breakfastModificationReq.getBreakfastDate()));
		breakfastItems.setItemDesc(breakfastModificationReq.getDesc());
		breakfastItems.setItemName(breakfastModificationReq.getItemName());
		breakfastItems.setItemType(MealType.BREAKFAST);
		breakfastItems.setPrice(breakfastModificationReq.getPrice());
		breakfastItems.setReducedPrice(breakfastModificationReq.getReducedPrice());
		return breakfastItems;
	}

	/**This method used for get the latest active month**/
	@Override
	public Map<String, String> latestActiveMonth(Long mealSchoolId, Integer schoolYearVal) {
		Map<String, String> resp = new HashMap<String, String>();
		try{
			SchoolYear schoolYear = schoolYearRepository.findByMealSchoolSchoolIdAndSchoolYear(mealSchoolId, schoolYearVal);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String yearMonth = null;
			if(schoolYear != null){
				yearMonth = schoolMealRepository.latestActiveMonth(mealSchoolId, sdf.format(schoolYear.getSessionStartDateTime()), 
						sdf.format(schoolYear.getSessionEndDateTime()));
			}
			resp.put("activeMenuMonth", yearMonth);
			logger.info("Active latest menu month: "+yearMonth);
		}catch(Exception e){
			logger.error("Failed to get the latest active menu month due to "+e.getMessage());
		}		
		return resp;
	}

	/**This method used for get the latest active month**/
	@Override
	public Map<String, String> latestActiveMonthV2(Long mealSchoolId, Integer schoolYearVal) {
		Map<String, String> resp = new HashMap<String, String>();
		try{
			SchoolYear schoolYear = schoolYearRepository.findByMealSchoolSchoolIdAndSchoolYear(mealSchoolId, schoolYearVal);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String yearMonth = null;
			if(schoolYear != null){
				yearMonth = mealCalendarSummaryRepository.latestActiveMonth(mealSchoolId, sdf.format(schoolYear.getSessionStartDateTime()),
						sdf.format(schoolYear.getSessionEndDateTime()));
			}
			resp.put("activeMenuMonth", yearMonth);
			logger.info("Active latest menu month: "+yearMonth);
		}catch(Exception e){
			logger.error("Failed to get the latest active menu month due to "+e.getMessage());
		}
		return resp;
	}

	/**This method used for get the menu items by summary id
	 * @throws Exception **/
	@Override
	public Object getMealSummaryDetails(Long menuSummaryId, Long mealSchoolId, String yearMonth, String grade
			, ItemTypeConstants itemType, Long studentRecId) throws Exception {
		Map<String, MenuSummaryDetailDTO> menusByMonth = new HashMap<String, MenuSummaryDetailDTO>();
		try{			
			if(itemType == null)
				itemType = ItemTypeConstants.Lunch;
			if((menuSummaryId == null || menuSummaryId == 0) && yearMonth == null && grade != null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				Set<BigInteger> summaryIds = new HashSet<>();
				
				if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null && 
						SecurityContextHolder.getContext().getAuthentication().getAuthorities() != null && 
						SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().toUpperCase().contains("ADMIN"))
					summaryIds = summaryRepo.getAllMenus4Admin(mealSchoolId, itemType.toString(), grade, sdf.format(new Date()));
				else{
					LocalDate ld = LocalDate.now();
					ld = ld.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
					String weekYearMonth = ld.getYear()+""+new DecimalFormat("00").format(ld.getMonthValue());
					summaryIds = summaryRepo.getAllMenus(mealSchoolId, itemType.toString(), grade, sdf.format(new Date()), weekYearMonth);
				}
				List<MealCalendarSummary> mealCalendarSummaries = summaryRepo.findByIdIn(summaryIds.stream().map(s -> Long.valueOf(s.toString())).collect(Collectors.toList()));
				MenuSummaryDetailDTO menuSummaryDetailDto = null;
				List<MenuDetailDTO> menuDetailList = null;
				for(MealCalendarSummary summary : mealCalendarSummaries){
					menuSummaryDetailDto = mapMenuSummary(summary);
					List<Object[]> objList = menuItemRepository.getMenuItemsBySummaryId(menuSummaryDetailDto.getId());
					menuDetailList = objList.stream().map(MenuDetailDTO::new).collect(Collectors.toList());
					menuDetailList.sort(Comparator.comparing(MenuDetailDTO::getType));
					menuSummaryDetailDto.setMenuItemsList(menuDetailList);
					menusByMonth.put(summary.getYearMonth(), menuSummaryDetailDto);
				}
			}else{
				MenuSummaryDetailDTO menuSummaryDetailDto = new MenuSummaryDetailDTO();
				List<MenuDetailDTO> menuDetailList = null;//manageMenuDao.getMenuItemsForSummary(menuSummaryId);
				if(menuSummaryId != null && menuSummaryId > 0){
					MealCalendarSummary mealCalendarSummary = mealCalendarSummaryRepository.findOne(menuSummaryId);
					menuSummaryDetailDto = mapMenuSummary(mealCalendarSummary);
				}else{
					List<Object[]> objArray = mealCalendarSummaryRepository.getSummaryInfo(mealSchoolId, yearMonth, grade, itemType.toString());
					if(objArray == null || objArray.get(0) == null)
						throw new Exception("Menu not available.");
					Object[] obj = objArray.get(0);
					menuSummaryDetailDto.setId(Long.parseLong(obj[0].toString()));
					menuSummaryDetailDto.setCutOffDatetime(obj[1] != null ? (Date)obj[1] : null);
					menuSummaryDetailDto.setOrderDateExtensionStatus(obj[2] != null ? (Boolean)obj[2] : false);
					menuSummaryDetailDto.setPublished(obj[3] != null ? (Boolean)obj[3] : false);
					menuSummaryDetailDto.setIsSideSelect(obj[4] != null ? (Boolean)obj[4] : false);
					menuSummaryDetailDto.setIsExtraPreOrder(obj[5] != null ? (Boolean)obj[5] : false);
					menuSummaryDetailDto.setItemPriceDisForMonthlyOrder(obj[6] != null ? Double.valueOf(obj[6].toString()) : null);
					if(!itemType.toString().equalsIgnoreCase("Breakfast") && (menuSummaryDetailDto.getPublished() == null || !menuSummaryDetailDto.getPublished()))
						throw new Exception("Menu not published yet by school!!");
				}
				List<Object[]> objList = menuItemRepository.getMenuItemsBySummaryId(menuSummaryDetailDto.getId());
				menuDetailList = objList.stream().map(MenuDetailDTO::new).collect(Collectors.toList());
				menuDetailList.sort(Comparator.comparing(MenuDetailDTO::getType));
				menuSummaryDetailDto.setMenuItemsList(menuDetailList);
				if(menuSummaryId != null && menuSummaryId > 0)
					return menuSummaryDetailDto;
				else
					menusByMonth.put(yearMonth, menuSummaryDetailDto);
				
			}
			Map<String, MenuSummaryDetailDTO> orderMenuByMonth = new HashMap<String, MenuSummaryDetailDTO>();
			for(Entry<String, MenuSummaryDetailDTO> entry : menusByMonth.entrySet()){
				MenuSummaryDetailDTO menuSummaryDetailDTO = mealManageAPIService.getOrderedMealsInfo(studentRecId, entry.getKey(), itemType);
				if(menuSummaryDetailDTO != null && menuSummaryDetailDTO.getMenuItemsList() != null && menuSummaryDetailDTO.getMenuItemsList().size() > 0)
					orderMenuByMonth.put(entry.getKey(), menuSummaryDetailDTO);
			}
			Map<String, Object> menuByType = new HashMap<String, Object>();
			menuByType.put("availableMenu", menusByMonth);
			menuByType.put("orderedMenu", orderMenuByMonth);
			return menuByType;
		}catch(Exception e){
			logger.error("Failed to get the menu items by summary id::"+menuSummaryId+" due to "+e.getMessage());
			throw new Exception("Failed to get menu items.");
		}
	}

	@Override
	public ServiceResponse schoolMealsCreateV2(ItemTypeConstants menuType, MealsRequest mealsRequest) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
			String finalGradesName = gradeFormatBuild.getGradesFromSet(mealsRequest.getGradesList());
			serviceResponse = manageMenuDao.schoolMealsCreateV2(menuType,mealsRequest, finalGradesName);
		}catch(Exception e){
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to create Menu. Please try again later!");
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	@Override
	public ServiceResponse allMasterItemsByCategory(Long mealSchoolId, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			Map<MealType, List<MenuItemInfo>> menuByCategory = new HashMap<MealType, List<MenuItemInfo>>();
			List<String> types = null;
			List<MenuItemInfo> menuDetailList = null;
			if(menuType.toString().equalsIgnoreCase("Lunch"))
				types = Arrays.asList(MealType.MEAL.toString(),MealType.HOLIDAY.toString(), MealType.SIDE.toString(), MealType.EXTRA.toString());
			else
				types = Arrays.asList(MealType.BREAKFAST.toString(),MealType.HOLIDAY.toString(), MealType.SIDE.toString());
			List<Object[]> objList = menuItemRepository.getAllMasterItems(mealSchoolId, types);
			menuDetailList = objList.stream().map(MenuItemInfo::new).collect(Collectors.toList());
			menuDetailList.sort(Comparator.comparing(MenuItemInfo::getTitle));
			if(menuDetailList != null)
				menuByCategory = menuDetailList.stream().collect(Collectors.groupingBy(
	    				MenuItemInfo::getType));
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved all master menu items by category.");
			serviceResponse.setResponse(menuByCategory);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to get all master menu item by category for mealSchoolId::"+mealSchoolId+" and menuType::"+menuType);
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for read excel file and build json data**/
	@Override
	@Transactional
	public ServiceResponse menuJson(MultipartFile file, Long mealSchoolId, Set<SchoolGrades> gradeNames,
			String yearMonth, Long masterRecId, ItemTypeConstants menuType, Boolean isExtraPreOrder) {
		logger.info("Reading file and building data into Json format for "+menuType.toString()+" items");
		ServiceResponse serviceResponse = new ServiceResponse();
		MealCreateJson mealCreateJson = null;
		String mealJsonData = null;
		try{
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			//String finalGradesName = getGradesFromSet(gradeNames);	
			Date monthStartDate = new SimpleDateFormat("yyyyMMdd").parse(yearMonth+""+01);
			List<SchoolHoliday> schoolHolidays = mealManageAPIService.schoolHolidays(mealSchoolId, monthStartDate, 
					lastDayOfMonth(monthStartDate));
			mealCreateJson = menuItemsJsonAndFile(file, mealSchoolId, yearMonth, gradeNames, schoolHolidays, masterRecId, menuType, isExtraPreOrder);
			mealJsonData = objectMapper.writeValueAsString(mealCreateJson.getMealItems());
			serviceResponse.setMealJsonData(mealJsonData.replace("\"new Date(", "new Date(").replace("')'\"", ")"));
			serviceResponse.setStatus(mealCreateJson.getStatus());
			serviceResponse.setStatusCode(mealCreateJson.getStatusCode());
			serviceResponse.setErrorMessage(mealCreateJson.getErrorMessage());
			serviceResponse.setStatusMessage(mealCreateJson.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to import the menu file due to "+e.getMessage());
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to import the Menu file. Please check file and try again later!!");
		}
		return serviceResponse;
	}
	
	/**This method used for map the menu items**/
	private List<MealMenu> prepareMenu(List<MenuDetailDTO> menus){
		List<MealMenu> mealMenus = new ArrayList<MealMenu>();
		MealMenu mealMenu = null;
		for(MenuDetailDTO menuDetailDTO : menus){
			mealMenu = new MealMenu();
			mealMenu.setPrice(menuDetailDTO.getPrice());
			mealMenu.setReducedPrice(menuDetailDTO.getReducedPrice());
			mealMenu.setTitle(menuDetailDTO.getName());
			mealMenu.setType(menuDetailDTO.getType());
			mealMenu.setDesc(menuDetailDTO.getDesc());
			mealMenu.setStart(menuDetailDTO.getDate());
			mealMenus.add(mealMenu);
		}
		return mealMenus;
	}
	
	/**This method used for map the menu summary data**/
	private MenuSummaryDetailDTO mapMenuSummary(MealCalendarSummary mealCalendarSummary){
		MenuSummaryDetailDTO menuSummaryDetailDto = new MenuSummaryDetailDTO();
		menuSummaryDetailDto.setId(mealCalendarSummary.getId());
		menuSummaryDetailDto.setPdfLink(mealCalendarSummary.getPdfLink());
		menuSummaryDetailDto.setAutoReminderDate1(mealCalendarSummary.getAutoReminderDate1());
		menuSummaryDetailDto.setAutoReminderDate2(mealCalendarSummary.getAutoReminderDate2());
		menuSummaryDetailDto.setGrades(mealCalendarSummary.getGrades());
		menuSummaryDetailDto.setOrderDateExtensionStatus(mealCalendarSummary.getOrderDateExtensionStatus());
		menuSummaryDetailDto.setPublished(mealCalendarSummary.getIsPublished());
		menuSummaryDetailDto.setYearMonth(mealCalendarSummary.getYearMonth());
		menuSummaryDetailDto.setIsSideSelect(mealCalendarSummary.getIsSideSelect());
		menuSummaryDetailDto.setAllowOrderNDaysBefore(mealCalendarSummary.getAllowOrderNDaysBefore());
		menuSummaryDetailDto.setCutOffType(mealCalendarSummary.getCutOffType());
		menuSummaryDetailDto.setCutOffDatetime(mealCalendarSummary.getCutOffDateTime());
		menuSummaryDetailDto.setWeeklyOrderCutOffDay(mealCalendarSummary.getWeeklyOrderCutOffDay());
		menuSummaryDetailDto.setWeeklyOrderCutOffTime(mealCalendarSummary.getWeeklyOrderCutOffTime());
		menuSummaryDetailDto.setIsExtraPreOrder(mealCalendarSummary.getIsExtraPreOrder());
		menuSummaryDetailDto.setItemPriceDisForMonthlyOrder(mealCalendarSummary.getItemPriceDisForMonthlyOrder());
		return menuSummaryDetailDto;
	}

	@Override
	public ServiceResponse getItemLocations(Long mealSchoolId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			List<Object[]> objArray = menuItemRepository.getItemPOSLocations(mealSchoolId,sdf.format(new Date()));
			List<ItemPosLocation> locations = new ArrayList<>();
			ItemPosLocation location = null;
			for(Object[] obj : objArray){
				location = new ItemPosLocation();
				location.setItemId(obj[0] != null ? Long.parseLong(obj[0].toString()) : 0);
				location.setItemName(obj[1] != null ? obj[1].toString() : "");
				location.setLocationIds(obj[2] != null ? Arrays.stream(obj[2].toString().split(",")).map(Long::parseLong).collect(Collectors.toList()) : null);
				locations.add(location);
			}
			serviceResponse.setResponse(locations);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Items POS locations retrieved successfully.");
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get items POS locations.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	@Override
	public ServiceResponse saveItemLocations(Long mealSchoolId, List<ItemPosLocation> locations) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<PosLocation> posLocs = posLocationRepo.findByMealSchoolSchoolIdAndIsActive(mealSchoolId, true);
			Map<Long, List<PosLocation>> posLocById = posLocs.stream().collect(Collectors.groupingBy(
    				PosLocation::getId));
			Map<Long, List<ItemPosLocation>> itemLocById = locations.stream().collect(Collectors.groupingBy(ItemPosLocation::getItemId));
			List<MenuItem> items = menuItemRepository.findByIdIn(itemLocById.keySet());
			Set<PosLocation> locs = null;
			for(MenuItem m : items){
				locs = new HashSet<>();
				for(Long l : itemLocById.get(m.getId()).get(0).getLocationIds()){
					locs.add(posLocById.get(l).get(0));
				}
				m.setLocations(locs);
				menuItemRepository.save(m);
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Items POS locations mapping processed successfully.");
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to map items POS locations.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	@Override
	public ServiceResponse nutritionInfo(Long mealSchoolId, List<NutritionAudit> nutritionAudits) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			for(NutritionAudit nutritionAudit : nutritionAudits){
				nutritionAuditRepo.save(nutritionAudit);
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Nutrition info updated successfully.");
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Nutrition info failed to update.");	
		}
		return serviceResponse;		
	}

	/**Send the link to parent for updated order**/
	/*private void buildPdfAndSendToParentForMenuUpdate(MealSchool mealSchool, Set<MealOrderDetails> mealOrderDetailsList, 
			String loggedUser) throws Exception{
		String schoolName = mealSchool.getSchoolName();
		String logoLink = mealSchool.getLogoLink();
		String schoolTimezone = mealSchool.getSchoolTimezone().toString();
		Boolean priEmailIsSubscribe = null;
		Boolean altEmailIsSubscribe = null;
		UsersAuthInfo usersAuthInfo = null;
		for(MealOrderDetails mealOrderDetails : mealOrderDetailsList){
			ParentUser parentUser = mealOrderDetails.getStudentUser().getParentuser();
			if(parentUser.getUserName() != null){
				usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getUserName());
				priEmailIsSubscribe = usersAuthInfo.getEmailIsSubscribe();
			}
			if(parentUser.getParentAltEmail() != null && !parentUser.getParentAltEmail().trim().equalsIgnoreCase("")){
				usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getParentAltEmail());
				altEmailIsSubscribe = usersAuthInfo.getEmailIsSubscribe();
			}
			orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, schoolName, loggedUser, logoLink, 
								parentUser, priEmailIsSubscribe, altEmailIsSubscribe, schoolTimezone);
		}
	}*/
	
}
