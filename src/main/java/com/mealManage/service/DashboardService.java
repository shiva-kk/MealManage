package com.mealManage.service;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;

public interface DashboardService {
	
	public Map<String, Long> ordersCountByMonth(Long mealSchoolId, String yearMonthStart, String yearMonthEnd, ItemTypeConstants menuType, Boolean isCaterer);
	
	public Map<String, Long> nMostOrderedItems(Long mealSchoolId, Date startDate, Date endDate, Long requiredTopItems, ItemTypeConstants menuType, Boolean isCaterer);
	
	public Map<String, Long> negativeBalanceStudents(Long mealSchoolId, Integer schoolYear);
	
	public Object balancePaymentTrend(Long mealSchoolId, String startDate, String endDate, 
			HttpServletResponse httpServletResponse, Boolean fileExport,Integer schoolYear, Long districtId) throws Exception;
	
	public Map<String, Map<String, Double>> balanceByGrade(Long mealSchoolId, Integer schoolYear);
	
	public EnumMap<SchoolGrades, Map<String, Long>> studentsEligibilty(Long mealSchoolId, Integer schoolYear);

}
