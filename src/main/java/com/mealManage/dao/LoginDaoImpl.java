package com.mealManage.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import com.mealManage.domain.Message;
import com.mealManage.domain.MessageEnvelope;
import com.mealManage.domain.NotificationRequest;
import com.mealManage.domain.UserActivationNotification;
import com.mealManage.mealmodel.repository.CatererRepository;
import com.mealManage.mealmodel.repository.DistrictRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.ParentDeviceInfoRepository;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.user.ParentDeviceInfo;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.SchoolUser;
import com.mealManage.mealmodel.user.SuperAdminUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.response.ParentValidationResp;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.UserDetails;

@Repository
@Transactional
/** This class implement by using LoginDao Interface **/
public class LoginDaoImpl implements LoginDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// Create entityManager persistence context reference
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private DistrictRepository districtRepository;
	
	@Autowired
	private ParentDeviceInfoRepository parentDeviceInfoRepository;
	
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	@Autowired
	private CatererRepository catererRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private StudentUserRepository studentUserRepository;	
	@Value("${sms.send.url}")
	private String smsSendURL;
	
	@Value("${email.opt.url}")
	private String emailSendOTPURL;
	@Autowired
	private Environment env;
	@Value("${email.notification.url}")
	private String notificationParentURL;
	@Value("${sms.otp.messages}")
	private String smsOtpMessage;
	@Value("${sms.otp.message.type}")
	private String smsOtpMsgType;
	@Value("${spring.mealmanage.subdomain}")
	private String mealManageAppSubdomain;

	/**
	 * This method used to insert the auto generated token into UserAuthInfo table.
	 * @param UsersAuthInfo
	 * @return the token value
	 *
	 */
	@Override
	public ServiceResponse inserForgotPasswordTokenInDB(UsersAuthInfo user) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			user.setfTokenTime(new Date());
			user.setModifiedOn(new Date());
			user.setModifiedBy(user.getUsername());
			entityManager.merge(user);
			logger.info("Token & token time inserted successfully in DB");
			serviceResponse.setStatusMessage("Password reset link generated.");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatus("Success");
			serviceResponse.setResetPwdLink(user.getfToken());
		} catch (Exception e) {
			logger.error("Error occured during insert the Forgot password Token in database due to :" + e.getMessage());
			serviceResponse.setStatusMessage("Failed to generate reset password link.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}

	/**
	 * This method used for reset the new login password.
	 * @param UsersAuthInfo
	 * @return the status value
	 *
	 */
	@Override
	public int updateForgotPassword(UsersAuthInfo user) {
		int status = 0;
		try {
			entityManager.merge(user);
			if(user.getRole().equalsIgnoreCase("ROLE_ADMIN")){
				SchoolUser schoolUser = mealSchoolRepository.schoolUser(user.getUsername());
				if(schoolUser != null && !schoolUser.getIsVerified()){
					schoolUser.setIsVerified(true);
					entityManager.merge(schoolUser);
				}
			}else if(user.getRole().equalsIgnoreCase("ROLE_CATERER"))
				catererRepository.updateIsVerified(user.getUsername());
			else if(user.getRole().equalsIgnoreCase("ROLE_DISTRICT"))
				districtRepository.updateIsVerified(user.getUsername());
			/*else if(user.getRole().equalsIgnoreCase("ROLE_SUPERADMIN")){
				SuperAdminUser superAdminUser = superAdminUserRepository.findByUsername(user.getUsername());
				if(superAdminUser != null && !superAdminUser.getIsActive()){
					superAdminUser.setIsActive(true);
					superAdminUserRepository.save(superAdminUser);
				}
			}*/
			status = 1;
			logger.info("Password update successfully");
		} catch (Exception e) {
			logger.error("Error occured during insert the Forgot password Token in database due to :" + e.getMessage());
		}
		return status;
	}

	/**
	 * This method used for create new super admin user.
	 * @return the status value
	 *
	 */
	@Override
	public String addSuperAdmin(SuperAdminUser users) {
		String status = null;
		entityManager.persist(users);
		UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(users.getUsername(), "ROLE_SUPERADMIN");
		if(usersAuthInfo == null){
			usersAuthInfo = new UsersAuthInfo();
			usersAuthInfo.setUsername(users.getUsername());
			usersAuthInfo.setRole("ROLE_SUPERADMIN");
			usersAuthInfo.setCreatedBy(users.getLoggedUser());
			usersAuthInfo.setCreatedOn(new Date());
			usersAuthInfo.setMobile(users.getMobileNo());
			entityManager.persist(usersAuthInfo); //using entity manager instead of repository because repository not updating created by column.
		}
		status = "Super Admin User created successfully.";
	return status;
	}
	
	/**
	 * This method used for validate the parent user and device info. 
	 * @Param UserDetails
	 * @return ParentValidationResp
	 *
	 */
	@Override
	public ParentValidationResp validateParent(UserDetails user, String mobNumb) {
		ParentValidationResp parentValidationResp = new ParentValidationResp();
		ParentDeviceInfo parentDeviceInfo = parentDeviceInfoRepository.findByDeviceDetailsAndUsername(user.getDeviceId(),
				user.getUsername());
		if(parentDeviceInfo == null){
			parentValidationResp.setStatusCode(200);
			parentValidationResp.setStatus("Device not registered");
			if(mobNumb != null && mobNumb.length() > 3)
				parentValidationResp.setMobileNumber("XXXXXXX"+mobNumb.substring(mobNumb.length()-3));
		}else{
			parentValidationResp.setStatus("Authenticated Successfully");
			parentValidationResp.setStatusCode(200);
		}
		return parentValidationResp;
	}
	
	/**
	 * This method used for generate the OTP and send it on registered email or mobile.
	 * @Param UserDetails
	 * @return ParentValidationResp
	 *
	 */
	@Override
	public ParentValidationResp generateOTP(UserDetails user) {
		ParentValidationResp parentValidationResp = new ParentValidationResp();
		try{		
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsername(user.getUsername());
			if(user.getParentMobile() != null && !user.getParentMobile().equalsIgnoreCase("")){
				Set<ParentUser> parentUsers = studentUserRepository.findUsersByEmail(user.getUsername());
				if(parentUsers != null)
					for(ParentUser parentUser : parentUsers){
						if(parentUser.getMobileNo() == null || parentUser.getMobileNo().equalsIgnoreCase("")){
							parentUser.setMobileNo(user.getParentMobile());
							entityManager.merge(parentUser);
						}
					}
				if(usersAuthInfo.getMobile() == null || usersAuthInfo.getMobile().equalsIgnoreCase("")){
					usersAuthInfo.setMobile(user.getParentMobile());
					entityManager.merge(usersAuthInfo);
				}				
			}
			
			if(usersAuthInfo != null){
				String otp = createOTP();
				usersAuthInfo.setOtp(otp);
				usersAuthInfoRepository.save(usersAuthInfo);
				switch(user.getOtpOn()){
				case "mobile" : 
					if(usersAuthInfo.getMobile() != null)
						sendOtpOnMobile(usersAuthInfo.getMobile(), otp);
					break;
				case "email" :
						sendOtpOnEmail(usersAuthInfo.getUsername().trim(), otp, usersAuthInfo.getEmailIsSubscribe());
					break;
				case "both" : 
						sendOtpOnEmail(usersAuthInfo.getUsername().trim(), otp, usersAuthInfo.getEmailIsSubscribe());
					if(usersAuthInfo.getMobile() != null)
						sendOtpOnMobile(usersAuthInfo.getMobile(), otp);
					break;
				}
				parentValidationResp.setStatus("OTP sent");
				parentValidationResp.setStatusCode(200);
			}else{
				parentValidationResp.setStatus("This user doesn't exist in system. Please check and try again.");
				parentValidationResp.setStatusCode(422);
			}
		}catch(Exception e){
			logger.error("Error occurred during generate OTP and send on device mobile or email. "+e.getMessage());
			parentValidationResp.setStatusCode(500);
			parentValidationResp.setStatus("Failed to send OTP");
		}
		return parentValidationResp;
	}
	
	/**This method used for create the OTP (i.e. temporary one time password)**/
	private String createOTP(){
		int randomPin   =(int)(Math.random()*9000)+1000;
		return String.valueOf(randomPin);
	}
	
	/**This method used for send the generated OTP on registered mobile number**/
	private void sendOtpOnMobile(String mobileNumber, String otp){
		Message message = new Message();
		List<MessageEnvelope> messageEnvelopeList = new ArrayList<MessageEnvelope>();
		MessageEnvelope messageEnvelope = new MessageEnvelope();
		messageEnvelope.setDestinationNumber(mobileNumber);
		messageEnvelope.setMessage(smsOtpMessage.replace("<<otp>>", otp));
		messageEnvelopeList.add(messageEnvelope);
		message.setMessageEnvelopes(messageEnvelopeList);
		message.setMessageType(smsOtpMsgType);
		restTemplate.postForObject(smsSendURL, message, String.class);
	}
	
	/**This method used for send the generated OTP on registered email address**/
	private void sendOtpOnEmail(String email, String otp, Boolean emailIsSubscribe){
		List<UserActivationNotification> adminInfoList= new ArrayList<UserActivationNotification>();
		UserActivationNotification adminInfo = new UserActivationNotification();
		adminInfo.setEmail(email);
		adminInfo.setToken(otp);
		adminInfoList.add(adminInfo);
		NotificationRequest notificationRequest = new NotificationRequest();
		if(emailIsSubscribe != null && adminInfoList.size() > 0 && emailIsSubscribe){
			notificationRequest.setUsers(adminInfoList);
			/**Call API to send the notification**/
			restTemplate.postForObject(emailSendOTPURL, notificationRequest, String.class);
		}
	}
	
	/**
	 * This method used for validate the OTP and register the parent device.
	 * @Param UserDetails
	 * @return ParentValidationResp
	 *
	 */
	@Override
	public ParentValidationResp validateOTP(UserDetails user) {
		ParentValidationResp parentValidationResp = new ParentValidationResp();
		try{
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsername(user.getUsername());
			if(usersAuthInfo != null)
				if(usersAuthInfo.getOtp() != null && user.getOtp().equalsIgnoreCase(usersAuthInfo.getOtp()) 
					&& ((user.getIsMobileApp() != null && user.getIsMobileApp()) || user.getfToken().equalsIgnoreCase(usersAuthInfo.getfToken()))){
				ParentDeviceInfo parentDeviceInfo = parentDeviceInfoRepository.findByDeviceDetailsAndUsername(user.getDeviceId(),
						user.getUsername());
				if(parentDeviceInfo == null){
					parentDeviceInfo = new ParentDeviceInfo();
					parentDeviceInfo.setDeviceDetails(user.getDeviceId());
					parentDeviceInfo.setUsername(user.getUsername());
					parentDeviceInfo.setDeviceIP(user.getDeviceIP());
					parentDeviceInfo.setCreatedBy(user.getUsername());
					parentDeviceInfo.setCreatedOn(new Date());
					parentDeviceInfoRepository.save(parentDeviceInfo);
					Set<ParentUser> parentUsers = studentUserRepository.findUsersByEmail(user.getUsername());
					for(ParentUser parentUser : parentUsers){
						if(parentUser.getIsParentRegistered() == null || !parentUser.getIsParentRegistered()){
							parentUser.setIsParentRegistered(true);
							entityManager.merge(parentUser);
						}
					}
					parentValidationResp.setStatus("Device registered successfully");
					parentValidationResp.setStatusCode(200);
				}else{
					parentValidationResp.setStatus("Device already registered.");
					parentValidationResp.setStatusCode(200);
				}
				usersAuthInfo.setOtp(null);
				usersAuthInfoRepository.save(usersAuthInfo);
				parentValidationResp.setfToken(usersAuthInfo.getfToken());
			}else{
				parentValidationResp.setStatus("OTP not matched");
				parentValidationResp.setStatusCode(417);
			}
		}catch(Exception e){
			logger.error("Error occurred during device registration. "+e.getMessage());
			parentValidationResp.setStatus("Failed to register device");
			parentValidationResp.setStatusCode(500);
		}
		return parentValidationResp;
	}

	/**This method used for self register to the parent**/
	@Override
	public ServiceResponse selfRegParent(String emailId) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(emailId, "ROLE_PARENT");
			int status = 0;
			if(usersAuthInfo != null){
				if(usersAuthInfo.getfToken() != null/* && usersAuthInfo.getEmailIsSubscribe() != null && usersAuthInfo.getEmailIsSubscribe()*/){
					List<UserActivationNotification> notificationInfos = new ArrayList<UserActivationNotification>();
					UserActivationNotification notificationInfo = new UserActivationNotification();
					notificationInfo.setEmail(emailId);		
					notificationInfo.setToken(parentUserActivationLink(emailId, usersAuthInfo.getfToken()));
					notificationInfo.setAdminEmail("NA");
					notificationInfos.add(notificationInfo);
					NotificationRequest notificationRequest = new NotificationRequest();
					notificationRequest.setUsers(notificationInfos);
					/**Call API for send the notification**/
					restTemplate.postForObject(notificationParentURL, notificationRequest, String.class);
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatusMessage("Registration invite has been sent successfully on your email id!!");
					status = 1;
				}else{					
					serviceResponse.setStatusCode(200);
					serviceResponse.setStatusMessage("Not valid user.");
				}
			}else{
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Entry not found.");
			}
			Query query = entityManager.createNativeQuery("Insert into requestedemails(emailId, requestedTime, linkSendStatus) " +
		            " values (?,?,?)");
			query.setParameter(1, emailId);
	        query.setParameter(2, new Date());
	        query.setParameter(3, status);
	        query.executeUpdate();
			serviceResponse.setStatus("Success");
		}catch(Exception e){
			logger.error("Error occurred during self register to parent "+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusMessage("Failed to register parent. Please try again later.");
		}
		return serviceResponse;
	}
	
	private String parentUserActivationLink(String userName, String token){
		String domainName = "https://"+mealManageAppSubdomain+"."+ env.getProperty("spring.mail.domainName");
		return  domainName + "activateParentAccount?parentId=" + userName+"&token="+token;
	}

	@Override
	/**This method used for update the stripe account id in meal school**/
	public void updateMealSchoolStripe(MealSchool mealSchool) {
		mealSchool.setModifiedBy("StripeAccountUpdate");
		mealSchool.setModifiedOn(new Date());
		entityManager.merge(mealSchool);	
		logger.info("Stripe account id updated successfully in meal school table");
	}

}
