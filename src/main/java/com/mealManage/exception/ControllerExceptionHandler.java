package com.mealManage.exception;

import com.mealManage.model.ServiceResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({RuntimeException.class, DatabaseErrorException.class})
    public ResponseEntity<ServiceResponseDTO> handleRuntimException(Exception e) {
        return new ResponseEntity<>(ServiceResponseDTO.builder()
                .statusMessage(e.getMessage())
                .errorMessage(e.getCause() != null ? e.getCause().getMessage() : null)
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
