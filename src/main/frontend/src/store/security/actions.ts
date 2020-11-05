import {
  SET_CSRF_TOKEN,
  LOGIN_INIT,
  LOGIN_COMPLETE,
  LOGOUT_INIT,
  LOGOUT_COMPLETE,
  PROFILE_LOAD_INIT,
  PROFILE_LOAD_COMPLETE,
  SecurityTypes,
  SET_PASSWORD_COMPLETE,
  SET_PASSWORD_INIT,
  PROFILE_UPDATE_INIT,
  PROFILE_UPDATE_COMPLETE
} from './types';

import { Account } from 'model/account';

// Action Creators
export function setCsrfToken(csrfToken: string | null): SecurityTypes {
  return {
    type: SET_CSRF_TOKEN,
    csrfToken,
  };
}

export function loginInit(): SecurityTypes {
  return {
    type: LOGIN_INIT,
  };
}

export function loginComplete(csrfToken: string, username: string): SecurityTypes {
  return {
    type: LOGIN_COMPLETE,
    csrfToken,
    username,
  };
}

export function logoutInit(): SecurityTypes {
  return {
    type: LOGOUT_INIT,
  };
}

export function logoutComplete(csrfToken: string): SecurityTypes {
  return {
    type: LOGOUT_COMPLETE,
    csrfToken,
  };
}

export function loadProfileInit(): SecurityTypes {
  return {
    type: PROFILE_LOAD_INIT,
  };
}

export function loadProfileComplete(profile: Account): SecurityTypes {
  return {
    type: PROFILE_LOAD_COMPLETE,
    profile,
  };
}

export function updateProfileInit(): SecurityTypes {
  return {
    type: PROFILE_UPDATE_INIT,
  };
}

export function updateProfileComplete(profile: Account): SecurityTypes {
  return {
    type: PROFILE_UPDATE_COMPLETE,
    profile,
  };
}

export function setPasswordInit(): SecurityTypes {
  return {
    type: SET_PASSWORD_INIT,
  };
}

export function setPasswordComplete(): SecurityTypes {
  return {
    type: SET_PASSWORD_COMPLETE,
  };
}
