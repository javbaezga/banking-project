import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { MainLayoutComponent } from './main-layout.component';
import { HeaderComponent } from '../header/header.component';
import { SidebarComponent } from '../sidebar/sidebar.component';

describe('MainLayoutComponent', () => {
  let fixture: ComponentFixture<MainLayoutComponent>;
  let component: MainLayoutComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MainLayoutComponent, HeaderComponent, SidebarComponent],
      imports: [RouterModule.forRoot([])],
    }).compileComponents();

    fixture = TestBed.createComponent(MainLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the main layout component', () => {
    expect(component).toBeTruthy();
  });

  it('should render a header', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('app-header')).not.toBeNull();
  });

  it('should render a sidebar', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('app-sidebar')).not.toBeNull();
  });

  it('should render a main content area', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.layout__content')).not.toBeNull();
  });

  it('should have a router-outlet inside the content area', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('router-outlet')).not.toBeNull();
  });
});
