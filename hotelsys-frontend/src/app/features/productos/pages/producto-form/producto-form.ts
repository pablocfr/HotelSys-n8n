import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductoService } from '../../services/producto';

@Component({
  selector: 'app-producto-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './producto-form.html',
  styleUrl: './producto-form.scss'
})
export class ProductoForm implements OnInit {
  productoForm: FormGroup;
  isEditMode = false;
  productoId: number | null = null;
  pageTitle = 'Crear Nuevo Producto';

  constructor(
    private fb: FormBuilder,
    private productoService: ProductoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.productoForm = this.fb.group({
      nombreProducto: ['', Validators.required],
      precio: ['', [Validators.required, Validators.min(0)]],
      stock: ['', [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.productoId = this.route.snapshot.params['id'];
    if (this.productoId) {
      this.isEditMode = true;
      this.pageTitle = 'Editar Producto';
      this.productoService.getProductoById(this.productoId).subscribe(producto => {
        this.productoForm.patchValue(producto);
      });
    }
  }

  onSubmit(): void {
    if (this.productoForm.invalid) {
      return;
    }

    const formValue = this.productoForm.value;

    if (this.isEditMode && this.productoId) {
      this.productoService.updateProducto(this.productoId, formValue).subscribe(() => {
        this.router.navigate(['/app/productos']);
      });
    } else {
      this.productoService.createProducto(formValue).subscribe(() => {
        this.router.navigate(['/app/productos']);
      });
    }
  }
}
