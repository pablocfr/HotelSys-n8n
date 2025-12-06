import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/services/auth';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private apiUrl = 'http://localhost:8081/api/clientes';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getClientes(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  getClienteById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  createCliente(cliente: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, cliente, { headers: this.getAuthHeaders() });
  }

  updateCliente(id: number, cliente: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, cliente, { headers: this.getAuthHeaders() });
  }

  desactivarCliente(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/desactivar/${id}`, {}, { headers: this.getAuthHeaders() });
  }

  searchClientes(term: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/buscar`, { params: { term }, headers: this.getAuthHeaders() });
  }
}
