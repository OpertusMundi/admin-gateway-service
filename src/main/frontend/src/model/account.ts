import { Moment } from 'moment';

import { EnumRole } from 'model/role';

export enum EnumSortField {
  EMAIL = 'EMAIL',
  FIRST_NAME = 'FIRST_NAME',
  LAST_NAME = 'LAST_NAME',
}

interface AccountBase {
  active: boolean;
  blocked: boolean;
  email: string;
  firstName: string;
  image: string | null;
  imageMimeType: string | null;
  lastName: string;
  locale: string;
  mobile: string;
  phone: string;
  roles: EnumRole[];
}

export interface AccountCommand extends AccountBase {
  id?: number;
  password: string | null;
  passwordMatch: string | null;
}

export interface Account extends AccountBase {
  createdOn: Moment | null;
  emailVerified: boolean;
  emailVerifiedOn: Moment | null;
  id: number;
  key: string;
  modifiedOn: Moment | null;
}

export interface AccountFormData {
  account: Account;
}

export interface AccountQuery {
  name: string;
}

export interface ProfileCommand {
  firstName: string;
  image: string | null;
  imageMimeType: string | null;
  lastName: string;
  locale: string;
  mobile: string;
  phone: string;
}

export interface SetPasswordCommand {
  password: string | null;
  passwordMatch: string | null;
}
