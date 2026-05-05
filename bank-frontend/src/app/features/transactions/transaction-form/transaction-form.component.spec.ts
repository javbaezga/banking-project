import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { TransactionFormComponent } from './transaction-form.component';
import { TransactionService } from '../../../core/services/transaction.service';
import { ErrorDialogComponent } from '../../../shared/error-dialog/error-dialog.component';
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

describe('TransactionFormComponent', () => {
  let fixture: ComponentFixture<TransactionFormComponent>;
  let component: TransactionFormComponent;
  let transactionServiceSpy: jest.Mocked<TransactionService>;

  beforeEach(async () => {
    transactionServiceSpy = {
      create: jest.fn().mockReturnValue(of(mockTransaction)),
      query: jest.fn(),
    } as unknown as jest.Mocked<TransactionService>;

    await TestBed.configureTestingModule({
      declarations: [TransactionFormComponent, ErrorDialogComponent],
      imports: [ReactiveFormsModule, RouterModule.forRoot([])],
      providers: [{ provide: TransactionService, useValue: transactionServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should build form with accountNumber, value, description controls', () => {
    expect(component.form.contains('accountNumber')).toBe(true);
    expect(component.form.contains('value')).toBe(true);
    expect(component.form.contains('description')).toBe(true);
  });

  it('should mark form invalid when empty', () => {
    expect(component.form.valid).toBe(false);
  });

  it('should require accountNumber', () => {
    const ctrl = component.form.get('accountNumber')!;
    ctrl.setValue('');
    ctrl.markAsTouched();
    expect(ctrl.hasError('required')).toBe(true);
  });

  it('should reject accountNumber shorter than 6 digits', () => {
    const ctrl = component.form.get('accountNumber')!;
    ctrl.setValue('12345');
    ctrl.markAsTouched();
    expect(ctrl.hasError('minlength')).toBe(true);
  });

  it('should reject accountNumber longer than 6 digits', () => {
    const ctrl = component.form.get('accountNumber')!;
    ctrl.setValue('1234567');
    ctrl.markAsTouched();
    expect(ctrl.hasError('maxlength')).toBe(true);
  });

  it('should reject accountNumber with non-digit characters', () => {
    const ctrl = component.form.get('accountNumber')!;
    ctrl.setValue('12345A');
    ctrl.markAsTouched();
    expect(ctrl.hasError('onlyDigits')).toBe(true);
  });

  it('should reject all-zeros accountNumber', () => {
    const ctrl = component.form.get('accountNumber')!;
    ctrl.setValue('000000');
    ctrl.markAsTouched();
    expect(ctrl.hasError('allZeros')).toBe(true);
  });

  it('should require value', () => {
    const ctrl = component.form.get('value')!;
    ctrl.setValue(null);
    ctrl.markAsTouched();
    expect(ctrl.hasError('required')).toBe(true);
  });

  it('should reject value with more than 2 decimal places', () => {
    const ctrl = component.form.get('value')!;
    ctrl.setValue(1.234);
    ctrl.markAsTouched();
    expect(ctrl.hasError('twoDecimals')).toBe(true);
  });

  it('should require description', () => {
    const ctrl = component.form.get('description')!;
    ctrl.setValue('');
    ctrl.markAsTouched();
    expect(ctrl.hasError('required')).toBe(true);
  });

  it('should call create service when form is valid', () => {
    component.form.setValue({ accountNumber: '123456', value: 500, description: 'Deposit' });
    component.onSave();
    expect(transactionServiceSpy.create).toHaveBeenCalledWith({
      accountNumber: '123456',
      value: 500,
      description: 'Deposit',
    });
  });

  it('should show error message when API returns error on save', () => {
    transactionServiceSpy.create.mockReturnValue(
      throwError(() => ({ error: { detail: 'Insufficient balance' } })),
    );
    component.form.setValue({ accountNumber: '123456', value: 500, description: 'Deposit' });
    component.onSave();
    expect(component.errorMessage).toBe('Insufficient balance');
  });

  it('should not call create when form is invalid', () => {
    component.onSave();
    expect(transactionServiceSpy.create).not.toHaveBeenCalled();
  });
});
