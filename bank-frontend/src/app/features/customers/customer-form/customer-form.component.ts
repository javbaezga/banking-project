import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../../../core/services/customer.service';
import { ApiError } from '../../../core/models/api-error.model';
import { CustomerFormData } from '../../../core/models/customer.model';
import { HttpErrorResponse } from '@angular/common/http';
import { allZerosValidator, onlyDigitsValidator } from '../../../shared/validators/form-validators';

@Component({
  selector: 'app-customer-form',
  standalone: false,
  templateUrl: './customer-form.component.html',
  styleUrls: ['./customer-form.component.css'],
})
export class CustomerFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  customerId: number | null = null;
  loading = false;
  errorMessage: string | null = null;
  private returnQueryParams: Record<string, string | number | null> = {};

  readonly genderOptions = [
    { label: 'Male', value: 'MALE' },
    { label: 'Female', value: 'FEMALE' },
  ];

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly customerService: CustomerService,
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.customerId = Number(idParam);
    }

    const qp = this.route.snapshot.queryParamMap;
    this.returnQueryParams = {
      page: qp.get('page'),
      size: qp.get('size'),
      search: qp.get('search'),
      sort_by: qp.get('sort_by'),
      sort_direction: qp.get('sort_direction'),
    };

    this.buildForm();

    if (this.isEditMode && this.customerId !== null) {
      this.loadCustomer(this.customerId);
    }
  }

  private buildForm(): void {
    this.form = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      gender: ['MALE', [Validators.required]],
      age: [null, [Validators.required, Validators.min(13), Validators.max(120)]],
      idNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/), allZerosValidator]],
      address: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(255)]],
      phone: ['', [Validators.required, Validators.minLength(9), Validators.maxLength(10), onlyDigitsValidator, allZerosValidator]],
      username: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(25)]],
      password: ['', this.isEditMode
        ? [Validators.minLength(8), Validators.maxLength(50)]
        : [Validators.required, Validators.minLength(8), Validators.maxLength(50)]],
      status: [true, [Validators.required]],
    });
  }

  private loadCustomer(id: number): void {
    this.loading = true;
    this.customerService.getById(id).subscribe({
      next: (customer) => {
        this.form.patchValue({
          fullName: customer.fullName,
          gender: customer.gender,
          age: customer.age,
          idNumber: customer.idNumber,
          address: customer.address,
          phone: customer.phone,
          username: customer.username,
          status: customer.status,
        });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  onSave(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const data: CustomerFormData = { ...this.form.value } as CustomerFormData;
    if (this.isEditMode && !data.password) {
      delete data.password;
    }

    const request$ = this.isEditMode && this.customerId !== null
      ? this.customerService.update(this.customerId, data)
      : this.customerService.create(data);

    request$.subscribe({
      next: () => {
        this.loading = false;
        this.goBack();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const apiError: ApiError = err.error as ApiError;
        this.errorMessage = apiError?.detail ?? 'An unexpected error occurred.';
      },
    });
  }

  onCancel(): void {
    this.goBack();
  }

  onCloseError(): void {
    this.errorMessage = null;
  }

  hasError(field: string, error: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.touched && ctrl.hasError(error));
  }

  isTouchedInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.touched && ctrl.invalid);
  }

  private goBack(): void {
    this.router.navigate(['/customers'], { queryParams: this.returnQueryParams });
  }
}
