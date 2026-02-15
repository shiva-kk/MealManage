package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.mealManage.domain.FMActualReport;
import com.mealManage.mealmodel.school.SchoolGrades;

/** This utility class used for export the School Reports in pdf file format **/
@Component
public class FMRPActualReport{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font boldFontSchoolName=FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 12);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private List<String> header;
    private Image rightTickImage;
    private Map<String, String> gradesKeyVal;
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
	
	/**This method used for generate the pdf of School report
	 * @throws Exception **/
	public void exportFMEligibilitySurveyPdfReport(List<FMActualReport> fmActualReports, 
			HttpServletResponse response, Long mealSchoolId, int schoolYear, String eligType, String countryCode) throws Exception{
		String pdfFilePath = "LunchEligibilityActualReport_"+mealSchoolId+"_"+schoolYear+".pdf";
		/*String logoPath = "";
		if(logoLink != null)
			logoPath = logoLink;
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";*/
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try	{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			/*document.add(generateSchoolPdfReport(pdfFilePath, logoPath, schoolName, schoolYear));
    		document.newPage();	*/
    		
    		GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
    		gradesKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode); //setting the grade key value (i.e. one means 1, two means 2,..etc)
    				
    		String rightTickPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/rightTick.PNG";
    		rightTickImage = Image.getInstance(rightTickPath);
    		rightTickImage.scaleAbsolute(11f, 11f);
    		rightTickImage.setAlignment(Image.ALIGN_CENTER);
    			
    		/**Setting generally used header in school report table**/
    		if(eligType != null && (eligType.equalsIgnoreCase("Free") || eligType.equalsIgnoreCase("Reduced")))
    			header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME", "STUDENT ID", "TEACHER NAME"));
    		else
    			header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME", "STUDENT ID", "TEACHER NAME","FREE MEAL?","REDUCED PRICE?"));
    	
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
            	    document.add(createSchoolDayTable(gradeVal.toString(), gradeStudent, schoolYear,eligType));
            		document.newPage();	
        		}	
    	    }
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
	    	response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
	    	IOUtils.copy(myStream, response.getOutputStream());
	    	response.flushBuffer();
	    	myStream.close();
	    	new File(pdfFilePath).delete(); 
		}catch (Exception e){
			logger.error("Error occurred during build free meal/reduced price actual report pdf file. "+e.getMessage());
		}
	}
	
	/**This method used for generate the pdf file of School report**/
	/*private Element generateSchoolPdfReport(String pdfPath, String logoPath, String schoolName, int schoolYear) throws Exception{	
			PdfPTable mainTable = new PdfPTable(1);
			PdfPCell cell=new PdfPCell();
			cell.setFixedHeight(750);
			cell.addElement(createContentTable(pdfPath, logoPath, schoolName, schoolYear));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTable.addCell(cell);
			mainTable.setWidthPercentage(100);
			return mainTable;
	}*/
	
	/*private Element createContentTable(String pdfPath, String logoPath, String schoolName, int schoolYear) throws Exception{        	
        	PdfPTable mainTab = new PdfPTable(1);
    		mainTab.setWidthPercentage(100);
    		Image image = Image.getInstance(logoPath);
        	image.scaleAbsolute(60f, 60f);
        	image.setAlignment(Image.ALIGN_CENTER);
    		//for first row
    		PdfPCell first = new PdfPCell();	   
    		first.addElement(image);
    		first.setBorder(0);
    		first.setHorizontalAlignment(Element.ALIGN_CENTER);
    		//first.setPaddingLeft(155);
    		mainTab.addCell(first);
    		
    		first = new PdfPCell(new Phrase(schoolName.toUpperCase(), boldFontSchoolName));
    		first.setBorder(0);		
    		first.setHorizontalAlignment(Element.ALIGN_CENTER);
    		mainTab.addCell(first);
    		first = new PdfPCell(new Phrase("FREE MEAL/REDUCED PRICE ELIGIBILITY ACTUAL REPORT FOR SCHOOL YEAR: "+
    		Integer.toString(schoolYear), generalDateFont));
			first.setBorder(0);	
			first.setHorizontalAlignment(Element.ALIGN_CENTER);
			mainTab.addCell(first);
			return mainTab;
        }*/	
		
		private Element createSchoolDayTable(String gradeVal, List<FMActualReport> studentsInfo, int schoolYear,String eligType) throws Exception{
			PdfPTable table = null; 
			PdfPCell cell = null;
			if(eligType != null && eligType.equalsIgnoreCase("Free")){
				cell = new PdfPCell(new Phrase("FREE MEAL ELIGIBILITY ACTUAL REPORT FOR SCHOOL YEAR "+schoolYear));
				table=new PdfPTable(4); 
			}else if(eligType != null && eligType.equalsIgnoreCase("Reduced")){
				cell = new PdfPCell(new Phrase("REDUCED PRICE MEAL ELIGIBILITY ACTUAL REPORT FOR SCHOOL YEAR "+schoolYear));
				table=new PdfPTable(4); 
			}else{
				cell = new PdfPCell(new Phrase("FREE/REDUCED PRICE MEAL ELIGIBILITY ACTUAL REPORT FOR SCHOOL YEAR "+schoolYear));
				table=new PdfPTable(6); 
			}
			cell.setBorder(0);
			cell.setPaddingBottom(10);
			cell.setColspan(6);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("GRADE: " + gradesKeyVal.get(gradeVal) ,boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(6);
			table.addCell(cell);
			
        	for(String head2 : header){
        		cell = new PdfPCell(new Phrase(head2, boldFont));
            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cell); 	
        	}	
        	
    		for (int i = 0; i < studentsInfo.size(); i++) {
    			String sno=String.valueOf(i+1);
    			cell = new PdfPCell(new Phrase(sno, generalFont));
    			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(cell);
    			String studentName = "";
    			if(studentsInfo.get(i).getStudentLName() != null)
    				studentName = studentsInfo.get(i).getStudentLName().toUpperCase();
    			if(studentsInfo.get(i).getStudentFName() != null)
    				studentName = studentName +", "+ studentsInfo.get(i).getStudentFName().toUpperCase();			
    			cell = new PdfPCell(new Phrase(studentName, generalFont));
    			table.addCell(cell);
    			cell = new PdfPCell(new Phrase(studentsInfo.get(i).getStudentId(), generalFont));
    			table.addCell(cell);
    			cell = new PdfPCell(new Phrase(studentsInfo.get(i).getTeacherName(), generalFont));
    			table.addCell(cell);
    			if(eligType == null || (!eligType.equalsIgnoreCase("Free") && !eligType.equalsIgnoreCase("Reduced"))){
    				if(studentsInfo.get(i).isFreeMeal()){
        				cell = new PdfPCell();
        				cell.addElement(rightTickImage);
        				table.addCell(cell);
        			}else{
        				table.addCell(new Phrase("", generalFont));
        			}
        			if(studentsInfo.get(i).isReducedPrice()){
        				cell = new PdfPCell();
        				cell.addElement(rightTickImage);
        				table.addCell(cell);
        			}else{
        				table.addCell(new Phrase("", generalFont));
        			}
    			}    			
    		}
    		table.setWidthPercentage(100);
			return table;
		}
}
