import { Routes } from '@angular/router';
import { HabitacionList } from './pages/habitacion-list/habitacion-list';
import { HabitacionForm } from './pages/habitacion-form/habitacion-form';

export const HABITACIONES_ROUTES: Routes = [
    { path: '', component: HabitacionList },
    { path: 'nueva', component: HabitacionForm },
    { path: 'editar/:id', component: HabitacionForm }
];
