package com.mealManage.dao;

import com.mealManage.domain.SchoolDataDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class SchoolDataDao {
	@PersistenceContext
    private EntityManager entityManager;

    

    public List<SchoolDataDTO> getAllSchoolData() {
        try {
            String nativeQuery = "SELECT ms.schoolId, ms.schoolName, ms.type, s.countryCode, s.State, s.city, ms.isPaymentEnabled, " +
                    "ms.sisIntegrationEnabled, c.name as catererName, d.name as districtName, COUNT(su.userId) " +
                    "FROM MealSchool_v2 ms " +
                    "INNER JOIN School_v2 s ON s.schoolId = ms.school_id " +
                    "INNER JOIN MealSchool_SchoolYear sy ON sy.mealSchool_schoolId=ms.schoolId " +
                    "AND (NOW() BETWEEN sy.sessionStartDateTime AND sy.sessionEndDateTime) " +
                    "LEFT JOIN Caterer c ON c.id=ms.catererId " +
                    "LEFT JOIN District d ON d.id=ms.districtId " +
                    "LEFT JOIN StudentUser_v2 su ON su.mealSchool_schoolId=ms.schoolId AND su.isActive = TRUE and su.schoolYear=sy.schoolYear " +
                    "WHERE ms.isActive = TRUE " +
                    "GROUP BY ms.schoolId, ms.schoolName, ms.type, s.countryCode, s.State, s.city, ms.isPaymentEnabled, " +
                    "ms.sisIntegrationEnabled, c.name, d.name";

            Query query = entityManager.createNativeQuery(nativeQuery);

            // Execute the query and get the result list
            List<Object[]> resultList = query.getResultList();

            // Convert the result list into a list of DTOs
            List<SchoolDataDTO> schoolDataList = new ArrayList<>();
            for (Object[] row : resultList) {
                SchoolDataDTO schoolData = new SchoolDataDTO();
                schoolData.setSchoolId((Long) row[0]);
                schoolData.setSchoolName((String) row[1]);
                schoolData.setSchoolType((String) row[2]);
                schoolData.setCountry((String) row[3]);
                schoolData.setState((String) row[4]);
                schoolData.setCity((String) row[5]);
                schoolData.setPaymentIntegration((Boolean) row[6]);
                schoolData.setSisIntegrationStatus((Boolean) row[7]);
                schoolData.setCatererName((String) row[8]);
                schoolData.setDistrictName((String) row[9]);
                schoolData.setNoOfStudents((Long) row[10]);
                schoolDataList.add(schoolData);
            }

            return schoolDataList;
        } catch (Exception e) {
            
            e.printStackTrace();
            
            return new ArrayList<>();
        }
    }
}
