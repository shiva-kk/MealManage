package com.mealManage.service;

import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.mealmodel.repository.*;
import com.mealManage.mealmodel.school.MealSchoolPartnerSchoolMapping;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.school.SchoolYear;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.menu.entities.EligibilityCode;
import com.mealManage.model.StudentInfo;
import com.mealManage.util.CommonUtil;
import com.mealManage.util.SendNotificationUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MealSchoolRepository mealSchoolRepository;

    @Autowired
    private SchoolYearRepository schoolYearRepository;

    @Autowired
    private SendNotificationUtil sendNotificationUtil;

    @Autowired
    private MealManageAPIDao mealManageAPIDao;

    @Autowired
    private MMDataSyncService mmDataSyncService;

    @Autowired
    private MealSchoolPartnerSchoolMappingRepository mealSchoolPartnerSchoolMappingRepository;

    @Autowired
    private EligibilityCodeRepo eligibilityCodeRepository;

    private Map<String, EligibilityCode> eligibilityCodeMap = null;

    public static String TYPE = "text/csv";

    public boolean process(File file) throws Exception {
        AtomicReference<String> adminEmails = new AtomicReference<>(StringUtils.EMPTY);
        AtomicBoolean processStatus = new AtomicBoolean(true);

        try {
            logger.info("fileName of the received file::{}", file.getName());
            if (!file.getName().contains(".csv")) {
                logger.error("unsupported fileType received, supported fileType is .csv");
                return false;
            }
            List<StudentInfo> studentInfoList = extractStudentInfoFromFile(file);
            Map<String, List<StudentInfo>> partnerSchoolIdToStudentInfoListMap = studentInfoList.stream().collect(
                    Collectors.groupingBy(StudentInfo::getSchoolId));

            if (CollectionUtils.isEmpty(eligibilityCodeMap)) {
                List<EligibilityCode> eligibilityCodeList = new ArrayList<>();
                eligibilityCodeRepository.findAll().forEach(eligibilityCodeList::add);
                eligibilityCodeMap = eligibilityCodeList.stream().collect(Collectors.toMap(EligibilityCode::getCode,
                        Function.identity()));
            }

            partnerSchoolIdToStudentInfoListMap.forEach((partnerSchoolId, studentInfoListFromMap) -> {
                logger.info("Started processing student records for partnerSchoolId::{}", partnerSchoolId);
                Long mealSchoolId = 0L;
                MealSchoolPartnerSchoolMapping mealSchoolPartnerSchoolMapping = getMealSchool(partnerSchoolId);
                if (mealSchoolPartnerSchoolMapping != null) {
                    mealSchoolId = mealSchoolPartnerSchoolMapping.getMealSchoolId();
                } else {
                    logger.error("mealSchoolId is not available for PartnerSchoolId::{}", partnerSchoolId);
                    throw new RuntimeException("mealSchoolId is not available for PartnerSchoolId , Breaking out of remaining loop!!");
                }

                logger.info("Started processing student records for partnerSchoolId/mealSchoolId::"
                        + partnerSchoolId + "/" + mealSchoolId);
                adminEmails.set(String.join(",", mealSchoolRepository.allAdminEmails(mealSchoolId)));

                Integer schoolYear = schoolYearRepository.schoolYearBySchoolAndDate(mealSchoolId, getFormattedDate());
                if (schoolYear == null || schoolYear < 2000) {
                    List<SchoolYear> schoolYearsObj = schoolYearRepository.latestSchoolYear(mealSchoolId);
                    if (schoolYearsObj != null && !schoolYearsObj.isEmpty()) {
                        for (SchoolYear schoolYearObj : schoolYearsObj) {
                            if (new Date().after(schoolYearObj.getSessionEndDateTime())) {
                                schoolYear = schoolYearObj.getSchoolYear() + 1;
                                break;
                            }
                        }
                    } else {
                        //handle the failure if not found any configured school year and send failure email to the admin users.
                        //logic for send failed email of data sync
                        Map<String, String> emailReq = new HashMap<>();
                        emailReq.put("adminEmails", adminEmails.get());
                        emailReq.put("status", "Failed");
                        emailReq.put("failureError", "School year not created for the school yet. Please create school year first then proceed it again.");
                        logger.error("School year not created for the school yet. Please create school year first then proceed it again.");
                        sendNotificationUtil.dataSyncProcessStatus(emailReq);
                        processStatus.set(false);
                    }
                }

                if (processStatus.get()) {
                    //Read the file and build the required request data for data sync process
                    List<StudentUser> studentUsers = buildReqDataForDataSync(mealSchoolId, studentInfoListFromMap, schoolYear);
                    Date schoolYearEndDate = schoolYearRepository.getSchoolYearEndDate(mealSchoolId, schoolYear);
                    mmDataSyncService.dataSyncStudents(studentUsers, mealSchoolId, adminEmails.get(), schoolYearEndDate, "Data Sync");
                } else {
                    throw new RuntimeException("fileProcessing failed, Breaking out of remaining loop!!");
                }
                logger.info("Successfully processed " + studentInfoListFromMap.size() + " student records for partnerSchoolId/mealSchoolId::"
                        + partnerSchoolId + "/" + mealSchoolId);
            });

        } catch (Exception e) {
            logger.error("Error while processing the file: {}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return processStatus.get();
    }

    /**
     * This method used for read csv file and build data
     **/
    private List<StudentUser> buildReqDataForDataSync(Long mealSchoolId, List<StudentInfo> studentInfoList, Integer schoolYear) {
        List<StudentUser> studentUsers = new ArrayList<>();
        Map<String, String> gradeKeyVal = mealManageAPIDao.gradeBackMapByCountry(mealSchoolRepository.getSchoolCountry(mealSchoolId));
        AtomicReference<String> gradeVal = new AtomicReference<>(StringUtils.EMPTY);
        studentInfoList.forEach(studentInfo -> {
            StudentUser studentUser = new StudentUser();
            ParentUser parentUser = new ParentUser();
            studentUser.setFirstName(studentInfo.getFirstName());
            studentUser.setLastName(studentInfo.getLastName());
            //studentUser.setStudentId(studentInfo.getStudentStateId());
            studentUser.setStudentId(studentInfo.getStudentNumber());
            parentUser.setUserName(studentInfo.getHouseHoldParent1Email());
            if (StringUtils.isNotBlank(studentInfo.getHouseHoldParent2Email())) {
                parentUser.setParentAltEmail(studentInfo.getHouseHoldParent2Email());
            }

            if (StringUtils.isBlank(parentUser.getUserName())) {
                if (StringUtils.isNotBlank(studentInfo.getHouseHoldParent2Email())) {
                    parentUser.setUserName(parentUser.getParentAltEmail());
                } else {
                    parentUser.setUserName("NA");
                }
            }

            if (StringUtils.isNotBlank(studentInfo.getHouseHoldParent1CellPhone())) {
                studentUser.setMobileNo(studentInfo.getHouseHoldParent1CellPhone());
                parentUser.setMobileNo(studentInfo.getHouseHoldParent1CellPhone());
            }
            studentUser.setTeacherName(studentInfo.getHomeRoomTeacher());
            //studentUser.setSchoolStudentId(studentInfo.getStudentNumber());
            studentUser.setSchoolStudentId(studentInfo.getStudentStateId());
            if (StringUtils.isNotBlank(studentInfo.getMealEligibilityStatus())) {
                EligibilityCode eligibilityCode =
                        eligibilityCodeMap.getOrDefault(studentInfo.getMealEligibilityStatus(), new EligibilityCode());
                studentUser.setIsReducePriceEligible(eligibilityCode.isRedElig());
                studentUser.setIsFreeMealEligible(eligibilityCode.isFreeElig());
                studentUser.setDecisionReason(eligibilityCode.getCode());
                studentUser.setCategory(eligibilityCode.getCodeDesc());
            }
            gradeVal.set(gradeKeyVal.getOrDefault(studentInfo.getGrade(), ""));
            if (StringUtils.isBlank(gradeVal.get())) {
                gradeVal.set(studentInfo.getGrade());
            }
            gradeVal.set(CommonUtil.validGrade(gradeVal.get()));
            studentUser.setGradeName(SchoolGrades.valueOf(gradeVal.get()));
            studentUser.setIsActive(StringUtils.isNotBlank(studentInfo.getEnrollmentStatus())
                    && studentInfo.getEnrollmentStatus().equalsIgnoreCase("Active"));
            studentUser.setNumberStreetApt(studentInfo.getHouseholdParentsMailingAddressLine1());
            studentUser.setCityStateZip(studentInfo.getHouseholdParentsMailingAddressLine2());

            if (StringUtils.isNotBlank(studentInfo.getBAndACPacketComplete())) {
                studentUser.setIsEnrollBCAndACPkt("TRUE".equalsIgnoreCase(studentInfo.getBAndACPacketComplete())
                        ? Boolean.TRUE : Boolean.FALSE);
            }
            studentUser.setParentuser(parentUser);
            studentUser.setSchoolYear(schoolYear);
            studentUser.setPin(studentInfo.getStudentPOSPin());
            studentUser.setLoggedUser("AutomatedDataSyncProcess");
            studentUsers.add(studentUser);
        });

        return studentUsers;
    }

    private List<StudentInfo> extractStudentInfoFromFile(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
        Iterable<CSVRecord> csvRecords = csvParser.getRecords();
        List<StudentInfo> studentInfoList = new ArrayList<>();
        for (CSVRecord csvRecord : csvRecords) {
            StudentInfo studentInfo = StudentInfo.builder()
                    .schoolId(csvRecord.get(0))
                    .firstName(csvRecord.get(1))
                    .middleName(csvRecord.get(2))
                    .lastName(csvRecord.get(3))
                    .grade(csvRecord.get(4))
                    .studentNumber(csvRecord.get(5))
                    .enrollmentStatus(csvRecord.get(6))
                    .mealEligibilityStatus(csvRecord.get(7))
//                    .lunchStatusCode(csvRecord.get(7))
//                    .lunchStatusCode1(csvRecord.get(8))
                    .homeRoomTeacher(csvRecord.get(8))
                    .houseHoldParent1Email(csvRecord.get(9))
                    .houseHoldParent2Email(csvRecord.get(10))
                    .houseHoldParent1CellPhone(csvRecord.get(11))
                    .houseHoldParent2CellPhone(csvRecord.get(12))
                    .householdParentsMailingAddressLine1(csvRecord.get(13))
                    .householdParentsMailingAddressLine2(csvRecord.get(14))
//                    .enrollmentEntryCode(csvRecord.get(16))
//                    .withdrawalCode(csvRecord.get(17))
                    .studentPOSPin(csvRecord.get(15))
                    .studentStateId(csvRecord.get(16))
                    .bAndACPacketComplete(csvRecord.get(17))
                    .build();
            studentInfoList.add(studentInfo);
        }
        return studentInfoList;
    }

    private MealSchoolPartnerSchoolMapping getMealSchool(String partnerSchoolId) {
        return mealSchoolPartnerSchoolMappingRepository.findByPartnerSchoolId(partnerSchoolId).orElse(null);
    }

    private Date getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            logger.error("Error while formatting the Date: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
}
