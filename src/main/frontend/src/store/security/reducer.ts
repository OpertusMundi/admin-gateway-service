import {
  SET_CSRF_TOKEN,
  LOGIN_INIT,
  LOGIN_COMPLETE,
  LOGOUT_INIT,
  LOGOUT_COMPLETE,
  PROFILE_LOAD_COMPLETE,
  SecurityTypes,
  SecurityState,
  PROFILE_UPDATE_COMPLETE
} from "./types";

const initialState: SecurityState = {
  csrfToken: null,
  profile: null,
};

export function securityReducer(
  state = initialState,
  action: SecurityTypes
): SecurityState {

  switch (action.type) {
    case SET_CSRF_TOKEN:
      return {
        ...state,
        csrfToken: action.csrfToken,
      };

    case LOGIN_INIT:
      return {
        ...state,
        profile: null,
      };

    case LOGIN_COMPLETE:
      return {
        ...state,
        csrfToken: action.csrfToken,
      };

    case LOGOUT_INIT:
      return {
        ...initialState,
      };

    case LOGOUT_COMPLETE:
      return {
        ...state,
        csrfToken: action.csrfToken,
      };

    case PROFILE_LOAD_COMPLETE:
      return {
        ...state,
        profile: action.profile,
      };

    case PROFILE_UPDATE_COMPLETE:
      return {
        ...state,
        profile: action.profile,
      };

    default:
      return state;
  }
}
