import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account, AccountFormData } from '../models/account.model';
import { PageResponse } from '../models/page-response.model';
import { AccountQueryParams } from '../models/account-query.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private readonly baseUrl = `${environment.apiUrl}/accounts`;

  constructor(private readonly http: HttpClient) {}

  query(params: AccountQueryParams): Observable<PageResponse<Account>> {
    const httpParams = new HttpParams()
      .set('search', params.search)
      .set('sort_by', params.sort_by)
      .set('sort_direction', params.sort_direction)
      .set('page', params.page.toString())
      .set('size', params.size.toString());

    return this.http.get<PageResponse<Account>>(`${this.baseUrl}/query`, {
      params: httpParams,
    });
  }

  getById(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.baseUrl}/${id}`);
  }

  create(data: AccountFormData): Observable<Account> {
    return this.http.post<Account>(this.baseUrl, data);
  }

  update(id: number, data: AccountFormData): Observable<Account> {
    return this.http.patch<Account>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
