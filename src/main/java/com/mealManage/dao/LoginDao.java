package com.mealManage.dao;

import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.user.SuperAdminUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.response.ParentValidationResp;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.UserDetails;

public interface LoginDao {
	
	public ServiceResponse inserForgotPasswordTokenInDB(UsersAuthInfo user);

	public int updateForgotPassword(UsersAuthInfo user);
	
	public String addSuperAdmin(SuperAdminUser users);
	
	public ParentValidationResp validateParent(UserDetails user, String mobNumb);
	
	public ParentValidationResp generateOTP(UserDetails user);
	
	public ParentValidationResp validateOTP(UserDetails user);
	
	public ServiceResponse selfRegParent(String emailId);
	
	public void updateMealSchoolStripe(MealSchool mealSchool);

}
