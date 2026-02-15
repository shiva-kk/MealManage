package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.mealmodel.reimbursement.ReimbursementRatesInfo;
import com.mealManage.mealmodel.reimbursement.ReimbursementType;
import com.mealManage.response.ServiceResponse;

@Component
public class DistAuditReportUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 9);
	public static final  Font generalFont1=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 11);
	public static final  Font boldFontHeader=FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
	public static final  Font boldFontHeader1=FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
    private List<String> header;
    private final DecimalFormat df = new DecimalFormat("##0.0000");
	private final DecimalFormat df1 = new DecimalFormat("##0.00");
	
	/**This method used for generate the pdf of audit reimbursement report
	 * @throws Exception **/
	public ServiceResponse audiReportGeneration(HttpServletResponse response, String startDate, String endDate, 
			Long districtId, String districtName, Map<String, List<ReimbursementRatesInfo>> reimbByType, 
			Map<String, Map<String, Map<String, String>>> auditByType, Map<String, Integer> needyStdCountMap, String currencySymbol, Integer noOfSchool) throws Exception{
		String pdfFilePath = "BasicClaim_"+districtId+".pdf";
		if(startDate.equalsIgnoreCase(endDate))
			endDate = null;
		ServiceResponse serviceResponse = new ServiceResponse();
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(addPageHeader(startDate, endDate, districtName, noOfSchool));
			document.add(createAuditSummaryTable(auditByType));
			document.add(createAuditReimbursementTable(reimbByType, auditByType, needyStdCountMap, currencySymbol));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Audit report generated successfully.");
		}catch (Exception e){
			logger.error("Failed to export audit report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the audit report.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for build page header
	 * @throws ParseException ***/
	private PdfPTable addPageHeader(String startDate, String endDate, String districtName, Integer noOfSchool) throws ParseException{
		PdfPTable table = new PdfPTable(2);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM dd, yyyy");
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("Summarized Reimbursement Claim", boldFontHeader));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(2);
		table.addCell(cell);
		/*cell = new PdfPCell(new Phrase(districtName, boldFontHeader1));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell);*/
		if(endDate == null || endDate.equalsIgnoreCase(""))
			cell = new PdfPCell(new Phrase(sdf1.format(sdf.parse(startDate)), generalDateFont));
		else
			cell = new PdfPCell(new Phrase(sdf1.format(sdf.parse(startDate))+" through "+sdf1.format(sdf.parse(endDate)), generalDateFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(2);
		table.addCell(cell);
		boldFontHeader1.setColor(BaseColor.PINK);
		cell = new PdfPCell(new Phrase("SOME VALUES ON THIS REPORT ARE DERIVED FROM FORCED BENEFITS, WHICH COULD "
				+ "RESULT IN AN OVERCLAIM.", boldFontHeader1));
		cell.setBorder(0);
		//cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPaddingTop(17);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("School District: "+districtName, boldFont));
		//cell.setBackgroundColor(BaseColor.GRAY);
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Total No Of Schools: "+noOfSchool, boldFont));	
		//cell.setBackgroundColor(BaseColor.GRAY);
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
		
	/**This method used for build the audit report summary first table**/
	private Element createAuditSummaryTable(Map<String, Map<String, Map<String, String>>> auditByType) throws Exception {
		header = new ArrayList<String>(Arrays.asList("Paid Students","Reduced Students","Free Students","Paid Adults","Serving Days","Total Students","Extended Free"));
		PdfPTable table = new PdfPTable(new float[] {60,35,3,35,3,35,3,35,3,35,3,35,3,35});
		PdfPCell cell;
		List<String> itemTypes = Arrays.asList("Lunch", "Breakfast", "Milk");
		for(String type : itemTypes){
			Map<String, Map<String, String>> auditMap = auditByType.get(type);
			if(auditMap.size() > 0){
				cell = new PdfPCell(new Phrase(type+" Claim", boldFont));
				cell.setBorder(0);
				cell.setPaddingTop(10);
				table.addCell(cell);
				
				for(String head : header){
					cell = new PdfPCell(new Phrase(head, generalFont));	
					cell.setPaddingTop(10);
					cell.setBorder(Rectangle.BOTTOM);
					if(head.equalsIgnoreCase("Paid Adults") || head.equalsIgnoreCase("Total Students"))
						cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					table.addCell(defaultCell());
				}
				cell = defaultCell();
				cell.setColspan(14);
				cell.setPaddingTop(4);
				table.addCell(cell);
				for(Map.Entry<String, Map<String, String>> entry: auditMap.entrySet()){
					cell = new PdfPCell(new Phrase(entry.getKey(), generalFont));	
					cell.setBorder(0);
					cell.setPaddingLeft(10);
					table.addCell(cell);
					for(Map.Entry<String, String> auditDetails : entry.getValue().entrySet()){
						switch(auditDetails.getKey()){
							case "paidStudents" : 
								cell = buildCell(auditDetails.getValue());
								table.addCell(cell); table.addCell(defaultCell()); break;
							case "reducedStudents" : 
								cell = buildCell(auditDetails.getValue());
								table.addCell(cell); table.addCell(defaultCell()); break;
							case "freeStudents" : 
								cell = buildCell(auditDetails.getValue());
								table.addCell(cell); table.addCell(defaultCell()); break;
							case "paidAdults" : 
								cell = buildCell(auditDetails.getValue());
								table.addCell(cell); table.addCell(defaultCell()); break;
							case "servingDays" : 
								cell = buildCell(auditDetails.getValue());
								table.addCell(cell); table.addCell(defaultCell()); break;
							case "attendance" : 
								cell = buildCell(auditDetails.getValue());
								table.addCell(cell); table.addCell(defaultCell()); break;
							case "extendedFree" : 
								cell = buildCell(auditDetails.getValue());
								table.addCell(cell); break;
						}				
					}
				}
			}
			
		}
		cell = new PdfPCell(new Phrase("= possible overclaim", generalFont));	
		cell.setPaddingLeft(400);
		cell.setColspan(14);
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Amounts shown were computed using current reimbursement rates.", generalFont1));	
		cell.setColspan(14);
		cell.setBorder(0);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for audit reimbursement table creation**/
	private PdfPTable createAuditReimbursementTable(Map<String, List<ReimbursementRatesInfo>> reimbByType, 
			Map<String, Map<String, Map<String, String>>> auditByType, Map<String, Integer> needyStdCountMap, String currencySymbol){
		PdfPTable table = new PdfPTable(new float[]{70, 80, 7, 80});
		PdfPCell cell;
		table.addCell(defaultCell());
		cell = new PdfPCell(new Phrase("Federal Reimbursement", generalFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		table.addCell(defaultCell());
		cell = new PdfPCell(new Phrase("State Reimbursement", generalFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		List<String> childObj = new LinkedList<>(Arrays.asList("Lunch","Breakfast","Milk","Needy"));
		List<String> childDetailsObj = new LinkedList<>(Arrays.asList("Regular Servings","Reduced Servings","Free Servings", "Total Servings"));
		String fieldVal = "0";
		double otherVal = 0;
		String fedTotal = "0.00";
		String stateTotal = "0.00";
		List<ReimbursementRatesInfo> reimbursementRatesInfos = null;
		ReimbursementRatesInfo needyReimbRate = new ReimbursementRatesInfo();
		ReimbursementRatesInfo regReimbRate = null;
		for(String obj : childObj){
			regReimbRate = new ReimbursementRatesInfo();
			reimbursementRatesInfos = reimbByType.get(obj);
			if(reimbursementRatesInfos != null)
				for(ReimbursementRatesInfo reimbursementRatesInfo : reimbursementRatesInfos){
					if(reimbursementRatesInfo.getReimbursementType().toString().equalsIgnoreCase(ReimbursementType.Regular.toString()))
						regReimbRate = reimbursementRatesInfo;
					if(obj.contains("Lunch") && reimbursementRatesInfo.getReimbursementType().toString().equalsIgnoreCase(ReimbursementType.Needy.toString()))
						needyReimbRate = reimbursementRatesInfo;	
				}
			Map<String, Map<String, String>> auditMap = auditByType.get(obj);
			if(obj.contains("Needy") || auditMap.size() > 0){
				cell = new PdfPCell(new Phrase(obj.contains("Needy")?(obj+ " (marked with * above)"):(obj+" Regular"), generalFont));
				cell.setColspan(4);
				cell.setBorder(0);
				table.addCell(cell);
				for(String childDetails : childDetailsObj){
					cell = new PdfPCell(new Phrase(childDetails, generalFont));
					cell.setBorder(0);
					cell.setPaddingLeft(10);
					table.addCell(cell);
					switch(childDetails){
						case "Regular Servings": 
							fieldVal = !obj.contains("Needy") ? getStdCountVal(auditMap, needyStdCountMap,"paidStudents") : String.valueOf(needyStdCountMap.get("paidStudents"));
							if(fieldVal == null)
								fieldVal = "0";
							otherVal = !obj.contains("Needy") ? getExactValue(regReimbRate.getTotFedReimbRate()) : getExactValue(needyReimbRate.getTotFedReimbRate());
							fedTotal = df1.format(Float.parseFloat(fedTotal)+(Integer.parseInt(fieldVal)*otherVal));
							table.addCell(buildReimbCell(fieldVal, otherVal, currencySymbol));
							table.addCell(defaultCell()); 
							otherVal = !obj.contains("Needy") ? getExactValue(regReimbRate.getTotStateReimbRate()) : getExactValue(needyReimbRate.getTotStateReimbRate());
							stateTotal = df1.format(Float.parseFloat(stateTotal)+(Integer.parseInt(fieldVal)*otherVal));
							table.addCell(buildReimbCell(fieldVal, otherVal, currencySymbol)); break;
						case "Reduced Servings": 
							fieldVal = !obj.contains("Needy") ? getStdCountVal(auditMap, needyStdCountMap,"reducedStudents") : String.valueOf(needyStdCountMap.get("reducedStudents"));
							if(fieldVal == null)
								fieldVal = "0";
							otherVal = !obj.contains("Needy") ? getExactValue(regReimbRate.getRedFedReimbRate()) : getExactValue(needyReimbRate.getRedFedReimbRate());
							fedTotal = df1.format(Float.parseFloat(fedTotal)+(Integer.parseInt(fieldVal)*otherVal));
							table.addCell(buildReimbCell(fieldVal, otherVal, currencySymbol));
							table.addCell(defaultCell()); 
							otherVal = !obj.contains("Needy") ? getExactValue(regReimbRate.getRedStateReimbRate()) : getExactValue(needyReimbRate.getRedStateReimbRate());
							stateTotal = df1.format(Float.parseFloat(stateTotal)+(Integer.parseInt(fieldVal)*otherVal));
							table.addCell(buildReimbCell(fieldVal, otherVal, currencySymbol)); break;
						case "Free Servings": 
							fieldVal = !obj.contains("Needy") ? getStdCountVal(auditMap, needyStdCountMap,"freeStudents") : String.valueOf(needyStdCountMap.get("freeStudents"));
							otherVal = !obj.contains("Needy") ? getExactValue(regReimbRate.getFreFedReimbRate()) : getExactValue(needyReimbRate.getFreFedReimbRate());
							fedTotal = df1.format(Float.parseFloat(fedTotal)+(Integer.parseInt(fieldVal)*otherVal));
							table.addCell(buildReimbCell(fieldVal, otherVal, currencySymbol));
							table.addCell(defaultCell()); 
							otherVal = !obj.contains("Needy") ? getExactValue(regReimbRate.getFreStateReimbRate()) : getExactValue(needyReimbRate.getFreStateReimbRate());
							stateTotal = df1.format(Float.parseFloat(stateTotal)+(Integer.parseInt(fieldVal)*otherVal));
							table.addCell(buildReimbCell(fieldVal, otherVal, currencySymbol)); break;
						case "Total Servings": 
							fieldVal = !obj.contains("Needy") ? getStdCountVal(auditMap, needyStdCountMap,"totalStudents") : String.valueOf(needyStdCountMap.get("totalStudents"));
							table.addCell(buildReimbTotCell(fieldVal));
							table.addCell(defaultCell()); 
							table.addCell(buildReimbTotCell(fieldVal));break;
					}
				}
			}
			
		}
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		table.addCell(cell);
		table.addCell(totalCellCreate(currencySymbol+fedTotal));
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		table.addCell(cell);
		table.addCell(totalCellCreate(currencySymbol+stateTotal));
		table.setWidthPercentage(100);
		return table;
	}
	
	private PdfPCell buildCell(String textVal){
		PdfPCell cell = new PdfPCell(new Phrase(textVal, generalFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		return cell;
	}
	
	private PdfPCell defaultCell(){
		PdfPCell cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		return cell;
	}
	
	private PdfPCell buildReimbCell(String fieldVal, double otherVal, String currencySymbol){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = new PdfPTable(new float[]{35,15,70,15,80});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase(fieldVal, generalFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("*", generalFont));
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(currencySymbol+df.format(otherVal), generalFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("=", generalFont));
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(currencySymbol+df1.format(Integer.parseInt(fieldVal)*otherVal), generalFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		cellFinal.setBorder(0);
		return cellFinal;
	}
	
	private PdfPCell buildReimbTotCell(String fieldVal){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = new PdfPTable(new float[]{35,15,70,15,80});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase(fieldVal, generalFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setColspan(4);
		table.addCell(cell);
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		cellFinal.setBorder(0);
		return cellFinal;
	}
	
	private PdfPCell totalCellCreate(String totalVal){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = new PdfPTable(new float[]{35,15,70,15,80});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setColspan(4);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(totalVal, generalFont));
		cell.setBorder(Rectangle.TOP);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		cellFinal.setBorder(0);
		return cellFinal;
	}
	
	private double getExactValue(BigDecimal bigDecimal){
		if(bigDecimal == null)
			return 0.0000;
		else
			return bigDecimal.doubleValue();
	}
	
	private String getStdCountVal(Map<String, Map<String, String>> auditMap, Map<String, Integer> needyStdCountMap, String type){
		return String.valueOf(Integer.parseInt(auditMap.get("Meals Served").get(type)) - 
				needyStdCountMap.get(type));
	}
}
