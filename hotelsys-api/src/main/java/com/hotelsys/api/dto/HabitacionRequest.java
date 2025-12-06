package com.hotelsys.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HabitacionRequest {
    private String numero;
    private Boolean requiereLimpieza;
    private Integer tipoHabitacionId;
    private Integer estadoHabitacionId;
}
