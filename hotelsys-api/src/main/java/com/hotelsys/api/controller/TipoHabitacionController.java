package com.hotelsys.api.controller;

import com.hotelsys.api.model.catalogos.TipoHabitacion;
import com.hotelsys.api.repository.TipoHabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-habitacion")
@RequiredArgsConstructor
public class TipoHabitacionController {

    private final TipoHabitacionRepository tipoHabitacionRepository;

    @GetMapping
    public ResponseEntity<List<TipoHabitacion>> getAllTiposHabitacion() {
        return ResponseEntity.ok(tipoHabitacionRepository.findAll());
    }
}
