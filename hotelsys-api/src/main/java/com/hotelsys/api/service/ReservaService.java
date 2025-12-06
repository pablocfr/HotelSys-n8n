package com.hotelsys.api.service;

import com.hotelsys.api.dto.ReservaProductoRequest;
import com.hotelsys.api.dto.ReservaRequest;
import com.hotelsys.api.model.catalogos.EstadoHabitacion;
import com.hotelsys.api.model.catalogos.EstadoReserva;
import com.hotelsys.api.model.entidades.*;
import com.hotelsys.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final HabitacionRepository habitacionRepository;
    private final ProductoRepository productoRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final EstadoHabitacionRepository estadoHabitacionRepository;

    @Transactional(readOnly = true)
    public List<Reserva> getAllActiveReservas() {
        return reservaRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Reserva> getReservaById(Integer id) {
        return reservaRepository.findById(id);
    }

    @Transactional
    public Reserva createReserva(ReservaRequest reservaRequest) {
        Cliente cliente = clienteRepository.findById(reservaRequest.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        long noches = ChronoUnit.DAYS.between(reservaRequest.getFechaCheckIn(), reservaRequest.getFechaCheckOut());
        if (noches <= 0) {
            throw new RuntimeException("La fecha de Check-Out debe ser posterior a la de Check-In.");
        }

        BigDecimal montoTotalHabitaciones = BigDecimal.ZERO;
        List<Habitacion> habitacionesAReservar = new ArrayList<>();
        for (Integer habitacionId : reservaRequest.getHabitacionIds()) {
            Habitacion habitacion = habitacionRepository.findById(habitacionId)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada con ID: " + habitacionId));

            if (!habitacion.getActivo() || habitacion.getEstadoHabitacion().getId() != 1) {
                throw new RuntimeException("La habitación " + habitacion.getNumero() + " no está disponible.");
            }
            habitacionesAReservar.add(habitacion);
            BigDecimal costoPorNoche = habitacion.getTipoHabitacion().getPrecioBaseNoche();
            montoTotalHabitaciones = montoTotalHabitaciones.add(costoPorNoche.multiply(new BigDecimal(noches)));
        }

        BigDecimal montoTotalProductos = BigDecimal.ZERO;
        List<Producto> productosAActualizar = new ArrayList<>();
        List<ReservaProducto> detallesProducto = new ArrayList<>();
        for (ReservaProductoRequest prodReq : reservaRequest.getProductos()) {
            Producto producto = productoRepository.findById(prodReq.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + prodReq.getProductoId()));

            if (!producto.getActivo() || producto.getStock() < prodReq.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombreProducto());
            }
            producto.setStock(producto.getStock() - prodReq.getCantidad());
            productosAActualizar.add(producto);
            montoTotalProductos = montoTotalProductos.add(producto.getPrecio().multiply(new BigDecimal(prodReq.getCantidad())));

            ReservaProducto detalle = new ReservaProducto();
            detalle.setProducto(producto);
            detalle.setCantidad(prodReq.getCantidad());
            detalle.setPrecioUnitarioGrabado(producto.getPrecio());
            detallesProducto.add(detalle);
        }

        BigDecimal subtotal = montoTotalHabitaciones.add(montoTotalProductos);
        BigDecimal descuento = reservaRequest.getDescuento() != null ? reservaRequest.getDescuento() : BigDecimal.ZERO;
        BigDecimal montoTotalCalculado = subtotal.subtract(descuento);

        EstadoReserva estadoReserva = estadoReservaRepository.findById(reservaRequest.getEstadoReservaId())
                .orElseThrow(() -> new RuntimeException("Estado de reserva no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setFechaCheckIn(reservaRequest.getFechaCheckIn());
        reserva.setFechaCheckOut(reservaRequest.getFechaCheckOut());
        reserva.setDescuento(descuento);
        reserva.setMontoTotalCalculado(montoTotalCalculado);
        reserva.setEstadoReserva(estadoReserva);
        reserva.setActivo(true);
        reserva.setEstadoFacturacion("PENDIENTE");

        detallesProducto.forEach(dp -> dp.setReserva(reserva));
        reserva.setProductos(detallesProducto);

        List<ReservaHabitacion> detallesHabitacion = new ArrayList<>();
        EstadoHabitacion estadoOcupada = estadoHabitacionRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Estado 'Ocupada' no encontrado"));

        for (Habitacion hab : habitacionesAReservar) {
            hab.setEstadoHabitacion(estadoOcupada);
            ReservaHabitacion detalle = new ReservaHabitacion();
            detalle.setHabitacion(hab);
            detalle.setPrecioNocheGrabado(hab.getTipoHabitacion().getPrecioBaseNoche());
            detalle.setReserva(reserva);
            detallesHabitacion.add(detalle);
        }
        reserva.setHabitaciones(detallesHabitacion);

        habitacionRepository.saveAll(habitacionesAReservar);
        productoRepository.saveAll(productosAActualizar);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Optional<Reserva> deleteLogicoReserva(Integer id) {
        return reservaRepository.findById(id).map(reserva -> {
            EstadoReserva estadoCancelada = estadoReservaRepository.findById(3)
                    .orElseThrow(() -> new RuntimeException("Estado 'Cancelada' no encontrado"));
            reserva.setActivo(false);
            reserva.setEstadoReserva(estadoCancelada);

            EstadoHabitacion estadoDisponible = estadoHabitacionRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Estado 'Disponible' no encontrado"));

            reserva.getHabitaciones().forEach(detalle -> {
                Habitacion habitacion = detalle.getHabitacion();
                habitacion.setEstadoHabitacion(estadoDisponible);
                habitacionRepository.save(habitacion);
            });

            reserva.getProductos().forEach(detalle -> {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            });

            return reservaRepository.save(reserva);
        });
    }

    @Transactional
    public Optional<Reserva> updateReserva(Integer id, ReservaRequest reservaRequest) {
        return reservaRepository.findById(id).map(reservaExistente -> {

            if ("GENERADO".equals(reservaExistente.getEstadoFacturacion())) {
                throw new RuntimeException("No se puede modificar una reserva que ya tiene un comprobante generado.");
            }

            EstadoHabitacion estadoDisponible = estadoHabitacionRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Estado 'Disponible' no encontrado"));

            reservaExistente.getHabitaciones().forEach(detalle -> {
                Habitacion habitacion = detalle.getHabitacion();
                habitacion.setEstadoHabitacion(estadoDisponible);
                habitacionRepository.save(habitacion);
            });

            reservaExistente.getProductos().forEach(detalle -> {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            });

            reservaExistente.getHabitaciones().clear();
            reservaExistente.getProductos().clear();
            reservaRepository.flush();

            Cliente cliente = clienteRepository.findById(reservaRequest.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            long noches = ChronoUnit.DAYS.between(reservaRequest.getFechaCheckIn(), reservaRequest.getFechaCheckOut());
            if (noches <= 0) {
                throw new RuntimeException("La fecha de Check-Out debe ser posterior a la de Check-In.");
            }

            BigDecimal montoTotalHabitaciones = BigDecimal.ZERO;
            List<Habitacion> habitacionesAReservar = new ArrayList<>();
            for (Integer habitacionId : reservaRequest.getHabitacionIds()) {
                Habitacion habitacion = habitacionRepository.findById(habitacionId)
                        .orElseThrow(() -> new RuntimeException("Habitación no encontrada con ID: " + habitacionId));

                if (!habitacion.getActivo() || (habitacion.getEstadoHabitacion().getId() != 1 && !reservaExistente.getHabitaciones().stream().anyMatch(h -> h.getHabitacion().getId().equals(habitacionId)))) {
                    throw new RuntimeException("La habitación " + habitacion.getNumero() + " no está disponible.");
                }
                habitacionesAReservar.add(habitacion);
                BigDecimal costoPorNoche = habitacion.getTipoHabitacion().getPrecioBaseNoche();
                montoTotalHabitaciones = montoTotalHabitaciones.add(costoPorNoche.multiply(new BigDecimal(noches)));
            }

            BigDecimal montoTotalProductos = BigDecimal.ZERO;
            List<Producto> productosAActualizar = new ArrayList<>();
            List<ReservaProducto> detallesProducto = new ArrayList<>();
            if (reservaRequest.getProductos() != null) {
                for (ReservaProductoRequest prodReq : reservaRequest.getProductos()) {
                    Producto producto = productoRepository.findById(prodReq.getProductoId())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + prodReq.getProductoId()));

                    if (!producto.getActivo() || producto.getStock() < prodReq.getCantidad()) {
                        throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombreProducto());
                    }
                    producto.setStock(producto.getStock() - prodReq.getCantidad());
                    productosAActualizar.add(producto);
                    montoTotalProductos = montoTotalProductos.add(producto.getPrecio().multiply(new BigDecimal(prodReq.getCantidad())));

                    ReservaProducto detalle = new ReservaProducto();
                    detalle.setProducto(producto);
                    detalle.setCantidad(prodReq.getCantidad());
                    detalle.setPrecioUnitarioGrabado(producto.getPrecio());
                    detalle.setReserva(reservaExistente);
                    detallesProducto.add(detalle);
                }
            }

            BigDecimal subtotal = montoTotalHabitaciones.add(montoTotalProductos);
            BigDecimal descuento = reservaRequest.getDescuento() != null ? reservaRequest.getDescuento() : BigDecimal.ZERO;
            BigDecimal montoTotalCalculado = subtotal.subtract(descuento);

            EstadoReserva estadoReserva = estadoReservaRepository.findById(reservaRequest.getEstadoReservaId())
                    .orElseThrow(() -> new RuntimeException("Estado de reserva no encontrado"));

            reservaExistente.setCliente(cliente);
            reservaExistente.setFechaCheckIn(reservaRequest.getFechaCheckIn());
            reservaExistente.setFechaCheckOut(reservaRequest.getFechaCheckOut());
            reservaExistente.setDescuento(descuento);
            reservaExistente.setMontoTotalCalculado(montoTotalCalculado);
            reservaExistente.setEstadoReserva(estadoReserva);

            reservaExistente.getProductos().addAll(detallesProducto);

            EstadoHabitacion estadoOcupada = estadoHabitacionRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado 'Ocupada' no encontrado"));

            List<ReservaHabitacion> detallesHabitacion = new ArrayList<>();
            for (Habitacion hab : habitacionesAReservar) {
                hab.setEstadoHabitacion(estadoOcupada);
                ReservaHabitacion detalle = new ReservaHabitacion();
                detalle.setHabitacion(hab);
                detalle.setPrecioNocheGrabado(hab.getTipoHabitacion().getPrecioBaseNoche());
                detalle.setReserva(reservaExistente);
                detallesHabitacion.add(detalle);
            }
            reservaExistente.getHabitaciones().addAll(detallesHabitacion);

            habitacionRepository.saveAll(habitacionesAReservar);
            productoRepository.saveAll(productosAActualizar);
            return reservaRepository.save(reservaExistente);
        });
    }

    // n8n integration methods
    @Transactional(readOnly = true)
    public List<Reserva> getReservasByClienteTelefono(String telefono) {
        return reservaRepository.findByClienteTelefono(telefono);
    }

    @Transactional(readOnly = true)
    public List<Reserva> getReservasByClienteNombreCompleto(String nombreCompleto) {
        return reservaRepository.findByClienteNombreCompleto(nombreCompleto);
    }
}
