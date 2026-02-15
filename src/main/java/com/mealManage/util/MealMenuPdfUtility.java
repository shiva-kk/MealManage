package com.mealManage.util;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

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
import java.util.Map;
import java.util.Properties;

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
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.mealmodel.meal.MealMenu;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.meal.SchoolMealSummary;

/**This utility class used for generate PDF with the created Meal Menu items details**/
@Component
public class MealMenuPdfUtility {
	
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
    private List<MealMenu> mealMenuList;
    private SchoolMealSummary schoolMealSummary;
   // private MealCalendarSummary mealCalendarSummary;
    private PdfPTable calendarTable = new PdfPTable(5);
    private PdfPTable cellTable = null;
    private PdfPCell cellVal = null;
    private DecimalFormat df = new DecimalFormat("0.00");
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
	
    @Async
	public void mealMenuPdf(List<MealMenu> mealMenuList, SchoolMealSummary schoolMealSummary,String currencySymbol, 
			Boolean isItemized,List<Integer> nonSchoolDays) throws Exception{
    	this.mealMenuList = mealMenuList;
    	this.schoolMealSummary = schoolMealSummary;
		String pdfFilePath = mealMenuPdfFinalLink(schoolMealSummary.getMealSchool().getSchoolId(), 
				schoolMealSummary.getYearMonth(), schoolMealSummary.getSchoolId(), false);
		String logoPath = "";
		if(schoolMealSummary.getMealSchool().getLogoLink() != null)
			logoPath = schoolMealSummary.getMealSchool().getLogoLink();
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try
		{
			PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();
			document.add(createMealMenuPdf(pdfFilePath, logoPath, schoolMealSummary,currencySymbol, isItemized,nonSchoolDays));
			document.close();
		}
		catch (FileNotFoundException e)
		{
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}
		catch (DocumentException e)
		{
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}	
		
		awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "MealMenu");
	}

	/*@Async
	public void mealMenuPdf(List<MealMenu> mealMenuList, MealCalendarSummary mealCalendarSummary, String currencySymbol) throws Exception{
		this.mealMenuList = mealMenuList;
		this.mealCalendarSummary = mealCalendarSummary;
		String pdfFilePath = mealMenuPdfFinalLink(mealCalendarSummary.getSchool().getSchoolId(),
				mealCalendarSummary.getYearMonth(), mealCalendarSummary.getId(), false);
		String logoPath = "";
		if(mealCalendarSummary.getSchool().getLogoLink() != null)
			logoPath = mealCalendarSummary.getSchool().getLogoLink();
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";

		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try
		{
			PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();
			document.add(createMealMenuPdfV2(pdfFilePath, logoPath, mealCalendarSummary.getSchool().getSchoolName(),
					mealCalendarSummary.getGrades().stream().map(schoolGrade -> schoolGrade.toString()).collect(Collectors.joining(",")),currencySymbol));
			document.close();
		}
		catch (FileNotFoundException e)
		{
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}
		catch (DocumentException e)
		{
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}

		awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "MealMenu");
	}*/
	
	private Element createMealMenuPdf(String pdfPath, String logoPath, SchoolMealSummary summary,String currencySymbol, Boolean isItemized,List<Integer> nonSchoolDays) throws Exception{
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell=new PdfPCell();
		cell.addElement(createContentTable(pdfPath, logoPath, summary,currencySymbol, isItemized,nonSchoolDays));
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}

	/*private Element createMealMenuPdfV2(String pdfPath, String logoPath, String schoolName, String gradesName,String currencySymbol) throws Exception{
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell=new PdfPCell();
		cell.addElement(createContentTableV2(pdfPath, logoPath, schoolName, gradesName,currencySymbol));
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}*/
	
	private Element createContentTable(String pdfPath, String logoPath, SchoolMealSummary summary,String currencySymbol, Boolean isItemized
			,List<Integer> nonSchoolDays) throws Exception{
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
		String schoolName = summary.getMealSchool().getSchoolName();
		String gradesName = summary.getGradeNames();
		first = new PdfPCell();
		first.setBorder(0);		
		String countryCode = summary.getMealSchool().getCountryCode();
		first.addElement(createSchoolBox(schoolName,gradesName, summary.getMenuType().toString(),countryCode));
		mainTab.addCell(first);
		
		first = new PdfPCell();	
		first.addElement(createCellForData());
		first.setColspan(3);
		first.setRowspan(1);
		first.setBorder(0);	
		mainTab.addCell(first);

		String itemType = CommonUtil.getItemType(summary.getMenuType());
		PdfPCell calenderView = new PdfPCell();
		calenderView.addElement(createCalendarTable(currencySymbol, itemType, isItemized, schoolMealSummary.getIsExtraPreOrder(),nonSchoolDays));
		calenderView.setColspan(5);
		calenderView.setRowspan(1);
		calenderView.setBorder(0);
		mainTab.addCell(calenderView);
		
		if(schoolMealSummary.getIsExtraPreOrder() == null || !schoolMealSummary.getIsExtraPreOrder()){
			PdfPCell extraItemsTitle = new PdfPCell(new Phrase("BELOW ITEMS ARE AVAILABLE FOR ON-SITE PURCHASE \n", generalDateFont));
			extraItemsTitle.setColspan(5);
			extraItemsTitle.setRowspan(1);
			extraItemsTitle.setBorder(0);
			extraItemsTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
			mainTab.addCell(extraItemsTitle);
			
			PdfPCell extraTable = new PdfPCell();
			extraTable.addElement(createExtraItemTable(currencySymbol));
			extraTable.setColspan(5);
			extraTable.setRowspan(1);
			extraTable.setBorder(0);
			mainTab.addCell(extraTable);
		}		
		
		if(countryCode.equalsIgnoreCase("US")){
			PdfPCell pdfFooter = new PdfPCell();
			pdfFooter.addElement(createFooterTable());
			pdfFooter.setColspan(5);
			pdfFooter.setRowspan(1);
			pdfFooter.setBorder(0);
			mainTab.addCell(pdfFooter);
		}
		
		return mainTab;
	}

	/*private Element createContentTableV2(String pdfPath, String logoPath, String schoolName, String gradesName,String currencySymbol) throws Exception{
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
		first.addElement(createSchoolBoxV2(schoolName,gradesName));
		mainTab.addCell(first);

		first = new PdfPCell();
		first.addElement(createCellForData());
		first.setColspan(3);
		first.setRowspan(1);
		first.setBorder(0);
		mainTab.addCell(first);

		PdfPCell calenderView = new PdfPCell();
		calenderView.addElement(createCalendarTableV2(currencySymbol));
		calenderView.setColspan(5);
		calenderView.setRowspan(1);
		calenderView.setBorder(0);
		mainTab.addCell(calenderView);

		PdfPCell extraItemsTitle = new PdfPCell(new Phrase("BELOW ITEMS ARE AVAILABLE FOR ON-SITE PURCHASE \n", generalDateFont));
		extraItemsTitle.setColspan(5);
		extraItemsTitle.setRowspan(1);
		extraItemsTitle.setBorder(0);
		extraItemsTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTab.addCell(extraItemsTitle);

		PdfPCell extraTable = new PdfPCell();
		extraTable.addElement(createExtraItemTable(currencySymbol));
		extraTable.setColspan(5);
		extraTable.setRowspan(1);
		extraTable.setBorder(0);
		mainTab.addCell(extraTable);

		PdfPCell pdfFooter = new PdfPCell();
		pdfFooter.addElement(createFooterTable());
		pdfFooter.setColspan(5);
		pdfFooter.setRowspan(1);
		pdfFooter.setBorder(0);
		mainTab.addCell(pdfFooter);

		return mainTab;
	}*/
	
	private Element createMealMenu(String currencySymbol, String itemType, Boolean isItemized, Boolean isExtraPreOrder,List<Integer> nonSchoolDays){
		calendarTable.setWidthPercentage(100);
		int yearVal = Integer.parseInt(schoolMealSummary.getYearMonth().substring(0,4));
    	int monthVal = Integer.parseInt(schoolMealSummary.getYearMonth().substring(4))-1;
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
    	    //if(!isSunday(calendar) && !isSpecialDay(calendar)){
    	    if(!CommonUtil.isWeekend(calendar, nonSchoolDays)){
    	    	List<MealMenu> mealMenus = mealMenuList.stream()
    	    	        .filter(p ->  p.getStart() != null && sdf1.format(/*dateUtility.convertGMTtoUserTime(
    	    	        		p.getStart())*/p.getStart()).compareTo(sdf1.format(cal))==0)
    	    	        //.map(SchoolMeal::new)
    	    	        .collect(toCollection(ArrayList::new));
    	    	calendarTable.addCell(getDayCell(calendar, locale, mealMenus,currencySymbol, itemType, isExtraPreOrder));
    	    }
    	}
    	calendarTable.completeRow();
		return calendarTable;
	}

	/*private Element createMealMenuV2(String currencySymbol){
		calendarTable.setWidthPercentage(100);
		int yearVal = Integer.parseInt(mealCalendarSummary.getYearMonth().substring(0,4));
		int monthVal = Integer.parseInt(mealCalendarSummary.getYearMonth().substring(4))-1;
		Calendar calendar = Calendar.getInstance();
		calendar.set(yearVal, monthVal, 01);
		Locale locale = new Locale(LANGUAGE);

		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int day = 1;
		int position = 2;
		while (position != calendar.get(Calendar.DAY_OF_WEEK)) {
			if(!Comm){
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
			if(!isSunday(calendar) && !isSpecialDay(calendar)){
				List<MealMenu> mealMenus = mealMenuList.stream()
						.filter(p ->  p.getStart() != null && sdf1.format(dateUtility.convertGMTtoUserTime(
    	    	        		p.getStart())p.getStart()).compareTo(sdf1.format(cal))==0)
						//.map(SchoolMeal::new)
						.collect(toCollection(ArrayList::new));
				calendarTable.addCell(getDayCell(calendar, locale, mealMenus,currencySymbol));
			}
		}
		calendarTable.completeRow();
		return calendarTable;
	}*/
	
	
	private Element createCalendarTable(String currencySymbol, String itemType, Boolean isItemized, 
			Boolean isExtraPreOrder,List<Integer> nonSchoolDays){
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
    	createMealMenu(currencySymbol, itemType, isItemized, isExtraPreOrder,nonSchoolDays);
		return calendarTable;
	}

	/*private Element createCalendarTableV2(String currencySymbol){
		calendarTable = new PdfPTable(5);
		PdfPCell calCell;
		String[] daysArray = {"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"};
		for(String day : daysArray){
			calCell = new PdfPCell(new Phrase(day, boldFont));
			calCell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
			calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			calendarTable.addCell(calCell);
		}
		createMealMenuV2(currencySymbol);
		return calendarTable;
	}*/
	
	private Element createCellForData(){
		PdfPTable textTable = new PdfPTable(3);
		PdfPCell calCell;
		String[] cellHeader = {"NAME: ","GRADE: ","TEACHER: "};
    	for(String textVal : cellHeader){
    		calCell = new PdfPCell(new Phrase(textVal, generalDateFont));
    		calCell.setBorder(0);
    		textTable.addCell(calCell);
    	}
    	textTable.setWidthPercentage(100);
		return textTable;
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

	/**
	 * Creates a PdfPCell for a specific day
	 * @param calendar a date
	 * @param locale a locale
	 * @return a PdfPCell
	 */
	/*public PdfPCell getDayCell(Calendar calendar, Locale locale, List<MealMenu> mealMenus) {
	    PdfPCell cell = new PdfPCell();
	    Paragraph p = new Paragraph();
	    Paragraph p1 = new Paragraph();
	    // and the number of the day
	    p1.add(new Chunk(String.format(locale, "%1$te", calendar), generalDateFont));
	    p1.setAlignment(Element.ALIGN_RIGHT);
	   // p.add(p1);
	    p.setAlignment(Element.ALIGN_LEFT);
	    
	    List<MealMenu> menuSides = new ArrayList<MealMenu>();
	    for(MealMenu mealMenu : mealMenus){
	    	if(mealMenu.getType().toString().equalsIgnoreCase("MEAL")){
	    	    p.add(new Chunk(mealMenu.getTitle().toUpperCase()+" $"+mealMenu.getPrice(), generalFont));
	    	    p.add(new Phrase("\n"));
	    	}else if(mealMenu.getType().toString().equalsIgnoreCase("HOLIDAY")){
	    	    p.add(new Chunk(mealMenu.getTitle().toUpperCase(), generalFont));
	    	    p.add(new Phrase("\n"));
	    		cell.setBackgroundColor(WebColors.getRGBColor("#F3F1F3"));
	    		p.setAlignment(Element.ALIGN_CENTER);
	    	}else if(mealMenu.getType().toString().equalsIgnoreCase("SIDE")){
	    		menuSides.add(mealMenu);
	    	}
	    }	    
	    for(MealMenu mealMenu : menuSides){
	    	 p.add(new Chunk(mealMenu.getTitle().toUpperCase(), sideFont));
	    	 p.add(new Phrase("\n"));
	    }
	    cell.addElement(p1);
	    cell.addElement(p);
	    return cell;
	}*/
	public PdfPCell getDayCell(Calendar calendar, Locale locale, List<MealMenu> mealMenus,String currencySymbol, String itemType, Boolean isExtraPreOrder) {
	    PdfPCell cell = new PdfPCell();
	    cellTable = new PdfPTable(5);
	    // and the number of the day
		cellVal = new PdfPCell(new Phrase(String.format(locale, "%1$te", calendar), dayFont));
		cellVal.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellVal.setColspan(5);
		cellVal.setBorder(0);
		cellTable.addCell(cellVal);
		Boolean isHoliday = false;
	    
	    List<MealMenu> menuSides = new ArrayList<MealMenu>();
	    List<MealMenu> menuExtras = new ArrayList<MealMenu>();
	    for(MealMenu mealMenu : mealMenus){
	    	if(mealMenu.getType().toString().equalsIgnoreCase(itemType)){
	    		cellVal = new PdfPCell(new Phrase(mealMenu.getTitle().toUpperCase(), generalFont));
				cellVal.setColspan(3);
				cellVal.setBorder(0);
	    		cellTable.addCell(cellVal);
	    		cellVal = new PdfPCell(new Phrase(currencySymbol+df.format(mealMenu.getPrice()), generalFont));
	    		cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    		cellVal.setColspan(2);
	    		cellVal.setBorder(0);
	 			cellTable.addCell(cellVal);
	    	}else if(mealMenu.getType().toString().equalsIgnoreCase("HOLIDAY")){
	    		cellVal = new PdfPCell(new Phrase(mealMenu.getTitle().toUpperCase(), generalFont));
				cellVal.setColspan(5);
				cellVal.setBorder(0);
	    		cellTable.addCell(cellVal);
	    		isHoliday = true;
	    	}else if(mealMenu.getType().toString().equalsIgnoreCase("SIDE")){
	    		menuSides.add(mealMenu);
	    	}else if(mealMenu.getType().toString().equalsIgnoreCase(MealType.EXTRA.toString()) && isExtraPreOrder != null 
	    			&& isExtraPreOrder){
	    		menuExtras.add(mealMenu);
	    	}
	    }	    
	    for(MealMenu mealMenu : menuSides){
	    	cellVal = new PdfPCell(new Phrase(mealMenu.getTitle().toUpperCase(), sideFont));
			cellVal.setColspan(5);
			cellVal.setBorder(0);
    		cellTable.addCell(cellVal);
	    }
	    for(MealMenu mealMenu : menuExtras){
	    	cellVal = new PdfPCell(new Phrase("E: "+mealMenu.getTitle().toUpperCase(), generalFont));
			cellVal.setColspan(3);
			cellVal.setBorder(0);
    		cellTable.addCell(cellVal);
    		cellVal = new PdfPCell(new Phrase(currencySymbol+df.format(mealMenu.getPrice()), generalFont));
    		cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		cellVal.setColspan(2);
    		cellVal.setBorder(0);
 			cellTable.addCell(cellVal);
	    }
		cellTable.setWidthPercentage(100);
		cell.addElement(cellTable);
		if(isHoliday)
			cell.setBackgroundColor(WebColors.getRGBColor("#F3F1F3"));
	    return cell;
	}
	
	private Element createSchoolBox(String schoolName, String gradesName, String type, String countryCode){
		PdfPTable schTable = new PdfPTable(1);
		schTable.setWidthPercentage(150);
		PdfPCell schCell;
		schCell = new PdfPCell(new Phrase(schoolName.toUpperCase(), boldFont));
		schCell.setBorder(0);
		schTable.addCell(schCell);
		GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
		Map<String, String> gradeMap = mealManageAPIDao.gradeMapByCountry(countryCode);
		schCell = new PdfPCell(new Phrase(type.toUpperCase()+" MENU FOR "+Month.of(Integer.parseInt(schoolMealSummary.getYearMonth().substring(4)))
			.name()+" "+schoolMealSummary.getYearMonth().substring(0,4)+", GRADE: "+gradeFormatBuild.buildGradeName(gradesName, gradeMap)
				, generalDateFont));
		schCell.setBorder(0);	
		schCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		schTable.addCell(schCell);
		return schTable;
	}

	/*private Element createSchoolBoxV2(String schoolName, String gradesName){
		PdfPTable schTable = new PdfPTable(1);
		schTable.setWidthPercentage(150);
		PdfPCell schCell;
		schCell = new PdfPCell(new Phrase(schoolName.toUpperCase(), boldFont));
		schCell.setBorder(0);
		schTable.addCell(schCell);
		GradeFormatBuild gradeFormatBuild = new GradeFormatBuild();
		schCell = new PdfPCell(new Phrase("LUNCH MENU FOR "+Month.of(Integer.parseInt(mealCalendarSummary.getYearMonth().substring(4)))
				.name()+" "+mealCalendarSummary.getYearMonth().substring(0,4)+", GRADE: "+gradeFormatBuild.buildGradeName(gradesName)
				, generalDateFont));
		schCell.setBorder(0);
		schCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		schTable.addCell(schCell);
		return schTable;
	}*/
	
	private Element createExtraItemTable(String currencySymbol){
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(50);
		PdfPCell cell = null;
		String[] tableColumns = {"ITEM","PRICE"};
    	for(String tblColumn : tableColumns){
        	cell = new PdfPCell(new Phrase(tblColumn, boldFont));
        	cell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
    	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    	    table.addCell(cell);
    	}
    	List<MealMenu> extraMealMenus = mealMenuList.stream()
				 .filter(mealMenu -> 
				 mealMenu.getType().toString().equalsIgnoreCase("EXTRA"))
				 .collect(toList());
   	
    	for(MealMenu mealMenu : extraMealMenus){
    		if(mealMenu.getType().toString().equalsIgnoreCase("EXTRA")){
	   			cell = new PdfPCell(new Phrase(mealMenu.getTitle().toUpperCase(), generalFont));
	   			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(currencySymbol+df.format(mealMenu.getPrice()), generalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
   		}
    	}
    	table.completeRow();
    	return table;
	}
	
	private Element createFooterTable() throws Exception{
		PdfPTable table = new PdfPTable(2);
		table.setWidths(new int[]{1,11});
		table.setWidthPercentage(100);
		String footerLogoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/MenuPdfFooterLogo.PNG";
		Image image = Image.getInstance(footerLogoPath);
    	image.scaleAbsolute(20f, 20f);
    	image.setAlignment(Image.ALIGN_LEFT);
		//for first row
		PdfPCell pdfCell = new PdfPCell();	   
		pdfCell.addElement(image);
		pdfCell.setBorder(0);
		table.addCell(pdfCell);
		
		String footerContent = "In accordance with Federal civil rights law and U.S. Department of Agriculture (USDA) civil rights "+
				"regulations and policies, the USDA, its Agencies, offices, and employees, and institutions participating in or "+
				"administering USDA programs are prohibited from discriminating based on race, color, national origin, sex, "+
				"disability, age, or reprisal or retaliation for prior civil rights activity in any program or activity "+
				"conducted or funded by USDA. \n"+
				"Persons with disabilities who require alternative means of communication for program information (e.g. Braille, large "+
				"print, audiotape, American Sign Language, etc.), should contact the Agency (State or local) where they applied for benefits. "+
				"Individuals who are deaf, hard of hearing or have speech disabilities may contact USDA through the Federal Relay Service at "+
				"(800) 877-8339. Additionally, program information may be made available in languages other than English. \n"+
				"To file a program complaint of discrimination, complete the USDA Program Discrimination Complaint Form, (AD-3027) found online "+
				"at http://www.ascr.usda.gov/complaint_filing_cust.html, and at any USDA office, or write a letter addressed to USDA and provide in the letter all of the "+
				"information requested in the form. To request a copy of the complaint form, call (866) 632-9992. Submit your completed form or "+
				"letter to USDA by: (1) mail: U.S. Department of Agriculture, Office of the Assistant Secretary for Civil Rights "+
				"1400 Independence Avenue, SW, Washington, D.C. 20250-9410; (2) fax: (202) 690-7442; or (3) email: program.intake@usda.gov. "+
				"This institution is an equal opportunity provider. ";

		pdfCell = new PdfPCell(new Phrase(footerContent, generalFont));
		pdfCell.setBorder(0);	
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
