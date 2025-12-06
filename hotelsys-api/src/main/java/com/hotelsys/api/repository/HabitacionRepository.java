package com.hotelsys.api.repository;

import com.hotelsys.api.model.entidades.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
    List<Habitacion> findByActivoTrue();
    Optional<Habitacion> findByNumero(String numero);
}
