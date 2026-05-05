import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { HeaderComponent } from './layout/header/header.component';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { HomeComponent } from './features/home/home.component';
import { CustomersComponent } from './features/customers/customers.component';
import { AccountsComponent } from './features/accounts/accounts.component';
import { TransactionsComponent } from './features/transactions/transactions.component';
import { ReportsComponent } from './features/reports/reports.component';
import { BankStatementFilterComponent } from './features/reports/bank-statement-filter/bank-statement-filter.component';
import { BankStatementReportComponent } from './features/reports/bank-statement-report/bank-statement-report.component';
import { CustomersListComponent } from './features/customers/customers-list/customers-list.component';
import { CustomerFormComponent } from './features/customers/customer-form/customer-form.component';
import { AccountsListComponent } from './features/accounts/accounts-list/accounts-list.component';
import { AccountFormComponent } from './features/accounts/account-form/account-form.component';
import { TransactionsListComponent } from './features/transactions/transactions-list/transactions-list.component';
import { TransactionFormComponent } from './features/transactions/transaction-form/transaction-form.component';
import { SkeletonComponent } from './shared/skeleton/skeleton.component';
import { ConfirmDialogComponent } from './shared/confirm-dialog/confirm-dialog.component';
import { ErrorDialogComponent } from './shared/error-dialog/error-dialog.component';

@NgModule({
  declarations: [
    App,
    HeaderComponent,
    SidebarComponent,
    MainLayoutComponent,
    HomeComponent,
    CustomersComponent,
    AccountsComponent,
    TransactionsComponent,
    ReportsComponent,
    BankStatementFilterComponent,
    BankStatementReportComponent,
    CustomersListComponent,
    CustomerFormComponent,
    AccountsListComponent,
    AccountFormComponent,
    TransactionsListComponent,
    TransactionFormComponent,
    SkeletonComponent,
    ConfirmDialogComponent,
    ErrorDialogComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(),
  ],
  bootstrap: [App],
})
export class AppModule {}
