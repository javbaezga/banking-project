import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from './sidebar.component';

describe('SidebarComponent', () => {
  let fixture: ComponentFixture<SidebarComponent>;
  let component: SidebarComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SidebarComponent],
      imports: [RouterModule.forRoot([])],
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the sidebar component', () => {
    expect(component).toBeTruthy();
  });

  it('should have exactly 4 navigation items', () => {
    expect(component.navItems.length).toBe(4);
  });

  it('should contain Customers nav item', () => {
    const customerItem = component.navItems.find((item) => item.label === 'Customers');
    expect(customerItem).toBeDefined();
    expect(customerItem?.route).toBe('/customers');
  });

  it('should contain Accounts nav item', () => {
    const accountItem = component.navItems.find((item) => item.label === 'Accounts');
    expect(accountItem).toBeDefined();
    expect(accountItem?.route).toBe('/accounts');
  });

  it('should contain Transactions nav item', () => {
    const txItem = component.navItems.find((item) => item.label === 'Transactions');
    expect(txItem).toBeDefined();
    expect(txItem?.route).toBe('/transactions');
  });

  it('should contain Reports nav item', () => {
    const reportItem = component.navItems.find((item) => item.label === 'Reports');
    expect(reportItem).toBeDefined();
    expect(reportItem?.route).toBe('/reports');
  });

  it('should render all nav items in the DOM', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const links = compiled.querySelectorAll('.sidebar__link');
    expect(links.length).toBe(4);
  });
});
