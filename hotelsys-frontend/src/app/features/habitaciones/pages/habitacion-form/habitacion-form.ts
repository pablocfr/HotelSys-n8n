import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HabitacionService } from '../../services/habitacion';
import { CatalogoService } from '../../../../core/services/catalogo';
import { ValidationMessages } from '../../../../shared/components/validation-messages/validation-messages';

@Component({
  selector: 'app-habitacion-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './habitacion-form.html',
  styleUrl: './habitacion-form.scss'
})
export class HabitacionForm implements OnInit {
  habitacionForm: FormGroup;
  tiposHabitacion: any[] = [];
  estadosHabitacion: any[] = [];
  isEditMode = false;
  habitacionId: number | null = null;
  pageTitle = 'Crear Nueva Habitación';
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private habitacionService: HabitacionService,
    private catalogoService: CatalogoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.habitacionForm = this.fb.group({
      numero: ['', Validators.required],
      requiereLimpieza: [false],
      tipoHabitacionId: ['', Validators.required],
      estadoHabitacionId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCatalogos();
    this.habitacionId = this.route.snapshot.params['id'];
    if (this.habitacionId) {
      this.isEditMode = true;
      this.pageTitle = 'Editar Habitación';
      this.habitacionService.getHabitacionById(this.habitacionId).subscribe(habitacion => {
        this.habitacionForm.patchValue({
          ...habitacion,
          tipoHabitacionId: habitacion.tipoHabitacion.id,
          estadoHabitacionId: habitacion.estadoHabitacion.id
        });
      });
    }
  }

  loadCatalogos(): void {
    this.catalogoService.getTiposHabitacion().subscribe(data => this.tiposHabitacion = data);
    this.catalogoService.getEstadosHabitacion().subscribe(data => this.estadosHabitacion = data);
  }

  onSubmit(): void {
    this.errorMessage = null;
    if (this.habitacionForm.invalid) {
      return;
    }

    const formValue = this.habitacionForm.value;
    const request = this.isEditMode
      ? this.habitacionService.updateHabitacion(this.habitacionId!, formValue)
      : this.habitacionService.createHabitacion(formValue);

    request.subscribe({
      next: () => this.router.navigate(['/app/habitaciones']),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Ha ocurrido un error inesperado.';
      }
    });
  }

  get f() { return this.habitacionForm.controls; }
}
