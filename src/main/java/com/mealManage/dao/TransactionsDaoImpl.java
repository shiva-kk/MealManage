package com.mealManage.dao;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import com.mealManage.domain.AccBalanceTransferSibling;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.packages.BCACAudit;
import com.mealManage.mealmodel.packages.PackageSubscriptionsTrx;
import com.mealManage.mealmodel.packages.PickupAuthorized;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.transaction.MasterTransactionsAudit;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.mealmodel.transaction.PurchaseItemType;
import com.mealManage.mealmodel.transaction.StudentWiseTransaction;
import com.mealManage.mealmodel.transaction.TransactionType;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentBalanceImportResp;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.StripeUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

@Repository
@Transactional(rollbackOn = Exception.class)
@SuppressWarnings("unchecked")
/**This class used for implement the TransactionsDao's method**/
public class TransactionsDaoImpl implements TransactionsDao{
	
	@PersistenceContext
	private EntityManager entityManager;
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;
	@Autowired
	private StripeUtil stripeUtil;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**This method used for deposit the offline transaction or refund amount**/
	@Override
	public MasterTransactionsAudit depositAmtThruOffline(MasterTransactionsAudit masterTransactionsAudit) {
		entityManager.persist(masterTransactionsAudit);
		for(StudentWiseTransaction studentWiseTransaction: masterTransactionsAudit.getStudentWiseTransactions()){
			StudentUser studentUser = studentWiseTransaction.getStudentUser();
			studentUser.setModifiedBy(masterTransactionsAudit.getCreatedBy());
			studentUser.setModifiedOn(new Date());
			studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", studentWiseTransaction.getFinalBalance())));
			entityManager.merge(studentUser);
		}
		if(masterTransactionsAudit.getTransactionType().toString().equalsIgnoreCase(TransactionType.Refund.toString()))
			logger.info("Refunded amount to the student successfully with Id: "+masterTransactionsAudit.getRecId());
		else if(masterTransactionsAudit.getTransactionType().toString().equalsIgnoreCase(TransactionType.Adjustment.toString()))
			logger.info("Balance adjusted to the student successfully with Id: "+masterTransactionsAudit.getRecId());
		else
			logger.info("Deposit amount through offline process completed with Id: "+masterTransactionsAudit.getRecId());
		return masterTransactionsAudit;
	}
	
	/**This method used for pay the amount through online
	 * @throws Exception **/
	@Override
	public MasterTransactionsAudit depositAmtThruOnline(MasterTransactionsAudit masterTransactionsAudit, String currencyCode) throws Exception{
		String tansactionToken = masterTransactionsAudit.getTransactionToken();
		entityManager.persist(masterTransactionsAudit);
		for(StudentWiseTransaction studentWiseTransaction: masterTransactionsAudit.getStudentWiseTransactions()){
			StudentUser studentUser = studentWiseTransaction.getStudentUser();
			studentUser.setModifiedBy(masterTransactionsAudit.getCreatedBy());
			studentUser.setModifiedOn(new Date());
			studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", studentWiseTransaction.getFinalBalance())));
			entityManager.merge(studentUser);
		}
		//entityManager.flush();
		if(masterTransactionsAudit.getPaymentGateway() == null || masterTransactionsAudit.getPaymentGateway().equalsIgnoreCase("Stripe")){
			/*Stripe.apiKey = stripeSecretKey;
			Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("amount", (int) Math.round((masterTransactionsAudit.getTotalTransactionAmount()+
					masterTransactionsAudit.getAppFeeAmount()+masterTransactionsAudit.getTransactionFees())*100));
			chargeParams.put("currency", CommonUtil.getCurrCode(currencyCode));
			chargeParams.put("destination", masterTransactionsAudit.getMealSchool().getStripeAccountId());
			chargeParams.put("source", tansactionToken);
			chargeParams.put("application_fee_amount", (int) Math.round((masterTransactionsAudit.getAppFeeAmount()+masterTransactionsAudit.getTransactionFees())*100));*/
			/*Map<String, Object> chargeParams = stripeUtil.prepareStripeDestinationCharge(masterTransactionsAudit.getTotalTransactionAmount(),
					masterTransactionsAudit.getAppFeeAmount(), masterTransactionsAudit.getTransactionFees(), CommonUtil.getCurrCode(currencyCode), 
					masterTransactionsAudit.getMealSchool().getStripeAccountId(), tansactionToken);*/
			Charge charge = stripeUtil.prepareStripeDirectCharge(masterTransactionsAudit.getTotalTransactionAmount(),
					masterTransactionsAudit.getAppFeeAmount(), masterTransactionsAudit.getTransactionFees(), CommonUtil.getCurrCode(currencyCode), 
					masterTransactionsAudit.getMealSchool().getStripeAccountId(), tansactionToken,masterTransactionsAudit.getMealSchool().isTrxFeeOnSchool());
			masterTransactionsAudit.setChargeId(charge.getId());
			masterTransactionsAudit.setTransferId(charge.getBalanceTransaction());
			entityManager.merge(masterTransactionsAudit);
		}
		if(masterTransactionsAudit.getMealSchool().isTrxFeeOnSchool()){
			masterTransactionsAudit.setTransactionFees(0.0);
			masterTransactionsAudit.setAppFeeAmount(0.0);
		}
		logger.info("Deposit amount through online process completed with Id: "+masterTransactionsAudit.getRecId()
			+" charge id: "+masterTransactionsAudit.getChargeId());
		return masterTransactionsAudit;
	}
	
	/**This method used for audit the purchase transaction details**/
	@Override
	public void purchaseItemAudit(List<MasterTransactionsAudit> masterTransactionsAudits) {
		StudentUser su = null;
		for(MasterTransactionsAudit masterTransactionsAudit : masterTransactionsAudits){
			if(masterTransactionsAudit.getStudentWiseTransactions().size() > 1)
				su = (new LinkedList<>(masterTransactionsAudit.getStudentWiseTransactions())).get(masterTransactionsAudit.getStudentWiseTransactions().size()-1).getStudentUser();
			else
				su = (new LinkedList<>(masterTransactionsAudit.getStudentWiseTransactions())).get(0).getStudentUser();
			entityManager.persist(masterTransactionsAudit);
			entityManager.merge(su);
		}
		
		/*entityManager.persist(masterTransactionsAudit);
		for(StudentWiseTransaction studentWiseTransaction: masterTransactionsAudit.getStudentWiseTransactions()){
			StudentUser studentUser = studentWiseTransaction.getStudentUser();
			studentUser.setModifiedBy(masterTransactionsAudit.getCreatedBy());
			studentUser.setModifiedOn(new Date());
			studentUser.setAccBalance(studentWiseTransaction.getFinalBalance());
			entityManager.merge(studentUser);
		}*/
		logger.info("Purchase transactions details audited successfully");
	}
	
	/**This method used for audit the purchase transaction details**/
	@Override
	public void purchaseItemAuditV2(List<MasterTransactionsAudit> masterTransactionsAudits) {
		for(MasterTransactionsAudit masterTransactionsAudit : masterTransactionsAudits){
			if(masterTransactionsAudit.getPayToAmt() != null && masterTransactionsAudit.getPayToAmt() > 0){
				MasterTransactionsAudit masterTransaction = new MasterTransactionsAudit();
				BeanUtils.copyProperties(masterTransactionsAudit, masterTransaction);
				masterTransaction.setItemTaken(true);
				if(masterTransaction.getNote() == null)
					masterTransaction.setNote("Meal balance added from POS app.");
				masterTransaction.setPosDeposit(true);
				if(masterTransaction.getPaymentType() == null)
					masterTransaction.setPaymentType(PaymentType.Cash);
				masterTransaction.setPurchaseItemType(null);
				masterTransaction.setTotalTransactionAmount(masterTransactionsAudit.getPayToAmt());
				masterTransaction.setTransactionDescription("Meal balance added");
				masterTransaction.setTransactionType(TransactionType.Deposit);
				StudentWiseTransaction studentWiseTransactionOld = new ArrayList<>(masterTransactionsAudit.getStudentWiseTransactions()).get(0);
				StudentWiseTransaction studentWiseTransaction = new StudentWiseTransaction();
				BeanUtils.copyProperties(studentWiseTransactionOld, studentWiseTransaction);
				studentWiseTransaction.setEligStatus(null);
				studentWiseTransaction.setEmrgLunchServe(false);
				studentWiseTransaction.setFinalBalance(masterTransactionsAudit.getAccBalance());
				studentWiseTransaction.setIsPosted(true);
				studentWiseTransaction.setCcAmt(0.0);
				studentWiseTransaction.setItems(null);
				studentWiseTransaction.setPrepaidAmt(0.0);
				if(masterTransactionsAudit.getChargedAmt() != null && masterTransactionsAudit.getChargedAmt() > 0){
					studentWiseTransaction.setChargedAmt(masterTransactionsAudit.getChargedAmt());
					masterTransaction.setPurchaseItemType(masterTransactionsAudit.getPurchaseItemType());
					studentWiseTransaction.setMealType("Regular");
					studentWiseTransaction.setEligStatus(studentWiseTransactionOld.getEligStatus());
				}
				studentWiseTransaction.setTransactionAmount(Double.valueOf(new DecimalFormat("##.00").format(masterTransactionsAudit.getPayToAmt())));
				Set<StudentWiseTransaction> studentWiseTransactions = new HashSet<StudentWiseTransaction>();
				studentWiseTransactions.add(studentWiseTransaction);
				masterTransaction.setStudentWiseTransactions(studentWiseTransactions);
				entityManager.persist(masterTransaction);
			}
		}
		StudentUser su = null;
		for(MasterTransactionsAudit masterTransactionsAudit : masterTransactionsAudits){
			entityManager.persist(masterTransactionsAudit);
			if(masterTransactionsAudit.getStudentWiseTransactions().size() > 1)
				su = (new LinkedList<>(masterTransactionsAudit.getStudentWiseTransactions())).get(masterTransactionsAudit.getStudentWiseTransactions().size()-1).getStudentUser();
			else
				su = (new LinkedList<>(masterTransactionsAudit.getStudentWiseTransactions())).get(0).getStudentUser();
			entityManager.merge(su);
		}
		logger.info("Purchase transactions details audited successfully");
	}

	/**This method used for get the ordered meal item details based on selected date and school
	 * @throws Exception **/
	@Override
	public List<Object[]> orderedItemsDetails(String selectedDate, Long mealSchoolId) throws Exception {
		List<Object[]> orderedItems = null;
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String qry = "Select o.studentRecId, su.isFreeMealEligible, su.isReducePriceEligible, mm.mealId, "
				+ "mm.mealName, mm.mealPrice, mm.reducedPrice, mm.mealLongDesc, mm.mealtype, sms.reducedPriceStatus from "
				+ "OrderMealItemsDetailReport o Inner Join StudentUser_v2 su on o.studentRecId=su.userId Inner Join MealOrdersAudit_v2 moa on o.orderId = moa.schoolId Inner Join "
				+ "MealMenu_v2 mm on o.mealId = mm.mealId Inner Join SchoolMeals_v2 sm on o.schoolMealId = sm.schoolId "
				+ "Inner Join SchoolMealsSummary_v2 sms on sm.schoolMealSummary_schoolId = sms.schoolId"
				+ " where o.mealSchoolId = :mealSchoolId and o.mealDate = :mealDate and o.mealType IN ('MEAL','SIDE')";
		orderedItems = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("mealDate", selectedDate+" 00:00:00").getResultList();
		logger.info("Return the ordered item details");
		return orderedItems;
	}
	
	/**This method used for get the ordered meal item details based on selected date and school
	 * @throws Exception **/
	@Override
	public List<Object[]> orderedItemsDetailsV2(String selectedDate, Long mealSchoolId, ItemTypeConstants menuType) throws Exception {
		List<Object[]> orderedItems = null;
		List<String> itemTypes = null;
		switch (menuType.toString().toUpperCase()) {
			case "LUNCH": itemTypes = Arrays.asList(MealType.MEAL.toString(), MealType.SIDE.toString(), MealType.EXTRA.toString()); break;
			case "SNACK": itemTypes = Arrays.asList(MealType.SNACK.toString(), MealType.SIDE.toString(), MealType.EXTRA.toString()); break;
			case "DINNER": itemTypes = Arrays.asList(MealType.DINNER.toString(), MealType.SIDE.toString(), MealType.EXTRA.toString()); break;
			case "BREAKFAST": itemTypes= Arrays.asList(MealType.BREAKFAST.toString(), MealType.SIDE.toString(), MealType.EXTRA.toString()); break;
		}
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String qry = "Select o.studentRecId, su.isFreeMealEligible, su.isReducePriceEligible, mm.id, "
				+ "mm.name, c.price, c.reducedPrice, mm.longDescription, mm.category, sms.reducedPriceStatus from "
				+ "OrderMealItemsDetailReport o Inner Join StudentUser_v2 su on o.studentRecId=su.userId Inner Join MealOrdersAudit_v2 moa on o.orderId = moa.schoolId Inner Join "
				+ "menu_items mm on o.mealId = mm.id Inner Join meal_calendar c on o.schoolMealId = c.id "
				+ "Inner Join meal_calendar_summary sms on c.meal_calendar_summary_id = sms.id"
				+ " where o.mealSchoolId = :mealSchoolId and sms.mealType = :menuType and o.mealDate = :mealDate and o.mealType IN (:itemTypes)";
		orderedItems = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("menuType", menuType.toString())
				.setParameter("mealDate", selectedDate+" 00:00:00").setParameter("itemTypes", itemTypes).getResultList();
		logger.info("Return the ordered item details");
		return orderedItems;
	}

	/**This method used for get the the available menu item details based on selected date and mealSchoolId**/
	@Override
	public List<Object[]> menuItemDetails(String selectedDate, Long mealSchoolId) throws Exception {
		List<Object[]> menuItems = null;
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String qry = "Select mm.mealId, mm.mealLongDesc, mm.mealPrice, mm.reducedPrice, mm.mealName, mm.mealtype, "
				+ "sms.reducedPriceStatus, smg.grades_name from SchoolMeals_v2 sm Inner Join SchoolMealsSummary_v2 sms "
				+ "on sm.schoolMealSummary_schoolId = sms.schoolId Inner Join MealMenu_v2 mm on "
				+ "sm.mealMenu_Id = mm.mealId Inner Join schoolMeal_grades smg on sm.schoolId = smg.schoolmeal_Id "
				+ "where sm.mealSchool_schoolId = :mealSchoolId and mm.mealDate = :mealDate and mm.mealtype IN "
				+ "('MEAL','SIDE','HOLIDAY') and sms.isPublished = 1 and sm.isDelete = 0";
		menuItems = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("mealDate", selectedDate+" 00:00:00").getResultList();
		logger.info("Return the available menu item details");
		return menuItems;
	}
	
	/**This method used for get the the available menu item details based on selected date and mealSchoolId**/
	@Override
	public List<Object[]> menuItemDetailsV2(String selectedDate, Long mealSchoolId) throws Exception {
		List<Object[]> menuItems = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yearMonth = new SimpleDateFormat("yyyyMM").format(sdf.parse(selectedDate));
		String qry = "Select mm.mealId, mm.mealLongDesc, mm.mealPrice, mm.reducedPrice, mm.mealName, mm.mealtype, "
				+ "sms.reducedPriceStatus, smg.grades_name from SchoolMeals_v2 sm Inner Join SchoolMealsSummary_v2 sms "
				+ "on sm.schoolMealSummary_schoolId = sms.schoolId Inner Join MealMenu_v2 mm on "
				+ "sm.mealMenu_Id = mm.mealId Inner Join schoolMeal_grades smg on sm.schoolId = smg.schoolmeal_Id "
				+ "where sm.mealSchool_schoolId = :mealSchoolId and ((mm.mealDate = :mealDate and mm.mealtype IN "
				+ "('MEAL','SIDE','HOLIDAY')) or (sms.yearMonth = :yearMonth and mm.mealtype = 'EXTRA')) and sms.isPublished = 1 and sm.isDelete = 0";
		menuItems = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("mealDate", selectedDate+" 00:00:00").setParameter("yearMonth", yearMonth).getResultList();
		logger.info("Return the available menu item details");
		return menuItems;
	}
	
	/**This method used for get the the available menu item details based on selected date and mealSchoolId**/
	@Override
	public List<Object[]> menuItemDetailsV3(String selectedDate, Long mealSchoolId, ItemTypeConstants menuType, Long locationId) throws Exception {
		List<Object[]> menuItems = null;
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
		String isPublished = "0,1";
		List<String> itemTypes = null;
		switch (menuType.toString().toUpperCase()) {
			case "LUNCH": itemTypes = Arrays.asList(MealType.MEAL.toString(), MealType.SIDE.toString(), MealType.HOLIDAY.toString(), MealType.EXTRA.toString()); 
				if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Lunch Order Management") != null 
					&& mealSchool.getModuleAccess().get("Lunch Order Management").equalsIgnoreCase("Yes"))
					isPublished = "1";
				break;
			case "SNACK": itemTypes = Arrays.asList(MealType.SNACK.toString(), MealType.SIDE.toString(), MealType.HOLIDAY.toString(), MealType.EXTRA.toString()); 
				if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Snack Order Management") != null 
					&& mealSchool.getModuleAccess().get("Snack Order Management").equalsIgnoreCase("Yes"))
					isPublished = "1";
				break;
			case "DINNER": itemTypes = Arrays.asList(MealType.DINNER.toString(), MealType.SIDE.toString(), MealType.HOLIDAY.toString(), MealType.EXTRA.toString()); break;
			case "BREAKFAST": itemTypes= Arrays.asList(MealType.BREAKFAST.toString(), MealType.SIDE.toString(), MealType.HOLIDAY.toString(),MealType.EXTRA.toString()); 
				if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Breakfast Order Management") != null 
						&& mealSchool.getModuleAccess().get("Breakfast Order Management").equalsIgnoreCase("Yes"))
					isPublished = "1";
				break;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yearMonth = new SimpleDateFormat("yyyyMM").format(sdf.parse(selectedDate));
		String qry = "Select DISTINCT mm.id, mm.longDescription, c.price, c.reducedPrice, mm.name, mm.category, "
				+ "sms.reducedPriceStatus, smg.grades_name from meal_calendar c Inner Join meal_calendar_summary sms "
				+ "on c.meal_calendar_summary_id = sms.id Inner Join menu_items mm on "
				+ "c.menu_item_id = mm.id Inner Join meal_summary_grades smg on sms.id = smg.meal_calendar_summary_id "
				+ "LEFT JOIN menuItem_posLocation p ON mm.id = p.menuId where sms.mealSchool_schoolId = :mealSchoolId "
				+ "and mm.category IN (:itemTypes) and sms.mealType = :menuType and sms.yearMonth = :yearMonth and "
				+ "(c.date = :mealDate or c.date is null) and c.isActive = true AND (mm.category != 'EXTRA' OR :locationId IS NULL "
				+ "OR (mm.category = 'EXTRA' AND p.locationId = :locationId)) and sms.isPublished IN ("+isPublished+")";
		menuItems = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("menuType", menuType.toString())
				.setParameter("mealDate", selectedDate+" 00:00:00").setParameter("itemTypes", itemTypes).setParameter("yearMonth", yearMonth)
				.setParameter("locationId", locationId).getResultList();
		logger.info("Return the available menu item details");
		return menuItems;
	}

	/**This method used for for transfer the account balance between siblings**/
	@Override
	public MasterTransactionsAudit transferBalanceSibling(AccBalanceTransferSibling accBalanceTransferSibling) {
		MasterTransactionsAudit sourceMasterTransactionsAudit = accBalanceTransferSibling.getSourceTransferTransaction();
		entityManager.persist(sourceMasterTransactionsAudit);
		MasterTransactionsAudit targetMasterTransactionsAudit = accBalanceTransferSibling.getTragetTransferTransaction();
		targetMasterTransactionsAudit.setTransferSourceRecId(sourceMasterTransactionsAudit.getRecId());

		MasterTransactionsAudit targetMasterTransactionsAuditResp = targetMasterTransactionsAudit;
		entityManager.persist(targetMasterTransactionsAudit);
		targetMasterTransactionsAuditResp.setRecId(targetMasterTransactionsAudit.getRecId());
		StudentUser studentUser = null;
		for(StudentWiseTransaction studentWiseTransaction: sourceMasterTransactionsAudit.getStudentWiseTransactions()){
			studentUser = studentWiseTransaction.getStudentUser();
			studentUser.setModifiedBy(sourceMasterTransactionsAudit.getCreatedBy());
			studentUser.setModifiedOn(new Date());
			studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", studentWiseTransaction.getFinalBalance())));
			entityManager.merge(studentUser);
		}
		for(StudentWiseTransaction studentWiseTransaction: targetMasterTransactionsAudit.getStudentWiseTransactions()){
			studentUser = studentWiseTransaction.getStudentUser();
			studentUser.setModifiedBy(targetMasterTransactionsAudit.getCreatedBy());
			studentUser.setModifiedOn(new Date());
			studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", studentWiseTransaction.getFinalBalance())));
			entityManager.merge(studentUser);
		}
		logger.info("Transfer amount between Siblings completed successfully");
		return targetMasterTransactionsAuditResp;
	}

	/**This method used for get the purchased lunch/breakfast/Milk students details**/
	@Override
	public List<BigInteger> menuPurchasedStudents(Long mealSchoolId, String startDateTime, String endDateTime,
			String menuType, String transactionType, String itemType) {
		Query query = entityManager.createNativeQuery("select swt.studentUser_userId from MasterTransactionsAudit mta Inner Join StudentWiseTransactions swt"
				+ " on mta.recId = swt.MasterTransactionsAudit_RecId where mta.mealSchool_schoolId = :mealSchoolId and "
				+ "mta.transactionType = :transactionType and mta.purchaseItemType = :menuType and isPosted = 1 and (swt.mealType is null or swt.mealType = :itemType) "
				+ "and (mta.transactionDateTime between :startDateTime and :endDateTime)")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("transactionType", transactionType)
				.setParameter("menuType", menuType).setParameter("itemType", itemType).setParameter("startDateTime", startDateTime)
				.setParameter("endDateTime", endDateTime);
		logger.info("menuPurchasedStudents dao method executed successfully");
		return query.getResultList();
	}

	/**This method used for get all the students who ordered lunch but not served to the students**/
	@Override
	public List<Object[]> lunchNotServedStudents(String selectedDate, Long mealSchoolId, String startDateTime, 
			String endDateTime) throws Exception {
		List<Object[]> orderedItems = null;
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String qry = "Select o.studentRecId, o.mealName, su.firstName, su.gradeName, su.lastName, su.studentId from "
				+ "OrderMealItemsDetailReport o Inner Join StudentUser_v2 su on o.studentRecId=su.userId where o.mealSchoolId = :mealSchoolId and o.mealDate = :selectedDate and "
				+ "o.mealType = 'MEAL' and NOT EXISTS (select null from MasterTransactionsAudit mta Inner Join "
				+ "StudentWiseTransactions swt on mta.recId = swt.MasterTransactionsAudit_RecId where "
				+ "swt.studentUser_userId = o.studentRecId and swt.isPosted=true and mta.mealSchool_schoolId = :mealSchoolId and "
				+ "mta.transactionType = 'Purchase' and mta.purchaseItemType = 'Lunch' and (mta.transactionDateTime between "
				+ ":startDateTime and :endDateTime))";
		orderedItems = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("selectedDate", selectedDate+" 00:00:00").setParameter("startDateTime", startDateTime)
				.setParameter("endDateTime", endDateTime).getResultList();
		logger.info("Return the student details who ordered lunch but not served yet.");
		return orderedItems;
	}

	/**This method used for add/overwrite the balance of students**/
	@Override
	public ServiceResponse importStudentsBalance(List<StudentBalanceImportResp> studentBalanceImportList,
			String loggedUser, Long mealSchoolId, Integer schoolYear, String type) {
		ServiceResponse serviceResponse = new ServiceResponse();
		StudentUser studentUser = null;
		Double accBalance = 0.0;
		Double finalAmt = 0.0;
		int count = 0;
		Map<String, String> failedData = new HashMap<String, String>();
		Boolean isAdd = null;
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
		MasterTransactionsAudit masterTransactionsAudit = null;
		Set<StudentWiseTransaction> studentWiseTransactions = null;
		for(StudentBalanceImportResp studentBal : studentBalanceImportList){
			studentUser = studentUserRepository.findByMealSchoolSchoolIdAndStudentIdAndSchoolYearAndIsActive(mealSchoolId, 
					studentBal.getStudentId(), schoolYear, true);
			if(studentUser == null || studentUser.getUserId() == null){
				String inavlidIds = (failedData.get("InvalidStudentIds") != null ? failedData.get("InvalidStudentIds")+","
						:"")+studentBal.getStudentId();
				failedData.put("InvalidStudentIds", inavlidIds);
				continue;
			}
			accBalance = studentUser.getAccBalance();
			if(type.equalsIgnoreCase("Add")){
				studentUser.setAccBalance(Double.parseDouble(String.format("%.2f", accBalance
						+studentBal.getBalance())));
				isAdd = true;
			}else if(type.equalsIgnoreCase("Overwrite")){
				studentUser.setAccBalance(studentBal.getBalance());
				isAdd = false;
			}else{
				String invalidProcessType =  (failedData.get("InvalidProcessTypeStudentsIds") != null ? 
						failedData.get("InvalidProcessTypeStudentsIds")+",":"")+studentBal.getStudentId();
				failedData.put("InvalidProcessTypeStudentsIds", invalidProcessType);
				continue;
			}
			studentUser.setModifiedBy(loggedUser);
			studentUser.setModifiedOn(new Date());
			masterTransactionsAudit = new MasterTransactionsAudit();
			masterTransactionsAudit.setMealSchool(mealSchool);
			masterTransactionsAudit.setNote(isAdd?"Added through balance import process":
				"Overwritten through balance import process");
			masterTransactionsAudit.setTransactionDateTime(new Date());
			masterTransactionsAudit.setCreatedBy(loggedUser);
			masterTransactionsAudit.setCreatedOn(new Date());
			studentWiseTransactions = new HashSet<StudentWiseTransaction>();
			StudentWiseTransaction studentWiseTransaction = new StudentWiseTransaction();
			studentWiseTransaction.setStudentUser(studentUser);
			studentWiseTransaction.setStudentFName(studentUser.getFirstName());
			studentWiseTransaction.setStudentLName(studentUser.getLastName());
			studentWiseTransaction.setFinalBalance(studentUser.getAccBalance());
			studentWiseTransaction.setGrade(studentUser.getGradeName());
			if(isAdd){
				studentWiseTransaction.setTransactionAmount(studentBal.getBalance());
				masterTransactionsAudit.setPaymentType(PaymentType.ImportBalanceCR);
			}else{
				finalAmt = Double.parseDouble(String.format("%.2f", studentBal.getBalance()
						-accBalance));
				if(finalAmt == 0.0)
					continue;
				if(finalAmt > 0){
					masterTransactionsAudit.setPaymentType(PaymentType.ImportBalanceCR);
					studentWiseTransaction.setTransactionAmount(finalAmt);
				}else{
					masterTransactionsAudit.setPurchaseItemType(PurchaseItemType.ImportBalanceDR);
					studentWiseTransaction.setTransactionAmount(-finalAmt);
				}
			}
			masterTransactionsAudit.setTotalTransactionAmount(studentWiseTransaction.getTransactionAmount());
			studentWiseTransactions.add(studentWiseTransaction);
			masterTransactionsAudit.setTransactionType(TransactionType.ImportBalance);
			masterTransactionsAudit.setTransactionDescription("Amount adjusted through import process");
			/*if(studentUser.getParentuser().getParentAltEmail() != null && 
					!studentUser.getParentuser().getParentAltEmail().isEmpty())
				masterTransactionsAudit.setParentUserEmails(studentUser.getParentuser().getUserName()+","+
						studentUser.getParentuser().getParentAltEmail());
			else
				masterTransactionsAudit.setParentUserEmails(studentUser.getParentuser().getUserName());*/
			masterTransactionsAudit.setStudentWiseTransactions(studentWiseTransactions);
			entityManager.persist(masterTransactionsAudit);
			entityManager.merge(studentUser);
			count++;
		}
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage(count+" Students lunch balances imported successfully.");
		serviceResponse.setMapKeyVal(failedData);
		logger.info("Invalid Student Ids:"+failedData.get("InvalidStudentIds"));
		logger.info("Invalid Process Type Student Ids:"+failedData.get("InvalidProcessTypeStudentsIds"));
		return serviceResponse;
	}

	/**This method used for get the transaction details based on transaction id**/
	@Override
	public List<Object[]> getPurchaseTrx(Long trxId) {
		List<Object[]> transactionDetail = entityManager.createNativeQuery("select mta.purchaseItemType,Date(mta.transactionDateTime),"
				+ "swt.transactionAmount,swt.studentUser_userId,mta.paymentType,mta.transactionType from MasterTransactionsAudit mta inner join StudentWiseTransactions swt on mta.recId=swt.MasterTransactionsAudit_RecId "
				+ "where swt.recId=:trxId and swt.isPosted=true AND (mta.paymentType is null OR mta.paymentType != 'Online') AND mta.transactionType IN ('Purchase','Deposit','InstantPayment')").setParameter("trxId", trxId).getResultList();
		return transactionDetail;
	}

	/**This method used for deactivate the purchase transaction and create new adjustment transaction
	 * @throws Exception **/
	@Override
	public ServiceResponse removeAdjPurchaseTrx(MasterTransactionsAudit mta, Long trxId) throws Exception {
		int count = entityManager.createNativeQuery("update StudentWiseTransactions set isPosted = false where recId=:trxId")
				.setParameter("trxId", trxId).executeUpdate();
		if(count > 0){
			MasterTransactionsAudit mtaFinal = depositAmtThruOffline(mta);
			if(mtaFinal.getRecId() == null || mtaFinal.getRecId() == 0)
				throw new Exception("Failed to delete the transaction.");
		}else
			throw new Exception("Failed to delete the transaction.");
		ServiceResponse serviceResponse = new ServiceResponse();
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("Transaction deleted successfully.");
		return serviceResponse;
	}

	/**This method used for purchase the package**/
	@Override
	public PackageSubscriptionsTrx subscribePackage(PackageSubscriptionsTrx packageSubscriptionsTrx) {
		/*List<PickupAuthorized> pickupAuthorizeds = new ArrayList<>();
		for(SubscriptionsTrxByStd trx : packageSubscriptionsTrx.getSubscriptionsTrxByStds()){
			List<PickupAuthorized> pp = trx.getPickupAuthorizeds();
			pp.stream().forEach(p->p.setStdRecId(trx.getStudentUser().getUserId()));
			pickupAuthorizeds.addAll(pp);
		}*/
		if(packageSubscriptionsTrx.getPickupAuthorizeds() != null)
			authorizePP(packageSubscriptionsTrx.getPickupAuthorizeds());
		entityManager.persist(packageSubscriptionsTrx);
		return packageSubscriptionsTrx;
	}

	/**This method used for audit the BC & AC package subscriptions**/
	@Override
	public ServiceResponse bcAcSubscriptionsAudit(List<BCACAudit> bcacAudits) {
		ServiceResponse serviceResponse = new ServiceResponse();
		String loggedUser = "";
		if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
			loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
		for(BCACAudit bcacAudit : bcacAudits){
			if(bcacAudit.getBcacAuditID() == null || bcacAudit.getBcacAuditID() == 0){
				bcacAudit.setCreatedOn(new Date());
				bcacAudit.setCreatedBy(loggedUser);
			}else{
				bcacAudit.setModifiedOn(new Date());
				bcacAudit.setModifiedBy(loggedUser);
			}
			entityManager.merge(bcacAudit);
		}
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("BC & AC package subscriptions audited successfully.");
		return serviceResponse;
	}

	@Override
	public ServiceResponse authorizePP(List<PickupAuthorized> pickupAuthorizeds) {
		ServiceResponse serviceResponse = new ServiceResponse();
		for(PickupAuthorized p : pickupAuthorizeds){
			entityManager.merge(p);
		}
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		serviceResponse.setStatusMessage("Authorized pickup persons details updated successfully.");
		return serviceResponse;
	}

	/**This method used for update the package due payment status**/
	@Override
	public PackageSubscriptionsTrx pkgDuePayment(Long masterPkgTrxId, PaymentType type, String refId, String loggedUser) {
		entityManager.createNativeQuery("Update PackageSubscriptionsTrx set paymentType=:type, checkNumb=:refId, "
				+ "modifiedBy=:loggedUser, isPaid=true, modifiedOn=now() where trxId=:masterPkgTrxId").setParameter("type", type.toString())
			.setParameter("refId", refId).setParameter("loggedUser", loggedUser).setParameter("masterPkgTrxId", masterPkgTrxId).executeUpdate();
		return entityManager.find(PackageSubscriptionsTrx.class, masterPkgTrxId);
	}

	/**This method used for update the package payment status**/
	@Override
	public PackageSubscriptionsTrx pkgPayment(Long masterPkgTrxId, PackageSubscriptionsTrx pkgTrx) {
		entityManager.createNativeQuery("Update PackageSubscriptionsTrx set paymentType=:type, chargeId=:chargeId, transferId=:transferId, "
				+ "paymentGateway=:paymentGateway,modifiedBy=:loggedUser, isPaid=true, modifiedOn=now() where trxId=:masterPkgTrxId").setParameter("type", pkgTrx.getPaymentType().toString())
			.setParameter("chargeId", pkgTrx.getChargeId()).setParameter("transferId", pkgTrx.getTransferId()).setParameter("paymentGateway", pkgTrx.getPaymentGateway())
			.setParameter("loggedUser", pkgTrx.getLoggedUser()).setParameter("masterPkgTrxId", masterPkgTrxId).executeUpdate();
		return entityManager.find(PackageSubscriptionsTrx.class, masterPkgTrxId);
	}
}
