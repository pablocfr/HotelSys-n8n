package com.hotelsys.api.controller;

import com.hotelsys.api.dto.HabitacionRequest;
import com.hotelsys.api.model.entidades.Habitacion;
import com.hotelsys.api.service.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<Habitacion>> getAllActiveHabitaciones() {
        return ResponseEntity.ok(habitacionService.getAllActiveHabitaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> getHabitacionById(@PathVariable Integer id) {
        return habitacionService.getHabitacionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Habitacion> createHabitacion(@RequestBody HabitacionRequest habitacionRequest) {
        return ResponseEntity.ok(habitacionService.createHabitacion(habitacionRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habitacion> updateHabitacion(@PathVariable Integer id, @RequestBody HabitacionRequest habitacionRequest) {
        return habitacionService.updateHabitacion(id, habitacionRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/desactivar/{id}")
    public ResponseEntity<Habitacion> deleteLogicoHabitacion(@PathVariable Integer id) {
        return habitacionService.deleteLogicoHabitacion(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Habitacion>> getAvailableHabitaciones() {
        List<Habitacion> disponibles = habitacionService.getAllActiveHabitaciones().stream()
                .filter(h -> h.getEstadoHabitacion().getId() == 1) // ID 1 = Disponible
                .toList();
        return ResponseEntity.ok(disponibles);
    }
}
