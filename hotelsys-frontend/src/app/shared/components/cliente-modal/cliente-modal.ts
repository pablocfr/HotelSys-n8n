import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClienteService } from '../../../features/clientes/services/cliente';
import { CatalogoService } from '../../../core/services/catalogo';
import { ValidationMessages } from '../validation-messages/validation-messages';

declare var bootstrap: any;

@Component({
  selector: 'app-cliente-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ValidationMessages],
  templateUrl: './cliente-modal.html',
  styleUrl: './cliente-modal.scss'
})
export class ClienteModal implements OnInit {
  @Output() clienteCreado = new EventEmitter<any>();
  clienteForm: FormGroup;
  tiposDocumento: any[] = [];
  errorMessage: string | null = null;
  private modalInstance: any;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private catalogoService: CatalogoService
  ) {
    this.clienteForm = this.fb.group({
      nombreCompleto: ['', Validators.required],
      numeroDocumento: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefono: [''],
      tipoDocumentoId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadTiposDocumento();
    const modalElement = document.getElementById('crearClienteModal');
    if (modalElement) {
      this.modalInstance = new bootstrap.Modal(modalElement);
    }
  }

  open(): void {
    this.errorMessage = null;
    this.clienteForm.reset();
    this.modalInstance.show();
  }

  close(): void {
    this.modalInstance.hide();
  }

  loadTiposDocumento(): void {
    this.catalogoService.getTiposDocumento().subscribe(data => {
      this.tiposDocumento = data;
    });
  }

  onSubmit(): void {
    this.errorMessage = null;
    if (this.clienteForm.invalid) {
      return;
    }
    this.clienteService.createCliente(this.clienteForm.value).subscribe({
        next: (nuevoCliente) => {
          this.clienteCreado.emit(nuevoCliente);
          this.close();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Ha ocurrido un error inesperado.';
        }
    });
  }
  
  get f() { return this.clienteForm.controls; }
}
