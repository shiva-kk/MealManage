package com.mealManage.dao;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mealManage.domain.MealChangeNotificationRequest;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.transaction.TransactionType;
import com.mealManage.util.CommonUtil;

@Repository
@Transactional
@SuppressWarnings("unchecked")
/** This class implement by using ReportsDao Interface **/
public class ReportsDaoImpl implements ReportsDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// Create entityManager persistence context reference
	@PersistenceContext
	private EntityManager entityManager;
	
	/**This method used for get the total count of meal ordered Students with respective total amount by grade and payment status**/
	/*@Override
	public List<Object[]> orderedCountByGrade(Long mealSchoolId, String yearMonth) {
		List<Object[]> objList = null;
		objList = entityManager.createNativeQuery("Select count(o.recNo), sum(o.orderPrice), o.grade, o.paymentStatus From OrderMealsReport o "
				+ "where o.mealSchoolId = :mealSchoolId and o.yearMonth = :selectedMonth group by o.grade, o.paymentStatus")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("selectedMonth", yearMonth).getResultList();
		logger.info("orderedCountByGrade method executed successfully");	
		return objList;
	}*/
	
	/**This method used for get the total count of meal ordered Students by grade**/
	@Override
	public List<Object[]> orderedCountByGrade(Long mealSchoolId, String yearMonth, ItemTypeConstants menuType) {
		List<Object[]> objList = null;
		objList = entityManager.createNativeQuery("Select count(o.recNo), o.grade From OrderMealsReport o inner join StudentUser_v2 su on o.studentRecId=su.userId "
				+ "where su.isActive=true and o.mealSchoolId = :mealSchoolId and o.yearMonth = :selectedMonth and o.menuType = :menuType group by o.grade")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("selectedMonth", yearMonth).setParameter("menuType", menuType.toString()).getResultList();
		logger.info("orderedCountByGrade method executed successfully");	
		return objList;
	}
	
	/**This method used for get the total count of meal ordered Students by school**/
	@Override
	public List<Object[]> orderedCountBySchool(Long catererId, String yearMonth, ItemTypeConstants menuType) {
		List<Object[]> objList = null;
		objList = entityManager.createNativeQuery("Select count(o.recNo), o.mealSchoolId From OrderMealsReport o inner join StudentUser_v2 su on o.studentRecId=su.userId "
				+ "Inner Join MealSchool_v2 ms on o.mealSchoolId = ms.schoolId where su.isActive=true and ms.catererId = :catererId and o.yearMonth = :selectedMonth and o.menuType = :menuType group by o.mealSchoolId")
				.setParameter("catererId", catererId).setParameter("selectedMonth", yearMonth).setParameter("menuType", menuType.toString()).getResultList();
		logger.info("orderedCountBySchool method executed successfully");	
		return objList;
	}
	
	/**This method used for get the total count of not ordered Students by grade**/
	@Override
	public List<Object[]> notOrderedCountByGrade(Long mealSchoolId, String yearMonth, List<String> grades, Integer schoolYear) {
		List<Object[]> objList = null;
		if(grades != null && grades.size() > 0)
		objList = entityManager.createNativeQuery("Select count(su.userId), su.gradeName from StudentUser_v2 su where "
				+ "su.mealSchool_schoolId = :mealSchoolId and su.isActive =true and su.gradeName IN ("
				+ ":grades) and su.schoolYear = :schoolYear and NOT EXISTS (select null from OrderMealItemsDetailReport o where "
				+ "o.studentRecId = su.userId and o.yearMonth = :selectedMonth) group by su.gradeName")
		.setParameter("mealSchoolId", mealSchoolId).setParameter("selectedMonth", yearMonth).setParameter("grades", grades)
		.setParameter("schoolYear", schoolYear).getResultList();
		logger.info("notOrderedCountByGrade method executed successfully");	
		return objList;
	}
	
	/**This method used for the total students count**/
	@Override
	public List<Object[]> allStudentsCountByGrade(Long mealSchoolId, String yearMonth, List<String> grades,
			Integer schoolYear) {
		List<Object[]> objList = null;
		if(grades != null && grades.size() > 0)
		objList = entityManager.createNativeQuery("Select count(su.userId), su.gradeName from StudentUser_v2 su where "
				+ "su.mealSchool_schoolId = :mealSchoolId and su.isActive =true and su.gradeName IN ("
				+ ":grades) and su.schoolYear = :schoolYear group by su.gradeName")
		.setParameter("mealSchoolId", mealSchoolId).setParameter("grades", grades)
		.setParameter("schoolYear", schoolYear).getResultList();
		logger.info("allStudentsCountByGrade method executed successfully");	
		return objList;
	}
	
	/**This method used for get the meal item count by meal school id, start and end date**/
	/*@Override
	public List<Object[]> catererReport(Long mealSchoolId, Date startDate, Date endDate, List<String> grades) {
		List<Object[]> objList = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(grades != null && grades.size() > 0){
			objList = entityManager.createNativeQuery("Select count(m.id), m.mealName from OrderMealItemsDetailReport m where "
					+ "m.mealSchoolId = :mealSchoolId and (Date(m.mealDate) between :startdate and :endDate) and m.grade IN (:grades) "
					+ "and m.mealType = 'MEAL' group by m.mealName").setParameter("mealSchoolId", mealSchoolId).setParameter("startdate", sdf.format(startDate))
					.setParameter("endDate", sdf.format(endDate)).setParameter("grades", grades).getResultList();
		}else{
			objList = entityManager.createNativeQuery("Select count(m.id), m.mealName from OrderMealItemsDetailReport m where "
					+ "m.mealSchoolId = :mealSchoolId and (Date(m.mealDate) between :startdate and :endDate) and m.mealType = 'MEAL' group by m.mealName")
					.setParameter("mealSchoolId", mealSchoolId).setParameter("startdate", sdf.format(startDate)).setParameter("endDate", 
							sdf.format(endDate)).getResultList();
		}
		return objList;
	}*/
	
	/**This method used for get the all menu item list by school, date and grades**/
	/*@Override
	public List<String> menuNamesBySchoolAndDateAndGrade(Long mealSchoolId, Date startDate, Date endDate, List<String> grades) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> menuItems = entityManager.createNativeQuery("Select m.mealName from MealMenu_v2 m INNER JOIN SchoolMeals_v2 s "
				+ "ON m.mealId = s.mealMenu_Id INNER JOIN schoolMeal_grades sg ON s.schoolId = sg.schoolmeal_Id where m.mealtype = 'MEAL'"
				+ " and s.mealSchool_schoolId = :mealSchoolId and (Date(m.mealDate) between :startdate and :endDate) and sg.grades_name IN "
				+ "(:grades) group by m.mealName").setParameter("mealSchoolId", mealSchoolId).setParameter("startdate", 
						sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).setParameter("grades", grades).getResultList();
		return menuItems;
	}*/

	/**This method used for get all the meals with meal Date**/
	/*@Override
	public List<Object[]> allMealsWithDate(Long mealSchoolId, Date startDate, Date endDate, List<String> grades) {
		List<Object[]> allMealsArray = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(grades != null && grades.size() > 0)
			allMealsArray = entityManager.createNativeQuery("Select m.mealName, m.mealDate from MealMenu_v2 m INNER JOIN SchoolMeals_v2 s "
					+ "ON m.mealId = s.mealMenu_Id INNER JOIN schoolMeal_grades sg ON s.schoolId = sg.schoolmeal_Id "
					+ "INNER JOIN SchoolMealsSummary_v2 sms on s.schoolMealSummary_schoolId = sms.schoolId where m.mealtype = 'MEAL'"
					+ " and sms.isPublished = 1 and s.mealSchool_schoolId = :mealSchoolId and (Date(m.mealDate) between :startdate and :endDate) and sg.grades_name IN "
					+ "(:grades) group by m.mealName, Date(m.mealDate)").setParameter("mealSchoolId", mealSchoolId).setParameter("startdate", 
							sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).setParameter("grades", grades).getResultList();
		else
			allMealsArray = entityManager.createNativeQuery("Select m.mealName, m.mealDate from MealMenu_v2 m INNER JOIN SchoolMeals_v2 s "
					+ "ON m.mealId = s.mealMenu_Id INNER JOIN SchoolMealsSummary_v2 sms on s.schoolMealSummary_schoolId = sms.schoolId "
					+ "where m.mealtype = 'MEAL' and sms.isPublished = 1 and s.mealSchool_schoolId = :mealSchoolId "
					+ "and (Date(m.mealDate) between :startdate and :endDate) group by m.mealName, Date(m.mealDate)").setParameter("mealSchoolId", 
					mealSchoolId).setParameter("startdate",	sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).getResultList();
		return allMealsArray;
	}*/
	
	/**This method used for get all the meal items with the meal Date and grade**/
	@Override
	public List<Object[]> allMealsWithDateAndGrades(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, String type) {
		List<Object[]> allMealsArray = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder sb = new StringBuilder();
		sb.append("Select m.mealName, m.mealDate, sg.grades_name from MealMenu_v2 m INNER JOIN SchoolMeals_v2 s "
			+ "ON m.mealId = s.mealMenu_Id INNER JOIN schoolMeal_grades sg ON s.schoolId = sg.schoolmeal_Id "
			+ "INNER JOIN SchoolMealsSummary_v2 sms on s.schoolMealSummary_schoolId = sms.schoolId where m.mealtype = :type and "
			+ "sms.isPublished = 1 and s.isDelete=0 and s.mealSchool_schoolId = :mealSchoolId and (Date(m.mealDate) between :startdate and :endDate)");
		if(grades != null)
			sb.append(" and sg.grades_name IN (:grades)");
		
		sb.append("group by m.mealName, Date(m.mealDate), sg.grades_name");
		
		Query query = entityManager.createNativeQuery(sb.toString()).setParameter("type", type).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("startdate", sdf.format(startDate)).setParameter("endDate", sdf.format(endDate));
	
		if(grades != null)
			query.setParameter("grades", grades);
	
		allMealsArray = query.getResultList();
		logger.info("allMealsWithDateAndGrades method executed successfully.");
		return allMealsArray;
	}
	
	/**This method used for get all the meal items with the meal Date and grade**/
	@Override
	public List<Object[]> allMealsWithDateAndGradesV2(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, String type) {
		List<Object[]> allMealsArray = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder sb = new StringBuilder();
		sb.append("Select m.name, c.date, sg.grades_name from menu_items m INNER JOIN meal_calendar c "
			+ "ON m.id = c.menu_item_id INNER JOIN meal_calendar_summary sms on c.meal_calendar_summary_id = sms.id "
			+ "INNER JOIN meal_summary_grades sg ON sms.id = sg.meal_calendar_summary_id where m.category = :type and "
			+ "sms.isPublished = 1 and c.isActive=true and sms.mealSchool_schoolId = :mealSchoolId and (Date(c.date) between :startdate and :endDate)");
		if(grades != null)
			sb.append(" and sg.grades_name IN (:grades)");
		
		sb.append("group by m.name, Date(c.date), sg.grades_name");
		
		Query query = entityManager.createNativeQuery(sb.toString()).setParameter("type", type).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("startdate", sdf.format(startDate)).setParameter("endDate", sdf.format(endDate));
	
		if(grades != null)
			query.setParameter("grades", grades);
	
		allMealsArray = query.getResultList();
		logger.info("allMealsWithDateAndGrades method executed successfully.");
		return allMealsArray;
	}

	/**This method used for get the ordered items details report**/
	@Override
	public List<Object[]> orderedMealItemsReport(Long mealSchoolId, Date startDate, Date endDate,
			Boolean paymentStatus, List<String> grades) {
		List<Object[]> objArray = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String query = "select o.id, o.studentId, o.studentRecId, o.mealSchoolId, o.grade, o.mealId, o.mealName, o.mealType, "
						    + "o.mealPrice, o.studentFname, o.studentLname, o.mealDate, o.yearMonth, o.mealImage, o.schoolMealId, "
						    + "o.paymentStatus from OrderMealItemsDetailReport o where o.mealSchoolId = :mealSchoolId and (Date(o.mealDate) "
						    + "between :startDate and :endDate)";
			
			if(grades != null)
				query = query + " and o.grade IN (:grades)";
			if(paymentStatus != null)
				query = query + " and o.paymentStatus = :paymentStatus";
			
			if(grades == null && paymentStatus == null)
				objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate",
						sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).getResultList();
			else if(grades == null && paymentStatus != null)
				objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate",
						sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).setParameter("paymentStatus", paymentStatus).getResultList();
			else if(grades != null && paymentStatus == null)
				objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate",
						sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).setParameter("grades", grades).getResultList();
			else if(grades != null && paymentStatus != null)
				objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate",
						sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).setParameter("grades", grades).
						setParameter("paymentStatus", paymentStatus).getResultList();
			logger.info("orderedMealItemsReport method executed successfully");
		return objArray;
	}

	@Override
	public List<Object[]> orderSummaryReport(Long mealSchoolId, List<String> yearMonths, Boolean paymentStatus,
			List<String> grades, ItemTypeConstants menuType) throws Exception {
		List<Object[]> objArray = null;
		String query = "select o.recNo, o.parentId, o.studentId, o.studentRecId, o.studentFName, o.studentLName, o.grade, o.orderDate, "
					    + "o.orderPrice, o.paymentStatus, o.totItems, o.yearMonth, o.mealSchoolId, o.pdfLink, moa.schoolId, moa.createdOn, moa.modifiedOn from OrderMealsReport o inner join StudentUser_v2 su on o.studentRecId=su.userId "
					    + "Inner Join MealOrdersAudit_v2 moa on o.studentRecId = moa.studentUser_userId and o.yearMonth = moa.yearMonth "
					    + "where su.isActive=true and o.mealSchoolId = :mealSchoolId and o.yearMonth IN (:yearMonths) and o.menuType=:menuType and moa.menuType=:menuType";
		
		if(grades != null)
			query = query + " and o.grade IN (:grades)";
		if(paymentStatus != null)
			query = query + " and o.paymentStatus = :paymentStatus";
		
		if(grades == null && paymentStatus == null)
			objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonths",
					yearMonths).setParameter("menuType", menuType.toString()).getResultList();
		else if(grades == null && paymentStatus != null)
			objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonths",
					yearMonths).setParameter("menuType", menuType.toString()).setParameter("paymentStatus", paymentStatus).getResultList();
		else if(grades != null && paymentStatus == null)
			objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonths",
					yearMonths).setParameter("menuType", menuType.toString()).setParameter("grades", grades).getResultList();
		else if(grades != null && paymentStatus != null)
			objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonths",
					yearMonths).setParameter("menuType", menuType.toString()).setParameter("grades", grades).setParameter("paymentStatus", paymentStatus).getResultList();
		logger.info("orderSummaryReport method executed successfully");
	return objArray;
	}

	/**This method used for get the ordered meal by student id and meal date**/
	@Override
	public List<Object[]> orderedMealByStudentAndDate(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, String itemType, ItemTypeConstants menuType) {
		List<Object[]> objArray = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String query = "select o.studentRecId, o.mealName, o.mealDate,o.mealType from OrderMealItemsDetailReport o where o.mealSchoolId "
				+ "= :mealSchoolId and o.menuType = :menuType and (Date(o.mealDate) between :startDate and :endDate) and o.mealType IN (:itemType,'SIDE','EXTRA') group by o.studentRecId, o.mealName, Date(o.mealDate)";
		
		if(grades != null)
			query = query + " and o.grade IN (:grades)";
		
		if(grades == null)
			objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("menuType", menuType.toString()).setParameter("startDate",
					sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).setParameter("itemType", itemType).getResultList();
		else if(grades != null)
			objArray = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("menuType", menuType.toString()).setParameter("startDate",
					sdf.format(startDate)).setParameter("endDate", sdf.format(endDate)).setParameter("itemType", itemType).setParameter("grades", grades).getResultList();
		
	return objArray;
	}

	/**This method used for get the total payment amount of meal ordered Students by grade and payment status**/
	/*@Override
	public List<Object[]> orderedPaymentAmtByGrade(Long mealSchoolId, String yearMonth) {
		List<Object[]> objList = null;
		objList = entityManager.createNativeQuery("Select sum(o.orderPrice), o.grade, o.paymentStatus From OrderMealsReport o "
				+ "where o.mealSchoolId = :mealSchoolId and o.yearMonth = :selectedMonth group by o.grade, o.paymentStatus")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("selectedMonth", yearMonth).getResultList();
		logger.info("orderedPaymentAmtByGrade method executed successfully");	
		return objList;
	}*/

	/**This method used for get the details of requested email id regarding parent self registration**/
	@Override
	public List<Object[]> selfRegReqParentDetails(Date requestedTimeStart, Date requestedTimeEnd,
			Boolean sendStatus) {
		List<Object[]> requestedDetails = null;
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append("Select e.recNo, e.emailId, e.requestedTime, e.linkSendStatus "
				+ "from requestedemails e where (e.requestedTime between :requestedTimeStart and :requestedTimeEnd)");
		if(sendStatus != null)
			sb.append(" and e.linkSendStatus = :sendStatus");
		
		Query query = entityManager.createNativeQuery(sb.toString()).setParameter("requestedTimeStart", sdf.format(requestedTimeStart))
				.setParameter("requestedTimeEnd", sdf.format(requestedTimeEnd));
		if(sendStatus != null)
			query = query.setParameter("sendStatus", sendStatus ? 1 : 0);
		
		requestedDetails = query.getResultList();
		return requestedDetails;
	}

	/**This method used for get all the meals with ordered item count by date**/
	@Override
	public List<Object[]> allMealsWithOrderedCountByDate(Long mealSchoolId, Date startDate, Date endDate, List<String> grades) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> menuItems = null;
		StringBuilder sb = new StringBuilder();
		//sb.append("Select m.mealName, Date(m.mealDate), (select count(om.id) from OrderMealItemsDetailReport om where m.mealId = om.mealId");
		/*sb.append("Select m.mealName, Date(m.mealDate), (select count(om.id) from OrderMealItemsDetailReport om where m.mealName = om.mealName "
				+ "and Date(m.mealDate) = Date(om.mealDate)");
		if(grades != null)
			sb.append(" and om.grade IN (:grades1)");
		sb.append(") from MealMenu_v2 m INNER JOIN SchoolMeals_v2 s ON m.mealId = s.mealMenu_Id INNER JOIN "
				+ "schoolMeal_grades sg ON s.schoolId = sg.schoolmeal_Id INNER JOIN SchoolMealsSummary_v2 sms on s.schoolMealSummary_schoolId = sms.schoolId"
				+ " where m.mealtype = 'MEAL' and s.isDelete = 0 and s.mealSchool_schoolId = :mealSchoolId and sms.isPublished = 1 and (Date(m.mealDate) between :startdate and :endDate)");
		if(grades != null)
			sb.append(" and sg.grades_name IN (:grades)");
		
		sb.append("group by m.mealName, Date(m.mealDate) order by Date(m.mealDate) ");*/
		
		//Optimize query for Caterer reports
		sb.append("Select m.mealName, Date(m.mealDate), count(om.id) from MealMenu_v2 m INNER JOIN SchoolMeals_v2 s ON m.mealId = s.mealMenu_Id"
				+ " INNER JOIN SchoolMealsSummary_v2 sms on s.schoolMealSummary_schoolId = sms.schoolId");
		if(grades != null)
			sb.append("  INNER JOIN schoolMeal_grades sg ON s.schoolId = sg.schoolmeal_Id and sg.grades_name IN (:grades)");
		sb.append(" left join OrderMealItemsDetailReport om on s.schoolId=om.schoolMealId ");
		if(grades != null)
			sb.append(" and om.grade in (:grades)");
		sb.append(" where m.mealtype = 'MEAL' and s.isDelete = 0 and s.mealSchool_schoolId = :mealSchoolId and sms.isPublished = 1 and (Date(m.mealDate) "
				+ "between :startdate and :endDate) group by m.mealName, Date(m.mealDate) order by Date(m.mealDate)");
		logger.info("Query for Caterer report:: "+sb.toString());
		Query query = entityManager.createNativeQuery(sb.toString()).setParameter("mealSchoolId", mealSchoolId)
					.setParameter("startdate", sdf.format(startDate)).setParameter("endDate", sdf.format(endDate));
		if(grades != null)
			query.setParameter("grades", grades);	
		menuItems = query.getResultList();
		return menuItems;
	}
	
	/**This method used for get all the meals with ordered item count by date**/
	@Override
	public List<Object[]> allMealsWithOrderedCountByDateV2(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, ItemTypeConstants menuType) {
		String itemType = CommonUtil.getItemType(menuType);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> menuItems = null;
		StringBuilder sb = new StringBuilder();		
		//Optimize query for Caterer reports
		sb.append("Select m.name, Date(c.date), count(om.id) from menu_items m INNER JOIN meal_calendar c ON m.id = c.menu_item_id"
				+ " INNER JOIN meal_calendar_summary sms on c.meal_calendar_summary_id = sms.id");
		if(grades != null)
			sb.append("  INNER JOIN meal_summary_grades sg ON sms.id = sg.meal_calendar_summary_id and sg.grades_name IN (:grades)");
		sb.append(" inner join OrderMealItemsDetailReport om on c.id=om.schoolMealId and c.date = om.mealDate "
				+ "inner join StudentUser_v2 su on om.studentRecId = su.userId and su.isActive = true ");
		if(grades != null)
			sb.append(" and sg.grades_name = om.grade");
		sb.append(" where (m.category = :itemType or (m.category='SIDE' and sms.isSideSelect is not null and sms.isSideSelect = true) or (m.category='EXTRA' and sms.isExtraPreOrder is not null and sms.isExtraPreOrder = true))"
				+ " and sms.mealType = :menuType and c.isActive = true and sms.mealSchool_schoolId = :mealSchoolId and sms.isPublished = 1 and (Date(c.date) "
				+ "between :startdate and :endDate) group by m.name, Date(c.date) order by Date(c.date)");
		logger.info("Query for Caterer report:: "+sb.toString());
		Query query = entityManager.createNativeQuery(sb.toString()).setParameter("itemType", itemType).setParameter("menuType", menuType.toString()).setParameter("mealSchoolId", mealSchoolId)
					.setParameter("startdate", sdf.format(startDate)).setParameter("endDate", sdf.format(endDate));
		if(grades != null)
			query.setParameter("grades", grades);	
		menuItems = query.getResultList();
		return menuItems;
	}
	
	/**This method used for caterers report**/
	@Override
	public List<Object[]> caterersReport(Long catererId, Date startDate, Date endDate, ItemTypeConstants menuType, Long mealSchoolId) {
		String itemType = CommonUtil.getItemType(menuType);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String qry = "Select m.name, c.date, count(om.id),m.category, sms.mealSchool_schoolId from menu_items m INNER JOIN meal_calendar c ON m.id = c.menu_item_id"
				+ " INNER JOIN meal_calendar_summary sms on c.meal_calendar_summary_id = sms.id Inner Join MealSchool_v2 ms on sms.mealSchool_schoolId = ms.schoolId "
				+ "inner join OrderMealItemsDetailReport om on c.id=om.schoolMealId and c.date = om.mealDate inner join StudentUser_v2 su on om.studentRecId = su.userId "
				+ "and su.isActive = true where (m.category = :itemType or (m.category='SIDE' and sms.isSideSelect is not null and sms.isSideSelect = true) or "
				+ "(m.category='EXTRA' and sms.isExtraPreOrder is not null and sms.isExtraPreOrder = true and sms.extraEnableForCaterer = true)) and sms.mealType = :menuType "
				+ "and c.isActive = true and ms.catererId = :catererId and sms.isPublished = 1 and (Date(c.date) between :startdate and :endDate)";
		if(mealSchoolId != null)
			qry = qry+" and ms.schoolId = :mealSchoolId";
		qry = qry + " group by m.name, Date(c.date), m.category, sms.mealSchool_schoolId order by Date(c.date)";
		logger.info("Query for Caterer's report:: "+qry);
		Query query = entityManager.createNativeQuery(qry).setParameter("itemType", itemType).setParameter("menuType", menuType.toString()).setParameter("catererId", catererId)
				.setParameter("startdate", sdf.format(startDate)).setParameter("endDate", sdf.format(endDate));
		if(mealSchoolId != null)
			query.setParameter("mealSchoolId", mealSchoolId);
		return query.getResultList();
	}

	/**This method used for return the meal menu id, name, meal date and grades**/
	@Override
	public List<Object[]> monthlyMenuDetails(Long mealSchoolId, String mealDate, String menuType) {
		List<Object[]> mealDetails = null;
		mealDetails = entityManager.createNativeQuery("select od.mealId, od.mealName, count(od.id),"+
				"od.mealDate from OrderMealItemsDetailReport od where od.mealSchoolId = :mealSchoolId and od.mealDate = :mealDate "+
				"and od.mealType = :menuType group by od.mealId, od.mealName")
				.setParameter("mealSchoolId", mealSchoolId).setParameter("mealDate", mealDate).setParameter("menuType", menuType).getResultList();
		logger.info("monthlyMenuDetails method executed successfully");
		return mealDetails;
	}

	/**This method used for get all the parent email ids to send the notification regarding meal change**/
	@Override
	public List<Object[]> mealChangeDetailsForSendNotificationToParent(MealChangeNotificationRequest mealChangeReq) {
		List<Object[]> emails = null;
		emails = entityManager.createNativeQuery("select p.userName, p.parentAltEmail, DATE(od.mealDate), od.mealName, (select "
				+ "uo.emailIsSubscribe from UserAuthInfo_v2 uo where p.userName = uo.userName) as pStatus, (select "
				+ "uo.emailIsSubscribe from UserAuthInfo_v2 uo where p.parentAltEmail = uo.userName) as sStatus from OrderMealItemsDetailReport od "+
				"Inner Join StudentUser_v2 s on od.studentRecId = s.userId Inner Join ParentUser_v2 p on s.parentuser_userId = p.userId "+
				"where od.mealSchoolId = :mealSchoolId and od.yearMonth = :yearMonth and od.mealId = :mealId group by p.userName, "+
				"p.parentAltEmail, DATE(od.mealDate), od.mealName").setParameter("mealSchoolId", mealChangeReq.getMealSchoolId())
				.setParameter("yearMonth", mealChangeReq.getYearMonth()).setParameter("mealId", mealChangeReq.getItemId()).getResultList();
		logger.info("mealChangeDetailsForSendNotificationToParent dao method executed successfully");
		return emails;
	}

	/**This method used for get all the grades for which Meal has been published by school**/
	@Override
	public List<String> getMealPublishedGrades(Long mealSchoolId, String yearMonth, List<String> schoolGrades, Boolean autoReminderStatus) {
		List<String> grades = null;
		String qry = "";
		if(!autoReminderStatus)
			qry = "select sg.grades_name from SchoolMeals_v2 sm INNER JOIN schoolMeal_grades sg on sm.schoolId = "
				+ "sg.schoolmeal_Id INNER JOIN SchoolMealsSummary_v2 s on sm.schoolMealSummary_schoolId = s.schoolId "
				+ "where sm.mealSchool_schoolId = :mealSchoolId and sm.yearMonth = :yearMonth and s.isPublished = 1";
		else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dt = sdf.format(new Date());
			qry = "select sg.grades_name from SchoolMeals_v2 sm INNER JOIN schoolMeal_grades sg on sm.schoolId = "
					+ "sg.schoolmeal_Id INNER JOIN SchoolMealsSummary_v2 s on sm.schoolMealSummary_schoolId = s.schoolId where "
				+ "(Date(s.autoReminderDate1) = '"+dt+"' or Date(s.autoReminderDate2) = '"+dt+"') and sm.mealSchool_schoolId = :mealSchoolId "
				+ "and sm.yearMonth = :yearMonth and s.isPublished = 1";
		}
		if(schoolGrades != null && schoolGrades.size() > 0)
			qry = qry+" and sg.grades_name in (:schoolGrades)";
		qry = qry+" group by sg.grades_name";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonth",yearMonth);
		if(schoolGrades != null && schoolGrades.size() > 0)
			query.setParameter("schoolGrades", schoolGrades);
		grades = query.getResultList();
		logger.info("getMealPublishedGrades dao method executed successfully");
		return grades;
	}
	
	/**This method used for get all the grades for which Meal has been published by school**/
	@Override
	public List<String> getMealPublishedGradesV2(Long mealSchoolId, String yearMonth, List<String> schoolGrades, Boolean autoReminderStatus, ItemTypeConstants menuType) {
		List<String> grades = null;
		String qry = "";
		if(!autoReminderStatus)
			qry = "select sg.grades_name from meal_calendar_summary sm INNER JOIN meal_summary_grades sg on sm.id = "
				+ "sg.meal_calendar_summary_id where sm.mealSchool_schoolId = :mealSchoolId and sm.yearMonth = :yearMonth and sm.isPublished = 1 and sm.mealType = :menuType";
		else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dt = sdf.format(new Date());
			qry = "select sg.grades_name from meal_calendar_summary sm INNER JOIN meal_summary_grades sg on sm.id = "
					+ "sg.meal_calendar_summary_id where (Date(sm.autoReminderDate1) = '"+dt+"' or Date(sm.autoReminderDate2) = '"+dt+"') and sm.mealSchool_schoolId = :mealSchoolId "
				+ "and sm.yearMonth = :yearMonth and sm.isPublished = 1 and sm.mealType = :menuType";
		}
		if(schoolGrades != null && schoolGrades.size() > 0)
			qry = qry+" and sg.grades_name in (:schoolGrades)";
		qry = qry+" group by sg.grades_name";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonth",yearMonth).setParameter("menuType", menuType.toString());
		if(schoolGrades != null && schoolGrades.size() > 0)
			query.setParameter("schoolGrades", schoolGrades);
		grades = query.getResultList();
		logger.info("getMealPublishedGrades dao method executed successfully");
		return grades;
	}

	/**This method used for get all the not ordered students (i.e. who haven't ordered yet but meal created for them)**/
	@Override
	public List<Object[]> notOrderedStudents(Long mealSchoolId, String yearMonth, List<String> schoolGrades, Integer schoolYear, ItemTypeConstants menuType) {
		List<Object[]> students = null;
		String qry = "Select su.userId, su.studentId, su.firstName, su.lastName, su.gradeName from StudentUser_v2 su where "
				+"su.studentId not in (select distinct omr.studentId from OrderMealItemsDetailReport omr " + 
				"where omr.mealSchoolId =:mealSchoolId and  omr.yearMonth = :selectedMonth and omr.menuType = :menuType) and "
				+ "su.mealSchool_schoolId = :mealSchoolId and su.isActive = true and "
				+ "su.gradeName IN (:grades) and su.schoolYear = :schoolYear ";
		students = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("selectedMonth",yearMonth)
				.setParameter("schoolYear", schoolYear).setParameter("grades", schoolGrades).setParameter("menuType", menuType.toString()).getResultList();
		return students;
	}
	/**This method used for get all the meals by meal summary id and return it list of object array**/
	@Override
	public List<Object[]> mealsBySummaryId(Long mealSummaryId) {
		List<Object[]> objArray = entityManager.createNativeQuery("Select sms.reducedPriceStatus, m.mealId, m.mealPrice, "
				+ "m.reducedPrice, m.mealLongDesc, m.mealName, m.mealDate, m.mealtype from SchoolMealsSummary_v2 sms Inner "
				+ "Join SchoolMeals_v2 sm on sms.schoolId = sm.schoolMealSummary_schoolId Inner Join MealMenu_v2 m on "
				+ "sm.mealMenu_Id = m.mealId where sms.schoolId = :mealSummaryId and sm.isDelete = 0")
				.setParameter("mealSummaryId", mealSummaryId).getResultList();
		return objArray;
	}

	/**This method used for get the students for allergies report**/
	@Override
	public List<Object[]> studentsWithAllergiesDetails(Long mealSchoolId, int schoolYear, List<String> grades) {
		List<Object[]> objArray = null;
		String query = "select s.firstName, s.lastName, s.gradeName, s.teacherName, s.allergies from StudentUser_v2 s "
					    + "where s.mealSchool_schoolId = :mealSchoolId and s.schoolYear = :schoolYear and "
					    + " s.isActive = 1 and s.allergies is not null and s.allergies != ''";
		
		if(grades != null && grades.size() > 0)
			query = query + " and s.gradeName IN (:grades)";	

		Query queryGen = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("schoolYear", schoolYear);
		if(grades != null && grades.size() > 0)
			queryGen.setParameter("grades", grades);
		objArray = queryGen.getResultList();
		logger.info("studentsWithAllergiesDetails dao method executed successfully");
		return objArray;
	}

	/**This method used for get all the school admin user email addresses by parent email**/
	@Override
	public List<Object[]> adminUsersEmail(String parentEmail) {
		return entityManager.createNativeQuery("Select shu.username,ms.schoolId from ParentUser_v2 pu Inner Join StudentUser_v2 stu ON "+
				"pu.userId = stu.parentuser_userId Inner Join MealSchool_v2 ms ON stu.mealSchool_schoolId = ms.schoolId "+
				"Inner Join SchoolUser_v2 shu ON ms.schoolId = shu.mealSchool_id where (pu.userName = :email OR "+
				"pu.parentAltEmail = :email) and shu.isActive = 1 and shu.isVerified = 1 and ms.isActive = 1 and "+
				"stu.isActive = 1 and shu.isUnsubscribeGenNotif = 0 group by shu.username,ms.schoolId")
				.setParameter("email", parentEmail).getResultList();
	}

	/**This method used for get all the Meal School Id by parent email**/
	@Override
	public List<Long> mealSchoolIdsByParentEmail(String parentEmail) {
		return entityManager.createNativeQuery("Select ms.schoolId from ParentUser_v2 pu Inner Join StudentUser_v2 stu ON "
				+ "pu.userId = stu.parentuser_userId Inner Join MealSchool_v2 ms ON stu.mealSchool_schoolId = ms.schoolId where "
				+ "(pu.userName = :email OR pu.parentAltEmail = :email) and ms.isActive = 1 and stu.isActive = 1 and "
				+ "stu.isRegister = 1 group by ms.schoolId").setParameter("email", parentEmail).getResultList();
	}

	@Override
	public String getLatestYearMonth(List<Long> mealSchoolIds) {
		Date date = new Date();
		Object yearMonthVal = "";
		yearMonthVal = entityManager.createNativeQuery("select max(yearMonth) from SchoolMealsSummary_v2 where "
				+ "cutOffDateTime >= :currentDateTime and isPublished = 1 and mealSchool_schoolId IN (:schoolIds)").setParameter(
						"currentDateTime", date).setParameter("schoolIds", mealSchoolIds).getSingleResult();
		if(yearMonthVal != null)
			return yearMonthVal.toString();
		else
			return null;
	}
	
	@Override
	public String getLatestYearMonthV2(List<Long> mealSchoolIds) {
		Date date = new Date();
		Object yearMonthVal = "";
		yearMonthVal = entityManager.createNativeQuery("select max(yearMonth) from meal_calendar_summary where "
				+ "cutOffDateTime >= :currentDateTime and isPublished = 1 and mealSchool_schoolId IN (:schoolIds)").setParameter(
						"currentDateTime", date).setParameter("schoolIds", mealSchoolIds).getSingleResult();
		if(yearMonthVal != null)
			return yearMonthVal.toString();
		else
			return null;
	}

	/**This method used for get all the admin users email address based on parent email id**/
	@Override
	public List<String> adminUserEmails(String parentUserEmail) {
		return entityManager.createNativeQuery("Select su.username from ParentUser_v2 pu inner join StudentUser_v2 stdu on "
				+ "pu.userId = stdu.parentuser_userId inner join MealSchool_v2 ms on stdu.mealSchool_schoolId = ms.schoolId "
				+ "Inner join SchoolUser_v2 su on ms.schoolId = su.mealSchool_id where (pu.userName = :email OR "
				+ "pu.parentAltEmail = :email) and ms.isActive = 1 and stdu.isActive = 1 "
				+ "and su.isVerified = 1 and su.isActive = 1 and su.isUnsubscribeGenNotif = 0 group by su.username")
				.setParameter("email", parentUserEmail)
				.getResultList();
	}

	/**This method return list of object[] which contains the order month and student**/
	@Override
	public List<Object[]> monthStudentsByParentEmail(String parentEmail, String currentYearMonth) {
		Date currentDate = new Date();
		return entityManager.createNativeQuery("Select stu.userId, max(sms.yearMonth) from ParentUser_v2 pu Inner Join "+
				"StudentUser_v2 stu ON pu.userId = stu.parentuser_userId Inner Join MealSchool_v2 ms ON "+
				"stu.mealSchool_schoolId = ms.schoolId Inner Join SchoolMealsSummary_v2 sms ON "+
				"ms.schoolId = sms.mealSchool_schoolId Inner Join SchoolMeals_v2 sm ON sms.schoolId = sm.schoolMealSummary_schoolId"+
				" Inner Join schoolMeal_grades smg ON sm.schoolId = smg.schoolmeal_Id and smg.grades_name = stu.gradeName where "+
				"(pu.userName = :parentEmail OR pu.parentAltEmail = :parentEmail) and ms.isActive = 1 and stu.isActive = 1 "
				+ "and stu.isRegister = 1 and  sms.isPublished = 1 and sms.yearMonth >= :currentYearMonth and (sms.cutOffDateTime "
				+ ">= :currentDate OR sms.orderDateExtensionStatus = 1) group by stu.userId")
				.setParameter("parentEmail", parentEmail).setParameter("currentYearMonth", currentYearMonth)
				.setParameter("currentDate", currentDate).getResultList();
	}

	/**This method return list of object[] which contains the order month and student**/
	@Override
	public List<Object[]> monthStudentsByParentEmailV2(String parentEmail, String currentYearMonth) {
		//Date currentDate = new Date();
		LocalDate ld = LocalDate.now();
		ld = ld.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
		String weekYearMonth = ld.getYear()+""+new DecimalFormat("00").format(ld.getMonthValue());
		/*return entityManager.createNativeQuery("Select stu.userId, max(sms.yearMonth) from ParentUser_v2 pu Inner Join "+
				"StudentUser_v2 stu ON pu.userId = stu.parentuser_userId Inner Join MealSchool_v2 ms ON "+
				"stu.mealSchool_schoolId = ms.schoolId Inner Join meal_calendar_summary sms ON "+
				"ms.schoolId = sms.mealSchool_schoolId Inner Join meal_summary_grades smg ON sms.id = smg.meal_calendar_summary_id"
				+ " and smg.grades_name = stu.gradeName where (pu.userName = :parentEmail OR pu.parentAltEmail = :parentEmail) and ms.isActive = 1 and stu.isActive = 1 "
				+ "and stu.isRegister = 1 and  sms.isPublished = 1 and sms.yearMonth >= :currentYearMonth and (sms.cutOffDateTime "
				+ ">= :currentDate OR sms.orderDateExtensionStatus = 1) group by stu.userId")
				.setParameter("parentEmail", parentEmail).setParameter("currentYearMonth", currentYearMonth)
				.setParameter("currentDate", currentDate).getResultList();*/
		return entityManager.createNativeQuery("Select stu.userId, min(sms.yearMonth) from ParentUser_v2 pu Inner Join "+
				"StudentUser_v2 stu ON pu.userId = stu.parentuser_userId Inner Join MealSchool_v2 ms ON "+
				"stu.mealSchool_schoolId = ms.schoolId Inner Join meal_calendar_summary sms ON "+
				"ms.schoolId = sms.mealSchool_schoolId Inner Join meal_summary_grades smg ON sms.id = smg.meal_calendar_summary_id"
				+ " and smg.grades_name = stu.gradeName where (pu.userName = :parentEmail OR pu.parentAltEmail = :parentEmail) and ms.isActive = 1 and stu.isActive = 1 "
				+ "and stu.isRegister = 1 and  sms.isPublished = 1 and sms.yearMonth >= :currentYearMonth and ((sms.cutOffType = 'M' and (sms.cutOffDateTime >= now() "
				+ "OR sms.orderDateExtensionStatus = 1)) or (sms.cutOffType = 'R' and sms.yearMonth >= date_format(DATE_ADD(now(), INTERVAL sms.allowOrderNDaysBefore DAY), '%Y%m'))"
				+ " or (sms.cutOffType = 'W' and sms.yearMonth >= :weekYearMonth)) group by stu.userId")
				.setParameter("parentEmail", parentEmail).setParameter("currentYearMonth", currentYearMonth)
				.setParameter("weekYearMonth", weekYearMonth).getResultList();
	}

	/**This method used for get the student's transaction history date**/
	@Override
	public List<Object[]> transactionsHistory(Long studentRecId, String startDate, String endDate) {
		List<Object[]> transactionsHstry = null;		
		Query query = entityManager.createNativeQuery("select mta.transactionDateTime, mta.paymentType, mta.purchaseItemType,"
				+ " mta.transactionType, swt.transactionAmount, swt.finalBalance,mta.transactionDescription,e.eventName,swt.mealType from MasterTransactionsAudit mta Inner Join"
				+ " StudentWiseTransactions swt on mta.recId = swt.MasterTransactionsAudit_RecId left join EventInfo e on swt.eventInfo_recId=e.recId"
				+ " where swt.studentUser_userId = :studentRecId and swt.isPosted=true and "
				+ "(mta.transactionDateTime between :startDate and :endDate) order by swt.recId desc");
		query.setParameter("studentRecId", studentRecId).setParameter("startDate", startDate.replace("T", " "))
			.setParameter("endDate", endDate.replace("T", " "));
		transactionsHstry = query.getResultList();
		logger.info("Get the transaction history method executed successfully");
		return transactionsHstry;
	}

	/**This method used for fetch all the deposit/purchase transactions details**/
	@Override
	public List<Object[]> transactionsReport(Long mealSchoolId, String startDate, String endDate, Boolean isDeposit,Integer schoolYear, Boolean isAdjTrx) {
		List<Object[]> transactionsHstry = null;	
		List<String> transactionTypes = null;
		if(isAdjTrx != null && isAdjTrx)
			transactionTypes = Arrays.asList(TransactionType.Adjustment.toString());
		else{
			if(isDeposit != null && isDeposit)
				transactionTypes = Arrays.asList(TransactionType.Deposit.toString(), TransactionType.InstantPayment.toString());
			else
				transactionTypes = Arrays.asList(TransactionType.Purchase.toString()/*, TransactionType.Transfer.toString(), 
						TransactionType.ImportBalance.toString()*/);
		}
		String query = "select swt.recId, swt.studentLName, swt.studentFName, "
				+ "mta.transactionDateTime, swt.transactionAmount, mta.note, mta.transactionDescription, mta.paymentType, "
				+ "mta.purchaseItemType, mta.createdBy, swt.grade, mta.checkNumb, mta.transferId,swt.mealType, l.location,mta.posDeposit "
				+ "from MasterTransactionsAudit mta Inner Join StudentWiseTransactions swt "
				+ "on mta.recId = swt.MasterTransactionsAudit_RecId Inner Join StudentUser_v2 su on swt.studentUser_userId=su.userId "
				+ "LEFT JOIN PosLocation l on mta.locationId = l.id where mta.mealSchool_schoolId = :mealSchoolId and "
				+ "mta.transactionType IN (:transactionTypes) and (mta.transactionDateTime between :startDate and :endDate) and su.schoolYear = :schoolYear and swt.isPosted=true ";
		if(isAdjTrx == null || !isAdjTrx){
			if(isDeposit != null && isDeposit)
				query = query+"and (mta.paymentType is not null and mta.paymentType != 'TransferCR' and mta.paymentType != 'Wallet')";
			else
				query = query+"and mta.purchaseItemType is not null ";
		}		
		query = query+"order by mta.transactionDateTime desc";
		transactionsHstry = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId).setParameter("transactionTypes", transactionTypes)
			.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).setParameter("schoolYear", schoolYear).getResultList();
		logger.info("Get all the deposit/purchase transactions details method executed successfully");
		return transactionsHstry;
	}
	
	/**This method used for fetch all the online report transactions details**/
	@Override
	public List<Object[]> onlinePaymetReport(Long districtId, String startDate, String endDate,Integer schoolYear) {
		List<Object[]> transactionsHstry = null;	
		List<String> transactionTypes = Arrays.asList(TransactionType.Deposit.toString(), TransactionType.InstantPayment.toString());
		
		String query = "select swt.recId, swt.studentLName, swt.studentFName, "
				+ "mta.transactionDateTime, swt.transactionAmount, mta.note, mta.transactionDescription, mta.paymentType, "
				+ "mta.purchaseItemType, ms.schoolName, swt.grade, mta.checkNumb, mta.transferId "
				+ "from MasterTransactionsAudit mta Inner Join StudentWiseTransactions swt "
				+ "on mta.recId = swt.MasterTransactionsAudit_RecId Inner Join StudentUser_v2 su on swt.studentUser_userId=su.userId "
				+ "Inner Join MealSchool_v2 ms on su.mealSchool_schoolId = ms.schoolId where ms.districtId = :districtId and "
				+ "mta.transactionType IN (:transactionTypes) and (mta.transactionDateTime between :startDate and :endDate) and su.schoolYear = :schoolYear and swt.isPosted=true "+
				"and (mta.paymentType is not null and mta.paymentType = 'Online')";
		
		query = query+"order by ms.schoolName asc,mta.transactionDateTime desc";
		transactionsHstry = entityManager.createNativeQuery(query).setParameter("districtId", districtId).setParameter("transactionTypes", transactionTypes)
			.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).setParameter("schoolYear", schoolYear).getResultList();
		logger.info("Get all the online report details method executed successfully");
		return transactionsHstry;
	}

	/**This method used for generate the low balance students report**/
	@Override
	public List<Object[]> lowBalanceReport(Long mealSchoolId, Integer schoolYear, Double minLowBal, Double maxLowBal,
			Boolean isZeroExclude,Double amount, String operator) {
		List<Object[]> objArray = null;
		String query = "Select su.gradeName, su.studentId, su.userId, su.firstName, su.lastName, su.mobileNo, pu.userName, "+
				"pu.parentAltEmail, su.teacherName, su.isReducePriceEligible, su.isFreeMealEligible, "+
				"su.accBalance from StudentUser_v2 su Inner Join ParentUser_v2 pu on su.parentuser_userId = pu.userId "+
				"where su.mealSchool_schoolId = :mealSchoolId and su.schoolYear = :schoolYear and su.isActive = 1 ";
		boolean isCustom = false;
		if(amount != null && operator != null && !operator.trim().isEmpty()){
			switch (operator.toUpperCase()) {
			case "LE": query = query+" and su.accBalance <= :amount"; break;
			case "L": query = query+" and su.accBalance < :amount"; break;
			case "GE": query = query+" and su.accBalance >= :amount"; break;
			case "G": query = query+" and su.accBalance > :amount"; break;
			default: query = query+" and su.accBalance = :amount"; break;
			}
			isCustom = true;
		}else{
			query = query+" and su.accBalance >= :minLowBal and su.accBalance <= :maxLowBal";
		}
		if(isZeroExclude != null && isZeroExclude)
			query = query + " and su.accBalance != 0";	
		Query queryGen = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("schoolYear", schoolYear);
		if(isCustom)
			queryGen.setParameter("amount", amount);
		else
			queryGen.setParameter("minLowBal", minLowBal).setParameter("maxLowBal", maxLowBal);
		objArray = queryGen.getResultList();
		logger.info("Get all the low balance student's details dao method executed successfully");
		return objArray;
	}

	/**This method used for get the students data who are eligible for free meal/reduced price**/
	@Override
	public List<Object[]> studentFmEligibiltyData(Long mealSchoolId, int schoolYear, String eligType, Boolean isTemp,Boolean isDistId) {
		List<Object[]> objArray = null;
		String query = "select su.firstName, su.lastName, su.studentId, su.isFreeMealEligible, su.isReducePriceEligible, su.gradeName, su.teacherName"
				+ ",m.schoolName,pu.userName from StudentUser_v2 su INNER JOIN MealSchool_v2 m on su.mealSchool_schoolId=m.schoolId Inner Join ParentUser_v2 pu"
				+ " on su.parentuser_userId = pu.userId where su.isActive = 1 "
				+ "and su.schoolYear = :schoolYear and ";
		if(isDistId != null && isDistId)
			query = query+" m.districtId = :mealSchoolId and ";
		else
			query = query+" m.schoolId = :mealSchoolId and ";
		if(eligType != null && eligType.equalsIgnoreCase("Free")){
			query = query+"su.isFreeMealEligible = 1";
		}else if(eligType != null && eligType.equalsIgnoreCase("Reduced")){
			query=query+"su.isReducePriceEligible = 1";
		}else{
			query=query+"(su.isFreeMealEligible = 1 or su.isReducePriceEligible = 1)";
		}
		if(isTemp != null && isTemp)
			query=query+" and su.reCertificateDate is null and su.recertPending = 'Y'";
		objArray = entityManager.createNativeQuery(query+"  ORDER BY m.schoolName,su.studentId").setParameter("mealSchoolId", mealSchoolId)
				.setParameter("schoolYear", schoolYear).getResultList();
		logger.info("Get all the students who are eligible for "+eligType+" price");
		return objArray;
	}

	/**This method used for get those student's details who didn't order lunch in current month but having in previous month**/
	@Override
	public List<Object[]> notOrderedLunchReport(Long mealSchoolId, String yearMonth, List<String> schoolGrades,
			Integer schoolYear, String previousYearMonth, ItemTypeConstants menuType) {
		List<Object[]> students = null;
		String qry = "Select su.userId, su.studentId, su.firstName, su.lastName, su.gradeName, su.teacherName from "
				+ "StudentUser_v2 su where su.mealSchool_schoolId = :mealSchoolId and su.isActive = true and "
				+ "su.gradeName IN (:schoolGrades) and su.schoolYear = :schoolYear and NOT EXISTS (select null from "
				+ "OrderMealItemsDetailReport o where o.studentRecId = su.userId and o.yearMonth = :yearMonth and o.menuType = :menuType) and EXISTS "
				+ "(select null from OrderMealItemsDetailReport o where o.studentRecId = su.userId and o.yearMonth = :previousYearMonth and o.menuType = :menuType)";
		students = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("yearMonth",yearMonth)
				.setParameter("schoolYear", schoolYear).setParameter("schoolGrades", schoolGrades)
				.setParameter("previousYearMonth", previousYearMonth).setParameter("menuType", menuType.toString()).getResultList();
		return students;
	}

	/**This method used for get the meals served count by their eligibility**/
	@Override
	public List<Object[]> mealsServedCountByElig(Long mealSchoolId, String startDate, String endDate,
			String itemType, boolean isNeedy) {
		List<Object[]> resp = null;	
		String qry = "select count(swt.recId),swt.eligStatus from StudentWiseTransactions swt inner join  "
				+ "MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :mealType and mta.transactionType='Purchase' and "
				+ "mta.mealSchool_schoolId = :mealSchoolId and swt.eligStatus is not null and (swt.mealType is null or swt.mealType = 'Regular') and swt.isPosted=true and swt.grade != 'staff' and ";
		//if(isNeedy)
			qry = qry+"swt.isEmrgLunchServe= :isNeedy and";
		qry = qry+" mta.transactionDateTime between :startDate and :endDate group by swt.eligStatus";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealType", itemType).setParameter("mealSchoolId", 
				mealSchoolId).setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " "));
		//if(isNeedy)
			query.setParameter("isNeedy", isNeedy);
		resp = query.getResultList();
		return resp;
	}
	
	/**This method used for get the staff served meals**/
	@Override
	public Object staffServedMeals(Long mealSchoolId, String startDate, String endDate,
			String itemType) {
		String qry = "select count(swt.recId) from StudentWiseTransactions swt inner join  "
				+ "MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :mealType and mta.transactionType='Purchase' and "
				+ "mta.mealSchool_schoolId = :mealSchoolId and (swt.mealType is null or swt.mealType != 'ALaCarte') and swt.eligStatus is not null and swt.isPosted=true and swt.grade = 'staff' and ";
		qry = qry+" mta.transactionDateTime between :startDate and :endDate";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealType", itemType).setParameter("mealSchoolId", 
				mealSchoolId).setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " "));
		return query.getSingleResult();
	}
	
	/**This method used for get the additional served meals**/
	@Override
	public Object otherServedMeals(Long mealSchoolId, String startDate, String endDate, String itemType, String mealType) {
		String qry = "select count(swt.recId) from StudentWiseTransactions swt inner join  "
				+ "MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :mealType and mta.transactionType='Purchase' and "
				+ "mta.mealSchool_schoolId = :mealSchoolId and swt.isPosted=true and swt.mealType = :mealTypeV and ";
		qry = qry+" mta.transactionDateTime between :startDate and :endDate";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealType", itemType).setParameter("mealSchoolId", 
				mealSchoolId).setParameter("mealTypeV", mealType).setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " "));
		return query.getSingleResult();
	}
	
	/**This method used for get the a la carte served meals**/
	@Override
	public Object alaCarteServed(Long mealSchoolId, String startDate, String endDate,
			String itemType) {
		String qry = "select count(swt.recId) from StudentWiseTransactions swt inner join  "
				+ "MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :mealType and "
				+ "mta.mealSchool_schoolId = :mealSchoolId and swt.eligStatus is not null and swt.isPosted=true and swt.grade = 'staff' and ";
		qry = qry+" mta.transactionDateTime between :startDate and :endDate";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealType", itemType).setParameter("mealSchoolId", 
				mealSchoolId).setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " "));
		return query.getSingleResult();
	}
	
	/**This method used for get the meal cash**/
	@Override
	public Object regMealCash(Long mealSchoolId, String startDate, String endDate,
			String itemType) {
		String qry = "select sum(swt.transactionAmount) from StudentWiseTransactions swt inner join  "
				+ "MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :mealType and "
				+ "mta.mealSchool_schoolId = :mealSchoolId and swt.eligStatus is not null and swt.isPosted=true and swt.grade != 'staff' and ";
		qry = qry+" mta.transactionDateTime between :startDate and :endDate and mta.isPrePaid = 0";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealType", itemType).setParameter("mealSchoolId", 
				mealSchoolId).setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " "));
		return query.getSingleResult();
	}
	
	/**This method used for get the meals served count by their eligibility**/
	@Override
	public List<Object[]> distMealsServedCountByElig(Long districtId, String startDate, String endDate, String itemType, boolean isNeedy) {
		List<Object[]> resp = null;	
		String qry = "select count(swt.recId),swt.eligStatus from StudentWiseTransactions swt inner join  "
				+ "MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId Inner Join MealSchool_v2 ms "
				+ "on mta.mealSchool_schoolId = ms.schoolId where mta.purchaseItemType is not null and mta.purchaseItemType = :mealType and "
				+ "ms.districtId = :districtId and mta.transactionType='Purchase' and (swt.mealType is null or swt.mealType = 'Regular') and swt.eligStatus is not null and swt.isPosted=true and swt.grade != 'staff' and "
				+ "swt.isEmrgLunchServe= :isNeedy and mta.transactionDateTime between :startDate and :endDate group by swt.eligStatus";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealType", itemType).setParameter("districtId", 
				districtId).setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).setParameter("isNeedy", isNeedy);
		resp = query.getResultList();
		return resp;
	}

	/**This method used for get the meals served days count**/
	@Override
	public Integer mealsServingDays(Long mealSchoolId, String startDate, String endDate, String itemType) {
		Integer servedDays = 0;
		String qry = "select count(DISTINCT date(mta.transactionDateTime)) from MasterTransactionsAudit mta inner join "
				+ "StudentWiseTransactions swt on mta.recId = swt.MasterTransactionsAudit_RecId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :mealType and swt.grade != 'staff' and "
				+ "mta.mealSchool_schoolId = :mealSchoolId and swt.eligStatus is not null and swt.isPosted=true and "
				+ "mta.transactionDateTime between :startDate and :endDate";
		servedDays = Integer.parseInt(entityManager.createNativeQuery(qry).setParameter("mealType", itemType).setParameter("mealSchoolId", 
				mealSchoolId).setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).getSingleResult().toString());
		return servedDays;
	}

	/**This method used for get the meals served days count**/
	@Override
	public Integer distMealsServingDays(Long districtId, String startDate, String endDate, String itemType) {
		Integer servedDays = 0;
		String qry = "select count(DISTINCT date(mta.transactionDateTime)) from MasterTransactionsAudit mta inner join "
				+ "StudentWiseTransactions swt on mta.recId = swt.MasterTransactionsAudit_RecId  Inner Join MealSchool_v2 ms "
				+ "on mta.mealSchool_schoolId = ms.schoolId where mta.purchaseItemType is not null and mta.purchaseItemType = :mealType "
				+ "and swt.grade != 'staff' and ms.districtId = :districtId and swt.eligStatus is not null and swt.isPosted=true and "
				+ "mta.transactionDateTime between :startDate and :endDate";
		servedDays = Integer.parseInt(entityManager.createNativeQuery(qry).setParameter("mealType", itemType).setParameter("districtId", 
				districtId).setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).getSingleResult().toString());
		return servedDays;
	}

	/**This method used for get the students count based on their eligibility and date**/
	@Override
	public List<Object[]> dailyAuditCheck(Long mealSchoolId, String itemType, String startDate, String endDate, String timezoneV) {	
		List<Object[]> respObj = null;
		String query = "select count(swt.recId),swt.eligStatus, DATE_FORMAT(Date(CONVERT_TZ(mta.transactionDateTime,'+00:00',:timezone)),'%d') from StudentWiseTransactions swt"
				+ " inner join MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :itemType and mta.transactionType='Purchase' and mta.mealSchool_schoolId = :mealSchoolId"
				+ " and swt.eligStatus is not null and swt.isPosted=true AND (swt.mealType IS NULL OR swt.mealType = 'Regular') and swt.isEmrgLunchServe= false and swt.grade != 'staff' and mta.transactionDateTime between :startDate and :endDate group by "
				+ "swt.eligStatus, DATE_FORMAT(Date(CONVERT_TZ(mta.transactionDateTime,'+00:00',:timezone)),'%d')";
		respObj = entityManager.createNativeQuery(query).setParameter("itemType", itemType).setParameter("mealSchoolId", mealSchoolId).setParameter("timezone", timezoneV)
				.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).getResultList();
		return respObj;
	}
	
	/**This method used for get the students count based on their eligibility and date**/
	@Override
	public List<Object[]> staffDailyAuditCheck(Long mealSchoolId, String itemType, String startDate, String endDate, String timezoneV) {	
		List<Object[]> respObj = null;
		String query = "select count(swt.recId),swt.eligStatus, DATE_FORMAT(Date(CONVERT_TZ(mta.transactionDateTime,'+00:00',:timezone)),'%d') from StudentWiseTransactions swt"
				+ " inner join MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :itemType and mta.mealSchool_schoolId = :mealSchoolId"
				+ " and swt.eligStatus is not null and swt.isPosted=true AND (swt.mealType IS NULL OR swt.mealType != 'ALaCarte') and swt.isEmrgLunchServe= false and swt.grade = 'staff' and mta.transactionDateTime between :startDate and :endDate group by "
				+ "swt.eligStatus, DATE_FORMAT(Date(CONVERT_TZ(mta.transactionDateTime,'+00:00',:timezone)),'%d')";
		respObj = entityManager.createNativeQuery(query).setParameter("itemType", itemType).setParameter("mealSchoolId", mealSchoolId).setParameter("timezone", timezoneV)
				.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).getResultList();
		return respObj;
	}
	
	/**This method used for get the daily income based on their eligibility and date**/
	@Override
	public List<Object[]> dailyReimbIncome(Long mealSchoolId, String itemType, String startDate, String endDate, String timezoneV) {	
		List<Object[]> respObj = null;
		String query = "select SUM(IFNULL(swt.prepaidAmt,0)), SUM(IFNULL(swt.ccAmt,0)), SUM(IFNULL(swt.chargedAmt,0))"
				+ ",swt.eligStatus, DATE_FORMAT(Date(CONVERT_TZ(mta.transactionDateTime,'+00:00',:timezone)),'%d') from StudentWiseTransactions swt"
				+ " inner join MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :itemType and mta.mealSchool_schoolId = :mealSchoolId"
				+ " and swt.eligStatus is not null and swt.eligStatus != 0 and swt.isEmrgLunchServe= false and swt.grade != 'staff' and "
				+ "(swt.mealType is null or swt.mealType='Regular') AND swt.isPosted=true and mta.transactionDateTime between :startDate and :endDate group by "
				+ "swt.eligStatus, DATE_FORMAT(Date(CONVERT_TZ(mta.transactionDateTime,'+00:00',:timezone)),'%d')";
		respObj = entityManager.createNativeQuery(query).setParameter("itemType", itemType).setParameter("mealSchoolId", mealSchoolId).setParameter("timezone", timezoneV)
				.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).getResultList();
		return respObj;
	}
	
	/**This method used for get the daily income based on type and date**/
	@Override
	public List<Object[]> dailyIncomeByType(Long mealSchoolId, String itemType, String startDate, String endDate, String timezoneV, Boolean isStaff) {	
		List<Object[]> respObj = null;
		String query = "select SUM(IFNULL(swt.prepaidAmt,0)), SUM(IFNULL(swt.ccAmt,0)), SUM(IFNULL(swt.chargedAmt,0))"
				+ ",IFNULL(swt.mealType,'Regular'), DATE_FORMAT(Date(CONVERT_TZ(mta.transactionDateTime,'+00:00',:timezone)),'%d') from StudentWiseTransactions swt"
				+ " inner join MasterTransactionsAudit mta on swt.MasterTransactionsAudit_RecId = mta.recId where "
				+ "mta.purchaseItemType is not null and mta.purchaseItemType = :itemType AND swt.isPosted=true and mta.mealSchool_schoolId = :mealSchoolId"
				+ " and swt.eligStatus is not null ";
		if(isStaff)
			query = query+"and swt.grade = 'staff'";
		else
			query = query+"and swt.grade != 'staff'";
		query = query+" and mta.transactionDateTime between :startDate and :endDate group by "
				+ "IFNULL(swt.mealType,'Regular'), DATE_FORMAT(Date(CONVERT_TZ(mta.transactionDateTime,'+00:00',:timezone)),'%d')";
		respObj = entityManager.createNativeQuery(query).setParameter("itemType", itemType).setParameter("mealSchoolId", mealSchoolId).setParameter("timezone", timezoneV)
				.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).getResultList();
		return respObj;
	}

	/**This method used for get the school holidays**/
	@Override
	public List<String> getSchoolHolidays(Long mealSchoolId, String yearMonth, String itemType) {
		List<String> schoolHolidays = null;
		String query = "";
		/*if(itemType.equalsIgnoreCase("Breakfast")){
			query = "select DATE_FORMAT(date(bi.breakfastDate),'%d') from BreakfastMaster bm inner join BreakfastItems bi "
					+ "on bm.recId = bi.breakfastMaster_Id where bm.mealSchool_schoolId = :mealSchoolId and "
					+ "bm.yearMonth = :yearMonth and bi.itemType = 'HOLIDAY' group by DATE_FORMAT(date(bi.breakfastDate),'%d')";
		}else{
			query = "select DATE_FORMAT(date(mm.mealDate),'%d') from SchoolMealsSummary_v2 sms inner join SchoolMeals_v2 sm "
					+ "on sms.schoolId=sm.schoolMealSummary_schoolId inner join MealMenu_v2 mm on sm.mealMenu_Id=mm.mealId "
					+ "where sms.mealSchool_schoolId = :mealSchoolId and sms.yearMonth = :yearMonth and sm.isDelete = 0 and "
					+ "mm.mealtype = 'HOLIDAY' group by DATE_FORMAT(date(mm.mealDate),'%d')";
		}*/
		
		query = "select DATE_FORMAT(date(c.date),'%d') from meal_calendar_summary s inner join meal_calendar c "
				+ "on s.id=c.meal_calendar_summary_id inner join menu_items m on c.menu_item_id=m.id "
				+ "where s.mealSchool_schoolId = :mealSchoolId and s.yearMonth = :yearMonth and c.isActive = 1 and "
				+ "m.category = 'HOLIDAY' group by DATE_FORMAT(date(c.date),'%d')";
		schoolHolidays = entityManager.createNativeQuery(query).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("yearMonth", yearMonth).getResultList();
		return schoolHolidays;
	}

	/**Get all the available meals grade**/
	@Override
	public List<String> getAllGrades(Long mealSchoolId, Date startDate, Date endDate, List<String> grades, String itemType) {
		List<String> gradesFinal = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder sb = new StringBuilder();
		sb.append("Select distinct sg.grades_name from menu_items m INNER JOIN meal_calendar c "
			+ "ON m.id = c.menu_item_id INNER JOIN meal_calendar_summary sms on c.meal_calendar_summary_id = sms.id "
			+ "INNER JOIN meal_summary_grades sg ON sms.id = sg.meal_calendar_summary_id where m.category = :itemType and "
			+ "sms.isPublished = 1 and c.isActive=true and sms.mealSchool_schoolId = :mealSchoolId and (Date(c.date) between :startdate and :endDate)");
		if(grades != null)
			sb.append(" and sg.grades_name IN (:grades)");
		Query query = entityManager.createNativeQuery(sb.toString()).setParameter("itemType", itemType).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("startdate", sdf.format(startDate)).setParameter("endDate", sdf.format(endDate));
		if(grades != null)
			query.setParameter("grades", grades);
		gradesFinal = query.getResultList();
		logger.info("getAllGrades method executed successfully.");
		return gradesFinal;
	}

	/**This method used for get the package payment transactions info**/
	@Override
	public List<Object[]> packagePaymentsTrx(Long mealSchoolId, Integer schoolYear, String startDate, String endDate,
			Long stdRecId) {
		String qry = "Select su.firstName, su.lastName, ps.createdOn, ps.paymentType, s.paidAmt, ps.transferId, "
				+ "ps.createdBy, p.packageName, s.startDate, s.endDate, p.`type`, su.gradeName, ps.checkNumb from PackageSubscriptionsTrx ps "
				+ "inner join SubscriptionsTrxByStd s on ps.trxId = s.PackageSubscriptionsTrx_trxId inner join "
				+ "SchoolPackage p on s.schoolPackage_packageId = p.packageId inner join StudentUser_v2 su on "
				+ "s.studentUser_userId = su.userId where ps.mealSchool_schoolId = :mealSchoolId AND "
				+ "su.schoolYear = :schoolYear and (ps.createdOn BETWEEN :startDt AND :endDt) and ps.isPaid = true";
		if(stdRecId != null)
			qry = qry + " and su.userId = :stdRecId";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId)
				.setParameter("schoolYear", schoolYear).setParameter("startDt", startDate).setParameter("endDt", endDate);
		if(stdRecId != null)
			query.setParameter("stdRecId", stdRecId);
		return query.getResultList();
	}

	/**This method used for get the paymob charges transactions report**/
	@Override
	public List<Object[]> payMobTrxCharges(Long mealSchoolId, String startDate, String endDate) {
		return entityManager.createNativeQuery("SELECT mta.transactionDateTime, mta.totalTransactionAmount, mta.transferId, "
				+ "mta.chargeId, mta.appFee, mta.createdBy FROM MasterTransactionsAudit mta WHERE "
				+ "mta.mealSchool_schoolId = :mealSchoolId AND mta.appFee IS NOT NULL AND mta.paymentGateway = 'PayMob' AND "
				+ "(mta.transactionDateTime BETWEEN :startDate AND :endDate)").setParameter("mealSchoolId", mealSchoolId)
				.setParameter("startDate", startDate.replace("T", " ")).setParameter("endDate", endDate.replace("T", " ")).getResultList();
	}

	/**This method used for generate the order cost report**/
	@Override
	public List<Object[]> orderCostReport(Long mealSchoolId, String startDt, String endDt, ItemTypeConstants menuType, SchoolGrades	grade) {
		//String itemType =CommonUtil.getItemType(menuType);
		String qry = "Select su.studentId, su.gradeName, su.firstName, su.lastName, c.date AS DATE, "
				+ "SUM(if(m.category='EXTRA',c.price,(IF(moa.isEligibleForReducedPrice,c.reducedPrice,(IF(moa.isEligForDiscount,(c.price-moa.itemDiscount),IF(moa.isEligibleForFreeMeal,0,c.price))))))) AS cost"
				+ " from menu_items m INNER JOIN meal_calendar c ON m.id = c.menu_item_id INNER JOIN meal_calendar_summary sms on c.meal_calendar_summary_id = sms.id inner join "
				+ "OrderMealItemsDetailReport om on c.id=om.schoolMealId  and c.date = om.mealDate Inner Join MealOrdersAudit_v2 moa on om.orderId = moa.schoolId inner join StudentUser_v2 su "
				+ "on om.studentRecId = su.userId and su.isActive = true  where c.isActive = true and sms.mealSchool_schoolId = :mealSchoolId and sms.isPublished = 1 and "
				+ "(Date(c.date)  between :startDt and :endDt) ";
		if(menuType != null)
			qry = qry+"and sms.mealType = :menuType ";
		if(grade != null)
			qry = qry+"and su.gradeName = :grade ";
		qry = qry+"group by su.studentId, su.firstName, su.lastName, Date(c.date) order by Date(c.date)";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("startDt", startDt).setParameter("endDt", endDt);
		if(menuType != null)
			query = query.setParameter("menuType", menuType.toString());
		if(grade != null)
			query = query.setParameter("grade", grade.toString());
		return query.getResultList();
	}

	@Override
	public List<Object[]> catererOrders(Long catererId, Date startDate, Date endDate, String itemType,
			ItemTypeConstants menuType, Long mealSchoolId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String query = "select o.studentRecId, o.mealName, o.mealDate,o.mealType, su.firstName,su.lastName, su.gradeName, su.allergies, su.studentId, su.teacherName, su.mealSchool_schoolId from OrderMealItemsDetailReport o Inner Join MealSchool_v2 ms on o.mealSchoolId = ms.schoolId "
				+ "Inner Join meal_calendar c on c.id=o.schoolMealId and c.date = o.mealDate Inner Join menu_items m on m.id = c.menu_item_id INNER JOIN meal_calendar_summary sms on c.meal_calendar_summary_id = sms.id "
				+ "Inner Join StudentUser_v2 su on o.studentRecId = su.userId"
				+ " where ms.catererId = :catererId and o.menuType = :menuType and (Date(o.mealDate) between :startDate and :endDate) "
				+ "and (m.category = :itemType or (m.category='SIDE' and sms.isSideSelect is not null and sms.isSideSelect = true) or (m.category='EXTRA' and sms.isExtraPreOrder is not null and sms.isExtraPreOrder = true and sms.extraEnableForCaterer = true)) ";
		if(mealSchoolId != null)
			query = query + " and ms.schoolId = :mealSchoolId";
		query = query+" group by o.studentRecId, o.mealName, Date(o.mealDate),o.mealType";
		Query qry = entityManager.createNativeQuery(query).setParameter("catererId", catererId).setParameter("menuType", menuType.toString()).setParameter("startDate", sdf.format(startDate))
				.setParameter("endDate", sdf.format(endDate)).setParameter("itemType", itemType);
		if(mealSchoolId != null)
			qry.setParameter("mealSchoolId", mealSchoolId);
		return qry.getResultList();
	}

	@Override
	public Double paidAmt(Long mealSchoolId, String startDate, String endDate, String trxType) {
		Object ob = entityManager.createNativeQuery("SELECT SUM(mta.totalTransactionAmount) FROM MasterTransactionsAudit mta "
				+ "WHERE mta.transactionType = :trxType AND mta.mealSchool_schoolId = :mealSchoolId AND mta.transactionDateTime between :startDate and :endDate")
				.setParameter("trxType", trxType).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate", startDate).setParameter("endDate", endDate)
				.getSingleResult();
		return ob != null ? Double.valueOf(ob.toString()) : 0.0;
	}

	@Override
	/**This method used for get the charges info**/
	public List<Object[]> getCharges(Long mealSchoolId, String startDt, String endDt, String grade, Boolean alcTotal) {
		String qry = "SELECT SUM(IFNULL(s.prepaidAmt,0)), SUM(IFNULL(s.ccAmt,0)), SUM(IFNULL(s.chargedAmt,0)),";
		if(alcTotal == null)
			qry =  qry+"IFNULL(s.mealType,'Regular'),";
		else
			qry =  qry+"'Total',";
		qry =  qry+" m.purchaseItemType FROM StudentWiseTransactions s INNER JOIN MasterTransactionsAudit m ON "
				+ "s.MasterTransactionsAudit_RecId = m.recId WHERE m.mealSchool_schoolId = :mealSchoolId AND m.purchaseItemType IN ('Lunch','Breakfast','Snack')"
				+ " and s.isPosted=true AND (m.transactionDateTime BETWEEN :startDate AND :endDate)";
		if(grade == null)
			qry = qry+"  AND s.grade != 'staff'";
		else if(!grade.equalsIgnoreCase("both"))
			qry = qry+"  AND s.grade = :grade";
		if(alcTotal != null && alcTotal)
			qry = qry+"  AND s.mealType = 'ALaCarte'";
		else if(alcTotal != null && !alcTotal)
			qry = qry+"  AND (s.mealType is null or s.mealType != 'ALaCarte')";
		/*if(menuType != null)
			qry = qry+" AND m.purchaseItemType = :itemType";
		else 
			qry = qry+" AND m.purchaseItemType IN ('Lunch','Breakfast','Snack')";*/
		qry = qry+" GROUP BY m.purchaseItemType";
		if(alcTotal == null)
			qry = qry+",IFNULL(s.mealType,'Regular')";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate", startDt)
				.setParameter("endDate", endDt);
		if(grade != null && !grade.equalsIgnoreCase("both"))
			query.setParameter("grade", grade);
		/*if(menuType != null)
			query.setParameter("itemType", menuType);*/
		return query.getResultList();
	}
	
	@Override
	/**This method used for get the total sales info**/
	public Object[] totalSales(Long mealSchoolId, String startDt, String endDt,Boolean isPrg) {
		String qry = "SELECT SUM(IFNULL(s.prepaidAmt,0)), SUM(IFNULL(s.ccAmt,0)), SUM(IFNULL(s.chargedAmt,0)) FROM StudentWiseTransactions s INNER JOIN MasterTransactionsAudit m ON "
				+ "s.MasterTransactionsAudit_RecId = m.recId WHERE m.mealSchool_schoolId = :mealSchoolId AND m.purchaseItemType IN ('Lunch','Breakfast','Snack')"
				+ " AND (m.transactionDateTime BETWEEN :startDate AND :endDate) AND s.isPosted=true ";
		if(isPrg)
			qry=qry+" and (s.mealType is null or s.mealType = 'Regular') and s.grade != 'staff' ";
		else
			qry=qry+" and ((s.grade != 'staff' and s.mealType is not null and s.mealType != 'Regular') || s.grade = 'staff') ";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate", startDt)
				.setParameter("endDate", endDt);
		return (Object[]) query.getResultList().get(0);
	}
	
	@Override
	/**This method used for get the online charged info**/
	public Double chargedPOS(Long mealSchoolId, String startDt, String endDt) {
		String qry = "SELECT SUM(IFNULL(s.chargedAmt,0)) FROM StudentWiseTransactions s INNER JOIN MasterTransactionsAudit m ON "
				+ "s.MasterTransactionsAudit_RecId = m.recId WHERE m.mealSchool_schoolId = :mealSchoolId AND m.purchaseItemType IN ('Lunch','Breakfast','Snack')"
				+ " AND (m.transactionDateTime BETWEEN :startDate AND :endDate) AND s.isPosted=true AND m.posDeposit=true ";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate", startDt)
				.setParameter("endDate", endDt);
		return (query.getSingleResult() != null ? Double.valueOf(query.getSingleResult().toString()) : 0.0);
	}
	
	@Override
	/**This method used for get the online charged info**/
	public Double totPosDeposit(Long mealSchoolId, String startDt, String endDt, Boolean isDirect) {
		String qry = "SELECT SUM(IFNULL(s.transactionAmount,0)) FROM StudentWiseTransactions s INNER JOIN MasterTransactionsAudit m ON "
				+ "s.MasterTransactionsAudit_RecId = m.recId WHERE m.mealSchool_schoolId = :mealSchoolId "
				+ " AND (m.transactionDateTime BETWEEN :startDate AND :endDate) AND s.isPosted=true AND m.posDeposit=true and m.directPosDeposit=:isDirect";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate", startDt)
				.setParameter("endDate", endDt).setParameter("isDirect", isDirect);
		return (query.getSingleResult() != null ? Double.valueOf(query.getSingleResult().toString()) : 0.0);
	}

	@Override
	public Double schoolDeposit(Long mealSchoolId, String startDt, String endDt) {
		String qry = "SELECT SUM(IFNULL(s.transactionAmount,0)) FROM StudentWiseTransactions s INNER JOIN MasterTransactionsAudit m ON "
				+ "s.MasterTransactionsAudit_RecId = m.recId WHERE m.mealSchool_schoolId = :mealSchoolId "
				+ " AND (m.transactionDateTime BETWEEN :startDate AND :endDate) AND s.isPosted=true AND m.posDeposit=false and "
				+ "m.transactionType IN ('Deposit','InstantPayment') AND m.paymentType IN ('Cash','Check','CreditCard')";
		Query query = entityManager.createNativeQuery(qry).setParameter("mealSchoolId", mealSchoolId).setParameter("startDate", startDt)
				.setParameter("endDate", endDt);
		return (query.getSingleResult() != null ? Double.valueOf(query.getSingleResult().toString()) : 0.0);
	}
}
