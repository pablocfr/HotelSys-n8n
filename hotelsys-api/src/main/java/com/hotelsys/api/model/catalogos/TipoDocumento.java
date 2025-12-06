package com.hotelsys.api.model.catalogos;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tipos_documento")
public class TipoDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "descripcion", length = 50, nullable = false)
    private String descripcion;
}
