package com.mealManage.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.ApplicationFee;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;

/**This class used for Stripe account operation**/
@SuppressWarnings("unused")
public class StripeAccountOperationTesting {
	
	public static void main(String[] args) {
		Stripe.apiKey = System.getenv("STRIPE_API_KEY"); // Use env var - do not commit real keys
		try{
			//createStripeAccount("US","raghavtarun8@gmail.com","express");
			//createStripeCustomer("mealmanagetm@gmail.com", "acct_1DLpAPLUPlr9JvPq");
			//fetchStripeAcc("acct_1DQTTgA2LEq4Qlwl");
			//updateStripeAccount("acct_1DQTTgA2LEq4Qlwl");
			//generateTokenForTransactions();
			makePayment(generateTokenForTransactions());
			//makePaymentUsingToken();
			//deleteAccount("acct_1DLpAPLUPlr9JvPq");
			/*Map<String, Object> applicationfeeParams = new HashMap<String, Object>();
			applicationfeeParams.put("limit", "3");

			ApplicationFee.list(applicationfeeParams);*/
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	private static void makePaymentUsingToken() throws Exception{
		String token = generateTokenForTransactions();
		makePayment(token);
		
	}
	
	//create stripe account
	private static void createStripeAccount(String countryCode, String emailId, String accType) throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("country", countryCode);
		params.put("type", accType);
		params.put("email", emailId);
		//params.put("business_name", "ABC XYZ");
		Account acct = Account.create(params);
		System.out.println(acct.getKeys());
		System.out.println(acct.getId());
		/*Map<String, Object> accountParams = new HashMap<String, Object>();
		accountParams.put("type", "custom");
		accountParams.put("country", "US");
		Map<String, Object> externalAccountParams = new HashMap<String, Object>();
		externalAccountParams.put("object", "bank_account");
		externalAccountParams.put("country", "US");
		externalAccountParams.put("currency", "usd");
		externalAccountParams.put("routing_number", "110000000");
		externalAccountParams.put("account_number", "000123456789");
		accountParams.put("external_account", externalAccountParams);
		Map<String, Object> tosParams = new HashMap<String, Object>();
		tosParams.put("date", 1539686435);
		tosParams.put("ip", "183.83.167.159");
		accountParams.put("tos_acceptance", tosParams);
		Account acct = Account.create(accountParams);
		String acctId = acct.getId();
		System.out.println(acctId);*/
	}
	
	//create stripe customer
	private static void createStripeCustomer(String customerEmail, String accountId) throws Exception {
		RequestOptions requestOptions = RequestOptions.builder().setStripeAccount(accountId).build();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", customerEmail);
		Customer cust = Customer.create(params, requestOptions);
		System.out.println(cust.getId());
	}
	
	//retrieve stripe account details
	private static void fetchStripeAcc(String accountId) throws Exception{
		Account acct = Account.retrieve(accountId, null);
		acct.getLoginLinks().create().getUrl(); 
		System.out.println(acct);
	}
	
	//update stripe account or accept term & conditions
	private static void updateStripeAccount(String accountId) throws Exception{
		@SuppressWarnings("deprecation")
		Account acct = Account.retrieve(accountId);
		/*Map<String, Object> tosAcceptanceParams = new HashMap<String, Object>();
		tosAcceptanceParams.put("date", (long) System.currentTimeMillis() / 1000L);
		tosAcceptanceParams.put("ip", "52.12.25.23");*/ // Assumes you're not using a proxy
		Map<String, Object> params = new HashMap<String, Object>();
		//params.put("tos_acceptance", tosAcceptanceParams);
		params.put("support_phone", "555-867-5309");
		acct.update(params);
		System.out.println("Account updated");
	}
			
	//delete existing stripe account
	private static void deleteAccount(String accountId) throws Exception{
		Account acct = Account.retrieve(accountId, null);
		acct.delete();
		System.out.println("Account deleted successfully");
	}
			
	//Make payment using token
	private static void makePayment(String token) throws Exception{
		/*Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", 1079);
		params.put("currency", "usd");
		params.put("source", "tok_visa");
		Map<String, Object> transferDataParams = new HashMap<String, Object>();
		transferDataParams.put("amount", 1000);
		transferDataParams.put("destination", "acct_1Euk2BEu6Bqq24Hg");
		params.put("transfer_data", transferDataParams);
		Charge charge = Charge.create(params);*/
		//Map<String, Object> chargeParams = new HashMap<String, Object>();
		/*Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", (int) Math.round((1029.50)*100));
		chargeParams.put("currency", "usd");
		chargeParams.put("destination", "acct_1Euk2BEu6Bqq24Hg");
		chargeParams.put("source", token);
		chargeParams.put("application_fee_amount", (int) Math.round((29.50)*100));
		Charge charge = Charge.create(chargeParams);*/
		
		ArrayList paymentMethodTypes = new ArrayList();
		paymentMethodTypes.add("card");

		Map<String, Object> params = new HashMap<>();
		params.put("payment_method_types", paymentMethodTypes);
		params.put("amount", 1000);
		params.put("currency", "usd");
		params.put("application_fee_amount", 123);

		RequestOptions requestOptions = RequestOptions.builder().setStripeAccount("acct_1Euk2BEu6Bqq24Hg").build();
		PaymentIntent paymentIntent = PaymentIntent.create(params, requestOptions);
		System.out.println();
		/*chargeParams.put("amount", 10340);
		chargeParams.put("currency", "usd");
		
		chargeParams.put("source", token);
		chargeParams.put("application_fee_amount", 340);*/
		/*Map<String, Object> transferDataParams = new HashMap<String, Object>();
		transferDataParams.put("destination", "acct_1Euk2BEu6Bqq24Hg");*/
		//transferDataParams.put("amount", 10000);
		//chargeParams.put("destination", "acct_1Euk2BEu6Bqq24Hg");
		//chargeParams.put("transfer_data", transferDataParams);
		/*Map<String, Object> sourceParams = new HashMap<String, Object>();
		sourceParams.put("object", "card");
		sourceParams.put("number", "4000000760000002");
		sourceParams.put("token", "tok_br");
		sourceParams.put("exp_month", 2);
		sourceParams.put("exp_year", 2020);
		chargeParams.put("source", sourceParams);*/ // obtained with Stripe.js
		//Charge charge = Charge.create(chargeParams);
		/*Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", 103020);
		params.put("currency", "usd");
		params.put("source", "tok_visa");
		params.put("application_fee_amount", 20);

		RequestOptions requestOptions = RequestOptions.builder().setStripeAccount("acct_1Euk2BEu6Bqq24Hg").build();
		Charge charge = Charge.create(params, requestOptions);*/
		/*System.out.println("Transactions id:"+charge.getTransfer());
		System.out.println("Source Transaction id: "+charge.getId());
		System.out.println(charge);*/
	}
	
	private static String generateTokenForTransactions() throws Exception{
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4000000000004202");
		cardParams.put("exp_month", 11);
		cardParams.put("exp_year", 2021);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		System.out.println("Token value: "+token.getId());
		return token.getId();
	}
			
}
