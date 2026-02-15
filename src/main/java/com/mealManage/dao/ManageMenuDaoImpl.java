package com.mealManage.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.MealSummaryUpdateReq;
import com.mealManage.domain.MealsRequest;
import com.mealManage.domain.MenuModificationReq;
import com.mealManage.domain.PublishedMenuNotifReq;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealMenu;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.meal.MealsExcelSummary;
import com.mealManage.mealmodel.meal.SchoolMeal;
import com.mealManage.mealmodel.meal.SchoolMealSummary;
import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.MealCalendarSummaryRepository;
import com.mealManage.mealmodel.repository.MealOrderDetailsRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.MealsExcelSummaryRepository;
import com.mealManage.mealmodel.repository.MenuItemRepository;
import com.mealManage.mealmodel.repository.SchoolMealRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.school.SchoolType;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.mealschedule.entities.MealCalendar;
import com.mealManage.mealschedule.entities.MealCalendarSummary;
import com.mealManage.menu.entities.MenuItem;
import com.mealManage.response.MealCreateJson;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.ServiceResponse;
import com.mealManage.util.AWSUtility;
import com.mealManage.util.BreakfastMenuPdfUtilityV2;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.DateUtility;
import com.mealManage.util.ExcelReadUtil;
import com.mealManage.util.MealMenuPdfUtility;
import com.mealManage.util.SendNotificationUtil;

@Transactional
@Repository
@SuppressWarnings("unchecked")
/**This class implementing the MealManageAPIDao interface**/
public class ManageMenuDaoImpl implements ManageMenuDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired 
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private ExcelReadUtil excelReadUtil;
	@Autowired
	private SchoolMealRepository schoolMealsRepo;
	@Autowired
	private MealsExcelSummaryRepository mealsExcelSummaryRepository;
	@Autowired
	private MealMenuPdfUtility mealMenuPdfUtility;
	@Autowired
	private AWSUtility awsUtility;
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	@Autowired
	private MealOrderDetailsRepository mealOrderDetailsRepository;
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
    private MealCalendarSummaryRepository mealCalendarSummaryRepository;
	@Autowired
	private BreakfastMenuPdfUtilityV2 breakfastMenuPdfUtilityV2;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**This method used for create single or multiple school meals along with Meal Menu**/
	@SuppressWarnings("unused")
	@Override
	public ServiceResponse schoolMealsCreate(MealsRequest mealsRequest, String gradesName) {
		ServiceResponse serviceResponse = new ServiceResponse();
		String status = "";
		Boolean gradeStatus = true;
		try{
			Set<SchoolMeal> schoolMealList = new HashSet<SchoolMeal>();
			List<MealMenu> menuItems = new ArrayList<MealMenu>();
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealsRequest.getMealSchoolId());
			Boolean isItemized = CommonUtil.checkItemized(mealSchool);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
			List<String> gradesList1 = new ArrayList<String>();
			String countryCode = mealSchool.getCountryCode();
			SchoolMealSummary mealSummary = null;
			List<String> previousGrades = null;
			String gradeFailed = "";
			for(SchoolType type : mealSchool.getSchool().getSchoolType()){
				gradesList1.addAll(type.getValues());
			}
			if(mealsRequest.getSchoolMealSummaryId() != null && mealsRequest.getSchoolMealSummaryId() != 0)
				mealSummary = entityManager.find(SchoolMealSummary.class, mealsRequest.getSchoolMealSummaryId());
			if(mealSummary != null)
				previousGrades =  Arrays.asList(mealSummary.getGradeNames().split(","));
			List<String> mealCreatedGradeList = mealCreatedGrades(mealsRequest.getMealSchoolId(), mealsRequest.getYearMonth());
			//Date monthStartDate = new SimpleDateFormat("yyyyMMdd").parse(yearMonth+""+01);
			//Date monthEndDate = lastDayOfMonth(monthStartDate);
			for(SchoolGrades grade : mealsRequest.getGradesList()){
				if(gradesList1.contains(grade.toString()) && 
						((mealsRequest.getSchoolMealSummaryId() != null && mealsRequest.getSchoolMealSummaryId() != 0 
						&& (previousGrades.contains(grade.toString()) || !mealCreatedGradeList.contains(grade.toString()))) || 
								!mealCreatedGradeList.contains(grade.toString()))){
					
				}else{
					gradeStatus = false;
					gradeFailed = grade.toString();
					break;
				}
			}
			if(gradeStatus){
			schoolMealList = schoolMealsRepo.findByMealSchoolSchoolIdAndYearMonthAndGradesInAndIsDelete(mealsRequest.getMealSchoolId(),
					mealsRequest.getYearMonth(), new ArrayList<>(mealsRequest.getGradesList()), false);
			if(schoolMealList.size() < 1){
				SchoolMealSummary schoolMealSummary = new SchoolMealSummary();
				schoolMealSummary.setCreatedBy(mealsRequest.getLoggedUser());
				schoolMealSummary.setCreatedOn(new Date());
				if(mealsRequest.getCutOffDateTime() != null)
					schoolMealSummary.setCutOffDateTime(mealsRequest.getCutOffDateTime());
				else{
					DateUtility dateUtility = new DateUtility();
					schoolMealSummary.setCutOffDateTime(dateUtility.add15daysToCurrentDate());
				}
				schoolMealSummary.setYearMonth(mealsRequest.getYearMonth());
				schoolMealSummary.setMealsPdfLink("");
				schoolMealSummary.setMealSchool(mealSchool);
				schoolMealSummary.setGradeNames(gradesName);
				schoolMealSummary.setReducedPriceStatus(mealsRequest.getReducedPriceStatus());
				schoolMealSummary.setOrderDateExtensionStatus(mealsRequest.getOrderDateExtensionStatus());
				schoolMealSummary.setAutoReminderDate1(mealsRequest.getAutoReminderDate1());
				schoolMealSummary.setAutoReminderDate2(mealsRequest.getAutoReminderDate2());
				entityManager.persist(schoolMealSummary);
				
				String mealMenuPdfLink = mealMenuPdfUtility.mealMenuPdfFinalLink(mealsRequest.getMealSchoolId(), 
						mealsRequest.getYearMonth(), schoolMealSummary.getSchoolId(), true);
				schoolMealSummary.setMealsPdfLink(mealMenuPdfLink);
				entityManager.merge(schoolMealSummary);
				for(MealMenu mealMenu : mealsRequest.getMealMenus()){
					SchoolMeal schoolMeal = new SchoolMeal();
					schoolMeal.setLoggedUser(mealsRequest.getLoggedUser());
					mealMenu.setCreatedBy(schoolMeal.getLoggedUser());
					mealMenu.setCreatedOn(new Date());
					/*if(mealMenu.getStart() != null)
						mealMenu.setStart(getDate(mealMenu.getStart()));*/
					mealMenu.setStart(mealMenu.getStart());
					schoolMeal.setMealMenu(mealMenu);
					schoolMeal.setMealSchool(mealSchool);
					schoolMeal.setGrades(mealsRequest.getGradesList());
					schoolMeal.setYearMonth(mealsRequest.getYearMonth());	
					schoolMeal.setSchoolMealSummary(schoolMealSummary);
					schoolMealsRepo.save(schoolMeal);
					menuItems.add(mealMenu);
				}
				mealMenuPdfUtility.mealMenuPdf(menuItems, schoolMealSummary,currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
				serviceResponse.setStatusMessage("Menu created Successfully.");
				logger.info("Menu created Successfully");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatus("Success");
			}else if(mealsRequest.getSchoolMealSummaryId() != null && mealsRequest.getSchoolMealSummaryId() != 0){
				schoolMealList = schoolMealsRepo.findBySchoolMealSummarySchoolIdAndIsDelete(mealsRequest.getSchoolMealSummaryId(), 
						false);
				SchoolMealSummary schoolMealSummary = null;
				if(schoolMealList != null && schoolMealList.size() > 0){
					schoolMealSummary = schoolMealList.stream().findFirst().get().getSchoolMealSummary();
					if(schoolMealSummary.getIsPublished() == null || !schoolMealSummary.getIsPublished()){
							for(SchoolMeal schoolMeal : schoolMealList){
								schoolMealsRepo.delete(schoolMeal);
							}
							schoolMealSummary.setModifiedBy(mealsRequest.getLoggedUser());
							schoolMealSummary.setModifiedOn(new Date());
							if(mealsRequest.getCutOffDateTime() != null)
								schoolMealSummary.setCutOffDateTime(mealsRequest.getCutOffDateTime());
							schoolMealSummary.setYearMonth(mealsRequest.getYearMonth());
							schoolMealSummary.setGradeNames(gradesName);
							schoolMealSummary.setReducedPriceStatus(mealsRequest.getReducedPriceStatus());
							schoolMealSummary.setOrderDateExtensionStatus(mealsRequest.getOrderDateExtensionStatus());
							schoolMealSummary.setAutoReminderDate1(mealsRequest.getAutoReminderDate1());
							schoolMealSummary.setAutoReminderDate2(mealsRequest.getAutoReminderDate2());
							entityManager.persist(schoolMealSummary);
							
							String mealMenuPdfLink = mealMenuPdfUtility.mealMenuPdfFinalLink(mealsRequest.getMealSchoolId(), 
									mealsRequest.getYearMonth(), schoolMealSummary.getSchoolId(), true);
							schoolMealSummary.setMealsPdfLink(mealMenuPdfLink);
							entityManager.merge(schoolMealSummary);
							for(MealMenu mealMenu : mealsRequest.getMealMenus()){
								SchoolMeal schoolMeal = new SchoolMeal();
								schoolMeal.setLoggedUser(mealsRequest.getLoggedUser());
								mealMenu.setCreatedBy(schoolMeal.getLoggedUser());
								mealMenu.setCreatedOn(new Date());
								/*if(mealMenu.getStart() != null)
									mealMenu.setStart(getDate(mealMenu.getStart()));*/
								mealMenu.setStart(mealMenu.getStart());
								schoolMeal.setMealMenu(mealMenu);
								schoolMeal.setMealSchool(mealSchool);
								schoolMeal.setGrades(mealsRequest.getGradesList());
								schoolMeal.setYearMonth(mealsRequest.getYearMonth());	
								schoolMeal.setSchoolMealSummary(schoolMealSummary);
								schoolMealsRepo.save(schoolMeal);
								menuItems.add(mealMenu);
							}
							mealMenuPdfUtility.mealMenuPdf(menuItems, schoolMealSummary,currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
							serviceResponse.setStatusMessage("Menu updated successfully.");
							logger.info("Menu updated successfully");
							serviceResponse.setStatusCode(200);
							serviceResponse.setStatus("Success");
						}else{
						serviceResponse.setStatusMessage("Menu can not update as it is already published by school.");
						logger.info("Menu can not update as it is already published by school");
						serviceResponse.setStatusCode(409);
						serviceResponse.setStatus("Failed");
					}
			}else{
				serviceResponse.setStatusMessage("There are no menu items for update.");
				logger.info("There are no menu items for update");
				serviceResponse.setStatusCode(409);
				serviceResponse.setStatus("Failed");
			}		
			}else{
				logger.info("Menu can not create as it is already created by school for the selected grades.");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Menu can not create as it is already created by school for the selected grades.");
			}			
			}else{
				serviceResponse.setStatus("Failed");
				Map<String, String> gradeKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode);
				if(!gradesList1.contains(gradeFailed)){
					serviceResponse.setStatusMessage("Selected Grade "+gradeKeyVal.get(gradeFailed)+" does not belong to this School.");
				}else if((mealsRequest.getSchoolMealSummaryId() == null || mealsRequest.getSchoolMealSummaryId() == 0) 
						&& mealCreatedGradeList.contains(gradeFailed)){
					serviceResponse.setStatusMessage("Menu already created for the selected Grade "+gradeKeyVal.get(gradeFailed)+".");
				}else if(mealsRequest.getSchoolMealSummaryId() != null && mealsRequest.getSchoolMealSummaryId() != 0 && (!previousGrades.contains(gradeFailed) || 
						mealCreatedGradeList.contains(gradeFailed))){
					serviceResponse.setStatusMessage("Menu can not update for the selected Grade "+gradeKeyVal.get(gradeFailed)+" as it is belong to other Menu.");
				}
				logger.info(serviceResponse.getStatusMessage());
				serviceResponse.setStatusCode(422);
			}
		}catch(Exception e){	
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusMessage("Failed to create Menu. Please try again later!");
		}
		return serviceResponse;
	}

	//This API will be used to create the menu, and editing the menu.
	//Editing menu is not possible if the menu is already published
	@Override
	public ServiceResponse schoolMealsCreateV2(ItemTypeConstants menuType, MealsRequest mealsRequest, String finalGradesName) throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();
		Boolean gradeStatus = true;
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealsRequest.getMealSchoolId());
		Boolean isItemized =  CommonUtil.checkItemized(mealSchool);
		String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
		List<String> gradesList1 = new ArrayList<String>();
		String countryCode = mealSchool.getCountryCode();
		MealCalendarSummary mealSummary = null;
		List<String> previousGrades = null;
		String gradeFailed = "";
		String loggedUser = "";
		if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
			loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
		for(SchoolType type : mealSchool.getSchool().getSchoolType()){
			gradesList1.addAll(type.getValues());
		}

		if(mealsRequest.getSchoolMealSummaryId() != null && mealsRequest.getSchoolMealSummaryId() != 0)
			mealSummary = entityManager.find(MealCalendarSummary.class, mealsRequest.getSchoolMealSummaryId());
		if(mealSummary != null)
			previousGrades =  mealSummary.getGrades().stream().map(schoolGrade -> schoolGrade.toString()).collect(Collectors.toList());
		List<String> mealCreatedGradeList = retrieveMenuScheduledGrades(mealsRequest.getMealSchoolId(), mealsRequest.getYearMonth(),menuType);
		//Date monthStartDate = new SimpleDateFormat("yyyyMMdd").parse(yearMonth+""+01);
		//Date monthEndDate = lastDayOfMonth(monthStartDate);
		for(SchoolGrades grade : mealsRequest.getGradesList()){
			if(gradesList1.contains(grade.toString()) &&
					((mealsRequest.getSchoolMealSummaryId() != null && mealsRequest.getSchoolMealSummaryId() != 0 && previousGrades != null
							&& (previousGrades.contains(grade.toString()) || !mealCreatedGradeList.contains(grade.toString()))) ||
							!mealCreatedGradeList.contains(grade.toString()))){
			}else{
				gradeStatus = false;
				gradeFailed = grade.toString();
				break;
			}
		}
		String mealMenuPdfLink = null;
		if (gradeStatus) {
			// Identify whether it is create request or edit request and If create request do the below
			if (mealsRequest.getSchoolMealSummaryId() == null) { // new menu calendar summary case
				// If summaryId is available, and it is aleady published restrict editing. Otherwise find and either create or update the menu items
				// and update the menu summary To do that
				if (mealsRequest != null && !CollectionUtils.isEmpty(mealsRequest.getMealMenus())) { 
					// Long schoolId = mealsRequest.getMealSchoolId();
					Set<MealCalendar> mealCalendarList = upsertMenuItemAndPrepareMenuCalendar(menuType, mealsRequest, mealSchool, loggedUser);
					// By here all the mealMenus will have id associated. Prepare the menucalendarsummary and persist
					MealCalendarSummary summary = new MealCalendarSummary();
					summary.setCreatedBy(loggedUser);
					summary.setCreatedOn(new Date());
					summary.setSchool(mealSchool);//mealsRequest.getMealSchoolId();
					summary.setYearMonth(mealsRequest.getYearMonth());
					summary.setMealType(menuType);
					summary.setSchoolYear(mealsRequest.getSchoolYear());
					summary = prepareMealCalendarSummary(summary, mealsRequest, mealCalendarList, loggedUser);
					summary.setMealByDays(mealCalendarList);
					summary = mealCalendarSummaryRepository.save(summary);
					/*if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Lunch.toString()))
						mealMenuPdfLink = mealMenuPdfUtility.mealMenuPdfFinalLink(mealsRequest.getMealSchoolId(),
							mealsRequest.getYearMonth(), summary.getId(), true);
					else */
					/*if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Breakfast.toString()))
						mealMenuPdfLink = mealMenuPdfUtility.breakfastMenuPdfFinalLink(mealsRequest.getMealSchoolId(),
								mealsRequest.getYearMonth(), summary.getId(), true);
					else*/
						mealMenuPdfLink = mealMenuPdfUtility.mealMenuPdfFinalLink(mealsRequest.getMealSchoolId(),
								mealsRequest.getYearMonth(), summary.getId(), true);
					summary.setPdfLink(mealMenuPdfLink);
					entityManager.merge(summary);
					/*if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Lunch.toString())){
						SchoolMealSummary schoolMealSummary = prepareCalendarSummary(summary);
						mealMenuPdfUtility.mealMenuPdf(mealsRequest.getMealMenus(), schoolMealSummary, currencySymbol);
					}else */
					/*if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Breakfast.toString()))
						breakfastMenuPdfUtilityV2.breakfastMenuPdf(summary, currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
					else{*/
						SchoolMealSummary schoolMealSummary = prepareCalendarSummary(summary);
						mealMenuPdfUtility.mealMenuPdf(mealsRequest.getMealMenus(), schoolMealSummary, currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
					//}
					serviceResponse.setStatusMessage("Menu Created successfully.");
					logger.info(serviceResponse.getStatusMessage() + " for month::" + mealsRequest.getYearMonth()
							+ " and schoolId::" + mealsRequest.getMealSchoolId());
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatus("Success");
				}
			} else { // Handle update menu calendar summary case and Fetch the publish status. If its published , throw error response
					MealCalendarSummary existingSummary = mealCalendarSummaryRepository.findOne(mealsRequest.getSchoolMealSummaryId());
					if (existingSummary != null && !existingSummary.getIsPublished()) {
						// Iterate and create new menu items
						Set<MealCalendar> mealCalendarList = upsertMenuItemAndPrepareMenuCalendar(menuType,
								mealsRequest, mealSchool, loggedUser);
						existingSummary.setModifiedOn(new Date());
						existingSummary.setModifiedBy(loggedUser);
						existingSummary = prepareMealCalendarSummary(existingSummary, mealsRequest, mealCalendarList, loggedUser);
						/*if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Lunch.toString()))
							mealMenuPdfLink = mealMenuPdfUtility.mealMenuPdfFinalLink(
									mealsRequest.getMealSchoolId(), mealsRequest.getYearMonth(),
									existingSummary.getId(), true);
						else */if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Breakfast.toString()))
							mealMenuPdfLink = mealMenuPdfUtility.breakfastMenuPdfFinalLink(mealsRequest.getMealSchoolId(),
									mealsRequest.getYearMonth(), existingSummary.getId(), true);
						else
							mealMenuPdfLink = mealMenuPdfUtility.mealMenuPdfFinalLink(
									mealsRequest.getMealSchoolId(), mealsRequest.getYearMonth(),
									existingSummary.getId(), true);
						existingSummary.setPdfLink(mealMenuPdfLink);
						existingSummary.getMealByDays().clear();
						existingSummary.getMealByDays().addAll(mealCalendarList);
						//existingSummary.setMealByDays(mealCalendarList);
						entityManager.merge(existingSummary);
						/*if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Lunch.toString())){
							SchoolMealSummary schoolMealSummary = prepareCalendarSummary(existingSummary);
							mealMenuPdfUtility.mealMenuPdf(mealsRequest.getMealMenus(), schoolMealSummary, currencySymbol);
						}else */if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Breakfast.toString()))
							breakfastMenuPdfUtilityV2.breakfastMenuPdf(existingSummary, currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
						else{
							SchoolMealSummary schoolMealSummary = prepareCalendarSummary(existingSummary);
							mealMenuPdfUtility.mealMenuPdf(mealsRequest.getMealMenus(), schoolMealSummary, currencySymbol, isItemized,CommonUtil.getNonSchoolDays(mealSchool));
						}						
						serviceResponse.setStatusMessage("Menu Updated successfully.");
						logger.info(serviceResponse.getStatusMessage() + " for month::"
								+ mealsRequest.getYearMonth() + " and schoolId::" + mealsRequest.getMealSchoolId());
						serviceResponse.setStatusCode(200);
						serviceResponse.setStatus("Success");
					} else {
						if(existingSummary != null){
							serviceResponse.setStatusMessage("Menu can not update as it is already published by school.");
							logger.info(serviceResponse.getStatusMessage() + " for month::"
									+ mealsRequest.getYearMonth() + " and schoolId::" + mealsRequest.getMealSchoolId());
						}else{
							serviceResponse.setStatusMessage("Calendar does not exist.");
							logger.info(serviceResponse.getStatusMessage()+" with summaryId::"+mealsRequest.getSchoolMealSummaryId());
						}
						serviceResponse.setStatusCode(409);
						serviceResponse.setStatus("Failed");
					}
				}
		} else {
			serviceResponse.setStatus("Failed");
			Map<String, String> gradeKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode);
			if (!gradesList1.contains(gradeFailed))
				serviceResponse.setStatusMessage("Selected Grade " + gradeKeyVal.get(gradeFailed) + " does not belong to this School.");
			else if ((mealsRequest.getSchoolMealSummaryId() == null || mealsRequest.getSchoolMealSummaryId() == 0)
					&& mealCreatedGradeList.contains(gradeFailed))
				serviceResponse.setStatusMessage("Menu already created for the selected Grade " + gradeKeyVal.get(gradeFailed) + ".");
			else if (mealsRequest.getSchoolMealSummaryId() != null && mealsRequest.getSchoolMealSummaryId() != 0
					&& (!previousGrades.contains(gradeFailed) || mealCreatedGradeList.contains(gradeFailed)))
				serviceResponse.setStatusMessage("Menu can not update for the selected Grade "+ gradeKeyVal.get(gradeFailed) + " as it is belong to other Menu.");
			logger.info(serviceResponse.getStatusMessage());
			serviceResponse.setStatusCode(422);
		}
		return serviceResponse;
	}

	/**This method used for create master menu if not available during meal calendar creation**/
	private Set<MealCalendar> upsertMenuItemAndPrepareMenuCalendar(ItemTypeConstants menuType, MealsRequest mealsRequest, MealSchool mealSchool, String loggedUser) {
		Set<MealCalendar> mealCalendarList = new HashSet<>();
		//Long schoolId = upsertMenuItemAndPrepareMenuCalendar(menuType, mealsRequest, mealSchool, mealCalendarList);
		List<MealType> types = null;
		if(menuType.toString().equalsIgnoreCase("Lunch"))
			types = Arrays.asList(MealType.MEAL,MealType.HOLIDAY, MealType.SIDE, MealType.EXTRA);
		else if(menuType.toString().equalsIgnoreCase("Breakfast"))
			types = Arrays.asList(MealType.BREAKFAST,MealType.HOLIDAY, MealType.SIDE, MealType.EXTRA);
		else if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Snack.toString()))
			types = Arrays.asList(MealType.SNACK,MealType.HOLIDAY, MealType.SIDE, MealType.EXTRA);
		else if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Dinner.toString()))
			types = Arrays.asList(MealType.DINNER,MealType.HOLIDAY, MealType.SIDE, MealType.EXTRA);
		List<MenuItem> availableMenuItems = menuItemRepository.findActiveMenuItemsBySchoolAndTypes(mealsRequest.getMealSchoolId(), types);
		mealsRequest.getMealMenus().stream().forEach(mealMenu -> {
			MealCalendar calendar = new MealCalendar();
			calendar.setActive(true);
			calendar.setDate(mealMenu.getStart());
			calendar.setPrice(mealMenu.getPrice());
			calendar.setReducedPrice(mealMenu.getReducedPrice());
			//Query menuItem table
			Optional<MenuItem> menuItemOptional = availableMenuItems.stream().filter(menuItem
					-> (menuItem.getName().trim().equalsIgnoreCase(mealMenu.getTitle().trim()) && 
							mealMenu.getType().toString().equalsIgnoreCase(menuItem.getCategory().toString()))).findFirst();
			if(!menuItemOptional.isPresent()){
				//Prepare MenuItem and Save
				MenuItem menuItem = prepareMenuItem(mealSchool, mealMenu, loggedUser);
				//mealMenu.get;
				menuItemRepository.save(menuItem);
				//Update the menuItemid to the mealMenu
				mealMenu.setMealId(menuItem.getId());
				//Add the saved entity to the list
				availableMenuItems.add(menuItem);
				calendar.setMenuItem(menuItem);
			}
			else {
				MenuItem dbMenuItem = menuItemOptional.get();
				if(mealMenu.getDesc() != null && (dbMenuItem.getLongDescription() == null || !dbMenuItem.getLongDescription().equalsIgnoreCase(mealMenu.getDesc()))){
					dbMenuItem.setLongDescription(mealMenu.getDesc());
					dbMenuItem.setModifiedOn(new Date());
					dbMenuItem.setModifiedBy(loggedUser);
					menuItemRepository.save(dbMenuItem);
				}
				mealMenu.setMealId(dbMenuItem.getId());
				calendar.setMenuItem(dbMenuItem);
			}
			mealCalendarList.add(calendar);
		});
		return mealCalendarList;
	}

	/**This method used for prepare the meal calendar summary**/
	private MealCalendarSummary prepareMealCalendarSummary(MealCalendarSummary summary, MealsRequest mealsRequest, Set<MealCalendar> mealCalendarList, String loggedUser) {
		summary.setAutoReminderDate1(mealsRequest.getAutoReminderDate1());
		summary.setAutoReminderDate2(mealsRequest.getAutoReminderDate2());
		summary.setGrades(mealsRequest.getGradesList());
		summary.setOrderDateExtensionStatus(mealsRequest.getOrderDateExtensionStatus());
		summary.setReducedPriceStatus(mealsRequest.getReducedPriceStatus());
		summary.setCutOffDateTime(mealsRequest.getCutOffDateTime());
		summary.setLoggedUser(loggedUser);
		summary.setIsSideSelect(mealsRequest.getIsSideSelect());
		summary.setAllowOrderNDaysBefore(mealsRequest.getAllowOrderNDaysBefore());
		if(mealsRequest.getCutOffType() != null && mealsRequest.getCutOffType().equalsIgnoreCase("R") 
				&& mealsRequest.getAllowOrderNDaysBefore() == null)
			summary.setAllowOrderNDaysBefore(0);
		summary.setCutOffType(mealsRequest.getCutOffType());
		summary.setWeeklyOrderCutOffDay(mealsRequest.getWeeklyOrderCutOffDay());
		summary.setWeeklyOrderCutOffTime(mealsRequest.getWeeklyOrderCutOffTime());
		summary.setIsExtraPreOrder(mealsRequest.getIsExtraPreOrder());
		summary.setItemPriceDisForMonthlyOrder(mealsRequest.getItemPriceDisForMonthlyOrder());
		summary.setExtraEnableForCaterer(mealsRequest.isExtraEnableForCaterer());
		return summary;
	}

	private MenuItem prepareMenuItem(MealSchool mealSchool, MealMenu mealMenu, String loggedUser) {
		MenuItem menuItem = new MenuItem();
		menuItem.setName(mealMenu.getTitle());
		menuItem.setCategory(mealMenu.getType());
		//menuItem.setCategoryType(mealMenu.getType().toString());
		menuItem.setImageUrl(mealMenu.getMealImage());
		menuItem.setLongDescription(mealMenu.getDesc());
		menuItem.setShortDescription(mealMenu.getMealShortDesc());
		/*menuItem.setPrice(mealMenu.getPrice());
		menuItem.setReducedPrice(mealMenu.getReducedPrice());*/
		menuItem.setActive(true);
		menuItem.setSchoolDetails(mealSchool);
		//menuItem.setmealMenu.getReducedPrice();
		menuItem.setCreatedBy(loggedUser);
		menuItem.setCreatedOn(mealMenu.getCreatedOn());
		//menuItem.setmealMenu.getLoggedUser();
		//menuItem.setModifiedBy(mealMenu.getModifiedBy());
		//menuItem.setModifiedOn(mealMenu.getModifiedOn());
		return menuItem;
	}

	/**This Method used for store the uploaded meals excel summary**/
	@Override
	public MealCreateJson mealExcelSummary(MultipartFile multipartFile, Long mealSchoolId, String yearMonth, 
			String loggedUser, Set<SchoolGrades> grades, List<SchoolHoliday> schoolHolidays, Long mealSummaryId) {
		MealCreateJson mealCreateJson = new MealCreateJson();
		Boolean gradeStatus = true;
		try{
			String filePath = "";
			Set<SchoolMeal> schoolMealList = new HashSet<SchoolMeal>();
			SchoolMealSummary mealSummary = null;
			List<String> previousGrades = null;
			List<String> mealCreatedGradeList = mealCreatedGrades(mealSchoolId, yearMonth);
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			Map<String, String> gradeKeyVal = mealManageAPIDao.gradeMapByCountry(mealSchool.getCountryCode());
			String gradeFailed = "";
			List<String> gradesList1 = new ArrayList<String>();
			if(mealSummaryId != null && mealSummaryId != 0)
				mealSummary = entityManager.find(SchoolMealSummary.class, mealSummaryId);
			if(mealSummary != null)
				previousGrades =  Arrays.asList(mealSummary.getGradeNames().split(","));
			for(SchoolType type : mealSchool.getSchool().getSchoolType()){
				gradesList1.addAll(type.getValues());
			}
			for(SchoolGrades grade : grades){
				if(gradesList1.contains(grade.toString()) && ((mealSummaryId != null && mealSummaryId != 0 
						&& (previousGrades.contains(grade.toString()) || !mealCreatedGradeList.contains(grade.toString()))) || 
						!mealCreatedGradeList.contains(grade.toString()))){
					
				}else{
					gradeStatus = false;
					gradeFailed = grade.toString();
					break;
				}
			}
			if(gradeStatus){
			if(mealSummaryId != null && mealSummaryId != 0)
				schoolMealList = schoolMealsRepo.findByMealSchoolSchoolIdAndYearMonthAndGradesInAndSchoolMealSummaryIsPublishedAndIsDelete(
						mealSchoolId, yearMonth, new ArrayList<>(grades), true, false);
			else
				schoolMealList = schoolMealsRepo.findByMealSchoolSchoolIdAndYearMonthAndGradesInAndIsDelete(mealSchoolId, yearMonth, 
					new ArrayList<>(grades), false);
			if(schoolMealList.size() < 1){
			Set<MealsExcelSummary> mealsExcelSummaries = mealsExcelSummaryRepository.findByMealSchoolSchoolIdAndYearMonthAndGradesInAndItemType(
					mealSchoolId, yearMonth, new ArrayList<>(grades), ItemTypeConstants.Lunch);
			//File convFile = new File(mealSchoolId+"_"+yearMonth+"_"+gradeNames.replace(",", "_")+multipartFile.getOriginalFilename());

			mealCreateJson = excelReadUtil.mealJson(multipartFile, yearMonth, schoolHolidays, "Lunch", null, false, null);
			if(mealCreateJson.getStatusCode() != 200)
				return mealCreateJson;
			if(mealsExcelSummaries.size() > 1){
				mealsExcelSummaryRepository.delete(mealsExcelSummaries);
				mealsExcelSummaries = new HashSet<MealsExcelSummary>();
			}
			
			MealsExcelSummary mealsExcelSummary = new MealsExcelSummary();
			
			if(mealsExcelSummaries.size() < 1){
					mealsExcelSummary.setCreatedBy(loggedUser);
					mealsExcelSummary.setCreatedOn(new Date());
					mealsExcelSummary.setMealSchool(mealSchool);
					mealsExcelSummary.setExcelLink("");
					mealsExcelSummary.setYearMonth(yearMonth);
					mealsExcelSummary.setItemType(ItemTypeConstants.Lunch);
					mealsExcelSummaryRepository.save(mealsExcelSummary);
				}
				if(mealsExcelSummaries.size() == 1)
					mealsExcelSummary = new ArrayList<>(mealsExcelSummaries).get(0);
				
				File convFile = new File(mealSchoolId+"_"+yearMonth+"_"+mealsExcelSummary.getId()+"_Meals."+
						FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
				FileOutputStream fos = new FileOutputStream(convFile);
			    fos.write(multipartFile.getBytes());
			    fos.close();
				filePath = convFile.getAbsolutePath();
				String finalFileLink = awsUtility.fileUploadPath(filePath, "excelMenuFilelink");				
				
				if(mealsExcelSummary != null){
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
			
			}else{
				if(mealSummaryId != null && mealSummaryId != 0){
					logger.info("Menu can not update as it is already published by school.");
					mealCreateJson.setStatusMessage("Menu can not update as it is already published by school.");
				}else{
					logger.info("Menu can not create as it is already created by school for the selected grades");
					mealCreateJson.setStatusMessage("Menu can not create as it is already created by school for the selected grades.");
				}
				mealCreateJson.setStatus("Failed");
				mealCreateJson.setStatusCode(417);				
			}
			}else{
				logger.info("Please try again with valid grades");
				mealCreateJson.setStatus("Failed");
				if(!gradesList1.contains(gradeFailed)){
					mealCreateJson.setStatusMessage("Selected Grade "+gradeKeyVal.get(gradeFailed)+" does not belong to this School.");
				}else if((mealSummaryId == null || mealSummaryId == 0) && mealCreatedGradeList.contains(gradeFailed)){
					mealCreateJson.setStatusMessage("Menu already created for the selected Grade "+gradeKeyVal.get(gradeFailed)+".");
				}else if(mealSummaryId != null && mealSummaryId != 0 && (!previousGrades.contains(gradeFailed) || 
						mealCreatedGradeList.contains(gradeFailed))){
					mealCreateJson.setStatusMessage("Menu can not update for the selected Grade "+gradeKeyVal.get(gradeFailed)+" as it is belong to other Menu.");
				}
				logger.info(mealCreateJson.getStatusMessage());
				mealCreateJson.setStatusCode(422);
			}
		}catch(Exception e){
			logger.error("Failed to import the meal file due to "+e.getMessage());
			mealCreateJson.setStatus("Failed");
			mealCreateJson.setStatusMessage("Failed to import the meal menu file. Please check file and try again later!");
			mealCreateJson.setStatusCode(500);
			mealCreateJson.setErrorMessage(e.getMessage());
		}
		return mealCreateJson;
	}

	/**This method used for update the meal summary i.e. cut-off date, order extension status, auto reminder date, meal publish status, ....etc**/
	@Override
	public ServiceResponse updateMealSummary(MealSummaryUpdateReq mealSmryUpdReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		/*if(mealSmryUpdReq.getCutOffDateTime() != null || mealSmryUpdReq.getOrderDateExtensionStatus() != null ||
				mealSmryUpdReq.getAutoReminderDate1() != null || mealSmryUpdReq.getAutoReminderDate2() != null){
			StringBuilder sb = new StringBuilder();
			String qry = null;
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			sb.append("Update SchoolMealsSummary_v2 s set ");
			if(mealSmryUpdReq.getCutOffDateTime() != null)
				sb.append("s.cutOffDateTime = '"+dateTimeFormat.format(mealSmryUpdReq.getCutOffDateTime())+"',");
			if(mealSmryUpdReq.getOrderDateExtensionStatus() != null)
				sb.append("s.orderDateExtensionStatus = "+mealSmryUpdReq.getOrderDateExtensionStatus()+",");
			if(mealSmryUpdReq.getAutoReminderDate1() != null)
				sb.append("s.autoReminderDate1 = '"+dateFormat.format(mealSmryUpdReq.getAutoReminderDate1())+"',");
			else
				sb.append("s.autoReminderDate1 = null,");
			if(mealSmryUpdReq.getAutoReminderDate2() != null)
				sb.append("s.autoReminderDate2 = '"+dateFormat.format(mealSmryUpdReq.getAutoReminderDate2())+"',");
			else
				sb.append("s.autoReminderDate2 = null,");
			qry = sb.toString().substring(0, sb.length()-1);
			sb = new StringBuilder();
			sb.append(qry);
			sb.append(" where s.schoolId = :summaryId");

			int numb = entityManager.createNativeQuery(sb.toString()).setParameter("summaryId", mealSmryUpdReq.getMealSummaryId())
					.executeUpdate();
			if(numb > 0){
				serviceResponse.setStatusMessage("Meal Summary has been updated successfully");
				serviceResponse.setStatusCode(200);
			}
			else{
				serviceResponse.setStatusMessage("There are no eligible entry to update");
				serviceResponse.setStatusCode(422);
			}
		}else{
			serviceResponse.setStatusMessage("No data found in request which would be update");
			serviceResponse.setStatusCode(417);
		}*/
		SchoolMealSummary schoolMealSummary = entityManager.find(SchoolMealSummary.class, mealSmryUpdReq.getMealSummaryId());
		Boolean status = false;
		Boolean isPublishEmail = false;
		if(schoolMealSummary != null){
			if(mealSmryUpdReq.getIsPublished() != null){
				if(mealSmryUpdReq.getIsPublished() && (schoolMealSummary.getIsPublished() == null || !schoolMealSummary.getIsPublished())){
					//String latestYearMonth = reportsDao.getLatestYearMonth(Arrays.asList(schoolMealSummary.getMealSchool().getSchoolId()));
					String latestYearMonth = getLatestPublishedMenuMonth(schoolMealSummary.getMealSchool().getSchoolId(), 
							Arrays.asList(schoolMealSummary.getGradeNames().split("\\s*,\\s*")));
					if(latestYearMonth != null && !latestYearMonth.equalsIgnoreCase(schoolMealSummary.getYearMonth())){
						serviceResponse.setStatusCode(422);
						serviceResponse.setStatus("Failed");
						serviceResponse.setStatusMessage("This menu can not publish untill "+Month.of(Integer.parseInt(latestYearMonth.substring(4)))
						.name()+"-"+latestYearMonth.substring(0,4)+" month menu cut-off-date is crossed.");
						return serviceResponse;
					}
					schoolMealSummary.setIsPublished(mealSmryUpdReq.getIsPublished());
					status = true;
					isPublishEmail = true;
				}
			}else{
				if(mealSmryUpdReq.getCutOffDateTime() != null)
					schoolMealSummary.setCutOffDateTime(mealSmryUpdReq.getCutOffDateTime());
				if(mealSmryUpdReq.getOrderDateExtensionStatus() != null)
					schoolMealSummary.setOrderDateExtensionStatus(mealSmryUpdReq.getOrderDateExtensionStatus());
				schoolMealSummary.setAutoReminderDate1(mealSmryUpdReq.getAutoReminderDate1());
				schoolMealSummary.setAutoReminderDate2(mealSmryUpdReq.getAutoReminderDate2());
				status = true;
			}
			if(status){
				schoolMealSummary.setModifiedBy(mealSmryUpdReq.getLoggedUser());
				schoolMealSummary.setModifiedOn(new Date());
				entityManager.merge(schoolMealSummary);
				if(isPublishEmail != null && isPublishEmail){
					List<PublishedMenuNotifReq> publishedMenuNotifReqs = buildMailOnPublishedMenu(schoolMealSummary, mealSmryUpdReq.getSchoolYear());
					Map<String, List<PublishedMenuNotifReq>> menuPublishReminderReq = new HashMap<String, List<PublishedMenuNotifReq>>();					
					if(publishedMenuNotifReqs != null && publishedMenuNotifReqs.size() > 0){
						int i = 1;
						List<PublishedMenuNotifReq> publishedMenuNotifReqList = new ArrayList<PublishedMenuNotifReq>();
						for(PublishedMenuNotifReq publishedMenuNotifReq : publishedMenuNotifReqs){
							publishedMenuNotifReqList.add(publishedMenuNotifReq);
							if(i == 50){
								menuPublishReminderReq.put("users", publishedMenuNotifReqList);	
								sendNotificationUtil.sendPublishedMenuReminder(menuPublishReminderReq);
								publishedMenuNotifReqList = new ArrayList<PublishedMenuNotifReq>();
								menuPublishReminderReq = new HashMap<String, List<PublishedMenuNotifReq>>();
								i = 0;
							}
							i++;
						}
						if(publishedMenuNotifReqList.size() > 0){
							menuPublishReminderReq.put("users", publishedMenuNotifReqList);	
							sendNotificationUtil.sendPublishedMenuReminder(menuPublishReminderReq);
						}						
					}				}
				serviceResponse.setStatusMessage("Menu Summary updated successfully.");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatus("Success");
			}else{
				serviceResponse.setStatusMessage("Failed to update menu summary.");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
			}			
		}else{
			serviceResponse.setStatusMessage("Menu summary record not found.");
			serviceResponse.setStatusCode(404);
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}
	/**This method used for update the meal summary i.e. cut-off date, order extension status, auto reminder date, meal publish status, ....etc**/
	@Override
	public ServiceResponse updateMealSummaryV2(MealSummaryUpdateReq mealSmryUpdReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		MealCalendarSummary schoolMealSummary = entityManager.find(MealCalendarSummary.class, mealSmryUpdReq.getMealSummaryId());
		Boolean status = false;
		Boolean isPublishEmail = false;
		if(schoolMealSummary != null){
			if(mealSmryUpdReq.getIsPublished() != null){
				if(mealSmryUpdReq.getIsPublished() && (schoolMealSummary.getIsPublished() == null || !schoolMealSummary.getIsPublished())){
					//String latestYearMonth = reportsDao.getLatestYearMonth(Arrays.asList(schoolMealSummary.getMealSchool().getSchoolId()));
					/*String latestYearMonth = getLatestPublishedMenuMonthV2(mealSmryUpdReq.getMealSummaryId(), schoolMealSummary.getSchool().getSchoolId());
					if(latestYearMonth != null){
						serviceResponse.setStatusCode(422);
						serviceResponse.setStatus("Failed");
						serviceResponse.setStatusMessage("This menu can not publish untill "+Month.of(Integer.parseInt(latestYearMonth.substring(4)))
						.name()+"-"+latestYearMonth.substring(0,4)+" month menu cut-off-date is crossed.");
						return serviceResponse;
					}*/
					schoolMealSummary.setIsPublished(mealSmryUpdReq.getIsPublished());
					status = true;
					isPublishEmail = true;
				}
			}else{
				if(mealSmryUpdReq.getCutOffDateTime() != null)
					schoolMealSummary.setCutOffDateTime(mealSmryUpdReq.getCutOffDateTime());
				if(mealSmryUpdReq.getOrderDateExtensionStatus() != null)
					schoolMealSummary.setOrderDateExtensionStatus(mealSmryUpdReq.getOrderDateExtensionStatus());
				schoolMealSummary.setAutoReminderDate1(mealSmryUpdReq.getAutoReminderDate1());
				schoolMealSummary.setAutoReminderDate2(mealSmryUpdReq.getAutoReminderDate2());
				schoolMealSummary.setIsSideSelect(mealSmryUpdReq.getIsSideSelect());
				schoolMealSummary.setExtraEnableForCaterer(mealSmryUpdReq.isExtraEnableForCaterer());
				schoolMealSummary.setAllowOrderNDaysBefore(mealSmryUpdReq.getAllowOrderNDaysBefore());
				schoolMealSummary.setCutOffType(mealSmryUpdReq.getCutOffType());
				schoolMealSummary.setWeeklyOrderCutOffDay(mealSmryUpdReq.getWeeklyOrderCutOffDay());
				schoolMealSummary.setWeeklyOrderCutOffTime(mealSmryUpdReq.getWeeklyOrderCutOffTime());
				status = true;
			}
			if(status){
				schoolMealSummary.setModifiedBy(mealSmryUpdReq.getLoggedUser());
				schoolMealSummary.setModifiedOn(new Date());
				entityManager.merge(schoolMealSummary);
				if(isPublishEmail != null && isPublishEmail){
					List<PublishedMenuNotifReq> publishedMenuNotifReqs = buildMailOnPublishedMenuV2(schoolMealSummary, mealSmryUpdReq.getSchoolYear());
					Map<String, List<PublishedMenuNotifReq>> menuPublishReminderReq = new HashMap<String, List<PublishedMenuNotifReq>>();
					if(publishedMenuNotifReqs != null && publishedMenuNotifReqs.size() > 0){
						int i = 1;
						List<PublishedMenuNotifReq> publishedMenuNotifReqList = new ArrayList<PublishedMenuNotifReq>();
						for(PublishedMenuNotifReq publishedMenuNotifReq : publishedMenuNotifReqs){
							publishedMenuNotifReqList.add(publishedMenuNotifReq);
							if(i == 50){
								menuPublishReminderReq.put("users", publishedMenuNotifReqList);
								sendNotificationUtil.sendPublishedMenuReminder(menuPublishReminderReq);
								publishedMenuNotifReqList = new ArrayList<PublishedMenuNotifReq>();
								menuPublishReminderReq = new HashMap<String, List<PublishedMenuNotifReq>>();
								i = 0;
							}
							i++;
						}
						if(publishedMenuNotifReqList.size() > 0){
							menuPublishReminderReq.put("users", publishedMenuNotifReqList);
							sendNotificationUtil.sendPublishedMenuReminder(menuPublishReminderReq);
						}
					}				}
				serviceResponse.setStatusMessage("Menu Summary updated successfully.");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatus("Success");
			}else{
				serviceResponse.setStatusMessage("Failed to update menu summary.");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
			}
		}else{
			serviceResponse.setStatusMessage("Menu summary record not found.");
			serviceResponse.setStatusCode(404);
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}

	/**This method used for get all the grades for whom meal created by school**/
	@Override
	public List<String> mealCreatedGrades(Long mealSchoolId, String yearMonth) throws Exception {
		List<String> grades = null;
		grades = entityManager.createNativeQuery("select sg.grades_name from SchoolMeals_v2 sm INNER JOIN schoolMeal_grades sg on sm.schoolId = "
				+ "sg.schoolmeal_Id INNER JOIN SchoolMealsSummary_v2 s on sm.schoolMealSummary_schoolId = s.schoolId "
				+ "where sm.mealSchool_schoolId = :mealSchoolId and sm.yearMonth = :yearMonth group by sg.grades_name")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonth",yearMonth).getResultList();
		logger.info("API dao exeuted for get the meal grades.");
		return grades;
	}

	public List<String> retrieveMenuScheduledGrades(Long mealSchoolId, String yearMonth, ItemTypeConstants mealType)  {
		List<String> grades = null;
		grades = entityManager.createNativeQuery("select g.grades_name from meal_calendar_summary sm inner join meal_summary_grades g " +
				"on g.meal_calendar_summary_id = sm.id where sm.mealSchool_schoolId=:mealSchoolId and sm.yearMonth=:yearMonth and sm.mealType=:mealType")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonth",yearMonth).setParameter("mealType",mealType.toString()).getResultList();
		return grades;
	}
	
	/**This method used for get all the grades for created breakfast menu**/
	@Override
	public List<String> breakfastCreatedGrades(Long mealSchoolId, String yearMonth) {
		List<String> grades = null;
		grades = entityManager.createNativeQuery("select bmg.grades_name from BreakfastMaster bm INNER JOIN BreakfastMaster_Grades bmg on bm.recId = "
				+ "bmg.breakfastMaster_Id where bm.mealSchool_schoolId = :mealSchoolId and bm.yearMonth = :yearMonth group by bmg.grades_name")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonth",yearMonth).getResultList();
		logger.info("API dao exeuted for get the breakfast created grades.");
		return grades;
	}
	
	/**This method used for restored the cancelled menu items**/
	@Override
	public ServiceResponse restoreCancelledOrder(Long mealSchoolId, List<MealOrderDetails> mealOrderDetails) {
		ServiceResponse serviceResponse = new ServiceResponse();
		for(MealOrderDetails mealOrderDetail : mealOrderDetails){
			mealOrderDetailsRepository.save(mealOrderDetail);
		}
		serviceResponse.setStatusCode(200);
		logger.info("Save operation completed in DAO for the restore cancelled menu items");
		return serviceResponse;
	}

	/**This method used for get the getMenuOrderDetailIds for whome ordered item need to be remove**/
	@Override
	public Set<String> getStudentIds(String yearMonth, Long schoolMealId, Long mealSchoolId) {
		List<String> studentIds = null;
		studentIds = entityManager.createNativeQuery("Select su.studentId from MealOrdersAudit_v2 moa INNER join "
				+ "mealOrdersAudit_schoolMeals moas on moa.schoolId = moas.orderId Inner join StudentUser_v2 su on "
				+ "moa.studentUser_userId = su.userId where su.mealSchool_schoolId = :mealSchoolId and "
				+ "moa.yearMonth = :yearMonth and moas.schoolMealId = :schoolMealId "
				+ "group by su.studentId").setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonth", yearMonth)
				.setParameter("schoolMealId", schoolMealId).getResultList();
		return new HashSet<String>(studentIds);
	}
	
	/**This method used for get the getMenuOrderDetailIds for whome ordered item need to be remove**/
	@Override
	public Set<String> getStudentIdsV2(String yearMonth, Long schoolMealId, Long mealSchoolId) {
		List<String> studentIds = null;
		studentIds = entityManager.createNativeQuery("Select su.studentId from MealOrdersAudit_v2 moa INNER join "
				+ "mealOrdersAudit_calendarMenu moas on moa.schoolId = moas.orderId Inner join StudentUser_v2 su on "
				+ "moa.studentUser_userId = su.userId where su.mealSchool_schoolId = :mealSchoolId and "
				+ "moa.yearMonth = :yearMonth and moas.mealCalendarId = :schoolMealId "
				+ "group by su.studentId").setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonth", yearMonth)
				.setParameter("schoolMealId", schoolMealId).getResultList();
		return new HashSet<String>(studentIds);
	}

	/**This method used for add/remove/edit the menu item**/
	@Override
	public ServiceResponse menuModification(List<MealOrderDetails> mealOrderDetails, List<SchoolMeal> schoolMeals,
			MenuModificationReq menuModificationReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		serviceResponse.setStatusCode(400);
		if(mealOrderDetails != null && mealOrderDetails.size() > 0)
			for(MealOrderDetails mealOrderDetail : mealOrderDetails){
				mealOrderDetailsRepository.save(mealOrderDetail);
			}
		if(schoolMeals != null && schoolMeals.size() > 0){
			for(SchoolMeal schoolMeal : schoolMeals){
				schoolMealsRepo.save(schoolMeal);
			}
			entityManager.flush();
		}
 		/*if(menuModificationReq.getDeleteSchoolMealId() != null)
			schoolMealsRepo.delete(menuModificationReq.getDeleteSchoolMealId());*/
		serviceResponse.setStatusCode(200);
		logger.info("Menu modification request has been proceed successfully");
		return serviceResponse;
	}

	/**This method used for add/remove/edit the menu item**/
	@Override
	public ServiceResponse menuModificationV2(MealCalendarSummary summary) {
		ServiceResponse serviceResponse = new ServiceResponse();
		serviceResponse.setStatusCode(400);
		entityManager.merge(summary);

		entityManager.flush();
		/*if(menuModificationReq.getDeleteSchoolMealId() != null)
			schoolMealsRepo.delete(menuModificationReq.getDeleteSchoolMealId());*/
		serviceResponse.setStatusCode(200);
		logger.info("Menu modification request has been proceed successfully");
		return serviceResponse;
	}

	/**This method used for get the grades for the created meal**/
	@Override
	public Set<String> getMenuAddGrades(Long schoolMealId) {
		List<String> schoolGrades =  entityManager.createNativeQuery("Select smg.grades_name from SchoolMeals_v2 sm inner "
				+ "join schoolMeal_grades smg on sm.schoolId = smg.schoolmeal_Id where sm.schoolId = :schoolMealId")
				.setParameter("schoolMealId", schoolMealId).getResultList();
		return new HashSet<String>(schoolGrades);
	}	/**This method used for get the grades for the created meal**/

	@Override
	public Set<String> getMenuAddGradesV2(Long summaryId) {
		List<String> schoolGrades =  entityManager.createNativeQuery("Select msg.grades_name from meal_summary_grades msg  "
				+ "where msg.`meal_calendar_summary_id` = :summaryId")
				.setParameter("summaryId", summaryId).getResultList();
		return new HashSet<String>(schoolGrades);
	}


	/**this method used for build the request to send the email on parent email id regarding the menu published**/
	private List<PublishedMenuNotifReq> buildMailOnPublishedMenu(SchoolMealSummary schoolMealSummary, String schoolYear){
		List<PublishedMenuNotifReq> publishedMenuNotifReqs = new ArrayList<PublishedMenuNotifReq>();
		List<String> menuGrades = entityManager.createNativeQuery("Select sg.grades_name from SchoolMealsSummary_v2 sms "
				+ "Inner Join SchoolMeals_v2 sm on sms.schoolId = sm.schoolMealSummary_schoolId Inner Join schoolMeal_grades sg "
				+ "on sm.schoolId = sg.schoolmeal_Id where sms.schoolId = :menuSummaryId and sms.isPublished =1 "
				+ "group by sg.grades_name").setParameter("menuSummaryId", schoolMealSummary.getSchoolId()).getResultList();
		if(menuGrades != null && menuGrades.size() > 0){
			List<Object[]> objArray = entityManager.createNativeQuery("Select pu.userName, pu.parentAltEmail from ParentUser_v2 pu"
					+ " Inner Join StudentUser_v2 stu on pu.userId = stu.parentuser_userId where stu.mealSchool_schoolId = "
					+ ":mealSchoolId and stu.schoolYear = :schoolYear and stu.gradeName IN (:menuGrades) and stu.isActive = 1 "
					+ "and stu.isRegister = 1 and pu.isActive = 1 group by pu.userName, pu.parentAltEmail").setParameter("mealSchoolId", 
							schoolMealSummary.getMealSchool().getSchoolId()).setParameter("schoolYear", schoolYear).setParameter(
									"menuGrades", menuGrades).getResultList();
			if(objArray != null && objArray.size()>0){
				PublishedMenuNotifReq publishedMenuNotifReq = null;
				Set<String> parentUqEmail = new HashSet<String>();
				for(Object[] obj : objArray){
					if(obj[0] != null && !obj[0].toString().trim().isEmpty())
						parentUqEmail.add(obj[0].toString());
					if(obj[1] != null && !obj[0].toString().trim().isEmpty())
						parentUqEmail.add(obj[1].toString());
				}
				UsersAuthInfo usersAuthInfo = null;
				for(String parentEmail : parentUqEmail){
					if(parentEmail != null && !parentEmail.trim().equalsIgnoreCase("")){
						usersAuthInfo = usersAuthInfoRepository.findByUsername(parentEmail);
						if(usersAuthInfo.getfToken() != null && !usersAuthInfo.getfToken().trim().equalsIgnoreCase("") && 
								usersAuthInfo.getEmailIsSubscribe() != null && usersAuthInfo.getEmailIsSubscribe() /*&&
								usersAuthInfo.getLunchReminderEnable() != null && usersAuthInfo.getLunchReminderEnable()*/){
							publishedMenuNotifReq = new PublishedMenuNotifReq();
							publishedMenuNotifReq.setEmail(parentEmail);
							publishedMenuNotifReq.setSchool(schoolMealSummary.getMealSchool().getSchoolName());
							publishedMenuNotifReq.setMonth(schoolMealSummary.getYearMonth());
							publishedMenuNotifReq.setAdminEmail(schoolMealSummary.getMealSchool().getContactPEmail() != null ? schoolMealSummary.getMealSchool().getContactPEmail() : "");
							publishedMenuNotifReq.setUrl(mealManageAPIDao.parentUserActivationLink(parentEmail, usersAuthInfo.getfToken()));
							publishedMenuNotifReqs.add(publishedMenuNotifReq);
						}
					}					
				}
			}
			else
				logger.info("There are no eligible parent users to whom email need to be send.");
		}else
			logger.info("There are no valid menu grade for send the mail to parent user regarding menu published.");
		
		return publishedMenuNotifReqs;
	}

	/**this method used for build the request to send the email on parent email id regarding the menu published**/
	private List<PublishedMenuNotifReq> buildMailOnPublishedMenuV2(MealCalendarSummary mealCalendarSummary, String schoolYear){
		List<PublishedMenuNotifReq> publishedMenuNotifReqs = new ArrayList<PublishedMenuNotifReq>();

		List<String> menuGrades = entityManager.createNativeQuery("Select sg.grades_name from meal_summary_grades sg "
				+ "where sg.meal_calendar_summary_id = :menuSummaryId").setParameter("menuSummaryId", mealCalendarSummary.getId()).getResultList();
		if(menuGrades != null && menuGrades.size() > 0){
			List<Object[]> objArray = entityManager.createNativeQuery("Select pu.userName, pu.parentAltEmail from ParentUser_v2 pu"
					+ " Inner Join StudentUser_v2 stu on pu.userId = stu.parentuser_userId where stu.mealSchool_schoolId = "
					+ ":mealSchoolId and stu.schoolYear = :schoolYear and stu.gradeName IN (:menuGrades) and stu.isActive = 1 "
					+ "and stu.isRegister = 1 and pu.isActive = 1 group by pu.userName, pu.parentAltEmail").setParameter("mealSchoolId",
					mealCalendarSummary.getSchool().getSchoolId()).setParameter("schoolYear", schoolYear).setParameter(
					"menuGrades", menuGrades).getResultList();

			if(objArray != null && objArray.size()>0){
				PublishedMenuNotifReq publishedMenuNotifReq = null;
				Set<String> parentUqEmail = new HashSet<String>();
				for(Object[] obj : objArray){
					if(obj[0] != null && !obj[0].toString().trim().isEmpty())
						parentUqEmail.add(obj[0].toString());
					if(obj[1] != null && !obj[0].toString().trim().isEmpty())
						parentUqEmail.add(obj[1].toString());
				}
				UsersAuthInfo usersAuthInfo = null;
				for(String parentEmail : parentUqEmail){
					if(parentEmail != null && !parentEmail.trim().equalsIgnoreCase("")){
						usersAuthInfo = usersAuthInfoRepository.findByUsername(parentEmail);
						if(usersAuthInfo.getfToken() != null && !usersAuthInfo.getfToken().trim().equalsIgnoreCase("") &&
								usersAuthInfo.getEmailIsSubscribe() != null && usersAuthInfo.getEmailIsSubscribe() /*&&
								usersAuthInfo.getLunchReminderEnable() != null && usersAuthInfo.getLunchReminderEnable()*/){
							publishedMenuNotifReq = new PublishedMenuNotifReq();
							publishedMenuNotifReq.setEmail(parentEmail);
							publishedMenuNotifReq.setSchool(mealCalendarSummary.getSchool().getSchoolName());
							publishedMenuNotifReq.setMonth(mealCalendarSummary.getYearMonth());
							publishedMenuNotifReq.setAdminEmail(mealCalendarSummary.getSchool().getContactPEmail() != null ? mealCalendarSummary.getSchool().getContactPEmail() : "");
							publishedMenuNotifReq.setUrl(mealManageAPIDao.parentUserActivationLink(parentEmail, usersAuthInfo.getfToken()));
							publishedMenuNotifReqs.add(publishedMenuNotifReq);
						}
					}
				}
			}
			else
				logger.info("There are no eligible parent users to whom email need to be send.");
		}else
			logger.info("There are no valid menu grade for send the mail to parent user regarding menu published.");

		return publishedMenuNotifReqs;
	}
	
	/**This method used for get the latest published menu month whose eligible for order meal based on specified condition**/
	private String getLatestPublishedMenuMonth(Long mealSchoolId, List<String> grades){
		Date date = new Date();
		Object yearMonthVal = "";
		yearMonthVal = entityManager.createNativeQuery("select max(sms.yearMonth) from SchoolMealsSummary_v2 sms Inner Join "
				+ "SchoolMeals_v2 sm on sms.schoolId = sm.schoolMealSummary_schoolId Inner Join schoolMeal_grades smg on "
				+ "sm.schoolId = smg.schoolmeal_Id where sms.mealSchool_schoolId = :mealSchoolId and (sms.cutOffDateTime >= "
				+ ":currentDateTime OR sms.orderDateExtensionStatus = 1) and sms.isPublished = 1 and smg.grades_name IN (:grades)")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("currentDateTime", date).setParameter("grades", grades)
				.getSingleResult();
		if(yearMonthVal != null)
			return yearMonthVal.toString();
		else
			return null;
	}

	/**This method used for get the latest published menu month whose eligible for order meal based on specified condition**/
	/*private String getLatestPublishedMenuMonthV2(Long summaryId, Long mealSchoolId){
		Date date = new Date();
		Object yearMonthVal = "";
		//Fetch the schoolid and grade for the passed in summaryid
		//For that school and grade, find all the summaries
		//Fetch max of yearmonth based on cutoffdate time and
		yearMonthVal = entityManager.createNativeQuery("select max(mcs.yearMonth) from meal_calendar_summary mcs inner join  meal_summary_grades mg on mg.meal_calendar_summary_id=mcs.id where " +
				"mcs.mealSchool_schoolId = :mealSchoolId and mg.grades_name in (SELECT mg.grades_name FROM meal_summary_grades mg where mg.meal_calendar_summary_id=:summaryId) " +
				"and (mcs.cutOffDateTime >= :currentDateTime OR mcs.orderDateExtensionStatus = 1) and mcs.isPublished = 1 ")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("summaryId", summaryId).setParameter("currentDateTime", date)
				.getSingleResult();
		if(yearMonthVal != null)
			return yearMonthVal.toString();
		else
			return null;
	}*/

	/**This method prepare the Summary info**/
	@Override
	public SchoolMealSummary prepareCalendarSummary(MealCalendarSummary summary){
		SchoolMealSummary menuSummary = new SchoolMealSummary();
		menuSummary.setGradeNames(summary.getGrades().stream().map(schoolGrade -> schoolGrade.toString()).collect(Collectors.joining(",")));
		menuSummary.setMealSchool(summary.getSchool());
		menuSummary.setYearMonth(summary.getYearMonth());
		menuSummary.setSchoolId(summary.getId());
		menuSummary.setMenuType(summary.getMealType());
		menuSummary.setIsExtraPreOrder(summary.getIsExtraPreOrder());
		menuSummary.setMealSchool(summary.getSchool());
		return menuSummary;
	}

	/*@Override
	public List<MenuDetailDTO> getMenuItemsForSummary(Long summaryId ){
		List<MenuDetailDTO> menuDetailList = new ArrayList<>();
		List<Object[]> objArray = entityManager.createNativeQuery("select d.date, e.name, e.ingredients, e.shortDescription, e.allergens, e.price " +
				"FROM meal_calendar d INNER JOIN menu_items e on d.menu_item_id=e.id where d.meal_calendar_summary_id = :summaryId")
				.setParameter("summaryId",summaryId).getResultList();
		objArray.stream().forEach(obj -> {
			MenuDetailDTO menuDetailDTO = new MenuDetailDTO();
			menuDetailDTO.setDate(obj[0]!=null?(Timestamp)obj[0]:null);
			menuDetailDTO.setName(obj[1]!=null?(String)obj[1]:null);
			menuDetailDTO.setIngredients(obj[2]!=null?(String)obj[2]:null);
			menuDetailDTO.setShortDescription(obj[3]!=null?(String)obj[3]:null);
			menuDetailDTO.setAllergens(obj[4]!=null?(String)obj[4]:null);
			menuDetailDTO.setPrice(obj[5]!=null?(Double)obj[5]:null);
			menuDetailList.add(menuDetailDTO);
				});
		return menuDetailList;
	}*/
}
