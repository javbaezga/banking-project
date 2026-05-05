import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ReportService } from './report.service';
import { BankStatement } from '../models/bank-statement.model';
import { PageResponse } from '../models/page-response.model';

describe('ReportService', () => {
  let service: ReportService;
  let httpMock: HttpTestingController;

  const mockStatement: BankStatement = {
    id: 1,
    date: '2024-01-15',
    type: 'CREDIT',
    value: 500.0,
    balance: 1000.0,
    initialBalance: 500.0,
    status: true,
    accountNumber: '123456',
    customer: 'José Lema',
  };

  const mockPage: PageResponse<BankStatement> = {
    content: [mockStatement],
    totalElements: 1,
    totalPages: 1,
    number: 0,
    size: 10,
    first: true,
    last: true,
    empty: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), ReportService],
    });
    service = TestBed.inject(ReportService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET /reports/bank-statements/{customerId} with correct params', () => {
    service
      .getBankStatements({ customerId: 1, startDate: '2024-01-01', endDate: '2024-01-31', page: 0, size: 10 })
      .subscribe((result) => {
        expect(result.content.length).toBe(1);
        expect(result.totalElements).toBe(1);
      });

    const req = httpMock.expectOne((r) => r.url.includes('/reports/bank-statements/1'));
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('start_date')).toBe('2024-01-01');
    expect(req.request.params.get('end_date')).toBe('2024-01-31');
    req.flush(mockPage);
  });

  it('should call GET /reports/bank-statements/{customerId}/pdf', () => {
    service
      .downloadBankStatementPdf({ customerId: 1, startDate: '2024-01-01', endDate: '2024-01-31' })
      .subscribe((pdf) => expect(pdf).toBe('pdf-content'));

    const req = httpMock.expectOne((r) => r.url.includes('/reports/bank-statements/1/pdf'));
    expect(req.request.method).toBe('GET');
    req.flush('pdf-content');
  });

  it('should store lastFilter', () => {
    const filter = {
      customerId: 1,
      customerIdNumberInput: '1234567890',
      customerFullName: 'José Lema',
      customerIdNumber: '1234567890',
      startDate: '2024-01-01',
      endDate: '2024-01-31',
    };
    service.lastFilter = filter;
    expect(service.lastFilter).toEqual(filter);
  });
});
