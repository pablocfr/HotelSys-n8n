package com.hotelsys.api.controller;

import com.hotelsys.api.model.catalogos.EstadoHabitacion;
import com.hotelsys.api.repository.EstadoHabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estados-habitacion")
@RequiredArgsConstructor
public class EstadoHabitacionController {

    private final EstadoHabitacionRepository estadoHabitacionRepository;

    @GetMapping
    public ResponseEntity<List<EstadoHabitacion>> getAllEstadosHabitacion() {
        return ResponseEntity.ok(estadoHabitacionRepository.findAll());
    }
}
