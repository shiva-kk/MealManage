package com.mealManage.quartzJob;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.mealManage.service.MealManageAPIService;

public class AutoReminderQuartzJob implements Job{
	
	@Autowired
	private MealManageAPIService mealManageAPIService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			logger.info("Starting the execution of Quartz Job for send the auto-reminder to parent regarding lunch");
			mealManageAPIService.buildAutoReminderRequest();
			logger.info("Completed the execution of Quartz Job for send the auto-reminder to parent regarding lunch");
		} catch (Exception e) {
			logger.error("Error occurred during execution of Quartz Job for auto reminder of Lunch to parents. "+e.getMessage());
		}
	}

}
