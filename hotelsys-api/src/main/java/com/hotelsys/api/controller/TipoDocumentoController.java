package com.hotelsys.api.controller;

import com.hotelsys.api.model.catalogos.TipoDocumento;
import com.hotelsys.api.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
public class TipoDocumentoController {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    @GetMapping
    public ResponseEntity<List<TipoDocumento>> getAllTiposDocumento() {
        return ResponseEntity.ok(tipoDocumentoRepository.findAll());
    }
}
