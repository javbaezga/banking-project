import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SidebarStateService {
  private readonly collapsed$ = new BehaviorSubject<boolean>(false);
  readonly isCollapsed$ = this.collapsed$.asObservable();

  toggle(): void {
    this.collapsed$.next(!this.collapsed$.value);
  }

  get isCollapsed(): boolean {
    return this.collapsed$.value;
  }
}
