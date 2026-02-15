package com.mealManage.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mealManage.dao.TransactionsDao;
import com.mealManage.domain.AccBalanceTransferSibling;
import com.mealManage.domain.LunchNotServedStudents;
import com.mealManage.domain.MenuItemDetails;
import com.mealManage.domain.MenuItemDetailsV2;
import com.mealManage.domain.OrderedMenuItemDetails;
import com.mealManage.domain.StudentDetailsWithOrderedItem;
import com.mealManage.mealmodel.meal.BreakfastItems;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.packages.BCACAudit;
import com.mealManage.mealmodel.packages.PackageSubscriptionsTrx;
import com.mealManage.mealmodel.packages.PickupAuthorized;
import com.mealManage.mealmodel.repository.BreakfastMasterRepository;
import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.LowBalanceSchoolSettingRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.school.CountryDetail;
import com.mealManage.mealmodel.school.LowBalanceSchoolSetting;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.transaction.MasterTransactionsAudit;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.mealmodel.transaction.PurchaseItemType;
import com.mealManage.mealmodel.transaction.StudentWiseTransaction;
import com.mealManage.mealmodel.transaction.TransactionType;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentBalanceImportResp;
import com.mealManage.util.BCACPaymentRecieptPdf;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.DateUtilityV2;
import com.mealManage.util.ExcelReadUtil;
import com.mealManage.util.PaymentRecieptPdf;
import com.mealManage.util.StripeUtil;
import com.stripe.model.Charge;

@Service
/**This class used for implement the TransactionsService's methods**/
public class TransactionsServiceImpl implements TransactionsService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TransactionsDao transactionsDao;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private BreakfastMasterRepository breakfastMasterRepository;
	@Autowired
	private PaymentRecieptPdf paymentRecieptPdf;
	@Autowired
	private ExcelReadUtil excelReadUtil;
	@Autowired
	private DateUtilityV2 du;
	@Autowired
	private StripeUtil stripeUtill;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private LowBalanceSchoolSettingRepository lowBalanceSchoolSettingRepository;
	@Autowired
	private BCACPaymentRecieptPdf bcacPaymentRecieptPdf;/*
	@Autowired
	private MealCalendarSummaryRepository summaryRepo;*/
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;

	/**This method used for deposit the payment at school campus by cheque or cash or creditcard**/
	@Override
	public ServiceResponse depositAmtThruOffline(MasterTransactionsAudit masterTransactionsAudit) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			masterTransactionsAudit = buildTransactionPaymentObject(masterTransactionsAudit);
			MasterTransactionsAudit masterTransactionsAuditResp = transactionsDao.depositAmtThruOffline(masterTransactionsAudit);
			if(masterTransactionsAuditResp.getRecId() != null && masterTransactionsAuditResp.getRecId() != 0){
				masterTransactionsAudit.setRecId(masterTransactionsAuditResp.getRecId());
				String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(masterTransactionsAudit.getMealSchoolId()));
				paymentRecieptPdf.paymentReceiptGenerate(masterTransactionsAudit,currencySymbol);
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Lunch Balance added successfully.");
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Failed to complete the transaction.");
			}
			logger.info(serviceResponse.getStatusMessage());
			//send email regarding payment deposit
		}catch(Exception e){
			logger.error("Failed to deposit the payment through offline due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Transaction failed to process, Please try again.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for deposit the amount through online process**/
	@Override
	public ServiceResponse depositAmtThruOnline(MasterTransactionsAudit masterTransactionsAudit) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			masterTransactionsAudit = buildTransactionPaymentObject(masterTransactionsAudit);
			if(masterTransactionsAudit.getMealSchool().getIsPaymentEnabled() != null && 
					masterTransactionsAudit.getMealSchool().getIsPaymentEnabled() && 
					(masterTransactionsAudit.getMealSchool().getStripeAccountId() != null || (masterTransactionsAudit.getPaymentGateway() != null 
					&& masterTransactionsAudit.getPaymentGateway().equalsIgnoreCase("PayMob")))){
				CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchoolRepository.getSchoolCountry(masterTransactionsAudit.getMealSchoolId()));
				MasterTransactionsAudit masterTransactionsAuditResp = transactionsDao.depositAmtThruOnline(masterTransactionsAudit,countryDetail.getCurrencyCode());
				if(masterTransactionsAuditResp.getRecId() != null && masterTransactionsAuditResp.getRecId() != 0){
					masterTransactionsAudit.setRecId(masterTransactionsAuditResp.getRecId());
					//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(masterTransactionsAudit.getMealSchoolId()));
					paymentRecieptPdf.paymentReceiptGenerate(masterTransactionsAudit,countryDetail.getCurrencySymbol());
					serviceResponse.setStatus("Success");
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatusMessage("Lunch Balance added successfully.");
				}else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("Failed to complete the transaction.");
				}
				logger.info(serviceResponse.getStatusMessage());
			}else
				throw new Exception("School not accepting online payment. Please contact to the School Admin");
			//send email regarding payment deposit through online process
		}catch(Exception e){
			serviceResponse.setStatusMessage("Online deposit transaction failed, Amount will be refunded if it is deducted.");
			logger.error(serviceResponse.getStatusMessage()+" for schoolId::"+masterTransactionsAudit.getMealSchoolId()+" and parentEmail::"+masterTransactionsAudit.getParentUserEmails()+" due to '"+e.getMessage()+"'");
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used to purchase the events for a students**/
	@Override
	public ServiceResponse eventPurchaseThruOnline(MasterTransactionsAudit masterTransactionsAudit) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			masterTransactionsAudit = buildTransactionPaymentObject(masterTransactionsAudit);
			if(masterTransactionsAudit.getMealSchool().getIsPaymentEnabled() != null && 
					masterTransactionsAudit.getMealSchool().getIsPaymentEnabled() && 
					masterTransactionsAudit.getMealSchool().getStripeAccountId() != null){
				CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchoolRepository.getSchoolCountry(masterTransactionsAudit.getMealSchoolId()));
				MasterTransactionsAudit masterTransactionsAuditResp = transactionsDao.depositAmtThruOnline(masterTransactionsAudit,countryDetail.getCurrencyCode());
				if(masterTransactionsAuditResp.getRecId() != null && masterTransactionsAuditResp.getRecId() != 0){
					masterTransactionsAudit.setRecId(masterTransactionsAuditResp.getRecId());
					//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(masterTransactionsAudit.getMealSchoolId()));
					paymentRecieptPdf.paymentReceiptGenerate(masterTransactionsAudit,countryDetail.getCurrencySymbol());
					serviceResponse.setStatus("Success");
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatusMessage("Events purchase transaction done successfully");
				}else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("Failed to complete the events purchase transaction.");
				}
				logger.info(serviceResponse.getStatusMessage());
			}else
				throw new Exception("School not accepting online payment. Please contact to the School Admin");
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to audit the event purchase transaction.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error("Events purchase transactions failed for schoolId::"+masterTransactionsAudit.getMealSchoolId()+" and parentEmail::"+masterTransactionsAudit.getParentUserEmails()+" due to '"+e.getMessage()+"'");
		}
		return serviceResponse;
	}

	/**This method used for audit the purchase transaction details**/
	@Override
	public ServiceResponse purchaseItemAudit(List<MasterTransactionsAudit> masterTransactionsAudits, Long mealSchoolId, String systemDateTime) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			Date transactionDt = null;
			if(systemDateTime != null && !systemDateTime.trim().isEmpty()){
				//String dateTime = new DateUtility().formatDateToString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(systemDateTime.replace("T", " ")), "yyyy-MM-dd HH:mm:ss", "UTC");
				transactionDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(systemDateTime.replace("T", " "));
			}else{
				transactionDt = new Date();
			}
			Map<ItemTypeConstants, List<BigInteger>> servedStds = purchasedMenuStudents(mealSchoolId, 
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(transactionDt),true);
			masterTransactionsAudits = buildPurchaseTransactionObject(masterTransactionsAudits, mealSchoolId,servedStds,transactionDt);
			transactionsDao.purchaseItemAudit(masterTransactionsAudits);
			//if(masterTransactionsAudit.getRecId() != null && masterTransactionsAudit.getRecId() != 0){
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage(masterTransactionsAudits.size()+" purchase transactions details audited successfully.");
			/*}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Failed to complete the purchase transaction audit");
			}*/
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to audit the purchase transaction details due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to audit the purchase transaction details.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for audit the purchase transaction details**/
	@Override
	public ServiceResponse purchaseItemAuditV2(List<MasterTransactionsAudit> masterTransactionsAudits, Long mealSchoolId, String systemDateTime) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			Date transactionDt = null;
			if(systemDateTime != null && !systemDateTime.trim().isEmpty()){
				//String dateTime = new DateUtility().formatDateToString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(systemDateTime.replace("T", " ")), "yyyy-MM-dd HH:mm:ss", "UTC");
				transactionDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(systemDateTime.replace("T", " "));
			}else{
				transactionDt = new Date();
			}
			Map<ItemTypeConstants, List<BigInteger>> servedStds = purchasedMenuStudents(mealSchoolId, 
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(transactionDt),true);
			masterTransactionsAudits = buildPurchaseTransactionObject(masterTransactionsAudits, mealSchoolId,servedStds,transactionDt);
			transactionsDao.purchaseItemAuditV2(masterTransactionsAudits);
			//if(masterTransactionsAudit.getRecId() != null && masterTransactionsAudit.getRecId() != 0){
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage(masterTransactionsAudits.size()+" purchase transactions details audited successfully.");
			/*}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Failed to complete the purchase transaction audit");
			}*/
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to audit the purchase transaction details due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to audit the purchase transaction details.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get the students by school, school year and date with the ordered item details.**/
	@Override
	public ServiceResponse studentsWithOrderedItem(Integer schoolYear, String selectedDate, Long mealSchoolId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<StudentUser> studentUsers = new ArrayList<>(studentUserRepository
					.findByMealSchoolSchoolIdAndIsActiveAndSchoolYear(mealSchoolId, true, schoolYear));
			List<Object[]> orderedMealItems = transactionsDao.orderedItemsDetails(selectedDate, mealSchoolId);
			List<StudentDetailsWithOrderedItem> studentDetailsWithOrderedItems = buildStudentsWithOrderItems(studentUsers, 
					orderedMealItems, mealSchoolId);
			serviceResponse.setStudentDetailsWithOrderedItems(studentDetailsWithOrderedItems);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved all the Student details with ordered items details successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to retrieved the student details along with ordered items due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to retrieved the student details along with ordered items.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get the students by school, school year and date with the ordered item details.**/
	@Override
	public ServiceResponse studentsWithOrderedItemV2(Integer schoolYear, String selectedDate, Long mealSchoolId, Boolean isVersion2, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<StudentUser> studentUsers = new ArrayList<>(studentUserRepository
					.findByMealSchoolSchoolIdAndIsActiveAndSchoolYear(mealSchoolId, true, schoolYear));
			List<Object[]> orderedMealItems = null;
			if(isVersion2 != null && isVersion2)
				orderedMealItems = transactionsDao.orderedItemsDetailsV2(selectedDate, mealSchoolId, menuType);
			else
				orderedMealItems = transactionsDao.orderedItemsDetails(selectedDate, mealSchoolId);
			List<StudentDetailsWithOrderedItem> studentDetailsWithOrderedItems = buildStudentsWithOrderItems(studentUsers, 
					orderedMealItems, mealSchoolId);
			studentDetailsWithOrderedItems = buildNewReqFormat(studentDetailsWithOrderedItems);
			serviceResponse.setStudentDetailsWithOrderedItems(studentDetailsWithOrderedItems);
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			serviceResponse.setDateFormat(countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy");
			serviceResponse.setCurrencySymbol(countryDetail.getCurrencySymbol());
			serviceResponse.setPhoneValidation(countryDetail.getPhoneValidation());
			serviceResponse.setCountryCode(mealSchool.getCountryCode());
			serviceResponse.setIsPOSIdVerificationReq(schoolYearRepository.posVerStatus(mealSchoolId, schoolYear));
			Boolean isFreeMeal = schoolYearRepository.isSchoolFreeMeal(mealSchoolId, schoolYear);
			serviceResponse.setFreeMeal(isFreeMeal != null ? isFreeMeal : false);
			Map<String, String> moduleInfo = mealSchool.getModuleAccess();
			Map<String, String> moduleAccess = new HashMap<>();
			moduleAccess.put("PosDeposit", moduleInfo.get("POS Deposits") != null ? moduleInfo.get("POS Deposits") : "No");
			moduleAccess.put("LunchMenuManagement", moduleInfo.get("Lunch Menu Management") != null ? moduleInfo.get("Lunch Menu Management") : "Monthly");
			moduleAccess.put("LunchOrderManagement", moduleInfo.get("Lunch Menu Creation - Upload") != null ? moduleInfo.get("Lunch Menu Creation - Upload") : "No");
			if(moduleAccess.get("LunchOrderManagement").equalsIgnoreCase("No"))
				moduleAccess.put("LunchOrderManagement", moduleInfo.get("Lunch Menu Creation - Interactive") != null ? moduleInfo.get("Lunch Menu Creation - Interactive") : "No");
			moduleAccess.put("BreakfastMenuManagement ", moduleInfo.get("Breakfast Menu Management ") != null ? moduleInfo.get("Breakfast Menu Management ") : "Monthly");
			moduleAccess.put("BreakfastOrderManagement", moduleInfo.get("Breakfast Menu Creation - Upload") != null ? moduleInfo.get("Breakfast Menu Creation - Upload") : "No");
			if(moduleAccess.get("BreakfastOrderManagement").equalsIgnoreCase("No"))
				moduleAccess.put("BreakfastOrderManagement", moduleInfo.get("Breakfast Menu Creation - Interactive") != null ? moduleInfo.get("Breakfast Menu Creation - Interactive") : "No");
			moduleAccess.put("SnackMenuManagement", moduleInfo.get("Snack Menu Management") != null ? moduleInfo.get("Snack Menu Management") : "Monthly");
			moduleAccess.put("SnackOrderManagement", moduleInfo.get("Snack Menu Creation - Upload") != null ? moduleInfo.get("Snack Menu Creation - Upload") : "No");
			if(moduleAccess.get("SnackOrderManagement").equalsIgnoreCase("No"))
				moduleAccess.put("SnackOrderManagement", moduleInfo.get("Snack Menu Creation - Interactive") != null ? moduleInfo.get("Snack Menu Creation - Interactive") : "No");
			moduleAccess.put("MenuOrderType", moduleInfo.get("Menu Order Type") != null ? moduleInfo.get("Menu Order Type") : "Regular");
			moduleAccess.put("BatchUpdate", moduleInfo.get("Batch Update") != null ? moduleInfo.get("Batch Update") : "No");
			serviceResponse.setMapKeyVal(moduleAccess);
			serviceResponse.setGradesInfo(countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode()).getGradesMap());
			serviceResponse.setResponse((moduleInfo != null && moduleInfo.get("Instant Payment for Orders") != null && 
					moduleInfo.get("Instant Payment for Orders").equalsIgnoreCase("Yes")) ? true : false);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved all the Student details with ordered items details successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to retrieved the student details along with ordered items due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to retrieved the student details along with ordered items.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for get the available menu by grade based on selected date and mealSchoolId**/
	@Override
	public ServiceResponse menuAvailableByGrade(String selectedDate, Long mealSchoolId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Object[]> menuItems = transactionsDao.menuItemDetails(selectedDate, mealSchoolId);
			List<MenuItemDetails> menuItemDetailsList = buildMenuItemDetails(menuItems);
			Map<SchoolGrades, List<MenuItemDetails>> menuItemsByGrade = menuItemDetailsList.stream().collect(
					Collectors.groupingBy(MenuItemDetails::getGrade));
			serviceResponse.setMenuItemsByGrade(menuItemsByGrade);
			Set<BreakfastItems> breakfastItems = breakfastMasterRepository.findMenuBySchoolAndDate(mealSchoolId, 
					sdf.parse(selectedDate), Arrays.asList(MealType.BREAKFAST,MealType.SIDE,MealType.HOLIDAY));
			serviceResponse.setBreakfastItems(new ArrayList<>(breakfastItems));
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved menu details successfully.");
		}catch(Exception e){
			logger.error("Failed to retrieve the menu details due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to retrieve the menu details.");
			serviceResponse.setStatusCode(500);
		}
		return serviceResponse;
	}
	
	/**This method used for get the available menu by grade based on selected date and mealSchoolId**/
	@Override
	public ServiceResponse menuAvailableByGradeV2(String selectedDate, Long mealSchoolId, Boolean isVersion2, ItemTypeConstants menuType, Long locationId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Object[]> menuItems = null;
			if(isVersion2 != null && isVersion2)
				menuItems = transactionsDao.menuItemDetailsV3(selectedDate, mealSchoolId, menuType, locationId);
			else
				menuItems = transactionsDao.menuItemDetailsV2(selectedDate, mealSchoolId);
			List<MenuItemDetailsV2> menuItemDetailsList = buildMenuItemDetailsV2(menuItems);
			Map<SchoolGrades, List<MenuItemDetailsV2>> menuItemsByGrade = menuItemDetailsList.stream().collect(
					Collectors.groupingBy(MenuItemDetailsV2::getGrade));
			Map<SchoolGrades, Map<MealType, List<MenuItemDetailsV2>>> mealByTypeAndGrade = new HashMap<SchoolGrades, Map<MealType, List<MenuItemDetailsV2>>>();
			for (Map.Entry<SchoolGrades, List<MenuItemDetailsV2>> entry : menuItemsByGrade.entrySet()) {
				Map<MealType, List<MenuItemDetailsV2>> mealItemsByType = entry.getValue().stream().collect(
						Collectors.groupingBy(MenuItemDetailsV2::getMealType));
				mealByTypeAndGrade.put(entry.getKey(), mealItemsByType);
			}
			serviceResponse.setLunchByTypeAndGrade(mealByTypeAndGrade);
			/*List<MenuItemDetailsV2> breakfasts = new ArrayList<MenuItemDetailsV2>();
			List<MenuItemDetailsV2> breakfastList = null;
			if(isVersion2 != null && isVersion2){
				Set<MealCalendar> breakfastItems = summaryRepo.getBreakfastItem(mealSchoolId, 
						sdf.parse(selectedDate), ItemTypeConstants.Breakfast);
				//Map BreakfastItems to MenuItemDetailsV2 object
				breakfastList = mapBreakfastDataV2(breakfastItems);
			}else{
				Set<BreakfastItems> breakfastItems = breakfastMasterRepository.findMenuBySchoolAndDate(mealSchoolId, 
						sdf.parse(selectedDate), Arrays.asList(MealType.BREAKFAST,MealType.SIDE,MealType.HOLIDAY));
				//Map BreakfastItems to MenuItemDetailsV2 object
				breakfastList = mapBreakfastData(breakfastItems);
			}
			for(MenuItemDetailsV2 menuItemDetail : breakfastList){
				if(menuItemDetail.getMealType().toString().equalsIgnoreCase(MealType.SIDE.toString())){
					String sideName = menuItemDetail.getMealName() != null ? menuItemDetail.getMealName() : null;
					if(sideName != null && sideName.length() > 0){
						for(String value : Arrays.asList(sideName.split("\\s*,\\s*"))){
							MenuItemDetailsV2 menuItemDetail1 = new MenuItemDetailsV2();
							BeanUtils.copyProperties(menuItemDetail1, menuItemDetail);
							menuItemDetail1.setMealName(value);
							breakfasts.add(menuItemDetail1);
						}
					}else
						breakfasts.add(menuItemDetail);
				}else
					breakfasts.add(menuItemDetail);
			}
			Map<MealType, List<MenuItemDetailsV2>> breakfastByType = breakfasts.stream().collect(
					Collectors.groupingBy(MenuItemDetailsV2::getMealType));
			serviceResponse.setBreakfastByType(breakfastByType);*/
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved menu details successfully.");
		}catch(Exception e){
			logger.error("Failed to retrieve the menu details due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to retrieve the menu details.");
			serviceResponse.setStatusCode(500);
		}
		return serviceResponse;
	}

	/**This method used for transfer the balance between siblings**/
	@Override
	public ServiceResponse transferBalanceSibling(AccBalanceTransferSibling accBalanceTransferSibling) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			accBalanceTransferSibling = buildTransactionTransferObject(accBalanceTransferSibling);
			MasterTransactionsAudit masterTransactionsAudit = transactionsDao.transferBalanceSibling(accBalanceTransferSibling);
			//if(masterTransactionsAudit.getRecId() != null && masterTransactionsAudit.getRecId() != 0){
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(masterTransactionsAudit.getMealSchoolId()));
			paymentRecieptPdf.paymentReceiptGenerate(masterTransactionsAudit,currencySymbol);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Lunch Balance transfer is successfully.");
			/*}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Failed to complete the transfer transaction");
			}*/
			logger.info(serviceResponse.getStatusMessage());
			//send email regarding payment deposit
		}catch(Exception e){
			logger.error("Failed during transfer the balance between siblings due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed during transfer the balance between siblings.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get the lunch / breakfast purchased item students details
	 * @throws ParseException **/
	@Override
	public Map<ItemTypeConstants, List<BigInteger>> purchasedMenuStudents(Long mealSchoolId, String selectedDate,Boolean isSysDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
		String tz = mealSchool.getSchoolTimezone().toString();
		//sdf.setTimeZone(TimeZone.getTimeZone(mealSchool.getSchoolTimezone().toString()));
		if(isSysDate)
			selectedDate=du.formatDateToString(sdf.parse(selectedDate), "yyyy-MM-dd", tz);
		String dtStart = selectedDate+" 00:00:00";
		String dtEnd = selectedDate+" 23:59:59";
		String startDateTime = du.formatDateToStringUTC(sdf.parse(dtStart), "yyyy-MM-dd HH:mm:ss", tz);
		String endDateTime = du.formatDateToStringUTC(sdf.parse(dtEnd), "yyyy-MM-dd HH:mm:ss", tz);
		logger.info("startDateTime: "+startDateTime+", endDateTime: "+endDateTime);
		List<BigInteger> lunchStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Lunch.toString(), TransactionType.Purchase.toString(),"Regular");
		List<BigInteger> lunchExtraStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Lunch.toString(), TransactionType.Purchase.toString(),"ALaCarte");
		List<BigInteger> breakfastStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Breakfast.toString(), TransactionType.Purchase.toString(),"Regular");
		List<BigInteger> breakfastExtraStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Breakfast.toString(), TransactionType.Purchase.toString(),"ALaCarte");
		List<BigInteger> milkStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Milk.toString(), TransactionType.Purchase.toString(),"Regular");
		List<BigInteger> snackStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Snack.toString(), TransactionType.Purchase.toString(),"Regular");
		List<BigInteger> snackExtraStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Snack.toString(), TransactionType.Purchase.toString(),"ALaCarte");
		List<BigInteger> dinnerStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Dinner.toString(), TransactionType.Purchase.toString(),"Regular");
		List<BigInteger> dinnerExtraStudents = transactionsDao.menuPurchasedStudents(mealSchoolId, startDateTime, endDateTime, 
				PurchaseItemType.Dinner.toString(), TransactionType.Purchase.toString(),"ALaCarte");
		Map<ItemTypeConstants, List<BigInteger>> studentsByType = new HashMap<ItemTypeConstants, List<BigInteger>>();
		studentsByType.put(ItemTypeConstants.Lunch, lunchStudents);
		studentsByType.put(ItemTypeConstants.Breakfast, breakfastStudents);
		studentsByType.put(ItemTypeConstants.Milk, milkStudents);
		studentsByType.put(ItemTypeConstants.Snack, snackStudents);
		studentsByType.put(ItemTypeConstants.Dinner, dinnerStudents);
		studentsByType.put(ItemTypeConstants.LunchALaCarte, lunchExtraStudents);
		studentsByType.put(ItemTypeConstants.BreakfastALaCarte, breakfastExtraStudents);
		studentsByType.put(ItemTypeConstants.SnackALaCarte, snackExtraStudents);
		studentsByType.put(ItemTypeConstants.DinnerALaCarte, dinnerExtraStudents);
		logger.info("purchasedMenuStudents service method executed successfully");
		return studentsByType;
	}
	
	/**This method used for ordered the lunch but not served to the students**/
	@Override
	public ServiceResponse lunchNotServedStudents(Long mealSchoolId, String selectedDate, Integer schoolYear) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String dtStart = selectedDate+" 00:00:00";
			String dtEnd = selectedDate+" 23:59:59";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			//sdf.setTimeZone(TimeZone.getTimeZone(mealSchool.getSchoolTimezone().toString()));
			String startDateTime = du.formatDateToStringUTC(sdf.parse(dtStart), "yyyy-MM-dd'T'HH:mm:ss", mealSchool.getSchoolTimezone().toString());
			String endDateTime = du.formatDateToStringUTC(sdf.parse(dtEnd), "yyyy-MM-dd'T'HH:mm:ss", mealSchool.getSchoolTimezone().toString());
			logger.info("startDateTime: "+startDateTime+", endDateTime: "+endDateTime);
			List<Object[]> lunchNotServedStudentsObj = transactionsDao.lunchNotServedStudents(selectedDate, mealSchoolId, 
					startDateTime, endDateTime);
			List<LunchNotServedStudents> lunchNotServedStudentList = builLunchNotServedStudents(lunchNotServedStudentsObj);
			lunchNotServedStudentList.sort(Comparator.comparing(LunchNotServedStudents::getStudentLName));
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved the lunch not served student details successfully.");
			serviceResponse.setLunchNotServedStudentList(lunchNotServedStudentList);
		}catch(Exception e){
			logger.error("Failed to get the ordered lunch, but not served to the students due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get the ordered lunch, but not served to the students.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for import student's balance to add/override the amount based on type**/
	@Override
	public ServiceResponse importStudentsBalance(MultipartFile file, Long mealSchoolId, Integer schoolYear, String type) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<StudentBalanceImportResp> studentBalanceImportRespList = excelReadUtil.studentUsersBalance(file);
			String loggedUser = "";
			if(SecurityContextHolder.getContext() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			serviceResponse = transactionsDao.importStudentsBalance(studentBalanceImportRespList, loggedUser, 
					mealSchoolId, schoolYear, type);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to update the student balance due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update the student balance.");
		}
		return serviceResponse;
	}

	/**This method used for stripe agreement acceptance**/
	@Override
	public ServiceResponse acceptStripeAgreement(Long mealSchoolId, String systemIP) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			if(mealSchool.getStripeAccountId() == null || mealSchool.getStripeAccountId().isEmpty() 
					|| mealSchool.isStripeAcceptance()){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				if(mealSchool.isStripeAcceptance())
					serviceResponse.setStatusMessage("Already accepted stripe agreement.");
				else
					serviceResponse.setStatusMessage("No stripe account there for this school to accept agreement.");
				return serviceResponse;
			}
			boolean status = stripeUtill.stripeAgreementAcceptance(mealSchool.getStripeAccountId(), 
					systemIP);
			if(status){
				mealSchool.setStripeAcceptance(true);
				mealSchoolRepository.save(mealSchool);
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Agreement accepted successfully.");
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to accept the stripe agreement.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error("Failed to accept the stripe agreement due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for build the master transactions audit object data
	 * @throws Exception **/
	private MasterTransactionsAudit buildTransactionPaymentObject(MasterTransactionsAudit masterTransactionsAudit) throws Exception{
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(masterTransactionsAudit.getMealSchoolId());
		if(mealSchool == null || mealSchool.getSchoolId() == null)
			throw new Exception("Meal School Id is not valid");
		masterTransactionsAudit.setMealSchool(mealSchool);
		if(masterTransactionsAudit.getTransactionType().toString().equalsIgnoreCase(TransactionType.Refund.toString()))
			masterTransactionsAudit.setNote("Amount refunded by school");
		else if(masterTransactionsAudit.getTransactionType().toString().equalsIgnoreCase(TransactionType.Adjustment.toString()))
			masterTransactionsAudit.setNote("Balance adjusted by school");
		else if(masterTransactionsAudit.getTransactionType().toString().equalsIgnoreCase(TransactionType.Event.toString()))
			masterTransactionsAudit.setNote("Amount paid for Events");
		else if(masterTransactionsAudit.getNote() == null)
			masterTransactionsAudit.setNote("Amount paid through "+masterTransactionsAudit.getPaymentType());
		masterTransactionsAudit.setTransactionDateTime(new Date());
		if(SecurityContextHolder.getContext().getAuthentication() != null)
			masterTransactionsAudit.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
		masterTransactionsAudit.setCreatedOn(new Date());
		Set<StudentWiseTransaction> studentWiseTransactions = new HashSet<StudentWiseTransaction>();
		StudentUser studentUser = null;
		for(StudentWiseTransaction studentWiseTransaction : masterTransactionsAudit.getStudentWiseTransactions()){
			studentUser = studentUserRepository.findByUserIdAndIsActive(studentWiseTransaction.getStudentRecId(), true);
			if(studentUser == null || studentUser.getUserId() == null)
				throw new Exception("Student does not exist with id: "+studentWiseTransaction.getStudentRecId());
			studentWiseTransaction.setStudentUser(studentUser);
			studentWiseTransaction.setStudentFName(studentUser.getFirstName());
			studentWiseTransaction.setStudentLName(studentUser.getLastName());
			if(masterTransactionsAudit.getTransactionType().toString().equalsIgnoreCase(TransactionType.Event.toString()))
				studentWiseTransaction.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance())));
			else if(masterTransactionsAudit.getTransactionType().toString().equalsIgnoreCase(TransactionType.Refund.toString()) 
					|| (masterTransactionsAudit.getTransactionType().toString().equalsIgnoreCase(TransactionType.Adjustment.toString()) 
							&& masterTransactionsAudit.getPurchaseItemType() != null && masterTransactionsAudit.getPurchaseItemType().toString().equalsIgnoreCase(PurchaseItemType.AdjustmentDR.toString())))
				studentWiseTransaction.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance()-studentWiseTransaction.getTransactionAmount())));
			else
				studentWiseTransaction.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance()+studentWiseTransaction.getTransactionAmount())));
			studentWiseTransaction.setGrade(studentUser.getGradeName());
			studentWiseTransactions.add(studentWiseTransaction);
		}
		if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null 
				&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().toUpperCase().contains("ROLE_PARENT"))
			masterTransactionsAudit.setParentUserEmails(SecurityContextHolder.getContext().getAuthentication().getName());
		if(masterTransactionsAudit.getParentUserEmails() == null || masterTransactionsAudit.getParentUserEmails().trim().isEmpty()){
			if(studentUser.getParentuser().getParentAltEmail() != null && 
					!studentUser.getParentuser().getParentAltEmail().isEmpty())
				masterTransactionsAudit.setParentUserEmails(studentUser.getParentuser().getUserName()+","+
						studentUser.getParentuser().getParentAltEmail());
			else
				masterTransactionsAudit.setParentUserEmails(studentUser.getParentuser().getUserName());
		}
		masterTransactionsAudit.setStudentWiseTransactions(studentWiseTransactions);
		if(masterTransactionsAudit.isDirectPosDeposit())
			masterTransactionsAudit.setPosDeposit(true);
		return masterTransactionsAudit;
	}
	
	/**This method used for build the object for audit the purchase transaction details
	 * @throws Exception **/
	private List<MasterTransactionsAudit> buildPurchaseTransactionObject(
			List<MasterTransactionsAudit> masterTransactionsAudits, Long mealSchoolId, Map<ItemTypeConstants, List<BigInteger>> servedStds, Date transactionDt) throws Exception{
		List<MasterTransactionsAudit> masterTransactionsAuditFinal = new ArrayList<MasterTransactionsAudit>();
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
		String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
		if(mealSchool == null || mealSchool.getSchoolId() == null)
			throw new Exception("Meal School Id is not valid");
		String loggedUser = "";
		if(SecurityContextHolder.getContext().getAuthentication() != null)
			loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
		StudentUser studentUser = null;
		Map<Long, StudentUser> studentMap = new HashMap<Long, StudentUser>();
		List<BigInteger> lunchServedStdIds = servedStds.get(ItemTypeConstants.Lunch);
		List<BigInteger> breakfastServedStdIds = servedStds.get(ItemTypeConstants.Breakfast);
		List<BigInteger> milkServedStdIds = servedStds.get(ItemTypeConstants.Milk);
		List<BigInteger> snackServedStdIds = servedStds.get(ItemTypeConstants.Snack);
		List<BigInteger> dinnerServedStdIds = servedStds.get(ItemTypeConstants.Dinner);
		Set<StudentWiseTransaction> studentWiseTransactions = null;
		for(MasterTransactionsAudit masterTransactionsAudit : masterTransactionsAudits){
			studentUser = null;
			studentWiseTransactions = new LinkedHashSet<>();
			int i = 0;
			String itemType = masterTransactionsAudit.getPurchaseItemType().toString();
			List<BigInteger> servedStdIds = null;
			switch(itemType){
				case "Lunch" : servedStdIds = lunchServedStdIds; break;
				case "Breakfast" : servedStdIds = breakfastServedStdIds; break;
				case "Milk" : servedStdIds = milkServedStdIds; break;
				case "Snack" : servedStdIds = snackServedStdIds; break;
				case "Dinner" : servedStdIds = dinnerServedStdIds; break;
				default : servedStdIds = null; break;
			}
			for(StudentWiseTransaction studentWiseTransaction : masterTransactionsAudit.getStudentWiseTransactions()){
				//studentWiseTransaction = new ArrayList<>(masterTransactionsAudit.getStudentWiseTransactions()).get(0);
				//if(servedStdIds == null|| itemType.equalsIgnoreCase("Milk")){
					if((studentWiseTransaction.getMealType() == null || studentWiseTransaction.getMealType().equalsIgnoreCase("Regular")) && servedStdIds.contains(BigInteger.valueOf(studentWiseTransaction.getStudentRecId())))
						studentWiseTransaction.setMealType("Additional");
					if(i == 0){
						masterTransactionsAudit.setMealSchool(mealSchool);
						if(masterTransactionsAudit.isItemTaken())
							masterTransactionsAudit.setNote(masterTransactionsAudit.getPurchaseItemType()+" purchased with the total amount: "
								+currencySymbol+studentWiseTransaction.getTransactionAmount());
						else
							masterTransactionsAudit.setNote(masterTransactionsAudit.getPurchaseItemType()+" ordered but not taken "
									+ "with the total amount: "+currencySymbol+studentWiseTransaction.getTransactionAmount());
						masterTransactionsAudit.setTransactionDateTime(transactionDt);
						masterTransactionsAudit.setCreatedBy(loggedUser);
						masterTransactionsAudit.setCreatedOn(new Date());
						if(studentMap.get(studentWiseTransaction.getStudentRecId()) != null)
							studentUser = studentMap.get(studentWiseTransaction.getStudentRecId());
						else
							studentUser = studentUserRepository.findByUserIdAndIsActive(studentWiseTransaction.getStudentRecId(),
									true);
						if(studentUser == null || studentUser.getUserId() == null)
							throw new Exception("Student does not exist with id: "+studentWiseTransaction.getStudentRecId());
						
					}
					//studentWiseTransaction.setStudentUser(studentUser);
					studentWiseTransaction.setStudentFName(studentUser.getFirstName());
					studentWiseTransaction.setStudentLName(studentUser.getLastName());
					if(i == 0 && masterTransactionsAudit.getPayToAmt() != null && masterTransactionsAudit.getPayToAmt() > 0){
						studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance() + 
								masterTransactionsAudit.getPayToAmt())));
						masterTransactionsAudit.setAccBalance(studentUser.getAccBalance());
					}
					studentWiseTransaction.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance() - 
							studentWiseTransaction.getTransactionAmount())));
					studentUser.setAccBalance(studentWiseTransaction.getFinalBalance());
					studentUser.setModifiedBy(masterTransactionsAudit.getCreatedBy());
					studentUser.setModifiedOn(new Date());
					studentMap.put(studentUser.getUserId(), studentUser);
					studentWiseTransaction.setStudentUser(studentUser);
					studentWiseTransaction.setGrade(studentUser.getGradeName());
					if(studentWiseTransaction.getChargedAmt() != null && studentWiseTransaction.getChargedAmt() > 0){
						masterTransactionsAudit.setChargedAmt(studentWiseTransaction.getChargedAmt());
						studentWiseTransaction.setChargedAmt(0.0);
					}
					/*studentWiseTransaction.setEligStatus(studentUser.getIsFreeMealEligible() ? 0 : 
						(studentUser.getIsReducePriceEligible() ? 1: 2));
					if(masterTransactionsAudit.getPurchaseItemType().toString().equalsIgnoreCase(PurchaseItemType.Breakfast.toString()) 
							&& studentUser.isBeforeCare()){
						studentWiseTransaction.setEligStatus(0);
					}*/
					studentWiseTransactions.add(studentWiseTransaction);
					logger.info("CCAmt:"+studentWiseTransaction.getCcAmt()+" & ppAmt::"+studentWiseTransaction.getPrepaidAmt()+
							" & chargedAmt:"+masterTransactionsAudit.getChargedAmt()+" & trxAmt"+studentWiseTransaction.getTransactionAmount()+
							" & payToAmt:"+masterTransactionsAudit.getPayToAmt());
				/*}else{
					logger.info(masterTransactionsAudit.getPurchaseItemType()+" already served for this student record id: "+studentWiseTransaction.getStudentRecId()+" that's why skipping from the process.");
				}*/
				i++;
			}
			masterTransactionsAudit.setStudentWiseTransactions(studentWiseTransactions);
			masterTransactionsAuditFinal.add(masterTransactionsAudit);
		}
		return masterTransactionsAuditFinal;
	}
	
	/**This method used for build the ordered items details with student details**/
	private List<StudentDetailsWithOrderedItem> buildStudentsWithOrderItems(List<StudentUser> studentUsers, 
			List<Object[]> orderedItems, Long mealSchoolId){
		List<StudentDetailsWithOrderedItem> studentDetailsWithOrderedItems = new ArrayList<StudentDetailsWithOrderedItem>();
		Map<Long, List<OrderedMenuItemDetails>> orderedMenuByStudent = buildOrderedMenuByStudent(orderedItems); 
		StudentDetailsWithOrderedItem studentDetailsWithOrderedItem = null;
		List<OrderedMenuItemDetails> orderedMenuItemDetails = null;
		Double lowBalMinCriteria = (double) 0;
		Set<LowBalanceSchoolSetting> lowBalanceSchoolSettings = lowBalanceSchoolSettingRepository.findByMealSchoolSchoolId(mealSchoolId);
		if(lowBalanceSchoolSettings != null && lowBalanceSchoolSettings.size() > 0)
			lowBalMinCriteria = new ArrayList<>(lowBalanceSchoolSettings).get(0).getLowBalMinCriteria();
		for(StudentUser studentUser : studentUsers){
			studentDetailsWithOrderedItem = new StudentDetailsWithOrderedItem();
			studentDetailsWithOrderedItem.setAllergies(studentUser.getAllergies());
			studentDetailsWithOrderedItem.setAdditionalNotes(studentUser.getAdditionalNotes());
			studentDetailsWithOrderedItem.setBarCode(studentUser.getBarcode());
			studentDetailsWithOrderedItem.setFirstName(studentUser.getFirstName());
			studentDetailsWithOrderedItem.setGradeName(studentUser.getGradeName());
			studentDetailsWithOrderedItem.setLastName(studentUser.getLastName());
			studentDetailsWithOrderedItem.setAccBalance(studentUser.getAccBalance());
			studentDetailsWithOrderedItem.setImage(studentUser.getImage());
			if(lowBalMinCriteria != null && studentUser.getAccBalance() <= lowBalMinCriteria)
				studentDetailsWithOrderedItem.setIsThresholdBal(true);
			studentDetailsWithOrderedItem.setThresholdAmt(lowBalMinCriteria != null ? lowBalMinCriteria : 0);
			studentDetailsWithOrderedItem.setMealSchoolId(studentUser.getMealSchool().getSchoolId());
			studentDetailsWithOrderedItem.setStudentId(studentUser.getStudentId());
			studentDetailsWithOrderedItem.setTeacherName(studentUser.getTeacherName());
			studentDetailsWithOrderedItem.setStudentRecId(studentUser.getUserId());
			studentDetailsWithOrderedItem.setIsBeforeCare(studentUser.isBeforeCare());
			studentDetailsWithOrderedItem.setHasMilkCard(studentUser.isHasMilkCard());
			studentDetailsWithOrderedItem.setIsEnrollBCAndACPkt(studentUser.getIsEnrollBCAndACPkt());
			/*if(studentUser.getSchoolStudentId() != null && !studentUser.getSchoolStudentId().equalsIgnoreCase(""))
				studentDetailsWithOrderedItem.setSchoolStudentId(studentUser.getSchoolStudentId());
			else*/
			studentDetailsWithOrderedItem.setSchoolStudentId(studentUser.getStudentId());
			orderedMenuItemDetails = orderedMenuByStudent.get(studentUser.getUserId());
			studentDetailsWithOrderedItem.setIsFreeMealEligible(studentUser.getIsFreeMealEligible());
			studentDetailsWithOrderedItem.setIsReducePriceEligible(studentUser.getIsReducePriceEligible());
			studentDetailsWithOrderedItem.setPin(studentUser.getPin());
			if(orderedMenuItemDetails != null && orderedMenuItemDetails.size() > 0){
				/*studentDetailsWithOrderedItem.setIsFreeMealEligible(orderedMenuItemDetails.get(0).getIsFreeMealEligible());
				studentDetailsWithOrderedItem.setIsReducePriceEligible(orderedMenuItemDetails.get(0).getIsReducePriceEligible());*/
				studentDetailsWithOrderedItem.setMenuItemDetails(orderedMenuItemDetails);
			}
			studentDetailsWithOrderedItems.add(studentDetailsWithOrderedItem);
		}
		return studentDetailsWithOrderedItems;
	}
	
	/**This method used for build the ordered menu items object by student record id**/
	private Map<Long, List<OrderedMenuItemDetails>> buildOrderedMenuByStudent(List<Object[]> orderedItems){
		Map<Long, List<OrderedMenuItemDetails>> orderedItemsByStudent = new HashMap<Long, List<OrderedMenuItemDetails>>();
		List<OrderedMenuItemDetails> itemList = null;
		Boolean menuEligibleForReducedPrice = false;
		OrderedMenuItemDetails orderedMenuItemDetails = null;
		for(Object[]  obj : orderedItems){
			if(obj[0] != null){
				itemList = orderedItemsByStudent.get(Long.parseLong(obj[0].toString()));
				if(itemList == null || itemList.size() < 1)
					itemList = new ArrayList<OrderedMenuItemDetails>();
				if(obj[9] != null)
					menuEligibleForReducedPrice = obj[9] != null ? Boolean.parseBoolean(obj[9].toString()) : false;
				orderedMenuItemDetails = new OrderedMenuItemDetails();
				orderedMenuItemDetails.setIsFreeMealEligible(obj[1] != null ? Boolean.parseBoolean(obj[1].toString()) : false);
				orderedMenuItemDetails.setIsReducePriceEligible(obj[2] != null ? Boolean.parseBoolean(obj[2].toString()) : false);
				orderedMenuItemDetails.setMenuId(obj[3] != null ? Long.parseLong(obj[3].toString()) : null);
				orderedMenuItemDetails.setItemName(obj[4] != null ? obj[4].toString() : null);
				orderedMenuItemDetails.setItemOriginalPrice(obj[5] != null ? Double.parseDouble(obj[5].toString()) : 0.0);
				orderedMenuItemDetails.setReducedPrice(obj[6] != null ? Double.parseDouble(obj[6].toString()) : 0.0);
				orderedMenuItemDetails.setMealReducedPriceElig(menuEligibleForReducedPrice);
				if(orderedMenuItemDetails.getIsReducePriceEligible() && menuEligibleForReducedPrice)
					orderedMenuItemDetails.setItemFinalPrice(obj[6] != null ? Double.parseDouble(obj[6].toString()) : 0.0);
				else if(orderedMenuItemDetails.getIsFreeMealEligible())
					orderedMenuItemDetails.setItemFinalPrice(0.0);
				else
					orderedMenuItemDetails.setItemFinalPrice(orderedMenuItemDetails.getItemOriginalPrice());
				orderedMenuItemDetails.setItemDesc(obj[7] != null ? obj[7].toString() : null);
				orderedMenuItemDetails.setItemtype(obj[8] != null ? MealType.valueOf(obj[8].toString()) : null);
				itemList.add(orderedMenuItemDetails);
				orderedItemsByStudent.put(Long.parseLong(obj[0].toString()), itemList);
			}	
		}
		return orderedItemsByStudent;
	}
	
	/**This method used for build the available menu items data**/
	private List<MenuItemDetails> buildMenuItemDetails(List<Object[]> menuItems){
		List<MenuItemDetails> menuItemDetailsList = new ArrayList<MenuItemDetails>();
		MenuItemDetails menuItemDetails = null;
		for(Object[] obj : menuItems){
			menuItemDetails = new MenuItemDetails();
			menuItemDetails.setMealId(obj[0] != null ? Long.parseLong(obj[0].toString()) : 0);
			menuItemDetails.setMealDesc(obj[1] != null ? obj[1].toString() : null);
			menuItemDetails.setMealPrice(obj[2] != null ? Double.parseDouble(obj[2].toString()) : 0.0);
			menuItemDetails.setReducedPrice(obj[3] != null ? Double.parseDouble(obj[3].toString()) : 0.0);
			menuItemDetails.setMealName(obj[4] != null ? obj[4].toString() : null);
			menuItemDetails.setMealType(obj[5] != null ? MealType.valueOf(obj[5].toString()) : null);
			menuItemDetails.setReducedPriceEligStatus(obj[6] != null ? Boolean.parseBoolean(obj[6].toString()) : false);
			menuItemDetails.setGrade(obj[7] != null ? SchoolGrades.valueOf(obj[7].toString()) : null);
			menuItemDetailsList.add(menuItemDetails);
		}
		return menuItemDetailsList;
	}
	
	/**This method used for build the available menu items data
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException **/
	private List<MenuItemDetailsV2> buildMenuItemDetailsV2(List<Object[]> menuItems) throws IllegalAccessException, InvocationTargetException{
		List<MenuItemDetailsV2> menuItemDetailsList = new ArrayList<MenuItemDetailsV2>();
		MenuItemDetailsV2 menuItemDetails = null;
		for(Object[] obj : menuItems){
			menuItemDetails = new MenuItemDetailsV2();
			menuItemDetails.setMealId(obj[0] != null ? Long.parseLong(obj[0].toString()) : 0);
			menuItemDetails.setMealDesc(obj[1] != null ? obj[1].toString() : null);
			menuItemDetails.setMealPrice(obj[2] != null ? Double.parseDouble(obj[2].toString()) : 0.0);
			menuItemDetails.setReducedPrice(obj[3] != null ? Double.parseDouble(obj[3].toString()) : 0.0);
			menuItemDetails.setMealType(obj[5] != null ? MealType.valueOf(obj[5].toString()) : null);
			if(menuItemDetails.getMealType().toString().equalsIgnoreCase("EXTRA"))
				menuItemDetails.setReducedPrice(menuItemDetails.getMealPrice());
			menuItemDetails.setReducedPriceEligStatus(obj[6] != null ? Boolean.parseBoolean(obj[6].toString()) : false);
			menuItemDetails.setGrade(obj[7] != null ? SchoolGrades.valueOf(obj[7].toString()) : null);
			if(menuItemDetails.getMealType().toString().equalsIgnoreCase(MealType.SIDE.toString())){
				String sideName = obj[4] != null ? obj[4].toString() : null;
				if(sideName != null && sideName.length() > 0){
					for(String value : Arrays.asList(sideName.split("\\s*,\\s*"))){
						MenuItemDetailsV2 menuItemDetails1 = new MenuItemDetailsV2();
						BeanUtils.copyProperties(menuItemDetails1, menuItemDetails);
						menuItemDetails1.setMealName(value);
						menuItemDetailsList.add(menuItemDetails1);
					}
				}else{
					menuItemDetails.setMealName(sideName);
					menuItemDetailsList.add(menuItemDetails);
				}
			}else{
				menuItemDetails.setMealName(obj[4] != null ? obj[4].toString() : null);
				menuItemDetailsList.add(menuItemDetails);
			}
		}
		return menuItemDetailsList;
	}
	
	/**This method used for build the master transactions audit object data using transfer amount between siblings
	 * @throws Exception **/
	private AccBalanceTransferSibling buildTransactionTransferObject(AccBalanceTransferSibling accBalanceTransferSibling) throws Exception{
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(accBalanceTransferSibling.getSourceTransferTransaction().
				getMealSchoolId());
		if(mealSchool == null || mealSchool.getSchoolId() == null)
			throw new Exception("Meal School Id is not valid");
		MasterTransactionsAudit sourceMasterTransactionsAudit = accBalanceTransferSibling.getSourceTransferTransaction();
		MasterTransactionsAudit targetMasterTransactionsAudit = accBalanceTransferSibling.getTragetTransferTransaction();
		sourceMasterTransactionsAudit.setMealSchool(mealSchool);
		sourceMasterTransactionsAudit.setNote("Amount transfered to the Sibling's account");
		sourceMasterTransactionsAudit.setTransactionDateTime(new Date());
		sourceMasterTransactionsAudit.setTransactionType(TransactionType.Transfer);
		sourceMasterTransactionsAudit.setPurchaseItemType(PurchaseItemType.TransferDR);
		targetMasterTransactionsAudit.setMealSchool(mealSchool);
		targetMasterTransactionsAudit.setNote("Amount recieved from the Sibling's account");
		targetMasterTransactionsAudit.setTransactionDateTime(new Date());
		targetMasterTransactionsAudit.setTransactionType(TransactionType.Deposit);
		targetMasterTransactionsAudit.setPaymentType(PaymentType.TransferCR);
		Double masterAccountTransferAmt = 0.0;
		Double childAccountTransferAmt = 0.0;
		if(SecurityContextHolder.getContext().getAuthentication() != null){
			targetMasterTransactionsAudit.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
			sourceMasterTransactionsAudit.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
		}
		sourceMasterTransactionsAudit.setCreatedOn(new Date());
		targetMasterTransactionsAudit.setCreatedOn(new Date());
		Set<StudentWiseTransaction> sourceStudentWiseTransactions = new HashSet<StudentWiseTransaction>();
		StudentUser studentUser = null;
		for(StudentWiseTransaction studentWiseTransaction : sourceMasterTransactionsAudit.getStudentWiseTransactions()){
			studentUser = studentUserRepository.findByUserIdAndIsActive(studentWiseTransaction.getStudentRecId(),
					true);
			if(studentUser == null || studentUser.getUserId() == null)
				throw new Exception("Student does not exist with id: "+studentWiseTransaction.getStudentRecId());
			studentWiseTransaction.setStudentUser(studentUser);
			studentWiseTransaction.setStudentFName(studentUser.getFirstName());
			studentWiseTransaction.setStudentLName(studentUser.getLastName());
			studentWiseTransaction.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance() - 
					studentWiseTransaction.getTransactionAmount())));
			if(studentWiseTransaction.getFinalBalance() < 0)
				throw new Exception("Transfer can not be proceed as amount exceeded the limit");
			masterAccountTransferAmt = masterAccountTransferAmt + studentWiseTransaction.getTransactionAmount();
			studentWiseTransaction.setGrade(studentUser.getGradeName());
			sourceStudentWiseTransactions.add(studentWiseTransaction);
			targetMasterTransactionsAudit.setSourceTransferAccInfo(studentUser.getLastName()+", "+studentUser.getFirstName()+" ["+studentUser.getStudentId()+"]");
		}
		Set<StudentWiseTransaction> targetStudentWiseTransactions = new HashSet<StudentWiseTransaction>();
		for(StudentWiseTransaction studentWiseTransaction : targetMasterTransactionsAudit.getStudentWiseTransactions()){
			studentUser = studentUserRepository.findByUserIdAndIsActive(studentWiseTransaction.getStudentRecId(),
					true);
			if(studentUser == null || studentUser.getUserId() == null)
				throw new Exception("Student does not exist with id: "+studentWiseTransaction.getStudentRecId());
			studentWiseTransaction.setStudentUser(studentUser);
			studentWiseTransaction.setStudentFName(studentUser.getFirstName());
			studentWiseTransaction.setStudentLName(studentUser.getLastName());
			studentWiseTransaction.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance() + 
					studentWiseTransaction.getTransactionAmount())));
			childAccountTransferAmt = childAccountTransferAmt + studentWiseTransaction.getTransactionAmount();
			studentWiseTransaction.setGrade(studentUser.getGradeName());
			targetStudentWiseTransactions.add(studentWiseTransaction);
		}
		masterAccountTransferAmt = Double.parseDouble(String.format("%.2f", masterAccountTransferAmt));
		if(!masterAccountTransferAmt.equals(Double.parseDouble(String.format("%.2f", childAccountTransferAmt))))
			throw new Exception("Amount transfer is not matched with total amount");
		if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null 
				&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().toUpperCase().contains("ROLE_PARENT"))
			targetMasterTransactionsAudit.setParentUserEmails(SecurityContextHolder.getContext().getAuthentication().getName());
		if(targetMasterTransactionsAudit.getParentUserEmails() == null || targetMasterTransactionsAudit.getParentUserEmails().trim().isEmpty()){
			if(studentUser.getParentuser().getParentAltEmail() != null && 
					!studentUser.getParentuser().getParentAltEmail().isEmpty())
				targetMasterTransactionsAudit.setParentUserEmails(studentUser.getParentuser().getUserName()+","+
						studentUser.getParentuser().getParentAltEmail());
			else
				targetMasterTransactionsAudit.setParentUserEmails(studentUser.getParentuser().getUserName());
		}
		sourceMasterTransactionsAudit.setStudentWiseTransactions(sourceStudentWiseTransactions);
		targetMasterTransactionsAudit.setStudentWiseTransactions(targetStudentWiseTransactions);
		accBalanceTransferSibling.setSourceTransferTransaction(sourceMasterTransactionsAudit);
		accBalanceTransferSibling.setTragetTransferTransaction(targetMasterTransactionsAudit);
		return accBalanceTransferSibling;
	}

	/**Build the object of those students who ordered lunch but not served**/
	private List<LunchNotServedStudents> builLunchNotServedStudents(List<Object[]> lunchNotServedStudentsObj){
		List<LunchNotServedStudents> lunchNotServedStudentList = new ArrayList<LunchNotServedStudents>();
		LunchNotServedStudents lunchNotServedStudents = null;
		if(lunchNotServedStudentsObj != null)
			for(Object[] obj : lunchNotServedStudentsObj){
				if(obj[0] != null && obj[3] != null){
					lunchNotServedStudents = new LunchNotServedStudents();
					lunchNotServedStudents.setStudentRecId(Long.parseLong(obj[0].toString()));
					lunchNotServedStudents.setMealName(obj[1] != null ? obj[1].toString() : "");
					lunchNotServedStudents.setStudentFName(obj[2] != null ? obj[2].toString() : "");
					lunchNotServedStudents.setSchoolGrades(SchoolGrades.valueOf(obj[3].toString()));
					lunchNotServedStudents.setStudentLName(obj[4] != null ? obj[4].toString() : "");
					lunchNotServedStudents.setStudentId(obj[5] != null ? obj[5].toString() : "");
					lunchNotServedStudentList.add(lunchNotServedStudents);
				}
			}
		return lunchNotServedStudentList;
	}

	/**This method used for refund the amount of student**/
	@Override
	public ServiceResponse refundAmount(MasterTransactionsAudit masterTransactionsAudit) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			masterTransactionsAudit = buildTransactionPaymentObject(masterTransactionsAudit);
			MasterTransactionsAudit masterTransactionsAuditResp = transactionsDao.depositAmtThruOffline(masterTransactionsAudit);
			if(masterTransactionsAuditResp.getRecId() != null && masterTransactionsAuditResp.getRecId() != 0){
				masterTransactionsAudit.setRecId(masterTransactionsAuditResp.getRecId());
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Amount has been refunded successfully.");
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Failed to Refund the amount.");
			}
			logger.info(serviceResponse.getStatusMessage());
			//send email regarding payment deposit
		}catch(Exception e){
			logger.error("Failed to refund the amount due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to refund the amount, Please try again!");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for adjust the balance of a student**/
	@Override
	public ServiceResponse adjustBalance(MasterTransactionsAudit masterTransactionsAudit) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			masterTransactionsAudit = buildTransactionPaymentObject(masterTransactionsAudit);
			MasterTransactionsAudit masterTransactionsAuditResp = transactionsDao.depositAmtThruOffline(masterTransactionsAudit);
			if(masterTransactionsAuditResp.getRecId() != null && masterTransactionsAuditResp.getRecId() != 0){
				masterTransactionsAudit.setRecId(masterTransactionsAuditResp.getRecId());
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Balance has been adjusted successfully.");
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Failed to adjust the balance.");
			}
			logger.info(serviceResponse.getStatusMessage());
			//send email regarding payment deposit
		}catch(Exception e){
			logger.error("Failed to adjust the balance due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to adjust the balance, Please try again!");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for deactivate the transaction**/
	@Override
	public ServiceResponse removePurchaseTrx(Long trxId, Long mealSchoolId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Object[]> trxDetails = transactionsDao.getPurchaseTrx(trxId);
			if(trxDetails == null || trxDetails.size() < 1){
				throw new Exception("Transaction does not exist with this trx Id: "+trxId);
			}
			MasterTransactionsAudit adjustTrx = buildPurchaseAdjustTrx(trxDetails, mealSchoolId, trxId);
			adjustTrx = buildTransactionPaymentObject(adjustTrx);
			serviceResponse = transactionsDao.removeAdjPurchaseTrx(adjustTrx, trxId);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to deactivate the transaction due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to delete the transaction.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for build the deactivate transactions object
	 * @throws ParseException **/
	private MasterTransactionsAudit buildPurchaseAdjustTrx(List<Object[]> trxDetails, Long mealSchoolId, Long trxId) throws ParseException{
		MasterTransactionsAudit mta = new MasterTransactionsAudit();
		StudentWiseTransaction swt = new StudentWiseTransaction();
		String trxDesc = "";
		for(Object[] obj : trxDetails){
			swt.setStudentRecId(obj[3] != null ? Long.parseLong(obj[3].toString()) : 0);
			swt.setTransactionAmount(obj[2] != null ? Double.parseDouble(obj[2].toString()) : 0);
			String trxType = obj[5] != null ? obj[5].toString() : "";
			if(trxType.equalsIgnoreCase("Purchase")){
				trxDesc = "Balance adjustment for void "+(obj[0] != null ? obj[0].toString() : "")+" for ";
				mta.setPaymentType(PaymentType.AdjustmentCR);
			}else{
				trxDesc = "Balance adjustment for void "+(obj[4] != null ? obj[4].toString() : "")+" Deposit for ";
				mta.setPurchaseItemType(PurchaseItemType.AdjustmentDR);
			}
			if(obj[1] != null)
				trxDesc = trxDesc+(new SimpleDateFormat("dd-MMM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(obj[1].toString())));
			//trxDesc = trxDesc+" and trx Id# "+trxId;
			
		}
		mta.setMealSchoolId(mealSchoolId);
		mta.setTransactionType(TransactionType.Adjustment);
		mta.setTransactionDescription(trxDesc);
		mta.setTotalTransactionAmount(swt.getTransactionAmount());
		Set<StudentWiseTransaction> studentWiseTransactions = new HashSet<StudentWiseTransaction>();
		studentWiseTransactions.add(swt);
		mta.setStudentWiseTransactions(studentWiseTransactions);
		return mta;
	}
	
	private List<StudentDetailsWithOrderedItem> buildNewReqFormat(List<StudentDetailsWithOrderedItem> studentDetailsWithOrderedItems) throws IllegalAccessException, InvocationTargetException{
		List<StudentDetailsWithOrderedItem> studentDetailsWithOrderedItemList = new ArrayList<StudentDetailsWithOrderedItem>();
		List<MenuItemDetailsV2> menuItems = null;
		MenuItemDetailsV2 menuItemDetailsV2 = null;
		for(StudentDetailsWithOrderedItem studentDetailsWithOrderedItem : studentDetailsWithOrderedItems){
			if(studentDetailsWithOrderedItem.getMenuItemDetails() != null && studentDetailsWithOrderedItem.getMenuItemDetails().size() > 0){
				menuItems = new ArrayList<MenuItemDetailsV2>();
				for(OrderedMenuItemDetails orderedMenuItemDetail : studentDetailsWithOrderedItem.getMenuItemDetails()){
					menuItemDetailsV2 = new MenuItemDetailsV2();
					menuItemDetailsV2.setMealId(orderedMenuItemDetail.getMenuId());
					menuItemDetailsV2.setMealPrice(orderedMenuItemDetail.getItemOriginalPrice());
					menuItemDetailsV2.setReducedPrice(orderedMenuItemDetail.getReducedPrice());
					menuItemDetailsV2.setMealDesc(orderedMenuItemDetail.getItemDesc());
					menuItemDetailsV2.setMealType(orderedMenuItemDetail.getItemtype());
					menuItemDetailsV2.setReducedPriceEligStatus(orderedMenuItemDetail.getMealReducedPriceElig());
					if(menuItemDetailsV2.getMealType().toString().equalsIgnoreCase(MealType.SIDE.toString())){
						String sideName = orderedMenuItemDetail.getItemName() != null ? orderedMenuItemDetail.getItemName() : null;
						if(sideName != null && sideName.length() > 0){
							for(String value : Arrays.asList(sideName.split("\\s*,\\s*"))){
								MenuItemDetailsV2 menuItemDetail = new MenuItemDetailsV2();
								BeanUtils.copyProperties(menuItemDetail, menuItemDetailsV2);
								menuItemDetail.setMealName(value);
								menuItems.add(menuItemDetail);
							}
						}else
							menuItems.add(menuItemDetailsV2);
					}else{
						menuItemDetailsV2.setMealName(orderedMenuItemDetail.getItemName());
						menuItems.add(menuItemDetailsV2);
					}
				}
				Map<MealType, List<MenuItemDetailsV2>> menuByType = menuItems.stream().collect(
						Collectors.groupingBy(MenuItemDetailsV2::getMealType));
				studentDetailsWithOrderedItem.setMenuItemDetails(null);
				studentDetailsWithOrderedItem.setMenuByType(menuByType);
			}			
			studentDetailsWithOrderedItemList.add(studentDetailsWithOrderedItem);
		}
		return studentDetailsWithOrderedItemList;
	}
	
	//Map the breakfast data
	/*private List<MenuItemDetailsV2> mapBreakfastData(Set<BreakfastItems> breakfastItems){
		List<MenuItemDetailsV2> menuItemDetails = new ArrayList<MenuItemDetailsV2>();
		MenuItemDetailsV2 menuItemDetail = null;
		for(BreakfastItems breakfastItem : breakfastItems){
			menuItemDetail = new MenuItemDetailsV2();
			menuItemDetail.setMealDesc(breakfastItem.getItemDesc());
			menuItemDetail.setMealId(breakfastItem.getRecId());
			menuItemDetail.setMealName(breakfastItem.getItemName());
			menuItemDetail.setMealPrice(breakfastItem.getPrice());
			menuItemDetail.setMealType(breakfastItem.getItemType());
			menuItemDetail.setReducedPrice(breakfastItem.getReducedPrice());
			menuItemDetails.add(menuItemDetail);
		}
		return menuItemDetails;
	}
	
	//Map the breakfast data
		private List<MenuItemDetailsV2> mapBreakfastDataV2(Set<MealCalendar> breakfastItems){
			List<MenuItemDetailsV2> menuItemDetails = new ArrayList<MenuItemDetailsV2>();
			MenuItemDetailsV2 menuItemDetail = null;
			for(MealCalendar calendar : breakfastItems){
				menuItemDetail = new MenuItemDetailsV2();
				menuItemDetail.setMealDesc(calendar.getMenuItem().getLongDescription());
				menuItemDetail.setMealId(calendar.getMenuItem().getId());
				menuItemDetail.setMealName(calendar.getMenuItem().getName());
				menuItemDetail.setMealPrice(calendar.getPrice());
				menuItemDetail.setMealType(calendar.getMenuItem().getCategory());
				menuItemDetail.setReducedPrice(calendar.getReducedPrice());
				menuItemDetails.add(menuItemDetail);
			}
			return menuItemDetails;
		}*/

		/**This method used for purchase the package**/
		@Override
		public ServiceResponse subscribePackage(PackageSubscriptionsTrx packageSubscriptionsTrx) {
			ServiceResponse serviceResponse = new ServiceResponse();
			try{
				if(packageSubscriptionsTrx.getLoggedUser() != null)
					packageSubscriptionsTrx.setCreatedBy(packageSubscriptionsTrx.getLoggedUser());
				packageSubscriptionsTrx.setCreatedOn(new Date());
				MealSchool mealSchool = mealSchoolRepository.findBySchoolId(packageSubscriptionsTrx.getMealSchool().getSchoolId());
				packageSubscriptionsTrx.setMealSchool(mealSchool);
				if(packageSubscriptionsTrx.getPaymentType() == null){
					packageSubscriptionsTrx.setPaid(false);
				}else if(packageSubscriptionsTrx.getPaymentType().toString().equalsIgnoreCase("Online") 
						&& packageSubscriptionsTrx.getMealSchool().getIsPaymentEnabled() != null && 
						packageSubscriptionsTrx.getMealSchool().getIsPaymentEnabled() && 
						(packageSubscriptionsTrx.getMealSchool().getStripeAccountId() != null || (packageSubscriptionsTrx.getPaymentGateway() != null 
						&& packageSubscriptionsTrx.getPaymentGateway().equalsIgnoreCase("PayMob")))){
					packageSubscriptionsTrx.setPaid(true);
					String tansactionToken = packageSubscriptionsTrx.getTransactionToken();
					if((packageSubscriptionsTrx.getPaymentGateway() == null || packageSubscriptionsTrx.getPaymentGateway().equalsIgnoreCase("Stripe")) 
							&& packageSubscriptionsTrx.getTotalPaidAmt() > 0){
						/*Stripe.apiKey = stripeSecretKey;
						Map<String, Object> chargeParams = new HashMap<String, Object>();
						chargeParams.put("amount", (int) Math.round((packageSubscriptionsTrx.getTotalPaidAmt()+
								packageSubscriptionsTrx.getAppFeeAmount()+packageSubscriptionsTrx.getTransactionFees())*100));
						chargeParams.put("currency", CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())));
						chargeParams.put("destination", packageSubscriptionsTrx.getMealSchool().getStripeAccountId());
						chargeParams.put("source", tansactionToken);
						chargeParams.put("application_fee_amount", (int) Math.round((packageSubscriptionsTrx.getAppFeeAmount()+packageSubscriptionsTrx.getTransactionFees())*100));*/
						/*Map<String, Object> chargeParams = stripeUtill.prepareStripeDestinationCharge(packageSubscriptionsTrx.getTotalPaidAmt(), 
								packageSubscriptionsTrx.getAppFeeAmount(), packageSubscriptionsTrx.getTransactionFees(), 
								CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())), 
										packageSubscriptionsTrx.getMealSchool().getStripeAccountId(), tansactionToken);*/
						Charge charge = null;
						try{
							charge = stripeUtill.prepareStripeDirectCharge(packageSubscriptionsTrx.getTotalPaidAmt(), 
									packageSubscriptionsTrx.getAppFeeAmount(), packageSubscriptionsTrx.getTransactionFees(), 
									CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())), 
											packageSubscriptionsTrx.getMealSchool().getStripeAccountId(), tansactionToken,mealSchool.isTrxFeeOnSchool());
							if(mealSchool.isTrxFeeOnSchool()){
								packageSubscriptionsTrx.setTransactionFees(0.0);
								packageSubscriptionsTrx.setAppFeeAmount(0.0);
							}
						}catch(Exception e){
							serviceResponse.setStatus("Failed");
							serviceResponse.setStatusCode(422);
							serviceResponse.setStatusMessage("Failed to subscribe the package. Please try again!!");
							serviceResponse.setErrorMessage(e.getMessage());
							logger.error("Failed to make online payment for subscribe the package with schoolId::"+packageSubscriptionsTrx.getMealSchool().getSchoolId()+" and parentEmail::"+packageSubscriptionsTrx.getLoggedUser()+" due to '"+e.getMessage()+"'");
							return serviceResponse;
						}
						packageSubscriptionsTrx.setChargeId(charge.getId());
						packageSubscriptionsTrx.setTransferId(charge.getBalanceTransaction());
					}
				}else
					throw new Exception("School not accepting online payment. Please contact to the School Admin");
				transactionsDao.subscribePackage(packageSubscriptionsTrx);				
				if(packageSubscriptionsTrx.isPaid()){
					if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null 
							&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().toUpperCase().contains("ROLE_PARENT"))
						packageSubscriptionsTrx.setParentUserEmails(SecurityContextHolder.getContext().getAuthentication().getName());
					String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(packageSubscriptionsTrx.getMealSchool().getSchoolId()));
					bcacPaymentRecieptPdf.paymentReceiptGenerate(packageSubscriptionsTrx,currencySymbol);
				}
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Package has been subscribed successfully.");
				logger.info(serviceResponse.getStatusMessage());
			}catch(Exception e){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("Failed to subscribe the package.");
				serviceResponse.setErrorMessage(e.getMessage());
				logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
			}
			return serviceResponse;
		}

		/**This method used for audit BC & AC package subscription**/
		@Override
		public ServiceResponse bcAcSubscriptionsAudit(List<BCACAudit> bcacAudits) {
			ServiceResponse serviceResponse = new ServiceResponse();
			try{
				serviceResponse = transactionsDao.bcAcSubscriptionsAudit(bcacAudits);
				logger.info(serviceResponse.getStatusMessage());
			}catch(Exception e){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("Failed to audit the BC & AC package subscriptions.");
				serviceResponse.setErrorMessage(e.getMessage());
				logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
			}
			return serviceResponse;
		}

		@Override
		/**This method used for add/update/delete the authorized pickup persons**/
		public ServiceResponse authorizePP(List<PickupAuthorized> pickupAuthorizeds) {
			ServiceResponse serviceResponse = new ServiceResponse();
			try{
				serviceResponse = transactionsDao.authorizePP(pickupAuthorizeds);
				logger.info(serviceResponse.getStatusMessage());
			}catch(Exception e){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("Failed to update the authorized pickup persons.");
				serviceResponse.setErrorMessage(e.getMessage());
				logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
			}
			return serviceResponse;
		}

		/**This method used for update the package due payment status**/
		@Override
		public ServiceResponse pkgDuePayment(Long masterPkgTrxId, PaymentType type, String refId) {
			ServiceResponse serviceResponse = new ServiceResponse();
			try{
				String loggedUser = "";
				if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
					loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
				PackageSubscriptionsTrx packageSubscriptionsTrx = transactionsDao.pkgDuePayment(masterPkgTrxId, type, refId, loggedUser);
				String currencySymbol = countryDetailsRepository.getCurrencySymbol(packageSubscriptionsTrx.getMealSchool().getCountryCode());
				packageSubscriptionsTrx.setParentUserEmails(packageSubscriptionsTrx.getCreatedBy());
				bcacPaymentRecieptPdf.paymentReceiptGenerate(packageSubscriptionsTrx,currencySymbol);
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Package payment status updated successfully.");
				logger.info(serviceResponse.getStatusMessage());
			}catch(Exception e){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("Failed to update package payment status.");
				serviceResponse.setErrorMessage(e.getMessage());
				logger.error(serviceResponse.getStatusMessage()+" for masterPkgTrxId::"+masterPkgTrxId+" due to "+e.getMessage());
			}
			return serviceResponse;
		}

		/**This method used for update the package due payment status**/
		@Override
		public ServiceResponse pkgPayment(Long masterPkgTrxId, PackageSubscriptionsTrx pkgTrx) {
			ServiceResponse serviceResponse = new ServiceResponse();
			try{
				String loggedUser = "";
				if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
					loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
				MealSchool mealSchool = mealSchoolRepository.findBySchoolId(pkgTrx.getMealSchool().getSchoolId());
				pkgTrx.setLoggedUser(loggedUser);
				if(pkgTrx.getPaymentType().toString().equalsIgnoreCase("Online") 
						&& mealSchool.getIsPaymentEnabled() != null && mealSchool.getIsPaymentEnabled() && 
						(mealSchool.getStripeAccountId() != null || (pkgTrx.getPaymentGateway() != null 
						&& pkgTrx.getPaymentGateway().equalsIgnoreCase("PayMob")))){
					String tansactionToken = pkgTrx.getTransactionToken();
					if((pkgTrx.getPaymentGateway() == null || pkgTrx.getPaymentGateway().equalsIgnoreCase("Stripe")) 
							&& pkgTrx.getTotalPaidAmt() > 0){
						/*Stripe.apiKey = stripeSecretKey;
						Map<String, Object> chargeParams = new HashMap<String, Object>();
						chargeParams.put("amount", (int) Math.round((pkgTrx.getTotalPaidAmt()+
								pkgTrx.getAppFeeAmount()+pkgTrx.getTransactionFees())*100));
						chargeParams.put("currency", CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())));
						chargeParams.put("destination", mealSchool.getStripeAccountId());
						chargeParams.put("source", tansactionToken);
						chargeParams.put("application_fee_amount", (int) Math.round((pkgTrx.getAppFeeAmount()+pkgTrx.getTransactionFees())*100));*/
						/*Map<String, Object> chargeParams = stripeUtill.prepareStripeDestinationCharge(pkgTrx.getTotalPaidAmt(),
								pkgTrx.getAppFeeAmount(), pkgTrx.getTransactionFees(), CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())),
								mealSchool.getStripeAccountId(), tansactionToken);*/
						Charge charge = null;
						try{
							charge =stripeUtill.prepareStripeDirectCharge(pkgTrx.getTotalPaidAmt(),
									pkgTrx.getAppFeeAmount(), pkgTrx.getTransactionFees(), CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())),
									mealSchool.getStripeAccountId(), tansactionToken,mealSchool.isTrxFeeOnSchool());
						}catch(Exception e){
							serviceResponse.setStatus("Failed");
							serviceResponse.setStatusCode(422);
							serviceResponse.setStatusMessage("Failed to create online payment. Please try again!!");
							serviceResponse.setErrorMessage(e.getMessage());
							logger.error("Failed to make online payment for package with schoolId::"+pkgTrx.getMealSchool().getSchoolId()+"parentEmail::"+loggedUser+" due to '"+e.getMessage()+"'");
							return serviceResponse;
						}
						pkgTrx.setChargeId(charge.getId());
						pkgTrx.setTransferId(charge.getBalanceTransaction());
					}
				}else
					throw new Exception("School not accepting online payment. Please contact to the School Admin");
				PackageSubscriptionsTrx packageSubscriptionsTrx = transactionsDao.pkgPayment(masterPkgTrxId, pkgTrx);
				packageSubscriptionsTrx.setAppFeeAmount(pkgTrx.getAppFeeAmount());
				packageSubscriptionsTrx.setTransactionFees(pkgTrx.getTransactionFees());
				if(mealSchool.isTrxFeeOnSchool()){
					packageSubscriptionsTrx.setAppFeeAmount(0.0);
					packageSubscriptionsTrx.setTransactionFees(0.0);
				}
				String currencySymbol = countryDetailsRepository.getCurrencySymbol(packageSubscriptionsTrx.getMealSchool().getCountryCode());
				packageSubscriptionsTrx.setParentUserEmails(loggedUser);
				bcacPaymentRecieptPdf.paymentReceiptGenerate(packageSubscriptionsTrx,currencySymbol);
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Package payment status updated successfully.");
				logger.info(serviceResponse.getStatusMessage()+" from Parent dashboard.");
			}catch(Exception e){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("Failed to update package payment status.");
				serviceResponse.setErrorMessage(e.getMessage());
				logger.error(serviceResponse.getStatusMessage()+" from parent dashboard for masterPkgTrxId::"+masterPkgTrxId+" due to "+e.getMessage());
			}
			return serviceResponse;
		}
}
