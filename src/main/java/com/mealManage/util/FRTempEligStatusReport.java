package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

/** This utility class used for export the School Reports in pdf file format **/
@Component
public class FRTempEligStatusReport{
	
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
			HttpServletResponse response, Long mealSchoolId, int schoolYear, String eligType, String countryCode,Boolean isDistId, String loggedUser,String currDate,String name) throws Exception{
		String pdfFilePath = "TempEligibilityStatusReport_"+mealSchoolId+"_"+schoolYear+".pdf";
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try	{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
    		gradesKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode); //setting the grade key value (i.e. one means 1, two means 2,..etc)
    				
    		String rightTickPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/rightTick.PNG";
    		rightTickImage = Image.getInstance(rightTickPath);
    		rightTickImage.scaleAbsolute(11f, 11f);
    		rightTickImage.setAlignment(Image.ALIGN_CENTER);
    			
    		/**Setting generally used header in school report table**/
    		if(isDistId != null && isDistId){
    			if(eligType != null && (eligType.equalsIgnoreCase("Free") || eligType.equalsIgnoreCase("Reduced")))
        			header = new ArrayList<String>(Arrays.asList("S.NO.","SCHOOL NAME","STUDENT NAME", "STUDENT ID", "GRADE"));
        		else
        			header = new ArrayList<String>(Arrays.asList("S.NO.","SCHOOL NAME","STUDENT NAME", "STUDENT ID", "GRADE","FREE MEAL?","REDUCED PRICE?"));
    		}else{
    			if(eligType != null && (eligType.equalsIgnoreCase("Free") || eligType.equalsIgnoreCase("Reduced")))
        			header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME", "STUDENT ID", "GRADE"));
        		else
        			header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME", "STUDENT ID", "GRADE","FREE MEAL?","REDUCED PRICE?"));
    		}    		
    	
    		/**This object reference will store all the student info with other details for each grade**/
    		String reportName = "";
    		if(eligType != null && eligType.equalsIgnoreCase("Free"))
    			reportName="FREE TEMP STATUS REPORT";
    		else if(eligType != null && eligType.equalsIgnoreCase("Reduced"))
    			reportName="REDUCED TEMP STATUS REPORT";
    		else
    			reportName="TEMP STATUS REPORT";
    		document.add(CommonUtil.generateHeader(pdfFilePath, name, loggedUser, currDate, amazonS3Bucketname, reportName,""));
    		document.add(createEligTable(fmActualReports,eligType,schoolYear, isDistId));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
	    	response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
	    	IOUtils.copy(myStream, response.getOutputStream());
	    	response.flushBuffer();
	    	myStream.close();
	    	new File(pdfFilePath).delete(); 
		}catch (Exception e){
			logger.error("Error occurred during build temp free meal/reduced price report pdf file. "+e.getMessage());
		}
	}
		
	private Element createEligTable(List<FMActualReport> studentsInfo,String eligType, Integer schoolYear, Boolean isDistId) throws Exception{
			PdfPTable table = null;
			PdfPCell cell = null;
			if(isDistId != null && isDistId){
				if(eligType != null && (eligType.equalsIgnoreCase("Free") || eligType.equalsIgnoreCase("Reduced")))
					table = new PdfPTable(5);  
				else
					table = new PdfPTable(7);  
			}else{
				if(eligType != null && (eligType.equalsIgnoreCase("Free") || eligType.equalsIgnoreCase("Reduced")))
					table = new PdfPTable(4); 
				else
					table = new PdfPTable(6);
			}
			
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
    			if(isDistId != null && isDistId){
    				cell = new PdfPCell(new Phrase(studentsInfo.get(i).getSchoolName(), generalFont));
        			table.addCell(cell);
    			}
    			String studentName = "";
    			if(studentsInfo.get(i).getStudentLName() != null)
    				studentName = studentsInfo.get(i).getStudentLName().toUpperCase();
    			if(studentsInfo.get(i).getStudentFName() != null)
    				studentName = studentName +", "+ studentsInfo.get(i).getStudentFName().toUpperCase();			
    			cell = new PdfPCell(new Phrase(studentName, generalFont));
    			table.addCell(cell);
    			cell = new PdfPCell(new Phrase(studentsInfo.get(i).getStudentId(), generalFont));
    			table.addCell(cell);
    			cell = new PdfPCell(new Phrase(gradesKeyVal.get(studentsInfo.get(i).getGradeName()), generalFont));
    			table.addCell(cell);
    			if(eligType == null || (!eligType.equalsIgnoreCase("Free") && !eligType.equalsIgnoreCase("Reduced"))){
	    			if(studentsInfo.get(i).isFreeMeal()){
	        			cell = new PdfPCell();
	        			cell.addElement(rightTickImage);
	        			table.addCell(cell);
	        		}else
	        			table.addCell(new Phrase("", generalFont));
	        		if(studentsInfo.get(i).isReducedPrice()){
	        			cell = new PdfPCell();
	        			cell.addElement(rightTickImage);
	        			table.addCell(cell);
	        		}else
	        			table.addCell(new Phrase("", generalFont));  
    			}
    		}
    		table.setWidthPercentage(100);
			return table;
		}
}
