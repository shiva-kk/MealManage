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
import java.util.stream.Collectors;

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
import com.mealManage.response.OrderCostInfo;

/**This Util class used for generate Order Cost report**/
@Component
public class OrderCostReportUtil {
	
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
    private Map<String, String> gradesKeyVal;
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
	
	/**This method used for generate the pdf of order cost report
	 * @throws Exception **/
	public void orderCostReportExport(List<OrderCostInfo> orderCostReports, HttpServletResponse response, 
			String startDate, String endDate,Long mealSchoolId, Map<String, Double> costByGrade, String currencySymbol, String dateFormat, String countryCode, Map<String, Long> countByGrade) throws Exception{
		String pdfFilePath = "OrderCostReport_"+mealSchoolId+".pdf";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(generateSchoolPdfReport(pdfFilePath, startDate, endDate));
    		//document.newPage();	
			gradesKeyVal = mealManageAPIDao.gradeMapByCountry(countryCode);
			
			/**Setting generally used header in Transaction report table**/
    		header = new ArrayList<String>(Arrays.asList("S.NO.","STUDENT", "GRADE", "DATE", "COST ("+currencySymbol+")"));
			
			document.add(createCostReportTable(orderCostReports, currencySymbol, dateFormat));
			if(costByGrade != null && costByGrade.size() > 0)
				document.add(createOrderCostSummaryTable(costByGrade, startDate, endDate,currencySymbol, countByGrade));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
		}catch (Exception e){
			logger.error("Error occurred during build order cost pdf report file due to  "+e.getMessage());
		}
	}
	
	/**This method used for create the first page pdf content**/
	private Element generateSchoolPdfReport(String pdfPath, String startDate, String endDate) throws Exception {
		PdfPTable mainTab = new PdfPTable(1);
		mainTab.setWidthPercentage(100);
		PdfPCell first = new PdfPCell();

		first = new PdfPCell(new Phrase("ORDER COST REPORT", generalDateBoldFont));
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
		
	/**This method used for build the Order Cost Report table**/
	private Element createCostReportTable(List<OrderCostInfo> orderCostInfos,String currencySymbol, String dateFormat) throws Exception {
		PdfPTable table = null;
		table = new PdfPTable(new float[] {30, 60, 35, 45, 40});
		PdfPCell cell;
		for (String head2 : header) {
			cell = new PdfPCell(new Phrase(head2, boldFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		String sno = null;
		Integer sNoVal = 1;
		OrderCostInfo orderCostInfo = null;
		Map<String, List<OrderCostInfo>> dataByGrade = orderCostInfos.stream().collect(Collectors.groupingBy(OrderCostInfo::getGrade));
		for(Map.Entry<String, String> entry : gradesKeyVal.entrySet()){
			if(dataByGrade.get(entry.getKey()) != null){
				List<OrderCostInfo> costInfos = dataByGrade.get(entry.getKey());
				for (int i = 0; i < costInfos.size(); i++) {
					sno = String.valueOf(sNoVal);
					cell = new PdfPCell(new Phrase(sno, generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					orderCostInfo = costInfos.get(i);
					/*cell = new PdfPCell(new Phrase(transactionsDetail.getIdNumb().toString(), generalFont));
					table.addCell(cell);*/
					cell = new PdfPCell(new Phrase(orderCostInfo.getLastName()+", "
							+orderCostInfo.getFirstName(), generalFont));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(gradesKeyVal.get(orderCostInfo.getGrade()), generalFont));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase((new SimpleDateFormat(dateFormat).format(orderCostInfo.getDate())), generalFont));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(df.format(orderCostInfo.getCost()), generalFont));
					table.addCell(cell);
					sNoVal++;
				}
			}
		}
				
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for build the order cost by grade table**/
	public Element createOrderCostSummaryTable(Map<String, Double> costByGrade, String startDate, 
			String endDate, String currencySymbol, Map<String, Long> countByGrade) throws Exception {
		header = new ArrayList<String>(Arrays.asList("S.NO.","GRADE","TOTAL COUNT","TOTAL COST ("+currencySymbol+")"));
		PdfPTable table = new PdfPTable(new float[] {20, 50, 50, 40});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("ORDER COST SUMMARY REPORT", generalDateBoldFont));
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
		Double grandTtlAmt = 0.0;
		Long grandTtlCount = (long) 0;
		for(Map.Entry<String, String> entry : gradesKeyVal.entrySet()){
			if (costByGrade.keySet().contains(entry.getKey())) {
				cell = new PdfPCell(new Phrase(String.valueOf(sno), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(gradesKeyVal.get(entry.getKey()), generalFont));
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(String.valueOf(countByGrade.get(entry.getKey())), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(df.format(costByGrade.get(entry.getKey())), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				grandTtlAmt = grandTtlAmt + costByGrade.get(entry.getKey());
				grandTtlCount = grandTtlCount + countByGrade.get(entry.getKey());
				sno++;
			}
		}
		if(grandTtlAmt > 0){
			cell = new PdfPCell(new Phrase("Grand Total:", boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setColspan(2);
			cell.setBorder(0);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(grandTtlCount), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(df.format(grandTtlAmt), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		return table;
	}
}
