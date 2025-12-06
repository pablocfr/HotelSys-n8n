package com.hotelsys.api.repository;

import com.hotelsys.api.model.catalogos.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, Integer> {
}
