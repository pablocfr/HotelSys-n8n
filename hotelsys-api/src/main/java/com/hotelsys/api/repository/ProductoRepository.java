package com.hotelsys.api.repository;

import com.hotelsys.api.model.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByActivoTrue();
}
