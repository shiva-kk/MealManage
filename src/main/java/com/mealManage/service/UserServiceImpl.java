package com.mealManage.service;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mealManage.mealmodel.caterer.CatererUser;
import com.mealManage.mealmodel.repository.CatererRepository;
import com.mealManage.mealmodel.repository.DistrictRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.SuperAdminUserRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.DistrictUser;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.user.SchoolUser;
import com.mealManage.mealmodel.user.SuperAdminUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.util.PBKDF2Utility;

@Service
/**This class implements by UserDetailsService interface for user details during authentication**/
public class UserServiceImpl  implements UserDetailsService{
	
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private CatererRepository catererRepository;
	@Autowired
	private DistrictRepository districtRepository;
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private SuperAdminUserRepository superAdminUserRepository;
	
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**This method used for get the Users data and authenticating it**/
	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	UsersAuthInfo users=new UsersAuthInfo();
		UserDetails userDetails=null;
		try{
			users = usersAuthInfoRepository.findByUsername(username);

			if (users == null)
			   throw new UsernameNotFoundException("Oops! Please try again later.");

			        GrantedAuthority authority = new SimpleGrantedAuthority(users.getRole());
		        	 PBKDF2Utility pBKDF2Utility = new PBKDF2Utility();	
			        if(!authority.toString().equalsIgnoreCase("ROLE_SUPERADMIN") && authority.toString().equalsIgnoreCase("ROLE_ADMIN")){
			        	SchoolUser schoolUser = mealSchoolRepository.schoolUser(username);
			        	MealSchool mealSchool = mealSchoolRepository.findBySchoolUsersUsername(username);
			        	Boolean isPassAuth = loginService.getPassAuthStatus(username.toLowerCase());
			        	if(isPassAuth != null && isPassAuth)
			        		userDetails = (UserDetails)new User(users.getUsername(), pBKDF2Utility.encode(users.getPassword()),schoolUser.getIsActive(), 
					        		 mealSchool.getIsActive(), schoolUser.getIsVerified(), true
					        		 /*users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked()*/, Arrays.asList(authority));
			        	else
			        		userDetails = (UserDetails)new User(users.getUsername(), users.getPassword(),schoolUser.getIsActive(), 
			        		 mealSchool.getIsActive(), schoolUser.getIsVerified(), true
			        		 /*users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked()*/, Arrays.asList(authority));
			         }else if(authority.toString().equalsIgnoreCase("ROLE_PARENT")){	        	 
			        	 userDetails = (UserDetails)new User(users.getUsername(), 
			        			 pBKDF2Utility.encode(users.getfToken()),true, true, true, true
				        		 /*users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked()*/, Arrays.asList(authority));
			         }else if(authority.toString().equalsIgnoreCase("ROLE_CATERER")){
			        	CatererUser catererUser = catererRepository.catererUser(users.getUsername());
			        	 userDetails = (UserDetails)new User(users.getUsername(), 
			        			 users.getPassword(),catererUser.getIsActive(), catererUser.getIsVerified(), true, true
				        		 /*users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked()*/, Arrays.asList(authority,new SimpleGrantedAuthority("ROLE_ADMIN")));
			         }else if(authority.toString().equalsIgnoreCase("ROLE_DISTRICT")){
			        	 DistrictUser districtUser = districtRepository.districtUser(users.getUsername());
			        	 userDetails = (UserDetails)new User(users.getUsername(), 
			        			 users.getPassword(),districtUser.getIsActive(), districtUser.getIsVerified(), true, true
				        		 /*users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked()*/, Arrays.asList(authority,new SimpleGrantedAuthority("ROLE_ADMIN")));
			         }else if(authority.toString().equalsIgnoreCase("ROLE_SUPERADMIN")){
			        	 SuperAdminUser superAdminUser = superAdminUserRepository.findByUsername(users.getUsername());
			        	/* List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			        	 authorities.add(authority);
			        	 authorities.add(new SimpleGrantedAuthority("ACTUATOR"));*/
			        	 userDetails = (UserDetails)new User(users.getUsername(), 
				        		 users.getPassword(),superAdminUser.getIsActive(), true, true, true
				        		 /*users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked()*/, Arrays.asList(authority,new SimpleGrantedAuthority("ROLE_ADMIN")));
			         }
			         else{
				         userDetails = (UserDetails)new User(users.getUsername(), 
				        		 users.getPassword(),true, true, true, true
				        		 /*users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked()*/, Arrays.asList(authority));
			        }
	}catch(Exception e){
		logger.error("Error occured during authenticating user. "+e.getMessage());
		return null;
	}
		return userDetails;
	}
	
	/**This method used for authenticating school admin user**/
    public UserDetails loadUserByUsername(String username,String schoolId) throws UsernameNotFoundException {
    	UsersAuthInfo users=new UsersAuthInfo();
    	UserDetails userDetails=null;
    	MealSchool mealSchool = mealSchoolRepository.findBySchoolUsersUsername(username);
		try{
			users = usersAuthInfoRepository.findByUsername(username) ;
			if(users == null)
				throw new UsernameNotFoundException("Oops! Please try again later.");
			
				if(!users.getRole().equals("ROLE_ADMIN") && Integer.parseInt(schoolId) != mealSchool.getSchoolId()){
					return null;
				
			}else{
				 GrantedAuthority authority = new SimpleGrantedAuthority(users.getRole());
			        userDetails = (UserDetails)new User(users.getUsername(), 
			        		users.getPassword(),mealSchool.getIsActive(), /*users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked()*/
			        		true, true, true, Arrays.asList(authority));
			}
			}catch(Exception e){
			logger.error("Error occured during authenticating user. "+e.getMessage());
			return null;
		}
		return userDetails;
    }
}
