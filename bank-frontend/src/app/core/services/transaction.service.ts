import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Transaction, TransactionFormData } from '../models/transaction.model';
import { PageResponse } from '../models/page-response.model';
import { TransactionQueryParams } from '../models/transaction-query.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private readonly baseUrl = `${environment.apiUrl}/transactions`;

  constructor(private readonly http: HttpClient) {}

  query(params: TransactionQueryParams): Observable<PageResponse<Transaction>> {
    const httpParams = new HttpParams()
      .set('search', params.search)
      .set('sort_by', params.sort_by)
      .set('sort_direction', params.sort_direction)
      .set('page', params.page.toString())
      .set('size', params.size.toString());

    return this.http.get<PageResponse<Transaction>>(`${this.baseUrl}/query`, {
      params: httpParams,
    });
  }

  create(data: TransactionFormData): Observable<Transaction> {
    return this.http.post<Transaction>(this.baseUrl, data);
  }
}
