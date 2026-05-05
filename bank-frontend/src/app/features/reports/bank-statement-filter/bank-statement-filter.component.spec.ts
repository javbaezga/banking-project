import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { BankStatementFilterComponent } from './bank-statement-filter.component';
import { CustomerService } from '../../../core/services/customer.service';
import { ReportService } from '../../../core/services/report.service';
import { ErrorDialogComponent } from '../../../shared/error-dialog/error-dialog.component';
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

describe('BankStatementFilterComponent', () => {
  let fixture: ComponentFixture<BankStatementFilterComponent>;
  let component: BankStatementFilterComponent;
  let customerServiceSpy: jest.Mocked<CustomerService>;
  let reportServiceSpy: jest.Mocked<ReportService>;

  beforeEach(async () => {
    customerServiceSpy = {
      getByIdNumber: jest.fn().mockReturnValue(of(mockCustomer)),
      query: jest.fn(),
      getById: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
    } as unknown as jest.Mocked<CustomerService>;

    reportServiceSpy = {
      getBankStatements: jest.fn(),
      downloadBankStatementPdf: jest.fn(),
      lastFilter: null,
    } as unknown as jest.Mocked<ReportService>;

    await TestBed.configureTestingModule({
      declarations: [BankStatementFilterComponent, ErrorDialogComponent],
      imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
      providers: [
        { provide: CustomerService, useValue: customerServiceSpy },
        { provide: ReportService, useValue: reportServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BankStatementFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should build form with customerId, startDate, endDate controls', () => {
    expect(component.form.contains('customerId')).toBe(true);
    expect(component.form.contains('startDate')).toBe(true);
    expect(component.form.contains('endDate')).toBe(true);
  });

  it('should be invalid when empty', () => {
    expect(component.form.valid).toBe(false);
  });

  it('should validate startDate format (YYYY-MM-DD)', () => {
    const ctrl = component.form.get('startDate')!;
    ctrl.setValue('15-01-2024');
    ctrl.markAsTouched();
    expect(ctrl.hasError('dateFormat')).toBe(true);
  });

  it('should accept valid date format', () => {
    const ctrl = component.form.get('startDate')!;
    ctrl.setValue('2024-01-15');
    ctrl.markAsTouched();
    expect(ctrl.hasError('dateFormat')).toBe(false);
  });

  it('should search customer by idNumber', () => {
    component.customerIdNumberInput = '1234567890';
    component.onSearchCustomer();
    expect(customerServiceSpy.getByIdNumber).toHaveBeenCalledWith('1234567890');
    expect(component.foundCustomer).toEqual(mockCustomer);
  });

  it('should set error when customer not found', () => {
    customerServiceSpy.getByIdNumber.mockReturnValue(
      throwError(() => ({ status: 404 })),
    );
    component.customerIdNumberInput = '9999999999';
    component.onSearchCustomer();
    expect(component.errorMessage).toBeTruthy();
    expect(component.foundCustomer).toBeNull();
  });

  it('should select found customer and patch form', () => {
    component.foundCustomer = mockCustomer;
    component.onSelectCustomer();
    expect(component.selectedCustomer).toEqual(mockCustomer);
    expect(component.form.get('customerId')!.value).toBe(1);
  });

  it('should clear form on onClear', () => {
    component.selectedCustomer = mockCustomer;
    component.customerIdNumberInput = '1234567890';
    component.form.patchValue({ customerId: 1, startDate: '2024-01-01', endDate: '2024-01-31' });
    component.onClear();
    expect(component.selectedCustomer).toBeNull();
    expect(component.customerIdNumberInput).toBe('');
    expect(reportServiceSpy.lastFilter).toBeNull();
  });

  it('should close error dialog', () => {
    component.errorMessage = 'Some error';
    component.onCloseError();
    expect(component.errorMessage).toBeNull();
  });

  it('should not call getByIdNumber when input is empty', () => {
    component.customerIdNumberInput = '';
    component.onSearchCustomer();
    expect(customerServiceSpy.getByIdNumber).not.toHaveBeenCalled();
  });
});
