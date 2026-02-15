package com.mealManage.aop;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.repository.PaymentGatewayRepo;
import com.mealManage.mealmodel.school.BaseEntity;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.transaction.PaymentGateway;
import com.mealManage.mealmodel.transaction.SchoolPayGatewayInfo;
import com.mealManage.mealmodel.user.DemoRequest;
import com.mealManage.mealmodel.user.FMEligibilitySurvey;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;
import com.mealManage.mealmodel.user.SchoolUser;
import com.mealManage.mealmodel.user.SupportUser;
import com.mealManage.menu.entities.MenuItem;
import com.mealManage.menu.entities.NutritionAudit;
import com.mealManage.util.AOPUtil;
import com.mealManage.util.AWSUtility;
import com.mealManage.util.StripeUtil;

@Aspect
@Component
public class AspectAOP  implements ThrowsAdvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(AspectAOP.class);
    private static final Logger LOGGERA = LoggerFactory.getLogger("analytics");
	@SuppressWarnings("unused")
	private static final Logger LOGGERI = LoggerFactory.getLogger("info");
	private static final Logger LOGGERE = LoggerFactory.getLogger("error");
	
	@Autowired
	private AOPUtil aopUtil;
	@Autowired
	private StripeUtil stripeUtil;

	@Autowired
	private AWSUtility awsUtility;
	@Autowired
	private PaymentGatewayRepo paymentGatewayRepo;


    @Around("execution(* com.mealManage.repository..*.*(..))")
	public Object aroundServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		LOGGERA.debug(">>>>>>>> invoking {}", joinPoint.getSignature());
		Date start = new Date();
		Object result = joinPoint.proceed();
		Date end = new Date();
		LOGGERA.debug(">>>>>>>> return of {} with {}", joinPoint.getSignature(), BeanUtils.describe(result));
		LOGGERA.debug(">>>>>>>> end of {} take {} millisec",
					joinPoint.getSignature(), end.getTime() - start.getTime());
		return result;
	}


   @Before("execution(* com.mealManage.mealmodel.repository..*.*(..))")
   public void beforeMethod(JoinPoint joinPoint) throws Exception {
        StringBuilder arguments = generateArgumentsString(joinPoint.getArgs());
       LOGGER.info("****  method:" + joinPoint.getSignature().getName() + " *****Params :"+arguments);

       if(joinPoint.getSignature().getName().equalsIgnoreCase("save") && arguments.toString().contains("com.mealManage.mealmodel")) {
		   BaseEntity baseEnt = (BaseEntity) joinPoint.getArgs()[0];
		   if (baseEnt.getCreatedBy() != null || baseEnt.getCreatedOn() != null) {
			   LOGGER.info("Invoking the API for update the existing record.");
			   baseEnt.setModifiedBy(baseEnt.getLoggedUser());
			   baseEnt.setModifiedOn(new Date());
			   if (arguments.toString().contains("com.mealManage.mealmodel.user.HouseholdApplicationForFRM")) {
				   HouseholdApplicationForFRM householdApplicationForFRM = (HouseholdApplicationForFRM) joinPoint.getArgs()[0];
				   LOGGER.info("Update the application status");
				   householdApplicationForFRM.setStatusUpdateDate(new Date());
			   }
		   } else if (baseEnt.getCreatedBy() == null || baseEnt.getCreatedOn() == null) {
			   LOGGER.info("Invoking the API for creating new record.");
			   baseEnt.setCreatedOn(new Date());
			   baseEnt.setCreatedBy(baseEnt.getLoggedUser());
			   if (arguments.toString().contains("com.mealManage.mealmodel.school.MealSchool")) {
				   MealSchool mealSchool = (MealSchool) joinPoint.getArgs()[0];
				   if (mealSchool.getSchoolId() == null) {
					   if(mealSchool.getSubdomain() == null || mealSchool.getSubdomain().trim().isEmpty())
						   mealSchool = aopUtil.generateSubdomain(mealSchool);
					   Boolean isStripeEnable = false;
					   if(mealSchool.getPaymentGateways() != null)
							for(SchoolPayGatewayInfo schoolGateway : mealSchool.getPaymentGateways()){
								PaymentGateway paymentGateway = paymentGatewayRepo.findOne(schoolGateway.getPaymentGateway().getId());
								if(paymentGateway.getName().equalsIgnoreCase("Stripe")){
									isStripeEnable = true;
									break;
								}
							}
					   //send email to the school primary user for create stripe account setup if school having isPaymentEnable status as true
					   if (isStripeEnable && mealSchool.getIsPaymentEnabled() != null && mealSchool.getIsPaymentEnabled()
							   && (mealSchool.getStripeAccountId() == null || mealSchool.getStripeAccountId().isEmpty())) {
						   String primaryUserEmail = "";
						   SchoolUser schoolUser = mealSchool.getSchoolUsers().stream()
								   .filter(p -> p.getIsPrimaryUser() != null && p.getIsPrimaryUser() && p.getIsPaymentRegister() != null
										   && p.getIsPaymentRegister())
								   .collect(Collectors.toCollection(ArrayList::new)).get(0);
						   if (schoolUser != null && schoolUser.getUsername() != null && schoolUser.getIsPrimaryUser()) {
							   primaryUserEmail = schoolUser.getUsername();
							   stripeUtil.sendStripeSetupEmail(primaryUserEmail, mealSchool.getSchoolName());
						   }
					   }
				   }
				   aopUtil.schoolUserAuth(mealSchool);
			   }
		   }
		   /**Set the school details based on the parent email and set all the admin emails to whom we need notify**/
		   if (arguments.toString().contains("com.mealManage.mealmodel.user.FMEligibilitySurvey")) {
			   FMEligibilitySurvey fmEligibilitySurvey = (FMEligibilitySurvey) joinPoint.getArgs()[0];
			   fmEligibilitySurvey = aopUtil.buildFmSurveyElig(fmEligibilitySurvey);
			   LOGGER.info("Meal school relationship added in survey entry");
		   }
		   /**Set the school reference in support user entity**/
		   if (arguments.toString().contains("com.mealManage.mealmodel.user.SupportUser")) {
			   SupportUser supportUser = (SupportUser) joinPoint.getArgs()[0];
			   if (supportUser.getStudentUser() != null && supportUser.getStudentUser().getUserId() != null) {
				   supportUser.setMealSchool(supportUser.getStudentUser().getMealSchool());
				   LOGGER.info("Created the School reference in Support User entity");
			   }
		   }
		   /**Create/Update the Caterer User Auth**/
			if (arguments.toString().contains("com.mealManage.mealmodel.caterer.Caterer")) {
				Caterer caterer = (Caterer) joinPoint.getArgs()[0];
				aopUtil.catererUserAuth(caterer);
			}
			/**Create/Update the District User Auth**/
			if (arguments.toString().contains("com.mealManage.mealmodel.school.District")) {
				District district = (District) joinPoint.getArgs()[0];
				aopUtil.districtUserAuth(district);
			}
	   }

       if(joinPoint.getSignature().getName().equalsIgnoreCase("save") && arguments.toString().contains("com.mealManage.menu.entities.MenuItem")){
		   MenuItem menuItem = (MenuItem)joinPoint.getArgs()[0];
		   if(menuItem.getImageBase64Content() != null && !StringUtils.isEmpty(menuItem.getImageBase64Content())) {
			   Long schoolId = menuItem.getSchoolDetails().getSchoolId();
			   String name = menuItem.getName().replace(" ","_")+".jpg";
			   String fileName = "School_"+schoolId+"_"+name;
			   String uploadedPath = awsUtility.uploadImage(menuItem.getImageBase64Content(),fileName);
			   menuItem.setImageBase64Content(null);
			   menuItem.setImageUrl(uploadedPath);
		   }
	   }
       /**For add/update item's nutrition info **/
       if(joinPoint.getSignature().getName().equalsIgnoreCase("save") && arguments.toString().contains("com.mealManage.menu.entities.NutritionAudit")){
    	   NutritionAudit nutritionAudit = (NutritionAudit) joinPoint.getArgs()[0];
    	   aopUtil.nutritionAudits(nutritionAudit);
	   }
   }
    
    //@After("execution(* save(..))")
    @After("execution(* com.mealManage.mealmodel.repository..*.*(..))")
    public void afterMethod(JoinPoint joinPoint) throws Exception {
		StringBuilder arguments = generateArgumentsString(joinPoint.getArgs());
        LOGGER.info("****  method:" + joinPoint.getSignature().getName() + " *****Params of Entity class :"+arguments+" has been completed successfully");
        if(joinPoint.getSignature().getName().equalsIgnoreCase("save") && arguments.toString().contains("com.mealManage.mealmodel")){
        	/**Send the mail to school admin users for the free/reduced price lunch eligibility survey on the behalf of parent**/
        	if(arguments.toString().contains("com.mealManage.mealmodel.user.FMEligibilitySurvey")){
      			FMEligibilitySurvey fmEligibilitySurvey = (FMEligibilitySurvey) joinPoint.getArgs()[0];
      			if(fmEligibilitySurvey.getSurveyId() != null && fmEligibilitySurvey.getSurveyId() != 0){
      				aopUtil.sendEmailToAdminFM(fmEligibilitySurvey);
                	LOGGER.info("Admin user has been notify for the Free/Reduced Price lunch program eligibility survey on behalf of parent user");
      			}      			
     		 }
        	/**Send the mail to school admin users/MealManage contact regarding support**/
        	if(arguments.toString().contains("com.mealManage.mealmodel.user.SupportUser")){
      			SupportUser supportUser = (SupportUser) joinPoint.getArgs()[0];
      			if(supportUser.getSupportReqId() != null && supportUser.getSupportReqId() != 0 
      					&& (supportUser.getTktCurrentStatus() == null || supportUser.getTktCurrentStatus() == 0)
      					&& supportUser.getModifiedOn() == null){
      				aopUtil.sendEmailToAdminAndMealManageSupport(supportUser);
                	LOGGER.info("Email has been sent successfully regarding parent user support");
      			}      			
     		 }
        	/**Audit the menu order history**/
        	if(arguments.toString().contains("com.mealManage.mealmodel.meal.MealOrderDetails")){
        		MealOrderDetails mealOrderDetails = (MealOrderDetails) joinPoint.getArgs()[0];
      			if(mealOrderDetails.getSchoolId() != null && mealOrderDetails.getSchoolId() != 0){
      				aopUtil.auditMenuOrderForHistory(mealOrderDetails);
                	LOGGER.info("Menu order details successfully saved in order audit history table");
      			}      			
     		 }
        	/**Used for approved/declined the application**/
        	if(arguments.toString().contains("com.mealManage.mealmodel.user.HouseholdApplicationForFRM")){
      			HouseholdApplicationForFRM householdApplicationForFRM = (HouseholdApplicationForFRM) joinPoint.getArgs()[0];
      			if(!householdApplicationForFRM.getStatus().equalsIgnoreCase("pending")){
      				LOGGER.info("Application status has been changed as "+(householdApplicationForFRM.getStatus()));
          			if(householdApplicationForFRM.getStatus().equalsIgnoreCase("in-complete"))
          				aopUtil.incompleteApplication(householdApplicationForFRM, false, null);
          			else if(!householdApplicationForFRM.getStatus().equalsIgnoreCase("cancelled"))
          				aopUtil.approvedDeclinedApplication(householdApplicationForFRM, false, null);
      			}else if(householdApplicationForFRM.getStatus().equalsIgnoreCase("pending") && 
      					householdApplicationForFRM.getModifiedBy() == null && householdApplicationForFRM.getModifiedOn() == null){
      				LOGGER.info("Send email to admin when first time household application submit by parent for free/reduced price meals");
      				aopUtil.householdAppEmail(householdApplicationForFRM);
      			}
     		 }
        	/**Used for send the email to support@mealmanage.com when demo request come**/
        	if(arguments.toString().contains("com.mealManage.mealmodel.user.DemoRequest")){
      			DemoRequest demoRequest = (DemoRequest) joinPoint.getArgs()[0];
      			if(demoRequest != null && demoRequest.getModifiedOn() == null){
      				LOGGER.info("Sending email to the MM support regarding demo request");
          			aopUtil.sendDemoReqEmail(demoRequest);
      			}
     		 }
        	
        	/**Call token generate partner API**/
			if (arguments.toString().contains("com.mealManage.mealmodel.school.District")) {
				District district = (District) joinPoint.getArgs()[0];
				aopUtil.generatePartnerToken(district,false);
				aopUtil.generatePartnerToken(district,true);
			}
        }
    }

    @AfterThrowing(pointcut = "execution(* com.mealManage..*.*(..))" , throwing= "error")
    public void logAfterThrowingException(JoinPoint joinPoint, Throwable error) {
    	LOGGERE.error("***** Class: "+joinPoint.getTarget().getClass().getName());
    	LOGGERE.error("**** Exception : " + error);
    }

    public StringBuilder generateArgumentsString(Object[] objects){
        StringBuilder stringBuilder = new StringBuilder("");
        for(Object str : objects) {
            stringBuilder.append(str);
           }
        return stringBuilder;
        }	
}