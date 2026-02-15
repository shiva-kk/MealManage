package com.mealManage.service;

import com.mealManage.mealmodel.user.DemoRequest;
import com.mealManage.mealmodel.user.DemoRequestHistory;
import com.mealManage.mealmodel.user.LeadStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface LeadManagementService {

    List<DemoRequest> getLeadRequests(String status, int offset, int limit, String sortCriteria);

    List<DemoRequestHistory> getLeadRequestHistory(Long requestId);

    ResponseEntity<Object> updateDemoRequests(DemoRequest demoRequest, Map<String, Object> errorResponse) throws Exception;

    List<LeadStatusCode> getLeadStatus();

    ResponseEntity<Object> deleteLeadRequest(Long requestId);
}
