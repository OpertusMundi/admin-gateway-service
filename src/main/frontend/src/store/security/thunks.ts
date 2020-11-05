
import { ThunkAction } from 'redux-thunk'

import { RootState } from 'store';
import { SecurityTypes } from './types';
import { Account, ProfileCommand, SetPasswordCommand } from 'model/account';
import { loginInit, loginComplete, logoutInit, logoutComplete, loadProfileComplete, loadProfileInit, updateProfileInit, updateProfileComplete, setPasswordInit, setPasswordComplete } from './actions';
import { SecurityApi } from 'service/security';
import { CsrfResult } from 'model/security';
import { ObjectResponse } from 'model/response';
import AccountApi from 'service/account';

type ThunkResult<R> = ThunkAction<R, RootState, unknown, SecurityTypes>;

export const login = (username: string, password: string): ThunkResult<Promise<ObjectResponse<CsrfResult>>> => async (dispatch) => {
  dispatch(loginInit());

  const api = new SecurityApi();

  const response = await api.login(username, password);

  dispatch(loginComplete(response.data.result.csrfToken, username));

  return response.data;
}

export const logout = (): ThunkResult<Promise<ObjectResponse<CsrfResult>>> => async (dispatch) => {
  dispatch(logoutInit());

  const api = new SecurityApi();

  const response = await api.logout();

  dispatch(logoutComplete(response.data.result.csrfToken));

  return response.data;
}

export const refreshProfile = (): ThunkResult<Promise<Account>> => async (dispatch) => {
  dispatch(loadProfileInit());

  const api = new AccountApi();

  const response = await api.getProfile();

  dispatch(loadProfileComplete(response.data.result));

  return response.data.result;
};

export const setProfile = (command: ProfileCommand): ThunkResult<Promise<ObjectResponse<Account>>> => async (dispatch) => {
  dispatch(updateProfileInit());

  const api = new AccountApi();

  const response = await api.setProfile(command);

  if (response.data.success) {
    dispatch(updateProfileComplete(response.data.result));
  }

  return response.data;
};

export const setPassword = (command: SetPasswordCommand): ThunkResult<Promise<ObjectResponse<Account>>> => async (dispatch) => {
  dispatch(setPasswordInit());

  const api = new AccountApi();

  const response = await api.setPassword(command);

  dispatch(setPasswordComplete());

  return response.data;
};
