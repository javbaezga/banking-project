import { AbstractControl, ValidationErrors } from '@angular/forms';

export function allZerosValidator(control: AbstractControl): ValidationErrors | null {
  const value: string = control.value ?? '';
  if (value.length > 0 && /^0+$/.test(value)) {
    return { allZeros: true };
  }
  return null;
}

export function onlyDigitsValidator(control: AbstractControl): ValidationErrors | null {
  const value: string = control.value ?? '';
  if (value.length > 0 && !/^\d+$/.test(value)) {
    return { onlyDigits: true };
  }
  return null;
}

export function twoDecimalsValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (value === null || value === '') return null;
  if (!/^-?\d+(\.\d{1,2})?$/.test(String(value))) {
    return { twoDecimals: true };
  }
  return null;
}

export function dateFormatValidator(control: AbstractControl): ValidationErrors | null {
  const value: string = control.value ?? '';
  if (!value) return null;
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return { dateFormat: true };
  }
  return null;
}
