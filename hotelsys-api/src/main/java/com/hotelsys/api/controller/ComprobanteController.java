package com.hotelsys.api.controller;

import com.hotelsys.api.model.entidades.Comprobante;
import com.hotelsys.api.service.ComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    @GetMapping
    public ResponseEntity<List<Comprobante>> getAllComprobantes() {
        return ResponseEntity.ok(comprobanteService.getAllComprobantes());
    }

    @PostMapping("/generar/{reservaId}")
    public ResponseEntity<Comprobante> generarComprobante(@PathVariable Integer reservaId) {
        return ResponseEntity.ok(comprobanteService.generarComprobante(reservaId));
    }

    @GetMapping("/descargar/{id}/{formato}")
    public ResponseEntity<InputStreamResource> descargarComprobante(@PathVariable Integer id, @PathVariable String formato) {
        ByteArrayInputStream bis;
        String filename;
        MediaType mediaType;

        if ("pdf".equalsIgnoreCase(formato)) {
            bis = comprobanteService.generarPdf(id);
            filename = "Comprobante-" + id + ".pdf";
            mediaType = MediaType.APPLICATION_PDF;
        } else if ("xml".equalsIgnoreCase(formato)) {
            bis = comprobanteService.generarXml(id);
            filename = "Comprobante-" + id + ".xml";
            mediaType = MediaType.APPLICATION_XML;
        } else {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(mediaType)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comprobante> getComprobanteById(@PathVariable Integer id) {
        return ResponseEntity.ok(comprobanteService.getComprobanteById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComprobante(@PathVariable Integer id) {
        comprobanteService.deleteComprobante(id);
        return ResponseEntity.noContent().build();
    }
}
