import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComprobanteService } from '../../services/comprobante';
import { Router } from '@angular/router';

@Component({
  selector: 'app-comprobante-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './comprobante-list.html',
  styleUrl: './comprobante-list.scss'
})
export class ComprobanteList implements OnInit {
  comprobantes: any[] = [];

  constructor(
    private comprobanteService: ComprobanteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadComprobantes();
  }

  loadComprobantes(): void {
    this.comprobanteService.getComprobantes().subscribe(data => {
      this.comprobantes = data;
    });
  }

  descargar(id: number, formato: 'pdf' | 'xml'): void {
    this.comprobanteService.downloadComprobante(id, formato).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Comprobante-${id}.${formato}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      a.remove();
    });
  }

  verComprobante(id: number): void {
    this.router.navigate(['/app/comprobantes/ver', id]);
  }

  eliminarComprobante(id: number): void {
    if (confirm('¿Está seguro de que desea eliminar este comprobante? La reserva volverá a estar pendiente de facturación.')) {
      this.comprobanteService.deleteComprobante(id).subscribe(() => {
        this.loadComprobantes();
      });
    }
  }
}