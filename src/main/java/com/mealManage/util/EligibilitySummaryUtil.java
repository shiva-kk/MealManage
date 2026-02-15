package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.response.EligSummaryResp;
import com.mealManage.response.ServiceResponse;

@Component
/**This class used for generate eligibility summary report**/
public class EligibilitySummaryUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
 	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	@Autowired
	private DateUtilityV2 du;
 	private static DecimalFormat df = new DecimalFormat("##,##,###");
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 9);
	public static final  Font boldFontHeader=FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
	public static final  Font boldFontHeader2=FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLD);
	public static final  Font boldFontHeader1=FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
	
	/**This method used for generate the pdf of eligibility summary report
	 * @throws Exception **/
	public ServiceResponse eligSummaryReport(HttpServletResponse response, Long districtId, String districtName, 
			String timezone, String loggedUser, List<EligSummaryResp> eligSummaryResps) throws Exception{
		String pdfFilePath = "EligibilitySummary_"+districtId+".pdf";
		ServiceResponse serviceResponse = new ServiceResponse();
		Document document=new Document(PageSize.A4.rotate());// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(addPageHeader(districtName, timezone, loggedUser));
			document.add(createSummaryTable(eligSummaryResps));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Eligibility Summary report generated successfully.");
		}catch (Exception e){
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate Eligibility Summary report.");
			serviceResponse.setErrorMessage(e.getMessage());
			logger.error(serviceResponse.getStatusMessage()+" due to "+e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for build page header
	 * @throws ParseException 
	 * @throws Exception 
	 * @throws MalformedURLException 
	 * @throws BadElementException ***/
	private PdfPTable addPageHeader(String districtName, String timezone, String loggedUser) throws ParseException, BadElementException, MalformedURLException, Exception{
		PdfPTable table = new PdfPTable(new float[]{30, 40, 30});
		PdfPCell cell = new PdfPCell();
		cell.addElement(addLeftHeader());
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell();
		cell.addElement(addCenterHeader(districtName));
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell();
		cell.addElement(addRightHeader(timezone, loggedUser));
		cell.setBorder(0);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for create Eligibility Summary table data**/
	private PdfPTable createSummaryTable(List<EligSummaryResp> eligSummaryResps){
		PdfPTable table = new PdfPTable(new float[]{36, 12, 12, 12, 10, 10, 11, 11, 12, 10, 12, 12, 16, 13, 13, 15, 15, 11, 14});
		PdfPCell cell = null;
		//DecimalFormat df = new DecimalFormat("###0");
		cell = new PdfPCell(new Phrase("", boldFontHeader1));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(19);
		cell.setBackgroundColor(WebColors.getRGBColor("#797979"));
		table.addCell(cell);
		String[] headers = {"Site","Income Free","Income Red.","Income Paid","Temp Free","Temp Red.","SNAP",
				"TANF","Foster Child","Inst","FDPIR","Hd/Ev Start","Homeless","Migrant","Runaway","Medicaid Free",
				"Medicaid Reduced","Direct Cert.","Custom Case"};
		for(String head : headers){
			cell = new PdfPCell(new Phrase(head, boldFontHeader1));
			cell.setHorizontalAlignment(Element.ALIGN_BOTTOM);
			cell.setBorder(Rectangle.BOTTOM);
			cell.setBackgroundColor(WebColors.getRGBColor("#CECECE"));
			table.addCell(cell);
		}
		EligSummaryResp el = null;
		Boolean isEven = false;
		Integer t1=0,t2=0,t3=0,t4=0,t5=0,t6=0,t7=0,t8=0,t9=0,t10=0,t11=0,t12=0,t13=0,t14=0,t15=0,t16=0,t17=0,t18 = 0;
		Integer tf=0,tr=0,tp=0,ti=0;
		for(int i=1; i<=eligSummaryResps.size(); i++){
			el = eligSummaryResps.get(i-1);
			if(i % 2 == 0)
				isEven = true;
			else
				isEven = false;
			for(int j=0; j<19; j++){
				switch (j) {
				case 0: 
					cell = new PdfPCell(new Phrase(el.getSchoolName(), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					table.addCell(cell);
					break;
				case 1: 
					cell = new PdfPCell(new Phrase(df.format(el.getIncomeFree()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t1 = t1+el.getIncomeFree();
					table.addCell(cell);
					break;
				case 2: 
					cell = new PdfPCell(new Phrase(df.format(el.getIncomeRed()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t2=t2+el.getIncomeRed();
					table.addCell(cell);
					break;
				case 3: 
					cell = new PdfPCell(new Phrase(df.format(el.getIncomePaid()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t3=t3+el.getIncomePaid();
					table.addCell(cell);
					break;
				case 4: 
					cell = new PdfPCell(new Phrase(df.format(el.getTempFree()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t4=t4+el.getTempFree();
					table.addCell(cell);
					break;
				case 5: 
					cell = new PdfPCell(new Phrase(df.format(el.getTempRed()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t5=t5+el.getTempRed();
					table.addCell(cell);
					break;
				case 6: 
					cell = new PdfPCell(new Phrase(df.format(el.getSnap()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t6=t6+el.getSnap();
					table.addCell(cell);
					break;
				case 7: 
					cell = new PdfPCell(new Phrase(df.format(el.getTanf()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t7=t7+el.getTanf();
					table.addCell(cell);
					break;
				case 8: 
					cell = new PdfPCell(new Phrase(df.format(el.getFosterChild()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t8=t8+el.getFosterChild();
					table.addCell(cell);
					break;
				case 9: 
					cell = new PdfPCell(new Phrase(df.format(el.getInst()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t9=t9+el.getInst();
					table.addCell(cell);
					break;
				case 10: 
					cell = new PdfPCell(new Phrase(df.format(el.getFdpir()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t10=t10+el.getFdpir();
					table.addCell(cell);
					break;
				case 11: 
					cell = new PdfPCell(new Phrase(df.format(el.getHdStart()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t11=t11+el.getHdStart();
					table.addCell(cell);
					break;
				case 12: 
					cell = new PdfPCell(new Phrase(df.format(el.getHomeless()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t12=t12+el.getHomeless();
					table.addCell(cell);
					break;
				case 13: 
					cell = new PdfPCell(new Phrase(df.format(el.getMigrant()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t13=t13+el.getMigrant();
					table.addCell(cell);
					break;
				case 14: 
					cell = new PdfPCell(new Phrase(df.format(el.getRunway()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t14=t14+el.getRunway();
					table.addCell(cell);
					break;
				case 15: 
					cell = new PdfPCell(new Phrase(df.format(el.getMedicaidFree()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t15=t15+el.getMedicaidFree();
					table.addCell(cell);
					break;
				case 16: 
					cell = new PdfPCell(new Phrase(df.format(el.getMedicaidRed()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t16=t16+el.getMedicaidRed();
					table.addCell(cell);
					break;
				case 17: 
					cell = new PdfPCell(new Phrase(df.format(el.getDirectCert()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t17=t17+el.getDirectCert();
					table.addCell(cell);
					break;
				case 18: 
					cell = new PdfPCell(new Phrase(df.format(el.getCustomCase()), generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(isEven)
						cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
					cell.setBorder(0);
					t18=t18+el.getCustomCase();
					table.addCell(cell);
					break;
				}
			}
			cell = new PdfPCell(new Phrase("Total Free: "+df.format(el.getTotalFree()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setColspan(3);
			tf=tf+el.getTotalFree();
			cell.setBorder(0);
			if(isEven)
				cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Total Reduced: "+df.format(el.getTotalRed()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setColspan(5);
			tr=tr+el.getTotalRed();
			cell.setBorder(0);
			if(isEven)
				cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Total Paid: "+df.format(el.getTotalPaid()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setColspan(5);
			tp=tp+el.getTotalPaid();
			cell.setBorder(0);
			if(isEven)
				cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Total Inactive: "+df.format(el.getTotalInactive()), generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setColspan(4);
			ti=ti+el.getTotalInactive();
			cell.setBorder(0);
			if(isEven)
				cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("", generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setColspan(2);
			cell.setBorder(0);
			if(isEven)
				cell.setBackgroundColor(WebColors.getRGBColor("#D7D7D7"));
			table.addCell(cell);
		}
		for(int j=0; j<19; j++){
			switch (j) {
			case 0: 
				cell = new PdfPCell(new Phrase("Totals", boldFontHeader1));
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 1: 
				cell = new PdfPCell(new Phrase(df.format(t1), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 2: 
				cell = new PdfPCell(new Phrase(df.format(t2), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 3: 
				cell = new PdfPCell(new Phrase(df.format(t3), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 4: 
				cell = new PdfPCell(new Phrase(df.format(t4), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 5: 
				cell = new PdfPCell(new Phrase(df.format(t5), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 6: 
				cell = new PdfPCell(new Phrase(df.format(t6), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 7: 
				cell = new PdfPCell(new Phrase(df.format(t7), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 8: 
				cell = new PdfPCell(new Phrase(df.format(t8), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 9: 
				cell = new PdfPCell(new Phrase(df.format(t9), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 10: 
				cell = new PdfPCell(new Phrase(df.format(t10), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 11: 
				cell = new PdfPCell(new Phrase(df.format(t11), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 12: 
				cell = new PdfPCell(new Phrase(df.format(t12), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 13: 
				cell = new PdfPCell(new Phrase(df.format(t13), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 14: 
				cell = new PdfPCell(new Phrase(df.format(t14), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 15: 
				cell = new PdfPCell(new Phrase(df.format(t15), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 16: 
				cell = new PdfPCell(new Phrase(df.format(t16), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 17: 
				cell = new PdfPCell(new Phrase(df.format(t17), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			case 18: 
				cell = new PdfPCell(new Phrase(df.format(t18), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorder(Rectangle.TOP);
				table.addCell(cell);
				break;
			}
		}
		cell = new PdfPCell(new Phrase("Total Free: "+df.format(tf), generalFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setColspan(3);
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Total Reduced: "+df.format(tr), generalFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setColspan(5);
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Total Paid: "+df.format(tp), generalFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setColspan(5);
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Total Inactive: "+df.format(ti), generalFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setColspan(4);
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setColspan(2);
		cell.setBorder(0);
		table.addCell(cell);
    	table.setWidthPercentage(100);
		return table;
	}
	
	private PdfPTable addLeftHeader() throws ParseException, BadElementException, MalformedURLException, Exception{
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell;
		String logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";
		Image image = Image.getInstance(logoPath);
    	image.scaleAbsolute(30f, 30f);
    	image.setAlignment(Image.ALIGN_LEFT);
		//for first row
		cell = new PdfPCell();	   
		cell.addElement(image);
		cell.setBorder(0);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	private PdfPTable addCenterHeader(String districtName) throws ParseException, BadElementException, MalformedURLException, Exception{
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell;
		cell = new PdfPCell(new Phrase(districtName, boldFontHeader));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Eligibility Summary", boldFontHeader2));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	private PdfPTable addRightHeader(String timezone, String loggedUser) throws ParseException, BadElementException, MalformedURLException, Exception{
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("Printed: "+du.formatDateToString(new Date(), "MM/dd/yyyy hh:mm a", timezone), generalDateFont));
		cell.setBorder(0);
		cell.setPaddingTop(12);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Printed By: "+loggedUser, generalDateFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
}
