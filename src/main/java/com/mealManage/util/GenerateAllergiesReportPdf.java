package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.mealManage.domain.StudentsWithAllergies;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.user.FMEligibilitySurvey;
import com.mealManage.response.NotOrderedStudentResp;

@Component
/** This class used for generate the Allergies report in Pdf format **/
public class GenerateAllergiesReportPdf {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${amazon.s3.bucket}")
	private String amazonS3Bucketname;
	@Autowired
	private MealManageAPIDao mealManageAPIDao;

	public static final Font boldFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final Font boldFontSchoolName = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
	public static final Font generalFont = FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final Font generalDateFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
	/** The language code for the calendar */
	public static final String LANGUAGE = "en";
	private List<String> header;
    private Map<String, String> gradesKeyVal;
    private Image rightTickImage;
    private DecimalFormat df = new DecimalFormat("0.00");

	/** This method used for generate the Allergies report in Pdf format **/
	public void exportAllergiesPdfReport(List<StudentsWithAllergies> studentsWithAllergies, 
			HttpServletResponse response, Long mealSchoolId, int schoolYear, String countryCode) throws Exception {
		String pdfFilePath = "AllergiesReport_" + mealSchoolId + ".pdf";
		/*String logoPath = "";
		if (logoLink != null)
			logoPath = logoLink;
		else
			logoPath = "https://s3.amazonaws.com/" + amazonS3Bucketname + "/mealManageLogo.PNG";*/

		Document document = new Document(PageSize.A4);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); // This event used for add the page number at the bottom of each page..
			document.open();
			/*document.add(generateAllergiesPdfReport(pdfFilePath, logoPath, schoolName, false, false));
			document.newPage();*/
			
			GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode);// setting the grade key value (i.e. one means 1, two means 2,..etc)

			/** Setting generally used header in school report table **/
			header = new ArrayList<String>(Arrays.asList("S.NO.", "STUDENT NAME", "ALLERGIES"));

			/** Sort out the student by first & last name **/
			/*studentsWithAllergies.sort(Comparator.comparing(StudentsWithAllergies::getStdFName)
					.thenComparing(StudentsWithAllergies::getStdLName));*/

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
						document.add(createAllergiesTable(studentsTeacherEntry, gradeName, schoolYear));
						document.newPage();
					}
				}
				}	
			}			
			document.close();
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e) {
			logger.error("Failed to export the Allergies report in Pdf due to " + e.getMessage());
		}
	}

	/** This method used for generate the Pdf file of Allergies report **/
	/*private Element generateAllergiesPdfReport(String pdfPath, String logoPath, String schoolName, 
			Boolean isFmSurveyReport, Boolean isNotOrderedStudents) throws Exception {
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(750);
		cell.addElement(createContentTable(pdfPath, logoPath, schoolName, isFmSurveyReport, isNotOrderedStudents));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}

	private Element createContentTable(String pdfPath, String logoPath, String schoolName, 
			Boolean isFmSurveyReport, Boolean isNotOrderedStudents) throws Exception {
		PdfPTable mainTab = new PdfPTable(1);
		mainTab.setWidthPercentage(100);
		Image image = Image.getInstance(logoPath);
		image.scaleAbsolute(60f, 60f);
		image.setAlignment(Image.ALIGN_CENTER);
		PdfPCell first = new PdfPCell();
		first.addElement(image);
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(schoolName.toUpperCase(), boldFontSchoolName));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		if(isFmSurveyReport && !isNotOrderedStudents)
			first = new PdfPCell(new Phrase("FREE/REDUCED LUNCH ELIGIBILITY SURVEY REPORT ", generalDateFont));
		else if (!isNotOrderedStudents)
			first = new PdfPCell(new Phrase("STUDENTS ALLERGIES REPORT ", generalDateFont));
		else
			first = new PdfPCell(new Phrase("NOT ORDERED LUNCH STUDENTS REPORT ", generalDateFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		return mainTab;
	}*/

	private Element createAllergiesTable(Entry<String, List<StudentsWithAllergies>> teacherStudentsEntry, String gradeName, int schoolYear) throws Exception {
		PdfPTable table = new PdfPTable(3);
		PdfPCell cell = new PdfPCell(new Phrase("STUDENTS ALLERGIES REPORT FOR SCHOOL YEAR "+schoolYear, generalDateFont));
		cell.setBorder(0);
		cell.setPaddingBottom(10);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(
				new Phrase("GRADE: " + gradesKeyVal.get(gradeName)+ ",     TEACHER NAME: " + teacherStudentsEntry.getKey().toUpperCase(), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(3);
		table.addCell(cell);
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		List<StudentsWithAllergies> studentsWithAllergies = teacherStudentsEntry.getValue();
		String sno = null;
		for (int i = 0; i < studentsWithAllergies.size(); i++) {
			sno = String.valueOf(i + 1);
			cell = new PdfPCell(new Phrase(sno, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			String studentName = "";
			if (studentsWithAllergies.get(i).getStdLName() != null)
				studentName = studentsWithAllergies.get(i).getStdLName().toUpperCase();
			if (studentsWithAllergies.get(i).getStdFName() != null)
				studentName = studentName +", "+ studentsWithAllergies.get(i).getStdFName().toUpperCase();
			

			cell = new PdfPCell(new Phrase(studentName, generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(studentsWithAllergies.get(i).getAllergies().toUpperCase(), generalFont));
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		return table;
	}
	
	/** This method used for generate the Free/Reduced Lunch Program Eligibility survey report in Pdf format **/
	public void exportFMEligibilitySurveyPdfReport(List<FMEligibilitySurvey> fmEligibilitySurveys, HttpServletResponse response, Long mealSchoolId,String currencySymbol, String countryCode) throws Exception {
		String pdfFilePath = "Free_Reduced_Lunch_SurveyReport_" + mealSchoolId + ".pdf";
		/*String logoPath = "";
		if (logoLink != null)
			logoPath = logoLink;
		else
			logoPath = "https://s3.amazonaws.com/" + amazonS3Bucketname + "/mealManageLogo.PNG";*/

		Document document = new Document(PageSize.A4);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); // This event used for add the page number at the bottom of each page..
			document.open();
			/*document.add(generateAllergiesPdfReport(pdfFilePath, logoPath, schoolName, true, false));
			document.newPage();*/
			

			/** Setting Header used in Free/Reduced Lunch Program Eligibility survey **/
			List<String> surveyReportheader = new ArrayList<String>(Arrays.asList("S.NO.", "PARENT EMAIL", "HOUSE HOLD SIZE", "INCOME ("+currencySymbol+")", 
					"INCOME MULTIPLE FOR ANNUALLY", "FREE LUNCH ELIGIBLE", "REDUCED PRICE ELIGIBLE"));

			/** Sort out the student by parent email**/
			fmEligibilitySurveys.sort(Comparator.comparing(FMEligibilitySurvey::getParentEmail));
			document.add(createSurveyReportTable(fmEligibilitySurveys, surveyReportheader));
			document.close();
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e) {
			logger.error("Free/Reduced Lunch Program Eligibility survey report in Pdf due to " + e.getMessage());
		}
	}
	
	private Element createSurveyReportTable(List<FMEligibilitySurvey> fmEligibilitySurveys, List<String> surveyReportheader) throws Exception {
		PdfPTable table = new PdfPTable(7);
		PdfPCell cell = null;
		String rightTickPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/rightTick.PNG";
		rightTickImage = Image.getInstance(rightTickPath);
		rightTickImage.scaleAbsolute(11f, 11f);
		rightTickImage.setAlignment(Image.ALIGN_CENTER);
		cell = new PdfPCell(new Phrase("FREE/REDUCED LUNCH ELIGIBILITY SURVEY REPORT ", generalDateFont));
		cell.setBorder(0);
		cell.setColspan(7);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPaddingBottom(20);
		table.addCell(cell);
		for (String head2 : surveyReportheader) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		int snNo = 1;
		for (FMEligibilitySurvey fmEligibilitySurvey : fmEligibilitySurveys) {
			cell = new PdfPCell(new Phrase(String.valueOf(snNo), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(fmEligibilitySurvey.getParentEmail().toUpperCase(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(fmEligibilitySurvey.getHouseholdSize()), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(fmEligibilitySurvey.getIncome()), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(fmEligibilitySurvey.getIncomeType()), generalFont));
			table.addCell(cell);
			if(fmEligibilitySurvey.getIsFreeLunchEligible() != null && fmEligibilitySurvey.getIsFreeLunchEligible()){
				cell = new PdfPCell();
				cell.addElement(rightTickImage);
				table.addCell(cell);
			}else{
				table.addCell(new Phrase("", generalFont));
			}
			if(fmEligibilitySurvey.getIsReducedPriceEligible() != null && fmEligibilitySurvey.getIsReducedPriceEligible()){
				cell = new PdfPCell();
				cell.addElement(rightTickImage);
				table.addCell(cell);
			}else{
				table.addCell(new Phrase("", generalFont));
			}
			snNo++;
		}
		table.setWidthPercentage(100);
		return table;
	}
	
	/** This method used for generate the report in Pdf format for those students who haven't order lunch in current month 
	 * but having orders in previous month**/
	public void exportNotOrderedStudentsPdfReport(List<NotOrderedStudentResp> notOrderedStudentRespList, HttpServletResponse response, Long mealSchoolId, String yearMonth, String countryCode) throws Exception {
		String pdfFilePath = "NotOrderedStudentsReport_" + mealSchoolId + ".pdf";
		/*String logoPath = "";
		if (logoLink != null)
			logoPath = logoLink;
		else
			logoPath = "https://s3.amazonaws.com/" + amazonS3Bucketname + "/mealManageLogo.PNG";*/

		Document document = new Document(PageSize.A4);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); // This event used for add the page number at the bottom of each page..
			document.open();
			/*document.add(generateAllergiesPdfReport(pdfFilePath, logoPath, schoolName, false, true));
			document.newPage();*/
			
			GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode); // setting the grade key value (i.e. one means 1, two means 2,..etc)

			/** Setting generally used header in school report table **/
			header = new ArrayList<String>(Arrays.asList("S.NO.", "STUDENT NAME", "STUDENT ID"));

			/**This object reference store the students for a grade**/
			List<NotOrderedStudentResp> gradeStudents = null;
			/** This Map having key as the grade and value as the list of students with other details**/
			Map<String, List<NotOrderedStudentResp>> studentsGroupByGrade = null;
			/** This map having key as the teacher name and value as the list of student info with other details **/
			Map<String, List<NotOrderedStudentResp>> studentsGroupByTeacher = null;
			String gradeName = "";
			studentsGroupByGrade = notOrderedStudentRespList.stream().collect(Collectors.groupingBy(
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
						studentsGroupByTeacher = gradeStudents.stream().collect(Collectors.groupingBy(
								NotOrderedStudentResp::getTeacherName));
					/** Iterating the map for each teacher name **/
					for(Entry<String, List<NotOrderedStudentResp>> studentsTeacherEntry : studentsGroupByTeacher.entrySet()){
						document.add(createNotOrderedStudentsTable(studentsTeacherEntry, gradeName, yearMonth));
						document.newPage();
					}
				}
				}	
			}			
			document.close();
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e) {
			logger.error("Failed to export the Not Ordered Students report in Pdf due to " + e.getMessage());
		}
	}

	private Element createNotOrderedStudentsTable(Entry<String, List<NotOrderedStudentResp>> teacherStudentsEntry, String gradeName, String yearMonth) throws Exception {
		PdfPTable table = new PdfPTable(3);
		PdfPCell cell = new PdfPCell(new Phrase("NOT ORDERED LUNCH STUDENTS REPORT FOR MONTH "+
				(new SimpleDateFormat("MMMM yyyy").format(new SimpleDateFormat("yyyyMM").parse(yearMonth))).toUpperCase(),generalDateFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingBottom(10);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(
				new Phrase("GRADE: " + gradesKeyVal.get(gradeName)+ ",     TEACHER NAME: " + teacherStudentsEntry.getKey().toUpperCase(), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(3);
		table.addCell(cell);
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		List<NotOrderedStudentResp> notOrderedStudents = teacherStudentsEntry.getValue();
		String sno = null;
		for (int i = 0; i < notOrderedStudents.size(); i++) {
			sno = String.valueOf(i + 1);
			cell = new PdfPCell(new Phrase(sno, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			String studentName = "";
			if (notOrderedStudents.get(i).getStudentLName() != null)
				studentName = notOrderedStudents.get(i).getStudentLName().toUpperCase();
			if (notOrderedStudents.get(i).getStudentFName() != null)
				studentName = studentName +", "+ notOrderedStudents.get(i).getStudentFName().toUpperCase();
			

			cell = new PdfPCell(new Phrase(studentName, generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(notOrderedStudents.get(i).getStudentId().toUpperCase(), generalFont));
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		return table;
	}
}
