import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TransactionService } from './transaction.service';
import { Transaction } from '../models/transaction.model';
import { PageResponse } from '../models/page-response.model';

describe('TransactionService', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;

  const mockTransaction: Transaction = {
    id: 1,
    date: '2024-01-15',
    type: 'CREDIT',
    value: 500.0,
    balance: 1000.0,
    description: 'Deposit',
    status: true,
    account: {
      id: 1,
      number: '123456',
      type: 'SAVINGS',
      initialBalance: 1000,
      balance: 1000,
      dailyBalance: 0,
      dailyBalanceResetDate: '2024-01-01',
      status: true,
      customer: {
        id: 1,
        fullName: 'José Lema',
        gender: 'MALE',
        age: 21,
        idNumber: '1234567890',
        address: 'Otavalo SN y principal',
        phone: '098254785',
        username: 'jose.lema',
        status: true,
      },
    },
  };

  const mockPage: PageResponse<Transaction> = {
    content: [mockTransaction],
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
      providers: [provideHttpClient(), provideHttpClientTesting(), TransactionService],
    });
    service = TestBed.inject(TransactionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET /transactions/query with correct params', () => {
    service
      .query({ search: 'jose', sort_by: 'id', sort_direction: 'asc', page: 0, size: 10 })
      .subscribe((result) => {
        expect(result.content.length).toBe(1);
        expect(result.totalElements).toBe(1);
      });

    const req = httpMock.expectOne((r) => r.url.includes('/transactions/query'));
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('search')).toBe('jose');
    expect(req.request.params.get('sort_by')).toBe('id');
    expect(req.request.params.get('sort_direction')).toBe('asc');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    req.flush(mockPage);
  });

  it('should call POST /transactions on create', () => {
    const formData = { accountNumber: '123456', value: 500, description: 'Deposit' };
    service.create(formData).subscribe((t) => expect(t.id).toBe(1));
    const req = httpMock.expectOne((r) => r.url.endsWith('/transactions'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(formData);
    req.flush(mockTransaction);
  });
});
