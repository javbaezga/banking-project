import { Component } from '@angular/core';
import { SidebarStateService } from '../sidebar/sidebar-state.service';

@Component({
  selector: 'app-main-layout',
  standalone: false,
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css'],
})
export class MainLayoutComponent {
  constructor(readonly sidebarState: SidebarStateService) {}
}
