import { Account } from 'model/account';

// State
export interface SecurityState {
  csrfToken: string | null;
  profile: Account | null;
}

// Actions
export const SET_CSRF_TOKEN = 'SET_CSRF_TOKEN'

export const LOGIN_INIT = 'LOGIN_INIT';
export const LOGIN_COMPLETE = 'LOGIN_COMPLETE';

export const LOGOUT_INIT = 'LOGOUT_INIT';
export const LOGOUT_COMPLETE = 'LOGOUT_COMPLETE';

export const PROFILE_LOAD_INIT = 'PROFILE_LOAD_INIT';
export const PROFILE_LOAD_COMPLETE = 'PROFILE_LOAD_COMPLETE';

export const PROFILE_UPDATE_INIT = 'PROFILE_UPDATE_INIT';
export const PROFILE_UPDATE_COMPLETE = 'PROFILE_UPDATE_COMPLETE';

export const SET_PASSWORD_INIT = 'SET_PASSWORD_INIT';
export const SET_PASSWORD_COMPLETE = 'SET_PASSWORD_COMPLETE';

interface SetCsrfTokenAction {
  type: typeof SET_CSRF_TOKEN,
  csrfToken: string | null,
}

interface LoginInitAction {
  type: typeof LOGIN_INIT,
}

interface LoginCompleteAction {
  type: typeof LOGIN_COMPLETE,
  csrfToken: string,
  username: string,
}

export interface LogoutInitAction {
  type: typeof LOGOUT_INIT,
}

interface LogoutCompleteAction {
  type: typeof LOGOUT_COMPLETE,
  csrfToken: string,
}

interface LoadProfileInitAction {
  type: typeof PROFILE_LOAD_INIT,
}

interface LoadProfileCompleteAction {
  type: typeof PROFILE_LOAD_COMPLETE,
  profile: Account,
}

interface UpdateProfileInitAction {
  type: typeof PROFILE_UPDATE_INIT,
}

interface UpdateProfileCompleteAction {
  type: typeof PROFILE_UPDATE_COMPLETE,
  profile: Account,
}

interface SetPasswordInitPassword {
  type: typeof SET_PASSWORD_INIT,
}

interface SetPasswordCompleteAction {
  type: typeof SET_PASSWORD_COMPLETE,
}

export type SecurityTypes =
  | SetCsrfTokenAction
  | LoginInitAction
  | LoginCompleteAction
  | LogoutInitAction
  | LogoutCompleteAction
  | LoadProfileInitAction
  | LoadProfileCompleteAction
  | UpdateProfileInitAction
  | UpdateProfileCompleteAction
  | SetPasswordInitPassword
  | SetPasswordCompleteAction
  ;
