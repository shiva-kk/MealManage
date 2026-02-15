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
import com.mealManage.domain.AccBalanceSummary;

/**This util class used for generate the deposit summary report**/
@Component
public class DepositSummaryReportUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalFont1=FontFactory.getFont(FontFactory.HELVETICA, 7);
 	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	public static final  Font boldFontSchoolName=FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private List<String> header;
    private DecimalFormat df = new DecimalFormat("##,###,##0.00");
	
	/**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void depositSummary(Map<String, Map<String, Double>> paymentTrends, 
			HttpServletResponse response, String startDate, String endDate,	Long mealSchoolId, String currencySymbol) throws Exception{
		String pdfFilePath = "DepositSummaryReport_"+mealSchoolId+".pdf";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(createTransactionsTable(paymentTrends, startDate, endDate,currencySymbol));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e){
			logger.error("Failed to export deposit summary report due to "+e.getMessage());
		}
	}
		
	/**This method used for build the transaction history table**/
	public Element createTransactionsTable(Map<String, Map<String, Double>> paymentTrends, String startDate, 
			String endDate, String currencySymbol) throws Exception {
		header = new ArrayList<String>(Arrays.asList("S.NO.","TRANSACTION TYPE","TOTAL TRANSACTIONS","TOTAL AMOUNT ("+currencySymbol+")"));
		PdfPTable table = new PdfPTable(new float[] {20, 50, 50, 50});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("DEPOSITS SUMMARY", generalDateBoldFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(4);
		table.addCell(cell);
		SimpleDateFormat sdfOrg = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
		long difference = sdfOrg.parse(endDate).getTime() - sdfOrg.parse(startDate).getTime();
		if (difference / (1000 * 60 * 60 * 24) > 1)
			cell = new PdfPCell(new Phrase(sdf.format(sdfOrg.parse(startDate)) + " - " 
					+sdf.format(sdfOrg.parse(endDate)), generalDateFont));
		else
			cell = new PdfPCell(new Phrase(sdf.format(sdfOrg.parse(startDate)), generalDateFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(4);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("         ", generalFont));
		cell.setColspan(4);
		cell.setBorder(0);
		table.addCell(cell); 
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		int sno = 1;
		Integer grandTtlTrx = 0;
		Double grandTtlAmt = 0.0;
		for (Map.Entry<String, Map<String, Double>> entry : paymentTrends.entrySet()) {
			cell = new PdfPCell(new Phrase(String.valueOf(sno), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(entry.getKey(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(entry.getValue().get("totalTransactions").intValue()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			grandTtlTrx = grandTtlTrx+entry.getValue().get("totalTransactions").intValue();
			cell = new PdfPCell(new Phrase(df.format(entry.getValue().get("totalAmount")), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			grandTtlAmt = grandTtlAmt+entry.getValue().get("totalAmount");
			sno++;
		}
		if(grandTtlTrx > 0){
			cell = new PdfPCell(new Phrase("Grand Total:", boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setColspan(2);
			cell.setBorder(0);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(grandTtlTrx), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(currencySymbol+df.format(grandTtlAmt), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for generate the pdf of account balance summary report
	 * @throws Exception **/
	public void accBalSummary(List<AccBalanceSummary> summaries, 
			HttpServletResponse response, Long id, String currencySymbol, Boolean isDistrict, String loggedUser,String currDate, String name,String pDate) throws Exception{
		String pdfFilePath = "AccountBalanceSummary_"+id+".pdf";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(CommonUtil.generateHeader(pdfFilePath, name, loggedUser, currDate,amazonS3Bucketname,"MEAL A/C BALANCE SUMMARY",pDate));
			document.add(createAccSummaryTable(summaries,currencySymbol, isDistrict));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e){
			logger.error("Failed to export Account Balance summary report due to "+e.getMessage());
		}
	}
		
	/**This method used for build the account balance summary table**/
	public Element createAccSummaryTable(List<AccBalanceSummary> summaries, String currencySymbol, Boolean isDistrict) throws Exception {
		List<String> header1 = null;
		PdfPTable table = null;
		if(isDistrict){
			header1 = new ArrayList<String>(Arrays.asList("S.NO.","SCHOOL NAME","TYPE","BALANCE ("+currencySymbol+")","NEGATIVE BALANCE ("+currencySymbol+")"));
			table = new PdfPTable(new float[] {15, 50, 30, 40, 40});
		}else{
			header1 = new ArrayList<String>(Arrays.asList("TYPE","BALANCE ("+currencySymbol+")","NEGATIVE BALANCE ("+currencySymbol+")"));
			table = new PdfPTable(new float[] {60, 50, 50});
		}
		PdfPCell cell;
		/*cell = new PdfPCell(new Phrase("ACCOUNT BALANCE SUMMARY", generalDateBoldFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(header1.size());
		table.addCell(cell);*/
		cell = new PdfPCell(new Phrase("         ", generalFont));
		cell.setColspan(header1.size());
		cell.setBorder(0);
		table.addCell(cell); 
		for (String head2 : header1) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		int sno = 1;
		Double totalStdPositiveBal = 0.0;
		Double totalStdNegativeBal = 0.0;
		Double totalStaffPositiveBal = 0.0;
		Double totalStaffNegativeBal = 0.0;
		for (AccBalanceSummary summary : summaries) {
			if(isDistrict){
				cell = new PdfPCell(new Phrase(String.valueOf(sno), generalFont));
				cell.setRowspan(3);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(summary.getSchoolName().toUpperCase(), generalFont));
				cell.setRowspan(3);
				table.addCell(cell);
			}
			cell = new PdfPCell(new Phrase("STUDENT", generalFont));
			table.addCell(cell);
			totalStdPositiveBal = totalStdPositiveBal+summary.getStdAccBalance();
			totalStaffPositiveBal = totalStaffPositiveBal+summary.getStaffAccBalance();
			totalStdNegativeBal = totalStdNegativeBal+summary.getStdNegativeBalance();
			totalStaffNegativeBal = totalStaffNegativeBal+summary.getStaffNegativeBalance();
			cell = new PdfPCell(new Phrase(df.format(summary.getStdAccBalance()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(summary.getStdNegativeBalance()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("STAFF", generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(summary.getStaffAccBalance()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(summary.getStaffNegativeBalance()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("TOTAL", boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setBorder(0);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(currencySymbol+df.format(summary.getStdAccBalance()+summary.getStaffAccBalance()), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(currencySymbol+df.format(summary.getStdNegativeBalance()+summary.getStaffNegativeBalance()), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			sno++;
		}
		if(isDistrict){
			cell = new PdfPCell(new Phrase("GRAND TOTAL:", boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setBorder(0);
			cell.setColspan(2);
			cell.setRowspan(3);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("STUDENT", generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(totalStdPositiveBal), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(totalStdNegativeBal), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("STAFF", generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(totalStaffPositiveBal), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(totalStaffNegativeBal), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("TOTAL", boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setBorder(0);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(currencySymbol+df.format(totalStdPositiveBal+totalStaffPositiveBal), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(currencySymbol+df.format(totalStdNegativeBal+totalStaffNegativeBal), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		return table;
	}
	
}
