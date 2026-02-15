package com.mealManage.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.transaction.TransactionType;
import com.mealManage.util.CommonUtil;

/**This class implemented by DashboardDao interface for the dashboard chart**/
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class DashboardDaoImpl implements DashboardDao{
	
	// Create entityManager persistence context reference
	@PersistenceContext
	private EntityManager entityManager;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**This method used for get the total order count by month**/
	@Override
	public List<Object[]> ordersCountByMonth(Long mealSchoolId, String yearMonthStart, String yearMonthEnd, ItemTypeConstants menuType, Boolean isCaterer) {
		String qry = "Select count(o.recNo), o.yearMonth from OrderMealsReport o inner join StudentUser_v2 su on o.studentRecId=su.userId "
				+ "Inner Join MealSchool_v2 ms on o.mealSchoolId = ms.schoolId where su.isActive=true and o.yearMonth >= :yearMonthStart and "
				+ "o.yearMonth <= :yearMonthEnd and o.menuType = :menuType ";
		if(isCaterer != null && isCaterer)
			qry = qry + " and ms.catererId = :mealSchoolId";
		else
			qry = qry + " and ms.schoolId = :mealSchoolId";
		qry = qry + " group by o.yearMonth order by o.yearMonth asc";
		
		List<Object[]> obj = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("yearMonthStart", yearMonthStart).setParameter("yearMonthEnd", yearMonthEnd).setParameter("menuType", menuType.toString()).getResultList();
		logger.info("ordersCountByMonth dao method executed successfully");
		return obj;
	}

	/**This method used for get the top n most ordered items with their respective count**/
	@Override
	public List<Object[]> nMostOrderedItems(Long mealSchoolId, Date startDate, Date endDate, Long requiredTopItems, ItemTypeConstants menuType, Boolean isCaterer) {
		String itemType = CommonUtil.getItemType(menuType);
		String qry = "Select count(o.id), o.mealName from OrderMealItemsDetailReport o inner join MealSchool_v2 ms on o.mealSchoolId = ms.schoolId "
				+ " where o.mealType = :menuType and (Date(o.mealDate) between :startDate and :endDate)";
		if(isCaterer != null && isCaterer)
			qry = qry + " and ms.catererId = :mealSchoolId";
		else
			qry = qry + " and ms.schoolId = :mealSchoolId";
		qry = qry + " group by o.mealName order by count(o.id) desc limit :requiredTopItems";
		List<Object[]> obj = entityManager.createNativeQuery(qry)
				.setParameter("mealSchoolId", mealSchoolId).setParameter("menuType", itemType).setParameter("startDate", startDate)
				.setParameter("endDate", endDate).setParameter("requiredTopItems", requiredTopItems).getResultList();
		logger.info("nMostOrderedItems dao method executed successfully");
		return obj;
	}

	/**This method used for get the low balance students count with the garde**/
	@Override
	public List<Object[]> negativeBalanceStudents(Long mealSchoolId, Integer schoolYear) {
		List<Object[]> obj = entityManager.createNativeQuery("Select count(su.userId), su.gradeName from StudentUser_v2 su "
				+ "where su.mealSchool_schoolId = :mealSchoolId and su.schoolYear = :schoolYear and su.isActive = true and "
				+ " su.accBalance < 0 group by su.gradeName")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("schoolYear", schoolYear).getResultList();
		logger.info("negativeBalanceStudents dao method executed successfully");
		return obj;
	}
	
	/**This method used for get payment trend data**/
	@Override
	public List<Object[]> balancePaymentTrend(Long mealSchoolId, String startDate, String endDate,Integer schoolYear) {
		List<String> transactionType = Arrays.asList(TransactionType.Deposit.toString(), TransactionType.InstantPayment.toString());
		List<Object[]> obj = entityManager.createNativeQuery("select count(mta.recId), sum(swt.transactionAmount), "
				+ "mta.paymentType from MasterTransactionsAudit mta INNER JOIN StudentWiseTransactions swt ON"
				+ " mta.recId=swt.MasterTransactionsAudit_RecId INNER JOIN StudentUser_v2 su on swt.studentUser_userId=su.userId "
				+ "where mta.mealSchool_schoolId = :mealSchoolId and "
				+ "mta.transactionType IN (:transactionType) and (mta.transactionDateTime between :startDate and "
				+ ":endDate) and mta.paymentType is not null and mta.paymentType != 'TransferCR' and mta.paymentType != 'Wallet' and"
				+ " su.schoolYear = :schoolYear and swt.isPosted=true group by mta.paymentType")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("transactionType", transactionType)
				.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " "))
				.setParameter("schoolYear", schoolYear).getResultList();
		logger.info("balancePaymentTrend dao method executed successfully");
		return obj;
	}
	
	/**This method used for get payment trend data by district**/
	@Override
	public List<Object[]> balancePaymentTrendByDist(String startDate, String endDate, Integer schoolYear,
			List<Long> schoolIds) {
		List<String> transactionType = Arrays.asList(TransactionType.Deposit.toString(), TransactionType.InstantPayment.toString());
		List<Object[]> obj = entityManager.createNativeQuery("select count(mta.recId), sum(swt.transactionAmount), "
				+ "mta.paymentType from MasterTransactionsAudit mta INNER JOIN StudentWiseTransactions swt ON"
				+ " mta.recId=swt.MasterTransactionsAudit_RecId INNER JOIN StudentUser_v2 su on swt.studentUser_userId=su.userId where mta.mealSchool_schoolId IN (:schoolIds) and "
				+ "mta.transactionType IN (:transactionType) and (mta.transactionDateTime between :startDate and "
				+ ":endDate) and mta.paymentType is not null and mta.paymentType = 'Online' and su.schoolYear = :schoolYear and swt.isPosted=true group by mta.paymentType")
				.setParameter("schoolIds", schoolIds).setParameter("transactionType", transactionType)
				.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " "))
				.setParameter("schoolYear", schoolYear).getResultList();
		logger.info("balancePaymentTrend dao method executed successfully");
		return obj;
	}

	/**This method used for get the available students with their count by grade**/
	@Override
	public List<Object[]> balanceByGrade(Long mealSchoolId, Integer schoolYear) {
		List<Object[]> obj = entityManager.createNativeQuery("Select count(su.userId), sum(su.accBalance), su.gradeName from "
				+ "StudentUser_v2 su where su.mealSchool_schoolId = :mealSchoolId and su.schoolYear = :schoolYear group by "
				+ "su.gradeName").setParameter("mealSchoolId", mealSchoolId).setParameter("schoolYear", schoolYear)
				.getResultList();
		logger.info("balanceByGrade dao method executed successfully");
		return obj;
	}

	/**This method used for get the students eligibility count by grade**/
	@Override
	public List<Object[]> studentsEligibilty(Long mealSchoolId, Integer schoolYear) {
		List<Object[]> obj = entityManager.createNativeQuery("select sum(su.isFreeMealEligible), sum(su.isReducePriceEligible),"
				+ " count(su.userId), su.gradeName from StudentUser_v2 su where su.mealSchool_schoolId = :mealSchoolId and "
				+ "su.schoolYear = :schoolYear group by su.gradeName").setParameter("mealSchoolId", mealSchoolId)
				.setParameter("schoolYear", schoolYear).getResultList();
		logger.info("studentsEligibilty dao method executed successfully");
		return obj;
	}

}
