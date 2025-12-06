package com.hotelsys.api.model.entidades;

import com.hotelsys.api.model.catalogos.EstadoReserva;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

@Entity
@Data
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_check_in", nullable = false)
    private LocalDate fechaCheckIn;

    @Column(name = "fecha_check_out", nullable = false)
    private LocalDate fechaCheckOut;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento;

    @Column(name = "monto_total_calculado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotalCalculado;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "estado_facturacion", length = 20, nullable = false)
    private String estadoFacturacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "estado_reserva_id", nullable = false)
    private EstadoReserva estadoReserva;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaHabitacion> habitaciones;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaProducto> productos;

    @OneToOne(mappedBy = "reserva", fetch = FetchType.LAZY)
    private Comprobante comprobante;

    @JsonProperty("comprobanteId")
    public Integer getComprobanteId() {
        return (comprobante != null) ? comprobante.getId() : null;
    }
}
