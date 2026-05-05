import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CustomerFormComponent } from './customer-form.component';
import { CustomerService } from '../../../core/services/customer.service';
import { SkeletonComponent } from '../../../shared/skeleton/skeleton.component';
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

describe('CustomerFormComponent — create mode', () => {
  let fixture: ComponentFixture<CustomerFormComponent>;
  let component: CustomerFormComponent;
  let customerServiceSpy: jest.Mocked<CustomerService>;

  beforeEach(async () => {
    customerServiceSpy = {
      create: jest.fn().mockReturnValue(of(mockCustomer)),
      update: jest.fn(),
      getById: jest.fn(),
      query: jest.fn(),
      delete: jest.fn(),
    } as unknown as jest.Mocked<CustomerService>;

    await TestBed.configureTestingModule({
      declarations: [CustomerFormComponent, SkeletonComponent, ErrorDialogComponent],
      imports: [ReactiveFormsModule, RouterModule.forRoot([])],
      providers: [
        { provide: CustomerService, useValue: customerServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({}), queryParamMap: convertToParamMap({}) } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create in create mode', () => {
    expect(component).toBeTruthy();
    expect(component.isEditMode).toBe(false);
  });

  it('should build a form with all required controls', () => {
    const controls = ['fullName', 'gender', 'age', 'idNumber', 'address', 'phone', 'username', 'password', 'status'];
    controls.forEach((c) => expect(component.form.contains(c)).toBe(true));
  });

  it('should mark form invalid when empty', () => {
    expect(component.form.valid).toBe(false);
  });

  it('should validate fullName is required', () => {
    const ctrl = component.form.get('fullName')!;
    ctrl.setValue('');
    ctrl.markAsTouched();
    expect(component.hasError('fullName', 'required')).toBe(true);
  });

  it('should validate age min 13', () => {
    const ctrl = component.form.get('age')!;
    ctrl.setValue(12);
    ctrl.markAsTouched();
    expect(component.hasError('age', 'min')).toBe(true);
  });

  it('should validate age max 120', () => {
    const ctrl = component.form.get('age')!;
    ctrl.setValue(121);
    ctrl.markAsTouched();
    expect(component.hasError('age', 'max')).toBe(true);
  });

  it('should validate idNumber pattern (10 digits)', () => {
    const ctrl = component.form.get('idNumber')!;
    ctrl.setValue('123');
    ctrl.markAsTouched();
    expect(component.hasError('idNumber', 'pattern')).toBe(true);
  });

  it('should reject all-zeros idNumber', () => {
    const ctrl = component.form.get('idNumber')!;
    ctrl.setValue('0000000000');
    ctrl.markAsTouched();
    expect(component.hasError('idNumber', 'allZeros')).toBe(true);
  });

  it('should reject non-digit phone', () => {
    const ctrl = component.form.get('phone')!;
    ctrl.setValue('09825478A');
    ctrl.markAsTouched();
    expect(component.hasError('phone', 'onlyDigits')).toBe(true);
  });

  it('should validate password min 8 chars', () => {
    const ctrl = component.form.get('password')!;
    ctrl.setValue('abc');
    ctrl.markAsTouched();
    expect(component.hasError('password', 'minlength')).toBe(true);
  });

  it('should call create service when form is valid', () => {
    component.form.setValue({
      fullName: 'Test User',
      gender: 'MALE',
      age: 25,
      idNumber: '1234567891',
      address: 'Test Street 123',
      phone: '0987654321',
      username: 'test.user',
      password: 'password123',
      status: true,
    });
    component.onSave();
    expect(customerServiceSpy.create).toHaveBeenCalled();
  });

  it('should show error dialog when API returns error on save', () => {
    customerServiceSpy.create.mockReturnValue(
      throwError(() => ({
        error: { detail: 'Duplicate username' },
      }))
    );
    component.form.setValue({
      fullName: 'Test User',
      gender: 'MALE',
      age: 25,
      idNumber: '1234567891',
      address: 'Test Street 123',
      phone: '0987654321',
      username: 'test.user',
      password: 'password123',
      status: true,
    });
    component.onSave();
    expect(component.errorMessage).toBe('Duplicate username');
  });
});

describe('CustomerFormComponent — edit mode', () => {
  let fixture: ComponentFixture<CustomerFormComponent>;
  let component: CustomerFormComponent;
  let customerServiceSpy: jest.Mocked<CustomerService>;

  beforeEach(async () => {
    customerServiceSpy = {
      getById: jest.fn().mockReturnValue(of(mockCustomer)),
      update: jest.fn().mockReturnValue(of(mockCustomer)),
      create: jest.fn(),
      query: jest.fn(),
      delete: jest.fn(),
    } as unknown as jest.Mocked<CustomerService>;

    await TestBed.configureTestingModule({
      declarations: [CustomerFormComponent, SkeletonComponent, ErrorDialogComponent],
      imports: [ReactiveFormsModule, RouterModule.forRoot([])],
      providers: [
        { provide: CustomerService, useValue: customerServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }), queryParamMap: convertToParamMap({}) } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should detect edit mode from route param', () => {
    expect(component.isEditMode).toBe(true);
    expect(component.customerId).toBe(1);
  });

  it('should load customer data into the form', () => {
    expect(customerServiceSpy.getById).toHaveBeenCalledWith(1);
    expect(component.form.get('fullName')?.value).toBe('José Lema');
  });

  it('should call update service when saving in edit mode', () => {
    component.form.get('password')?.setValue('newpassword');
    component.onSave();
    expect(customerServiceSpy.update).toHaveBeenCalledWith(1, expect.any(Object));
  });
});
