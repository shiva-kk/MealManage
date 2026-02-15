package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.response.SchoolMealReportResp;
import com.mealManage.response.StudentInfoWithMeal;

/** This utility class used for export the School Reports in pdf file format **/
@Component
public class SchoolPdfReportGeneration{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font boldFontSchoolName=FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 12);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private SchoolMealReportResp schoolMealReportResp;
    private List<String> header;
    private Image checkBoxImage;
    private Image rightTickImage;
    private Map<String, String> gradesKeyVal;
    private Boolean isWeekStatus = false; 
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
	
	/**This method used for generate the pdf of School report
	 * @throws Exception **/
	public void exportSchoolPdfReport(SchoolMealReportResp schoolMealReportResp, 
			HttpServletResponse response, String schoolPdfFileName, Long mealSchoolId) throws Exception{
		this.schoolMealReportResp = schoolMealReportResp;
		String pdfFilePath = "SchoolReport_"+mealSchoolId+".pdf";
		if(schoolPdfFileName != null && !schoolPdfFileName.equalsIgnoreCase(""))
			pdfFilePath = schoolPdfFileName;
		/*String logoPath = "";
		if(logoLink != null)
			logoPath = logoLink;
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";*/
		isWeekStatus = false; 
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			//document.add(generateSchoolPdfReport(pdfFilePath, logoPath, schoolName));
    		//document.newPage();
			createContentTable(pdfFilePath);
			//if selected date range is one week
    		if(isWeekStatus != null && isWeekStatus){
    			/**This map having key as the grade and value as the other map which having key as date and value as the list of meal items**/
    			Map<String, Map<String, List<String>>> gradeDateMealMap = new TreeMap<String, Map<String, List<String>>>(
    					schoolMealReportResp.getMealsByGradeAndDate());
    			
    			GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
    			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(schoolMealReportResp.getCountryCode());//setting the grade key value (i.e. one means 1, two means 2,..etc)

    			String rightTickPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/rightTick.PNG";
    			rightTickImage = Image.getInstance(rightTickPath);
    			rightTickImage.scaleAbsolute(11f, 11f);
    			rightTickImage.setAlignment(Image.ALIGN_CENTER);
    			
    			/**Setting generally used header in school report table**/
    			if(schoolMealReportResp.getIsAllergyEnabled() != null && schoolMealReportResp.getIsAllergyEnabled())
    				header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME", "ALLERGIES"));
    			else
    				header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME"));
    			 List<StudentInfoWithMeal> allStudentWithMeal = schoolMealReportResp.getStudentWithMeal();
    			 
    			 /**Sort out the student by first & last name**/
    			 /*allStudentWithMeal.sort(Comparator.comparing(StudentInfoWithMeal::getStudentFName)
    					.thenComparing(StudentInfoWithMeal::getStudentLName));*/
    				
    			/**This object reference will store all the student info with other details for each grade**/
    			List<StudentInfoWithMeal> gradeStudent = null;
    			/**This map having key as the teacher name and value as the list of student info with other details**/
    			Map<String, List<StudentInfoWithMeal>> studentsGroupByTeacher = null;
    			Map<String, List<StudentInfoWithMeal>> studentsGroupByTeacherSorted = null;
    			Map<String, List<String>> dateMealsMapData = null;
    	    	if(gradeDateMealMap != null && gradeDateMealMap.size() > 0){
    	    		/**Get all the grade in sorting order**/
    				SchoolGrades[] schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(new ArrayList<String>(
    						gradeDateMealMap.keySet())).toArray(new SchoolGrades[0]);
    				Arrays.sort(schoolGrades);
    				
    				//iterate the grades and proceed one by one
    				for(SchoolGrades gradeVal : schoolGrades){
    					dateMealsMapData = gradeDateMealMap.get(gradeVal.toString());
    					if(dateMealsMapData != null)
    						dateMealsMapData = dateMealsMapData.entrySet().stream().sorted(Map.Entry.comparingByKey())
    						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        				/**Getting all the student info with other details for specified grade**/
        				gradeStudent = allStudentWithMeal.stream().filter(p ->  p.getGrade() != null && p.getGrade()
        						.equalsIgnoreCase(gradeVal.toString())).collect(Collectors.toCollection(LinkedList::new));
        				
        				/**Grouping the student info of that specified grade by teacher name and storing into map which having key 
        				 * as the teacher name and value as the list of POJO (i.e. which contains student info with other details)**/
        				studentsGroupByTeacher = gradeStudent.stream().collect(Collectors.groupingBy(StudentInfoWithMeal::getTeacherName));
        				studentsGroupByTeacherSorted = studentsGroupByTeacher.entrySet().stream().sorted(Map.Entry.comparingByKey())
        						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        				
        				/**Iterating the map for each teacher name**/
        				for(Entry<String, List<StudentInfoWithMeal>> studentEntry : studentsGroupByTeacherSorted.entrySet()){
        					/**Iterating the map for each meal date**/
            	    		/*for (Entry<String, List<String>> dateMealMapEntry : dateMealsMapData.entrySet()) {
            	    			mealsData = dateMealMapEntry.getValue();
            	    			if(mealsData != null && mealsData.size() > 0){
            	    				document.add(createSchoolWeeklyReport(gradeVal.toString(), mealsData, dateMealMapEntry.getKey(), studentEntry));
            			    		document.newPage();	
            	    			}
            	    		}  	*/
        					document.add(createSchoolWeeklyReport(gradeVal.toString(), dateMealsMapData, studentEntry,"WEEKLY SCHOOL REPORT"));
    			    		document.newPage();	
        				}	  
        			}
    				
    	    	}
    		}else{
    			/**This map having key as the grade and value as the other map which having key as date and value as the list of meal items**/
    			Map<String, Map<String, List<String>>> gradeDateMealMap = new TreeMap<String, Map<String, List<String>>>(
    					schoolMealReportResp.getMealsByGradeAndDate());
    			
    			GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
    			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(schoolMealReportResp.getCountryCode()); //setting the grade key value (i.e. one means 1, two means 2,..etc)

    			String checkBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkBox.PNG";
    			checkBoxImage = Image.getInstance(checkBoxPath);
    			checkBoxImage.scaleAbsolute(11f, 11f);
    			checkBoxImage.setAlignment(Image.ALIGN_CENTER);
    			
    			String rightTickPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/rightTick.PNG";
    			rightTickImage = Image.getInstance(rightTickPath);
    			rightTickImage.scaleAbsolute(11f, 11f);
    			rightTickImage.setAlignment(Image.ALIGN_CENTER);
    			
    			/**Setting generally used header in school report table**/
    			if(schoolMealReportResp.getIsAllergyEnabled() != null && schoolMealReportResp.getIsAllergyEnabled())
    				header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME", "ALLERGIES"));
    			else
    				header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME"));
    			 List<StudentInfoWithMeal> allStudentWithMeal = schoolMealReportResp.getStudentWithMeal();
    			 
    			 /**Sort out the student by first & last name**/
    			 /*allStudentWithMeal.sort(Comparator.comparing(StudentInfoWithMeal::getStudentFName)
    					.thenComparing(StudentInfoWithMeal::getStudentLName));*/
    				
    			/**This object reference will store all the student info with other details for each grade**/
    			List<StudentInfoWithMeal> gradeStudent = null;
    			/**This map having key as the teacher name and value as the list of student info with other details**/
    			Map<String, List<StudentInfoWithMeal>> studentsGroupByTeacher = null;
    			Map<String, List<StudentInfoWithMeal>> studentsGroupByTeacherSorted = null;
    			Map<String, List<String>> dateMealsMapData = null;
    			List<String> mealsData = null;
    	    	if(gradeDateMealMap != null && gradeDateMealMap.size() > 0){
    	    		/**Get all the grade in sorting order**/
    				SchoolGrades[] schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(new ArrayList<String>(
    						gradeDateMealMap.keySet())).toArray(new SchoolGrades[0]);
    				Arrays.sort(schoolGrades);
    				
    				//iterate the grades and proceed one by one
    				for(SchoolGrades gradeVal : schoolGrades){
    					dateMealsMapData = gradeDateMealMap.get(gradeVal.toString());
    					if(dateMealsMapData != null)
    						dateMealsMapData = dateMealsMapData.entrySet().stream().sorted(Map.Entry.comparingByKey())
    						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        				/**Getting all the student info with other details for specified grade**/
        				gradeStudent = allStudentWithMeal.stream().filter(p ->  p.getGrade() != null && p.getGrade()
        						.equalsIgnoreCase(gradeVal.toString())).collect(Collectors.toCollection(LinkedList::new));
        				
        				/**Grouping the student info of that specified grade by teacher name and storing into map which having key 
        				 * as the teacher name and value as the list of POJO (i.e. which contains student info with other details)**/
        				studentsGroupByTeacher = gradeStudent.stream().collect(Collectors.groupingBy(StudentInfoWithMeal::getTeacherName));
        				studentsGroupByTeacherSorted = studentsGroupByTeacher.entrySet().stream().sorted(Map.Entry.comparingByKey())
        						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        				
        				/**Iterating the map for each teacher name**/
        				for(Entry<String, List<StudentInfoWithMeal>> studentEntry : studentsGroupByTeacherSorted.entrySet()){
        					/**Iterating the map for each meal date**/
            	    		for (Entry<String, List<String>> dateMealMapEntry : dateMealsMapData.entrySet()) {
            	    			mealsData = dateMealMapEntry.getValue();
            	    			if(mealsData != null && mealsData.size() > 0){
            	    				document.add(createSchoolDayTable(gradeVal.toString(), mealsData, dateMealMapEntry.getKey(), studentEntry, "SCHOOL REPORT"));
            			    		document.newPage();	
            	    			}
            	    		}  	
        				}	  
        			}
    				
    	    	}
    		}
    		
    		/*else{
	    		document.add(createEmptySchoolTable());
	    	}*/
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
			logger.error("Error occurred during build school report pdf file. "+e.getMessage());
		}
	}
	private String createContentTable(String pdfPath) throws Exception{
		String reportName = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");    		
		long difference = sdf1.parse(schoolMealReportResp.getEndDate()).getTime() - sdf1.parse(schoolMealReportResp.getStartDate()).getTime();
		if(difference/(1000*60*60*24) > 1)
			reportName = "SCHOOL REPORT FOR DATE RANGE: "+sdf.format(sdf1.parse(schoolMealReportResp.getStartDate()))+" - "
					+sdf.format(sdf1.parse(schoolMealReportResp.getEndDate()));
		else
			reportName = "SCHOOL REPORT FOR DATE: "+sdf.format(sdf1.parse(schoolMealReportResp.getStartDate()));
		
		if(difference/(1000*60*60*24) == 6)
			isWeekStatus = true;
		return reportName;
    }
	
		private Element createSchoolDayTable(String gradeVal, List<String> gradeMenus, String mealDate, 
				Entry<String, List<StudentInfoWithMeal>> teacherStudentsEntry, String reportName) throws Exception{
			int size1=0;
			/**Setting the size of table (i.e. count of meal items + 3 default columns + 1 check-box column**/
			size1=gradeMenus.size()+header.size()+1;
			PdfPTable table=new PdfPTable(size1);

			PdfPCell cell = new PdfPCell(new Phrase(reportName, generalDateFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			cell.setColspan(size1);
			cell.setPaddingBottom(10);
			table.addCell(cell);
        	cell = new PdfPCell(new Phrase("DATE: "+mealDate+",     GRADE: "+gradesKeyVal.get(gradeVal)
        		+",     TEACHER NAME: "+teacherStudentsEntry.getKey().toUpperCase(), boldFont));
        	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	cell.setColspan(size1);
	    	table.addCell(cell); 
    	    
        	for(String head2 : header){
        		cell = new PdfPCell(new Phrase(head2, boldFont));
            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cell); 	
        	}
        	int j=header.size();
        	int size2 = 0;
        	
        		//List<String> mealcolsEachGrade = new ArrayList<>();
        		//mealcolsEachGrade = gradeMenus;
        		/**Adding the header in table based on available meal item for the specified meal date and grade**/
    			for (int k = j; k < j + gradeMenus.size(); k++) {
    				cell = new PdfPCell(new Phrase(gradeMenus.get(k - header.size() - size2).toUpperCase(), boldFont));
                	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
                	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                	table.addCell(cell); 	
    			}
    			
    			cell = new PdfPCell(new Phrase("DISTRIBUTED", boldFont));
            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            	table.addCell(cell); 
    		/**This map having key as the meal date and value as the list of meals**/
    		Map<String, List<String>> mealOrderedByDate = null;
    		/**Sort the students who ordered meals at top and remaining bottom of ordered meal students with same first & last name sorting order**/
    		/*LinkedList<StudentInfoWithMeal> studentInfoWithMeals = new LinkedList<StudentInfoWithMeal>();
    		studentInfoWithMeals.addAll(teacherStudentsEntry.getValue().stream()
	    	        .filter(p ->  p.getMealOrderedByDate() != null &&  p.getMealOrderedByDate().get(mealDate) != null)
	    	        .collect(Collectors.toCollection(LinkedList::new)));
    		
    		studentInfoWithMeals.addAll(teacherStudentsEntry.getValue().stream()
	    	        .filter(p ->  (p.getMealOrderedByDate() != null &&  p.getMealOrderedByDate().get(mealDate) == null) 
	    	        		|| p.getMealOrderedByDate() == null).collect(Collectors.toCollection(LinkedList::new)));*/
    		List<StudentInfoWithMeal> studentInfoWithMeals = teacherStudentsEntry.getValue();
    				
    		for (int i = 0; i < studentInfoWithMeals.size(); i++) {
    			/**Setting the meal items in map where key as the meal date and value as the list of meal items**/
    			mealOrderedByDate = studentInfoWithMeals.get(i).getMealOrderedByDate();
    			String sno=String.valueOf(i+1);
    			cell = new PdfPCell(new Phrase(sno, generalFont));
    			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(cell);
    			String studentName = "";
    			if(studentInfoWithMeals.get(i).getStudentLName() != null)
    				studentName = studentInfoWithMeals.get(i).getStudentLName().toUpperCase();
    			if(studentInfoWithMeals.get(i).getStudentFName() != null)
    				studentName = studentName +", "+ studentInfoWithMeals.get(i).getStudentFName().toUpperCase();
    			/*if(studentInfoWithMeals.get(i).getStudentFName() != null)
    				studentName = studentInfoWithMeals.get(i).getStudentFName()+" ";
    			if(studentInfoWithMeals.get(i).getStudentLName() != null)
    				studentName = studentName + studentInfoWithMeals.get(i).getStudentLName();*/
    			
    			cell = new PdfPCell(new Phrase(studentName, generalFont));
    			//cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(cell);
    			if(schoolMealReportResp.getIsAllergyEnabled() != null && schoolMealReportResp.getIsAllergyEnabled()){
    				cell = new PdfPCell(new Phrase(studentInfoWithMeals.get(i).getAllergies() != null ? studentInfoWithMeals.get(i).getAllergies().toUpperCase() : "", generalFont));
        			table.addCell(cell);
    			}    			
    			if (mealOrderedByDate != null) {
    					/**Setting all the meal items in list of String object**/
    					List<String> orderedMeals = mealOrderedByDate.get(mealDate);
    						/**Iterating the gradeMealsEntry value (i.e. list of all meal items) and checking that this item 
    						 * ordered or not. If ordered then setting as YES else empty string**/
    						for(String meal : gradeMenus){
    							if(orderedMeals != null && orderedMeals.contains(meal)){
    								/*cell = new PdfPCell(new Phrase("YES", generalFont));
    				    			cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
    								cell = new PdfPCell();
    								cell.addElement(rightTickImage);
    								table.addCell(cell);
    							}else{
    								table.addCell(new Phrase("", generalFont));
    							}
    						}
    			}
    			else{
    				for (int m = header.size(); m < size1-1; m++) {
    				table.addCell(new Phrase("", generalFont));
    			}
    		}
    			 cell = new PdfPCell();
    	         cell.addElement(checkBoxImage);
    			 table.addCell(cell);
    		}
    		table.setWidthPercentage(100);
			return table;
		}
		
		private Element createSchoolWeeklyReport(String gradeVal, Map<String, List<String>> dateMealMap, 
				Entry<String, List<StudentInfoWithMeal>> teacherStudentsEntry, String reportName) throws Exception{
			int size1 = header.size(); //size for default added columns
			for(Entry<String, List<String>> mealMapEntry : dateMealMap.entrySet()){
				size1 = size1 + mealMapEntry.getValue().size();
			}
			PdfPTable table=new PdfPTable(size1);
			PdfPCell cell = new PdfPCell(new Phrase(reportName, generalDateFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			cell.setColspan(size1);
			cell.setPaddingBottom(10);
			table.addCell(cell);
        	cell = new PdfPCell(new Phrase("GRADE: "+gradesKeyVal.get(gradeVal)
        		+",     TEACHER NAME: "+teacherStudentsEntry.getKey().toUpperCase(), boldFont));
        	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	cell.setColspan(size1);
	    	table.addCell(cell); 
	    	
	    	cell = new PdfPCell(new Phrase("DATE: ", boldFont));
	    	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
	    	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	cell.setColspan(header.size());
	    	table.addCell(cell); 
	    	
	    	for(Entry<String, List<String>> mealMapEntry : dateMealMap.entrySet()){
	    		cell = new PdfPCell(new Phrase(mealMapEntry.getKey(), boldFont));
		    	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		    	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	cell.setColspan(mealMapEntry.getValue().size());
		    	table.addCell(cell); 
			}
    	    
        	for(String head2 : header){
        		cell = new PdfPCell(new Phrase(head2, boldFont));
            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cell); 	
        	}
        		//List<String> mealcolsEachGrade = new ArrayList<>();
        		//mealcolsEachGrade = gradeMenus;
        		/**Adding the header in table based on available meal item for the specified meal date and grade**/
        	for(Entry<String, List<String>> mealMapEntry : dateMealMap.entrySet()){
        		for(String itemName : mealMapEntry.getValue()){
        			cell = new PdfPCell(new Phrase(itemName.toUpperCase(), boldFont));
                	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
                	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                	table.addCell(cell); 	
        		}
			}
    		/**This map having key as the meal date and value as the list of meals**/
    		Map<String, List<String>> mealOrderedByDate = null;
    		List<StudentInfoWithMeal> studentInfoWithMeals = teacherStudentsEntry.getValue();
    				
    		for (int i = 0; i < studentInfoWithMeals.size(); i++) {
    			/**Setting the meal items in map where key as the meal date and value as the list of meal items**/
    			mealOrderedByDate = studentInfoWithMeals.get(i).getMealOrderedByDate();
    			String sno=String.valueOf(i+1);
    			cell = new PdfPCell(new Phrase(sno, generalFont));
    			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(cell);
    			String studentName = "";
    			if(studentInfoWithMeals.get(i).getStudentLName() != null)
    				studentName = studentInfoWithMeals.get(i).getStudentLName().toUpperCase();
    			if(studentInfoWithMeals.get(i).getStudentFName() != null)
    				studentName = studentName +", "+ studentInfoWithMeals.get(i).getStudentFName().toUpperCase();
    			

    			cell = new PdfPCell(new Phrase(studentName, generalFont));
				cell.setPaddingBottom(7);
    			//cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(cell);
    			if(schoolMealReportResp.getIsAllergyEnabled() != null && schoolMealReportResp.getIsAllergyEnabled()){
        			cell = new PdfPCell(new Phrase(studentInfoWithMeals.get(i).getAllergies() != null ? studentInfoWithMeals.get(i).getAllergies().toUpperCase() : "", generalFont));
        			table.addCell(cell);    				
    			}
    			if (mealOrderedByDate != null) {
				for (Entry<String, List<String>> mealMapEntry : dateMealMap.entrySet()) {
					/** Setting all the meal items in list of String object **/
					List<String> orderedMeals = mealOrderedByDate.get(mealMapEntry.getKey());
					for (String meal : mealMapEntry.getValue()) {
						if (orderedMeals != null && orderedMeals.contains(meal)) {
							cell = new PdfPCell();
							cell.addElement(rightTickImage);
							table.addCell(cell);
						} else {
							table.addCell(new Phrase("", generalFont));
						}
					}
				}
    			}
    			else{
    				for (int m = header.size(); m < size1; m++) {
    				table.addCell(new Phrase("", generalFont));
    			}
    		}
    		}
    		table.setWidthPercentage(100);
			return table;
		}
}
