import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProductoService } from '../../services/producto';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-producto-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './producto-list.html',
  styleUrl: './producto-list.scss'
})
export class ProductoList implements OnInit {
  productos: any[] = [];
  productosFiltrados: any[] = [];
  terminoBusqueda = '';

  constructor(
    private productoService: ProductoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProductos();
  }

  loadProductos(): void {
    this.productoService.getProductos().subscribe(data => {
      this.productos = data;
      this.productosFiltrados = data;
    });
  }

  buscar(): void {
    this.productosFiltrados = this.productos.filter(p =>
      p.nombreProducto.toLowerCase().includes(this.terminoBusqueda.toLowerCase())
    );
  }

  limpiarBusqueda(): void {
    this.terminoBusqueda = '';
    this.productosFiltrados = this.productos;
  }
  
  crearProducto(): void {
    this.router.navigate(['/app/productos/nuevo']);
  }
  
  editarProducto(id: number): void {
    this.router.navigate(['/app/productos/editar', id]);
  }
  
  desactivarProducto(id: number): void {
    if (confirm('¿Está seguro de que desea desactivar este producto?')) {
      this.productoService.desactivarProducto(id).subscribe(() => {
        this.loadProductos();
      });
    }
  }
}
