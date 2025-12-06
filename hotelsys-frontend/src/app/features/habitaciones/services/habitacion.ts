import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/services/auth';

@Injectable({
  providedIn: 'root'
})
export class HabitacionService {
  private apiUrl = 'http://localhost:8081/api/habitaciones';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getHabitaciones(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }
  
  getHabitacionById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  createHabitacion(habitacion: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, habitacion, { headers: this.getAuthHeaders() });
  }

  updateHabitacion(id: number, habitacion: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, habitacion, { headers: this.getAuthHeaders() });
  }

  desactivarHabitacion(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/desactivar/${id}`, {}, { headers: this.getAuthHeaders() });
  }

  getAvailableHabitaciones(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/disponibles`, { headers: this.getAuthHeaders() });
  }
}
