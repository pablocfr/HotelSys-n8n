package com.hotelsys.api.model.catalogos;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "estados_reserva")
public class EstadoReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "descripcion", length = 50, nullable = false)
    private String descripcion;
}
