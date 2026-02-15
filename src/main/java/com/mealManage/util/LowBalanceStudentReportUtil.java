package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.domain.LowBalanceStudents;
import com.mealManage.mealmodel.school.SchoolGrades;

/**This util class used for generate the low balance student's account report in pdf**/
@Component
public class LowBalanceStudentReportUtil {
	
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
    private Map<String, String> gradesKeyVal;
    private DecimalFormat df = new DecimalFormat("0.00");
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
	
	/**This method used for generate the pdf of low balance student's report
	 * @throws Exception **/
	public void pdfReportLowBalance(List<LowBalanceStudents> lowBalanceStudents, String logoLink, String schoolName, 
			HttpServletResponse response, Long mealSchoolId, Double minLowBalance, Double maxLowBalance,Double amount, String operator,String currencySymbol, String countryCode) throws Exception{
		String pdfFilePath = "LowBalanceStudentsReport_"+mealSchoolId+".pdf";
		/*String logoPath = "";
		if(logoLink != null)
			logoPath = logoLink;
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";*/
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			/*document.add(generateSchoolPdfReport(pdfFilePath, logoPath, schoolName, minLowBalance, maxLowBalance));
    		document.newPage();	*/
    		
    		GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode);//setting the grade key value (i.e. one means 1, two means 2,..etc)
			String reportName = "LOW BALANCE STUDENTS REPORT WITH RANGE ("+currencySymbol+"): ";
			if(amount != null && operator != null && !operator.trim().isEmpty()){
				switch (operator.toUpperCase()) {
				case "LE": reportName = reportName+" <= "+df.format(amount); break;
				case "L": reportName = reportName+" < "+df.format(amount); break;
				case "GE": reportName = reportName+" >= "+df.format(amount); break;
				case "G": reportName = reportName+" > "+df.format(amount); break;
				default: reportName = reportName+" = "+df.format(amount); break;
				}
			}else{
				reportName = reportName+df.format(minLowBalance)+" to "+df.format(maxLowBalance); 
			}
			/**Setting generally used header in Transaction report table**/
    		header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME","STUDENT ID#","TEACHER NAME", "BALANCE("+currencySymbol+")"));
    		
    		Map<String, List<LowBalanceStudents>> studentsByGrade = lowBalanceStudents.stream().collect(Collectors.groupingBy(LowBalanceStudents::getGradeName));
    		/**Get all the grade in sorting order**/
			SchoolGrades[] schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(new ArrayList<String>(
					studentsByGrade.keySet())).toArray(new SchoolGrades[0]);
			Arrays.sort(schoolGrades);
			
			List<LowBalanceStudents> lowBalanceStudentsList = null;
			//iterate the grades and proceed one by one
			for (SchoolGrades gradeVal : schoolGrades) {
				lowBalanceStudentsList = studentsByGrade.get(gradeVal.toString());
				document.add(createStudentsReport(gradeVal.toString(), lowBalanceStudentsList, reportName));
				document.newPage();
			}	  
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e){
			logger.error("Error occurred during generate the low balance report due to  "+e.getMessage());
		}
	}
	
	/**This method used for generate the pdf file first page of transaction history report**/
	/*private Element generateSchoolPdfReport(String pdfPath, String logoPath, String schoolName, Double minLowBalance, 
			Double maxLowBalance) throws Exception {
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(750);
		cell.addElement(createContentTable(pdfPath, logoPath, schoolName, minLowBalance, maxLowBalance));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}*/
	
	/**This method used for create the first page pdf content**/
	/*private Element createContentTable(String pdfPath, String logoPath, String schoolName, Double minLowBalance, 
			Double maxLowBalance) throws Exception {
		PdfPTable mainTab = new PdfPTable(1);
		mainTab.setWidthPercentage(100);
		Image image = Image.getInstance(logoPath);
		image.scaleAbsolute(60f, 60f);
		image.setAlignment(Image.ALIGN_CENTER);
		// for first row
		PdfPCell first = new PdfPCell();
		first.addElement(image);
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		// first.setPaddingLeft(155);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(schoolName.toUpperCase(), boldFontSchoolName));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		
		first = new PdfPCell(new Phrase("LOW BALANCE STUDENTS REPORT", generalDateFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		
		first = new PdfPCell(new Phrase("LOW BALANCE CRITERIA RANGE ($): "+df.format(minLowBalance)+" to "+df.format(maxLowBalance), generalDateFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		return mainTab;
	}*/
		
	/**This method used for build the low balance students report table**/
	private Element createStudentsReport(String grade, List<LowBalanceStudents> lowBalanceStudents, String reportName) throws Exception {
		PdfPTable table = new PdfPTable(new float[] { 30, 50, 40, 45, 35});
		PdfPCell cell = new PdfPCell(new Phrase(reportName, generalDateFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(5);
		cell.setPaddingBottom(10);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("GRADE: "+gradesKeyVal.get(grade), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(5);
		table.addCell(cell); 
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		String sno = null;
		LowBalanceStudents lowBalanceStudent = null;
		for (int i = 0; i < lowBalanceStudents.size(); i++) {
			sno = String.valueOf(i + 1);
			cell = new PdfPCell(new Phrase(sno, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			lowBalanceStudent = lowBalanceStudents.get(i);
			cell = new PdfPCell(new Phrase(lowBalanceStudent.getLastName()+", "+lowBalanceStudent.getFirstName(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(lowBalanceStudent.getStudentId(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(lowBalanceStudent.getTeacherName(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(lowBalanceStudent.getAccBalance()), generalFont));
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		return table;
	}


}
