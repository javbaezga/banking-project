import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BankStatement, BankStatementParams } from '../models/bank-statement.model';
import { PageResponse } from '../models/page-response.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ReportService {
  private readonly baseUrl = `${environment.apiUrl}/reports`;

  lastFilter: {
    customerId: number | null;
    customerIdNumberInput: string;
    customerFullName: string;
    customerIdNumber: string;
    startDate: string;
    endDate: string;
  } | null = null;

  constructor(private readonly http: HttpClient) {}

  getBankStatements(params: BankStatementParams): Observable<PageResponse<BankStatement>> {
    const httpParams = new HttpParams()
      .set('start_date', params.startDate)
      .set('end_date', params.endDate)
      .set('page', params.page.toString())
      .set('size', params.size.toString());

    return this.http.get<PageResponse<BankStatement>>(
      `${this.baseUrl}/bank-statements/${params.customerId}`,
      { params: httpParams },
    );
  }

  downloadBankStatementPdf(params: Omit<BankStatementParams, 'page' | 'size'>): Observable<string> {
    const httpParams = new HttpParams()
      .set('start_date', params.startDate)
      .set('end_date', params.endDate);

    return this.http.get(
      `${this.baseUrl}/bank-statements/${params.customerId}/pdf`,
      { params: httpParams, responseType: 'text' },
    );
  }
}
