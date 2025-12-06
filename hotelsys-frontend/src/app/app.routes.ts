import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { authGuard } from './core/guards/auth-guard';
import { loginGuard } from './core/guards/login-guard';

export const routes: Routes = [
    {
        path: 'login',
        canActivate: [loginGuard],
        loadComponent: () => import('./auth/pages/login/login').then(m => m.Login)
    },
    {
        path: 'app',
        canActivate: [authGuard],
        children: [
            { path: 'home', component: Home },
            {
                path: 'reservas',
                loadChildren: () => import('./features/reservas/reservas.routes').then(m => m.RESERVAS_ROUTES)
            },          
            {
                path: 'productos',
                loadChildren: () => import('./features/productos/productos.routes').then(m => m.PRODUCTOS_ROUTES)
            },
            {
                path: 'habitaciones',
                loadChildren: () => import('./features/habitaciones/habitaciones.routes').then(m => m.HABITACIONES_ROUTES)
            },
            {
                path: 'clientes',
                loadChildren: () => import('./features/clientes/clientes.routes').then(m => m.CLIENTES_ROUTES)
            },
            {
                path: 'comprobantes',
                loadChildren: () => import('./features/comprobantes/comprobantes.routes').then(m => m.COMPROBANTES_ROUTES)
            },
            { path: '', redirectTo: 'home', pathMatch: 'full' }
        ]
    },
    {
        path: '',
        redirectTo: 'app/home',
        pathMatch: 'full'
    },
    {
        path: '**',
        redirectTo: 'app/home'
    }
];
