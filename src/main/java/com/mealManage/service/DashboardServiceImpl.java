package com.mealManage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mealManage.dao.DashboardDao;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.response.ServiceResponse;
import com.mealManage.util.DepositSummaryReportUtil;

@Service
/**This class implemented by DashboardService interface for the dashboard chart**/
public class DashboardServiceImpl implements DashboardService {
	
	@Autowired
	private DashboardDao dashboardDao;
	@Autowired
	private DepositSummaryReportUtil depositSummaryReportUtil;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;

	/**This method used for get the total order's count by month**/
	@Override
	public Map<String, Long> ordersCountByMonth(Long mealSchoolId, String yearMonthStart, String yearMonthEnd, ItemTypeConstants menuType, Boolean isCaterer) {
		List<Object[]> ordersByMonthObj  = dashboardDao.ordersCountByMonth(mealSchoolId, yearMonthStart, yearMonthEnd, menuType, isCaterer);
		Map<String, Long> ordersByMonth = buildCountByReqKey(ordersByMonthObj);
		logger.info("ordersCountByMonth service method executed successfully");
		return ordersByMonth;
	}

	/**This method used for get the top n most ordered items with the respective count**/
	@Override
	public Map<String, Long> nMostOrderedItems(Long mealSchoolId, Date startDate, Date endDate, Long requiredTopItems, ItemTypeConstants menuType, Boolean isCaterer) {
		List<Object[]> mostOrderedItemsObj  = dashboardDao.nMostOrderedItems(mealSchoolId, startDate, endDate, requiredTopItems, menuType, isCaterer);
		Map<String, Long> nMostOrderedItemsMap = buildCountByReqKey(mostOrderedItemsObj);
		logger.info("nMostOrderedItems service method executed successfully");
		return nMostOrderedItemsMap;
	}
	
	/**This method used for generate the negative balance students dashboard report**/
	@Override
	public Map<String, Long> negativeBalanceStudents(Long mealSchoolId, Integer schoolYear) {
		List<Object[]> negativeBalanceStudentsObj  = dashboardDao.negativeBalanceStudents(mealSchoolId, schoolYear);
		Map<String, Long> negativeBalanceStudentsMap = buildCountByReqKey(negativeBalanceStudentsObj);
		logger.info("negativeBalanceStudents service method executed successfully");
		return negativeBalanceStudentsMap;
	}
	
	/**This method used for get the balance payment trend report data
	 * @throws Exception **/
	@Override
	public Object balancePaymentTrend(Long mealSchoolId, String startDate, String endDate,
			HttpServletResponse httpServletResponse, Boolean fileExport,Integer schoolYear, Long districtId) throws Exception {
		List<Long> schoolIds = new ArrayList<Long>();
		if((mealSchoolId == null || mealSchoolId == 0) && districtId != null)
			schoolIds = mealSchoolRepository.getSchoolIdsByDistrictId(districtId);
		List<Object[]> balancePaymentTrendsObj = null;
		if(mealSchoolId != null && mealSchoolId != 0)
			balancePaymentTrendsObj = dashboardDao.balancePaymentTrend(mealSchoolId, startDate, endDate,schoolYear);
		else if(districtId != null)
			balancePaymentTrendsObj = dashboardDao.balancePaymentTrendByDist(startDate, endDate, schoolYear, schoolIds);
		Map<String, Map<String, Double>> paymentTrends = buildPaymentTrend(balancePaymentTrendsObj);
		String currencySymbol = countryDetailsRepository.getCurrencySymbol(mealSchoolRepository.getSchoolCountry(mealSchoolId));
		logger.info("balancePaymentTrend service method executed successfully");
		if(fileExport != null && fileExport){
			ServiceResponse serviceResponse = new ServiceResponse();
			if(paymentTrends == null || paymentTrends.size() < 1){
				serviceResponse.setStatus("Fail");
				serviceResponse.setStatusCode(417);
				serviceResponse.setStatusMessage("No data available.");
				return serviceResponse;
			}
			depositSummaryReportUtil.depositSummary(paymentTrends, httpServletResponse, startDate, endDate, mealSchoolId,currencySymbol);
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Deposit summary report generated successfully.");
			return serviceResponse;
		}else
			return paymentTrends;
	}
	
	/**This method used for get the total available balance of student with their respective count by grade**/
	@Override
	public Map<String, Map<String, Double>> balanceByGrade(Long mealSchoolId, Integer schoolYear) {
		List<Object[]> balanceByGradeObj  = dashboardDao.balanceByGrade(mealSchoolId, schoolYear);
		Map<String, Map<String, Double>> balanceByGradeMap = buildBalanceByGrade(balanceByGradeObj);
		logger.info("balanceByGrade service method executed successfully");
		return balanceByGradeMap;
	}
	
	/**This method used for get the students eligiblity count by garde**/
	@Override
	public EnumMap<SchoolGrades, Map<String, Long>> studentsEligibilty(Long mealSchoolId, Integer schoolYear) {
		List<Object[]> studentsEligibilityObj  = dashboardDao.studentsEligibilty(mealSchoolId, schoolYear);
		EnumMap<SchoolGrades, Map<String, Long>> studentsEligMap = buildStudentsEligibilty(studentsEligibilityObj);
		logger.info("studentsEligibilty service method executed successfully");
		return studentsEligMap;
	}
	
	/**This method used for build the orders count by month**/
	/*private Map<String, Long> buildOrdersByMonth(List<Object[]> ordersByMonthObj){
		Map<String, Long> ordersByMonth = new HashMap<String, Long>();
		if(ordersByMonthObj != null && ordersByMonthObj.size() > 0){
			for(Object[] obj : ordersByMonthObj){
				if(obj[0] != null && obj[1] != null){
					ordersByMonth.put(obj[1].toString(), Long.parseLong(obj[0].toString()));
				}
			}
		}
		return ordersByMonth;
	}*/
	
	/**This method used for build the key & value to get the dashboard report**/
	private Map<String, Long> buildCountByReqKey(List<Object[]> objArrayList){
		Map<String, Long> keyValMap = new TreeMap<String, Long>();
		if(objArrayList != null && objArrayList.size() > 0){
			for(Object[] obj : objArrayList){
				if(obj[0] != null && obj[1] != null){
					keyValMap.put(obj[1].toString(), Long.parseLong(obj[0].toString()));
				}
			}
		}
		return keyValMap;
	}
	
	/**This method used for build the payment trend object data**/
	private Map<String, Map<String, Double>> buildPaymentTrend(List<Object[]> paymentTrendObj){
		Map<String, Map<String, Double>> paymentTrendMap = new TreeMap<String, Map<String, Double>>();
		Map<String, Double> countAndAmtMap = null;
		if(paymentTrendObj != null && paymentTrendObj.size() > 0){
			for(Object[] obj : paymentTrendObj){
				if(obj[0] != null && obj[1] != null && obj[2] != null){
					countAndAmtMap = new HashMap<String, Double>();
					countAndAmtMap.put("totalTransactions", Double.parseDouble(obj[0].toString()));
					countAndAmtMap.put("totalAmount", Double.parseDouble(String.format("%.2f", 
							Double.parseDouble(obj[1].toString()))));
					paymentTrendMap.put(obj[2].toString(), countAndAmtMap);
				}
			}
		}
		return paymentTrendMap;
	}
	
	/**This method used for build the account balance of students with the count by grade**/
	private Map<String, Map<String, Double>> buildBalanceByGrade(List<Object[]> balanceAmtByGradeObj){
		Map<String, Map<String, Double>> balanceByGradeMap = new TreeMap<String, Map<String, Double>>();
		Map<String, Double> countAndAmtMap = null;
		if(balanceAmtByGradeObj != null && balanceAmtByGradeObj.size() > 0){
			for(Object[] obj : balanceAmtByGradeObj){
				if(obj[0] != null && obj[1] != null && obj[2] != null){
					countAndAmtMap = new HashMap<String, Double>();
					countAndAmtMap.put("totalStudents", Double.parseDouble(obj[0].toString()));
					countAndAmtMap.put("totalAmount", Double.parseDouble(String.format("%.2f", 
							Double.parseDouble(obj[1].toString()))));
					balanceByGradeMap.put(obj[2].toString(), countAndAmtMap);
				}
			}
		}
		return balanceByGradeMap;
	}
	
	/**This method used for build the students eligibility data**/
	private EnumMap<SchoolGrades, Map<String, Long>> buildStudentsEligibilty(List<Object[]> studentsEligibilityObj){
		EnumMap<SchoolGrades, Map<String, Long>> studentsEligibilityMap = new EnumMap<>(SchoolGrades.class);
		Map<String, Long> countMap = null;
		if(studentsEligibilityObj != null && studentsEligibilityObj.size() > 0){
			for(Object[] obj : studentsEligibilityObj){
				if(obj[0] != null && obj[1] != null && obj[2] != null && obj[3] != null){
					countMap = new HashMap<String, Long>();
					countMap.put("freeLunchEligible", Long.parseLong(obj[0].toString()));
					countMap.put("reducedPriceEligible", Long.parseLong(obj[1].toString()));
					countMap.put("totalStudents", Long.parseLong(obj[2].toString()));
					studentsEligibilityMap.put(SchoolGrades.valueOf(obj[3].toString()), countMap);
				}
			}
		}
		return studentsEligibilityMap;
	}
	
}
