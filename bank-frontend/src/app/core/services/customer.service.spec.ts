import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { CustomerService } from './customer.service';
import { Customer } from '../models/customer.model';
import { PageResponse } from '../models/page-response.model';

describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;

  const mockCustomer: Customer = {
    id: 1,
    fullName: 'José Lema',
    gender: 'MALE',
    age: 21,
    idNumber: '1234567890',
    address: 'Otavalo SN y principal',
    phone: '098254785',
    username: 'jose.lema',
    status: true,
  };

  const mockPage: PageResponse<Customer> = {
    content: [mockCustomer],
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
      providers: [provideHttpClient(), provideHttpClientTesting(), CustomerService],
    });
    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET /customers/query with correct params', () => {
    service
      .query({ search: 'jose', sort_by: 'id', sort_direction: 'asc', page: 0, size: 10 })
      .subscribe((result) => {
        expect(result.content.length).toBe(1);
        expect(result.totalElements).toBe(1);
      });

    const req = httpMock.expectOne((r) => r.url.includes('/customers/query'));
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('search')).toBe('jose');
    expect(req.request.params.get('sort_by')).toBe('id');
    expect(req.request.params.get('sort_direction')).toBe('asc');
    req.flush(mockPage);
  });

  it('should call GET /customers/{id}', () => {
    service.getById(1).subscribe((c) => expect(c.fullName).toBe('José Lema'));
    const req = httpMock.expectOne((r) => r.url.endsWith('/customers/1'));
    expect(req.request.method).toBe('GET');
    req.flush(mockCustomer);
  });

  it('should call POST /customers on create', () => {
    const formData = { ...mockCustomer, password: 'secret123' };
    delete (formData as Partial<Customer>).id;
    service.create(formData as any).subscribe((c) => expect(c.id).toBe(1));
    const req = httpMock.expectOne((r) => r.url.endsWith('/customers'));
    expect(req.request.method).toBe('POST');
    req.flush(mockCustomer);
  });

  it('should call PATCH /customers/{id} on update', () => {
    const formData = { ...mockCustomer, password: 'secret123' };
    service.update(1, formData as any).subscribe((c) => expect(c.id).toBe(1));
    const req = httpMock.expectOne((r) => r.url.endsWith('/customers/1'));
    expect(req.request.method).toBe('PATCH');
    req.flush(mockCustomer);
  });

  it('should call DELETE /customers/{id}', () => {
    service.delete(1).subscribe(() => expect(true).toBe(true));
    const req = httpMock.expectOne((r) => r.url.endsWith('/customers/1'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
