package com.hotelsys.api.dto;

import lombok.Data;

@Data
public class ReservaProductoRequest {
    private Integer productoId;
    private Integer cantidad;
}
