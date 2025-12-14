USE hotelsys_db_n8n;

-- Insertar usuarios con contraseñas encriptadas (BCrypt con 10 rondas de hashing)
-- Probadas en https://bcrypt-generator.com/
-- Contraseña para admin@hotel.com: "admin"
-- Contraseña para usuario@hotel.com: "usuario"
INSERT INTO usuarios (nombre_usuario, email, password, rol) VALUES 
('Admin General', 'admin@hotel.com', '$2a$10$FLMCej4jcVBeh1Uvl7R7n.Xw2Fx.qmBniSaJLbcUgzrfi6kx2QyaG', 'ADMIN'),
('Juan Recepcionista', 'usuario@hotel.com', '$2a$10$8RXO6fLzX2GTK1zebCj9Ru6urLzQE3In2YVcPZrLwXzMdlUZsbYH2', 'USER');

INSERT INTO tipos_documento (descripcion) VALUES ('DNI'), ('RUC'), ('Pasaporte');
INSERT INTO tipos_habitacion (descripcion, precio_base_noche) VALUES ('Simple', 100.00), ('Doble', 180.00), ('Matrimonial', 220.00), ('Suite', 350.00);
INSERT INTO estados_habitacion (descripcion) VALUES ('Disponible'), ('Ocupada'), ('Mantenimiento');
INSERT INTO estados_reserva (descripcion) VALUES ('Pendiente'), ('Confirmada'), ('Cancelada'), ('Finalizada');

INSERT INTO clientes (nombre_completo, numero_documento, email, telefono, activo, tipo_documento_id) VALUES
('Juan Pérez Gonzales', '71234567', 'juan.perez@email.com', '987654321', 1, 1),
('Constructora El Sol S.A.C.', '20123456789', 'contacto@elsol.com', '014567890', 1, 2),
('María López Castillo', '87654321', 'maria.lopez@email.com', '912345678', 1, 1),
('Michael Smith', 'A1B2345C', 'msmith@email.com', '555-1234', 0, 3);

INSERT INTO productos (nombre_producto, precio, stock, activo) VALUES
('Leña para fogata', 30.00, 100, 1),
('Cena Ejecutiva', 45.00, 50, 1),
('Desayuno Buffet', 30.00, 80, 1),
('Papas Lays', 2.40, 250, 1),
('Gaseosa 500 ml', 2.50, 300, 0);

INSERT INTO habitaciones (numero, tipo_habitacion_id, estado_habitacion_id, requiere_limpieza, activo) VALUES
('101', 1, 1, 0, 1), ('102', 1, 1, 0, 1),
('201', 2, 1, 0, 1), ('202', 2, 1, 1, 1),
('301', 3, 1, 0, 1),
('401', 4, 3, 1, 1),
('402', 1, 1, 0, 1),
('405', 2, 2, 0, 0);

INSERT INTO reservas (fecha_reserva, fecha_check_in, fecha_check_out, monto_total_calculado, activo, cliente_id, estado_reserva_id) VALUES
(NOW(), '2025-08-25', '2025-08-30', 985.00, 1, 3, 2),
(NOW(), '2025-09-01', '2025-09-03', 450.00, 1, 1, 2);

SET @last_reserva_id = 1;

INSERT INTO reserva_habitaciones(reserva_id, habitacion_id, precio_noche_grabado) VALUES
(@last_reserva_id, 3, 180.00);

INSERT INTO reserva_productos(reserva_id, producto_id, cantidad, precio_unitario_grabado) VALUES
(@last_reserva_id, 2, 1, 45.00),
(@last_reserva_id, 4, 4, 2.40);

UPDATE habitaciones SET estado_habitacion_id = 2 WHERE id = 3;

-- ===============================================================
-- Comprobante de prueba
-- ===============================================================
-- Se genera una BOLETA para la primera reserva (cliente con DNI)
-- Total = 985.00. Subtotal = 985 / 1.18 = 834.75. IGV = 985 - 834.75 = 150.25
INSERT INTO comprobantes (reserva_id, tipo_comprobante, serie_numero, fecha_emision, subtotal, igv, total) VALUES
(1, 'BOLETA', 'B001-000001', NOW(), 834.75, 150.25, 985.00);

-- Actualizamos el estado de facturación de la reserva correspondiente
UPDATE reservas SET estado_facturacion = 'GENERADO' WHERE id = 1;

-- ===============================================================
-- DETALLES PARA LA SEGUNDA RESERVA
-- ===============================================================
INSERT INTO reserva_habitaciones(reserva_id, habitacion_id, precio_noche_grabado) VALUES
(2, 4, 180.00); -- Reserva 2, Habitación 202 (Doble)

INSERT INTO reserva_productos(reserva_id, producto_id, cantidad, precio_unitario_grabado) VALUES
(2, 3, 2, 30.00); -- Reserva 2, 2x Desayuno Buffet

UPDATE habitaciones SET estado_habitacion_id = 2 WHERE id = 4;

-- Actualizamos el estado de facturación de la reserva correspondiente
UPDATE reservas SET estado_facturacion = 'GENERADO' WHERE id = 1;