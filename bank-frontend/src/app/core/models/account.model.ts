import { Customer } from './customer.model';

export type AccountType = 'SAVINGS' | 'CURRENT';

export interface Account {
  id: number;
  customer: Customer;
  number: string;
  type: AccountType;
  initialBalance: number;
  balance: number;
  dailyBalance: number;
  dailyBalanceResetDate: string;
  status: boolean;
}

export interface AccountFormData {
  customerId: number;
  number: string;
  type: AccountType;
  initialBalance: number;
  status: boolean;
}
