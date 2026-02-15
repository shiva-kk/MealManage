package com.mealManage.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import com.mealManage.service.UserServiceImpl;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends
		AuthorizationServerConfigurerAdapter {

	private static String REALM = "MY_OAUTH_REALM";

	@Autowired
	private TokenStore tokenStore;
	/*@Autowired
    private DataSource dataSource;*/
	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserServiceImpl userServiceImp;
	@Autowired
	private ClientDetailsService clientDetailsService;
	
	/**Create the instance of Token store**/
	/*@Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }*/
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients)
			throws Exception {

		clients.inMemory()
				.withClient("my-trusted-client")
						.authorizedGrantTypes("password","refresh_token")
				.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
				.scopes("read", "write").secret("secret").resourceIds("my_rest_api")
				/*.accessTokenValiditySeconds(1800).
				refreshTokenValiditySeconds(3600)*/
				.accessTokenValiditySeconds(2*3600).
				refreshTokenValiditySeconds(4*3600)
				.and()
		.withClient("my-trusted-mobile-client")
				.authorizedGrantTypes("password","refresh_token")
		.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
		.scopes("read", "write").secret("secret").resourceIds("my_rest_api")
		.accessTokenValiditySeconds(2*3600).
		refreshTokenValiditySeconds(8*3600).and()
		.withClient("my-trusted-parent-client")
			.authorizedGrantTypes("password","refresh_token")
	.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
	.scopes("read", "write").secret("secret").resourceIds("my_rest_api")
	.accessTokenValiditySeconds(15*24*60*60).
	refreshTokenValiditySeconds(50*24*60*60);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws Exception {
		endpoints.tokenStore(tokenStore)
				.authenticationManager(authenticationManager);
		endpoints.tokenServices(this.customTokenServices());
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer)
			throws Exception {
		oauthServer.realm(REALM + "/client");
	}
	
	@Bean
	@Primary
	public DefaultTokenServices customTokenServices() {
		DefaultTokenServices tokenServices = new CustomTokenServices();
		tokenServices.setTokenStore(tokenStore);
		tokenServices.setAuthenticationManager(authenticationManager);
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setReuseRefreshToken(true);
		tokenServices.setClientDetailsService(clientDetailsService);
	    tokenServices.setAuthenticationManager(createPreAuthProvider());
		return tokenServices;
	}
	private ProviderManager createPreAuthProvider() {
	    PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
	    provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userServiceImp));
	    return new ProviderManager(Arrays.asList(provider));
	}
	
	private static class CustomTokenServices extends DefaultTokenServices {
		private TokenStore tokenStore;
		@Override
		public synchronized OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
			OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
			if (refreshToken == null) {
				throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
			}
			OAuth2Authentication authentication = this.tokenStore.readAuthenticationForRefreshToken(refreshToken);
			OAuth2AccessToken accessToken = this.tokenStore.getAccessToken(authentication);
			if (accessToken != null && accessToken.getExpiresIn() > 10) {
				return super.getAccessToken(authentication);
			}
			return super.refreshAccessToken(refreshTokenValue, tokenRequest);
		}

		@Override
		public void setTokenStore(TokenStore tokenStore) {
			super.setTokenStore(tokenStore);
			this.tokenStore = tokenStore;
		}
	}
}