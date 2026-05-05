import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AccountService } from '../../../core/services/account.service';
import { CustomerService } from '../../../core/services/customer.service';
import { ApiError } from '../../../core/models/api-error.model';
import { AccountFormData } from '../../../core/models/account.model';
import { Customer } from '../../../core/models/customer.model';
import { allZerosValidator, onlyDigitsValidator, twoDecimalsValidator } from '../../../shared/validators/form-validators';

@Component({
  selector: 'app-account-form',
  standalone: false,
  templateUrl: './account-form.component.html',
  styleUrls: ['./account-form.component.css'],
})
export class AccountFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  accountId: number | null = null;
  loading = false;
  errorMessage: string | null = null;
  errorDetail: string | null = null;
  private returnQueryParams: Record<string, string | number | null> = {};

  // Customer lookup
  customerIdNumberInput = '';
  searchingCustomer = false;
  foundCustomer: Customer | null = null;
  selectedCustomer: Customer | null = null;

  readonly typeOptions = [
    { label: 'Savings', value: 'SAVINGS' },
    { label: 'Current', value: 'CURRENT' },
  ];

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly accountService: AccountService,
    private readonly customerService: CustomerService,
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.accountId = Number(idParam);
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

    if (this.isEditMode && this.accountId !== null) {
      this.loadAccount(this.accountId);
    }
  }

  private buildForm(): void {
    this.form = this.fb.group({
      customerId: [null, [Validators.required, Validators.min(1)]],
      number: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(6),
          onlyDigitsValidator,
          allZerosValidator,
        ],
      ],
      type: ['SAVINGS', [Validators.required]],
      initialBalance: [
        null,
        [
          Validators.required,
          Validators.min(-999999999999.99),
          Validators.max(999999999999.99),
          twoDecimalsValidator,
        ],
      ],
      status: [true, [Validators.required]],
    });
  }

  private loadAccount(id: number): void {
    this.loading = true;
    this.accountService.getById(id).subscribe({
      next: (account) => {
        this.selectedCustomer = account.customer;
        this.customerIdNumberInput = account.customer.idNumber;
        this.form.patchValue({
          customerId: account.customer.id,
          number: account.number,
          type: account.type,
          initialBalance: account.initialBalance,
          status: account.status,
        });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  onSearchCustomer(): void {
    const idNumber = this.customerIdNumberInput.trim();
    if (!idNumber) return;

    this.searchingCustomer = true;
    this.foundCustomer = null;
    this.selectedCustomer = null;
    this.form.patchValue({ customerId: null });

    this.customerService.getByIdNumber(idNumber).subscribe({
      next: (customer) => {
        this.searchingCustomer = false;
        this.foundCustomer = customer;
      },
      error: () => {
        this.searchingCustomer = false;
        this.errorMessage = `No customer found with ID number "${idNumber}".`;
      },
    });
  }

  onSelectCustomer(): void {
    if (!this.foundCustomer) return;
    this.selectedCustomer = this.foundCustomer;
    this.foundCustomer = null;
    this.form.patchValue({ customerId: this.selectedCustomer.id });
  }

  onClearCustomer(): void {
    this.customerIdNumberInput = '';
    this.foundCustomer = null;
    this.selectedCustomer = null;
    this.form.patchValue({ customerId: null });
  }

  onSave(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const data: AccountFormData = this.form.value as AccountFormData;

    const request$ =
      this.isEditMode && this.accountId !== null
        ? this.accountService.update(this.accountId, data)
        : this.accountService.create(data);

    request$.subscribe({
      next: () => {
        this.loading = false;
        this.goBack();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const apiError: ApiError = err.error as ApiError;
        this.errorMessage = apiError?.detail ?? 'An unexpected error occurred.';
        this.errorDetail = null;
      },
    });
  }

  onCancel(): void {
    this.goBack();
  }

  onCloseError(): void {
    this.errorMessage = null;
    this.errorDetail = null;
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
    this.router.navigate(['/accounts'], { queryParams: this.returnQueryParams });
  }
}
