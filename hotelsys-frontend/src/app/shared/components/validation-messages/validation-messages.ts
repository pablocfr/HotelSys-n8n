import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-validation-messages',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="control && control.invalid && (control.dirty || control.touched)" class="invalid-feedback d-block">
      <div *ngIf="control.errors?.['required']">Este campo es requerido.</div>
      <div *ngIf="control.errors?.['email']">Por favor, ingrese un email válido.</div>
      <div *ngIf="control.errors?.['min']">El valor debe ser mayor o igual a {{ control.errors?.['min'].min }}.</div>
    </div>
  `
})
export class ValidationMessages {
  @Input() control: AbstractControl | null = null;
}
