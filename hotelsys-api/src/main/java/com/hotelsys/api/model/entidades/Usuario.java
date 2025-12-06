package com.hotelsys.api.model.entidades;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_usuario", length = 100, nullable = false)
    private String nombreUsuario;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Column(name = "rol", length = 20, nullable = false)
    private String rol;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
