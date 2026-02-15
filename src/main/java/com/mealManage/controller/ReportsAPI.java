package com.mealManage.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.mealManage.service.ReportsService;

@RestController
@RequestMapping("mealManage/reports")
/**It's used for API related to orders**/
public class ReportsAPI {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ReportsService reportsService;
	
	/**This API used for get the total count of meal ordered, paid meal ordered, not paid meal ordered and not meal ordered Students by grade
	 * @throws Exception **/
	/*@GetMapping("countByGrade")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public Map<String, CountChildObject> countByGrade(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value = "yearMonth", required = true) String yearMonth, @RequestParam(value="schoolYear", required=true) 
			Integer schoolYear) throws Exception{
		logger.info("Invoking the countByGrade API");
		return reportsService.countByGrade(mealSchoolId, yearMonth, schoolYear);
	}*/
	
	/**This API used for get the total count of meal ordered and not meal ordered Students by grade
	 * @throws Exception **/
	@GetMapping("countByGrade")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public Map<String, CountChildObject> countByGrade(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value = "yearMonth", required = true) String yearMonth, @RequestParam(value="schoolYear", required=true) 
			Integer schoolYear, @RequestParam(value="isVersion2", required=false) Boolean isVersion2, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType) throws Exception{
		logger.info("Invoking the countByGrade API");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		return reportsService.countByGrade(mealSchoolId, yearMonth, schoolYear, isVersion2, menuType);
	}
	
	/**This API used for get the total count of meal ordered and not meal ordered Students by grade
	 * @throws Exception **/
	@GetMapping("countBySchool")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public Map<String, Long> countBySchool(@RequestParam(value="catererId", required = true) Long catererId, @RequestParam(value="isCaterer", required=false) Boolean isCaterer,
			@RequestParam(value = "yearMonth", required = true) String yearMonth, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType) throws Exception{
		logger.info("Invoking the countBySchool API");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		return reportsService.countBySchool(catererId, yearMonth, menuType, isCaterer);
	}
	
	/**This API used for get the items count in caterer report
	 * @throws Exception **/
	@GetMapping("catererReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public CatererReportResp catererReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="startDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam(value="endDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
			@RequestParam(value="grades", required = false) List<String> grades, 
			@RequestParam(value="isVersion2", required=false) Boolean isVersion2, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType) throws Exception{
		logger.info("Invoking the catererReport API");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		return reportsService.catererReport(mealSchoolId, startDate, endDate, grades, isVersion2, menuType);
	}
	
	/**This API used for get the items count for Caterer's report
	 * @throws Exception **/
	@GetMapping("caterersReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> caterersReport(@RequestParam Long catererId,	@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType, 
			@RequestParam(value="mealSchoolId", required=false) Long mealSchoolId) throws Exception{
		logger.info("Invoking API to generate the Caterer's report.");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = reportsService.caterersReport(catererId, startDate, endDate, menuType, mealSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This method used for get the school meals daily/weekly report**/
	@GetMapping("schoolMealReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public SchoolMealReportResp schoolMealReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="startDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam(value="endDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
			@RequestParam(value="grades", required = false) List<SchoolGrades> grades, 
			@RequestParam(value="schoolYear", required=true) Integer schoolYear, @RequestParam(value="isOrder", required=false) Boolean isOrder, 
			@RequestParam(value="isVersion2", required=false) Boolean isVersion2, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType) throws Exception{
		logger.info("Invoking the schoolMealReport API");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		//return reportsService.schoolMealReport(mealSchoolId, startDate, endDate, grades, schoolYear);
		//return reportsService.schoolReport(mealSchoolId, startDate, endDate, grades, schoolYear, null);
		SchoolMealReportResp schoolMealReportResp = reportsService.schoolReport(mealSchoolId, startDate, endDate, grades, schoolYear, null, isOrder, isVersion2, menuType);
		try{
			Map<String, Map<String, List<String>>> dateGradeMapFinal = new TreeMap<String, Map<String, List<String>>>();
			if(schoolMealReportResp.getMealsByGradeAndDate() != null){
				for(Entry<String, Map<String, List<String>>> mealGradeDateEntry : schoolMealReportResp.getMealsByGradeAndDate().entrySet()){
					Map<String, List<String>> dateMealsMap = new TreeMap<String, List<String>>(mealGradeDateEntry.getValue());
					dateGradeMapFinal.put(mealGradeDateEntry.getKey(), dateMealsMap);
				}
				schoolMealReportResp.setMealsByGradeAndDate(dateGradeMapFinal);
			}
		}catch(Exception e){
			logger.error("Failed to generate the school report due to "+e.getMessage());
		}
		return schoolMealReportResp;
	}
	
	/**This API used for get the Caterer daily/weekly order report**/
	@GetMapping("catererOrdersReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> catererOrdersReport(@RequestParam Long catererId, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType, 
			@RequestParam(value="mealSchoolId", required=false) Long mealSchoolId) throws Exception{
		logger.info("Invoking API for generate the Caterer Orders Report.");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = reportsService.catererOrdersReport(catererId, startDate, endDate, menuType, mealSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the ordered meal item report details
	 * @throws Exception **/
	@GetMapping("orderedMealItemsReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public List<OrderedMealItemsReport> orderedMealItemsReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId, 
			@RequestParam(value="startDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam(value="endDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, 
			@RequestParam(value="paymentStatus", required = false) Boolean paymentStatus, @RequestParam(value="grades", required = false)
			List<String> grades) throws Exception{
		logger.info("Invoking the orderedMealItemsReport API");
		return reportsService.orderedMealItemsReport(mealSchoolId, startDate, endDate, paymentStatus, grades);
	}
	
	/**This API used for get the order summary**/
	@GetMapping("orderSummaryReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public List<MealOrderReport> orderSummaryReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId, 
			@RequestParam(value="yearMonths", required = true) List<String> yearMonths, @RequestParam(value="paymentStatus", 
			required = false) Boolean paymentStatus, @RequestParam(value="grades", required = false) List<String> grades, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType) throws Exception{
		logger.info("Invoking the orderSummaryReport API");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		return reportsService.orderSummaryReport(mealSchoolId, yearMonths, paymentStatus, grades, menuType);
	}
	
	/**This API used for generate the excel/pdf file for the caterer report based on passed input parameter fileType. Default it'll generate excel file**/
	@GetMapping("catererExportReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> catererExportReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="startDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam(value="endDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
			@RequestParam(value="grades", required = false) List<String> grades,HttpServletResponse response,
			@RequestParam(value="fileType", required = false) String filetype, @RequestParam(value="catererEmail", required=false) String catererEmail,
			@RequestParam(value="byItem", required=false) Boolean byItem, @RequestParam(value="isVersion2", required=false) Boolean isVersion2, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType){
		logger.info("Invoking the API for generate Excel/Pdf file for caterer Report");
		if(filetype == null || !filetype.equalsIgnoreCase("Pdf"))
			filetype = "Excel";
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = reportsService.catererExportReport(mealSchoolId, startDate, endDate, grades, response, filetype, catererEmail, byItem, isVersion2, menuType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generating excel/pdf file for the school of daily/weekly meals report based on input parameter value fileType. Default it'll generate excel file**/
	@GetMapping("schoolExportReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> schoolExportReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="startDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam(value="endDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,	@RequestParam(value="grades", required = false) 
			List<SchoolGrades> grades,HttpServletResponse response,@RequestParam(value="fileType", required = false) String filetype,
			@RequestParam(value="schoolYear", required=true) Integer schoolYear, @RequestParam(value="isOrder", required=false) Boolean isOrder, 
			@RequestParam(value="isVersion2", required=false) Boolean isVersion2, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType){
		logger.info("Invoking the API for generate the Excel/Pdf file for school report");
		ServiceResponse serviceResponse = null;
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		if(isOrder != null && isOrder)
			serviceResponse = reportsService.orderReportExport(mealSchoolId, startDate, endDate, grades,response, filetype, schoolYear, isVersion2, menuType);
		else{
			if(filetype == null || !filetype.equalsIgnoreCase("Pdf"))
				filetype = "Excel";
			serviceResponse = reportsService.schoolExportReport(mealSchoolId, startDate, endDate, grades,response, filetype, schoolYear, isVersion2, menuType);
		}		
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the details of requested email id regarding parent self registration
	 * @throws Exception **/
	@GetMapping("selfRegReqParentDetails")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public List<SelfRegParentRequestedEmail> selfRegReqParentDetails(@RequestParam(value="requestedTimeStart", required=true) 
		@DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date requestedTimeStart, @RequestParam(value="requestedTimeEnd", required=true) 
		@DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date requestedTimeEnd, @RequestParam(value="sendStatus", required=false) 
			Boolean sendStatus) throws Exception{
		logger.info("Invoking the selfRegReqParentDetails API for get the self registration parent details");
		return reportsService.selfRegReqParentDetails(requestedTimeStart, requestedTimeEnd, sendStatus);
	}
	
	/**This API used for generate the all reports in PDF format of daily/weekly and custom date based on input parameter value**/
	@GetMapping("exportReports")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> exportReports(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="startDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam(value="endDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,	
			@RequestParam(value="grades", required = false) List<SchoolGrades> grades, @RequestParam(value="loggedUser", 
			required=true) String loggedUser, @RequestParam(value="schoolYear", required=true) Integer schoolYear,
			@RequestParam(value="isVersion2", required=false) Boolean isVersion2, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType){
		logger.info("Invoking the API for generate the all reports in PDF format and send the mail to logged user with pdf links");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = reportsService.exportReports(mealSchoolId, startDate, endDate, grades, loggedUser, schoolYear, isVersion2, menuType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the ordered meal count with the grades and meal menu id
	 * @throws Exception **/
	@GetMapping("monthlyMenuDetails")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public Map<String, List<MonthlyMenuDetailsResp>> monthlyMenuDetails(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="orderDate", required = true) String orderDate, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType) throws Exception{
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		logger.info("Invoking the monthlyMenuDetails API");
		return reportsService.monthlyMenuDetails(mealSchoolId, orderDate, menuType);
	}
	
	/**This API used for send the notification to parent when menu change at the last movement of deliver meal**/
	@PostMapping("mealChangeNotification")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> mealChangeNotification(@RequestBody MealChangeNotificationRequest mealChangeNotificationRequest){
		logger.info("Invoking the mealChangeNotification API");
		ServiceResponse serviceResponse = reportsService.mealChangeNotification(mealChangeNotificationRequest);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}	
	
	/**This API used for get the all students for whom meal not ordered yet under the specified month, school and grades**/
	@GetMapping("notOrderedStudents")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public List<NotOrderedStudentResp> notOrderedStudents(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="yearMonth", required = true) String yearMonth, @RequestParam(value="grades", required = false) 
			List<String> grades, @RequestParam(value="schoolYear", required=true) Integer schoolYear, 
			@RequestParam(value="isVersion2", required=false) Boolean isVersion2, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType) throws Exception{
		logger.info("Invoking the notOrderedStudents API for get all the students who haven't ordered meal but meal created for that.");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		return reportsService.notOrderedStudents(mealSchoolId, yearMonth, grades, schoolYear, isVersion2, menuType);
	}
	
	/**This API used for get all the meals in json format like as mealJson API using school meal summary id**/
	@GetMapping("mealsBySummaryId")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public String mealsBySummaryId(@RequestParam(value = "mealSummaryId", required = true) Long mealSummaryId) throws Exception{
		logger.info("Invoking the API for get all the meals by meal summary id");
		return reportsService.mealsBySummaryId(mealSummaryId);
	}
	
	/**This API used for get the status (i.e. paymentReminderEnable, lunchReminderEnable and emailIsSubscribe) of email send notification to parent user
	 * @throws Exception **/
	@GetMapping("emailSendStatus")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public EmailSendResp emailSendStatus(@RequestParam(value="email", required=true) String email) throws Exception{
		logger.info("Invoking the emailSendStatus API for get the status");
		return reportsService.emailSendStatus(email);
	}
	
	/**This API used for generate the Allergies report of Student in Excel & Pdf format based on school year**/
	@GetMapping("allergiesReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> allergiesReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="schoolYear", required = true) int schoolYear, HttpServletResponse response,
			@RequestParam(value="grades", required = false) List<String> grades,	
			@RequestParam(value="fileType", required = false) String filetype){
		logger.info("Invoking the API for generate Excel/Pdf file of Allergies Report");
		/*if(filetype == null || !filetype.equalsIgnoreCase("Pdf"))
			filetype = "Excel";*/
		ServiceResponse serviceResponse = reportsService.allergiesReport(mealSchoolId, schoolYear, response, grades, filetype);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the yearMonth for whom parent can order menu**/
	@GetMapping("getMenuOrderYrMonth")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> getMenuOrderYrMonth(@RequestParam(value="parentEmail", required=true) String parentEmail, 
			@RequestParam(value="isVersion2", required=false) Boolean isVersion2){
		logger.info("Invoking the API for get the year month for whom parent can order menu");
		ServiceResponse serviceResponse = reportsService.getMenuOrderYrMonth(parentEmail, isVersion2);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the Free Meal / Reduced Price meal Eligibility check**/
	@GetMapping("fmSurveyReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> fmSurveyReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="fileType", required = true) String filetype, HttpServletResponse response){
		logger.info("Invoking the API for generate Excel/Pdf file of Free Meal / Reduced Price Eligibility survey");
		if(filetype == null || !filetype.equalsIgnoreCase("Pdf"))
			filetype = "Excel";
		ServiceResponse serviceResponse = reportsService.fmSurveyReport(mealSchoolId, response, filetype);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the Free Meal / Reduced Price meal actual Eligibility check**/
	@GetMapping("fmActualReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> fmActualReport(@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="fileType", required = false) String filetype, @RequestParam(value="isDistId",required=false) Boolean isDistId,
			@RequestParam(value="schoolYear", required=true) int schoolYear, HttpServletResponse response,
			@RequestParam(value="eligType", required=false) String eligType, @RequestParam(value="isTemp",required=false) Boolean isTemp){
		logger.info("Invoking the API for generate Excel/Pdf file of Free Meal / Reduced Price actual Eligibility survey");
		/*if(filetype == null || !filetype.equalsIgnoreCase("Pdf"))
			filetype = "Excel";*/
		ServiceResponse serviceResponse = reportsService.fmActualReport(mealSchoolId, response, filetype, schoolYear,eligType, isTemp,isDistId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the list of month along with student ids which are eligible for the order under that specific month
	 * @throws Exception **/
	@GetMapping("monthAndStudentListByEmail")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public Map<String, List<Long>> monthAndStudentListByEmail(@RequestParam(value="parentEmail", required = true) String parentEmail, 
			@RequestParam(value="currentYearMonth", required = true) String currentYearMonth, @RequestParam(value="isVersion2", required=false) Boolean isVersion2) throws Exception{
		logger.info("Invoking the monthAndStudentListByEmail API for get the student ids and order month using parent email "+parentEmail+
				" currentYearMonth "+currentYearMonth);
		return reportsService.monthAndStudentListByEmail(parentEmail, currentYearMonth, isVersion2);
	}
	
	/**This API used for generate the account transactions history report of student by student record id and date range
	 *  in pdf file format or Json response**/
	@GetMapping("transactionsHistory")
	public ResponseEntity<ServiceResponse> transactionsHistory(@RequestParam(value="studentRecId", required=true) 
		Long studentRecId, @RequestParam(value="startDate", required = true) String startDate, 
		@RequestParam(value="endDate", required = true) String endDate,	HttpServletResponse response, 
		@RequestParam(value="fileExport", required=false) Boolean fileExport){
		logger.info("Invoking API for generate the transaction history report with params: studentRecId="+studentRecId+
				", startDate="+startDate+", endDate="+endDate+", fileExport="+fileExport);
		ServiceResponse serviceResponse = reportsService.transactionsHistory(studentRecId, startDate, endDate, response, 
				fileExport);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the deposit/purchase transactions report by school and date range in pdf file format and json response**/
	@GetMapping("transactionsReport")
	public ResponseEntity<ServiceResponse> transactionsReport(@RequestParam(value="mealSchoolId", required=true) 
		Long mealSchoolId, @RequestParam(value="startDate", required = true) String startDate, 
		@RequestParam(value="endDate", required = true) String endDate,	HttpServletResponse response, 
		@RequestParam(value="fileExport", required=false) Boolean fileExport, 
		@RequestParam(value="isDeposit", required=true) Boolean isDeposit, @RequestParam Integer schoolYear, @RequestParam(value="isAdjTrx", required=false) Boolean isAdjTrx){
		logger.info("Invoking API for generate the deposit/purchase transactions report with params: mealSchoolId="+mealSchoolId+
				", startDate="+startDate+", endDate="+endDate+", fileExport="+fileExport+", isDeposit="+isDeposit+". schoolYear:"+schoolYear);
		ServiceResponse serviceResponse = reportsService.transactionsReport(mealSchoolId, startDate, endDate, response, 
				fileExport, isDeposit,schoolYear,isAdjTrx);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the online report by school and date range in pdf file format**/
	@GetMapping("onlinePaymetReport")
	public ResponseEntity<ServiceResponse> onlinePaymetReport(@RequestParam Long districtId, @RequestParam(value="startDate", required = true) String startDate, 
		@RequestParam(value="endDate", required = true) String endDate,	HttpServletResponse response, @RequestParam Integer schoolYear){
		logger.info("Invoking API for generate the online payment report with params: districtId="+districtId+
				", startDate="+startDate+", endDate="+endDate+". schoolYear:"+schoolYear);
		ServiceResponse serviceResponse = reportsService.onlinePaymetReport(districtId, startDate, endDate, response, 
				schoolYear);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the low balance student's report by school and date range in pdf/json response**/
	@GetMapping("lowBalanceReport")
	public ResponseEntity<ServiceResponse> lowBalanceReport(@RequestParam(value="mealSchoolId", required=true) 
		Long mealSchoolId, HttpServletResponse response, @RequestParam(value="fileExport", required=true) Boolean fileExport, 
			@RequestParam(value="schoolYear", required=true) Integer schoolYear, 
			@RequestParam(value="amount",required=false) Double amount, @RequestParam(value="operator",required=false) String operator){
		logger.info("Invoking API for generate the low balance students report with params: mealSchoolId="+mealSchoolId+
				", fileExport="+fileExport+", schoolYear="+schoolYear);
		ServiceResponse serviceResponse = reportsService.lowBalanceReport(mealSchoolId, response, fileExport, schoolYear,amount,operator);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the report for those students who haven't order lunch for current month but it's having
	 * order in previous month**/
	@GetMapping("notOrderedLunchReport")
	public ResponseEntity<ServiceResponse> notOrderedLunchReport(@RequestParam(value="mealSchoolId", required=true) 
		Long mealSchoolId, @RequestParam(value="yearMonth", required=true) String yearMonth, 
		@RequestParam(value="schoolYear", required=true) Integer schoolYear, HttpServletResponse response, 
		@RequestParam(value="fileType", required=false) String fileType, @RequestParam(value="isVersion2", required=false) Boolean isVersion2, 
		@RequestParam(value="menuType", required=false) ItemTypeConstants menuType){
		logger.info("Invoking API for generate the notOrderedLunchReport who don't have order in current month but having in previous month");
		if(fileType == null || !fileType.equalsIgnoreCase("Pdf"))
			fileType = "Excel";
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = reportsService.notOrderedLunchReport(mealSchoolId, yearMonth, schoolYear, 
				response, fileType, isVersion2, menuType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the pdf report of student by student record id who come under the Low Balance Criteria**/
	@GetMapping("lowBalanceStudentDetailsReport")
	public ResponseEntity<ServiceResponse> lowBalanceStudentDetailsReport(@RequestParam(value="studentRecId", required=true) 
			Long studentRecId, @RequestParam(value="startDate", required = true) String startDate, 
			@RequestParam(value="endDate", required = true) String endDate,	HttpServletResponse response){
		logger.info("Invoking API for generate the low balance student details report by record id: "+studentRecId+
				", start date: "+startDate+", end date: "+endDate);
		ServiceResponse serviceResponse = reportsService.lowBalanceStudentDetailsReport(studentRecId, startDate, endDate, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for audit report generation itemType should be like Lunch, Breakfast, Milk**/
	@GetMapping("generateAuditReport")
	public ResponseEntity<ServiceResponse> generateAuditReport(@RequestParam long mealSchoolId, 
			@RequestParam String startDate, @RequestParam(value="endDate", required=false) String endDate, 
			@RequestParam String itemType, @RequestParam Integer schoolYear, HttpServletResponse response){
		logger.info("Invoking API for generate the audit report with following critria mealSchoolId:"+mealSchoolId+
				", startDate:"+startDate+", endDate:"+endDate);
		ServiceResponse serviceResponse = reportsService.generateAuditReport(mealSchoolId, startDate, endDate, 
				itemType, schoolYear, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for basic claim report generation**/
	@GetMapping("basicClaimReport")
	public ResponseEntity<ServiceResponse> basicClaimReport(@RequestParam long districtId, 
			@RequestParam String startDate, @RequestParam(value="endDate", required=false) String endDate,
			@RequestParam Integer schoolYear, HttpServletResponse response){
		logger.info("Invoking API for generate the basic claim report with following critria districtId:"+districtId+
				", startDate:"+startDate+", endDate:"+endDate);
		ServiceResponse serviceResponse = reportsService.basicClaimReport(districtId, startDate, endDate, 
				schoolYear, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for audit edit check report generation**/
	@GetMapping("auditDailyEditCheck")
	public ResponseEntity<ServiceResponse> auditDailyEditCheck(@RequestParam long mealSchoolId, @RequestParam(value="districtId",required=false) Long districtId,
			@RequestParam String yearMonth,	@RequestParam String itemType, @RequestParam Integer schoolYear, 
			@RequestParam String startDate, @RequestParam String endDate, HttpServletResponse response){
		ServiceResponse serviceResponse = null;
		logger.info("Invoking API for generate the audit report with following critria mealSchoolId:"+mealSchoolId+
				", yearMonth:"+yearMonth+" and districtId::"+districtId);
		if(mealSchoolId != 0)
			serviceResponse = reportsService.auditDailyEditCheck(mealSchoolId, yearMonth, itemType, 
				schoolYear, startDate, endDate, response);
		else
			serviceResponse = reportsService.auditDailyEditCheckDist(districtId, yearMonth, itemType, 
					schoolYear, startDate, endDate, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for Eligibility Summary report generation**/
	@GetMapping("eligibilitySummary")
	public ResponseEntity<ServiceResponse> eligibilitySummary(@RequestParam Long districtId, 
			@RequestParam String currentDate, @RequestParam(value="isExport", required=false) Boolean isExport, HttpServletResponse response, @RequestParam(value="isSchoolId",required=false) Boolean isSchoolId){
		logger.info("Invoking API to generate the eligibility summary report for districtId:"+districtId+
				", currentDate:"+currentDate);
		ServiceResponse serviceResponse = reportsService.eligibilitySummary(districtId, currentDate, isExport, response,isSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the events report**/
	@GetMapping("eventsReport")
	public ResponseEntity<ServiceResponse> eventsReport(@RequestParam Long mealSchoolId, @RequestParam Integer schoolYear, 
			@RequestParam String startDate, @RequestParam String endDate, @RequestParam(value="isExport", required=false) Boolean isExport, HttpServletResponse httpResp){
		logger.info("Invoking API for generate the events report with mealSchoolId::"+mealSchoolId+" and schoolYear::"+schoolYear+" and startDate::"+startDate+" and toDate::"+endDate);
		ServiceResponse serviceResponse = reportsService.eventsReport(mealSchoolId, schoolYear, startDate, endDate, isExport, httpResp);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the BCAC Subscriptions details**/
	@GetMapping("bcacSubscriptions")
	public ResponseEntity<ServiceResponse> bcacSubscriptions(@RequestParam("mealSchoolId") Long mealSchoolId, HttpServletResponse response,
			@RequestParam("subscribeDate") String subscribeDate, @RequestParam(value="isExport", required=false) Boolean isExport){
		logger.info("Invoking API for get the BC AC subscription details with mealSchoolId::"+mealSchoolId+" and subscribeDate::"+subscribeDate);
		ServiceResponse serviceResponse = reportsService.bcacSubscriptions(mealSchoolId, subscribeDate, isExport, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the parent's related kids subscribed packages which end after current date**/
	@GetMapping("bcacKidsSubsPkg")
	public ResponseEntity<ServiceResponse> bcacKidsSubsPkg(@RequestParam Long mealSchoolId, @RequestParam String parentEmail, 
			@RequestParam String currentDate){
		logger.info("Invoking API to get the BCAC subscribed packages which not expired by parent::"+parentEmail+" and mealSchoolId::"+mealSchoolId+" and currentDt::"+currentDate);
		ServiceResponse serviceResponse = reportsService.bcacKidsSubsPkg(mealSchoolId, parentEmail, currentDate);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the BCAC subscribed packages deposit transactions**/
	@GetMapping("packagesTrx")
	public ResponseEntity<ServiceResponse> packagesTrx(@RequestParam Long mealSchoolId, @RequestParam Integer schoolYear, HttpServletResponse response ,
			@RequestParam String startDate, @RequestParam String endDate, @RequestParam(value="isExport", required=false) Boolean isExport, 
			@RequestParam(value="stdRecId", required=false) Long stdRecId){
		logger.info("Invoking API to get the packages transactions info for mealSchoolId::"+mealSchoolId+" and schoolYear::"+schoolYear+" and stdRecId::"+stdRecId);
		ServiceResponse serviceResponse = reportsService.packagesTrx(mealSchoolId, schoolYear, startDate, endDate, isExport, response, stdRecId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the paymob transaction charges report**/
	@GetMapping("payMobTrxCharges")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> payMobTrxCharges(@RequestParam Long mealSchoolId, @RequestParam String startDate, 
			@RequestParam String endDate, @RequestParam(value="isExport", required=false) Boolean isExport, HttpServletResponse response){
		logger.info("Invoking API to generate the paymob transaction charges report for mealSchoolId::"+mealSchoolId+" and startDt::"+startDate+" and endDt::"+endDate);
		ServiceResponse serviceResponse = reportsService.payMobTrxCharges(mealSchoolId, startDate, endDate, isExport, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the daily order cost report**/
	@GetMapping("orderCostReport")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> orderCostReport(@RequestParam Long mealSchoolId, @RequestParam String startDate, @RequestParam String endDate, 
			@RequestParam(value="menuType", required=false) ItemTypeConstants menuType, @RequestParam(value="isExport", required=false) Boolean isExport, HttpServletResponse response, 
			@RequestParam(value="grade", required=false) SchoolGrades grade){
		logger.info("Invoking API for generate the order cost report");
		if(isExport == null)
			isExport = false;
		ServiceResponse serviceResponse = reportsService.orderCostReport(mealSchoolId, startDate, endDate, menuType, isExport, response, grade);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get all the details based on package master transaction record id**/
	@GetMapping("pkgTrxInfo/{pkgMasterTrxId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> pkgTrxInfo(@PathVariable Long pkgMasterTrxId){
		logger.info("Invoking API for get the package transactions info based on master trx id::"+pkgMasterTrxId);
		ServiceResponse serviceResponse = reportsService.pkgTrxInfo(pkgMasterTrxId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the district dashboard data**/
	@GetMapping("districtDashboard/{districtId}")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_DISTRICT')")
	public ResponseEntity<ServiceResponse> districtDashboard(@PathVariable Long districtId, @RequestParam String currentDate){
		logger.info("Invoking API to get the district dashboard data for districtId::"+districtId);
		ServiceResponse serviceResponse = reportsService.districtDashboard(districtId, currentDate);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the revenue report**/
	@GetMapping("revenueReport/{schoolId}")
	public ResponseEntity<ServiceResponse> revenueReport(@PathVariable Long schoolId, @RequestParam String startDate, 
			@RequestParam String endDate, @RequestParam Integer schoolYear, HttpServletResponse response){
		logger.info("Invoking API for generate the revenue report for schoolId::"+schoolId);
		ServiceResponse serviceResponse = reportsService.revenueReport(schoolId, schoolYear, startDate, endDate, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the Income report**/
	@GetMapping("incomeReport/{schoolId}")
	public ResponseEntity<ServiceResponse> incomeReport(@PathVariable Long schoolId, @RequestParam String startDate, 
			@RequestParam String endDate, @RequestParam String itemType, String yearMonth, HttpServletResponse response){
		logger.info("Invoking API for generate the income report.");
		ServiceResponse serviceResponse = reportsService.incomeReport(schoolId, itemType, yearMonth, startDate, endDate, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for generate the account balance summary report**/
	@GetMapping("accBalanceSummary/{id}")
	public ResponseEntity<ServiceResponse> accBalanceSummary(@PathVariable Long id, @RequestParam Boolean isDistrict, 
			HttpServletResponse resp, @RequestParam(value="isExport", required=false) Boolean isExport, @RequestParam(value="schoolYear", required=false) Integer schoolYear, @RequestParam(value="dateV", required=false) String dateV){
		logger.info("Invoking API for generate the account balance summary report with id:"+id+" and isDistrict::"+isDistrict+" and schoolYear::"+schoolYear);
		ServiceResponse serviceResponse = reportsService.accBalanceSummary(id, isDistrict, resp, isExport,schoolYear, dateV);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
}
