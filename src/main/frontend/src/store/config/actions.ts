import { ApplicationConfiguration } from 'model/configuration';

import {
  ConfigurationTypes,
  CONFIG_LOAD_COMPLETE,
  CONFIG_LOAD_INIT,
} from './types';


// Action Creators
export function loadConfigurationInit(): ConfigurationTypes {
  return {
    type: CONFIG_LOAD_INIT,
  };
}

export function loadConfigurationComplete(config: ApplicationConfiguration): ConfigurationTypes {
  return {
    type: CONFIG_LOAD_COMPLETE,
    config,
  };
}
