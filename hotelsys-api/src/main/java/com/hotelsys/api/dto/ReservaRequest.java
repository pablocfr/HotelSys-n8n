package com.hotelsys.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReservaRequest {
    private Integer clienteId;
    private LocalDate fechaCheckIn;
    private LocalDate fechaCheckOut;
    private BigDecimal descuento;
    private Integer estadoReservaId;
    private List<Integer> habitacionIds;
    private List<ReservaProductoRequest> productos;
}
