package com.mealManage.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mealManage.dao.LoginDao;
import com.mealManage.domain.NotificationRequest;
import com.mealManage.domain.OAuthTokens;
import com.mealManage.domain.UserActivationNotification;
import com.mealManage.mealmodel.caterer.Caterer;
import com.mealManage.mealmodel.caterer.CatererUser;
import com.mealManage.mealmodel.repository.CatererRepository;
import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.DistrictRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.ParentDeviceInfoRepository;
import com.mealManage.mealmodel.repository.SchoolSessionRepo;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.repository.SuperAdminUserRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.CountryDetail;
import com.mealManage.mealmodel.school.District;
import com.mealManage.mealmodel.school.DistrictUser;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.user.ParentDeviceInfo;
import com.mealManage.mealmodel.user.SchoolUser;
import com.mealManage.mealmodel.user.SuperAdminUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.response.ParentValidationResp;
import com.mealManage.response.ResponseDetails;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.UserDetails;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.PBKDF2Utility;
import com.mealManage.util.SendNotificationUtil;
import com.mealManage.util.StripeUtil;

/** This class implement by LoginService interface for login related APIs **/
@Service
@Transactional
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginDao loginDao;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	
	@Autowired
	private SuperAdminUserRepository superAdminUserRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private StripeUtil stripeUtil;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	@Autowired
	private ParentDeviceInfoRepository parentDeviceInfoRepository;
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private CatererRepository catererRepository;
	@Autowired
	private DistrictRepository districtRepository;
	@Autowired
	private StudentUserRepository studentUserRepository;
	
	@Value("${reset.pwd.email.url}")
	private String notificationURL;
	@Value("${spring.mealmanage.subdomain}")
	private String mealManageAppSubdomain;
	@Value("${caterer.domain}")
	private String catererDomain;
	@Value("${auth.token.url}")
	private String authUrl;
	@Value("${server.port}")
	private String serverPort;
	@Autowired
	private MealManageAPIService mealManageAPIService;
	@Autowired
	private SchoolSessionRepo sessionRepo;
	private static Map<String, Boolean> isPassAuthByUsr = new HashMap<>();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	/**
	 * This method used for send the password recovery link to user on registered email address
	 */
	public ServiceResponse generateforgotPasswordLink(UserDetails userRegInfo) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			UsersAuthInfo userData = usersAuthInfoRepository.findByUsername(userRegInfo.getUsername());
			Boolean activeStatus = false;
			String subdomain = null;
			Long userId = (long) 0;
			if(userData != null){
			if(userData.getRole().equalsIgnoreCase("ROLE_ADMIN")){
				SchoolUser schoolUser = mealSchoolRepository.schoolUser(userRegInfo.getUsername());
				activeStatus = schoolUser.getIsActive();
				if(activeStatus)
					activeStatus = schoolUser.getIsVerified();
				MealSchool mealSchool = mealSchoolRepository.findBySchoolUsersUsername(userRegInfo.getUsername());
				subdomain = mealSchool.getSubdomain();
				userId = schoolUser.getUserId();
			}else if(userData.getRole().equalsIgnoreCase("ROLE_SUPERADMIN")){
				SuperAdminUser superAdminUser = superAdminUserRepository.findByUsername(userRegInfo.getUsername());
				activeStatus = superAdminUser.getIsActive();
			}else if(userData.getRole().equalsIgnoreCase("ROLE_CATERER")){
				activeStatus = true;
				subdomain = catererDomain;
				userId = catererRepository.getCatererUserId(userData.getUsername());
			}
			if(activeStatus){
				serviceResponse = generateAndInsertForgotPassowrdToken(userData);
				if (serviceResponse.getStatusCode() == 200) {
					List<UserActivationNotification> adminInfoList= new ArrayList<UserActivationNotification>();
					UserActivationNotification adminInfo = new UserActivationNotification();
					String resetPasswordLink = buildResetPasswordLink(userData, serviceResponse.getResetPwdLink(), subdomain, userId);
					adminInfo.setEmail(userRegInfo.getUsername());
					adminInfo.setToken(resetPasswordLink);
					adminInfoList.add(adminInfo);	
					NotificationRequest notificationRequest = new NotificationRequest();
					if(adminInfoList.size() > 0){
					notificationRequest.setUsers(adminInfoList);
					/**Call API to send the notification**/
					restTemplate.postForObject(notificationURL, notificationRequest, String.class);
					serviceResponse.setStatusMessage("Password reset link generated and sent through email.");
					serviceResponse.setResetPwdLink(resetPasswordLink);
					}
				}
			}else{
				logger.info("User account is inActivate. Please contact to Super Admin.");
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Account is deactivated.");
			}
			}else{
				logger.info("There are no record exist in system for this user."+userRegInfo.getUsername());
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatusMessage("Entry not found.");
			}
		} catch (Exception e) {
			logger.error("Failed to generate link for reset password to the user  ::: "+userRegInfo.getUsername()+"due to" + e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusMessage("Failed to generate link for reset password.");
		}
		return serviceResponse;
	}

	/** This method used for generate and insert the forgot password token info**/
	private ServiceResponse generateAndInsertForgotPassowrdToken(UsersAuthInfo user) {
		String token = UUID.randomUUID().toString();
		user.setfToken(token);
		return loginDao.inserForgotPasswordTokenInDB(user);
	}
	
	/**
	 * This method used to validate the token based on expire time and then allow the user to change the password.
	 * @param UserDetails
	 * @return ServiceResponse
	 */
	@Override
	public ServiceResponse recoveryPassword(UserDetails user, Boolean isCaterer, Boolean isDistrict) {
		ServiceResponse serviceResponse = new ServiceResponse();
		boolean status = false;
		try {
			PBKDF2Utility pBKDF2Utility = new PBKDF2Utility();
			try{
				if(isCaterer != null && isCaterer){
					if(StringUtils.isNumeric(user.getUsername()))
						user.setUsername(catererRepository.catererUserNameByUserId(Long.parseLong(user.getUsername())));
				}else if(isDistrict != null && isDistrict){
					if(StringUtils.isNumeric(user.getUsername()))
						user.setUsername(districtRepository.districtUserNameByUserId(Long.parseLong(user.getUsername())));
				}else
					if(StringUtils.isNumeric(user.getUsername())){
						SchoolUser schoolUser = mealSchoolRepository.schoolUserByUserId(Long.parseLong(user.getUsername()));
						user.setUsername(schoolUser.getUsername());
					}
			}catch(Exception e){
				
			}			
			UsersAuthInfo userData = usersAuthInfoRepository.findByUsername(user.getUsername());
			if(userData != null){
			if (userData.getfToken() != null && userData.getfToken().equalsIgnoreCase(user.getfToken())) {
				status = pBKDF2Utility.updateForgotPasswordToken(userData.getfTokenTime());
				if (status) {
					userData.setPassword(pBKDF2Utility.encode(user.getPassword()));
					userData.setModifiedBy(userData.getUsername());
					userData.setModifiedOn(new Date());
					int status1 = loginDao.updateForgotPassword(userData);
					if(status1 == 1){
						serviceResponse.setStatus("Success");
						serviceResponse.setStatusMessage("Password updated successfully.");
						serviceResponse.setStatusCode(200);
						if(!userData.getRole().equalsIgnoreCase("ROLE_PARENT")){
							userData.setfToken(null);
							userData.setfTokenTime(null);
							usersAuthInfoRepository.save(userData);
						}
						logger.info("Password updated successfully");
						/*if(userData.getSchoolId() != 0 || userData.getSchoolId() != null){
						mealManageAPIDao.activateAccount(userData.getUsername());
						}*/
						}else{
							serviceResponse.setStatus("Failed");
							serviceResponse.setStatusCode(500);
							serviceResponse.setStatusMessage("Failed to update password.");
							logger.info("Failed to update password for the user "+user.getUsername());
						}
				} else {
					serviceResponse.setUserName(user.getUsername());
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("Reset password link expired.");
				}
			} else {
				serviceResponse.setUserName(user.getUsername());
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Reset passord link is not valid.");
			}
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(422);
				serviceResponse.setStatusMessage("No entry found");
			}
		} catch (Exception e) {
			logger.error("Error occured during update password in forget case by token. "+e.getMessage());
			serviceResponse.setErrorMessage(e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to reset the password.");
			serviceResponse.setStatus("Failed");
		}
		return serviceResponse;
	}

	/**
	 * This method used for reset the user login password.
	 * @param UserDetails
	 * @return ServiceResponse
	 */
	@Override
	public ServiceResponse updatePassword(UserDetails userDetails) {
		ServiceResponse serviceResponse = new ServiceResponse();
		UsersAuthInfo user = usersAuthInfoRepository.findByUsername(userDetails.getUsername());
		if(user != null){
		try {
			PBKDF2Utility pBKDF2Utility = new PBKDF2Utility();
			if(pBKDF2Utility.matches(userDetails.getOldPassword(), user.getPassword())){
				user.setPassword(pBKDF2Utility.encode(userDetails.getPassword()));
				user.setModifiedBy(user.getUsername());
				user.setModifiedOn(new Date());
				int status1 = loginDao.updateForgotPassword(user);
				if(status1 == 1){
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusMessage("password updated successfully.");
				serviceResponse.setStatusCode(200);
				logger.info("Password updated successfully");
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusMessage("Failed to update password.");
				serviceResponse.setStatusCode(500);
				logger.info("Password not updated successfully");
				}
			}else{
				serviceResponse.setStatus("Failed");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("Old password not correct.");
				logger.info("Password not updated due to old password mismatch.");
			}
		} catch (Exception e) {
			logger.error("Error occured during update password. "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to reset password.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		}else{
			logger.error("There are no record exist in system.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(422);
			serviceResponse.setStatusMessage("No entry found.");
		}
		return serviceResponse;
	}

	/** this method used for build the reset password link **/
	private String buildResetPasswordLink(UsersAuthInfo user, String forgotPasswordToken, String subdomain, Long userId) {
		String html = "";
		if(subdomain == null){
			html = "https://"+mealManageAppSubdomain+"."+ env.getProperty("spring.mail.domainName") 
				+ "reset?token=" + forgotPasswordToken + "&" + "uid=" + user.getUsername();
		}else{
			html = "https://"+subdomain+"."  + env.getProperty("spring.mail.domainName")
				+ "reset?token=" + forgotPasswordToken + "&" + "uid=" + userId;
		}
		return html;
	}

	/**This method used for get the user details**/
	@Override
	public ResponseDetails getUserDetails(Long schoolId, String username, Collection<? extends GrantedAuthority> role) {
		ResponseDetails responseDetails=new ResponseDetails();
		try{
			MealSchool mealSchool =  null;
			SchoolUser schoolUserData = null;
			if(role.size() == 1 && role.toString().toUpperCase().contains("ROLE_ADMIN")){
				mealSchool = mealSchoolRepository.findBySchoolUsersUsername(username);
				schoolUserData = mealSchoolRepository.schoolUser(username);
			}else if(schoolId != 0){
				mealSchool = mealSchoolRepository.findBySchoolId(schoolId);
				if(role.toString().toUpperCase().contains("ROLE_DISTRICT")){
					Long distId = districtRepository.getDistrictRecId(username);
					if(distId != mealSchool.getDistrictId()){
						responseDetails.setError("Failed");
						responseDetails.setStatusCode(401);
						responseDetails.setStatus("Login school does not belong to this district.");
						return responseDetails;
					}
				}
				if(role.toString().toUpperCase().contains("ROLE_CATERER")){
					Long catId = catererRepository.getCatererRecId(username);
					if(catId != mealSchool.getCatererId()){
						responseDetails.setError("Failed");
						responseDetails.setStatusCode(401);
						responseDetails.setStatus("Login school does not belong to this Caterer.");
						return responseDetails;
					}
				}
			}
			
			if(mealSchool != null){
					if(schoolUserData != null){
						responseDetails.setFirstName(schoolUserData.getFirstName());
						responseDetails.setLastName(schoolUserData.getLastName());
						responseDetails.setMobileNo(schoolUserData.getMobileNo());
						responseDetails.setUsername(schoolUserData.getUsername());
						responseDetails.setPin(schoolUserData.getPin());
						responseDetails.setRole(schoolUserData.getRole());
						responseDetails.setUserId(schoolUserData.getUserId());
					}else{
						responseDetails.setLoginAsAdmin(true);
						if(role.toString().toUpperCase().contains("ROLE_DISTRICT")){
							District district = districtRepository.findByDistrictUsersUsername(username);
							DistrictUser distUser = null;
							if(district != null){						
								for(DistrictUser usr : district.getDistrictUsers()){
									if(usr.getUsername().equalsIgnoreCase(username)){
										distUser = usr;
										continue;
									}
								}
								if(distUser != null){
									responseDetails.setFirstName(distUser.getFirstName());
									responseDetails.setLastName(distUser.getLastName());
									responseDetails.setMobileNo(distUser.getMobileNo());
									responseDetails.setUsername(distUser.getUsername());
									responseDetails.setMessage("login successfully");
									responseDetails.setStatusCode(200);
									responseDetails.setRole(distUser.getRole());
									responseDetails.setUserId(distUser.getUserId());
									responseDetails.setDistrictId(district.getId());
								}
							}
						}else if(role.toString().toUpperCase().contains("ROLE_CATERER")){
							Caterer caterer = catererRepository.findByCatererUsersUsername(username);
							CatererUser catererUser = null;
							for(CatererUser usr : caterer.getCatererUsers()){
								if(usr.getUsername().equalsIgnoreCase(username)){
									catererUser = usr;
									continue;
								}
							}
							if(catererUser != null){
								responseDetails.setFirstName(catererUser.getFirstName());
								responseDetails.setLastName(catererUser.getLastName());
								responseDetails.setMobileNo(catererUser.getMobileNo());
								responseDetails.setUsername(catererUser.getUsername());
								responseDetails.setMessage("login successfully");
								responseDetails.setStatusCode(200);
								responseDetails.setRole(catererUser.getRole());
								responseDetails.setUserId(catererUser.getUserId());
							}
						}else if(role.toString().toUpperCase().contains("ROLE_SUPERADMIN")){
							SuperAdminUser superAdminUser = superAdminUserRepository.findByUsername(username);
							if(superAdminUser != null){
									responseDetails.setFirstName(superAdminUser.getFirstName());
									responseDetails.setLastName(superAdminUser.getLastName());
									responseDetails.setMobileNo(superAdminUser.getMobileNo());
									responseDetails.setUsername(superAdminUser.getUsername());
									responseDetails.setMessage("login successfully");
									responseDetails.setStatusCode(200);
									responseDetails.setRole(superAdminUser.getRole());
									responseDetails.setUserId(superAdminUser.getUserId());
							}
						}
					}
					/*responseDetails.setSupportSIS(mealSchool.isSupportSIS());
					responseDetails.setSupportBCPrg(mealSchool.isSupportBCPrg());
					responseDetails.setSupportFreeReducedPrg(mealSchool.isSupportFreeReducedPrg());
					responseDetails.setSchoolProvideBreakfast(mealSchool.isSchoolProvideBreakfast());
					responseDetails.setMenuByYear(mealSchool.isMenuByYear());
					responseDetails.setSupportStaffLunch(mealSchool.isSupportStaffLunch());
					responseDetails.setSupportInstantPayment(mealSchool.isSupportInstantPayment());*/
					responseDetails.setTierName(mealSchool.getTierName());
					responseDetails.setModuleAccess(mealSchool.getModuleAccess());
					responseDetails.setTrxFeeOnSchool(mealSchool.isTrxFeeOnSchool());
					responseDetails.setPaymentGateways(mealSchool.getPaymentGateways());
					responseDetails.setSchoolSessions(sessionRepo.findByMealSchoolId(mealSchool.getSchoolId()));
					if(mealSchool.getSchoolId() != null)
					responseDetails.setSchoolId(mealSchool.getSchoolId().toString());
					//responseDetails.setAuthToken(authToken);
					responseDetails.setMessage("Login successfully");
					if(mealSchool.getSchool() != null){
						responseDetails.setCtds(mealSchool.getSchool().getCtds());
						responseDetails.setSchoolName(mealSchool.getSchool().getSchoolName());
						responseDetails.setSchoolAddress(mealSchool.getSchool().getSchoolAddress());
						responseDetails.setCityStateZip(mealSchool.getSchool().getCityStateZip());
						responseDetails.setCounty(mealSchool.getSchool().getCounty());
						responseDetails.setSchoolSubDomain(mealSchool.getSubdomain());
						CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(mealSchool.getCountryCode());
						responseDetails.setCurrencySymbol(countryDetail.getCurrencySymbol());
						responseDetails.setIsdCode(countryDetail.getIsdCode());
						responseDetails.setSchoolTimeZone(mealSchool.getSchoolTimezone().toString());
						responseDetails.setDateFormat(countryDetail.getDateFormat() != null ? countryDetail.getDateFormat() : "MM/dd/yyyy");
						responseDetails.setCountryCode(mealSchool.getCountryCode());
						responseDetails.setPageSize(countryDetail.getPageSize());
						responseDetails.setPhoneValidation(countryDetail.getPhoneValidation());
						/*List<String> gradesList1 = new ArrayList<String>();
						for(SchoolType type : mealSchool.getSchool().getSchoolType()){
							gradesList1.addAll(type.getValues());
						}*/
						//responseDetails.setSchoolOtherInfo(buildSchoolGrade(countryDetail.getOtherInfoJson(), gradesList1));
						responseDetails.setSchoolOtherInfo(countryDetail.getOtherInfoJson());
						responseDetails.setGradesMap(countryDetail.getGradesMap());
						if(mealSchool.getContactPEmail() == null || mealSchool.getContactPEmail().trim().equalsIgnoreCase(""))
							responseDetails.setIsContactDetailsReq(true);
						else
							responseDetails.setIsContactDetailsReq(false);
						
						responseDetails.setNonSchoolDays(CommonUtil.getNonSchoolDays(mealSchool));
					}
					responseDetails.setCatererId(mealSchool.getCatererId());
					responseDetails.setDistrictId(mealSchool.getDistrictId());
					responseDetails.setModuleDetails(mealManageAPIService.getModulesByType("Regular School"));
					responseDetails.setStatusCode(200);
			}else{
				SuperAdminUser superAdminUser = superAdminUserRepository.findByUsername(username);
				if(superAdminUser != null){
						responseDetails.setFirstName(superAdminUser.getFirstName());
						responseDetails.setLastName(superAdminUser.getLastName());
						responseDetails.setMobileNo(superAdminUser.getMobileNo());
						responseDetails.setUsername(superAdminUser.getUsername());
						responseDetails.setMessage("login successfully");
						responseDetails.setStatusCode(200);
						responseDetails.setRole(superAdminUser.getRole());
						responseDetails.setUserId(superAdminUser.getUserId());
				}else{
					District district = districtRepository.findByDistrictUsersUsername(username);
					DistrictUser distUser = null;
					if(district != null){						
						for(DistrictUser usr : district.getDistrictUsers()){
							if(usr.getUsername().equalsIgnoreCase(username)){
								distUser = usr;
								continue;
							}
						}
						if(distUser != null){
							responseDetails.setFirstName(distUser.getFirstName());
							responseDetails.setLastName(distUser.getLastName());
							responseDetails.setMobileNo(distUser.getMobileNo());
							responseDetails.setUsername(distUser.getUsername());
							responseDetails.setMessage("login successfully");
							responseDetails.setStatusCode(200);
							responseDetails.setRole(distUser.getRole());
							responseDetails.setUserId(distUser.getUserId());
							//responseDetails.setCatering(catering);
							responseDetails.setDistrictId(district.getId());
							List<MealSchool> mealSchools = mealSchoolRepository.findByDistrictId(district.getId());
							responseDetails.setSchools(mealSchools);
							CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(district.getCountryCode());
							responseDetails.setCurrencySymbol(countryDetail.getCurrencySymbol());
							responseDetails.setIsdCode(countryDetail.getIsdCode());
							responseDetails.setCountryCode(countryDetail.getCountryCode());
							responseDetails.setDateFormat(countryDetail.getDateFormat());
							responseDetails.setSchoolTimeZone(district.getTimezone());
							responseDetails.setPageSize(countryDetail.getPageSize());
							responseDetails.setSchoolOtherInfo(countryDetail.getOtherInfoJson());
							responseDetails.setGradesMap(countryDetail.getGradesMap());
						}else{
							responseDetails.setError("Failed");
							responseDetails.setStatusCode(422);
							responseDetails.setStatus("Entry not found");
						}	
					}else{
						Caterer caterer = catererRepository.findByCatererUsersUsername(username);
						CatererUser catererUser = null;
						for(CatererUser usr : caterer.getCatererUsers()){
							if(usr.getUsername().equalsIgnoreCase(username)){
								catererUser = usr;
								continue;
							}
						}
						if(catererUser != null){
							responseDetails.setFirstName(catererUser.getFirstName());
							responseDetails.setLastName(catererUser.getLastName());
							responseDetails.setMobileNo(catererUser.getMobileNo());
							responseDetails.setUsername(catererUser.getUsername());
							responseDetails.setMessage("login successfully");
							responseDetails.setStatusCode(200);
							responseDetails.setRole(catererUser.getRole());
							responseDetails.setUserId(catererUser.getUserId());
							//responseDetails.setCatering(catering);
							responseDetails.setCatererId(caterer.getId());
							List<MealSchool> mealSchools = mealSchoolRepository.findByCatererId(caterer.getId());
							responseDetails.setSchools(mealSchools);
							CountryDetail countryDetail = countryDetailsRepository.findByCountryCode(caterer.getCountryCode());
							responseDetails.setCurrencySymbol(countryDetail.getCurrencySymbol());
							responseDetails.setIsdCode(countryDetail.getIsdCode());
							responseDetails.setCountryCode(countryDetail.getCountryCode());
							responseDetails.setDateFormat(countryDetail.getDateFormat());
							responseDetails.setSchoolTimeZone(caterer.getTimezone());
							responseDetails.setPageSize(countryDetail.getPageSize());
							responseDetails.setSchoolOtherInfo(countryDetail.getOtherInfoJson());
							responseDetails.setGradesMap(countryDetail.getGradesMap());
						}else{
							responseDetails.setError("Failed");
							responseDetails.setStatusCode(422);
							responseDetails.setStatus("Entry not found");
						}					
					
					}
				}	
			}
		}catch(Exception e){
			logger.error("Error occured during login user. "+e.getMessage());
			responseDetails.setError("Failed");
			responseDetails.setStatusCode(500);
			responseDetails.setErrorCode(e.getMessage());
			responseDetails.setStatus("Login failed.");
		}
		return responseDetails;
	}

	/**
	 * This method used for remove the user access token.
	 * @param tokenValue
	 * @return Boolean
	 */
	@Override
	public boolean revokeToken(String tokenValue) {
		try{
			 OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
			// accessToken.getExpiration().setTime(1600);
		        if (accessToken == null) {
		            return false;
		        }
		        if (accessToken.getRefreshToken() != null) {
		            tokenStore.removeRefreshToken(accessToken.getRefreshToken());
		        }
		        tokenStore.removeAccessToken(accessToken);
		        return true;
		}catch(Exception e){
			return false;
		}
   }
	
	/**This method used for create Super admin user**/
	@Override
	public ServiceResponse addSuperAdmin(SuperAdminUser users) {
		String status="";
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			users.setCreatedBy(users.getLoggedUser());
			users.setCreatedOn(new Date());
			status = loginDao.addSuperAdmin(users);
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage(status);
			serviceResponse.setStatus("Success");
		}catch(Exception e){
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to create Super Admin User.");
			logger.error("Error occured during Super Admin User creation. "+e.getMessage());
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	
	/**
	 * This method used for validate the parent email id, token and device info
	 * @param UserDetails
	 * @return ParentValidationResp
	 * @throws Exception 
	 */
	@Override
	public ParentValidationResp validateParent(UserDetails user) throws Exception {
		ParentValidationResp parentValidationResp = new ParentValidationResp();
		try {
			UsersAuthInfo users = usersAuthInfoRepository.findByUsername(user.getUsername());
			if(users != null){
			if ((user.getIsMobileApp() != null && user.getIsMobileApp()) || users.getfToken().equalsIgnoreCase(user.getfToken())) {
				parentValidationResp = loginDao.validateParent(user, users.getMobile());
				if(parentValidationResp.getStatus().equalsIgnoreCase("Authenticated Successfully"))
					parentValidationResp.setfToken(users.getfToken());
			} else {
					parentValidationResp.setStatus("Authentication Failed");
					parentValidationResp.setStatusCode(417);
					throw new Exception("Authentication Failed");
				}
			}else{
				parentValidationResp.setStatus("Authentication Failed");
				parentValidationResp.setStatusCode(422);
				throw new Exception("Authentication Failed");
			}
		} catch (Exception e) {
			logger.error("Error occured during parent signup by email and shared token. "+e.getMessage());
			parentValidationResp.setStatusCode(500);
			parentValidationResp.setStatus("Authentication Failed");
			throw new Exception("Authentication Failed");
		}
		return parentValidationResp;
	}

	/**This method used for validate the parent & device info**/
	@Override
	public Map<String, Object> validateParentAndDevice(String userEmailId, String deviceId) {
		Map<String, Object> resp = new HashMap<String, Object>();
		try{
			UsersAuthInfo users = usersAuthInfoRepository.findByUsernameAndRole(userEmailId,"ROLE_PARENT");
			if(users != null){
				ParentDeviceInfo parentDeviceInfo = parentDeviceInfoRepository.findByDeviceDetailsAndUsername(deviceId,
						userEmailId);
				if(parentDeviceInfo != null){
					resp.put("statusCode", 200);
					resp.put("statusMessage", "User verified successfully.");
					resp.put("token", users.getfToken());
				}else{
					/*ServiceResponse serviceResponse = loginDao.selfRegParent(userEmailId);
					resp.put("statusCode", serviceResponse.getStatusCode());
					resp.put("statusMessage", serviceResponse.getStatusMessage());*/
					resp.put("statusCode", 200);
					resp.put("statusMessage", "User device does not register yet!!");
					resp.put("token", users.getfToken());
				}
			}else{
				resp.put("statusCode", 417);
				resp.put("statusMessage", "User email id doesn't exist!!");
			}
		}catch(Exception e){
			resp.put("statusCode", 500);
			resp.put("statusMessage", "Failed to validate the user!!");
			resp.put("errorMessage", e.getMessage());
		}
		return resp;
	}
	
	/**
	 * This method used for generate the OPT and send it on registered mobile number or email id or both
	 * @param UserDetails
	 * @return ParentValidationResp
	 * @throws Exception 
	 */
	@Override
	public ParentValidationResp generateOTP(UserDetails user) throws Exception {
		return loginDao.generateOTP(user);
	}
	
	/**
	 * This method used for validate the OPT and register the parent device
	 * @param UserDetails
	 * @return ParentValidationResp
	 * @throws Exception 
	 */
	@Override
	public ParentValidationResp validateOTP(UserDetails user) throws Exception {
		return loginDao.validateOTP(user);
	}

	/**This method used for register the parent by parent user**/
	@Override
	public ServiceResponse selfRegParent(String emailId) {
		return loginDao.selfRegParent(emailId);
	}

	/**This method used for complete the stripe account setup and update the required details in backend**/
	@Override
	public ServiceResponse completeStripeAccSetup(String authCode) {
		ServiceResponse serviceResponse = new ServiceResponse();		 
		try{
			Map<String, String> stripeAccResp = stripeUtil.stripeAccSetupComplete(authCode);
			if(stripeAccResp != null){
				if(stripeAccResp.get("userEmail") == null || stripeAccResp.get("userEmail").trim().isEmpty()){
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("Failed to complete the payment setup, Please give valid email id during payment setup!!");
					return serviceResponse;
				}
				Long mealSchoolId = mealSchoolRepository.findSchoolIdByUsername(stripeAccResp.get("userEmail"));
				if (mealSchoolId == null || mealSchoolId == 0) {
					serviceResponse.setStatus("Failed");
					serviceResponse.setStatusCode(417);
					serviceResponse.setStatusMessage("Failed to complete the payment setup, Please make sure that you are using same email id on which you receive this setup link!!");
					return serviceResponse;
				}
				MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
				mealSchool.setStripeAccountId(stripeAccResp.get("stripeAccId"));
				loginDao.updateMealSchoolStripe(mealSchool);
				Map<String, String> notificationReq = new HashMap<String, String>();
				notificationReq.put("adminEmails", stripeAccResp.get("userEmail"));
				notificationReq.put("schoolName", mealSchool.getSchoolName());
				notificationReq.put("status", "Success");
				notificationReq.put("stripeLoginLink", "https://dashboard.stripe.com/login");
				logger.info("Sending email to the admin user regarding stripe account setup completed successfully");
				sendNotificationUtil.stripeAccSetupStatus(notificationReq);
				serviceResponse.setSubdomain(mealSchool.getSubdomain());
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Stripe account setup has been completed successfully and sent access link on registered email.");
				logger.info(serviceResponse.getStatusMessage());
			}else{
				logger.error("There are no valid entry for stripe account");
				throw new Exception("No entry found");
			}			
		}catch(Exception e){
			logger.error("Failed to complete the stripe account setup due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusMessage("Failed to complete the stripe account setup. Please contact your administrator.");
			serviceResponse.setStatusCode(500);
			serviceResponse.setErrorMessage(e.getMessage());
			/*Map<String, String> notificationReq = new HashMap<String, String>();
			notificationReq.put("adminEmails", stripeAccResp.get("userEmail"));
			notificationReq.put("schoolName", mealSchool.getSchoolName());
			notificationReq.put("status", "Success");
			notificationReq.put("stripeLoginLink", stripeAccResp.get("stripeAccessLink"));
			logger.info("Sending email to the admin user regarding stripe account setup completed successfully");
			sendNotificationUtil.stripeAccSetupStatus(notificationReq);*/
		}
		return serviceResponse;
	}

	/**This method used for validate the user registration link**/
	@Override
	public boolean isLinkValidate(String token, String userId, Boolean isCaterer, Boolean isDistrict) {
		boolean isValid = false;
		try {
			PBKDF2Utility pBKDF2Utility = new PBKDF2Utility();
			try{
				if(isCaterer != null && isCaterer){
					if(StringUtils.isNumeric(userId))
						userId = catererRepository.catererUserNameByUserId(Long.parseLong(userId));
				}else if(isDistrict != null && isDistrict){
					if(StringUtils.isNumeric(userId))
						userId = districtRepository.districtUserNameByUserId(Long.parseLong(userId));
				}else
					if(StringUtils.isNumeric(userId)){
						SchoolUser schoolUser = mealSchoolRepository.schoolUserByUserId(Long.parseLong(userId));
						userId = schoolUser.getUsername();
					}
			}catch(Exception e){}			
				UsersAuthInfo userData = usersAuthInfoRepository.findByUsername(userId);
			if (userData != null) {
				if (userData.getfToken() != null && userData.getfToken().equalsIgnoreCase(token))
					isValid = pBKDF2Utility.updateForgotPasswordToken(userData.getfTokenTime());
			}
		} catch (Exception e) {
			isValid = false;
		}
		return isValid;
	}
	
	/**This method used for build the school required grade**/
	/*private Map<String, Object>  buildSchoolGrade(Map<String, Object> countrySetting, List<String> gradesList1){
		@SuppressWarnings("unchecked")
		Map<String, String> gradeMap = (Map<String, String>) countrySetting.get("grade");
		Map<String, String> finalGrade = new HashMap<String, String>();
		for(String grade : gradesList1){
			if(gradeMap.get(grade) != null){
				finalGrade.put(grade, gradeMap.get(grade));
			}
		}
		countrySetting.put("grade", finalGrade);
		return countrySetting;
	}*/

	/**This method used for validate email id**/
	@Override
	public ServiceResponse emailValidate(String email, String type) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse.setStatusCode(417);
			serviceResponse.setStatus("Failed");
			if(mealSchoolRepository.findSchoolByUsername(email) != null)
				serviceResponse.setStatusMessage("This email id already used for one of the Admin user!!");
			else if(type.equalsIgnoreCase("Caterer") && catererRepository.findByCatererUsersUsername(email) != null)
				serviceResponse.setStatusMessage("This email id already used for one of the Caterer user!!");
			else if(type.equalsIgnoreCase("District") && districtRepository.findByDistrictUsersUsername(email) != null)
				serviceResponse.setStatusMessage("This email id already used for one of the District user!!");
			else if(type.equalsIgnoreCase("Parent") && studentUserRepository.findUsersByEmail(email) != null 
					&& studentUserRepository.findUsersByEmail(email).size() > 0)
				serviceResponse.setStatusMessage("This email id already used for one of the Parent user!!");
			else if(usersAuthInfoRepository.findByUsername(email) != null && !type.equalsIgnoreCase("Parent"))
				serviceResponse.setStatusMessage("This email id already used for one of the user!!");
			else{
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Email id is valid!!");
			}
			logger.info(serviceResponse.getStatusMessage()+" with emailId::"+email+" and type::"+type);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to validate email.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" with email::"+email+" and type::"+type+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for validate the subdomain**/
	@Override
	public ServiceResponse subdomainValidate(String subdomain) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse.setStatusCode(417);
			serviceResponse.setStatus("Failed");
			Boolean subdomainStatus = mealSchoolRepository.validateSubdomain(subdomain);
			if(!subdomainStatus)
				serviceResponse.setStatusMessage("This subdomain already used for one of the school!!");			
			else{
				serviceResponse.setStatus("Success");
				serviceResponse.setStatusCode(200);
				serviceResponse.setStatusMessage("Subdomain is valid!!");
			}
			logger.info(serviceResponse.getStatusMessage()+" with subdomain::"+subdomain);
		}catch(Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to validate subdomain.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" with subdomain::"+subdomain+" due to "+e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for authenticate admin user by PIN**/
	@Override
	public ServiceResponse authenticateByPin(Map<String, String> req) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(417);
			if(req.get("username") != null && !req.get("username").trim().isEmpty() && 
					req.get("pin") != null && !req.get("pin").trim().isEmpty()){
				UsersAuthInfo user = usersAuthInfoRepository.findByUsernameAndRole(req.get("username"),"ROLE_ADMIN");
				if(user != null){
					SchoolUser schoolUser = mealSchoolRepository.schoolUser(user.getUsername()); 
					if(schoolUser.getPin() != null && schoolUser.getPin().equalsIgnoreCase(req.get("pin"))){
						isPassAuthByUsr.put(user.getUsername().toLowerCase(), true);
						req.put("password", user.getPassword());
						OAuthTokens oAuthTokens = getAuthTokenInfo(req);
						isPassAuthByUsr.put(user.getUsername().toLowerCase(), null);
						serviceResponse.setResponse(oAuthTokens);
						serviceResponse.setStatus("Success");
						serviceResponse.setStatusCode(200);
						serviceResponse.setStatusMessage("Admin user authenticated successfully.");
					}else
						serviceResponse.setStatusMessage("Passcode is not valid!!");					
				}else
					serviceResponse.setStatusMessage("User doesn't exist.");
			}else{
				if(req.get("username") == null || req.get("username").trim().isEmpty())
					serviceResponse.setStatusMessage("Userame is missing.");
				else
					serviceResponse.setStatusMessage("Passcode is missing.");
			}
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			isPassAuthByUsr.put(req.get("username"), null);
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to authenticate admin user by PIN.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" for username::"+req.get("username")+" and PIN::"+req.get("pin")+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for get the oauth token info**/
	private OAuthTokens getAuthTokenInfo(Map<String, String> req){
		OAuthTokens oAuthTokens = new OAuthTokens();
		OAuth2AccessToken accessToken = getOAuthTokens(req);
		oAuthTokens.setAccess_token(accessToken.toString());
		oAuthTokens.setRefresh_token(accessToken.getRefreshToken().toString());
		oAuthTokens.setExpires(accessToken.getExpiresIn());
		oAuthTokens.setScope(accessToken.getScope().toString());
		return oAuthTokens;
	}
	
	/**This method used for generate the access and refresh token using device id**/
	private OAuth2AccessToken getOAuthTokens(Map<String, String> req) {
		ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
	    resourceDetails.setUsername(req.get("username"));
	    resourceDetails.setPassword(req.get("password"));
	    resourceDetails.setAccessTokenUri((authUrl.replace("<<port>>", serverPort)));
	    resourceDetails.setClientId(req.get("clientId"));
	    resourceDetails.setClientSecret(req.get("secretId"));
	    resourceDetails.setGrantType("password");
	    resourceDetails.setScope(Arrays.asList("read", "write"));
	    DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();
	    OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
	    restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
	    OAuth2AccessToken accessToken=restTemplate.getAccessToken();
	    return accessToken;
	}
	
	public Boolean getPassAuthStatus(String username){
		return isPassAuthByUsr.get(username);
	}
}
