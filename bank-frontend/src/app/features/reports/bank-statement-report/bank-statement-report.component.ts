import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ReportService } from '../../../core/services/report.service';
import { BankStatement } from '../../../core/models/bank-statement.model';
import { PageResponse } from '../../../core/models/page-response.model';

@Component({
  selector: 'app-bank-statement-report',
  standalone: false,
  templateUrl: './bank-statement-report.component.html',
  styleUrls: ['./bank-statement-report.component.css'],
})
export class BankStatementReportComponent implements OnInit, OnDestroy {
  statements: BankStatement[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 5;
  loading = false;
  downloading = false;

  customerId = 0;
  startDate = '';
  endDate = '';
  customerName = '';

  errorMessage: string | null = null;
  errorDetail: string | null = null;

  readonly pageSizeOptions = [5, 10, 20, 50, 100];

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly reportService: ReportService,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.pipe(takeUntil(this.destroy$)).subscribe((params) => {
      this.customerId = Number(params['customerId'] ?? 0);
      this.startDate = params['startDate'] ?? '';
      this.endDate = params['endDate'] ?? '';
      this.customerName = params['customerName'] ?? '';
      this.currentPage = 0;
      this.loadStatements();
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadStatements(): void {
    if (!this.customerId || !this.startDate || !this.endDate) return;

    this.loading = true;
    this.reportService
      .getBankStatements({
        customerId: this.customerId,
        startDate: this.startDate,
        endDate: this.endDate,
        page: this.currentPage,
        size: this.pageSize,
      })
      .subscribe({
        next: (page: PageResponse<BankStatement>) => {
          this.statements = page.content;
          this.totalElements = page.totalElements;
          this.totalPages = page.totalPages;
          this.loading = false;
        },
        error: (err: HttpErrorResponse) => {
          this.loading = false;
          const apiError = err.error as { detail?: string };
          this.errorMessage = apiError?.detail ?? 'Failed to load bank statements.';
        },
      });
  }

  onRefresh(): void {
    this.loadStatements();
  }

  onPageSizeChange(value: number): void {
    this.pageSize = value;
    this.currentPage = 0;
    this.loadStatements();
  }

  onDownload(): void {
    if (!this.customerId || !this.startDate || !this.endDate) return;

    this.downloading = true;
    this.reportService
      .downloadBankStatementPdf({
        customerId: this.customerId,
        startDate: this.startDate,
        endDate: this.endDate,
      })
      .subscribe({
        next: (base64: string) => {
          this.downloading = false;
          const binary = atob(base64);
          const bytes = new Uint8Array(binary.length);
          for (let i = 0; i < binary.length; i++) {
          bytes[i] = binary.codePointAt(i) ?? 0;
          }
          const blob = new Blob([bytes], { type: 'application/pdf' });
          const url = URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `bank-statement-${this.customerName || this.customerId}_${this.startDate}_${this.endDate}.pdf`;
          a.click();
          URL.revokeObjectURL(url);
        },
        error: (err: HttpErrorResponse) => {
          this.downloading = false;
          const apiError = err.error as { detail?: string };
          this.errorMessage = apiError?.detail ?? 'Failed to download PDF.';
        },
      });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadStatements();
  }

  onCloseError(): void {
    this.errorMessage = null;
    this.errorDetail = null;
  }

  mapStatus(status: boolean): string {
    return status ? 'Completed' : 'Failed';
  }
}
