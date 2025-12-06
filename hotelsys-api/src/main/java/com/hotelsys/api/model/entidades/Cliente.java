package com.hotelsys.api.model.entidades;

import com.hotelsys.api.model.catalogos.TipoDocumento;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_completo", length = 200, nullable = false)
    private String nombreCompleto;

    @Column(name = "numero_documento", length = 20, nullable = false, unique = true)
    private String numeroDocumento;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TipoDocumento tipoDocumento;
}
