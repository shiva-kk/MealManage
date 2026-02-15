package com.mealManage.mealmodel.school;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MealSchool_PartnerSchool_Mapping")
public class MealSchoolPartnerSchoolMapping {

    @Id
    @Column(name = "mealSchoolId")
    private Long mealSchoolId;
    @Column(name = "partnerSchoolId")
    private String partnerSchoolId;

    public Long getMealSchoolId() { return mealSchoolId; }
    public String getPartnerSchoolId() { return partnerSchoolId; }
}
