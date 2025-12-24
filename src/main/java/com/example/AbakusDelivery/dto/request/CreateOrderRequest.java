package com.example.AbakusDelivery.dto.request;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class CreateOrderRequest {

    private String comment;
    private List<OrderItemRequest> items;
}
