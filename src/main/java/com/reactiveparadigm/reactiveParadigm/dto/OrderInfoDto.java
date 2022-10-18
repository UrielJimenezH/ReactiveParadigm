package com.reactiveparadigm.reactiveParadigm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class OrderInfoDto {
    private String orderNumber;
    private String userName;
    private String phoneNumber;
    private String productCode;
    private String productName;
    private String productId;

    public OrderInfoDto(OrderDto order, ProductDto productDto) {
        orderNumber = order.getOrderNumber();
        phoneNumber = order.getPhoneNumber();
        productCode = order.getProductCode();

        productName = productDto.getProductName();
        productId = productDto.getProductId();
    }
}
