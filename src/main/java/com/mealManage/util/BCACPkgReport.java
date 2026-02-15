package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.mealManage.response.BCACSubscriptions;

/**This util class used for generate the PACKAGE TRANSACTIONS REPORTS**/
@Component
public class BCACPkgReport {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private List<String> header;
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
	@Autowired
	private DateUtilityV2 du;
    private Map<String, String> gradesKeyVal;
	
	/**This method used for generate the pdf of package transactions report
	 * @throws Exception **/
	public void pkgReport(List<BCACSubscriptions> bcacSubscriptions, HttpServletResponse response, 
			String subscribeDt, Long mealSchoolId, String countryCode, String timezone, String dateFormat) throws Exception{
		String pdfFilePath = "BCACReports_"+mealSchoolId+".pdf";		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode);//setting the grade key value (i.e. one means 1, two means 2,..etc)
			String reportName = "BEFORE & AFTER CARE PACKAGES REPORT";
			/**Setting generally used header in Transaction report table**/
			header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT","GRADE","PACKAGE","CHECK-IN","CHECK-OUT", "PICKUP BY"));
			document.add(generateSchoolPdfReport(pdfFilePath, reportName, subscribeDt, dateFormat));
    		//document.newPage();	
			
			/** Iterating the list<object[]> for each transaction details **/
			document.add(createTransactionsTable(bcacSubscriptions, timezone, dateFormat));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
			logger.info("BCAC packages pdf report generated successfully.");
		}catch (Exception e){
			logger.error("Failed to generate pdf report of BCAC packages due to "+e.getMessage());
		}
	}
	
	/**This method used for create the first page pdf content**/
	private Element generateSchoolPdfReport(String pdfPath, String reportName, String subscribeDt, String dateFormat) throws Exception {
		PdfPTable mainTab = new PdfPTable(1);
		mainTab.setWidthPercentage(100);
		SimpleDateFormat sdfOrg = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		PdfPCell first = new PdfPCell();
		first = new PdfPCell(new Phrase((reportName+" - "+sdf.format(sdfOrg.parse(subscribeDt))).toUpperCase(), generalDateBoldFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		first.setPaddingBottom(20);
		mainTab.addCell(first);
		return mainTab;
	}
		
	/**This method used for build the transaction history table**/
	private Element createTransactionsTable(List<BCACSubscriptions> bcacSubscriptions, String timezone, String dateFormat) throws Exception {
		PdfPTable table = null;
		table = new PdfPTable(new float[] {30, 60, 35, 50, 55, 55, 60});
		PdfPCell cell;
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		String sno = null;
		BCACSubscriptions bcacSubscription = null;
		for (int i = 0; i < bcacSubscriptions.size(); i++) {
			sno = String.valueOf(i + 1);
			cell = new PdfPCell(new Phrase(sno, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			bcacSubscription = bcacSubscriptions.get(i);
			cell = new PdfPCell(new Phrase(bcacSubscription.getStdLName()+", "
						+bcacSubscription.getStdFName(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase((gradesKeyVal.get(bcacSubscription.getGrade())).toUpperCase(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(bcacSubscription.getPackageName(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase((du.formatDateToString(bcacSubscription.getCheckIn(), (dateFormat+" hh:mm:ss a"), timezone)), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase((du.formatDateToString(bcacSubscription.getCheckOut(), (dateFormat+" hh:mm:ss a"), timezone)), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(bcacSubscription.getPickupBy(), generalFont));
			table.addCell(cell);			
		}
		table.setWidthPercentage(100);
		return table;
	}
}
