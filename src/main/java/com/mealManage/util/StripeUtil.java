package com.mealManage.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stripe.Stripe;
import com.stripe.model.Account;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.net.ApiResource;
import com.stripe.net.LiveStripeResponseGetter;
import com.stripe.net.RequestOptions;
import com.stripe.net.StripeResponseGetter;
import com.stripe.model.Charge;
import com.stripe.exception.StripeException;


/***This util class used for setup the stripe account and do other operation**/
@Component
public class StripeUtil {
	
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	
	@Value("${stripe.secret.key}")
    private String stripeSecretKey;
	@Value("${stripe.client.id}")
	private String stripeClientId;
	@Value("${stripe.base.url}")
	private String stripeBaseUrl;
	@Value("${stripe.account.setup.url}")
	private String stripeAccSetupUrl;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**This method used for build the URL for create stripe account and send email to the primary admin user**/
	public void sendStripeSetupEmail(String adminEmail, String schoolName){
		Map<String, String> notificationReq = new HashMap<String, String>();
		notificationReq.put("adminEmails", adminEmail);
		notificationReq.put("schoolName", schoolName);
		notificationReq.put("stripeSetupLink", stripeBaseUrl+stripeAccSetupUrl.replace("<<clientId>>", stripeClientId));
		logger.info("Sending email for setup the stripe account to the admin user");
		sendNotificationUtil.stripeAccSetupReminder(notificationReq);
	}
	
	/**This method used for complete the stripe account setup
	 * @throws Exception **/
	public Map<String, String> stripeAccSetupComplete(String authCode) throws Exception{
		Map<String, Object> mapReq = new HashMap<String, Object>();
		mapReq.put("client_secret", stripeSecretKey);
		mapReq.put("code", authCode);
		mapReq.put("grant_type", "authorization_code");
		TokenResponse tokenResponse = stripeAccBuild(mapReq, null);
		Map<String, String> stripeAccResp = new HashMap<String, String>();
		stripeAccResp.put("stripeAccId", tokenResponse.getStripeUserId());
		Account account = stripeAccDetails(tokenResponse.getStripeUserId());
		stripeAccResp.put("userEmail", account.getEmail());
		//stripeAccResp.put("stripeAccessLink", account.getLoginLinks().create().getUrl());
		return stripeAccResp;
	}
	
	/**This method used for generate the stripe account access link**/
	public String stripeAccAccessLink(String accountId) throws Exception{
		Stripe.apiKey = stripeSecretKey;
		Account acct = Account.retrieve(accountId, null);
		return acct.getLoginLinks().create().getUrl(); 
	}
	
	/**This method used for accept the stripe agreement**/
	public boolean stripeAgreementAcceptance(String stripeAccountId, String systemIP) throws Exception{
		boolean status = false;
		Stripe.apiKey = stripeSecretKey;
		Account acct = Account.retrieve(stripeAccountId, null);
		Map<String, Object> tosAcceptanceParams = new HashMap<String, Object>();
		tosAcceptanceParams.put("date", (long) System.currentTimeMillis() / 1000L);
		tosAcceptanceParams.put("ip", systemIP); 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tos_acceptance", tosAcceptanceParams);
		acct.update(params);
		status = true;
		return status;
	}
	
	/**This method used for authenticate the stripe details and return the stripe account details**/
	private TokenResponse stripeAccBuild(Map<String, Object> params, RequestOptions options) throws Exception {
		Stripe.apiKey = stripeSecretKey;
		StripeResponseGetter stripeResponseGetter = new LiveStripeResponseGetter();
		String url = stripeBaseUrl + "oauth/token";
		return stripeResponseGetter.oauthRequest(ApiResource.RequestMethod.POST, url, params, TokenResponse.class,
				ApiResource.RequestType.NORMAL, options);
	}
	
	/**This method used for retrieved the stripe account details using stripe account id
	 * @throws Exception **/
	private Account stripeAccDetails(String accountId) throws Exception{
		Account acct = Account.retrieve(accountId, null);
		//acct.getLoginLinks().create();
		return acct; 
	}
	
	public Map<String, Object> prepareStripeDestinationCharge(Double totalTrxAmt, Double appFee, Double trxFee, String currencyCode, 
			String stripeAccId, String trxToken){
		Stripe.apiKey = stripeSecretKey;
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", (int) Math.round((totalTrxAmt+appFee+trxFee)*100));
		chargeParams.put("currency", CommonUtil.getCurrCode(currencyCode));
		chargeParams.put("destination", stripeAccId);
		chargeParams.put("source", trxToken);
		chargeParams.put("application_fee_amount", (int) Math.round((appFee+trxFee)*100));
		return chargeParams;
	}
	
	
	public Charge prepareStripeDirectCharge(Double totalTrxAmt, Double appFee, Double trxFee, String currencyCode, 
			String stripeAccId, String trxToken, Boolean trxFeeOnMerchant) throws Exception{
		Charge charge = null;
		Stripe.apiKey = stripeSecretKey;
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		if(trxFeeOnMerchant){
			chargeParams.put("amount", (int) Math.round((totalTrxAmt)*100));
			chargeParams.put("application_fee_amount", (int) Math.round((appFee)*100));
		}else{
			chargeParams.put("amount", (int) Math.round((totalTrxAmt+appFee+trxFee)*100));
			chargeParams.put("application_fee_amount", (int) Math.round((appFee)*100));
		}
		chargeParams.put("currency", CommonUtil.getCurrCode(currencyCode));
		chargeParams.put("source", trxToken);
		RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(stripeAccId).build();
		charge = Charge.create(chargeParams, requestOptions);
		return charge;
	}
	
	/**This method used for create the stripe account of school using test key or live key. Always use test key for testing**/
	/*public Account createStripeAccount(String userEmail, String country, String schoolName) throws Exception{
		Stripe.apiKey = stripeSecretKey; //define key (i.e. test or live)
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("country", country);
		params.put("type", stripeAccountType);
		params.put("email", userEmail);
		Account acct = Account.create(params);
		logger.info("Account created successfully for the user: "+userEmail+" with account id: "+acct.getId());
		Map<String, String> notificationReq = new HashMap<String, String>();
		notificationReq.put("adminEmails", userEmail);
		notificationReq.put("schoolName", schoolName);
		sendNotificationUtil.stripeAccSetupReminder(notificationReq);
		return acct;
	}*/

}
