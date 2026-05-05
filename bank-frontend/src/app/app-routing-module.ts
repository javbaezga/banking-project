import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AccountsListComponent } from './features/accounts/accounts-list/accounts-list.component';
import { AccountFormComponent } from './features/accounts/account-form/account-form.component';
import { TransactionsListComponent } from './features/transactions/transactions-list/transactions-list.component';
import { TransactionFormComponent } from './features/transactions/transaction-form/transaction-form.component';
import { ReportsComponent } from './features/reports/reports.component';
import { BankStatementFilterComponent } from './features/reports/bank-statement-filter/bank-statement-filter.component';
import { BankStatementReportComponent } from './features/reports/bank-statement-report/bank-statement-report.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { CustomersListComponent } from './features/customers/customers-list/customers-list.component';
import { CustomerFormComponent } from './features/customers/customer-form/customer-form.component';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      {
        path: 'customers',
        children: [
          { path: '', component: CustomersListComponent },
          { path: 'new', component: CustomerFormComponent },
          { path: ':id/edit', component: CustomerFormComponent },
        ],
      },
      {
        path: 'accounts',
        children: [
          { path: '', component: AccountsListComponent },
          { path: 'new', component: AccountFormComponent },
          { path: ':id/edit', component: AccountFormComponent },
        ],
      },
      {
        path: 'transactions',
        children: [
          { path: '', component: TransactionsListComponent },
          { path: 'new', component: TransactionFormComponent },
        ],
      },
      {
        path: 'reports',
        children: [
          { path: '', component: ReportsComponent },
          { path: 'bank-statement', component: BankStatementFilterComponent },
          { path: 'bank-statement/result', component: BankStatementReportComponent },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
