import type { Config } from 'jest';

const config: Config = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  moduleFileExtensions: ['ts', 'html', 'js', 'json', 'mjs'],
  transformIgnorePatterns: [
    'node_modules/(?!(.*\.mjs$|@angular/common/locales/.*\.js$))',
  ],
  transform: {
    '^.+\\.(ts|js|mjs|html|svg)$': [
      '<rootDir>/node_modules/jest-preset-angular/build/index.js',
      {
        tsconfig: '<rootDir>/tsconfig.spec.json',
        stringifyContentPathRegex: '\\.(html|svg)$',
      },
    ],
  },
  collectCoverageFrom: ['src/**/*.ts', '!src/**/*.spec.ts', '!src/main.ts'],
};

export default config;
