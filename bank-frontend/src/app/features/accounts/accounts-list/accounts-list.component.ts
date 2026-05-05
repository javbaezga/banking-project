import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AccountService } from '../../../core/services/account.service';
import { Account } from '../../../core/models/account.model';
import { PageResponse } from '../../../core/models/page-response.model';
import { AccountQueryParams, AccountSortBy } from '../../../core/models/account-query.model';
import { BaseListComponent } from '../../../core/base/base-list.component';

@Component({
  selector: 'app-accounts-list',
  standalone: false,
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.css'],
})
export class AccountsListComponent extends BaseListComponent {
  accounts: Account[] = [];
  sortBy: AccountSortBy = 'id';

  deleteTargetId: number | null = null;
  showDeleteConfirm = false;

  readonly sortableColumns: AccountSortBy[] = ['id', 'number'];

  constructor(
    private readonly accountService: AccountService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    super();
  }

  override ngOnInit(): void {
    const qp = this.route.snapshot.queryParamMap;
    const page = qp.get('page');
    const size = qp.get('size');
    const search = qp.get('search');
    const sortBy = qp.get('sort_by') as AccountSortBy | null;
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
    const params: AccountQueryParams = {
      search: this.searchText,
      sort_by: this.sortBy,
      sort_direction: this.sortDirection,
      page: this.currentPage,
      size: this.pageSize,
    };

    this.accountService.query(params).subscribe({
      next: (res: PageResponse<Account>) => {
        this.accounts = res.content;
        this.totalElements = res.totalElements;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const apiError = err.error as { detail?: string };
        this.errorMessage = 'An error occurred while loading accounts.';
        this.errorDetail = apiError?.detail ?? null;
      },
    });
  }

  onSort(column: AccountSortBy): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = column;
      this.sortDirection = 'asc';
    }
    this.currentPage = 0;
    this.loadData();
  }

  getSortIcon(column: AccountSortBy): string {
    if (this.sortBy !== column) return '↕';
    return this.sortDirection === 'asc' ? '↑' : '↓';
  }

  isSortActive(column: AccountSortBy): boolean {
    return this.sortBy === column;
  }

  onCreate(): void {
    this.router.navigate(['/accounts', 'new']);
  }

  onRefresh(): void {
    this.loadData();
  }

  onEdit(id: number): void {
    this.router.navigate(['/accounts', id, 'edit'], {
      queryParams: {
        page: this.currentPage,
        size: this.pageSize,
        search: this.searchText || null,
        sort_by: this.sortBy,
        sort_direction: this.sortDirection,
      },
    });
  }

  onDeleteRequest(id: number): void {
    this.deleteTargetId = id;
    this.showDeleteConfirm = true;
  }

  onDeleteConfirmed(): void {
    if (this.deleteTargetId === null) return;
    this.showDeleteConfirm = false;
    this.loading = true;
    this.accountService.delete(this.deleteTargetId).subscribe({
      next: () => {
        this.deleteTargetId = null;
        this.loadData();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.deleteTargetId = null;
        const apiError = err.error as { detail?: string };
        this.errorMessage = 'An error occurred while deleting the account.';
        this.errorDetail = apiError?.detail ?? null;
      },
    });
  }

  onDeleteCancelled(): void {
    this.showDeleteConfirm = false;
    this.deleteTargetId = null;
  }

  mapType(type: string): string {
    return type === 'SAVINGS' ? 'S' : 'C';
  }

  mapStatus(status: boolean): string {
    return status ? 'Enabled' : 'Disabled';
  }
}

