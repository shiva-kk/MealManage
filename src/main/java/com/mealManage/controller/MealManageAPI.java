package com.mealManage.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.mealManage.mealmodel.user.*;
import com.mealManage.service.LeadManagementService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import com.mealManage.domain.FreeReducedLunchEligReq;
import com.mealManage.domain.ParentsNotificationRequest;
import com.mealManage.domain.PkgNotificationSetting;
import com.mealManage.domain.StudentMealOrders;
import com.mealManage.domain.StudentMealOrdersV2;
import com.mealManage.domain.TierInfo;
import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.school.DataSyncFieldConstants;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.ModuleInfo;
import com.mealManage.mealmodel.school.ReportsByCategory;
import com.mealManage.mealmodel.school.SchoolTimezone;
import com.mealManage.mealmodel.school.SchoolType;
import com.mealManage.mealmodel.school.SchoolYear;
import com.mealManage.mealschedule.model.MenuSummaryDetailDTO;
import com.mealManage.response.OrderStatusResp;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;
import com.mealManage.service.MMDataSyncService;
import com.mealManage.service.MealManageAPIService;

@RestController
@RequestMapping("mealManage")
/**This used for MealManage APIs**/
public class MealManageAPI {
	
	@Autowired
	private MealManageAPIService mealManageAPIService;
	@Autowired
	private MMDataSyncService mmDataSyncService;
	@Autowired
	private SchoolYearRepository schoolYearRepository;

	@Autowired
	private LeadManagementService leadManagementService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**This API used for provide the details to school admin user to activate the school admin account**/
	@GetMapping("adminAccActivationInfo")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> adminAccActivationInfo(@RequestParam(value="schoolId", required=false) Long schoolId,
			@RequestParam(value="loggedUser", required=false) String loggedUser,
			@RequestParam(value="schoolUserName", required=false) String schoolUserName){
		logger.info("Calling adminAccActivationInfo API.");
		ServiceResponse serviceResponse= mealManageAPIService.adminAccActivationInfo(schoolId, loggedUser, schoolUserName);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for send notification**/
	@GetMapping("catererAccActivation")
	@PreAuthorize("hasAuthority('ROLE_CATERER') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> catererAccActivation(@RequestParam String username){
		logger.info("Invoking API to send the account activation link of the Caterer user:: "+username);
		ServiceResponse serviceResponse= mealManageAPIService.catererAccActivation(username);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for send notification**/
	@GetMapping("districtAccActivation")
	@PreAuthorize("hasAuthority('ROLE_DISTRICT') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> districtAccActivation(@RequestParam String username){
		logger.info("Invoking API to send the account activation link of the District user:: "+username);
		ServiceResponse serviceResponse= mealManageAPIService.districtAccActivation(username);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for insert the Student users list**/
	@PostMapping("students")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<StudentCreateResp> students(@RequestBody List<StudentUser> students, @RequestParam(value="mealSchoolId") Long mealSchoolId){
		logger.info("This API invoke for insert the list of students.");
		StudentCreateResp studentCreateResp = mealManageAPIService.students(students, mealSchoolId);
		return new ResponseEntity<StudentCreateResp>(studentCreateResp, HttpStatus.valueOf(studentCreateResp.getStatusCode()));
	}
	
	/**This method used for import the excel file data into student users**/
	@PostMapping("importStudents")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<StudentCreateResp> importStudents(@RequestPart(value = "file") MultipartFile multiPartFile, 
			@RequestParam(value="schoolId") Long mealSchoolId,@RequestParam(value="loggedUser") String loggedUser, 
			@RequestParam(value="schoolYear", required=true) Integer schoolYear){
		StudentCreateResp studentCreateResp = mealManageAPIService.importStudents(multiPartFile, mealSchoolId, loggedUser, schoolYear);
		return new ResponseEntity<StudentCreateResp>(studentCreateResp, HttpStatus.valueOf(studentCreateResp.getStatusCode()));
	}
	
	/**This method used for import the excel file data into student users**/
	@PostMapping("importCertification")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> importCertification(@RequestPart(value = "file") MultipartFile multiPartFile, 
			@RequestParam(value="schoolId") Long mealSchoolId,@RequestParam(value="schoolYear", required=true) Integer schoolYear){
		ServiceResponse serviceResponse = mealManageAPIService.importCertification(multiPartFile, mealSchoolId, schoolYear);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API created for send the notification to registered parent email id for parent signup or any meal update reminder**/
	@PostMapping("sendNotificationParents")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> sendParentsNotificationInfo(@RequestBody ParentsNotificationRequest parentsNotificationRequest) throws Exception{
		logger.info("Invoking sendNotificationParents API.");
		ServiceResponse serviceResponse = mealManageAPIService.sendNotificationParents(parentsNotificationRequest, false);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for order the meal to a students**/
	@PostMapping("orderMenu")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> orderMenu(@RequestBody List<StudentMealOrders> studentMealOrders, 
			@RequestParam("loggedUser") String loggedUser, @RequestParam("yearMonth") String yearMonth){
		logger.info("Invoking the orderMenu API for add/update the menu order to Students");
		ServiceResponse serviceResponse= mealManageAPIService.orderMenu(studentMealOrders, loggedUser, yearMonth);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for order the meal to a students**/
	@PostMapping("v2/orderMenu")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> orderMenuV2(@RequestBody StudentMealOrdersV2 mealOrders, 
			@RequestParam("loggedUser") String loggedUser, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType){
		logger.info("Invoking the orderMenu API for add/update the menu order to Students for studentRecId::"+mealOrders.getStudentId()+" and menuType::"+menuType);
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse= mealManageAPIService.orderMenuV2(mealOrders, loggedUser, menuType);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for refresh the ordered menu**/
	@GetMapping("v2/refreshOrderPdf")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> refreshOrderPdf(@RequestParam Long orderId){
		logger.info("Invoking the API for refresh the ordered menu for orderId::"+orderId);
		ServiceResponse serviceResponse= mealManageAPIService.refreshOrderPdf(orderId);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for update the payment status of order**/
	@PostMapping("orderPayment")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> orderPayment(@RequestBody List<Long> studentRecIds, 
			@RequestParam("loggedUser") String loggedUser, @RequestParam("yearMonth") String yearMonth){
		logger.info("Invoking the orderPayment API for update the order payment staus");
		ServiceResponse serviceResponse= mealManageAPIService.orderPayment(studentRecIds, loggedUser, yearMonth);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for update the MealSchool and admin user**/
	@PostMapping("mealSchoolUpdate")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_DISTRICT')")
	public ResponseEntity<ServiceResponse> mealSchoolUpdate(@RequestBody MealSchool mealSchool, 
			@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId){
		logger.info("Invoking the mealSchoolUpdate API for update the MealSchool & user");
		ServiceResponse serviceResponse= mealManageAPIService.mealSchoolUpdate(mealSchool, mealSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for inActivate the school user**/
	@GetMapping("enableDisableSchoolUser")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_DISTRICT')")
	public ResponseEntity<ServiceResponse> enableDisableSchoolUser(@RequestParam(value="schoolUserName", required = true) String schoolUserName, 
			@RequestParam(value="activeStatus", required = true) Boolean activeStatus,
			@RequestParam(value="loggedUser", required = false) String loggedUser){
		logger.info("Invoking the enableDisableSchoolUser API for activate / inActivate the school admin user");
		ServiceResponse serviceResponse= mealManageAPIService.enableDisableSchoolUser(schoolUserName, activeStatus, loggedUser);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for inActivate the school user**/
	@DeleteMapping("enableDisableDistrictUser")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_DISTRICT')")
	public ResponseEntity<ServiceResponse> enableDisableDistrictUser(@RequestParam(value="userName", required = true) String userName, 
			@RequestParam(value="activeStatus", required = true) Boolean activeStatus,
			@RequestParam(value="loggedUser", required = false) String loggedUser){
		logger.info("Invoking the enableDisableDistrictUser API for activate / inActivate the District admin user");
		ServiceResponse serviceResponse= mealManageAPIService.enableDisableDistrictUser(userName, activeStatus, loggedUser);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for update the Student user**/
	@PostMapping("studentUpdate")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> studentUpdate(@RequestBody StudentUser studentUser, 
			@RequestParam(value="studentRecId", required = true) Long studentRecId){
		logger.info("Invoking the studentUpdate API for update the Studenyt User");
		ServiceResponse serviceResponse = mealManageAPIService.studentUpdate(studentUser, studentRecId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for upload the logo file to S3 bucket and attach the reference link to the Meal School**/
	@PostMapping("uploadMealSchoolLogo")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> uploadMealSchoolLogo(@RequestPart(value = "file", required = true) MultipartFile logoFile, 
			@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId,
			@RequestParam(value="loggedUser", required = false) String loggedUser){
		logger.info("Invoking the API for upload the Meal School Logo");
		ServiceResponse serviceResponse = mealManageAPIService.uploadMealSchoolLogo(logoFile, mealSchoolId, loggedUser);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the status on Meal menu and menu ordered**/
	@GetMapping("menuOrderStatus")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public OrderStatusResp menuOrderStatus(@RequestParam(value="studentRecId", required=true) Long studentRecId, 
			@RequestParam(value="yearMonth", required=true) String yearMonth){
		logger.info("Invoking the menuOrderStatus API");
		return mealManageAPIService.menuOrderStatus(studentRecId, yearMonth);
	}
	
	/**This API used for get the status on Meal menu and menu ordered**/
	@GetMapping("v2/menuOrderStatus")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public OrderStatusResp menuOrderStatusV2(@RequestParam(value="studentRecId", required=true) Long studentRecId, 
			@RequestParam(value="yearMonth", required=true) String yearMonth){
		logger.info("Invoking the menuOrderStatus API");
		return mealManageAPIService.menuOrderStatusV2(studentRecId, yearMonth);
	}
	
	/**This API used for import the Holiday file to a school**/
	@PostMapping("importHolidays")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> importHolidays(@RequestPart(value = "file", required=true) MultipartFile multiPartFile, 
			@RequestParam(value="mealSchoolId", required = true) Long mealSchoolId, @RequestParam(value="loggedUser", required=false) String loggedUser){
		logger.info("Invoking the API for import the holiday data of school using excel file");
		ServiceResponse serviceResponse = mealManageAPIService.importHolidays(multiPartFile, mealSchoolId, loggedUser);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for import the Holiday file to a school**/
	@PostMapping("createHolidays")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> createHolidays(@RequestBody List<SchoolHoliday> schoolHolidays, 
			@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, @RequestParam(value="loggedUser", required=false) String loggedUser){
		logger.info("Invoking the API for create the holidays of school");
		ServiceResponse serviceResponse = mealManageAPIService.createHolidays(schoolHolidays, mealSchoolId, loggedUser);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get all the holiday list of school between the start & end date
	 * @throws Exception **/
	@GetMapping("schoolHolidays")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public List<SchoolHoliday> schoolHolidays(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, @RequestParam(value="startDate",
	required=true) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam(value="endDate", required = true) 
	 @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) throws Exception{
		logger.info("Invoking the API for get all school holidays list");
		return mealManageAPIService.schoolHolidays(mealSchoolId, startDate, endDate);
	}
	
	/**This API used for update the status of email (i.e. paymentReminderEnable, lunchReminderEnable and emailIsSubscribe) regarding send email to parent**/
	@GetMapping("updateEmailStatus")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> updateEmailStatus(@RequestParam(value="email", required=true) String email,
			@RequestParam(value="paymentReminderEnable", required=false) Boolean paymentReminderEnable,
			@RequestParam(value="lunchReminderEnable", required=false) Boolean lunchReminderEnable,
			@RequestParam(value="emailIsSubscribe", required=false) Boolean emailIsSubscribe){
		logger.info("Invoking updateEmailStatus API for update the status");
		ServiceResponse serviceResponse = mealManageAPIService.updateEmailStatus(email, paymentReminderEnable, 
				lunchReminderEnable, emailIsSubscribe);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for storing the DemoRequest Details in the database**/
	/*@PostMapping("demoRequest")
	public void demoRequest(@RequestBody DemoRequest requestDemo, 
	@RequestParam(value="firstName", required = true) String firstName,
	@RequestParam(value="lastName", required = true) String lastName,
	@RequestParam(value="schoolName", required = true) String schoolName,
	@RequestParam(value="emailAddress", required = true)String emailAddress){
		logger.info("Invoking the API for storing the DemoRequest Details in the database");
		mealManageAPIService.demoRequest(firstName, lastName, schoolName, emailAddress);
	}*/
	
	/**This API used for get the available timezone for the school**/
	@GetMapping("availableTimezones")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public Map<String, String> availableTimezones(){
		logger.info("API executing for get all the available timezones");
		Map<String, String> timeZoneMap = new HashMap<String, String>();
		for(SchoolTimezone schoolTimezone : SchoolTimezone.values()){
			timeZoneMap.put(schoolTimezone.name().toString(), schoolTimezone.desc().toString());
		}
		return timeZoneMap;
	}
	
	/**This API used for save the data sync file field mapping details**/
	@PostMapping("dataSyncFieldMapping")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> dataSyncFieldMapping(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestBody Map<DataSyncFieldConstants, String> fileFieldMapping){
		logger.info("Invoking the API for update the School's Data sync file field mapping in backend");
		ServiceResponse serviceResponse = mmDataSyncService.dataSyncFieldMapping(mealSchoolId, fileFieldMapping);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the Data Sync fiele's field mapping details by meal school id**/
	@GetMapping("dataSyncFieldMapping")
	public Map<DataSyncFieldConstants, String> getDataSyncFieldMapping(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId){
		logger.info("Invoking the API for get the Data Sync file's field mapping details");
		return mmDataSyncService.getDataSyncFieldMapping(mealSchoolId);
	}
	
	/**This API used for get all the schools details along with session by parent email id**/
	@GetMapping("schoolInfoByParentEmail")
	public ResponseEntity<ServiceResponse> schoolInfoByParentEmail(@RequestParam(value="parentEmail", required = true) 
			String parentEmail, @RequestParam(value="currentDate", required=false) String currentDate, @RequestParam(value="isVersion2", required=false) Boolean isVersion2){
		logger.info("Invoking API for get the school details along with other info by parent email");
		ServiceResponse serviceResponse = mealManageAPIService.schoolInfoByParentEmail(parentEmail, currentDate, isVersion2);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get all admin user emails for send the notification by meal school id**/
	@GetMapping("adminEmailsBySchoolId")
	public List<String> adminEmailsBySchoolId(@RequestParam(value="mealSchoolId", required=true)
			Long mealSchoolId){
		logger.info("Invoking API for get all the admin emails by meal school id");
		return mealManageAPIService.adminEmailsBySchoolId(mealSchoolId);
	}
	
	/**This API used for generate the stripe account access link
	 * @throws Exception **/
	@GetMapping("stripeAcAccessLink")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> stripeAcAccessLink(@RequestParam(value="accountId", required=true) String accountId) throws Exception{
		logger.info("Invoking API for generate the stripe account access link");
		ServiceResponse serviceResponse = mealManageAPIService.stripeAcAccessLink(accountId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API created for update the free meal / reduced price eligibility status update**/
	@PostMapping("freeReducedLunchEligUpdate")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> freeReducedLunchEligUpdate(@RequestBody FreeReducedLunchEligReq 
			freeReducedLunchEligReq) throws Exception{
		logger.info("Invoking freeReducedLunchEligUpdate API for student eligibility i.e. free lunch, reduced price "
				+ "and before care");
		ServiceResponse serviceResponse = mealManageAPIService.freeReducedLunchEligUpdate(freeReducedLunchEligReq);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API created for school year insert/update**/
	@PostMapping("schoolYearSetup/{schoolYearId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> schoolYearSetup(@RequestBody SchoolYear schoolYear, @PathVariable Long schoolYearId, 
			@RequestParam Long mealSchoolId){
		logger.info("Invoking API for insert/update the school year setup details");
		schoolYear.setSchoolId(schoolYearId);
		ServiceResponse serviceResponse = mealManageAPIService.schoolYearSetup(schoolYear, mealSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the school year info by school and year**/
	@GetMapping("getSchoolInfo")
	public SchoolYear getSchoolInfo(@RequestParam long mealSchoolId, @RequestParam int schoolYear){
		logger.info("Invoking API for get the school year object info");
		SchoolYear schoolYearDetails = schoolYearRepository.findByMealSchoolSchoolIdAndSchoolYear(mealSchoolId, schoolYear);
		schoolYearDetails.setMealSchool(null);
		schoolYearDetails.setReimbursementRatesInfos(null);
		return schoolYearDetails;
	}
	
	/**This API used for submit the free/reduced meal eligibility application**/
	@PostMapping("householdApplication")
	public ResponseEntity<ServiceResponse> householdApplication(@RequestBody HouseholdApplicationForFRM householdApplicationForFRM){
		logger.info("Invoking API to submit the household application for free/reduced meals eligibility program");
		ServiceResponse serviceResponse = mealManageAPIService.householdApplication(householdApplicationForFRM);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get all the applications by mealSchoolId and schoolYear
	 * @throws Exception **/
	@GetMapping("getHouseholdApp")
	public List<HouseholdApplicationForFRM> getHouseholdApp(@RequestParam Long mealSchoolId, @RequestParam Integer schoolYear,
			@RequestParam(value="districtId", required=false) Long districtId) throws Exception{
		logger.info("Invoking API for get all the household applications by mealSchoolId and schoolYear");
		return mealManageAPIService.getHouseholdApp(mealSchoolId, schoolYear, districtId);
	}
	
	/**This API used for get household application by application Id
	 * @throws Exception **/
	@GetMapping("getHouseholdAppById")
	public HouseholdApplicationForFRM getHouseholdAppById(@RequestParam long applicationId) throws Exception{
		logger.info("Invoking API to get household application by application Id");
		return mealManageAPIService.getHouseholdAppById(applicationId);
	}
	
	/**This API used for get the household app info**/
	@GetMapping("getStdAppInfo")
	public ResponseEntity<ServiceResponse> getStdAppInfo(@RequestParam Long mealSchoolId, @RequestParam Integer schoolYear,
			@RequestParam("parentEmails") List<String> parentEmails){
		logger.info("Invoking API for get the students household app info.");
		ServiceResponse serviceResponse = mealManageAPIService.getStdAppInfo(mealSchoolId, schoolYear, parentEmails);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used to make the school admin user as primary**/
	@GetMapping("makeAdminPrimary")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> makeAdminPrimary(@RequestParam String userName){
		logger.info("Invoking API to make the admin user as primary with userName: "+userName);
		ServiceResponse serviceResponse = mealManageAPIService.makeAdminPrimary(userName);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for add/update the module details**/
	@PostMapping("addModuleDetails")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> addModuleDetails(@RequestBody List<ModuleDetails> moduleDetails){
		logger.info("Invoking API for add/update the module details");
		ServiceResponse serviceResponse = mealManageAPIService.addModuleDetails(moduleDetails);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the module details by type**/
	@GetMapping("getModulesByType")
	public Map<String, Map<String, List<ModuleTypeMapping>>> getModulesByType(@RequestParam String userType){
		logger.info("Invoking API for get the module details by type: "+userType);
		return mealManageAPIService.getModulesByType(userType);
	}
	
	/**This API used for add the module details by type**/
	@PostMapping("addModuleType")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> addModuleType(@RequestBody Map<String, Map<String, List<ModuleTypeMapping>>> moduleTypeDetails){
		logger.info("Invoking API for add/update the module type details");
		ServiceResponse serviceResponse = mealManageAPIService.addModuleType(moduleTypeDetails);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for one time to create the student eligibility audit record for existing students**/
	@GetMapping("eligibilityAuditForExistingStd")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> eligibilityAuditForExistingStd(@RequestParam Integer schoolYear){
		logger.info("Invoking API for audit the existing student eligibility.");
		ServiceResponse serviceResponse = mealManageAPIService.eligibilityAuditForExistingStd(schoolYear);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for publish the event and notify the respective parent**/
	@PutMapping("publishEvent/{eventId}")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> publishEvent(@PathVariable Long eventId){
		logger.info("Invoking API for publish the event with eventId::"+eventId);
		ServiceResponse serviceResponse = mealManageAPIService.publishEvent(eventId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the student & events info using details which provided in event notification link**/
	@GetMapping("getEventWithStudentsInfo")
	public ResponseEntity<ServiceResponse> getEventWithStudentInfo(@RequestParam String parentEmail, @RequestParam Long mealSchoolId, 
			@RequestParam Integer schoolYear, @RequestParam String currentDate){
		logger.info("Invoking API to get the events along with students parentEmailId::"+parentEmail+" and schoolYear::"+schoolYear+" and mealSchoolId::"+mealSchoolId);
		ServiceResponse serviceResponse = mealManageAPIService.getEventWithStudentInfo(parentEmail, mealSchoolId, schoolYear, currentDate);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for activate/deactivate the student**/
	@PutMapping("changeStdStatus/{studentRecId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> changeStdStatus(@PathVariable Long studentRecId, @RequestParam Boolean isActive){
		logger.info("Invoking API for change the student status with studentId::"+studentRecId+" and isActive::"+isActive);
		ServiceResponse serviceResponse = mealManageAPIService.changeStdStatus(studentRecId, isActive);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for one time to create the student status audit record for existing students**/
	@GetMapping("statusAuditForExistingStd")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> statusAuditForExistingStd(@RequestParam Integer schoolYear){
		logger.info("Invoking API for audit the existing student status.");
		ServiceResponse serviceResponse = mealManageAPIService.statusAuditForExistingStd(schoolYear);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for save the module info**/
	@PostMapping("moduleInfo")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> moduleInfo(@RequestBody List<ModuleInfo> moduleInfos){
		logger.info("Invoking API for add the module info");
		ServiceResponse serviceResponse = mealManageAPIService.moduleInfo(moduleInfos);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for save the tier info along with module mapping**/
	@PostMapping("tierWithModule")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> tierWithModule(@RequestBody TierInfo tierInfo){
		logger.info("Invoking API for add the tier info along with module mapping");
		ServiceResponse serviceResponse = mealManageAPIService.tierWithModule(tierInfo);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the reports name by their Category**/
	@GetMapping("reportsByCategory")
	public Map<String, List<String>> reportsByCategory(){
		logger.info("Invoking method to get the reports name by Category");
		Map<String, List<String>> reportsByCategory = new HashMap<String, List<String>>();
		for (ReportsByCategory type : ReportsByCategory.values()) {
			reportsByCategory.put(type.toString(), type.getValues());
		}
		return reportsByCategory;
	}
	
	@GetMapping("orderedMealsInfo")
	//@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	/**This method used for get the menu item details by order id.**/
	public MenuSummaryDetailDTO orderedMealsInfo(@RequestParam Long studentRecId, @RequestParam String yearMonth, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType) throws Exception{
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		logger.info("Invoking API to get the ordered items info for studentRecId: "+studentRecId+" and yearMonth::"+yearMonth+" and menuType::"+menuType);
		return mealManageAPIService.getOrderedMealsInfo(studentRecId, yearMonth, menuType);
	}
	
	@PutMapping("pkgNoifSetting")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> updatePkgNoifSetting(@RequestParam Integer days, @RequestParam Long mealSchoolId, @RequestParam Integer schoolYear){
		logger.info("Invoking API for update the due package notification setting for mealSchoolId::"+mealSchoolId+" and schoolYear::"+schoolYear);
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			schoolYearRepository.pkgDueNotificationDays(days, mealSchoolId, schoolYear);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Updated package notification setting successfully.");
			logger.info(serviceResponse.getStatusMessage()+" for mealSchoolId::"+mealSchoolId);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update package notification setting.");
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage()+" for mealSchoolId::"+mealSchoolId);
		}
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	@GetMapping("pkgNoifSetting")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> getPkgNoifSetting(@RequestParam Long mealSchoolId, @RequestParam Integer schoolYear){
		logger.info("Invoking API for get the due package notification setting for mealSchoolId::"+mealSchoolId+" and schoolYear::"+schoolYear);
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			PkgNotificationSetting pkgNotificationSetting = schoolYearRepository.getPkgDueNotificationDays(mealSchoolId, schoolYear);
			serviceResponse.setResponse(pkgNotificationSetting);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Package notification setting retrieved successfully.");
			logger.info(serviceResponse.getStatusMessage()+" for mealSchoolId::"+mealSchoolId);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to retrieve package notification setting.");
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage()+" for mealSchoolId::"+mealSchoolId);
		}
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	@GetMapping("schoolTypes")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public Map<SchoolType, List<String>> schoolTypes(){
		logger.info("Invoking API for get the school types with all grades");
		Map<SchoolType, List<String>> gradesByType = new HashMap<>();
		for(SchoolType type : SchoolType.values()){
			gradesByType.put(type, type.getValues());
		}
		return gradesByType;
	}
	
	@PutMapping("{catererId}/caterer")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_CATERER')")
	public ResponseEntity<ServiceResponse> catererUpdate(@PathVariable Long catererId, @RequestBody Caterer caterer){
		logger.info("Invoking API for update the Caterer info with catererId:: "+catererId);
		ServiceResponse serviceResponse = mealManageAPIService.catererUpdate(catererId, caterer);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for district update**/
	@PutMapping("{districtId}/district")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_DISTRICT')")
	public ResponseEntity<ServiceResponse> districtUpdate(@PathVariable Long districtId, @RequestBody District district){
		logger.info("Invoking API for update the District info with districtId:: "+districtId);
		ServiceResponse serviceResponse = mealManageAPIService.districtUpdate(districtId, district);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}	
	
	/**This API used for generate the PIN of School Admin User**/
	@PutMapping("{schoolId}/{userId}/generateAdminPIN")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> generateAdminPIN(@PathVariable Long schoolId, @PathVariable Long userId, @RequestParam(value="pin",required=false) String pin){
		logger.info("Invoking API for generate the Admin User's PIN.");
		ServiceResponse serviceResponse = mealManageAPIService.generateAdminPIN(schoolId, userId,pin);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for export the students data**/
	@GetMapping("{schoolId}/studentsExport/{schoolYear}")
	public ResponseEntity<ServiceResponse> studentsExport(@PathVariable Long schoolId, @PathVariable Integer schoolYear, HttpServletResponse response){
		logger.info("Invoking API for export the students data");
		ServiceResponse serviceResponse = mealManageAPIService.studentsExport(schoolId, schoolYear, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for export the students data**/
	@GetMapping("{schoolId}/studentBalanceExport/{schoolYear}")
	public ResponseEntity<ServiceResponse> studentBalanceExport(@PathVariable Long schoolId, @PathVariable Integer schoolYear, HttpServletResponse response){
		logger.info("Invoking API for export the students Balance data");
		ServiceResponse serviceResponse = mealManageAPIService.studentBalanceExport(schoolId, schoolYear, response);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the school/student's count**/
	@GetMapping("getCountByCategory")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> getCountByCategory(@RequestParam String category){
		logger.info("Invoking API for get the count of "+category);
		ServiceResponse serviceResponse = mealManageAPIService.getCountByCategory(category);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	@GetMapping("{mealSchoolId}/enrollments")
	public ResponseEntity<ServiceResponse> enrollments(@PathVariable Long mealSchoolId, @RequestParam String startDate, 
			@RequestParam String endDate, @RequestParam Boolean isSummary, @RequestParam(value="parentEmail",required=false) String parentEmail){
		logger.info("Invoking API for get the enrollments details");
		ServiceResponse serviceResponse = mealManageAPIService.enrollments(mealSchoolId, startDate, endDate, isSummary, parentEmail);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	@GetMapping("generateHoseholdLetter/{appId}")
	public ResponseEntity<ServiceResponse> generateHoseholdLetter(@PathVariable Long appId, HttpServletResponse resp){
		logger.info("Invoking API for generate the household letter with appId::"+appId);
		ServiceResponse serviceResponse = mealManageAPIService.generateHoseholdLetter(appId, resp);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}


	/**This API used for get Lead Information of the DemoRequests
	 * @return List<DemoRequest> **/
	@GetMapping("leads")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<List<DemoRequest>> getLeads(@RequestParam(name = "status", required = false) String status,
													  @RequestParam(name = "offset", required = false) int offset,
													  @RequestParam(name = "limit", required = false) int limit,
													  @RequestParam(name = "sortCriteria", required = false) String sortCriteria ){
		logger.info("Invoking API to get the leads");
		return new ResponseEntity<>(leadManagementService.getLeadRequests(status, offset, limit, sortCriteria), HttpStatus.OK);
	}

	/**This API used for update Lead Information of the DemoRequests
	 * @return ResponseEntity<Object>**/
	@PutMapping("leads")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<Object> updateLeads(@RequestBody DemoRequest demoRequest){
		logger.info("Invoking API to update the Leads");
		Map<String, Object> errorResponse = new HashMap<>();
		try {
			return leadManagementService.updateDemoRequests(demoRequest, errorResponse);
		} catch (Exception e) {
			logger.error("Failed to updateLeads: {} ", ExceptionUtils.getStackTrace(e));

			errorResponse.put("errorCode", "500");
			errorResponse.put("errorDescription", "System Error Occurred");

			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**This API used for get Lead Information of the DemoRequests
	 * @return List<DemoRequest> **/
	@GetMapping("leads/history")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<List<DemoRequestHistory>> getLeadsHistory(@RequestParam(name = "requestId", required = false) Long requestId){
		logger.info("Invoking API to get the leads");
		return new ResponseEntity<>(leadManagementService.getLeadRequestHistory(requestId), HttpStatus.OK);
	}

	@GetMapping("leads/status")
	public ResponseEntity<List<LeadStatusCode>> getLeadStatus(){
		logger.info("Invoking API to get the leads");
		return new ResponseEntity<>(leadManagementService.getLeadStatus(), HttpStatus.OK);
	}

	/**This API used for deleting Lead Information of the DemoRequests
	 * @return List<DemoRequest> **/
	@DeleteMapping("leads")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<Object> deleteLeads(@RequestParam(name = "requestId", required = false) Long requestId){
		logger.info("Invoking API to delete the leads");
		return leadManagementService.deleteLeadRequest(requestId);
	}
}
