package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.response.SchoolMealReportResp;
import com.mealManage.response.StudentInfoWithMeal;

/** This utility class used for export the orders Reports in pdf file format **/
@Component
public class OrderPdfReportGeneration{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 12);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private SchoolMealReportResp schoolMealReportResp;
    private List<String> header;
    private Map<String, String> gradesKeyVal;
    private Boolean isWeekStatus = false; 
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");
	private SimpleDateFormat sdf = null;
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
	private static DateFormat formatter = new SimpleDateFormat("EEEE");
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	
	/**This method used for generate the pdf of School report
	 * @throws Exception **/
	public void exportOrderPdfReport(SchoolMealReportResp schoolMealReportResp, 
			HttpServletResponse response, String schoolPdfFileName, Long mealSchoolId, Boolean isItemized, ItemTypeConstants menuType, String dateFormat) throws Exception{
		this.schoolMealReportResp = schoolMealReportResp;
		String pdfFilePath = "OrderReport_"+mealSchoolId+".pdf";
		if(schoolPdfFileName != null && !schoolPdfFileName.equalsIgnoreCase(""))
			pdfFilePath = schoolPdfFileName;
		isWeekStatus = false; 
		sdf = new SimpleDateFormat(dateFormat);
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			checkReportIsByWeek(pdfFilePath);
			List<String> dateList = new ArrayList<String>();
			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(schoolMealReportResp.getCountryCode());//setting the grade key value (i.e. one means 1, two means 2,..etc)
			//if selected date range is one week
    		if(isWeekStatus != null && isWeekStatus){
    			if(schoolMealReportResp.getIsAllergyEnabled() != null && schoolMealReportResp.getIsAllergyEnabled())
    				header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME", "ALLERGIES"));
    			else
    				header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME"));
            	String dateVal = schoolMealReportResp.getStartDate();
            	Boolean status = true;
            	while(status){
            		if(isItemized && !formatter.format(sdf1.parse(dateVal)).equalsIgnoreCase("Friday") && 
            				!formatter.format(sdf1.parse(dateVal)).equalsIgnoreCase("Saturday"))
        		    	dateList.add(dateVal);
            		if(!isItemized && !formatter.format(sdf1.parse(dateVal)).equalsIgnoreCase("Saturday") 
            				&& !formatter.format(sdf1.parse(dateVal)).equalsIgnoreCase("Sunday") )
            			dateList.add(dateVal);
            		if(dateVal.equalsIgnoreCase(schoolMealReportResp.getEndDate()))
            			status = false;
            		else
            			dateVal = sdf1.format(addDays(sdf1.parse(dateVal), 1));
            	}
    			 /*document.add(createOrderWeeklyTable("WEEKLY ORDERS REPORT    "+sdf2.format(sdf1.parse(dateList.get(0)))
    			 	+" - "+sdf2.format(sdf1.parse(dateList.get(4))), schoolMealReportResp, dateList));*/
    		}else{
    			header = new LinkedList<String>(Arrays.asList("S.NO.","STUDENT NAME", "ITEMS NAME", "SIDES"));
    			if(schoolMealReportResp.getHavingExtraPreOrders() != null && schoolMealReportResp.getHavingExtraPreOrders())
    				header.add("EXTRA");
    			if(schoolMealReportResp.getIsAllergyEnabled() != null && schoolMealReportResp.getIsAllergyEnabled())
    				header.add("ALLERGIES");
    			header.add("SERVED");
    			
    			 //document.add(createOrderDayTable("ORDERS REPORT    "+sdf.format(sdf1.parse(schoolMealReportResp.getStartDate())), schoolMealReportResp));
    		}
    		List<StudentInfoWithMeal> gradeStudent = null;
    		Map<String, List<StudentInfoWithMeal>> studentGroupByGrade = null;
    		Map<String, List<StudentInfoWithMeal>> studentsGroupByTeacher = null;
			Map<String, List<StudentInfoWithMeal>> studentsGroupByTeacherSorted = null;
			studentGroupByGrade = schoolMealReportResp.getStudentWithMeal().stream().collect(Collectors.groupingBy(StudentInfoWithMeal::getGrade));
			SchoolGrades[] schoolGrades = new GradeFormatBuild().convertToSchoolGradeSet(new ArrayList<String>(
					studentGroupByGrade.keySet())).toArray(new SchoolGrades[0]);
			Arrays.sort(schoolGrades);
			for(SchoolGrades gradeVal : schoolGrades){
				/**Getting all the student info with other details for specified grade**/
				gradeStudent = studentGroupByGrade.get(gradeVal.toString());
				/**Grouping the student info of that specified grade by teacher name and storing into map which having key 
				 * as the teacher name and value as the list of POJO (i.e. which contains student info with other details)**/
				studentsGroupByTeacher = gradeStudent.stream().collect(Collectors.groupingBy(StudentInfoWithMeal::getTeacherName));
				studentsGroupByTeacherSorted = studentsGroupByTeacher.entrySet().stream().sorted(Map.Entry.comparingByKey())
						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
				/**Iterating the map for each teacher name**/
				for(Entry<String, List<StudentInfoWithMeal>> studentEntry : studentsGroupByTeacherSorted.entrySet()){
					if(isWeekStatus != null && isWeekStatus)
						document.add(createOrderWeeklyTable(menuType.toString().toUpperCase()+" WEEKLY ORDERS REPORT    "+sdf.format(sdf1.parse(dateList.get(0)))
	    			 	+" - "+sdf.format(sdf1.parse(dateList.get(4))), studentEntry, dateList, gradeVal));
					else
						document.add(createOrderDayTable(menuType.toString().toUpperCase()+" ORDERS REPORT    "+sdf.format(sdf1.parse(schoolMealReportResp.getStartDate())), studentEntry, gradeVal));
					document.newPage();	
				}	  
			}
			document.close();    
			if(schoolPdfFileName == null || schoolPdfFileName.equalsIgnoreCase("") || response != null){
				InputStream myStream = new FileInputStream(pdfFilePath);
	    		response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
	    		IOUtils.copy(myStream, response.getOutputStream());
	    		response.flushBuffer();
	    		myStream.close();
	    		new File(pdfFilePath).delete();
			}  
		}catch (Exception e){
			logger.error("Failed to generate order pdf report due to "+e.getMessage());
		}
	}
	/**This method used for check weekly report status**/
	private void checkReportIsByWeek(String pdfPath) throws Exception{	
		long difference = sdf1.parse(schoolMealReportResp.getEndDate()).getTime() - sdf1.parse(schoolMealReportResp.getStartDate()).getTime();		
		if(difference/(1000*60*60*24) == 6)
			isWeekStatus = true;
    }
	
		/**This method used for build the Daily Orders Report**/
		private Element createOrderDayTable(String reportName, Entry<String, List<StudentInfoWithMeal>> studentsEntry, SchoolGrades gradeVal) throws Exception{
			PdfPTable table= null;
			/*if(header.size() == 7)
				table = new PdfPTable(new float[]{20,50,60,60,30});
			else if(header.size() == 8)
				table = new PdfPTable(new float[]{20,50,60,60,40,30});
			else if(header.size() == 9)
				table = new PdfPTable(new float[]{20,50,60,60,60,40,30});
			else*/
				table = new PdfPTable(header.size());
			PdfPCell cell = new PdfPCell(new Phrase(reportName, generalDateFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			cell.setColspan(header.size());
			cell.setPaddingBottom(10);
			table.addCell(cell);    
			cell = new PdfPCell(new Phrase("GRADE: "+gradesKeyVal.get(gradeVal.toString())+",             TEACHER NAME: "+studentsEntry.getKey().toUpperCase(), boldFont));
        	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
        	cell.setColspan(header.size());
        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	table.addCell(cell); 	
        	for(String head2 : header){
        		cell = new PdfPCell(new Phrase(head2, boldFont));
            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cell); 	
        	}
        	List<StudentInfoWithMeal> allStudentWithMeal = studentsEntry.getValue();
			 /**Sort out the student by first & last name**/
			 allStudentWithMeal.sort(Comparator.comparing(StudentInfoWithMeal::getStudentFName)
					.thenComparing(StudentInfoWithMeal::getStudentLName));	
			 int i = 0;
			 Map<String, String> sidesByGrade = new HashMap<String, String>();
			 if(schoolMealReportResp.getMealsByGradeAndDate() != null){
				 schoolMealReportResp.getMealsByGradeAndDate().forEach((key, value) -> 
				 	sidesByGrade.put(key, value.get(schoolMealReportResp.getStartDate()).get(0))
				);
			 }
    		for (StudentInfoWithMeal studentInfoWithMeal : allStudentWithMeal) {
     			String sideNames = "";
     			String extraNames = "";
    			String sno=String.valueOf(++i);
    			cell = new PdfPCell(new Phrase(sno, generalFont));
    			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(cell);
    			String studentName = "";
    			if(studentInfoWithMeal.getStudentLName() != null)
    				studentName = studentInfoWithMeal.getStudentLName();
    			if(studentInfoWithMeal.getStudentFName() != null)
    				studentName = studentName +", "+ studentInfoWithMeal.getStudentFName();    			
    			cell = new PdfPCell(new Phrase(studentName, generalFont));
    			table.addCell(cell);
    			/*cell = new PdfPCell(new Phrase(gradesKeyVal.get(studentInfoWithMeal.getGrade()), generalFont));
    			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(cell);
    			cell = new PdfPCell(new Phrase(studentInfoWithMeal.getTeacherName() != null ? studentInfoWithMeal.getTeacherName():"", generalFont));
    			table.addCell(cell);*/
    			String itemNames = "";
    			if(studentInfoWithMeal.getMealOrderedByDate() != null){
    				for(String menuName : studentInfoWithMeal.getMealOrderedByDate().get(schoolMealReportResp.getStartDate())){
        				if(!itemNames.trim().isEmpty())
        					itemNames = itemNames+", ";
        				itemNames = itemNames+menuName;
        			}
    			}    			
    			if(studentInfoWithMeal.getSideOrderedByDate() != null 
    					&& studentInfoWithMeal.getSideOrderedByDate().get(schoolMealReportResp.getStartDate()) != null){
    				for(String menuName : studentInfoWithMeal.getSideOrderedByDate().get(schoolMealReportResp.getStartDate())){
        				if(!sideNames.trim().isEmpty())
        					sideNames = sideNames+", ";
        				sideNames = sideNames+menuName;
        			}
    			}
    			if(studentInfoWithMeal.getExtraOrderedByDate() != null 
    					&& studentInfoWithMeal.getExtraOrderedByDate().get(schoolMealReportResp.getStartDate()) != null){
    				for(String menuName : studentInfoWithMeal.getExtraOrderedByDate().get(schoolMealReportResp.getStartDate())){
        				if(!extraNames.trim().isEmpty())
        					extraNames = extraNames+", ";
        				extraNames = extraNames+menuName;
        			}
    			}
    			
    			cell = new PdfPCell(new Phrase(itemNames, generalFont));
    			table.addCell(cell);
    			cell = new PdfPCell(new Phrase(sideNames != null ? sideNames : "", generalFont));
    			table.addCell(cell);
    			if(schoolMealReportResp.getHavingExtraPreOrders() != null && schoolMealReportResp.getHavingExtraPreOrders()){
    				cell = new PdfPCell(new Phrase(extraNames != null ? extraNames : "", generalFont));
        			table.addCell(cell);
    			}    				
    			if(schoolMealReportResp.getIsAllergyEnabled() != null && schoolMealReportResp.getIsAllergyEnabled()){
    				cell = new PdfPCell(new Phrase(studentInfoWithMeal.getAllergies() != null ? studentInfoWithMeal.getAllergies() : "", generalFont));
        			table.addCell(cell);
    			}    
    			//cell = new PdfPCell(new Phrase(studentInfoWithMeal.getServed(), generalFont));
    			cell = new PdfPCell(new Phrase("", generalFont));
    			table.addCell(cell);
    		}
    		table.setWidthPercentage(100);
			return table;
		}
		
		/**This method used for build the weekly Orders Report**/
		private Element createOrderWeeklyTable(String reportName, Entry<String, List<StudentInfoWithMeal>> studentsEntry, List<String> dateList, SchoolGrades gradeVal) throws Exception{
			PdfPTable table= null;
			if(header.size() == 2)
				table = new PdfPTable(new float[]{20,50,50,50,50,50,50});
			else if(header.size() == 3)
				table = new PdfPTable(new float[]{30,50,50,50,50,50,50,50});
			else
				table = new PdfPTable(header.size());
			PdfPCell cell = new PdfPCell(new Phrase(reportName, generalDateFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			cell.setColspan(header.size()+5);
			cell.setPaddingBottom(10);
			table.addCell(cell);    	
			cell = new PdfPCell(new Phrase("GRADE: "+gradesKeyVal.get(gradeVal.toString())+",             TEACHER NAME: "+studentsEntry.getKey().toUpperCase(), boldFont));
        	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
        	cell.setColspan(header.size()+5);
        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	table.addCell(cell); 
        	for(String head2 : header){
        		cell = new PdfPCell(new Phrase(head2, boldFont));
            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cell); 	
        	}        	
        	for(String dateVal : dateList){
        		cell = new PdfPCell(new Phrase(sdf2.format(sdf1.parse(dateVal)), boldFont));
            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cell);
        	}
        	List<StudentInfoWithMeal> allStudentWithMeal = studentsEntry.getValue();
			 /**Sort out the student by first & last name**/
			 allStudentWithMeal.sort(Comparator.comparing(StudentInfoWithMeal::getStudentFName)
					.thenComparing(StudentInfoWithMeal::getStudentLName));	
			 int i = 0;
			
    		for (StudentInfoWithMeal studentInfoWithMeal : allStudentWithMeal) {
    			String sno=String.valueOf(++i);
    			cell = new PdfPCell(new Phrase(sno, generalFont));
    			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(cell);
    			String studentName = "";
    			if(studentInfoWithMeal.getStudentLName() != null)
    				studentName = studentInfoWithMeal.getStudentLName();
    			if(studentInfoWithMeal.getStudentFName() != null)
    				studentName = studentName +", "+ studentInfoWithMeal.getStudentFName();    			
    			cell = new PdfPCell(new Phrase(studentName, generalFont));
    			table.addCell(cell);
    			/*cell = new PdfPCell(new Phrase(gradesKeyVal.get(studentInfoWithMeal.getGrade()), generalFont));
    			table.addCell(cell);
    			cell = new PdfPCell(new Phrase(studentInfoWithMeal.getTeacherName() != null ? studentInfoWithMeal.getTeacherName():"", generalFont));
    			table.addCell(cell);*/
    			if(schoolMealReportResp.getIsAllergyEnabled() != null && schoolMealReportResp.getIsAllergyEnabled()){
    				cell = new PdfPCell(new Phrase(studentInfoWithMeal.getAllergies() != null ? studentInfoWithMeal.getAllergies() : "", generalFont));
        			table.addCell(cell);
    			} 
    			for(String date : dateList){
    				String itemNames = "";
    				LinkedList<String> itemsName = new LinkedList<String>();
    				if(studentInfoWithMeal.getMealOrderedByDate() != null && studentInfoWithMeal.getMealOrderedByDate().get(date) != null)
    					itemsName.addAll(studentInfoWithMeal.getMealOrderedByDate().get(date));
    				if(studentInfoWithMeal.getExtraOrderedByDate() != null && studentInfoWithMeal.getExtraOrderedByDate().get(date) != null)
    					itemsName.addAll(studentInfoWithMeal.getExtraOrderedByDate().get(date));
    				
					for (String menuName : itemsName) {
						if (!itemNames.trim().isEmpty())
							itemNames = itemNames + ", ";
						itemNames = itemNames + menuName;
					}
	    			cell = new PdfPCell(new Phrase(itemNames, generalFont));
	        		table.addCell(cell);
    			}
    		}
    		table.setWidthPercentage(100);
			return table;
		}
		
		/**Add no. of days in specific date**/
		private static Date addDays(Date date, int days) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.add(Calendar.DATE, days);
			return cal.getTime();
		}
}