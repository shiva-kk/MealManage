package com.mealManage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mealManage.dao.MMDataSyncDao;
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.dao.ReportsDao;
import com.mealManage.domain.AssistanceProgram;
import com.mealManage.domain.EligCertReq;
import com.mealManage.domain.FreeReducedLunchEligReq;
import com.mealManage.domain.HouseholdAppDeclinedReason;
import com.mealManage.domain.HouseholdAppOtherInfo;
import com.mealManage.domain.HouseholdIncompleteApp;
import com.mealManage.domain.IncomeInfo;
import com.mealManage.domain.MenuOrderCancellationReq;
import com.mealManage.domain.NotificationRequest;
import com.mealManage.domain.ParentsNotificationRequest;
import com.mealManage.domain.SchoolDetailsInfo;
import com.mealManage.domain.StatusUpdateNotificationReq;
import com.mealManage.domain.StudentDetailSendNotif;
import com.mealManage.domain.StudentMealOrders;
import com.mealManage.domain.StudentMealOrdersV2;
import com.mealManage.domain.TierInfo;
import com.mealManage.domain.UserActivationNotification;
import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.caterer.CatererUser;
import com.mealManage.mealmodel.meal.BreakfastMaster;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.meal.SchoolMeal;
import com.mealManage.mealmodel.repository.BreakfastMasterRepository;
import com.mealManage.mealmodel.repository.CatererRepository;
import com.mealManage.mealmodel.repository.CertDateImportFileBkpRepo;
import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.DistrictRepository;
import com.mealManage.mealmodel.repository.EligibilityCodeRepo;
import com.mealManage.mealmodel.repository.EventInfoRepo;
import com.mealManage.mealmodel.repository.HouseholdAppForFRMRepository;
import com.mealManage.mealmodel.repository.MealCalendarSummaryRepository;
import com.mealManage.mealmodel.repository.MealOrderDetailsRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.SchoolPackageRepo;
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.CountryDetail;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.DistrictUser;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.ModuleInfo;
import com.mealManage.mealmodel.school.SchoolYear;
import com.mealManage.mealmodel.transaction.EventInfo;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;
import com.mealManage.mealmodel.user.ModuleDetails;
import com.mealManage.mealmodel.user.ModuleTypeMapping;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.mealschedule.entities.MealCalendar;
import com.mealManage.mealschedule.entities.MealCalendarSummary;
import com.mealManage.mealschedule.model.MenuDetailDTO;
import com.mealManage.mealschedule.model.MenuSummaryDetailDTO;
import com.mealManage.menu.entities.CertDateImportFileBkp;
import com.mealManage.menu.entities.EligibilityCode;
import com.mealManage.response.EligAppResp;
import com.mealManage.response.Enrollments;
import com.mealManage.response.OrderStatusResp;
import com.mealManage.response.ParentEmailWithToken;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;
import com.mealManage.util.AOPUtil;
import com.mealManage.util.AWSUtility;
import com.mealManage.util.CommonExcelGenerator;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.DateUtilityV2;
import com.mealManage.util.ExcelReadUtil;
import com.mealManage.util.OrderedMenuPdfUtility;
import com.mealManage.util.SendNotificationUtil;

/**This class implement by MealManageAPIService interface for MealManage APIs**/
@Service
public class MealManageAPIServiceImpl implements MealManageAPIService {
	
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	
	@Autowired
	private ExcelReadUtil excelReadUtil;
	@Autowired
	private CatererRepository catererRepository;
	@Autowired
	private ReportsDao reportsDao;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	@Autowired
	private SchoolYearRepository mealSchoolSchoolYearRepository;
	@Autowired
	private MealOrderDetailsRepository mealOrderDetailsRepository;
	@Autowired
	private OrderedMenuPdfUtility orderedMenuPdfUtility;
	@Autowired
	private AOPUtil aopUtil;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Autowired
	private BreakfastMasterRepository breakfastMasterRepository;
	@Autowired
	private MealCalendarSummaryRepository summaryRepo;
	/*@Autowired
	private StripeUtil stripeUtil;*/
	@Value("${email.payment.reminder.subject}")
	private String paymentReminderSubject;
	@Value("${email.payment.reminder.message}")
	private String paymentReminderMessage;
	@Value("${caterer.domain}")
	private String catererDomain;
	@Value("${district.domain}")
	private String districtDomain;
	@Autowired
	private HouseholdAppForFRMRepository householdAppForFRMRepository;
	@Autowired
	private CommonExcelGenerator commonExcelGenerator;
	@Autowired
	private EventInfoRepo eventInfoRepo;
	@Autowired
	private MMDataSyncDao mmDataSyncDao;
	@Autowired
	private AWSUtility awsUtility;
	@Value("${federal.poverty_limit}")
	private Integer federalPovertyLimit;
	@Value("${federal.poverty.increment}")
	private Integer additionalMemberIncr;
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private CertDateImportFileBkpRepo certBkpRepo;
	@Autowired
	private EligibilityCodeRepo eligibilityCodeRepo;
	@Autowired
	private SchoolPackageRepo packageRepo;
	private static DecimalFormat df2 = new DecimalFormat("00");
	private static DecimalFormat df4 = new DecimalFormat("0000");
	@Autowired
	private DistrictRepository districtRepository;
	@Autowired
	private DateUtilityV2 du;
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**This method used for get the information to send notification to the admin user**/
	@Override
	public ServiceResponse adminAccActivationInfo(Long schoolId, String loggedUser, String schoolUserName) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse = mealManageAPIDao.adminAccActivationInfo(schoolId, loggedUser, schoolUserName);
		} catch (Exception e) {
			logger.error("Error occurred during the adminAccActivationInfo API execution. "+e.getMessage());
			serviceResponse.setStatusMessage("Failed to notify the school admin user for account activation. Please try again later!");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}	
	
	/**This method used for send the account activation link to the Caterer user**/
	@Override
	public ServiceResponse catererAccActivation(String username) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(username, "ROLE_CATERER");
			if(usersAuthInfo == null){
				logger.error("Caterer user ::"+username+" does not exist");
				throw new Exception("Caterer user ::"+username+" does not exist");
			}else{
				usersAuthInfo.setfToken(UUID.randomUUID().toString());
				usersAuthInfo.setfTokenTime(new Date());
				usersAuthInfoRepository.save(usersAuthInfo);
				Long userId = catererRepository.getCatererUserId(username);
				String resetLinkUrl = mealManageAPIDao.buildResetPasswordLink(userId, usersAuthInfo.getfToken(), catererDomain, username);
				UserActivationNotification adminInfo = new UserActivationNotification();
				adminInfo.setEmail(username);
				adminInfo.setToken(resetLinkUrl);
				NotificationRequest notificationRequest = new NotificationRequest();
				notificationRequest.setUsers(Arrays.asList(adminInfo));	
				sendNotificationUtil.schoolUserAccActivationNotification(notificationRequest);
				serviceResponse.setStatusMessage("Caterer users has been notify successfully for their account activation.");
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				logger.info(serviceResponse.getStatusMessage());
			}
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to send the account activation link.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" to Caterer user::"+username+" due to "+e.getMessage());			
		}
		return serviceResponse;
	}
	
	/**This method used for send the district account activation link**/
	@Override
	public ServiceResponse districtAccActivation(String username) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(username, "ROLE_DISTRICT");
			if(usersAuthInfo == null){
				logger.error("District user ::"+username+" does not exist");
				throw new Exception("District user ::"+username+" does not exist");
			}else{
				usersAuthInfo.setfToken(UUID.randomUUID().toString());
				usersAuthInfo.setfTokenTime(new Date());
				usersAuthInfoRepository.save(usersAuthInfo);
				Long userId = districtRepository.getDistrictUserId(username);
				String resetLinkUrl = mealManageAPIDao.buildResetPasswordLink(userId, usersAuthInfo.getfToken(), districtDomain, username);
				UserActivationNotification adminInfo = new UserActivationNotification();
				adminInfo.setEmail(username);
				adminInfo.setToken(resetLinkUrl);
				NotificationRequest notificationRequest = new NotificationRequest();
				notificationRequest.setUsers(Arrays.asList(adminInfo));	
				sendNotificationUtil.schoolUserAccActivationNotification(notificationRequest);
				serviceResponse.setStatusMessage("District users has been notify successfully for their account activation.");
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				logger.info(serviceResponse.getStatusMessage());
			}
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to send the account activation link.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" to District user::"+username+" due to "+e.getMessage());			
		}
		return serviceResponse;
	}
	
	/**This method used for insert the multiple students data**/
	@Override
	public StudentCreateResp students(List<StudentUser> students, Long mealSchoolId) {
		StudentCreateResp studentCreateResp = new StudentCreateResp();
		try{
			Date schoolYearEndDate = null;
			if(students != null && students.size() > 0)
				schoolYearEndDate = schoolYearRepository.getSchoolYearEndDate(mealSchoolId, students.get(0).getSchoolYear());
			studentCreateResp = mealManageAPIDao.students(students, mealSchoolId, false, schoolYearEndDate, "Manage Screen");
			logger.info("Success Insert Count: "+studentCreateResp.getSuccessInsertCount()+
					", Success Update Count: "+studentCreateResp.getSuccessUpdateCount()+", Failed insert count: "+
					studentCreateResp.getFailedInsertCount()+", Failed update count: "+studentCreateResp.getFailedUpdateCount());
		}catch (Exception e) {
			logger.error("Failed to create students"+e.getMessage());
			if(e instanceof DataIntegrityViolationException || e.getMessage().contains("javax.persistence.RollbackException") 
					|| e.getMessage().contains("ConstraintViolationException")){
				studentCreateResp.setStatusCode(409);
				studentCreateResp.setErrorMessage("Found duplicate entry: "+e.getMessage());
				studentCreateResp.setStatusMessage("Failed to create the Students due to "+e.getCause().getCause().getMessage().split("for key")[0]+".");
			}else{
				studentCreateResp.setStatusCode(500);
				studentCreateResp.setErrorMessage(e.getMessage());
				studentCreateResp.setStatusMessage("Failed to create the Students.");
			}
			studentCreateResp.setStatus("Failed");
		}
		return studentCreateResp;
	}
	
	/**This method used for import the students data into table from excel file (i.e. xlsx,xls)**/
	@Override
	public StudentCreateResp importStudents(MultipartFile multipartFile, Long mealSchoolId, String loggedUser, Integer schoolYear) {
		logger.info("Reading file and importing data into Students table");
		StudentCreateResp studentCreateResp = new StudentCreateResp();
		try{
			List<StudentUser> studentUsers = excelReadUtil.studentUsers(multipartFile, mealSchoolId, loggedUser, schoolYear);
			if(studentUsers != null && studentUsers.size() > 0){
				String fileLink = commonExcelGenerator.studentExport(mealSchoolId, schoolYear, "studentImport",null);
				mmDataSyncDao.studentBkpFileLink(fileLink, mealSchoolId, schoolYear,"studentImport", loggedUser);
			}
			Date schoolYearEndDate = null;
			if(studentUsers != null && studentUsers.size() > 0)
				schoolYearEndDate = schoolYearRepository.getSchoolYearEndDate(mealSchoolId, studentUsers.get(0).getSchoolYear());
			studentCreateResp = mealManageAPIDao.students(studentUsers, mealSchoolId, true, schoolYearEndDate, "File Import");
		}catch(Exception e){
			logger.error("Failed to create students"+e.getMessage());
			if(e instanceof DataIntegrityViolationException || e.getMessage().contains("javax.persistence.RollbackException") 
					|| e.getMessage().contains("ConstraintViolationException")){
				studentCreateResp.setStatusCode(409);
				studentCreateResp.setErrorMessage("Found duplicate entry: "+e.getMessage());
				studentCreateResp.setStatusMessage("Failed to create the Students due to "+e.getCause().getCause().getMessage().split("for key")[0]+".");
			}else{
				studentCreateResp.setStatusCode(500);
				studentCreateResp.setErrorMessage(e.getMessage());
				studentCreateResp.setStatusMessage("Failed to create the Students.");
			}
			studentCreateResp.setStatus("Failed");
		}
		return studentCreateResp;
	}
	
	/**This method used for import the eligibility certifications**/
	@Override
	public ServiceResponse importCertification(MultipartFile multipartFile, Long mealSchoolId, Integer schoolYear) {
		logger.info("Importing certification file for student eligibility for school Id::"+mealSchoolId+" and schoolYear::"+schoolYear);
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String loggedUser = "";
			if(SecurityContextHolder.getContext() !=  null && SecurityContextHolder.getContext().getAuthentication() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			List<EligCertReq> eligCertReqs = excelReadUtil.stdEligCertDateImport(multipartFile, mealSchoolId, schoolYear);
			if(eligCertReqs != null && eligCertReqs.size() > 0){
				CertDateImportFileBkp certDateImportFileBkp = new CertDateImportFileBkp();
				certDateImportFileBkp.setCreatedBy(loggedUser);
				certDateImportFileBkp.setCreatedOn(new Date());
				certDateImportFileBkp.setMealSchoolId(mealSchoolId);
				certDateImportFileBkp.setSchoolYear(schoolYear);
				certBkpRepo.save(certDateImportFileBkp);
				String filePath = "CertDateImport_"+mealSchoolId+"_"+certDateImportFileBkp.getId()+"."+FilenameUtils.getExtension(multipartFile.getOriginalFilename());
				String fileLink = awsUtility.fileUploadPath(filePath, "studentCertBkpFileLink");
				File file = new File(filePath);
				FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
				awsUtility.uploadFileToAWSS3Bucket(filePath, "studentCertBkp");
				certDateImportFileBkp.setFileLink(fileLink);
				certBkpRepo.save(certDateImportFileBkp);
			}
			serviceResponse = mealManageAPIDao.importCertificationDate(eligCertReqs, mealSchoolId, schoolYear,loggedUser);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to import certificate date for students.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" for schoolId::"+mealSchoolId+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for send the notification to registered parent email id for registration link, meal update and payment update.
	 * @throws Exception **/
	@Override
	public ServiceResponse sendNotificationParents(ParentsNotificationRequest parentsNotificationRequest, Boolean autoReminderStatus) throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();
		if(parentsNotificationRequest.getNotificationType().equalsIgnoreCase("Registration"))
			serviceResponse = mealManageAPIDao.sendNotificationParents(parentsNotificationRequest);
		else if(parentsNotificationRequest.getNotificationType().equalsIgnoreCase("PaymentStatus")){
			try{
				List<Object[]> parentEmails = mealManageAPIDao.sendPaymentReminderToParent(parentsNotificationRequest);
				List<StatusUpdateNotificationReq> notificationInfos = new ArrayList<StatusUpdateNotificationReq>();	
				MealSchool mealSchool = null;
				String month = "";
				String schoolName = "";
				if(parentsNotificationRequest.getSchoolId() != null){
					mealSchool = mealSchoolRepository.findBySchoolId(parentsNotificationRequest.getSchoolId());	
					schoolName = mealSchool.getSchoolName().toUpperCase();
				}
				month = Month.of(Integer.parseInt(parentsNotificationRequest.getYearMonth().substring(4))).name()+" "
	                	+parentsNotificationRequest.getYearMonth().substring(0,4);
				Set<String> parentEmailsUq = new HashSet<String>();
				for(Object[] obj : parentEmails){
					if(obj[0] != null && !obj[0].toString().isEmpty())
						parentEmailsUq.add(obj[0].toString());
					if(obj[1] != null && !obj[1].toString().isEmpty())
						parentEmailsUq.add(obj[1].toString());
				}
				UsersAuthInfo usersAuthInfo = null;
				StatusUpdateNotificationReq notificationInfo = null;
				for(String email : parentEmailsUq){
					if(email != null){
						usersAuthInfo = usersAuthInfoRepository.findByUsername(email);
						if(usersAuthInfo != null && usersAuthInfo.getfToken() != null && usersAuthInfo.getEmailIsSubscribe() != null 
								&& usersAuthInfo.getEmailIsSubscribe() && usersAuthInfo.getPaymentReminderEnable() != null && 
								usersAuthInfo.getPaymentReminderEnable()){
							notificationInfo = new StatusUpdateNotificationReq();
							notificationInfo.setEmail(email);
							notificationInfo.setOrdermsg(paymentReminderMessage.replace("<<month>>", month));
							notificationInfo.setSubjectMsg(paymentReminderSubject.replace("<<schoolName>>", schoolName));
							notificationInfo.setAdminEmail(mealSchool.getContactPEmail() != null ? mealSchool.getContactPEmail() : "");
							notificationInfos.add(notificationInfo);
						}					
					}
				}
				List<StatusUpdateNotificationReq> parentUserUpdateStatusNotifications = notificationInfos.stream().distinct().
						collect(Collectors.toList());
				if(parentUserUpdateStatusNotifications.size()>0){
				Map<String, List<StatusUpdateNotificationReq>> notificationRequest = new HashMap<String, List<StatusUpdateNotificationReq>>();
				notificationRequest.put("users", parentUserUpdateStatusNotifications);		
				sendNotificationUtil.parentUserReminderNotification(parentsNotificationRequest, notificationRequest);
				serviceResponse.setStatusMessage("The notification will be send to the parent user for the reminder of pending payment.");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatus("Success");
				logger.info("API successfully invoked for the send notification to parent user regarding pending amount reminder");
				}else{
					serviceResponse.setStatusMessage("There are no emails for send notification.");
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatus("Success");
				}
			}catch(Exception e){
				logger.error("Error occured during get the information for send the notification to parent user. "+e.getMessage());
				serviceResponse.setStatusMessage("The notification send to parent users failed. Please try again later.");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatus("Failed");
				serviceResponse.setErrorMessage(e.getMessage());
				throw new Exception("Notification to parent user regarding payment status reminder faild. Please try again later");
			}
		}else if(parentsNotificationRequest.getNotificationType().equalsIgnoreCase("MealOrderStatus")){
			try{
				List<String> gradeNames = null;
				if(parentsNotificationRequest.getMenuType() == null)
					parentsNotificationRequest.setMenuType(ItemTypeConstants.Lunch);
				if(parentsNotificationRequest.getGradeName() != null)
					gradeNames = Arrays.asList(parentsNotificationRequest.getGradeName().toString());
				List<String> grades = reportsDao.getMealPublishedGradesV2(parentsNotificationRequest.getSchoolId(),
						parentsNotificationRequest.getYearMonth(), gradeNames, autoReminderStatus, parentsNotificationRequest.getMenuType());
				List<Object[]> notOrderedLunchStudents = mealManageAPIDao.getNotOrderedLunchStudents(parentsNotificationRequest, grades);
				if(notOrderedLunchStudents != null && notOrderedLunchStudents.size() > 0){
					List<Object[]> cutOffDateTimeByGrade = mealManageAPIDao.getCutOffDateTimeGradeV2(parentsNotificationRequest);
					MealSchool mealSchool = mealSchoolRepository.findBySchoolId(parentsNotificationRequest.getSchoolId());
					Map<String, String> gradeCutOffDateTimeMap = gradeCutOffDateMapBuild(cutOffDateTimeByGrade, mealSchool.getSchoolTimezone().toString());
					Map<String, List<StudentDetailSendNotif>> stdDetailsMap = studentDetailsMap(notOrderedLunchStudents, gradeCutOffDateTimeMap);
					serviceResponse = mealManageAPIDao.sendLunchReminderToParent(parentsNotificationRequest, stdDetailsMap);
					logger.info("Lunch reminder to parent process has been started and will send reminder.");
					}else{
						serviceResponse.setStatusCode(200);
						serviceResponse.setStatusMessage("There are no eligible entry to send the lunch reminder.");
						serviceResponse.setStatus("Success");
						logger.info("There are no eligible entry to send the lunch reminder");
					}
			}catch(Exception e){
				logger.error("Error occurred during execution of the API for send the notification to parent regarding lunch order"+e.getMessage());
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("Failed during excution of API for send reminder to parent regarding lunch order.");
				serviceResponse.setErrorMessage(e.getMessage());
			}
		}
		return serviceResponse;
	}

	/**This method used for add/update the menu orders to students**/
	@Override
	public ServiceResponse orderMenu(List<StudentMealOrders> studentMealOrders, String loggedUser, String yearMonth) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			if(studentMealOrders.size() < 1){
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatusMessage("Input data is not valid.");
				serviceResponse.setStatus("Failed");
				logger.info("Request Body is not valid for the menu order API");
				return serviceResponse;
			}
			serviceResponse = mealManageAPIDao.orderMenu(studentMealOrders, loggedUser, yearMonth);
			logger.info("Menu ordered successfully for the Student record Id : "+studentMealOrders.get(0).getStudentId() +" under the month "+yearMonth);
		}catch(Exception e){
			logger.error("Failed to order the menu for the Student Id: "+studentMealOrders.get(0).getStudentId() +" under the month "+yearMonth+" due to "+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to order the Menu.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for add/update the menu orders to students**/
	@Override
	public ServiceResponse orderMenuV2(StudentMealOrdersV2 mealOrders, String loggedUser, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		String loggedEmail = "";
		try{
			if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
				loggedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			if(mealOrders == null || mealOrders.getStudentId() == null){
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatusMessage("Input data is not valid.");
				serviceResponse.setStatus("Failed");
				logger.info("Request Body is not valid for the menu order API");
				return serviceResponse;
			}
			serviceResponse = mealManageAPIDao.orderMenuV2(mealOrders, loggedUser, menuType);
			logger.info(serviceResponse.getStatusMessage()+" for studentRecId:: "+mealOrders.getStudentId());
		}catch(Exception e){
			logger.error("Failed to order the menu for the Student Id:: "+mealOrders.getStudentId()+" and loggedEmail::"+loggedEmail+" due to '"+e.getMessage()+"'");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to order the Menu.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This API used for refresh the ordered menu pdf**/
	@Override
	public ServiceResponse refreshOrderPdf(Long orderId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			if(orderId == null || orderId == 0){
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatusMessage("Order Id is not valid.");
				serviceResponse.setStatus("Failed");
				logger.info(serviceResponse.getStatusMessage());
				return serviceResponse;
			}
			serviceResponse = mealManageAPIDao.refreshOrderPdf(orderId);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to refresh the ordered Menu.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" for orderId::"+orderId+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for update the order payment staus**/
	@Override
	public ServiceResponse orderPayment(List<Long> studentRecIds, String loggedUser, String yearMonth) {
		return mealManageAPIDao.orderPayment(studentRecIds, loggedUser, yearMonth);
	}
	
	/**This method used for update the MealSchool and user**/
	@Override
	public ServiceResponse mealSchoolUpdate(MealSchool mealSchool, Long mealSchoolId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse = mealManageAPIDao.mealSchoolUpdate(mealSchool, mealSchoolId);
		}catch(Exception e){
			logger.error("Failed to create students"+e.getMessage());
			if(e instanceof DataIntegrityViolationException || e.getMessage().contains("javax.persistence.RollbackException") 
					|| e.getMessage().contains("ConstraintViolationException")){
				serviceResponse.setStatusCode(409);
				serviceResponse.setErrorMessage("Found duplicate entry: "+e.getMessage());
				serviceResponse.setStatusMessage("Failed to update the School & User due to "+e.getCause().getCause().getMessage().split("for key")[0]+".");
			}else{
				serviceResponse.setStatusCode(500);
				serviceResponse.setErrorMessage(e.getMessage());
				serviceResponse.setStatusMessage("Failed to update the School.");
			}
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}
	
	/**This method used for activate / inActivate the school admin user**/
	@Override
	public ServiceResponse enableDisableSchoolUser(String schoolUserName, Boolean activeStatus, String loggedUser){
		return mealManageAPIDao.enableDisableSchoolUser(schoolUserName, activeStatus, loggedUser);
	}
	
	/**This method used for activate / deactivate the district user**/
	@Override
	public ServiceResponse enableDisableDistrictUser(String userName, Boolean activeStatus, String loggedUser) {
		return mealManageAPIDao.enableDisableDistrictUser(userName, activeStatus, loggedUser);
	}
	
	/**This method used for update the student user**/
	@Override
	public ServiceResponse studentUpdate(StudentUser studentUser, Long studentRecId){
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse = mealManageAPIDao.studentUpdate(studentUser, studentRecId, "Manage Screen", null,false);
		}catch(Exception e){
			logger.error("Failed to update students"+e.getMessage());
			if(e instanceof DataIntegrityViolationException || e.getMessage().contains("javax.persistence.RollbackException") 
					|| e.getMessage().contains("ConstraintViolationException")){
				serviceResponse.setStatusCode(409);
				serviceResponse.setErrorMessage("Found duplicate entry: "+e.getMessage());
				serviceResponse.setStatusMessage("Failed to update the Student due to "+e.getCause().getCause().getMessage().split("for key")[0]+".");
			}else{
				serviceResponse.setStatusCode(500);
				serviceResponse.setErrorMessage(e.getMessage());
				serviceResponse.setStatusMessage("Failed to update the Student.");
			}
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}
	
	/**This method used for upload the Meal School Logo**/
	@Override
	public ServiceResponse uploadMealSchoolLogo(MultipartFile logoFile, Long mealSchoolId, String loggedUser){
		return mealManageAPIDao.uploadMealSchoolLogo(logoFile, mealSchoolId, loggedUser);
	}
	
	/**This method used for Meal Menu & Menu Order status**/
	@Override
	public OrderStatusResp menuOrderStatus(Long studentRecId, String yearMonth) {
		return mealManageAPIDao.menuOrderStatus(studentRecId, yearMonth);
	}
	
	/**This method used for Meal Menu & Menu Order status**/
	@Override
	public OrderStatusResp menuOrderStatusV2(Long studentRecId, String yearMonth) {
		return mealManageAPIDao.menuOrderStatusV2(studentRecId, yearMonth);
	}

	/**This method used for import the school holidays data using .xlsx and .xls file**/
	@Override
	public ServiceResponse importHolidays(MultipartFile multipartFile, Long mealSchoolId, String loggedUser) {
		logger.info("Reading the holidays file and importing data into schoolholidays table");
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<SchoolHoliday> schoolHolidays = excelReadUtil.importHolidays(multipartFile, mealSchoolId);
			serviceResponse = mealManageAPIDao.createHolidays(schoolHolidays, mealSchoolId, loggedUser);
		}catch(Exception e){
			logger.error("Failed to create holidays for the school "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusMessage("Failed to create holidays for the school. Please try again later.");
			serviceResponse.setStatusCode(500);
		}
		return serviceResponse;
	}

	/**This method used for create the school holidays**/
	@Override
	public ServiceResponse createHolidays(List<SchoolHoliday> schoolHolidays, Long mealSchoolId, String loggedUser) {
		return mealManageAPIDao.createHolidays(schoolHolidays, mealSchoolId, loggedUser);
	}

	/**This method used for get all holiday list of school
	 * @throws Exception **/
	@Override
	public List<SchoolHoliday> schoolHolidays(Long mealSchoolId, Date StartDate, Date endDate) throws Exception {
		List<SchoolHoliday> schoolHolidays = new ArrayList<SchoolHoliday>();
		try{
			List<Object[]> objArrList = mealManageAPIDao.schoolHolidays(mealSchoolId, StartDate, endDate);
			for(Object[] holiday : objArrList){
				SchoolHoliday schoolHoliday = new SchoolHoliday();
				schoolHoliday.setHolidayName(holiday[0] != null ? holiday[0].toString() : null);
				schoolHoliday.setHolidayDesc(holiday[1] != null ? holiday[1].toString() : null);
				schoolHoliday.setDateOfHoliday(holiday[2] != null ? holiday[2].toString() : null);
				schoolHoliday.setMealSchoolId(holiday[3] != null ? Long.parseLong(holiday[3].toString()) : null);
				schoolHoliday.setRecId(holiday[4] != null ? Long.parseLong(holiday[4].toString()) : null);
				schoolHolidays.add(schoolHoliday);
			}
		}catch(Exception e){
			logger.error("Failed to get the school holidays");
			throw new Exception("Failed to get the school holidays");
		}
		return schoolHolidays;
	}
	
	/**This method used for map the students data to POJO class who haven't order lunch**/
	private Map<String, List<StudentDetailSendNotif>> studentDetailsMap(List<Object[]> objArray, Map<String, String> gradeCutOffDateTimeMap){
		Map<String, List<StudentDetailSendNotif>> stdDetailsMap = new HashMap<String, List<StudentDetailSendNotif>>();
		List<StudentDetailSendNotif> stdDetails = null;
		StudentDetailSendNotif stdDetail = null;
		for(Object[] obj : objArray){
			stdDetail = new StudentDetailSendNotif();
			stdDetail.setStudentFName(obj[0] != null ? obj[0].toString() : null);
			stdDetail.setStudentLName(obj[1] != null ? obj[1].toString() : null);
			stdDetail.setStudentId(obj[2] != null ? obj[2].toString() : null);
			stdDetail.setGrade(obj[3] != null ? obj[3].toString() : null);
			stdDetail.setCutOffDateTime(gradeCutOffDateTimeMap.get(stdDetail.getGrade()));
			stdDetails = stdDetailsMap.get(obj[4].toString());
			if(stdDetails == null)
				stdDetails = new ArrayList<StudentDetailSendNotif>();
			stdDetails.add(stdDetail);
			stdDetailsMap.put(obj[4].toString(), stdDetails);
			
			if(obj[5] != null && !obj[5].toString().trim().equalsIgnoreCase("")){
				if(!obj[4].toString().equalsIgnoreCase(obj[5].toString())){
					stdDetails = stdDetailsMap.get(obj[5].toString());
					if(stdDetails == null)
						stdDetails = new ArrayList<StudentDetailSendNotif>();
					stdDetails.add(stdDetail);
					stdDetailsMap.put(obj[5].toString(), stdDetails);
				}
			}
		}
		return stdDetailsMap;
	}
	
	/**This method used for map the grade & cutOffDateTime to Map object
	 * @throws ParseException **/
	private Map<String, String> gradeCutOffDateMapBuild(List<Object[]> objArray, String schoolTimezone) throws ParseException{
		Map<String, String> gradeCutOffDateMap = new HashMap<String, String>();
		String reqDateFormat = "dd-MMM-yyyy hh:mm a";
		for(Object[] obj : objArray){
			if(obj[0] != null && obj[1] != null){
				gradeCutOffDateMap.put(obj[1].toString(), du.formatDateToString(df.parse(obj[0].toString()), 
						reqDateFormat, schoolTimezone));
			}
		}
		return gradeCutOffDateMap;
	}

	/**This method used for build the request for send the lunch reminder to parent automatically**/
	@Override
	public void buildAutoReminderRequest() {
		logger.info("Building the request body for execute the method of send lunch reminder");
		ParentsNotificationRequest parentsNotificationRequest = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dt = sdf.format(new Date());
			Long mealSchoolId = null;
			List<Object[]> objArray = mealManageAPIDao.getSchoolIdAndMonthForReminderV2(dt);
			Integer schoolYear = null;
			for(Object[] obj : objArray){
				parentsNotificationRequest = new ParentsNotificationRequest();
				if(obj[0] != null && obj[1] != null){
					mealSchoolId = Long.parseLong(obj[0].toString());
					schoolYear = mealSchoolSchoolYearRepository.schoolYearBySchoolAndDate(mealSchoolId,	sdf.parse(dt));
					if(schoolYear != null && schoolYear != 0){
					parentsNotificationRequest.setSchoolId(mealSchoolId);
					parentsNotificationRequest.setYearMonth(obj[1].toString());
					parentsNotificationRequest.setNotificationType("MealOrderStatus");
					parentsNotificationRequest.setSchoolYear(schoolYear);
					sendNotificationParents(parentsNotificationRequest, true);
					}
				}else{
					logger.info("Did not find school year for the school :"+mealSchoolId);
				}
			} 
			}catch (Exception e) {
				logger.error("Error occurred during execution of the batch job for lunch auto-reminder. "+e.getMessage());
			}
	}

	/**This method used for update the status regarding email send to parent**/
	@Override
	public ServiceResponse updateEmailStatus(String email, Boolean paymentReminderEnable, Boolean lunchReminderEnable,
			Boolean emailIsSubscribe) {
		ServiceResponse serviceResponse = null;
		try{
			serviceResponse = mealManageAPIDao.updateEmailStatus(email, paymentReminderEnable, lunchReminderEnable, emailIsSubscribe);
		}catch(Exception e){
			serviceResponse = new ServiceResponse();
			logger.error("Error occurred during execution of the updateEmailStatus API. "+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to update the email status.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for cancel the menu order**/
	@Override
	public ServiceResponse menuOrderCancel(Long mealSchoolId, MenuOrderCancellationReq menuOrderCancelReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		Set<MealOrderDetails> mealOrderDetailsList = new HashSet<MealOrderDetails>();
		List<MealOrderDetails> mealOrderDetailsFinalList = null;
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			Boolean isItemized = CommonUtil.checkItemized(mealSchool);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			/**Check the date for whom menu need to cancel, that date should be greater than current date**/
			String currentDateVal = du.formatDateToString(new Date(), "yyyy-MM-dd", 
					mealSchool.getSchoolTimezone().toString());
			if(menuOrderCancelReq.getDateList() != null && menuOrderCancelReq.getDateList().size() > 0){

				for(String cancelOrderDate : menuOrderCancelReq.getDateList()){
					if(sdf.parse(currentDateVal).compareTo(sdf.parse(cancelOrderDate)) > 0){
						serviceResponse.setStatus("Failed");
						serviceResponse.setStatusCode(400);
						serviceResponse.setStatusMessage("Order can not cancel for previous: "+cancelOrderDate+". Please select valid date.");
						logger.info("Order can not cancel for previous & current date: "+cancelOrderDate+". Please select valid date.");
						return serviceResponse;
					}
				}
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatusMessage("Please select atleast one date for whom order need to be cancel!");
				logger.info("Please select atleast one date for whom order need to be cancel!");
				return serviceResponse;
			}
			ItemTypeConstants menuType = ItemTypeConstants.Lunch;
			if(menuOrderCancelReq.getIsGradeWise() != null && menuOrderCancelReq.getIsGradeWise()){
				if(menuOrderCancelReq.getGradeList() != null && menuOrderCancelReq.getGradeList().size() > 0)
					mealOrderDetailsList = mealOrderDetailsRepository.ordersByGradesAndMonth(mealSchoolId, 
							menuOrderCancelReq.getGradeList(), menuOrderCancelReq.getYearMonth(), menuType);
				else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatusMessage("Please select atleast one Grade for whom order need to be cancel!");
					logger.info("Please select atleast one Grade for whom order need to be cancel!");
					return serviceResponse;
				}
			}else {
				if(menuOrderCancelReq.getStudentRecordIds() != null && menuOrderCancelReq.getStudentRecordIds().size() > 0)
					mealOrderDetailsList = mealOrderDetailsRepository.ordersByStudentsAndMonth(mealSchoolId, 
							menuOrderCancelReq.getStudentRecordIds(), menuOrderCancelReq.getYearMonth(), menuType);
				else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatusMessage("Please select atleast one Student for whom order need to be cancel!");
					logger.info("Please select atleast one Student for whom order need to be cancel!");
					return serviceResponse;
				}
			}
				
			/**Get the final meal order details data**/
			mealOrderDetailsFinalList = buildCancelMenuOrderUpdateData(mealOrderDetailsList, menuOrderCancelReq);
			try{
				if(mealOrderDetailsFinalList != null && mealOrderDetailsFinalList.size() > 0)
					serviceResponse = mealManageAPIDao.menuOrderCancel(mealSchoolId, mealOrderDetailsFinalList);
				else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("There are no orders for the selected criteria.");
					logger.info("There are no entry for the selected criteria.");
					return serviceResponse;
				}
			}catch(Exception e){
				logger.info("Failed to cancel the menu order for the specific dates due to "+e.getMessage());
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Failed to cancel the menu order and transaction has been rolled back!");
				serviceResponse.setErrorMessage(e.getMessage());
				return serviceResponse;
			}
			if(serviceResponse.getStatusCode() == 200){
				serviceResponse.setStatusMessage("Lunch order cancelled successfully for the date "+
						new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd")
								.parse(menuOrderCancelReq.getDateList().get(0)))+".");
				//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
				CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
				buildPdfAndSendToParent(mealSchool, new HashSet<MealOrderDetails>(mealOrderDetailsFinalList), menuOrderCancelReq,countryDetail.getCurrencySymbol(), isItemized, countryDetail.getDateFormat());
				serviceResponse.setStatus("Success");
			}else{
				serviceResponse.setStatusMessage("Failed to cancel the menu order for the date"+
						new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd")
								.parse(menuOrderCancelReq.getDateList().get(0)))+".");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
			}
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to cancel the order for the meal school id :"+mealSchoolId+" grades:"+menuOrderCancelReq.getGradeList()
			+" student record ids: "+menuOrderCancelReq.getStudentRecordIds()+" month: "+menuOrderCancelReq.getYearMonth()+" due to error : "+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to cancel the menu order. Please try again later!");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for cancel the menu order**/
	@Override
	public ServiceResponse menuOrderCancelV2(Long mealSchoolId, MenuOrderCancellationReq menuOrderCancelReq, ItemTypeConstants menuType) {
		ServiceResponse serviceResponse = new ServiceResponse();
		Set<MealOrderDetails> mealOrderDetailsList = new HashSet<MealOrderDetails>();
		List<MealOrderDetails> mealOrderDetailsFinalList = null;
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			Boolean isItemized = CommonUtil.checkItemized(mealSchool);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			/**Check the date for whom menu need to cancel, that date should be greater than current date**/
			String currentDateVal = du.formatDateToString(new Date(), "yyyy-MM-dd", 
					mealSchool.getSchoolTimezone().toString());
			if(menuOrderCancelReq.getDateList() != null && menuOrderCancelReq.getDateList().size() > 0){
				for(String cancelOrderDate : menuOrderCancelReq.getDateList()){
					if(sdf.parse(currentDateVal).compareTo(sdf.parse(cancelOrderDate)) > 0){
						serviceResponse.setStatus("Failed");
						serviceResponse.setStatusCode(400);
						serviceResponse.setStatusMessage("Order can not cancel for previous date: "+cancelOrderDate+". Please select valid date.");
						logger.info("Order can not cancel for previous date: "+cancelOrderDate+". Please select valid date.");
						return serviceResponse;
					}
				}
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatusMessage("Please select atleast one date for whom order need to be cancel!");
				logger.info("Please select atleast one date for whom order need to be cancel!");
				return serviceResponse;
			}
			
			if(menuOrderCancelReq.getIsGradeWise() != null && menuOrderCancelReq.getIsGradeWise()){
				if(menuOrderCancelReq.getGradeList() != null && menuOrderCancelReq.getGradeList().size() > 0)
					mealOrderDetailsList = mealOrderDetailsRepository.ordersByGradesAndMonth(mealSchoolId, 
							menuOrderCancelReq.getGradeList(), menuOrderCancelReq.getYearMonth(), menuType);
				else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatusMessage("Please select atleast one Grade for whom order need to be cancel!");
					logger.info("Please select atleast one Grade for whom order need to be cancel!");
					return serviceResponse;
				}
			}else {
				if(menuOrderCancelReq.getStudentRecordIds() != null && menuOrderCancelReq.getStudentRecordIds().size() > 0)
					mealOrderDetailsList = mealOrderDetailsRepository.ordersByStudentsAndMonth(mealSchoolId, 
							menuOrderCancelReq.getStudentRecordIds(), menuOrderCancelReq.getYearMonth(), menuType);
				else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatusMessage("Please select atleast one Student for whom order need to be cancel!");
					logger.info("Please select atleast one Student for whom order need to be cancel!");
					return serviceResponse;
				}
			}
			String itemType = CommonUtil.getItemType(menuType);
			/**Get the final meal order details data**/
			mealOrderDetailsFinalList = buildCancelMenuOrderUpdateDataV2(mealOrderDetailsList, menuOrderCancelReq, mealSchool, itemType, isItemized);
			try{
				if(mealOrderDetailsFinalList != null && mealOrderDetailsFinalList.size() > 0)
					serviceResponse = mealManageAPIDao.menuOrderCancel(mealSchoolId, mealOrderDetailsFinalList);
				else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("There are no orders for the selected criteria.");
					logger.info("There are no entry for the selected criteria.");
					return serviceResponse;
				}
			}catch(Exception e){
				logger.info("Failed to cancel the menu order for the specific dates due to "+e.getMessage());
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Failed to cancel the menu order and transaction has been rolled back!");
				serviceResponse.setErrorMessage(e.getMessage());
				return serviceResponse;
			}
			if(serviceResponse.getStatusCode() == 200){
				serviceResponse.setStatusMessage("Lunch order cancelled successfully for the date "+
						new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd")
								.parse(menuOrderCancelReq.getDateList().get(0)))+".");
				//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchool.getCountryCode());
				CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
				buildPdfAndSendToParent(mealSchool, new HashSet<MealOrderDetails>(mealOrderDetailsFinalList), menuOrderCancelReq,countryDetail.getCurrencySymbol(), isItemized, countryDetail.getDateFormat());
				serviceResponse.setStatus("Success");
			}else{
				serviceResponse.setStatusMessage("Failed to cancel the menu order for the date"+
						new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd")
								.parse(menuOrderCancelReq.getDateList().get(0)))+".");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatus("Failed");
			}
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to cancel the order for the meal school id :"+mealSchoolId+" grades:"+menuOrderCancelReq.getGradeList()
			+" student record ids: "+menuOrderCancelReq.getStudentRecordIds()+" month: "+menuOrderCancelReq.getYearMonth()+" due to error : "+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to cancel the menu order. Please try again later!");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get all the admin emails by meal school id**/
	@Override
	public List<String> adminEmailsBySchoolId(Long mealSchoolId) {
		List<String> emailIds = mealSchoolRepository.allAdminEmails(mealSchoolId);
		logger.info("Returning the all admin emails for whom noification need to send");
		return emailIds;
	}

	/**This method used for insert/update school year setup details**/
	@Override
	@Transactional
	public ServiceResponse schoolYearSetup(SchoolYear schoolYear, Long mealSchoolId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			schoolYear.setMealSchool(mealSchool);
			if(schoolYear.getSchoolPdfBase64() != null && schoolYear.getSchoolPdfBase64().length() > 0){
				String finalPdfPath = uploadSchoolPdf(schoolYear, mealSchoolId);
				schoolYear.setSchoolPdfUrl(finalPdfPath);
			}				
			schoolYearRepository.save(schoolYear);
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("School year setup details request has been proceed successfully.");
			serviceResponse.setStatus("Success");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to proceed the school year setup request due to "+e.getMessage());
			serviceResponse.setStatus("Fail");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to proceed the school year setup request.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	private List<MealOrderDetails> buildCancelMenuOrderUpdateData(Set<MealOrderDetails> mealOrderDetailsList, 
			MenuOrderCancellationReq menuOrderCancelReq) throws Exception{
		List<MealOrderDetails> mealOrderDetailsFinalList = new ArrayList<MealOrderDetails>();
		List<SchoolMeal> schoolMealList = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Double totalReducedPrice = 0.0;
		Double totalPrice = 0.0;
		Double orderAmount = 0.0;
		for(MealOrderDetails mealOrderDetails : mealOrderDetailsList){
			schoolMealList = new ArrayList<>(mealOrderDetails.getSchoolMeals());
			if(schoolMealList.stream().filter(p ->  p.getMealMenu().getStart() != null 
					&& menuOrderCancelReq.getDateList().contains(sdf1.format(p.getMealMenu().getStart())) &&  
					p.getMealMenu().getType().toString().equalsIgnoreCase("MEAL")).collect(
							Collectors.toCollection(ArrayList::new)).size() > 0){
				schoolMealList = schoolMealList.stream().filter(p ->  p.getMealMenu().getStart() != null 
						&& (!menuOrderCancelReq.getDateList().contains(sdf1.format(p.getMealMenu().getStart())) || 
						p.getMealMenu().getType().toString().equalsIgnoreCase("HOLIDAY"))).collect(
								Collectors.toCollection(ArrayList::new));
				mealOrderDetails.setSchoolMeals(new HashSet<>(schoolMealList));
				//mealOrderDetails.setModifiedBy(menuOrderCancelReq.getLoggedUser());
				//mealOrderDetails.setModifiedOn(new Date());
				mealOrderDetails.setLoggedUser(menuOrderCancelReq.getLoggedUser());
				mealOrderDetails.setCrudOperationVal(2);
				mealOrderDetails.setCancellationNote(menuOrderCancelReq.getCancellationNote());
				mealOrderDetails.setCancellationDates(String.join(",", menuOrderCancelReq.getDateList()));
				mealOrderDetails.setLoggedUser(menuOrderCancelReq.getLoggedUser());
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
				if(schoolMealList.get(0).getMealSchool().getModuleAccess() != null && schoolMealList.get(0).getMealSchool().getModuleAccess().get("Instant Payment for Orders") != null && 
						schoolMealList.get(0).getMealSchool().getModuleAccess().get("Instant Payment for Orders").equalsIgnoreCase("Yes") 
						&& mealOrderDetails.getPaymentStatus() != null && mealOrderDetails.getPaymentStatus()){
					Double refundAmt = mealOrderDetails.getOrderAmount() - orderAmount;
					StudentMealOrdersV2 stdOrder = new StudentMealOrdersV2();
					stdOrder.setStudentId(mealOrderDetails.getStudentUser().getUserId());
					mealManageAPIDao.addInstantPayTrx(menuOrderCancelReq.getLoggedUser(), schoolMealList.get(0).getMealSchool(), stdOrder, refundAmt,mealOrderDetails.getMenuType());
				}
				mealOrderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", totalPrice)));
				mealOrderDetails.setOrderAmount(Double.parseDouble(String.format("%.2f", orderAmount)));
				mealOrderDetailsFinalList.add(mealOrderDetails);
			}			
		}
		return mealOrderDetailsFinalList;
	}
	
	private List<MealOrderDetails> buildCancelMenuOrderUpdateDataV2(Set<MealOrderDetails> mealOrderDetailsList, 
			MenuOrderCancellationReq menuOrderCancelReq, MealSchool mealSchool, String itemType, Boolean isItemized) throws Exception{
		List<MealOrderDetails> mealOrderDetailsFinalList = new ArrayList<MealOrderDetails>();
		List<MealCalendar> calendars = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Double totalReducedPrice = 0.0;
		Double totalPrice = 0.0;
		Double extraItemPrice = 0.0;
		Double orderAmount = 0.0;
		for(MealOrderDetails mealOrderDetails : mealOrderDetailsList){
			calendars = new ArrayList<>(mealOrderDetails.getMealCalendars());
			if(calendars.stream().filter(p ->  p.getDate() != null 
					&& menuOrderCancelReq.getDateList().contains(sdf1.format(p.getDate())) &&  
					p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType)).collect(
							Collectors.toCollection(ArrayList::new)).size() > 0){
				calendars = calendars.stream().filter(p ->  p.getDate() != null 
						&& (!menuOrderCancelReq.getDateList().contains(sdf1.format(p.getDate())) || 
						p.getMenuItem().getCategory().toString().equalsIgnoreCase("HOLIDAY"))).collect(
								Collectors.toCollection(ArrayList::new));
				mealOrderDetails.setMealCalendars(new HashSet<>(calendars));
				//mealOrderDetails.setModifiedBy(menuOrderCancelReq.getLoggedUser());
				//mealOrderDetails.setModifiedOn(new Date());
				mealOrderDetails.setLoggedUser(menuOrderCancelReq.getLoggedUser());
				mealOrderDetails.setCrudOperationVal(2);
				mealOrderDetails.setCancellationNote(menuOrderCancelReq.getCancellationNote());
				mealOrderDetails.setCancellationDates(String.join(",", menuOrderCancelReq.getDateList()));
				mealOrderDetails.setLoggedUser(menuOrderCancelReq.getLoggedUser());
				mealOrderDetails.setItems_count((int) calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
						p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType)).count());
				totalPrice = calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
						p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType)).mapToDouble(o -> o.getPrice()).sum();
				totalReducedPrice = calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
						p.getMenuItem().getCategory().toString().equalsIgnoreCase(itemType) && p.getReducedPrice() != null)
						.mapToDouble(o -> o.getReducedPrice()).sum();
				extraItemPrice = calendars.stream().filter(p -> p.getMenuItem().getCategory() != null && 
						p.getMenuItem().getCategory().toString().equalsIgnoreCase("EXTRA")).mapToDouble(o -> o.getPrice()).sum();
				if(mealOrderDetails.getIsEligibleForFreeMeal())
					orderAmount = 0.0;
				else if(mealOrderDetails.getIsEligibleForReducedPrice())
					orderAmount = totalReducedPrice;
				else
					orderAmount = totalPrice;
				orderAmount = orderAmount+extraItemPrice;
				totalPrice = totalPrice+extraItemPrice;
				if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Instant Payment for Orders") != null && 
						mealSchool.getModuleAccess().get("Instant Payment for Orders").equalsIgnoreCase("Yes") 
						&& mealOrderDetails.getPaymentStatus() != null && mealOrderDetails.getPaymentStatus()){
					Double refundAmt = mealOrderDetails.getOrderAmount() - orderAmount;
					StudentMealOrdersV2 stdOrder = new StudentMealOrdersV2();
					stdOrder.setStudentId(mealOrderDetails.getStudentUser().getUserId());
					if(refundAmt < 0)
						stdOrder.setWalletAmt(-(refundAmt));
					mealManageAPIDao.addInstantPayTrx(menuOrderCancelReq.getLoggedUser(), mealSchool, stdOrder, refundAmt, mealOrderDetails.getMenuType());
				}
				mealOrderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", totalPrice)));
				mealOrderDetails.setOrderAmount(Double.parseDouble(String.format("%.2f", orderAmount)));
				mealOrderDetails.setIsEligForDiscount(false);
				mealOrderDetailsFinalList.add(mealOrderDetails);
			}			
		}
		return mealOrderDetailsFinalList;
	}
	
	private void buildPdfAndSendToParent(MealSchool mealSchool, Set<MealOrderDetails> mealOrderDetailsList, 
			MenuOrderCancellationReq menuOrderCancelReq,String currencySymbol, Boolean isItemized, String dateFormat) throws Exception{
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
			orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, schoolName, menuOrderCancelReq.getLoggedUser(), logoLink, 
								parentUser, priEmailIsSubscribe, altEmailIsSubscribe, schoolTimezone,currencySymbol, mealSchool.getContactPEmail(),
								null, isItemized, dateFormat,CommonUtil.getNonSchoolDays(mealSchool),false);
		}
	}

	/**This method used for get the school info by parent email**/
	@Override
	public ServiceResponse schoolInfoByParentEmail(String parentEmail, String currentDate, Boolean isVersion2) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			Set<MealSchool> mealSchools = studentUserRepository.schoolsByEmail(parentEmail);
			List<SchoolDetailsInfo> schoolDetailsInfos = new ArrayList<SchoolDetailsInfo>();
			SchoolDetailsInfo schoolDetailsInfo = null;
			List<SchoolYear> schoolYears = null;
			List<Map<String, Object>> schoolYearsInfos = null;
			Map<String, Object> schoolYearsInfo = null;
			List<EventInfo> eventInfos = null;
			List<EventInfo> eventsFinal = null;
    		Date currentDt = null;
    		if(currentDate != null && !currentDate.trim().isEmpty())
    			currentDt = df.parse(currentDate+" 00:00:00");
    		else
    			currentDt = new Date();
			for(MealSchool mealSchool : mealSchools){
				eventsFinal = new ArrayList<EventInfo>();
	    		Integer schoolYearVal = 2000;
				schoolDetailsInfo = new SchoolDetailsInfo();
				schoolDetailsInfo.setContactPEmail(mealSchool.getContactPEmail());
				schoolDetailsInfo.setContactPName(mealSchool.getContactPName());
				schoolDetailsInfo.setContactPPhone(mealSchool.getContactPPhone());
				schoolDetailsInfo.setIsPaymentEnabled(mealSchool.getIsPaymentEnabled());
				schoolDetailsInfo.setLogoLink(mealSchool.getLogoLink());
				schoolDetailsInfo.setTrxFeeOnSchool(mealSchool.isTrxFeeOnSchool());
				schoolDetailsInfo.setMealSchoolId(mealSchool.getSchoolId());
				schoolDetailsInfo.setSchoolName(mealSchool.getSchoolName());
				schoolDetailsInfo.setNonSchoolDays(CommonUtil.getNonSchoolDays(mealSchool));
				schoolDetailsInfo.setSchoolTimezone(mealSchool.getSchoolTimezone());
				schoolDetailsInfo.setStripeAccountId(mealSchool.getStripeAccountId());
				schoolDetailsInfo.setSubdomain(mealSchool.getSubdomain());
				schoolDetailsInfo.setPaymentGateways(mealSchool.getPaymentGateways());
				CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
				schoolDetailsInfo.setCurrencySymbol(countryDetail.getCurrencySymbol());
				schoolDetailsInfo.setIsdCode(countryDetail.getIsdCode());
				schoolDetailsInfo.setSchoolOtherInfo(countryDetail.getOtherInfoJson());
				schoolDetailsInfo.setGradesMap(countryDetail.getGradesMap());
				schoolDetailsInfo.setTierName(mealSchool.getTierName());
				schoolDetailsInfo.setModuleAccess(mealSchool.getModuleAccess());
				schoolDetailsInfo.setDateFormat(countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy");
				schoolDetailsInfo.setCountryCode(countryDetail.getCountryCode());
				schoolDetailsInfo.setPhoneValidation(countryDetail.getPhoneValidation());
				schoolYears = schoolYearRepository.findByMealSchoolSchoolId(mealSchool.getSchoolId());
				schoolYearsInfos = new ArrayList<Map<String, Object>>();
				for(SchoolYear schoolYear : schoolYears){
					schoolYearsInfo = new HashMap<String, Object>();
					schoolYearsInfo.put("schoolYear", schoolYear.getSchoolYear());
					schoolYearsInfo.put("sessionStartDateTime", schoolYear.getSessionStartDateTime());
					schoolYearsInfo.put("sessionEndDateTime", schoolYear.getSessionEndDateTime());
					schoolYearsInfo.put("name", schoolYear.getName());
					schoolYearsInfo.put("schoolPdfUrl", schoolYear.getSchoolPdfUrl());
					if(currentDt.compareTo(schoolYear.getSessionStartDateTime()) >=0 && currentDt.compareTo(schoolYear.getSessionEndDateTime()) <= 0)
						schoolYearVal = schoolYear.getSchoolYear();
					schoolYearsInfos.add(schoolYearsInfo);
				}
				schoolDetailsInfo.setSchoolYears(schoolYearsInfos);
				String currentYearMonth = du.formatDateToString(new Date(), "yyyyMM", 
						mealSchool.getSchoolTimezone().toString());
				if(isVersion2 != null && isVersion2){
					List<MealCalendarSummary> summaries = new ArrayList<>(summaryRepo.findBySchoolSchoolIdAndYearMonthAndMealType(
							mealSchool.getSchoolId(), currentYearMonth, ItemTypeConstants.Breakfast));
					if(summaries.size() > 0)
						schoolDetailsInfo.setIsBreakfastAvailable(true);
				}else{
					List<BreakfastMaster> breakfastMastersList = new ArrayList<>(breakfastMasterRepository
		    				.findByMealSchoolSchoolIdAndYearMonth(mealSchool.getSchoolId(), currentYearMonth));
		    		if(breakfastMastersList.size() > 0)
		    			schoolDetailsInfo.setIsBreakfastAvailable(true);
				}
	    		eventInfos = eventInfoRepo.getSchoolQualifyEvents(mealSchool.getSchoolId(),currentDt, schoolYearVal);
	    		if(eventInfos != null && eventInfos.size() > 0){
	    			for(EventInfo event : eventInfos){
	    				List<BigInteger> studentsRecId = eventInfoRepo.notPaidStdRecIds(parentEmail, mealSchool.getSchoolId(),
	    						schoolYearVal, event.getRecId());
	    				if(studentsRecId != null && studentsRecId.size() > 0){
	    					event.setNotPaidStudentRecId(studentsRecId);
	    					event.setMealSchool(null);
	    					eventsFinal.add(event);
	    				}
	    			}
	    		}
	    			//eventInfos.stream().forEach(event -> event.setMealSchool(null));
	    		schoolDetailsInfo.setEvents(eventsFinal);
				schoolDetailsInfos.add(schoolDetailsInfo);
				schoolDetailsInfo.setPkgRegisteredStds(packageRepo.getPackageRegisteredStds(mealSchool.getSchoolId(), schoolYearVal, parentEmail));
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved the school details successfully.");
			serviceResponse.setSchoolDetailsInfos(schoolDetailsInfos);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to get the school info along with other details by parent email due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get the school info along with other details by parent email.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for generate the stripe account access link
	 * @throws Exception **/
	@Override
	public ServiceResponse stripeAcAccessLink(String accountId) throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			//serviceResponse.setStripeAcLink(stripeUtil.stripeAccAccessLink(accountId));
			serviceResponse.setStripeAcLink("https://dashboard.stripe.com/login");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Stripe account access link created successfully.");
			serviceResponse.setStatus("Success");
		}catch(Exception e){
			logger.error("Failed to generate the stripe account access link due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the stripe account access link due to "+e.getMessage()+".");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for update the free / reduced lunch eligibility status update**/
	@Override
	public ServiceResponse freeReducedLunchEligUpdate(FreeReducedLunchEligReq freeReducedLunchEligReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			if(freeReducedLunchEligReq.getIsFreeLunch() == null && freeReducedLunchEligReq.getIsBeforeCare() == null){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(400);
				serviceResponse.setStatusMessage("isFreeLunch or isBeforeCare shouldn't be null.");
				return serviceResponse;
			}
			serviceResponse = mealManageAPIDao.freeReducedLunchEligUpdate(freeReducedLunchEligReq);
		}catch(Exception e){
			logger.error("Failed to update the free / reduced price status");
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update the free / reduced price/before care status due to "+e.getMessage()+".");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for submit the free/reduced meal program application**/
	@Override
	@Transactional
	public ServiceResponse householdApplication(HouseholdApplicationForFRM householdApplicationForFRM) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String loggedUser = "";
			if(SecurityContextHolder.getContext().getAuthentication() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();			
			householdApplicationForFRM.setLoggedUser(loggedUser);
			if(householdApplicationForFRM.getStatus().equalsIgnoreCase("pending")){
				List<HouseholdAppOtherInfo> householdAppOtherInfos = updateStudentEligibility(
						householdApplicationForFRM.getHouseholdAppInfo());
				householdApplicationForFRM.setHouseholdAppInfo(householdAppOtherInfos);
			}
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			String arrayToJson = objectMapper.writeValueAsString(householdApplicationForFRM.getHouseholdAppInfo());
			householdApplicationForFRM.setOtherInfo(arrayToJson);
			if(householdApplicationForFRM.getDeclinedReasonList() != null 
					&& householdApplicationForFRM.getDeclinedReasonList().size() > 0){
				String deniedReason = objectMapper.writeValueAsString(householdApplicationForFRM.getDeclinedReasonList());
				householdApplicationForFRM.setDeclinedReason(deniedReason);
			}
			if(householdApplicationForFRM.getIncompleteReasonList() != null 
					&& householdApplicationForFRM.getIncompleteReasonList().size() > 0){
				String incReason = objectMapper.writeValueAsString(householdApplicationForFRM.getIncompleteReasonList());
				householdApplicationForFRM.setIncompleteReason(incReason);
			}
			serviceResponse = mealManageAPIDao.householdApplication(householdApplicationForFRM);
		}catch(Exception e){
			if(e.getMessage() != null && e.getMessage().contains("ConstraintViolationException")){
				serviceResponse.setStatusCode(409);
				serviceResponse.setStatusMessage("Failed to submit application due to duplicate application entry.");
			}else{
				serviceResponse.setStatusCode(500);
				serviceResponse.setStatusMessage("Failed to submit application.");
			}
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+", error:"+e.getMessage());
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}
	
	/**This method used for get the household applications by mealSchoolId and schoolYear
	 * @throws Exception **/
	@Override
	public List<HouseholdApplicationForFRM> getHouseholdApp(Long mealSchoolId, Integer schoolYear, Long districtId) throws Exception {
		List<HouseholdApplicationForFRM> householdApplicationForFRMFinal = new ArrayList<HouseholdApplicationForFRM>();
		try{
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	    	List<Long> schoolIds = new ArrayList<>();
	    	if((mealSchoolId == null || mealSchoolId == 0) && districtId != null)
	    		schoolIds = mealSchoolRepository.getSchoolIdsByDistrictId(districtId);
	    	List<HouseholdApplicationForFRM> householdApplicationForFRMs = null;
	    	if(mealSchoolId != null && mealSchoolId != 0)
	    		householdApplicationForFRMs = householdAppForFRMRepository.findByMealSchoolIdAndSchoolYear(mealSchoolId, schoolYear);
	    	else
	    		householdApplicationForFRMs = householdAppForFRMRepository.findByMealSchoolIdInAndSchoolYear(schoolIds, schoolYear);
	    	if(householdApplicationForFRMs != null)
				for(HouseholdApplicationForFRM householdApplicationForFRM : householdApplicationForFRMs){
					TypeReference<List<HouseholdAppOtherInfo>> mapType = new TypeReference<List<HouseholdAppOtherInfo>>() {};
			    	List<HouseholdAppOtherInfo> jsonToOtherInfoList = objectMapper.readValue(householdApplicationForFRM.getOtherInfo(), mapType);
			    	if(householdApplicationForFRM.getDeclinedReason() != null && !householdApplicationForFRM.getDeclinedReason().trim().isEmpty()){
			    		TypeReference<List<HouseholdAppDeclinedReason>> mapTypeDenied = new TypeReference<List<HouseholdAppDeclinedReason>>() {};
				    	List<HouseholdAppDeclinedReason> jsonToDeniedInfoList = objectMapper.readValue(householdApplicationForFRM.getDeclinedReason(), mapTypeDenied);
				    	householdApplicationForFRM.setDeclinedReasonList(jsonToDeniedInfoList);
			    	}	
			    	if(householdApplicationForFRM.getIncompleteReason() != null && !householdApplicationForFRM.getIncompleteReason().trim().isEmpty()){
			    		TypeReference<List<HouseholdIncompleteApp>> mapTypeINC = new TypeReference<List<HouseholdIncompleteApp>>() {};
				    	List<HouseholdIncompleteApp> jsonToINCInfoList = objectMapper.readValue(householdApplicationForFRM.getIncompleteReason(), mapTypeINC);
				    	householdApplicationForFRM.setIncompleteReasonList(jsonToINCInfoList);
			    	}	
			    	householdApplicationForFRM.setHouseholdAppInfo(jsonToOtherInfoList);
			    	householdApplicationForFRMFinal.add(householdApplicationForFRM);
				}
		}catch(Exception e){
			logger.error("Failed to get the household application due to "+e.getMessage());
			throw new Exception("Failed to get the household application due to "+e.getMessage());
		}
		return householdApplicationForFRMFinal;
	}
	
	/**This method used for get the students household app info**/
	@Override
	public ServiceResponse getStdAppInfo(Long mealSchoolId, Integer schoolYear, List<String> parentEmails) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	    	List<HouseholdApplicationForFRM> householdApplicationForFRMs = null;
	    	List<EligAppResp> eligAppResps = new ArrayList<>();
	    	EligAppResp eligAppResp = null;
	    	householdApplicationForFRMs = householdAppForFRMRepository.getAppInfo(mealSchoolId, schoolYear, parentEmails);
	    	if(householdApplicationForFRMs != null)
	    		for(HouseholdApplicationForFRM householdApplicationForFRM : householdApplicationForFRMs){
					TypeReference<List<HouseholdAppOtherInfo>> mapType = new TypeReference<List<HouseholdAppOtherInfo>>() {};
			    	List<HouseholdAppOtherInfo> jsonToOtherInfoList = objectMapper.readValue(householdApplicationForFRM.getOtherInfo(), mapType);	    	
			    	if(jsonToOtherInfoList != null)
			    		for(HouseholdAppOtherInfo childApp : jsonToOtherInfoList){
			    			if(childApp.getStudentRecId() != null && childApp.getStudentRecId() != 0){
			    				eligAppResp = new EligAppResp();
			    				eligAppResp.setAppId(householdApplicationForFRM.getApplicationId());
			    				eligAppResp.setAppStatus(householdApplicationForFRM.getStatus());
			    				eligAppResp.setEligibility(childApp.getIsFreeMeal() == null ? "Regular" : (childApp.getIsFreeMeal() ? "Free" : "Reduced"));
			    				eligAppResp.setGrade(childApp.getGrade());
			    				eligAppResp.setStdFirstName(childApp.getFname());
			    				eligAppResp.setStdLastName(childApp.getLname());
			    				eligAppResp.setStdRecId(childApp.getStudentRecId());
			    				eligAppResps.add(eligAppResp);
			    			}
			    		}
				}
	    	serviceResponse.setResponse(eligAppResps);
	    	serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved household app info successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get the household app info.");
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for get the household application by applicationId
	 * @throws Exception **/
	@Override
	public HouseholdApplicationForFRM getHouseholdAppById(Long applicationId) throws Exception {
		HouseholdApplicationForFRM householdApplicationForFRMFinal = new HouseholdApplicationForFRM();
		try{
			ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	    	householdApplicationForFRMFinal = householdAppForFRMRepository.findOne(applicationId);
			if (householdApplicationForFRMFinal != null) {
				TypeReference<List<HouseholdAppOtherInfo>> mapType = new TypeReference<List<HouseholdAppOtherInfo>>() {};
				List<HouseholdAppOtherInfo> jsonToOtherInfoList = objectMapper
						.readValue(householdApplicationForFRMFinal.getOtherInfo(), mapType);
				householdApplicationForFRMFinal.setHouseholdAppInfo(jsonToOtherInfoList);
				if(householdApplicationForFRMFinal.getDeclinedReason() != null && !householdApplicationForFRMFinal.getDeclinedReason().trim().isEmpty()){
					TypeReference<List<HouseholdAppDeclinedReason>> mapTypeDenied = new TypeReference<List<HouseholdAppDeclinedReason>>() {};
			    	List<HouseholdAppDeclinedReason> jsonToDeniedInfoList = objectMapper.readValue(householdApplicationForFRMFinal.getDeclinedReason(), mapTypeDenied);
			    	householdApplicationForFRMFinal.setDeclinedReasonList(jsonToDeniedInfoList);
				}
				if(householdApplicationForFRMFinal.getIncompleteReason() != null && !householdApplicationForFRMFinal.getIncompleteReason().trim().isEmpty()){
		    		TypeReference<List<HouseholdIncompleteApp>> mapTypeINC = new TypeReference<List<HouseholdIncompleteApp>>() {};
			    	List<HouseholdIncompleteApp> jsonToINCInfoList = objectMapper.readValue(householdApplicationForFRMFinal.getIncompleteReason(), mapTypeINC);
			    	householdApplicationForFRMFinal.setIncompleteReasonList(jsonToINCInfoList);
		    	}
			}
		}catch(Exception e){
			logger.error("Failed to get the household application due to "+e.getMessage());
			throw new Exception("Failed to get the household application due to "+e.getMessage());
		}
		return householdApplicationForFRMFinal;
	}
	
	/**This method used for recommend the student eligibility**/
	private List<HouseholdAppOtherInfo> updateStudentEligibility(List<HouseholdAppOtherInfo> householdAppOtherInfos){
		Boolean studentPrgEligStatus = null;
		Boolean stdFreeEligibility = false;
		String decisionReason = "";
		String actualPrg = "";
		List<EligibilityCode>  eligibilityCodes = eligibilityCodeRepo.findByIsActive(true);
		Map<String, List<EligibilityCode>> eligMap = eligibilityCodes.stream().collect(Collectors.groupingBy(EligibilityCode::getCode));
		HouseholdAppOtherInfo result = householdAppOtherInfos.stream().filter(x -> "Adult".equalsIgnoreCase(x.getPersonType()) && 
				x.getFilledTheApplication() != null && x.getFilledTheApplication()).findAny().orElse(null);  
		if(result != null && result.getAssistancePrograms() != null){
			for(AssistanceProgram assistanceProgram : result.getAssistancePrograms()){
				if(assistanceProgram.getCaseNumber() != null && !assistanceProgram.getCaseNumber().trim().equalsIgnoreCase("")){
					studentPrgEligStatus = true;
					switch (assistanceProgram.getAccronym()) {
						/*case "SNAP": decisionReason = "DC";
									category = eligMap.get(decisionReason).get(0).getCodeDesc(); break;
						case "TANF": decisionReason = "DCT";
									category = eligMap.get(decisionReason).get(0).getCodeDesc(); break;
						case "FDPIR": decisionReason = "DCF";
									category = eligMap.get(decisionReason).get(0).getCodeDesc(); break;*/
						case "SNAP": actualPrg = "SNAP"; break;
						case "TANF": actualPrg = "TANF"; break;
						case "FDPIR": actualPrg = "FDPIR"; break;
					}
					decisionReason = "FT";
			}
		}	
		}
		List<HouseholdAppOtherInfo> householdAppOtherInfoFinal = new ArrayList<HouseholdAppOtherInfo>();
		List<HouseholdAppOtherInfo> stdEligInfo = new ArrayList<HouseholdAppOtherInfo>();
		Double totalIncome = 0.0;
		int householdSize = 0;
		for(HouseholdAppOtherInfo householdAppOtherInfo : householdAppOtherInfos){
			if(householdAppOtherInfo.getIncome() != null){
				for(IncomeInfo incomeInfo : householdAppOtherInfo.getIncome()){
					if(incomeInfo.getAmount() != null && incomeInfo.getFrequency() != null){
						totalIncome = totalIncome+(incomeInfo.getAmount()*Integer.parseInt(incomeInfo.getFrequency()));
					}
				}
			}
			if(householdAppOtherInfo.getPersonType().equalsIgnoreCase("Student")){
				stdFreeEligibility = false;
				String stdDecReas = null;
				if(studentPrgEligStatus != null && studentPrgEligStatus){
					householdAppOtherInfo.setIsFreeMeal(true);
					stdDecReas = decisionReason;
					stdFreeEligibility = true;
				}else if(householdAppOtherInfo.getLiveUnderFosterCare() != null && householdAppOtherInfo.getLiveUnderFosterCare()){
					householdAppOtherInfo.setIsFreeMeal(true);
					stdFreeEligibility = true;
					stdDecReas = decisionReason;
					actualPrg = "Foster";
				}
				if(stdFreeEligibility){
					householdAppOtherInfo.setDecisionReason(stdDecReas);
					householdAppOtherInfo.setCategory(eligMap.get(stdDecReas).get(0).getCodeDesc());
					householdAppOtherInfo.setActualPrg(actualPrg);
					householdAppOtherInfoFinal.add(householdAppOtherInfo);
				}else{
					if(householdAppOtherInfo.getHomelessOrMigrantOrRunaway() != null && householdAppOtherInfo.getHomelessOrMigrantOrRunaway())
						householdAppOtherInfo.setActualPrg("Homeless");
					stdEligInfo.add(householdAppOtherInfo);		
				}
			}else
				householdAppOtherInfoFinal.add(householdAppOtherInfo);
		}
		Boolean isFreeEligStatus = null;
		decisionReason = "D";
		if(stdEligInfo != null && stdEligInfo.size() > 0){
			householdSize = householdAppOtherInfos.size();
			
			Double FederalIncomeLimit = (double) (federalPovertyLimit+(householdSize-1)*additionalMemberIncr);
			
			Double reducedEligLimit = FederalIncomeLimit * 1.85 ;
			
			Double freeEligLimit = FederalIncomeLimit * 1.30 ;
			
			
			if ((totalIncome <= freeEligLimit)) {
				isFreeEligStatus = true;
				decisionReason="FI";
	        } else if (totalIncome <= reducedEligLimit && totalIncome >= freeEligLimit ) {
	        	isFreeEligStatus = false;
				decisionReason="R";
	        }
		}
		for(HouseholdAppOtherInfo householdAppOtherInf : stdEligInfo){
			householdAppOtherInf.setIsFreeMeal(isFreeEligStatus);
			householdAppOtherInf.setDecisionReason(decisionReason);
			householdAppOtherInf.setCategory(eligMap.get(decisionReason).get(0).getCodeDesc());
			householdAppOtherInfoFinal.add(householdAppOtherInf);
		}
		return householdAppOtherInfoFinal;
	}
	
	/**This method used for upload school pdf file and update S3 link
	 * @throws Exception **/
	private String uploadSchoolPdf(SchoolYear schoolYear, Long mealSchoolId) throws Exception{
		byte[] schoolPdf = Base64.decodeBase64(schoolYear.getSchoolPdfBase64());
		String filePath = "";
		File convFile = new File("SchoolPdf_" + mealSchoolId+"_"+schoolYear.getSchoolYear() + ".pdf");
		FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(schoolPdf);
	    fos.flush();
	    fos.close();
		filePath = convFile.getAbsolutePath();
		String finalFilePath = awsUtility.fileUploadPath(filePath, "schoolPdfUploadFilePath");
		awsUtility.uploadFileToAWSS3Bucket(filePath, "schoolPdfFile");
		logger.info("School Pdf files has been uploaded successfully");
		return finalFilePath;
	}

	/**This method used for get the onboarded schools info**/
	@Override
	public List<Map<String, String>> onboardedSchoolsInfo(String currentDate) {
		List<Map<String, String>> schoolsInfoList = new ArrayList<Map<String, String>>();
		try{
			List<Object[]> resp = mealManageAPIDao.getOnboardedSchools(currentDate);
			for(Object[] obj : resp){
				if(obj[0] != null && obj[1] != null && obj[2] != null){
					Map<String, String> schoolInfo = new HashMap<String, String>();
					schoolInfo.put("schoolId", obj[0].toString());
					schoolInfo.put("schoolName", obj[1].toString());
					schoolInfo.put("schoolYear", obj[2].toString());
					schoolInfo.put("schoolPdfUrl", obj[3] != null ? obj[3].toString() : "");
					schoolsInfoList.add(schoolInfo);
				}
			}
			logger.info("On-boarded school info retrieved successfully");
		}catch(Exception e){
			logger.error("Failed to get the onboarded school info due to "+e.getMessage());
		}
		return schoolsInfoList;
	}

	/**This method used for get the students details for household application in web-site**/
	@Override
	public List<Map<String, String>> websiteStudentsInfo(Long mealSchoolId, Integer schoolYear, String parentEmail, Boolean isSupport) {
		List<Map<String, String>> websiteStudents = new ArrayList<Map<String, String>>();
		try{
			List<Object[]> resp = mealManageAPIDao.getWebsiteStudents(mealSchoolId, schoolYear,parentEmail, isSupport);
			for(Object[] obj : resp){
				Map<String, String> studentMap = new HashMap<String, String>();
				if(obj[0] != null)
					studentMap.put("studentRecId", obj[0].toString());
				if(obj[1] != null)
					studentMap.put("firstName", obj[1].toString());
				if(obj[2] != null)
					studentMap.put("lastName", obj[2].toString());
				if(obj[3] != null)
					studentMap.put("gradeName", obj[3].toString());
				if(obj[4] != null)
					studentMap.put("studentId", obj[4].toString());
				websiteStudents.add(studentMap);
			}
			logger.info("Student details in website retrieved successfully");
		}catch(Exception e){
			logger.error("Failed to get the students details for household application in website");
		}
		return websiteStudents;
	}

	/**This method used for make the school admin user as primary**/
	@Override
	public ServiceResponse makeAdminPrimary(String userName) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse = mealManageAPIDao.makeAdminPrimary(userName);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to make the school admin user as primary due to "+e.getMessage());
			serviceResponse.setStatusMessage("Failed to make the school admin user as primary.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used add/update the module details**/
	@Override
	public ServiceResponse addModuleDetails(List<ModuleDetails> moduleDetails) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse = mealManageAPIDao.addModuleDetails(moduleDetails);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to add/update the module details due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to add/update the module details.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for get the module details by type**/
	@Override
	public Map<String, Map<String, List<ModuleTypeMapping>>> getModulesByType(String userType) {
		Map<String, Map<String, List<ModuleTypeMapping>>> moduleDetailsByType = null;
		try{
			List<Object[]> objArrList = mealManageAPIDao.getModulesByType(userType);
			moduleDetailsByType = moduleDetailsByTypeMap(objArrList);
		}catch(Exception e){
			logger.error("Failed to get the module details by type due to "+e.getMessage());
		}
		return moduleDetailsByType;
	}
	
	/**This method used for map the module by type object
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException **/
	private Map<String, Map<String, List<ModuleTypeMapping>>> moduleDetailsByTypeMap(List<Object[]> objArrList) throws JsonParseException, JsonMappingException, IOException{
		Map<String, Map<String, List<ModuleTypeMapping>>> moduleDetailsByType = new HashMap<String, Map<String, List<ModuleTypeMapping>>>();
		ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		TypeReference<Map<String, Boolean>> mapType = new TypeReference<Map<String, Boolean>>() {};
		if(objArrList != null && objArrList.size() > 0){
			List<ModuleTypeMapping> moduleTypeMappingList = new ArrayList<ModuleTypeMapping>();
			ModuleTypeMapping moduleTypeMapping = null;
			for(Object[] obj : objArrList){
				moduleTypeMapping = new ModuleTypeMapping();
				moduleTypeMapping.setModuleId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
				moduleTypeMapping.setModule(obj[1] != null ? obj[1].toString() : "");
				moduleTypeMapping.setSubModule(obj[2] != null ? obj[2].toString() : "");
				moduleTypeMapping.setPageName(obj[3] != null ? obj[3].toString() : "");
				moduleTypeMapping.setModuleIcon(obj[4] != null ? obj[4].toString() : "");
				moduleTypeMapping.setAccess(obj[8] != null ? obj[8].toString() : (obj[5] != null ? obj[5].toString() : ""));
				moduleTypeMapping.setRecId(obj[6] != null ? Long.parseLong(obj[6].toString()) : null);
				moduleTypeMapping.setUserType(obj[7] != null ? obj[7].toString() : "");
				if(moduleTypeMapping.getAccess() != null && !moduleTypeMapping.getAccess().equalsIgnoreCase("")){
					Map<String, Boolean> accessMap = objectMapper.readValue(moduleTypeMapping.getAccess(), mapType);
					moduleTypeMapping.setAccessMap(accessMap);
				}
				moduleTypeMappingList.add(moduleTypeMapping);
			}
			Map<String, List<ModuleTypeMapping>> accessByGroup = moduleTypeMappingList.stream().collect(Collectors.groupingBy(
    				ModuleTypeMapping::getModule));
			for(Entry<String, List<ModuleTypeMapping>> entry : accessByGroup.entrySet()){
				Map<String, List<ModuleTypeMapping>> accessBySubgrp = entry.getValue().stream().collect(Collectors.groupingBy(ModuleTypeMapping::getSubModule, LinkedHashMap::new, Collectors.toList()));
				moduleDetailsByType.put(entry.getKey(), accessBySubgrp);
			}
		}
		return moduleDetailsByType;
	}

	/**This method used add the module type details**/
	@Override
	public ServiceResponse addModuleType(Map<String, Map<String, List<ModuleTypeMapping>>> moduleTypeDetails) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse = mealManageAPIDao.addModuleType(moduleTypeDetails);
			logger.info(serviceResponse.getStatusMessage());
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Module type details added successfully.");
		}catch(Exception e){
			logger.error("Failed to add the module type details due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to add the module type details.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get the school info by parent email if it's valid email id**/
	@Override
	public Map<String, Object> schoolsByParentEmail(String parentEmail, String systemDate) {
		Map<String, Object> resp = new HashMap<String, Object>();
		try{
			Set<MealSchool> mealSchools = studentUserRepository.schoolsByEmail(parentEmail);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date currDate = sdf.parse(systemDate != null ? systemDate : sdf.format(new Date()));
			if(mealSchools != null && mealSchools.size() > 0){
				//List<SchoolYear> schoolYears = new ArrayList<SchoolYear>();
				List<Map<String, Object>> schoolList = new ArrayList<Map<String, Object>>();
				//List<Map<String, Object>> schoolYearsInfos = null;
				for(MealSchool mealSchool : mealSchools){
					Map<String, Object> school = new HashMap<String, Object>();
					school.put("mealSchoolId", mealSchool.getSchoolId());
					school.put("schoolName", mealSchool.getSchoolName());
					/*schoolYears = schoolYearRepository.findByMealSchoolSchoolId(mealSchool.getSchoolId());
					schoolYearsInfos = new ArrayList<Map<String, Object>>();
					for(SchoolYear schoolYear : schoolYears){
						Map<String, Object> schoolYearsInfo = new HashMap<String, Object>();
						schoolYearsInfo.put("schoolYear", schoolYear.getSchoolYear());
						schoolYearsInfo.put("sessionStartDateTime", schoolYear.getSessionStartDateTime());
						schoolYearsInfo.put("sessionEndDateTime", schoolYear.getSessionEndDateTime());
						schoolYearsInfo.put("name", schoolYear.getName());
						schoolYearsInfos.add(schoolYearsInfo);
					}
					school.put("schoolYears", schoolYearsInfos);*/
					school.put("schoolYear", schoolYearRepository.schoolYearBySchoolAndDate(mealSchool.getSchoolId(), currDate));
					schoolList.add(school);
				}
				resp.put("status", "Email id is valid.");
				resp.put("schoolInfo", schoolList);
			}else
				resp.put("status", "Please enter valid email id!!");
		}catch(Exception e){
			logger.error("Failed to get the school info due to "+e.getMessage());
			resp.put("status", "Please contact school admin!!.");
		}
		return resp;
	}

	/**This method used for audit the existing student's eligibility**/
	@Override
	@Transactional
	public ServiceResponse eligibilityAuditForExistingStd(Integer schoolYear) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			//List<Long> studentExistingIds = studentEligibilityAuditRepo.getStudentIds(schoolYear);
			List<StudentUser> studentList = studentUserRepository.getExistingStds(schoolYear);
			Map<Long, Date> endDateBySchool = new HashMap<Long, Date>();
			Map<Long, Date> startDateBySchool = new HashMap<Long, Date>();
			for(StudentUser su : studentList){
				if(endDateBySchool.get(su.getMealSchool().getSchoolId()) == null)
					endDateBySchool.put(su.getMealSchool().getSchoolId(), schoolYearRepository.getSchoolYearEndDate(su.getMealSchool().getSchoolId(), schoolYear));
				if(startDateBySchool.get(su.getMealSchool().getSchoolId()) == null)
					startDateBySchool.put(su.getMealSchool().getSchoolId(), schoolYearRepository.getSchoolYearStartDate(su.getMealSchool().getSchoolId(), schoolYear));
				Integer currentEligStatus = mealManageAPIDao.getEligStatus(su.getIsFreeMealEligible(), su.getIsReducePriceEligible());
				mealManageAPIDao.addUpdateStudentEligibility(currentEligStatus, null, false, startDateBySchool.get(su.getMealSchool().getSchoolId()), 
						endDateBySchool.get(su.getMealSchool().getSchoolId()), "Existing Students", su);
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Student's eligibility audited for existing students.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to proceed the existing student's eligibility for audit.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for publish the event**/
	@Override
	/**This method used for publish the event**/
	public ServiceResponse publishEvent(Long eventId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			EventInfo eventInfo = eventInfoRepo.findOne(eventId);
			eventInfo.setIsPublished(true);
			eventInfoRepo.save(eventInfo);
			String adminEmail = eventInfo.getMealSchool().getContactPEmail() != null ? eventInfo.getMealSchool().getContactPEmail() : "";
			Set<String> uniqueEmail = new HashSet<String>();
			List<ParentEmailWithToken> parentsInfo = eventInfoRepo.getParentInfo(eventInfo.getMealSchool().getSchoolId(),
					eventInfo.getSchoolYear(), eventId);
			if(parentsInfo != null && parentsInfo.size() > 0){
				List<Map<String, String>> notificationReqList = new ArrayList<Map<String, String>>();
				for(ParentEmailWithToken pInfo : parentsInfo){
					if(isNotNull(pInfo.getPriToken()) && !uniqueEmail.contains(pInfo.getPriEmailId())){
						notificationReqList.add(buildEventNotifReq(pInfo.getPriEmailId(), pInfo.getPriToken()));
						uniqueEmail.add(pInfo.getPriEmailId());
					}
					if(isNotNull(pInfo.getAltEmailId()) && isNotNull(pInfo.getAltToken()) && !uniqueEmail.contains(pInfo.getPriEmailId())){
						notificationReqList.add(buildEventNotifReq(pInfo.getAltEmailId(), pInfo.getAltToken()));
						uniqueEmail.add(pInfo.getAltEmailId());
					}
				}
				if(notificationReqList != null && notificationReqList.size() > 0){
					Map<String, Object> notificationFinalReq = new HashMap<String, Object>();
					notificationFinalReq.put("schoolName", eventInfo.getMealSchool().getSchoolName());
					notificationFinalReq.put("configurableMsg", eventInfo.getConfigurableMsg() != null ? eventInfo.getConfigurableMsg() : "");
					notificationFinalReq.put("adminEmail", adminEmail);
					notificationFinalReq.put("emailList", notificationReqList);
					notificationFinalReq.put("cutOffDate", du.formatDateToString(eventInfo.getEndDate(), 
							"dd-MMM-yyyy", "UTC"));
					notificationFinalReq.put("eventType", eventInfo.getType());
					sendNotificationUtil.sendEmailWhenEventPublish(notificationFinalReq);
				}
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Event has been publish successfully.");
			logger.info(serviceResponse.getStatusMessage()+" with eventId::"+eventId);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to publish the event.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" with eventId::"+eventId+" due to "+e.getMessage());
		}		
		return serviceResponse;
	}
	
	private Map<String, String> buildEventNotifReq(String email, String token){
		Map<String, String> notifReq = new HashMap<String, String>();
		notifReq.put("email", email);
		//notifReq.put("token", mealManageAPIDao.parentUserEventLink(email, token, eventInfo.getRecId(), schoolId, eventInfo.getSchoolYear()));
		notifReq.put("token", mealManageAPIDao.parentUserActivationLink(email, token));
		return notifReq;
	}
	
	private Boolean isNotNull(String value){
		return (value!=null && !value.trim().isEmpty())? true: false;
	}

	/**This method used for get the events details along with student info**/
	@Override
	public ServiceResponse getEventWithStudentInfo(String parentEmail, Long mealSchoolId, Integer schoolYear, String currentDate) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<EventInfo> eventsFinal = new ArrayList<EventInfo>();
			Map<String, Object> respMap = new HashMap<String, Object>();
    		Date currentDt = null;
    		if(isNotNull(currentDate))
    			currentDt = df.parse(currentDate+" 00:00:00");
    		else
    			currentDt = new Date();
			List<EventInfo> eventInfos = eventInfoRepo.getSchoolQualifyEvents(mealSchoolId, currentDt, schoolYear);
			Set<BigInteger> finalStdRecIds = new HashSet<BigInteger>();
			if(eventInfos != null && eventInfos.size() > 0){
    			for(EventInfo event : eventInfos){
    				List<BigInteger> studentsRecId = eventInfoRepo.notPaidStdRecIds(parentEmail, mealSchoolId,
    						schoolYear, event.getRecId());
    				if(studentsRecId != null && studentsRecId.size() > 0){
    					event.setNotPaidStudentRecId(studentsRecId);
    					finalStdRecIds.addAll(studentsRecId);
    					event.setMealSchool(null);
    					eventsFinal.add(event);
    				}
    			}
				List<StudentUser> studentUsers = studentUserRepository.findByUserIdIn(finalStdRecIds.stream().map(s -> Long.valueOf(s.toString())).collect(Collectors.toList()));
				String schoolName = "";
				if(studentUsers != null && studentUsers.size() > 0)
					schoolName = studentUsers.get(0).getMealSchool().getSchoolName();
				studentUsers.stream().forEach(su->su.setMealSchool(null));
				respMap.put("events", eventsFinal);
				respMap.put("students", studentUsers);
				respMap.put("schoolName", schoolName);
    		}
			serviceResponse.setResponse(respMap);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Retrieved the events info along with students.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get the events info along with students");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" with parentEmailId::"+parentEmail+" and mealSchoolId::"+mealSchoolId
					+" and schoolYear::"+schoolYear+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for activate/deactivate the student**/
	@Override
	@Transactional(rollbackOn = Exception.class)
	public ServiceResponse changeStdStatus(Long studentRecId, Boolean isActive) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			StudentUser su = studentUserRepository.findOne(studentRecId);
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(su.getMealSchool().getCountryCode());
			if(su != null && Boolean.compare(su.getIsActive(), isActive) != 0){
				if(isActive != null && !isActive){
					String dateTime = du.formatDateToString(new Date(), "yyyy-MM-dd", su.getMealSchool().getSchoolTimezone());
					if(su.getAccBalance() != 0)
						serviceResponse.setStatusMessage("Student can't delete because it's having "+su.getAccBalance()+currencySymbol+" account balance.");
					else if(mealOrderDetailsRepository.checkFutureOrders(studentRecId, dateTime).intValue() > 0)
						serviceResponse.setStatusMessage("Student can't delete because it's having Meal Orders on future dates.");
					else if(packageRepo.checkFutureSubs(studentRecId, dateTime).intValue() > 0)
						serviceResponse.setStatusMessage("Student can't delete because it's having Package Subscribed on future dates.");
					
					if(serviceResponse.getStatusMessage() != null){
						serviceResponse.setStatus("Failed");
						serviceResponse.setStatusCode(417);
						logger.info(serviceResponse.getStatusMessage());
						return serviceResponse;
					}	
				}
				Date schoolYearEndDate = schoolYearRepository.getSchoolYearEndDate(su.getMealSchool().getSchoolId(), su.getSchoolYear());
				studentUserRepository.changeStdStatus(studentRecId, isActive);
				mealManageAPIDao.auditStudentStatus(isActive, su.getIsActive(), true, null, schoolYearEndDate, "Manage Screen", su);
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Student has been "+(isActive ? "Activated":"Deactivated")+" successfully.");
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Student can not be "+(isActive ? "Activate":"Deactivate")+". Please try again later!!");
			}
			logger.info(serviceResponse.getStatusMessage()+" with studentRecId::"+studentRecId);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to "+(isActive ? "Activate":"Deactivate")+" the student.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" with studentRecId::"+studentRecId+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for audit the existing student's status**/
	@Override
	public ServiceResponse statusAuditForExistingStd(Integer schoolYear) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<StudentUser> studentList = studentUserRepository.getExistingStdsForStatus(schoolYear);
			Map<Long, Date> endDateBySchool = new HashMap<Long, Date>();
			Map<Long, Date> startDateBySchool = new HashMap<Long, Date>();
			for(StudentUser su : studentList){
				if(endDateBySchool.get(su.getMealSchool().getSchoolId()) == null)
					endDateBySchool.put(su.getMealSchool().getSchoolId(), schoolYearRepository.getSchoolYearEndDate(su.getMealSchool().getSchoolId(), schoolYear));
				if(startDateBySchool.get(su.getMealSchool().getSchoolId()) == null)
					startDateBySchool.put(su.getMealSchool().getSchoolId(), schoolYearRepository.getSchoolYearStartDate(su.getMealSchool().getSchoolId(), schoolYear));
				mealManageAPIDao.auditStudentStatus(su.getIsActive(), null, false, startDateBySchool.get(su.getMealSchool().getSchoolId()), 
						endDateBySchool.get(su.getMealSchool().getSchoolId()), "Existing Students", su);
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Student's status audited for existing students successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to proceed the existing student's status for audit.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for add the module info**/
	@Override
	public ServiceResponse moduleInfo(List<ModuleInfo> moduleInfos) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			mealManageAPIDao.addModules(moduleInfos);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Module info added successfully");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to add module info.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getErrorMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}



	@Override
	public ServiceResponse tierWithModule(TierInfo tierInfo) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			mealManageAPIDao.tierWithModule(tierInfo);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Tier info added successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to add tier info along with module mapping.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getErrorMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for get the ordered meal info by order id**/
	@Override
	public MenuSummaryDetailDTO getOrderedMealsInfo(Long studentRecId, String yearMonth, ItemTypeConstants menuType) {
		MenuSummaryDetailDTO menuSummaryDetailDto = new MenuSummaryDetailDTO();
		List<MenuDetailDTO> menuDetailList = null;//manageMenuDao.getMenuItemsForSummary(menuSummaryId);
		try{
			List<Object[]> objArray = mealOrderDetailsRepository.getOrderPdf(studentRecId, yearMonth, menuType);
			if(objArray == null || objArray.size() < 1 || objArray.get(0) == null)
				throw new Exception("This student does not have order");
			Object[] obj = objArray.get(0);
			menuSummaryDetailDto.setPdfLink(obj[0] != null ? obj[0].toString() : null);
			menuSummaryDetailDto.setIsStdFreeMealElig(obj[1] != null ? (Boolean)obj[1] : false);
			menuSummaryDetailDto.setIsStdReducedPriceElig(obj[2] != null ? (Boolean)obj[2] : false);
			menuSummaryDetailDto.setIsStdBeforeCare(obj[3] != null ? (Boolean)obj[3] : false);
			menuSummaryDetailDto.setItemPriceDisForMonthlyOrder(obj[4] != null ? (Double)obj[4] : null);
			menuSummaryDetailDto.setIsEligForDiscount(obj[5] != null ? (Boolean)obj[5] : false);
			List<Object[]> objList = mealOrderDetailsRepository.getMenuItemsByStudentAndMonth(studentRecId, yearMonth, menuType.toString());
			menuDetailList = objList.stream().map(MenuDetailDTO::new).collect(Collectors.toList());
			List<Long> calendarIds = menuDetailList.stream().map(MenuDetailDTO::getMealCalendarId).collect(Collectors.toList());
			menuSummaryDetailDto.setMealCalendarIds(calendarIds);
			menuDetailList.sort(Comparator.comparing(MenuDetailDTO::getType));
			menuSummaryDetailDto.setMenuItemsList(menuDetailList);
			if(menuDetailList != null && menuDetailList.size() > 0){
				List<Object[]> objList1 = mealOrderDetailsRepository.getHolidayList(menuDetailList.get(0).getSummaryId());
				menuDetailList = objList1.stream().map(MenuDetailDTO::new).collect(Collectors.toList());
			}
			menuSummaryDetailDto.setHolidayList(menuDetailList);			
		}catch(Exception e){
			if(e.getMessage().equalsIgnoreCase("This student does not have order"))
				logger.info("WARN:: "+e.getMessage()+" for month::"+yearMonth+" and studetRecId::"+studentRecId);
			else
				logger.error("Failed to get the order menu items by student record id::"+studentRecId+" and yearMonth::"+yearMonth+" due to "+e.getMessage());
		}
		return menuSummaryDetailDto;
	}

	@Override
	@Transactional
	public ServiceResponse catererUpdate(Long catererId, Caterer caterer) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			Caterer catererOrg = catererRepository.findOne(catererId);
			if(caterer.getAddressLine1() != null)
				catererOrg.setAddressLine1(caterer.getAddressLine1());
			if(caterer.getAddressLine2() != null)
				catererOrg.setAddressLine2(caterer.getAddressLine2());
			if(caterer.getCity() != null)
				catererOrg.setCity(caterer.getCity());
			if(caterer.getTimezone() != null && !caterer.getTimezone().toString().equalsIgnoreCase(""))
				catererOrg.setTimezone(caterer.getTimezone());
			if(caterer.getContactNumber() != null)
				catererOrg.setContactNumber(caterer.getContactNumber());
			if(caterer.getCountry() != null)
				catererOrg.setCountry(caterer.getCountry());
			if(caterer.getFaxNumber() != null)
				catererOrg.setFaxNumber(caterer.getFaxNumber());
			if(caterer.getName() != null)
				catererOrg.setName(caterer.getName());
			if(caterer.getState() != null)
				catererOrg.setState(caterer.getState());
			if(caterer.getZip() != null)
				catererOrg.setZip(caterer.getZip());
			if(caterer.getCountryCode() != null)
				catererOrg.setCountryCode(caterer.getCountryCode());
			catererOrg.setModifiedBy(caterer.getLoggedUser());
			catererOrg.setModifiedOn(new Date());
			Set<CatererUser> catererUsers = catererOrg.getCatererUsers();
			for(CatererUser catererUser : caterer.getCatererUsers()){
				CatererUser catererUser2 = catererRepository.catererUser(catererUser.getUsername());
				if(catererUser2 == null){
					catererUser2 = new CatererUser();
					catererUser2.setCreatedBy(caterer.getLoggedUser());
					catererUser2.setCreatedOn(new Date());
					catererUser2.setFirstName(catererUser.getFirstName());
					catererUser2.setLastName(catererUser.getLastName());
					catererUser2.setMobileNo(catererUser.getMobileNo());
					catererUser2.setRole("ROLE_CATERER");
					catererUser2.setUsername(catererUser.getUsername());
					catererUser2.setIsPrimaryUser(catererUser.getIsPrimaryUser());
					catererUsers.add(catererUser2);	
				}else{
					catererUsers.remove(catererUser2);
					catererUser2.setModifiedBy(caterer.getLoggedUser());
					catererUser2.setModifiedOn(new Date());
					if(catererUser.getFirstName() != null && !catererUser.getFirstName().equalsIgnoreCase(""))
						catererUser2.setFirstName(catererUser.getFirstName());
					if(catererUser.getLastName() != null && !catererUser.getLastName().equalsIgnoreCase(""))
						catererUser2.setLastName(catererUser.getLastName());
					if(catererUser.getMobileNo() != null && !catererUser.getMobileNo().equalsIgnoreCase(""))
						catererUser2.setMobileNo(catererUser.getMobileNo());
					catererUser2.setIsPrimaryUser(catererUser.getIsPrimaryUser());
					catererUsers.add(catererUser2);
				}
			} 
			catererOrg.setCatererUsers(catererUsers);
			mealManageAPIDao.catererUpdate(catererOrg, catererUsers);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Caterer info updated successfully.");
			logger.info(serviceResponse.getStatusMessage()+" for CatererId:: "+catererId);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update Caterer info.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" for CatererId:: "+catererId+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for update the district info**/
	@Override
	@Transactional
	public ServiceResponse districtUpdate(Long districtId, District district) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			District districtOrg = districtRepository.findOne(districtId);
			if(district.getAddressLine1() != null)
				districtOrg.setAddressLine1(district.getAddressLine1());
			if(district.getAddressLine2() != null)
				districtOrg.setAddressLine2(district.getAddressLine2());
			if(district.getCity() != null)
				districtOrg.setCity(district.getCity());
			if(district.getTimezone() != null && !district.getTimezone().toString().equalsIgnoreCase(""))
				districtOrg.setTimezone(district.getTimezone());
			if(district.getContactNumber() != null)
				districtOrg.setContactNumber(district.getContactNumber());
			if(district.getCountry() != null)
				districtOrg.setCountry(district.getCountry());
			if(district.getFaxNumber() != null)
				districtOrg.setFaxNumber(district.getFaxNumber());
			if(district.getName() != null)
				districtOrg.setName(district.getName());
			if(district.getState() != null)
				districtOrg.setState(district.getState());
			if(district.getZip() != null)
				districtOrg.setZip(district.getZip());
			if(district.getCountryCode() != null)
				districtOrg.setCountryCode(district.getCountryCode());
			districtOrg.setModifiedBy(district.getLoggedUser());
			districtOrg.setModifiedOn(new Date());
			Set<DistrictUser> districtUsers = districtOrg.getDistrictUsers();
			for(DistrictUser districtUser : district.getDistrictUsers()){
				DistrictUser districtUser2 = districtRepository.districtUser(districtUser.getUsername());
				if(districtUser2 == null){
					districtUser2 = new DistrictUser();
					districtUser2.setCreatedBy(district.getLoggedUser());
					districtUser2.setCreatedOn(new Date());
					districtUser2.setFirstName(districtUser.getFirstName());
					districtUser2.setLastName(districtUser.getLastName());
					districtUser2.setMobileNo(districtUser.getMobileNo());
					districtUser2.setRole("ROLE_DISTRICT");
					districtUser2.setUsername(districtUser.getUsername());
					districtUser2.setIsPrimaryUser(districtUser.getIsPrimaryUser());
					districtUsers.add(districtUser2);	
				}else{
					districtUsers.remove(districtUser2);
					districtUser2.setModifiedBy(district.getLoggedUser());
					districtUser2.setModifiedOn(new Date());
					if(districtUser.getFirstName() != null && !districtUser.getFirstName().equalsIgnoreCase(""))
						districtUser2.setFirstName(districtUser.getFirstName());
					if(districtUser.getLastName() != null && !districtUser.getLastName().equalsIgnoreCase(""))
						districtUser2.setLastName(districtUser.getLastName());
					if(districtUser.getMobileNo() != null && !districtUser.getMobileNo().equalsIgnoreCase(""))
						districtUser2.setMobileNo(districtUser.getMobileNo());
					districtUser2.setIsPrimaryUser(districtUser.getIsPrimaryUser());
					districtUsers.add(districtUser2);
				}
			} 
			districtOrg.setDistrictUsers(districtUsers);
			mealManageAPIDao.districtUpdate(districtOrg, districtUsers);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("District info updated successfully.");
			logger.info(serviceResponse.getStatusMessage()+" for districtId:: "+districtId);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update District info.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" for districtId:: "+districtId+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for generate the admin user's PIN**/
	@Override
	public ServiceResponse generateAdminPIN(Long schoolId, Long userId, String pin) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String sId = df2.format(schoolId);
			String uId = df2.format(userId);
			if(pin == null || pin.trim().isEmpty())
				pin = sId.substring(sId.length()-2)+""+uId.substring(uId.length()-2);
			serviceResponse = mealManageAPIDao.saveAdminPIN(schoolId, userId, pin);
			logger.info(serviceResponse.getStatusMessage()+" userId::"+userId);
		}catch(Exception e){
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to generate PIN for Admin user.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" userId::"+userId+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for generate student's PIN**/
	@Override
	public ServiceResponse generateStudentPIN(Long schoolId, String grade, Long stdRecId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Student's PIN generated successfully.");
			Map<Long, String> pinByStd = new HashMap<>();
			String maxId = studentUserRepository.getMaxPin(schoolId,2021);
			String sId = df2.format(schoolId);
			Integer seqNo = 1;
			if(maxId != null)
				seqNo = Integer.parseInt(maxId.substring(2))+1;
			String uId = null;
			if(stdRecId != null && stdRecId != 0){
				uId = df4.format(seqNo);
				pinByStd.put(stdRecId, (sId.substring(sId.length()-2)+""+uId.substring(uId.length()-4)));
			}else if(grade != null && !grade.trim().isEmpty()){
				List<BigInteger> ids = studentUserRepository.getIdsWithoutPin(schoolId, grade);
				for(BigInteger id : ids){
					uId = df4.format(seqNo);
					pinByStd.put(id.longValue(), (sId.substring(sId.length()-2)+""+uId.substring(uId.length()-4)));
					seqNo++;
				}
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Select atleast one student for generate PIN.");
			}
			if(pinByStd.size() > 0)
				mealManageAPIDao.saveStdPin(pinByStd);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate students PIN.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" for schoolId::"+schoolId+" and grade::"+grade+" and stdRecId::"+stdRecId+" due to "+e.getMessage());
		}
		return serviceResponse;
	}



	@Override
	public ServiceResponse studentsExport(Long schoolId, Integer schoolYear, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			commonExcelGenerator.exportStudents(schoolId, schoolYear, response);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Students data exported successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to export the students data.");
			logger.error(serviceResponse.getStatusMessage()+" for schoolId::"+schoolId+" and schoolYear::"+schoolYear+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for export student account balance**/
	@Override
	public ServiceResponse studentBalanceExport(Long schoolId, Integer schoolYear, HttpServletResponse response) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			commonExcelGenerator.exportBalanceStudents(schoolId, schoolYear, response);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Students balance data exported successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to export the students balance data.");
			logger.error(serviceResponse.getStatusMessage()+" for schoolId::"+schoolId+" and schoolYear::"+schoolYear+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	@Override
	public ServiceResponse getCountByCategory(String category) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			List<Map<String, String>> resp = new ArrayList<>();
			Map<String, String> map = null;
			if(category.equalsIgnoreCase("Student")){
				List<Object[]> objs = mealSchoolRepository.getStdCountBySchool();
				if(objs != null)
				for(Object[] obj : objs){
					map = new HashMap<>();
					map.put("mealSchoolId", obj[0] != null ? obj[0].toString() : "0");
					map.put("districtName", obj[1] != null ? obj[1].toString() : "");
					map.put("stdCount", obj[2] != null ? obj[2].toString() : "0");
					resp.add(map);
				}
			}else if(category.equalsIgnoreCase("School")){
				List<Object[]> objs = catererRepository.getSchoolsCountBYCaterer();
				if(objs != null)
				for(Object[] obj : objs){
					map = new HashMap<>();
					map.put("catererId", obj[0] != null ? obj[0].toString() : "0");
					map.put("schoolCount", obj[1] != null ? obj[1].toString() : "0");
					resp.add(map);
				}
			}
			serviceResponse.setResponse(resp);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Count by category info retrieved successfully.");
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get Count by category.");
		}
		return serviceResponse;
	}

	/**This method used for get the enrollments info**/
	@Override
	public ServiceResponse enrollments(Long mealSchoolId, String startDate, String endDate, Boolean isSummary, String parentEmail) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(parentEmail != null && !parentEmail.trim().isEmpty()){
				List<Object[]> objs = packageRepo.getEnrollmentsByParent(mealSchoolId, sdf.parse(startDate), sdf.parse(endDate), parentEmail);
				serviceResponse.setResponse(buildEnrollments(objs, mealSchoolId));
			}else if(isSummary){
				Map<String, Map<String, Integer>> countByDate = new HashMap<>();
				Map<String, Integer> countByType = null;
				int totalEnrolled = 0;
				while(sdf.parse(startDate).before(sdf.parse(endDate)) ||  sdf.parse(startDate).equals(sdf.parse(endDate))){
					List<Object[]> objs = packageRepo.getCountByPkg(mealSchoolId, sdf.parse(startDate));
					if(objs != null){
						totalEnrolled = 0;
						countByType = new LinkedHashMap<>();
						countByType.put("Total Enrolled", totalEnrolled);
						for(Object[] obj : objs){
							countByType.put(obj[0].toString(), Integer.valueOf(obj[1].toString()));
							totalEnrolled = totalEnrolled+Integer.valueOf(obj[1].toString());
						}
						countByType.put("Total Enrolled", totalEnrolled);
						Integer paidEnr = packageRepo.getPaidCount(mealSchoolId, sdf.parse(startDate));
						countByType.put("Paid", paidEnr);
						countByType.put("Payment Pending", (totalEnrolled-paidEnr));
						countByDate.put(startDate, countByType);
					}
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(startDate));
					c.add(Calendar.DATE, 1); // number of days to add.
					startDate = sdf.format(c.getTime()); 
				}
				serviceResponse.setResponse(countByDate);
			}else{
				List<Object[]> objs = packageRepo.getEnrollments(mealSchoolId, sdf.parse(startDate), sdf.parse(endDate));
				serviceResponse.setResponse(buildEnrollments(objs, mealSchoolId));
			}
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Enrollments info retrieved successfully.");
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to get enrollments info.");
			logger.error("Failed to get enrollments info due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	private List<Enrollments> buildEnrollments(List<Object[]> objs, Long mealSchoolId) throws Exception{
		List<Enrollments> enrollments = new ArrayList<>();
		String timezone = mealSchoolRepository.getSchoolTimezone(mealSchoolId);
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yy");
		Enrollments enrl = new Enrollments();
		if(objs != null)
			for(Object[] obj : objs){
				enrl = new Enrollments();
				enrl.setName(obj[0] != null ? obj[0].toString() : "");
				enrl.setType(obj[1] != null ? obj[1].toString() : "");
				enrl.setEnrolledOn(obj[2] != null ? du.formatDateToString(df.parse(obj[2].toString()), "MM/dd/yy", timezone) : "");
				enrl.setStartDate(obj[3] != null ? sdf1.format(df.parse(obj[3].toString())) : "");
				enrl.setEndDate(obj[4] != null ? sdf1.format(df.parse(obj[4].toString())) : "");
				enrl.setPayment(obj[5] != null && Boolean.valueOf(obj[5].toString()) ? "Paid" : "Pending");
				enrl.setPaymentType(obj[6] != null ? obj[6].toString() : "");
				enrl.setAmount(obj[7] != null ? Double.valueOf(obj[7].toString()) : 0.0);
				enrl.setTrxRecId(obj[8] != null ? Long.valueOf(obj[8].toString()) : 0);
				enrollments.add(enrl);
			}
		return enrollments;
	}

	@Override
	public ServiceResponse generateHoseholdLetter(Long appId, HttpServletResponse resp) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			HouseholdApplicationForFRM householdApplicationForFRM = getHouseholdAppById(appId);
			
			if(householdApplicationForFRM == null || householdApplicationForFRM.getStatus().equalsIgnoreCase("pending")){
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				if(householdApplicationForFRM == null)
					serviceResponse.setStatusMessage("Household application id is invalid.");
				else
					serviceResponse.setStatusMessage("Household application having pending status.");
			}else{
				if(!householdApplicationForFRM.getStatus().equalsIgnoreCase("pending")){
					if(householdApplicationForFRM.getStatus().equalsIgnoreCase("in-complete"))
		  				aopUtil.incompleteApplication(householdApplicationForFRM, true, resp);
		  			else
		  				aopUtil.approvedDeclinedApplication(householdApplicationForFRM, true, resp);
				}
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Household letter generated successfully.");
			}
			logger.info(serviceResponse.getStatusMessage()+" appId::"+appId);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate household letter.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage()+" with householdId::"+appId);
		}
		
		return serviceResponse;
	}
	
	/*@Override
	public void demoRequest(String firstName, String lastName, String schoolName, String emailAddress) {
		// TODO Auto-generated method stub
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse = mealManageAPIDao.demoRequest(firstName, lastName, schoolName, emailAddress);
		} catch (Exception e) {
			logger.error("Error occurred during the adminAccActivationInfo API execution. "+e.getMessage());
			serviceResponse.setStatusMessage("Failed to notify the school admin user for account activation. Please try again later!");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
		}
	}*/
}
