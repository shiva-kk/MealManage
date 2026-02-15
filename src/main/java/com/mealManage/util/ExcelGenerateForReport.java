package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.domain.FMActualReport;
import com.mealManage.domain.StudentsWithAllergies;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.user.FMEligibilitySurvey;
import com.mealManage.response.NotOrderedStudentResp;
import com.mealManage.response.SchoolMealReportResp;
import com.mealManage.response.StudentInfoWithMeal;

/** This utility class used for export the excel file for Reports **/
@Component
public class ExcelGenerateForReport {
	
	@Autowired
	private MealManageAPIDao mealManageAPIDao;

	/** This method used for generate the excel file of Caterer Report **/
	public void exportCatererReport(String schoolName, List<String> grades, Date startDate, HttpServletResponse response, 
			Map<String, Map<String, Integer>> mealsMap, Date endDate, String countryCode)
			throws Exception {
		String fileName = "CatererReport.xls";
		String grade = "";
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("CatererReport");
		sheet.setDefaultColumnWidth(30);

		if (grades == null || grades.size() <1)
			grade = "All";
		else {
			GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
			Set<SchoolGrades> schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(grades);
			String gradeName = gradeFormatBuild.getGradesFromSet(schoolGrades);
			Map<String, String> gradeMap = mealManageAPIDao.gradeMapByCountry(countryCode);
			grade = gradeFormatBuild.buildGradeName(gradeName, gradeMap);
		}

		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.NONE);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);

		CellStyle style2 = buildStyle(workbook);
		style2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		CellStyle style3 = buildStyle(workbook);
		style3.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());

		// create header row
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yy");
		//long difference = endDate.getTime() - startDate.getTime();
		Row head = sheet.createRow(0);
		head.createCell(0).setCellValue("School: " + schoolName);
		head.getCell(0).setCellStyle(style);
		head.createCell(1).setCellValue("          Grade: " + grade);
		head.getCell(1).setCellStyle(style);
		/*if(difference/(1000*60*60*24) > 1)
			head.createCell(2).setCellValue("Date Range: " + sdf1.format(startDate)+"-"+sdf1.format(endDate));
		else
			head.createCell(2).setCellValue("Date: " + sdf1.format(sdf.parse(catererReportResp.getStartDate())));*/
		head.createCell(2).setCellValue("Date Range: " + sdf1.format(startDate)+"-"+sdf1.format(endDate));
		//head.createCell(2).setCellValue(new Date());
		head.getCell(2).setCellStyle(style2);

		Row head1 = sheet.createRow(1);

		head1.createCell(0).setCellValue("S.NO");
		head1.getCell(0).setCellStyle(style2);
		head1.createCell(1).setCellValue("Item Name");
		head1.getCell(1).setCellStyle(style2);
		head1.createCell(2).setCellValue("Total Number");
		head1.getCell(2).setCellStyle(style2);

		int rowCount = 2;
		Row userRow = null;
		int i = 1;
		Map<String, Integer> mealAndCountMap = null;
		for (Entry<String, Map<String, Integer>> entry : mealsMap.entrySet()) {
			userRow = sheet.createRow(rowCount++);
			userRow.createCell(0).setCellValue("Date: "+entry.getKey());
			userRow.getCell(0).setCellStyle(style3);
			userRow.createCell(1).setCellStyle(style3);
			userRow.createCell(2).setCellStyle(style3);
			sheet.addMergedRegion(new CellRangeAddress(rowCount-1, rowCount-1, 0, 2));
			mealAndCountMap = entry.getValue();
			i = 1;
			if(mealAndCountMap != null)
				for(Entry<String, Integer> mealAndCountEntry : mealAndCountMap.entrySet()){
					userRow = sheet.createRow(rowCount++);
					userRow.createCell(0).setCellValue(i++);
					userRow.createCell(1).setCellValue(mealAndCountEntry.getKey());
					userRow.createCell(2).setCellValue(mealAndCountEntry.getValue());
					userRow.getCell(0).setCellStyle(cellStyle);
					userRow.getCell(1).setCellStyle(cellStyle);
					userRow.getCell(2).setCellStyle(cellStyle);
				}
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);

		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();

		InputStream myStream = new FileInputStream(fileName);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType("application/vnd.ms-excel");
		IOUtils.copy(myStream, response.getOutputStream());
		response.flushBuffer();

		myStream.close();
		new File(fileName).delete();
	}

	/**This method used for export the excel file of school Meals**/
	public void exportSchoolMealReport(SchoolMealReportResp schoolMealReportResp, HttpServletResponse response) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		String fileName = "SchoolMealReport.xls";
		@SuppressWarnings("unused")
		Sheet sheet = null;
		Boolean status = false;
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderBottom(BorderStyle.THIN);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);

		CellStyle style2 = buildStyle(workbook);
		style2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style2.setBorderLeft(BorderStyle.THIN);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);

		CellStyle orderedCell = workbook.createCellStyle();
		orderedCell.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
		orderedCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		orderedCell.setAlignment(HorizontalAlignment.CENTER);
		// create excel sheet
		List<StudentInfoWithMeal> allStudentWithMeal = schoolMealReportResp.getStudentWithMeal();
		 /**This map having key as the teacher name and value as the list of student info with other details**/
		Map<String, List<StudentInfoWithMeal>> studentsGroupByTeacher = null;
		
		/**This object reference will store all the student info with other details for each grade**/
		List<StudentInfoWithMeal> gradeStudent = null;
		 
		 /**Sort out the student by first & last name**/
		 /*allStudentWithMeal.sort(Comparator.comparing(StudentInfoWithMeal::getStudentFName)
				.thenComparing(StudentInfoWithMeal::getStudentLName));*/
		 GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
		 Map<String, String> gradeKeyValue = mealManageAPIDao.gradeMapByCountry(schoolMealReportResp.getCountryCode()); 
		 Map<String, List<String>> dateMenusMap = null;
		 if(schoolMealReportResp.getMealsByGradeAndDate() != null){
			 /**Get all the grade in sorting order**/
				SchoolGrades[] schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(new ArrayList<String>(
						schoolMealReportResp.getMealsByGradeAndDate().keySet())).toArray(new SchoolGrades[0]);
				Arrays.sort(schoolGrades);
			 //for(Entry<String, Map<String, List<String>>> gradeDateMealMapEntry : schoolMealReportResp.getMealsByGradeAndDate().entrySet()){
			for(SchoolGrades gradeVal : schoolGrades){
				dateMenusMap = schoolMealReportResp.getMealsByGradeAndDate().get(gradeVal.toString());
				 /**Getting all the student info with other details for specified grade**/
 				gradeStudent = allStudentWithMeal.stream().filter(p ->  p.getGrade() != null && p.getGrade()
 						.equalsIgnoreCase(gradeVal.toString())).collect(Collectors.toCollection(LinkedList::new));
				 /**Grouping the student info of that specified grade by teacher name and storing into map which having key 
					 * as the teacher name and value as the list of POJO (i.e. which contains student info with other details)**/
				studentsGroupByTeacher = gradeStudent.stream().collect(Collectors.groupingBy(StudentInfoWithMeal::getTeacherName));
				if(studentsGroupByTeacher != null && studentsGroupByTeacher.size() > 0){
					/**Iterating the map for each teacher name**/
					for(Entry<String, List<StudentInfoWithMeal>> studentEntry : studentsGroupByTeacher.entrySet()){
						Map<String, List<String>> dateMealsMap = new TreeMap<String, List<String>>(dateMenusMap);
						schoolReportSheetGeneration(workbook, dateMealsMap, studentEntry, gradeKeyValue.get(gradeVal.toString()), style, 
								style2, cellStyle, orderedCell, schoolMealReportResp);
						if(!status)
							status = true;
					}  
				}
			 }
		 }
		if(!status)
			sheet = workbook.createSheet("School Report");
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();

		InputStream myStream = new FileInputStream(fileName);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType("application/vnd.ms-excel");
		IOUtils.copy(myStream, response.getOutputStream());
		response.flushBuffer();

		myStream.close();
		new File(fileName).delete();

	}
	
	/**This method used for export the excel file of Free Meal/Reduced Price actual report**/
	public void exportFreeMealReducedPrice(List<FMActualReport> fmActualReports, HttpServletResponse response, String eligType, String countryCode) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		String fileName = "";
		if(eligType != null && eligType.equalsIgnoreCase("Free"))
			fileName = "FreeLunchEligibilityReport.xls";
		else if(eligType != null && eligType.equalsIgnoreCase("Reduced"))
			fileName = "ReducedLunchEligibilityReport.xls";
		else 
			fileName = "LunchEligibilityReport.xls";
		@SuppressWarnings("unused")
		Sheet sheet = null;
		Boolean status = false;
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderBottom(BorderStyle.THIN);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);

		CellStyle style2 = buildStyle(workbook);
		style2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style2.setBorderLeft(BorderStyle.THIN);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);

		CellStyle orderedCell = workbook.createCellStyle();
		orderedCell.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
		orderedCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		orderedCell.setAlignment(HorizontalAlignment.CENTER);
		
		 GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
		 Map<String, String> gradeKeyValue = mealManageAPIDao.gradeMapByCountry(countryCode); 
		 
		 /**This object reference will store all the student info with other details for each grade**/
 		List<FMActualReport> gradeStudent = null;
 		Map<String, List<FMActualReport>> studentsByGrade = fmActualReports.stream().collect(Collectors.groupingBy(
 				FMActualReport::getGradeName));
 		Set<String> grades = studentsByGrade.keySet();
 	    if(grades != null && grades.size() > 0){
 	    		/**Get all the grade in sorting order**/
 	    	SchoolGrades[] schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(new ArrayList<String>(grades)).toArray(new SchoolGrades[0]);
 			Arrays.sort(schoolGrades);
 			//iterate the grades and proceed one by one
 			for(SchoolGrades gradeVal : schoolGrades){
 				gradeStudent = studentsByGrade.get(gradeVal.toString());
 				freeMealReducedPriceReport(workbook, gradeStudent, gradeKeyValue.get(gradeVal.toString()), style, 
						style2, cellStyle, orderedCell, eligType);
				if(!status)
					status = true;
     		}	
 	    }
		if(!status)
			sheet = workbook.createSheet("Free Meal Reduced Price Report");
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();

		InputStream myStream = new FileInputStream(fileName);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType("application/vnd.ms-excel");
		IOUtils.copy(myStream, response.getOutputStream());
		response.flushBuffer();

		myStream.close();
		new File(fileName).delete();

	}
	
	private void schoolReportSheetGeneration(Workbook workbook, Map<String, List<String>> dateMealsMap,
			Entry<String, List<StudentInfoWithMeal>> teacherStudentsMap, String grade, CellStyle style, 
			CellStyle style2, CellStyle cellStyle, CellStyle orderedCell, SchoolMealReportResp schoolReportResp) throws Exception {
		Sheet sheet = workbook.createSheet(grade+"_"+teacherStudentsMap.getKey());
		sheet.setDefaultColumnWidth(30);	
		int sizeFix = 2;
		if(schoolReportResp.getIsAllergyEnabled() != null && schoolReportResp.getIsAllergyEnabled())
			sizeFix = 3;
		int size = sizeFix;
		for (Entry<String, List<String>> entry : dateMealsMap.entrySet()) {
			size += entry.getValue().size();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yy");
		Row head = sheet.createRow(0);
		long difference = sdf.parse(schoolReportResp.getEndDate()).getTime()
				- sdf.parse(schoolReportResp.getStartDate()).getTime();
		for (int i = 0; i < size; i++) {
			if (i == 0) {
				head.createCell(i).setCellValue("Grade : " + grade);
				head.getCell(i).setCellStyle(style);
			} else if (i == sizeFix) {
				head.createCell(i).setCellValue("Teacher: " + teacherStudentsMap.getKey().toUpperCase());
				head.getCell(i).setCellStyle(style);
			} else if (i == sizeFix+2 && difference / (1000 * 60 * 60 * 24) > 1) {
				head.createCell(i)
						.setCellValue("Date Range: " + sdf1.format(sdf.parse(schoolReportResp.getStartDate())) + "-"
								+ sdf1.format(sdf.parse(schoolReportResp.getEndDate())));
				head.getCell(i).setCellStyle(style);
			} else
				head.createCell(i).setCellStyle(style);

		}

		Row head1 = sheet.createRow(1);
		Row header = sheet.createRow(2);

		head1.createCell(0).setCellValue("Date");
		head1.getCell(0).setCellStyle(style2);
		head1.createCell(1).setCellStyle(style2);
		if(schoolReportResp.getIsAllergyEnabled() != null && schoolReportResp.getIsAllergyEnabled()){
			head1.createCell(2).setCellStyle(style2);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
		}else
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		header.createCell(0).setCellValue("S.No");
		header.getCell(0).setCellStyle(style2);
		header.createCell(1).setCellValue("Name");
		header.getCell(1).setCellStyle(style2);
		if(schoolReportResp.getIsAllergyEnabled() != null && schoolReportResp.getIsAllergyEnabled()){
			header.createCell(2).setCellValue("Allergies");
			header.getCell(2).setCellStyle(style2);
		}		
		int j = sizeFix;
		int size1 = 0;
		List<String> colsByDate = new ArrayList<>();
		for (Entry<String, List<String>> entry : dateMealsMap.entrySet()) {
			colsByDate = entry.getValue();
			head1.createCell(j).setCellValue(entry.getKey());
			head1.getCell(j).setCellStyle(style2);
			for (int k = j; k < j + colsByDate.size(); k++) {
				header.createCell(k).setCellValue(colsByDate.get(k - sizeFix - size1));
				header.getCell(k).setCellStyle(style2);
			}
			if (colsByDate.size() > 1)
				sheet.addMergedRegion(new CellRangeAddress(1, 1, j, j + colsByDate.size() - 1));

			size1 += colsByDate.size();
			j += colsByDate.size();
		}
		Map<String, List<String>> mealOrderedByDate = null;
		/*
		 * InputStream inputStream = new FileInputStream("rightTick.png");
		 * byte[] imageBytes = IOUtils.toByteArray(inputStream); int
		 * pictureureIdx = workbook.addPicture(imageBytes,
		 * Workbook.PICTURE_TYPE_PNG); inputStream.close(); CreationHelper
		 * helper = workbook.getCreationHelper(); Drawing drawing =
		 * sheet.createDrawingPatriarch();
		 */
		List<StudentInfoWithMeal> studentInfoWithMeals = teacherStudentsMap.getValue();
		for (int i = 0; i < studentInfoWithMeals.size(); i++) {
			mealOrderedByDate = studentInfoWithMeals.get(i).getMealOrderedByDate();
			Row userRow = sheet.createRow(i + 3);
			userRow.createCell(0).setCellValue(i + 1); // this used for set the
														// Sr.No in excel
			userRow.getCell(0).setCellStyle(cellStyle);
			String studentName = "";
			if (studentInfoWithMeals.get(i).getStudentLName() != null)
				studentName = studentInfoWithMeals.get(i).getStudentLName().toUpperCase();
			if (studentInfoWithMeals.get(i).getStudentFName() != null)
				studentName = studentName + ", " + studentInfoWithMeals.get(i).getStudentFName().toUpperCase();
			userRow.createCell(1).setCellValue(studentName);
			userRow.getCell(1).setCellStyle(cellStyle);
			if(schoolReportResp.getIsAllergyEnabled() != null && schoolReportResp.getIsAllergyEnabled()){
				userRow.createCell(2).setCellValue(studentInfoWithMeals.get(i).getAllergies() != null ? studentInfoWithMeals.get(i).getAllergies().toUpperCase() : "");
				userRow.getCell(2).setCellStyle(cellStyle);
			}			
			if (mealOrderedByDate != null) {
				int cellNum = sizeFix;
				// map.entrySet().forEach((m) ->{
				for (Entry<String, List<String>> m : dateMealsMap.entrySet()) {
					List<String> orderedMeals = mealOrderedByDate.get(m.getKey());
					for (String meal : m.getValue()) {
						if (orderedMeals != null && orderedMeals.contains(meal)) {
							userRow.createCell(cellNum).setCellValue("Yes");
							userRow.getCell(cellNum).setCellStyle(orderedCell);
							/*
							 * ClientAnchor anchor =
							 * helper.createClientAnchor();
							 * anchor.setCol1(cellNum); anchor.setRow1(i+3);
							 * Picture pict = drawing.createPicture(anchor,
							 * pictureureIdx); pict.resize();
							 */
						} else {
							userRow.createCell(cellNum);// .setCellValue("No");
							userRow.getCell(cellNum).setCellStyle(orderedCell);
						}
						cellNum++;
					}
				}
				;
			} /*
				 * else{ for (int m = 2; m < size; m++) {
				 * userRow.createCell(m).setCellValue("N/A");
				 * userRow.getCell(m).setCellStyle(cellStyle); } }
				 */
			sheet.autoSizeColumn(i + sizeFix);
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		if(schoolReportResp.getIsAllergyEnabled() != null && schoolReportResp.getIsAllergyEnabled())
			sheet.autoSizeColumn(2);
	}
	
	/**This method used for export the Allergies report in excel format**/
	public void exportAllergiesExcelReport(List<StudentsWithAllergies> studentsWithAllergies, HttpServletResponse response, String countryCode) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		String fileName = "AllergiesReport.xls";
		@SuppressWarnings("unused")
		Sheet sheet = null;
		Boolean status = false;
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderBottom(BorderStyle.THIN);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);

		CellStyle style2 = buildStyle(workbook);
		style2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style2.setBorderLeft(BorderStyle.THIN);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		 
		 /**Sort out the student by first & last name**/
		 /*studentsWithAllergies.sort(Comparator.comparing(StudentsWithAllergies::getStdFName)
				.thenComparing(StudentsWithAllergies::getStdLName));*/
		 GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
		 Map<String, String> gradeKeyValue = mealManageAPIDao.gradeMapByCountry(countryCode);
		 /**This object reference store the students for a grade**/
		List<StudentsWithAllergies> gradeStudents = null;
		/** This Map having key as the grade and value as the list of students with other details**/
		Map<String, List<StudentsWithAllergies>> studentsGroupByGrade = null;
		/** This map having key as the teacher name and value as the list of student info with other details **/
		Map<String, List<StudentsWithAllergies>> studentsGroupByTeacher = null;
		String gradeName = "";
		studentsGroupByGrade = studentsWithAllergies.stream().collect(Collectors.groupingBy(StudentsWithAllergies::getGrade));
		if(studentsGroupByGrade != null){
			/**Get all the grade in sorting order**/
			SchoolGrades[] schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(new ArrayList<String>(
					studentsGroupByGrade.keySet())).toArray(new SchoolGrades[0]);
			Arrays.sort(schoolGrades);
			/** Iterating the map for each grade of students **/
			//for(Entry<String, List<StudentsWithAllergies>> gradeStudentsEntry : studentsGroupByGrade.entrySet()){
			for(SchoolGrades gradeVal : schoolGrades){
				gradeStudents = studentsGroupByGrade.get(gradeVal.toString());
				gradeName = gradeVal.toString();
				if(gradeStudents != null && gradeStudents.size() > 0) {
					studentsGroupByTeacher = gradeStudents.stream().collect(Collectors.groupingBy(StudentsWithAllergies::getTeacherName));
				/** Iterating the map for each teacher name **/
				for(Entry<String, List<StudentsWithAllergies>> studentsTeacherEntry : studentsGroupByTeacher.entrySet()){
					allergiesReportSheetGeneration(workbook, studentsTeacherEntry, gradeKeyValue.get(gradeName), style, 
							style2, cellStyle);
					if(!status)
						status = true;
				}
			}
			}
		}
		if(!status)
			sheet = workbook.createSheet("ALLERGIES Report");
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();
		InputStream myStream = new FileInputStream(fileName);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType("application/vnd.ms-excel");
		IOUtils.copy(myStream, response.getOutputStream());
		response.flushBuffer();
		myStream.close();
		new File(fileName).delete();
	}
	
	/**This method used for export the Free/Reduced Lunch Program Eligibility survey report in excel format**/
	public void exportFMEligibilityExcelReport(List<FMEligibilitySurvey> fmEligibilitySurveys, HttpServletResponse response,String currencySymbol) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		String fileName = "Free_Reduced_Lunch_SurveyReport_.xls";
		@SuppressWarnings("unused")
		Sheet sheet = null;
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderBottom(BorderStyle.THIN);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);

		CellStyle style2 = buildStyle(workbook);
		style2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style2.setBorderLeft(BorderStyle.THIN);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		 
		 /**Sort out the records by parent email**/
		 fmEligibilitySurveys.sort(Comparator.comparing(FMEligibilitySurvey::getParentEmail));
		fmEligibilityReportSheetGeneration(workbook, fmEligibilitySurveys, style, style2, cellStyle,currencySymbol);
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();
		InputStream myStream = new FileInputStream(fileName);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType("application/vnd.ms-excel");
		IOUtils.copy(myStream, response.getOutputStream());
		response.flushBuffer();
		myStream.close();
		new File(fileName).delete();
	}

	/**This method used for export the Not ordered students report in excel format**/
	public void exportNotOrderedStudentsExcelReport(List<NotOrderedStudentResp> notOrderedStudentResps, 
			HttpServletResponse response, String countryCode) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		String fileName = "NotOrderedStudentsReport.xls";
		@SuppressWarnings("unused")
		Sheet sheet = null;
		Boolean status = false;
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderBottom(BorderStyle.THIN);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);

		CellStyle style2 = buildStyle(workbook);
		style2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style2.setBorderLeft(BorderStyle.THIN);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		 
		 /**Sort out the student by first & last name**/
		 /*studentsWithAllergies.sort(Comparator.comparing(StudentsWithAllergies::getStdFName)
				.thenComparing(StudentsWithAllergies::getStdLName));*/
		 GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
		 Map<String, String> gradeKeyValue = mealManageAPIDao.gradeMapByCountry(countryCode);
		 /**This object reference store the students for a grade**/
		List<NotOrderedStudentResp> gradeStudents = null;
		/** This Map having key as the grade and value as the list of students with other details**/
		Map<String, List<NotOrderedStudentResp>> studentsGroupByGrade = null;
		/** This map having key as the teacher name and value as the list of student info with other details **/
		Map<String, List<NotOrderedStudentResp>> studentsGroupByTeacher = null;
		String gradeName = "";
		studentsGroupByGrade = notOrderedStudentResps.stream().collect(Collectors.groupingBy(
				NotOrderedStudentResp::getGradeName));
		if(studentsGroupByGrade != null){
			/**Get all the grade in sorting order**/
			SchoolGrades[] schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(new ArrayList<String>(
					studentsGroupByGrade.keySet())).toArray(new SchoolGrades[0]);
			Arrays.sort(schoolGrades);
			/** Iterating the map for each grade of students **/
			//for(Entry<String, List<StudentsWithAllergies>> gradeStudentsEntry : studentsGroupByGrade.entrySet()){
			for(SchoolGrades gradeVal : schoolGrades){
				gradeStudents = studentsGroupByGrade.get(gradeVal.toString());
				gradeName = gradeVal.toString();
				if(gradeStudents != null && gradeStudents.size() > 0) {
					studentsGroupByTeacher = gradeStudents.stream().collect(Collectors.groupingBy(NotOrderedStudentResp::getTeacherName));
				/** Iterating the map for each teacher name **/
				for(Entry<String, List<NotOrderedStudentResp>> studentsTeacherEntry : studentsGroupByTeacher.entrySet()){
					notOrderedReportSheetGeneration(workbook, studentsTeacherEntry, gradeKeyValue.get(gradeName), style, 
							style2, cellStyle);
					if(!status)
						status = true;
				}
			}
			}
		}
		if(!status)
			sheet = workbook.createSheet("ALLERGIES Report");
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();
		InputStream myStream = new FileInputStream(fileName);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType("application/vnd.ms-excel");
		IOUtils.copy(myStream, response.getOutputStream());
		response.flushBuffer();
		myStream.close();
		new File(fileName).delete();
	}
	
	private void allergiesReportSheetGeneration(Workbook workbook, Entry<String, List<StudentsWithAllergies>> studentsTeacherEntry,
			String grade, CellStyle style, CellStyle style2, CellStyle cellStyle) throws Exception {
		Sheet sheet = workbook.createSheet(grade+"_"+studentsTeacherEntry.getKey().toUpperCase());
		sheet.setDefaultColumnWidth(30);
		int size = 3;
		Row head = sheet.createRow(0);
		for (int i = 0; i < size; i++) {
			if (i == 0) {
				head.createCell(i).setCellValue("GRADE: " + grade);
				head.getCell(i).setCellStyle(style);
			} else if (i == 1) {
				head.createCell(i).setCellValue("TEACHER: " + studentsTeacherEntry.getKey().toUpperCase());
				head.getCell(i).setCellStyle(style);
			}else
				head.createCell(i).setCellStyle(style);
		}
		head = sheet.createRow(1);
		head.createCell(0).setCellValue("S.NO");
		head.getCell(0).setCellStyle(style2);
		head.createCell(1).setCellValue("STUDENT NAME");
		head.getCell(1).setCellStyle(style2);
		head.createCell(2).setCellValue("ALLERGIES");
		head.getCell(2).setCellStyle(style2);
		List<StudentsWithAllergies> studentsWithAllergies = studentsTeacherEntry.getValue();
		for (int i = 0; i < studentsWithAllergies.size(); i++) {
			Row userRow = sheet.createRow(i + 2);
			userRow.createCell(0).setCellValue(i + 1); // this used for set the Sr.No in excel
			userRow.getCell(0).setCellStyle(cellStyle);
			String studentName = "";
			if (studentsWithAllergies.get(i).getStdLName() != null)
				studentName = studentsWithAllergies.get(i).getStdLName().toUpperCase();
			if (studentsWithAllergies.get(i).getStdFName() != null)
				studentName = studentName  + ", "+ studentsWithAllergies.get(i).getStdFName().toUpperCase();
			userRow.createCell(1).setCellValue(studentName);
			userRow.getCell(1).setCellStyle(cellStyle);
			userRow.createCell(2).setCellValue(studentsWithAllergies.get(i).getAllergies().toUpperCase());
			userRow.getCell(2).setCellStyle(cellStyle);
			sheet.autoSizeColumn(i + 2);
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
	}
	
	private void fmEligibilityReportSheetGeneration(Workbook workbook, List<FMEligibilitySurvey> fmEligibilitySurveys, CellStyle style, 
			CellStyle style2, CellStyle cellStyle,String currencySymbol) throws Exception {
		Sheet sheet = workbook.createSheet("Eligibility Survey Report");
		sheet.setDefaultColumnWidth(30);
		Row head = sheet.createRow(0);
		head.createCell(0).setCellValue("S.NO");
		head.getCell(0).setCellStyle(style2);
		head.createCell(1).setCellValue("PARENT EMAIL");
		head.getCell(1).setCellStyle(style2);
		head.createCell(2).setCellValue("HOUSE HOLD SIZE");
		head.getCell(2).setCellStyle(style2);
		head.createCell(3).setCellValue("INCOME ("+currencySymbol+")");
		head.getCell(3).setCellStyle(style2);
		head.createCell(4).setCellValue("INCOME MULTIPLE FOR ANNUALLY");
		head.getCell(4).setCellStyle(style2);
		head.createCell(5).setCellValue("FREE LUNCH ELIGIBLE");
		head.getCell(5).setCellStyle(style2);
		head.createCell(6).setCellValue("REDUCED PRICE ELIGIBLE");
		head.getCell(6).setCellStyle(style2);
		int snNo = 1;
		for (FMEligibilitySurvey fmEligibilitySurvey : fmEligibilitySurveys) {
			Row userRow = sheet.createRow(snNo);
			userRow.createCell(0).setCellValue(snNo); // this used for set the Sr.No in excel
			userRow.getCell(0).setCellStyle(cellStyle);
			userRow.createCell(1).setCellValue(fmEligibilitySurvey.getParentEmail().toUpperCase());
			userRow.getCell(1).setCellStyle(cellStyle);
			userRow.createCell(2).setCellValue(fmEligibilitySurvey.getHouseholdSize());
			userRow.getCell(2).setCellStyle(cellStyle);
			userRow.createCell(3).setCellValue(fmEligibilitySurvey.getIncome());
			userRow.getCell(3).setCellStyle(cellStyle);
			userRow.createCell(4).setCellValue(fmEligibilitySurvey.getIncomeType());
			userRow.getCell(4).setCellStyle(cellStyle);
			if(fmEligibilitySurvey.getIsFreeLunchEligible() != null && fmEligibilitySurvey.getIsFreeLunchEligible())
				userRow.createCell(5).setCellValue("YES");
			else
				userRow.createCell(5).setCellValue("");
			userRow.getCell(5).setCellStyle(cellStyle);
			if(fmEligibilitySurvey.getIsReducedPriceEligible() != null && fmEligibilitySurvey.getIsReducedPriceEligible())
				userRow.createCell(6).setCellValue("YES");
			else
				userRow.createCell(6).setCellValue("");
			userRow.getCell(6).setCellStyle(cellStyle);
			snNo++;
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);
	}
	
	private void notOrderedReportSheetGeneration(Workbook workbook, Entry<String, List<NotOrderedStudentResp>> studentsTeacherEntry,
			String grade, CellStyle style, CellStyle style2, CellStyle cellStyle) throws Exception {
		Sheet sheet = workbook.createSheet(grade+"_"+studentsTeacherEntry.getKey().toUpperCase());
		sheet.setDefaultColumnWidth(30);
		int size = 3;
		Row head = sheet.createRow(0);
		for (int i = 0; i < size; i++) {
			if (i == 0) {
				head.createCell(i).setCellValue("GRADE: " + grade);
				head.getCell(i).setCellStyle(style);
			} else if (i == 1) {
				head.createCell(i).setCellValue("TEACHER: " + studentsTeacherEntry.getKey().toUpperCase());
				head.getCell(i).setCellStyle(style);
			}else
				head.createCell(i).setCellStyle(style);
		}
		head = sheet.createRow(1);
		head.createCell(0).setCellValue("S.NO");
		head.getCell(0).setCellStyle(style2);
		head.createCell(1).setCellValue("STUDENT NAME");
		head.getCell(1).setCellStyle(style2);
		head.createCell(2).setCellValue("STUDENT ID");
		head.getCell(2).setCellStyle(style2);
		List<NotOrderedStudentResp> notOrderedStudentResps = studentsTeacherEntry.getValue();
		for (int i = 0; i < notOrderedStudentResps.size(); i++) {
			Row userRow = sheet.createRow(i + 2);
			userRow.createCell(0).setCellValue(i + 1); // this used for set the Sr.No in excel
			userRow.getCell(0).setCellStyle(cellStyle);
			String studentName = "";
			if (notOrderedStudentResps.get(i).getStudentLName() != null)
				studentName = notOrderedStudentResps.get(i).getStudentLName().toUpperCase();
			if (notOrderedStudentResps.get(i).getStudentFName() != null)
				studentName = studentName  + ", "+ notOrderedStudentResps.get(i).getStudentFName().toUpperCase();
			userRow.createCell(1).setCellValue(studentName);
			userRow.getCell(1).setCellStyle(cellStyle);
			userRow.createCell(2).setCellValue(notOrderedStudentResps.get(i).getStudentId().toUpperCase());
			userRow.getCell(2).setCellStyle(cellStyle);
			sheet.autoSizeColumn(i + 2);
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
	}
	
	private CellStyle buildStyle(Workbook workbook){
		CreationHelper createHelper = workbook.getCreationHelper();
		CellStyle style2 = workbook.createCellStyle();
		//style2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
		Font font2 = workbook.createFont();
		font2.setColor(IndexedColors.BLACK.getIndex());
		font2.setBold(true);
		style2.setFont(font2);
		return style2;
	}
	
	/**This method used for generate the free meal/reduced price actual report in excel**/
	private void freeMealReducedPriceReport(Workbook workbook, List<FMActualReport> fmActualReports, String grade, CellStyle style, 
			CellStyle style2, CellStyle cellStyle, CellStyle orderedCell, String eligType) throws Exception {
		Sheet sheet = workbook.createSheet("Grade_"+grade);
		sheet.setDefaultColumnWidth(30);		
		/*Row head = sheet.createRow(0);
		for (int i = 0; i < 6; i++) {
			if (i == 3) {
				head.createCell(i).setCellValue("Grade : " + grade);
				head.getCell(i).setCellStyle(style);
			} else
				head.createCell(i).setCellStyle(style);
		}*/
		Row header = sheet.createRow(0);
		//sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
		header.createCell(0).setCellValue("S.No");
		header.getCell(0).setCellStyle(style2);
		header.createCell(1).setCellValue("STUDENT NAME");
		header.getCell(1).setCellStyle(style2);
		header.createCell(2).setCellValue("STUDENT ID");
		header.getCell(2).setCellStyle(style2);
		if(eligType == null || (!eligType.equalsIgnoreCase("Free") && !eligType.equalsIgnoreCase("Reduced") )){
			header.createCell(3).setCellValue("FREE MEAL?");
			header.getCell(3).setCellStyle(style2);
			header.createCell(4).setCellValue("REDUCED PRICE?");
			header.getCell(4).setCellStyle(style2);
		}		
		
		for (int i = 0; i < fmActualReports.size(); i++) {
			Row userRow = sheet.createRow(i + 1);
			userRow.createCell(0).setCellValue(i + 1); // this used for set the
														// Sr.No in excel
			userRow.getCell(0).setCellStyle(cellStyle);
			String studentName = "";
			if (fmActualReports.get(i).getStudentLName() != null)
				studentName = fmActualReports.get(i).getStudentLName().toUpperCase();
			if (fmActualReports.get(i).getStudentFName() != null)
				studentName = studentName + ", " + fmActualReports.get(i).getStudentFName().toUpperCase();
			userRow.createCell(1).setCellValue(studentName);
			userRow.getCell(1).setCellStyle(cellStyle);
			userRow.createCell(2).setCellValue(fmActualReports.get(i).getStudentId());
			userRow.getCell(2).setCellStyle(cellStyle);
			if(eligType == null || (!eligType.equalsIgnoreCase("Free") && !eligType.equalsIgnoreCase("Reduced") )){
				if (fmActualReports.get(i).isFreeMeal()) {
					userRow.createCell(3).setCellValue("Yes");
					userRow.getCell(3).setCellStyle(orderedCell);
				} else {
					userRow.createCell(3);// .setCellValue("No");
					userRow.getCell(3).setCellStyle(orderedCell);
				}
				if (fmActualReports.get(i).isReducedPrice()) {
					userRow.createCell(4).setCellValue("Yes");
					userRow.getCell(4).setCellStyle(orderedCell);
				} else {
					userRow.createCell(4);// .setCellValue("No");
					userRow.getCell(4).setCellStyle(orderedCell);
				}
			}			
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
	}
}
