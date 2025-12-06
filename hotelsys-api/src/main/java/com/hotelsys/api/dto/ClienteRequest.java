package com.hotelsys.api.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequest {
    private String nombreCompleto;
    private String numeroDocumento;
    private String email;
    private String telefono;
    private Integer tipoDocumentoId;
}
