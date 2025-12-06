import { Routes } from '@angular/router';
import { ComprobanteList } from './pages/comprobante-list/comprobante-list';
import { ComprobanteView } from './pages/comprobante-view/comprobante-view';

export const COMPROBANTES_ROUTES: Routes = [
    { path: '', component: ComprobanteList },
    { path: 'ver/:id', component: ComprobanteView }
];
