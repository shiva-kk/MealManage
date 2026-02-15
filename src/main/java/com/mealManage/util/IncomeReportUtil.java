package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

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
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.response.IncomeResp;

/**This util class used for generate the transaction history report**/
@Component
public class IncomeReportUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font titleFont=FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
	public static final  Font boldFontSchoolName=FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
 	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
    private List<String> header1;
    private List<String> header2;
    private DecimalFormat df = new DecimalFormat("0.00");
   /**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void generateIncomeReport(List<IncomeResp> incomeResps, HttpServletResponse response, String itemType,
			String startDate, String endDate,Long districtId, String currencySymbol, String districtName) throws Exception{
		String pdfFilePath = "";
		pdfFilePath = "IncomeReport_"+itemType+"_"+districtId+".pdf";
		header1 = Arrays.asList("CASH / CHECK", "PREPAID", "CHARGED");
		header2 = Arrays.asList("Date", "Full Price", "Reduced Price", "Full Price", "Reduced Price", "Full Price", "Reduced Price", (itemType.toUpperCase()+" INCOME"), "Cash / Check", "Prepaid", "Charged", "Cash / Check", "Prepaid", "Charged", "OTHER INCOME",("TOTAL "+itemType.toUpperCase()+" INCOME"));
		Document document=new Document(PageSize.A4.rotate());// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(generateSchoolPdfReport(pdfFilePath, itemType, startDate, endDate, districtName));
			document.add(createIncomeTable(incomeResps, currencySymbol, itemType));
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
	private Element generateSchoolPdfReport(String pdfPath, String itemType, String startDate, String endDate, String districtName) throws Exception {
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
		first = new PdfPCell(new Phrase(("Daily "+itemType+" Income Report"), generalDateBoldFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_LEFT);
		first.setColspan(2);
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
		first.setColspan(2);
		mainTab.addCell(first);
		return mainTab;
	}
		
	/**This method used for build the report table**/
	private Element createIncomeTable(List<IncomeResp> incomeResps, String currencySymbol, String itemType) throws Exception {
		PdfPTable table = null;
		table = new PdfPTable(new float[]{20, 20, 30, 20, 30, 20, 30, 30, 25,25, 25, 25, 25,25,30, 40});
		PdfPCell cell;
		
		cell = new PdfPCell(new Phrase("", boldFont));
		cell.setRowspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("REIMBURSEMENT "+itemType.toUpperCase()+" INCOME ($)", titleFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setColspan(6);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("NON REIMBURSEMENT "+itemType.toUpperCase()+" INCOME ($)", titleFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setColspan(6);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", boldFont));
		cell.setRowspan(2);
		table.addCell(cell);
		for(String h : header1){
			cell = new PdfPCell(new Phrase(h, titleFont));
			cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("ADDITIONAL MEAL INCOME", boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(3);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("A LA CARTE INCOME", boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", boldFont));
		table.addCell(cell);
		for(String h : header2){
			cell = new PdfPCell(new Phrase(h, titleFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		Double lunchIncome = 0.0;
		Double totalLunchIncome = 0.0;
		Double otherIncome = 0.0;
		Double tcfp = 0.0, tcrp = 0.0,tppfp = 0.0, tpprp = 0.0, tcpfp = 0.0, tcprp = 0.0, tli = 0.0, tccAm=0.0, tppAm = 0.0, tcpAm = 0.0, tccAlc = 0.0, tppAlc = 0.0, tcpAlc = 0.0,toi = 0.0, ttli = 0.0;
		for(IncomeResp ir : incomeResps){
			lunchIncome = ir.getcFullPrice()+ir.getcRedPrice()+ir.getPpFullPrice()+ir.getPpRedPrice()+ir.getCpFullPrice()+ir.getCpRedPrice();
			cell = new PdfPCell(new Phrase(ir.getDate(), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getcFullPrice()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getcRedPrice()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getPpFullPrice()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getPpRedPrice()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getCpFullPrice()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getCpRedPrice()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(lunchIncome), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			otherIncome=ir.getCcAm()+ir.getPpAm()+ir.getCpAm()+ir.getCcAlc()+ir.getPpAlc()+ir.getCpAlc();
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getCcAm()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getPpAm()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getCpAm()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getCcAlc()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getPpAlc()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(ir.getCpAlc()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("$"+df.format(otherIncome), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			totalLunchIncome = lunchIncome+otherIncome;
			cell = new PdfPCell(new Phrase("$"+df.format(totalLunchIncome), boldFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			tcfp = tcfp+ir.getcFullPrice();
			tcrp = tcrp+ir.getcRedPrice();
			tppfp = tppfp+ir.getPpFullPrice();
			tpprp = tpprp+ir.getPpRedPrice();
			tcpfp = tcpfp+ir.getCpFullPrice();
			tcprp = tcprp+ir.getCpRedPrice();
			tli = tli+lunchIncome;
			toi=toi+otherIncome;
			tccAm = tccAm+ir.getCcAm();
			tppAm = tppAm+ir.getPpAm();
			tcpAm = tcpAm+ir.getCpAm();
			tccAlc = tccAlc+ir.getCcAlc();
			tppAlc = tppAlc+ir.getPpAlc();
			tcpAlc = tcpAlc+ir.getCpAlc();
			ttli = ttli+totalLunchIncome;
			
		}
		cell = new PdfPCell(new Phrase("TOTAL", boldFont));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tcfp), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tcrp), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tppfp), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tpprp), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tcpfp), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tcprp), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tli), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tccAm), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tppAm), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tcpAm), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tccAlc), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tppAlc), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(tcpAlc), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(toi), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("$"+df.format(ttli), boldFont));
		cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		
		table.setWidthPercentage(100);
		return table;
	}
}
