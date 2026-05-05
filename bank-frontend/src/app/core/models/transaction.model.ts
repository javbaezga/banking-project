import { Account } from './account.model';

export type TransactionType = 'CREDIT' | 'DEBIT';

export interface Transaction {
  id: number;
  account: Account;
  date: string;
  type: TransactionType;
  value: number;
  description: string;
  balance: number;
  status: boolean;
}

export interface TransactionFormData {
  accountNumber: string;
  value: number;
  description: string;
}
