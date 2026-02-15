package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
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
import com.mealManage.mealmodel.packages.PackagePaymentsTrx;

/**This util class used for generate the PACKAGE TRANSACTIONS REPORTS**/
@Component
public class PackagePaymentTrxUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font boldFontSchoolName=FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private List<String> header;
    private DecimalFormat df = new DecimalFormat("0.00");
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
    private Map<String, String> gradesKeyVal;
	
	/**This method used for generate the pdf of package transactions report
	 * @throws Exception **/
	public void transactionsDetailsReport(List<PackagePaymentsTrx> transactionsDetails, HttpServletResponse response, 
			String startDate, String endDate,Long mealSchoolId, String currencySymbol, Long stdRecId, String countryCode, String dateFormat) throws Exception{
		String pdfFilePath = "PackagePaymentsTrxReport_"+mealSchoolId+".pdf";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();

			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode);//setting the grade key value (i.e. one means 1, two means 2,..etc)
			String reportName = "PACKAGE PAYMENT TRANSACTIONS REPORT";
			/**Setting generally used header in Transaction report table**/
			if(stdRecId != null){
				header = new ArrayList<String>(Arrays.asList("S.NO.","TYPE", "PACKAGE", "DATE RANGE", "PAY. TYPE",
						"REF. ID","USER","TX. DATE","AMOUNT ("+currencySymbol+")"));
				reportName = reportName + " FOR "+(transactionsDetails.get(0).getStdFName()+" "+transactionsDetails.get(0).getStdLName()).toUpperCase();
			}else
	    		header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT","GRADE","TYPE", "PACKAGE", "DATE RANGE", "PAY. TYPE",
						"REF. ID","USER", "TX. DATE","AMOUNT ("+currencySymbol+")"));
			document.add(generateSchoolPdfReport(pdfFilePath, startDate, endDate, reportName));
    		//document.newPage();	
			
			/** Iterating the list<object[]> for each transaction details **/
			document.add(createTransactionsTable(transactionsDetails,currencySymbol, stdRecId, dateFormat));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
			logger.info("Package payment transactions pdf report generated successfully.");
		}catch (Exception e){
			logger.error("Failed to generate pdf report of Package payments transactions due to "+e.getMessage());
		}
	}
	
	/**This method used for create the first page pdf content**/
	private Element generateSchoolPdfReport(String pdfPath, String startDate, String endDate, String reportName) throws Exception {
		PdfPTable mainTab = new PdfPTable(1);
		mainTab.setWidthPercentage(100);
		PdfPCell first = new PdfPCell();
		first = new PdfPCell(new Phrase(reportName, generalDateBoldFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		SimpleDateFormat sdfOrg = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
		long difference = sdfOrg.parse(endDate).getTime() - sdfOrg.parse(startDate).getTime();
		if (difference / (1000 * 60 * 60 * 24) > 1)
			first = new PdfPCell(new Phrase(sdf.format(sdfOrg.parse(startDate)) + " - " 
					+sdf.format(sdfOrg.parse(endDate)), generalDateFont));
		else
			first = new PdfPCell(new Phrase(sdf.format(sdfOrg.parse(startDate)), generalDateFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		first.setPaddingBottom(20);
		mainTab.addCell(first);
		return mainTab;
	}
		
	/**This method used for build the transaction history table**/
	private Element createTransactionsTable(List<PackagePaymentsTrx> transactionsDetails, String currencySymbol, Long stdRecId, String dateFormat) throws Exception {
		PdfPTable table = null;
		SimpleDateFormat sdfV = new SimpleDateFormat(dateFormat);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		if(stdRecId != null)
			table = new PdfPTable(new float[] {30, 40, 50, 50, 30, 50, 50, 60, 40});
		else
			table = new PdfPTable(new float[] {30, 60, 35, 40, 50, 50, 30, 50, 50, 60, 40});
		PdfPCell cell;
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		String sno = null;
		PackagePaymentsTrx transactionsDetail = null;
		for (int i = 0; i < transactionsDetails.size(); i++) {
			sno = String.valueOf(i + 1);
			cell = new PdfPCell(new Phrase(sno, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			transactionsDetail = transactionsDetails.get(i);
			if(stdRecId == null){
				cell = new PdfPCell(new Phrase(transactionsDetail.getStdLName()+", "
						+transactionsDetail.getStdFName(), generalFont));
				table.addCell(cell);
				cell = new PdfPCell(new Phrase((gradesKeyVal.get(transactionsDetail.getGrade())).toUpperCase(), generalFont));
				table.addCell(cell);
			}
			cell = new PdfPCell(new Phrase(transactionsDetail.getPackageType(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(transactionsDetail.getPackageName(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(!transactionsDetail.getStartDt().trim().isEmpty() 
					? (sdfV.format(sdf.parse(transactionsDetail.getStartDt()))+" - "+(sdfV.format(sdf.parse(transactionsDetail.getEndDt())))) : "", generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(transactionsDetail.getPaymentType(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(transactionsDetail.getTransferId(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase((transactionsDetail.getUserEmail()), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(transactionsDetail.getTransactionDateTime(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(transactionsDetail.getAmount()), generalFont));
			table.addCell(cell);			
		}
		table.setWidthPercentage(100);
		return table;
	}
}
