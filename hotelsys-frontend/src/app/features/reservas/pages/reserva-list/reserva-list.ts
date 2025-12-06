import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ReservaService } from '../../services/reserva';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reserva-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './reserva-list.html',
  styleUrl: './reserva-list.scss'
})
export class ReservaList implements OnInit {
  reservas: any[] = [];
  reservasFiltradas: any[] = [];
  terminoBusqueda = '';

  constructor(
    private reservaService: ReservaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadReservas();
  }

  loadReservas(): void {
    this.reservaService.getReservas().subscribe(data => {
      this.reservas = data;
      this.reservasFiltradas = data;
    });
  }

  buscar(): void {
    this.reservasFiltradas = this.reservas.filter(r =>
      r.cliente.nombreCompleto.toLowerCase().includes(this.terminoBusqueda.toLowerCase())
    );
  }

  limpiarBusqueda(): void {
    this.terminoBusqueda = '';
    this.reservasFiltradas = this.reservas;
  }
  
  crearReserva(): void {
    this.router.navigate(['/app/reservas/nueva']);
  }
  
  editarReserva(id: number): void {
    this.router.navigate(['/app/reservas/editar', id]);
  }
  
  cancelarReserva(id: number): void {
    if (confirm('¿Está seguro de que desea cancelar esta reserva? Esta acción no se puede deshacer.')) {
      this.reservaService.cancelarReserva(id).subscribe(() => {
        this.loadReservas();
      });
    }
  }

  // Helper para mostrar números de habitación
  getHabitaciones(reserva: any): string {
    return reserva.habitaciones.map((h: any) => h.habitacion.numero).join(', ');
  }

  generarComprobante(id: number): void {
    this.reservaService.generarComprobante(id).subscribe(() => {
        // Opcional: mostrar un mensaje de éxito
        alert('Comprobante generado con éxito.');
        this.loadReservas(); // Recargar la lista para actualizar el botón
    });
  }

  verComprobante(comprobanteId: number): void {
      this.router.navigate(['/app/comprobantes/ver', comprobanteId]);
  }
}
