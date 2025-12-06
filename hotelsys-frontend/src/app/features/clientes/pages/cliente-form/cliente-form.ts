import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ClienteService } from '../../services/cliente';
import { CatalogoService } from '../../../../core/services/catalogo';
import { ValidationMessages } from '../../../../shared/components/validation-messages/validation-messages';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, ValidationMessages],
  templateUrl: './cliente-form.html',
  styleUrl: './cliente-form.scss'
})
export class ClienteForm implements OnInit {
  clienteForm: FormGroup;
  tiposDocumento: any[] = [];
  isEditMode = false;
  clienteId: number | null = null;
  pageTitle = 'Crear Nuevo Cliente';
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private catalogoService: CatalogoService,
    private router: Router,
    private route: ActivatedRoute
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
    this.clienteId = this.route.snapshot.params['id'];
    if (this.clienteId) {
      this.isEditMode = true;
      this.pageTitle = 'Editar Cliente';
      this.clienteService.getClienteById(this.clienteId).subscribe(cliente => {
        this.clienteForm.patchValue({
          ...cliente,
          tipoDocumentoId: cliente.tipoDocumento.id
        });
      });
    }
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

    const formValue = this.clienteForm.value;
    const request = this.isEditMode
      ? this.clienteService.updateCliente(this.clienteId!, formValue)
      : this.clienteService.createCliente(formValue);

    request.subscribe({
      next: () => this.router.navigate(['/app/clientes']),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Ha ocurrido un error inesperado.';
      }
    });
  }

  get f() { return this.clienteForm.controls; }
}
