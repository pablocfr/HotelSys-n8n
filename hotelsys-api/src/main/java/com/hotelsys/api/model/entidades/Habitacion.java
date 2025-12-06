package com.hotelsys.api.model.entidades;

import com.hotelsys.api.model.catalogos.EstadoHabitacion;
import com.hotelsys.api.model.catalogos.TipoHabitacion;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "habitaciones")
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero", length = 10, nullable = false, unique = true)
    private String numero;

    @Column(name = "requiere_limpieza", nullable = false)
    private Boolean requiereLimpieza;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @ManyToOne
    @JoinColumn(name = "estado_habitacion_id", nullable = false)
    private EstadoHabitacion estadoHabitacion;
}
