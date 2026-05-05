import { Component } from '@angular/core';
import { SidebarStateService } from '../sidebar/sidebar-state.service';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  constructor(readonly sidebarState: SidebarStateService) {}
}
