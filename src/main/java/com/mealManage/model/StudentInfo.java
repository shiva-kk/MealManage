package com.mealManage.model;

public class StudentInfo {

    private String schoolId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String grade;
    private String studentNumber;
    private String enrollmentStatus;
    private String mealEligibilityStatus;
    private String homeRoomTeacher;
    private String houseHoldParent1Email;
    private String houseHoldParent2Email;
    private String houseHoldParent1CellPhone;
    private String houseHoldParent2CellPhone;
    private String householdParentsMailingAddressLine1;
    private String householdParentsMailingAddressLine2;
    private String studentPOSPin;
    private String studentStateId;
    private String bAndACPacketComplete;

    public static StudentInfoBuilder builder() { return new StudentInfoBuilder(); }

    public String getSchoolId() { return schoolId; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getGrade() { return grade; }
    public String getStudentNumber() { return studentNumber; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public String getMealEligibilityStatus() { return mealEligibilityStatus; }
    public String getHomeRoomTeacher() { return homeRoomTeacher; }
    public String getHouseHoldParent1Email() { return houseHoldParent1Email; }
    public String getHouseHoldParent2Email() { return houseHoldParent2Email; }
    public String getHouseHoldParent1CellPhone() { return houseHoldParent1CellPhone; }
    public String getHouseHoldParent2CellPhone() { return houseHoldParent2CellPhone; }
    public String getHouseholdParentsMailingAddressLine1() { return householdParentsMailingAddressLine1; }
    public String getHouseholdParentsMailingAddressLine2() { return householdParentsMailingAddressLine2; }
    public String getStudentPOSPin() { return studentPOSPin; }
    public String getStudentStateId() { return studentStateId; }
    public String getBAndACPacketComplete() { return bAndACPacketComplete; }

    public static class StudentInfoBuilder {
        private final StudentInfo info = new StudentInfo();
        public StudentInfoBuilder schoolId(String v) { info.schoolId = v; return this; }
        public StudentInfoBuilder firstName(String v) { info.firstName = v; return this; }
        public StudentInfoBuilder middleName(String v) { info.middleName = v; return this; }
        public StudentInfoBuilder lastName(String v) { info.lastName = v; return this; }
        public StudentInfoBuilder grade(String v) { info.grade = v; return this; }
        public StudentInfoBuilder studentNumber(String v) { info.studentNumber = v; return this; }
        public StudentInfoBuilder enrollmentStatus(String v) { info.enrollmentStatus = v; return this; }
        public StudentInfoBuilder mealEligibilityStatus(String v) { info.mealEligibilityStatus = v; return this; }
        public StudentInfoBuilder homeRoomTeacher(String v) { info.homeRoomTeacher = v; return this; }
        public StudentInfoBuilder houseHoldParent1Email(String v) { info.houseHoldParent1Email = v; return this; }
        public StudentInfoBuilder houseHoldParent2Email(String v) { info.houseHoldParent2Email = v; return this; }
        public StudentInfoBuilder houseHoldParent1CellPhone(String v) { info.houseHoldParent1CellPhone = v; return this; }
        public StudentInfoBuilder houseHoldParent2CellPhone(String v) { info.houseHoldParent2CellPhone = v; return this; }
        public StudentInfoBuilder householdParentsMailingAddressLine1(String v) { info.householdParentsMailingAddressLine1 = v; return this; }
        public StudentInfoBuilder householdParentsMailingAddressLine2(String v) { info.householdParentsMailingAddressLine2 = v; return this; }
        public StudentInfoBuilder studentPOSPin(String v) { info.studentPOSPin = v; return this; }
        public StudentInfoBuilder studentStateId(String v) { info.studentStateId = v; return this; }
        public StudentInfoBuilder bAndACPacketComplete(String v) { info.bAndACPacketComplete = v; return this; }
        public StudentInfo build() { return info; }
    }
}
