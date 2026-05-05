import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer, CustomerFormData } from '../models/customer.model';
import { PageResponse } from '../models/page-response.model';
import { CustomerQueryParams } from '../models/customer-query.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly baseUrl = `${environment.apiUrl}/customers`;

  constructor(private readonly http: HttpClient) {}

  query(params: CustomerQueryParams): Observable<PageResponse<Customer>> {
    const httpParams = new HttpParams()
      .set('search', params.search)
      .set('sort_by', params.sort_by)
      .set('sort_direction', params.sort_direction)
      .set('page', params.page.toString())
      .set('size', params.size.toString());

    return this.http.get<PageResponse<Customer>>(`${this.baseUrl}/query`, {
      params: httpParams,
    });
  }

  getById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/${id}`);
  }

  getByIdNumber(idNumber: string): Observable<Customer> {
    const params = new HttpParams().set('id_number', idNumber);
    return this.http.get<Customer>(this.baseUrl, { params });
  }

  create(data: CustomerFormData): Observable<Customer> {
    return this.http.post<Customer>(this.baseUrl, data);
  }

  update(id: number, data: CustomerFormData): Observable<Customer> {
    return this.http.patch<Customer>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
