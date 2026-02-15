package com.mealManage.service;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import com.mealManage.mealmodel.user.SuperAdminUser;
import com.mealManage.response.ParentValidationResp;
import com.mealManage.response.ResponseDetails;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.UserDetails;

public interface LoginService {
	
	public  ServiceResponse generateforgotPasswordLink(UserDetails userRegInfo);
	
	public ServiceResponse recoveryPassword(UserDetails userRegInfo, Boolean isCaterer, Boolean isDistrict);
	
	public boolean isLinkValidate(String token, String userId, Boolean isCaterer, Boolean isDistrict);
	
	public ServiceResponse updatePassword(UserDetails userDetails);
	
	public ResponseDetails getUserDetails(Long schoolId, String userName,Collection<? extends GrantedAuthority> role);
	
	public boolean revokeToken(String tokenValue);
	
	public ServiceResponse addSuperAdmin(SuperAdminUser users);
	
	public ParentValidationResp validateParent(UserDetails userRegInfo) throws Exception ;
	
	public Map<String, Object> validateParentAndDevice(String userEmailId, String deviceId);
	
	public ParentValidationResp generateOTP(UserDetails userRegInfo) throws Exception ;
	
	public ParentValidationResp validateOTP(UserDetails userRegInfo) throws Exception ;
	
	public ServiceResponse selfRegParent(String emailId);
	
	public ServiceResponse completeStripeAccSetup(String authCode);
	
	public ServiceResponse emailValidate(String email, String type);
	
	public ServiceResponse subdomainValidate(String subdomain);
	
	public ServiceResponse authenticateByPin(Map<String, String> req);
	
	public Boolean getPassAuthStatus(String username);

}
