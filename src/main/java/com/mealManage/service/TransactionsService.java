package com.mealManage.service;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.mealManage.domain.AccBalanceTransferSibling;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.packages.BCACAudit;
import com.mealManage.mealmodel.packages.PackageSubscriptionsTrx;
import com.mealManage.mealmodel.packages.PickupAuthorized;
import com.mealManage.mealmodel.transaction.MasterTransactionsAudit;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.response.ServiceResponse;

public interface TransactionsService {
	
	public ServiceResponse depositAmtThruOffline(MasterTransactionsAudit masterTransactionsAudit);
	
	public ServiceResponse depositAmtThruOnline(MasterTransactionsAudit masterTransactionsAudit);
	
	public ServiceResponse eventPurchaseThruOnline(MasterTransactionsAudit masterTransactionsAudit);
	
	public ServiceResponse purchaseItemAudit(List<MasterTransactionsAudit> masterTransactionsAudits, Long mealSchoolId, String systemDateTime);
	
	public ServiceResponse purchaseItemAuditV2(List<MasterTransactionsAudit> masterTransactionsAudits, Long mealSchoolId, String systemDateTime);
	
	public ServiceResponse studentsWithOrderedItem(Integer schoolYear, String selectedDate, Long mealSchoolId);
	
	public ServiceResponse studentsWithOrderedItemV2(Integer schoolYear, String selectedDate, Long mealSchoolId, Boolean isVersion2, ItemTypeConstants menuType);
	
	public ServiceResponse menuAvailableByGrade(String selectedDate, Long mealSchoolId);
	
	public ServiceResponse menuAvailableByGradeV2(String selectedDate, Long mealSchoolId, Boolean isVersion2, ItemTypeConstants menuType, Long locationId);
	
	public ServiceResponse transferBalanceSibling(AccBalanceTransferSibling accBalanceTransferSibling);
	
	public Map<ItemTypeConstants, List<BigInteger>> purchasedMenuStudents(Long mealSchoolId, String selectedDate,Boolean isSysDate) throws ParseException;
	
	public ServiceResponse lunchNotServedStudents(Long mealSchoolId, String selectedDate, Integer schoolYear);
	
	public ServiceResponse importStudentsBalance(MultipartFile file, Long mealSchoolId, Integer schoolYear, String type);
	
	public ServiceResponse acceptStripeAgreement(Long mealSchoolId, String systemIP);
	
	public ServiceResponse refundAmount(MasterTransactionsAudit masterTransactionsAudit);
	
	public ServiceResponse adjustBalance(MasterTransactionsAudit masterTransactionsAudit);
	
	public ServiceResponse removePurchaseTrx(Long trxId, Long mealSchoolId);
	
	public ServiceResponse subscribePackage(PackageSubscriptionsTrx packageSubscriptionsTrx);
	
	public ServiceResponse bcAcSubscriptionsAudit(List<BCACAudit> bcacAudits);
	
	public ServiceResponse authorizePP(List<PickupAuthorized> pickupAuthorizeds);
	
	public ServiceResponse pkgDuePayment(Long masterPkgTrxId, PaymentType type, String refId);
	
	public ServiceResponse pkgPayment(Long masterPkgTrxId, PackageSubscriptionsTrx pkgTrx);

}
