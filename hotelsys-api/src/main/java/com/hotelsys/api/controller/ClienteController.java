package com.hotelsys.api.controller;

import com.hotelsys.api.dto.ClienteRequest;
import com.hotelsys.api.model.entidades.Cliente;
import com.hotelsys.api.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> getAllActiveClientes() {
        return ResponseEntity.ok(clienteService.getAllActiveClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Integer id) {
        return clienteService.getClienteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.ok(clienteService.createCliente(clienteRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Integer id, @RequestBody ClienteRequest clienteRequest) {
        return clienteService.updateCliente(id, clienteRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/desactivar/{id}")
    public ResponseEntity<Cliente> deleteLogicoCliente(@PathVariable Integer id) {
        return clienteService.deleteLogicoCliente(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> searchClientes(@RequestParam String term) {
        // Esta es una búsqueda simple. Se puede mejorar con Spring Data Specifications.
        List<Cliente> clientes = clienteService.getAllActiveClientes().stream()
                .filter(c -> c.getNombreCompleto().toLowerCase().contains(term.toLowerCase()) ||
                        c.getNumeroDocumento().contains(term))
                .toList();
        return ResponseEntity.ok(clientes);
    }
}
