import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AccountFormComponent } from './account-form.component';
import { AccountService } from '../../../core/services/account.service';
import { CustomerService } from '../../../core/services/customer.service';
import { SkeletonComponent } from '../../../shared/skeleton/skeleton.component';
import { ErrorDialogComponent } from '../../../shared/error-dialog/error-dialog.component';
import { Account } from '../../../core/models/account.model';
import { Customer } from '../../../core/models/customer.model';

const mockCustomer: Customer = {
  id: 1,
  fullName: 'José Lema',
  gender: 'MALE',
  age: 21,
  idNumber: '1234567890',
  address: 'Otavalo SN',
  phone: '098254785',
  username: 'jose.lema',
  status: true,
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

describe('AccountFormComponent', () => {
  let component: AccountFormComponent;
  let fixture: ComponentFixture<AccountFormComponent>;
  let accountServiceMock: jest.Mocked<AccountService>;
  let customerServiceMock: jest.Mocked<CustomerService>;
  let routerMock: jest.Mocked<Router>;

  function createComponent(id?: string): void {
    TestBed.configureTestingModule({
      declarations: [AccountFormComponent, SkeletonComponent, ErrorDialogComponent],
      imports: [ReactiveFormsModule, FormsModule],
      providers: [
        { provide: AccountService, useValue: accountServiceMock },
        { provide: CustomerService, useValue: customerServiceMock },
        { provide: Router, useValue: routerMock },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => id ?? null }, queryParamMap: { get: () => null } } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(() => {
    accountServiceMock = {
      getById: jest.fn().mockReturnValue(of(mockAccount)),
      create: jest.fn().mockReturnValue(of(mockAccount)),
      update: jest.fn().mockReturnValue(of(mockAccount)),
    } as any;

    customerServiceMock = {
      getByIdNumber: jest.fn().mockReturnValue(of(mockCustomer)),
    } as any;

    routerMock = { navigate: jest.fn() } as any;
  });

  afterEach(() => TestBed.resetTestingModule());

  describe('Create mode', () => {
    beforeEach(() => createComponent());

    it('should create', () => expect(component).toBeTruthy());

    it('should be in create mode', () => {
      expect(component.isEditMode).toBe(false);
    });

    it('should mark form invalid and not call create when form empty', () => {
      component.onSave();
      expect(accountServiceMock.create).not.toHaveBeenCalled();
      expect(component.form.touched).toBe(true);
    });

    it('should call create with valid form', () => {
      component.selectedCustomer = mockCustomer;
      component.form.setValue({
        customerId: 1,
        number: '000001',
        type: 'SAVINGS',
        initialBalance: 500,
        status: true,
      });
      component.onSave();
      expect(accountServiceMock.create).toHaveBeenCalled();
    });

    it('should navigate to /accounts on cancel', () => {
      component.onCancel();
      expect(routerMock.navigate).toHaveBeenCalledWith(['/accounts'], expect.any(Object));
    });
  });

  describe('Edit mode', () => {
    beforeEach(() => createComponent('1'));

    it('should be in edit mode', () => {
      expect(component.isEditMode).toBe(true);
    });

    it('should load account on init', () => {
      expect(accountServiceMock.getById).toHaveBeenCalledWith(1);
      expect(component.selectedCustomer).toEqual(mockCustomer);
      expect(component.form.get('number')?.value).toBe('000001');
    });

    it('should call update on save', () => {
      component.onSave();
      expect(accountServiceMock.update).toHaveBeenCalledWith(1, expect.any(Object));
    });
  });

  describe('Customer search', () => {
    beforeEach(() => createComponent());

    it('should find customer on search and select on confirm', () => {
      component.customerIdNumberInput = '1234567890';
      component.onSearchCustomer();
      expect(customerServiceMock.getByIdNumber).toHaveBeenCalledWith('1234567890');
      expect(component.foundCustomer).toEqual(mockCustomer);
      component.onSelectCustomer();
      expect(component.selectedCustomer).toEqual(mockCustomer);
      expect(component.form.get('customerId')?.value).toBe(1);
    });

    it('should show error when no customer returned', () => {
      customerServiceMock.getByIdNumber = jest.fn().mockReturnValue(
        throwError(() => new Error()),
      );
      component.customerIdNumberInput = '0000000000';
      component.onSearchCustomer();
      expect(component.foundCustomer).toBeNull();
      expect(component.selectedCustomer).toBeNull();
      expect(component.errorMessage).toBeTruthy();
    });

    it('should show not found on error', () => {
      customerServiceMock.getByIdNumber = jest.fn().mockReturnValue(
        throwError(() => new Error()),
      );
      component.customerIdNumberInput = '0000000000';
      component.onSearchCustomer();
      expect(component.foundCustomer).toBeNull();
    });
  });

  describe('Error handling', () => {
    beforeEach(() => createComponent());

    it('should show error dialog on save failure', () => {
      accountServiceMock.create = jest.fn().mockReturnValue(
        throwError(() => ({ error: { detail: 'Bad request' } })),
      );
      component.selectedCustomer = mockCustomer;
      component.form.setValue({
        customerId: 1,
        number: '000001',
        type: 'SAVINGS',
        initialBalance: 500,
        status: true,
      });
      component.onSave();
      expect(component.errorMessage).toBe('Bad request');
    });
  });
});
