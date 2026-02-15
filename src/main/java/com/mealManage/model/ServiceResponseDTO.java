package com.mealManage.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ServiceResponseDTO {

    private String statusMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
    private Object data;

    public static ServiceResponseDTO builder() { return new ServiceResponseDTO(); }
    public ServiceResponseDTO statusMessage(String v) { this.statusMessage = v; return this; }
    public ServiceResponseDTO errorMessage(String v) { this.errorMessage = v; return this; }
    public ServiceResponseDTO data(Object v) { this.data = v; return this; }
    public ServiceResponseDTO build() { return this; }
}
