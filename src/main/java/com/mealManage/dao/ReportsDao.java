package com.mealManage.dao;
import java.util.Date;
import java.util.List;

import com.mealManage.domain.MealChangeNotificationRequest;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;

public interface ReportsDao {
	
	public List<Object[]> orderedCountByGrade(Long mealSchoolId, String yearMonth, ItemTypeConstants menuType);
	
	public List<Object[]> orderedCountBySchool(Long catererId, String yearMonth, ItemTypeConstants menuType);
	
	public List<Object[]> notOrderedCountByGrade(Long mealSchoolId, String yearMonth, List<String> grades, Integer schoolYear);
	
	public List<Object[]> allStudentsCountByGrade(Long mealSchoolId, String yearMonth, List<String> grades, Integer schoolYear);
	
	//public List<Object[]> catererReport(Long mealSchoolId, Date startDate, Date endDate, List<String> grades);
	
	//public List<String> menuNamesBySchoolAndDateAndGrade(Long mealSchoolId, Date startDate, Date endDate, List<String> grades);
	
	//public List<Object[]> allMealsWithDate(Long mealSchoolId, Date startDate, Date endDate, List<String> grades);
	
	public List<Object[]> allMealsWithDateAndGrades(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, String type);
	
	public List<Object[]> allMealsWithDateAndGradesV2(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, String type);
	
	public List<String> getAllGrades(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, String itemType);
	
	public List<Object[]> orderedMealByStudentAndDate(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, String itemType, ItemTypeConstants menuType);
	
	public List<Object[]> catererOrders(Long catererId, Date startDate, Date endDate, String itemType, ItemTypeConstants menuType, Long mealSchoolId);
	
	public List<Object[]> orderedMealItemsReport(Long mealSchoolId, Date startDate, Date endDate, Boolean paymentStatus, List<String> grades);
	
	public List<Object[]> orderSummaryReport(Long mealSchoolId, List<String> yearMonths, Boolean paymentStatus, List<String> grades, ItemTypeConstants menuType) throws Exception;
	
	//public List<Object[]> orderedPaymentAmtByGrade(Long mealSchoolId, String yearMonth);
	
	public List<Object[]> selfRegReqParentDetails(Date requestedTimeStart, Date requestedTimeEnd, Boolean sendStatus);
	
	public List<Object[]> allMealsWithOrderedCountByDate(Long mealSchoolId, Date startDate, Date endDate, List<String> grades);
	
	public List<Object[]> allMealsWithOrderedCountByDateV2(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, ItemTypeConstants menuType);
	
	public List<Object[]> caterersReport(Long catererId, Date startDate, Date endDate, ItemTypeConstants menuType, Long mealSchoolId);
	
	public List<Object[]> monthlyMenuDetails(Long mealSchoolId, String yearMonth, String menuType);
	
	public List<Object[]> mealChangeDetailsForSendNotificationToParent(MealChangeNotificationRequest mealChangeNotificationRequest);
	
	public List<String> getMealPublishedGrades(Long mealSchoolId, String yearMonth, List<String> schoolGrades, Boolean autoReminderStatus);
	
	public List<String> getMealPublishedGradesV2(Long mealSchoolId, String yearMonth, List<String> schoolGrades, Boolean autoReminderStatus, ItemTypeConstants menuType);
	
	public List<Object[]> notOrderedStudents(Long mealSchoolId, String yearMonth, List<String> schoolGrades, Integer schoolYear, ItemTypeConstants menuType);
	
	public List<Object[]> mealsBySummaryId(Long mealSummaryId);
	
	public List<Object[]> studentsWithAllergiesDetails(Long mealSchoolId, int schoolYear, List<String> grades);
	
	public List<Object[]> adminUsersEmail(String parentEmail);
	
	public List<Long> mealSchoolIdsByParentEmail(String parentEmail);
	
	public String getLatestYearMonth(List<Long> mealSchoolIds);
	
	public String getLatestYearMonthV2(List<Long> mealSchoolIds);
	
	public List<String> adminUserEmails(String parentUserEmail);
	
	public List<Object[]> monthStudentsByParentEmail(String parentEmail, String currentYearMonth);
	
	public List<Object[]> monthStudentsByParentEmailV2(String parentEmail, String currentYearMonth);
	
	public List<Object[]> transactionsHistory(Long studentRecId, String startDate, String endDate);
	
	public List<Object[]> transactionsReport(Long mealSchoolId, String startDate, String endDate, Boolean isDeposit,Integer schoolYear, Boolean isAdjTrx);
	
	public List<Object[]> onlinePaymetReport(Long districtId, String startDate, String endDate,Integer schoolYear);
	
	public List<Object[]> lowBalanceReport(Long mealSchoolId, Integer schoolYear, Double minLowBal, Double maxLowBal, 
			Boolean isZeroExclude, Double amount, String operator);
	
	public List<Object[]> notOrderedLunchReport(Long mealSchoolId, String yearMonth, List<String> schoolGrades, 
			Integer schoolYear, String previousYearMonth, ItemTypeConstants menuType);
	
	public List<Object[]> studentFmEligibiltyData(Long mealSchoolId, int schoolYear, String eligType,Boolean isTemp,Boolean isDistId);
	
	public List<Object[]> mealsServedCountByElig(Long mealSchoolId, String startDate, String endDate, String itemType, boolean isNeedy);
	
	public Object staffServedMeals(Long mealSchoolId, String startDate, String endDate, String itemType);
	
	public Object otherServedMeals(Long mealSchoolId, String startDate, String endDate, String itemType, String mealType);
	
	public Object alaCarteServed(Long mealSchoolId, String startDate, String endDate, String itemType);
	
	public Object regMealCash(Long mealSchoolId, String startDate, String endDate, String itemType);
	
	public Double paidAmt(Long mealSchoolId, String startDate, String endDate, String trxType);
	
	public List<Object[]> distMealsServedCountByElig(Long districtId, String startDate, String endDate, String itemType, boolean isNeedy);
	
	public Integer mealsServingDays(Long mealSchoolId, String startDate, String endDate, String itemType);
	
	public Integer distMealsServingDays(Long mealSchoolId, String startDate, String endDate, String itemType);
	
	public List<Object[]> dailyAuditCheck(Long mealSchoolId, String itemType, String startDate, String endDate, String timezone);
	
	public List<Object[]> staffDailyAuditCheck(Long mealSchoolId, String itemType, String startDate, String endDate, String timezone);

	public List<Object[]> dailyReimbIncome(Long mealSchoolId, String itemType, String startDate, String endDate, String timezone);
	
	public List<Object[]> dailyIncomeByType(Long mealSchoolId, String itemType, String startDate, String endDate, String timezone, Boolean isStaff);
	
	public List<String> getSchoolHolidays(Long mealSchoolId, String yearMonth, String itemType);
	
	public List<Object[]> packagePaymentsTrx(Long mealSchoolId, Integer schoolYear, String startDate, String endDate, Long stdRecId);
	
	public List<Object[]> payMobTrxCharges(Long mealSchoolId, String startDate, String endDate);
	
	public List<Object[]> orderCostReport(Long mealSchoolId, String startDt, String endDt, ItemTypeConstants menuType, SchoolGrades grade);
	
	public List<Object[]> getCharges(Long mealSchoolId, String startDt, String endDt, String grade, Boolean alcTotal);
	
	public Object[] totalSales(Long mealSchoolId, String startDt, String endDt,Boolean isPrg);
	
	public Double chargedPOS(Long mealSchoolId, String startDt, String endDt);
	
	public Double totPosDeposit(Long mealSchoolId, String startDt, String endDt, Boolean isDirect);
	
	public Double schoolDeposit(Long mealSchoolId, String startDt, String endDt);
	
	//public List<Object[]> packageTrx(Long mealSchoolId, Integer )
}
