package com.hotelsys.api.service;

import com.hotelsys.api.dto.ClienteRequest;
import com.hotelsys.api.model.catalogos.TipoDocumento;
import com.hotelsys.api.model.entidades.Cliente;
import com.hotelsys.api.repository.ClienteRepository;
import com.hotelsys.api.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    @Transactional(readOnly = true)
    public List<Cliente> getAllActiveClientes() {
        return clienteRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> getClienteById(Integer id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente createCliente(ClienteRequest clienteRequest) {
        clienteRepository.findByNumeroDocumento(clienteRequest.getNumeroDocumento()).ifPresent(c -> {
            throw new RuntimeException("El número de documento ya está registrado.");
        });
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(clienteRequest.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

        Cliente cliente = new Cliente();
        cliente.setNombreCompleto(clienteRequest.getNombreCompleto());
        cliente.setNumeroDocumento(clienteRequest.getNumeroDocumento());
        cliente.setEmail(clienteRequest.getEmail());
        cliente.setTelefono(clienteRequest.getTelefono());
        cliente.setTipoDocumento(tipoDocumento);
        cliente.setActivo(true);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Optional<Cliente> updateCliente(Integer id, ClienteRequest clienteRequest) {
        Optional<Cliente> clienteExistenteConMismoDocumento = clienteRepository.findByNumeroDocumento(clienteRequest.getNumeroDocumento());
        if (clienteExistenteConMismoDocumento.isPresent() && !clienteExistenteConMismoDocumento.get().getId().equals(id)) {
            throw new RuntimeException("El número de documento ya pertenece a otro cliente.");
        }
        return clienteRepository.findById(id).map(clienteExistente -> {
            TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(clienteRequest.getTipoDocumentoId())
                    .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

            clienteExistente.setNombreCompleto(clienteRequest.getNombreCompleto());
            clienteExistente.setNumeroDocumento(clienteRequest.getNumeroDocumento());
            clienteExistente.setEmail(clienteRequest.getEmail());
            clienteExistente.setTelefono(clienteRequest.getTelefono());
            clienteExistente.setTipoDocumento(tipoDocumento);
            return clienteRepository.save(clienteExistente);
        });
    }

    @Transactional
    public Optional<Cliente> deleteLogicoCliente(Integer id) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setActivo(false);
            return clienteRepository.save(cliente);
        });
    }

    // n8n integration methods
    @Transactional(readOnly = true)
    public Optional<Cliente> getClienteByTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> getClienteByNombreCompleto(String nombreCompleto) {
        return clienteRepository.findByNombreCompleto(nombreCompleto);
    }
}
