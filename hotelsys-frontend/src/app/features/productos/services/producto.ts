import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/services/auth';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private apiUrl = 'http://localhost:8081/api/productos';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getProductos(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }
  
  getProductoById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  createProducto(producto: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, producto, { headers: this.getAuthHeaders() });
  }

  updateProducto(id: number, producto: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, producto, { headers: this.getAuthHeaders() });
  }

  desactivarProducto(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/desactivar/${id}`, {}, { headers: this.getAuthHeaders() });
  }

  searchProductos(term: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/buscar`, { params: { term }, headers: this.getAuthHeaders() });
  }
}
