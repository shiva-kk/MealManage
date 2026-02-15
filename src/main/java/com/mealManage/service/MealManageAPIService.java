package com.mealManage.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.FreeReducedLunchEligReq;
import com.mealManage.domain.MenuOrderCancellationReq;
import com.mealManage.domain.ParentsNotificationRequest;
import com.mealManage.domain.StudentMealOrders;
import com.mealManage.domain.StudentMealOrdersV2;
import com.mealManage.domain.TierInfo;
import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.ModuleInfo;
import com.mealManage.mealmodel.school.SchoolYear;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;
import com.mealManage.mealmodel.user.ModuleDetails;
import com.mealManage.mealmodel.user.ModuleTypeMapping;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.mealschedule.model.MenuSummaryDetailDTO;
import com.mealManage.response.OrderStatusResp;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;

public interface MealManageAPIService {
	
	public ServiceResponse adminAccActivationInfo(Long schoolId, String loggedUser, String schoolUserName);
	
	public ServiceResponse catererAccActivation(String username);
	
	public ServiceResponse districtAccActivation(String username);
	
	public StudentCreateResp students(List<StudentUser> students, Long mealSchoolId);
	
	public StudentCreateResp importStudents(MultipartFile multipartFile, Long mealSchoolId, String loggedUser, Integer schoolYear);
	
	public ServiceResponse importCertification(MultipartFile multipartFile, Long mealSchoolId, Integer schoolYear);
	
	public ServiceResponse sendNotificationParents(ParentsNotificationRequest parentsNotificationRequest, Boolean autoReminderStatus) throws Exception;
	
	public ServiceResponse orderMenu(List<StudentMealOrders> studentMealOrders, String loggedUser, String yearMonth);
	
	public ServiceResponse orderMenuV2(StudentMealOrdersV2 mealOrders, String loggedUser, ItemTypeConstants menuType);
	
	public ServiceResponse refreshOrderPdf(Long orderId);
	
	public ServiceResponse orderPayment(List<Long> studentRecIds, String loggedUser, String yearMonth);
	
	public ServiceResponse mealSchoolUpdate(MealSchool mealSchool, Long mealSchoolId);
	
	public ServiceResponse enableDisableSchoolUser(String schoolUserName, Boolean activeStatus, String loggedUser);
	
	public ServiceResponse enableDisableDistrictUser(String userName, Boolean activeStatus, String loggedUser);
	
	public ServiceResponse studentUpdate(StudentUser studentUser, Long studentRecId);
	
	public ServiceResponse uploadMealSchoolLogo(MultipartFile logoFile, Long mealSchoolId, String loggedUser);
	
	public OrderStatusResp menuOrderStatus(Long studentRecId, String yearMonth);
	
	public OrderStatusResp menuOrderStatusV2(Long studentRecId, String yearMonth);
	
	public ServiceResponse importHolidays(MultipartFile multipartFile, Long mealSchoolId, String loggedUser);
	
	public ServiceResponse createHolidays(List<SchoolHoliday> schoolHolidays, Long mealSchoolId, String loggedUser);
	
	public List<SchoolHoliday> schoolHolidays(Long mealSchoolId, Date StartDate, Date endDate) throws Exception;
	
	public void buildAutoReminderRequest();
	
	public ServiceResponse updateEmailStatus(String email, Boolean paymentReminderEnable, Boolean lunchReminderEnable, 
			Boolean emailIsSubscribe);
	
	public ServiceResponse menuOrderCancel(Long mealSchoolId, MenuOrderCancellationReq menuOrderCancellationReq);
	
	public ServiceResponse menuOrderCancelV2(Long mealSchoolId, MenuOrderCancellationReq menuOrderCancellationReq, ItemTypeConstants menuType);
	
	public ServiceResponse schoolInfoByParentEmail(String parentEmail, String currentDate, Boolean isVersion2);
	
	public List<String> adminEmailsBySchoolId(Long mealSchoolId);
	
	public ServiceResponse stripeAcAccessLink(String accountId) throws Exception;
	
	public ServiceResponse freeReducedLunchEligUpdate(FreeReducedLunchEligReq freeReducedLunchEligReq);
	
	public ServiceResponse schoolYearSetup(SchoolYear schoolYear, Long mealSchoolId);
	
	public ServiceResponse householdApplication(HouseholdApplicationForFRM householdApplicationForFRM);
	
	public List<HouseholdApplicationForFRM> getHouseholdApp(Long mealSchoolId, Integer schoolYear, Long districtId) throws Exception;
	
	public ServiceResponse getStdAppInfo(Long mealSchoolId, Integer schoolYear, List<String> parentEmails);
	
	public HouseholdApplicationForFRM getHouseholdAppById(Long applicationId) throws Exception;
	
	public List<Map<String, String>> onboardedSchoolsInfo(String currentDate);
	
	public List<Map<String, String>> websiteStudentsInfo(Long mealSchoolId, Integer schoolYear, String parentEmail, Boolean isSupport);
	
	public ServiceResponse makeAdminPrimary(String userName);
	
	public ServiceResponse addModuleDetails(List<ModuleDetails> moduleDetails);
	
	public Map<String, Map<String, List<ModuleTypeMapping>>> getModulesByType(String userType);
	
	public ServiceResponse addModuleType(Map<String, Map<String, List<ModuleTypeMapping>>> moduleTypeDetails);
	
	public Map<String, Object> schoolsByParentEmail(String parentEmail, String systemDate);
	
	public ServiceResponse eligibilityAuditForExistingStd(Integer schoolYear);
	
	public ServiceResponse publishEvent(Long eventId);
	
	public ServiceResponse getEventWithStudentInfo(String parentEmail, Long mealSchoolId, Integer schoolYear, String currentDate);
	
	public ServiceResponse changeStdStatus(Long studentRecId, Boolean isActive);
	
	public ServiceResponse statusAuditForExistingStd(Integer schoolYear);
	
	public ServiceResponse moduleInfo(List<ModuleInfo> moduleInfos);
	
	public ServiceResponse tierWithModule(TierInfo tierInfo);
	
	public MenuSummaryDetailDTO getOrderedMealsInfo(Long studentRecId, String yearMonth, ItemTypeConstants menuType);
	
	public ServiceResponse catererUpdate(Long catererId, Caterer caterer);
	
	public ServiceResponse districtUpdate(Long districtId, District district);
	
	public ServiceResponse generateAdminPIN(Long schoolId, Long userId,String pin);
	
	public ServiceResponse generateStudentPIN(Long schoolId, String grade, Long stdRecId);
	
	public ServiceResponse studentsExport(Long schoolId, Integer schoolYear, HttpServletResponse response);
	
	public ServiceResponse studentBalanceExport(Long schoolId, Integer schoolYear, HttpServletResponse response);
	
	public ServiceResponse getCountByCategory(String category);
	
	public ServiceResponse enrollments(Long mealSchoolId, String startDate, String endDate, Boolean isSummary, String parentEmail);
	
	public ServiceResponse generateHoseholdLetter(Long appId, HttpServletResponse resp);
	//public void demoRequest(String firstName, String lastName, String schoolName, String emailAddress);
	
}
