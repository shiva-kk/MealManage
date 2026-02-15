package com.mealManage.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.EligCertReq;
import com.mealManage.domain.FreeReducedLunchEligReq;
import com.mealManage.domain.GradesInfo;
import com.mealManage.domain.MealReminderRequest;
import com.mealManage.domain.NotificationRequest;
import com.mealManage.domain.ParentsNotificationRequest;
import com.mealManage.domain.StudentDetailSendNotif;
import com.mealManage.domain.StudentMealOrders;
import com.mealManage.domain.StudentMealOrdersV2;
import com.mealManage.domain.TierInfo;
import com.mealManage.domain.UserActivationNotification;
import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.caterer.CatererUser;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.meal.SchoolMealSummary;
import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.DistrictRepository;
import com.mealManage.mealmodel.repository.EligibilityCodeRepo;
import com.mealManage.mealmodel.repository.HouseholdAppForFRMRepository;
import com.mealManage.mealmodel.repository.MealCalendarSummaryRepository;
import com.mealManage.mealmodel.repository.MealOrderDetailsRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.PaymentGatewayRepo;
import com.mealManage.mealmodel.repository.SchoolMealRepository;
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.repository.StudentEligibilityAuditRepo;
import com.mealManage.mealmodel.repository.StudentStatusAuditRepo;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.CountryDetail;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.DistrictUser;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.ModuleInfo;
import com.mealManage.mealmodel.school.SchoolType;
import com.mealManage.mealmodel.transaction.MasterTransactionsAudit;
import com.mealManage.mealmodel.transaction.PaymentGateway;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.mealmodel.transaction.PurchaseItemType;
import com.mealManage.mealmodel.transaction.SchoolPayGatewayInfo;
import com.mealManage.mealmodel.transaction.StudentWiseTransaction;
import com.mealManage.mealmodel.transaction.TransactionType;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;
import com.mealManage.mealmodel.user.ModuleDetails;
import com.mealManage.mealmodel.user.ModuleTypeMapping;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.SchoolUser;
import com.mealManage.mealmodel.user.StudentEligibilityAudit;
import com.mealManage.mealmodel.user.StudentStatusAudit;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.mealschedule.entities.MealCalendar;
import com.mealManage.mealschedule.entities.MealCalendarSummary;
import com.mealManage.menu.entities.EligibilityCode;
import com.mealManage.response.OrderStatusResp;
import com.mealManage.response.RejectedStudentsInfo;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;
import com.mealManage.util.AWSUtility;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.DateUtilityV2;
import com.mealManage.util.OrderedMenuPdfUtility;
import com.mealManage.util.SendNotificationUtil;
import com.mealManage.util.StripeUtil;
import com.stripe.model.Charge;

@Transactional
@Repository
@SuppressWarnings("unchecked")
/**This class implementing the MealManageAPIDao interface**/
public class MealManageAPIDaoImpl implements MealManageAPIDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired 
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private Environment env;
	@Autowired
	private SchoolMealRepository schoolMealsRepo;
	@Autowired
	private MealOrderDetailsRepository mealOrderDetailsRepository;
	@Autowired
	private OrderedMenuPdfUtility orderedMenuPdfUtility;
	@Autowired
	private AWSUtility awsUtility;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	@Autowired
	private StripeUtil stripeUtil;
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private StudentEligibilityAuditRepo eligibilityAuditRepo;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Autowired
	private StudentStatusAuditRepo studentStatusAuditRepo;
	@Autowired
	private MealCalendarSummaryRepository mealCalendarSummaryRepository;
	/*@Autowired
	private ReportsDao reportsDao;*/
	@Value("${spring.mealmanage.subdomain}")
	private String mealManageAppSubdomain;
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;
	@Autowired
	private HouseholdAppForFRMRepository householdAppForFRMRepository;
	@Autowired
	private PaymentGatewayRepo paymentGatewayRepo;
	@Autowired
	private DistrictRepository districtRepository;
	@Autowired
	private DateUtilityV2 du;
	@Autowired
	private EligibilityCodeRepo eligibilityCodeRepo;
	private static DecimalFormat df4 = new DecimalFormat("0000");
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**This Method used for send the activation link to school user based meal school id or Meal school id and username **/
	@Override
	public ServiceResponse adminAccActivationInfo(Long schoolId, String loggedUser, String schoolUserName) {
			ServiceResponse serviceResponse = new ServiceResponse();
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(schoolId);
			List<UserActivationNotification> adminInfoList= new ArrayList<UserActivationNotification>();
			String subDomain = mealSchool.getSubdomain();
				if(schoolUserName != null){
						SchoolUser schoolUser = mealSchoolRepository.schoolUser(schoolUserName);
						if(schoolUser != null){
							adminInfoList = getAdminUserForAccActivation(schoolUser, loggedUser, subDomain, adminInfoList, true, schoolUser.getUserId());
						}
				}else{
					List<SchoolUser> schoolUsers = new ArrayList<SchoolUser>(mealSchool.getSchoolUsers());
					if(schoolUsers != null){
					for(SchoolUser schoolUser : schoolUsers){
						adminInfoList = getAdminUserForAccActivation(schoolUser, loggedUser, subDomain, adminInfoList, false, schoolUser.getUserId());
						}
					}
				}
				NotificationRequest notificationRequest = new NotificationRequest();
				if(adminInfoList.size() > 0){
				notificationRequest.setUsers(adminInfoList);	
				sendNotificationUtil.schoolUserAccActivationNotification(notificationRequest);
				serviceResponse.setStatusMessage("School users has been notify successfully for their account activation.");
				}else{
					serviceResponse.setStatusMessage("There are no school user for notify.");
				}
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatus("Success");
		return serviceResponse;
	}
	
	/** This method used for build the email body to send account activation link **/
	public String buildResetPasswordLink(Long userId, String forgotPasswordToken, String subdomain, String username) {
		String domainName = "https://"+subdomain+"." + env.getProperty("spring.mail.domainName");
		String html = domainName + "activate?token=" + forgotPasswordToken + "&" + "uid=" + (username !=  null ? username : userId);
		logger.info("Link: "+html );
		return html;
	}
	
	/**This method used for insert the multiple students together
	 * @throws Exception **/
	@Override
	public StudentCreateResp students(List<StudentUser> students, Long mealSchoolId, Boolean isImport, 
			Date schoolYearEndDate, String processType) throws Exception {
		List<RejectedStudentsInfo> rejectedStudentsInfos = new ArrayList<RejectedStudentsInfo>();
		StudentCreateResp studentCreateResp = new StudentCreateResp();
		List<StudentUser> rejectedStudentsDueToGrades = new ArrayList<StudentUser>();
		//try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			List<String> gradesList = new ArrayList<String>();
			for(SchoolType type : mealSchool.getSchool().getSchoolType()){
				gradesList.addAll(type.getValues());
			}
			Boolean isMMGenId = false;
			Integer seq = 1;
			Integer schoolYear = students.size() > 0 ?  students.get(0).getSchoolYear() : 2021;
			List<String> usedPins = studentUserRepository.getUsedPins(mealSchoolId, schoolYear);
			Map<String, String> moduleInfo = mealSchool.getModuleAccess();
			if(moduleInfo != null){
				String stdIdType = moduleInfo.get("Student Id Type") != null ? moduleInfo.get("Student Id Type") : "School Defined Id";
				if(stdIdType.equalsIgnoreCase("MM Generated Id")){
					String maxPin = studentUserRepository.getMaxPin(mealSchoolId, schoolYear);
					if(maxPin != null)
						seq = Integer.parseInt(maxPin)+1;
					if(seq > 9999)
						seq = 0001;			
					isMMGenId = true;
				}
			}			
		StudentUser stdUser;
		ServiceResponse serviceResponseUpdate;
		int successInsertCount = 0;
		int successUpdateCount = 0;
		int failedInsertCount = 0;
		int failedUpdateCount = 0;

		String isdCode = countryDetailsRepository.getIsdCode(mealSchool.getCountryCode());
		for(StudentUser std : students){
				stdUser = null;
				if(std.getGradeName() != null && gradesList.contains(std.getGradeName().toString())){
					String mob = std.getMobileNo();
					if(mob != null){
						mob = mob.replaceAll("[^a-zA-Z0-9]", "");
						if(mob.length() < 10)
							mob = null;
						else if(mob.length() > 10)
							mob = "+"+mob;
						else
							mob = isdCode+mob;
						std.setMobileNo(mob);
					}
					
					if(isImport != null && isImport)
						stdUser = studentUserRepository.findByMealSchoolSchoolIdAndStudentIdAndSchoolYear(mealSchoolId, std.getStudentId(),
								std.getSchoolYear());
						if(stdUser != null && stdUser.getUserId() != null){
							serviceResponseUpdate = studentUpdate(std, stdUser.getUserId(), processType, schoolYearEndDate,isMMGenId);
							if(serviceResponseUpdate.getStatusCode() == 200)
								successUpdateCount++;
							else
								failedUpdateCount++;
							continue;
						}
						if(isMMGenId && (std.getPin() == null || std.getPin().trim().isEmpty())){
							while(usedPins.contains(df4.format(seq))){
								if(seq >= 9999)
									seq = 0001;
								seq++;
							}
							std.setPin(df4.format(seq));
							usedPins.add(df4.format(seq));
						}else
							std.setPin(df4.format(Integer.parseInt(std.getPin())));
					ParentUser usr = studentUserRepository.findByUsername(std.getParentuser().getUserName());
					if(usr == null){
						usr = std.getParentuser();
						usr.setCreatedBy(std.getLoggedUser());
						usr.setCreatedOn(new Date());
						usr.setIsActive(false);
						usr.setMobileNo(std.getMobileNo());
						usr.setRole("ROLE_PARENT");
						entityManager.persist(usr);
					}else {
						if(std.getParentuser().getParentAltEmail() != null && !std.getParentuser().getParentAltEmail().
								equalsIgnoreCase("") && ((usr.getParentAltEmail() != null && !usr.getParentAltEmail().equalsIgnoreCase(std.getParentuser()
										.getParentAltEmail())) || usr.getParentAltEmail() == null)){
						usr.setModifiedBy(std.getLoggedUser());
						usr.setModifiedOn(new Date());
						usr.setParentAltEmail(std.getParentuser().getParentAltEmail());
						entityManager.merge(usr);
						}
						
							if(std.getMobileNo() != null && !std.getMobileNo().equalsIgnoreCase("") && 
							(!usr.getMobileNo().equalsIgnoreCase(std.getMobileNo())) ||  
									usr.getMobileNo() == null){
							usr.setModifiedBy(std.getLoggedUser());
							usr.setModifiedOn(new Date());
							usr.setMobileNo(std.getMobileNo());
							entityManager.merge(usr);
							}						
					}
					
						UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getUserName(), "ROLE_PARENT");
						if(usersAuthInfo == null){
							usersAuthInfo = new UsersAuthInfo();
							usersAuthInfo.setUsername(usr.getUserName());
							usersAuthInfo.setRole("ROLE_PARENT");
							usersAuthInfo.setCreatedBy(std.getLoggedUser());
							usersAuthInfo.setCreatedOn(new Date());
							usersAuthInfo.setMobile(usr.getMobileNo());
							entityManager.persist(usersAuthInfo);
						}else if(usr.getMobileNo() != null && !usr.getMobileNo().equalsIgnoreCase("") && 
									(!usr.getMobileNo().equalsIgnoreCase(usersAuthInfo.getMobile())) || usersAuthInfo.getMobile() == null){
								usersAuthInfo.setModifiedBy(std.getLoggedUser());
								usersAuthInfo.setModifiedOn(new Date());
								usersAuthInfo.setMobile(usr.getMobileNo());
								entityManager.merge(usersAuthInfo);
						}
						
					if(usr.getParentAltEmail()!=null && !usr.getParentAltEmail().equalsIgnoreCase("")){
						 usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getParentAltEmail(), "ROLE_PARENT");
						if(usersAuthInfo == null){
							usersAuthInfo = new UsersAuthInfo();
							usersAuthInfo.setUsername(usr.getParentAltEmail());
							usersAuthInfo.setRole("ROLE_PARENT");
							usersAuthInfo.setCreatedBy(std.getLoggedUser());
							usersAuthInfo.setCreatedOn(new Date());
							usersAuthInfo.setMobile(usr.getMobileNo());
							entityManager.persist(usersAuthInfo);
						}if(usr.getMobileNo() != null && !usr.getMobileNo().equalsIgnoreCase("") && 
							(!usr.getMobileNo().equalsIgnoreCase(usersAuthInfo.getMobile())) || usersAuthInfo.getMobile() == null){
								usersAuthInfo.setModifiedBy(std.getLoggedUser());
								usersAuthInfo.setModifiedOn(new Date());
								usersAuthInfo.setMobile(usr.getMobileNo());
								entityManager.merge(usersAuthInfo);
							}
					}
					
					std.setCreatedBy(std.getLoggedUser());
					std.setCreatedOn(new Date());
					if(std.getIsFreeMealEligible() || std.getIsReducePriceEligible())
						std.setRecertPending("Y");
					std.setParentuser(usr);
					std.setMealSchool(mealSchool);
					std.setRole("ROLE_PARENT");
					entityManager.persist(std);	
					addUpdateStudentEligibility(getEligStatus(std.getIsFreeMealEligible(), std.getIsReducePriceEligible()), 
							null, false, null, schoolYearEndDate, processType, std);
					auditStudentStatus(std.getIsActive(), null, false, null, schoolYearEndDate, processType, std);
					successInsertCount++;
			}else{
				rejectedStudentsDueToGrades.add(std);
				failedInsertCount++;
			}
		}
		
		if(rejectedStudentsDueToGrades.size() > 0){
			RejectedStudentsInfo rejectedStudentsInfo = new RejectedStudentsInfo();
			rejectedStudentsInfo.setStatus("Failed to create the following Students");
			rejectedStudentsInfo.setErrorMessage("Invalid grade");
			rejectedStudentsInfo.setStudentUsers(rejectedStudentsDueToGrades);
			rejectedStudentsInfos.add(rejectedStudentsInfo);
			studentCreateResp.setStatus("Partially student records created");
			logger.info("Partially student records created");
		}else{
			logger.info("Students created successfully");
			studentCreateResp.setStatusMessage("Students created successfully.");
		}
		studentCreateResp.setStatusCode(200);
		studentCreateResp.setStatus("Success");
		studentCreateResp.setSuccessInsertCount(successInsertCount);
		studentCreateResp.setSuccessUpdateCount(successUpdateCount);
		studentCreateResp.setFailedInsertCount(failedInsertCount);
		studentCreateResp.setFailedUpdateCount(failedUpdateCount);
		/*}catch(Exception e){
			logger.info("Failed to create the Students"+e.getMessage());
			if(e instanceof DataIntegrityViolationException || e.getMessage().contains("javax.persistence.RollbackException")){
				studentCreateResp.setStatusCode(409);
				studentCreateResp.setErrorMessage("Duplicate entry"+e.getMessage());
				studentCreateResp.setStatusMessage("Failed to create the Students due to "+e.getCause().getCause().getMessage().split("for key")[0]);
				throw new Exception(e);
			}else{
				studentCreateResp.setStatusCode(500);
				studentCreateResp.setStatusMessage("Failed to create the Students. Please try again later!");
				studentCreateResp.setErrorMessage(e.getMessage());
			}
			studentCreateResp.setStatus("Failed");
		}*/
		studentCreateResp.setRejectedStudentsInfos(rejectedStudentsInfos);
		return studentCreateResp;
	}
	
	/**Method used for certification date import**/
	@Override
	public ServiceResponse importCertificationDate(List<EligCertReq> eligCertReqs, Long mealSchoolId,
			Integer schoolYear, String loggedUser) throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();
		Map<String, List<EligCertReq>> reqMap = eligCertReqs.stream().collect(Collectors.groupingBy(EligCertReq::getStudentId));
		List<StudentUser> stdList = studentUserRepository.findByStudentIdInAndMealSchoolSchoolIdAndSchoolYear(reqMap.keySet(),mealSchoolId, schoolYear);
		Map<String, List<StudentUser>> respMap = stdList.stream().collect(Collectors.groupingBy(StudentUser::getStudentId));
		List<EligibilityCode> eligibilityCodes  = eligibilityCodeRepo.findByIsActive(true);
		Map<String, List<EligibilityCode>> eligMap = eligibilityCodes.stream().collect(Collectors.groupingBy(EligibilityCode::getCode));
		List<String> invalidStdIds = new ArrayList<>();
		List<String> missingCertDateStds = new ArrayList<>();
		for(EligCertReq eligCertReq : eligCertReqs){
			if(respMap.get(eligCertReq.getStudentId()) != null){
				StudentUser su = respMap.get(eligCertReq.getStudentId()).get(0);
				if(eligCertReq.getCertDate() != null){
					su.setReCertificateDate(eligCertReq.getCertDate());
					switch (eligCertReq.getPrgSource().toUpperCase()) {
						case "SNAP":  su.setDecisionReason("DC");
									su.setCategory(eligMap.get("DC").get(0).getCodeDesc()); break;
						case "TANF":  su.setDecisionReason("DCT");
						  			su.setCategory(eligMap.get("DCT").get(0).getCodeDesc()); break;
						case "FOSTER":  su.setDecisionReason("DCF");
						  			su.setCategory(eligMap.get("DCF").get(0).getCodeDesc()); break;
						 default: su.setCategory(eligCertReq.getPrgSource());
					}
					su.setModifiedBy(loggedUser);
					su.setModifiedOn(new Date());
					su.setIsFreeMealEligible(true);
					su.setIsReducePriceEligible(false);
					su.setRecertPending("N");
					su.setActualPrg(null);
					studentUserRepository.save(su);
				}else{
					missingCertDateStds.add(eligCertReq.getStudentId());
					logger.info("WARN:: Certification date is missing for student id::"+eligCertReq.getStudentId());
				}
			}else{
				invalidStdIds.add(eligCertReq.getStudentId());
				logger.info("WARN:: Student Id does not foud for update certification date, stdId::"+eligCertReq.getStudentId());
			}
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put("MissingCertDateStds", missingCertDateStds);
		map.put("InvalidStds", invalidStdIds);
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		if(invalidStdIds.size() > 0 || missingCertDateStds.size() > 0)
			serviceResponse.setStatusMessage("Partially Certified date imported for students.");
		else
			serviceResponse.setStatusMessage("Certified date imported for students successfully.");
		serviceResponse.setResponse(map);
		return serviceResponse;
	}
	
	/**This method used for get the information of parent user and send notification to them.
	 * @throws Exception **/
	@Override
	public ServiceResponse sendNotificationParents(ParentsNotificationRequest parentsNotReq) throws Exception {
		Set<ParentUser> parentUsers = new HashSet<ParentUser>();
		List<UserActivationNotification> notificationInfos = new ArrayList<UserActivationNotification>();
		ServiceResponse serviceResponse = new ServiceResponse();
		MealSchool mealSchool = null;
		Boolean tokenStatus = false;
		if(parentsNotReq.getSchoolId() != null){
			if(parentsNotReq.getGradeName() != null){
				parentUsers = studentUserRepository.findByMealSchoolAndGradeNameAndIsActiveAndSchoolYear(
						parentsNotReq.getSchoolId(), parentsNotReq.getGradeName(), true, parentsNotReq.getSchoolYear());
				studentUserRepository.registerStudentBySchoolAndGradeAndYear(parentsNotReq.getSchoolId(),
						parentsNotReq.getGradeName(), parentsNotReq.getSchoolYear());
			}else if(parentsNotReq.getStudentIds() != null && parentsNotReq.getStudentIds().size() > 0){
				parentUsers = studentUserRepository.findByStudentIdInAndMealSchoolIdAndIsActiveAndYear(parentsNotReq.getStudentIds(),
						parentsNotReq.getSchoolId(), true, parentsNotReq.getSchoolYear());
				studentUserRepository.registerStudentByStudentIdsAndSchoolIdAndYear(parentsNotReq.getStudentIds(),
						parentsNotReq.getSchoolId(), parentsNotReq.getSchoolYear());
			}else {
				parentUsers = studentUserRepository.findByMealSchoolSchoolIdAndIsActiveAndYear(parentsNotReq.getSchoolId(), true, 
						parentsNotReq.getSchoolYear());
				studentUserRepository.registerStudentBySchoolAndYear(parentsNotReq.getSchoolId(), parentsNotReq.getSchoolYear());
				}
			mealSchool = mealSchoolRepository.findBySchoolId(parentsNotReq.getSchoolId());
		}
		try{
			for(ParentUser std : parentUsers){
				if(std.getUserName()!=null){
					UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsername(std.getUserName());
					if(usersAuthInfo != null && usersAuthInfo.getfToken() == null){
						usersAuthInfo.setfToken(UUID.randomUUID().toString());
						usersAuthInfo.setfTokenTime(new Date());
						usersAuthInfo.setModifiedBy(std.getUserName());
						usersAuthInfo.setModifiedOn(new Date());
						entityManager.merge(usersAuthInfo);
						tokenStatus = true;
					}
					if((tokenStatus || parentsNotReq.getSendStatus()) && usersAuthInfo.getEmailIsSubscribe() != null
							&& usersAuthInfo.getEmailIsSubscribe()){
						UserActivationNotification notificationInfo = new UserActivationNotification();
						notificationInfo.setEmail(std.getUserName());		
						notificationInfo.setToken(parentUserActivationLink(std.getUserName(), usersAuthInfo.getfToken()));
						notificationInfo.setSchoolName(mealSchool.getSchoolName());
						notificationInfo.setAdminEmail(mealSchool.getContactPEmail() != null ? mealSchool.getContactPEmail() : "");
						notificationInfos.add(notificationInfo);
						tokenStatus = false;
					}					
				}
				
				if(std.getParentAltEmail()!=null && !std.getParentAltEmail().trim().equalsIgnoreCase("")){
					UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsername(std.getParentAltEmail());
					if(usersAuthInfo != null && usersAuthInfo.getfToken() == null){
						usersAuthInfo.setfToken(UUID.randomUUID().toString());
						usersAuthInfo.setfTokenTime(new Date());
						usersAuthInfo.setModifiedBy(std.getParentAltEmail());
						usersAuthInfo.setModifiedOn(new Date());
						entityManager.merge(usersAuthInfo);
						tokenStatus = true;
					}
					if((tokenStatus || parentsNotReq.getSendStatus()) && usersAuthInfo.getEmailIsSubscribe() != null
							&& usersAuthInfo.getEmailIsSubscribe()){
						UserActivationNotification notificationInfo = new UserActivationNotification();
						notificationInfo.setEmail(std.getParentAltEmail());		
						notificationInfo.setToken(parentUserActivationLink(std.getParentAltEmail(), usersAuthInfo.getfToken()));
						notificationInfo.setSchoolName(mealSchool.getSchoolName());
						notificationInfo.setAdminEmail(mealSchool.getContactPEmail() != null ? mealSchool.getContactPEmail() : "");
						notificationInfos.add(notificationInfo);
					}				
				}			
				if(parentsNotReq.getNotificationType().equalsIgnoreCase("Registration") && (std.getIsActive() == null || !std.getIsActive())){
					std.setIsActive(true);
					entityManager.merge(std);
				}
			}
		List<UserActivationNotification> parentUserActivationNotifications = notificationInfos.stream().distinct().
				collect(Collectors.toList());
		if(parentUserActivationNotifications.size()>0){
			int i = 1;
			NotificationRequest notificationRequest = null;
			List<UserActivationNotification> parentUserActivationNotifReq = new ArrayList<UserActivationNotification>();
			for(UserActivationNotification userActivationNotification : parentUserActivationNotifications){
				parentUserActivationNotifReq.add(userActivationNotification);
				if(i == 50){
					notificationRequest = new NotificationRequest();
					notificationRequest.setUsers(parentUserActivationNotifReq);
					sendNotificationUtil.parentUserAccActivationNotification(notificationRequest);
					parentUserActivationNotifReq = new ArrayList<UserActivationNotification>();
					i = 0;
				}				
				i++;
			}
			if(parentUserActivationNotifReq.size() > 0){
				notificationRequest = new NotificationRequest();
				notificationRequest.setUsers(parentUserActivationNotifReq);
				sendNotificationUtil.parentUserAccActivationNotification(notificationRequest);
			}
			serviceResponse.setStatusMessage("Parent user will notify soon for their account activation.");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
		}else{
			serviceResponse.setStatusMessage("There are no parent user for send notification.");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
		}
		}catch(Exception e){
			logger.error("Error occured during get the information for parent notification. "+e.getMessage());
			serviceResponse.setStatusMessage("Failed to notify the parent users. Please try again later.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			throw new Exception("Failed to notify the parent users. Please try again later.");
		}
		return serviceResponse;
	}

	/**This method used for get the parent user activation link**/
	@Override
	public String parentUserActivationLink(String userName, String token){
		String domainName = "https://"+mealManageAppSubdomain+"."+ env.getProperty("spring.mail.domainName");
		return  domainName + "activateParentAccount?parentId=" + userName+"&token="+token;
	}
	
	/**This method used for get the parent user event notification**/
	@Override
	public String parentUserEventLink(String userName, String token, Long eventId, Long schoolId, Integer schoolYear){
		String domainName = "https://"+mealManageAppSubdomain+"."+ env.getProperty("spring.mail.domainName");
		return  domainName + "eventPayment?parentId=" + userName+"&token="+token+"&shId="+schoolId+"&evId="+eventId+"&shYr="+schoolYear;
	}

	
	
	//This method used for get the date without time.
	/*@SuppressWarnings("deprecation")
	private Date getDate(Date dt){
		Calendar cal = Calendar.getInstance();
		dt.setHours(00);
		dt.setMinutes(00);
		dt.setSeconds(00);
		cal.setTime(dt);
		return cal.getTime();
	}*/
	
	/**This method used for add/update the menu orders to students**/
	public ServiceResponse orderMenuV2(StudentMealOrdersV2 mealOrders, String loggedUser, ItemTypeConstants menuType){
		ServiceResponse serviceResponse = new ServiceResponse();
		String schoolName = "";
		String adminEmail = "";
		String logoLink = "";
		String schoolTimezone = "";
		MealSchool mealSchool = null;
		Boolean isInstantPayment = false;
		try{
			String userRole = "Parent";
			StudentUser studentUser = studentUserRepository.findOne(mealOrders.getStudentId());
			String parentUserEmail = "";//studentMealOrders2.getParentUserEmails();
			if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null 
					&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().toUpperCase().contains("ROLE_PARENT"))
				parentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			else if(studentUser.getDefaultNotifyEmail() == null || studentUser.getDefaultNotifyEmail().contains("Primary"))
				parentUserEmail = studentUser.getParentuser().getUserName();
			else if(studentUser.getDefaultNotifyEmail().contains("Alternate") && studentUser.getParentuser().getParentAltEmail() != null)
				parentUserEmail = studentUser.getParentuser().getParentAltEmail();
			else if(studentUser.getDefaultNotifyEmail().contains("Both"))
				parentUserEmail = studentUser.getParentuser().getUserName()+(studentUser.getParentuser().getParentAltEmail() != null ? (","+studentUser.getParentuser().getParentAltEmail()) :"");
			Boolean priEmailIsSubscribe = null;
			Boolean altEmailIsSubscribe = null;
			UsersAuthInfo usersAuthInfo = null;
			List<MealOrderDetails> orderList = new ArrayList<MealOrderDetails>();
			ParentUser parentUser = studentUserRepository.findByUsername(studentUser.getParentuser().getUserName());
			if(parentUser.getUserName() != null){
				usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getUserName());
				priEmailIsSubscribe = usersAuthInfo.getEmailIsSubscribe();
			}
			if(parentUser.getParentAltEmail() != null && !parentUser.getParentAltEmail().trim().equalsIgnoreCase("")){
				usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getParentAltEmail());
				altEmailIsSubscribe = usersAuthInfo.getEmailIsSubscribe();
			}
			mealSchool = studentUser.getMealSchool();
			if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Instant Payment for Orders") != null && mealSchool.getModuleAccess().get("Instant Payment for Orders").equalsIgnoreCase("Yes"))
				isInstantPayment = true;
			//String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(studentUser.getMealSchool().getSchoolId()));
			CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchoolRepository.getSchoolCountry(studentUser.getMealSchool().getSchoolId()));		
			if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null && 
					SecurityContextHolder.getContext().getAuthentication().getAuthorities() != null && 
					SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().toUpperCase().contains("ADMIN"))
				userRole = "Admin";
			schoolName = mealSchool.getSchoolName();
			Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
			adminEmail = mealSchool.getContactPEmail() != null ? mealSchool.getContactPEmail() : "";
			logoLink = mealSchool.getLogoLink();
			schoolTimezone = mealSchool.getSchoolTimezone().toString();
			Double totalOrderAmt = 0.0;
			String mealType = CommonUtil.getItemType(menuType);
			for(Map.Entry<String, List<Long>> orderEntry : mealOrders.getCalendarIdsByMonth().entrySet()){
				MealOrderDetails mealOrderDetails = mealOrderDetailsRepository.findByStudentUserUserIdAndYearMonthAndMenuType(mealOrders.getStudentId(), orderEntry.getKey(), menuType);
			if(studentUser != null && studentUser.getIsActive()){
				MealCalendarSummary summary = mealCalendarSummaryRepository.findBySchoolSchoolIdAndMealTypeAndYearMonthAndGrades(studentUser.getMealSchool().getSchoolId(), menuType,
						orderEntry.getKey(), studentUser.getGradeName());
				Date cutOffDateTime = summary.getCutOffDateTime();
				Boolean orderDtExtensionStatus = summary.getOrderDateExtensionStatus();
				Boolean isPublishedStatus = summary.getIsPublished();
				Set<MealCalendar> mealCalendars = new HashSet<MealCalendar>();
				if(orderEntry.getValue().size() > 0)
					mealCalendars = mealCalendarSummaryRepository.getMealCalendarByIds(orderEntry.getValue(), true, summary.getId());
				List<Date> mainItemDates1 = mealCalendarSummaryRepository.getMainItemDates(summary.getId(), mealType);
				List<String> mainItemDates = mainItemDates1.stream().map(s -> sdf.format(s)).collect(Collectors.toList());
				Double discount = summary.getItemPriceDisForMonthlyOrder();
				Double discountPriceTotal = 0.0;
				Double totalPrice = 0.0;
				Set<String> itemDates = new HashSet<>();
				Boolean isDisElig = ((studentUser.getIsReducePriceEligible() == null || !studentUser.getIsReducePriceEligible()) && 
						(studentUser.getIsFreeMealEligible() == null || !studentUser.getIsFreeMealEligible()) && discount != null) ? true : false;
				Double reducedPriceTotal = 0.0;
				Double extraItemsPrice = 0.0;
				Double orderAmount = 0.0;
				int itemCount = 0;
				Boolean menusEligibleForReducedPrice = summary.getReducedPriceStatus();
				Boolean orderEligibleForReducedPrice = false;
				Boolean orderEligibleForFreeMeal = false;
				Boolean orderEligibleForDiscount = false;
				for(MealCalendar mealCalendar : mealCalendars){
					if(mealCalendar.getMenuItem().getCategory().toString().equalsIgnoreCase(mealType)){
						totalPrice = totalPrice+mealCalendar.getPrice();
						if(mealCalendar.getReducedPrice() != null && mealCalendar.getMenuItem().getCategory().toString().equalsIgnoreCase(mealType))
							reducedPriceTotal = reducedPriceTotal+mealCalendar.getReducedPrice();
						itemCount++;
						if(!mainItemDates.contains(sdf.format(mealCalendar.getDate())))
							isDisElig = false;
						if(isDisElig){
							discountPriceTotal = discountPriceTotal+(mealCalendar.getPrice() - discount);
							itemDates.add(sdf.format(mealCalendar.getDate()));
						}
					}else if(mealCalendar.getMenuItem().getCategory().toString().equalsIgnoreCase("EXTRA"))
						extraItemsPrice = extraItemsPrice+mealCalendar.getPrice();
				}
				if((studentUser.getIsFreeMealEligible() != null && studentUser.getIsFreeMealEligible() 
						 && (!isItemized || menuType.toString().equalsIgnoreCase("Breakfast"))) || 
						(studentUser.isBeforeCare() && menuType.toString().equalsIgnoreCase("Breakfast"))){
					orderAmount = 0.0;
					orderEligibleForFreeMeal = true;
				}else if(studentUser.getIsReducePriceEligible() != null && studentUser.getIsReducePriceEligible() && 
						menusEligibleForReducedPrice  && (!isItemized || menuType.toString().equalsIgnoreCase("Breakfast"))){
					orderAmount = Double.parseDouble(String.format("%.2f", reducedPriceTotal));
					orderEligibleForReducedPrice = true;
				}else if(isDisElig && mainItemDates.size() == itemDates.size()){
					orderAmount = Double.parseDouble(String.format("%.2f", discountPriceTotal));
					orderEligibleForDiscount = true;
				}else
					
					orderAmount = Double.parseDouble(String.format("%.2f", totalPrice));
				
				orderAmount = Double.parseDouble(String.format("%.2f", (orderAmount+extraItemsPrice)));
				totalPrice = Double.parseDouble(String.format("%.2f", (totalPrice+extraItemsPrice)));
				if(isPublishedStatus != null && isPublishedStatus){					
					/*if(orderDtExtensionStatus == null)
						orderDtExtensionStatus = false;
					//Check if orderDtExtensionStatus is false and user having admin role then allow it to create/edit the order after cutOffDate also
					if(!orderDtExtensionStatus && SecurityContextHolder.getContext().getAuthentication().getAuthorities()
							.toString().contains("ROLE_ADMIN"))*/
						orderDtExtensionStatus = true;
					if(mealOrderDetails == null){
						if(mealCalendars.size() == 0){
							serviceResponse.setStatus("Success");
							serviceResponse.setStatusMessage("None of item selected to place the order for month::"+orderEntry.getKey());
							serviceResponse.setStatusCode(200);
							logger.info("WARN:: "+serviceResponse.getStatusMessage()+" and studentId::"+mealOrders.getStudentId()+" and menuType"+menuType.toString());
							continue;
						}
						if(mealCalendars.size() > 0 && mealCalendars.size() == orderEntry.getValue().size() && 
	                               (cutOffDateTime != null && cutOffDateTime.compareTo(new Date()) >= 0 || orderDtExtensionStatus)){						
							mealOrderDetails = new MealOrderDetails();
							//mealOrderDetails.setCreatedBy(loggedUser);
							//mealOrderDetails.setCreatedOn(new Date());
							mealOrderDetails.setCrudOperationVal(0);
							mealOrderDetails.setMenuType(menuType);
							mealOrderDetails.setLoggedUser(loggedUser);
							mealOrderDetails.setStudentUser(studentUser);
							mealOrderDetails.setTotalPrice(totalPrice);
							mealOrderDetails.setYearMonth(orderEntry.getKey());
							mealOrderDetails.setItems_count(itemCount);
							//mealOrderDetails.setSchoolMeals(schoolMeals);
							mealOrderDetails.setMealCalendars(mealCalendars);
							mealOrderDetails.setCutOffDateTime(cutOffDateTime);
							String menuOrderedPdfLink = orderedMenuPdfUtility.orderedMenuPdfLink(mealOrderDetails, true);
							mealOrderDetails.setMenuOrderedPdfLink(menuOrderedPdfLink);
							mealOrderDetails.setOrderAmount(orderAmount);
							totalOrderAmt = totalOrderAmt+orderAmount;
							mealOrderDetails.setIsEligibleForFreeMeal(orderEligibleForFreeMeal);
							mealOrderDetails.setIsEligibleForReducedPrice(orderEligibleForReducedPrice);
							mealOrderDetails.setIsEligForDiscount(orderEligibleForDiscount);
							mealOrderDetails.setItemDiscount(discount);
							if(isInstantPayment){
								mealOrderDetails.setPaymentStatus(true);
								mealOrderDetails.setPaymentIdChanged(true);
							}
							orderList.add(mealOrderDetails);
							serviceResponse.setStatusCode(200);
						}else{
							if(mealCalendars.size() != orderEntry.getValue().size())
								serviceResponse.setStatusMessage("Menu can not order as grade is invalid.");
							else
								serviceResponse.setStatusMessage("Menu can not order as cut off date is already over.");
							serviceResponse.setStatusCode(417);
							serviceResponse.setStatus("Failed");
							logger.info("WARN:: "+serviceResponse.getStatusMessage()+" with studentId::"+mealOrders.getStudentId()+" for month "+orderEntry.getKey());
							break;
						}						
					}else if(/*mealCalendars.size() > 0 && !mealOrderDetails.getPaymentStatus() &&*/ ((cutOffDateTime != null && cutOffDateTime.compareTo(new Date()) >= 0)  || orderDtExtensionStatus)
						&& mealCalendars.size() == orderEntry.getValue().size()){
						List<BigInteger> existingOrderCalIds = mealOrderDetailsRepository.getExistingCalendars(mealOrders.getStudentId(), orderEntry.getKey(), menuType.toString());
						//List<Long> aList = orderEntry.getValue();//.stream().map(Long::intValue).collect(Collectors.toList());
					    List<Long> oldCalIds = existingOrderCalIds.stream().map(BigInteger::longValue).collect(Collectors.toList());//.stream().map(Long::intValue).collect(Collectors.toList());
					    if(orderEntry.getValue().stream().allMatch(num -> oldCalIds.contains(num)) 
					    		&& oldCalIds.stream().allMatch(num -> orderEntry.getValue().contains(num))){
							serviceResponse.setStatus("Warn");
							serviceResponse.setStatusCode(200);
							serviceResponse.setStatusMessage("Nothing change in order for the month::"+orderEntry.getKey());
							logger.info("WARN::"+serviceResponse.getStatusMessage()+" for studentRecId::"+mealOrders.getStudentId()+" and menuType::"+menuType.toString());
							continue;
						}
						//mealOrderDetailsRepository.delete(mealOrderDetails);
						//mealOrderDetails.setModifiedBy(loggedUser);
						//mealOrderDetails.setModifiedOn(new Date());
						//Double refundAmt = mealOrderDetails.getOrderAmount() - orderAmount;
						totalOrderAmt = totalOrderAmt+(orderAmount-mealOrderDetails.getOrderAmount());
						MealOrderDetails mealOrderDetails2 = new MealOrderDetails();
						BeanUtils.copyProperties(mealOrderDetails, mealOrderDetails2);
						mealOrderDetails2.setCrudOperationVal(1);
						mealOrderDetails2.setLoggedUser(loggedUser);
						mealOrderDetails2.setModifiedOn(new Date());
						mealOrderDetails2.setModifiedBy(loggedUser);
						mealOrderDetails2.setTotalPrice(totalPrice);
						mealOrderDetails2.setItems_count(itemCount);
						//mealOrderDetails.setSchoolMeals(schoolMeals);
						mealOrderDetails2.setMealCalendars(mealCalendars);
						String menuOrderedPdfLink = orderedMenuPdfUtility.orderedMenuPdfLink(mealOrderDetails2, true);
						mealOrderDetails2.setMenuOrderedPdfLink(menuOrderedPdfLink);
						if(orderAmount-mealOrderDetails2.getOrderAmount() > 0)
							mealOrderDetails2.setPaymentIdChanged(true);
						mealOrderDetails2.setOrderAmount(orderAmount);
						mealOrderDetails2.setIsEligibleForFreeMeal(orderEligibleForFreeMeal);
						mealOrderDetails2.setIsEligibleForReducedPrice(orderEligibleForReducedPrice);
						mealOrderDetails2.setIsEligForDiscount(orderEligibleForDiscount);
						mealOrderDetails2.setItemDiscount(discount);
						orderList.add(mealOrderDetails2);
						serviceResponse.setStatusCode(200);
					}else{
						if(mealCalendars.size() < 1 || mealCalendars.size() != orderEntry.getValue().size())
							serviceResponse.setStatusMessage("Please select the valid menu item for specified student grade.");
						else if(mealOrderDetails.getPaymentStatus())
							serviceResponse.setStatusMessage("Menu order can not update as payment already done.");
						else
							serviceResponse.setStatusMessage("Menu order can not update as cut off date is over.");	
						serviceResponse.setStatusCode(417);
						serviceResponse.setStatus("Failed");
						logger.info("WARN:: "+serviceResponse.getStatusMessage()+" with studentId::"+mealOrders.getStudentId()+" for month "+orderEntry.getKey());
					}
				}else{
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusMessage("Menu items not published yet by School.");
					logger.info("WARN:: "+serviceResponse.getStatusMessage()+" with studentId::"+mealOrders.getStudentId()+" for month "+orderEntry.getKey());
					break;
				}
			}else{
				serviceResponse.setStatusMessage("Student is not active.");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatus("Failed");
				logger.info("WARN:: "+"Failed to order menu as "+serviceResponse.getStatusMessage()+" with studentId::"+studentUser.getStudentId());
				break;
			}	
			}
			totalOrderAmt = Double.parseDouble(String.format("%.2f", (totalOrderAmt)));
			if(mealOrders.getInstantPayAmt() == null)
				mealOrders.setInstantPayAmt(0.0);
			if(mealOrders.getWalletAmt() == null)
				mealOrders.setWalletAmt(0.0);
			if(isInstantPayment && totalOrderAmt < 0 && serviceResponse.getStatusCode() == 200)
				addInstantPayTrx(loggedUser, mealSchool, mealOrders, -(totalOrderAmt), menuType);
			if(serviceResponse.getStatusCode() == 200){
				if((!isInstantPayment || ((totalOrderAmt > 0 && totalOrderAmt <= (mealOrders.getWalletAmt()+mealOrders.getInstantPayAmt())) || totalOrderAmt <= 0))){
					Long trxId = null;
					mealOrders.setFinalOrderAmt(totalOrderAmt);
					if(isInstantPayment && (totalOrderAmt > 0 || mealOrders.getInstantPayAmt() > 0))
						trxId = addInstantPayTrx(loggedUser, mealSchool, mealOrders, null, menuType);
					for(MealOrderDetails mealOrderDetails : orderList){
						if(isInstantPayment && mealOrderDetails.isPaymentIdChanged())
							mealOrderDetails.setInstantPaymentId(trxId);
						mealOrderDetailsRepository.save(mealOrderDetails);
					}
					entityManager.flush();
					for(MealOrderDetails mealOrderDetails : orderList){
						//if(menuType.toString().equalsIgnoreCase(ItemTypeConstants.Itemized.toString()))
							orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, schoolName, userRole, logoLink, 
								parentUser, priEmailIsSubscribe, altEmailIsSubscribe, schoolTimezone,countryDetail.getCurrencySymbol(), adminEmail, parentUserEmail, isItemized, 
								countryDetail.getDateFormat(),CommonUtil.getNonSchoolDays(mealSchool),false);
					}
				}else{
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusMessage("Total paid amount does not match!!");
					logger.info("WARN:: "+serviceResponse.getStatusMessage()+" Total paid amount::"+(mealOrders.getWalletAmt()+mealOrders.getInstantPayAmt())+" and minimum total amount need to pay::"+(totalOrderAmt)
					+" and mealSchoolId::"+studentUser.getMealSchool().getSchoolId()+" and studentRecId::"+studentUser.getUserId());
					return serviceResponse;
				}
			}				
			if(serviceResponse.getStatusCode() == 200){
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusMessage("Order processed successfully.");
				if(orderList.size() < 1)
					serviceResponse.setStatusMessage("Order processed successfully without any changes.");
			}
			logger.info(serviceResponse.getStatusMessage()+" with studentId::"+mealOrders.getStudentId());
		}catch(Exception e){
			serviceResponse.setStatusMessage("Failed to order the menu for kid.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" with studentId::"+mealOrders.getStudentId()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for refresh the order menu pdf
	 * @throws Exception **/
	@Override
	public ServiceResponse refreshOrderPdf(Long orderId) throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();
		MealOrderDetails mealOrderDetails = mealOrderDetailsRepository.findOne(orderId);
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("Ordered item pdf refreshed successfully.");
		if(mealOrderDetails != null){
			MealSchool mealSchool = mealOrderDetails.getStudentUser().getMealSchool();
			Boolean isItemized  = CommonUtil.checkItemized(mealSchool);
			String lastModifiedUser =  (mealOrderDetails.getModifiedBy() != null ? 
					mealOrderDetails.getModifiedBy() : mealOrderDetails.getCreatedBy());
			String role = usersAuthInfoRepository.findByUsername(lastModifiedUser).getRole();
			if(role.equalsIgnoreCase("ROLE_ADMIN"))
				role = "Admin";
			else
				role = "Parent";
			CountryDetail countryDetail =  countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
			orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, mealSchool.getSchoolName(), role, mealSchool.getLogoLink(), 
							null, null, null, mealSchool.getSchoolTimezone(),countryDetail.getCurrencySymbol(), null, null, isItemized, 
							countryDetail.getDateFormat(),CommonUtil.getNonSchoolDays(mealSchool),true);
		}else{
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Order doesn't exist with this order id!!");
				logger.info("WARN:: "+serviceResponse.getStatusMessage()+" orderId::"+orderId);
				return serviceResponse;
		}		
		logger.info(serviceResponse.getStatusMessage()+" with orderId::"+orderId);
		return serviceResponse;
	}
	
	/**This method used for add/update the menu orders to students**/
	@SuppressWarnings("unused")
	public ServiceResponse orderMenu(List<StudentMealOrders> studentMealOrders, String loggedUser, String yearMonth){
		ServiceResponse serviceResponse = new ServiceResponse();
		String schoolName = "";
		String adminEmail = "";
		String logoLink = "";
		String schoolTimezone = "";
		MealSchool mealSchool = null;
		Boolean isInstantPayment = false;
		int i = 0;
		try{
			String userRole = "Parent";
			StudentUser studentUser1 = studentUserRepository.findOne(studentMealOrders.get(0).getStudentId());
			String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(studentUser1.getMealSchool().getSchoolId()));
			if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null && 
					SecurityContextHolder.getContext().getAuthentication().getAuthorities() != null && 
					SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().toUpperCase().contains("ADMIN"))
				userRole = "Admin";
			for(StudentMealOrders studentMealOrders2 : studentMealOrders){
				MealOrderDetails mealOrderDetails = mealOrderDetailsRepository.findByStudentUserUserIdAndYearMonthAndMenuType(
						studentMealOrders2.getStudentId(), yearMonth, ItemTypeConstants.Lunch);
				StudentUser studentUser = studentUserRepository.findByUserIdAndIsActive(studentMealOrders2.getStudentId(), true);
				String parentUserEmail = "";//studentMealOrders2.getParentUserEmails();
				if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null 
						&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().toUpperCase().contains("ROLE_PARENT"))
					parentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
				else if(studentUser.getDefaultNotifyEmail() == null || studentUser.getDefaultNotifyEmail().contains("Primary"))
					parentUserEmail = studentUser.getParentuser().getUserName();
				else if(studentUser.getDefaultNotifyEmail().contains("Alternate") && studentUser.getParentuser().getParentAltEmail() != null)
					parentUserEmail = studentUser.getParentuser().getParentAltEmail();
				else if(studentUser.getDefaultNotifyEmail().contains("Both"))
					parentUserEmail = studentUser.getParentuser().getUserName()+(studentUser.getParentuser().getParentAltEmail() != null ? (","+studentUser.getParentuser().getParentAltEmail()) :"");
				Date cutOffDateTime = null;
				Boolean orderDtExtensionStatus = null;
				Boolean isPublishedStatus = false;
			if(studentUser != null){/*
				Set<SchoolMeal> schoolMeals = schoolMealsRepo.findBySchoolIdInAndGradesAndMealSchoolSchoolIdAndIsDelete(
						studentMealOrders2.getSchoolMealIds(), studentUser.getGradeName(), studentUser.getMealSchool().getSchoolId(), 
						false);
				Double totalPrice = 0.0;
				Double reducedPriceTotal = 0.0;
				Double orderAmount = 0.0;
				int itemCount = 0;
				Boolean menusEligibleForReducedPrice = false;
				Boolean orderEligibleForReducedPrice = false;
				for(SchoolMeal schoolMeal : schoolMeals){
					if(schoolMeal.getMealMenu().getType().toString().equalsIgnoreCase("MEAL")){
						totalPrice = totalPrice+schoolMeal.getMealMenu().getPrice();
						if(schoolMeal.getMealMenu().getReducedPrice() != null)
							reducedPriceTotal = reducedPriceTotal+schoolMeal.getMealMenu().getReducedPrice();
						itemCount++;
					}
						
					if(i == 0){
						mealSchool = schoolMeal.getMealSchool();
						schoolName = mealSchool.getSchoolName();
						adminEmail = mealSchool.getContactPEmail() != null ? mealSchool.getContactPEmail() : "";
						logoLink = mealSchool.getLogoLink();
						cutOffDateTime = schoolMeal.getSchoolMealSummary().getCutOffDateTime();
						orderDtExtensionStatus = schoolMeal.getSchoolMealSummary().getOrderDateExtensionStatus();
						isPublishedStatus = schoolMeal.getSchoolMealSummary().getIsPublished();
						schoolTimezone = mealSchool.getSchoolTimezone().toString();
						menusEligibleForReducedPrice = schoolMeal.getSchoolMealSummary().getReducedPriceStatus();
						if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Instant Payment for Orders") != null && mealSchool.getModuleAccess().get("Instant Payment for Orders").equalsIgnoreCase("Yes"))
							isInstantPayment = true;
					}	
					++i;
				}
				if(studentUser.getIsFreeMealEligible() != null && studentUser.getIsFreeMealEligible())
					orderAmount = 0.0;
				else if(studentUser.getIsReducePriceEligible() != null && studentUser.getIsReducePriceEligible() && 
						menusEligibleForReducedPrice){
					orderAmount = Double.parseDouble(String.format("%.2f", reducedPriceTotal));
					orderEligibleForReducedPrice = true;
				}else
					orderAmount = Double.parseDouble(String.format("%.2f", totalPrice));
				
				totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
				if(isInstantPayment){
					Double orderAmtVal = orderAmount;
					if(mealOrderDetails != null && (orderAmount-mealOrderDetails.getOrderAmount()) > 0)
						orderAmtVal =orderAmount- mealOrderDetails.getOrderAmount();
					else if(mealOrderDetails != null)
						orderAmtVal = studentMealOrders2.getWalletAmt()+studentMealOrders2.getInstantPayAmt();
					if(orderAmtVal != (studentMealOrders2.getWalletAmt()+studentMealOrders2.getInstantPayAmt())){
						serviceResponse.setStatus("Failed");
						serviceResponse.setStatusCode(500);
						serviceResponse.setStatusMessage("Order amount does not match with total paid amount.");
						logger.info("WARN:: "+serviceResponse.getStatusMessage()+" Order Amt::"+orderAmount+" and total Amt::"+studentMealOrders2.getWalletAmt()+studentMealOrders2.getInstantPayAmt());
						return serviceResponse;
					}					
				}
				if(isPublishedStatus != null && isPublishedStatus){
					Boolean priEmailIsSubscribe = null;
					Boolean altEmailIsSubscribe = null;
					UsersAuthInfo usersAuthInfo = null;
					ParentUser parentUser = studentUserRepository.findByUsername(studentUser.getParentuser().getUserName());
					if(parentUser.getUserName() != null){
						usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getUserName());
						priEmailIsSubscribe = usersAuthInfo.getEmailIsSubscribe();
					}
					if(parentUser.getParentAltEmail() != null && !parentUser.getParentAltEmail().trim().equalsIgnoreCase("")){
						usersAuthInfo = usersAuthInfoRepository.findByUsername(parentUser.getParentAltEmail());
						altEmailIsSubscribe = usersAuthInfo.getEmailIsSubscribe();
					}
					
					if(orderDtExtensionStatus == null)
						orderDtExtensionStatus = false;
					*//**Check if orderDtExtensionStatus is false and user having admin role then allow it to create/edit the order after cutOffDate also**//*
					if(!orderDtExtensionStatus && SecurityContextHolder.getContext().getAuthentication().getAuthorities()
							.toString().contains("ROLE_ADMIN"))
						orderDtExtensionStatus = true;
					if(mealOrderDetails == null){
							if(schoolMeals.size() > 0 && schoolMeals.size() == studentMealOrders2.getSchoolMealIds().size() && 
	                                (cutOffDateTime.compareTo(new Date()) >= 0 || orderDtExtensionStatus)){						
								mealOrderDetails = new MealOrderDetails();
								//mealOrderDetails.setCreatedBy(loggedUser);
								//mealOrderDetails.setCreatedOn(new Date());
								mealOrderDetails.setCrudOperationVal(0);
								mealOrderDetails.setLoggedUser(loggedUser);
								mealOrderDetails.setStudentUser(studentUser);
								mealOrderDetails.setTotalPrice(totalPrice);
								mealOrderDetails.setYearMonth(yearMonth);
								mealOrderDetails.setItems_count(itemCount);
								mealOrderDetails.setSchoolMeals(schoolMeals);
								mealOrderDetails.setCutOffDateTime(cutOffDateTime);
								String menuOrderedPdfLink = orderedMenuPdfUtility.orderedMenuPdfLink(mealOrderDetails, true);
								mealOrderDetails.setMenuOrderedPdfLink(menuOrderedPdfLink);
								mealOrderDetails.setOrderAmount(orderAmount);
								mealOrderDetails.setIsEligibleForFreeMeal(studentUser.getIsFreeMealEligible() != null ?
										studentUser.getIsFreeMealEligible() : null);
								mealOrderDetails.setIsEligibleForReducedPrice(orderEligibleForReducedPrice);
								if(isInstantPayment)
									mealOrderDetails.setPaymentStatus(true);
								mealOrderDetailsRepository.save(mealOrderDetails);
								if(isInstantPayment)
									transactionsDao.addInstantPayTrx(mealOrderDetails.getSchoolId(), loggedUser, 
												mealSchool, studentMealOrders2, yearMonth, null);
								//entityManager.persist(mealOrderDetails);
								entityManager.flush();
								orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, schoolName, userRole, logoLink, 
										parentUser, priEmailIsSubscribe, altEmailIsSubscribe, schoolTimezone,currencySymbol, adminEmail, parentUserEmail);
								serviceResponse.setStatusMessage("Menu ordered successfully.");
								logger.info("Menu ordered successfully");
								serviceResponse.setStatusCode(200);
								serviceResponse.setStatus("Success");
								}else{
									if(schoolMeals.size() != studentMealOrders2.getSchoolMealIds().size()){
										serviceResponse.setStatusMessage("Menu can not order as grade is invalid.");
										logger.info("Menu can not order as grade is invalid.");
									}else{
										serviceResponse.setStatusMessage("Menu can not order as cut off date is already over.");
										logger.info("Menu can not order as cut off date is already over");
									}
									serviceResponse.setStatusCode(417);
									serviceResponse.setStatus("Failed");
								}						
					}else if(schoolMeals.size() > 0 && !mealOrderDetails.getPaymentStatus() && (cutOffDateTime.compareTo(new Date()) >= 0  || orderDtExtensionStatus)
							&& schoolMeals.size() == studentMealOrders2.getSchoolMealIds().size()){
							//mealOrderDetailsRepository.delete(mealOrderDetails);
							//mealOrderDetails.setModifiedBy(loggedUser);
							//mealOrderDetails.setModifiedOn(new Date());
							Double refundAmt = mealOrderDetails.getOrderAmount() - orderAmount;
							mealOrderDetails.setCrudOperationVal(1);
							mealOrderDetails.setLoggedUser(loggedUser);
							mealOrderDetails.setTotalPrice(totalPrice);
							mealOrderDetails.setItems_count(itemCount);
							mealOrderDetails.setSchoolMeals(schoolMeals);
							String menuOrderedPdfLink = orderedMenuPdfUtility.orderedMenuPdfLink(mealOrderDetails, true);
							mealOrderDetails.setMenuOrderedPdfLink(menuOrderedPdfLink);
							mealOrderDetails.setOrderAmount(orderAmount);
							mealOrderDetails.setIsEligibleForFreeMeal(studentUser.getIsFreeMealEligible() != null ? 
									studentUser.getIsFreeMealEligible() : null);
							mealOrderDetails.setIsEligibleForReducedPrice(orderEligibleForReducedPrice);
							mealOrderDetailsRepository.save(mealOrderDetails);
							if(refundAmt > 0)
								transactionsDao.addInstantPayTrx(mealOrderDetails.getSchoolId(), loggedUser, 
										mealSchool, studentMealOrders2, yearMonth, refundAmt);
							else if(refundAmt < 0)
								transactionsDao.addInstantPayTrx(mealOrderDetails.getSchoolId(), loggedUser, 
										mealSchool, studentMealOrders2, yearMonth, null);
								
							//entityManager.merge(mealOrderDetails);
							entityManager.flush();
							orderedMenuPdfUtility.orderedMenuPdf(mealOrderDetails, schoolName, userRole, logoLink, 
									parentUser, priEmailIsSubscribe, altEmailIsSubscribe,schoolTimezone,currencySymbol, adminEmail, parentUserEmail);
							serviceResponse.setStatusMessage("Meal orders has been updated successfully.");
							logger.info("Meal orders has been updated successfully in Dao");
							serviceResponse.setStatusCode(200);
							serviceResponse.setStatus("Success");
						}else{
							if(schoolMeals.size() < 1 || schoolMeals.size() != studentMealOrders2.getSchoolMealIds().size()){
								serviceResponse.setStatusMessage("Please select the valid menu item for specified student grade.");
								logger.info("Please select the valid menu item for specified student grade");
							}else if(mealOrderDetails.getPaymentStatus()){
								serviceResponse.setStatusMessage("Menu order can not update as payment already done.");
								logger.info("Menu order can not update as payment already done.");
							}else{
								serviceResponse.setStatusMessage("Menu order can not update as cut off date is over.");
								logger.info("Menu order can not update as cut off date is over.");
							}
							
							serviceResponse.setStatusCode(417);
							serviceResponse.setStatus("Failed");
						}
				}else{
					serviceResponse.setStatusCode(400);
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusMessage("Menu items not published yet by School.");
					logger.info("Menu items not published yet by School.");
				}
			*/}else{
				serviceResponse.setStatusMessage("There are no activate and register children for order menu.");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatus("Failed");
				logger.info("There are no activate and register children for order menu");
			}	
			}	
		}catch(Exception e){
			logger.error("Failed to order the Menu for the Student Id: "+studentMealOrders.get(0).getStudentId() +" under the month "+yearMonth+" due to "+e.getMessage());
			serviceResponse.setStatusMessage("Failed to order the menu for children.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	
	/**This method used for update the order payment staus**/
	public ServiceResponse orderPayment(List<Long> studentRecIds, String loggedUser, String yearMonth){
		ServiceResponse serviceResponse = new ServiceResponse();
		String status = "Failed to update the order payment staus";
		Integer statusCode;
		try{
			for(Long studentRecId : studentRecIds){
				MealOrderDetails mealOrderDetails = mealOrderDetailsRepository.findByStudentUserUserIdAndYearMonthAndPaymentStatus(studentRecId,
							yearMonth, false);
				if(mealOrderDetails != null){
					mealOrderDetails.setModifiedBy(loggedUser);
					mealOrderDetails.setModifiedOn(new Date());
					mealOrderDetails.setPaymentStatus(true);
					entityManager.merge(mealOrderDetails);
				}
			}	
			status = "Order payment updated successfully";
			statusCode=200;
			serviceResponse.setStatus("Success");
		}catch(Exception e){
			logger.error("Error occured during execution of orderPayment API.");
			statusCode=500;
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		serviceResponse.setStatusMessage(status);
		serviceResponse.setStatusCode(statusCode);
		return serviceResponse;
	}
	
	/**This method used for update the MealSchool and user
	 * @throws Exception **/
	public ServiceResponse mealSchoolUpdate(MealSchool mealSchool, Long mealSchoolId) throws Exception{
		ServiceResponse serviceResponse = new ServiceResponse();
		String status = "Failed to update the School and School users.";
		//try{
			MealSchool mealSchool2 = mealSchoolRepository.findBySchoolId(mealSchoolId);
			if(mealSchool.getContactPName() != null)
				mealSchool2.setContactPName(mealSchool.getContactPName());
			if(mealSchool.getContactPPhone() != null)
				mealSchool2.setContactPPhone(mealSchool.getContactPPhone());
			if(mealSchool.getContactPEmail() != null)
				mealSchool2.setContactPEmail(mealSchool.getContactPEmail());
			if(mealSchool.getSchoolTimezone() != null && !mealSchool.getSchoolTimezone().toString().equalsIgnoreCase(""))
				mealSchool2.setSchoolTimezone(mealSchool.getSchoolTimezone());
			if(mealSchool.getIsPaymentEnabled() != null)
				mealSchool2.setIsPaymentEnabled(mealSchool.getIsPaymentEnabled());
			if(mealSchool.getContactPAddress() != null)
				mealSchool2.setContactPAddress(mealSchool.getContactPAddress());
			if(mealSchool.getPrincipalName() != null)
				mealSchool2.setPrincipalName(mealSchool.getPrincipalName());
			if(mealSchool.getPrincipalEmail() != null)
				mealSchool2.setPrincipalEmail(mealSchool.getPrincipalEmail());
			if(mealSchool.getPrincipalPhone() != null)
				mealSchool2.setPrincipalPhone(mealSchool.getPrincipalPhone());
			if(mealSchool.getPrincipalAddress() != null)
				mealSchool2.setPrincipalAddress(mealSchool.getPrincipalAddress());
			if(mealSchool.getCountryCode() != null && !mealSchool.getCountryCode().equalsIgnoreCase(""))
				mealSchool2.setCountryCode(mealSchool.getCountryCode());
			if(mealSchool.getTierName() != null)
				mealSchool2.setTierName(mealSchool.getTierName());
			if(mealSchool.getModuleAccess() != null)
				mealSchool2.setModuleAccess(mealSchool.getModuleAccess());
			if(mealSchool.getPaymentGateways() != null){
				mealSchool2.getPaymentGateways().clear();
				mealSchool2.getPaymentGateways().addAll(mealSchool.getPaymentGateways());
			}
			if(mealSchool.getCatererId() != null)
				mealSchool2.setCatererId(mealSchool.getCatererId());
			if(mealSchool.getDistrictId() != null)
				mealSchool2.setDistrictId(mealSchool.getDistrictId());
			/*mealSchool2.setSupportSIS(mealSchool.isSupportSIS());
			mealSchool2.setSupportBCPrg(mealSchool.isSupportBCPrg());
			mealSchool2.setSupportFreeReducedPrg(mealSchool.isSupportFreeReducedPrg());
			mealSchool2.setSchoolProvideBreakfast(mealSchool.isSchoolProvideBreakfast());
			mealSchool2.setMenuByYear(mealSchool.isMenuByYear());
			mealSchool2.setSupportStaffLunch(mealSchool.isSupportStaffLunch());
			mealSchool2.setSupportInstantPayment(mealSchool.isSupportInstantPayment());*/
			mealSchool2.setTrxFeeOnSchool(mealSchool.isTrxFeeOnSchool());
			Set<SchoolUser> schoolUsers = mealSchool2.getSchoolUsers();
			String primaryUserEmail = "";
			for(SchoolUser schoolUser : mealSchool.getSchoolUsers()){
				if(schoolUser.getIsPrimaryUser() != null && schoolUser.getIsPrimaryUser() && schoolUser.getUsername() != null
						 && schoolUser.getIsPaymentRegister() != null && schoolUser.getIsPaymentRegister()){
					primaryUserEmail = schoolUser.getUsername();
				}
				SchoolUser schoolUser2 = mealSchoolRepository.schoolUser(schoolUser.getUsername());
				if(schoolUser2 == null){
					schoolUser2 = new SchoolUser();
					schoolUser2.setCreatedBy(mealSchool2.getLoggedUser());
					schoolUser2.setCreatedOn(new Date());
					schoolUser2.setFirstName(schoolUser.getFirstName());
					schoolUser2.setLastName(schoolUser.getLastName());
					schoolUser2.setMobileNo(schoolUser.getMobileNo());
					schoolUser2.setRole("ROLE_ADMIN");
					schoolUser2.setUsername(schoolUser.getUsername());
					schoolUser2.setIsPrimaryUser(schoolUser.getIsPrimaryUser());
					schoolUser2.setIsPaymentRegister(schoolUser.getIsPaymentRegister());
					schoolUser2.setIsUnsubscribeGenNotif(schoolUser.getIsUnsubscribeGenNotif());
					schoolUser2.setPin(schoolUser.getPin());
					schoolUsers.add(schoolUser2);	
				}else{
					schoolUsers.remove(schoolUser2);
					schoolUser2.setModifiedBy(mealSchool2.getLoggedUser());
					schoolUser2.setModifiedOn(new Date());
					if(schoolUser.getFirstName() != null && !schoolUser.getFirstName().equalsIgnoreCase(""))
						schoolUser2.setFirstName(schoolUser.getFirstName());
					if(schoolUser.getLastName() != null && !schoolUser.getLastName().equalsIgnoreCase(""))
						schoolUser2.setLastName(schoolUser.getLastName());
					if(schoolUser.getMobileNo() != null && !schoolUser.getMobileNo().equalsIgnoreCase(""))
						schoolUser2.setMobileNo(schoolUser.getMobileNo());
					schoolUser2.setIsPrimaryUser(schoolUser.getIsPrimaryUser());
					schoolUser2.setIsPaymentRegister(schoolUser.getIsPaymentRegister());
					schoolUser2.setIsUnsubscribeGenNotif(schoolUser.getIsUnsubscribeGenNotif());
					if(schoolUser.getPin() != null && !schoolUser.getPin().trim().isEmpty())
						schoolUser2.setPin(schoolUser.getPin());
					schoolUsers.add(schoolUser2);
				}

				UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(schoolUser.getUsername(), "ROLE_ADMIN");
				if(usersAuthInfo == null){
					usersAuthInfo = new UsersAuthInfo();
					usersAuthInfo.setUsername(schoolUser.getUsername());
					usersAuthInfo.setRole("ROLE_ADMIN");/*
					usersAuthInfo.setfToken(UUID.randomUUID().toString());
					usersAuthInfo.setfTokenTime(new Date());*/
					usersAuthInfo.setCreatedBy(mealSchool.getLoggedUser());
					usersAuthInfo.setCreatedOn(new Date());
					usersAuthInfo.setMobile(schoolUser.getMobileNo());
					entityManager.persist(usersAuthInfo);
				}else if(schoolUser.getMobileNo() != null && !usersAuthInfo.getMobile().equalsIgnoreCase(schoolUser.getMobileNo())){
					usersAuthInfo.setModifiedBy(mealSchool.getLoggedUser());
					usersAuthInfo.setModifiedOn(new Date());
					usersAuthInfo.setMobile(schoolUser.getMobileNo());
					entityManager.merge(usersAuthInfo);
				}
			}
			Boolean isStripeEnable = false;
			if(mealSchool2.getPaymentGateways() != null)
			for(SchoolPayGatewayInfo schoolGateway : mealSchool2.getPaymentGateways()){
				PaymentGateway paymentGateway = paymentGatewayRepo.findOne(schoolGateway.getPaymentGateway().getId());
				if(paymentGateway.getName().equalsIgnoreCase("Stripe")){
					isStripeEnable = true;
					break;
				}
			}
			if(isStripeEnable && mealSchool.getIsPaymentEnabled() != null && mealSchool.getIsPaymentEnabled()){
				if(mealSchool2.getStripeAccountId() == null || mealSchool2.getStripeAccountId().isEmpty()){
					/*Account account = stripeUtil.createStripeAccount(primaryUserEmail, "US", mealSchool2.getSchoolName());
					mealSchool2.setStripeAccountId(account.getId());
					mealSchool2.setSecret_key(account.getKeys().getSecret());
					mealSchool2.setPublic_key(account.getKeys().getPublishable());*/
					stripeUtil.sendStripeSetupEmail(primaryUserEmail, mealSchool2.getSchoolName());
					logger.info("Sending email to the school primary admin user for stripe account setup");
				}
			}
			mealSchool2.setSchoolUsers(schoolUsers);
			entityManager.merge(mealSchool2);
			status = "School and School Users has been updated successfully.";
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
		/*}catch(Exception e){
			logger.error("Error occured during execution of mealSchoolUpdate API.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
		}*/
		serviceResponse.setStatusMessage(status);
		return serviceResponse;
	}
	
	/**This method used for activate / inActivate the school admin user**/
	public ServiceResponse enableDisableSchoolUser(String schoolUserName, Boolean activeStatus, String loggedUser){
		ServiceResponse serviceResponse= new ServiceResponse();
		try{
			SchoolUser schoolUser = mealSchoolRepository.schoolUser(schoolUserName);
			if(activeStatus != null){
				if(activeStatus != schoolUser.getIsActive()){
					schoolUser.setIsActive(activeStatus);
					schoolUser.setModifiedBy(loggedUser);
					schoolUser.setModifiedOn(new Date());
					entityManager.merge(schoolUser);
				}
			}
			if(activeStatus)
				serviceResponse.setStatusMessage("User account has been acivated.");
			else
				serviceResponse.setStatusMessage("User account has been deactivated.");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
		}catch(Exception e){
			logger.error("Error occured during execution of enableDisableSchoolUser API.");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			if(activeStatus)
				serviceResponse.setStatusMessage("Failed to activate the user account.");
			else
				serviceResponse.setStatusMessage("Failed to deactivate the user account.");
		}
		logger.info(serviceResponse.getStatusMessage());
		return serviceResponse;
	}
	
	/**This method used for activate/deactivate this district user**/
	@Override
	public ServiceResponse enableDisableDistrictUser(String userName, Boolean activeStatus, String loggedUser) {
		ServiceResponse serviceResponse= new ServiceResponse();
		try{
			DistrictUser districtUser = districtRepository.districtUser(userName);
			if(activeStatus != null){
				if(activeStatus != districtUser.getIsActive()){
					districtUser.setIsActive(activeStatus);
					districtUser.setModifiedBy(loggedUser);
					districtUser.setModifiedOn(new Date());
					entityManager.merge(districtUser);
				}
			}
			if(activeStatus)
				serviceResponse.setStatusMessage("User account has been acivated.");
			else
				serviceResponse.setStatusMessage("User account has been deactivated.");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
		}catch(Exception e){
			logger.error("Error occured during execution of enableDisableSchoolUser API.");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			if(activeStatus)
				serviceResponse.setStatusMessage("Failed to activate the user account.");
			else
				serviceResponse.setStatusMessage("Failed to deactivate the user account.");
		}
		logger.info(serviceResponse.getStatusMessage());
		return serviceResponse;
	}
	
	/**This method used for update the student user**/
	public ServiceResponse studentUpdate(StudentUser studentUser, Long studentRecId, String processType, Date schoolYearEndDate, Boolean isMMGenId){
		
		ServiceResponse serviceResponse = new ServiceResponse();
		//try{
			StudentUser studentUser2 = studentUserRepository.findByUserIdAndIsActive(studentRecId, true);
			if(studentUser2 != null){
				if(schoolYearEndDate == null)
					schoolYearEndDate = schoolYearRepository.getSchoolYearEndDate(studentUser2.getMealSchool().getSchoolId(), studentUser2.getSchoolYear());
				Integer previousEligStatus = getEligStatus(studentUser2.getIsFreeMealEligible(), studentUser2.getIsReducePriceEligible());
				List<String> gradesList = new ArrayList<String>();
				MealSchool mealSchool = mealSchoolRepository.findBySchoolId(studentUser2.getMealSchool().getSchoolId());
				/**Check the grades if it's not null and not same as existing one then get all the possible grades for the meal school**/
				if(studentUser.getGradeName() != null 
						&& !studentUser.getGradeName().toString().equalsIgnoreCase(studentUser2.getGradeName().toString())){
					for(SchoolType type : mealSchool.getSchool().getSchoolType()){
						gradesList.addAll(type.getValues());
					}		
				}
				String isdCode = countryDetailsRepository.getIsdCode(mealSchool.getCountryCode());
				String mob = studentUser.getMobileNo();
				if(mob != null){
					mob = mob.replaceAll("[^a-zA-Z0-9]", "");
					if(mob.length() < 10)
						mob = null;
					else if(mob.length() > 10)
						mob = "+"+mob;
					else
						mob = isdCode+mob;
					studentUser.setMobileNo(mob);
				}
			
			/**Update the existing student info**/
			if(studentUser.getFirstName() != null && !studentUser.getFirstName().equalsIgnoreCase(""))
				studentUser2.setFirstName(studentUser.getFirstName());	
			if(studentUser.getLastName() != null && !studentUser.getLastName().equalsIgnoreCase(""))
				studentUser2.setLastName(studentUser.getLastName());
			if(studentUser.getAllergies() != null && !studentUser.getAllergies().equalsIgnoreCase(""))
				studentUser2.setAllergies(studentUser.getAllergies());
			if(studentUser.getAdditionalNotes() != null)
				studentUser2.setAdditionalNotes(studentUser.getAdditionalNotes());
			if(studentUser.getTeacherName() != null && !studentUser.getTeacherName().equalsIgnoreCase(""))
				studentUser2.setTeacherName(studentUser.getTeacherName());
			if(studentUser.getGradeName() != null 
					&& !studentUser.getGradeName().toString().equalsIgnoreCase(studentUser2.getGradeName().toString())){
				if(gradesList.contains(studentUser.getGradeName().toString())){
					studentUser2.setGradeName(studentUser.getGradeName());
				}else{
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(422);
					serviceResponse.setStatusMessage("Selected Grade is not valid.");
					logger.info("Selected Grade is not valid for the student ID: "+studentUser.getStudentId());
					return serviceResponse;
				}
			}
			if(studentUser.getMobileNo() != null && !studentUser.getMobileNo().equalsIgnoreCase(""))
				studentUser2.setMobileNo(studentUser.getMobileNo());
			if(studentUser.getStudentId() != null && !studentUser.getStudentId().equalsIgnoreCase(""))
				studentUser2.setStudentId(studentUser.getStudentId());
			if(studentUser.getIsReducePriceEligible() != null)
				studentUser2.setIsReducePriceEligible(studentUser.getIsReducePriceEligible());
			if(studentUser.getIsFreeMealEligible() != null)
				studentUser2.setIsFreeMealEligible(studentUser.getIsFreeMealEligible());
			if(studentUser.getSchoolYear() != null && studentUser.getSchoolYear() != 0)
				studentUser2.setSchoolYear(studentUser.getSchoolYear());
			if(studentUser.getNumberStreetApt() != null && !studentUser.getNumberStreetApt().isEmpty())
				studentUser2.setNumberStreetApt(studentUser.getNumberStreetApt());
			if(studentUser.getCityStateZip() != null && !studentUser.getCityStateZip().isEmpty())
				studentUser2.setCityStateZip(studentUser.getCityStateZip());
			if(studentUser.isBeforeCare() != studentUser2.isBeforeCare())
				studentUser2.setBeforeCare(studentUser.isBeforeCare());
			if(studentUser.isHasMilkCard() != studentUser2.isHasMilkCard())
				studentUser2.setHasMilkCard(studentUser.isHasMilkCard());
			if(studentUser.getIsEnrollBCAndACPkt() != null && !studentUser.getIsEnrollBCAndACPkt())
				studentUser2.setBeforeCare(false);
			studentUser2.setIsEnrollBCAndACPkt(studentUser.getIsEnrollBCAndACPkt());
			if(studentUser.getSchoolStudentId() != null)
				studentUser2.setSchoolStudentId(studentUser.getSchoolStudentId());
			if(studentUser.getDefaultNotifyEmail() != null)
				studentUser2.setDefaultNotifyEmail(studentUser.getDefaultNotifyEmail());
			if(studentUser.getDecisionReason() != null)
				studentUser2.setDecisionReason(studentUser.getDecisionReason());
			if(studentUser.getCategory() != null)
				studentUser2.setCategory(studentUser.getCategory());
			if(studentUser.getActualPrg() != null)
				studentUser2.setActualPrg(studentUser.getActualPrg());
			//if(!isMMGenId && studentUser.getPin() != null && !studentUser.getPin().trim().isEmpty())
			if(studentUser.getPin() != null && !studentUser.getPin().trim().isEmpty() && (studentUser2.getPin() == null ||
					!df4.format(Integer.parseInt(studentUser.getPin())).equalsIgnoreCase(df4.format(Integer.parseInt(studentUser2.getPin())))))
				studentUser2.setPin(df4.format(Integer.parseInt(studentUser.getPin())));
			if(studentUser2.getReCertificateDate() == null && (studentUser2.getIsFreeMealEligible() || studentUser2.getIsReducePriceEligible()))
				studentUser2.setRecertPending("Y");
				
			/**Get the parent user and update info if required**/
			ParentUser usr = null;
			if(studentUser.getParentuser() != null && studentUser.getParentuser().getUserName() != null)
				usr = studentUserRepository.findByUsername(studentUser.getParentuser().getUserName());
			else
				usr = studentUserRepository.findByUsername(studentUser2.getParentuser().getUserName());
			if(usr == null){
				usr = studentUser.getParentuser();
				usr.setCreatedBy(studentUser.getLoggedUser());
				usr.setCreatedOn(new Date());
				usr.setIsActive(false);
				if(studentUser.getMobileNo() != null)
					usr.setMobileNo(studentUser.getMobileNo());
				else
					usr.setMobileNo(studentUser2.getMobileNo());
				usr.setRole("ROLE_PARENT");
				entityManager.persist(usr);
			}else {
				usr.setModifiedBy(studentUser.getLoggedUser());
				usr.setModifiedOn(new Date());
				if(studentUser.getParentuser() != null && studentUser.getParentuser().getFirstName() != null 
						&& !studentUser.getParentuser().getFirstName().equalsIgnoreCase(""))
					usr.setFirstName(studentUser.getParentuser().getFirstName());
				if(studentUser.getParentuser() != null && studentUser.getParentuser().getLastName() != null 
						&& !studentUser.getParentuser().getLastName().equalsIgnoreCase(""))
					usr.setLastName(studentUser.getParentuser().getLastName());
				if(studentUser.getMobileNo() != null 
						&& !studentUser.getMobileNo().equalsIgnoreCase(""))
					usr.setMobileNo(studentUser.getMobileNo());
				if(studentUser.getParentuser() != null && studentUser.getParentuser().getParentAltEmail() != null 
						&& !studentUser.getParentuser().getParentAltEmail().equalsIgnoreCase(""))
					usr.setParentAltEmail(studentUser.getParentuser().getParentAltEmail());
				
				entityManager.merge(usr);
			}
			
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getUserName(), "ROLE_PARENT");
			if(usersAuthInfo == null){
				usersAuthInfo = new UsersAuthInfo();
				usersAuthInfo.setUsername(usr.getUserName());
				usersAuthInfo.setRole("ROLE_PARENT");
				usersAuthInfo.setCreatedBy(studentUser.getLoggedUser());
				usersAuthInfo.setCreatedOn(new Date());
				usersAuthInfo.setMobile(usr.getMobileNo());
				entityManager.persist(usersAuthInfo);
			}else if(usr.getMobileNo() != null && !usr.getMobileNo().equalsIgnoreCase("") && 
						(!usr.getMobileNo().equalsIgnoreCase(usersAuthInfo.getMobile())) || usersAuthInfo.getMobile() == null){
					usersAuthInfo.setModifiedBy(studentUser.getLoggedUser());
					usersAuthInfo.setModifiedOn(new Date());
					usersAuthInfo.setMobile(usr.getMobileNo());
					entityManager.merge(usersAuthInfo);
			}
			
		if(usr.getParentAltEmail()!=null && !usr.getParentAltEmail().equalsIgnoreCase("")){
			 usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getParentAltEmail(), "ROLE_PARENT");
			if(usersAuthInfo == null){
				usersAuthInfo = new UsersAuthInfo();
				usersAuthInfo.setUsername(usr.getParentAltEmail());
				usersAuthInfo.setRole("ROLE_PARENT");
				usersAuthInfo.setCreatedBy(studentUser.getLoggedUser());
				usersAuthInfo.setCreatedOn(new Date());
				usersAuthInfo.setMobile(usr.getMobileNo());
				entityManager.persist(usersAuthInfo);
			}if(usr.getMobileNo() != null && !usr.getMobileNo().equalsIgnoreCase("") && 
				(!usr.getMobileNo().equalsIgnoreCase(usersAuthInfo.getMobile())) || usersAuthInfo.getMobile() == null){
					usersAuthInfo.setModifiedBy(studentUser.getLoggedUser());
					usersAuthInfo.setModifiedOn(new Date());
					usersAuthInfo.setMobile(usr.getMobileNo());
					entityManager.merge(usersAuthInfo);
				}
		}
		studentUser2.setParentuser(usr);
		studentUser2.setModifiedBy(studentUser.getLoggedUser());
		studentUser2.setModifiedOn(new Date());
		Integer currentEligStatus = getEligStatus(studentUser2.getIsFreeMealEligible(), studentUser2.getIsReducePriceEligible());
		if(currentEligStatus != previousEligStatus){
			try {
				studentUser2.setRecertPending("N");
				studentUser2.setReCertificateDate(sdf.parse(du.formatDateToString(new Date(), "yyyy-MM-dd", studentUser2.getMealSchool().getSchoolTimezone())));
				if(!studentUser2.getIsReducePriceEligible() && !studentUser2.getIsFreeMealEligible()){
					studentUser2.setDecisionReason(null);
					studentUser2.setCategory(null);
					studentUser2.setActualPrg(null);
				}
			} catch (ParseException e) {
				logger.error("Failed to update certification date for student id::"+studentUser2.getStudentId()+" due to "+e.getMessage());
			}
		}
		entityManager.merge(studentUser2);
		if(currentEligStatus != previousEligStatus)
			addUpdateStudentEligibility(currentEligStatus, previousEligStatus, true, null, schoolYearEndDate, processType, studentUser2);
		serviceResponse.setStatusMessage("Student updated successfully.");
		logger.info("Student updated successfully with Student ID: "+studentUser.getStudentId());
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		}else{
			serviceResponse.setStatusMessage("No active student for update.");
			serviceResponse.setStatusCode(422);
			serviceResponse.setStatus("Failed");
			logger.info("No active student for update with Student ID: "+studentUser.getStudentId());
		}/*
		}catch(Exception e){
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update the student");
			logger.error("Failed to update the student with Student ID: "+studentUser.getStudentId());
		}*/
		return serviceResponse;
	}
	

	public ServiceResponse uploadMealSchoolLogo(MultipartFile logoFile, Long mealSchoolId, String loggedUser){
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			String filePath = "";
			File convFile = new File(mealSchool.getSchoolId()+"_School_Logo."+FilenameUtils.getExtension(logoFile.getOriginalFilename()));
			FileOutputStream fos = new FileOutputStream(convFile);
		    fos.write(logoFile.getBytes());
		    fos.close();
			filePath = convFile.getAbsolutePath();
			String finalLogoLink = awsUtility.fileUploadPath(filePath, "schoolLogoLink");
			if(finalLogoLink != null){
				mealSchool.setLogoLink(finalLogoLink);
				mealSchool.setModifiedBy(loggedUser);
				mealSchool.setModifiedOn(new Date());
				entityManager.merge(mealSchool);	
				awsUtility.uploadSchoolLogo(filePath, "MealSchoolLogo");
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("School Logo uploaded successfully.");
			}else{
				serviceResponse.setStatusMessage("Failed to upload school logo.");
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(422);
			}
		}catch(Exception e){
			logger.error("Error occurred during upload logo of Meal School");
			serviceResponse.setStatusMessage("Failed to upload school logo.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
		}
		return serviceResponse;
	}

	/**This method used for return the Meal Menu and Menu Order Status**/
	@Override
	public OrderStatusResp menuOrderStatus(Long studentRecId, String yearMonth) {
		StudentUser studentUser = studentUserRepository.findOne(studentRecId);
		//List<SchoolGrades> schoolGrades = new ArrayList<SchoolGrades>();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//schoolGrades.add(studentUser.getGradeName());
		OrderStatusResp orderStatusResp = new OrderStatusResp();
		/*Set<SchoolMeal> schoolMeals = schoolMealsRepo.findByMealSchoolSchoolIdAndYearMonthAndGradesIn(
				studentUser.getMealSchool().getSchoolId(), yearMonth, schoolGrades);
		if(schoolMeals.size() > 0){*/
		List<String> yearMonths = new ArrayList<String>();
		yearMonths.add(yearMonth);
		try{
			Set<SchoolMealSummary> schoolMealSummaries = schoolMealsRepo.mealMenuPdfByMealSchoolIdAndYearMonthsAndGradesAndPublished(
					studentUser.getMealSchool().getSchoolId(), yearMonths, studentUser.getGradeName());
			if(schoolMealSummaries.size() > 0){
				MealOrderDetails mealOrderDetails = mealOrderDetailsRepository.findByStudentUserUserIdAndYearMonthAndMenuType(studentRecId, yearMonth, ItemTypeConstants.Lunch);
				//mealMenuStatus = mealOrderDetailsRepository.studentOrderStatus(studentRecId, yearMonth);
				if(mealOrderDetails != null){
					orderStatusResp.setTotalPrice(mealOrderDetails.getOrderAmount());
					orderStatusResp.setPaymentStatus(mealOrderDetails.getPaymentStatus());
					orderStatusResp.setOrderStatus(true);
					if(mealOrderDetails.getModifiedOn() != null)
						orderStatusResp.setOrderedDate(mealOrderDetails.getModifiedOn());
					else
						orderStatusResp.setOrderedDate(mealOrderDetails.getCreatedOn());
				}else{
					orderStatusResp.setOrderStatus(false);
				}
			}
			logger.info("Menu Order status API for Student executed successfully.");
		}catch (Exception e) {
			logger.error("Failed to get the order status of Student."+e.getMessage());
		}
		
		return orderStatusResp;
	}
	
	/**This method used for return the Meal Menu and Menu Order Status**/
	@Override
	public OrderStatusResp menuOrderStatusV2(Long studentRecId, String yearMonth) {
		StudentUser studentUser = studentUserRepository.findOne(studentRecId);
		OrderStatusResp orderStatusResp = new OrderStatusResp();
		/*List<String> yearMonths = new ArrayList<String>();
		yearMonths.add(yearMonth);*/
		try{
			MealCalendarSummary summary = mealCalendarSummaryRepository.findBySchoolSchoolIdAndMealTypeAndYearMonthAndGrades(
					studentUser.getMealSchool().getSchoolId(), ItemTypeConstants.Lunch, yearMonth, studentUser.getGradeName());
			if(summary != null){
				MealOrderDetails mealOrderDetails = mealOrderDetailsRepository.findByStudentUserUserIdAndYearMonthAndMenuType(studentRecId, yearMonth, ItemTypeConstants.Lunch);
				//mealMenuStatus = mealOrderDetailsRepository.studentOrderStatus(studentRecId, yearMonth);
				if(mealOrderDetails != null){
					orderStatusResp.setTotalPrice(mealOrderDetails.getOrderAmount());
					orderStatusResp.setPaymentStatus(mealOrderDetails.getPaymentStatus());
					orderStatusResp.setOrderStatus(true);
					if(mealOrderDetails.getModifiedOn() != null)
						orderStatusResp.setOrderedDate(mealOrderDetails.getModifiedOn());
					else
						orderStatusResp.setOrderedDate(mealOrderDetails.getCreatedOn());
				}else
					orderStatusResp.setOrderStatus(false);
			}
			logger.info("Menu Order status API for Student executed successfully.");
		}catch (Exception e) {
			logger.error("Failed to get the order status of Student."+e.getMessage());
		}
		return orderStatusResp;
	}

	/**This method used for create the holidays for school**/
	@Override
	public ServiceResponse createHolidays(List<SchoolHoliday> schoolHolidays, Long mealSchoolId, String loggedUser) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			 Query query = entityManager.createNativeQuery("Insert into schoolholidays(holidayName, holidayDesc, holidayDate, mealSchoolId, createdBy, createdOn) " +
			            " values (?,?,?,?,?,?)");
			 Date today = new Date();
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for(SchoolHoliday schoolHoliday : schoolHolidays){
				query.setParameter(1, schoolHoliday.getHolidayName());
		        query.setParameter(2, schoolHoliday.getHolidayDesc());
		        query.setParameter(3, schoolHoliday.getHolidayDate());
		        query.setParameter(4, mealSchoolId);
		        query.setParameter(5, loggedUser);
		        query.setParameter(6, today);
		        try{
		        	 query.executeUpdate();
		        }catch(Exception e){
		        	if(!e.toString().contains("ConstraintViolationException")){
		        		logger.error("Failed to create holidays "+e.getMessage());
		        		serviceResponse.setStatus("Failed");
		        		serviceResponse.setStatusCode(400);
		        		serviceResponse.setErrorMessage(e.getMessage());
		        		serviceResponse.setStatusMessage("Failed to create school holidays.");
		        		return serviceResponse;
		        	}
		        }
			}
			logger.info("School holidays created successfully.");
			serviceResponse.setStatusMessage("School holidays created successfully.");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
		}catch(Exception e){
			logger.error("Failed to create holidays for the school "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusMessage("Failed to create school holidays.");
			serviceResponse.setStatusCode(500);
		}
		return serviceResponse;
	}

	/**This method used for get all the holiday list of school
	 * @throws Exception **/
	@Override
	public List<Object[]> schoolHolidays(Long mealSchoolId, Date startDate, Date endDate) throws Exception {
		List<Object[]> holidays = new ArrayList<Object[]>();
		holidays = entityManager.createNativeQuery("Select h.holidayName, h.holidayDesc, h.holidayDate, h.mealSchoolId, h.recId from "
				+ "schoolholidays h where h.mealSchoolId = :mealSchoolId and (h.holidayDate between :startDate and :endDate)")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("startDate", sdf.format(startDate)).
						setParameter("endDate", sdf.format(endDate)).getResultList();
		return holidays;
	}

	/**This method used for send the notification to parent regarding pending payment reminder**/
	@Override
	public List<Object[]> sendPaymentReminderToParent(ParentsNotificationRequest parentsNotiReq){
		List<Object[]> objArray = null;
		String query = "Select p.userName, p.parentAltEmail from OrderMealsReport o Inner Join StudentUser_v2 s on "
				+ "o.studentRecId = s.userId Inner Join ParentUser_v2 p on s.parentuser_userId = p.userId where "
				+ "o.mealSchoolId = :mealSchoolId and o.paymentStatus = 0  and s.schoolYear = :schoolYear ";
		if(parentsNotiReq.getSchoolId() != null){
			if(parentsNotiReq.getGradeName() != null){
				query = query+"and o.grade = :grade ";
			}else if(parentsNotiReq.getStudentIds() != null && parentsNotiReq.getStudentIds().size() > 0){
				query = query+"and s.studentId IN (:studentIds) ";
			}
			query = query+" group by p.userName, p.parentAltEmail";
			Query qry = entityManager.createNativeQuery(query).setParameter("mealSchoolId", parentsNotiReq.getSchoolId())
					.setParameter("schoolYear", parentsNotiReq.getSchoolYear());
			if(parentsNotiReq.getGradeName() != null){
				qry.setParameter("grade", parentsNotiReq.getGradeName().toString());
			}else if(parentsNotiReq.getStudentIds() != null && parentsNotiReq.getStudentIds().size() > 0){
				qry.setParameter("studentIds", parentsNotiReq.getStudentIds());
			}
			objArray = qry.getResultList();
		}
		return objArray;
	}

	/**This method used for send the reminder to parent regarding lunch order of Kids**/
	@Override
	public ServiceResponse sendLunchReminderToParent(ParentsNotificationRequest parentsNotificationRequest, 
			Map<String, List<StudentDetailSendNotif>> stdDetailsMap) throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();
		UsersAuthInfo usersAuthInfo = null;
		/**This map having key as the parent email and value as the parent account access link**/
		Map<String, String> parentDetailsMap = new HashMap<String, String>();
		/**This map having key as the parent email and value as the list of student details (i.e. student name, grade, cutOffDateTime, student id)**/
		Map<String, List<StudentDetailSendNotif>> stdDetailsMapFinal = new HashMap<String, List<StudentDetailSendNotif>>();
		MealReminderRequest mealReminderRequest = new MealReminderRequest();
		MealSchool mealSchool = null;
		String schoolName = "";
		if(parentsNotificationRequest.getSchoolId() != null)
			mealSchool = mealSchoolRepository.findBySchoolId(parentsNotificationRequest.getSchoolId());	
		schoolName = mealSchool.getSchoolName().toUpperCase();
		mealReminderRequest.setSchoolName(schoolName);
		mealReminderRequest.setYearMonth(parentsNotificationRequest.getYearMonth());
		mealReminderRequest.setAdminEmail(mealSchool.getContactPEmail() != null ? mealSchool.getContactPEmail() : "");
		int i = 1;
		for(Entry<String, List<StudentDetailSendNotif>> stdDetailsEntry : stdDetailsMap.entrySet()){
			usersAuthInfo = usersAuthInfoRepository.findByUsername(stdDetailsEntry.getKey());
			if(usersAuthInfo.getfToken() != null && !usersAuthInfo.getfToken().trim().equalsIgnoreCase("") && 
					usersAuthInfo.getEmailIsSubscribe() != null && usersAuthInfo.getEmailIsSubscribe() &&
					usersAuthInfo.getLunchReminderEnable() != null && usersAuthInfo.getLunchReminderEnable()){
				parentDetailsMap.put(stdDetailsEntry.getKey(), parentUserActivationLink(stdDetailsEntry.getKey(), 
						usersAuthInfo.getfToken()));
				stdDetailsMapFinal.put(stdDetailsEntry.getKey(), stdDetailsEntry.getValue());
				if(i == 50){
					mealReminderRequest.setEmailStudentDetailsMap(stdDetailsMapFinal);
					mealReminderRequest.setEmailLinkMap(parentDetailsMap);
					sendNotificationUtil.lunchReminder(mealReminderRequest);
					parentDetailsMap = new HashMap<String, String>();
					stdDetailsMapFinal = new HashMap<String, List<StudentDetailSendNotif>>();
					i = 0;
				}
				i++;
			}
		}
		if(stdDetailsMapFinal.size() > 0){
			mealReminderRequest.setEmailStudentDetailsMap(stdDetailsMapFinal);
			mealReminderRequest.setEmailLinkMap(parentDetailsMap);
			sendNotificationUtil.lunchReminder(mealReminderRequest);
		}		
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("The API for send the reminder to parent regarding lunch order has been executed successfully.");
		return serviceResponse;
	}
	
	/**This method used for get all the student details who havn't ordered lunch for the specified month**/
	@Override
	public List<Object[]> getNotOrderedLunchStudents(ParentsNotificationRequest parentsNotiReq, List<String> grades) {
		List<Object[]> studentDetails= null;
		StringBuilder sb = new StringBuilder();
		sb.append("Select s.firstName, s.lastName, s.studentId, s.gradeName, p.userName, p.parentAltEmail from StudentUser_v2 s "
				+ "Inner Join ParentUser_v2 p on s.parentuser_userId = p.userId where s.mealSchool_schoolId = :mealSchoolId and "
				+ "s.isActive = 1 and s.schoolYear = :schoolYear and NOT EXISTS (select null from OrderMealsReport "
				+ "m where m.mealSchoolId = :schoolId and m.studentRecId = s.userId and m.yearMonth = :selectedMonth)");
		if(grades != null)
			sb.append(" and s.gradeName IN (:grades)");
		if(parentsNotiReq.getStudentIds() != null && parentsNotiReq.getStudentIds().size() > 0)
			sb.append(" and s.studentId IN (:studentIds)");
				
		Query query = entityManager.createNativeQuery(sb.toString()).setParameter("mealSchoolId", parentsNotiReq.getSchoolId())
				.setParameter("schoolId", parentsNotiReq.getSchoolId()).setParameter("schoolYear", parentsNotiReq.getSchoolYear())
						.setParameter("selectedMonth", parentsNotiReq.getYearMonth());
		if(grades != null)
			query.setParameter("grades", grades);
		if(parentsNotiReq.getStudentIds() != null && parentsNotiReq.getStudentIds().size() > 0)
			query.setParameter("studentIds", parentsNotiReq.getStudentIds());
		
		studentDetails = query.getResultList();		
		return studentDetails;
	}

	/**This method used for get the cut-off-date time for the school grade**/
	@Override
	public List<Object[]> getCutOffDateTimeGrade(ParentsNotificationRequest parentsNotificationRequest) {
		List<Object[]> cutOffDateTimeGrade = null;
		cutOffDateTimeGrade = entityManager.createNativeQuery("select ss.cutOffDateTime, sg.grades_name from SchoolMeals_v2 s Inner Join schoolMeal_grades sg on "+
				"s.schoolId = sg.schoolmeal_Id Inner join SchoolMealsSummary_v2 ss on s.schoolMealSummary_schoolId = ss.schoolId where ss.mealSchool_schoolId = :mealSchoolId"+
				" and ss.yearMonth = :selectedMonth group by ss.cutOffDateTime, sg.grades_name").setParameter("mealSchoolId", parentsNotificationRequest.getSchoolId()).
				setParameter("selectedMonth", parentsNotificationRequest.getYearMonth()).getResultList();
		return cutOffDateTimeGrade;
	}
	
	/**This method used for get the cut-off-date time for the school grade**/
	@Override
	public List<Object[]> getCutOffDateTimeGradeV2(ParentsNotificationRequest parentsNotificationRequest) {
		List<Object[]> cutOffDateTimeGrade = null;
		cutOffDateTimeGrade = entityManager.createNativeQuery("select s.cutOffDateTime, sg.grades_name from meal_calendar_summary s Inner Join meal_summary_grades sg on "+
				"s.id = sg.meal_calendar_summary_id where s.mealSchool_schoolId = :mealSchoolId and s.yearMonth = :selectedMonth group by s.cutOffDateTime, sg.grades_name")
				.setParameter("mealSchoolId", parentsNotificationRequest.getSchoolId()).setParameter("selectedMonth", parentsNotificationRequest.getYearMonth()).getResultList();
		return cutOffDateTimeGrade;
	}

	/**This method used for get the meal school id and year month for whom we have send lunch reminder**/
	@Override
	public List<Object[]> getSchoolIdAndMonthForReminder(String dt) {
		List<Object[]> objArray = null;
		objArray = entityManager.createNativeQuery("Select s.mealSchool_schoolId, s.yearMonth from SchoolMealsSummary_v2 s where "
				+ "Date(s.autoReminderDate1) = :dt or Date(s.autoReminderDate2) = :dt group by s.mealSchool_schoolId, s.yearMonth")
				.setParameter("dt", dt).getResultList();
		return objArray;
	}
	
	/**This method used for get the meal school id and year month for whom we have send lunch reminder**/
	@Override
	public List<Object[]> getSchoolIdAndMonthForReminderV2(String dt) {
		List<Object[]> objArray = null;
		objArray = entityManager.createNativeQuery("Select s.mealSchool_schoolId, s.yearMonth from meal_calendar_summary s where "
				+ "Date(s.autoReminderDate1) = :dt or Date(s.autoReminderDate2) = :dt group by s.mealSchool_schoolId, s.yearMonth")
				.setParameter("dt", dt).getResultList();
		return objArray;
	}

	/**This method used for update the status regarding email send to parent**/
	@Override
	public ServiceResponse updateEmailStatus(String email, Boolean paymentReminderEnable, Boolean lunchReminderEnable,
			Boolean emailIsSubscribe) {
		ServiceResponse serviceResponse = new ServiceResponse();
		UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(email, "ROLE_PARENT");
		if(usersAuthInfo != null){
			if(paymentReminderEnable != null)
				usersAuthInfo.setPaymentReminderEnable(paymentReminderEnable);
			if(lunchReminderEnable != null)
				usersAuthInfo.setLunchReminderEnable(lunchReminderEnable);
			if(emailIsSubscribe != null)
				usersAuthInfo.setEmailIsSubscribe(emailIsSubscribe);
			usersAuthInfoRepository.save(usersAuthInfo);
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("Email notification enable/disable updated successfully.");
		}else{
			serviceResponse.setStatusCode(404);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusMessage("No entry found to update.");
		}
		return serviceResponse;
	}
	
	/**This method used for cancel the menu order on specific date**/
	@Override
	public ServiceResponse menuOrderCancel(Long mealSchoolId, List<MealOrderDetails> mealOrderDetailsList) {
		ServiceResponse serviceResponse = new ServiceResponse();
		for(MealOrderDetails mealOrderDetails : mealOrderDetailsList){
			mealOrderDetailsRepository.save(mealOrderDetails);
		}
		serviceResponse.setStatusCode(200);
		logger.info("Save operation completed in DAO for the menu order cancellation");
		return serviceResponse;
	}
	
	@Override
	public ServiceResponse freeReducedLunchEligUpdate(FreeReducedLunchEligReq freeReducedLunchEligReq) {
		ServiceResponse serviceResponse = new ServiceResponse();
		String query = "Update StudentUser_v2 Set";
		if(freeReducedLunchEligReq.getIsFreeLunch() != null){
			if(freeReducedLunchEligReq.getIsFreeLunch())
				query = query + " isFreeMealEligible = true, isReducePriceEligible = false";
			else
				query = query + " isReducePriceEligible = true, isFreeMealEligible = false";
		}else
				query = query + " isBeforeCare="+freeReducedLunchEligReq.getIsBeforeCare();	
		query = query + " where schoolYear = :schoolYear and mealSchool_schoolId = :mealSchoolId";
		if(freeReducedLunchEligReq.getGradeName() != null)
			query = query + " and gradeName = :grade";
		else if(freeReducedLunchEligReq.getStudentIds() != null && freeReducedLunchEligReq.getStudentIds().size() > 0)
			query = query + " and studentId IN (:studentIds)";		

		Query queryGen = entityManager.createNativeQuery(query).setParameter("schoolYear", freeReducedLunchEligReq.getSchoolYear())
				.setParameter("mealSchoolId", freeReducedLunchEligReq.getSchoolId());
		if(freeReducedLunchEligReq.getGradeName() != null)
			queryGen.setParameter("grade", freeReducedLunchEligReq.getGradeName().toString());
		else if(freeReducedLunchEligReq.getStudentIds() != null && freeReducedLunchEligReq.getStudentIds().size() > 0)
			queryGen.setParameter("studentIds", freeReducedLunchEligReq.getStudentIds());
		queryGen.executeUpdate();
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("Updated student eligibilty successfully.");
		return serviceResponse;
		
	}

	/*@Override
	public ServiceResponse demoRequest(String firstName, String lastName, String schoolName, String emailAddress) {
		// TODO Auto-generated method stub
		int status = entityManager.createNativeQuery("insert into DemoRequest (firstName, lastName, schoolName, emailAddress)"
				+ " values (:firstName, :lastName, :schoolName, :emailAddress)")
		.setParameter("firstName", firstName).setParameter("lastName", lastName).setParameter("schoolName", schoolName).setParameter("emailAddress", emailAddress).executeUpdate();
		if(status > 0) {
			logger.info("API dao exeuted for insert demo request feilds to database");
		}else {logger.info("API dao counld'nt insert demo requests fields to database");}
		return null;
	}*/
	
	private List<UserActivationNotification> getAdminUserForAccActivation(SchoolUser schoolUser, String loggedUser, String subDomain, 
			List<UserActivationNotification> adminInfoList, Boolean isByUserName, Long userId){
		UserActivationNotification adminInfo = new UserActivationNotification();
		Boolean sendSatus = false;
		adminInfo.setEmail(schoolUser.getUsername());
		UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(adminInfo.getEmail(), "ROLE_ADMIN");
		if(usersAuthInfo == null){
			usersAuthInfo = new UsersAuthInfo();
			usersAuthInfo.setUsername(adminInfo.getEmail());
			usersAuthInfo.setRole("ROLE_ADMIN");
			usersAuthInfo.setfToken(UUID.randomUUID().toString());
			usersAuthInfo.setfTokenTime(new Date());
			usersAuthInfo.setCreatedBy(loggedUser.toString());
			usersAuthInfo.setCreatedOn(new Date());
			usersAuthInfo.setMobile(schoolUser.getMobileNo());
			entityManager.persist(usersAuthInfo);
			sendSatus = true;
		//}else if(usersAuthInfo.getfToken() == null || !schoolUser.getIsVerified()){
		}else if(!schoolUser.getIsVerified()){
			if(isByUserName){
				usersAuthInfo.setfToken(UUID.randomUUID().toString());
				usersAuthInfo.setfTokenTime(new Date());
				usersAuthInfo.setModifiedBy(loggedUser.toString());
				usersAuthInfo.setModifiedOn(new Date());
				entityManager.merge(usersAuthInfo);
				sendSatus = true;
			}else if(usersAuthInfo.getfToken() == null || usersAuthInfo.getfToken().isEmpty()){
				usersAuthInfo.setfToken(UUID.randomUUID().toString());
				usersAuthInfo.setfTokenTime(new Date());
				usersAuthInfo.setModifiedBy(loggedUser.toString());
				usersAuthInfo.setModifiedOn(new Date());
				entityManager.merge(usersAuthInfo);
				sendSatus = true;
			}			
		}
		if(sendSatus){
			adminInfo.setToken(buildResetPasswordLink(userId, usersAuthInfo.getfToken(), subDomain, null));
			adminInfoList.add(adminInfo);
		}
		return adminInfoList;
	}

	/**This method used for submit the household application
	 * @throws Exception **/
	@Override
	public ServiceResponse householdApplication(HouseholdApplicationForFRM householdApplication) throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();
		//entityManager.persist(householdApplication);
		householdAppForFRMRepository.save(householdApplication);
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("Household application has been submitted successfully.");
		return serviceResponse;
	}
	
	/**This method used for get all the student record id list for whom application already submitted**/
	@Override
	public List<Object> getApplicationSubmittedStudents(String parentEmail) {
		List<Object> studentRecIds = new ArrayList<Object>();
		Query query = entityManager.createNativeQuery("Select houseApp.studentRecId from FRM_HouseholdApplication houseApp "
				+ "inner join StudentUser_v2 su on houseApp.studentRecId = su.userId inner join ParentUser_v2 pu on "
				+ "su.parentuser_userId = pu.userId where pu.userName = :parentEmail OR pu.parentAltEmail = :parentEmail")
				.setParameter("parentEmail", parentEmail);
		studentRecIds = query.getResultList();
		return studentRecIds;
	}

	/**This method used for get the on-boarded schools info**/
	@Override
	public List<Object[]> getOnboardedSchools(String currentDate) {
		List<Object[]> resp = entityManager.createNativeQuery("Select ms.schoolId, ms.schoolName, sy.schoolYear, sy.schoolPdfUrl from "
				+ "MealSchool_v2 ms inner join MealSchool_SchoolYear sy on ms.schoolId = sy.mealSchool_schoolId and "
				+ ":date between sy.sessionStartDateTime and sy.sessionEndDateTime where ms.isActive = 1")
				.setParameter("date", currentDate).getResultList();
		return resp;
	}

	/**This method used to get the students info for website household app**/
	@Override
	public List<Object[]> getWebsiteStudents(Long mealSchoolId, Integer schoolYear, String parentEmail, Boolean isSupport) {
		List<Object[]> resp = null;
		String studentIds = "";
		if(isSupport == null || !isSupport){
			List<Integer> pendingAppStudentIds = entityManager.createNativeQuery("select GROUP_CONCAT(houseApp.studentRecIds)"
					+ " from FRM_HouseholdApplication houseApp where houseApp.status = 'pending' and houseApp.mealSchoolId = :mealSchoolId "
					+ "and houseApp.schoolYear = :schoolYear").setParameter("mealSchoolId", mealSchoolId)
					.setParameter("schoolYear", schoolYear).getResultList();
			if(pendingAppStudentIds != null && pendingAppStudentIds.size() > 0)
				studentIds = pendingAppStudentIds.toString().replace("[", "").replace("]", "").replace("null", "0");
		}
		String query = "Select su.userId, su.firstName, su.lastName, su.gradeName, su.studentId from StudentUser_v2 su inner join ParentUser_v2 pu on su.parentuser_userId=pu.userId"
				+ " where su.mealSchool_schoolId = :mealSchoolId and su.schoolYear = :schoolYear and su.isActive=1 and su.isRegister =1 ";
		if(isSupport == null || !isSupport)
			query = query+" and su.userId not IN ("+studentIds+")";
		if(parentEmail != null && !parentEmail.trim().isEmpty())
			query = query+" and (pu.userName=:parentEmail or pu.parentAltEmail=:parentEmail)";
		Query finalQry = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("schoolYear", schoolYear);
		if(parentEmail != null && !parentEmail.trim().isEmpty())
			finalQry.setParameter("parentEmail", parentEmail);
		resp = finalQry.getResultList();		
		return resp;
	}

	/**This method used to make the school admin user as primary**/
	@Override
	public ServiceResponse makeAdminPrimary(String userName) {
		ServiceResponse serviceResponse = new ServiceResponse();
		MealSchool mealSchool = mealSchoolRepository.findBySchoolUsersUsername(userName);
		if(mealSchool == null || mealSchool.getSchoolUsers() == null || mealSchool.getSchoolUsers().size() < 1){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(417);
			serviceResponse.setStatusMessage("School Admin user not found.");
			return serviceResponse;
		}
		Set<SchoolUser> schoolUsersFinal = new HashSet<SchoolUser>();
		for(SchoolUser schoolUser : mealSchool.getSchoolUsers()){
			if(schoolUser.getUsername().equalsIgnoreCase(userName))
				schoolUser.setIsPrimaryUser(true);
			/*else if(schoolUser.getIsPrimaryUser() != null && schoolUser.getIsPrimaryUser())
				schoolUser.setIsPrimaryUser(false);*/
			schoolUsersFinal.add(schoolUser);
		}
		mealSchool.setSchoolUsers(schoolUsersFinal);
		if(schoolUsersFinal != null && schoolUsersFinal.size() > 0){
			mealSchoolRepository.save(mealSchool);
		}
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("Admin has been changed as primary user successfully!!");
		return serviceResponse;
	}

	/**This method used for add the module details**/
	@Override
	public ServiceResponse addModuleDetails(List<ModuleDetails> moduleDetails) {
		ServiceResponse serviceResponse = new ServiceResponse();
		for(ModuleDetails moduleDetail : moduleDetails){
			entityManager.merge(moduleDetail);
		}
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("Added/Updated module details successfully");
		return serviceResponse;
	}

	/**This method used for get the module details by type**/
	@Override
	public List<Object[]> getModulesByType(String userType) {
		List<Object[]> objArrayList = null;
		if(userType == null)
			userType = "";
		objArrayList = entityManager.createNativeQuery("Select md.moduleId,md.module,md.subModule,md.pageName,md.moduleIcon,"
				+ "md.access,mtm.recId,mtm.userType,mtm.access as mtmAccess from ModuleDetails md left join ModuleTypeMapping mtm "
				+ "on md.moduleId=mtm.moduleId and mtm.userType=:userType").setParameter("userType", userType).getResultList();
		return objArrayList;
	}

	/**This method used for add the module type details**/
	@Override
	public ServiceResponse addModuleType(Map<String, Map<String, List<ModuleTypeMapping>>> moduleTypeDetails) {
		ServiceResponse serviceResponse = new ServiceResponse();
		for(Entry<String, Map<String, List<ModuleTypeMapping>>> grpEntry : moduleTypeDetails.entrySet()){
			for(Entry<String, List<ModuleTypeMapping>> subgrpEntry : grpEntry.getValue().entrySet()){
				for(ModuleTypeMapping moduleTypeMapping : subgrpEntry.getValue()){
					entityManager.merge(moduleTypeMapping);
				}
			}
		}
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("Module type details added successfully.");
		return serviceResponse;
	}
	
	/**This method used for add/update the student eligibility audit data**/
	@SuppressWarnings("deprecation")
	public void addUpdateStudentEligibility(Integer currentEligStatus, Integer previousEligStatus, Boolean isUpdate, 
			Date startdate, Date schoolYearEndDate, String note, StudentUser studentUser){
		String loggedUser = "";
		Date startDt = new Date();
		if(SecurityContextHolder.getContext() != null 
				&& SecurityContextHolder.getContext().getAuthentication() != null)
			loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if(isUpdate != null && isUpdate){
			StudentEligibilityAudit studentEligibilityAudit = eligibilityAuditRepo
					.findByStudentUserUserIdAndEffectiveEndDateAfter(studentUser.getUserId(), new Date());
			if(studentEligibilityAudit != null){
				startDt.setSeconds(0);
				studentEligibilityAudit.setEffectiveEndDate(startDt);
				studentEligibilityAudit.setLastModifiedBy(loggedUser);
				entityManager.merge(studentEligibilityAudit);
				startDt.setSeconds(5);
			}
		}
		StudentEligibilityAudit studentEligibilityAudit = new StudentEligibilityAudit();
		studentEligibilityAudit.setCurrentEligStatus(currentEligStatus);
		studentEligibilityAudit.setPreviousEligStatus(previousEligStatus != null ? previousEligStatus : currentEligStatus);
		studentEligibilityAudit.setEffectiveStartDate(startdate != null ? startdate : startDt);
		studentEligibilityAudit.setEffectiveEndDate(schoolYearEndDate);
		studentEligibilityAudit.setNote(note);
		studentEligibilityAudit.setSchoolYear(studentUser.getSchoolYear());
		studentEligibilityAudit.setStudentUser(studentUser);
		studentEligibilityAudit.setLastModifiedBy(loggedUser);
		studentEligibilityAudit.setDecisionReason(studentUser.getDecisionReason());
		studentEligibilityAudit.setCategory(studentUser.getCategory());
		entityManager.persist(studentEligibilityAudit);
	}
	
	/**This method used for get the eligibility status**/
	public Integer getEligStatus(Boolean isFreeMeal, Boolean isReducedPriceMeal){
		Integer eligStatus = 2;
		if(isFreeMeal != null && isFreeMeal)
			eligStatus = 0;
		else if(isReducedPriceMeal != null && isReducedPriceMeal)
			eligStatus = 1;
		return eligStatus;
	}

	@SuppressWarnings("deprecation")
	@Override
	/**This method used for audit the student status**/
	public void auditStudentStatus(Boolean currentStatus, Boolean previousStatus, Boolean isUpdate, 
			Date startdate, Date schoolYearEndDate, String note, StudentUser studentUser) {
		String loggedUser = "";
		Date startDt = new Date();
		if(SecurityContextHolder.getContext() != null 
				&& SecurityContextHolder.getContext().getAuthentication() != null)
			loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if(isUpdate != null && isUpdate){
			StudentStatusAudit studentStatusAudit = studentStatusAuditRepo
					.findByStudentUserUserIdAndEffectiveEndDateAfter(studentUser.getUserId(), new Date());
			if(studentStatusAudit != null){
				startDt.setSeconds(0);
				studentStatusAudit.setEffectiveEndDate(startDt);
				studentStatusAudit.setLastModifiedBy(loggedUser);
				entityManager.merge(studentStatusAudit);
				startDt.setSeconds(5);
			}
		}
		StudentStatusAudit studentStatusAudit = new StudentStatusAudit();
		studentStatusAudit.setCurrentStatus(currentStatus);
		studentStatusAudit.setPreviousStatus(previousStatus != null ? previousStatus : currentStatus);
		studentStatusAudit.setEffectiveStartDate(startdate != null ? startdate : startDt);
		studentStatusAudit.setEffectiveEndDate(schoolYearEndDate);
		studentStatusAudit.setNote(note);
		studentStatusAudit.setSchoolYear(studentUser.getSchoolYear());
		studentStatusAudit.setStudentUser(studentUser);
		studentStatusAudit.setLastModifiedBy(loggedUser);
		entityManager.merge(studentStatusAudit);
	}

	@Override
	public void addModules(List<ModuleInfo> moduleInfos) {
		for(ModuleInfo moduleInfo : moduleInfos){
			entityManager.merge(moduleInfo);
		}
	}

	@Override
	public void tierWithModule(TierInfo tierInfo) {
		entityManager.merge(tierInfo);
	}
	
	/**This method used for add the instant payment transaction
	 * @throws Exception **/
	@Override
	public Long addInstantPayTrx(String loggedUser, MealSchool mealSchool, StudentMealOrdersV2 stdOrder, Double refundAmt, ItemTypeConstants menuType) throws Exception {
		Double walletAmt = stdOrder.getWalletAmt();
		Double addAmtInWallet = (double)0;
		MasterTransactionsAudit mta = null;
		if(refundAmt != null && refundAmt > 0){
			buildTrxData(refundAmt, stdOrder, loggedUser, mealSchool, true, stdOrder.getPaymentType(), null, null);
			logger.info("Amount refunded for studentId::"+stdOrder.getStudentId()+" while order modified");
		}else{
			Double instantPayAmt = stdOrder.getInstantPayAmt();
			String chargeId = null;
			String transferId = null;
			if(instantPayAmt != null && instantPayAmt > 0 && stdOrder.getPaymentType().toString().equalsIgnoreCase("Online")){
				if(stdOrder.getPaymentType().toString().equalsIgnoreCase("Online") && (mealSchool.getIsPaymentEnabled() == null 
						|| !mealSchool.getIsPaymentEnabled() || mealSchool.getStripeAccountId() == null))
					throw new Exception("School not accepting online payment. Please contact to the School Admin.");
				/*Stripe.apiKey = stripeSecretKey;
				Map<String, Object> chargeParams = new HashMap<String, Object>();
				chargeParams.put("amount", (int) Math.round((stdOrder.getInstantPayAmt()+
						stdOrder.getAppFeeAmount()+stdOrder.getTransactionFees())*100));
				chargeParams.put("currency", CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())));
				chargeParams.put("destination", mealSchool.getStripeAccountId());
				chargeParams.put("source", stdOrder.getTransactionToken());
				chargeParams.put("application_fee_amount", (int) Math.round((stdOrder.getAppFeeAmount()+stdOrder.getTransactionFees())*100));*/
				/*Map<String, Object> chargeParams = stripeUtil.prepareStripeDestinationCharge(stdOrder.getInstantPayAmt(), stdOrder.getAppFeeAmount(), stdOrder.getTransactionFees(),
						CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())), 
						mealSchool.getStripeAccountId(), stdOrder.getTransactionToken());*/
				Charge charge = stripeUtil.prepareStripeDirectCharge(stdOrder.getInstantPayAmt(), stdOrder.getAppFeeAmount(), stdOrder.getTransactionFees(),
						CommonUtil.getCurrCode(countryDetailsRepository.getCurrencyCode(mealSchool.getCountryCode())), 
						mealSchool.getStripeAccountId(), stdOrder.getTransactionToken(),mealSchool.isTrxFeeOnSchool());
				chargeId = charge.getId();
				transferId = charge.getBalanceTransaction();
			}
			if(walletAmt != null && walletAmt > 0){
				mta = buildTrxData(walletAmt, stdOrder, loggedUser, mealSchool, false, PaymentType.Wallet, null, null);
				logger.info("Wallet instant payment trx added for studentId::"+stdOrder.getStudentId());
			}
			if(instantPayAmt != null && instantPayAmt > 0){
				addAmtInWallet = instantPayAmt - (stdOrder.getFinalOrderAmt() > 0 ? stdOrder.getFinalOrderAmt() : 0);
				if(addAmtInWallet > 0)
					instantPayAmt = instantPayAmt - addAmtInWallet;
				if(instantPayAmt > 0)
					mta = buildTrxData(instantPayAmt, stdOrder, loggedUser, mealSchool, false, stdOrder.getPaymentType(), chargeId, transferId);
				else
					mta = buildAddPayTrxData(addAmtInWallet, stdOrder, loggedUser, mealSchool, null, chargeId, transferId, menuType);
				logger.info("Instant payment trx added for studentId::"+stdOrder.getStudentId());
				
				if(addAmtInWallet > 0 && instantPayAmt > 0)
					buildAddPayTrxData(addAmtInWallet, stdOrder, loggedUser, mealSchool, mta.getRecId(), chargeId, transferId,menuType);
		}
		}
		return (mta != null && mta.getRecId() != null) ? mta.getRecId() : null;
	}
	
	/**This method used for build the add payment transaction object for negative student balance**/
	private MasterTransactionsAudit buildAddPayTrxData(Double payAmt, StudentMealOrdersV2 stdOrder, String loggedUser, MealSchool mealSchool, Long trxId, String chargeId, String transferId, ItemTypeConstants menuType) throws Exception{
		MasterTransactionsAudit trxAudit = new MasterTransactionsAudit();
		trxAudit.setLoggedUser(loggedUser);
		trxAudit.setMealSchool(mealSchool);
		payAmt = Double.valueOf(new DecimalFormat("##.00").format(payAmt));
		trxAudit.setNote("Amount paid through "+stdOrder.getPaymentType().toString());
		trxAudit.setTransactionDescription("Added A/c Balance along with order instant payment trxId::"+trxId);
		trxAudit.setTransactionType(TransactionType.Deposit);
		trxAudit.setPaymentType(stdOrder.getPaymentType());
		trxAudit.setCheckNumb(stdOrder.getCheckNum());
		trxAudit.setChargeId(chargeId);
		trxAudit.setTransferId(transferId);
		trxAudit.setTransactionDateTime(new Date());
		trxAudit.setCreatedBy(loggedUser);
		trxAudit.setCreatedOn(new Date());	
		trxAudit.setPurchaseItemType(PurchaseItemType.Lunch);
		//trxAudit.setPurchaseItemType(menuType != null ? PurchaseItemType.valueOf(menuType.toString()) : null);
		trxAudit.setTotalTransactionAmount(payAmt);
		Set<StudentWiseTransaction> studentWiseTransactions = new HashSet<StudentWiseTransaction>();
		StudentUser studentUser = studentUserRepository.findByUserIdAndIsActive(stdOrder.getStudentId(), true);
		StudentWiseTransaction swt = new StudentWiseTransaction();
		if(studentUser == null || studentUser.getUserId() == null)
			throw new Exception("Student does not exist with id: "+stdOrder.getStudentId());
		swt.setStudentUser(studentUser);
		swt.setStudentFName(studentUser.getFirstName());
		swt.setMealType("Regular");
		swt.setStudentLName(studentUser.getLastName());
		swt.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance()+payAmt)));
		swt.setGrade(studentUser.getGradeName());
		swt.setChargedAmt(payAmt);
		Integer eligStatus = 2;
		if(studentUser.getIsFreeMealEligible() != null && studentUser.getIsFreeMealEligible())
			eligStatus = 0;
		else if(studentUser.getIsReducePriceEligible() != null && studentUser.getIsReducePriceEligible())
			eligStatus =1;
		swt.setEligStatus(eligStatus);
		studentUser.setModifiedBy(trxAudit.getCreatedBy());
		studentUser.setModifiedOn(new Date());
		studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", swt.getFinalBalance())));
		entityManager.merge(studentUser);
		swt.setTransactionAmount(payAmt);
		studentWiseTransactions.add(swt);
		trxAudit.setStudentWiseTransactions(studentWiseTransactions);
		entityManager.persist(trxAudit);
		return trxAudit;
	}

	/**This method used for build the transaction object**/
	private MasterTransactionsAudit buildTrxData(Double amt, StudentMealOrdersV2 stdOrder, String loggedUser, MealSchool mealSchool, Boolean isRefund, PaymentType paymentType, String chargeId, String transferId) throws Exception{
		MasterTransactionsAudit trxAudit = new MasterTransactionsAudit();
		trxAudit.setLoggedUser(loggedUser);
		Double amtToAddAcc = (double) 0;
		trxAudit.setMealSchool(mealSchool);
		if(isRefund){
			trxAudit.setNote("Amount refunded while modified order.");
			trxAudit.setTransactionDescription("Amount refunded while modified order.");
			trxAudit.setTransactionType(TransactionType.Refund);
			trxAudit.setPaymentType(PaymentType.AdjustmentCR);
		}else{
			if(paymentType.toString().equalsIgnoreCase(PaymentType.Wallet.toString())){
				trxAudit.setNote("Account balance redeemed for order.");
				trxAudit.setTransactionDescription("Account balance redeemed for order.");
			}else{
				trxAudit.setNote("Paid amount for order.");
				amtToAddAcc = amt - (stdOrder.getFinalOrderAmt() >= 0 ? stdOrder.getFinalOrderAmt() : (double)0);
				trxAudit.setTransactionDescription("Paid amount for order.");
				if(amtToAddAcc > 0)
					trxAudit.setTransactionDescription("Along with order instant payment, paid "+amtToAddAcc+" in kid's Wallet acccount.");
			}
			trxAudit.setTransactionType(TransactionType.InstantPayment);
			trxAudit.setPaymentType(paymentType);
			if(!paymentType.toString().equalsIgnoreCase(PaymentType.Wallet.toString()))
				trxAudit.setCheckNumb(stdOrder.getCheckNum());
		}
		trxAudit.setTransactionDateTime(new Date());
		trxAudit.setCreatedBy(loggedUser);
		trxAudit.setCreatedOn(new Date());		
		trxAudit.setTotalTransactionAmount(amt);
		Set<StudentWiseTransaction> studentWiseTransactions = new HashSet<StudentWiseTransaction>();
		StudentUser studentUser = studentUserRepository.findByUserIdAndIsActive(stdOrder.getStudentId(), true);
		StudentWiseTransaction swt = new StudentWiseTransaction();
		if(studentUser == null || studentUser.getUserId() == null)
			throw new Exception("Student does not exist with id: "+stdOrder.getStudentId());
		swt.setStudentUser(studentUser);
		swt.setStudentFName(studentUser.getFirstName());
		swt.setStudentLName(studentUser.getLastName());
		swt.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance())));
		swt.setGrade(studentUser.getGradeName());
		if(trxAudit.getPaymentType().toString().equalsIgnoreCase("Wallet")){
			swt.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance()-amt)));
			studentUser.setModifiedBy(trxAudit.getCreatedBy());
			studentUser.setModifiedOn(new Date());
			studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", swt.getFinalBalance())));
			entityManager.merge(studentUser);
		}
		if(isRefund || amtToAddAcc > 0){
			swt.setFinalBalance(Double.parseDouble(String.format("%.2f", studentUser.getAccBalance()+ (isRefund ? amt : amtToAddAcc))));
			studentUser.setModifiedBy(trxAudit.getCreatedBy());
			studentUser.setModifiedOn(new Date());
			studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", swt.getFinalBalance())));
			entityManager.merge(studentUser);
		}
		swt.setTransactionAmount(amt);
		studentWiseTransactions.add(swt);
		trxAudit.setStudentWiseTransactions(studentWiseTransactions);
		trxAudit.setChargeId(chargeId);
		trxAudit.setTransferId(transferId);
		entityManager.persist(trxAudit);
		return trxAudit;
	}
	
	@Override
	/**This method used for get the grade display value**/
	@Cacheable(cacheNames = "gradeMapByCountry",key = "{#countryCode}")
	public Map<String, String> gradeMapByCountry(String countryCode) {
		logger.info("Getting grades map for country:: "+countryCode);
		CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(countryCode);
		Map<String, String> gradeKeyVal = new LinkedMap();
		List<GradesInfo> gradeList = countryDetail.getGradesMap();
		gradeList.sort(Comparator.comparingInt(GradesInfo::getDisplayOrder));
		for (GradesInfo gradesInfo : gradeList) {
			gradeKeyVal.put(gradesInfo.getValue().toString(), gradesInfo.getLabel());
		}
		return gradeKeyVal;
	}

	@Override
	/**This method used for get the grade display value**/
	@Cacheable(cacheNames = "gradeBackMapByCountry",key = "{#countryCode}")
	public Map<String, String> gradeBackMapByCountry(String countryCode) {
		logger.info("Getting grades Back map for country:: "+countryCode);
		CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(countryCode);
		Map<String, String> gradeKeyVal = new HashMap<String, String>();
		for (GradesInfo gradesInfo : countryDetail.getGradesMap()) {
			gradeKeyVal.put(gradesInfo.getLabel(), gradesInfo.getValue().toString());
		}
		gradeKeyVal.put("01", "one");
		gradeKeyVal.put("02", "two");
		gradeKeyVal.put("03", "three");
		gradeKeyVal.put("04", "four");
		gradeKeyVal.put("05", "five");
		gradeKeyVal.put("06", "six");
		gradeKeyVal.put("07", "seven");
		gradeKeyVal.put("08", "eight");
		gradeKeyVal.put("09", "nine");
		gradeKeyVal.put("KF", "k");
		gradeKeyVal.put("3F", "pk");
		gradeKeyVal.put("3H", "pk");
		gradeKeyVal.put("4F", "pk");
		gradeKeyVal.put("4H", "pk");
		return gradeKeyVal;
	}
	
	@Override
	public void catererUpdate(Caterer caterer, Set<CatererUser> catererUsers) {
		for(CatererUser catererUser : catererUsers){
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(catererUser.getUsername(), "ROLE_CATERER");
			if(usersAuthInfo == null){
				usersAuthInfo = new UsersAuthInfo();
				usersAuthInfo.setUsername(catererUser.getUsername());
				usersAuthInfo.setRole("ROLE_CATERER");
				usersAuthInfo.setCreatedBy(caterer.getLoggedUser());
				usersAuthInfo.setCreatedOn(new Date());
				usersAuthInfo.setMobile(catererUser.getMobileNo());
				entityManager.persist(usersAuthInfo);
			}else if(catererUser.getMobileNo() != null && !usersAuthInfo.getMobile().equalsIgnoreCase(catererUser.getMobileNo())){
				usersAuthInfo.setModifiedBy(caterer.getLoggedUser());
				usersAuthInfo.setModifiedOn(new Date());
				usersAuthInfo.setMobile(catererUser.getMobileNo());
				entityManager.merge(usersAuthInfo);
			}
		}
		entityManager.merge(caterer);
	}

	/**This method used for update district info**/
	@Override
	public void districtUpdate(District district, Set<DistrictUser> districtUsers) {
		for(DistrictUser districtUser : districtUsers){
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(districtUser.getUsername(), "ROLE_DISTRICT");
			if(usersAuthInfo == null){
				usersAuthInfo = new UsersAuthInfo();
				usersAuthInfo.setUsername(districtUser.getUsername());
				usersAuthInfo.setRole("ROLE_DISTRICT");
				usersAuthInfo.setCreatedBy(district.getLoggedUser());
				usersAuthInfo.setCreatedOn(new Date());
				usersAuthInfo.setMobile(districtUser.getMobileNo());
				entityManager.persist(usersAuthInfo);
			}else if(districtUser.getMobileNo() != null && !usersAuthInfo.getMobile().equalsIgnoreCase(districtUser.getMobileNo())){
				usersAuthInfo.setModifiedBy(district.getLoggedUser());
				usersAuthInfo.setModifiedOn(new Date());
				usersAuthInfo.setMobile(districtUser.getMobileNo());
				entityManager.merge(usersAuthInfo);
			}
		}
		entityManager.merge(district);
	}

	/**This method used for save the admin user's PIN**/
	@Override
	public ServiceResponse saveAdminPIN(Long schoolId, Long userId, String pin) {
		SchoolUser usr = entityManager.find(SchoolUser.class, userId);
		ServiceResponse serviceResponse = new ServiceResponse();
		if(usr != null){
			usr.setPin(pin);
			entityManager.merge(usr);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("PIN has been generated successfully for Admin user.");
		}else{
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(417);
			serviceResponse.setStatusMessage("User does not exist!!");
		}
		return serviceResponse;
	}

	/**This method used for save student's PIN**/
	@Override
	public void saveStdPin(Map<Long, String> pinByStd) {
		Query qry = entityManager.createNativeQuery("UPDATE StudentUser_v2 SET pin = :pin where userId=:userId and pin is null");
		for(Map.Entry<Long, String> entry : pinByStd.entrySet()){
			qry.setParameter("pin", entry.getValue()).setParameter("userId", entry.getKey()).executeUpdate();
		}
	}
	
	/**This method used to get the students info for website household app**/
	
		
	/**This method used for get the last day of month**/
	/*private Date lastDayOfMonth(Date monthStartDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(monthStartDate);
		cal.set(Calendar.DAY_OF_MONTH,
		cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}*/

}
