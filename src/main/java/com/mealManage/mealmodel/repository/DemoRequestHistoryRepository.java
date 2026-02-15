package com.mealManage.mealmodel.repository;

import com.mealManage.mealmodel.user.DemoRequestHistory;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Api(value = "demoRequestHistory", description = "These API enabled for demoRequestHistory data")
public interface DemoRequestHistoryRepository extends JpaRepository<DemoRequestHistory, Long> {

    List<DemoRequestHistory> findByRequestId(Long requestId);
}