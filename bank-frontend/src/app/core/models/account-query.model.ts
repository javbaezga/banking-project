export type AccountSortBy = 'id' | 'number';
export type SortDirection = 'asc' | 'desc';

export interface AccountQueryParams {
  search: string;
  sort_by: AccountSortBy;
  sort_direction: SortDirection;
  page: number;
  size: number;
}
