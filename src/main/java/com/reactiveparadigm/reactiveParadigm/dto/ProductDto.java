package com.reactiveparadigm.reactiveParadigm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDto {
    private String productId;
    private String productCode;
    private String productName;
    private Float score;
}
