package com.hotelsys.api.repository;

import com.hotelsys.api.model.entidades.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Integer> {
}