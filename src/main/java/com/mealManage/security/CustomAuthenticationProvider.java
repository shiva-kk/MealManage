package com.mealManage.security;

import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.mealManage.service.UserServiceImpl;
import com.mealManage.util.PBKDF2Utility;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	public CustomAuthenticationProvider() {

	}

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		@SuppressWarnings("rawtypes")
		LinkedHashMap userDetails=(LinkedHashMap) authentication.getDetails();
		String schoolId=(String) userDetails.get("schoolId");

		UserDetails user = userServiceImpl.loadUserByUsername(authentication.getName(),schoolId);
		DaoAuthenticationProvider authenticationProvider = authenticationProvider();
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();
		if (authenticationProvider.authenticate(authentication)
				.isAuthenticated())
		{
			if(user==null){
				return null;
			}else{
				return new UsernamePasswordAuthenticationToken(name, password,
						user.getAuthorities());
			}
			
		} else {
			return null;
					
		}

	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userServiceImpl);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PBKDF2Utility();
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class
				.isAssignableFrom(authentication);
	}

}