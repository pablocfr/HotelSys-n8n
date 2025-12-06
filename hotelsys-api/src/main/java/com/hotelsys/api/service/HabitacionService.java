package com.hotelsys.api.service;

import com.hotelsys.api.dto.HabitacionRequest;
import com.hotelsys.api.model.catalogos.EstadoHabitacion;
import com.hotelsys.api.model.catalogos.TipoHabitacion;
import com.hotelsys.api.model.entidades.Habitacion;
import com.hotelsys.api.repository.EstadoHabitacionRepository;
import com.hotelsys.api.repository.HabitacionRepository;
import com.hotelsys.api.repository.ReservaRepository;
import com.hotelsys.api.repository.TipoHabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final EstadoHabitacionRepository estadoHabitacionRepository;
    private final ReservaRepository reservaRepository;

    @Transactional(readOnly = true)
    public List<Habitacion> getAllActiveHabitaciones() {
        return habitacionRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Habitacion> getHabitacionById(Integer id) {
        return habitacionRepository.findById(id);
    }

    @Transactional
    public Habitacion createHabitacion(HabitacionRequest habitacionRequest) {
        habitacionRepository.findByNumero(habitacionRequest.getNumero()).ifPresent(h -> {
            throw new RuntimeException("El número de habitación ya está registrado.");
        });

        TipoHabitacion tipoHabitacion = tipoHabitacionRepository.findById(habitacionRequest.getTipoHabitacionId())
                .orElseThrow(() -> new RuntimeException("Tipo de habitación no encontrado"));
        EstadoHabitacion estadoHabitacion = estadoHabitacionRepository.findById(habitacionRequest.getEstadoHabitacionId())
                .orElseThrow(() -> new RuntimeException("Estado de habitación no encontrado"));

        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(habitacionRequest.getNumero());
        habitacion.setRequiereLimpieza(habitacionRequest.getRequiereLimpieza());
        habitacion.setTipoHabitacion(tipoHabitacion);
        habitacion.setEstadoHabitacion(estadoHabitacion);
        habitacion.setActivo(true);

        return habitacionRepository.save(habitacion);
    }

    @Transactional
    public Optional<Habitacion> updateHabitacion(Integer id, HabitacionRequest habitacionRequest) {
        Optional<Habitacion> habitacionExistenteConMismoNumero = habitacionRepository.findByNumero(habitacionRequest.getNumero());
        if (habitacionExistenteConMismoNumero.isPresent() && !habitacionExistenteConMismoNumero.get().getId().equals(id)) {
            throw new RuntimeException("El número de habitación ya pertenece a otra habitación.");
        }

        return habitacionRepository.findById(id).map(habitacionExistente -> {
            TipoHabitacion tipoHabitacion = tipoHabitacionRepository.findById(habitacionRequest.getTipoHabitacionId())
                    .orElseThrow(() -> new RuntimeException("Tipo de habitación no encontrado"));
            EstadoHabitacion estadoHabitacion = estadoHabitacionRepository.findById(habitacionRequest.getEstadoHabitacionId())
                    .orElseThrow(() -> new RuntimeException("Estado de habitación no encontrado"));

            habitacionExistente.setNumero(habitacionRequest.getNumero());
            habitacionExistente.setRequiereLimpieza(habitacionRequest.getRequiereLimpieza());
            habitacionExistente.setTipoHabitacion(tipoHabitacion);
            habitacionExistente.setEstadoHabitacion(estadoHabitacion);
            return habitacionRepository.save(habitacionExistente);
        });
    }

    @Transactional
    public Optional<Habitacion> deleteLogicoHabitacion(Integer id) {
        return habitacionRepository.findById(id).map(habitacion -> {
            habitacion.setActivo(false);
            return habitacionRepository.save(habitacion);
        });
    }

    // n8n integration
    @Transactional(readOnly = true)
    public List<Habitacion> getHabitacionesDisponibles(LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findHabitacionesDisponiblesPorFechas(fechaInicio, fechaFin);
    }
}
