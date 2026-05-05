import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-skeleton',
  standalone: false,
  templateUrl: './skeleton.component.html',
  styleUrls: ['./skeleton.component.css'],
})
export class SkeletonComponent {
  @Input() rows = 5;
  get rowsArray(): number[] {
    return Array.from({ length: this.rows }, (_, i) => i);
  }
}
