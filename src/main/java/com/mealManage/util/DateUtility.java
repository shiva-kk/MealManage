package com.mealManage.util;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
 
/**
 * Used for date conversion
 * 
 */
public class DateUtility {
	
	public Date convertGMTtoUserTime(Date dt){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); 
		calendar.setTime(dt);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return calendar.getTime();
	}
	
	public Date add15daysToCurrentDate(){
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DATE, 15);
		return cal.getTime();
	}
	
	/**This method used for convert date into specific timezone from UTC**/
	/*public String formatDateToString(Date date, String format, String timeZone) {
		// null check
		if (date == null) return null;
		// create SimpleDateFormat object with input format
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		// default system timezone if passed null or empty
		if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
			timeZone = Calendar.getInstance().getTimeZone().getID();
		}
		// set timezone to SimpleDateFormat
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		// return Date in required format with timezone as String
		return sdf.format(date);
	}*/
	
}