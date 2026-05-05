import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AccountsListComponent } from './accounts-list.component';
import { AccountService } from '../../../core/services/account.service';
import { SkeletonComponent } from '../../../shared/skeleton/skeleton.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';
import { ErrorDialogComponent } from '../../../shared/error-dialog/error-dialog.component';
import { PageResponse } from '../../../core/models/page-response.model';
import { Account } from '../../../core/models/account.model';

const mockAccount: Account = {
  id: 1,
  customer: {
    id: 1,
    fullName: 'José Lema',
    gender: 'MALE',
    age: 21,
    idNumber: '1234567890',
    address: 'Otavalo SN',
    phone: '098254785',
    username: 'jose.lema',
    status: true,
  },
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

describe('AccountsListComponent', () => {
  let component: AccountsListComponent;
  let fixture: ComponentFixture<AccountsListComponent>;
  let accountServiceMock: jest.Mocked<AccountService>;
  let routerMock: jest.Mocked<Router>;

  beforeEach(async () => {
    accountServiceMock = {
      query: jest.fn().mockReturnValue(of(mockPage)),
      delete: jest.fn(),
    } as any;

    routerMock = { navigate: jest.fn() } as any;

    await TestBed.configureTestingModule({
      declarations: [
        AccountsListComponent,
        SkeletonComponent,
        ConfirmDialogComponent,
        ErrorDialogComponent,
      ],
      providers: [
        { provide: AccountService, useValue: accountServiceMock },
        { provide: Router, useValue: routerMock },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParamMap: convertToParamMap({}) } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load accounts on init', () => {
    expect(accountServiceMock.query).toHaveBeenCalled();
    expect(component.accounts).toHaveLength(1);
    expect(component.totalElements).toBe(1);
  });

  it('should navigate to new account on onCreate', () => {
    component.onCreate();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/accounts', 'new']);
  });

  it('should navigate to edit on onEdit', () => {
    component.onEdit(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/accounts', 1, 'edit'], expect.any(Object));
  });

  it('should show confirm dialog on onDeleteRequest', () => {
    component.onDeleteRequest(1);
    expect(component.showDeleteConfirm).toBe(true);
    expect(component.deleteTargetId).toBe(1);
  });

  it('should cancel delete correctly', () => {
    component.onDeleteRequest(1);
    component.onDeleteCancelled();
    expect(component.showDeleteConfirm).toBe(false);
    expect(component.deleteTargetId).toBeNull();
  });

  it('should call delete and reload on confirmed', () => {
    accountServiceMock.delete = jest.fn().mockReturnValue(of(undefined));
    component.onDeleteRequest(1);
    component.onDeleteConfirmed();
    expect(accountServiceMock.delete).toHaveBeenCalledWith(1);
    expect(accountServiceMock.query).toHaveBeenCalledTimes(2);
  });

  it('should show error on delete failure', () => {
    accountServiceMock.delete = jest.fn().mockReturnValue(
      throwError(() => ({ error: { detail: 'Cannot delete' } })),
    );
    component.onDeleteRequest(1);
    component.onDeleteConfirmed();
    expect(component.errorMessage).toBe('An error occurred while deleting the account.');
    expect(component.errorDetail).toBe('Cannot delete');
  });

  it('should toggle sort direction on same column', () => {
    component.sortBy = 'id';
    component.sortDirection = 'asc';
    component.onSort('id');
    expect(component.sortDirection).toBe('desc');
  });

  it('should reset sort direction on new column', () => {
    component.sortBy = 'id';
    component.sortDirection = 'desc';
    component.onSort('number');
    expect(component.sortBy).toBe('number');
    expect(component.sortDirection).toBe('asc');
  });

  it('should mapType correctly', () => {
    expect(component.mapType('SAVINGS')).toBe('S');
    expect(component.mapType('CURRENT')).toBe('C');
  });

  it('should mapStatus correctly', () => {
    expect(component.mapStatus(true)).toBe('Enabled');
    expect(component.mapStatus(false)).toBe('Disabled');
  });
});
