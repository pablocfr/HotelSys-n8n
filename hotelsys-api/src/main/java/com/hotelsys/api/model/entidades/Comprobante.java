package com.hotelsys.api.model.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotelsys.api.config.LocalDateTimeAdapter;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "comprobantes")
@XmlRootElement(name = "Comprobante")
@XmlAccessorType(XmlAccessType.FIELD)
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    @XmlTransient
    @JsonIgnore
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @Column(name = "serie_numero", length = 20, nullable = false)
    private String serieNumero;

    @Column(name = "fecha_emision", nullable = false)
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime fechaEmision;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @JsonProperty("clienteNombre")
    public String getClienteNombre() {
        return reserva != null && reserva.getCliente() != null ? reserva.getCliente().getNombreCompleto() : null;
    }

    @JsonProperty("clienteDocumento")
    public String getClienteDocumento() {
        return reserva != null && reserva.getCliente() != null ? reserva.getCliente().getNumeroDocumento() : null;
    }

    @JsonProperty("detalleHabitaciones")
    public List<String> getDetalleHabitaciones() {
        return reserva != null ? reserva.getHabitaciones().stream()
                .map(h -> "Hospedaje - Habitación " + h.getHabitacion().getNumero())
                .collect(Collectors.toList()) : null;
    }

    @JsonProperty("detalleProductos")
    public List<String> getDetalleProductos() {
        return reserva != null ? reserva.getProductos().stream()
                .map(p -> p.getCantidad() + "x " + p.getProducto().getNombreProducto())
                .collect(Collectors.toList()) : null;
    }

    @XmlElement(name = "Cliente")
    public String getXmlClienteNombre() { return getClienteNombre(); }

    @XmlElement(name = "ClienteDocumento")
    public String getXmlClienteDocumento() { return getClienteDocumento(); }

    public enum TipoComprobante {
        BOLETA, FACTURA
    }
}