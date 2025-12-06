import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

import { ReservaService } from '../../services/reserva';
import { ClienteService } from '../../../clientes/services/cliente';
import { HabitacionService } from '../../../habitaciones/services/habitacion';
import { ProductoService } from '../../../productos/services/producto';

import { ClienteModal } from '../../../../shared/components/cliente-modal/cliente-modal';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, FormsModule, ClienteModal],
  templateUrl: './reserva-form.html',
  styleUrl: './reserva-form.scss'
})
export class ReservaForm implements OnInit {
  @ViewChild(ClienteModal) clienteModal!: ClienteModal;
  reservaForm: FormGroup;
  isEditMode = false;
  reservaId: number | null = null;
  pageTitle = 'Crear Nueva Reserva';

  clienteBusquedaResultados: any[] = [];
  habitacionesDisponibles: any[] = [];
  productosDisponibles: any[] = [];
  
  clienteSeleccionado: any = null;
  habitacionesSeleccionadas: any[] = [];
  productosSeleccionados: any[] = [];

  nochesCalculadas = 0;
  subtotal = 0;
  montoDescuento = 0;
  total = 0;

  constructor(
    private fb: FormBuilder,
    private reservaService: ReservaService,
    private clienteService: ClienteService,
    private habitacionService: HabitacionService,
    private productoService: ProductoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.reservaForm = this.fb.group({
      clienteBusqueda: [''],
      fechaCheckIn: [new Date().toISOString().split('T')[0], Validators.required],
      fechaCheckOut: ['', Validators.required],
      habitacionBusqueda: [''],
      productoBusqueda: [''],
      descuento: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.reservaId = this.route.snapshot.params['id'];
    this.setupDynamicSearch();
    
    if (this.reservaId) {
      this.isEditMode = true;
      this.pageTitle = `Editar Reserva #${this.reservaId}`;
      this.loadReservaData();
    }
  }

  abrirModalCliente(): void {
    this.clienteModal.open();
  }

  onClienteCreado(nuevoCliente: any): void {
    this.seleccionarCliente(nuevoCliente);
  }

  private setupDynamicSearch(): void {
    this.reservaForm.get('clienteBusqueda')?.valueChanges.pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => this.buscarClientes(false));

    this.reservaForm.get('productoBusqueda')?.valueChanges.pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => this.buscarProductos(false));
    
    this.reservaForm.get('fechaCheckIn')?.valueChanges.subscribe(() => this.calcularTotal());
    this.reservaForm.get('fechaCheckOut')?.valueChanges.subscribe(() => this.calcularTotal());
    this.reservaForm.get('descuento')?.valueChanges.subscribe(val => {
      this.montoDescuento = val;
      this.calcularTotal();
    });
  }
  
  private loadReservaData(): void {
    this.reservaService.getReservaById(this.reservaId!).subscribe(reserva => {
      this.reservaForm.patchValue({
        fechaCheckIn: reserva.fechaCheckIn,
        fechaCheckOut: reserva.fechaCheckOut,
        descuento: reserva.descuento
      });
      this.clienteSeleccionado = reserva.cliente;
      this.habitacionesSeleccionadas = reserva.habitaciones.map((h: any) => h.habitacion);
      this.productosSeleccionados = reserva.productos.map((p: any) => ({ ...p.producto, cantidad: p.cantidad }));
      this.calcularTotal();
    });
  }

  buscarClientes(listarTodos: boolean = false): void {
    const term = this.reservaForm.get('clienteBusqueda')?.value;
    if (listarTodos && !term) {
      this.clienteService.getClientes().subscribe(data => this.clienteBusquedaResultados = data);
    } else if (term) {
      this.clienteService.searchClientes(term).subscribe(data => this.clienteBusquedaResultados = data);
    } else {
      this.clienteBusquedaResultados = [];
    }
  }

  seleccionarCliente(cliente: any): void {
    this.clienteSeleccionado = cliente;
    this.clienteBusquedaResultados = [];
    this.reservaForm.get('clienteBusqueda')?.setValue('', { emitEvent: false });
  }

  buscarHabitaciones(): void {
    this.habitacionService.getAvailableHabitaciones().subscribe(habitaciones => {
      const term = this.reservaForm.get('habitacionBusqueda')?.value.toLowerCase();
      this.habitacionesDisponibles = habitaciones.filter(h => 
        !this.habitacionesSeleccionadas.some(hs => hs.id === h.id) &&
        (h.numero.toLowerCase().includes(term) || h.tipoHabitacion.descripcion.toLowerCase().includes(term))
      );
    });
  }

  agregarHabitacion(habitacion: any): void {
    if (!this.habitacionesSeleccionadas.some(h => h.id === habitacion.id)) {
        this.habitacionesSeleccionadas.push(habitacion);
        this.habitacionesDisponibles = this.habitacionesDisponibles.filter(h => h.id !== habitacion.id);
        this.calcularTotal();
    }
  }

  quitarHabitacion(habitacionId: number): void {
    this.habitacionesSeleccionadas = this.habitacionesSeleccionadas.filter(h => h.id !== habitacionId);
    this.habitacionesDisponibles = [];
    this.calcularTotal();
  }

  buscarProductos(listarTodos: boolean = false): void {
    const term = this.reservaForm.get('productoBusqueda')?.value;
    if (listarTodos && !term) {
      this.productoService.getProductos().subscribe(data => this.productosDisponibles = data);
    } else if (term) {
      this.productoService.searchProductos(term).subscribe(data => this.productosDisponibles = data);
    } else {
      this.productosDisponibles = [];
    }
  }
  
  agregarProducto(producto: any, cantidadInput: HTMLInputElement): void {
    const cantidad = parseInt(cantidadInput.value, 10);
    if (cantidad > 0 && cantidad <= producto.stock) {
      const existente = this.productosSeleccionados.find(p => p.id === producto.id);
      if (existente) {
        existente.cantidad += cantidad;
      } else {
        this.productosSeleccionados.push({ ...producto, cantidad });
      }
      cantidadInput.value = '1';
      this.productosDisponibles = [];
      this.reservaForm.get('productoBusqueda')?.setValue('', { emitEvent: false });
      this.calcularTotal();
    }
  }

  quitarProducto(productoId: number): void {
    this.productosSeleccionados = this.productosSeleccionados.filter(p => p.id !== productoId);
    this.calcularTotal();
  }

  onCantidadProductoChange(): void {
    this.calcularTotal();
  }

  calcularTotal(): void {
    const checkIn = this.reservaForm.get('fechaCheckIn')?.value;
    const checkOut = this.reservaForm.get('fechaCheckOut')?.value;
    
    let noches = 0;
    if (checkIn && checkOut) {
      const diff = new Date(checkOut).getTime() - new Date(checkIn).getTime();
      noches = Math.ceil(diff / (1000 * 3600 * 24));
    }
    this.nochesCalculadas = noches > 0 ? noches : 0;

    const totalHabitaciones = this.habitacionesSeleccionadas.reduce((sum, h) => sum + (h.tipoHabitacion.precioBaseNoche * this.nochesCalculadas), 0);
    const totalProductos = this.productosSeleccionados.reduce((sum, p) => sum + (p.precio * p.cantidad), 0);
    
    this.subtotal = totalHabitaciones + totalProductos;
    this.total = this.subtotal - this.montoDescuento;
  }

  onSubmit(): void {
    // ... (el método onSubmit no necesita cambios)
    if (!this.clienteSeleccionado || this.habitacionesSeleccionadas.length === 0 || this.reservaForm.invalid) {
      alert('Por favor, complete todos los campos requeridos: Cliente, Fechas y al menos una habitación.');
      return;
    }

    const payload = {
      clienteId: this.clienteSeleccionado.id,
      fechaCheckIn: this.reservaForm.get('fechaCheckIn')?.value,
      fechaCheckOut: this.reservaForm.get('fechaCheckOut')?.value,
      descuento: this.reservaForm.get('descuento')?.value,
      estadoReservaId: 2,
      habitacionIds: this.habitacionesSeleccionadas.map(h => h.id),
      productos: this.productosSeleccionados.map(p => ({ productoId: p.id, cantidad: p.cantidad }))
    };

    const request = this.isEditMode
      ? this.reservaService.updateReserva(this.reservaId!, payload)
      : this.reservaService.createReserva(payload);
    
    request.subscribe({
      next: () => this.router.navigate(['/app/reservas']),
      error: (err) => alert(`Error al guardar la reserva: ${err.message}`)
    });
  }
}
