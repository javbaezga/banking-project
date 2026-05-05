export type SortDirection = 'asc' | 'desc';
export type CustomerSortBy = 'id' | 'fullName' | 'idNumber' | 'username';

export interface CustomerQueryParams {
  search: string;
  sort_by: CustomerSortBy;
  sort_direction: SortDirection;
  page: number;
  size: number;
}
