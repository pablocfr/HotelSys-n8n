package com.hotelsys.api.controller;

import com.hotelsys.api.dto.ProductoRequest;
import com.hotelsys.api.model.entidades.Producto;
import com.hotelsys.api.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getAllActiveProductos() {
        return ResponseEntity.ok(productoService.getAllActiveProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        return productoService.getProductoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody ProductoRequest productoRequest) {
        return ResponseEntity.ok(productoService.createProducto(productoRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Integer id, @RequestBody ProductoRequest productoRequest) {
        return productoService.updateProducto(id, productoRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/desactivar/{id}")
    public ResponseEntity<Producto> deleteLogicoProducto(@PathVariable Integer id) {
        return productoService.deleteLogicoProducto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> searchProductos(@RequestParam String term) {
        List<Producto> productos = productoService.getAllActiveProductos().stream()
                .filter(p -> p.getNombreProducto().toLowerCase().contains(term.toLowerCase()) && p.getStock() > 0)
                .toList();
        return ResponseEntity.ok(productos);
    }
}
