import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HabitacionService } from '../../services/habitacion';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-habitacion-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './habitacion-list.html',
  styleUrl: './habitacion-list.scss'
})
export class HabitacionList implements OnInit {
  habitaciones: any[] = [];
  habitacionesFiltradas: any[] = [];
  terminoBusqueda = '';

  constructor(
    private habitacionService: HabitacionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadHabitaciones();
  }

  loadHabitaciones(): void {
    this.habitacionService.getHabitaciones().subscribe(data => {
      this.habitaciones = data;
      this.habitacionesFiltradas = data;
    });
  }

  buscar(): void {
    this.habitacionesFiltradas = this.habitaciones.filter(h =>
      h.numero.toLowerCase().includes(this.terminoBusqueda.toLowerCase()) ||
      h.tipoHabitacion.descripcion.toLowerCase().includes(this.terminoBusqueda.toLowerCase())
    );
  }

  limpiarBusqueda(): void {
    this.terminoBusqueda = '';
    this.habitacionesFiltradas = this.habitaciones;
  }

  crearHabitacion(): void {
    this.router.navigate(['/app/habitaciones/nueva']);
  }
  
  editarHabitacion(id: number): void {
    this.router.navigate(['/app/habitaciones/editar', id]);
  }
  
  desactivarHabitacion(id: number): void {
    if (confirm('¿Está seguro de que desea desactivar esta habitación?')) {
      this.habitacionService.desactivarHabitacion(id).subscribe(() => {
        this.loadHabitaciones();
      });
    }
  }
}
