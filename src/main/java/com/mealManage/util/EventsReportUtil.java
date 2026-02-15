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
import com.mealManage.response.EventsResp;

/**This utility class used for export the events report in pdf**/
@Component
public class EventsReportUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
    private List<String> header;
    private DecimalFormat df = new DecimalFormat("0.00");
	
    /**This method used for export the events transactions report in pdf**/
	public void exportEventsPdf(Map<Long, List<EventsResp>> studentsByEvent, Long mealSchoolId, String startDate, 
			String endDate, HttpServletResponse httpResp){
		String pdfFilePath = "";
		pdfFilePath = "EventsTransactionsReport_"+mealSchoolId+".pdf";
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(generateSchoolPdfReport(pdfFilePath, startDate, endDate));
			/**Setting generally used header in Transaction report table**/
    		header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT NAME", "STUDENT ID#", "TRANSACTION DATE", "AMOUNT ($)"));
			
			/** Iterating the list<object[]> for each transaction details **/
			document.add(createTransactionsTable(studentsByEvent));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			httpResp.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, httpResp.getOutputStream());
			httpResp.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e){
			logger.error("Error occurred during build events transaction pdf report file due to  "+e.getMessage());
		}
	}
	
	/**This method used for create the first page pdf content**/
	private Element generateSchoolPdfReport(String pdfPath, String startDate, String endDate) throws Exception {
		PdfPTable mainTab = new PdfPTable(1);
		mainTab.setWidthPercentage(100);
		PdfPCell first = new PdfPCell();
		first = new PdfPCell(new Phrase("EVENTS TRANSACTIONS REPORT", generalDateBoldFont));
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
		
	/**This method used for build the event's transaction report table**/
	private Element createTransactionsTable(Map<Long, List<EventsResp>> studentsByEvent) throws Exception {
		PdfPTable table = new PdfPTable(new float[] {30, 60, 30, 60, 40});
		PdfPCell cell;
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		String sno = null;
		for(Map.Entry<Long, List<EventsResp>> entry : studentsByEvent.entrySet()){
			cell = new PdfPCell(new Phrase(entry.getValue().get(0).getEventName().toUpperCase(), boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#F7F4F4"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(4);
			table.addCell(cell);
			Double totalAmount = entry.getValue().stream().mapToDouble(x -> x.getTransactionAmount()).sum();
			cell = new PdfPCell(new Phrase(df.format(totalAmount), boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#F7F4F4"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			for (int i = 0; i < entry.getValue().size(); i++) {
				EventsResp eventsResp = entry.getValue().get(i);
				sno = String.valueOf(i+1);
				cell = new PdfPCell(new Phrase(sno, generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				cell = new PdfPCell(
						new Phrase(eventsResp.getLastName() + ", " + eventsResp.getFirstName(), generalFont));
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(eventsResp.getStudentId(), generalFont));
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(eventsResp.getTrxDateTime(), generalFont));
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(df.format(eventsResp.getTransactionAmount()), generalFont));
				table.addCell(cell);
			}
		}
		table.setWidthPercentage(100);
		return table;
	}

}
