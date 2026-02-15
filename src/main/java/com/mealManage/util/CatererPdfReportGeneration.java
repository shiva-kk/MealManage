package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.school.SchoolGrades;

/** This utility class used for export the Reports in pdf file format **/
@Component
public class CatererPdfReportGeneration {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;	
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	public static final  Font dayFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private Map<String, Map<String, Integer>> mealsMap;
    private PdfPTable calendarTable = new PdfPTable(5);
    private PdfPTable cellTable = null;
    private PdfPCell cellVal = null;
    private Boolean isWeekStatus = false; 
    private Boolean isItemized = false;
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
	private static DateFormat formatter = new SimpleDateFormat("EEEE");
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	
	/**This method used for generate the pdf of caterer report
	 * @throws Exception **/
	public void exportCaterePdfReport(String schoolName, List<String> grades, Date startDate, HttpServletResponse response, 
			String logoLink, Map<String, Map<String, Integer>> mealsMap, Date endDate, String catererPdfFileName, Long mealSchoolId, 
			String catererEmail, String adminEmail, Boolean byItem, Boolean isItemized, ItemTypeConstants menuType, String countryCode, 
			String dateFormat,List<Integer> nonSchoolDays) throws Exception{
		this.mealsMap = mealsMap;
		isWeekStatus = false;
		this.isItemized = isItemized;
		checkReportIsByWeek(startDate, endDate);
		String pdfFilePath = "CatererReport_"+mealSchoolId+".pdf";
		if(catererPdfFileName != null && !catererPdfFileName.equalsIgnoreCase(""))
			pdfFilePath = catererPdfFileName;
		String logoPath = "";
		if(logoLink != null)
			logoPath = logoLink;
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();
			document.add(generateCatererPdfReport(pdfFilePath, logoPath, schoolName, grades, startDate, endDate, byItem, menuType, countryCode, dateFormat,nonSchoolDays));
			document.close();     
			if(catererEmail != null && !catererEmail.trim().isEmpty()){
	    		byte[] encodedBytes = Files.readAllBytes(Paths.get(pdfFilePath));
	    		byte[] encoded = Base64.encodeBase64(encodedBytes);
	    		String encodedString = new String(encoded);
	    		Map<String, String> paymentReceiptMap = new HashMap<String, String>();
	    		paymentReceiptMap.put("userEmails", catererEmail);
	    		paymentReceiptMap.put("base64Data", encodedString);
	    		paymentReceiptMap.put("fileName", pdfFilePath);
	    		paymentReceiptMap.put("schoolName", schoolName);
	    		paymentReceiptMap.put("adminEmail", adminEmail != null ? adminEmail : "");
	    		sendNotificationUtil.catererReportNotif(paymentReceiptMap);
	    	}else if(catererPdfFileName == null || catererPdfFileName.equalsIgnoreCase("") || response != null){
	    		InputStream myStream = new FileInputStream(pdfFilePath);
		    	response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
		    	IOUtils.copy(myStream, response.getOutputStream());
		    	response.flushBuffer();
		    	myStream.close();
	    	}
	    	new File(pdfFilePath).delete();
		  }catch (Exception e){
			logger.error("Error occurred during build pdf file due to "+e.getMessage());
		}
	}
	
	/**This method used for generate the pdf file of caterer report**/
	private Element generateCatererPdfReport(String pdfPath, String logoPath, String schoolName, List<String> grades, 
			Date startDate,	Date endDate, Boolean byItem, ItemTypeConstants menuType, String countryCode, String dateFormat,List<Integer> nonSchoolDays) throws Exception{	
			PdfPTable mainTable = new PdfPTable(1);
			PdfPCell cell=new PdfPCell();
			cell.addElement(createContentTable(pdfPath, logoPath, schoolName, grades, startDate, endDate, byItem, menuType, countryCode, dateFormat,nonSchoolDays));
			mainTable.addCell(cell);
			mainTable.setWidthPercentage(100);
			return mainTable;
	}
	
	private Element createContentTable(String pdfPath, String logoPath, String schoolName, List<String> grades, Date startDate,
			Date enDate, Boolean byItem, ItemTypeConstants menuType, String countryCode, String dateFormat,List<Integer> nonSchoolDays) throws Exception{
        	
        	SimpleDateFormat sdfYm = new SimpleDateFormat("yyyyMM");
        	String grade = "";
        	if (grades == null)
    			grade = "All";
    		else {
    			GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
    			Set<SchoolGrades> schoolGrades = gradeFormatBuild.convertToSchoolGradeSet(grades);
    			String gradeName = gradeFormatBuild.getGradesFromSet(schoolGrades);
    			Map<String, String> gradeMap = mealManageAPIDao.gradeMapByCountry(countryCode);
    			grade = gradeFormatBuild.buildGradeName(gradeName, gradeMap);
    		}
        	
        	String yearMonth = sdfYm.format(startDate);
        	
        	PdfPTable mainTab = new PdfPTable(2);
    		mainTab.setWidthPercentage(100);
    		Image image = Image.getInstance(logoPath);
        	image.scaleAbsolute(30f, 30f);
        	image.setAlignment(Image.ALIGN_LEFT);
    		//for first row
    		PdfPCell first = new PdfPCell();	   
    		first.addElement(image);
    		first.setBorder(0);
    		first.setPaddingLeft(155);
    		mainTab.addCell(first);

    		first = new PdfPCell();
    		first.setBorder(0);		
    		first.addElement(createSchoolBox(schoolName, grade, yearMonth, startDate, enDate, menuType, dateFormat));
    		mainTab.addCell(first);
        	
    		if(isWeekStatus == null || !isWeekStatus){ //logic for monthly report
    			PdfPCell calenderView = new PdfPCell();
    			if(byItem != null && byItem){
    				Set<String> items = new HashSet<String>();
    	        	for(Map<String, Integer> itemSet : mealsMap.values()){
    	        		items.addAll(itemSet.keySet());
    	        	}
    	        	for(String item : items){
    	        		calenderView.addElement(createByItemCalendarTable(yearMonth, item,nonSchoolDays));
    	        	}    				
    			}else
    				calenderView.addElement(createCalendarTable(yearMonth,nonSchoolDays));
        		calenderView.setColspan(5);
        		calenderView.setRowspan(1);
        		calenderView.setBorder(0);
        		mainTab.addCell(calenderView);
    		}else{ //logic for weekly report
    			first = new PdfPCell();	
        		first.addElement(createCatererWeeklyTable(startDate, enDate, byItem));
        		first.setColspan(5);
        		first.setRowspan(1);
        		first.setBorder(0);
        		mainTab.addCell(first);
    		}
    		
			return mainTab;
        }
		
		private Element createSchoolBox(String schoolName, String gradesName, String yearMonth, Date startDate, Date endDate, ItemTypeConstants menuType, String dateFormat){
			PdfPTable schTable = new PdfPTable(1);
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			schTable.setWidthPercentage(150);
			PdfPCell schCell;
			schCell = new PdfPCell(new Phrase(schoolName.toUpperCase(), boldFont));
			schCell.setBorder(0);
			schTable.addCell(schCell);
			long difference = endDate.getTime() - startDate.getTime();
			if(isWeekStatus != null && isWeekStatus && !(startDate.equals(endDate)))
				schCell = new PdfPCell(new Phrase(menuType.toString().toUpperCase()+" WEEKLY CATERER REPORT    "+sdf.format(startDate)+" - "+sdf.format(endDate), generalDateFont));
			else if(difference/(1000*60*60*24) > 1)
				schCell = new PdfPCell(new Phrase(menuType.toString().toUpperCase()+" CATERER REPORT FOR "+Month.of(Integer.parseInt(yearMonth.substring(4)))
					.name()+" "+yearMonth.substring(0,4)+", DATE RANGE: "+sdf.format(startDate)+" - "+sdf.format(endDate), generalDateFont));
			else
				schCell = new PdfPCell(new Phrase(menuType.toString().toUpperCase()+" CATERER REPORT FOR "+Month.of(Integer.parseInt(yearMonth.substring(4)))
					.name()+" "+yearMonth.substring(0,4)+", DATE: "+sdf.format(startDate), generalDateFont));
			
			schCell.setBorder(0);	
			schCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			schTable.addCell(schCell);
			schCell = new PdfPCell(new Phrase("GRADE: "+gradesName	, generalDateFont));
			schCell.setBorder(0);
			schCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			schTable.addCell(schCell);
			return schTable;
		}
		
		private Element createCalendarTable(String yearMonth,List<Integer> nonSchoolDays){
			calendarTable = new PdfPTable(5);
			PdfPCell calCell;
			List<String> daysArray = CommonUtil.getWeekDays(nonSchoolDays);
			/*if(!isItemized)
				daysArray = Arrays.asList("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY");
			else
				daysArray = Arrays.asList("SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY");*/
	    	for(String day : daysArray){
	    		calCell = new PdfPCell(new Phrase(day, boldFont));
	    		calCell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
	    		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		calendarTable.addCell(calCell);
	    	}
	    	createMenuCalendar(yearMonth, null,nonSchoolDays);
			return calendarTable;
		}
		
		private Element createByItemCalendarTable(String yearMonth, String item,List<Integer> nonSchoolDays){
    		List<String> daysArray = CommonUtil.getWeekDays(nonSchoolDays);
			calendarTable = new PdfPTable(daysArray.size());
			PdfPCell calCell;
			calCell = new PdfPCell(new Phrase(item, boldFont));
    		//calCell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			calCell.setColspan(daysArray.size());
    		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    		calendarTable.addCell(calCell);
    		/*if(!isItemized)
    			daysArray = Arrays.asList("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY");
    		else
    			daysArray = Arrays.asList("SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY");*/
	    	for(String day : daysArray){
	    		calCell = new PdfPCell(new Phrase(day, boldFont));
	    		calCell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
	    		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		calendarTable.addCell(calCell);
	    	}
	    	createMenuCalendar(yearMonth, item,nonSchoolDays);
			return calendarTable;
		}
		
		private Element createMenuCalendar(String yearMonth, String item,List<Integer> nonSchoolDays){
			calendarTable.setWidthPercentage(100);
			int yearVal = Integer.parseInt(yearMonth.substring(0,4));
	    	int monthVal = Integer.parseInt(yearMonth.substring(4))-1;
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.set(yearVal, monthVal, 01);
	    	Locale locale = new Locale(LANGUAGE);
			
			int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    	int day = 1;
	    	int position = CommonUtil.getPosition(nonSchoolDays);
	    	/*if(isItemized)
	    		position = 1;*/
	    	while (position != calendar.get(Calendar.DAY_OF_WEEK)) {
	    	    if(!CommonUtil.isWeekendDay(position, nonSchoolDays)){
	    	    	calendarTable.addCell("");
	    	    }
	    	    position = (position % 7) + 1;
	    	}
	    	
	    	// add cells for each day
	    	while (day <= daysInMonth) {
	    	    calendar = new GregorianCalendar(yearVal, monthVal, day++);
	    	    final Date cal = calendar.getTime();
	    	    if(!CommonUtil.isWeekend(calendar, nonSchoolDays)){
	    	    	Map<String, Integer> mealMenus = mealsMap.get(sdf1.format(cal));
	    	    	calendarTable.addCell(getDayCell(calendar, locale, mealMenus, item));
	    	    }
	    	}
	    	calendarTable.completeRow();
			return calendarTable;
		}
		
		
		/**
		 * Creates a PdfPCell for a specific day
		 * @param calendar a date
		 * @param locale a locale
		 * @return a PdfPCell
		 */
		public PdfPCell getDayCell(Calendar calendar, Locale locale, Map<String, Integer> mealMenus, String item) {
			PdfPCell cell = new PdfPCell();
			cellTable = new PdfPTable(5);
		    // and the number of the day
			cellVal = new PdfPCell(new Phrase(String.format(locale, "%1$te", calendar), dayFont));
			cellVal.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellVal.setColspan(5);
			cellVal.setBorder(0);
			cellTable.addCell(cellVal);
			/*cellVal = new PdfPCell(new Phrase("", dayFont));
			cellVal.setBorder(0);
			cellTable.addCell(cellVal);*/
			if(mealMenus != null){
				if(item == null || item.trim().isEmpty()){
					for(Entry<String, Integer> map : mealMenus.entrySet()){
						cellVal = new PdfPCell(new Phrase(map.getKey().toUpperCase(), generalFont));
						cellVal.setColspan(4);
						cellVal.setBorder(0);
			    		cellTable.addCell(cellVal);
			    		cellVal = new PdfPCell(new Phrase(map.getValue().toString(), generalFont));
			    		cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
			    		cellVal.setBorder(0);
			 			cellTable.addCell(cellVal);
					}
				}else{
					cellVal = new PdfPCell(new Phrase(item.toUpperCase(), generalFont));
					cellVal.setColspan(4);
					cellVal.setBorder(0);
		    		cellTable.addCell(cellVal);
		    		cellVal = new PdfPCell(new Phrase((mealMenus.get(item) != null ? mealMenus.get(item) : 0)+"", generalFont));
		    		cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    		cellVal.setBorder(0);
		 			cellTable.addCell(cellVal);
				}				
		    }
			cellTable.setWidthPercentage(100);
			cell.addElement(cellTable);
		    return cell;
		}
		
		/**This method used for check weekly report status**/
		private void checkReportIsByWeek(Date startDate, Date endDate) throws Exception{	
			long difference = endDate.getTime() - startDate.getTime();		
			if(difference/(1000*60*60*24) <= 6)
				isWeekStatus = true;
	    }
		
		/**This method used for create weekly caterer report**/
		private Element createCatererWeeklyTable(Date startDate, Date endDate, Boolean byItem) throws Exception{
			List<String> dateList = new ArrayList<String>();
        	String dateVal = sdf1.format(startDate);
        	Boolean status = true;
        	while(status){
        		if(isItemized && !formatter.format(sdf1.parse(dateVal)).equalsIgnoreCase("Friday") && 
        				!formatter.format(sdf1.parse(dateVal)).equalsIgnoreCase("Saturday"))
    		    	dateList.add(dateVal);
        		if(!isItemized && !formatter.format(sdf1.parse(dateVal)).equalsIgnoreCase("Saturday") 
        				&& !formatter.format(sdf1.parse(dateVal)).equalsIgnoreCase("Sunday") )
        			dateList.add(dateVal);
        		if(dateVal.equalsIgnoreCase(sdf1.format(endDate)))
        			status = false;
        		else
        			dateVal = sdf1.format(addDays(sdf1.parse(dateVal), 1));
        	}
        	Set<String> items = new HashSet<String>();
        	for(Map<String, Integer> itemSet : mealsMap.values()){
        		items.addAll(itemSet.keySet());
        	}
        	if(byItem != null && byItem)
        		return weeklyReportByItem(startDate, endDate, dateList, items);
        	else
        		return weeklyReportByDay(startDate, endDate, dateList, items);
		}
		
		/**This method used for build weekly caterer report by day
		 * @throws Exception **/
		private Element weeklyReportByDay(Date startDate, Date endDate, List<String> dateList, Set<String> items) throws Exception{
			PdfPTable table = null;
			
			if(startDate.equals(endDate))
				table = new PdfPTable(new float[]{60,30,30});
			else
				table = new PdfPTable(new float[]{60,30,30,30,30,30,30});
			PdfPCell cell = null;;    	    
        	cell = new PdfPCell(new Phrase("ITEM", boldFont));
        	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	table.addCell(cell);
        	Integer totalNo = 0;
        	Integer count = 0;
        	for(String dtVal : dateList){
        		cell = new PdfPCell(new Phrase(sdf2.format(sdf1.parse(dtVal)), boldFont));
            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cell);
        	}
        	cell = new PdfPCell(new Phrase("Total", boldFont));
        	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	table.addCell(cell);
        	for(String item : items){
        		cell = new PdfPCell(new Phrase(item, generalFont));
    			table.addCell(cell);
    			totalNo = 0;
    			for(String datVal : dateList){
    				count = mealsMap.get(datVal) != null ? (mealsMap.get(datVal).get(item) != null 
    						? mealsMap.get(datVal).get(item) : 0) : 0;
    				cell = new PdfPCell(new Phrase((count+""), generalFont));
        			table.addCell(cell);
        			totalNo = totalNo+count;
    			}
    			cell = new PdfPCell(new Phrase((totalNo+""), generalFont));
    			table.addCell(cell);
        	}
    		table.setWidthPercentage(100);
			return table;
		}
		
		/**This method used for build weekly caterer report by item
		 * @throws Exception **/
		private Element weeklyReportByItem(Date startDate, Date endDate, List<String> dateList, Set<String> items) throws Exception{
			PdfPTable table = new PdfPTable(new float[]{30,30,30,30,30,30});
			PdfPCell cell = null;
			for(String item : items){
				cell = new PdfPCell(new Phrase(item, boldFont));
	        	//cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
	        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        	cell.setColspan(6);
	        	table.addCell(cell);
	        	Integer totalNo = 0;
	        	Integer count = 0;
	        	for(String dtVal : dateList){
	        		cell = new PdfPCell(new Phrase(sdf2.format(sdf1.parse(dtVal)), boldFont));
	            	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
	            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			    	table.addCell(cell);
	        	}
	        	cell = new PdfPCell(new Phrase("Total", boldFont));
	        	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
	        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    	table.addCell(cell);
	        	
	        	totalNo = 0;
	    		for(String datVal : dateList){
	    			count = mealsMap.get(datVal) != null ? (mealsMap.get(datVal).get(item) != null 
	    					? mealsMap.get(datVal).get(item) : 0) : 0;
	    			cell = new PdfPCell(new Phrase((count+""), generalFont));
	        		table.addCell(cell);
	        		totalNo = totalNo+count;
	    		}
	    		cell = new PdfPCell(new Phrase((totalNo+""), generalFont));
	    		table.addCell(cell);
			}
    		table.setWidthPercentage(100);
			return table;
		}
			
			/**
			 * Returns true for Sundays.
			 * @param calendar a date
			 * @return true for Sundays
			 */
			/*public boolean isSunday(Calendar calendar) {
			    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			        return true;
			    }
			    return false;
			}*/

			/**
			 * Returns true if the date was found in a list with special days (holidays).
			 * @param calendar a date
			 * @return true for holidays
			 */
			/*public boolean isSpecialDay(Calendar calendar) {
			    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			        return true;
			    return false;
			}*/
			/**Add no. of days in specific date**/
			private static Date addDays(Date date, int days) {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(date);
				cal.add(Calendar.DATE, days);
				return cal.getTime();
			}
}
