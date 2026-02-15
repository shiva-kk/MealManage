package com.mealManage.util;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.school.DataSyncFieldConstants;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.school.SchoolYear;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.service.MMDataSyncService;

@Component
public class SQSListner {
	
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	@Autowired
	private MMDataSyncService mmDataSyncService;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
    @Value("${amazon.s3.accesskey}")
    private String amazonS3AccessKey;
    @Value("${amazon.s3.secretkey}")
    private String amazonS3SecretKey;
    @Value("${amazon.s3.dataSyncFile.folder}")
    private String dataSyncFileFolder;
    @Value("${amazon.s3.dataSyncBackupFile.folder}")
    private String dataSyncBackupFileFolder;
    @Autowired
    private MealManageAPIDao mealManageAPIDao;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@SqsListener(value = "${sqs.queue.name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void queueListener(String msg) throws Exception {
		//S3EventNotification s3EventNotification = new ObjectMapper().readValue(msg, S3EventNotification.class);
		String adminEmails = "";
		String fileName = "";
		try{
			String fileWithFolder = new ObjectMapper().readValue(msg, S3EventNotification.class).getRecords().get(0).getS3().getObject().getKey();
			fileName = fileWithFolder.split("/")[1];
			Long mealSchoolId = Long.parseLong(fileName.substring(0, fileName.length()-4).split("_")[1]);
			adminEmails = String.join(",", mealSchoolRepository.allAdminEmails(mealSchoolId));
			logger.info("Reading the latest uploaded file from S3 bucket for the data sync: "+fileWithFolder);
			dataSyncProcess(fileWithFolder, mealSchoolId, fileName, adminEmails);
		}catch(Exception e){
			logger.error("Failed to execute the Data Sync process due to "+e.getMessage());
			//logic for send failed email of data sync
			Map<String, String> emailReq = new HashMap<String, String>();
			emailReq.put("adminEmails", adminEmails);
			emailReq.put("status", "Failed");
			emailReq.put("failureError", "error occurred while reading data sync file.");
			logger.error("error occurred while reading data sync file.");
			sendNotificationUtil.dataSyncProcessStatus(emailReq);
		}finally{
			new File(fileName).delete();
		} 
		
	}
	
	/**This method used for read file from S3 and return byte array
	 * @throws Exception **/
	@SuppressWarnings("deprecation")
	private void dataSyncProcess(String fileWithFolder, Long mealSchoolId, String fileName, String adminEmails) throws Exception{
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(amazonS3AccessKey, amazonS3SecretKey);
		AmazonS3 s3Client = new AmazonS3Client(awsCreds).withRegion(Region.getRegion(Regions.US_EAST_1));
		
		//get the file content from s3 bucket
		S3Object s3object = s3Client.getObject(new GetObjectRequest(amazonS3Bucketname, fileWithFolder));
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		FileUtils.copyInputStreamToFile(inputStream, new File(fileName));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Boolean processStatus = true;
		Integer schoolYear = schoolYearRepository.schoolYearBySchoolAndDate(mealSchoolId, sdf.parse(sdf.format(new Date())));
		if(schoolYear == null || schoolYear < 2000){
			List<SchoolYear> schoolYearsObj = schoolYearRepository.latestSchoolYear(mealSchoolId);
			if(schoolYearsObj != null && schoolYearsObj.size() > 0){
				for(SchoolYear schoolYearObj : schoolYearsObj){
					if(new Date().after(schoolYearObj.getSessionEndDateTime())){
						schoolYear = schoolYearObj.getSchoolYear()+1;
						break;
					}
				}
			}else{
				//handle the failure if not found any configured school year and send failure email to the admin users.
				//logic for send failed email of data sync
				Map<String, String> emailReq = new HashMap<String, String>();
				emailReq.put("adminEmails", adminEmails);
				emailReq.put("status", "Failed");
				emailReq.put("failureError", "School year not created for the school yet. Please create school year first then proceed it again.");
				logger.error("School year not created for the school yet. Please create school year first then proceed it again.");
				sendNotificationUtil.dataSyncProcessStatus(emailReq);
				processStatus = false;
			}
		}		
		if(processStatus){
			//Read the file and build the required request data for data sync process
			List<StudentUser> studentUsers = buildReqDataForDataSync(mealSchoolId, fileName, schoolYear);
			Date schoolYearEndDate = schoolYearRepository.getSchoolYearEndDate(mealSchoolId, schoolYear);
			mmDataSyncService.dataSyncStudents(studentUsers, mealSchoolId, adminEmails, schoolYearEndDate, "Data Sync");
			//copying the file in backup folder
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
			File file = new File(fileName);
			String targeFolderFile = dataSyncBackupFileFolder+mealSchoolId+"_"+schoolYear+"/Students_"+mealSchoolId+"_"
					+sdf1.format(new Date())+"."+getFileExtension(file);
			s3Client.putObject(new PutObjectRequest(amazonS3Bucketname, targeFolderFile, file)
					.withCannedAcl(CannedAccessControlList.PublicRead));
		}
	}
	
	/*private List<StudentUser> buildReqDataForDataSync(Long mealSchoolId, String schoolYear, String fileName ) throws Exception{
		BufferedReader fileBufferReader = null;
		List<StudentUser> studentUsers = new ArrayList<StudentUser>();
		try {
			List<DataSyncSchoolFieldMapping> dataSyncSchoolFieldMappings = mealManageAPIDao.getDataSyncFieldMapping(mealSchoolId);
			if(dataSyncSchoolFieldMappings == null || dataSyncSchoolFieldMappings.size() < 1){
				logger.error("Failed to process as field mapping doesn't exist for the school "+mealSchoolId);
				throw new Exception("Failed to process as field mapping doesn't exist for the school "+mealSchoolId);
			}
			Map<String, DataSyncFieldConstants> dataSyncMapping = buildDataSyncFieldMappingResp(dataSyncSchoolFieldMappings);
			fileBufferReader = new BufferedReader(new FileReader(fileName));
			String strLine = "";
			StringTokenizer st = null;
			Map<Integer, String> fileHeaderMap = new HashMap<Integer, String>();
			strLine = fileBufferReader.readLine();
			st = new StringTokenizer(strLine, ",");
			int i = 0;
			while (st.hasMoreTokens()) {
				fileHeaderMap.put(i, st.nextToken());
				i++;
			}
			String fieldVal;
			int columnNum;
			String absoluteKey;
			OUTER_LOOP: while ((strLine = fileBufferReader.readLine()) != null) {
				st = new StringTokenizer(strLine, ",");
				columnNum = 0;
				StudentUser studentUser = new StudentUser();
				ParentUser parentUser = new ParentUser();
				while (st.hasMoreTokens()) {
					fieldVal = st.nextToken();
					absoluteKey = dataSyncMapping.get(fileHeaderMap.get(columnNum)).toString();
					switch (absoluteKey) {
					case "FirstName":
						studentUser.setFirstName(fieldVal);
						break;
					case "LastName":
						studentUser.setLastName(fieldVal);
						break;
					case "StudentID":
						studentUser.setStudentId(fieldVal);
						if (fieldVal == null || fieldVal.isEmpty())
							break OUTER_LOOP;
						break;
					case "ParentEmail":
						parentUser.setUserName(fieldVal);
						break;
					case "ParentAltEmail":
						parentUser.setParentAltEmail(fieldVal);
						break;
					case "ParentMobileNumb":
						studentUser.setMobileNo(fieldVal);
						parentUser.setMobileNo(fieldVal);
						break;
					case "TeacherName":
						studentUser.setTeacherName(fieldVal);
						break;
					case "ReducedPriceElig":
						studentUser.setIsReducePriceEligible(fieldVal.equalsIgnoreCase("Yes") ? true : false);
						break;
					case "FreeLunchElig":
						studentUser.setIsFreeMealEligible(fieldVal.equalsIgnoreCase("Yes") ? true : false);
						break;
					case "StudentGrade":
						studentUser.setGradeName(SchoolGrades.valueOf(fieldVal.toLowerCase()));
						break;
					}
					
					columnNum++;
				}
				studentUser.setParentuser(parentUser);
				studentUser.setSchoolYear(Integer.parseInt(schoolYear));
				studentUser.setLoggedUser("DataSyncProcess");
				studentUsers.add(studentUser);
			}
		}
		finally {
			try {
				fileBufferReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return studentUsers;
	}*/
	
	/**This method used for read csv file and build data**/
	private List<StudentUser> buildReqDataForDataSync(Long mealSchoolId, String fileName, Integer schoolYear) throws Exception{
		List<StudentUser> studentUsers = new ArrayList<StudentUser>();
		Map<DataSyncFieldConstants, String> fieldMapping = mmDataSyncService.getDataSyncFieldMapping(mealSchoolId);
		if(fieldMapping == null || fieldMapping.get(DataSyncFieldConstants.StudentID) == null || 
				fieldMapping.get(DataSyncFieldConstants.StudentID).toString().isEmpty()){
			logger.error("Failed to process as field mapping doesn't exist for the school "+mealSchoolId);
			throw new Exception("Failed to process as field mapping doesn't exist for the school "+mealSchoolId);
		}
		Reader reader = Files.newBufferedReader(Paths.get(fileName));
		//Map<String, String> gradeKeyVal = customGradeKeyVal();
		Map<String, String> gradeKeyVal = mealManageAPIDao.gradeBackMapByCountry(mealSchoolRepository.getSchoolCountry(mealSchoolId));
		CSVParser csvParser = null;
		try{
			csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
			String gradeVal = "";
			String enrollBAC = null;
	        for (CSVRecord csvRecord : csvParser) {
	            StudentUser studentUser = new StudentUser();
				ParentUser parentUser = new ParentUser();
				studentUser.setFirstName(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.FirstName)));
				studentUser.setLastName(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.LastName)));
				studentUser.setStudentId(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.StudentID)));
				parentUser.setUserName(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.ParentEmail)));
				if(fieldMapping.get(DataSyncFieldConstants.ParentAltEmail) != null 
						&& !fieldMapping.get(DataSyncFieldConstants.ParentAltEmail).toString().isEmpty())
					parentUser.setParentAltEmail(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.ParentAltEmail)));
				if((parentUser.getUserName() == null || parentUser.getUserName().trim().isEmpty())){
					if(parentUser.getParentAltEmail() != null && !parentUser.getParentAltEmail().trim().isEmpty())
						parentUser.setUserName(parentUser.getParentAltEmail());
					else
						parentUser.setUserName("NA");
	        	}
				if(fieldMapping.get(DataSyncFieldConstants.ParentMobileNumb) != null 
						&& !fieldMapping.get(DataSyncFieldConstants.ParentMobileNumb).toString().isEmpty()){
					studentUser.setMobileNo(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.ParentMobileNumb)));
					parentUser.setMobileNo(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.ParentMobileNumb)));
				}
				studentUser.setTeacherName(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.TeacherName)));
				studentUser.setSchoolStudentId(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.SchoolStudentId)));
				if(fieldMapping.get(DataSyncFieldConstants.ReducedPriceElig) != null 
						&& !fieldMapping.get(DataSyncFieldConstants.ReducedPriceElig).toString().isEmpty())
					studentUser.setIsReducePriceEligible(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.ReducedPriceElig))
						.equalsIgnoreCase("Reduced") ? true : false);
				if(fieldMapping.get(DataSyncFieldConstants.FreeLunchElig) != null 
						&& !fieldMapping.get(DataSyncFieldConstants.FreeLunchElig).toString().isEmpty())
					studentUser.setIsFreeMealEligible(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.FreeLunchElig))
						.equalsIgnoreCase("Free") ? true : false);
				gradeVal = gradeKeyVal.get(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.StudentGrade)));
				if(gradeVal == null || gradeVal.isEmpty())
					gradeVal = csvRecord.get(fieldMapping.get(DataSyncFieldConstants.StudentGrade));
				gradeVal = CommonUtil.validGrade(gradeVal);
				studentUser.setGradeName(SchoolGrades.valueOf(gradeVal) );
				if(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.EnrollmentStatus)) != null 
						&& csvRecord.get(fieldMapping.get(DataSyncFieldConstants.EnrollmentStatus)).toString().equalsIgnoreCase("Withdrawn")
					/*&& csvRecord.get(fieldMapping.get(DataSyncFieldConstants.ExitCode)) != null 
					&& csvRecord.get(fieldMapping.get(DataSyncFieldConstants.ExitCode)).toString().toUpperCase().contains("W")*/)
					studentUser.setIsActive(false);
				else{
					studentUser.setIsActive(true);
					studentUser.setEntryCode(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.EntryCode).toString()));
				}
				studentUser.setNumberStreetApt(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.NumberStreetApt)));
				studentUser.setCityStateZip(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.CityStateZip)));
				enrollBAC = csvRecord.get(fieldMapping.get(DataSyncFieldConstants.EnrollBCAndACPkt));

				if(enrollBAC != null && !enrollBAC.trim().equalsIgnoreCase(""))
					studentUser.setIsEnrollBCAndACPkt(enrollBAC.equalsIgnoreCase("TRUE")?true:false);
				studentUser.setParentuser(parentUser);
				studentUser.setSchoolYear(schoolYear);
				if(fieldMapping.get(DataSyncFieldConstants.Pin) != null)
					studentUser.setPin(csvRecord.get(fieldMapping.get(DataSyncFieldConstants.Pin)));
				studentUser.setLoggedUser("AutomatedDataSyncProcess");
				studentUsers.add(studentUser);
	      }
		}finally{
			csvParser.close();
		}
		return studentUsers;
	}
	
	private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
	
	/*public Map<String, String> customGradeKeyVal(){
		Map<String, String> gradeKeyVal = new HashMap<String, String>();
		gradeKeyVal.put("PK", "pk");
		gradeKeyVal.put("KG", "k");
		gradeKeyVal.put("K", "k");
		gradeKeyVal.put("01", "one");
		gradeKeyVal.put("02", "two");
		gradeKeyVal.put("03", "three");
		gradeKeyVal.put("04", "four");
		gradeKeyVal.put("05", "five");
		gradeKeyVal.put("06", "six");
		gradeKeyVal.put("07", "seven");
		gradeKeyVal.put("08", "eight");
		gradeKeyVal.put("09", "nine");
		gradeKeyVal.put("1", "one");
		gradeKeyVal.put("2", "two");
		gradeKeyVal.put("3", "three");
		gradeKeyVal.put("4", "four");
		gradeKeyVal.put("5", "five");
		gradeKeyVal.put("6", "six");
		gradeKeyVal.put("7", "seven");
		gradeKeyVal.put("8", "eight");
		gradeKeyVal.put("9", "nine");
		gradeKeyVal.put("10", "ten");
		gradeKeyVal.put("11", "eleven");
		gradeKeyVal.put("12", "twelve");
		gradeKeyVal.put("13", "thirteen");
		gradeKeyVal.put("Staff", "staff");

		gradeKeyVal.put("year_1", "year_1");
		gradeKeyVal.put("year_2", "year_2");
		gradeKeyVal.put("year_3", "year_3");
		gradeKeyVal.put("year_4", "year_4");
		gradeKeyVal.put("year_5", "year_5");
		gradeKeyVal.put("year_6", "year_6");
		gradeKeyVal.put("year_7", "year_7");
		gradeKeyVal.put("year_8", "year_8");
		gradeKeyVal.put("year_9", "year_9");
		gradeKeyVal.put("year_10", "year_10");
		gradeKeyVal.put("year_11", "year_11");	
		return gradeKeyVal;
	}*/

}
