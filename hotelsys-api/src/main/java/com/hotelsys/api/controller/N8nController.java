package com.hotelsys.api.controller;

import com.hotelsys.api.model.catalogos.TipoHabitacion;
import com.hotelsys.api.model.entidades.Cliente;
import com.hotelsys.api.model.entidades.Habitacion;
import com.hotelsys.api.model.entidades.Reserva;
import com.hotelsys.api.repository.TipoHabitacionRepository;
import com.hotelsys.api.service.ClienteService;
import com.hotelsys.api.service.HabitacionService;
import com.hotelsys.api.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/n8n")
@RequiredArgsConstructor
public class N8nController {

    private final ClienteService clienteService;
    private final HabitacionService habitacionService;
    private final ReservaService reservaService;
    private final TipoHabitacionRepository tipoHabitacionRepository;

    @GetMapping("/cliente/por-telefono/{telefono}")
    public ResponseEntity<Cliente> getClientePorTelefono(@PathVariable String telefono) {
        return clienteService.getClienteByTelefono(telefono)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/por-nombre/{nombreCompleto}")
    public ResponseEntity<Cliente> getClientePorNombre(@PathVariable String nombreCompleto) {
        return clienteService.getClienteByNombreCompleto(nombreCompleto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/habitaciones/disponibilidad")
    public ResponseEntity<List<Habitacion>> getDisponibilidadPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(habitacionService.getHabitacionesDisponibles(fechaInicio, fechaFin));
    }

    @GetMapping("/habitaciones/tipos-precios")
    public ResponseEntity<List<TipoHabitacion>> getTiposHabitacion() {
        return ResponseEntity.ok(tipoHabitacionRepository.findAll());
    }

    @GetMapping("/reservas/por-telefono/{telefono}")
    public ResponseEntity<List<Reserva>> getReservasPorTelefono(@PathVariable String telefono) {
        return ResponseEntity.ok(reservaService.getReservasByClienteTelefono(telefono));
    }

    @GetMapping("/reservas/por-nombre/{nombreCompleto}")
    public ResponseEntity<List<Reserva>> getReservasPorNombre(@PathVariable String nombreCompleto) {
        return ResponseEntity.ok(reservaService.getReservasByClienteNombreCompleto(nombreCompleto));
    }
}
