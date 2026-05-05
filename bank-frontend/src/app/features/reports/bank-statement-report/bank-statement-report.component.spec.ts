import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { BankStatementReportComponent } from './bank-statement-report.component';
import { ReportService } from '../../../core/services/report.service';
import { SkeletonComponent } from '../../../shared/skeleton/skeleton.component';
import { ErrorDialogComponent } from '../../../shared/error-dialog/error-dialog.component';
import { BankStatement } from '../../../core/models/bank-statement.model';
import { PageResponse } from '../../../core/models/page-response.model';

const mockStatement: BankStatement = {
  id: 1,
  date: '2024-01-15',
  type: 'CREDIT',
  value: 500.0,
  balance: 1000.0,
  initialBalance: 500.0,
  status: true,
  accountNumber: '123456',
  customer: 'José Lema',
};

const mockPage: PageResponse<BankStatement> = {
  content: [mockStatement],
  totalElements: 1,
  totalPages: 1,
  number: 0,
  size: 10,
  first: true,
  last: true,
  empty: false,
};

describe('BankStatementReportComponent', () => {
  let fixture: ComponentFixture<BankStatementReportComponent>;
  let component: BankStatementReportComponent;
  let reportServiceSpy: jest.Mocked<ReportService>;

  beforeEach(async () => {
    reportServiceSpy = {
      getBankStatements: jest.fn().mockReturnValue(of(mockPage)),
      downloadBankStatementPdf: jest.fn().mockReturnValue(of('')),
      lastFilter: null,
    } as unknown as jest.Mocked<ReportService>;

    await TestBed.configureTestingModule({
      declarations: [BankStatementReportComponent, SkeletonComponent, ErrorDialogComponent],
      imports: [RouterModule.forRoot([])],
      providers: [
        { provide: ReportService, useValue: reportServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ customerId: '1', startDate: '2024-01-01', endDate: '2024-01-31', customerName: 'José Lema' }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BankStatementReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load statements on init from query params', () => {
    expect(reportServiceSpy.getBankStatements).toHaveBeenCalledWith(
      expect.objectContaining({ customerId: 1, startDate: '2024-01-01', endDate: '2024-01-31' }),
    );
    expect(component.statements.length).toBe(1);
    expect(component.totalElements).toBe(1);
  });

  it('should set customer info from query params', () => {
    expect(component.customerId).toBe(1);
    expect(component.startDate).toBe('2024-01-01');
    expect(component.endDate).toBe('2024-01-31');
    expect(component.customerName).toBe('José Lema');
  });

  it('should reload on refresh', () => {
    reportServiceSpy.getBankStatements.mockClear();
    component.onRefresh();
    expect(reportServiceSpy.getBankStatements).toHaveBeenCalledTimes(1);
  });

  it('should show error message on load failure', () => {
    reportServiceSpy.getBankStatements.mockReturnValue(
      throwError(() => ({ error: { detail: 'Not found' } })),
    );
    component.onRefresh();
    expect(component.errorMessage).toBeTruthy();
  });

  it('should skip load when customerId is 0', () => {
    reportServiceSpy.getBankStatements.mockClear();
    component.customerId = 0;
    component.loadStatements();
    expect(reportServiceSpy.getBankStatements).not.toHaveBeenCalled();
  });
});
