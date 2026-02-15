package com.mealManage.controller;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.response.ServiceResponse;
import com.mealManage.service.DashboardService;

/**
 * Dashboard chart related APIs 
 */
@RestController
@RequestMapping("mealManage/dashboard")
public class DashboardController {
	
	@Autowired
	private DashboardService dashboardService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**This API used for get the total orders count by month**/
	@GetMapping("ordersCountByMonth")
	public Map<String, Long> ordersCountByMonth(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestParam(value="yearMonthStart", required=true) String yearMonthStart, @RequestParam(value="yearMonthEnd", required=true) String yearMonthEnd, 
			@RequestParam(value="menuType", required=false) ItemTypeConstants menuType, @RequestParam(value="isCaterer", required=false) Boolean isCaterer){
		logger.info("Invoking the API for get the orders count by month");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		return dashboardService.ordersCountByMonth(mealSchoolId, yearMonthStart, yearMonthEnd, menuType, isCaterer);
	}
	
	/**This API used for get the top most ordered items with their respective count**/
	@GetMapping("nMostOrderedItems")
	public Map<String, Long> nMostOrderedItems(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestParam(value="startDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@RequestParam(value="endDate", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
			@RequestParam(value="requiredTopItems", required=true) Long requiredTopItems, @RequestParam(value="menuType", required=false) ItemTypeConstants menuType, 
			@RequestParam(value="isCaterer", required=false) Boolean isCaterer){
		logger.info("Invoking the API for get the n top ordered items with their respective count");
		if(menuType == null)
			menuType = ItemTypeConstants.Lunch;
		return dashboardService.nMostOrderedItems(mealSchoolId, startDate, endDate, requiredTopItems, menuType, isCaterer);
	}
	
	/**This API used for get the negative balance students count by grade**/
	@GetMapping("negativeBalanceStudents")
	public Map<String, Long> negativeBalanceStudents(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestParam(value="schoolYear", required=true) Integer schoolYear){
		logger.info("Invoking API for get the negative balance students count by grade");
		return dashboardService.negativeBalanceStudents(mealSchoolId, schoolYear);
	}
	
	/**This API used for get the total transactions count by payment type with the respective total amount
	 * @throws Exception **/
	@GetMapping("balancePaymentTrend")
	public ResponseEntity<Object>  balancePaymentTrend(@RequestParam(value="mealSchoolId", required=true) 
			Long mealSchoolId, @RequestParam(value="startDate", required = true) String startDate, 
			@RequestParam(value="endDate", required = true) String endDate, HttpServletResponse httpServletResponse,
			@RequestParam(value="fileExport", required=false) Boolean fileExport, @RequestParam Integer schoolYear, 
			@RequestParam(value="districtId", required=false) Long districtId) throws Exception{
		logger.info("Invoking API for get the balance payment trend data");
		Object obj = dashboardService.balancePaymentTrend(mealSchoolId, startDate, endDate, httpServletResponse, fileExport,schoolYear, districtId);
		if(fileExport != null && fileExport){
			ServiceResponse serviceResponse = (ServiceResponse) obj;			
			return new ResponseEntity<Object>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatusCode()));
		}else
			return new ResponseEntity<Object>(obj, HttpStatus.valueOf(200));
	}
	
	/**This API used for get the total available balance of students with their respective counts by grade**/
	@GetMapping("balanceByGrade")
	public Map<String, Map<String, Double>> balanceByGrade(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestParam(value="schoolYear", required=true) Integer schoolYear){
		logger.info("Invoking API for get the total available balance of students with their respective count by grade");
		return dashboardService.balanceByGrade(mealSchoolId, schoolYear);
	}
	
	/**This API used for get the free lunch eligible, reduced price eligible and regular price eligible students count by grade**/
	@GetMapping("studentsEligibilty")
	public EnumMap<SchoolGrades, Map<String, Long>> studentsEligibilty(@RequestParam(value="mealSchoolId", required=true) Long mealSchoolId, 
			@RequestParam(value="schoolYear", required=true) Integer schoolYear){
		logger.info("Invoking API for get the free lunch, reduced price and regular price eligibility students count by grade");
		return dashboardService.studentsEligibilty(mealSchoolId, schoolYear);
	}

}
