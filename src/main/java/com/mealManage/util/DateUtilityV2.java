package com.mealManage.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DateUtilityV2 {
	
	@Autowired 
	private AuditReportUtil auditReportUtil;
	
	public String formatDateToString(Date date, String format, String timeZone) {
		
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);
		String[] offset = "+:0:0".split(":");
		if(auditReportUtil.getOffsetByTimezone().get(timeZone) != null)
			offset = auditReportUtil.getOffsetByTimezone().get(timeZone).split(":");
		OffsetDateTime offsetDateTime = date.toInstant()
				.atOffset(ZoneOffset.ofHoursMinutes(Integer.valueOf(offset[0]+""+offset[1]), Integer.valueOf(offset[0]+""+offset[2])));
		return fmt.format(offsetDateTime);
	}
	
	public String formatDateToStringUTC(Date date, String format, String timeZone) {
		
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);
		String[] offset = "+:0:0".split(":");
		if(auditReportUtil.getOffsetByTimezone().get(timeZone) != null){
			String offsetV = auditReportUtil.getOffsetByTimezone().get(timeZone);
			if(offsetV.contains("-"))
				offsetV=offsetV.replace("-", "+");
			else
				offsetV=offsetV.replace("+", "-");
			offset = offsetV.split(":");
		}
		OffsetDateTime offsetDateTime = date.toInstant()
				.atOffset(ZoneOffset.ofHoursMinutes(Integer.valueOf(offset[0]+""+offset[1]), Integer.valueOf(offset[0]+""+offset[2])));
		return fmt.format(offsetDateTime);
	}

}
