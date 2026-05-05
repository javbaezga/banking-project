import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TransactionService } from '../../../core/services/transaction.service';
import { ApiError } from '../../../core/models/api-error.model';
import { TransactionFormData } from '../../../core/models/transaction.model';
import { allZerosValidator, onlyDigitsValidator, twoDecimalsValidator } from '../../../shared/validators/form-validators';

@Component({
  selector: 'app-transaction-form',
  standalone: false,
  templateUrl: './transaction-form.component.html',
  styleUrls: ['./transaction-form.component.css'],
})
export class TransactionFormComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  errorMessage: string | null = null;
  errorDetail: string | null = null;
  private returnQueryParams: Record<string, string | number | null> = {};

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly transactionService: TransactionService,
  ) {}

  ngOnInit(): void {
    const qp = this.route.snapshot.queryParamMap;
    this.returnQueryParams = {
      page: qp.get('page'),
      size: qp.get('size'),
      search: qp.get('search'),
      sort_by: qp.get('sort_by'),
      sort_direction: qp.get('sort_direction'),
    };
    this.buildForm();
  }

  private buildForm(): void {
    this.form = this.fb.group({
      accountNumber: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(6),
          onlyDigitsValidator,
          allZerosValidator,
        ],
      ],
      value: [
        null,
        [
          Validators.required,
          Validators.min(-999999999999.99),
          Validators.max(999999999999.99),
          twoDecimalsValidator,
        ],
      ],
      description: [
        '',
        [Validators.required, Validators.maxLength(50)],
      ],
    });
  }

  onSave(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const data: TransactionFormData = this.form.value as TransactionFormData;

    this.transactionService.create(data).subscribe({
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
    this.router.navigate(['/transactions'], { queryParams: this.returnQueryParams });
  }
}
