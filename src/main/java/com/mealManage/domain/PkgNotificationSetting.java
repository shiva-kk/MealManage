package com.mealManage.domain;

import java.util.Date;

public interface PkgNotificationSetting {
	
	public Integer getDuePkgNotificationDays();
	public Date getPkgDueNotificationLastRun();

}
