import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TransactionService } from '../../../core/services/transaction.service';
import { Transaction } from '../../../core/models/transaction.model';
import { PageResponse } from '../../../core/models/page-response.model';
import { TransactionQueryParams, TransactionSortBy } from '../../../core/models/transaction-query.model';
import { BaseListComponent } from '../../../core/base/base-list.component';

@Component({
  selector: 'app-transactions-list',
  standalone: false,
  templateUrl: './transactions-list.component.html',
  styleUrls: ['./transactions-list.component.css'],
})
export class TransactionsListComponent extends BaseListComponent {
  transactions: Transaction[] = [];
  sortBy: TransactionSortBy = 'id';

  readonly sortableColumns: TransactionSortBy[] = ['id', 'date'];

  constructor(
    private readonly transactionService: TransactionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    super();
    this.sortDirection = 'desc';
  }

  override ngOnInit(): void {
    const qp = this.route.snapshot.queryParamMap;
    const page = qp.get('page');
    const size = qp.get('size');
    const search = qp.get('search');
    const sortBy = qp.get('sort_by') as TransactionSortBy | null;
    const sortDir = qp.get('sort_direction') as 'asc' | 'desc' | null;

    if (page !== null) this.currentPage = Number(page);
    if (size !== null) this.pageSize = Number(size);
    if (search !== null) this.searchText = search;
    if (sortBy !== null) this.sortBy = sortBy;
    if (sortDir !== null) this.sortDirection = sortDir;

    super.ngOnInit();
  }

  protected override loadData(): void {
    this.loading = true;
    const params: TransactionQueryParams = {
      search: this.searchText,
      sort_by: this.sortBy,
      sort_direction: this.sortDirection,
      page: this.currentPage,
      size: this.pageSize,
    };

    this.transactionService.query(params).subscribe({
      next: (res: PageResponse<Transaction>) => {
        this.transactions = res.content;
        this.totalElements = res.totalElements;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const apiError = err.error as { detail?: string };
        this.errorMessage = 'An error occurred while loading transactions.';
        this.errorDetail = apiError?.detail ?? null;
      },
    });
  }

  onSort(column: TransactionSortBy): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = column;
      this.sortDirection = 'asc';
    }
    this.currentPage = 0;
    this.loadData();
  }

  getSortIcon(column: TransactionSortBy): string {
    if (this.sortBy !== column) return '↕';
    return this.sortDirection === 'asc' ? '↑' : '↓';
  }

  isSortActive(column: TransactionSortBy): boolean {
    return this.sortBy === column;
  }

  onCreate(): void {
    this.router.navigate(['/transactions', 'new'], {
      queryParams: {
        page: this.currentPage,
        size: this.pageSize,
        search: this.searchText || null,
        sort_by: this.sortBy,
        sort_direction: this.sortDirection,
      },
    });
  }

  onRefresh(): void {
    this.loadData();
  }

  mapType(type: string): string {
    return type === 'CREDIT' ? 'C' : 'D';
  }

  mapStatus(status: boolean): string {
    return status ? 'Completed' : 'Failed';
  }
}

