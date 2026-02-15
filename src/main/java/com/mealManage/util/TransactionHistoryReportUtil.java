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
import com.mealManage.domain.AccountTransactionHistory;
import com.mealManage.domain.StudentAccountDetails;
import com.mealManage.mealmodel.transaction.TransactionType;

/**This util class used for generate the transaction history report**/
@Component
public class TransactionHistoryReportUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font boldFontSchoolName=FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private List<String> header;
    private DecimalFormat df = new DecimalFormat("0.00");
	
	/**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void transactionHistoryReport(StudentAccountDetails studentAccountDetails, String logoLink, String schoolName, 
			HttpServletResponse response,String currencySymbol) throws Exception{
		String pdfFilePath = "TransactionHistory_"+studentAccountDetails.getStudentId()+".pdf";
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
			/*document.add(generateSchoolPdfReport(pdfFilePath, studentAccountDetails));
    		document.newPage();	*/
			
			/**Setting generally used header in Transaction report table**/
			header = new ArrayList<String>(Arrays.asList("S.NO.","TRANSACTION DATE", "TYPE", "ACCOUNT("+currencySymbol+")", "BALANCE ("+currencySymbol+")","NOTE"));
			
			/** Iterating the list<object[]> for each transaction details **/
			document.add(createTransactionHistoryTable(studentAccountDetails));
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
	
	/**This method used for generate the pdf file first page of transaction history report**/
	/*private Element generateSchoolPdfReport(String pdfPath, String logoPath, String schoolName, 
			StudentAccountDetails studentAccountDetails) throws Exception {
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(750);
		cell.addElement(createContentTable(pdfPath, logoPath, schoolName, studentAccountDetails));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}*/
	
	/**This method used for create the first page pdf content**/
	/*private Element createContentTable(String pdfPath, String logoPath, String schoolName, 
			StudentAccountDetails studentAccountDetails) throws Exception {
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

		first = new PdfPCell(new Phrase(
				"ACCOUNT HISTORY FOR " + studentAccountDetails.getStudentFName().toUpperCase() + " "+ 
					studentAccountDetails.getStudentLName().toUpperCase()+" (" + studentAccountDetails.getStudentId()+")",
				generalDateFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		long difference = sdf.parse(studentAccountDetails.getEndDate()).getTime() - 
				sdf.parse(studentAccountDetails.getStartDate()).getTime();
		if (difference / (1000 * 60 * 60 * 24) > 1)
			first = new PdfPCell(new Phrase(studentAccountDetails.getStartDate() + " - " 
					+studentAccountDetails.getEndDate(), generalDateFont));
		else
			first = new PdfPCell(new Phrase(studentAccountDetails.getStartDate(), generalDateFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);
		return mainTab;
	}*/
		
	/**This method used for build the transaction history table**/
	private Element createTransactionHistoryTable(StudentAccountDetails studentAccountDetails) throws Exception {
		PdfPTable table = new PdfPTable(new float[] { 25, 65, 70, 50, 50, 50});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase(
				"ACCOUNT HISTORY FOR " + studentAccountDetails.getStudentFName().toUpperCase() + " "+ 
					studentAccountDetails.getStudentLName().toUpperCase()+" (" + studentAccountDetails.getStudentId()+")",
				generalDateBoldFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(6);
		table.addCell(cell);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		long difference = sdf.parse(studentAccountDetails.getEndDate()).getTime() - 
				sdf.parse(studentAccountDetails.getStartDate()).getTime();
		if (difference / (1000 * 60 * 60 * 24) > 1)
			cell = new PdfPCell(new Phrase(studentAccountDetails.getStartDate() + " - " 
					+studentAccountDetails.getEndDate(), generalDateFont));
		else
			cell = new PdfPCell(new Phrase(studentAccountDetails.getStartDate(), generalDateFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(6);
		cell.setPaddingBottom(20);
		table.addCell(cell);
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		String sno = null;	
		AccountTransactionHistory accountTransactionHistory = null;
		StringBuffer sbf = null;
		for (int i = 0; i < studentAccountDetails.getAccountTransactionHistories().size(); i++) {
			sbf = new StringBuffer();
			sno = String.valueOf(i + 1);
			cell = new PdfPCell(new Phrase(sno, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			accountTransactionHistory = studentAccountDetails.getAccountTransactionHistories().get(i);
			cell = new PdfPCell(new Phrase(accountTransactionHistory.getTransactionDateTime(), generalFont));
			table.addCell(cell);
			if((accountTransactionHistory.getTransactionType().equalsIgnoreCase("Deposit") || 
					accountTransactionHistory.getTransactionType().equalsIgnoreCase("ImportBalance") || 
					accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.Adjustment.toString()) || 
					accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.Refund.toString()))
					&& accountTransactionHistory.getPaymentType() != null && !accountTransactionHistory.getPaymentType().isEmpty()){
				sbf.append("Deposit - ");
				if(accountTransactionHistory.getPaymentType() != null 
						&& accountTransactionHistory.getPaymentType().equalsIgnoreCase("CreditCard"))
					sbf.append("CC");
				else
					sbf.append(accountTransactionHistory.getPaymentType());
			}else{
				if(accountTransactionHistory.getPurchaseItemType().equalsIgnoreCase("TransferDR"))
					sbf.append("Transfer Withdrawal");
				else if(accountTransactionHistory.getTransactionType().equalsIgnoreCase("InstantPayment"))
					sbf.append("Instant Payment - "+(accountTransactionHistory.getPaymentType().equalsIgnoreCase("CreditCard") 
							? "CC" : accountTransactionHistory.getPaymentType()));
				else
					sbf.append(accountTransactionHistory.getPurchaseItemType());
			}
				
			cell = new PdfPCell(new Phrase(sbf.toString(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase((((accountTransactionHistory.getTransactionType().equalsIgnoreCase("Deposit") || 
					accountTransactionHistory.getTransactionType().equalsIgnoreCase("ImportBalance") || 
					accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.Adjustment.toString()) || 
					accountTransactionHistory.getTransactionType().equalsIgnoreCase(TransactionType.Refund.toString()))
					&& accountTransactionHistory.getPaymentType() != null && !accountTransactionHistory.getPaymentType().isEmpty()) ? "+" : 
						"-")+df.format(accountTransactionHistory.getTransactionAmount()), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(accountTransactionHistory.getFinalBalance()), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(accountTransactionHistory.getNote(), generalFont));
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		return table;
	}
}
