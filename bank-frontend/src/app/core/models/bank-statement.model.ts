export interface BankStatement {
  id: number;
  date: string;
  customer: string;
  accountNumber: string;
  type: string;
  initialBalance: number;
  status: boolean;
  value: number;
  balance: number;
}

export interface BankStatementParams {
  customerId: number;
  startDate: string;
  endDate: string;
  page: number;
  size: number;
}
