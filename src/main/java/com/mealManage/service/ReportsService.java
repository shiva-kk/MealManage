package com.mealManage.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.mealManage.domain.MealChangeNotificationRequest;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.response.CatererReportResp;
import com.mealManage.response.CountChildObject;
import com.mealManage.response.EmailSendResp;
import com.mealManage.response.MealOrderReport;
import com.mealManage.response.MonthlyMenuDetailsResp;
import com.mealManage.response.NotOrderedStudentResp;
import com.mealManage.response.OrderedMealItemsReport;
import com.mealManage.response.SchoolMealReportResp;
import com.mealManage.response.SelfRegParentRequestedEmail;
import com.mealManage.response.ServiceResponse;

public interface ReportsService {

	
	public Map<String, CountChildObject> countByGrade(Long mealSchoolId, String yearMonth, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType) throws Exception;
	
	public Map<String, Long> countBySchool(Long catererId, String yearMonth, ItemTypeConstants menuType, Boolean isCaterer) throws Exception;
	
	public CatererReportResp catererReport(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, Boolean isVersion2, ItemTypeConstants menuType) throws Exception;
	
	public ServiceResponse caterersReport(Long catererId, Date startDate, Date endDate, ItemTypeConstants menuType, Long mealSchoolId) throws Exception;
	
	//public SchoolMealReportResp schoolMealReport(Long mealSchoolId, Date startDate, Date endDate, List<SchoolGrades> grades, Integer schoolYear) throws Exception;
	
	public SchoolMealReportResp schoolReport(Long mealSchoolId, Date startDate, Date endDate, List<SchoolGrades> grades, 
			Integer schoolYear, String fileType, Boolean isOrder, Boolean isVersion2, ItemTypeConstants menuType) throws Exception;
	
	public ServiceResponse catererOrdersReport(Long catererId, Date startDate, Date endDate, ItemTypeConstants menuType, Long mealSchoolId);
	
	public List<OrderedMealItemsReport> orderedMealItemsReport(Long mealSchoolId, Date startDate, Date endDate, Boolean paymentStatus, List<String> grades) throws Exception;
	
	public List<MealOrderReport> orderSummaryReport(Long mealSchoolId, List<String> yearMonths, Boolean paymentStatus, List<String> grades, ItemTypeConstants menuType) throws Exception;
	
	public ServiceResponse catererExportReport(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, 
			HttpServletResponse response, String fileType, String catererEmail, Boolean byItem, Boolean isVersion2, ItemTypeConstants menuType);

	public ServiceResponse schoolExportReport(Long mealSchoolId, Date startDate, Date endDate,	List<SchoolGrades> grades,
			HttpServletResponse response, String fileType, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType);
	
	public ServiceResponse orderReportExport(Long mealSchoolId, Date startDate, Date endDate,	List<SchoolGrades> grades,
			HttpServletResponse response, String fileType, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType);
	
	public List<SelfRegParentRequestedEmail> selfRegReqParentDetails(Date requestedTimeStart, Date requestedTimeEnd, Boolean sendStatus) throws Exception;
	
	public ServiceResponse exportReports(Long mealSchoolId, Date startDate, Date endDate, List<SchoolGrades> grades, String loggedUser, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType);
	
	public Map<String, List<MonthlyMenuDetailsResp>> monthlyMenuDetails(Long mealSchoolId, String yearMonth, ItemTypeConstants menuType) throws Exception;
	
	public ServiceResponse mealChangeNotification(MealChangeNotificationRequest mealChangeNotificationRequest);
	
	public List<NotOrderedStudentResp> notOrderedStudents(Long mealSchoolId, String yearMonth, List<String> grades, Integer schoolYear, Boolean isVersion2, ItemTypeConstants menuType) throws Exception;
	
	public String mealsBySummaryId(Long mealSummaryId) throws Exception;
	
	public EmailSendResp emailSendStatus(String email) throws Exception;
	
	public ServiceResponse allergiesReport(Long mealSchoolId, int schoolYear, HttpServletResponse response, List<String> grades, 
			String fileType);
	
	public ServiceResponse getMenuOrderYrMonth(String parentEmail, Boolean isVersion2);
	
	public ServiceResponse fmSurveyReport(Long mealSchoolId, HttpServletResponse response, String fileType);
	
	public ServiceResponse fmActualReport(Long mealSchoolId, HttpServletResponse response, String fileType, int schoolYear, String eligType, Boolean isTemp,Boolean isDistId);
	
	public Map<String, List<Long>> monthAndStudentListByEmail(String parentEmail, String currentYearMonth, Boolean isVersion2) throws Exception;
	
	public ServiceResponse transactionsHistory(Long studentRecId, String startDate, String endDate, 
			HttpServletResponse response, Boolean fileExport);
	
	public ServiceResponse transactionsReport(Long mealSchoolId, String startDate, String endDate, 
			HttpServletResponse response, Boolean fileExport, Boolean isDeposit, Integer schoolYear,Boolean isAdjTrx);

	public ServiceResponse onlinePaymetReport(Long districtId, String startDate, String endDate, 
			HttpServletResponse response, Integer schoolYear);
	
	public ServiceResponse lowBalanceReport(Long mealSchoolId, HttpServletResponse response, Boolean fileExport, 
			Integer schoolYear, Double amount, String operator);
	
	public ServiceResponse notOrderedLunchReport(Long mealSchoolId, String yearMonth, Integer schoolYear, 
			HttpServletResponse response, String fileType, Boolean isVersion2, ItemTypeConstants menuType);
	
	public ServiceResponse lowBalanceStudentDetailsReport(Long studentRecId, String startDate, String endDate, 
			HttpServletResponse response);
	
	public ServiceResponse generateAuditReport(Long mealSchoolId, String startDate, String endDate, String itemType, 
			Integer schoolYear, HttpServletResponse response);
	
	public ServiceResponse basicClaimReport(Long districtId, String startDate, String endDate, 
			Integer schoolYear, HttpServletResponse response);
	
	public ServiceResponse auditDailyEditCheck(Long mealSchoolId, String yearMonth, String itemType, Integer schoolYear, 
			String startDate, String endDate, HttpServletResponse response);
	
	public ServiceResponse auditDailyEditCheckDist(Long distictId, String yearMonth, String itemType, Integer schoolYear, 
			String startDate, String endDate, HttpServletResponse response);
	
	public ServiceResponse eligibilitySummary(Long districtId, String currentDate, Boolean isExport, HttpServletResponse response,Boolean isSchoolId);
	
	public ServiceResponse eventsReport(Long mealSchoolId, Integer schoolYear, String startDate, String endDate, Boolean isExport, HttpServletResponse hhtpResp);
	
	public ServiceResponse bcacSubscriptions(Long mealSchoolId, String subscribeDate, Boolean isExport, HttpServletResponse response);
	
	public ServiceResponse bcacKidsSubsPkg(Long mealSchoolId, String parentEmail, String currentDate);
	
	public ServiceResponse packagesTrx(Long mealSchoolId, Integer schoolYear, String startDate, String endDate, 
			Boolean isExport, HttpServletResponse response, Long stdRecId);
	
	public ServiceResponse payMobTrxCharges(Long mealSchoolId, String startDate, String endDate, Boolean isExport, HttpServletResponse response);

	public ServiceResponse orderCostReport(Long mealSchoolId, String startDt, String endDt, ItemTypeConstants menuType, Boolean isExport, HttpServletResponse response, SchoolGrades grade);
	
	public ServiceResponse pkgTrxInfo(Long pkgMasterTrxId);
	
	public ServiceResponse districtDashboard(Long districtId, String currentDate);
	
	public ServiceResponse revenueReport(Long schoolId, Integer schoolYear, String startDate, String endDate, HttpServletResponse response);
	
	public ServiceResponse incomeReport(Long schoolId, String itemType, String yearMonth, String startDate, String endDate, HttpServletResponse response);
	
	public ServiceResponse accBalanceSummary(Long id, Boolean isDistrict, HttpServletResponse resp, Boolean isExport, Integer schoolYear, String dateV);
}
