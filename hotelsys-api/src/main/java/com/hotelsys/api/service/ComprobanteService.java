package com.hotelsys.api.service;

import com.hotelsys.api.model.catalogos.TipoDocumento;
import com.hotelsys.api.model.entidades.*;
import com.hotelsys.api.repository.ComprobanteRepository;
import com.hotelsys.api.repository.ReservaRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ComprobanteService {

    private final ComprobanteRepository comprobanteRepository;
    private final ReservaRepository reservaRepository;
    private static final BigDecimal IGV_RATE = new BigDecimal("1.18");

    @Transactional(readOnly = true)
    public List<Comprobante> getAllComprobantes() {
        return comprobanteRepository.findAll();
    }

    @Transactional
    public Comprobante generarComprobante(Integer reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!"PENDIENTE".equals(reserva.getEstadoFacturacion())) {
            throw new RuntimeException("La reserva ya tiene un comprobante generado.");
        }

        Comprobante comprobante = new Comprobante();
        comprobante.setReserva(reserva);

        boolean esFactura = "RUC".equals(reserva.getCliente().getTipoDocumento().getDescripcion());
        comprobante.setTipoComprobante(esFactura ? Comprobante.TipoComprobante.FACTURA : Comprobante.TipoComprobante.BOLETA);

        long count = comprobanteRepository.count() + 1;
        String serie = esFactura ? "F001-" : "B001-";
        comprobante.setSerieNumero(serie + String.format("%06d", count));

        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setTotal(reserva.getMontoTotalCalculado());

        BigDecimal subtotal = reserva.getMontoTotalCalculado().divide(IGV_RATE, 2, RoundingMode.HALF_UP);
        BigDecimal igv = reserva.getMontoTotalCalculado().subtract(subtotal);
        comprobante.setSubtotal(subtotal);
        comprobante.setIgv(igv);

        reserva.setEstadoFacturacion("GENERADO");
        reservaRepository.save(reserva);

        return comprobanteRepository.save(comprobante);
    }

    public ByteArrayInputStream generarPdf(Integer comprobanteId) {
        Comprobante comprobante = comprobanteRepository.findById(comprobanteId)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));

        Reserva reserva = comprobante.getReserva();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // --- Fuentes ---
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        // --- Encabezado ---
        Paragraph titulo = new Paragraph("HotelSys - Comprobante de Pago", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));

        Paragraph tipo = new Paragraph(comprobante.getTipoComprobante().toString(), fontSubtitulo);
        tipo.setAlignment(Element.ALIGN_CENTER);
        document.add(tipo);

        Paragraph numero = new Paragraph(comprobante.getSerieNumero(), fontSubtitulo);
        numero.setAlignment(Element.ALIGN_CENTER);
        document.add(numero);
        document.add(new Paragraph(" "));

        // --- Datos del Cliente ---
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------"));
        document.add(new Paragraph("Cliente: " + reserva.getCliente().getNombreCompleto(), fontNormal));
        document.add(new Paragraph("Documento: " + reserva.getCliente().getNumeroDocumento(), fontNormal));
        document.add(new Paragraph("Fecha de Emisión: " + comprobante.getFechaEmision().toLocalDate().toString(), fontNormal));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------"));
        document.add(new Paragraph(" "));

        // --- Cuerpo (DETALLE) ---
        Paragraph detalleHeader = new Paragraph("DETALLE", fontHeader);
        document.add(detalleHeader);

        long noches = ChronoUnit.DAYS.between(reserva.getFechaCheckIn(), reserva.getFechaCheckOut());
        noches = noches > 0 ? noches : 1; // Asegurar al menos una noche para el cálculo

        for (ReservaHabitacion detalle : reserva.getHabitaciones()) {
            String texto = "Hospedaje por " + noches + " noche(s) - Habitación " + detalle.getHabitacion().getNumero()
                    + " (" + detalle.getHabitacion().getTipoHabitacion().getDescripcion() + ")";
            document.add(new Paragraph(texto, fontNormal));
        }

        for (ReservaProducto detalle : reserva.getProductos()) {
            String texto = detalle.getCantidad() + "x " + detalle.getProducto().getNombreProducto();
            document.add(new Paragraph(texto, fontNormal));
        }
        document.add(new Paragraph(" "));

        // --- Totales ---
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------"));
        Paragraph subtotal = new Paragraph("Subtotal: S/ " + comprobante.getSubtotal(), fontNormal);
        subtotal.setAlignment(Element.ALIGN_RIGHT);
        document.add(subtotal);

        Paragraph igv = new Paragraph("IGV (18%): S/ " + comprobante.getIgv(), fontNormal);
        igv.setAlignment(Element.ALIGN_RIGHT);
        document.add(igv);

        Paragraph total = new Paragraph("Total a Pagar: S/ " + comprobante.getTotal(), fontSubtitulo);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generarXml(Integer comprobanteId) {
        Comprobante comprobante = comprobanteRepository.findById(comprobanteId)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));
        try {
            // Le decimos a JAXB todas las clases que podría encontrar en el camino
            JAXBContext context = JAXBContext.newInstance(Comprobante.class, Reserva.class, Cliente.class, TipoDocumento.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter sw = new StringWriter();
            marshaller.marshal(comprobante, sw);

            return new ByteArrayInputStream(sw.toString().getBytes());
        } catch (Exception e) {
            // Imprimimos el error en la consola del backend para poder depurar si algo más falla
            e.printStackTrace();
            throw new RuntimeException("Error al generar el XML", e);
        }
    }

    @Transactional(readOnly = true)
    public Comprobante getComprobanteById(Integer id) {
        return comprobanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));
    }

    @Transactional
    public void deleteComprobante(Integer id) {
        Comprobante comprobante = comprobanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));

        Reserva reserva = comprobante.getReserva();

        // Paso 1: Romper la relación desde la Reserva
        reserva.setEstadoFacturacion("PENDIENTE");
        reserva.setComprobante(null); // Le decimos a la reserva que olvide a su comprobante.

        // Paso 2: Guardar la Reserva actualizada
        reservaRepository.save(reserva);

        // Paso 3: Eliminar el Comprobante de forma segura
        comprobanteRepository.deleteById(id);
    }
}
