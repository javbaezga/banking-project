import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
import { getTestBed } from '@angular/core/testing';

// When running via `ng test` (@angular-builders/jest), its own setup.js already calls
// setupZoneTestEnv(). Skip to avoid NG0400 double-initialization error.
if (!getTestBed().platform) {
  setupZoneTestEnv();
}
