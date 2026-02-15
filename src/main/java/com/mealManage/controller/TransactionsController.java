package com.mealManage.controller;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.AccBalanceTransferSibling;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.packages.BCACAudit;
import com.mealManage.mealmodel.packages.PackageSubscriptionsTrx;
import com.mealManage.mealmodel.packages.PickupAuthorized;
import com.mealManage.mealmodel.transaction.MasterTransactionsAudit;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.response.ServiceResponse;
import com.mealManage.service.TransactionsService;

@RestController
@RequestMapping("mealManage/transactions")
/**This controller used for payment related APIs**/
public class TransactionsController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TransactionsService transactionsService;
	
	/**This API used for deposit the payment of students when parent user pay amount at school campus by cheque/cash/CreditCard and send email**/
	@PostMapping("depositAmtThruOffline")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> depositAmtThruOffline(@RequestBody MasterTransactionsAudit masterTransactionsAudit){
		logger.info("Invoking API for deposit the payment at school campus through cheque/cash.");
		ServiceResponse serviceResponse = transactionsService.depositAmtThruOffline(masterTransactionsAudit);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for deposit the payment of students when parent user pay amount through online process and send email**/
	@PostMapping("depositAmtThruOnline")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> depositAmtThruOnline(@RequestBody MasterTransactionsAudit masterTransactionsAudit){
		logger.info("Invoking API for deposit the payment through online process");
		ServiceResponse serviceResponse = transactionsService.depositAmtThruOnline(masterTransactionsAudit);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used to deposit the payment through online for purchase the students event item**/
	@PostMapping("eventPurchaseThruOnline")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> eventPurchaseThruOnline(@RequestBody MasterTransactionsAudit masterTransactionsAudit){
		logger.info("Invoking API for audit the event purchase transactions.");
		ServiceResponse serviceResponse = transactionsService.eventPurchaseThruOnline(masterTransactionsAudit);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for audit the purchase items transactions details**/
	@PostMapping("purchaseItemsAudit")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> purchaseItemAudit(@RequestBody List<MasterTransactionsAudit> 
		masterTransactionsAudits, @RequestParam(value="mealSchoolId", required = true) Long mealSchoolId, 
		@RequestParam(value="systemDateTime", required=false) String systemDateTime){
		logger.info("Invoking API for audit the item purchase transactions details under school id: "+mealSchoolId+" and systemDateTime:"+systemDateTime);
		ServiceResponse serviceResponse = transactionsService.purchaseItemAudit(masterTransactionsAudits, mealSchoolId, systemDateTime);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for audit the purchase items transactions details**/
	@PostMapping("purchaseItemsAuditV2")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> purchaseItemAuditV2(@RequestBody List<MasterTransactionsAudit> 
		masterTransactionsAudits, @RequestParam(value="mealSchoolId", required = true) Long mealSchoolId, 
		@RequestParam(value="systemDateTime", required=false) String systemDateTime){
		logger.info("Invoking API for audit the item purchase transactions details under school id: "+mealSchoolId+" and systemDateTime:"+systemDateTime);
		ServiceResponse serviceResponse = transactionsService.purchaseItemAuditV2(masterTransactionsAudits, mealSchoolId, systemDateTime);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the student list based on school year along with ordered menu item details for 
	 * selected date, in case if student having any ordered items. [Note: Date should be yyyy-MM-dd]**/
	@GetMapping("studentsWithOrderedItem")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> studentsWithOrderedItem(@RequestParam(value="schoolYear", required=true) 
		Integer schoolYear,	@RequestParam(value="selectedDate", required=true) String selectedDate,
		@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId){
		logger.info("Invoking API for get the students with ordered item details based on school, school year and date");
		ServiceResponse serviceResponse = transactionsService.studentsWithOrderedItem(schoolYear, selectedDate, mealSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the student list based on school year along with ordered menu item details for 
	 * selected date, in case if student having any ordered items. [Note: Date should be yyyy-MM-dd]**/
	@GetMapping("studentsWithOrderedItemV2")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> studentsWithOrderedItemV2(@RequestParam(value="schoolYear", required=true) 
		Integer schoolYear,	@RequestParam(value="selectedDate", required=true) String selectedDate,
		@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, @RequestParam(value="isVersion2", required=false) Boolean isVersion2, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType){
		logger.info("Invoking API for get the students with ordered item details based on school, school year and date with isVersion2::"+isVersion2);
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = transactionsService.studentsWithOrderedItemV2(schoolYear, selectedDate, mealSchoolId, isVersion2, menuType);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the available menu based on selected date and school id group by grade**/
	@GetMapping("menuAvailableByGrade")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> menuAvailableByGrade(@RequestParam(value="selectedDate", required=true) 
		String selectedDate, @RequestParam(value="mealSchoolId", required=true) Long mealSchoolId){
		logger.info("Invoking API for get the available menu by school id and selected date in group by grade");
		ServiceResponse serviceResponse = transactionsService.menuAvailableByGrade(selectedDate, mealSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for get the available menu based on selected date and school id group by grade**/
	@GetMapping("menuAvailableByGradeV2")
	//@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> menuAvailableByGradeV2(@RequestParam(value="selectedDate", required=true) 
		String selectedDate, @RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, @RequestParam(value="isVersion2", required=false) Boolean isVersion2, 
		@RequestParam(value="menuType", required=false) ItemTypeConstants menuType, @RequestParam(value="locationId", required=false) Long locationId){
		logger.info("Invoking API for get the available menu by school id and selected date in group by grade with isVersion2::"+isVersion2);
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		ServiceResponse serviceResponse = transactionsService.menuAvailableByGradeV2(selectedDate, mealSchoolId, isVersion2, menuType, locationId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for transfer the balance to the sibling**/
	@PostMapping("transferBalance")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> transferBalance(@RequestBody AccBalanceTransferSibling acBalanceTransferSibling){
		logger.info("Invoking the API for transfer the balance to the siblings");
		ServiceResponse serviceResponse = transactionsService.transferBalanceSibling(acBalanceTransferSibling);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}

	/**This API used for get all the student records id's who-ever taken breakfast/lunch/Milk
	 * @throws ParseException **/
	@GetMapping("purchasedMenuStudents")
	public Map<ItemTypeConstants, List<BigInteger>> purchasedMenuStudents(@RequestParam(value = "mealSchoolId", required=true) 
			Long mealSchoolId, @RequestParam(value="selectedDate", required=true) String selectedDate) throws ParseException{
		logger.info("Invoking the API for get all lunch, breakfast & milk purchased students data");
		return transactionsService.purchasedMenuStudents(mealSchoolId, selectedDate,false);
	}

	/**This API used for get the students details who ordered lunch but not served to student**/
	@GetMapping("lunchNotServedStudents")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> lunchNotServedStudents(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestParam(value="selectedDate", required=true) String selectedDate, 
			@RequestParam(value="schoolYear", required=true) Integer schoolYear){
		logger.info("Invoking the API for get the students who ordered lunch but not served");
		ServiceResponse serviceResponse = transactionsService.lunchNotServedStudents(mealSchoolId, selectedDate, schoolYear);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for import the excel file data to add/overwrite the balance of students [Type should be Add or Overwrite]**/
	@PostMapping("importStudentsBalance")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> importStudentsBalance(@RequestPart(value = "file") MultipartFile multiPartFile, 
			@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestParam(value="schoolYear", required=true) Integer schoolYear,
			@RequestParam(value="type", required=true) String type){
		logger.info("Invoking API for add/overwrite the balance amount of students for school:"+mealSchoolId+", "
				+ "schoolYear:"+schoolYear+", import process type:"+type);
		ServiceResponse serviceResponse = transactionsService.importStudentsBalance(multiPartFile, mealSchoolId, 
				schoolYear, type);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for accept the stripe agreement**/
	@GetMapping("acceptStripeAgreement/{mealSchoolId}")
	public ResponseEntity<ServiceResponse> acceptStripeAgreement(@PathVariable Long mealSchoolId, 
			@RequestParam String systemIP){
		logger.info("Invoking API for accept the stripe agreement");
		ServiceResponse serviceResponse = transactionsService.acceptStripeAgreement(mealSchoolId, systemIP);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for test the webhooks client event**/
	@PostMapping("refundWebhooksEvents")
	public void refundWebhooksEvents(@RequestBody Object obj){
		logger.info("Invoking API to create the refund transaction in back-end when refund done from stripe dashboard");
		logger.info("Object data:"+obj);
	}
	
	/**This API used to refund the amount for a student**/
	@PostMapping("refundAmount")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> refundAmount(@RequestBody MasterTransactionsAudit masterTransactionsAudit){
		logger.info("Invoking API to refund the amount for a student.");
		ServiceResponse serviceResponse = transactionsService.refundAmount(masterTransactionsAudit);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}

	/**This API used to adjust the balance of student**/
	@PostMapping("adjustBalance")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> adjustBalance(@RequestBody MasterTransactionsAudit masterTransactionsAudit){
		logger.info("Invoking API to adjust the balance of a student.");
		ServiceResponse serviceResponse = transactionsService.adjustBalance(masterTransactionsAudit);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for soft delete the transaction**/
	@DeleteMapping("removePurchaseTrx")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERADMIN')")
	public ResponseEntity<ServiceResponse> removePurchaseTrx(@RequestParam Long trxId, @RequestParam Long mealSchoolId){
		logger.info("Invoking API for deactivate the transaction");
		ServiceResponse serviceResponse = transactionsService.removePurchaseTrx(trxId, mealSchoolId);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for subscribe the Package**/
	@PostMapping("subscribePackage")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_PARENT')")
	public ResponseEntity<ServiceResponse> subscribePackage(@RequestBody PackageSubscriptionsTrx packageSubscriptionsTrx){
		logger.info("Invoking API for subscribe the package");
		ServiceResponse serviceResponse = transactionsService.subscribePackage(packageSubscriptionsTrx);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for audit the BCAC subscriptions info**/
	@PostMapping("bcAcSubscriptionsAudit")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> bcAcSubscriptionsAudit(@RequestBody List<BCACAudit> bcacAudits){
		logger.info("Invoking API for audit the BC & AC package subscriptions");
		ServiceResponse serviceResponse = transactionsService.bcAcSubscriptionsAudit(bcacAudits);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This method used for add/update/delete authorized pickup persons details separately without package purchase**/
	@PostMapping("authorizePP")
	public ResponseEntity<ServiceResponse> authorizePP(@RequestBody List<PickupAuthorized> pickupAuthorizeds){
		logger.info("Invoking API for add/update/delete the authorized pickup persons");
		ServiceResponse serviceResponse = transactionsService.authorizePP(pickupAuthorizeds);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for update the due package payment status**/
	@PutMapping("pkgDuePayment/{masterPkgTrxId}")
	@PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ServiceResponse> pkgDuePayment(@PathVariable Long masterPkgTrxId, @RequestBody Map<String, String> req){
		logger.info("Invoking API for update the due package payment status for masterPkgTrxId::"+masterPkgTrxId);
		ServiceResponse serviceResponse = transactionsService.pkgDuePayment(masterPkgTrxId, PaymentType.valueOf(req.get("type")), req.get("refId"));
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
	
	/**This API used for update the package due payment status**/
	@PreAuthorize("hasAuthority('ROLE_PARENT')")
	@PutMapping("pkgPayment/{masterPkgTrxId}")
	public ResponseEntity<ServiceResponse> pkgPayment(@PathVariable Long masterPkgTrxId, @RequestBody PackageSubscriptionsTrx pkgTrx){
		logger.info("Invoking API for update the package due payment status online for masterPkgTrxId::"+masterPkgTrxId);
		ServiceResponse serviceResponse = transactionsService.pkgPayment(masterPkgTrxId, pkgTrx);
		return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
	}
}
