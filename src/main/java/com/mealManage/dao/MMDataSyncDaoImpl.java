package com.mealManage.dao;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mealManage.mealmodel.repository.CountryDetailsRepository;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.repository.UsersAuthInfoRepository;
import com.mealManage.mealmodel.school.DataSyncSchoolFieldMapping;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolType;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.StudentBkpFileHistory;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.mealmodel.user.auth.UsersAuthInfo;
import com.mealManage.response.RejectedStudentsInfo;
import com.mealManage.response.ServiceResponse;
import com.mealManage.response.StudentCreateResp;
import com.mealManage.util.DateUtilityV2;

@Transactional
@Repository
@SuppressWarnings("unchecked")
/**This class implementing the MMDataSyncDao interface**/
public class MMDataSyncDaoImpl implements MMDataSyncDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private UsersAuthInfoRepository usersAuthInfoRepository;
	@Autowired
	private DateUtilityV2 du;
	@Autowired
	private CountryDetailsRepository countryDetailsRepository;
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	private static DecimalFormat df4 = new DecimalFormat("0000");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**This method used for delete the existing Data Sync file's field mapping**/
	@Override
	public void deleteExistingDataSyncMapping(Long mealSchoolId) {
		entityManager.createNativeQuery("Delete from DataSyncFieldMapping where mealSchoolId = :mealSchoolId")
		.setParameter("mealSchoolId", mealSchoolId).executeUpdate();
	}

	/**This method used for update the data sync file's field mapping details**/
	@Override
	public ServiceResponse updateDataSyncFieldMapping(List<DataSyncSchoolFieldMapping> dataSyncSchoolFieldMappings) {
		ServiceResponse serviceResponse = new ServiceResponse();
		for(DataSyncSchoolFieldMapping dataSyncSchoolFieldMapping : dataSyncSchoolFieldMappings){
			entityManager.persist(dataSyncSchoolFieldMapping);
		}
		serviceResponse.setStatusMessage("Field mapping updated successfully.");
		serviceResponse.setStatus("Success");
		serviceResponse.setStatusCode(200);
		return serviceResponse;
	}

	/**This method used for get the Data Sync file's field mapping details**/
	@Override
	public List<DataSyncSchoolFieldMapping> getDataSyncFieldMapping(Long mealSchoolId) {
		Query query = entityManager.createNativeQuery("SELECT fieldMapping.* FROM DataSyncFieldMapping as fieldMapping " +
                "WHERE fieldMapping.mealSchoolId = :mealSchoolId", DataSyncSchoolFieldMapping.class)
				.setParameter("mealSchoolId", mealSchoolId);
		logger.info("DAO method executing for get the Data Sync file's field mapping details");
        return query.getResultList();
	}
	
	/**This method used for update/insert the student data using data sync process 
	 * @throws Exception **/
	@Override
	public StudentCreateResp dataSyncStudents(List<StudentUser> students, Long mealSchoolId, 
			Date schoolYearEndDate, String processType) throws Exception {
		List<RejectedStudentsInfo> rejectedStudentsInfos = new ArrayList<RejectedStudentsInfo>();
		StudentCreateResp studentCreateResp = new StudentCreateResp();
		List<StudentUser> rejectedDueToInvalidData = new ArrayList<StudentUser>();
		MealSchool mealSchool = mealSchoolRepository.findBySchoolId(mealSchoolId);
		List<String> gradesList = new ArrayList<String>();
		for (SchoolType type : mealSchool.getSchool().getSchoolType()) {
			gradesList.addAll(type.getValues());
		}
		Set<String> studentIds = new HashSet<String>();
		StudentUser stdUser;
		ServiceResponse serviceResponseUpdate;
		int successInsertCount = 0;
		int successUpdateCount = 0;
		int failedInsertCount = 0;
		Set<String> failedInsertStudentIds = new HashSet<String>();
		Set<String> failedUpdateStudentIds = new HashSet<String>();
		int failedUpdateCount = 0;
		int skipRecCount = 0;
		String isdCode = countryDetailsRepository.getIsdCode(mealSchool.getCountryCode());
		Integer seq = 1;
		Boolean isMMGen = false;
		Integer schoolYear = students.size() > 0 ? students.get(0).getSchoolYear() : 2021;
		List<String> usedPins = studentUserRepository.getUsedPins(mealSchoolId, schoolYear);
		Map<String, String> moduleInfo = mealSchool.getModuleAccess();
		if(moduleInfo != null){
			String stdIdType = moduleInfo.get("Student Id Type") != null ? moduleInfo.get("Student Id Type") : "School Defined Id";
			if(stdIdType.equalsIgnoreCase("MM Generated Id")){
				String maxPin = studentUserRepository.getMaxPin(mealSchoolId, schoolYear);
				if(maxPin != null)
					seq = Integer.parseInt(maxPin)+1;
				if(seq > 9999)
					seq = 0001;
				isMMGen = true;
			}
		}
		for (StudentUser std : students) {
			try{
				stdUser = null;
				if (std.getGradeName() != null && gradesList.contains(std.getGradeName().toString()) && 
						std.getStudentId() != null && !std.getStudentId().isEmpty() && std.getParentuser() != null && 
						std.getParentuser().getUserName() != null && !std.getParentuser().getUserName().isEmpty()) {
					String mob = std.getMobileNo();
					if(mob != null){
						mob = mob.replaceAll("[^a-zA-Z0-9]", "");
						if(mob.length() < 10)
							mob = null;
						else if(mob.length() > 10)
							mob = "+"+mob;
						else
							mob = isdCode+mob;
						std.setMobileNo(mob);
					}
					stdUser = studentUserRepository.findByMealSchoolSchoolIdAndStudentIdAndSchoolYear(mealSchoolId,
							std.getStudentId(), std.getSchoolYear());
					if (stdUser != null && stdUser.getUserId() != null) {
						Integer previousEligStatus = mealManageAPIDao.getEligStatus(stdUser.getIsFreeMealEligible(), stdUser.getIsReducePriceEligible());
						serviceResponseUpdate = studentDataSyncUpdate(std, stdUser, schoolYearEndDate, processType, previousEligStatus, stdUser.getIsActive(),isMMGen);
						if (serviceResponseUpdate.getStatusCode() == 200){
							successUpdateCount++;
							if(serviceResponseUpdate.getStudentId() != null && !serviceResponseUpdate.getStudentId().isEmpty())
								studentIds.add(serviceResponseUpdate.getStudentId());
						}else if(serviceResponseUpdate.getStatusCode() == 202)
							skipRecCount++;
						else{
							failedUpdateCount++;
							failedUpdateStudentIds.add(std.getStudentId());
						}
						continue;
					}
					ParentUser usr = studentUserRepository.findByUsername(std.getParentuser().getUserName());
					usr = parentUserUpsert(usr, std);
					userAuthUpsert(usr, std);
					std.setCreatedBy(std.getLoggedUser());
					std.setCreatedOn(new Date());
					if(std.getIsFreeMealEligible() || std.getIsReducePriceEligible())
						std.setRecertPending("Y");
					std.setParentuser(usr);
					std.setMealSchool(mealSchool);
					std.setRole("ROLE_PARENT");
					
					if(isMMGen && (std.getPin() == null || std.getPin().trim().isEmpty())){
						while(usedPins.contains(df4.format(seq))){
							if(seq >= 9999)
								seq = 0001;
							seq++;
						}
						std.setPin(df4.format(seq));
						usedPins.add(df4.format(seq));
					}else
						std.setPin(df4.format(Integer.parseInt(std.getPin())));				
					entityManager.persist(std);
					successInsertCount++;
					mealManageAPIDao.addUpdateStudentEligibility(mealManageAPIDao.getEligStatus(std.getIsFreeMealEligible(),
							std.getIsReducePriceEligible()), null, false, null, schoolYearEndDate, processType, std);
					mealManageAPIDao.auditStudentStatus(std.getIsActive(), null, false, null, schoolYearEndDate, processType, std);
				} else {
					rejectedDueToInvalidData.add(std);
					logger.info("Data Sync process rejected record due to invalid data for student id: "+std.getStudentId()
						+", under school: "+mealSchoolId);
					failedInsertCount++;
					failedInsertStudentIds.add(std.getStudentId());
				}
			}catch(Exception e){
				logger.info("Failed to process the student with id: "+std.getStudentId());
				failedInsertCount++;
				failedInsertStudentIds.add(std.getStudentId());
			}			
		}

		if (rejectedDueToInvalidData.size() > 0) {
			RejectedStudentsInfo rejectedStudentsInfo = new RejectedStudentsInfo();
			rejectedStudentsInfo.setStatus("Failed to synced the following Students data");
			rejectedStudentsInfo.setErrorMessage("Invalid Data");
			rejectedStudentsInfo.setStudentUsers(rejectedDueToInvalidData);
			rejectedStudentsInfos.add(rejectedStudentsInfo);
			studentCreateResp.setStatus("Partially student records synced");
			logger.info("Partially student records synced");
		} else {
			logger.info("Students data synced successfully");
			studentCreateResp.setStatusMessage("Students data synced successfully.");
		}
		studentCreateResp.setStatusCode(200);
		studentCreateResp.setStatus("Success");
		studentCreateResp.setSuccessInsertCount(successInsertCount);
		studentCreateResp.setSuccessUpdateCount(successUpdateCount);
		studentCreateResp.setFailedInsertCount(failedInsertCount);
		studentCreateResp.setFailedUpdateCount(failedUpdateCount);
		studentCreateResp.setRejectedStudentsInfos(rejectedStudentsInfos);
		studentCreateResp.setSkipRecCount(skipRecCount);
		studentCreateResp.setStudentIds(studentIds);
		studentCreateResp.setFailedInsertStudentIds(new ArrayList<String>(failedInsertStudentIds));
		studentCreateResp.setFailedUpdateStudentIds(new ArrayList<String>(failedUpdateStudentIds));
		return studentCreateResp;
	}
	
	/**This method used for update the students through data sync process**/
	private ServiceResponse studentDataSyncUpdate(StudentUser studentUserReq, StudentUser studentUserResp, 
			Date schoolYearEndDate, String processType, Integer previousEligStatus, Boolean previousStatus, Boolean isMMGen) {
		ServiceResponse serviceResponse = new ServiceResponse();
		String studentId = null;
		Boolean isChanged = recordCompare(studentUserReq, studentUserResp);
		/** Check and Update/Skip the existing student info based on changes**/
		studentUserResp.setFirstName(studentUserReq.getFirstName());
		studentUserResp.setLastName(studentUserReq.getLastName());
		studentUserResp.setTeacherName(studentUserReq.getTeacherName());
		studentUserResp.setGradeName(studentUserReq.getGradeName());
		studentUserResp.setMobileNo(studentUserReq.getMobileNo());
		if(studentUserResp.getReCertificateDate() == null){
			studentUserResp.setIsReducePriceEligible(studentUserReq.getIsReducePriceEligible());
			studentUserResp.setIsFreeMealEligible(studentUserReq.getIsFreeMealEligible());
			studentUserResp.setDecisionReason(studentUserReq.getDecisionReason() != null ? studentUserReq.getDecisionReason() : "");
			studentUserResp.setCategory(studentUserReq.getCategory() != null ? studentUserReq.getCategory() : "");
		}		
		studentUserResp.setIsActive(studentUserReq.getIsActive());
		studentUserResp.setSchoolStudentId(studentUserReq.getSchoolStudentId());
		if(studentUserResp.getReCertificateDate() == null && (studentUserResp.getIsFreeMealEligible() || studentUserResp.getIsReducePriceEligible()))
			studentUserResp.setRecertPending("Y");
		
		//if(!isMMGen && studentUserReq.getPin() != null && !studentUserReq.getPin().trim().isEmpty())
		if(studentUserReq.getPin() != null && !studentUserReq.getPin().trim().isEmpty() && (studentUserResp.getPin() == null ||
				!df4.format(Integer.parseInt(studentUserReq.getPin())).equalsIgnoreCase(df4.format(Integer.parseInt(studentUserResp.getPin())))))
			studentUserResp.setPin(df4.format(Integer.parseInt(studentUserReq.getPin())));

		/** Get the parent user and update info if required **/
		ParentUser usr = studentUserRepository.findByUsername(studentUserReq.getParentuser().getUserName());
		if(usr == null){
			isChanged = true;
			if(studentUserResp.isRegister())
				studentId = studentUserReq.getStudentId();
		}else{
			if((!studentUserReq.getParentuser().getUserName().equalsIgnoreCase(studentUserResp.getParentuser().getUserName())) 
					|| (studentUserReq.getParentuser().getParentAltEmail() != null && studentUserResp.getParentuser().getParentAltEmail() == null)
					|| (studentUserReq.getParentuser().getParentAltEmail() != null && studentUserResp.getParentuser().getParentAltEmail() != null 
							&& !studentUserReq.getParentuser().getParentAltEmail().equalsIgnoreCase(studentUserResp.getParentuser().getParentAltEmail()))){
				isChanged = true;
				if(studentUserResp.isRegister())
					studentId = studentUserReq.getStudentId();
			}				
		}
		usr = parentUserUpsert(usr, studentUserReq);
		userAuthUpsert(usr, studentUserReq);
		studentUserResp.setParentuser(usr);
		studentUserResp.setModifiedBy(studentUserReq.getLoggedUser());
		studentUserResp.setModifiedOn(new Date());
		studentUserResp.setNumberStreetApt(studentUserReq.getNumberStreetApt());
		studentUserResp.setCityStateZip(studentUserReq.getCityStateZip());
		if(studentUserReq.getIsEnrollBCAndACPkt() != null && !studentUserReq.getIsEnrollBCAndACPkt())
			studentUserResp.setBeforeCare(false);
		studentUserResp.setIsEnrollBCAndACPkt(studentUserReq.getIsEnrollBCAndACPkt());
		Integer currentEligStatus = mealManageAPIDao.getEligStatus(studentUserResp.getIsFreeMealEligible(), 
				studentUserResp.getIsReducePriceEligible());
		if(isChanged != null && isChanged){
			if(currentEligStatus != previousEligStatus){
				try {
					studentUserResp.setRecertPending("N");
					studentUserResp.setReCertificateDate(sdf.parse(du.formatDateToString(new Date(), "yyyy-MM-dd", studentUserResp.getMealSchool().getSchoolTimezone())));
					if(!studentUserResp.getIsReducePriceEligible() && !studentUserResp.getIsFreeMealEligible()){
						studentUserResp.setDecisionReason(null);
						studentUserResp.setCategory(null);
						studentUserResp.setActualPrg(null);
					}
				} catch (ParseException e) {
					logger.error("Failed to update certification date for student id::"+studentUserResp.getStudentId()+" due to "+e.getMessage());
				}
			}
			entityManager.merge(studentUserResp);
			serviceResponse.setStatusCode(200);
			serviceResponse.setStatusMessage("Student updated successfully.");
			logger.info("Student updated successfully with Student ID: " + studentUserReq.getStudentId());
		}else{
			serviceResponse.setStatusCode(202);
		}
		
		if(currentEligStatus != previousEligStatus)
			mealManageAPIDao.addUpdateStudentEligibility(currentEligStatus, previousEligStatus, true, null, schoolYearEndDate, processType, studentUserResp);
		if(Boolean.compare(studentUserResp.getIsActive(), previousStatus) != 0)
			mealManageAPIDao.auditStudentStatus(studentUserResp.getIsActive(), previousStatus, true, null, schoolYearEndDate, processType, studentUserResp);
		serviceResponse.setStatus("Success");
		serviceResponse.setStudentId(studentId);
		return serviceResponse;
	}
	
	/**This method used for compare the old & new record**/
	private boolean recordCompare(StudentUser studentUserReq, StudentUser studentUserResp){
		Boolean isChanged = false;
		if(studentUserReq.getFirstName() != null && studentUserResp.getFirstName() != null && 
				!studentUserReq.getFirstName().equalsIgnoreCase(studentUserResp.getFirstName()))
			return true;
		if(studentUserReq.getFirstName() != studentUserResp.getFirstName() && 
				(studentUserReq.getFirstName() == null || studentUserResp.getFirstName() == null))
			return true;
		if(studentUserReq.getLastName() != null && studentUserResp.getLastName() != null && 
				!studentUserReq.getLastName().equalsIgnoreCase(studentUserResp.getLastName()))
			return true;
		if(studentUserReq.getLastName() != studentUserResp.getLastName() && 
				(studentUserReq.getLastName() == null || studentUserResp.getLastName() == null))
			return true;
		if(studentUserReq.getTeacherName() != null && studentUserResp.getTeacherName() != null && 
				!studentUserReq.getTeacherName().equalsIgnoreCase(studentUserResp.getTeacherName()))
			return true;
		if(studentUserReq.getTeacherName() != studentUserResp.getTeacherName() && 
				(studentUserReq.getTeacherName() == null || studentUserResp.getTeacherName() == null))
			return true;
		if(studentUserReq.getGradeName() != null && studentUserResp.getGradeName() != null && 
				!studentUserReq.getGradeName().toString().equalsIgnoreCase(studentUserResp.getGradeName().toString()))
			return true;
		if(studentUserReq.getMobileNo() != null && studentUserResp.getMobileNo() != null && 
				!studentUserReq.getMobileNo().equalsIgnoreCase(studentUserResp.getMobileNo()))
			return true;
		if(studentUserReq.getMobileNo() != studentUserResp.getMobileNo() && 
				(studentUserReq.getMobileNo() == null || studentUserResp.getMobileNo() == null))
			return true;
		if(studentUserReq.getIsReducePriceEligible() != null && studentUserResp.getIsReducePriceEligible() != null && 
				!studentUserReq.getIsReducePriceEligible().equals(studentUserResp.getIsReducePriceEligible()))
			return true;
		if(studentUserReq.getIsReducePriceEligible() != studentUserResp.getIsReducePriceEligible() && 
				(studentUserReq.getIsReducePriceEligible() == null || studentUserResp.getIsReducePriceEligible() == null))
			return true;
		if(studentUserReq.getIsFreeMealEligible() != null && studentUserResp.getIsFreeMealEligible() != null && 
				!studentUserReq.getIsFreeMealEligible().equals(studentUserResp.getIsFreeMealEligible()))
			return true;
		if(studentUserReq.getIsFreeMealEligible() != studentUserResp.getIsFreeMealEligible() && 
				(studentUserReq.getIsFreeMealEligible() == null || studentUserResp.getIsFreeMealEligible() == null))
			return true;
		if(studentUserReq.getIsActive() != null && studentUserResp.getIsActive() != null && 
				!studentUserReq.getIsActive().equals(studentUserResp.getIsActive()))
			return true;
		if(studentUserReq.getIsActive() != studentUserResp.getIsActive() && 
				(studentUserReq.getIsActive() == null || studentUserResp.getIsActive() == null))
			return true;
		if(studentUserReq.getSchoolStudentId() != studentUserResp.getSchoolStudentId() && 
				(studentUserReq.getSchoolStudentId() == null || studentUserResp.getSchoolStudentId() == null))
			return true;
		if(studentUserReq.getPin() != null && !studentUserReq.getPin().trim().isEmpty() && (studentUserResp.getPin() == null || 
				!df4.format(Integer.parseInt(studentUserReq.getPin())).equalsIgnoreCase(df4.format(Integer.parseInt(studentUserResp.getPin())))))
			return true;
		return isChanged;
	}
	
	/**This method used for create/update the user auth record**/
	private void userAuthUpsert(ParentUser usr, StudentUser std){
		UsersAuthInfo usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getUserName(), "ROLE_PARENT");
		if (usersAuthInfo == null) {
			usersAuthInfo = new UsersAuthInfo();
			usersAuthInfo.setUsername(usr.getUserName());
			usersAuthInfo.setRole("ROLE_PARENT");
			usersAuthInfo.setCreatedBy(std.getLoggedUser());
			usersAuthInfo.setCreatedOn(new Date());
			usersAuthInfo.setMobile(usr.getMobileNo());
			entityManager.persist(usersAuthInfo);
		} else if (usr.getMobileNo() != null && !usr.getMobileNo().equalsIgnoreCase("") 
				&& (usersAuthInfo.getMobile() == null || !usr.getMobileNo().equalsIgnoreCase(usersAuthInfo.getMobile()))){
			usersAuthInfo.setModifiedBy(std.getLoggedUser());
			usersAuthInfo.setModifiedOn(new Date());
			usersAuthInfo.setMobile(usr.getMobileNo());
			entityManager.merge(usersAuthInfo);
		}

		if (usr.getParentAltEmail() != null && !usr.getParentAltEmail().equalsIgnoreCase("")) {
			usersAuthInfo = usersAuthInfoRepository.findByUsernameAndRole(usr.getParentAltEmail(),
					"ROLE_PARENT");
			if (usersAuthInfo == null) {
				usersAuthInfo = new UsersAuthInfo();
				usersAuthInfo.setUsername(usr.getParentAltEmail());
				usersAuthInfo.setRole("ROLE_PARENT");
				usersAuthInfo.setCreatedBy(std.getLoggedUser());
				usersAuthInfo.setCreatedOn(new Date());
				usersAuthInfo.setMobile(usr.getMobileNo());
				entityManager.persist(usersAuthInfo);
			}
			if (usr.getMobileNo() != null && !usr.getMobileNo().equalsIgnoreCase("")
					&& (usersAuthInfo.getMobile() == null || 
					!usr.getMobileNo().equalsIgnoreCase(usersAuthInfo.getMobile()))) {
				usersAuthInfo.setModifiedBy(std.getLoggedUser());
				usersAuthInfo.setModifiedOn(new Date());
				usersAuthInfo.setMobile(usr.getMobileNo());
				entityManager.merge(usersAuthInfo);
			}
		}
	}
	
	/**This method used for the parent user upsert operation**/
	private ParentUser parentUserUpsert(ParentUser usr, StudentUser studentUserReq){
		if (usr == null) {
			usr = studentUserReq.getParentuser();
			usr.setCreatedBy(studentUserReq.getLoggedUser());
			usr.setCreatedOn(new Date());
			usr.setIsActive(false);
			usr.setMobileNo(studentUserReq.getMobileNo());
			usr.setRole("ROLE_PARENT");
			entityManager.persist(usr);
		} else {
			if(((studentUserReq.getMobileNo() != null && usr.getMobileNo() != null && 
					!studentUserReq.getMobileNo().equalsIgnoreCase(usr.getMobileNo())) 
					|| (studentUserReq.getMobileNo() != usr.getMobileNo() 
					&& (studentUserReq.getMobileNo() == null || usr.getMobileNo() == null)))
				|| ((studentUserReq.getParentuser().getParentAltEmail() != null && usr.getParentAltEmail() != null && 
					!studentUserReq.getParentuser().getParentAltEmail().equalsIgnoreCase(usr.getParentAltEmail())) 
					|| (studentUserReq.getParentuser().getParentAltEmail() != usr.getParentAltEmail() 
							&& (studentUserReq.getParentuser() == null || usr.getParentAltEmail() == null)))	
					){
				usr.setModifiedBy(studentUserReq.getLoggedUser());
				usr.setModifiedOn(new Date());
				usr.setMobileNo(studentUserReq.getMobileNo());
				usr.setParentAltEmail(studentUserReq.getParentuser().getParentAltEmail());
				entityManager.merge(usr);
			}
		}
		return usr;
	}

	/**This method used for update the student backup file link**/
	@Override
	public void studentBkpFileLink(String fileLink, Long mealSchoolId, Integer schoolYear, String type, String loggedUser)
			throws Exception {
		StudentBkpFileHistory studentBkpFileHistory = new StudentBkpFileHistory();
		studentBkpFileHistory.setBkpType(type.equalsIgnoreCase("dataSync")? "Data Sync":"Student Import");
		studentBkpFileHistory.setDate(new Date());
		studentBkpFileHistory.setFileS3Link(fileLink);
		studentBkpFileHistory.setMealSchoolId(mealSchoolId);
		studentBkpFileHistory.setSchoolYear(schoolYear);
		studentBkpFileHistory.setCreatedBy(loggedUser);
		entityManager.persist(studentBkpFileHistory);
		logger.info("Created the student export history record");
	}

}
