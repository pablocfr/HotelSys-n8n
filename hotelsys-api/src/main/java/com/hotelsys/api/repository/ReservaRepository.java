package com.hotelsys.api.repository;

import com.hotelsys.api.model.entidades.Habitacion;
import com.hotelsys.api.model.entidades.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByActivoTrue();
    // n8n integration
    List<Reserva> findByClienteTelefono(String telefono);
    List<Reserva> findByClienteNombreCompleto(String nombreCompleto);

    @Query("SELECT h FROM Habitacion h WHERE h.activo = true AND h.id NOT IN (" +
            "SELECT rh.habitacion.id FROM ReservaHabitacion rh JOIN rh.reserva r " +
            "WHERE r.activo = true AND (r.fechaCheckIn < :fechaFin AND r.fechaCheckOut > :fechaInicio))")
    List<Habitacion> findHabitacionesDisponiblesPorFechas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
}
