import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClienteService } from '../../services/cliente';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './cliente-list.html',
  styleUrl: './cliente-list.scss'
})
export class ClienteList implements OnInit {
  clientes: any[] = [];
  clientesFiltrados: any[] = [];
  terminoBusqueda = '';

  constructor(
    private clienteService: ClienteService,
    private router: Router
    ) {}

  ngOnInit(): void {
    this.loadClientes();
  }

  loadClientes(): void {
    this.clienteService.getClientes().subscribe(data => {
      this.clientes = data;
      this.clientesFiltrados = data;
    });
  }

  buscar(): void {
    this.clientesFiltrados = this.clientes.filter(cliente =>
      cliente.nombreCompleto.toLowerCase().includes(this.terminoBusqueda.toLowerCase()) ||
      cliente.numeroDocumento.includes(this.terminoBusqueda)
    );
  }

  limpiarBusqueda(): void {
    this.terminoBusqueda = '';
    this.clientesFiltrados = this.clientes;
  }
  
  crearCliente(): void {
    this.router.navigate(['/app/clientes/nuevo']);
  }
  
  editarCliente(id: number): void {
    this.router.navigate(['/app/clientes/editar', id]);
  }
  
  desactivarCliente(id: number): void {
    if (confirm('¿Está seguro de que desea desactivar este cliente?')) {
      this.clienteService.desactivarCliente(id).subscribe(() => {
        this.loadClientes();
      });
    }
  }
}
