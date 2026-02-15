package com.mealManage.mealmodel.user;


import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode
public class DemoRequestHistoryId implements Serializable {

    private Long requestId;
    private Date modifiedOn;

    public DemoRequestHistoryId() {
    }

    public DemoRequestHistoryId(Long requestId, Date modifiedOn) {
        this.requestId = requestId;
        this.modifiedOn = modifiedOn;
    }
}