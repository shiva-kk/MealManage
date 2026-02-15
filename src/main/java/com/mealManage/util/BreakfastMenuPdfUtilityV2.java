package com.mealManage.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealschedule.entities.MealCalendar;
import com.mealManage.mealschedule.entities.MealCalendarSummary;

/**This utility class used for generate PDF with the created Meal Menu items details**/
@Component
public class BreakfastMenuPdfUtilityV2 {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${amazon.s3.endpoint}")
	private String amazonS3Endpoint;
 	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
    @Value("${amazon.s3.mealMenuPdf.folder}")
    private String mealMenuPdfFolder;
    @Autowired
    private AWSUtility awsUtility;
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	//public static final  Font footerFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	public static final  Font sideFont=FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD);
	public static final  Font dayFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    /** Collection with special days */
    public static Properties specialDays = new Properties();
    private PdfPTable calendarTable = new PdfPTable(5);
    private PdfPTable cellTable = null;
    private PdfPCell cellVal = null;
    private DecimalFormat df = new DecimalFormat("0.00"); //for two digit decimal
	
    @Async
	public void breakfastMenuPdf(MealCalendarSummary summary, String currencySymbol, Boolean isItemized,List<Integer> nonSchoolDays) throws Exception{
		String pdfFilePath = breakfastMenuPdfFinalLink(summary.getSchool().getSchoolId(), 
				summary.getYearMonth(), summary.getId(), false);
		String logoPath = "";
		if(summary.getSchool().getLogoLink() != null)
			logoPath = summary.getSchool().getLogoLink();
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();
			document.add(createMealMenuPdf(pdfFilePath, logoPath, summary,currencySymbol, isItemized,nonSchoolDays));
			document.close();
		}catch (FileNotFoundException e){
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}catch (DocumentException e){
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}	
		
		awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "MealMenu");
	}
	
	private Element createMealMenuPdf(String pdfPath, String logoPath, MealCalendarSummary summary, String currencySymbol, 
			Boolean isItemized,List<Integer> nonSchoolDays)throws Exception{
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell=new PdfPCell();
		cell.addElement(createContentTable(pdfPath, logoPath, summary,currencySymbol, isItemized,nonSchoolDays));
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}
	
	private Element createContentTable(String pdfPath, String logoPath, MealCalendarSummary summary, String currencySymbol, 
			Boolean isItemized,List<Integer> nonSchoolDays) throws Exception{
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
		first.addElement(createSchoolBox(summary));
		mainTab.addCell(first);

		PdfPCell calenderView = new PdfPCell();
		calenderView.addElement(createCalendarTable(summary,currencySymbol, isItemized,nonSchoolDays));
		calenderView.setColspan(5);
		calenderView.setRowspan(1);
		calenderView.setBorder(0);
		mainTab.addCell(calenderView);
		
		PdfPCell pdfFooter = new PdfPCell();
		pdfFooter.addElement(createFooterTable());
		pdfFooter.setColspan(5);
		pdfFooter.setRowspan(1);
		pdfFooter.setBorder(0);
		mainTab.addCell(pdfFooter);
		
		return mainTab;
	}
	
	private Element createBreakfastMenu(MealCalendarSummary summary, String currencySymbol, Boolean isItemized,List<Integer> nonSchoolDays){
		calendarTable.setWidthPercentage(100);
		int yearVal = Integer.parseInt(summary.getYearMonth().substring(0,4));
    	int monthVal = Integer.parseInt(summary.getYearMonth().substring(4))-1;
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
    	
    	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    	//mealMenuList = new ArrayList<MealMenu>(mealsRequest.getMealMenus());
    	// add cells for each day
    	while (day <= daysInMonth) {
    	    calendar = new GregorianCalendar(yearVal, monthVal, day++);
    	    final Date cal = calendar.getTime();
    	    if(!CommonUtil.isWeekend(calendar,nonSchoolDays)){
    	    	List<MealCalendar> breakfastItems = summary.getMealByDays().stream()
    	    	        .filter(p ->  p.getDate() != null && sdf1.format(/*dateUtility.convertGMTtoUserTime(
    	    	        		p.getStart())*/p.getDate()).compareTo(sdf1.format(cal))==0)
    	    	        //.map(SchoolMeal::new)
    	    	        .collect(Collectors.toCollection(ArrayList::new));
    	    	calendarTable.addCell(getDayCell(calendar, locale, breakfastItems,currencySymbol));
    	    }
    	}
    	calendarTable.completeRow();
		return calendarTable;
	}
	
	
	private Element createCalendarTable(MealCalendarSummary summary, String currencySymbol, Boolean isItemized,List<Integer> nonSchoolDays){
		List<String> daysArray = CommonUtil.getWeekDays(nonSchoolDays);
		calendarTable = new PdfPTable(daysArray.size());
		PdfPCell calCell;
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
    	createBreakfastMenu(summary,currencySymbol, isItemized,nonSchoolDays);
		return calendarTable;
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

	public PdfPCell getDayCell(Calendar calendar, Locale locale, List<MealCalendar> breakfastItems, String currencySymbol) {
	    PdfPCell cell = new PdfPCell();
	    cellTable = new PdfPTable(5);
	    // and the number of the day
		cellVal = new PdfPCell(new Phrase(String.format(locale, "%1$te", calendar), dayFont));
		cellVal.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellVal.setColspan(5);
		cellVal.setBorder(0);
		cellTable.addCell(cellVal);
		Boolean isHoliday = false;
	    
	    List<MealCalendar> menuSides = new ArrayList<MealCalendar>();
	    for(MealCalendar breakfastItem : breakfastItems){
	    	if(breakfastItem.getMenuItem().getCategory().toString().equalsIgnoreCase(MealType.BREAKFAST.toString())){
	    		cellVal = new PdfPCell(new Phrase(breakfastItem.getMenuItem().getName().toUpperCase(), generalFont));
				cellVal.setColspan(3);
				cellVal.setBorder(0);
	    		cellTable.addCell(cellVal);
	    		cellVal = new PdfPCell(new Phrase(currencySymbol+df.format(breakfastItem.getPrice()), generalFont));
	    		cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    		cellVal.setColspan(2);
	    		cellVal.setBorder(0);
	 			cellTable.addCell(cellVal);
	    	}else if(breakfastItem.getMenuItem().getCategory().toString().equalsIgnoreCase(MealType.HOLIDAY.toString())){
	    		cellVal = new PdfPCell(new Phrase(breakfastItem.getMenuItem().getName().toUpperCase(), generalFont));
				cellVal.setColspan(5);
				cellVal.setBorder(0);
	    		cellTable.addCell(cellVal);
	    		isHoliday = true;
	    	}else if(breakfastItem.getMenuItem().getCategory().toString().equalsIgnoreCase(MealType.SIDE.toString())){
	    		menuSides.add(breakfastItem);
	    	}
	    }	    
	    for(MealCalendar mealMenu : menuSides){
	    	cellVal = new PdfPCell(new Phrase(mealMenu.getMenuItem().getName().toUpperCase(), sideFont));
			cellVal.setColspan(5);
			cellVal.setBorder(0);
    		cellTable.addCell(cellVal);
	    }
		cellTable.setWidthPercentage(100);
		cell.addElement(cellTable);
		if(isHoliday)
			cell.setBackgroundColor(WebColors.getRGBColor("#F3F1F3"));
	    return cell;
	}
	
	private Element createSchoolBox(MealCalendarSummary summary){
		PdfPTable schTable = new PdfPTable(1);
		schTable.setWidthPercentage(150);
		PdfPCell schCell;
		schCell = new PdfPCell(new Phrase(summary.getSchool().getSchoolName().toUpperCase(), boldFont));
		schCell.setBorder(0);
		schTable.addCell(schCell);
		schCell = new PdfPCell(new Phrase("BREAKFAST MENU FOR "+Month.of(
				Integer.parseInt(summary.getYearMonth().substring(4))).name()+" "+
				summary.getYearMonth().substring(0,4) , generalDateFont));
		schCell.setBorder(0);	
		schCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		schTable.addCell(schCell);
		return schTable;
	}
	
	private Element createFooterTable() throws Exception{
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		//for first row
		PdfPCell pdfCell = new PdfPCell();	
		
		String footerContent = "Drinking Water is made available to youth at all times at drinking fountains located in this facility \n"+
				"**Fresh Fruits & Vegetable selections subject to change based on quality, seasonality, and availability. \n"+
				"***All meals are served with 1% White Milk or Skim Chocolate Milk \n"+
				"This Institution is an equal opportunity provider";

		pdfCell = new PdfPCell(new Phrase(footerContent, generalFont));
		pdfCell.setBorder(0);	
		pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pdfCell);
    	table.completeRow();
    	return table;
	}
	
	public String mealMenuPdfFinalLink(Long mealSchoolId, String yearMonth, Long summaryId, Boolean isLink){
		File convFile = new File(mealSchoolId+"_"+yearMonth+"_"+summaryId+"_MealMenus"+".pdf");
		String pdfFilePath = convFile.getAbsolutePath();
		if(isLink)
			return pdfLink(pdfFilePath);
		else 
			return pdfFilePath;
	}
	
	public String breakfastMenuPdfFinalLink(Long mealSchoolId, String yearMonth, Long summaryId, Boolean isLink){
		File convFile = new File(mealSchoolId+"_"+yearMonth+"_"+summaryId+"_BreakfastMenus"+".pdf");
		String pdfFilePath = convFile.getAbsolutePath();
		if(isLink)
			return pdfLink(pdfFilePath);
		else 
			return pdfFilePath;
	}
	
	private String pdfLink(String pdfFilePath){
		String fileName = mealMenuPdfFolder+new File(pdfFilePath).getName();
		String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName; 
		return finalFilePath;	
	}
	
}
