import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AccountService } from './account.service';
import { Account } from '../models/account.model';
import { PageResponse } from '../models/page-response.model';

const mockCustomer = {
  id: 1, fullName: 'José Lema', gender: 'MALE' as const, age: 21,
  idNumber: '1234567890', address: 'Otavalo SN', phone: '098254785',
  username: 'jose.lema', status: true,
};

const mockAccount: Account = {
  id: 1,
  customer: mockCustomer,
  number: '000001',
  type: 'SAVINGS',
  initialBalance: 500,
  balance: 500,
  dailyBalance: 900,
  dailyBalanceResetDate: '2026-05-03',
  status: true,
};

const mockPage: PageResponse<Account> = {
  content: [mockAccount],
  totalElements: 1,
  totalPages: 1,
  number: 0,
  size: 10,
  first: true,
  last: true,
  empty: false,
};

describe('AccountService', () => {
  let service: AccountService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(AccountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET /accounts/query on query', () => {
    service.query({ search: '', sort_by: 'id', sort_direction: 'asc', page: 0, size: 10 })
      .subscribe((res) => expect(res.content).toHaveLength(1));
    const req = httpMock.expectOne((r) => r.url.includes('/accounts/query'));
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });

  it('should call GET /accounts/{id} on getById', () => {
    service.getById(1).subscribe((a) => expect(a.id).toBe(1));
    const req = httpMock.expectOne((r) => r.url.endsWith('/accounts/1'));
    expect(req.request.method).toBe('GET');
    req.flush(mockAccount);
  });

  it('should call POST /accounts on create', () => {
    const formData = { customerId: 1, number: '000001', type: 'SAVINGS' as const, initialBalance: 500, status: true };
    service.create(formData).subscribe((a) => expect(a.id).toBe(1));
    const req = httpMock.expectOne((r) => r.url.endsWith('/accounts'));
    expect(req.request.method).toBe('POST');
    req.flush(mockAccount);
  });

  it('should call PATCH /accounts/{id} on update', () => {
    const formData = { customerId: 1, number: '000001', type: 'SAVINGS' as const, initialBalance: 500, status: true };
    service.update(1, formData).subscribe((a) => expect(a.id).toBe(1));
    const req = httpMock.expectOne((r) => r.url.endsWith('/accounts/1'));
    expect(req.request.method).toBe('PATCH');
    req.flush(mockAccount);
  });

  it('should call DELETE /accounts/{id} on delete', () => {
    service.delete(1).subscribe(() => expect(true).toBe(true));
    const req = httpMock.expectOne((r) => r.url.endsWith('/accounts/1'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
