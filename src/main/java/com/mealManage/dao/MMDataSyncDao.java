package com.mealManage.dao;

import java.util.Date;
import java.util.List;

import com.mealManage.mealmodel.school.DataSyncSchoolFieldMapping;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;

public interface MMDataSyncDao {
	
	public void deleteExistingDataSyncMapping(Long mealSchoolId);
	
	public ServiceResponse updateDataSyncFieldMapping(List<DataSyncSchoolFieldMapping> dataSyncSchoolFieldMappings);
	
	public List<DataSyncSchoolFieldMapping> getDataSyncFieldMapping(Long mealSchoolId);
	
	public StudentCreateResp dataSyncStudents(List<StudentUser> students, Long mealSchoolId, Date schoolYearEndDate, String processType) throws Exception;
	
	public void studentBkpFileLink(String fileLink, Long mealSchoolId, Integer schoolYear, String type, String loggedUser) throws Exception;

}
