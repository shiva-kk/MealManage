package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.response.RevenueResp;

/**This util class used for generate the transaction history report**/
@Component
public class RevenueReportUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font titleFont=FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
	public static final  Font boldFontSchoolName=FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	public static final  Font generalFont1=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
 	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
    private List<String> header1;
   // private List<String> header2;
    private List<String> header3;
    private DecimalFormat df = new DecimalFormat("0.00");
   /**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void generateRevenueReport(RevenueResp revenueResp, HttpServletResponse response, 
			String startDate, String endDate,Long districtId, String currencySymbol, String districtName, String userName, String currDate) throws Exception{
		String pdfFilePath = "";
		pdfFilePath = "RevenueReport_"+districtId+".pdf";
		header1 = Arrays.asList("Breakfast", "Lunch", "Snack");
		//header2 = Arrays.asList("Paid"/*, "Earned", "Total"*/);
		header3 = Arrays.asList("Cash / Check", "Prepaid", "Charged", "Total");
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(generateSchoolPdfReport(pdfFilePath, startDate, endDate, districtName, userName, currDate));
			document.add(createMealsAndRevenueTable(revenueResp, currencySymbol));
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
	
	/**This method used for create the first page pdf content**/
	private Element generateSchoolPdfReport(String pdfPath, String startDate, String endDate, String districtName, 
			String userName, String currDate) throws Exception {
		PdfPTable mainTab = new PdfPTable(3);
		mainTab.setWidthPercentage(100);
		// for first row
		PdfPCell first = new PdfPCell();

		String logo = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";
		Image image = Image.getInstance(logo);
    	image.scaleAbsolute(30f, 30f);
    	image.setAlignment(Image.ALIGN_RIGHT);
		//for first row
		first = new PdfPCell();	   
		first.addElement(image);
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_RIGHT);
		first.setRowspan(3);
		first.setPaddingLeft(155);
		mainTab.addCell(first);
		
		first = new PdfPCell(new Phrase(districtName, boldFontSchoolName));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_LEFT);
		first.setColspan(2);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase("Meal and Revenue Report", generalDateBoldFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_LEFT);
		//first.setColspan(2);
		mainTab.addCell(first);
		SimpleDateFormat sdfOrg1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
		first = new PdfPCell(new Phrase("Generated On: "+sdf1.format(sdfOrg1.parse(currDate)), generalFont1));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
		first.setHorizontalAlignment(Element.ALIGN_LEFT);
		first.setPaddingBottom(20);
		//first.setColspan(2);
		mainTab.addCell(first);

		first = new PdfPCell(new Phrase("Generated By: "+userName, generalFont1));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_RIGHT);
		mainTab.addCell(first);
		
		return mainTab;
	}
	
	/**This method used for build the report table**/
	private PdfPCell createMealsTable(RevenueResp revenueResp) throws Exception {
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = null;
		table = new PdfPTable(new float[]{45,10,45});
		PdfPCell cell;
		
		cell = new PdfPCell(new Phrase("Reimbursement Meals", titleFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("", boldFont));
		cell.setBorder(0);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Non Reimbursement Meals", titleFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell = buildMealsTable(revenueResp.getReimbMeal());
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("", boldFont));
		cell.setBorder(0);
		table.addCell(cell);
		
		cell = buildMealsTable(revenueResp.getNonReimbMeal());
		table.addCell(cell);
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		cellFinal.setBorder(0);
		return cellFinal;
	}
		
	/**This method used for build the report table**/
	private Element createMealsAndRevenueTable(RevenueResp revenueResp, String currencySymbol) throws Exception {
		PdfPTable table = null;
		table = new PdfPTable(new float[]{45,17,38});
		PdfPCell cell;
		
		cell = createMealsTable(revenueResp);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Revenue from Meals ($)", titleFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = buildRevenueTable(revenueResp.getRevenueFromMeal(), currencySymbol);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("", boldFont));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Revenue from A la Carte Sales ($)", titleFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = buildRevenueTable(revenueResp.getRevenueFromAlaCarte(), currencySymbol);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("", boldFont));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		
		if(revenueResp.getRevenueByLoc() != null && revenueResp.getRevenueByLoc().size() > 0){
			cell = new PdfPCell(new Phrase("Location wise Revenue", titleFont));
			cell.setBackgroundColor(BaseColor.GRAY);
		}else
			cell = new PdfPCell(new Phrase("", titleFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", boldFont));
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Daily Sales Summary", titleFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		if(revenueResp.getRevenueByLoc() != null && revenueResp.getRevenueByLoc().size() > 0)
			table.addCell(buildRevenueByLoc(revenueResp.getRevenueByLoc(), currencySymbol));
		else{
			cell = new PdfPCell(new Phrase("", boldFont));
			cell.setBorder(0);
			table.addCell(cell);
		}
		cell = new PdfPCell(new Phrase("", boldFont));
		cell.setBorder(0);
		table.addCell(cell);	
		table.addCell(buildDailySummaryTab(revenueResp.getSalesSummary(), currencySymbol));
		
		table.setWidthPercentage(100);
		return table;
	}
	
	private PdfPCell buildMealsTable(Map<String, Map<String, Integer>> meals){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = null;
		table = new PdfPTable(new float[] {50, 25, 25, 25});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		for (String head2 : header1) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		
		for(Map.Entry<String, Map<String, Integer>> e1 : meals.entrySet()){
			cell = new PdfPCell(new Phrase(e1.getKey(), generalFont));
			table.addCell(cell);
			for(String h : header1){
				cell = new PdfPCell(new Phrase(String.valueOf(e1.getValue().get(h)), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(cell);
			}
		}
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		cellFinal.setBorder(0);
		return cellFinal;
	}
	
	@SuppressWarnings("unused")
	private PdfPCell buildRevenueTable(Map<String, Map<String, Map<String, Double>>> revenue, String currencySymbol){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = null;
		table = new PdfPTable(new float[] {40, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		for (String head2 : header1) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(4);
			table.addCell(cell);
		}
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		for(String h : header1){
			for (String head2 : header3) {
				cell = new PdfPCell(new Phrase(head2, boldFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}
		}
		Double amt = 0.0;
		for(Map.Entry<String, Map<String, Map<String, Double>>> e1 : revenue.entrySet()){
			cell = new PdfPCell(new Phrase(e1.getKey(), generalFont));
			table.addCell(cell);
			for(String h : header1){
				Map<String, Double> map1 = e1.getValue().get(h);
				for (String h2 : header3) {
					amt = map1.get(h2) != null ? map1.get(h2) : 0.0;
					cell = new PdfPCell(new Phrase("$"+df.format(amt), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table.addCell(cell);
				}
			}
		}
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		cellFinal.setBorder(0);
		return cellFinal;
	}
	
	private PdfPCell buildRevenueByLoc(List<Object[]> revenueByLoc, String currencySymbol){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = null;
		table = new PdfPTable(new float[] {20, 40, 20, 20});
		header1 = Arrays.asList("Prepaid","Cash / Check","Total");
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		for (String head2 : header1) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		Double amt1 = 0.0;
		Double amt2 = 0.0;
		for(Object[] obj : revenueByLoc){
			cell = new PdfPCell(new Phrase(obj[0].toString(), generalFont));
			table.addCell(cell);
			amt1 = obj[1] != null ? Double.valueOf(obj[1].toString()) : 0.0;
			cell = new PdfPCell(new Phrase("$"+df.format(amt1), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			amt2 = obj[2] != null ? Double.valueOf(obj[2].toString()) : 0.0;
			cell = new PdfPCell(new Phrase("$"+df.format(amt2), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(amt1+amt2), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		cellFinal.setBorder(0);
		return cellFinal;
	}
	
	private PdfPCell buildDailySummaryTab(Map<String, Double> deposits, String currencySymbol){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = null;
		table = new PdfPTable(new float[] {70, 30});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		Double amt = 0.0;
		List<String> subCat = Arrays.asList("Daily Cash / Check Payments","Pre-paid");
		for(Map.Entry<String, Double> e1 : deposits.entrySet()){
			cell = new PdfPCell(new Phrase(e1.getKey().replace("NPrg", ""), generalFont));
			if(subCat.contains(e1.getKey().replace("NPrg", "")))
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			amt = e1.getValue() != null ? e1.getValue() : 0.0;
			cell = new PdfPCell(new Phrase("$"+df.format(amt), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		cellFinal.setBorder(0);
		return cellFinal;
	}
}
