import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  standalone: false,
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css'],
})
export class ConfirmDialogComponent {
  @Input() message = 'Are you sure?';
  @Output() accepted = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  onAccept(): void {
    this.accepted.emit();
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
