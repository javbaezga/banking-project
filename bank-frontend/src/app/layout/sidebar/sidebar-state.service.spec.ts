import { TestBed } from '@angular/core/testing';
import { SidebarStateService } from './sidebar-state.service';

describe('SidebarStateService', () => {
  let service: SidebarStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SidebarStateService],
    });
    service = TestBed.inject(SidebarStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start as not collapsed', () => {
    expect(service.isCollapsed).toBe(false);
  });

  it('should toggle to collapsed on first call', () => {
    service.toggle();
    expect(service.isCollapsed).toBe(true);
  });

  it('should toggle back to expanded on second call', () => {
    service.toggle();
    service.toggle();
    expect(service.isCollapsed).toBe(false);
  });

  it('should emit updated value via isCollapsed$', (done) => {
    const values: boolean[] = [];
    service.isCollapsed$.subscribe((val) => values.push(val));
    service.toggle();
    service.toggle();
    expect(values).toEqual([false, true, false]);
    done();
  });
});
