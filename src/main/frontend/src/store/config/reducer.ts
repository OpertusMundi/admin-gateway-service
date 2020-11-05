import {
  LOGOUT_INIT,
} from 'store/security/types';

import {
  CONFIG_LOAD_COMPLETE,
  ConfigurationTypes,
  ConfigurationState,
} from 'store/config/types';

const initialState: ConfigurationState = {
};

export function configurationReducer(
  state = initialState,
  action: ConfigurationTypes
): ConfigurationState {

  switch (action.type) {
    case LOGOUT_INIT:
      return {
        ...initialState
      };

    case CONFIG_LOAD_COMPLETE:
      return {
        ...state,
        ...action.config
      };

    default:
      return state;
  }
}
