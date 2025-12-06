import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/services/auth';

@Injectable({
  providedIn: 'root'
})
export class ComprobanteService {
  private apiUrl = 'http://localhost:8081/api/comprobantes';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getComprobantes(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  downloadComprobante(id: number, formato: 'pdf' | 'xml'): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/descargar/${id}/${formato}`, {
      headers: this.getAuthHeaders(),
      responseType: 'blob' // Le dice a Angular que espere un archivo, no un JSON.
    });
  }

  getComprobanteById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  deleteComprobante(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }
}