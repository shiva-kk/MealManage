package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.user.StdCountByElig;
import com.mealManage.response.ServiceResponse;

@Component
public class DailyAuditCheck {
	
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private DateUtilityV2 du;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 9);
	public static final  Font boldFontHeader=FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
	public static final  Font boldFontHeader1=FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
	
	/**This method used for generate the pdf of daily audit check report
	 * @throws Exception **/
	public ServiceResponse audiDailyChkReportGeneration(HttpServletResponse response, Long mealSchoolId, String schoolName, 
			double attendanceFactor, String itemType, Map<Integer, Map<Integer, Integer>> stdCountByEligAndDt, 
			String yearMonth,	List<Integer> schoolHolidays, String schoolTimezone, Integer schoolYear) throws Exception{
		String pdfFilePath = "DailyAuditCheck_"+mealSchoolId+".pdf";
		ServiceResponse serviceResponse = new ServiceResponse();
		Document document=new Document(PageSize.A4.rotate());// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			document.add(addPageHeader(schoolName, yearMonth, itemType));
			document.add(createDailyEditChkTable(attendanceFactor, stdCountByEligAndDt, yearMonth, schoolHolidays, schoolTimezone, schoolYear,mealSchoolId));
			document.close();    
			InputStream myStream = new FileInputStream(pdfFilePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(pdfFilePath).delete();
			serviceResponse.setStatus("Success");
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Daily edit check report generated successfully.");
		}catch (Exception e){
			logger.error("Failed to export daily edit check report due to "+e.getMessage());
			serviceResponse.setStatus("Failed");
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to generate the daily edit check report.");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}
	
	/**This method used for build page header
	 * @throws ParseException ***/
	private PdfPTable addPageHeader(String schoolName, String yearMonth, String itemType) throws ParseException{
		PdfPTable table = new PdfPTable(new float[]{45,180,30,120,35,30,45,90,30});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase(itemType.toUpperCase()+" DAILY EDIT CHECK WORKSHEET", boldFontHeader));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(9);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("School: ", boldFontHeader1));
		cell.setBorder(0);
		cell.setPaddingTop(12);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(schoolName, generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setPaddingTop(12);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(0);
		cell.setPaddingTop(12);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Enrollment (Membership): ", boldFontHeader1));
		cell.setBorder(0);
		cell.setPaddingTop(12);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setPaddingTop(12);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(0);
		cell.setPaddingTop(12);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Month: ", boldFontHeader1));
		cell.setBorder(0);
		cell.setPaddingTop(12);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(Month.of(Integer.parseInt(yearMonth.substring(4))).name()+" "+
				yearMonth.substring(0,4), generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setPaddingTop(12);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(0);
		cell.setPaddingTop(12);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for create daily edit check table data**/
	private PdfPTable createDailyEditChkTable(double attendanceFactor, Map<Integer, Map<Integer, Integer>> stdCountByEligAndDt, 
			String yearMonth, List<Integer> schoolHolidays, String schoolTimezone, Integer schoolYear, Long mealSchoolId){
		PdfPTable table = new PdfPTable(14);
		PdfPCell cell = null;
		DecimalFormat df = new DecimalFormat("###0");
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(11);
		cell.setColspan(14);
		table.addCell(cell);
		String cellVal = "";
		int yearVal = Integer.parseInt(yearMonth.substring(0,4));
    	int monthVal = Integer.parseInt(yearMonth.substring(4))-1;
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(yearVal, monthVal, 01);
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	int day = -1;
    	boolean isHoliday = false;
    	int currentDay = 32;
    	String currentDate = du.formatDateToString(new Date(), "yyyyMMdd", schoolTimezone);
    	if(Integer.parseInt(yearMonth) >= Integer.parseInt(currentDate.substring(0, 6))){
    		currentDay = Integer.parseInt(currentDate.substring(6));
    	}
    	Integer totalMealServed;
    	Integer grandTotalMealServed = 0;
    	Integer stdCount = 0;
    	Integer mealServed = 0;
    	Integer freeStdCount = 0;
		Integer reducedStdCount = 0;
		Integer regularStdCount = 0;
    	while (day <= daysInMonth) {
    		totalMealServed = 0;
    		if(day >= 1){
    			calendar = new GregorianCalendar(yearVal, monthVal, day);
        	    if(isSunday(calendar) || isSpecialDay(calendar) || schoolHolidays.contains(day) || day > currentDay)
        	    	isHoliday = true;
        	    else
        	    	isHoliday = false;
        	    List<StdCountByElig> countByElg = studentUserRepository.countByElg((yearVal+"-"+(monthVal+1)+"-"+day+" 18:00:00"), mealSchoolId, schoolYear);
    			for(StdCountByElig stdCountByElig : countByElg){
    				switch (stdCountByElig.getElgStatus()) {
    					case 0:	freeStdCount = stdCountByElig.getCountVal(); break;
    					case 1:	reducedStdCount = stdCountByElig.getCountVal(); break;
    					case 2:	regularStdCount = stdCountByElig.getCountVal(); break;
    				}
    			}
    		}   	    
    		if(day == -1)
				cellVal = "";			
			else if(day == 0)
				cellVal = "Day of Month:";
			else
				cellVal = String.valueOf(day);
			cell = new PdfPCell(new Phrase(cellVal, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			for(int j=0; j <3; j++){
				//cell = buildChildTable(attendanceFactor, eligStdCount, mealServedCount, j, day, isWeekend);
				String startCellVal = "";
				if(day == 0){
					switch(j){
					case 0: startCellVal = "Free"; break;
					case 1: startCellVal = "Reduced"; break;
					case 2: startCellVal = "Paid"; break;
					}
				}else{
					switch(j){
					case 0: stdCount = freeStdCount; 
							mealServed = stdCountByEligAndDt.get(day) != null && stdCountByEligAndDt.get(day).get(0) != null ? stdCountByEligAndDt.get(day).get(0) : 0; break;
					case 1: stdCount = reducedStdCount;
							mealServed = stdCountByEligAndDt.get(day) != null && stdCountByEligAndDt.get(day).get(1) != null ? stdCountByEligAndDt.get(day).get(1) : 0; break;
					case 2: stdCount = regularStdCount;
							mealServed = stdCountByEligAndDt.get(day) != null && stdCountByEligAndDt.get(day).get(2) != null ? stdCountByEligAndDt.get(day).get(2) : 0; break;
					}
				}
				for(int k=0; k<4; k++){
					if(day == -1){
						switch(k){
							case 0: cellVal = "A"; break;
							case 1: cellVal = "B"; break;
							case 2: cellVal = "C"; break;
							case 3: cellVal = "D"; break;
						}
					}else if(day == 0){
						switch(k){
						case 0: cellVal = startCellVal+" Eligible"; break;
						case 1: cellVal = "AF"; break;
						case 2: cellVal = startCellVal+" Eligible X AF:"; break;
						case 3: cellVal = startCellVal+" Meals Served"; break;
						}
					}else{
						switch(k){
						case 0: cellVal = isHoliday ? "": String.valueOf(stdCount); break;
						case 1: cellVal = String.valueOf(attendanceFactor)+"%"; break;
						case 2: cellVal = isHoliday ? "0": String.valueOf(df.format(stdCount*attendanceFactor/100)); break;
						case 3: cellVal = mealServed == 0 ? "" : String.valueOf(mealServed); break;
						}
					}
					cell = new PdfPCell(new Phrase(cellVal, generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
				}
				totalMealServed = totalMealServed+mealServed;
			}
			if(day == -1)
				cellVal = "";			
			else if(day == 0)
				cellVal = "TOTAL COUNTS:";
			else
				cellVal = String.valueOf(totalMealServed);
			grandTotalMealServed = grandTotalMealServed+totalMealServed;
			cell = new PdfPCell(new Phrase(cellVal, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			day++;
    	}
    	cell = new PdfPCell(new Phrase("Monthly Total:", generalDateFont));
		cell.setBorder(0);
		cell.setColspan(13);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(String.valueOf(grandTotalMealServed), generalDateFont));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
    	table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for child table build**/
	/*private PdfPCell buildChildTable(double attendanceFactor, Integer eligStdCount, Integer mealServedCount, int j, int day, boolean isWeekend){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable table = new PdfPTable(4);
		PdfPCell cell;
		String cellVal = "";
		String startCellVal = "";
		if(day == 0){
			switch(j){
			case 0: startCellVal = "Free"; break;
			case 1: startCellVal = "Reduced"; break;
			case 2: startCellVal = "Paid"; break;
			}
		}		
		for(int k=0; k<4; k++){
			if(day == -1){
				switch(k){
					case 0: cellVal = "A"; break;
					case 1: cellVal = "B"; break;
					case 2: cellVal = "C"; break;
					case 3: cellVal = "D"; break;
				}
			}else if(day == 0){
				switch(k){
				case 0: cellVal = startCellVal+" Eligible"; break;
				case 1: cellVal = "AF"; break;
				case 2: cellVal = startCellVal+" Eligible X AF:"; break;
				case 3: cellVal = startCellVal+" Meals Served"; break;
				}
			}
			cell = new PdfPCell(new Phrase(cellVal, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
		}
		table.setWidthPercentage(100);
		cellFinal.addElement(table);
		//cellFinal.setBorder(0);
		return cellFinal;
	}*/
	
	/**
	 * Returns true for Sundays.
	 * @param calendar a date
	 * @return true for Sundays
	 */
	public boolean isSunday(Calendar calendar) {
	    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 
	        return true;
	    return false;
	}

	/**
	 * Returns true if the date was found in a list with special days (holidays).
	 * @param calendar a date
	 * @return true for holidays
	 */
	public boolean isSpecialDay(Calendar calendar) {
	    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
	        return true;
	    return false;
	}
}
