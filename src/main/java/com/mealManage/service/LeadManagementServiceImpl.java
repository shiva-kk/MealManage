package com.mealManage.service;

import com.mealManage.mealmodel.repository.DemoRequestHistoryRepository;
import com.mealManage.mealmodel.repository.DemoRequestRepository;
import com.mealManage.mealmodel.repository.LeadStatusCodeRepository;
import com.mealManage.mealmodel.user.DemoRequest;
import com.mealManage.mealmodel.user.DemoRequestHistory;
import com.mealManage.mealmodel.user.LeadStatusCode;
import com.mealManage.util.OffsetBasedPageRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = Exception.class)
public class LeadManagementServiceImpl implements LeadManagementService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DemoRequestRepository demoRequestRepository;
    private final DemoRequestHistoryRepository demoRequestHistoryRepository;
    private final LeadStatusCodeRepository leadStatusCodeRepository;

    @Autowired
    public LeadManagementServiceImpl(DemoRequestRepository demoRequestRepository,
            DemoRequestHistoryRepository demoRequestHistoryRepository,
            LeadStatusCodeRepository leadStatusCodeRepository) {
        this.demoRequestRepository = demoRequestRepository;
        this.demoRequestHistoryRepository = demoRequestHistoryRepository;
        this.leadStatusCodeRepository = leadStatusCodeRepository;
    }
    private List<LeadStatusCode> leadStatusCodes = null;
    private Map<String, Long> statusDescToCodeMap = null;

    @Override
    public List<DemoRequest> getLeadRequests(String status, int offset, int limit, String sortCriteria) {
        status = StringUtils.isNotBlank(status)? status : "";
        List<String> statusList = Arrays.asList(status.split(","));
        Sort.Direction sortDirection;
        /* By default setting offset,limit,status to 0,50,ALL respectively, if any of the information is missing*/
        if (offset < 0) {
            offset = 0;
        }
        if (limit <= 0) {
            limit = 50;
        }
        if (StringUtils.isBlank(status) || statusList.stream().anyMatch(s -> s.equalsIgnoreCase("ALL"))) {
            status = "ALL";
        }
        if (StringUtils.isNotBlank(sortCriteria)) {
            sortDirection = sortCriteria.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        } else {
            sortDirection = Sort.Direction.DESC;
        }

        Pageable pageable = new OffsetBasedPageRequest(offset, limit, sortDirection, "createdOn");

        if (status.equalsIgnoreCase("ALL")) {
            return demoRequestRepository.findAllByActive(true, pageable);
        }

        if (ObjectUtils.isEmpty(statusDescToCodeMap)) {
            leadStatusCodes = leadStatusCodeRepository.findAll();
            statusDescToCodeMap = leadStatusCodes.stream().collect(Collectors.toMap(LeadStatusCode::getStatusCodeDesc, LeadStatusCode::getStatusCodeId));
        }

        List<Long> statusCodeIds = statusList.stream()
                .map(statusCodeDesc -> statusDescToCodeMap.getOrDefault(statusCodeDesc, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (ObjectUtils.isEmpty(statusCodeIds)) {
            return new ArrayList<>();
        }
        return demoRequestRepository.findAllByStatusCodeIdInAndActive(statusCodeIds, true, pageable);
    }

    @Override
    public ResponseEntity<Object> updateDemoRequests(DemoRequest demoRequest, Map<String, Object> errorResponse) {

        if (demoRequest == null || demoRequest.getRequestId() == null) {
            errorResponse.put("errorCode", "400");
            errorResponse.put("errorDescription", "Missing input RequestId");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        DemoRequest response = Optional.ofNullable(demoRequestRepository.findOne(demoRequest.getRequestId()))
                .orElse(null);

        if (response == null) {
            errorResponse.put("errorCode", "400");
            errorResponse.put("errorDescription", "Invalid RequestId");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isNotBlank(demoRequest.getDateOfAction())
                && parseToLocalDate(demoRequest.getDateOfAction()) == null) {
            errorResponse.put("errorCode", "400");
            errorResponse.put("errorDescription", "Invalid Date Format for dateOfAction," +
                    " valid format is 'yyyy-MM-dd' (eg. 2021-09-30 ISO-8601 format");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isNotBlank(demoRequest.getFollowUpDate())
                && parseToLocalDate(demoRequest.getFollowUpDate()) == null) {
            errorResponse.put("errorCode", "400");
            errorResponse.put("errorDescription", "Invalid Date Format for followUpDate," +
                    " valid format is 'yyyy-MM-dd' (eg. 2021-09-30 ISO-8601 format");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            demoRequestHistoryRepository.saveAndFlush(getDemoRequestHistory(response, demoRequest));

            DemoRequest updateRequest = populateDemoRequestWithUpdateInfo(demoRequest, response);

            demoRequestRepository.save(updateRequest);
        } catch (Exception e) {
            logger.error("Failed to updateLeads: {} ", ExceptionUtils.getStackTrace(e));
            throw e;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public List<DemoRequestHistory> getLeadRequestHistory(Long requestId) {
        return demoRequestHistoryRepository.findByRequestId(requestId);
    }

    @Override
    public List<LeadStatusCode> getLeadStatus() {
        if (ObjectUtils.isEmpty(leadStatusCodes)) {
            leadStatusCodes = leadStatusCodeRepository.findAll();
        }
        return leadStatusCodes;
    }

    @Override
    public ResponseEntity<Object> deleteLeadRequest(Long requestId) {
        DemoRequest demoRequest = demoRequestRepository.findByRequestId(requestId);
        Map<String, Object> response = new HashMap<>();
        if(ObjectUtils.isEmpty(demoRequest)){
            response.put("errorCode", "400");
            response.put("errorDescription", "Invalid RequestId");

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        demoRequest.setActive(false);
        demoRequestRepository.save(demoRequest);

        response.put("status", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private DemoRequest populateDemoRequestWithUpdateInfo(DemoRequest demoRequest, DemoRequest response) {

        response.setModifiedOn(new Date());
        if (StringUtils.isNotBlank(demoRequest.getLoggedUser())) {
            response.setLoggedUser(demoRequest.getLoggedUser());
        } else {
            response.setLoggedUser("MealManageAdmin");
        }

        if (StringUtils.isNotBlank(demoRequest.getEmailAddress())) {
            response.setEmailAddress(demoRequest.getEmailAddress());
        }
        if (StringUtils.isNotBlank(demoRequest.getFirstName())) {
            response.setFirstName(demoRequest.getFirstName());
        }
        if (StringUtils.isNotBlank(demoRequest.getLastName())) {
            response.setLastName(demoRequest.getLastName());
        }
        if (StringUtils.isNotBlank(demoRequest.getSchoolName())) {
            response.setSchoolName(demoRequest.getSchoolName());
        }
        if (StringUtils.isNotBlank(demoRequest.getCity())) {
            response.setCity(demoRequest.getCity());
        }
        if (StringUtils.isNotBlank(demoRequest.getCountry())) {
            response.setCountry(demoRequest.getCountry());
        }
        if (StringUtils.isNotBlank(demoRequest.getMobileNo())) {
            response.setMobileNo(demoRequest.getMobileNo());
        }
        if (StringUtils.isNotBlank(demoRequest.getState())) {
            response.setState(demoRequest.getState());
        }
        if (StringUtils.isNotBlank(demoRequest.getRequestingFor())) {
            response.setRequestingFor(demoRequest.getRequestingFor());
        }
        if (demoRequest.getStatusCodeId() != null && demoRequest.getStatusCodeId() != 0) {
            response.setStatusCodeId(demoRequest.getStatusCodeId());
        }
        if (StringUtils.isNotBlank(demoRequest.getDateOfAction())) {
            response.setDateOfAction(demoRequest.getDateOfAction());
        }
        if (StringUtils.isNotBlank(demoRequest.getFollowUpDate())) {
            response.setFollowUpDate(demoRequest.getFollowUpDate());
        }
        if (StringUtils.isNotBlank(demoRequest.getComments())) {
            response.setComments(demoRequest.getComments());
        }
        if (!ObjectUtils.isEmpty(demoRequest.getActive())) {
            response.setActive(demoRequest.getActive());
        }

        return response;
    }

    private DemoRequestHistory getDemoRequestHistory(DemoRequest response, DemoRequest demoRequest) {
        DemoRequestHistory demoRequestHistory = new DemoRequestHistory();
        demoRequestHistory.setRequestId(response.getRequestId());
        demoRequestHistory.setEmailAddress(response.getEmailAddress());
        demoRequestHistory.setFirstName(response.getFirstName());
        demoRequestHistory.setLastName(response.getLastName());
        demoRequestHistory.setSchoolName(response.getSchoolName());
        demoRequestHistory.setCity(response.getCity());
        demoRequestHistory.setCountry(response.getCountry());
        demoRequestHistory.setMobileNo(response.getMobileNo());
        demoRequestHistory.setState(response.getState());
        demoRequestHistory.setRequestingFor(response.getRequestingFor());
        demoRequestHistory.setStatusCodeId(ObjectUtils.isEmpty(demoRequest.getStatusCodeId()) ? null : response.getStatusCodeId());
        demoRequestHistory.setDateOfAction(response.getDateOfAction());
        demoRequestHistory.setFollowUpDate(response.getFollowUpDate());
        demoRequestHistory.setComments(demoRequest.getComments());
        demoRequestHistory.setActive(demoRequest.getActive());
        demoRequestHistory.setCreatedBy(response.getCreatedBy());
        demoRequestHistory.setCreatedOn(response.getCreatedOn());
        demoRequestHistory.setModifiedBy(response.getModifiedBy());
        demoRequestHistory.setModifiedOn(response.getModifiedOn() != null ? response.getModifiedOn() : new Date());
        return demoRequestHistory;
    }

    private String parseToLocalDate(String date) {
        try {
            return LocalDate.parse(date).toString();
        } catch (Exception e) {
            logger.error("Failed to Parse the Data: {} ", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
}
