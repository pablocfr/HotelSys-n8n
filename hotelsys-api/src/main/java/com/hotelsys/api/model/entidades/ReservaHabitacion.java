package com.hotelsys.api.model.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@Data
@Table(name = "reserva_habitaciones")
public class ReservaHabitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "precio_noche_grabado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNocheGrabado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    @JsonIgnore
    @XmlTransient
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;
}
