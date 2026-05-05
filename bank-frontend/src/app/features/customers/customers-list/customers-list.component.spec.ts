import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CustomersListComponent } from './customers-list.component';
import { CustomerService } from '../../../core/services/customer.service';
import { SkeletonComponent } from '../../../shared/skeleton/skeleton.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';
import { PageResponse } from '../../../core/models/page-response.model';
import { Customer } from '../../../core/models/customer.model';

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

const emptyPage: PageResponse<Customer> = {
  content: [],
  totalElements: 0,
  totalPages: 0,
  number: 0,
  size: 10,
  first: true,
  last: true,
  empty: true,
};

describe('CustomersListComponent', () => {
  let fixture: ComponentFixture<CustomersListComponent>;
  let component: CustomersListComponent;
  let customerServiceSpy: jest.Mocked<CustomerService>;

  beforeEach(async () => {
    customerServiceSpy = {
      query: jest.fn().mockReturnValue(of(mockPage)),
      delete: jest.fn().mockReturnValue(of(undefined)),
      getById: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
    } as unknown as jest.Mocked<CustomerService>;

    await TestBed.configureTestingModule({
      declarations: [CustomersListComponent, SkeletonComponent, ConfirmDialogComponent],
      imports: [RouterModule.forRoot([])],
      providers: [{ provide: CustomerService, useValue: customerServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomersListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load customers on init', () => {
    expect(customerServiceSpy.query).toHaveBeenCalled();
    expect(component.customers.length).toBe(1);
    expect(component.totalElements).toBe(1);
  });

  it('should display customers in the DOM', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const rows = compiled.querySelectorAll('.data-table__row');
    expect(rows.length).toBe(1);
  });

  it('should map gender MALE to M', () => {
    expect(component.mapGender('MALE')).toBe('M');
  });

  it('should map gender FEMALE to F', () => {
    expect(component.mapGender('FEMALE')).toBe('F');
  });

  it('should map status true to Enabled', () => {
    expect(component.mapStatus(true)).toBe('Enabled');
  });

  it('should map status false to Disabled', () => {
    expect(component.mapStatus(false)).toBe('Disabled');
  });

  it('should toggle sort direction when same column clicked twice', () => {
    component.onSort('id');
    expect(component.sortDirection).toBe('desc');
    component.onSort('id');
    expect(component.sortDirection).toBe('asc');
  });

  it('should reset to asc when switching to a different sort column', () => {
    component.sortBy = 'id';
    component.sortDirection = 'desc';
    component.onSort('fullName');
    expect(component.sortBy).toBe('fullName');
    expect(component.sortDirection).toBe('asc');
  });

  it('should show sort icon for active column', () => {
    component.sortBy = 'id';
    component.sortDirection = 'asc';
    expect(component.getSortIcon('id')).toBe('↑');
    component.sortDirection = 'desc';
    expect(component.getSortIcon('id')).toBe('↓');
  });

  it('should show neutral icon for inactive column', () => {
    component.sortBy = 'id';
    expect(component.getSortIcon('fullName')).toBe('↕');
  });

  it('should show delete confirmation dialog on onDeleteRequest', () => {
    component.onDeleteRequest(1);
    expect(component.showDeleteConfirm).toBe(true);
    expect(component.deleteTargetId).toBe(1);
  });

  it('should hide dialog on delete cancel', () => {
    component.onDeleteRequest(1);
    component.onDeleteCancelled();
    expect(component.showDeleteConfirm).toBe(false);
    expect(component.deleteTargetId).toBeNull();
  });

  it('should call delete service and reload on confirm', () => {
    component.onDeleteRequest(1);
    component.onDeleteConfirmed();
    expect(customerServiceSpy.delete).toHaveBeenCalledWith(1);
    expect(customerServiceSpy.query).toHaveBeenCalledTimes(2);
  });

  it('should change page size', () => {
    component.onPageSizeChange(20);
    expect(component.pageSize).toBe(20);
    expect(component.currentPage).toBe(0);
  });

  it('should go to first page', () => {
    component.currentPage = 3;
    component.goToFirstPage();
    expect(component.currentPage).toBe(0);
  });

  it('should go to previous page', () => {
    component.currentPage = 2;
    component.totalPages = 5;
    component.goToPreviousPage();
    expect(component.currentPage).toBe(1);
  });

  it('should not go below page 0', () => {
    component.currentPage = 0;
    component.goToPreviousPage();
    expect(component.currentPage).toBe(0);
  });

  it('should go to next page', () => {
    component.currentPage = 0;
    component.totalPages = 3;
    component.goToNextPage();
    expect(component.currentPage).toBe(1);
  });

  it('should not exceed last page', () => {
    component.currentPage = 2;
    component.totalPages = 3;
    component.goToLastPage();
    component.goToNextPage();
    expect(component.currentPage).toBe(2);
  });

  it('should show empty state message when no customers', async () => {
    customerServiceSpy.query.mockReturnValue(of(emptyPage));
    component['loadData' as any]();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const empty = compiled.querySelector('.data-table__empty');
    expect(empty?.textContent?.trim()).toBe('No customers found.');
  });
});
