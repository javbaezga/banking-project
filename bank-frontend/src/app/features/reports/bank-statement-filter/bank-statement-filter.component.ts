import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { CustomerService } from '../../../core/services/customer.service';
import { ReportService } from '../../../core/services/report.service';
import { Customer } from '../../../core/models/customer.model';
import { HttpErrorResponse } from '@angular/common/http';
import { dateFormatValidator } from '../../../shared/validators/form-validators';

@Component({
  selector: 'app-bank-statement-filter',
  standalone: false,
  templateUrl: './bank-statement-filter.component.html',
  styleUrls: ['./bank-statement-filter.component.css'],
})
export class BankStatementFilterComponent implements OnInit {
  form: FormGroup;

  customerIdNumberInput = '';
  searchingCustomer = false;
  foundCustomer: Customer | null = null;
  selectedCustomer: Customer | null = null;

  errorMessage: string | null = null;
  errorDetail: string | null = null;

  constructor(
    private readonly fb: FormBuilder,
    private readonly router: Router,
    private readonly customerService: CustomerService,
    private readonly reportService: ReportService,
  ) {
    this.form = this.fb.group({
      customerId: [null, [Validators.required, Validators.min(1)]],
      startDate: ['', [Validators.required, dateFormatValidator]],
      endDate: ['', [Validators.required, dateFormatValidator]],
    });
  }

  ngOnInit(): void {
    const saved = this.reportService.lastFilter;
    if (saved) {
      this.customerIdNumberInput = saved.customerIdNumberInput;
      this.form.patchValue({ customerId: saved.customerId, startDate: saved.startDate, endDate: saved.endDate });
      if (saved.customerId) {
        this.selectedCustomer = {
          id: saved.customerId,
          fullName: saved.customerFullName,
          idNumber: saved.customerIdNumber,
        } as Customer;
      }
    }
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
      error: (err: HttpErrorResponse) => {
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

  onGenerate(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { customerId, startDate, endDate } = this.form.value as {
      customerId: number;
      startDate: string;
      endDate: string;
    };
    this.reportService.lastFilter = {
      customerId,
      customerIdNumberInput: this.customerIdNumberInput,
      customerFullName: this.selectedCustomer?.fullName ?? '',
      customerIdNumber: this.selectedCustomer?.idNumber ?? '',
      startDate,
      endDate,
    };
    this.router.navigate(['/reports/bank-statement/result'], {
      queryParams: { customerId, startDate, endDate, customerName: this.selectedCustomer?.fullName ?? '' },
    });
  }

  onClear(): void {
    this.customerIdNumberInput = '';
    this.foundCustomer = null;
    this.selectedCustomer = null;
    this.form.reset({ customerId: null, startDate: '', endDate: '' });
    this.reportService.lastFilter = null;
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
}
