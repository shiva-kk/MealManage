package com.mealManage.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.transaction.MasterTransactionsAudit;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.mealmodel.transaction.StudentWiseTransaction;
import com.mealManage.mealmodel.transaction.TransactionType;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.mealmodel.user.SupportOptions;
import com.mealManage.response.ServiceResponse;
import com.mealManage.service.MealManageAPIService;
import com.mealManage.service.TransactionsServiceImpl;

@RestController
@RequestMapping("website")
/**These API used for website APIs**/
public class WebsiteAPI {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MealManageAPIService mealManageAPIService;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private TransactionsServiceImpl transactionsServiceImpl;
	
	/**This API used for get the on-boarded school info**/
	@GetMapping("onboardedSchools")
	public List<Map<String, String>> onboardedSchoolsInfo(@RequestParam String currentDate){
		logger.info("Invoking API for get the onboarded schools info");
		return mealManageAPIService.onboardedSchoolsInfo(currentDate);
	}
	
	/**This API used for get the students details based on meal school id and date**/
	@GetMapping("studentsInfo")
	public List<Map<String, String>> studentsInfo(@RequestParam Long mealSchoolId, @RequestParam Integer schoolYear, 
			@RequestParam(value="parentEmail", required=false) String parentEmail, @RequestParam(value="isSupport", required=false) Boolean isSupport){
		logger.info("Invoking API for get the students info for website");
		return mealManageAPIService.websiteStudentsInfo(mealSchoolId, schoolYear, parentEmail, isSupport);
	}
	
	/**This API used for get all the schools details by parent email id**/
	@GetMapping("schoolsByParentEmail")
	public Map<String, Object> schoolsByParentEmail(@RequestParam(value="parentEmail", required = true) String parentEmail, 
			@RequestParam(value="systemDate", required=false) String systemDate){
		logger.info("Invoking API for get the school details info by parent email");
		return mealManageAPIService.schoolsByParentEmail(parentEmail, systemDate);
	}
	
	/**This API used for submit the free/reduced meal eligibility application**/
	@PostMapping("householdApplication")
	public ResponseEntity<ServiceResponse> householdApplication(@RequestBody HouseholdApplicationForFRM householdApplicationForFRM){
		logger.info("Invoking API to submit the household application for free/reduced meals eligibility program");
		ServiceResponse serviceResponse = null;
		if((householdApplicationForFRM.getStatus() != null && householdApplicationForFRM.getStatus().equalsIgnoreCase("approved")) 
				|| (householdApplicationForFRM.getApplicationId() != null && householdApplicationForFRM.getApplicationId() != 0)){
			serviceResponse = new ServiceResponse();
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(401);
			serviceResponse.setStatusMessage("You don't have access for this request!!");
		}else
			serviceResponse = mealManageAPIService.householdApplication(householdApplicationForFRM);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get Support options belong to the school/parent**/
	@GetMapping("supportOptions")
	public Map<String, List<String>> supportOptions(){
		logger.info("Invoking API to get the support options");
		Map<String, List<String>> options = new HashMap<String, List<String>>();
		for(SupportOptions so : SupportOptions.values()){
			options.put(so.name(), so.getValues());
		}
		return options;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("payMobAddBalanceCallback")
	public void payMobCallback(@RequestBody Object obj){
		logger.info("Invoking paymob proceed callback API with Object:: "+obj);
		try{
			ObjectMapper oMapper = new ObjectMapper();
			Map<String, Object> map = oMapper.convertValue(obj, Map.class);
			if(map.get("type") != null && map.get("type").toString().equalsIgnoreCase("TRANSACTION")){
				Object obj1 = map.get("obj");
				Map<String, Object> map1 = oMapper.convertValue(obj1, Map.class);
				Object obj2 = map1.get("data");
				Map<String, Object> map2 = oMapper.convertValue(obj2, Map.class);
				if(map2.get("txn_response_code") != null && map2.get("txn_response_code").toString().equalsIgnoreCase("APPROVED")){
					Object obj3 = map1.get("order");
					Map<String, Object> map3 = oMapper.convertValue(obj3, Map.class);
					String trxId = map3.get("id").toString();
					String chargeId = map1.get("id").toString();
					Object userEmailObj = map3.get("shipping_data");
					Map<String, Object> shippingData = oMapper.convertValue(userEmailObj, Map.class);
					String loggedUser = shippingData.get("email").toString();
					//Double totalAmt = Double.parseDouble(map3.get("paid_amount_cents").toString())/100;
					Object obj4 = map3.get("items");
					List<Object> stds = oMapper.convertValue(obj4, List.class);
					Long mealSchoolId = null;
					MasterTransactionsAudit mta = new MasterTransactionsAudit();
					mta.setTransactionType(TransactionType.Deposit);
					mta.setPaymentType(PaymentType.Online);
					mta.setTransactionDescription("Paid amount in advance");
					//mta.setTotalTransactionAmount(totalAmt);
					mta.setTransferId(trxId);
					mta.setChargeId(chargeId);
					mta.setPaymentGateway("PayMob");
					mta.setCreatedBy(loggedUser);
					Set<StudentWiseTransaction> swtList = new HashSet<StudentWiseTransaction>();
					Double totalTransactionAmt = (double) 0;
					for(Object obj5 : stds){
						StudentWiseTransaction swt = new StudentWiseTransaction();
						Map<String, Object> std = oMapper.convertValue(obj5, Map.class);
						if(mealSchoolId == null){
							StudentUser su = studentUserRepository.findOne(Long.parseLong(std.get("description").toString()));
							mealSchoolId = su.getMealSchool().getSchoolId();
						}
						swt.setStudentRecId(Long.parseLong(std.get("description").toString()));
						swt.setTransactionAmount(Double.parseDouble(std.get("amount_cents").toString())/100);
						totalTransactionAmt = totalTransactionAmt+(Double.parseDouble(std.get("amount_cents").toString())/100);
						swtList.add(swt);						
					}
					mta.setTotalTransactionAmount(totalTransactionAmt);
					mta.setMealSchoolId(mealSchoolId);
					mta.setAppFee(totalTransactionAmt*1/100);
					mta.setStudentWiseTransactions(swtList);
					transactionsServiceImpl.depositAmtThruOnline(mta);				
				}
			}
		}catch(Exception e){
			logger.error("Failed to proceed Add Balance paymob callback API due to "+e.getMessage());
		}
	}
}
