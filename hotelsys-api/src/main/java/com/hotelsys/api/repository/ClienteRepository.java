package com.hotelsys.api.repository;

import com.hotelsys.api.model.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByActivoTrue();
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    // n8n integration
    Optional<Cliente> findByTelefono(String telefono);
    Optional<Cliente> findByNombreCompleto(String nombreCompleto);
}
