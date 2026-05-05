import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomerService } from '../../../core/services/customer.service';
import { Customer } from '../../../core/models/customer.model';
import { PageResponse } from '../../../core/models/page-response.model';
import { CustomerQueryParams, CustomerSortBy } from '../../../core/models/customer-query.model';
import { BaseListComponent } from '../../../core/base/base-list.component';

@Component({
  selector: 'app-customers-list',
  standalone: false,
  templateUrl: './customers-list.component.html',
  styleUrls: ['./customers-list.component.css'],
})
export class CustomersListComponent extends BaseListComponent {
  customers: Customer[] = [];
  sortBy: CustomerSortBy = 'id';

  deleteTargetId: number | null = null;
  showDeleteConfirm = false;

  readonly sortableColumns: CustomerSortBy[] = ['id', 'fullName', 'idNumber', 'username'];

  constructor(
    private readonly customerService: CustomerService,
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
    const sortBy = qp.get('sort_by') as CustomerSortBy | null;
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
    const params: CustomerQueryParams = {
      search: this.searchText,
      sort_by: this.sortBy,
      sort_direction: this.sortDirection,
      page: this.currentPage,
      size: this.pageSize,
    };

    this.customerService.query(params).subscribe({
      next: (page: PageResponse<Customer>) => {
        this.customers = page.content;
        this.totalElements = page.totalElements;
        this.totalPages = page.totalPages;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const apiError = err.error as { detail?: string };
        this.errorMessage = 'An error occurred while loading customers.';
        this.errorDetail = apiError?.detail ?? null;
      },
    });
  }

  onSort(column: CustomerSortBy): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = column;
      this.sortDirection = 'asc';
    }
    this.currentPage = 0;
    this.loadData();
  }

  getSortIcon(column: CustomerSortBy): string {
    if (this.sortBy !== column) return '↕';
    return this.sortDirection === 'asc' ? '↑' : '↓';
  }

  isSortActive(column: CustomerSortBy): boolean {
    return this.sortBy === column;
  }

  onCreate(): void {
    this.router.navigate(['/customers/new']);
  }

  onRefresh(): void {
    this.loadData();
  }

  onEdit(id: number): void {
    this.router.navigate(['/customers', id, 'edit'], {
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
    this.customerService.delete(this.deleteTargetId).subscribe({
      next: () => {
        this.deleteTargetId = null;
        this.loadData();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.deleteTargetId = null;
        const apiError = err.error as { detail?: string };
        this.errorMessage = 'An error occurred while deleting the customer.';
        this.errorDetail = apiError?.detail ?? null;
      },
    });
  }

  onDeleteCancelled(): void {
    this.showDeleteConfirm = false;
    this.deleteTargetId = null;
  }

  mapGender(gender: string): string {
    return gender === 'MALE' ? 'M' : 'F';
  }

  mapStatus(status: boolean): string {
    return status ? 'Enabled' : 'Disabled';
  }
}

