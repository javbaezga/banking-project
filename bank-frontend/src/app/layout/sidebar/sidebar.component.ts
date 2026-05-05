import { Component, OnDestroy } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { NavItem } from './nav-item.model';
import { SidebarStateService } from './sidebar-state.service';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent implements OnDestroy {
  readonly navItems: NavItem[];
  expandedItem: string | null = null;
  currentUrl = '';

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly sanitizer: DomSanitizer,
    private readonly router: Router,
    readonly sidebarState: SidebarStateService,
  ) {
    this.currentUrl = this.router.url;
    this.router.events
      .pipe(
        filter((e): e is NavigationEnd => e instanceof NavigationEnd),
        takeUntil(this.destroy$),
      )
      .subscribe((e) => {
        this.currentUrl = e.urlAfterRedirects;
      });
    const s = (html: string): SafeHtml => sanitizer.bypassSecurityTrustHtml(html);
    this.navItems = [
      { label: 'Customers', route: '/customers', icon: s('<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>') },
      { label: 'Accounts', route: '/accounts', icon: s('<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/></svg>') },
      { label: 'Transactions', route: '/transactions', icon: s('<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M7 16V4m0 0L3 8m4-4 4 4"/><path d="M17 8v12m0 0 4-4m-4 4-4-4"/></svg>') },
      {
        label: 'Reports',
        route: '/reports',
        icon: s('<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M8 17V13"/><path d="M12 17V9"/><path d="M16 17v-4"/></svg>'),
        children: [
          { label: 'Bank Statement', route: '/reports/bank-statement', icon: s('<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>') },
        ],
      },
    ];
  }

  toggleExpand(item: NavItem): void {
    if (this.sidebarState.isCollapsed) {
      void this.router.navigate([item.route]);
      return;
    }
    this.expandedItem = this.expandedItem === item.label ? null : item.label;
  }

  isExpanded(label: string): boolean {
    return this.expandedItem === label;
  }

  isRouteActive(route: string): boolean {
    return this.currentUrl.startsWith(route);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
