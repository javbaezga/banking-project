import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-error-dialog',
  standalone: false,
  templateUrl: './error-dialog.component.html',
  styleUrls: ['./error-dialog.component.css'],
})
export class ErrorDialogComponent {
  @Input() message = 'An unexpected error occurred.';
  @Input() detail: string | null = null;
  @Output() closed = new EventEmitter<void>();

  onClose(): void {
    this.closed.emit();
  }
}
