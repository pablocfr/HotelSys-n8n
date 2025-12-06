package com.hotelsys.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Integer id;
    private String nombreUsuario;
    private String email;
    private String rol;
    private String token;
}
