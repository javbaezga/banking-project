export type TransactionSortBy = 'id' | 'date';
export type SortDirection = 'asc' | 'desc';

export interface TransactionQueryParams {
  search: string;
  sort_by: TransactionSortBy;
  sort_direction: SortDirection;
  page: number;
  size: number;
}
