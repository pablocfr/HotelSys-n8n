import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class CatalogoService {
  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getTiposDocumento(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/tipos-documento`, { headers: this.getAuthHeaders() });
  }

  getTiposHabitacion(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/tipos-habitacion`, { headers: this.getAuthHeaders() });
  }

  getEstadosHabitacion(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/estados-habitacion`, { headers: this.getAuthHeaders() });
  }
}
