package com.practice.Payment_Integration.dto.request;

import com.practice.Payment_Integration.config.StrictIntegerDeserializer;
import jakarta.validation.constraints.*;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Product price can have maximum 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Product stock is required")
    @Min(value = 0, message = "Product stock cannot be negative")
    @JsonDeserialize(using = StrictIntegerDeserializer.class)
    private Integer stock;
}
