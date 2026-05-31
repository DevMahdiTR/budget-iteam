package com.iteam.buget.core.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CategoryLimitRequest {
    @NotNull private Long categoryId;
    @NotNull @DecimalMin("0.01") private BigDecimal ceiling;
}
