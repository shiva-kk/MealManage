package com.mealManage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mealManage.dao.MMDataSyncDao;
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.domain.ParentsNotificationRequest;
import com.mealManage.mealmodel.school.DataSyncFieldConstants;
import com.mealManage.mealmodel.school.DataSyncSchoolFieldMapping;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;
import com.mealManage.util.CommonExcelGenerator;
import com.mealManage.util.SendNotificationUtil;

/**This class implemented for the data sync process related service's method**/
@Service
public class MMDataSyncServiceImpl implements MMDataSyncService{
	
	@Autowired
	private MMDataSyncDao mmDataSyncDao;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
    @Autowired
    private CommonExcelGenerator commonExcelGenerator;
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
	@Value("${mm.support.email}")
	private String mmSupportEmail;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**This method used for update the data sync file's field mapping details**/
	@Override
	public ServiceResponse dataSyncFieldMapping(Long mealSchoolId, Map<DataSyncFieldConstants, String> fileFieldMapping) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try{
			String loggedUser = "";
			if(SecurityContextHolder.getContext().getAuthentication() != null)
				loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
			List<DataSyncSchoolFieldMapping> dataSyncSchoolFieldMappings = buildDataSyncReq(mealSchoolId, fileFieldMapping, loggedUser);
			//Delete if any records found for the specified school
			mmDataSyncDao.deleteExistingDataSyncMapping(mealSchoolId);
			serviceResponse = mmDataSyncDao.updateDataSyncFieldMapping(dataSyncSchoolFieldMappings);
			logger.info(serviceResponse.getStatusMessage());
		}catch(Exception e){
			logger.error("Failed to update the data sync file field mapping due to "+e.getMessage());
			serviceResponse.setStatusCode(500);
			serviceResponse.setStatusMessage("Failed to update the data sync file field mapping.");
			serviceResponse.setStatus("Failed");
			serviceResponse.setErrorMessage(e.getMessage());
		}
		return serviceResponse;
	}

	/**This method used for get the Data Sync file's field mapping details**/
	@Override
	public Map<DataSyncFieldConstants, String> getDataSyncFieldMapping(Long mealSchoolId) {
		Map<DataSyncFieldConstants, String> dataSyncMapping  = new HashMap<DataSyncFieldConstants, String>();
		List<DataSyncSchoolFieldMapping> dataSyncSchoolFieldMappings = mmDataSyncDao.getDataSyncFieldMapping(mealSchoolId);
		dataSyncMapping = buildDataSyncFieldMappingResp(dataSyncSchoolFieldMappings);
		logger.info("Retrieved the Data Sync file's field mapping details successfully");
		return dataSyncMapping;
	}

	/**This method used for proceed the data sync data**/
	@Override
	public StudentCreateResp dataSyncStudents(List<StudentUser> studentUsers, Long mealSchoolId, String adminEmails, 
			Date schoolYearEndDate, String processType) {
		StudentCreateResp studentCreateResp = new StudentCreateResp();
		try {
			//export all the students data from DB and generate excel file, that file storing in S3 for backup.
			if(studentUsers != null && studentUsers.size() > 0){
				String fileLink = commonExcelGenerator.studentExport(mealSchoolId, studentUsers.get(0).getSchoolYear(),"dataSync",null);
				mmDataSyncDao.studentBkpFileLink(fileLink, mealSchoolId, studentUsers.get(0).getSchoolYear(),"dataSync","Automated");
			}
			studentCreateResp = mmDataSyncDao.dataSyncStudents(studentUsers, mealSchoolId, schoolYearEndDate, processType);
			logger.info("Success Insert Count: " + studentCreateResp.getSuccessInsertCount()
					+ ", Success Update Count: " + studentCreateResp.getSuccessUpdateCount() + ", Failed insert count: "
					+ studentCreateResp.getFailedInsertCount() + ", Failed update count: "
					+ studentCreateResp.getFailedUpdateCount() + ", Skip Records Count: "
					+ studentCreateResp.getSkipRecCount()+", failed insert student id list: '"+String.join(", ", studentCreateResp.getFailedInsertStudentIds())+"'"
					+", failed update student id list: '"+String.join(", ", studentCreateResp.getFailedUpdateStudentIds())+"'");
			Map<String, String> emailReq = new HashMap<String, String>();
			emailReq.put("adminEmails", adminEmails);
			emailReq.put("ccEmail", mmSupportEmail);
			emailReq.put("status", "Success");
			emailReq.put("successInsertCount", String.valueOf(studentCreateResp.getSuccessInsertCount()));
			emailReq.put("successUpdateCount", String.valueOf(studentCreateResp.getSuccessUpdateCount()));
			emailReq.put("failedInsertCount", String.valueOf(studentCreateResp.getFailedInsertCount()));
			emailReq.put("failedUpdateCount", String.valueOf(studentCreateResp.getFailedUpdateCount()));
			emailReq.put("skipRecordCount", String.valueOf(studentCreateResp.getSkipRecCount()));
			if(studentCreateResp.getFailedInsertStudentIds().size() > 0)
				emailReq.put("failedInsertStudentIds", String.join(", ", studentCreateResp.getFailedInsertStudentIds()));
			if(studentCreateResp.getFailedUpdateStudentIds().size() > 0)
				emailReq.put("failedUpdateStudentIds", String.join(", ", studentCreateResp.getFailedUpdateStudentIds()));
			sendNotificationUtil.dataSyncProcessStatus(emailReq);
			logger.info("Notify the parents for registration link with size:"+studentCreateResp.getStudentIds().size());
			if(studentCreateResp.getStudentIds() != null && studentCreateResp.getStudentIds().size() > 0){
				ParentsNotificationRequest parentsNotReq = new ParentsNotificationRequest();
				parentsNotReq.setSchoolId(mealSchoolId);
				parentsNotReq.setStudentIds(new ArrayList<String>(studentCreateResp.getStudentIds()));
				parentsNotReq.setSendStatus(true);
				parentsNotReq.setNotificationType("Registration");
				parentsNotReq.setSchoolYear(studentUsers.get(0).getSchoolYear());
				mealManageAPIDao.sendNotificationParents(parentsNotReq);
			}
		}catch (Exception e) {
			logger.error("Failed to create students"+e.getMessage());
			if(e instanceof DataIntegrityViolationException || e.getMessage().contains("javax.persistence.RollbackException") 
					|| e.getMessage().contains("ConstraintViolationException")){
				studentCreateResp.setStatusCode(409);
				studentCreateResp.setErrorMessage("Found duplicate entry: "+e.getMessage());
				studentCreateResp.setStatusMessage("Failed to create the Students due to "+e.getCause().getCause().getMessage().split("for key")[0]+".");
			}else{
				studentCreateResp.setStatusCode(500);
				studentCreateResp.setErrorMessage(e.getMessage());
				studentCreateResp.setStatusMessage("Failed to create the Students.");
			}
			studentCreateResp.setStatus("Failed");
			// logic for send failed email of data sync
			Map<String, String> emailReq = new HashMap<String, String>();
			emailReq.put("adminEmails", adminEmails);
			emailReq.put("status", "Failed");
			emailReq.put("failureError", "error occurred while syncing data in database.");
			sendNotificationUtil.dataSyncProcessStatus(emailReq);
		}
		return studentCreateResp;
	}
	
	/**This method used for build the request body for data sync field mapping**/
	private List<DataSyncSchoolFieldMapping> buildDataSyncReq(Long mealSchoolId, Map<DataSyncFieldConstants, String> fileFieldMapping, 
			String loggedUser){
		List<DataSyncSchoolFieldMapping> dataSyncSchoolFieldMappings = new ArrayList<DataSyncSchoolFieldMapping>();
		if(fileFieldMapping != null)
			fileFieldMapping.forEach((k,v) -> {
				DataSyncSchoolFieldMapping dataSyncSchoolFieldMapping = new DataSyncSchoolFieldMapping();
				dataSyncSchoolFieldMapping.setCreatedBy(loggedUser);
				dataSyncSchoolFieldMapping.setCreatedOn(new Date());
				dataSyncSchoolFieldMapping.setLoggedUser(loggedUser);
				dataSyncSchoolFieldMapping.setMealSchoolId(mealSchoolId);
				dataSyncSchoolFieldMapping.setStandardField(k);
				dataSyncSchoolFieldMapping.setFileMappingField(v);
				dataSyncSchoolFieldMappings.add(dataSyncSchoolFieldMapping);
			});
		return dataSyncSchoolFieldMappings;
	}
	
	/**This method used for build the response of Data Sync file's field mapping details**/
	private Map<DataSyncFieldConstants, String> buildDataSyncFieldMappingResp(List<DataSyncSchoolFieldMapping> dataSyncSchoolFields){
		Map<DataSyncFieldConstants, String> dataSyncMapping  = new HashMap<DataSyncFieldConstants, String>();
		if(dataSyncSchoolFields != null && dataSyncSchoolFields.size() > 0){
			for(DataSyncSchoolFieldMapping dataSyncField : dataSyncSchoolFields){
				dataSyncMapping.put(dataSyncField.getStandardField(), dataSyncField.getFileMappingField());
			}
		}else{
			for(DataSyncFieldConstants fieldConstant : DataSyncFieldConstants.values()){
				dataSyncMapping.put(fieldConstant, "");
			}
		}
		return dataSyncMapping;
	}

}
