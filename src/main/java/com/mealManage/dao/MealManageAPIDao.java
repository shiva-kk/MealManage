package com.mealManage.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.EligCertReq;
import com.mealManage.domain.FreeReducedLunchEligReq;
import com.mealManage.domain.ParentsNotificationRequest;
import com.mealManage.domain.StudentDetailSendNotif;
import com.mealManage.domain.StudentMealOrders;
import com.mealManage.domain.StudentMealOrdersV2;
import com.mealManage.domain.TierInfo;
import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.caterer.CatererUser;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.DistrictUser;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.ModuleInfo;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;
import com.mealManage.mealmodel.user.ModuleDetails;
import com.mealManage.mealmodel.user.ModuleTypeMapping;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.response.OrderStatusResp;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;

public interface MealManageAPIDao {
	
	public ServiceResponse adminAccActivationInfo(Long schoolId, String loggedUser, String schoolUserName);
	
	public StudentCreateResp students(List<StudentUser> students, Long mealSchoolId, Boolean isImport, Date schoolYearEndDate, String processType) throws Exception;
	
	public ServiceResponse importCertificationDate(List<EligCertReq> eligCertReqs, Long mealSchoolId, Integer schoolYear, String loggedUser) throws Exception;
	
	public ServiceResponse sendNotificationParents(ParentsNotificationRequest parentsNotificationRequest) throws Exception;
	
	public ServiceResponse orderMenu(List<StudentMealOrders> studentMealOrders, String loggedUser, String yearMonth);
	
	public ServiceResponse orderMenuV2(StudentMealOrdersV2 mealOrders, String loggedUser, ItemTypeConstants menuType);

	public ServiceResponse refreshOrderPdf(Long orderId) throws Exception;
	
	public ServiceResponse orderPayment(List<Long> studentRecIds, String loggedUser, String yearMonth);
	
	public ServiceResponse mealSchoolUpdate(MealSchool mealSchool, Long mealSchoolId) throws Exception;
	
	public ServiceResponse enableDisableSchoolUser(String schoolUserName, Boolean activeStatus, String loggedUser);
	
	public ServiceResponse enableDisableDistrictUser(String userName, Boolean activeStatus, String loggedUser);
	
	public ServiceResponse studentUpdate(StudentUser studentUser, Long studentRecId, String processType, Date schoolYearEndDate, Boolean isMMGenId);
	
	public ServiceResponse uploadMealSchoolLogo(MultipartFile logoFile, Long mealSchoolId, String loggedUser);
	
	public OrderStatusResp menuOrderStatus(Long studentRecId, String yearMonth);
	
	public OrderStatusResp menuOrderStatusV2(Long studentRecId, String yearMonth);
	
	public ServiceResponse createHolidays(List<SchoolHoliday> schoolHolidays, Long mealSchoolId, String loggedUser);
	
	public List<Object[]> schoolHolidays(Long mealSchoolId, Date StartDate, Date endDate) throws Exception;
	
	public List<Object[]> sendPaymentReminderToParent(ParentsNotificationRequest parentsNotificationRequest);
	
	public List<Object[]> getNotOrderedLunchStudents(ParentsNotificationRequest parentsNotificationRequest, List<String> grades);
	
	public List<Object[]> getCutOffDateTimeGrade(ParentsNotificationRequest parentsNotificationRequest);
	
	public List<Object[]> getCutOffDateTimeGradeV2(ParentsNotificationRequest parentsNotificationRequest);
	
	public ServiceResponse sendLunchReminderToParent(ParentsNotificationRequest parentsNotificationRequest, 
			Map<String, List<StudentDetailSendNotif>> stdDetailsMap) throws Exception;
	
	public List<Object[]> getSchoolIdAndMonthForReminder(String date);
	
	public List<Object[]> getSchoolIdAndMonthForReminderV2(String date);
	
	public ServiceResponse updateEmailStatus(String email, Boolean paymentReminderEnable, Boolean lunchReminderEnable, 
			Boolean emailIsSubscribe);
	
	public ServiceResponse menuOrderCancel(Long mealSchoolId, List<MealOrderDetails> mealOrderDetails);
	
	public String parentUserActivationLink(String userName, String token);
	
	public ServiceResponse freeReducedLunchEligUpdate(FreeReducedLunchEligReq freeReducedLunchEligReq);
	
	public ServiceResponse householdApplication(HouseholdApplicationForFRM householdApplication) throws Exception;
	
	public List<Object> getApplicationSubmittedStudents(String parentEmail);
	
	public List<Object[]> getOnboardedSchools(String currentDate);
	
	public List<Object[]> getWebsiteStudents(Long mealSchoolId, Integer schoolYear, String parentEmail, Boolean isSupport);
	
	public ServiceResponse makeAdminPrimary(String userName);
	
	public ServiceResponse addModuleDetails(List<ModuleDetails> moduleDetails);
	
	public List<Object[]> getModulesByType(String userType);
	
	public String buildResetPasswordLink(Long userId, String forgotPasswordToken, String subdomain, String username);
	
	public ServiceResponse addModuleType(Map<String, Map<String, List<ModuleTypeMapping>>> moduleTypeDetails);
	
	public void addUpdateStudentEligibility(Integer currentEligStatus, Integer previousEligStatus, Boolean isUpdate, 
			Date startDate, Date schoolYearEndDate, String note, StudentUser studentUser);
	
	public Integer getEligStatus(Boolean isFreeMeal, Boolean isReducedPriceMeal);
	
	public String parentUserEventLink(String userName, String token, Long eventId, Long schoolId, Integer schoolYear);
	
	public void auditStudentStatus(Boolean currentStatus, Boolean previousStatus, Boolean isUpdate, 
			Date startdate, Date schoolYearEndDate, String note, StudentUser studentUser);
	
	public Long addInstantPayTrx(String loggedUser, MealSchool mealSchool, StudentMealOrdersV2 stdOrder, Double refundAmt, ItemTypeConstants menuType) throws Exception;
	
	public void addModules(List<ModuleInfo> moduleInfos);
	
	public void tierWithModule(TierInfo tierInfo);
	
	public Map<String, String> gradeMapByCountry(String countryCode);
	
	public Map<String, String> gradeBackMapByCountry(String countryCode);
	
	public void catererUpdate(Caterer caterer, Set<CatererUser> catererUsers);
	
	public void districtUpdate(District district, Set<DistrictUser> districtUsers);
	
	public ServiceResponse saveAdminPIN(Long schoolId, Long userId, String pin);
	
	public void saveStdPin(Map<Long, String> pinByStd);
	
	//public ServiceResponse demoRequest(String firstName, String lastName, String schoolName, String emailAddress);
	

}
