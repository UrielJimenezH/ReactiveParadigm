package com.reactiveparadigm.reactiveParadigm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDto {
    private String phoneNumber;
    private String orderNumber;
    private String productCode;
}
