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
import com.mealManage.response.EditCheckResp;
import com.mealManage.response.ServiceResponse;

@Component
public class DistrictEditCheckUtil {
	
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
	public ServiceResponse audiDailyChkReportGeneration(HttpServletResponse response, String itemType, 
			String yearMonth, Integer schoolYear, List<EditCheckResp> resps, Long districtId) throws Exception{
		String pdfFilePath = "EditCheckReport_"+districtId+".pdf";
		ServiceResponse serviceResponse = new ServiceResponse();
		Document document=new Document(PageSize.A4.rotate());// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
			document.open();
			int i = 0;
			for(EditCheckResp resp : resps){
				if(i > 0)
					document.newPage();
				document.add(addPageHeader(resp.getSchoolName(), yearMonth, itemType, resp.getAttendanceFactor()));
				document.add(createDailyEditChkTable(resp.getAttendanceFactor(), resp.getStdCountByEligAndDt(), yearMonth, 
						resp.getSchoolHolidays(), resp.getSchoolTimezone(), schoolYear,resp.getMealSchoolId()));
				i++;
			}
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
	private PdfPTable addPageHeader(String schoolName, String yearMonth, String itemType, Double af) throws ParseException{
		PdfPTable table = new PdfPTable(new float[]{45,180,30,120,35,30,45,90,30, 30, 35, 35});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase(itemType.toUpperCase(), boldFontHeader));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(12);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("DAILY EDIT CHECK WORKSHEET", boldFontHeader));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(12);
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
		cell = new PdfPCell(new Phrase("AF: ", boldFontHeader1));
		cell.setBorder(0);
		cell.setPaddingTop(12);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(String.valueOf(af)+"%", generalDateFont));
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
		PdfPTable table = new PdfPTable(15);
		PdfPCell cell = null;
		DecimalFormat df = new DecimalFormat("###0");
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(11);
		cell.setColspan(15);
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
    	Integer totalStudents;
    	Integer grandTotalMealServed = 0;
    	Integer totalFMServ = 0;
    	Integer totalRMServ = 0;
    	Integer totalPMServ = 0;
    	//Integer grandTotalStudents = 0;
    	Integer stdCount = 0;
    	Integer mealServed = 0;
    	Integer freeStdCount = 0;
		Integer reducedStdCount = 0;
		Integer regularStdCount = 0;
    	while (day <= daysInMonth) {
    		totalMealServed = 0;
    		totalStudents = 0;
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
							mealServed = stdCountByEligAndDt.get(day) != null && stdCountByEligAndDt.get(day).get(0) != null ? stdCountByEligAndDt.get(day).get(0) : 0;
							totalFMServ =  totalFMServ+mealServed; 
							break;
					case 1: stdCount = reducedStdCount;
							mealServed = stdCountByEligAndDt.get(day) != null && stdCountByEligAndDt.get(day).get(1) != null ? stdCountByEligAndDt.get(day).get(1) : 0; 
							totalRMServ = totalRMServ+mealServed;
							break;
					case 2: stdCount = regularStdCount;
							mealServed = stdCountByEligAndDt.get(day) != null && stdCountByEligAndDt.get(day).get(2) != null ? stdCountByEligAndDt.get(day).get(2) : 0; 
							totalPMServ = totalPMServ+mealServed;
							break;
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
						case 1: cellVal = startCellVal+" Eligible with AF:"; break;
						case 2: cellVal = startCellVal+" Meals Served"; break;
						case 3: cellVal = "% of participation"; break;
						}
					}else{
						String totalEligStd = df.format(stdCount*attendanceFactor/100);
						switch(k){
						case 0: cellVal = isHoliday ? "": String.valueOf(stdCount); break;
						case 1: cellVal = isHoliday ? "0": totalEligStd; break;
						case 2: cellVal = mealServed == 0 ? "" : String.valueOf(mealServed); break;
						case 3: cellVal = stdCount == 0 ? "" : df.format((mealServed*100)/Integer.valueOf(totalEligStd)); break;
						}
					}
					cell = new PdfPCell(new Phrase(cellVal, generalFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table.addCell(cell);
				}
				totalMealServed = totalMealServed+mealServed;
				totalStudents = totalStudents+(isHoliday?0:stdCount);
			}
			if(day == -1)
				cellVal = "";			
			else if(day == 0)
				cellVal = "Total Served Counts:";
			else
				cellVal = String.valueOf(totalMealServed);
			cell = new PdfPCell(new Phrase(cellVal, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			if(day == -1)
				cellVal = "";			
			else if(day == 0)
				cellVal = "Total Students Counts:";
			else
				cellVal = String.valueOf(totalStudents);
			cell = new PdfPCell(new Phrase(cellVal, generalFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			grandTotalMealServed = grandTotalMealServed+totalMealServed;
			//grandTotalStudents = grandTotalStudents+totalStudents;
			day++;
    	}
    	cell = new PdfPCell(new Phrase("Monthly Total:", generalDateFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(String.valueOf(totalFMServ), generalDateFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(String.valueOf(totalRMServ), generalDateFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(String.valueOf(totalPMServ), generalDateFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(String.valueOf(grandTotalMealServed), generalDateFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(0);
		table.addCell(cell);
    	table.setWidthPercentage(100);
		return table;
	}
	
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
