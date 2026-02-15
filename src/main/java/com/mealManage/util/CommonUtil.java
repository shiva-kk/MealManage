package com.mealManage.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.EnumUtils;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolGrades;

public class CommonUtil {
	
	public static final  Font generalFont1=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font boldFontSchoolName=FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD);
	public static final  Font generalDateBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
	
	public static String getItemType(ItemTypeConstants menuType){
		String itemType = "";
		switch (menuType.toString().toUpperCase()) {
			case "LUNCH": itemType = MealType.MEAL.toString(); break;
			case "SNACK": itemType = MealType.SNACK.toString(); break;
			case "DINNER": itemType = MealType.DINNER.toString(); break;
			case "BREAKFAST": itemType= MealType.BREAKFAST.toString(); break;
		}
		return itemType;
	}
	
	/**Check weekend based on country**/
	public static boolean isWeekend(Calendar calendar, List<Integer> nonSchoolDays){
		boolean isWeekend = false;
		for(Integer d : nonSchoolDays){
			if((calendar.get(Calendar.DAY_OF_WEEK)-1) == d)
				return true;
		}
		/*if(isItemized){
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
		        return true;
		}else{
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
		        return true;
		}*/
		return isWeekend;
	}
	
	/**Check weekend based on country**/
	public static boolean isWeekendDay(Integer p, List<Integer> nonSchoolDays){
		boolean isWeekend = false;
		if(nonSchoolDays.contains(p-1))
			isWeekend = true;
		return isWeekend;
	}
	
	/**Check order type is Itemized or not**/
	public static Boolean checkItemized(MealSchool mealSchool) throws Exception{
		Boolean isItemized = false;
		if(mealSchool.getModuleAccess() != null && mealSchool.getModuleAccess().get("Menu Order Type") != null 
				&& mealSchool.getModuleAccess().get("Menu Order Type").equalsIgnoreCase("Itemized"))
			isItemized = true;
		return isItemized;
	}
	
	public static List<Integer> getNonSchoolDays(MealSchool mealSchool) throws Exception{
		List<Integer> nonSchoolDays = new ArrayList<>();
		if(mealSchool.getNonSchoolDays() != null){
			if(!mealSchool.getNonSchoolDays().trim().isEmpty())
				nonSchoolDays = Stream.of(mealSchool.getNonSchoolDays().split(",")).map(String::trim)
				  .map(Integer::parseInt).collect(Collectors.toList()); 
		}else{
			if(checkItemized(mealSchool)){
				nonSchoolDays.add(5);
				nonSchoolDays.add(6);
			}else{
				nonSchoolDays.add(6);
				nonSchoolDays.add(0);
			}
		}
		return nonSchoolDays;
	}
	
	public static String validGrade(String grade){
		String finalGrade = null;
		if(EnumUtils.isValidEnum(SchoolGrades.class, grade.toLowerCase()))
			finalGrade = grade.toLowerCase();
		else if(EnumUtils.isValidEnum(SchoolGrades.class, grade.toUpperCase()))
			finalGrade = grade.toUpperCase();
		else
			finalGrade = grade;
		return finalGrade;
		
	}
	//0-Sun,1-Mon,2-Tue,3-Wed,4-Thru,5-Fri,6-Sat
	//"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"
	public static Map<Integer, String> getDaysMap(){
		Map<Integer, String> daysMap = new LinkedHashMap<Integer, String>();
		daysMap.put(0, "SUNDAY");
		daysMap.put(1, "MONDAY");
		daysMap.put(2, "TUESDAY");
		daysMap.put(3, "WEDNESDAY");
		daysMap.put(4, "THURSDAY");
		daysMap.put(5, "FRIDAY");
		daysMap.put(6, "SATURDAY");
		return daysMap;
	}
	
	public static List<String> getWeekDays(List<Integer> nonSchoolDays){
		List<String> daysArray = new ArrayList<>();
		for(Map.Entry<Integer, String> entry : CommonUtil.getDaysMap().entrySet()){
			if(!nonSchoolDays.contains(entry.getKey()))
				daysArray.add(entry.getValue());
		}
		return daysArray;
	}
	
	public static Integer getPosition(List<Integer> nonSchoolDays){
		for(Map.Entry<Integer, String> entry : CommonUtil.getDaysMap().entrySet()){
			if(!nonSchoolDays.contains(entry.getKey()))
				return entry.getKey()+1;
		}
		return 2;
	}
	
	/**This method used for create the first page pdf content**/
	public static Element generateHeader(String pdfPath, String districtName, 
			String userName, String currDate, String amazonS3Bucketname, String reportName, String pDate) throws Exception {
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
		first = new PdfPCell(new Phrase(reportName, generalDateBoldFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_LEFT);
		//first.setColspan(2);
		mainTab.addCell(first);
		SimpleDateFormat sdfOrg1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
		first = new PdfPCell(new Phrase("Generated On: "+sdf1.format(sdfOrg1.parse(currDate)), generalFont1));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_RIGHT);
		mainTab.addCell(first);
		if(pDate == null)
			pDate = "";
		first = new PdfPCell(new Phrase(pDate, generalDateBoldFont));
		first.setBorder(0);
		first.setHorizontalAlignment(Element.ALIGN_LEFT);
		//first.setColspan(2);
		mainTab.addCell(first);
		first = new PdfPCell(new Phrase("Generated By: "+userName, generalFont1));
		first.setBorder(0);	
		first.setHorizontalAlignment(Element.ALIGN_RIGHT);
		mainTab.addCell(first);
		
		return mainTab;
	}
	
	public static Map<String, String> engSpaReasons(){
		Map<String, String> enSp = new HashMap<String, String>();
		enSp.put("NJ SNAP (Food Stamp) Number OR TANF Case Number (Step 2)", "El número de caso de NJ SNAP (cupones de alimentos) o el número de caso de TANF(Asistencia Temporera para Familias Necesitadas) (Paso 2)");
		enSp.put("Child income frequency not indicated (Step 3)", "Frecuencia de Ingreso del Niño no está indicado (Paso 3)");
		enSp.put("Indicate income using acceptable frequencies (Step 3)", "Indique ingreso usando frecuencias aceptables (Paso 3)");
		enSp.put("Frequency of income received by each household member (Step 3)", "Frecuencia de ingreso recibido por cada miembro del hogar (Paso 3)");
		enSp.put("Gross income (net income is not acceptable) (Step 3)", "Ingreso bruto (Ingreso después de deducciones no es aceptable) (Paso 3) ");
		enSp.put("Last four digits of Social Security Number for adult signing the application or if the adult does not have a Social Security Number, check the appropriate box (Step 3)", "Los últimos cuatro dígitos del número de seguro social del adulto que firmó la aplicación "
				+ "o si el adulto no tiene número de seguro social, indíquelo en el cuadrado apropiado (Paso 3)");
		enSp.put("Adult Signature (Step 4)", "Firma de un adulto (Paso 4)");
		enSp.put("Other:", "Otro:");
		return enSp;
	}
	
	public static String getCurrCode(String currCode){
		if(currCode == null || currCode.trim().isEmpty())
			return "USD";
		else
			return currCode;
	}

}
