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
import com.mealManage.domain.NotificationRequest;
import com.mealManage.domain.UserActivationNotification;
import com.mealManage.mealmodel.meal.MealMenu;
import com.mealManage.mealmodel.meal.MealOrderDetails;
import com.mealManage.mealmodel.meal.SchoolMeal;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealschedule.entities.MealCalendar;
import com.mealManage.menu.entities.MenuItem;

/**This utility class used for generate PDF with menu ordered items details**/
@Component
public class OrderedMenuPdfUtility {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${amazon.s3.endpoint}")
	private String amazonS3Endpoint;
 	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
 	@Value("${amazon.s3.orderedMenuPdf.folder}")
    private String orderedMenuPdfFolder;
	@Autowired
	private DateUtilityV2 du;
	/*@Value("${menu.pdf.email.url}")
	private String menuOrderedPdfnotificationURL;
	@Autowired
	private RestTemplate restTemplate;*/
    @Autowired
    private AWSUtility awsUtility;
    @Autowired
    private SendNotificationUtil sendNotificationUtil;
    private Boolean isItemized;
	
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	public static final  Font sideFont=FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD);
	public static final  Font dayFont=FontFactory.getFont(FontFactory.HELVETICA, 10);
	DecimalFormat df = new DecimalFormat("0.00");
    /** Collection with special days 
     * @param isItemized */
    //public static Properties specialDays = new Properties();
    /*private MealOrderDetails mealOrderDetails;
    private PdfPTable calendarTable = new PdfPTable(5);
    private PdfPTable cellTable = null;
    private PdfPCell cellVal = null;*/
	
    @Async
	public void orderedMenuPdf(MealOrderDetails mealOrderDetails, String schoolName, String loggedUser, String logoLink, 
			ParentUser parentUser, Boolean priEmailIsSubscribe, Boolean altEmailIsSubscribe, String schoolTimezone,String currencySymbol,
			String adminEmail, String parentUserEmail, Boolean isItemized, String dateFormat,List<Integer> nonSchoolDays,Boolean isRefresh) throws Exception{		
		//this.mealOrderDetails = mealOrderDetails;
    	String pdfFilePath = orderedMenuPdfLink(mealOrderDetails, false);
		String logoPath = "";
		this.isItemized = isItemized;
		if(logoLink != null)
			logoPath = logoLink;
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";
		
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		//sideFont.setColor(BaseColor.RED);
		try{
			PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();
			document.add(createOrderedMenuPdf(pdfFilePath, logoPath, schoolName, loggedUser, schoolTimezone, mealOrderDetails,currencySymbol, 
					dateFormat,nonSchoolDays));
			document.close();
		}catch (FileNotFoundException e){
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}
		catch (DocumentException e){
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}	
		
		awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "OrderedMenu");
		if(!isRefresh){
			List<UserActivationNotification> notificationInfos = new ArrayList<UserActivationNotification>();
			if(parentUserEmail == null || parentUserEmail.trim().isEmpty() || parentUserEmail.split(",").length > 1){
				if(parentUser.getUserName()!=null && mealOrderDetails.getMenuOrderedPdfLink() != null && priEmailIsSubscribe != null
						&& priEmailIsSubscribe){
					UserActivationNotification notificationInfo = new UserActivationNotification();
					notificationInfo.setEmail(parentUser.getUserName());		
					notificationInfo.setToken(mealOrderDetails.getMenuOrderedPdfLink());
					notificationInfo.setSchoolName(schoolName);
					notificationInfo.setDates(mealOrderDetails.getCancellationDates());
					notificationInfo.setCancellationNote(mealOrderDetails.getCancellationNote());
					notificationInfo.setAdminEmail(adminEmail != null ? adminEmail : "");
					notificationInfos.add(notificationInfo);			
					}
				if(parentUser.getParentAltEmail()!=null && !parentUser.getParentAltEmail().trim().equalsIgnoreCase("") 
						&& mealOrderDetails.getMenuOrderedPdfLink() != null && altEmailIsSubscribe != null && altEmailIsSubscribe){
					UserActivationNotification notificationInfo = new UserActivationNotification();
					notificationInfo.setEmail(parentUser.getParentAltEmail());		
					notificationInfo.setToken(mealOrderDetails.getMenuOrderedPdfLink());
					notificationInfo.setSchoolName(schoolName);
					notificationInfo.setAdminEmail(adminEmail != null ? adminEmail : "");
					notificationInfo.setDates(mealOrderDetails.getCancellationDates());
					notificationInfo.setCancellationNote(mealOrderDetails.getCancellationNote());
					notificationInfos.add(notificationInfo);
					}	
			}else{
				UserActivationNotification notificationInfo = new UserActivationNotification();
				notificationInfo.setEmail(parentUserEmail);		
				notificationInfo.setToken(mealOrderDetails.getMenuOrderedPdfLink());
				notificationInfo.setSchoolName(schoolName);
				notificationInfo.setDates(mealOrderDetails.getCancellationDates());
				notificationInfo.setCancellationNote(mealOrderDetails.getCancellationNote());
				notificationInfo.setAdminEmail(adminEmail != null ? adminEmail : "");
				notificationInfos.add(notificationInfo);
			}
			
			List<UserActivationNotification> parentUserActivationNotifications = notificationInfos.stream().distinct().
					collect(Collectors.toList());
			if(parentUserActivationNotifications.size()>0){
			NotificationRequest notificationRequest = new NotificationRequest();
			notificationRequest.setUsers(parentUserActivationNotifications);
			/**Call API for send the notification**/
			if(mealOrderDetails.getCancellationNote() != null && mealOrderDetails.getCancellationDates() != null)
				sendNotificationUtil.sendMenuOrderedPdfCancellation(notificationRequest);
			else
				sendNotificationUtil.sendMenuOrderedPdf(notificationRequest);
			//restTemplate.postForObject(notificationURL, notificationRequest, String.class);	
			}
		}
	}
	
	private Element createOrderedMenuPdf(String pdfPath, String logoPath, String schoolName, String loggedUser, 
			String schoolTimezone, MealOrderDetails mealOrderDetails,String currencySymbol, String dateFormat,List<Integer> nonSchoolDays) throws Exception{	
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell=new PdfPCell();
		cell.addElement(createContentTable(pdfPath, logoPath, schoolName, loggedUser, schoolTimezone, mealOrderDetails,currencySymbol, dateFormat,nonSchoolDays));
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}
		
	private Element createContentTable(String pdfPath, String logoPath, String schoolName, String loggedUser, 
			String schoolTimezone, MealOrderDetails mealOrderDetails,String currencySymbol, String dateFormat
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

		first = new PdfPCell();
		first.setBorder(0);		
		first.addElement(createSchoolBox(schoolName, mealOrderDetails));
		mainTab.addCell(first);
		
		first = new PdfPCell();
		first.setBorder(0);
		first.addElement(createTextCellLeftSide(schoolTimezone, mealOrderDetails, dateFormat));
		mainTab.addCell(first);
		
		first = new PdfPCell();
		first.setBorder(0);
		first.addElement(createTextCellRightSide(loggedUser, mealOrderDetails,currencySymbol));
		mainTab.addCell(first);
		
		PdfPCell calenderView = new PdfPCell();
		calenderView.addElement(createCalendarTable(mealOrderDetails,currencySymbol,nonSchoolDays));
		calenderView.setColspan(5);
		calenderView.setRowspan(1);
		calenderView.setBorder(0);
		mainTab.addCell(calenderView);
		
		return mainTab;
	}
	
	private Element createSchoolBox(String schoolName, MealOrderDetails mealOrderDetails){
		PdfPTable schTable = new PdfPTable(1);
		schTable.setWidthPercentage(150);
		PdfPCell schCell;
		schCell = new PdfPCell(new Phrase(schoolName.toUpperCase(), boldFont));
		schCell.setBorder(0);
		schTable.addCell(schCell);
		schCell = new PdfPCell(new Phrase(mealOrderDetails.getMenuType().toString().toUpperCase()+" ORDER DETAILS FOR "+Month.of(Integer.parseInt(mealOrderDetails.getYearMonth().substring(4)))
			.name()+" "+mealOrderDetails.getYearMonth().substring(0,4), generalDateFont));
		schCell.setBorder(0);	
		schCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		schTable.addCell(schCell);
		return schTable;
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
	/*public PdfPCell getDayCell(Calendar calendar, Locale locale, List<SchoolMeal> schoolMeals) {
	    PdfPCell cell = new PdfPCell();
	    Paragraph p = new Paragraph();
	    Paragraph p1 = new Paragraph();
	    // and the number of the day
	    p1.add(new Chunk(String.format(locale, "%1$te", calendar), generalDateFont));
	    p1.setAlignment(Element.ALIGN_RIGHT);
	   // p.add(p1);
	    p.setAlignment(Element.ALIGN_LEFT);
	    MealMenu mealMenu = null;
	    List<MealMenu> menuSides = new ArrayList<MealMenu>();
	    for(SchoolMeal schoolMeal : schoolMeals){
	    	mealMenu = schoolMeal.getMealMenu();
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
	    for(MealMenu mealMenu1 : menuSides){
	    	 p.add(new Chunk(mealMenu1.getTitle().toUpperCase(), sideFont));
	    	 p.add(new Phrase("\n"));
	    }
	    cell.addElement(p1);
	    cell.addElement(p);
	    return cell;
	}*/
	public PdfPCell getDayCell(Calendar calendar, Locale locale, List<SchoolMeal> schoolMeals, 
			Boolean isEligibleForFreeMeal, Boolean isEligibleForReducedPrice, String currencySymbol) {
	    PdfPCell cell = new PdfPCell();
	    PdfPTable cellTable = new PdfPTable(5);
	    // and the number of the day
		PdfPCell cellVal = new PdfPCell(new Phrase(String.format(locale, "%1$te", calendar), dayFont));
		cellVal.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellVal.setColspan(5);
		cellVal.setBorder(0);
		cellTable.addCell(cellVal);
		Boolean isHoliday = false;
	    MealMenu mealMenu = null;
	    Double itemPrice = null;
	    List<MealMenu> menuSides = new ArrayList<MealMenu>();
	    for(SchoolMeal schoolMeal : schoolMeals){
	    	mealMenu = schoolMeal.getMealMenu();
	    	if(mealMenu.getType().toString().equalsIgnoreCase("MEAL")){
	    		cellVal = new PdfPCell(new Phrase(mealMenu.getTitle().toUpperCase(), generalFont));
				cellVal.setColspan(3);
				cellVal.setBorder(0);
	    		cellTable.addCell(cellVal);
	    		if(isEligibleForFreeMeal)
	    			itemPrice = 0.0;
	    		else if(isEligibleForReducedPrice)
	    			itemPrice = mealMenu.getReducedPrice();
	    		else
	    			itemPrice = mealMenu.getPrice();
	    		cellVal = new PdfPCell(new Phrase(currencySymbol+df.format(itemPrice), generalFont));
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
	    	}
	    }	    
	    for(MealMenu mealMenu1 : menuSides){
	    	cellVal = new PdfPCell(new Phrase(mealMenu1.getTitle().toUpperCase(), sideFont));
			cellVal.setColspan(5);
			cellVal.setBorder(0);
    		cellTable.addCell(cellVal);
	    }
	    if(schoolMeals == null || schoolMeals.size() < 1){
	    	cellVal = new PdfPCell(new Phrase("**No Order**".toUpperCase(), sideFont));
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
	
	public PdfPCell getDayCalendarCell(Calendar calendar, Locale locale, List<MealCalendar> mealCalendars, 
			Boolean isEligibleForFreeMeal, Boolean isEligibleForReducedPrice, Boolean isBeforeCare, String currencySymbol, String itemType, Boolean isEligForDiscount, Double itemDiscount) {
	    PdfPCell cell = new PdfPCell();
	    PdfPTable cellTable = new PdfPTable(5);
	    // and the number of the day
		PdfPCell cellVal = new PdfPCell(new Phrase(String.format(locale, "%1$te", calendar), dayFont));
		cellVal.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellVal.setColspan(5);
		cellVal.setBorder(0);
		cellTable.addCell(cellVal);
		Boolean isHoliday = false;
	    MenuItem mealMenu = null;
	    Double itemPrice = null;
	    List<MenuItem> menuSides = new ArrayList<MenuItem>();
	    List<MenuItem> menuExtras = new ArrayList<MenuItem>();
	    for(MealCalendar mealCalendar : mealCalendars){
	    	mealMenu = mealCalendar.getMenuItem();
	    	if(mealMenu.getCategory().toString().equalsIgnoreCase(itemType)){
	    		cellVal = new PdfPCell(new Phrase(mealMenu.getName().toUpperCase(), generalFont));
				cellVal.setColspan(3);
				cellVal.setBorder(0);
	    		cellTable.addCell(cellVal);
	    		if((isEligibleForFreeMeal && (!isItemized || itemType.equalsIgnoreCase("Breakfast"))) || 
						(isBeforeCare && itemType.equalsIgnoreCase("Breakfast")))
	    			itemPrice = 0.0;
	    		else if(isEligibleForReducedPrice && (!isItemized || itemType.equalsIgnoreCase("Breakfast")))
	    			itemPrice = mealCalendar.getReducedPrice();
	    		else if(isEligForDiscount != null && isEligForDiscount && itemDiscount != null)
	    			itemPrice = mealCalendar.getPrice() - itemDiscount;
	    		else
	    			itemPrice = mealCalendar.getPrice();
	    		if(itemType.equalsIgnoreCase("Extra"))
	    			itemPrice = mealCalendar.getPrice();
	    		cellVal = new PdfPCell(new Phrase(currencySymbol+df.format(itemPrice), generalFont));
	    		cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellVal.setColspan(2);
	    		cellVal.setBorder(0);
	 			cellTable.addCell(cellVal);
	    	}else if(mealMenu.getCategory().toString().equalsIgnoreCase("HOLIDAY")){
	    		cellVal = new PdfPCell(new Phrase(mealMenu.getName().toUpperCase(), generalFont));
				cellVal.setColspan(5);
				cellVal.setBorder(0);
	    		cellTable.addCell(cellVal);
	    		isHoliday = true;
	    	}else if(mealMenu.getCategory().toString().equalsIgnoreCase("SIDE"))
	    		menuSides.add(mealMenu);
	    	else if(mealMenu.getCategory().toString().equalsIgnoreCase("EXTRA")){
	    		mealMenu.setItemPrice(mealCalendar.getPrice() != null ? mealCalendar.getPrice() : (double)0);
	    		menuExtras.add(mealMenu);
	    	}
	    }	    
	    for(MenuItem mealMenu1 : menuSides){
	    	cellVal = new PdfPCell(new Phrase(mealMenu1.getName().toUpperCase(), sideFont));
			cellVal.setColspan(5);
			cellVal.setBorder(0);
    		cellTable.addCell(cellVal);
	    }
	    for(MenuItem mealMenu1 : menuExtras){
	    	cellVal = new PdfPCell(new Phrase("E: "+mealMenu1.getName().toUpperCase(), generalFont));
			cellVal.setColspan(3);
			cellVal.setBorder(0);
    		cellTable.addCell(cellVal);
    		
    		cellVal = new PdfPCell(new Phrase(currencySymbol+df.format(mealMenu1.getItemPrice()), generalFont));
    		cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellVal.setColspan(2);
    		cellVal.setBorder(0);
 			cellTable.addCell(cellVal);
	    }
	    if(mealCalendars == null || mealCalendars.size() < 1){
	    	cellVal = new PdfPCell(new Phrase("**No Order**".toUpperCase(), sideFont));
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
	
	private Element createTextCellLeftSide(String schoolTimezone, MealOrderDetails mealOrderDetails, String dateFormat){
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell = new PdfPCell(new Phrase("STUDENT NAME: "+mealOrderDetails.getStudentUser().getFirstName().toUpperCase()+" "+
				mealOrderDetails.getStudentUser().getLastName().toUpperCase(), generalDateFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPaddingLeft(35);
		table.addCell(cell);
		
		//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		//String format = "MM/dd/yyyy";
		cell = new PdfPCell(new Phrase("ORDER DATE: "+du.formatDateToString(new Date(), dateFormat, schoolTimezone), generalDateFont));
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPaddingLeft(35);
		table.addCell(cell);
		table.setWidthPercentage(100);
		//table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
		return table;
	}
	
	private Element createTextCellRightSide(String orderBy, MealOrderDetails mealOrderDetails,String currencySymbol){
		PdfPTable rightTable = new PdfPTable(1);
		rightTable.setWidthPercentage(150);

		PdfPCell rightCell;
		rightCell = new PdfPCell(new Phrase("ORDER BY: "+orderBy.toUpperCase(), generalDateFont));
		rightCell.setBorder(0);
		rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		rightTable.addCell(rightCell);

		rightCell = new PdfPCell(new Phrase("ORDER AMOUNT: "+currencySymbol+df.format(mealOrderDetails.getOrderAmount()), generalDateFont));
		rightCell.setBorder(0);
		rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);	
		rightTable.addCell(rightCell);
		rightTable.setWidthPercentage(100);
		//rightTable.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
		return rightTable;
	}
	
	public String orderedMenuPdfLink(MealOrderDetails mealOrderDetails, Boolean isLink){
		File convFile = new File(mealOrderDetails.getStudentUser().getFirstName()+"_"+mealOrderDetails.getStudentUser()
		.getLastName()+"_"+mealOrderDetails.getStudentUser().getUserId()+"_"+mealOrderDetails.getYearMonth()+"_"+mealOrderDetails.getMenuType().toString()+".pdf");
		String pdfFilePath = convFile.getAbsolutePath();
		if(isLink)
			return pdfLink(pdfFilePath);
		else 
			return pdfFilePath;
	}
	
	private String pdfLink(String pdfFilePath){
		String fileName = orderedMenuPdfFolder+new File(pdfFilePath).getName();
		String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName; 
		return finalFilePath;	
	}
	
	private Element createCalendarTable(MealOrderDetails mealOrderDetails,String currencySymbol,List<Integer> nonSchoolDays){
		List<String> daysArray = CommonUtil.getWeekDays(nonSchoolDays);
		PdfPTable calendarTable = new PdfPTable(daysArray.size());
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
    	calendarTable = (PdfPTable) createOrderedMenu(calendarTable, mealOrderDetails,currencySymbol,nonSchoolDays);
		return calendarTable;
	}
	
	private Element createOrderedMenu(PdfPTable calendarTable, MealOrderDetails mealOrderDetails,String currencySymbol,List<Integer> nonSchoolDays){
		calendarTable.setWidthPercentage(100);
		String itemType = CommonUtil.getItemType(mealOrderDetails.getMenuType());
		int yearVal = Integer.parseInt(mealOrderDetails.getYearMonth().substring(0,4));
    	int monthVal = Integer.parseInt(mealOrderDetails.getYearMonth().substring(4))-1;
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(yearVal, monthVal, 01);
    	Locale locale = new Locale(LANGUAGE);
		
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	int day = 1;
    	int position = CommonUtil.getPosition(nonSchoolDays);
    	while (position != calendar.get(Calendar.DAY_OF_WEEK)) {
    	    if(!CommonUtil.isWeekendDay(position, nonSchoolDays)){
    	    	calendarTable.addCell("");
    	    }
    	    position = (position % 7) + 1;
    	}
    	
    	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    	if(mealOrderDetails.getSchoolMeals() != null && mealOrderDetails.getSchoolMeals().size() > 0){
    		List<SchoolMeal> schoolMealList = new ArrayList<SchoolMeal>(mealOrderDetails.getSchoolMeals());
        	// add cells for each day
        	while (day <= daysInMonth) {
        	    calendar = new GregorianCalendar(yearVal, monthVal, day++);
        	    final Date cal = calendar.getTime();
        	    if(!CommonUtil.isWeekend(calendar, nonSchoolDays)){
        	    	List<SchoolMeal> schoolMeals = schoolMealList.stream()
        	    	        .filter(p ->  p.getMealMenu().getStart() != null && sdf1.format(/*dateUtility.convertGMTtoUserTime(
        	    	        		p.getStart())*/p.getMealMenu().getStart()).compareTo(sdf1.format(cal))==0)
        	    	        //.map(SchoolMeal::new)
        	    	        .collect(Collectors.toCollection(ArrayList::new));
        	    	calendarTable.addCell(getDayCell(calendar, locale, schoolMeals, mealOrderDetails.getIsEligibleForFreeMeal(),
        	    			mealOrderDetails.getIsEligibleForReducedPrice(),currencySymbol));
        	    }
        	}
    	}else{
    		List<MealCalendar> mealCalendars = new ArrayList<MealCalendar>(mealOrderDetails.getMealCalendars());
        	// add cells for each day
        	while (day <= daysInMonth) {
        	    calendar = new GregorianCalendar(yearVal, monthVal, day++);
        	    final Date cal = calendar.getTime();
        	    if(!CommonUtil.isWeekend(calendar, nonSchoolDays)){
        	    	List<MealCalendar> mealCalendars2 = mealCalendars.stream()
        	    	        .filter(p ->  p.getDate() != null && sdf1.format(/*dateUtility.convertGMTtoUserTime(
        	    	        		p.getStart())*/p.getDate()).compareTo(sdf1.format(cal))==0)
        	    	        //.map(SchoolMeal::new)
        	    	        .collect(Collectors.toCollection(ArrayList::new));
        	    	calendarTable.addCell(getDayCalendarCell(calendar, locale, mealCalendars2, mealOrderDetails.getIsEligibleForFreeMeal(),
        	    			mealOrderDetails.getIsEligibleForReducedPrice(), mealOrderDetails.getStudentUser().isBeforeCare(),currencySymbol, itemType, mealOrderDetails.getIsEligForDiscount(), mealOrderDetails.getItemDiscount()));
        	    }
        	}
    	}    	
    	calendarTable.completeRow();
		return calendarTable;
	}
	
}
