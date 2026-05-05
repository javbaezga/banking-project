import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  let fixture: ComponentFixture<HeaderComponent>;
  let component: HeaderComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HeaderComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the header component', () => {
    expect(component).toBeTruthy();
  });

  it('should render the BANK title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const title = compiled.querySelector('.app-header__title');
    expect(title?.textContent?.trim()).toBe('BANK');
  });

  it('should have a header element with the app-header class', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const header = compiled.querySelector('.app-header');
    expect(header).not.toBeNull();
  });
});
