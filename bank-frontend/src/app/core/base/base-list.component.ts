import { Directive, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

/**
 * Abstract base for paginated list components.
 * Implements the Template Method pattern — subclasses define `loadData()`
 * while this class provides common pagination, search-debounce, and error state.
 */
@Directive()
export abstract class BaseListComponent implements OnInit, OnDestroy {
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 5;
  searchText = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  loading = false;
  errorMessage: string | null = null;
  errorDetail: string | null = null;

  readonly pageSizeOptions = [5, 10, 20, 50, 100];

  protected readonly searchSubject = new Subject<string>();
  protected readonly destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.searchSubject
      .pipe(debounceTime(400), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPage = 0;
        this.loadData();
      });
    this.loadData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /** Subclasses implement this to fetch and assign their data. */
  protected abstract loadData(): void;

  onSearchChange(value: string): void {
    this.searchText = value;
    this.searchSubject.next(value);
  }

  onPageSizeChange(value: number): void {
    this.pageSize = value;
    this.currentPage = 0;
    this.loadData();
  }

  onCloseError(): void {
    this.errorMessage = null;
    this.errorDetail = null;
  }

  goToFirstPage(): void {
    if (this.currentPage === 0) return;
    this.currentPage = 0;
    this.loadData();
  }

  goToPreviousPage(): void {
    if (this.currentPage === 0) return;
    this.currentPage--;
    this.loadData();
  }

  goToNextPage(): void {
    if (this.currentPage >= this.totalPages - 1) return;
    this.currentPage++;
    this.loadData();
  }

  goToLastPage(): void {
    if (this.currentPage >= this.totalPages - 1) return;
    this.currentPage = this.totalPages - 1;
    this.loadData();
  }
}
