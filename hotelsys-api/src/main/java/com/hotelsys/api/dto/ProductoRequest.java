package com.hotelsys.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequest {
    private String nombreProducto;
    private BigDecimal precio;
    private Integer stock;
}
