export type Gender = 'MALE' | 'FEMALE';

export interface Customer {
  id: number;
  fullName: string;
  gender: Gender;
  age: number;
  idNumber: string;
  address: string;
  phone: string;
  username: string;
  status: boolean;
}

export interface CustomerFormData {
  fullName: string;
  gender: Gender;
  age: number;
  idNumber: string;
  address: string;
  phone: string;
  username: string;
  password?: string;
  status: boolean;
}
