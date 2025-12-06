import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/services/auth';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private apiUrl = 'http://localhost:8081/api/reservas';

  private apiUrlComprobantes = 'http://localhost:8081/api/comprobantes';

  generarComprobante(reservaId: number): Observable<any> {
    return this.http.post(`${this.apiUrlComprobantes}/generar/${reservaId}`, {}, { headers: this.getAuthHeaders() });
  }

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getReservas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  cancelarReserva(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/cancelar/${id}`, {}, { headers: this.getAuthHeaders() });
  }

  getReservaById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  createReserva(reserva: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, reserva, { headers: this.getAuthHeaders() });
  }

  updateReserva(id: number, reserva: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, reserva, { headers: this.getAuthHeaders() });
  }
}
