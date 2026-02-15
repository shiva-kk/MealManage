package com.mealManage.security;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import com.mealManage.service.UserServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter  {

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
    private DataSource dataSource;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/*@Autowired
	private Authentication authentication;*/



	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth)
			throws Exception {
		 auth
         .userDetailsService(userServiceImpl)
         .passwordEncoder(passwordEncoder);
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().anonymous().disable().authorizeRequests()
		      //  .antMatchers("").permitAll()
				.antMatchers("/oauth/token")
				.permitAll();
	}


	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
		web.ignoring()
		.antMatchers("/mealManage/forgotPassword","/mealManage/recoveryPassword","/mealManage/isLinkValidate","/mealManage/validateParent","/mealManage/usersAuthInfoes/search/validateUserName","/mealManage/completeStripeAccSetup","/mealManage/selfRegParent","/mealManage/supportUsers","/website/**","/v2/api-docs", "/configuration/**", "/swagger-resources/**",  "/swagger-ui.html", "/webjars/**", "/api-docs/**","/health","/metrics","/trace","/info","/mealManage/demoRequests",
				"/mealManage/transactions/studentsWithOrderedItem","/mealManage/authenticateByPin","/mealManage/transactions/menuAvailableByGrade","/mealManage/adminEmailsBySchoolId","/website/**","/mealManage/transactions/refundWebhooksEvents","/mealManage/validateParentAndDevice","/mealManage/generateOTP","/mealManage/validateOTP","/mealManage/subdomainValidate");
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public TokenStore tokenStore() {
		//return new InMemoryTokenStore();
		return new JdbcTokenStore(dataSource);
	}
	@Bean
	@Autowired
	public TokenStoreUserApprovalHandler userApprovalHandler(
			TokenStore tokenStore) {
		TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
		handler.setTokenStore(tokenStore);
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(
				clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}
}
