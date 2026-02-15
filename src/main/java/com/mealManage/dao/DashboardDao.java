package com.mealManage.dao;

import java.util.Date;
import java.util.List;

import com.mealManage.mealmodel.meal.ItemTypeConstants;

public interface DashboardDao {
	
	public List<Object[]> ordersCountByMonth(Long mealSchoolId, String yearMonthStart, String yearMonthEnd, ItemTypeConstants menuType, Boolean isCaterer);
	
	public List<Object[]> nMostOrderedItems(Long mealSchoolId, Date startDate, Date endDate, Long requiredTopItems, ItemTypeConstants menuType, Boolean isCaterer);
	
	public List<Object[]> negativeBalanceStudents(Long mealSchoolId, Integer schoolYear);
	
	public List<Object[]> balancePaymentTrend(Long mealSchoolId, String startDate, String endDate,Integer schoolYear);
	
	public List<Object[]> balancePaymentTrendByDist(String startDate, String endDate,Integer schoolYear, List<Long> schoolIds);
	
	public List<Object[]> balanceByGrade(Long mealSchoolId, Integer schoolYear);
	
	public List<Object[]> studentsEligibilty(Long mealSchoolId, Integer schoolYear);

}
