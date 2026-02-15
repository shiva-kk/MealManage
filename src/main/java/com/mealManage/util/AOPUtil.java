package com.mealManage.util;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.dao.ReportsDao;
import com.mealManage.domain.FreeMealEligSurveyEmailReq;
import com.mealManage.domain.HouseholdAppOtherInfo;
import com.mealManage.domain.SupportUserNotificationReq;
import com.mealManage.domain.SurveyRequest;
import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.caterer.CatererUser;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.meal.MenuOrderHistoryAudit;
import com.mealManage.mealmodel.meal.SchoolMeal;
import com.mealManage.mealmodel.repository.DistrictRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.MenuItemRepository;
import com.mealManage.mealmodel.repository.NutritionAuditRepo;
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.DistrictUser;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.user.DemoRequest;
import com.mealManage.mealmodel.user.FMEligibilitySurvey;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;
import com.mealManage.mealmodel.user.SchoolUser;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.mealmodel.user.SupportUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.mealschedule.entities.MealCalendar;
import com.mealManage.menu.entities.MenuItem;
import com.mealManage.menu.entities.NutritionAudit;

@Component
@Transactional
public class AOPUtil {
	
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private DateUtilityV2 du;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private ReportsDao reportsDao;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	@Value("${mm.support.email}")
	private String mmSupportEmail;
	@Value("${mm.contact.email}")
	private String mmContactEmail;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private NoticeBenefitsApp noticeBenefitsApp;
	@Autowired
	private NoticeBenefitsLetterV2 noticeBenefitsLetterV2;
	@Autowired
	private DistrictRepository districtRepository;
	@Autowired
	private AWSUtility awsUtility;
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Autowired
	private NutritionAuditRepo nutritionAuditRepo;
	
	/**Creating school user in userauth during meal school creation**/
	public void schoolUserAuth(MealSchool mealSchool) throws Exception{	
		try{
			for(SchoolUser schoolUser : mealSchool.getSchoolUsers()){
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
				}else if(!usersAuthInfo.getMobile().equalsIgnoreCase(schoolUser.getMobileNo())){
					usersAuthInfo.setModifiedBy(mealSchool.getLoggedUser());
					usersAuthInfo.setModifiedOn(new Date());
					usersAuthInfo.setMobile(schoolUser.getMobileNo());
					entityManager.merge(usersAuthInfo);
				}
			}
		}catch(Exception e){
			logger.error("Error occured during onboard school");
			throw new Exception("Error occured during onboard school");
		}
	}
	
	/**This method used for auto-generate the subdomain during onboard school (i.e. meal school creation)**/
	public MealSchool generateSubdomain(MealSchool mealSchool) throws Exception{
		String schoolName = mealSchool.getSchoolName();
		String subdomain = schoolName.replaceAll("[^a-zA-Z0-9]", "").substring(0, 3);
		Boolean subdomainStatus = false;
		if(subdomain != null)
			subdomainStatus = mealSchoolRepository.validateSubdomain(subdomain);
		if(subdomainStatus)
			mealSchool.setSubdomain(subdomain.toLowerCase());
		else{
			String stateZip = mealSchool.getSchool().getCityStateZip();
			char ch = (char) (Integer.parseInt(stateZip.toString().substring(stateZip.length()-1)) + 65);
			subdomain = subdomain+""+ch;
			subdomainStatus = mealSchoolRepository.validateSubdomain(subdomain);
			if(subdomainStatus)
				mealSchool.setSubdomain(subdomain.toLowerCase());
			else{
				ch = (char) (Integer.parseInt(stateZip.toString().substring(stateZip.length()-2,stateZip.length()-1)) + 65);
				subdomain = subdomain+""+ch;
				subdomainStatus = mealSchoolRepository.validateSubdomain(subdomain);
				if(subdomainStatus)
					mealSchool.setSubdomain(subdomain.toLowerCase());
				else
					throw new Exception("Subdomain already exist for othetr meal school");
			}				
		}
		return mealSchool;
	}
	
	/**This method used for set the school id and send email to the respective admin users
	 * @throws Exception **/
	public FMEligibilitySurvey buildFmSurveyElig(FMEligibilitySurvey fmEligibilitySurvey) throws Exception{
		List<Object[]> schoolIdAndAdminEmails = reportsDao.adminUsersEmail(fmEligibilitySurvey.getParentEmail());
		Set<String> adminEmails = new HashSet<String>();
		Set<Long> mealSchoolIds = new HashSet<Long>();
		Set<MealSchool> mealSchools = new HashSet<MealSchool>();
		for(Object[] obj : schoolIdAndAdminEmails){
			if(obj[0] != null && !obj[0].toString().equalsIgnoreCase(""))
				adminEmails.add(obj[0].toString());
			if(obj[1] != null)
				mealSchoolIds.add(Long.parseLong(obj[1].toString()));
		}
		MealSchool mealSchool = null;
		for(Long mealSchoolId : mealSchoolIds){
			mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
			mealSchools.add(mealSchool);
		}
		fmEligibilitySurvey.setMealSchools(mealSchools);
		fmEligibilitySurvey.setAdminEmails(adminEmails);
		return fmEligibilitySurvey;
	}
	
	/**This method used for send the free meal eligibility email to admin users**/
	public void sendEmailToAdminFM(FMEligibilitySurvey fmEligibilitySurvey) throws Exception{	
		try{
			List<FreeMealEligSurveyEmailReq> notificationInfos = new ArrayList<FreeMealEligSurveyEmailReq>();
			FreeMealEligSurveyEmailReq notificationInfo = null;
			SurveyRequest surveyRequest = new SurveyRequest();
			surveyRequest.setEmail(fmEligibilitySurvey.getParentEmail());
			surveyRequest.setHouseHoldSize(fmEligibilitySurvey.getHouseholdSize());
			surveyRequest.setIncome(fmEligibilitySurvey.getIncome());
			surveyRequest.setIncomeType(fmEligibilitySurvey.getIncomeType());
			
			/*String mailSubject = "Free meal eligibilty check request from Parent User";
			String mailMessage = "There is a request from Parent user regarding free meal eligibility check. "
					+ "Please find the parent user personal information here. Parent email: "+fmEligibilitySurvey.getParentEmail()+
					", Household Size: "+fmEligibilitySurvey.getHouseholdSize()+", Income: $"+fmEligibilitySurvey.getIncome()+
					", Income multiple for Anuually: "+fmEligibilitySurvey.getIncomeType();*/
			if(fmEligibilitySurvey.getAdminEmails() != null)
			for(String adminEmail : fmEligibilitySurvey.getAdminEmails()){
				notificationInfo = new FreeMealEligSurveyEmailReq();
				notificationInfo.setEmail(adminEmail);
				notificationInfo.setSurveyMsg(surveyRequest);
				notificationInfos.add(notificationInfo);
			}
			/*if(notificationInfos != null && notificationInfos.size() > 0){
				Map<String, List<FreeMealEligSurveyEmailReq>> notificationRequest = new HashMap<String, List<FreeMealEligSurveyEmailReq>>();
				notificationRequest.put("users", notificationInfos);		
				sendNotificationUtil.freeMealEligMail(notificationRequest);
			}*/
		}catch(Exception e){
			logger.error("Failed to send the Free meal eligibility survey email");
			throw new Exception("Failed to send the Free meal eligibility survey entry");
		}
	}
	
	/**This method used for send the email to school admin user /MealManage support team regarding user support**/
	public void sendEmailToAdminAndMealManageSupport(SupportUser supportUser) throws Exception{
		Set<String> adminUsersEmail = new HashSet<String>();
		//check support request belong to parent or school and based on that send confirmation email
		Map<String, String> selfNotifReq = new HashMap<String, String>();
		SupportUserNotificationReq supportUserNotificationReq = new SupportUserNotificationReq();
		if(supportUser.getMealSchool() != null && supportUser.getMealSchool().getSchoolId() != null){
			selfNotifReq.put("type", "parent");
			selfNotifReq.put("replyEmail", supportUser.getMealSchool().getContactPEmail() != null ? supportUser.getMealSchool().getContactPEmail() : "");
			selfNotifReq.put("subject", "MealManage support ticket created");
			supportUserNotificationReq.setType("parent");
		}else{
			selfNotifReq.put("type", "school");
			selfNotifReq.put("replyEmail", mmContactEmail);
			selfNotifReq.put("subject", "Thank you for your inquiry on MealManage");
			supportUserNotificationReq.setType("school");
		}	
		selfNotifReq.put("userName", supportUser.getUserName() != null ? supportUser.getUserName() : "");
		selfNotifReq.put("userEmails", supportUser.getUserEmail());
		selfNotifReq.put("requestId", StringUtils.leftPad(supportUser.getSupportReqId().toString(), 5,"0"));
		sendNotificationUtil.selfSupportNotification(selfNotifReq);
		
		/**Get all the admin users if issue related to student profile or student's Lunch order based on school id**/
		if(supportUser.getMealSchool() != null && supportUser.getMealSchool().getSchoolId() != null
				&& supportUser.getMealSchool().getSchoolUsers() != null){
			for(SchoolUser schoolUser : supportUser.getMealSchool().getSchoolUsers()){
				if(schoolUser.getIsActive() && schoolUser.getIsVerified() && !schoolUser.getIsUnsubscribeGenNotif())
					adminUsersEmail.add(schoolUser.getUsername());
			}
		}
		/**Get all the admin users if issue related to registration based on parent user email id**/
		/*else{
			adminUsersEmail = new HashSet<>(reportsDao.adminUserEmails(supportUser.getUserEmail()));
		}*/
		supportUserNotificationReq.setCustomMessage(supportUser.getCustomMessage());
		supportUserNotificationReq.setAdminEmails(new ArrayList<String>(adminUsersEmail));
		supportUserNotificationReq.setIssueType(supportUser.getIssueType());
		supportUserNotificationReq.setUserEmail(supportUser.getUserEmail());
		/**Build email notification request, if issue related to student profile or student's Lunch order**/
		if(supportUser.getStudentUser() != null && supportUser.getStudentUser().getUserId() != null){
			supportUserNotificationReq.setGrade(supportUser.getStudentUser().getGradeName().toString());
			supportUserNotificationReq.setStudentName(supportUser.getStudentUser().getFirstName()+" "+supportUser.getStudentUser().getLastName());
			supportUserNotificationReq.setStudentId(supportUser.getStudentUser().getStudentId());
			supportUserNotificationReq.setOrderIssueYearMonth(supportUser.getOrderIssueYearMonth() != null ? 
					Month.of(Integer.parseInt(supportUser.getOrderIssueYearMonth().substring(4)))
					.name()+" "+supportUser.getOrderIssueYearMonth().substring(0,4): null);
			supportUserNotificationReq.setSchoolName(supportUser.getMealSchool().getSchoolName());
		}else if(supportUser.getMealSchool() != null && supportUser.getMealSchool().getSchoolId() != null){
			supportUserNotificationReq.setGrade(supportUser.getStudentGrade() != null ? supportUser.getStudentGrade() : "");
			supportUserNotificationReq.setStudentName(supportUser.getStudentName());
			supportUserNotificationReq.setStudentId("");
			supportUserNotificationReq.setOrderIssueYearMonth("");
			supportUserNotificationReq.setSchoolName(supportUser.getMealSchool().getSchoolName());
		}
		/**Build email notification request, if issue related to registration**/
		else{
			supportUserNotificationReq.setmMAdminEmail(mmSupportEmail);
		}
		if(supportUserNotificationReq != null){
			Map<String, List<SupportUserNotificationReq>> notificationRequest = new HashMap<String, List<SupportUserNotificationReq>>();
			notificationRequest.put("users", Arrays.asList(supportUserNotificationReq));		
			sendNotificationUtil.supportEmailSend(notificationRequest);
		}
	}
	
	/**This method used for save the menu order audit history on every operation (i.e. create, update and cancel order)**/
	public void auditMenuOrderForHistory(MealOrderDetails mealOrderDetails){
		MenuOrderHistoryAudit menuOrderHistoryAudit = new MenuOrderHistoryAudit();
		List<SchoolMeal> schoolMeals = new ArrayList<>(mealOrderDetails.getSchoolMeals());
		List<MealCalendar> mealCalendars = new ArrayList<>(mealOrderDetails.getMealCalendars());
		menuOrderHistoryAudit.setCreatedBy(mealOrderDetails.getLoggedUser());
		menuOrderHistoryAudit.setCreatedOn(new Date());
		menuOrderHistoryAudit.setItems_count(mealOrderDetails.getItems_count());
		menuOrderHistoryAudit.setLatestOrdersPdfLink(mealOrderDetails.getMenuOrderedPdfLink());
		menuOrderHistoryAudit.setOrderId(mealOrderDetails.getSchoolId());
		menuOrderHistoryAudit.setPaymentStatus(mealOrderDetails.getPaymentStatus());
		menuOrderHistoryAudit.setSchoolMeals(new HashSet<>(schoolMeals));
		menuOrderHistoryAudit.setMealCalendars(new HashSet<>(mealCalendars));
		menuOrderHistoryAudit.setStudentUser(mealOrderDetails.getStudentUser());
		menuOrderHistoryAudit.setTotalPrice(mealOrderDetails.getTotalPrice());
		menuOrderHistoryAudit.setYearMonth(mealOrderDetails.getYearMonth());
		menuOrderHistoryAudit.setCrudOperationVal(mealOrderDetails.getCrudOperationVal());
		menuOrderHistoryAudit.setCancellationNote(mealOrderDetails.getCancellationNote());
		menuOrderHistoryAudit.setCancellationDates(mealOrderDetails.getCancellationDates());
		menuOrderHistoryAudit.setOrderAmount(mealOrderDetails.getOrderAmount());
		menuOrderHistoryAudit.setIsEligibleForFreeMeal(mealOrderDetails.getIsEligibleForFreeMeal());
		menuOrderHistoryAudit.setIsEligibleForReducedPrice(mealOrderDetails.getIsEligibleForReducedPrice());
		menuOrderHistoryAudit.setIsEligForDiscount(mealOrderDetails.getIsEligForDiscount());
		menuOrderHistoryAudit.setInstantPaymentId(mealOrderDetails.getInstantPaymentId());
		menuOrderHistoryAudit.setMenuType(mealOrderDetails.getMenuType());
		entityManager.merge(menuOrderHistoryAudit);
		logger.info("Menu order data captured in history audit table");
	}
	
	/**This method used for approved the applocation
	 * @throws Exception **/
	public void approvedDeclinedApplication(HouseholdApplicationForFRM householdApplicationForFRM, Boolean isExport, HttpServletResponse resp) throws Exception{
		List<HouseholdAppOtherInfo> studentsData = householdApplicationForFRM.getHouseholdAppInfo().stream()
				.filter(x -> "Student".equalsIgnoreCase(x.getPersonType())).collect(Collectors.toCollection(LinkedList::new));
		List<HouseholdAppOtherInfo> freeStudents = new ArrayList<HouseholdAppOtherInfo>();
		List<HouseholdAppOtherInfo> reducedStudents = new ArrayList<HouseholdAppOtherInfo>();
		List<HouseholdAppOtherInfo> declinedStudents = new ArrayList<HouseholdAppOtherInfo>();
		String freeStudentsName = "";
		String reducedStudentsName = "";
		String declinedStudentsName = "";
		String schoolName = "";
		String adminEmail = "";
		Set<String> parentEmails = new HashSet<String>();
		Date schoolYearEndDate = schoolYearRepository.getSchoolYearEndDate(householdApplicationForFRM.getMealSchoolId(), householdApplicationForFRM.getSchoolYear());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(HouseholdAppOtherInfo householdAppOtherInfo : studentsData){
			StudentUser studentUser = studentUserRepository.findOne(householdAppOtherInfo.getStudentRecId());
			Integer previousEligStatus = mealManageAPIDao.getEligStatus(studentUser.getIsFreeMealEligible(), studentUser.getIsReducePriceEligible());
			if(!isExport && householdApplicationForFRM.getStatus().equalsIgnoreCase("Approved")){
				if(householdAppOtherInfo.getIsFreeMeal() != null){
					if(householdAppOtherInfo.getIsFreeMeal()){
						studentUser.setIsFreeMealEligible(true);
						studentUser.setIsReducePriceEligible(false);
					}else{
						studentUser.setIsReducePriceEligible(true);
						studentUser.setIsFreeMealEligible(false);
					}
					studentUser.setDecisionReason(householdAppOtherInfo.getDecisionReason());
					studentUser.setCategory(householdAppOtherInfo.getCategory());
					studentUser.setActualPrg(householdAppOtherInfo.getActualPrg());
				}else{
					studentUser.setIsReducePriceEligible(false);
					studentUser.setIsFreeMealEligible(false);	
					studentUser.setDecisionReason(null);
					studentUser.setCategory(null);
					studentUser.setActualPrg(null);
				}
				studentUser.setReCertificateDate(sdf.parse(du.formatDateToString(new Date(), "yyyy-MM-dd", studentUser.getMealSchool().getSchoolTimezone())));
				studentUser.setRecertPending("N");	
				Integer currentEligStatus = mealManageAPIDao.getEligStatus(studentUser.getIsFreeMealEligible(), studentUser.getIsReducePriceEligible());
				if(currentEligStatus != previousEligStatus)
					mealManageAPIDao.addUpdateStudentEligibility(currentEligStatus, previousEligStatus, true, null, schoolYearEndDate, "Online Application Process", studentUser);
				studentUserRepository.save(studentUser);
			}
			if(schoolName.trim().isEmpty())
				schoolName = studentUser.getMealSchool().getSchoolName();
			if(adminEmail.trim().isEmpty())
				adminEmail = studentUser.getMealSchool().getContactPEmail() != null ? studentUser.getMealSchool().getContactPEmail() : "";
			if(householdAppOtherInfo.getIsFreeMeal() == null){
				declinedStudents.add(householdAppOtherInfo);
				declinedStudentsName = declinedStudentsName+(declinedStudentsName.trim().isEmpty()?"":", ")+studentUser.getFirstName()+" "+studentUser.getLastName();
			}else if(householdAppOtherInfo.getIsFreeMeal()){
				freeStudents.add(householdAppOtherInfo);
				freeStudentsName = freeStudentsName+(freeStudentsName.trim().isEmpty()?"":", ")+studentUser.getFirstName()+" "+studentUser.getLastName();
			}else{
				reducedStudents.add(householdAppOtherInfo);
				reducedStudentsName = reducedStudentsName+(reducedStudentsName.trim().isEmpty()?"":", ")+studentUser.getFirstName()+" "+studentUser.getLastName();
			}
			parentEmails.add(studentUser.getParentuser().getUserName());
			if(studentUser.getParentuser().getParentAltEmail() != null 
					&& !studentUser.getParentuser().getParentAltEmail().trim().isEmpty())
				parentEmails.add(studentUser.getParentuser().getParentAltEmail());				
		}
		if(freeStudents.size() > 0){
			buildHouseholdAppStatusUpdate(parentEmails, freeStudentsName, true, freeStudents, schoolName, householdApplicationForFRM, adminEmail,isExport,resp);
		}
		if(reducedStudents.size() > 0){
			buildHouseholdAppStatusUpdate(parentEmails, reducedStudentsName, false, reducedStudents, schoolName, householdApplicationForFRM, adminEmail,isExport,resp);
		}
		if(declinedStudents.size() > 0){
			buildHouseholdAppStatusUpdate(parentEmails, declinedStudentsName, null, declinedStudents, schoolName, householdApplicationForFRM, adminEmail,isExport,resp);
		}
	}
	
	/**This method used for approved the applocation
	 * @throws Exception **/
	public void incompleteApplication(HouseholdApplicationForFRM householdApplicationForFRM, Boolean isExport, HttpServletResponse resp) throws Exception{
		List<HouseholdAppOtherInfo> studentsData = householdApplicationForFRM.getHouseholdAppInfo().stream()
				.filter(x -> "Student".equalsIgnoreCase(x.getPersonType())).collect(Collectors.toCollection(LinkedList::new));
		List<HouseholdAppOtherInfo> incStds = new ArrayList<HouseholdAppOtherInfo>();
		String incStudentsName = "";
		String schoolName = "";
		String adminEmail = "";
		Set<String> parentEmails = new HashSet<String>();
		for(HouseholdAppOtherInfo householdAppOtherInfo : studentsData){
			StudentUser studentUser = studentUserRepository.findOne(householdAppOtherInfo.getStudentRecId());
			if(schoolName.trim().isEmpty())
				schoolName = studentUser.getMealSchool().getSchoolName();
			if(adminEmail.trim().isEmpty())
				adminEmail = studentUser.getMealSchool().getContactPEmail() != null ? studentUser.getMealSchool().getContactPEmail() : "";
			incStds.add(householdAppOtherInfo);
			incStudentsName = incStudentsName+(incStudentsName.trim().isEmpty()?"":", ")+studentUser.getFirstName()+" "+studentUser.getLastName();
			parentEmails.add(studentUser.getParentuser().getUserName());
			if(studentUser.getParentuser().getParentAltEmail() != null 
					&& !studentUser.getParentuser().getParentAltEmail().trim().isEmpty())
				parentEmails.add(studentUser.getParentuser().getParentAltEmail());				
		}
		if(incStds.size() > 0)
			buildHouseholdAppINCStatusUpdate(parentEmails, incStudentsName, incStds, schoolName, householdApplicationForFRM, adminEmail, isExport, resp);
	}
	
	/**This method used for send the email to parent reagrding household application status update
	 * @throws Exception **/
	private void buildHouseholdAppStatusUpdate(Set<String> parentEmails, String studentsName, Boolean isFree,
			List<HouseholdAppOtherInfo> studentList, String schoolName, HouseholdApplicationForFRM householdApplicationForFRM, String adminEmail,Boolean isExport, HttpServletResponse resp) throws Exception{
		//send email to admin user for the application 
		String pdfFileName = "NoticeBenefits_"+householdApplicationForFRM.getApplicationId()+"_"+(isFree==null?"Declined":(isFree?"Free":"Reduced"))+".pdf";
		String  noticeLink = awsUtility.fileUploadPath(pdfFileName, "noticeBenefits");
		/*String pdfFileName1 = "NoticeBenefits_"+householdApplicationForFRM.getApplicationId()+"_"+(isFree==null?"Declined":(isFree?"Free":"Reduced"))+"_Sp.pdf";
		String  noticeLink1 = awsUtility.fileUploadPath(pdfFileName1, "noticeBenefits");*/
		List<BigInteger> schoolIds = districtRepository.getSchoolIds((long) 4);
		
		if(schoolIds != null && schoolIds.contains(BigInteger.valueOf(householdApplicationForFRM.getMealSchoolId()))){
			noticeBenefitsLetterV2.noticeBenefitsPdf(studentList, isFree, householdApplicationForFRM, pdfFileName,schoolName, isExport, resp);
			//noticeBenefitsLetterSpanish.noticeBenefitsPdf(studentList, isFree, householdApplicationForFRM, pdfFileName1, schoolName);
		}else
			noticeBenefitsApp.noticeBenefitsPdf(studentList, isFree, householdApplicationForFRM, pdfFileName,schoolName, isExport, resp);
		if(!isExport){
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("stdName", studentsName);
			reqMap.put("schoolName", schoolName);
			reqMap.put("status", householdApplicationForFRM.getStatus().equalsIgnoreCase("Approved") ? "Approved":"Declined");
			reqMap.put("eligibility", isFree == null ? "None": (isFree ? "Free": "Reduced Price"));
			reqMap.put("parentEmails", String.join(",", parentEmails));
			reqMap.put("noticeLink", noticeLink);
			//reqMap.put("noticeLinkSp", noticeLink1);
			reqMap.put("adminEmail", adminEmail);
			if(schoolIds != null && schoolIds.contains(BigInteger.valueOf(householdApplicationForFRM.getMealSchoolId()))){
				sendNotificationUtil.aprvDeclineApplicationEmailV2(reqMap);
			}else
				sendNotificationUtil.aprvDeclineApplicationEmail(reqMap);
		}		
	}
	
	/**This method used for send the email to parent reagrding household application status update
	 * @throws Exception **/
	private void buildHouseholdAppINCStatusUpdate(Set<String> parentEmails, String studentsName,
			List<HouseholdAppOtherInfo> studentList, String schoolName, HouseholdApplicationForFRM householdApplicationForFRM, String adminEmail, Boolean isExport, HttpServletResponse resp) throws Exception{
		//send email to admin user for the application 
		String pdfFileName = "NoticeBenefits_"+householdApplicationForFRM.getApplicationId()+"_Incomplete.pdf";
		String  noticeLink = awsUtility.fileUploadPath(pdfFileName, "noticeBenefits");
		/*String pdfFileName1 = "NoticeBenefits_"+householdApplicationForFRM.getApplicationId()+"_Incomplete_Sp.pdf";
		String  noticeLink1 = awsUtility.fileUploadPath(pdfFileName1, "noticeBenefits");*/
		noticeBenefitsLetterV2.noticeIncBenefitsPdf(studentList, householdApplicationForFRM, pdfFileName,schoolName,isExport, resp);
		//noticeBenefitsLetterSpanish.noticeIncBenefitsPdf(studentList, householdApplicationForFRM, pdfFileName1,schoolName);
		if(!isExport){
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("stdName", studentsName);
			reqMap.put("schoolName", schoolName);
			reqMap.put("status", "Incomplete");
			reqMap.put("eligibility", "N/A");
			reqMap.put("parentEmails", String.join(",", parentEmails));
			reqMap.put("noticeLink", noticeLink);
			reqMap.put("adminEmail", adminEmail);
			reqMap.put("spanish", "yes");
			sendNotificationUtil.aprvDeclineApplicationEmailV2(reqMap);
		}		
	}
	
	/**This method used for send email to school admin when parent submit household application for free/reduced price meals**/
	public void householdAppEmail(HouseholdApplicationForFRM householdApplicationForFRM){
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(householdApplicationForFRM.getMealSchoolId());
		List<String> adminUsersEmail = new ArrayList<String>();
		Map<String, String> reqMap = new HashMap<String, String>();
		if(mealSchool != null){
			for(SchoolUser schoolUser : mealSchool.getSchoolUsers()){
				if(schoolUser.getIsActive() && schoolUser.getIsVerified() && !schoolUser.getIsUnsubscribeGenNotif() && 
						schoolUser.getIsPrimaryUser() != null && schoolUser.getIsPrimaryUser())
					adminUsersEmail.add(schoolUser.getUsername());
			}
		}
		HouseholdAppOtherInfo result = householdApplicationForFRM.getHouseholdAppInfo().stream()
				.filter(x -> "Adult".equalsIgnoreCase(x.getPersonType()) && x.getFilledTheApplication() != null 
				&& x.getFilledTheApplication()).findAny().orElse(null);  
		if(result != null)
			reqMap.put("parentUserName", result.getFname()+" "+result.getLname());
		reqMap.put("parentEmail", householdApplicationForFRM.getPrmyParentEmail());
		reqMap.put("applicationId", String.format("%05d", householdApplicationForFRM.getApplicationId()));
		reqMap.put("adminEmails", String.join(",", adminUsersEmail));
		sendNotificationUtil.householdAppEmail(reqMap);
	}
	
	/**This method used for send the email to the MM support regarding demo request**/
	public void sendDemoReqEmail(DemoRequest demoRequest) {
		Map<String, String> selfNotifReq = new HashMap<String, String>();
		SupportUserNotificationReq supportUserNotificationReq = new SupportUserNotificationReq();
		selfNotifReq.put("replyEmail", mmContactEmail);
		selfNotifReq.put("subject", "Request received by MealManage");
		supportUserNotificationReq.setType("parent");

		selfNotifReq.put("userName", demoRequest.getName() != null ? demoRequest.getName() : "");
		selfNotifReq.put("userEmails", demoRequest.getEmailAddress());
		selfNotifReq.put("requestId", StringUtils.leftPad(demoRequest.getRequestId().toString(), 5, "0"));
		selfNotifReq.put("phone", demoRequest.getMobileNo());
		selfNotifReq.put("school", demoRequest.getSchoolName());
		selfNotifReq.put("requestingFor", demoRequest.getRequestingFor());
		selfNotifReq.put("ccEmail", mmContactEmail);
		sendNotificationUtil.selfRequestNotification(selfNotifReq);
		
		
		/*Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("name", demoRequest.getFirstName() + " "+demoRequest.getLastName());
		reqMap.put("firstName", demoRequest.getFirstName());
		reqMap.put("schoolName", (demoRequest.getSchoolName() != null && !demoRequest.getSchoolName().equalsIgnoreCase("null")) ? demoRequest.getSchoolName() : "");
		reqMap.put("userEmail", demoRequest.getEmailAddress());
		reqMap.put("emailTo", mmContactEmail);
		reqMap.put("city", (demoRequest.getCity() != null ? demoRequest.getCity() : ""));
		reqMap.put("country", (demoRequest.getCountry() != null ? demoRequest.getCountry() : ""));
		reqMap.put("mobile", (demoRequest.getMobileNo() != null ? demoRequest.getMobileNo() : ""));
		reqMap.put("requestingFor", demoRequest.getRequestingFor() != null ? demoRequest.getRequestingFor() : "");	
		sendNotificationUtil.sendDemoReqEmail(reqMap);*/
	}

	/**This method used for create the Caterer User Auth**/
	public void catererUserAuth(Caterer caterer) throws Exception {
		try{
			for(CatererUser usr : caterer.getCatererUsers()){
				UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getUsername(), "ROLE_CATERER");
				if(usersAuthInfo == null){
					usersAuthInfo = new UsersAuthInfo();
					usersAuthInfo.setUsername(usr.getUsername());
					usersAuthInfo.setRole("ROLE_CATERER");/*
					usersAuthInfo.setfToken(UUID.randomUUID().toString());
					usersAuthInfo.setfTokenTime(new Date());*/
					usersAuthInfo.setCreatedBy(caterer.getLoggedUser());
					usersAuthInfo.setCreatedOn(new Date());
					usersAuthInfo.setMobile(usr.getMobileNo());
					entityManager.persist(usersAuthInfo);
				}else if(!usersAuthInfo.getMobile().equalsIgnoreCase(usr.getMobileNo())){
					usersAuthInfo.setModifiedBy(caterer.getLoggedUser());
					usersAuthInfo.setModifiedOn(new Date());
					usersAuthInfo.setMobile(usr.getMobileNo());
					entityManager.merge(usersAuthInfo);
				}
			}
		}catch(Exception e){
			logger.error("Failed to onboard Caterer due to "+e.getMessage());
			throw new Exception("Failed to onboard Caterer");
		}
	}
	
	/**This method used for create the District User Auth**/
	public void districtUserAuth(District district) throws Exception {
		try{
			for(DistrictUser usr : district.getDistrictUsers()){
				UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getUsername(), "ROLE_DISTRICT");
				if(usersAuthInfo == null){
					usersAuthInfo = new UsersAuthInfo();
					usersAuthInfo.setUsername(usr.getUsername());
					usersAuthInfo.setRole("ROLE_DISTRICT");/*
					usersAuthInfo.setfToken(UUID.randomUUID().toString());
					usersAuthInfo.setfTokenTime(new Date());*/
					usersAuthInfo.setCreatedBy(district.getLoggedUser());
					usersAuthInfo.setCreatedOn(new Date());
					usersAuthInfo.setMobile(usr.getMobileNo());
					usersAuthInfo.setPartner_id(district.getPartnerId());
					usersAuthInfo.setPartnerName(district.getPartnerName());
					entityManager.persist(usersAuthInfo);
				}else if(!usersAuthInfo.getMobile().equalsIgnoreCase(usr.getMobileNo())){
					usersAuthInfo.setModifiedBy(district.getLoggedUser());
					usersAuthInfo.setModifiedOn(new Date());
					usersAuthInfo.setMobile(usr.getMobileNo());
					if(district.getPartnerId() != null && !district.getPartnerId().trim().isEmpty())
						usersAuthInfo.setPartner_id(district.getPartnerId());
					entityManager.merge(usersAuthInfo);
				}
			}
		}catch(Exception e){
			logger.error("Failed to onboard District due to "+e.getMessage());
			throw new Exception("Failed to onboard District");
		}
	}
	
	/**This method used for create the District User Auth**/
	public void generatePartnerToken(District district, boolean apiCall) throws Exception {
		try{
			for(DistrictUser usr : district.getDistrictUsers()){
				UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getUsername(), "ROLE_DISTRICT");
				if(usersAuthInfo != null && usersAuthInfo.getPartner_id() != null && !usersAuthInfo.getPartner_id().trim().isEmpty()){
					if(!apiCall){
						usersAuthInfo.setDistrict_id(district.getId());
						entityManager.merge(usersAuthInfo);
					}else				
						sendNotificationUtil.distPartnerAPI(district.getId(), usersAuthInfo.getPartner_id());
				}
			}
		}catch(Exception e){
			logger.error("Failed to call partner API for token generation due to "+e.getMessage());
			throw new Exception("Failed to call partner API for token generation.");
		}
	}
	
	/**This method used for handle nutrition audit**/
	public void nutritionAudits(NutritionAudit nutritionAudit){
		NutritionAudit nutritionAudit2 = nutritionAuditRepo.findByItemIdAndEffectiveEndDateIsNull(nutritionAudit.getItemId());
		if(nutritionAudit2 != null){
			nutritionAudit2.setEffectiveEndDate(new Date());
			entityManager.merge(nutritionAudit2); 
		}
		updateMenuNutrition(nutritionAudit);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
			nutritionAudit.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
		nutritionAudit.setCreatedDate(new Date());
		nutritionAudit.setEffectiveStartDate(new Date());
	}
	
	private void updateMenuNutrition(NutritionAudit nutritionAudit){
		MenuItem menuItem = menuItemRepository.findMenuItemsById(nutritionAudit.getItemId());
		if(menuItem != null){
			menuItem.setCalories(nutritionAudit.getCalories());
			menuItem.setTotalFat(nutritionAudit.getTotalFat());
			menuItem.setSaturatedFat(nutritionAudit.getSaturatedFat());
			menuItem.setCholestral(nutritionAudit.getCholestral());
			menuItem.setSodium(nutritionAudit.getSodium());
			menuItem.setTotalCarbohydrate(nutritionAudit.getTotalCarbohydrate());
			menuItem.setDietaryFiber(nutritionAudit.getDietaryFiber());
			menuItem.setSugars(nutritionAudit.getSugars());
			menuItem.setProtein(nutritionAudit.getProtein());
			menuItem.setVitaminA(nutritionAudit.getVitaminA());
			menuItem.setVitaminB6(nutritionAudit.getVitaminB6());
			menuItem.setVitaminB12(nutritionAudit.getVitaminB12());
			menuItem.setVitaminC(nutritionAudit.getVitaminC());
			menuItem.setVitaminD(nutritionAudit.getVitaminD());
			menuItem.setVitaminE(nutritionAudit.getVitaminE());
			menuItem.setVitaminK(nutritionAudit.getVitaminK());
			menuItem.setCalcium(nutritionAudit.getCalcium());
			menuItem.setIron(nutritionAudit.getIron());
			menuItem.setPotassium(nutritionAudit.getPotassium());
			menuItem.setThiamin(nutritionAudit.getThiamin());
			menuItem.setRiboFlavin(nutritionAudit.getRiboFlavin());
			menuItem.setNiacin(nutritionAudit.getNiacin());
			menuItem.setFolate(nutritionAudit.getFolate());
			menuItem.setPantothenicAcid(nutritionAudit.getPantothenicAcid());
			menuItem.setPhosphorous(nutritionAudit.getPhosphorous());
			menuItem.setManganese(nutritionAudit.getManganese());
			menuItem.setMagnesium(nutritionAudit.getMagnesium());
			menuItem.setZinc(nutritionAudit.getZinc());
			menuItem.setSelenium(nutritionAudit.getSelenium());
			menuItem.setCopper(nutritionAudit.getCopper());
			menuItem.setIsNutrAvailable(true);
			menuItemRepository.save(menuItem);
		}		
	}
}
