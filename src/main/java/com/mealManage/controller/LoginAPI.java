package com.mealManage.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.user.SuperAdminUser;
import com.mealManage.response.ParentValidationResp;
import com.mealManage.response.ResponseDetails;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.UserDetails;
import com.mealManage.service.LoginService;
import com.mealManage.service.ManageMenuService;


/**
 * An {@link LoginAPI} implementation services for Login and forget password from a LoginServiceImpl
 *
 */
@RestController
@RequestMapping("mealManage")
@CrossOrigin
public class LoginAPI {
	
	@Autowired
	private LoginService loginService;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Autowired
	private ManageMenuService manageMenuService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * This API used for send the password recovery link to user on registered email address
	 * @param UserDetails
	 * @return ServiceResponse
	 */
	@PostMapping("forgotPassword")
	public ResponseEntity<ServiceResponse>  forgotPassword(@RequestBody UserDetails users) {
		logger.info("Invoking the forgotPassword API");
		ServiceResponse serviceResponse= loginService.generateforgotPasswordLink(users);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**
	 * This API used to allow the user to recover the password.
	 * @param UserDetails
	 * @return ServiceResponse
	 */
	@PostMapping("recoveryPassword")
	public  ResponseEntity<ServiceResponse> recoveryPassword(@RequestBody UserDetails users, @RequestParam(value="isCaterer", required=false) Boolean isCaterer,
			@RequestParam(value="isDistrict", required=false) Boolean isDistrict) {
		logger.info("Invoking the recoveryPassword API");
		ServiceResponse serviceResponse= loginService.recoveryPassword(users, isCaterer, isDistrict);
		return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This method used for validate the admin registration link**/
	@GetMapping("isLinkValidate")
	public boolean isLinkValidate(@RequestParam String token, @RequestParam String userId, @RequestParam(value="isCaterer", required=false) Boolean isCaterer
			, @RequestParam(value="isDistrict", required=false) Boolean isDistrict){
		logger.info("Invoking API for validate the user registration link");
		return loginService.isLinkValidate(token, userId, isCaterer, isDistrict);
	}
	
	
	/**
	 * This API used to allow the user to reset new password.
	 * @param UserDetails
	 * @return ServiceResponse
	 */
	@PostMapping("updatePassword")
	public ResponseEntity<ServiceResponse> updatePassword(@RequestBody UserDetails users){
		logger.info("Invoke the API for update the password.");
		ServiceResponse serviceResponse= loginService.updatePassword(users);
		return new ResponseEntity<ServiceResponse>(serviceResponse ,  HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**
	  * This method used for login the user and return user details.
	  * @return ResponseDetails
	  * @throws Exception 
	  */
	@RequestMapping(value = "login/{schoolId}", method = RequestMethod.GET)
	public ResponseEntity<ResponseDetails> loginAuthenticate(@PathVariable("schoolId") String schoolId, 
			@RequestParam("access_token") String access_token, @RequestParam(value="currentDate", required=false) String currentDate) throws Exception {
		logger.info("Invoked in loginAuthenticate() method of LoginContoller");
		ResponseDetails responseDetails=null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		    String currentUserName = authentication.getName();
		    responseDetails=loginService.getUserDetails(Long.valueOf(schoolId),currentUserName,authentication.getAuthorities());
		    if(responseDetails.getStatusCode() != 200){
	    	      loginService.revokeToken(access_token);
		    	return new ResponseEntity<ResponseDetails>(responseDetails ,HttpStatus.valueOf(responseDetails.getStatusCode()));
		    }
		    if(currentDate != null && !currentDate.trim().isEmpty()){
		    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    	if(responseDetails.getRole().equalsIgnoreCase("ROLE_ADMIN") || (responseDetails.getLoginAsAdmin() != null && responseDetails.getLoginAsAdmin())){
		    		Integer schoolYear = schoolYearRepository.schoolYearBySchoolAndDate(Long.valueOf(schoolId), sdf.parse(currentDate));
			    	responseDetails.setSchoolYear(schoolYear);
			    	responseDetails.setLatestActiveMonth((manageMenuService.latestActiveMonth(Long.valueOf(schoolId), schoolYear)).get("activeMenuMonth"));
		    	}else if(responseDetails.getSchools() != null){
		    		List<MealSchool> mealSchoolList = new ArrayList<MealSchool>();
		    		for(MealSchool mealSchool : (List<MealSchool>)responseDetails.getSchools()){
		    			Integer schoolYear = schoolYearRepository.schoolYearBySchoolAndDate(Long.valueOf(mealSchool.getSchoolId()), sdf.parse(currentDate));
				    	mealSchool.setActiveSchoolYear(schoolYear);
				    	mealSchool.setSchoolUsers(null);
				    	mealSchool.setSchool(null);
				    	mealSchoolList.add(mealSchool);
		    		}
		    		responseDetails.setSchools(mealSchoolList);
		    	}		    	
		    }
		  
		    /*if(!schoolId.equalsIgnoreCase("0") && (responseDetails.getRole().equalsIgnoreCase("ROLE_SUPERADMIN") 
		    		|| (responseDetails.getRole().equalsIgnoreCase("ROLE_CATERER") || responseDetails.getRole().equalsIgnoreCase("ROLE_DISTRICT")))){
		    	throw new Exception("School ID is not valid.");
		    }*/
		    if(!responseDetails.getRole().equalsIgnoreCase("ROLE_SUPERADMIN") &&
		    		!responseDetails.getRole().equalsIgnoreCase("ROLE_CATERER") && 
		    		!responseDetails.getRole().equalsIgnoreCase("ROLE_DISTRICT") && 
		    		!responseDetails.getSchoolId().equalsIgnoreCase(schoolId)){
		    	      loginService.revokeToken(access_token);
		    	      logger.info("School ID is not valid.");	
		    	      throw new Exception("School ID is not valid.");	    	      
		    }
		}
		return new ResponseEntity<ResponseDetails>(responseDetails ,HttpStatus.valueOf(responseDetails.getStatusCode()));
	}
	
	
	/**This API used for create the new super admin user**/
	@PostMapping("addSuperAdmin")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> addSuperAdmin(@RequestBody SuperAdminUser users){
		ServiceResponse serviceResponse = new ServiceResponse();
		logger.info("Invoking the addSuperAdmin API");
		if(users.getRole().equalsIgnoreCase("ROLE_SUPERADMIN")){
			serviceResponse=loginService.addSuperAdmin(users);
		}else{
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(422);
			serviceResponse.setStatusMessage("User role is invalid.");
		}
		return new ResponseEntity<ServiceResponse>(serviceResponse ,  HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**
	 * This API used for validate the parent email id & device info.
	 */
	@PostMapping("validateParent")
	public ResponseEntity<ParentValidationResp> validateParent(@RequestBody UserDetails users) throws Exception {
		logger.info("Invoking the validateParent API to validate the parent user and device info");
		ParentValidationResp parentValidationResp=loginService.validateParent(users);
		return new ResponseEntity<ParentValidationResp>(parentValidationResp ,  HttpStatus.valueOf(parentValidationResp.getStatusCode()));
	}
	
	/**
	 * This API used for validate the parent email id & device info.
	 */
	@GetMapping("validateParentAndDevice")
	public ResponseEntity<Map<String, Object>> validateParentAndDevice(@RequestParam String userEmailId, @RequestParam String deviceId) throws Exception {
		logger.info("Invoking the validateParentAndDevice API to validate the parent user and device info");
		Map<String, Object> resp = loginService.validateParentAndDevice(userEmailId, deviceId);
		return new ResponseEntity<Map<String, Object>>(resp ,  HttpStatus.valueOf(Integer.valueOf(resp.get("statusCode").toString())));
	}
	
	/**
	 * This API used to generate the OTP and send it on registered mobile number or email id or both.
	 */
	@PostMapping("generateOTP")
	//@PreAuthorize("hasAuthority('ROLE_PARENT') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ParentValidationResp> generateOTP(@RequestBody UserDetails users) throws Exception {
		logger.info("Invoking the generateOTP API to generate the OTP and send on email or mobile or both");
		ParentValidationResp parentValidationResp=loginService.generateOTP(users);
		return new ResponseEntity<ParentValidationResp>(parentValidationResp ,  HttpStatus.valueOf(parentValidationResp.getStatusCode()));
	}
	
	/**This API used for validate the OTP and register the parent device**/
	@PostMapping("validateOTP")
	//@PreAuthorize("hasAuthority('ROLE_PARENT') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ParentValidationResp> validateOTP(@RequestBody UserDetails users) throws Exception {
		logger.info("Invoking the validateOTP API to validate the OTP and register the parent device");
		ParentValidationResp parentValidationResp=loginService.validateOTP(users);
		return new ResponseEntity<ParentValidationResp>(parentValidationResp ,  HttpStatus.valueOf(parentValidationResp.getStatusCode()));
	}
	
	/**
	 * This API used for logout the user.
	 */
	@GetMapping("logout")
	public  Boolean logout(@RequestParam(value="access_token", required=true) String access_token) {
		logger.info("Invoking the logout API");
		return loginService.revokeToken(access_token);
	}
	
	/**This API used for self-register to the parent**/
	@GetMapping("selfRegParent")
	public ResponseEntity<ServiceResponse> selfRegParent(@RequestParam(value="emailId", required = true) String emailId){
		ServiceResponse serviceResponse = loginService.selfRegParent(emailId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for complete the stripe account setup and update the stripe account id in backend**/
	@GetMapping("completeStripeAccSetup")
	public ResponseEntity<ServiceResponse> completeStripeAccSetup(@RequestParam(value="authCode", required = true) 
			String authCode){
		logger.info("Invoking API for complete the stripe account setup and update the details in backend");
		ServiceResponse serviceResponse = loginService.completeStripeAccSetup(authCode);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for validate the email id by type (i.e. Parent/Admin)**/
	@GetMapping("emailValidate")
	public ResponseEntity<ServiceResponse> emailValidate(@RequestParam String email, @RequestParam String type){
		logger.info("Invoking API to validate the user email id::"+email+" and type::"+type);
		ServiceResponse serviceResponse = loginService.emailValidate(email, type);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for validate the subdomain**/
	@GetMapping("subdomainValidate")
	public ResponseEntity<ServiceResponse> subdomainValidate(@RequestParam String subdomain){
		logger.info("Invoking API to validate the subdomain::"+subdomain);
		ServiceResponse serviceResponse = loginService.subdomainValidate(subdomain);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for authenticate user by PIN**/
	@PostMapping("authenticateByPin")
	public ResponseEntity<ServiceResponse> authenticateByPin(@RequestBody Map<String, String> req){
		logger.info("Invoking API for authenticate the admin user by PIN.");
		ServiceResponse serviceResponse = loginService.authenticateByPin(req);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
}
