package com.hotelsys.api.model.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@Data
@Table(name = "reserva_productos")
public class ReservaProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_grabado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioGrabado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    @JsonIgnore
    @XmlTransient
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
