package com.mealManage.service;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mealManage.dao.ReportsDao;
import com.mealManage.domain.AccBalanceSummary;
import com.mealManage.domain.AccountTransactionHistory;
import com.mealManage.domain.FMActualReport;
import com.mealManage.domain.LowBalanceStudents;
import com.mealManage.domain.MealChangeNotificationRequest;
import com.mealManage.domain.MealDesc;
import com.mealManage.domain.StatusUpdateNotificationReq;
import com.mealManage.domain.StudentAccountDetails;
import com.mealManage.domain.StudentsWithAllergies;
import com.mealManage.domain.TransactionsDetails;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.packages.PackagePaymentsTrx;
import com.mealManage.mealmodel.packages.PackagesSubscribed;
import com.mealManage.mealmodel.packages.PickupAuthorizedResp;
import com.mealManage.mealmodel.reimbursement.ReimbursementMealsType;
import com.mealManage.mealmodel.reimbursement.ReimbursementRatesInfo;
import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.DistrictRepository;
import com.mealManage.mealmodel.repository.EventInfoRepo;
import com.mealManage.mealmodel.repository.FMEligibilitySurveyRepository;
import com.mealManage.mealmodel.repository.LowBalanceSchoolSettingRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.SchoolPackageRepo;
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.CountryDetail;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.DistrictUser;
import com.mealManage.mealmodel.school.LowBalanceSchoolSetting;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.school.SchoolYear;
import com.mealManage.mealmodel.school.TimezoneDetails;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.mealmodel.user.FMEligibilitySurvey;
import com.mealManage.mealmodel.user.SchoolUser;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.response.BCACSubscriptions;
import com.mealManage.response.CatererReportResp;
import com.mealManage.response.CaterersResp;
import com.mealManage.response.CountChildObject;
import com.mealManage.response.DistrictDashboardResp;
import com.mealManage.response.DuePaymentResp;
import com.mealManage.response.EditCheckResp;
import com.mealManage.response.EligSummaryResp;
import com.mealManage.response.EligSummaryResp1;
import com.mealManage.response.EmailSendResp;
import com.mealManage.response.EventsResp;
import com.mealManage.response.IncomeResp;
import com.mealManage.response.MealItems;
import com.mealManage.response.MealJsonData;
import com.mealManage.response.MealOrderReport;
import com.mealManage.response.MonthlyMenuDetailsResp;
import com.mealManage.response.NotOrderedStudentResp;
import com.mealManage.response.OrderCostInfo;
import com.mealManage.response.OrderedMealItemsReport;
import com.mealManage.response.PaymobTrxChargesResp;
import com.mealManage.response.RevenueResp;
import com.mealManage.response.SchoolMealReportResp;
import com.mealManage.response.SelfRegParentRequestedEmail;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentInfoWithMeal;
import com.mealManage.util.AuditReportUtil;
import com.mealManage.util.BCACPkgReport;
import com.mealManage.util.CatererPdfReportGeneration;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.DateUtilityV2;
import com.mealManage.util.DepositSummaryReportUtil;
import com.mealManage.util.DistAuditReportUtil;
import com.mealManage.util.DistrictEditCheckUtil;
import com.mealManage.util.EligibilitySummaryUtil;
import com.mealManage.util.EventsReportUtil;
import com.mealManage.util.ExcelGenerateForReport;
import com.mealManage.util.FMRPActualReport;
import com.mealManage.util.FRTempEligStatusReport;
import com.mealManage.util.GenerateAllReportsInPdf;
import com.mealManage.util.GenerateAllergiesReportPdf;
import com.mealManage.util.GradeFormatBuild;
import com.mealManage.util.IncomeReportUtil;
import com.mealManage.util.LowBalanceStudentDetailsReportUtil;
import com.mealManage.util.LowBalanceStudentReportUtil;
import com.mealManage.util.OnlineReportUtil;
import com.mealManage.util.OrderCostReportUtil;
import com.mealManage.util.OrderPdfReportGeneration;
import com.mealManage.util.PackagePaymentTrxUtil;
import com.mealManage.util.PaymobTrxChargesUtil;
import com.mealManage.util.RevenueReportUtil;
import com.mealManage.util.SchoolPdfReportGeneration;
import com.mealManage.util.SendNotificationUtil;
import com.mealManage.util.TransactionHistoryReportUtil;
import com.mealManage.util.TransactionsDetailsReportUtil;

/** This class implement by ReportsService interface for Reports related APIs **/
@Service
public class ReportsServiceImpl implements ReportsService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ReportsDao reportsDao;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private ExcelGenerateForReport excelGenerateForReport;
	@Autowired
	private CatererPdfReportGeneration pdfGenerateForReport;
	@Autowired
	private SchoolPdfReportGeneration schoolPdfReportGeneration;
	@Autowired
	private GenerateAllReportsInPdf generateAllReportsInPdf;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	@Autowired
	private OrderPdfReportGeneration orderPdfReportGeneration;
	@Autowired
	private FRTempEligStatusReport frTempEligStatusReport;
	@Autowired
	private DepositSummaryReportUtil depositSummaryReportUtil;
	@Autowired
	private SchoolPackageRepo packageRepo;
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	@Value("${email.lunch.change.reminder.subject}")
	private String emailLunchChangeSubject;
	@Value("${email.lunch.change.remider.message}")
	private String emailLunchChangeMsg;
	@Autowired
	private GenerateAllergiesReportPdf generateAllergiesReportPdf;
	@Autowired
	private FMEligibilitySurveyRepository fMEligibilitySurveyRepository;
	@Autowired
	private TransactionHistoryReportUtil transactionHistoryReportUtil;
	@Autowired
	private TransactionsDetailsReportUtil transactionsDetailsReportUtil;
	@Autowired
	private LowBalanceStudentReportUtil lowBalanceStudentReportUtil;
	@Autowired
	private LowBalanceSchoolSettingRepository lowBalanceSchoolSettingRepository;
	@Autowired
	private LowBalanceStudentDetailsReportUtil lowBalanceStudentDetailsReportUtil;
	@Autowired
	private FMRPActualReport fMRPActualReport;
	@Autowired
	private AuditReportUtil auditReportUtil;
	@Autowired
	private DateUtilityV2 du;
	@Autowired
	private DistAuditReportUtil distAuditReportUtil;
	/*@Autowired
	private DailyAuditCheck dailyAuditCheck;*/
	@Autowired
	private DistrictEditCheckUtil districtEditCheckUtil;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Autowired
	private DashboardService dashboardService;
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private EventInfoRepo eventInfoRepo;
	@Autowired
	private PackagePaymentTrxUtil packagePaymentTrxUtil;
	@Autowired
	private PaymobTrxChargesUtil paymobTrxChargesUtil;
	@Autowired
	private BCACPkgReport bcacPkgReport;
	@Autowired
	private EventsReportUtil eventsReportUtil;
	@Autowired
	private EligibilitySummaryUtil eligibilitySummaryUtil;
	@Autowired
	private OrderCostReportUtil orderCostReportUtil;
	@Autowired
	private DistrictRepository districtRepository;
	@Autowired
	private RevenueReportUtil revenueReportUtil;
	@Autowired
	private IncomeReportUtil incomeReportUtil;
	@Autowired
	private OnlineReportUtil onlineReportUtil;
	private static SimpleDateFormat sdfReq = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static SimpleDateFormat sdfOrg = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DecimalFormat df = new DecimalFormat("##.00");
	
	/**This method used for get the total count of meal ordered, paid meal ordered, not paid meal ordered and not meal ordered Students by grade
	 * @throws Exception **/
	@Override
	public Map<String, CountChildObject> countByGrade(Long mealSchoolId, String yearMonth, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType) throws Exception {
		Map<String, CountChildObject> studentCountsResps = new HashMap<String, CountChildObject>();
		try{
			/**Getting & Setting all the ordered, paid & not paid count by grade**/
			List<Object[]> orderedCountArrList = reportsDao.orderedCountByGrade(mealSchoolId, yearMonth, menuType);
			CountChildObject countChildObject = mapOrderedCountByGrade(orderedCountArrList);
			studentCountsResps.put("ordered", countChildObject);
			logger.info("Getting not ordered count by grade");
			/**Getting & Setting all the not ordered count by grade**/
			List<String> grades = null;
			if(isVersion2 != null && isVersion2)
				grades = reportsDao.getMealPublishedGradesV2(mealSchoolId, yearMonth, null, false, menuType);
			else
				grades = reportsDao.getMealPublishedGrades(mealSchoolId, yearMonth, null, false);
			List<Object[]> allStudentsArrList = reportsDao.allStudentsCountByGrade(mealSchoolId, yearMonth, grades, schoolYear);
			countChildObject = mapNotOrderedCountByGrade(allStudentsArrList, studentCountsResps.get("ordered"));
			studentCountsResps.put("notOrdered", countChildObject);		
			logger.info("countByGrade API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occured during execution of countByGrade API "+e.getMessage());
			throw new Exception("Failed to get the report data by grade");
		}
		return studentCountsResps;
	}
	
	/**This method used for get the total count of meal ordered Students by school
	 * @throws Exception **/
	@Override
	public Map<String, Long> countBySchool(Long catererId, String yearMonth, ItemTypeConstants menuType, Boolean isCaterer) throws Exception {
		Map<String, Long> ordersByMonth = new HashMap<String, Long>();
		try{
			/**Getting & Setting all the ordered count by school**/
			List<Object[]> orderedCountArrList = null;
			if(isCaterer != null && isCaterer)
				orderedCountArrList = reportsDao.orderedCountBySchool(catererId, yearMonth, menuType);
			else 
				orderedCountArrList = reportsDao.orderedCountByGrade(catererId, yearMonth, menuType);
			ordersByMonth = buildCountByReqKey(orderedCountArrList);
			logger.info("countBySchool API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occured during execution of countBySchool API "+e.getMessage());
			throw new Exception("Failed to get the report data by school");
		}
		return ordersByMonth;
	}
	
	/**This method used for get the caterer report
	 * @throws Exception **/
	@Override
	public CatererReportResp catererReport(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, Boolean isVersion2, ItemTypeConstants menuType) throws Exception {
		CatererReportResp catererReportResp = new CatererReportResp();
		try{			
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			catererReportResp.setSchoolName(mealSchool.getSchoolName());
			catererReportResp.setGrades(grades);
			catererReportResp.setStartDate(sdf.format(startDate));
			catererReportResp.setEndDate(sdf.format(endDate));
			Map<String, Map<String, Integer>> mealsMap = mealForCatererPdfReport(mealSchoolId, startDate, endDate, grades, isVersion2, menuType);
			catererReportResp.setDateMealItemCountMap(mealsMap);
			logger.info("catererReport API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occurred during execution of catererReport API "+e.getMessage());
			throw new Exception("Error occured during execution of catererReport API");
		}
		return catererReportResp;
	}
	
	/**This method used for get the meals report to school**/
	/*@Override
	public SchoolMealReportResp schoolMealReport(Long mealSchoolId, Date startDate, Date endDate, List<SchoolGrades> grades, Integer schoolYear) throws Exception {
		SchoolMealReportResp schoolMealReportResp = new SchoolMealReportResp();
		try{
			List<StudentInfoWithMeal> studentInfoWithMeals = new ArrayList<StudentInfoWithMeal>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			schoolMealReportResp.setStartDate(sdf.format(startDate));
			schoolMealReportResp.setEndDate(sdf.format(endDate));
			schoolMealReportResp.setGrades(grades);
			List<String> gradeNames = getGradeNamesVal(grades);
			
			*//**Getting/Setting all the meal menu items by date**//*
			List<Object[]> mealsArray = reportsDao.allMealsWithDate(mealSchoolId, startDate, endDate, gradeNames);
			Map<String, List<String>> mealsDateMap = new HashMap<String, List<String>>();
			List<String> meals = null;
			if(mealsArray != null)
			for(Object[] obj : mealsArray){
				if(obj[1] != null){
					String mealDate = sdf.format(sdf.parse(obj[1].toString()));
					meals = mealsDateMap.get(mealDate);
					if(meals == null)
						meals = new ArrayList<String>();
					
					meals.add(obj[0].toString());
					mealsDateMap.put(mealDate, meals);
				}
			}
			if(mealsDateMap != null && mealsDateMap.size() > 0){
				schoolMealReportResp.setAllMealNameByDate(mealsDateMap);
				
				*//**Getting/Setting all the student info and ordered meal**//*
				Set<StudentUser> studentUsers = null;
				if(grades != null && grades.size() > 0)
					studentUsers = studentUserRepository.findByMealSchoolSchoolIdAndIsRegisterAndIsActiveAndGradeNameInAndSchoolYear(
							mealSchoolId, true, true, grades, schoolYear);
				else
					studentUsers = studentUserRepository.findByMealSchoolSchoolIdAndIsRegisterAndIsActiveAndSchoolYear(mealSchoolId, true, true, schoolYear);
				
				List<Object[]> studentOrdered = reportsDao.orderedMealByStudentAndDate(mealSchoolId, startDate, endDate, gradeNames);
				Map<String, List<MealDesc>> studentOrderedMap = new HashMap<String, List<MealDesc>>();
				List<MealDesc> mealDescs1 = null;
				for(Object[] obj : studentOrdered){
					mealDescs1 = studentOrderedMap.get(obj[0].toString());
					if(mealDescs1 == null)
						mealDescs1 = new ArrayList<MealDesc>();
					
					MealDesc mealDesc = new MealDesc();
					mealDesc.setMealName(obj[1].toString());
					mealDesc.setMealDate(sdf.format(sdf.parse(obj[2].toString())));
					mealDescs1.add(mealDesc);
					studentOrderedMap.put(obj[0].toString(), mealDescs1);					
				}
				
				Map<String, List<MealDesc>> orderedMealsByDate = null;
				StudentInfoWithMeal studentInfoWithMeal = null;
				List<MealDesc> mealDescs = null;
				for(StudentUser studentUser : studentUsers){
					studentInfoWithMeal = new StudentInfoWithMeal();
					studentInfoWithMeal.setStudentFName(studentUser.getFirstName());
					studentInfoWithMeal.setStudentLName(studentUser.getLastName());
					studentInfoWithMeal.setStudentId(studentUser.getStudentId());
					studentInfoWithMeal.setGrade(studentUser.getGradeName().toString());
					studentInfoWithMeal.setAllergies(studentUser.getAllergies() != null ? studentUser.getAllergies().toUpperCase() : "");
					studentInfoWithMeal.setTeacherName(studentUser.getTeacherName() != null ? studentUser.getTeacherName().toUpperCase() : "");
					mealDescs = studentOrderedMap.get(studentUser.getStudentId());
					if(mealDescs != null){
						Map<String, List<String>> orderedMealMenus = new HashMap<String, List<String>>();
						orderedMealsByDate = mealDescs.stream().collect(Collectors.groupingBy(MealDesc::getMealDate));
						orderedMealsByDate.entrySet().forEach((e) -> {
							List<String> menus = new ArrayList<String>();
							for(MealDesc mealDesc : e.getValue()){
								menus.add(mealDesc.getMealName());
							}
							orderedMealMenus.put(e.getKey(), menus);
				        });
						studentInfoWithMeal.setMealOrderedByDate(orderedMealMenus);
					}
					studentInfoWithMeals.add(studentInfoWithMeal);
				}
			}
			schoolMealReportResp.setStudentWithMeal(studentInfoWithMeals);
		}catch(Exception e){
			logger.error("Error occurred during execution of schoolMealReport API "+e.getMessage());
			throw new Exception("Error occurred during execution of schoolMealReport API");
		}
		return schoolMealReportResp;
	}*/
	
	/**This method used for get the meals data to generate school report**/
	@Override
	public SchoolMealReportResp schoolReport(Long mealSchoolId, Date startDate, Date endDate, List<SchoolGrades> grades, 
			Integer schoolYear, String fileType, Boolean isOrder, Boolean isVersion2, ItemTypeConstants menuType) throws Exception {
		MealSchool mealSchool = null;
		Boolean havingExtraPreOrders = false;
		SchoolMealReportResp schoolMealReportResp = new SchoolMealReportResp();
		try{
			List<String> gradeNames = getGradeNamesVal(grades);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			schoolMealReportResp.setStartDate(sdf.format(startDate));
			schoolMealReportResp.setEndDate(sdf.format(endDate));
			schoolMealReportResp.setGrades(grades);
			String itemType = CommonUtil.getItemType(menuType);
			List<String> gradeFinalStr = reportsDao.getAllGrades(mealSchoolId, startDate, endDate, gradeNames, itemType);
			List<SchoolGrades> gradesFinalVal = new ArrayList<SchoolGrades>();
			for(String grd : gradeFinalStr){
				gradesFinalVal.add(SchoolGrades.valueOf(grd));
			}
			
			/**Getting/Setting all the meal menu items with meal date and grade**/
			List<Object[]> mealsArray = new ArrayList<Object[]>();
			if(isVersion2 != null && isVersion2){
				if(isOrder == null || !isOrder)
					mealsArray = reportsDao.allMealsWithDateAndGradesV2(mealSchoolId, startDate, endDate, gradeNames, 
						((isOrder != null && isOrder) ? "SIDE" : itemType));
			}else
				mealsArray = reportsDao.allMealsWithDateAndGrades(mealSchoolId, startDate, endDate, gradeNames, 
						((isOrder != null && isOrder) ? "SIDE" : itemType));
			List<String> meals = null;
				/**This gradeDatesMealsMap Map object will store key as grade and value as the other Map of date & meal 
				 * (i.e. this another map will store key as the date and value as the list of Meals)**/
				Map<String, Map<String, List<String>>> gradeDatesMealsMap = new HashMap<String, Map<String, List<String>>>();
				/**This Map having key as the date and value as the list of meals**/
				Map<String, List<String>> dateMealsMap = new HashMap<String, List<String>>();
				String mealDate;
				String grade;
				for(Object[] obj : mealsArray){
					/**Checking that record not having meal date, grade and meal title as null **/
					if(obj[1] != null && obj[2] != null && obj[0] != null){
						mealDate = sdf.format(sdf.parse(obj[1].toString()));
						grade = obj[2].toString();
						dateMealsMap = gradeDatesMealsMap.get(grade); 
						if(dateMealsMap == null)
							dateMealsMap = new HashMap<String, List<String>>(); 
						
						meals = dateMealsMap.get(mealDate);
						if(meals == null)
							meals = new ArrayList<String>();
							
						meals.add(obj[0].toString());
						dateMealsMap.put(mealDate, meals);
						gradeDatesMealsMap.put(grade, dateMealsMap);
						//gradesFinalVal.add(SchoolGrades.valueOf(grade));
					}
				}
				schoolMealReportResp.setMealsByGradeAndDate(gradeDatesMealsMap);
			//}
			
			
			/**Getting all the student info based on passed input parameter**/
			Set<StudentUser> studentUsers = new HashSet<StudentUser>();
			if(grades != null && grades.size() > 0){
				if(gradesFinalVal != null && gradesFinalVal.size() > 0)
					studentUsers = studentUserRepository.findByMealSchoolSchoolIdAndIsActiveAndGradeNameInAndSchoolYear(
						mealSchoolId, true, gradesFinalVal, schoolYear);
				else{
					logger.info("WARN: No Order item availale.");
					return schoolMealReportResp;
				}
			}else
				studentUsers = studentUserRepository.findByMealSchoolSchoolIdAndIsActiveAndSchoolYear(mealSchoolId, 
						true, schoolYear);
			
			/**Getting all the student details who orders meal in given criteria**/
			List<Object[]> studentOrdered = reportsDao.orderedMealByStudentAndDate(mealSchoolId, startDate, endDate, gradeNames, itemType, menuType);
			
			/**This Map store the key as studentId and value as the list of PobjO class which contains meal name and date**/
			Map<Long, List<MealDesc>> studentOrderedMap = new HashMap<Long, List<MealDesc>>();
			Map<Long, List<MealDesc>> studentOrderedSideMap = new HashMap<Long, List<MealDesc>>();
			Map<Long, List<MealDesc>> studentOrderedExtraMap = new HashMap<Long, List<MealDesc>>();
			List<MealDesc> mealDescs = null;
			List<MealDesc> sidesDescs = null;
			List<MealDesc> extraDescs = null;
			MealDesc mealDesc = null;
			for(Object[] obj : studentOrdered){
				if(obj[3] != null && obj[3].toString().equalsIgnoreCase("Side")){
					mealDescs = studentOrderedSideMap.get(Long.valueOf(obj[0].toString()));
					if(mealDescs == null)
						mealDescs = new ArrayList<MealDesc>();
					mealDesc = new MealDesc();
					mealDesc.setMealName(obj[1].toString());
					mealDesc.setMealDate(sdf.format(sdf.parse(obj[2].toString())));
					mealDescs.add(mealDesc);
					studentOrderedSideMap.put(Long.valueOf(obj[0].toString()), mealDescs);
				}else if(obj[3] != null && obj[3].toString().equalsIgnoreCase("Extra")){
					mealDescs = studentOrderedExtraMap.get(Long.valueOf(obj[0].toString()));
					if(mealDescs == null)
						mealDescs = new ArrayList<MealDesc>();
					
					mealDesc = new MealDesc();
					mealDesc.setMealName(obj[1].toString());
					mealDesc.setMealDate(sdf.format(sdf.parse(obj[2].toString())));
					mealDescs.add(mealDesc);
					studentOrderedExtraMap.put(Long.valueOf(obj[0].toString()), mealDescs);
				}else{
					mealDescs = studentOrderedMap.get(Long.valueOf(obj[0].toString()));
					if(mealDescs == null)
						mealDescs = new ArrayList<MealDesc>();
					
					mealDesc = new MealDesc();
					mealDesc.setMealName(obj[1].toString());
					mealDesc.setMealDate(sdf.format(sdf.parse(obj[2].toString())));
					mealDescs.add(mealDesc);
					studentOrderedMap.put(Long.valueOf(obj[0].toString()), mealDescs);
				}
			}
			List<String> stdIds = null;
			if(isOrder != null && isOrder && startDate.compareTo(endDate) == 0 && startDate.before(new Date()))
				stdIds = studentUserRepository.getServedStdIds(mealSchoolId, startDate, schoolYear, menuType.toString());
			List<StudentInfoWithMeal> studentInfoWithMeals = new ArrayList<StudentInfoWithMeal>();
			/**This Map store the key as the meal date and value as the PobjO class which contains the meal date and name**/
			Map<String, List<MealDesc>> orderedMealsByDate = null;
			StudentInfoWithMeal studentInfoWithMeal = null;
			/**Iterating all the student users and setting them accordingly**/
				for(StudentUser studentUser : studentUsers){
					if(mealSchool == null)
						mealSchool = studentUser.getMealSchool();
					studentInfoWithMeal = new StudentInfoWithMeal();
					studentInfoWithMeal.setStudentFName(studentUser.getFirstName());
					studentInfoWithMeal.setStudentLName(studentUser.getLastName());
					studentInfoWithMeal.setStudentId(studentUser.getStudentId());
					if(studentUser.getTeacherName() == null)
						studentInfoWithMeal.setTeacherName("");
					else
						studentInfoWithMeal.setTeacherName(studentUser.getTeacherName()/*.toUpperCase()*/);
					
					studentInfoWithMeal.setGrade(studentUser.getGradeName().toString());
					studentInfoWithMeal.setAllergies(studentUser.getAllergies() != null ? studentUser.getAllergies()/*.toUpperCase()*/ : "");
					mealDescs = studentOrderedMap.get(studentUser.getUserId()); //getting all the ordered meal details by studentId
					sidesDescs = studentOrderedSideMap.get(studentUser.getUserId()); //getting all the ordered meal details by studentId
					extraDescs = studentOrderedExtraMap.get(studentUser.getUserId()); //getting all the ordered meal details by studentId
					/**If ordered meals not null then manipulating them and setting it to orderedMealMenus**/
					if(mealDescs != null){
						/**This map having key as the meal date and value as the list of meal items**/
						Map<String, List<String>> orderedMealMenus = new HashMap<String, List<String>>();
						/**Getting ordered meals details using group by meal date and setting it to map which having key as meal date
						 * and value as the list of pobjo class which contains meals details**/
						orderedMealsByDate = mealDescs.stream().collect(Collectors.groupingBy(MealDesc::getMealDate));
						
						/**Iterating the map and setting it to new map where key is the meal date and value as the list of meal items**/
						orderedMealsByDate.entrySet().forEach((e) -> {
							List<String> menus = new ArrayList<String>();
							for(MealDesc mealDesc1 : e.getValue()){
								menus.add(mealDesc1.getMealName());
							}
							orderedMealMenus.put(e.getKey(), menus);
				        });
						studentInfoWithMeal.setMealOrderedByDate(orderedMealMenus);
					}
					if(sidesDescs != null){
						/**This map having key as the meal date and value as the list of meal items**/
						Map<String, List<String>> orderedMealMenus = new HashMap<String, List<String>>();
						/**Getting ordered meals details using group by meal date and setting it to map which having key as meal date
						 * and value as the list of pobjo class which contains meals details**/
						orderedMealsByDate = sidesDescs.stream().collect(Collectors.groupingBy(MealDesc::getMealDate));
						
						/**Iterating the map and setting it to new map where key is the meal date and value as the list of meal items**/
						orderedMealsByDate.entrySet().forEach((e) -> {
							List<String> menus = new ArrayList<String>();
							for(MealDesc mealDesc1 : e.getValue()){
								menus.add(mealDesc1.getMealName());
							}
							orderedMealMenus.put(e.getKey(), menus);
				        });
						studentInfoWithMeal.setSideOrderedByDate(orderedMealMenus);
					}
					if(extraDescs != null){
						/**This map having key as the meal date and value as the list of meal items**/
						Map<String, List<String>> orderedMealMenus = new HashMap<String, List<String>>();
						/**Getting ordered meals details using group by meal date and setting it to map which having key as meal date
						 * and value as the list of pobjo class which contains meals details**/
						orderedMealsByDate = extraDescs.stream().collect(Collectors.groupingBy(MealDesc::getMealDate));
						
						/**Iterating the map and setting it to new map where key is the meal date and value as the list of meal items**/
						orderedMealsByDate.entrySet().forEach((e) -> {
							List<String> menus = new ArrayList<String>();
							for(MealDesc mealDesc1 : e.getValue()){
								menus.add(mealDesc1.getMealName());
							}
							orderedMealMenus.put(e.getKey(), menus);
				        });
						studentInfoWithMeal.setExtraOrderedByDate(orderedMealMenus);
						if(!havingExtraPreOrders)
							havingExtraPreOrders = true;
					}
					
					if(isOrder != null && isOrder){
						if(stdIds != null && stdIds.contains(studentInfoWithMeal.getStudentId()))
							studentInfoWithMeal.setServed("Yes");
						else if(startDate.before(new Date()))
							studentInfoWithMeal.setServed("No");
						else
							studentInfoWithMeal.setServed("");
						if(studentInfoWithMeal.getMealOrderedByDate() != null || studentInfoWithMeal.getExtraOrderedByDate() != null)
							studentInfoWithMeals.add(studentInfoWithMeal);
					}else
						studentInfoWithMeals.add(studentInfoWithMeal);
				}
			/**Sort the Students by last name **/
			studentInfoWithMeals.sort(Comparator.comparing(StudentInfoWithMeal::getStudentLName));
			schoolMealReportResp.setStudentWithMeal(studentInfoWithMeals);
			if(mealSchool != null && mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Manage Kids Allergies") != null && mealSchool.getModuleAccess().get("Manage Kids Allergies").equalsIgnoreCase("Yes"))
				schoolMealReportResp.setIsAllergyEnabled(true);
		}catch(Exception e){
			logger.error("Error occurred during execution of schoolMealReport API "+e.getMessage());
			throw new Exception("Failed to gerenate the school report");
		}
		schoolMealReportResp.setHavingExtraPreOrders(havingExtraPreOrders);
		schoolMealReportResp.setCountryCode(mealSchool.getCountryCode());
		return schoolMealReportResp;
	}
	


	/**This method used for generate the Caterer Orders Report**/
	@Override
	public ServiceResponse catererOrdersReport(Long catererId, Date startDate, Date endDate,
			ItemTypeConstants menuType, Long mealSchoolId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String itemType = CommonUtil.getItemType(menuType);
			/**Getting all the student details who orders meal in given criteria**/
			List<Object[]> studentOrdered = reportsDao.catererOrders(catererId, startDate, endDate, itemType, menuType, mealSchoolId);
			
			List<StudentInfoWithMeal> studentInfoWithMeals = new ArrayList<StudentInfoWithMeal>();
			/**This Map store the key as the meal date and value as the PobjO class which contains the meal date and name**/
			StudentInfoWithMeal studentInfoWithMeal = null;
			List<CaterersResp> caterersResps = new ArrayList<>();
			CaterersResp caterersResp = null;
			List<Long> studentRecIds = new ArrayList<>();
			for(Object[] obj : studentOrdered){
				caterersResp = new CaterersResp();
				caterersResp.setCategory(obj[3] != null ? obj[3].toString() : "");
				caterersResp.setStdRecId(obj[0] != null ? Long.valueOf(obj[0].toString()) : 0);
				caterersResp.setItemName(obj[1] != null ? obj[1].toString() : "");
				caterersResp.setMealDate(sdf.format(sdf.parse(obj[2].toString())));
				caterersResp.setMealSchoolId(Long.valueOf(obj[10].toString()));
				if(!studentRecIds.contains(Long.valueOf(obj[0].toString()))){
					studentInfoWithMeal = new StudentInfoWithMeal();
					studentInfoWithMeal.setStdRecId(Long.valueOf(obj[0].toString()));
					studentInfoWithMeal.setStudentFName(obj[4] != null ? obj[4].toString() : "");
					studentInfoWithMeal.setStudentLName(obj[5] != null ? obj[5].toString() : "");
					studentInfoWithMeal.setStudentId(obj[8] != null ? obj[8].toString() : "");
					studentInfoWithMeal.setTeacherName(obj[9] != null ? obj[9].toString() : "");
					studentInfoWithMeal.setGrade(obj[6] != null ? obj[6].toString() : "");
					studentInfoWithMeal.setAllergies(obj[7] != null ? obj[7].toString() : "");
					studentInfoWithMeal.setMealSchoolId(Long.valueOf(obj[10].toString()));
					studentInfoWithMeals.add(studentInfoWithMeal);
					studentRecIds.add(studentInfoWithMeal.getStdRecId());
				}
				caterersResp.setGrade(obj[6] != null ? obj[6].toString() : "");
				caterersResps.add(caterersResp);
			}
			List<StudentInfoWithMeal> studentInfoWithMeals2 = new ArrayList<StudentInfoWithMeal>();
			Map<Long, List<CaterersResp>> repByStd = caterersResps.stream().collect(Collectors.groupingBy(CaterersResp::getStdRecId));
			/**Iterating all the student users and setting them accordingly**/
			for(StudentInfoWithMeal stInfoWithMeal : studentInfoWithMeals){
				List<CaterersResp> caterersResps2 = repByStd.get(stInfoWithMeal.getStdRecId());
				Map<String, Map<String, List<CaterersResp>>> repByDateCategory = new HashMap<>();
				Map<String, List<CaterersResp>> repByDate = caterersResps2.stream().collect(Collectors.groupingBy(CaterersResp::getMealDate));
				for(Map.Entry<String, List<CaterersResp>> entry : repByDate.entrySet()){
					Map<String, List<CaterersResp>> repByCategory = entry.getValue().stream().collect(Collectors.groupingBy(CaterersResp::getCategory));
					repByDateCategory.put(entry.getKey(), repByCategory);
				}
				stInfoWithMeal.setOrders(repByDateCategory);
				if(repByDateCategory.size() > 0)
					studentInfoWithMeals2.add(stInfoWithMeal);
			}
			/**Sort the Students by last name **/
			studentInfoWithMeals2.sort(Comparator.comparing(StudentInfoWithMeal::getStudentLName));
			serviceResponse.setResponse(studentInfoWithMeals2);
			String category = CommonUtil.getItemType(menuType);
			Map<Long, Map<String, Integer>> countBySchoolGrade = new HashMap<>();
			Map<Long, List<CaterersResp>> repBySchool = caterersResps.stream().filter(cr -> cr.getCategory().equalsIgnoreCase(category)).collect(Collectors.groupingBy(CaterersResp::getMealSchoolId));
			for(Map.Entry<Long, List<CaterersResp>> entry : repBySchool.entrySet()){
				Map<String, List<CaterersResp>> repByGrade = entry.getValue().stream().collect(Collectors.groupingBy(CaterersResp::getGrade));
				Map<String, Integer> countByGrade = new HashMap<>();
				for(Map.Entry<String, List<CaterersResp>> entry2 : repByGrade.entrySet()){
					countByGrade.put(entry2.getKey(), entry2.getValue().size());
				}
				countBySchoolGrade.put(entry.getKey(), countByGrade);
			}
			serviceResponse.setSummary(countBySchoolGrade);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Caterer Orders report generated successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate Caterer orders report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get the ordered item details report**/
	@Override
	public List<OrderedMealItemsReport> orderedMealItemsReport(Long mealSchoolId, Date startDate, Date endDate,	
			Boolean paymentStatus, List<String> grades) throws Exception{
		List<OrderedMealItemsReport> orderedMealItemsReports = new ArrayList<OrderedMealItemsReport>();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Object[]> objArrayList = reportsDao.orderedMealItemsReport(mealSchoolId, startDate, endDate, paymentStatus, grades);
			for(Object[] obj : objArrayList){
				OrderedMealItemsReport orderedMealItemsReport = new OrderedMealItemsReport();
				orderedMealItemsReport.setId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
				orderedMealItemsReport.setStudentId(obj[1] != null ? obj[1].toString() : null);
				orderedMealItemsReport.setStudentRecId(obj[2] != null ? Long.parseLong(obj[2].toString()) : null);
				orderedMealItemsReport.setMealSchoolId(obj[3] != null ? Long.parseLong(obj[3].toString()) : null);
				orderedMealItemsReport.setGrade(obj[4] != null ? obj[4].toString() : null);
				orderedMealItemsReport.setMealId(obj[5] != null ? Long.parseLong(obj[5].toString()) : null);
				orderedMealItemsReport.setMealName(obj[6] != null ? obj[6].toString() : null);
				orderedMealItemsReport.setMealType(obj[7] != null ? obj[7].toString() : null);
				orderedMealItemsReport.setMealPrice(obj[8] != null ? Double.parseDouble(obj[8].toString()) : null);
				orderedMealItemsReport.setStudentFname(obj[9] != null ? obj[9].toString() : null);
				orderedMealItemsReport.setStudentLname(obj[10] != null ? obj[10].toString() : null);
				orderedMealItemsReport.setMealDate(obj[11] != null ? sdf.format(sdf.parse(obj[11].toString())) : null);
				orderedMealItemsReport.setYearMonth(obj[12] != null ? obj[12].toString() : null);
				orderedMealItemsReport.setMealImage(obj[13] != null ? obj[13].toString() : null);
				orderedMealItemsReport.setSchoolMealId(obj[14] != null ? Long.parseLong(obj[14].toString()) : null);
				orderedMealItemsReport.setPaymentStatus(obj[15] != null ? Boolean.parseBoolean(obj[15].toString()) : null);
				orderedMealItemsReports.add(orderedMealItemsReport);
			}
			logger.info("orderedMealItemsReport API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occurred during execution of the orderedMealItemsReport API "+e.getMessage());
			throw new Exception("Error occurred during execution of the orderedMealItemsReport API");
		}
		return orderedMealItemsReports;
	}

	/**This method used for get the order summary report**/
	@Override
	public List<MealOrderReport> orderSummaryReport(Long mealSchoolId, List<String> yearMonths, Boolean paymentStatus,
			List<String> grades, ItemTypeConstants menuType) throws Exception {
		List<MealOrderReport> mealOrderReports = new ArrayList<MealOrderReport>();
		try{
			List<Object[]> objArrayList = reportsDao.orderSummaryReport(mealSchoolId, yearMonths, paymentStatus, grades, menuType);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(Object[] obj : objArrayList){
				MealOrderReport mealOrderReport = new MealOrderReport();
				mealOrderReport.setRecNo(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
				mealOrderReport.setParentId(obj[1] != null ? Long.parseLong(obj[1].toString()) : null);
				mealOrderReport.setStudentId(obj[2] != null ? obj[2].toString() : null);
				mealOrderReport.setStudentRecId(obj[3] != null ? Long.parseLong(obj[3].toString()) : null);
				mealOrderReport.setStudentFName(obj[4] != null ? obj[4].toString() : null);
				mealOrderReport.setStudentLName(obj[5] != null ? obj[5].toString() : null);
				mealOrderReport.setGrade(obj[6] != null ? obj[6].toString() : null);
				mealOrderReport.setOrderDate(obj[7] != null ? sdf.format(sdf.parse(obj[7].toString())) : null);
				mealOrderReport.setOrderPrice(obj[8] != null ? Double.parseDouble(obj[8].toString()) : null);
				mealOrderReport.setPaymentStatus(obj[9] != null ? Boolean.parseBoolean(obj[9].toString()) : null);
				mealOrderReport.setTotItems(obj[10] != null ? Integer.parseInt(obj[10].toString()) : null);
				mealOrderReport.setYearMonth(obj[11] != null ? obj[11].toString() : null);
				mealOrderReport.setMealSchoolId(obj[12] != null ? Long.parseLong(obj[12].toString()) : null);
				mealOrderReport.setPdfLink(obj[13] != null ? obj[13].toString() : null);
				mealOrderReport.setOrderId(obj[14] != null ? Long.parseLong(obj[14].toString()) : null);
				if(obj[16] != null)
					mealOrderReport.setOrderDateTime(sdf1.parse(obj[16].toString()));
				else
					mealOrderReport.setOrderDateTime(sdf1.parse(obj[15].toString()));
				mealOrderReports.add(mealOrderReport);
			}
			logger.info("orderSummaryReport API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occurred during execution of the orderSummaryReport API "+e.getMessage());
			throw new Exception("Error occurred during execution of the orderSummaryReport API");
		}
		return mealOrderReports;
	}	
	
	/**This method used for set the ordered, paid and not paid count by grade**/
	/*private Map<String, CountChildObject> mapOrderedCountByGrade(List<Object[]> orderedPaymentAmtArrList){
		Map<String, CountChildObject> studentCountsResps = new HashMap<String, CountChildObject>();
		List<CountByGrade> countPaymentByGrades = new ArrayList<CountByGrade>();
		//List<CountByGrade> paymentByGrades = new ArrayList<CountByGrade>();
		CountByGrade countByGrade = null;
		for(Object[] obj : orderedPaymentAmtArrList){
			countByGrade = new CountByGrade();
			countByGrade.setTotalCount(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
			countByGrade.setTotalAmount(obj[1] != null ? Double.parseDouble(obj[1].toString()) : null);
			countByGrade.setGradeName(obj[2] != null ? obj[2].toString() : null);
			countByGrade.setPaymentStatus(obj[3] != null ? Boolean.parseBoolean(obj[3].toString()) : null);	
			countPaymentByGrades.add(countByGrade);
		}
		//paymentByGrades.addAll(countByGrades);
		
		for(Object[] obj : paymentAmtOrders){
			CountByGrade countByGrade = new CountByGrade();
			countByGrade.setTotalAmount(obj[0] != null ? Double.parseDouble(obj[0].toString()) : null);
			countByGrade.setGradeName(obj[1] != null ? obj[1].toString() : null);
			countByGrade.setPaymentStatus(obj[2] != null ? Boolean.parseBoolean(obj[2].toString()) : null);	
			paymentByGrades.add(countByGrade);
		}
		
		*//**Setting the paid ordered count**//*
		Map<String, Long> paidCountingByGrade =countPaymentByGrades.stream().filter(p ->  p.getPaymentStatus() != null 
				&& p.getPaymentStatus() == true).collect(
		                Collectors.groupingBy(CountByGrade::getGradeName, Collectors.summingLong(CountByGrade::getTotalCount)));
		Map<String, Double> paidAmtByGrade =countPaymentByGrades.stream().filter(p ->  p.getPaymentStatus() != null 
				&& p.getPaymentStatus() == true).collect(
		                Collectors.groupingBy(CountByGrade::getGradeName, Collectors.summingDouble(CountByGrade::getTotalAmount)));
		CountChildObject paidCountChildObject = new CountChildObject();
		paidCountChildObject.setCountByGrades(paidCountingByGrade);
		paidCountChildObject.setAllGradesCount(countPaymentByGrades.stream().filter(p ->  p.getPaymentStatus() != null 
				&& p.getPaymentStatus() == true).collect(Collectors.summingLong(CountByGrade::getTotalCount)));
		paidCountChildObject.setAllGradesPaidAmount(countPaymentByGrades.stream().filter(p ->  p.getPaymentStatus() != null 
				&& p.getPaymentStatus() == true).collect(Collectors.summingDouble(CountByGrade::getTotalAmount)));
		paidCountChildObject.setPaidAmountByGrades(paidAmtByGrade);
		studentCountsResps.put("paidOrdered", paidCountChildObject);
		
		*//**Setting the not paid ordered count**//*
		Map<String, Long> notPaidCountingByGrade =countPaymentByGrades.stream().filter(p ->  p.getPaymentStatus() != null 
				&& p.getPaymentStatus() == false).collect(
		                Collectors.groupingBy(CountByGrade::getGradeName, Collectors.summingLong(CountByGrade::getTotalCount)));
		Map<String, Double> notPaidAmtByGrade =countPaymentByGrades.stream().filter(p ->  p.getPaymentStatus() != null 
				&& p.getPaymentStatus() == false).collect(
		                Collectors.groupingBy(CountByGrade::getGradeName, Collectors.summingDouble(CountByGrade::getTotalAmount)));
		CountChildObject notPaidCountChildObject = new CountChildObject();
		notPaidCountChildObject.setCountByGrades(notPaidCountingByGrade);
		notPaidCountChildObject.setAllGradesCount(countPaymentByGrades.stream().filter(p ->  p.getPaymentStatus() != null 
				&& p.getPaymentStatus() == false).collect(Collectors.summingLong(CountByGrade::getTotalCount)));
		notPaidCountChildObject.setAllGradesNotPaidAmount(countPaymentByGrades.stream().filter(p ->  p.getPaymentStatus() != null 
				&& p.getPaymentStatus() == false).collect(Collectors.summingDouble(CountByGrade::getTotalAmount)));
		notPaidCountChildObject.setNotPaidAmountByGrades(notPaidAmtByGrade);
		studentCountsResps.put("notPaidOrdered", notPaidCountChildObject);
		
		*//**Setting the total ordered count**//*
		Map<String, Long> countingByGrade = countByGrades.stream().collect(
                Collectors.groupingBy(CountByGrade::getGradeName, Collectors.counting()));
		Map<String, Long> orderedCountingByGrade = countPaymentByGrades.stream().collect(
                Collectors.groupingBy(CountByGrade::getGradeName, Collectors.summingLong(CountByGrade::getTotalCount)));
		CountChildObject orderedcountChildObject = new CountChildObject();
		orderedcountChildObject.setCountByGrades(orderedCountingByGrade);
		orderedcountChildObject.setAllGradesCount(countPaymentByGrades.stream().collect(Collectors.summingLong(CountByGrade::getTotalCount)));
		studentCountsResps.put("ordered", orderedcountChildObject);
		return studentCountsResps;
	}*/
	
	/**This method used for set the ordered count by grade**/
	private CountChildObject mapOrderedCountByGrade(List<Object[]> CountArrList){
		Map<String, Long> CountingByGrade = new HashMap<String, Long>();
		Long totalCount = (long) 0;
		for(Object[] obj : CountArrList){
			CountingByGrade.put(obj[1].toString(), Long.parseLong(obj[0].toString()));
			totalCount = totalCount+Long.parseLong(obj[0].toString());
		}
		
		/**Setting the total ordered count**/
		CountChildObject orderedcountChildObject = new CountChildObject();
		orderedcountChildObject.setCountByGrades(CountingByGrade);
		orderedcountChildObject.setAllGradesCount(totalCount);
		return orderedcountChildObject;
	}
	
	/**This method used for set the not ordered count by grade**/
	private CountChildObject mapNotOrderedCountByGrade(List<Object[]> allStudentCountArrList, CountChildObject orderedCount){
		Map<String, Long> CountingByGrade = new HashMap<String, Long>();
		Long totalCount = (long) 0;
		Map<String, Long> orderedCountByGrade = orderedCount.getCountByGrades();
		if(allStudentCountArrList != null)
			for(Object[] obj : allStudentCountArrList){
				CountingByGrade.put(obj[1].toString(), Long.parseLong(obj[0].toString()) - 
						(orderedCountByGrade.get(obj[1].toString()) != null ? orderedCountByGrade.get(obj[1].toString()) :0));
				totalCount = totalCount+Long.parseLong(obj[0].toString());
			}
		
		/**Setting the total ordered count**/
		CountChildObject orderedcountChildObject = new CountChildObject();
		orderedcountChildObject.setCountByGrades(CountingByGrade);
		orderedcountChildObject.setAllGradesCount(totalCount - orderedCount.getAllGradesCount());
		return orderedcountChildObject;
	}

	/**This method used for export the caterer report in excel/pdf file**/
	@Override
	public ServiceResponse catererExportReport(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, 
			HttpServletResponse response, String fileType, String catererEmail, Boolean byItem, Boolean isVersion2, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{		
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			Boolean isItemized = CommonUtil.checkItemized(mealSchool);
			Map<String, Map<String, Integer>> mealsMap = mealForCatererPdfReport(mealSchoolId, startDate, endDate, grades, isVersion2, menuType);
			if(mealsMap == null || mealsMap.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("No data available.");
				return serviceResponse;
			}
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			if(fileType.equalsIgnoreCase("Pdf")){
				pdfGenerateForReport.exportCaterePdfReport(mealSchool.getSchoolName(), grades, startDate, response, 
						mealSchool.getLogoLink(), mealsMap, endDate, null, mealSchoolId, catererEmail, mealSchool.getContactPEmail(), 
						byItem, isItemized, menuType, mealSchool.getCountryCode(), 
						(countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy"),CommonUtil.getNonSchoolDays(mealSchool));
			}else{
				/*CatererReportResp catererReportResp = new CatererReportResp();
				catererReportResp=catererReport(mealSchoolId, startDate, endDate, grades);
				excelGenerateForReport.exportCatererReport(catererReportResp, response);*/
				excelGenerateForReport.exportCatererReport(mealSchool.getSchoolName(), grades, startDate, response, mealsMap, 
						endDate, mealSchool.getCountryCode());
			}
			serviceResponse.setStatus("Success");
			if(catererEmail != null && !catererEmail.trim().isEmpty())
				serviceResponse.setStatusMessage("Caterer Report has been sent sucessfully.");
			else
				serviceResponse.setStatusMessage("Caterer Report has been exported successfully in "+fileType+" format.");			
			logger.info(serviceResponse.getStatusMessage()+" with mealSchoolId::"+mealSchoolId+" and catererEmail::"+catererEmail);
				
			serviceResponse.setStatusCode(200);
		}catch(Exception e){
			logger.error("Error occurred during export excel file for caterer report in "+fileType+" format."+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to export caterer report in "+fileType+" format.");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
		}
	return serviceResponse;
		
	}

	/**This method used for export the school meals report in excel/pdf file.**/
	@Override
	public ServiceResponse schoolExportReport(Long mealSchoolId, Date startDate, Date endDate, List<SchoolGrades> grades,
			HttpServletResponse response, String fileType, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try
		{
			SchoolMealReportResp schoolMealReportResp = new SchoolMealReportResp();
			schoolMealReportResp = schoolReport(mealSchoolId, startDate, endDate, grades, schoolYear, fileType, false, isVersion2, menuType);
			if(schoolMealReportResp == null || schoolMealReportResp.getMealsByGradeAndDate() == null || 
					schoolMealReportResp.getMealsByGradeAndDate().size() < 1 || schoolMealReportResp.getStudentWithMeal() == null || 
					schoolMealReportResp.getStudentWithMeal().size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("No data available.");
				serviceResponse.setStatusCode(417);
				return serviceResponse;
			}
			if(fileType.equalsIgnoreCase("Pdf")){
				//MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
				schoolPdfReportGeneration.exportSchoolPdfReport(schoolMealReportResp, response, null, mealSchoolId);
			}else{
				//schoolMealReportResp=schoolMealReport(mealSchoolId, startDate, endDate, grades, schoolYear);
				excelGenerateForReport.exportSchoolMealReport(schoolMealReportResp, response);
			}
			
			logger.info("School meals report in "+fileType+" format has been exported successfully");
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("School report generated.");
			serviceResponse.setStatusCode(200);
		}catch(Exception e){
			logger.error("Error occurred during export "+fileType+" file for school meal report");
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to export school meal report in "+fileType+" format.");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
		}
	return serviceResponse;
	}
	
	/**This method used for generate the order report in pdf**/
	@Override
	public ServiceResponse orderReportExport(Long mealSchoolId, Date startDate, Date endDate, List<SchoolGrades> grades,
			HttpServletResponse response, String fileType, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			SchoolMealReportResp schoolMealReportResp = new SchoolMealReportResp();
			schoolMealReportResp = schoolReport(mealSchoolId, startDate, endDate, grades, schoolYear, fileType, true, isVersion2, menuType);
			if(schoolMealReportResp == null || schoolMealReportResp.getStudentWithMeal() == null || 
					schoolMealReportResp.getStudentWithMeal().size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("No data available.");
				serviceResponse.setStatusCode(417);
				return serviceResponse;
			}
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			Boolean isItemized = CommonUtil.checkItemized(mealSchool);
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			orderPdfReportGeneration.exportOrderPdfReport(schoolMealReportResp, response, null, mealSchoolId, isItemized, menuType, (countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy"));
			serviceResponse.setStatusMessage("Orders report in pdf has been generated successfully");
			logger.info(serviceResponse.getStatusMessage());
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
		}catch(Exception e){
			serviceResponse.setStatusMessage("Failed to generate orders report in pdf.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			logger.error(serviceResponse.getStatusMessage()+" for mealSchoolId::"+mealSchoolId+" and schoolYear::"+schoolYear+" due to "+e.getMessage());
		}
	return serviceResponse;
	}


	/**This method used for get the details of requested email id regarding parent self registration
	 * @throws Exception **/
	@Override
	public List<SelfRegParentRequestedEmail> selfRegReqParentDetails(Date requestedTimeStart, Date requestedTimeEnd,
			Boolean sendStatus) throws Exception {
		List<SelfRegParentRequestedEmail> selfRegParentRequestedEmails = new ArrayList<SelfRegParentRequestedEmail>();
		try{
			List<Object[]> requestedEmailDetails = reportsDao.selfRegReqParentDetails(requestedTimeStart, requestedTimeEnd, sendStatus);
			for(Object[] obj : requestedEmailDetails){
				SelfRegParentRequestedEmail selfRegParentRequestedEmail = new SelfRegParentRequestedEmail();
				selfRegParentRequestedEmail.setRecId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
				selfRegParentRequestedEmail.setEmailId(obj[1] != null ? obj[1].toString() : null);
				selfRegParentRequestedEmail.setRequestedTime(obj[2] != null ? obj[2].toString() : null);
				selfRegParentRequestedEmail.setLinkSendStatus(obj[3] != null ? Boolean.parseBoolean(obj[3].toString()) : null);
				selfRegParentRequestedEmails.add(selfRegParentRequestedEmail);
			}
			logger.info("The selfRegReqParentDetails API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occurred during execution of selfRegReqParentDetails API to get the requested parent user "
					+ "details for self registartion. "+e.getMessage());
			throw new Exception("Error occured during execution of selfRegReqParentDetails API");
		}
		return selfRegParentRequestedEmails;
	}
	
	/**This method created for get the meal items by date with count
	 * @throws ParseException **/
	private Map<String,Map<String, Integer>> mealForCatererPdfReport(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, Boolean isVersion2, ItemTypeConstants menuType) throws ParseException{
		Map<String, Map<String, Integer>> mealDetails = new HashMap<String, Map<String, Integer>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> allMealsByDate = null;
		if(isVersion2 !=  null && isVersion2)
			allMealsByDate = reportsDao.allMealsWithOrderedCountByDateV2(mealSchoolId, startDate, endDate, grades, menuType);
		else
			allMealsByDate = reportsDao.allMealsWithOrderedCountByDate(mealSchoolId, startDate, endDate, grades);
		Map<String, Integer> mealNamesWithCount = null;
		for(Object[] obj : allMealsByDate){
			String dt = (obj[1] != null ? sdf.format(sdf.parse(obj[1].toString())) : null);
			mealNamesWithCount = mealDetails.get(dt);
			if(mealNamesWithCount == null)
				mealNamesWithCount = new HashMap<String, Integer>();
			if(obj[0] != null && obj[2] != null && Integer.parseInt(obj[2].toString()) > 0)
				mealNamesWithCount.put(obj[0].toString(), Integer.parseInt(obj[2].toString()));	
			mealDetails.put(dt, mealNamesWithCount);
		}

		Map<String, Map<String, Integer>> sortedMealMap = new TreeMap<String, Map<String, Integer>>(mealDetails);
		logger.info("mealForCatererPdfReport method executed successfully.");
		return sortedMealMap;
	}

	/**This method used for generate the all reports and upload them on S3 bucket then share the link to logged admin user through email **/
	@Override
	public ServiceResponse exportReports(Long mealSchoolId, Date startDate, Date endDate, List<SchoolGrades> grades,
			String loggedUser, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try
		{
			List<String> gradeNames = getGradeNamesVal(grades);
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			SchoolMealReportResp schoolMealReportResp =schoolReport(mealSchoolId, startDate, endDate, grades, schoolYear, "Pdf", null, isVersion2, menuType);
			Map<String, Map<String, Integer>> catererMealMap = mealForCatererPdfReport(mealSchoolId, startDate, endDate, gradeNames, isVersion2, menuType);
			if(catererMealMap == null || catererMealMap.size() < 1 || schoolMealReportResp == null || schoolMealReportResp.getMealsByGradeAndDate().size() < 1
					|| schoolMealReportResp.getStudentWithMeal() == null || schoolMealReportResp.getStudentWithMeal() == null || schoolMealReportResp.getStudentWithMeal().size()<1){
				serviceResponse.setStatus("Fail");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("No data available for the selected month.");
				return serviceResponse;
			}
			Boolean isItemized = CommonUtil.checkItemized(mealSchool);
			generateAllReportsInPdf.exportAllReports(schoolMealReportResp, mealSchool.getLogoLink(), 
						mealSchool.getSchoolName(), mealSchoolId, loggedUser, catererMealMap, gradeNames, isItemized, menuType,CommonUtil.getNonSchoolDays(mealSchool));
			
			logger.info("All reports will generate and upload into S3 bucket then S3 link will be shared in email of logged admin user");
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("All reports will generate and upload into S3 bucket then S3 link will be shared in email of logged admin user.");
			serviceResponse.setStatusCode(200);
		}catch(Exception e){
			logger.error("Error occurred during generating reports. "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to export all reports.");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
		}
		return serviceResponse;
	}

	/**This API used for get the ordered meal count with the grades and meal menu id 
	 * @throws Exception **/
	@Override
	public Map<String, List<MonthlyMenuDetailsResp>> monthlyMenuDetails(Long mealSchoolId, String orderDate, ItemTypeConstants menuType) throws Exception {
		Map<String, List<MonthlyMenuDetailsResp>> monthlyMenusMap = new LinkedHashMap<String, List<MonthlyMenuDetailsResp>>();
		try{
			String itemType = CommonUtil.getItemType(menuType);
			List<Object[]> mealDetails = reportsDao.monthlyMenuDetails(mealSchoolId, orderDate, itemType);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String mealDate = null;
			List<MonthlyMenuDetailsResp> monthlyMenus = null;
			MonthlyMenuDetailsResp monthlyMenuDetailsResp = null;
		
			for(Object[] obj : mealDetails){
				if(obj != null){
					mealDate = sdf.format(sdf1.parse(obj[3].toString()));					
					monthlyMenus = monthlyMenusMap.get(mealDate);
					if(monthlyMenus == null)
						monthlyMenus = new ArrayList<MonthlyMenuDetailsResp>();
					
					monthlyMenuDetailsResp = new MonthlyMenuDetailsResp();
					monthlyMenuDetailsResp.setItemId(Long.parseLong(obj[0].toString()));
					monthlyMenuDetailsResp.setItemName(obj[1].toString());
					monthlyMenuDetailsResp.setCount(Long.parseLong(obj[2].toString()));
					monthlyMenus.add(monthlyMenuDetailsResp);
					monthlyMenusMap.put(mealDate, monthlyMenus);
				}					
			}
		}catch(Exception e){
			logger.error("Error occurred during execution of the monthlyMenuDetails API");
			throw new Exception("Error occured during execution of monthlyMenuDetails API");
			
		}
		return monthlyMenusMap;
	}

	/**This method used for send the notification to all the relevant parent when any meal item changed with new one by caterer**/
	@Override
	public ServiceResponse mealChangeNotification(MealChangeNotificationRequest mealChangeReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Object[]> objArray = reportsDao.mealChangeDetailsForSendNotificationToParent(mealChangeReq);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Set<String> parentEmails = new HashSet<String>();
			String mealDate = "";
			String oldMealItem = "";
			for(Object[] obj : objArray){
				if(obj[0] != null && obj[4] != null && Integer.parseInt(obj[4].toString()) == 1)
					parentEmails.add(obj[0].toString());
				if(obj[1] != null && obj[5] != null  && Integer.parseInt(obj[5].toString()) == 1)
					parentEmails.add(obj[1].toString());
			}
			if(objArray.size() > 0){
				mealDate = sdf.format(sdf1.parse(objArray.get(0)[2].toString()));
				oldMealItem = objArray.get(0)[3].toString();
			}
			MealSchool mealSchool = null;
			String schoolName = "";
			if(mealChangeReq.getMealSchoolId() != null)
				mealSchool = mealSchoolRepository.findBySchoolId(mealChangeReq.getMealSchoolId());	
			schoolName = mealSchool.getSchoolName().toUpperCase();
			String mailContent = "";
			if(mealChangeReq.getCustomMessage() != null && !mealChangeReq.getCustomMessage().equalsIgnoreCase(""))
				mailContent = mealChangeReq.getCustomMessage();
			else
				mailContent = emailLunchChangeMsg.replace("<<oldMealItem>>", oldMealItem).replace("<<newItem>>", 
						mealChangeReq.getNewItemName()).replace("<<mealDate>>", mealDate);
			
			List<StatusUpdateNotificationReq> notificationInfos = new ArrayList<StatusUpdateNotificationReq>();
			Map<String, List<StatusUpdateNotificationReq>> notificationRequest = null;
			int i = 1;
			StatusUpdateNotificationReq notificationInfo = null;
			for(String email : parentEmails){
				notificationInfo = new StatusUpdateNotificationReq();
				notificationInfo.setEmail(email);
				notificationInfo.setOrdermsg(mailContent);
				notificationInfo.setSubjectMsg(emailLunchChangeSubject.replace("<<schoolName>>", schoolName));
				notificationInfo.setAdminEmail(mealSchool.getContactPEmail() != null ? mealSchool.getContactPEmail() : "");
				notificationInfos.add(notificationInfo);
				if(i == 50){
					notificationRequest = new HashMap<String, List<StatusUpdateNotificationReq>>();
					notificationRequest.put("users", notificationInfos);
					sendNotificationUtil.mealChangeNotificationToParent(notificationRequest);
					notificationInfos = new ArrayList<StatusUpdateNotificationReq>();
					i = 0;
				}
				i++;
			}
			
			if(parentEmails.size()>0){
				if(notificationInfos.size() > 0){
					notificationRequest = new HashMap<String, List<StatusUpdateNotificationReq>>();
					notificationRequest.put("users", notificationInfos);
					sendNotificationUtil.mealChangeNotificationToParent(notificationRequest);
				}				
				serviceResponse.setStatusMessage("Parent has been notify for Menu item change.");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatus("Success");
				logger.info("API successfully invoked for the send notification to parent user regarding meal item changes");
			}else{
				serviceResponse.setStatusMessage("There are no entry for send the notification.");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatus("Success");
				logger.info("API successfully invoked for the send notification to parent user regarding meal item changes");
			}
		}catch(Exception e){
			logger.error("Error occurred during execution of the mealChangeNotification API. "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to send notification to the parent user on Menu item change.");
			serviceResponse.setErrorMessage(e.getMessage());
			
		}
		return serviceResponse;
	}

	/**This method used for get all the students who haven't order meal but meal created for that grades by school**/
	@Override
	public List<NotOrderedStudentResp> notOrderedStudents(Long mealSchoolId, String yearMonth, List<String> grades, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType) throws Exception{
		List<NotOrderedStudentResp> notOrderedStudentResps = new ArrayList<>();
		try{
			List<Object[]> objArray = new ArrayList<Object[]>();
			List<String> gradeNames = null;
			if(isVersion2 != null && isVersion2)
				gradeNames = reportsDao.getMealPublishedGradesV2(mealSchoolId, yearMonth, grades, false, menuType);
			else
				gradeNames = reportsDao.getMealPublishedGrades(mealSchoolId, yearMonth, grades, false);
			if(gradeNames != null && gradeNames.size() > 0)
				objArray = reportsDao.notOrderedStudents(mealSchoolId, yearMonth, gradeNames, schoolYear, menuType);
			NotOrderedStudentResp notOrderedStudentResp = null;
			for(Object[] obj : objArray){
				notOrderedStudentResp = new NotOrderedStudentResp();
				notOrderedStudentResp.setStudentRecId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
				notOrderedStudentResp.setStudentId(obj[1] != null ? obj[1].toString() : null);
				notOrderedStudentResp.setStudentFName(obj[2] != null ? obj[2].toString() : null);
				notOrderedStudentResp.setStudentLName(obj[3] != null ? obj[3].toString() : null);
				notOrderedStudentResp.setGradeName(obj[4] != null ? obj[4].toString() : null);
				notOrderedStudentResps.add(notOrderedStudentResp);
			}
			logger.info("notOrderedStudents API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occurred during execution of the notOrderedStudents API. "+e.getMessage());
			throw new Exception("Error occurred during execution of the notOrderedStudents API. "+e.getMessage());
		}
		return notOrderedStudentResps;
	}

	/**This method used for get all the meals by meal summary id in the json format same like we are returning during meal excel import**/
	@Override
	public String mealsBySummaryId(Long mealSummaryId) throws Exception{
		String mealJson = "";
		try{
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	    	MealItems mealItems = new MealItems();
	    	List<Object[]> allItems = reportsDao.mealsBySummaryId(mealSummaryId);
	    	mealItems = buildMealsRequiredFormat(allItems);
	    	mealJson = objectMapper.writeValueAsString(mealItems);  	
	    	mealJson = mealJson.replace("\"new Date(", "new Date(").replace("')'\"", ")");
			logger.info("mealsBySummaryId API has been executed successfully");
		}catch(Exception e){
			logger.error("Error occurred during execution of the mealsBySummaryId API. "+e.getMessage());
			throw new Exception("Error occurred during execution of the mealsBySummaryId API. "+e.getMessage());
		}
		return mealJson;
	}
	
	/**This method used for map the meals data**/
	private MealItems buildMealsRequiredFormat(List<Object[]> allItems) throws Exception{
		MealItems mealItems = new MealItems();
		List<MealJsonData> extraItems = new ArrayList<MealJsonData>();
		List<MealJsonData> otherItems = new ArrayList<MealJsonData>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateValue = null;
		String[] dtArray = null;
		MealJsonData mealJsonData = null;
		for(Object[] obj : allItems){
			mealJsonData = new MealJsonData();
			if(obj[7] != null){
				mealJsonData.setId(obj[1] != null ? Integer.parseInt(obj[1].toString()) : null);
				mealJsonData.setPrice(obj[2] != null ? Double.parseDouble(obj[2].toString()) : null);
				mealJsonData.setReducedPrice(obj[3] != null ? Double.parseDouble(obj[3].toString()) : null);
				mealJsonData.setDesc(obj[4] != null ? obj[4].toString() : null);
				mealJsonData.setTitle(obj[5] != null ? obj[5].toString() : null);
				mealJsonData.setType(obj[7] != null ? obj[7].toString() : null);
				if(!obj[7].toString().equalsIgnoreCase("EXTRA")){
					if(obj[6] != null )
					dtArray = sdf.format(sdf.parse(obj[6].toString())).split("-"); 
					dateValue = dtArray[0]+", "+(Integer.parseInt(dtArray[1])-1)+", "+(Integer.parseInt(dtArray[2]));
					mealJsonData.setStart("new Date("+dateValue+"')'");
					mealJsonData.setEnd("new Date("+dateValue+"')'");
					otherItems.add(mealJsonData);
				}else{
					extraItems.add(mealJsonData);
				}
			}
		}
		mealItems.setMealMenuItems(otherItems);
		mealItems.setExtra(extraItems);
		Object obj = allItems.get(0)[0];
		if(obj != null)
			mealItems.setReducedPriceStatus(Boolean.parseBoolean(obj.toString()));
		return mealItems;
	}

	/**This method used for get all the status of email send to parent user**/
	@Override
	public EmailSendResp emailSendStatus(String email) throws Exception{
		EmailSendResp emailSendResp = new EmailSendResp();
		try{
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsername(email);
			if(usersAuthInfo != null){
				emailSendResp.setEmailIsSubscribe(usersAuthInfo.getEmailIsSubscribe());
				emailSendResp.setLunchReminderEnable(usersAuthInfo.getLunchReminderEnable());
				emailSendResp.setPaymentReminderEnable(usersAuthInfo.getPaymentReminderEnable());
			}
		}catch(Exception e){
			logger.error("Error occurred during execution of the emailSendStatus API. "+e.getMessage());
			throw new Exception("Error occurred during execution of the emailSendStatus API. "+e.getMessage());
		}
		return emailSendResp;
	}
	
	private List<String> getGradeNamesVal(List<SchoolGrades> grades){
		List<String> gradeNames = null;
		if(grades != null){
			gradeNames = new ArrayList<String>();
			for(SchoolGrades schoolGrades : grades){
				gradeNames.add(schoolGrades.toString());
			}
		}
		return gradeNames;
	}

	/**This method used for generate the allergies report in Excel/Pdf**/
	@Override
	public ServiceResponse allergiesReport(Long mealSchoolId, int schoolYear, HttpServletResponse response, 
			List<String> grades, String fileType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Object[]> objArray = reportsDao.studentsWithAllergiesDetails(mealSchoolId, schoolYear, grades);
			List<StudentsWithAllergies> studentsWithAllergies = new ArrayList<StudentsWithAllergies>();
			StudentsWithAllergies studentsWithAllergy = null; 
			String countryCode = mealSchoolRepository.getSchoolCountry(mealSchoolId);
			for(Object[] obj : objArray){
				studentsWithAllergy = new StudentsWithAllergies();
				studentsWithAllergy.setStdFName(obj[0] != null ? obj[0].toString() : null);
				studentsWithAllergy.setStdLName(obj[1] != null ? obj[1].toString() : null);
				studentsWithAllergy.setGrade(obj[2] != null ? obj[2].toString() : null);
				studentsWithAllergy.setTeacherName(obj[3] != null ? obj[3].toString() : "");
				studentsWithAllergy.setAllergies(obj[4] != null ? obj[4].toString() : null);
				studentsWithAllergies.add(studentsWithAllergy);
			}
			/**Used for sort the students by last name**/
			studentsWithAllergies.sort(Comparator.comparing(StudentsWithAllergies::getStdLName));
			if(studentsWithAllergies == null || studentsWithAllergies.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("No data available.");
				serviceResponse.setStatusCode(417);
				return serviceResponse;
			}
			if(fileType.equalsIgnoreCase("Pdf")){
				//MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
				generateAllergiesReportPdf.exportAllergiesPdfReport(studentsWithAllergies, response, mealSchoolId, schoolYear, countryCode);
			}else if(fileType.equalsIgnoreCase("Excel")){
				excelGenerateForReport.exportAllergiesExcelReport(studentsWithAllergies, response, countryCode);
			}else
				serviceResponse.setResponse(studentsWithAllergies);
			
			logger.info("Allergy report in "+fileType+" format has been generated successfully");
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Allergies report generated successfully.");
			serviceResponse.setStatusCode(200);
		}catch(Exception e){
			logger.error("Failed to generate Allergy report in "+fileType+" format.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to generate Allergy report in "+fileType+" format.");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
		}
		return serviceResponse;
	}

	/**This method used for get the year month for whom parent can order the menu**/
	@Override
	public ServiceResponse getMenuOrderYrMonth(String parentEmail, Boolean isVersion2) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Long> mealSchoolIds = reportsDao.mealSchoolIdsByParentEmail(parentEmail);
			if(mealSchoolIds != null && mealSchoolIds.size() > 0){
				if(isVersion2 != null && isVersion2)
					serviceResponse.setYearMonth(reportsDao.getLatestYearMonthV2(mealSchoolIds));
				else
					serviceResponse.setYearMonth(reportsDao.getLatestYearMonth(mealSchoolIds));
				serviceResponse.setStatusMessage("Latest year month recieved successfully.");
			}else
				serviceResponse.setStatusMessage("There are no eligible year month.");
				
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
		}catch(Exception e){
			logger.error("Failed to get the year month for whom parent can order the menu."+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get the year month for whom parent can order the menu.");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}
	
	/**This method used for generate the Free/Reduced Lunch Program Eligibility survey report **/
	@Override
	public ServiceResponse fmSurveyReport(Long mealSchoolId, HttpServletResponse response, String fileType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			Set<FMEligibilitySurvey> fmEligibilitySurveys = fMEligibilitySurveyRepository.findByMealSchoolsSchoolId(mealSchoolId);
			if(fmEligibilitySurveys == null || fmEligibilitySurveys.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("No data available.");
				return serviceResponse;
			}
			String countryCode = mealSchoolRepository.getSchoolCountry(mealSchoolId);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(mealSchoolId));
			List<FMEligibilitySurvey> fmEligibilitySurveyList = new ArrayList<FMEligibilitySurvey>(fmEligibilitySurveys);
			if(fileType.equalsIgnoreCase("Pdf")){
				/*MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
				if(mealSchool != null)*/
					generateAllergiesReportPdf.exportFMEligibilitySurveyPdfReport(fmEligibilitySurveyList, response, mealSchoolId,currencySymbol, countryCode);
				/*else{
					serviceResponse.setStatusCode(404);
					serviceResponse.setStatusMessage("This school does not exist.");
					serviceResponse.setStatus("Failed");
					return serviceResponse;
				}*/
			}else{
				excelGenerateForReport.exportFMEligibilityExcelReport(fmEligibilitySurveyList, response,currencySymbol);
			}
			
			logger.info("Free/Reduced Lunch Program Eligibility survey report in "+fileType+" format has been generated successfully");
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Free/Reduced Lunch Program Eligibility survey report has been generated successfully in "+fileType+" format.");
			serviceResponse.setStatusCode(200);
		}catch(Exception e){
			logger.error("Failed to generate Free/Reduced Lunch Program Eligibility survey report in "+fileType+" format.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to generate Free/Reduced Lunch Program Eligibility survey report in "+fileType+" format.");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
		}
		return serviceResponse;
	}
	
	/**This method used for export the actual free meal/reduced price eligibility report**/
	@Override
	public ServiceResponse fmActualReport(Long mealSchoolId, HttpServletResponse response, String fileType,
			int schoolYear, String eligType, Boolean isTemp, Boolean isDistId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String loggedUser = "";
			if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			LinkedList<FMActualReport> fmActualReports = new LinkedList<>();
			List<Object[]> objArray = reportsDao.studentFmEligibiltyData(mealSchoolId, schoolYear, eligType,isTemp,isDistId);
			LinkedList<FMActualReport> fmActualReports1 = mapFmActualData(objArray);
			String countryCode = null;
			String timezone = "";
			String name = "";
			if(isDistId != null && isDistId){
				countryCode = districtRepository.getSchoolCountry(mealSchoolId);
				timezone = districtRepository.getTimezone(mealSchoolId);
				name = districtRepository.getDistName(mealSchoolId);
			}else{
				countryCode = mealSchoolRepository.getSchoolCountry(mealSchoolId);
				timezone = mealSchoolRepository.getSchoolTimezone(mealSchoolId);
				name = mealSchoolRepository.getSchoolName(mealSchoolId);
			}
			String currDate = du.formatDateToString(new Date(), "yyyy-MM-dd HH:mm:ss", timezone);
			if(isTemp != null && isTemp){
				/*Comparator<FMActualReport> sortBy = Comparator.comparing(FMActualReport::getSchoolName)
	                    .thenComparing(FMActualReport::getStudentId);
				List<FMActualReport> repData = fmActualReports1.stream().sorted(sortBy).collect(Collectors.toList());*/
				List<FMActualReport> gradeStudent = null;
	    		Map<String, List<FMActualReport>> studentsByGrade = fmActualReports1.stream().collect(Collectors.groupingBy(
	    				FMActualReport::getGradeName));
	    		Set<String> grades = studentsByGrade.keySet();
	    		GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
	    	    SchoolGrades[] schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(new ArrayList<String>(grades)).toArray(new SchoolGrades[0]);
	    		Arrays.sort(schoolGrades);
	    		//iterate the grades and proceed one by one
	    		if(studentsByGrade != null)
		    		for(SchoolGrades gradeVal : schoolGrades){
		    				gradeStudent = studentsByGrade.get(gradeVal.toString());
		    				if(gradeStudent != null)
		    					fmActualReports.addAll(gradeStudent);
		        		}	
	    		fmActualReports.sort(Comparator.comparing(FMActualReport::getSchoolName));
			}else{
				fmActualReports1.sort(Comparator.comparing(FMActualReport::getStudentLName));
				fmActualReports.addAll(fmActualReports1);
			}
			
			if(fmActualReports == null || fmActualReports.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("No data available.");
				serviceResponse.setStatusCode(417);
				return serviceResponse;
			}
			if(fileType.equalsIgnoreCase("Pdf")){
				//MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
				//if(mealSchool != null)
				if(isTemp != null && isTemp)
					frTempEligStatusReport.exportFMEligibilitySurveyPdfReport(fmActualReports, response, mealSchoolId, schoolYear, eligType, countryCode,isDistId,loggedUser,currDate,name);
				else
					fMRPActualReport.exportFMEligibilitySurveyPdfReport(fmActualReports, /*mealSchool.getLogoLink(), 
							mealSchool.getSchoolName(), */response, mealSchoolId, schoolYear,eligType, countryCode);
				/*else{
					serviceResponse.setStatusCode(404);
					serviceResponse.setStatusMessage("This school does not exist.");
					serviceResponse.setStatus("Failed");
					return serviceResponse;
				}*/
			}else if(fileType.equalsIgnoreCase("Excel")){
				excelGenerateForReport.exportFreeMealReducedPrice(fmActualReports, response, eligType, countryCode);
			}else
				serviceResponse.setResponse(fmActualReports);
			
			logger.info("Free/Reduced Lunch Program Eligibility survey report in "+fileType+" format has been generated successfully");
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Free/Reduced Lunch Program Actual Eligibility report generated.");
			serviceResponse.setStatusCode(200);
		}catch(Exception e){
			logger.error("Failed to generate the free meal/reduced price actual eligibility survey report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the free meal/reduced price actual eligibility survey report.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for map the free meal/reduced price eligibility actual data**/
	private LinkedList<FMActualReport> mapFmActualData(List<Object[]> objArray){
		LinkedList<FMActualReport> fmActualReports = new LinkedList<FMActualReport>();
		FMActualReport fmActualReport = null;
		for(Object[] obj : objArray){
			fmActualReport = new FMActualReport();
			if(obj[0] != null)
				fmActualReport.setStudentFName(obj[0].toString());;
			if(obj[1] != null)
				fmActualReport.setStudentLName(obj[1].toString());;
			if(obj[2] != null)
				fmActualReport.setStudentId(obj[2].toString());
			if(obj[3] != null)
				fmActualReport.setFreeMeal(obj[3].toString().equalsIgnoreCase("true") ? true : false);
			if(obj[4] != null)
				fmActualReport.setReducedPrice(obj[4].toString().equalsIgnoreCase("true") ? true : false);
			if(obj[5] != null)
				fmActualReport.setGradeName(obj[5].toString());
			if(obj[6] != null)
				fmActualReport.setTeacherName(obj[6].toString());
			if(obj[7] != null)
				fmActualReport.setSchoolName(obj[7].toString());
			if(obj[8] != null)
				fmActualReport.setParentEmail(obj[8].toString());
			fmActualReports.add(fmActualReport);
		}
		return fmActualReports;
	}

	/**Using method to get the order month along with students based on parent email and month**/
	@Override
	public Map<String, List<Long>> monthAndStudentListByEmail(String parentEmail, String currentYearMonth, Boolean isVersion2) throws Exception {
		Map<String, List<Long>> monthAndStudentsMap = new HashMap<String, List<Long>>();
		try{
			List<Object[]> monthStudentsArray = null;
			if(isVersion2 != null && isVersion2)
				monthStudentsArray = reportsDao.monthStudentsByParentEmailV2(parentEmail, currentYearMonth);
			else
				monthStudentsArray = reportsDao.monthStudentsByParentEmail(parentEmail, currentYearMonth);
			monthAndStudentsMap = buildMonthStudentsMap(monthStudentsArray);
			logger.info("monthAndStudentListByEmail API executed successfully.");
		}catch(Exception e){
			logger.error("Failed to get the order month and student list due to "+e.getMessage());
			throw new Exception("Failed to get the order month & student details");
		}
		return monthAndStudentsMap;
	}
	
	private Map<String, List<Long>> buildMonthStudentsMap(List<Object[]> monthStudentsArray){
		Map<String, List<Long>> monthAndStudentsMap = new HashMap<String, List<Long>>(); 
		List<Long> studentIds = null;
		for(Object[] obj : monthStudentsArray){
			if(obj[0] != null && obj[1] != null){
				studentIds = monthAndStudentsMap.get(obj[1].toString());
				if(studentIds == null)
					studentIds = new ArrayList<Long>();
				studentIds.add(Long.parseLong(obj[0].toString()));
				monthAndStudentsMap.put(obj[1].toString(), studentIds);
			}
		}
		return new TreeMap<String, List<Long>>(monthAndStudentsMap);
	}

	/**This method used for generate the student's transaction history report**/
	@Override
	public ServiceResponse transactionsHistory(Long studentRecId, String startDate, String endDate,	
			HttpServletResponse response, Boolean fileExport) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			StudentUser studentUser = studentUserRepository.findByUserIdAndIsActive(studentRecId, true);
			if(studentUser == null || studentUser.getUserId() == null)
				throw new Exception("Student does not exist the record Id: "+studentRecId);
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(studentUser.getMealSchool().getSchoolId());
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			List<Object[]> transactionHstryData = reportsDao.transactionsHistory(studentRecId, startDate, endDate);
			StudentAccountDetails studentAccountDetails = buildAccountTransactionDetails(transactionHstryData, 
					mealSchool.getSchoolTimezone().toString(), startDate, endDate, studentUser, (countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy"));
			serviceResponse.setStudentAccountDetails(studentAccountDetails);
			if(fileExport != null && fileExport){
				if(studentAccountDetails == null || studentAccountDetails.getAccountTransactionHistories() == null
						|| studentAccountDetails.getAccountTransactionHistories().size() < 1){
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("No data available.");
					return serviceResponse;
				}
				//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
				transactionHistoryReportUtil.transactionHistoryReport(studentAccountDetails, mealSchool.getLogoLink(), 
					mealSchool.getSchoolName(), response,countryDetail.getCurrencySymbol());
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Generated the account transaction history report successfully.");
			serviceResponse.setStatusCode(200);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to generate the transaction history report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to generate the transaction history report.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate the deposit/purchase transactions report**/
	@SuppressWarnings("unchecked")
	@Override
	public ServiceResponse transactionsReport(Long mealSchoolId, String startDate, String endDate,
			HttpServletResponse response, Boolean fileExport, Boolean isDeposit, Integer schoolYear, Boolean isAdjTrx) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			
			if(mealSchool == null || mealSchool.getSchoolId() == null)
				throw new Exception("School doesn't exist with school id: "+mealSchoolId);
			List<Object[]> transactionsReportData = reportsDao.transactionsReport(mealSchoolId, startDate, endDate, isDeposit,schoolYear, isAdjTrx);
			List<TransactionsDetails> transactionsDetails = buildTransactionsReportObj(transactionsReportData, 
					mealSchool.getSchoolTimezone().toString(), isDeposit, (countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy"), isAdjTrx);
			//transactionsDetails.sort(Comparator.comparing(TransactionsDetails::getStudentLName));
			serviceResponse.setTransactionReportsDetails(transactionsDetails);
			if(fileExport != null && fileExport){
				if(transactionsDetails == null || transactionsDetails.size() < 1){
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("No data available.");
					return serviceResponse;
				}
				Map<String, Map<String, Double>> paymentTrends = null;
				if(isDeposit != null && isDeposit && (isAdjTrx == null || !isAdjTrx))
					paymentTrends = (Map<String, Map<String, Double>>) dashboardService.balancePaymentTrend(mealSchoolId, startDate, endDate, response, false,schoolYear, null);
				String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
				transactionsDetailsReportUtil.transactionsDetailsReport(transactionsDetails, response,du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
								mealSchool.getSchoolTimezone().toString()), 
						du.formatDateToString(sdfReq.parse(endDate), 
										"yyyy-MM-dd", mealSchool.getSchoolTimezone().toString()), 
						mealSchoolId, isDeposit, paymentTrends,currencySymbol, isAdjTrx);
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Generated the deposit/purchase transactions history report successfully.");
			serviceResponse.setStatusCode(200);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to generate the deposit/purchase transactions history report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to generate the deposit/purchase transactions history report.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate the online report**/
	@SuppressWarnings("unchecked")
	@Override
	public ServiceResponse onlinePaymetReport(Long districtId, String startDate, String endDate,
			HttpServletResponse response, Integer schoolYear) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			District district = districtRepository.findOne(districtId);
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(district.getCountryCode());
			
			if(district == null || district.getId() == null)
				throw new Exception("District doesn't exist with id: "+districtId);
			List<Object[]> transactionsReportData = reportsDao.onlinePaymetReport(districtId, startDate, endDate, schoolYear);
			List<TransactionsDetails> transactionsDetails = buildonlinePaymetReportObj(transactionsReportData, 
					district.getTimezone().toString(), (countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy"));
			//transactionsDetails.sort(Comparator.comparing(TransactionsDetails::getStudentLName));
			serviceResponse.setTransactionReportsDetails(transactionsDetails);
			if(transactionsDetails == null || transactionsDetails.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("No data available.");
				return serviceResponse;
			}
			Map<String, Map<String, Double>> paymentTrends = null;
			paymentTrends = (Map<String, Map<String, Double>>) dashboardService.balancePaymentTrend(null, startDate, endDate, response, false,schoolYear, districtId);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(district.getCountryCode());
			onlineReportUtil.transactionsDetailsReport(transactionsDetails, response,du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
						district.getTimezone().toString()), du.formatDateToString(sdfReq.parse(endDate), 
						"yyyy-MM-dd", district.getTimezone().toString()), 
					districtId, paymentTrends,currencySymbol);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Generated the online transactions report successfully.");
			serviceResponse.setStatusCode(200);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to generate the online transactions report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to generate the online transactions report.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate the low balance student's report**/
	@Override
	public ServiceResponse lowBalanceReport(Long mealSchoolId, HttpServletResponse response, Boolean fileExport, 
			Integer schoolYear, Double amount, String operator) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			if(mealSchool == null || mealSchool.getSchoolId() == null){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("School doesn't exist.");
				logger.error(serviceResponse.getStatusMessage());
				return serviceResponse;
			}
			Double minLowBalance = -500.00;
			Double maxLowBalance = 0.0;
			Boolean isExcludeZeroBalance = false;
			if(amount == null || operator == null || operator.trim().isEmpty()){
				List<LowBalanceSchoolSetting> lowBalanceSchoolSettings = new ArrayList<>(lowBalanceSchoolSettingRepository
						.findByMealSchoolSchoolId(mealSchool.getSchoolId()));
				if(lowBalanceSchoolSettings != null && lowBalanceSchoolSettings.size() > 0){
					if(lowBalanceSchoolSettings.get(0).getLowBalMinCriteria() != null)
						minLowBalance = lowBalanceSchoolSettings.get(0).getLowBalMinCriteria();
					if(lowBalanceSchoolSettings.get(0).getLowBalMaxCriteria() != null)
						maxLowBalance = lowBalanceSchoolSettings.get(0).getLowBalMaxCriteria();
					if(lowBalanceSchoolSettings.get(0).getIsExcludeZeroBal() != null)
						isExcludeZeroBalance = lowBalanceSchoolSettings.get(0).getIsExcludeZeroBal();
				}
			}
			
			List<Object[]> lowBalanceStudentsObj = reportsDao.lowBalanceReport(mealSchoolId, schoolYear, minLowBalance, 
					maxLowBalance, isExcludeZeroBalance, amount, operator);
			List<LowBalanceStudents> lowBalanceStudentsList = buildLowBalStudents(lowBalanceStudentsObj);
			lowBalanceStudentsList.sort(Comparator.comparing(LowBalanceStudents::getLastName));
			serviceResponse.setLowBalanceStudentsList(lowBalanceStudentsList);
			if(fileExport != null && fileExport){
				if(lowBalanceStudentsList == null || lowBalanceStudentsList.size() < 1){
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("No data available.");
					return serviceResponse;
				}
				String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
				lowBalanceStudentReportUtil.pdfReportLowBalance(lowBalanceStudentsList, mealSchool.getLogoLink(), 
						mealSchool.getSchoolName(), response, mealSchoolId, minLowBalance, maxLowBalance,amount, operator,currencySymbol, mealSchool.getCountryCode());
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Low balance student's report generated successfully.");
			Map<String, String> keyVal = new HashMap<String, String>();
			keyVal.put("minLowBalance", minLowBalance.toString());
			keyVal.put("maxLowBalance", maxLowBalance.toString());
			serviceResponse.setMapKeyVal(keyVal);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to generate the low balance students report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to generate the low balance student's report.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate the not ordered students report who having lunch orders in previous month**/
	@Override
	public ServiceResponse notOrderedLunchReport(Long mealSchoolId, String yearMonth, Integer schoolYear, 
			HttpServletResponse response, String fileType, Boolean isVersion2, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		List<NotOrderedStudentResp> notOrderedStudentRespList = new ArrayList<NotOrderedStudentResp>();
		try{
			List<Object[]> objArray = new ArrayList<Object[]>();
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(yearMonth.substring(0, 4)), Integer.parseInt(yearMonth.substring(4))-1, 01);
			cal.add(Calendar.MONTH, -1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String previousYearMonth = sdf.format(cal.getTime());
			List<String> gradeNames = null;
			String countryCode = mealSchoolRepository.getSchoolCountry(mealSchoolId);
			if(isVersion2 != null && isVersion2)
				gradeNames = reportsDao.getMealPublishedGradesV2(mealSchoolId, yearMonth, null, false, menuType);
			else
				gradeNames = reportsDao.getMealPublishedGrades(mealSchoolId, yearMonth, null, false);
			if(gradeNames != null && gradeNames.size() > 0)
				objArray = reportsDao.notOrderedLunchReport(mealSchoolId, yearMonth, gradeNames, schoolYear, 
						previousYearMonth, menuType);
			NotOrderedStudentResp notOrderedStudentResp = null;
			for(Object[] obj : objArray){
				notOrderedStudentResp = new NotOrderedStudentResp();
				notOrderedStudentResp.setStudentRecId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
				notOrderedStudentResp.setStudentId(obj[1] != null ? obj[1].toString() : null);
				notOrderedStudentResp.setStudentFName(obj[2] != null ? obj[2].toString() : null);
				notOrderedStudentResp.setStudentLName(obj[3] != null ? obj[3].toString() : null);
				notOrderedStudentResp.setGradeName(obj[4] != null ? obj[4].toString() : null);
				notOrderedStudentResp.setTeacherName(obj[5] != null ? obj[5].toString() : "");
				notOrderedStudentRespList.add(notOrderedStudentResp);
			}
			/**Used for sort the students by last name**/
			notOrderedStudentRespList.sort(Comparator.comparing(NotOrderedStudentResp::getStudentLName));
			if(notOrderedStudentRespList == null || notOrderedStudentRespList.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Not available data.");
				return serviceResponse;
			}
			if(fileType.equalsIgnoreCase("Pdf")){
				//MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
				generateAllergiesReportPdf.exportNotOrderedStudentsPdfReport(notOrderedStudentRespList,response, mealSchoolId, yearMonth, countryCode); 
			}else{
				excelGenerateForReport.exportNotOrderedStudentsExcelReport(notOrderedStudentRespList, response, countryCode);
			}
			logger.info("Not ordered students report has been exported successfully");
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Not ordered students report has been generated successfully");
		}catch(Exception e){
			logger.error("Failed to generate the lunch not ordered students report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the lunch not ordered students report.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for low balance student's details report**/
	@Override
	public ServiceResponse lowBalanceStudentDetailsReport(Long studentRecId, String startDate, String endDate, 
			HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			StudentUser studentUser = studentUserRepository.findOne(studentRecId);
			if(studentUser == null || studentUser.getUserId() == null)
				throw new Exception("Student does not exist with the record Id: "+studentRecId);
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(studentUser.getMealSchool().getSchoolId());
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			List<Object[]> transactionHstryData = reportsDao.transactionsHistory(studentRecId, startDate, endDate);
			StudentAccountDetails studentAccountDetails = buildAccountTransactionDetails(transactionHstryData, 
					mealSchool.getSchoolTimezone().toString(), startDate, endDate, studentUser, (countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy"));
			serviceResponse.setStudentAccountDetails(studentAccountDetails);
			lowBalanceStudentDetailsReportUtil.transactionHistoryReport(studentAccountDetails, mealSchool.getSchoolName(), 
					response, mealSchool.getSchool(), mealSchool.getContactPPhone(), mealSchool.getSubdomain(), 
					studentUser.getAccBalance(),mealSchool.getSchoolTimezone(), (countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy"));
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Generated the low balance student details report successfully.");
			serviceResponse.setStatusCode(200);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to generate the low balance student's details report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the low balance student's details report due to "+e.getMessage()+".");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate the daily & monthly report**/
	@Override
	public ServiceResponse generateAuditReport(Long mealSchoolId, String startDate, String endDate, String itemType, 
			Integer schoolYear, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			Boolean isReimbursementDeclared = true;
			SchoolYear schoolYearDetails = schoolYearRepository.findByMealSchoolSchoolIdAndSchoolYear(mealSchoolId, schoolYear);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(mealSchoolId));
			if(schoolYearDetails == null || schoolYearDetails.getAttendanceFactor() == null || schoolYearDetails.getAttendanceFactor().toString().equalsIgnoreCase("0.00")){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatusMessage("Please declare the Attendance Factor first from school year setup screen, before generate this report!!");
				return serviceResponse;
			}
			/*List<ReimbursementRatesInfo> reimbursementRatesInfos = schoolYearDetails.getReimbursementRatesInfos().stream()
					.filter(p ->  p.getReimbursementType() != null && p.getReimbursementType().toString()
					.equalsIgnoreCase(itemType)).collect(Collectors.toCollection(LinkedList::new));*/
			List<ReimbursementRatesInfo> reimbursementRatesInfos = new ArrayList<ReimbursementRatesInfo>( 
			schoolYearRepository.getReimburseRates(schoolYearDetails.getSchoolId(), ReimbursementMealsType.valueOf(itemType)));
			if(reimbursementRatesInfos == null || reimbursementRatesInfos.size() < 1)
				isReimbursementDeclared = false;
			
			if(isReimbursementDeclared){
				for(ReimbursementRatesInfo rate : reimbursementRatesInfos){
					boolean statusVal = checkReimbursementRates(rate);
					if(!statusVal){
						isReimbursementDeclared = false;
						break;
					}
				}
			}
			if(!isReimbursementDeclared){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatusMessage("Please declare the Reimbursement Rates first from school year setup screen, before generate this report!!");
				return serviceResponse;
			}
			String schoolName = schoolYearDetails.getMealSchool().getSchoolName();
			
			List<Object[]> mealServedCountByElig = reportsDao.mealsServedCountByElig(mealSchoolId, startDate, endDate, itemType, false);
			if(mealServedCountByElig == null || mealServedCountByElig.size() < 1){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("No data available!!");
				return serviceResponse;
			}
			List<Object[]> needyMealServedCountByElig = null;
			//if(schoolYearDetails.isEmergeLunch())
			//	needyMealServedCountByElig = reportsDao.mealsServedCountByElig(mealSchoolId, startDate, endDate, itemType, true);
			Map<String, Integer> needyStdCountMap = needyStdCountMapBuild(needyMealServedCountByElig);
			DecimalFormat df1 = new DecimalFormat("##0");
			Map<String, Map<String, String>> auditMap = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> auditDetails = servedMealsMap(mealServedCountByElig, endDate);
			auditDetails.put("servingDays", String.valueOf(reportsDao.mealsServingDays(mealSchoolId, startDate, endDate, itemType)));
			Integer totalStudentCount = studentUserRepository.totalStudentsCount(mealSchoolId, schoolYear);
			Integer freeStudentCount = studentUserRepository.freeStudentsCount(mealSchoolId, schoolYear);
			Integer reducedStudentCount = studentUserRepository.reducedStudentsCount(mealSchoolId, schoolYear);
			auditDetails.put("attendance", String.valueOf(df1.format(totalStudentCount*schoolYearDetails.getAttendanceFactor().doubleValue()/100)));
			auditDetails.put("extendedFree", String.valueOf(0));
			auditMap.put("Meals Served", auditDetails);
			Map<String, String> auditDetails1 = new LinkedHashMap<String, String>();
			auditDetails1.put("paidStudents", String.valueOf(totalStudentCount-(freeStudentCount+reducedStudentCount)));
			auditDetails1.put("reducedStudents", String.valueOf(reducedStudentCount));
			auditDetails1.put("freeStudents", String.valueOf(freeStudentCount));
			auditDetails1.put("totalStudents", String.valueOf(totalStudentCount));
			auditDetails1.put("paidAdults", "");
			auditDetails1.put("servingDays", "");
			auditDetails1.put("attendance", "");
			auditDetails1.put("extendedFree", "");
			auditMap.put("Approved Benefits", auditDetails1);
			serviceResponse = auditReportUtil.audiReportGeneration(response, du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
					schoolYearDetails.getMealSchool().getSchoolTimezone().toString()), du.formatDateToString(sdfReq.parse(endDate), "yyyy-MM-dd", 
							schoolYearDetails.getMealSchool().getSchoolTimezone().toString()), mealSchoolId, 
					schoolName, reimbursementRatesInfos, itemType, auditMap, needyStdCountMap,currencySymbol);
			
		}catch(Exception e){
			logger.error("Failed to generate audit report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate audit report.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	

	
	/**This method used for generate the basic claim report**/
	@Override
	public ServiceResponse basicClaimReport(Long districtId, String startDate, String endDate, 
			Integer schoolYear, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			District district = districtRepository.findOne(districtId);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(district.getCountryCode());
			List<Long> schoolIds = mealSchoolRepository.getSchoolIdsByDistrictId(districtId);
			List<ReimbursementRatesInfo> reimLunch = null;
			List<ReimbursementRatesInfo> reimBreakfast = null;
			List<ReimbursementRatesInfo> reimMilk = null;
			for(Long mealSchoolId : schoolIds){
				BigInteger syId = schoolYearRepository.getSchoolYearId(mealSchoolId, schoolYear);
				if(reimLunch == null)
					reimLunch = manipulateReimb(syId.longValue(), "Lunch");
				if(reimBreakfast == null)
					reimBreakfast = manipulateReimb(syId.longValue(), "Breakfast");
				if(reimMilk == null)
					reimMilk = manipulateReimb(syId.longValue(), "Milk");						
			}
			Map<String, List<ReimbursementRatesInfo>> reimbByType = new HashMap<>();
			reimbByType.put("Lunch", reimLunch);
			reimbByType.put("Breakfast", reimBreakfast);
			reimbByType.put("Milk", reimMilk);
			
			Integer totalStudentCount = studentUserRepository.distTotalStudentsCount(districtId, schoolYear);
			Integer freeStudentCount = studentUserRepository.distFreeStudentsCount(districtId, schoolYear);
			Integer reducedStudentCount = studentUserRepository.distReducedStudentsCount(districtId, schoolYear);
			Map<String, Map<String, Map<String, String>>> auditByType = new HashMap<>();
			auditByType.put("Lunch", buildReimbObj(districtId, startDate, endDate, "Lunch", false, schoolYear, totalStudentCount, 
					reducedStudentCount, freeStudentCount));
			auditByType.put("Breakfast", buildReimbObj(districtId, startDate, endDate, "Breakfast", false, schoolYear, totalStudentCount, 
					reducedStudentCount, freeStudentCount));
			auditByType.put("Milk", buildReimbObj(districtId, startDate, endDate, "Milk", false, schoolYear, totalStudentCount, 
					reducedStudentCount, freeStudentCount));
			Map<String, Integer> needyStdCountMap = null;
			needyStdCountMap = needyStdCountMapBuild(reportsDao.distMealsServedCountByElig(districtId, startDate, endDate, "Lunch", true));
			
			serviceResponse = distAuditReportUtil.audiReportGeneration(response, du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
					district.getTimezone().toString()), du.formatDateToString(sdfReq.parse(endDate), "yyyy-MM-dd", 
							district.getTimezone().toString()), districtId, 
					district.getName(), reimbByType, auditByType, needyStdCountMap,currencySymbol, schoolIds.size());			
		}catch(Exception e){
			logger.error("Failed to generate audit report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate audit report.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate the audit daily edit check report**/
	@Override
	public ServiceResponse auditDailyEditCheck(Long mealSchoolId, String yearMonth, String itemType,
			Integer schoolYear, String startDate, String endDate, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			SchoolYear schoolYearDetails = schoolYearRepository.findByMealSchoolSchoolIdAndSchoolYear(mealSchoolId, schoolYear);
			if(schoolYearDetails == null || schoolYearDetails.getAttendanceFactor() == null || schoolYearDetails.getAttendanceFactor().toString().equalsIgnoreCase("0.00")){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatusMessage("Please declare the Attendance Factor first from school year setup screen, before generate this report!!");
				return serviceResponse;
			}
			String schoolName = schoolYearDetails.getMealSchool().getSchoolName();
			String schoolTimeZone = schoolYearDetails.getMealSchool().getSchoolTimezone().toString();
			String timezoneV = "+00:00";
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(schoolYearDetails.getMealSchool().getCountryCode());
			for(TimezoneDetails timezoneDetails : countryDetail.getTimezoneDetails()){
				if(schoolTimeZone.equalsIgnoreCase(timezoneDetails.getTimezoneName())){
					String tz[] = timezoneDetails.getTimezoneDesc().split(":");
					if(tz.length > 2)
						timezoneV = (tz[1]+":"+tz[2]).replace(" UTC", "");
					else if(tz.length > 1)
						timezoneV = (tz[1]+":00").replace(" UTC", "");
				}
			}
			/*Integer totalStudentCount = studentUserRepository.totalStudentsCount(mealSchoolId, schoolYear);
			Integer freeStudentCount = studentUserRepository.freeStudentsCount(mealSchoolId, schoolYear);
			Integer reducedStudentCount = studentUserRepository.reducedStudentsCount(mealSchoolId, schoolYear);*/
			/*List<Object[]> respObj = reportsDao.dailyAuditCheck(mealSchoolId, itemType, startDate, endDate, timezoneV);
			Map<Integer, Map<Integer, Integer>> stdCountByEligAndDt = builStudentDailyAuditChk(respObj);
			List<String> schoolHolidays = reportsDao.getSchoolHolidays(mealSchoolId, yearMonth, itemType); 
			List<Integer> schoolHolidayList = schoolHolidays.stream().map(Integer::parseInt).collect(Collectors.toList());
			serviceResponse = dailyAuditCheck.audiDailyChkReportGeneration(response, mealSchoolId, schoolName, schoolYearDetails.getAttendanceFactor().doubleValue(),
					itemType, stdCountByEligAndDt, yearMonth, schoolHolidayList, schoolTimeZone, schoolYear);*/
			List<EditCheckResp> resps = new ArrayList<>();
			EditCheckResp resp = new EditCheckResp();
			resp.setSchoolName(schoolName);
			List<Object[]> respObj = reportsDao.dailyAuditCheck(mealSchoolId, itemType, startDate, endDate, timezoneV);
			resp.setStdCountByEligAndDt(builStudentDailyAuditChk(respObj));
			List<String> schoolHolidays = reportsDao.getSchoolHolidays(mealSchoolId, yearMonth, itemType); 
			resp.setSchoolHolidays(schoolHolidays.stream().map(Integer::parseInt).collect(Collectors.toList()));;
			resp.setSchoolTimezone(schoolTimeZone);
			resp.setMealSchoolId(mealSchoolId);
			resp.setAttendanceFactor(schoolYearDetails.getAttendanceFactor().doubleValue());
			resps.add(resp);		
			serviceResponse = districtEditCheckUtil.audiDailyChkReportGeneration(response, itemType, yearMonth,
					schoolYear, resps, mealSchoolId);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to generate the daily audit check report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the daily audit check report.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used to generate the audit daily edit check report for district**/
	@Override
	public ServiceResponse auditDailyEditCheckDist(Long districtId, String yearMonth, String itemType,
			Integer schoolYear, String startDate, String endDate, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Long> schoolIds = mealSchoolRepository.getSchoolIdsByDistrictId(districtId);
			List<EditCheckResp> resps = new ArrayList<>();
			String timezoneV = "+00:00";
			District district = districtRepository.findOne(districtId);
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(district.getCountryCode());
			for(TimezoneDetails timezoneDetails : countryDetail.getTimezoneDetails()){
				if(district.getTimezone().equalsIgnoreCase(timezoneDetails.getTimezoneName())){
					String tz[] = timezoneDetails.getTimezoneDesc().split(":");
					if(tz.length > 2)
						timezoneV = (tz[1]+":"+tz[2]).replace(" UTC", "");
					else if(tz.length > 1)
						timezoneV = (tz[1]+":00").replace(" UTC", "");
				}
			}
			EditCheckResp resp = null;
			for(Long mealSchoolId : schoolIds){
				SchoolYear schoolYearDetails = schoolYearRepository.findByMealSchoolSchoolIdAndSchoolYear(mealSchoolId, schoolYear);
				if(schoolYearDetails != null &&  schoolYearDetails.getAttendanceFactor() != null && !schoolYearDetails.getAttendanceFactor().toString().equalsIgnoreCase("0.00")){
					resp = new EditCheckResp();
					resp.setSchoolName(schoolYearDetails.getMealSchool().getSchoolName());
					List<Object[]> respObj = reportsDao.dailyAuditCheck(mealSchoolId, itemType, startDate, endDate, timezoneV);
					resp.setStdCountByEligAndDt(builStudentDailyAuditChk(respObj));
					List<String> schoolHolidays = reportsDao.getSchoolHolidays(mealSchoolId, yearMonth, itemType); 
					resp.setSchoolHolidays(schoolHolidays.stream().map(Integer::parseInt).collect(Collectors.toList()));;
					resp.setSchoolTimezone(schoolYearDetails.getMealSchool().getSchoolTimezone().toString());
					resp.setMealSchoolId(mealSchoolId);
					resp.setAttendanceFactor(schoolYearDetails.getAttendanceFactor().doubleValue());
					resps.add(resp);
				}
			}			
			serviceResponse = districtEditCheckUtil.audiDailyChkReportGeneration(response, itemType, yearMonth,
					schoolYear, resps, districtId);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to generate the daily audit check report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the daily audit check report.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate eligibility summary report**/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ServiceResponse eligibilitySummary(Long districtId, String currentDate, Boolean isExport, HttpServletResponse response, Boolean isSchoolId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String districtName = "";
			String timezone = "";
			String loggedUser = "";
			List<Object[]> objArray = null;
			if(isSchoolId == null || !isSchoolId){
				District district = districtRepository.findOne(districtId);
				districtName = district.getName();
				timezone = district.getTimezone();
				if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null){
					loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
					DistrictUser districtUser = districtRepository.districtUser(loggedUser);
					if(districtUser != null)
						loggedUser = districtUser.getFirstName()+" "+districtUser.getLastName();
				}
				objArray = districtRepository.getEligSummary(districtId, currentDate);
			}else
				objArray = districtRepository.getEligSummaryBySchool(districtId, currentDate);
			
			List<EligSummaryResp1> eligSummaryResp1s = objArray.stream().map(EligSummaryResp1::new).collect(Collectors.toList());
			Map<Long, EligSummaryResp> eligRespMap = builMapData(eligSummaryResp1s);
			List<EligSummaryResp> eligSummaryResps = new ArrayList(eligRespMap.values());
			if(isExport != null && isExport)
				serviceResponse = eligibilitySummaryUtil.eligSummaryReport(response, districtId, districtName, timezone, loggedUser, eligSummaryResps);
			else{
				serviceResponse.setResponse(eligSummaryResps);
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Eligibility Summary report generated successfully.");
			}
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate eligibility summary report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}		
		return serviceResponse;
	}

	/**This method used for generate the events report**/
	@Override
	public ServiceResponse eventsReport(Long mealSchoolId, Integer schoolYear, String startDate, String endDate, Boolean isExport, 
			HttpServletResponse httpResp) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			if(mealSchool == null || mealSchool.getSchoolId() == null)
				throw new Exception("School doesn't exist with school id: "+mealSchoolId);
			List<EventsResp> eventsResps = eventInfoRepo.getEventsReport(mealSchoolId, startDate.replace("T", " "), endDate.replace("T", " "), schoolYear);
			eventsResps.stream().forEach(event-> event.setTrxDateTime(du.formatDateToString(event.getTransactionDateTime(), 
					"MM/dd/yyyy hh:mm:ss a", mealSchool.getSchoolTimezone())));
			List<Map<String, Object>> eventsReportResp = new ArrayList<Map<String, Object>>(); 
			if(eventsResps != null){
				Map<Long, List<EventsResp>> studentsByEvent = eventsResps.stream().collect(Collectors.groupingBy(EventsResp::getRecId));
				if(isExport != null && isExport){
					
					eventsReportUtil.exportEventsPdf(studentsByEvent, mealSchoolId, du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
							mealSchool.getSchoolTimezone().toString()), du.formatDateToString(sdfReq.parse(endDate), 
									"yyyy-MM-dd", mealSchool.getSchoolTimezone().toString()), httpResp);
				}else{
					for(Map.Entry<Long, List<EventsResp>> entry : studentsByEvent.entrySet()){
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put("eventName", entry.getValue().get(0).getEventName());
						dataMap.put("eventId", entry.getKey());
						dataMap.put("eventType", entry.getValue().get(0).getEventType());
						dataMap.put("eventAmt", entry.getValue().get(0).getAmount());
						dataMap.put("longDesc", entry.getValue().get(0).getLongDesc());
						dataMap.put("students", entry.getValue());
						eventsReportResp.add(dataMap);
					}
				}				
			}			
			serviceResponse.setResponse(eventsReportResp);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Events report generated successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the events report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" with mealSchoolId::"+mealSchoolId+" and schoolYear::"+schoolYear
					+" and startDate::"+startDate+" and endDate::"+endDate);
		}
		return serviceResponse;
	}
	
	/**This method used for build the account transaction history data
	 * @throws ParseException **/
	private StudentAccountDetails buildAccountTransactionDetails(List<Object[]> transactionHstryData, 
			String schoolTimeZone, String startDate, String endDate, StudentUser studentUser, String dateFormat) throws ParseException{
		StudentAccountDetails studentAccountDetails = new StudentAccountDetails();
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		studentAccountDetails.setStartDate(sdf.format(sdf1.parse(du.formatDateToString(sdfReq.parse(startDate), 
				"yyyy-MM-dd", schoolTimeZone))));
		studentAccountDetails.setEndDate(sdf.format(sdf1.parse(du.formatDateToString(sdfReq.parse(endDate), 
				"yyyy-MM-dd", schoolTimeZone))));
		studentAccountDetails.setStudentFName(studentUser.getFirstName());
		studentAccountDetails.setStudentLName(studentUser.getLastName());
		studentAccountDetails.setStudentId(studentUser.getStudentId());
		List<AccountTransactionHistory> accountTransactionHistories = new ArrayList<AccountTransactionHistory>();
		AccountTransactionHistory accountTransactionHistory = null;
		for (Object[] obj : transactionHstryData) {
			if(obj[0] != null){
				accountTransactionHistory = new AccountTransactionHistory();
				accountTransactionHistory.setTransactionDateTime(du
						.formatDateToString(sdfOrg.parse(obj[0].toString()), (dateFormat+" hh:mm:ss a"), schoolTimeZone));
				accountTransactionHistory.setTransactionType(obj[3].toString());
				accountTransactionHistory.setPaymentType(obj[1] != null ? obj[1].toString() : "");
				accountTransactionHistory.setPurchaseItemType(obj[2] != null ? (obj[8] != null ? (obj[2].toString()+" - "+obj[8].toString()) : obj[2].toString()) : "");
				accountTransactionHistory.setTransactionAmount(obj[4] != null ? Double.parseDouble(obj[4].toString()) : 0.0);
				accountTransactionHistory.setFinalBalance(obj[5] != null ? Double.parseDouble(obj[5].toString()) : 0.0);
				if(accountTransactionHistory.getTransactionType().equalsIgnoreCase("Refund") || 
						accountTransactionHistory.getTransactionType().contains("Adjustment"))
					accountTransactionHistory.setNote(obj[6] != null ? obj[6].toString() : "");
				else if(accountTransactionHistory.getTransactionType().contains("Event"))
					accountTransactionHistory.setNote(obj[7] != null ? ("Paid for "+obj[7].toString()) : "");
				else
					accountTransactionHistory.setNote("");
				accountTransactionHistories.add(accountTransactionHistory);
			}
		}
		studentAccountDetails.setAccountTransactionHistories(accountTransactionHistories);
		studentAccountDetails.setNumberStreetApt(studentUser.getNumberStreetApt());
		studentAccountDetails.setCityStateZip(studentUser.getCityStateZip());
		return studentAccountDetails;
	}
	
	/**This method used for map the deposit/purchase transactions history data
	 * @throws ParseException **/
	private List<TransactionsDetails> buildTransactionsReportObj(List<Object[]> transactionHstryData, 
			String schoolTimeZone, Boolean isDeposit, String dateFormat, Boolean isAdjTrx) throws ParseException{
		List<TransactionsDetails> transactionsDetails = new ArrayList<TransactionsDetails>();
		TransactionsDetails transactionsDetailResp = null;
		String transactionDateTime = null;
		for(Object[] obj : transactionHstryData){
			transactionsDetailResp = new TransactionsDetails();
			if(obj[0] != null && obj[0] != null){
				transactionsDetailResp.setIdNumb(Long.parseLong(obj[0].toString()));
				transactionsDetailResp.setStudentLName(obj[1] != null ? obj[1].toString() : "");
				transactionsDetailResp.setStudentFName(obj[2] != null ? obj[2].toString() : "");
				transactionDateTime =  du.formatDateToString(sdfOrg.parse(obj[3].toString()), 
						(dateFormat+" hh:mm:ss a"), schoolTimeZone);
				transactionsDetailResp.setTransactionDate(transactionDateTime.split(" ")[0]);
				transactionsDetailResp.setTransactionTime(transactionDateTime.split(" ")[1]+" "
						+transactionDateTime.split(" ")[2]);
				/*if(obj[7] == null)
					transactionsDetailResp.setAmount(-Double.parseDouble(obj[4] != null ? obj[4].toString() : "0.0"));
				else*/
					transactionsDetailResp.setAmount(Double.parseDouble(obj[4] != null ? obj[4].toString() : "0.0"));
				transactionsDetailResp.setNote(obj[5] != null ? obj[5].toString() : "");
if(isAdjTrx != null && isAdjTrx){
					if(obj[7] != null)
						transactionsDetailResp.setType("Credit");
					else
						transactionsDetailResp.setType("Debit");
				}else{
				if(isDeposit != null && isDeposit){
					if(obj[7] == null){
						transactionsDetailResp.setPaymentType(obj[8] != null ? obj[8].toString() : "");
					}else{
						transactionsDetailResp.setPaymentType(obj[7].toString());
						if(obj[7].toString().equalsIgnoreCase(PaymentType.Online.toString()))
							transactionsDetailResp.setSource("On");
						else
							transactionsDetailResp.setSource("Off");
					}					
				}else{
					transactionsDetailResp.setItemPurchased(obj[8] != null ? obj[8].toString() : "");
}
				}
				if((transactionsDetailResp.getPaymentType() != null && (transactionsDetailResp.getPaymentType().contains("Adjustment") 
						|| transactionsDetailResp.getPaymentType().contains("Refund"))) || transactionsDetailResp.getItemPurchased() != null)
					transactionsDetailResp.setTransactionDesc(obj[6] != null ? obj[6].toString() : "");
				else
					transactionsDetailResp.setTransactionDesc("");
				if(isAdjTrx != null && isAdjTrx)
					transactionsDetailResp.setTransactionDesc(obj[6] != null ? obj[6].toString() : "");
				transactionsDetailResp.setUser(obj[9] != null ? obj[9].toString() : "");
				transactionsDetailResp.setGrade(obj[10] != null ? obj[10].toString() : "");
				transactionsDetailResp.setCheckNum(obj[11] != null ? obj[11].toString() : "");
				transactionsDetailResp.setTransferId(obj[12] != null ? obj[12].toString() : "");
				/*transactionsDetailResp.setCategory(obj[13] != null  ? 
						(obj[13].toString().equalsIgnoreCase("Regular") ? "Reimbursement":"Non-Reimbursement") : "Reimbursement ");
				if(transactionsDetailResp.getGrade().equalsIgnoreCase("staff"))
					transactionsDetailResp.setCategory("Non-Reimbursement");*/
				transactionsDetailResp.setCategory(obj[13] != null  ? obj[13].toString() : "");
				transactionsDetailResp.setLocation(obj[14] != null  ? obj[14].toString() : "");
				if(isDeposit != null && isDeposit && (isAdjTrx == null || !isAdjTrx))
					transactionsDetailResp.setType(transactionsDetailResp.getPaymentType().equalsIgnoreCase("Online") ? 
							transactionsDetailResp.getPaymentType() : (Boolean.valueOf(obj[15].toString())?(transactionsDetailResp.getPaymentType()+" - By POS"):transactionsDetailResp.getPaymentType()+" - By School"));
				
				transactionsDetails.add(transactionsDetailResp);
			}
		}
		return transactionsDetails;
	}
	
	/**This method used for map the online report transactions history data
	 * @throws ParseException **/
	private List<TransactionsDetails> buildonlinePaymetReportObj(List<Object[]> transactionHstryData, 
			String schoolTimeZone, String dateFormat) throws ParseException{
		List<TransactionsDetails> transactionsDetails = new ArrayList<TransactionsDetails>();
		TransactionsDetails transactionsDetailResp = null;
		String transactionDateTime = null;
		for(Object[] obj : transactionHstryData){
			transactionsDetailResp = new TransactionsDetails();
			if(obj[0] != null && obj[0] != null){
				transactionsDetailResp.setIdNumb(Long.parseLong(obj[0].toString()));
				transactionsDetailResp.setStudentLName(obj[1] != null ? obj[1].toString() : "");
				transactionsDetailResp.setStudentFName(obj[2] != null ? obj[2].toString() : "");
				transactionDateTime =  du.formatDateToString(sdfOrg.parse(obj[3].toString()), 
						(dateFormat+" hh:mm:ss a"), schoolTimeZone);
				transactionsDetailResp.setTransactionDate(transactionDateTime.split(" ")[0]);
				transactionsDetailResp.setTransactionTime(transactionDateTime.split(" ")[1]+" "
						+transactionDateTime.split(" ")[2]);
				transactionsDetailResp.setAmount(Double.parseDouble(obj[4] != null ? obj[4].toString() : "0.0"));
				transactionsDetailResp.setNote(obj[5] != null ? obj[5].toString() : "");
				if(obj[7] == null){
					transactionsDetailResp.setPaymentType(obj[8] != null ? obj[8].toString() : "");
				}else{
					transactionsDetailResp.setPaymentType(obj[7].toString());
					if(obj[7].toString().equalsIgnoreCase(PaymentType.Online.toString()))
						transactionsDetailResp.setSource("On");
					else
						transactionsDetailResp.setSource("Off");
				}
				if((transactionsDetailResp.getPaymentType() != null && (transactionsDetailResp.getPaymentType().contains("Adjustment") 
						|| transactionsDetailResp.getPaymentType().contains("Refund"))) || transactionsDetailResp.getItemPurchased() != null)
					transactionsDetailResp.setTransactionDesc(obj[6] != null ? obj[6].toString() : "");
				else
					transactionsDetailResp.setTransactionDesc("");
				transactionsDetailResp.setSchoolName(obj[9] != null ? obj[9].toString() : "");
				transactionsDetailResp.setGrade(obj[10] != null ? obj[10].toString() : "");
				transactionsDetailResp.setCheckNum(obj[11] != null ? obj[11].toString() : "");
				transactionsDetailResp.setTransferId(obj[12] != null ? obj[12].toString() : "");
				transactionsDetails.add(transactionsDetailResp);
			}
		}
		return transactionsDetails;
	}
	
	/**This method used for map the students data under the low balance report**/
	private List<LowBalanceStudents> buildLowBalStudents(List<Object[]> lowBalStudentsObj){
		List<LowBalanceStudents> lowBalanceStudents = new ArrayList<LowBalanceStudents>();
		LowBalanceStudents lowBalanceStudent = null;
		for(Object[] obj : lowBalStudentsObj){
			lowBalanceStudent = new LowBalanceStudents();
			if(obj[2] != null && obj[0] != null){
				lowBalanceStudent.setGradeName(obj[0].toString());
				lowBalanceStudent.setStudentId(obj[1] != null ? obj[1].toString() : "");
				lowBalanceStudent.setUserId(Long.parseLong(obj[2].toString()));
				lowBalanceStudent.setFirstName(obj[3] != null ? obj[3].toString() : "");
				lowBalanceStudent.setLastName(obj[4] != null ? obj[4].toString() : "");
				lowBalanceStudent.setMobileNo(obj[5] != null ? obj[5].toString() : "");
				lowBalanceStudent.setUserName(obj[6] != null ? obj[6].toString() : "");
				lowBalanceStudent.setParentAltEmail(obj[7] != null ? obj[7].toString() : "");
				lowBalanceStudent.setTeacherName(obj[8] != null ? obj[8].toString() : "");
				lowBalanceStudent.setIsReducePriceEligible(obj[9] != null ? Boolean.parseBoolean(obj[9].toString()) : false);
				lowBalanceStudent.setIsFreeMealEligible(obj[10] != null ? Boolean.parseBoolean(obj[10].toString()) :  false);
				lowBalanceStudent.setAccBalance(obj[11] != null ? Double.parseDouble(obj[11].toString()) : 0.0);
			}
			lowBalanceStudents.add(lowBalanceStudent);
		}
		return lowBalanceStudents;
	}
	
	/**This method used for map the served meals info**/
	private Map<String, String> servedMealsMap(List<Object[]> mealServedCountByElig, String endDate){
		Map<String, String> auditDetails = new LinkedHashMap<String, String>();
		Integer totalStd = 0;
		Map<Integer, Integer> resMap = new HashMap<Integer, Integer>();
		for(Object[] obj : mealServedCountByElig){
			if(obj[1] != null){
				//auditDetails.put(Integer.parseInt(obj[1].toString()) == 0 ? "freeStudents" : (Integer.parseInt(obj[1].toString()) == 1 ? "reducedStudents" : "paidStudents"), obj[0].toString());
				resMap.put(Integer.parseInt(obj[1].toString()), Integer.parseInt(obj[0].toString()));
				totalStd = totalStd+Integer.parseInt(obj[0].toString());
			}
		}
		auditDetails.put("paidStudents", resMap.get(2) != null ? String.valueOf(resMap.get(2)) : "0");
		auditDetails.put("reducedStudents", resMap.get(1) != null ? String.valueOf(resMap.get(1)) : "0");
		auditDetails.put("freeStudents", resMap.get(0) != null ? String.valueOf(resMap.get(0)) : "0");
		auditDetails.put("totalStudents", String.valueOf(totalStd));
		auditDetails.put("paidAdults", String.valueOf(0));
		return auditDetails;
	}
	
	/**This method used for map the needy served meals info**/
	private Map<String, Integer> needyStdCountMapBuild(List<Object[]> mealServedCountByElig){
		Map<String, Integer> auditDetails = new LinkedHashMap<String, Integer>();
		Integer totalStd = 0;
		Map<Integer, Integer> resMap = new HashMap<Integer, Integer>();
		if(mealServedCountByElig != null){
			for(Object[] obj : mealServedCountByElig){
				if(obj[1] != null){
					//auditDetails.put(Integer.parseInt(obj[1].toString()) == 0 ? "freeStudents" : (Integer.parseInt(obj[1].toString()) == 1 ? "reducedStudents" : "paidStudents"), obj[0].toString());
					resMap.put(Integer.parseInt(obj[1].toString()), Integer.parseInt(obj[0].toString()));
					totalStd = totalStd+Integer.parseInt(obj[0].toString());
				}
			}
		}
		auditDetails.put("reducedStudents", resMap.get(1) != null ? resMap.get(1) : 0);
		auditDetails.put("freeStudents", resMap.get(0) != null ? resMap.get(0) : 0);
		auditDetails.put("paidStudents", totalStd-(auditDetails.get("reducedStudents")+auditDetails.get("freeStudents")));
		auditDetails.put("totalStudents", totalStd);
		return auditDetails;
	}
	
	/**This method used for build the student daily audit check report data**/
	private Map<Integer, Map<Integer, Integer>> builStudentDailyAuditChk(List<Object[]> respObj){
		Map<Integer, Map<Integer, Integer>> respMap = new HashMap<Integer, Map<Integer, Integer>>();
		Map<Integer, Integer> eligCountMap = null;
		for(Object[] obj : respObj){
			if(obj[2] != null && obj[1] != null){
				eligCountMap = respMap.get(Integer.parseInt(obj[2].toString()));
				if(eligCountMap == null)
					eligCountMap = new HashMap<Integer, Integer>();
				eligCountMap.put(Integer.parseInt(obj[1].toString()), Integer.parseInt(obj[0].toString()));
				respMap.put(Integer.parseInt(obj[2].toString()), eligCountMap);
				}
			}
		return respMap;
	}
	
	/**This method used for build the reimb income data**/
	private Map<Integer, Map<Integer, Map<String, Double>>> buildReimbIncome(List<Object[]> respObj){
		Map<Integer, Map<Integer, Map<String, Double>>> respMap = new HashMap<Integer, Map<Integer, Map<String, Double>>>();
		Map<String, Double> amtByType = null;
		Map<Integer, Map<String, Double>> amtByTypeElig = null;
		for(Object[] obj : respObj){
			if(obj[3] != null && obj[4] != null){
				amtByTypeElig = respMap.get(Integer.parseInt(obj[4].toString()));
				if(amtByTypeElig == null)
					amtByTypeElig = new HashMap<>();
				amtByType = new HashMap<>();
				amtByType.put("ppAmt", Double.valueOf(obj[0].toString()));
				amtByType.put("ccAmt", Double.valueOf(obj[1].toString()));
				amtByType.put("chargedAmt", Double.valueOf(obj[2].toString()));
				amtByTypeElig.put(Integer.parseInt(obj[3].toString()), amtByType);
				respMap.put(Integer.parseInt(obj[4].toString()), amtByTypeElig);
				}
			}
		return respMap;
	}
	
	/**This method used for build the income data by type**/
	private Map<Integer, Map<String, Map<String, Double>>> buildIncomeByType(Long mealSchoolId, String itemType, String startDate, String endDate, String timezone,
			String grade,Map<Integer, Map<String, Map<String, Double>>> respMap){
		List<Object[]> respObj = reportsDao.dailyIncomeByType(mealSchoolId, itemType, startDate, endDate, timezone, (grade.equalsIgnoreCase("")?false:true));
		Map<String, Map<String, Double>> amtByType = null;
		for(Object[] obj : respObj){
			if(obj[3] != null && obj[4] != null){
				amtByType = respMap.get(Integer.parseInt(obj[4].toString()));
				if(amtByType == null)
					amtByType = new HashMap<>();
				Map<String, Double> amtByPT = new HashMap<>();
				amtByPT.put("ppAmt", (Double.valueOf(obj[0].toString())));
				amtByPT.put("ccAmt", (Double.valueOf(obj[1].toString())));
				amtByPT.put("chargedAmt", (Double.valueOf(obj[2].toString())));
				amtByType.put(grade+""+(obj[3].toString()), amtByPT);
				respMap.put(Integer.parseInt(obj[4].toString()), amtByType);
			}
		}
		return respMap;
	}
	
	/**This method used to check the reimbursement rates**/
	private boolean checkReimbursementRates(ReimbursementRatesInfo rate){
		boolean statusVal = true;
		if(rate.getFreFedReimbRate().toString().equalsIgnoreCase("0.0000")
				&& rate.getFreStateReimbRate().toString().equalsIgnoreCase("0.0000")
				&& rate.getRedFedReimbRate().toString().equalsIgnoreCase("0.0000")
				&& rate.getRedStateReimbRate().toString().equalsIgnoreCase("0.0000")
				&& rate.getTotFedReimbRate().toString().equalsIgnoreCase("0.0000")
				&& rate.getTotStateReimbRate().toString().equalsIgnoreCase("0.0000"))
			statusVal = false;
		return statusVal;
		
	}

	/**This method used for get bcac subscriptions**/
	@Override
	public ServiceResponse bcacSubscriptions(Long mealSchoolId, String subscribeDate, Boolean isExport, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Object[]> objArray = packageRepo.getBCACSubscriptions(mealSchoolId, subscribeDate);
			List<BCACSubscriptions> bcacSubscriptions = null;
			bcacSubscriptions = objArray.stream().map(BCACSubscriptions::new).collect(Collectors.toList());
			//List<Long> stdRecIds = bcacSubscriptions.stream().map(BCACSubscriptions::getStdRecId) .collect(Collectors.toList());
			if(isExport == null || !isExport){
				List<Object[]> pickupObj = packageRepo.getAuthorizedPkp(mealSchoolId);
				List<PickupAuthorizedResp> pickupPersons = null;
				pickupPersons = pickupObj.stream().map(PickupAuthorizedResp::new).collect(Collectors.toList());
				Map<Long, List<PickupAuthorizedResp>> ppByStd = pickupPersons.stream().collect(Collectors.groupingBy(PickupAuthorizedResp::getStdRecId));
				bcacSubscriptions.stream().forEach(bac->bac.setPickupPersons(ppByStd.get(bac.getStdRecId())));
				serviceResponse.setResponse(bcacSubscriptions);
			}else{
				MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
				CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
				bcacPkgReport.pkgReport(bcacSubscriptions, response, subscribeDate, mealSchoolId, mealSchool.getCountryCode(), 
						mealSchool.getSchoolTimezone(), (countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy"));
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("BCAC subscriptions retrieved successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Failed to get BCAC subscriptions package info.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	@Override
	/**This method used for get the kids related active subscribed packages**/
	public ServiceResponse bcacKidsSubsPkg(Long mealSchoolId, String parentEmail, String currentDate) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Object[]> objArray = packageRepo.bcacSubsPackagesInfo(mealSchoolId, parentEmail, currentDate);
			List<PackagesSubscribed> bcacSubscriptions = null;
			bcacSubscriptions = objArray.stream().map(PackagesSubscribed::new).collect(Collectors.toList());
			Map<Long, List<PackagesSubscribed>> packageSubsByStd = bcacSubscriptions.stream().collect(Collectors.groupingBy(w -> w.getStudentRecId()));
			List<Object[]> ppObj = packageRepo.getAuthorizedPkpByParent(mealSchoolId, parentEmail);
			List<PickupAuthorizedResp> pickupPersons = null;
			pickupPersons = ppObj.stream().map(PickupAuthorizedResp::new).collect(Collectors.toList());
			Map<Long, List<PickupAuthorizedResp>> ppByStd = pickupPersons.stream().collect(Collectors.groupingBy(PickupAuthorizedResp::getStdRecId));
			serviceResponse.setResponse(packageSubsByStd);
			serviceResponse.setResp1(ppByStd);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("BCAC active subscriptions by parent retrieved successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Failed to get BCAC subscriptions active package info by parent::"+parentEmail+" and mealSchoolId::"+mealSchoolId+" and currentDt::"+currentDate);
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for get the packages related deposit transactions**/
	@Override
	public ServiceResponse packagesTrx(Long mealSchoolId, Integer schoolYear, String startDate, String endDate, 
			Boolean isExport, HttpServletResponse response, Long stdRecId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			
			//
			if(mealSchool == null || mealSchool.getSchoolId() == null)
				throw new Exception("School doesn't exist with school id: "+mealSchoolId);
			List<PackagePaymentsTrx> trxList = null;
			List<Object[]> objList = reportsDao.packagePaymentsTrx(mealSchoolId, schoolYear, 
					startDate.replace("T", " "), endDate.replace("T", " "), stdRecId);
			trxList = objList.stream().map(PackagePaymentsTrx::new).collect(Collectors.toList());
			trxList.stream().forEach(trx-> trx.setTransactionDateTime(du.formatDateToString(trx.getTrxDtTime(), 
					((countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy")+" hh:mm:ss a"), mealSchool.getSchoolTimezone())));
			if(isExport != null && isExport){
				if(trxList == null || trxList.size() < 1){
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("No data available.");
					return serviceResponse;
				}
				//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
				packagePaymentTrxUtil.transactionsDetailsReport(trxList, response, du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
								mealSchool.getSchoolTimezone().toString()), du.formatDateToString(sdfReq.parse(endDate), 
										"yyyy-MM-dd", mealSchool.getSchoolTimezone().toString()), mealSchoolId, countryDetail.getCurrencySymbol(), stdRecId, mealSchool.getCountryCode(), countryDetail.getDateFormat());
			}				
			serviceResponse.setResponse(trxList);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Packages deposit transactions retrieved successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get the packages deposit transactions.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage()+" with mealSchoolId::"+mealSchoolId+" and schoolYear::"+schoolYear);
		}
		return serviceResponse;
	}

	/**This method used for generate the paymob transaction charges report**/
	@Override
	public ServiceResponse payMobTrxCharges(Long mealSchoolId, String startDate, String endDate, Boolean isExport, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			
			if(mealSchool == null || mealSchool.getSchoolId() == null)
				throw new Exception("School doesn't exist with school id: "+mealSchoolId);
			List<PaymobTrxChargesResp> trxList = null;
			List<Object[]> objList = reportsDao.payMobTrxCharges(mealSchoolId, startDate, endDate);
			trxList = objList.stream().map(PaymobTrxChargesResp::new).collect(Collectors.toList());
			trxList.stream().forEach(trx-> trx.setTrxDateTime(du.formatDateToString(trx.getTrxDtTm(), 
					"MM/dd/yyyy hh:mm:ss a", mealSchool.getSchoolTimezone())));
			if(isExport != null && isExport){
				if(trxList == null || trxList.size() < 1){
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("No data available.");
					return serviceResponse;
				}
				String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
				paymobTrxChargesUtil.transactionsDetailsReport(trxList, response, du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
								mealSchool.getSchoolTimezone().toString()), du.formatDateToString(sdfReq.parse(endDate), 
										"yyyy-MM-dd", mealSchool.getSchoolTimezone().toString()), mealSchoolId, currencySymbol, mealSchool.getSchoolName());
			}
			serviceResponse.setResponse(trxList);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Paymob transaction charges report generated successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the paymob transaction charges report for mealSchoolId::"+mealSchoolId+" and startDt::"+startDate+" and endDt::"+endDate);
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for build the key & value to get the dashboard report**/
	private Map<String, Long> buildCountByReqKey(List<Object[]> objArrayList){
		Map<String, Long> keyValMap = new TreeMap<String, Long>();
		if(objArrayList != null && objArrayList.size() > 0){
			for(Object[] obj : objArrayList){
				if(obj[0] != null && obj[1] != null){
					keyValMap.put(obj[1].toString(), Long.parseLong(obj[0].toString()));
				}
			}
		}
		return keyValMap;
	}
	

	@Override
	/**This method used for generate the order cost report**/
	public ServiceResponse orderCostReport(Long mealSchoolId, String startDt, String endDt, ItemTypeConstants menuType, Boolean isExport, HttpServletResponse response, SchoolGrades grade) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Object[]> objList = reportsDao.orderCostReport(mealSchoolId, startDt, endDt, menuType, grade);
			List<OrderCostInfo> orderCostReports = objList.stream().map(OrderCostInfo::new).collect(Collectors.toList());
			Map<String, Double> costByGrade = orderCostReports.stream().filter(p ->  p.getCost() != null).collect(
			                Collectors.groupingBy(OrderCostInfo::getGrade, Collectors.summingDouble(OrderCostInfo::getCost)));
			Map<String, Long> countingByGrade = orderCostReports.stream().collect(
	                Collectors.groupingBy(OrderCostInfo::getGrade, Collectors.counting()));
			serviceResponse.setResponse(orderCostReports);
			serviceResponse.setCostByGrade(costByGrade);
			if(isExport){
				if(orderCostReports == null || orderCostReports.size() < 1){
					serviceResponse.setStatus("Success");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("No data available!!");
					return serviceResponse;
				}
				String countryCode = mealSchoolRepository.getSchoolCountry(mealSchoolId);
				CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(countryCode);
				orderCostReportUtil.orderCostReportExport(orderCostReports, response, startDt, endDt, mealSchoolId, costByGrade, countryDetail.getCurrencySymbol(), 
						countryDetail.getDateFormat(), countryCode, countingByGrade);
			}				
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Order cost report generated successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate order cost report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for get the package transactions info based on master transaction id**/
	@Override
	public ServiceResponse pkgTrxInfo(Long pkgMasterTrxId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Object[]> pkgInfo = packageRepo.duePaymentPkgInfo(pkgMasterTrxId);
			if(pkgInfo != null && pkgInfo.size() > 0){
				List<DuePaymentResp> duePaymentResps = pkgInfo.stream().map(DuePaymentResp::new).collect(Collectors.toList());
				serviceResponse.setResponse(duePaymentResps);
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Subscribed packages info retrieved successfully.");
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Transaction id is not valid.");
			}
			logger.info(serviceResponse.getStatusMessage()+" for trxId::"+pkgMasterTrxId);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to retrieved subscribed packages info.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for generate the Caterer's report***/
	@Override
	public ServiceResponse caterersReport(Long catererId, Date startDate, Date endDate, ItemTypeConstants menuType, Long mealSchoolId)
			throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Object[]> objList = reportsDao.caterersReport(catererId, startDate, endDate, menuType, mealSchoolId);
			List<CaterersResp> caterersResps = objList.stream().map(CaterersResp::new).collect(Collectors.toList());
			Map<Long, List<CaterersResp>> repBySchool = caterersResps.stream().collect(Collectors.groupingBy(CaterersResp::getMealSchoolId));
			Map<Long, Map<String, Map<String, List<CaterersResp>>>> repBySchoolDateCategory = new HashMap<>();
			for(Map.Entry<Long, List<CaterersResp>> entry : repBySchool.entrySet()){
				Map<String, Map<String, List<CaterersResp>>> repByDateCategory = new HashMap<>();
				Map<Date, List<CaterersResp>> repByDate = entry.getValue().stream().collect(Collectors.groupingBy(CaterersResp::getDate));
				for(Map.Entry<Date, List<CaterersResp>> entry2 : repByDate.entrySet()){
					Map<String, List<CaterersResp>> repByCategory = entry2.getValue().stream().collect(Collectors.groupingBy(CaterersResp::getCategory));
					repByDateCategory.put(sdf.format(entry2.getKey()), repByCategory);
				}
				Map<String, Map<String, List<CaterersResp>>> sortedMealMap = new TreeMap<String, Map<String, List<CaterersResp>>>(repByDateCategory);
				repBySchoolDateCategory.put(entry.getKey(), sortedMealMap);
			}
			serviceResponse.setResponse(repBySchoolDateCategory);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Caterer's report generated succesfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate Caterer's report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	private Map<Long, EligSummaryResp> builMapData(List<EligSummaryResp1> eligSummaryResp1s){	
		EligSummaryResp e1 = null;
		Map<Long, EligSummaryResp> eligMap = new HashMap<>();
		for(EligSummaryResp1 elResp1 : eligSummaryResp1s){
			if(eligMap.get(elResp1.getMealSchoolId()) == null){
				e1 = new EligSummaryResp();
				e1.setMealSchoolId(elResp1.getMealSchoolId());
				e1.setSchoolName(elResp1.getSchoolName());
				e1.setSchoolYear(elResp1.getSchoolYear());
			}else
				e1 = eligMap.get(elResp1.getMealSchoolId());
			if(elResp1.getIsActive() && elResp1.getReCertificateDate() == null && elResp1.getRecertPending().equalsIgnoreCase("Y")){
				if(elResp1.getIsFreeMeal())
					e1.setTempFree(e1.getTempFree()+elResp1.getStdCount());
				else if(elResp1.getIsReducedMeal())
					e1.setTempRed(e1.getTempRed()+elResp1.getStdCount());
			}
			if(elResp1.getIsActive() && elResp1.getReCertificateDate() != null && elResp1.getRecertPending().equalsIgnoreCase("N")){
				if(elResp1.getIsFreeMeal() && (elResp1.getDecisionReason().equalsIgnoreCase("DCT") || elResp1.getDecisionReason().equalsIgnoreCase("DC") || 
						elResp1.getDecisionReason().equalsIgnoreCase("DCF")))
					e1.setDirectCert(e1.getDirectCert()+elResp1.getStdCount());
				else if(elResp1.getDecisionReason().equalsIgnoreCase("FI") && elResp1.getIsFreeMeal())
					e1.setIncomeFree(e1.getIncomeFree()+elResp1.getStdCount());
				else if(elResp1.getDecisionReason().equalsIgnoreCase("R") && elResp1.getIsReducedMeal())
					e1.setIncomeRed(e1.getIncomeRed()+elResp1.getStdCount());
				else if(elResp1.getIsFreeMeal()){
					if(elResp1.getActualPrg().equalsIgnoreCase("SNAP"))
						e1.setSnap(e1.getSnap()+elResp1.getStdCount());
					else if(elResp1.getActualPrg().equalsIgnoreCase("TANF"))
						e1.setTanf(e1.getTanf()+elResp1.getStdCount());
					else if(elResp1.getActualPrg().equalsIgnoreCase("FDPIR"))
						e1.setFdpir(e1.getFdpir()+elResp1.getStdCount());
					else if(elResp1.getActualPrg().equalsIgnoreCase("Foster"))
						e1.setFosterChild(e1.getFosterChild()+elResp1.getStdCount());
				}
			}
			if(elResp1.getIsActive() && elResp1.getActualPrg().equalsIgnoreCase("Homeless"))
				e1.setHomeless(e1.getHomeless()+elResp1.getStdCount());		
			
			if(elResp1.getIsActive() && elResp1.getIsFreeMeal())
				e1.setTotalFree(e1.getTotalFree()+elResp1.getStdCount());
			else if(elResp1.getIsActive() && elResp1.getIsReducedMeal())
				e1.setTotalRed(e1.getTotalRed()+elResp1.getStdCount());
			else if(elResp1.getIsActive())
				e1.setTotalPaid(e1.getTotalPaid()+elResp1.getStdCount());
			if(!elResp1.getIsActive())
				e1.setTotalInactive(e1.getTotalInactive()+elResp1.getStdCount());
			eligMap.put(elResp1.getMealSchoolId(), e1);
		}
		return eligMap;
	}

	/**This method used for get district dashboard data**/
	@Override
	public ServiceResponse districtDashboard(Long districtId, String currentDate) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Object[]> deposits = districtRepository.getDeposits(districtId, currentDate);
			Map<Long, String> depositsBySchool = mapDeposit(deposits);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Map<Long, Map<String, Integer>> ordersBySch = mapOrders(districtRepository.getOrders(districtId, sdf.format(sdf1.parse(currentDate))));
			List<DistrictDashboardResp> resp = mapDistrictResp(districtRepository.getSchoolLedger(districtId, currentDate), 
					depositsBySchool, ordersBySch);
			serviceResponse.setResponse(resp);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("District dashboard data retrieved successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get district dashboar data.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	private Map<Long, String> mapDeposit(List<Object[]> deposits){
		Map<Long, String> depositBySchool = new HashMap<>();
		if(deposits != null)
			for(Object[] obj : deposits){
				if(obj[0] != null)
					depositBySchool.put(Long.parseLong(obj[0].toString()), df.format(Double.valueOf(obj[1].toString())));
			}
		return depositBySchool;
	}
	
	private List<DistrictDashboardResp> mapDistrictResp(List<Object[]> objArr, Map<Long, String> depBySch, 
			Map<Long, Map<String, Integer>> ordersBySch){
		List<DistrictDashboardResp> resps = new ArrayList<>();
		DistrictDashboardResp resp = null;
		for(Object[] obj : objArr){
			resp = new DistrictDashboardResp();
			resp.setMealSchoolId(obj[0] != null ? Long.valueOf(obj[0].toString()): 0);
			resp.setSchoolName(obj[1] != null ? obj[1].toString() : "");
			resp.setSchoolYear(obj[3] != null ? obj[3].toString() : "");
			resp.setSubdomain(obj[8] != null ? obj[8].toString() : "");
			Map<String, Integer> ledger = new HashMap<>();
			ledger.put("Free", Integer.valueOf(obj[4].toString()));
			ledger.put("Reduced", Integer.valueOf(obj[5].toString()));
			ledger.put("Regular", (Integer.valueOf(obj[7].toString()) != 0 ? 
					Integer.valueOf(obj[6].toString()) : 0));
			ledger.put("Total", Integer.valueOf(obj[7].toString()));
			resp.setLedger(ledger);
			Map<String, String> financial = new HashMap<>();
			financial.put("Balance", df.format(Double.valueOf(obj[2].toString())));
			financial.put("Deposit", depBySch.get(resp.getMealSchoolId()));
			resp.setFinancial(financial);
			resp.setOrders(ordersBySch.get(resp.getMealSchoolId()));
			resps.add(resp);
		}
		return resps;
	}
	
	private Map<Long, Map<String, Integer>> mapOrders(List<Object[]> objArr){
		Map<Long, Map<String, Integer>> mapResp = new HashMap<>();
		for(Object[] obj : objArr){
			Map<String, Integer> ordByElig = new HashMap<>();
			ordByElig.put("Free", Integer.valueOf(obj[1].toString()));
			ordByElig.put("Reduced", Integer.valueOf(obj[2].toString()));
			ordByElig.put("Regular", Integer.valueOf(obj[3].toString()));
			ordByElig.put("Total", Integer.valueOf(obj[4].toString()));
			if(obj[0] != null)
				mapResp.put(Long.valueOf(obj[0].toString()), ordByElig);
		}
		return mapResp;
	}
	
	private List<ReimbursementRatesInfo> manipulateReimb(Long mealSchoolId, String type){
		Boolean isReimbursementDeclared = true;
		List<ReimbursementRatesInfo> reimb = new ArrayList<ReimbursementRatesInfo>( 
				schoolYearRepository.getReimburseRates(mealSchoolId, ReimbursementMealsType.valueOf(type)));
		if(reimb == null || reimb.size() < 1)
			isReimbursementDeclared = false;
		if(isReimbursementDeclared){
			for(ReimbursementRatesInfo rate : reimb){
				boolean statusVal = checkReimbursementRates(rate);
				if(!statusVal){
					isReimbursementDeclared = false;
					break;
				}
			}
		}
		if(!isReimbursementDeclared){
			return null;
		}else
			return reimb;
	}
	
	private Map<String, Map<String, String>> buildReimbObj(Long districtId, String startDate, String endDate, String itemType, Boolean isNeedy, 
			Integer schoolYear, Integer totalStudentCount, Integer reducedStudentCount, Integer freeStudentCount){
		Map<String, Map<String, String>> auditMap = new LinkedHashMap<String, Map<String, String>>();
		List<Object[]> mealServedCountByElig = reportsDao.distMealsServedCountByElig(districtId, startDate, endDate, itemType, isNeedy);
		if(mealServedCountByElig != null && mealServedCountByElig.size() > 1){
			Map<String, String> auditDetails = servedMealsMap(mealServedCountByElig, endDate);
			auditDetails.put("servingDays", String.valueOf(reportsDao.distMealsServingDays(districtId, startDate, endDate, itemType)));
			auditDetails.put("attendance", String.valueOf(totalStudentCount));
			auditDetails.put("extendedFree", String.valueOf(0));
			Map<String, String> auditDetails1 = new LinkedHashMap<String, String>();
			auditDetails1.put("paidStudents", String.valueOf(totalStudentCount-(freeStudentCount+reducedStudentCount)));
			auditDetails1.put("reducedStudents", String.valueOf(reducedStudentCount));
			auditDetails1.put("freeStudents", String.valueOf(freeStudentCount));
			auditDetails1.put("totalStudents", String.valueOf(totalStudentCount));
			auditDetails1.put("paidAdults", "");
			auditDetails1.put("servingDays", "");
			auditDetails1.put("attendance", "");
			auditDetails1.put("extendedFree", "");
			auditMap.put("Approved Benefits", auditDetails1);
			auditMap.put("Meals Served", auditDetails);
		}else{
			Map<String, String> auditDetails = new LinkedHashMap<>();
			auditDetails.put("paidStudents", String.valueOf(0));
			auditDetails.put("reducedStudents", String.valueOf(0));
			auditDetails.put("freeStudents", String.valueOf(0));
			auditDetails.put("totalStudents", String.valueOf(0));
			auditDetails.put("paidAdults", String.valueOf(0));
			auditDetails.put("servingDays", String.valueOf(reportsDao.distMealsServingDays(districtId, startDate, endDate, itemType)));
			auditDetails.put("attendance", String.valueOf(0));
			auditDetails.put("extendedFree", String.valueOf(0));
			Map<String, String> auditDetails1 = new LinkedHashMap<String, String>();
			auditDetails1.put("paidStudents", String.valueOf(totalStudentCount-(freeStudentCount+reducedStudentCount)));
			auditDetails1.put("reducedStudents", String.valueOf(reducedStudentCount));
			auditDetails1.put("freeStudents", String.valueOf(freeStudentCount));
			auditDetails1.put("totalStudents", String.valueOf(totalStudentCount));
			auditDetails1.put("paidAdults", "");
			auditDetails1.put("servingDays", "");
			auditDetails1.put("attendance", "");
			auditDetails1.put("extendedFree", "");
			auditMap.put("Approved Benefits", auditDetails1);
			auditMap.put("Meals Served", auditDetails);
		}
		return auditMap;
	}

	/**This method used for generate the revenue report**/
	@Override
	public ServiceResponse revenueReport(Long schoolId, Integer schoolYear, String startDate, String endDate, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool school =mealSchoolRepository.findOne(schoolId);
			
			/*String timezoneV = "+00:00";
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(school.getCountryCode());
			for(TimezoneDetails timezoneDetails : countryDetail.getTimezoneDetails()){
				if(school.getSchoolTimezone().equalsIgnoreCase(timezoneDetails.getTimezoneName())){
					String tz[] = timezoneDetails.getTimezoneDesc().split(":");
					if(tz.length > 2)
						timezoneV = (tz[1]+":"+tz[2]).replace(" UTC", "");
					else if(tz.length > 1)
						timezoneV = (tz[1]+":00").replace(" UTC", "");
				}
			}*/
			String userName = "";
			if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
				userName = SecurityContextHolder.getContext().getAuthentication().getName();
			SchoolUser schoolUser = mealSchoolRepository.schoolUser(userName);
			if(schoolUser != null)
				userName = schoolUser.getFirstName()+" "+schoolUser.getLastName();
			Map<String, Map<String, Map<String, Double>>> charges = getRevenueCharges(schoolId, startDate, endDate);
			RevenueResp revenueResp = buildRevenueData(schoolId, schoolYear, startDate, endDate, charges);
			revenueReportUtil.generateRevenueReport(revenueResp, response, du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
					school.getSchoolTimezone().toString()), du.formatDateToString(sdfReq.parse(endDate), "yyyy-MM-dd", 
							school.getSchoolTimezone().toString()), schoolId, countryDetailsRepository.getCurrencySymbol(school.getCountryCode()), school.getSchoolName(),userName
					, du.formatDateToString(new Date(), "yyyy-MM-dd HH:mm:ss", school.getSchoolTimezone().toString()));
			serviceResponse.setStatus("success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Revenue report generated successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate revenue report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate income report**/
	@Override
	public ServiceResponse incomeReport(Long schoolId, String itemType, String yearMonth, String startDate, String endDate,
			HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool school = mealSchoolRepository.findOne(schoolId);
			
			String timezone = school.getSchoolTimezone().toString();
			String timezoneV = "+00:00";
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(school.getCountryCode());
			for(TimezoneDetails timezoneDetails : countryDetail.getTimezoneDetails()){
				if(timezone.equalsIgnoreCase(timezoneDetails.getTimezoneName())){
					String tz[] = timezoneDetails.getTimezoneDesc().split(":");
					if(tz.length > 2)
						timezoneV = (tz[1]+":"+tz[2]).replace(" UTC", "");
					else if(tz.length > 1)
						timezoneV = (tz[1]+":00").replace(" UTC", "");
				}
			}
			List<IncomeResp> incomeResps = buildIncomeData(schoolId, itemType, yearMonth, startDate, endDate, timezoneV);
			incomeReportUtil.generateIncomeReport(incomeResps, response, itemType, du.formatDateToString(sdfReq.parse(startDate), "yyyy-MM-dd", 
					timezone), du.formatDateToString(sdfReq.parse(endDate), "yyyy-MM-dd", 
							timezone), schoolId, countryDetailsRepository.getCurrencySymbol(school.getCountryCode()), school.getSchoolName());
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Income report generated successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate income report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for map revenue data
	 * @throws ParseException **/
	private RevenueResp buildRevenueData(Long schoolId, Integer schoolYear, String startDate, String endDate, Map<String, Map<String, Map<String, Double>>> charges) throws ParseException{
		RevenueResp revenueResp = new RevenueResp();
		Map<String, Map<String, Integer>> reimbMeals = new LinkedHashMap<>();
		//Map<String, Map<String, Integer>> map2 = new HashMap<>();
		List<Object[]> mealServedCountByElig = reportsDao.mealsServedCountByElig(schoolId, startDate, endDate, "Lunch", false);
		List<Object[]> breakfastServedCountByElig = reportsDao.mealsServedCountByElig(schoolId, startDate, endDate, "Breakfast", false);
		List<Object[]> snackServedCountByElig = reportsDao.mealsServedCountByElig(schoolId, startDate, endDate, "Snack", false);
		Map<String, String> lAuditDetails = servedMealsMap(mealServedCountByElig, endDate);
		Map<String, String> bAuditDetails = servedMealsMap(breakfastServedCountByElig, endDate);
		Map<String, String> sAuditDetails = servedMealsMap(snackServedCountByElig, endDate);
		Map<String, Integer> map1 = new HashMap<>();
		map1.put("Breakfast", Integer.valueOf(bAuditDetails.get("freeStudents")));
		map1.put("Lunch", Integer.valueOf(lAuditDetails.get("freeStudents")));
		map1.put("Snack", Integer.valueOf(sAuditDetails.get("freeStudents")));
		reimbMeals.put("Free", map1);
		
		map1 = new HashMap<>();
		map1.put("Breakfast", Integer.valueOf(bAuditDetails.get("reducedStudents")));
		map1.put("Lunch", Integer.valueOf(lAuditDetails.get("reducedStudents")));
		map1.put("Snack", Integer.valueOf(sAuditDetails.get("reducedStudents")));
		reimbMeals.put("Reduced", map1);
		
		map1 = new HashMap<>();
		map1.put("Breakfast", Integer.valueOf(bAuditDetails.get("paidStudents")));
		map1.put("Lunch", Integer.valueOf(lAuditDetails.get("paidStudents")));
		map1.put("Snack", Integer.valueOf(sAuditDetails.get("paidStudents")));
		reimbMeals.put("Paid", map1);
		
		map1 = new HashMap<>();
		map1.put("Breakfast", (Integer.valueOf(bAuditDetails.get("paidStudents"))
				+Integer.valueOf(bAuditDetails.get("reducedStudents"))+Integer.valueOf(bAuditDetails.get("freeStudents"))));
		map1.put("Lunch", (Integer.valueOf(lAuditDetails.get("paidStudents"))
				+Integer.valueOf(lAuditDetails.get("reducedStudents"))+Integer.valueOf(lAuditDetails.get("freeStudents"))));
		map1.put("Snack", (Integer.valueOf(sAuditDetails.get("paidStudents"))
				+Integer.valueOf(sAuditDetails.get("reducedStudents"))+Integer.valueOf(sAuditDetails.get("freeStudents"))));
		reimbMeals.put("Total", map1);
		
		revenueResp.setReimbMeal(reimbMeals);
		
		Map<String, Map<String, Integer>> nonReimbMeals = new LinkedHashMap<>();
		map1 = new HashMap<>();
		Integer abc = Integer.parseInt(reportsDao.otherServedMeals(schoolId, startDate, endDate, "Breakfast", "Additional").toString());
		Integer alc = Integer.parseInt(reportsDao.otherServedMeals(schoolId, startDate, endDate, "Lunch", "Additional").toString());
		Integer asc = Integer.parseInt(reportsDao.otherServedMeals(schoolId, startDate, endDate, "Snack", "Additional").toString());
		map1.put("Breakfast", abc);
		map1.put("Lunch", alc);
		map1.put("Snack", asc);
		nonReimbMeals.put("Additional Meal", map1);
		
		Integer bcount = Integer.parseInt(reportsDao.staffServedMeals(schoolId, startDate, endDate, "Breakfast").toString());
		map1 = new HashMap<>();
		map1.put("Breakfast", bcount);
		Integer lcount = Integer.parseInt(reportsDao.staffServedMeals(schoolId, startDate, endDate, "Lunch").toString());
		map1.put("Lunch", lcount);
		Integer scount = Integer.parseInt(reportsDao.staffServedMeals(schoolId, startDate, endDate, "Snack").toString());
		map1.put("Snack", scount);
		nonReimbMeals.put("Staff", map1);
		
		map1 = new HashMap<>();
		map1.put("Breakfast", bcount+abc);
		map1.put("Lunch", lcount+alc);
		map1.put("Snack", scount+asc);
		nonReimbMeals.put("Total", map1);
		revenueResp.setNonReimbMeal(nonReimbMeals);
		
		Map<String, Map<String, Map<String, Double>>> revenueFromMeal = new LinkedHashMap<>();
		Map<String, Map<String, Double>> map4 = new HashMap<>();
		Map<String, Double> chargesV = new HashMap<>();
		if(charges.get("Regular") != null && charges.get("Regular").get("Breakfast") != null)
			chargesV = charges.get("Regular").get("Breakfast");
		Map<String, Double> map3 = chargesMap(chargesV);
		map4.put("Breakfast",map3);
		chargesV = new HashMap<>();
		if(charges.get("Regular") != null && charges.get("Regular").get("Lunch") != null)
			chargesV = charges.get("Regular").get("Lunch");
		map3 = chargesMap(chargesV);
		map4.put("Lunch",map3);
		chargesV = new HashMap<>();
		if(charges.get("Regular") != null && charges.get("Regular").get("Snack") != null)
			chargesV = charges.get("Regular").get("Snack");
		map3 = chargesMap(chargesV);
		map4.put("Snack",map3);
		revenueFromMeal.put("Regular Meal", map4);
		
		map4 = new HashMap<>();
		chargesV = new HashMap<>();
		if(charges.get("Additional") != null && charges.get("Additional").get("Breakfast") != null)
			chargesV = charges.get("Additional").get("Breakfast");
		map3 = chargesMap(chargesV);
		map4.put("Breakfast",map3);
		chargesV = new HashMap<>();
		if(charges.get("Additional") != null && charges.get("Additional").get("Lunch") != null)
			chargesV = charges.get("Additional").get("Lunch");
		map3 = chargesMap(chargesV);
		map4.put("Lunch",map3);
		chargesV = new HashMap<>();
		if(charges.get("Additional") != null && charges.get("Additional").get("Snack") != null)
			chargesV = charges.get("Additional").get("Snack");
		map3 = chargesMap(chargesV);
		map4.put("Snack",map3);
		revenueFromMeal.put("Additional Meal", map4);
		map4 = new HashMap<>();
		map3 = staffChargesMap(charges,"Breakfast");
		map4.put("Breakfast",map3);
		map3 = staffChargesMap(charges,"Lunch");
		map4.put("Lunch",map3);
		map3 = staffChargesMap(charges,"Snack");
		map4.put("Snack",map3);
		revenueFromMeal.put("Staff", map4);
		
		map4 = new HashMap<>();
		chargesV = new HashMap<>();
		if(charges.get("Total") != null && charges.get("Total").get("Breakfast") != null)
			chargesV = charges.get("Total").get("Breakfast");
		map3 = chargesMap(chargesV);
		map4.put("Breakfast",map3);
		chargesV = new HashMap<>();
		if(charges.get("Total") != null && charges.get("Total").get("Lunch") != null)
			chargesV = charges.get("Total").get("Lunch");
		map3 = chargesMap(chargesV);
		map4.put("Lunch",map3);
		chargesV = new HashMap<>();
		if(charges.get("Total") != null && charges.get("Total").get("Snack") != null)
			chargesV = charges.get("Total").get("Snack");
		map3 = chargesMap(chargesV);
		map4.put("Snack",map3);
		revenueFromMeal.put("Total", map4);
		revenueResp.setRevenueFromMeal(revenueFromMeal);
		
		Map<String, Map<String, Map<String, Double>>> revenueFromALaCarte = new LinkedHashMap<>();
		map4 = new HashMap<>();
		chargesV = new HashMap<>();
		if(charges.get("ALaCarte") != null && charges.get("ALaCarte").get("Breakfast") != null)
			chargesV = charges.get("ALaCarte").get("Breakfast");
		map3 = chargesMap(chargesV);
		map4.put("Breakfast",map3);
		chargesV = new HashMap<>();
		if(charges.get("ALaCarte") != null && charges.get("ALaCarte").get("Lunch") != null)
			chargesV = charges.get("ALaCarte").get("Lunch");
		map3 = chargesMap(chargesV);
		map4.put("Lunch",map3);
		chargesV = new HashMap<>();
		if(charges.get("ALaCarte") != null && charges.get("ALaCarte").get("Snack") != null)
			chargesV = charges.get("ALaCarte").get("Snack");
		map3 = chargesMap(chargesV);
		map4.put("Snack",map3);
		revenueFromALaCarte.put("Student", map4);
		
		map4 = new HashMap<>();
		chargesV = new HashMap<>();
		if(charges.get("staffALaCarte") != null && charges.get("staffALaCarte").get("Breakfast") != null)
			chargesV = charges.get("staffALaCarte").get("Breakfast");
		map3 = chargesMap(chargesV);
		map4.put("Breakfast",map3);
		chargesV = new HashMap<>();
		if(charges.get("staffALaCarte") != null && charges.get("staffALaCarte").get("Lunch") != null)
			chargesV = charges.get("staffALaCarte").get("Lunch");
		map3 = chargesMap(chargesV);
		map4.put("Lunch",map3);
		chargesV = new HashMap<>();
		if(charges.get("staffALaCarte") != null && charges.get("staffALaCarte").get("Snack") != null)
			chargesV = charges.get("staffALaCarte").get("Snack");
		map3 = chargesMap(chargesV);
		map4.put("Snack",map3);
		revenueFromALaCarte.put("Staff", map4);
		
		map4 = new HashMap<>();
		chargesV = new HashMap<>();
		if(charges.get("AlcTotal") != null && charges.get("AlcTotal").get("Breakfast") != null)
			chargesV = charges.get("AlcTotal").get("Breakfast");
		map3 = chargesMap(chargesV);
		map4.put("Breakfast",map3);
		chargesV = new HashMap<>();
		if(charges.get("AlcTotal") != null && charges.get("AlcTotal").get("Lunch") != null)
			chargesV = charges.get("AlcTotal").get("Lunch");
		map3 = chargesMap(chargesV);
		map4.put("Lunch",map3);
		chargesV = new HashMap<>();
		if(charges.get("AlcTotal") != null && charges.get("AlcTotal").get("Snack") != null)
			chargesV = charges.get("AlcTotal").get("Snack");
		map3 = chargesMap(chargesV);
		map4.put("Snack",map3);
		revenueFromALaCarte.put("Total", map4);
		
		revenueResp.setRevenueFromAlaCarte(revenueFromALaCarte);
		
		Map<String, Map<String, Double>> depFromPay = new LinkedHashMap<>();
		map3 = new HashMap<>();
		map3.put("Prepayments", 0.0);
		map3.put("Charges Paid", 0.0);
		map3.put("Total", 0.0);
		depFromPay.put("Free", map3);
		map3 = new HashMap<>();
		Double redTotal = 0.0;
		map3.put("Prepayments", redTotal);
		map3.put("Charges Paid", 0.0);
		map3.put("Total", redTotal);
		depFromPay.put("Reduced", map3);
		
		map3 = new HashMap<>();
		Double paidTotal = 0.0;
		map3.put("Prepayments", paidTotal);
		map3.put("Charges Paid", 0.0);
		map3.put("Total", paidTotal);
		depFromPay.put("Paid", map3);
		
		Double totalAmt = redTotal+paidTotal;
		map3 = new HashMap<>();
		map3.put("Prepayments", totalAmt);
		map3.put("Charges Paid", 0.0);
		map3.put("Total", totalAmt);
		depFromPay.put("Total", map3);
		revenueResp.setDepositFromPayment(depFromPay);

		//SchoolYear schoolYearObj = schoolYearRepository.findByMealSchoolSchoolIdAndSchoolYear(schoolId, schoolYear);
		Double begBal = 0.0; //schoolYearObj.getEndingBal() != null ? schoolYearObj.getEndingBal() : 0.0;
		//Double deposits = reportsDao.paidAmt(schoolId, startDate, endDate, "Deposit");
		//Double adj = 0.0;//reportsDao.paidAmt(schoolId, startDate, endDate, "Transfer");
		map3 = new LinkedHashMap<>();
		Object[] sales = reportsDao.totalSales(schoolId, startDate, endDate,true);
		Double ppAmt = sales[0] != null ? Double.valueOf(sales[0].toString()) : 0.0;
		Double ccAmt = sales[1] != null ? Double.valueOf(sales[1].toString()) : 0.0;
		Object[] npsales = reportsDao.totalSales(schoolId, startDate, endDate,false);
		Double npppAmt = npsales[0] != null ? Double.valueOf(npsales[0].toString()) : 0.0;
		Double npccAmt = npsales[1] != null ? Double.valueOf(npsales[1].toString()) : 0.0;
		//Double totChargedAmt = sales[2] != null ? Double.valueOf(sales[2].toString()) : 0.0;
		Double chargedPOS = reportsDao.chargedPOS(schoolId, startDate, endDate);
		if(chargedPOS == null)
			chargedPOS = 0.0;
		Double posTotDeposit = reportsDao.totPosDeposit(schoolId, startDate, endDate,false);
		Double posDirectTotDeposit = reportsDao.totPosDeposit(schoolId, startDate, endDate,true);
		Double schoolDeposit = reportsDao.schoolDeposit(schoolId, startDate, endDate);
		if(posTotDeposit == null)
			posTotDeposit = 0.0;
		map3.put("Beginning Balance", begBal);
		map3.put("Reimbursement Sales", (ccAmt+ppAmt));
		map3.put("Daily Cash / Check Payments", ccAmt);
		map3.put("Pre-paid", ppAmt); //Prepayment
		map3.put("Non Reimbursement Sales", (npccAmt+npppAmt));
		map3.put("NPrgDaily Cash / Check Payments", npccAmt);
		map3.put("NPrgPre-paid", npppAmt); //Prepayment
		map3.put("Charged - POS", chargedPOS);
		map3.put("Deposits - POS", (posTotDeposit-(chargedPOS+ccAmt+npccAmt)));
		map3.put("School Deposits", (posDirectTotDeposit+schoolDeposit));
		Double endBal = begBal + (ccAmt+npccAmt+posDirectTotDeposit+schoolDeposit+chargedPOS+(posTotDeposit-(chargedPOS+ccAmt+npccAmt)));
		map3.put("Ending Cash Balance", endBal);
		//schoolYearObj.setEndingBal(Double.valueOf(df.format(endBal)));
		//schoolYearRepository.save(schoolYearObj);
		revenueResp.setSalesSummary(map3);
		List<Object[]> revenueByLoc = mealSchoolRepository.getRevenueByLoc(schoolId, 
				sdfOrg.parse(startDate.replace("T", " ")), sdfOrg.parse(endDate.replace("T", " ")));
		revenueResp.setRevenueByLoc(revenueByLoc);	
		return revenueResp;
	}
	
	private List<IncomeResp> buildIncomeData(Long schoolId, String itemType, String yearMonth, String startDate, String endDate,String timezone){
		List<IncomeResp> incomeResps = new LinkedList<IncomeResp>();
		List<Object[]> respObj = reportsDao.dailyReimbIncome(schoolId, itemType, startDate, endDate, timezone);
		Map<Integer, Map<Integer, Map<String, Double>>> stdAmtByEligAndDt = buildReimbIncome(respObj);
		Map<Integer, Map<String, Map<String, Double>>> amtByT = buildIncomeByType(schoolId, itemType, startDate, endDate, timezone,"",new HashMap<>());
		Map<Integer, Map<String, Map<String, Double>>> amtByType = buildIncomeByType(schoolId, itemType, startDate, endDate, timezone,"staff",amtByT);
		int yearVal = Integer.parseInt(yearMonth.substring(0,4));
    	int monthVal = Integer.parseInt(yearMonth.substring(4))-1;
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(yearVal, monthVal, 01);
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	int day = 1;
    	Double cFullPrice,cRedPrice,ppFullPrice,ppRedPrice,cpFullPrice,cpRedPrice,ccAm,ppAm,cpAm,ccAlc,ppAlc,cpAlc = null;

    	while (day <= daysInMonth) { 
    		cFullPrice = 0.0; cRedPrice=0.0; ppFullPrice=0.0;ppRedPrice=0.0;cpFullPrice=0.0;cpRedPrice=0.0;ccAm=0.0;ppAm=0.0;cpAm=0.0;ccAlc=0.0;ppAlc=0.0;cpAlc=0.0;
    		Map<Integer, Map<String, Double>> amtByPTElig = stdAmtByEligAndDt.get(day);
    		if(amtByPTElig != null && amtByPTElig.get(1) != null){
    			cRedPrice = amtByPTElig.get(1).get("ccAmt");
    			ppRedPrice = amtByPTElig.get(1).get("ppAmt");
    			cpRedPrice = amtByPTElig.get(1).get("chargedAmt");
    		}
    		if(amtByPTElig != null && amtByPTElig.get(2) != null){
    			cFullPrice = amtByPTElig.get(2).get("ccAmt");
    			ppFullPrice = amtByPTElig.get(2).get("ppAmt");
    			cpFullPrice = amtByPTElig.get(2).get("chargedAmt");
    		}
    		Map<String, Map<String, Double>> chargesByType = amtByType.get(day);
    		Map<String, Double> chargesByPT = null;
    		if(chargesByType != null){
    			List<String> types = Arrays.asList("staffRegular","Additional","staffAdditional");
    			for(String tp : types){
    				chargesByPT = chargesByType.get(tp);
        			if(chargesByPT != null){ 
        				ccAm = ccAm+chargesByPT.get("ccAmt");
        				ppAm = ppAm+chargesByPT.get("ppAmt");
        				cpAm = cpAm+chargesByPT.get("chargedAmt");
        			}
    			}
    			types = Arrays.asList("ALaCarte","staffALaCarte");
    			for(String tp : types){
    				chargesByPT = chargesByType.get(tp);
        			if(chargesByPT != null){ 
        				ccAlc = ccAlc+chargesByPT.get("ccAmt");
        				ppAlc = ppAlc+chargesByPT.get("ppAmt");
        				cpAlc = cpAlc+chargesByPT.get("chargedAmt");
        			}
    			}
    		}    		
    		if(day > 0)
    			incomeResps.add(new IncomeResp(cFullPrice,cRedPrice,ppFullPrice,ppRedPrice,cpFullPrice,cpRedPrice,ccAm,ppAm,cpAm,ccAlc,ppAlc,cpAlc, String.valueOf(day)));
    		day++;    		
    	}
    	return incomeResps;
	}
	
	private Map<String, Map<String, Map<String, Double>>> getRevenueCharges(Long schoolId, String startDate, String endDate){
		Map<String, Map<String, Map<String, Double>>> charges = new HashMap<>();
		Map<String, Map<String, Double>> chargesByIT = null;
		Map<String, Double> chargesByPT = null;
		List<Object[]> objList = reportsDao.getCharges(schoolId, startDate, endDate, null, null);
		for(Object[] obj : objList){
			chargesByPT = new HashMap<>();
			if(charges.get(obj[3].toString()) != null)
				chargesByIT = charges.get(obj[3].toString());
			else
				chargesByIT = new HashMap<>();
			chargesByPT.put("ppAmt", Double.valueOf(obj[0].toString()));
			chargesByPT.put("ccAmt", Double.valueOf(obj[1].toString()));
			chargesByPT.put("chargedAmt", Double.valueOf(obj[2].toString()));
			chargesByIT.put(obj[4].toString(), chargesByPT);
			charges.put(obj[3].toString(), chargesByIT);
		}
		List<Object[]> staffObjList = reportsDao.getCharges(schoolId, startDate, endDate, "staff", null);
		String type = "";
		for(Object[] obj : staffObjList){
			chargesByPT = new HashMap<>();
			type = "staff"+obj[3].toString();
			if(charges.get(type) != null)
				chargesByIT = charges.get(type);
			else
				chargesByIT = new HashMap<>();
			chargesByPT.put("ppAmt", Double.valueOf(obj[0].toString()));
			chargesByPT.put("ccAmt", Double.valueOf(obj[1].toString()));
			chargesByPT.put("chargedAmt", Double.valueOf(obj[2].toString()));
			chargesByIT.put(obj[4].toString(), chargesByPT);
			charges.put(type, chargesByIT);
		}
		List<Object[]> totObjList = reportsDao.getCharges(schoolId, startDate, endDate, "both", false);
		for(Object[] obj : totObjList){
			chargesByPT = new HashMap<>();
			if(charges.get(obj[3].toString()) != null)
				chargesByIT = charges.get(obj[3].toString());
			else
				chargesByIT = new HashMap<>();
			chargesByPT.put("ppAmt", Double.valueOf(obj[0].toString()));
			chargesByPT.put("ccAmt", Double.valueOf(obj[1].toString()));
			chargesByPT.put("chargedAmt", Double.valueOf(obj[2].toString()));
			chargesByIT.put(obj[4].toString(), chargesByPT);
			charges.put(obj[3].toString(), chargesByIT);
		}
		List<Object[]> totAlcObjList = reportsDao.getCharges(schoolId, startDate, endDate, "both", true);
		for(Object[] obj : totAlcObjList){
			type = "Alc"+obj[3].toString();
			chargesByPT = new HashMap<>();
			if(charges.get(type) != null)
				chargesByIT = charges.get(type);
			else
				chargesByIT = new HashMap<>();
			chargesByPT.put("ppAmt", Double.valueOf(obj[0].toString()));
			chargesByPT.put("ccAmt", Double.valueOf(obj[1].toString()));
			chargesByPT.put("chargedAmt", Double.valueOf(obj[2].toString()));
			chargesByIT.put(obj[4].toString(), chargesByPT);
			charges.put(type, chargesByIT);
		}
		
		return charges;
	}
	
	private Map<String, Double> chargesMap(Map<String, Double> chargesV){
		Map<String, Double> map3 = new HashMap<>();
		map3.put("Cash / Check", cAmt(chargesV.get("ccAmt")));
		map3.put("Prepaid", cAmt(chargesV.get("ppAmt")));
		map3.put("Charged", cAmt(chargesV.get("chargedAmt")));
		map3.put("Total", (cAmt(chargesV.get("ccAmt"))+cAmt(chargesV.get("ppAmt"))+cAmt(chargesV.get("chargedAmt"))));
		return map3;
	}
	
	private Map<String, Double> staffChargesMap(Map<String, Map<String, Map<String, Double>>> charges, String type){
		Map<String, Double> chargesV = new HashMap<>();
		Map<String, Double> chargesV1 = new HashMap<>();
		if(charges.get("staffRegular") != null && charges.get("staffRegular").get(type) != null)
			chargesV = charges.get("staffRegular").get(type);
		if(charges.get("staffAdditional") != null && charges.get("staffAdditional").get(type) != null)
			chargesV1 = charges.get("staffAdditional").get(type);
		Map<String, Double> map3 = new HashMap<>();
		map3.put("Cash / Check", (cAmt(chargesV.get("ccAmt"))+cAmt(chargesV1.get("ccAmt"))));
		map3.put("Prepaid", (cAmt(chargesV.get("ppAmt"))+cAmt(chargesV1.get("ppAmt"))));
		map3.put("Charged", (cAmt(chargesV.get("chargedAmt"))+cAmt(chargesV1.get("chargedAmt"))));
		map3.put("Total", ((cAmt(chargesV.get("ccAmt"))+cAmt(chargesV.get("ppAmt"))+cAmt(chargesV.get("chargedAmt"))))+
				(cAmt(chargesV1.get("ccAmt"))+cAmt(chargesV1.get("ppAmt"))+cAmt(chargesV1.get("chargedAmt"))));
		return map3;
	}
	
	private Double cAmt(Double amt){
		return amt != null ? amt : 0.0;
	}

	/**This method used for generate the account balance summary report**/
	@Override
	public ServiceResponse accBalanceSummary(Long id, Boolean isDistrict, HttpServletResponse resp, Boolean isExport, Integer schoolYear, String dateV) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String loggedUser = "";
			if(schoolYear == null)
				schoolYear = 2021;
			if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			List<AccBalanceSummary> balSummaryList = new ArrayList<>();
			Map<String, Double> stdBalMap = new HashMap<>();
			Map<String, Double> staffBalMap = new HashMap<>();
			Map<String, Double> stdNegBalMap = new HashMap<>();
			Map<String, Double> staffNegBalMap = new HashMap<>();
			Date dateV1 = null;
			if(dateV != null && !dateV.trim().isEmpty()){
				dateV1 = sdfOrg.parse(dateV.replace("T", " "));
				stdBalMap = getBalMap(studentUserRepository.getPDPostiveBal(id, isDistrict, false, schoolYear, dateV1));
				staffBalMap = getBalMap(studentUserRepository.getPDPostiveBal(id, isDistrict, true, schoolYear, dateV1));
				stdNegBalMap = getBalMap(studentUserRepository.getPDNegativeBal(id, isDistrict, false, schoolYear, dateV1));
				staffNegBalMap = getBalMap(studentUserRepository.getPDNegativeBal(id, isDistrict, true, schoolYear, dateV1));
			}else{
				stdBalMap = getBalMap(studentUserRepository.getPostiveBal(id, isDistrict, false, schoolYear));
				staffBalMap = getBalMap(studentUserRepository.getPostiveBal(id, isDistrict, true, schoolYear));
				stdNegBalMap = getBalMap(studentUserRepository.getNegativeBal(id, isDistrict, false, schoolYear));
				staffNegBalMap = getBalMap(studentUserRepository.getNegativeBal(id, isDistrict, true, schoolYear));
			}			
			AccBalanceSummary balSummary = null;
			for(Map.Entry<String, Double> entry : stdBalMap.entrySet()){
				balSummary = new AccBalanceSummary();
				balSummary.setSchoolName(entry.getKey());
				balSummary.setStdAccBalance(entry.getValue());
				balSummary.setStdNegativeBalance(stdNegBalMap.get(entry.getKey()));
				balSummary.setStaffAccBalance(staffBalMap.get(entry.getKey()));
				balSummary.setStaffNegativeBalance(staffNegBalMap.get(entry.getKey()));
				balSummaryList.add(balSummary);
			}
			String countryCode = null;
			String timezone = "";
			String name = "";
			if(isDistrict){
				timezone = districtRepository.getTimezone(id);
				countryCode = districtRepository.getSchoolCountry(id);
				name = districtRepository.getDistName(id);
			}else{
				countryCode = mealSchoolRepository.getSchoolCountry(id);
				timezone = mealSchoolRepository.getSchoolTimezone(id);
				name = mealSchoolRepository.getSchoolName(id);
			}
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(countryCode);
			String currDate = du.formatDateToString(new Date(), "yyyy-MM-dd HH:mm:ss", timezone);
			String pDate = null;
			if(dateV1 != null)
				pDate = "REPORT DATE: "+du.formatDateToString(dateV1, "MM/dd/yyyy", timezone);
			balSummaryList.sort(Comparator.comparing(AccBalanceSummary::getSchoolName));
			if(isExport != null && isExport)
				depositSummaryReportUtil.accBalSummary(balSummaryList, resp, id, currencySymbol, isDistrict,loggedUser, currDate,name,pDate);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setResponse(balSummaryList);
			serviceResponse.setStatusMessage("Account Balance Summary report generated successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to geneate account balance summary report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	private Map<String, Double> getBalMap(List<Object[]> objArray){
		Map<String, Double> map = new HashMap<>();
		for(Object[] obj : objArray){
			if(obj[0] != null)
				map.put(obj[0].toString(), (obj[1] != null ? Double.valueOf(obj[1].toString()) : 0));
		}
		return map;
 	}
}
