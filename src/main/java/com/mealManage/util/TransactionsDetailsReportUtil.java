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
import com.mealManage.domain.TransactionsDetails;

/**This util class used for generate the transaction history report**/
@Component
public class TransactionsDetailsReportUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DepositSummaryReportUtil depositSummaryReportUtil;
	
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
	public void transactionsDetailsReport(List<TransactionsDetails> transactionsDetails, HttpServletResponse response, 
			String startDate, String endDate,Long mealSchoolId, Boolean isDeposit, Map<String, Map<String, Double>> paymentTrends, String currencySymbol, Boolean isAdjTrx) throws Exception{
		String pdfFilePath = "";
		if(isAdjTrx != null && isAdjTrx)
			pdfFilePath = "AdjustmentTransactionsReport_"+mealSchoolId+".pdf";
		else{
			if(isDeposit != null && isDeposit)
				pdfFilePath = "DepositTransactionsReport_"+mealSchoolId+".pdf";
			else
				pdfFilePath = "PurchaseTransactionsReport_"+mealSchoolId+".pdf";
		}
		
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(generateSchoolPdfReport(pdfFilePath, startDate, endDate, isDeposit,isAdjTrx));
    		//document.newPage();	
			
			/**Setting generally used header in Transaction report table**/
if(isAdjTrx != null && isAdjTrx)
				header = new ArrayList<String>(Arrays.asList("S.NO.",/*"ID#",*/"STUDENT", "DATE", "TIME", "AMOUNT ("+currencySymbol+")",
						/*, "NOTE"*/ "USER", "TYPE","DESC"));
			else{
    		if(isDeposit != null && isDeposit)
    			header = new ArrayList<String>(Arrays.asList("S.NO.",/*"ID#",*/"STUDENT", "DATE", "TIME", "AMOUNT ("+currencySymbol+")",
					/*, "NOTE"*/ "USER", "TYPE","Check / CC TX. No. / TX. ID"));
    		else
    			header = new ArrayList<String>(Arrays.asList("S.NO.",/*"ID#",*/"STUDENT", "DATE", "TIME", "AMOUNT ("+currencySymbol+")",
    					/*"DESCRIPTION", "NOTE", */"ITEM","CATEGORY","LOCATION"));
	}  
			
			/** Iterating the list<object[]> for each transaction details **/
			document.add(createTransactionsTable(transactionsDetails, isDeposit,currencySymbol, isAdjTrx));
			if(paymentTrends != null && paymentTrends.size() > 0 && isDeposit != null && isDeposit)
				document.add(depositSummaryReportUtil.createTransactionsTable(paymentTrends, startDate, endDate,currencySymbol));
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
			String startDate, String endDate, Boolean isDeposit) throws Exception {
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(750);
		cell.addElement(createContentTable(pdfPath, logoPath, schoolName, startDate, endDate, isDeposit));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}*/
	
	/**This method used for create the first page pdf content**/
	private Element generateSchoolPdfReport(String pdfPath, String startDate, String endDate, Boolean isDeposit, Boolean isAdjTrx) throws Exception {
		PdfPTable mainTab = new PdfPTable(1);
		mainTab.setWidthPercentage(100);
		/*Image image = Image.getInstance(logoPath);
		image.scaleAbsolute(60f, 60f);
		image.setAlignment(Image.ALIGN_CENTER);*/
		// for first row
		PdfPCell first = new PdfPCell();
		/*first.addElement(image);
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		// first.setPaddingLeft(155);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase(schoolName.toUpperCase(), boldFontSchoolName));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(first);*/
		if(isAdjTrx != null && isAdjTrx)
			first = new PdfPCell(new Phrase("ADJUSTMENT TRANSACTIONS REPORT", generalDateBoldFont));
		else{
			if(isDeposit != null && isDeposit)
				first = new PdfPCell(new Phrase("DEPOSIT TRANSACTIONS REPORT", generalDateBoldFont));
			else
				first = new PdfPCell(new Phrase("PUCHASE TRANSACTIONS REPORT", generalDateBoldFont));
		}
		
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
	private Element createTransactionsTable(List<TransactionsDetails> transactionsDetails, Boolean isDeposit,String currencySymbol, Boolean isAdjTrx) throws Exception {
		PdfPTable table = null;
		if((isAdjTrx != null && isAdjTrx) || (isDeposit != null && isDeposit))
			table = new PdfPTable(new float[] {30, 50, 35, 35, 40, 50, 50, 50 });
		else
			table = new PdfPTable(new float[] {30, 50, 35, 35, 40, 50, 50, 50 });
		PdfPCell cell;
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		String sno = null;
		TransactionsDetails transactionsDetail = null;
		for (int i = 0; i < transactionsDetails.size(); i++) {
			sno = String.valueOf(i + 1);
			cell = new PdfPCell(new Phrase(sno, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			transactionsDetail = transactionsDetails.get(i);
			/*cell = new PdfPCell(new Phrase(transactionsDetail.getIdNumb().toString(), generalFont));
			table.addCell(cell);*/
			cell = new PdfPCell(new Phrase(transactionsDetail.getStudentLName()+", "
					+transactionsDetail.getStudentFName(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(transactionsDetail.getTransactionDate(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(transactionsDetail.getTransactionTime(), generalFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(transactionsDetail.getAmount()), generalFont));
			table.addCell(cell);
			/*if(isDeposit == null || !isDeposit){
				cell = new PdfPCell(new Phrase(transactionsDetail.getTransactionDesc(), generalFont));
				table.addCell(cell);
			}	*/		
			/*cell = new PdfPCell(new Phrase(transactionsDetail.getNote(), generalFont));
			table.addCell(cell);*/
			if(isDeposit != null && isDeposit){
				cell = new PdfPCell(new Phrase(transactionsDetail.getUser(), generalFont));
				table.addCell(cell);
			}			
			if(isDeposit != null && isDeposit)
				cell = new PdfPCell(new Phrase(transactionsDetail.getType(), generalFont));
			else
				cell = new PdfPCell(new Phrase(transactionsDetail.getItemPurchased().equalsIgnoreCase("TransferDR") ? 
						"Transfer Withdrawal": transactionsDetail.getItemPurchased(), generalFont));
			table.addCell(cell);
			if((isAdjTrx == null || !isAdjTrx) && (isDeposit == null || !isDeposit)){
				cell = new PdfPCell(new Phrase(transactionsDetail.getCategory(), generalFont));
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(transactionsDetail.getLocation(), generalFont));
				table.addCell(cell);
			}
			if(isAdjTrx != null && isAdjTrx){
				cell = new PdfPCell(new Phrase(transactionsDetail.getTransactionDesc(), generalFont));
				table.addCell(cell);
			}else{
				if(isDeposit != null && isDeposit){
					cell = new PdfPCell(new Phrase(transactionsDetail.getPaymentType().equalsIgnoreCase("Online") ? transactionsDetail.getTransferId() : 
						((transactionsDetail.getPaymentType().equalsIgnoreCase("CreditCard") || transactionsDetail.getPaymentType().equalsIgnoreCase("Check"))
								? transactionsDetail.getCheckNum() : ""), generalFont));
					table.addCell(cell);
					/*cell = new PdfPCell(new Phrase(transactionsDetail.getTransactionDesc(), generalFont));
					table.addCell(cell);*/
				}
			}
			
		}
		if((isAdjTrx == null || !isAdjTrx) && isDeposit != null && isDeposit){
			cell = new PdfPCell(new Phrase("", generalFont));
			cell.setColspan(8);
			cell.setPaddingBottom(25);
			cell.setBorder(0);
			table.addCell(cell);
		}		
		table.setWidthPercentage(100);
		return table;
	}
}
