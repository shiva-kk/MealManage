package com.mealManage.mealmodel.user;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "LeadStatusCode")
public class LeadStatusCode implements Serializable {

    private static final long serialVersionUID = 120640370652653795L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusCodeId", updatable = false, nullable = false)
    private Long statusCodeId;
    private String statusCodeDesc;

    public Long getStatusCodeId() {
        return statusCodeId;
    }

    public void setStatusCodeId(Long statusCodeId) {
        this.statusCodeId = statusCodeId;
    }

    public String getStatusCodeDesc() {
        return statusCodeDesc;
    }

    public void setStatusCodeDesc(String statusCodeDesc) {
        this.statusCodeDesc = statusCodeDesc;
    }
}
