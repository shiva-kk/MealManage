package com.mealManage.domain;

public class SchoolDataDTO {

    private Long schoolId;
    private String schoolName;
    private String schoolType;
    private String country;
    private String state;
    private String city;
    private Boolean paymentIntegration;
    private Boolean sisIntegrationStatus;
    private String catererName;
    private String districtName;
    private Long noOfStudents;

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getSchoolType() { return schoolType; }
    public void setSchoolType(String schoolType) { this.schoolType = schoolType; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Boolean getPaymentIntegration() { return paymentIntegration; }
    public void setPaymentIntegration(Boolean paymentIntegration) { this.paymentIntegration = paymentIntegration; }
    public Boolean getSisIntegrationStatus() { return sisIntegrationStatus; }
    public void setSisIntegrationStatus(Boolean sisIntegrationStatus) { this.sisIntegrationStatus = sisIntegrationStatus; }
    public String getCatererName() { return catererName; }
    public void setCatererName(String catererName) { this.catererName = catererName; }
    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }
    public Long getNoOfStudents() { return noOfStudents; }
    public void setNoOfStudents(Long noOfStudents) { this.noOfStudents = noOfStudents; }
}
