import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { TransactionsListComponent } from './transactions-list.component';
import { TransactionService } from '../../../core/services/transaction.service';
import { SkeletonComponent } from '../../../shared/skeleton/skeleton.component';
import { ErrorDialogComponent } from '../../../shared/error-dialog/error-dialog.component';
import { PageResponse } from '../../../core/models/page-response.model';
import { Transaction } from '../../../core/models/transaction.model';

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

const emptyPage: PageResponse<Transaction> = {
  content: [],
  totalElements: 0,
  totalPages: 0,
  number: 0,
  size: 10,
  first: true,
  last: true,
  empty: true,
};

describe('TransactionsListComponent', () => {
  let fixture: ComponentFixture<TransactionsListComponent>;
  let component: TransactionsListComponent;
  let transactionServiceSpy: jest.Mocked<TransactionService>;

  beforeEach(async () => {
    transactionServiceSpy = {
      query: jest.fn().mockReturnValue(of(mockPage)),
      create: jest.fn(),
    } as unknown as jest.Mocked<TransactionService>;

    await TestBed.configureTestingModule({
      declarations: [TransactionsListComponent, SkeletonComponent, ErrorDialogComponent],
      imports: [RouterModule.forRoot([])],
      providers: [{ provide: TransactionService, useValue: transactionServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load transactions on init', () => {
    expect(transactionServiceSpy.query).toHaveBeenCalled();
    expect(component.transactions.length).toBe(1);
    expect(component.totalElements).toBe(1);
  });

  it('should map type CREDIT to C', () => {
    expect(component.mapType('CREDIT')).toBe('C');
  });

  it('should map type DEBIT to D', () => {
    expect(component.mapType('DEBIT')).toBe('D');
  });

  it('should map status true to Completed', () => {
    expect(component.mapStatus(true)).toBe('Completed');
  });

  it('should map status false to Failed', () => {
    expect(component.mapStatus(false)).toBe('Failed');
  });

  it('should toggle sort direction when same column clicked twice', () => {
    component.onSort('id');
    expect(component.sortDirection).toBe('asc');
    component.onSort('id');
    expect(component.sortDirection).toBe('desc');
  });

  it('should reset to asc when switching to a different sort column', () => {
    component.sortBy = 'id';
    component.sortDirection = 'desc';
    component.onSort('date');
    expect(component.sortBy).toBe('date');
    expect(component.sortDirection).toBe('asc');
  });

  it('should show ↑ icon for ascending active column', () => {
    component.sortBy = 'id';
    component.sortDirection = 'asc';
    expect(component.getSortIcon('id')).toBe('↑');
  });

  it('should show ↓ icon for descending active column', () => {
    component.sortBy = 'id';
    component.sortDirection = 'desc';
    expect(component.getSortIcon('id')).toBe('↓');
  });

  it('should show ↕ icon for inactive column', () => {
    component.sortBy = 'id';
    expect(component.getSortIcon('date')).toBe('↕');
  });

  it('should reload on refresh', () => {
    transactionServiceSpy.query.mockClear();
    component.onRefresh();
    expect(transactionServiceSpy.query).toHaveBeenCalledTimes(1);
  });

  it('should show error message on load failure', () => {
    transactionServiceSpy.query.mockReturnValue(
      throwError(() => ({ error: { detail: 'Not found' } })),
    );
    component.onRefresh();
    expect(component.errorMessage).toBeTruthy();
    expect(component.errorDetail).toBe('Not found');
  });
});
