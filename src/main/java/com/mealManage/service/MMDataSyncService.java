package com.mealManage.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mealManage.mealmodel.school.DataSyncFieldConstants;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;

public interface MMDataSyncService {
	
	public ServiceResponse dataSyncFieldMapping(Long mealSchoolId, Map<DataSyncFieldConstants, String> fileFieldMapping);
	
	public Map<DataSyncFieldConstants, String> getDataSyncFieldMapping(Long mealSchoolId);
	
	public StudentCreateResp dataSyncStudents(List<StudentUser> studentUsers, Long mealSchoolId, String adminEmails, 
			Date schoolYearEndDate, String processType);

}
