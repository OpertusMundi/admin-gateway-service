import { ApplicationConfiguration } from 'model/configuration'

import { LogoutInitAction } from 'store/security/types';

// State
export interface ConfigurationState extends ApplicationConfiguration {

}

// Actions
export const CONFIG_LOAD_INIT = 'CONFIG_LOAD_INIT'
export const CONFIG_LOAD_COMPLETE = 'CONFIG_LOAD_COMPLETE'

interface ConfigLoadInitAction {
  type: typeof CONFIG_LOAD_INIT,
}

interface ConfigLoadCompleteAction {
  type: typeof CONFIG_LOAD_COMPLETE,
  config: ApplicationConfiguration,
}

export type ConfigurationTypes = ConfigLoadInitAction | ConfigLoadCompleteAction | LogoutInitAction;
