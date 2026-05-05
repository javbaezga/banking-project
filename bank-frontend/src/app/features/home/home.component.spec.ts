import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  let fixture: ComponentFixture<HomeComponent>;
  let component: HomeComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HomeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the home component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the welcome title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const title = compiled.querySelector('.home__title');
    expect(title?.textContent?.trim()).toBe('Welcome to BANK');
  });

  it('should render 4 feature cards', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const cards = compiled.querySelectorAll('.home__card');
    expect(cards.length).toBe(4);
  });

  it('should display card titles for all modules', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const titles = Array.from(compiled.querySelectorAll('.home__card-title')).map(
      (el) => el.textContent?.trim()
    );
    expect(titles).toContain('Customers');
    expect(titles).toContain('Accounts');
    expect(titles).toContain('Transactions');
    expect(titles).toContain('Reports');
  });

  it('should render a subtitle description', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const subtitle = compiled.querySelector('.home__subtitle');
    expect(subtitle?.textContent?.trim()).not.toBe('');
  });
});
