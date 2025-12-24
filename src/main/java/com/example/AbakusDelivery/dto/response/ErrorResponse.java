package com.example.AbakusDelivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private String path;
    private int status;
    private OffsetDateTime timestamp;
    private List<FieldErrorResponse> fieldErrors;
}
