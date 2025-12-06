import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ComprobanteService } from '../../services/comprobante';

@Component({
  selector: 'app-comprobante-view',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './comprobante-view.html',
  styleUrl: './comprobante-view.scss'
})
export class ComprobanteView implements OnInit {
  comprobante: any = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comprobanteService: ComprobanteService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.comprobanteService.getComprobanteById(id).subscribe(data => {
        this.comprobante = data;
      });
    }
  }
  
  descargar(formato: 'pdf' | 'xml'): void {
     this.comprobanteService.downloadComprobante(this.comprobante.id, formato).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Comprobante-${this.comprobante.id}.${formato}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      a.remove();
    });
  }

  eliminar(): void {
    if (confirm('¿Está seguro de que desea eliminar este comprobante? La reserva volverá a estar pendiente de facturación.')) {
      this.comprobanteService.deleteComprobante(this.comprobante.id).subscribe(() => {
        this.router.navigate(['/app/comprobantes']); // Volver a la lista
      });
    }
  }
}
