package com.mealManage.mealmodel.school;

public enum SchoolTimezone {
	
	SST("Samoa Standard Time: UTC-11:00"),
	HAST("Hawaii-Aleutian Standard Time: UTC-10:00"),
	AKST("Alaska Standard Time: UTC-09:00"),
	PST("Pacific Standard Time: UTC-08:00"),
	MST("Mountain Standard Time: UTC-07:00"),
	CST("Central Standard Time: UTC-06:00"),
	EST("Eastern Standard Time: UTC-05:00"),
	AST("Atlantic Standard Time: UTC-04:00"),
	CHST("Chamorro Standard Time: UTC+10:00"),
	WAKT("Wake Island Time Zone: UTC+12:00"),
	IST("India Standard Time: UTC+05:30");
	
	private String desc;

	SchoolTimezone(String desc) {
        this.desc = desc;
    }

    public String desc() {
        return desc;
    }

}
