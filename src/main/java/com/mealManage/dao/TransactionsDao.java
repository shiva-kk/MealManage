package com.mealManage.dao;

import java.math.BigInteger;
import java.util.List;

import com.mealManage.domain.AccBalanceTransferSibling;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.packages.BCACAudit;
import com.mealManage.mealmodel.packages.PackageSubscriptionsTrx;
import com.mealManage.mealmodel.packages.PickupAuthorized;
import com.mealManage.mealmodel.transaction.MasterTransactionsAudit;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentBalanceImportResp;
import com.stripe.exception.StripeException;

public interface TransactionsDao {
	
	public MasterTransactionsAudit depositAmtThruOffline(MasterTransactionsAudit masterTransactionsAudit);
	
	public MasterTransactionsAudit depositAmtThruOnline(MasterTransactionsAudit masterTransactionsAudit, String currencyCode) throws StripeException, Exception;
	
	public void purchaseItemAudit(List<MasterTransactionsAudit> masterTransactionsAudits);
	
	public void purchaseItemAuditV2(List<MasterTransactionsAudit> masterTransactionsAudits);
	
	public List<Object[]> orderedItemsDetails(String selectedDate, Long mealSchoolId) throws Exception;
	
	public List<Object[]> orderedItemsDetailsV2(String selectedDate, Long mealSchoolId, ItemTypeConstants menuType) throws Exception;
	
	public List<Object[]> menuItemDetails(String selectedDate, Long mealSchoolId) throws Exception;
	
	public List<Object[]> menuItemDetailsV2(String selectedDate, Long mealSchoolId) throws Exception;
	
	public List<Object[]> menuItemDetailsV3(String selectedDate, Long mealSchoolId, ItemTypeConstants menuType, Long locationId) throws Exception;
	
	public MasterTransactionsAudit transferBalanceSibling(AccBalanceTransferSibling accBalanceTransferSibling);
	
	public List<BigInteger> menuPurchasedStudents(Long mealSchoolId, String startDateTime, String endDateTime, String menuType, 
			String transactionType, String itemType);

	public List<Object[]> lunchNotServedStudents(String selectedDate, Long mealSchoolId, String startDateTime, 
			String endDateTime) throws Exception;
	
	public ServiceResponse importStudentsBalance(List<StudentBalanceImportResp> studentBalanceImportList, 
			String loggedUser, Long mealSchoolId, Integer schoolYear, String type);
	
	public List<Object[]> getPurchaseTrx(Long trxId);
	
	public ServiceResponse removeAdjPurchaseTrx(MasterTransactionsAudit mta, Long trxId) throws Exception;
	
	public PackageSubscriptionsTrx subscribePackage(PackageSubscriptionsTrx packageSubscriptionsTrx);
	
	public ServiceResponse bcAcSubscriptionsAudit(List<BCACAudit> bcacAudits);
	
	public ServiceResponse authorizePP(List<PickupAuthorized> pickupAuthorizeds);
	
	public PackageSubscriptionsTrx pkgDuePayment(Long masterPkgTrxId, PaymentType type, String refId, String loggedUser);
	
	public PackageSubscriptionsTrx pkgPayment(Long masterPkgTrxId, PackageSubscriptionsTrx pkgTrx);

}
