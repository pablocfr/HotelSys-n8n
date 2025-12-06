package com.hotelsys.api.model.catalogos;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "tipos_habitacion")
public class TipoHabitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "descripcion", length = 100, nullable = false)
    private String descripcion;

    @Column(name = "precio_base_noche", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBaseNoche;
}
