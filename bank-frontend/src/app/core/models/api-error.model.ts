export interface ApiErrorDetail {
  message: string;
  businessMessage: string;
}

export interface ApiError {
  title: string;
  detail: string;
  errors: ApiErrorDetail[];
  instance: string;
  type: string;
}
