package com.hotelsys.api.service;

import com.hotelsys.api.dto.ProductoRequest;
import com.hotelsys.api.model.entidades.Producto;
import com.hotelsys.api.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> getAllActiveProductos() {
        return productoRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProductoById(Integer id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public Producto createProducto(ProductoRequest productoRequest) {
        Producto producto = new Producto();
        producto.setNombreProducto(productoRequest.getNombreProducto());
        producto.setPrecio(productoRequest.getPrecio());
        producto.setStock(productoRequest.getStock());
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    @Transactional
    public Optional<Producto> updateProducto(Integer id, ProductoRequest productoRequest) {
        return productoRepository.findById(id).map(productoExistente -> {
            productoExistente.setNombreProducto(productoRequest.getNombreProducto());
            productoExistente.setPrecio(productoRequest.getPrecio());
            productoExistente.setStock(productoRequest.getStock());
            return productoRepository.save(productoExistente);
        });
    }

    @Transactional
    public Optional<Producto> deleteLogicoProducto(Integer id) {
        return productoRepository.findById(id).map(producto -> {
            producto.setActivo(false);
            return productoRepository.save(producto);
        });
    }
}
