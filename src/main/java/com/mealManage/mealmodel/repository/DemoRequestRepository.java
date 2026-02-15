package com.mealManage.mealmodel.repository;

import com.mealManage.mealmodel.user.DemoRequest;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Api(value = "demoRequests", description = "These API enabled for demorequest data")
public interface DemoRequestRepository extends PagingAndSortingRepository<DemoRequest, Long> {

    @RestResource(exported = false)
    /**This API disabled for delete the demo request data**/
    public void delete(DemoRequest demoRequest);

    List<DemoRequest> findAllByStatusCodeIdInAndActive(List<Long> statusCodeId, Boolean isActive, Pageable pageable);

    List<DemoRequest> findAllByActive(Boolean isActive, Pageable pageable);

    DemoRequest findByRequestId(Long requestId);
}