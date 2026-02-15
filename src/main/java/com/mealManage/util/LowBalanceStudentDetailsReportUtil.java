package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.domain.AccountTransactionHistory;
import com.mealManage.domain.StudentAccountDetails;
import com.mealManage.mealmodel.school.School;
import com.mealManage.mealmodel.transaction.TransactionType;

/**This utility class used for generate the low balance student's details report in pdf**/
@Component
public class LowBalanceStudentDetailsReportUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 9);
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
	@Autowired
	private DateUtilityV2 du;
	
	/**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void transactionHistoryReport(StudentAccountDetails studentAccountDetails, String schoolName, 
			HttpServletResponse response, School school, String schoolContact, String subdomain, Double accBalance,String timezone, String dateFormat) throws Exception{
		String pdfFilePath = "LowBalanceStudentReport_"+studentAccountDetails.getStudentId()+".pdf";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(createFirstPage(schoolName, studentAccountDetails, school,timezone));
    		document.newPage();	
			document.add(createSecondPage(studentAccountDetails, accBalance, school, schoolContact, subdomain,timezone, dateFormat));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e){
			logger.error("Error occurred during build transaction history pdf report file due to  "+e.getMessage());
		}
	}
	
	/**This method used for generate the pdf file first page of low balance student details report**/
	private Element createFirstPage(String schoolName, StudentAccountDetails studentAccountDetails, 
			School school,String timezone) throws Exception {
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(750);
		cell.addElement(createContentTable(schoolName, studentAccountDetails, school,timezone));
		//cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}
	
	/**This method used for generate the pdf file first page of low balance student details report**/
	private Element createSecondPage( StudentAccountDetails studentAccountDetails, Double accBalance,
			School school, String schoolContactNo, String subdomain,String timezone, String dateFormat) throws Exception {
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(750);
		cell.addElement(createSecondPageContent(studentAccountDetails, school, accBalance, 
				schoolContactNo, subdomain, timezone, dateFormat));
		if(studentAccountDetails.getAccountTransactionHistories() != null && 
				studentAccountDetails.getAccountTransactionHistories().size() > 32){
			mainTable.addCell(cell);
			cell = new PdfPCell();
		}		
		cell.addElement(createAccountActivity(studentAccountDetails));
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}
	
	/**This method used for create the first page pdf content**/
	private Element createContentTable(String schoolName, StudentAccountDetails studentAccountDetails, 
			School school, String timezone) throws Exception {
		PdfPTable mainTab = new PdfPTable(new float[] { 70, 30});
		mainTab.setWidthPercentage(100);
		// for first row
		PdfPCell first = new PdfPCell();
		first = new PdfPCell(new Phrase("Food Service Department", generalFont));
		first.setBorder(0);
		//first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(" ", generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(schoolName, generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(du.formatDateToString(new Date(),
				"EEEE, MMMM dd, yyyy", timezone), generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(school.getSchoolAddress(), generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(" ", generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(school.getSchoolDistrictName()+", "+school.getCity(), generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(" ", generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(school.getCounty()+", "+school.getCityStateZip(), generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(" ", generalFont));
		first.setBorder(0);
		mainTab.addCell(first);

		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk("To the Parent or Guardian of ", generalFont));
		paragraph.add(new Chunk(studentAccountDetails.getStudentFName()+" "+ 
				studentAccountDetails.getStudentLName(), boldFont));
		first = new PdfPCell(paragraph);
		first.setBorder(0);
		//first.setHorizontalAlignment(Element.ALIGN_CENTER);
		first.setPaddingLeft(80);
		first.setPaddingTop(70);
		first.setVerticalAlignment(Element.ALIGN_MIDDLE);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(" ", generalFont));
		first.setBorder(0);
		mainTab.addCell(first);
		
		if(studentAccountDetails.getNumberStreetApt() != null){
			paragraph = new Paragraph();
			paragraph.add(new Chunk(studentAccountDetails.getNumberStreetApt(), generalFont));
			first = new PdfPCell(paragraph);
			first.setBorder(0);
			//first.setHorizontalAlignment(Element.ALIGN_CENTER);
			first.setPaddingLeft(80);
			first.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTab.addCell(first);
			first = new PdfPCell(new Phrase(" ", generalFont));
			first.setBorder(0);
			mainTab.addCell(first);
		}
		
		if(studentAccountDetails.getCityStateZip() != null){
			paragraph = new Paragraph();
			paragraph.add(new Chunk(studentAccountDetails.getCityStateZip(), generalFont));
			first = new PdfPCell(paragraph);
			first.setBorder(0);
			//first.setHorizontalAlignment(Element.ALIGN_CENTER);
			first.setPaddingLeft(80);
			first.setVerticalAlignment(Element.ALIGN_MIDDLE);
			mainTab.addCell(first);
			first = new PdfPCell(new Phrase(" ", generalFont));
			first.setBorder(0);
			mainTab.addCell(first);
		}
		
		return mainTab;
	}
	
	/**This method used for create the second page pdf content**/
	private Element createSecondPageContent(StudentAccountDetails studentAccountDetails, 
			School school, Double accBalance, String schoolContactNo, String subdomain, String timezone, String dateFormat) throws Exception {
		PdfPTable mainTab = new PdfPTable(1);
		mainTab.setWidthPercentage(100);
		// for first row
		PdfPCell first = new PdfPCell();
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk("Parent or Guardian of ", generalFont));
		paragraph.add(new Chunk(studentAccountDetails.getStudentFName()+" "+ 
				studentAccountDetails.getStudentLName(), boldFont));
		first = new PdfPCell(paragraph);
		first.setBorder(0);
		mainTab.addCell(first);
		if(studentAccountDetails.getNumberStreetApt() != null){
			paragraph = new Paragraph();
			paragraph.add(new Chunk(studentAccountDetails.getNumberStreetApt(), generalFont));
			first = new PdfPCell(paragraph);
			first.setBorder(0);
			mainTab.addCell(first);
		}
		if(studentAccountDetails.getCityStateZip() != null){
			paragraph = new Paragraph();
			paragraph.add(new Chunk(studentAccountDetails.getCityStateZip(), generalFont));
			first = new PdfPCell(paragraph);
			first.setBorder(0);
			mainTab.addCell(first);
		}
		
		first = new PdfPCell(new Phrase(du.formatDateToString(new Date(),
				dateFormat, timezone), generalFont));
		first.setBorder(0);
		first.setPaddingTop(7);
		mainTab.addCell(first);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("To the parent or guardian of ", generalFont));
		paragraph.add(new Chunk(studentAccountDetails.getStudentFName()+" "+ 
				studentAccountDetails.getStudentLName(), boldFont));
		first = new PdfPCell(paragraph);
		first.setBorder(0);
		first.setPaddingTop(10);
		mainTab.addCell(first);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("The purpose of this notification is to let you know the status of ", generalFont));
		paragraph.add(new Chunk(studentAccountDetails.getStudentFName(), boldFont));
		paragraph.add(new Chunk("'s meal account. Your student's meal account has expired and "
				+ "has a balance of ", generalFont));
		paragraph.add(new Chunk(String.format("%.2f",accBalance), boldFont));
		paragraph.add(new Chunk(". Please note, this balance may not include meals purchased "
				+ "today.", generalFont));
		first = new PdfPCell(paragraph);
		first.setBorder(0);
		first.setPaddingTop(12);
		mainTab.addCell(first);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("Please send the appropriate amount needed to credit ", generalFont));
		paragraph.add(new Chunk(studentAccountDetails.getStudentFName(), boldFont));
		paragraph.add(new Chunk("'s meal account as soon as possible. Students with an expired "
				+ "meal account are unable purchase and eat a meal.", generalFont));
		first = new PdfPCell(paragraph);
		first.setBorder(0);
		first.setPaddingTop(14);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase("Thank you in advance for your quick response. If you have any questions or concerns, "
				+ "please contact the school office at "+(schoolContactNo != null ? schoolContactNo : 
					"xxxx-xx-xxxx")+".", generalFont));
		first.setBorder(0);
		first.setPaddingTop(15);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase("Thank you,", generalFont));
		first.setBorder(0);
		first.setPaddingTop(16);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase("The "+subdomain.toUpperCase()+" Food Service Department", generalFont));
		first.setBorder(0);
		first.setPaddingTop(17);
		mainTab.addCell(first);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("The following is a four week summary of ", generalFont));
		paragraph.add(new Chunk(studentAccountDetails.getStudentFName(), boldFont));
		paragraph.add(new Chunk("'s account activity:", generalFont));
		first = new PdfPCell(paragraph);
		first.setBorder(0);
		first.setPaddingTop(18);
		mainTab.addCell(first);		
		return mainTab;
	}
	
	private Element createAccountActivity(StudentAccountDetails studentAccountDetails) throws ParseException{
		PdfPCell cellValue = new PdfPCell();
		cellValue.setFixedHeight(750);
	    PdfPTable cellTable = new PdfPTable(new float[] { 15, 30, 20, 50,40});
	    List<String> headers = new ArrayList<String>(Arrays.asList("Date", "Transaction Type", "Amount", "Note",""));
	    PdfPCell cell;
		for (String head2 : headers) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBorder(0);
			cell.setPaddingTop(22);
			cellTable.addCell(cell);
		}
	    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		for (AccountTransactionHistory accountTransactionHistory : studentAccountDetails
				.getAccountTransactionHistories()) {
			cell = new PdfPCell(new Phrase(sdf.format(sdf.parse(accountTransactionHistory.getTransactionDateTime())),
					generalFont));
			cell.setBorder(0);
			cellTable.addCell(cell);
			cell = new PdfPCell(new Phrase(accountTransactionHistory.getTransactionType().equalsIgnoreCase("Deposit") || 
					((accountTransactionHistory.getTransactionType().equalsIgnoreCase("ImportBalance") || 
							accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.Adjustment.toString()) || 
							accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.InstantPayment.toString()) || 
							accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.Refund.toString()))
							&& accountTransactionHistory.getPaymentType() != null && !accountTransactionHistory.getPaymentType().trim().isEmpty()) ? accountTransactionHistory.getPaymentType() : 
						accountTransactionHistory.getPurchaseItemType(),
					generalFont));
			cell.setBorder(0);
			cellTable.addCell(cell);
			cell = new PdfPCell(new Phrase((accountTransactionHistory.getTransactionType().equalsIgnoreCase("Deposit") || 
					((accountTransactionHistory.getTransactionType().equalsIgnoreCase("ImportBalance") || 
							accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.Adjustment.toString()) || 
							accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.Refund.toString()))
							&& accountTransactionHistory.getPaymentType() != null && !accountTransactionHistory.getPaymentType().trim().isEmpty()) ? "+" : 
						"-")+String.format("%.2f", 
							accountTransactionHistory.getTransactionAmount()),
					generalFont));
			cell.setBorder(0);
			//cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellTable.addCell(cell);
			cell = new PdfPCell(new Phrase(accountTransactionHistory.getNote(), generalFont));
			cell.setBorder(0);
			cellTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ",	generalFont));
			cell.setBorder(0);
			cellTable.addCell(cell);
		}	
		return cellTable;
	}
}
