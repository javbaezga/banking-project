import { SafeHtml } from '@angular/platform-browser';

export interface NavItem {
  label: string;
  route: string;
  icon: SafeHtml;
  children?: NavChild[];
}

export interface NavChild {
  label: string;
  route: string;
  icon?: SafeHtml;
}
