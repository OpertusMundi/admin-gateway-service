
import { ThunkAction } from 'redux-thunk'

import { RootState } from 'store';
import { ApplicationConfiguration } from 'model/configuration';
import { loadConfigurationInit, loadConfigurationComplete } from './actions';
import { ConfigurationTypes } from './types';
import { ConfigurationApi } from 'service/config';

type ThunkResult<R> = ThunkAction<R, RootState, unknown, ConfigurationTypes>;

export const getConfiguration = (): ThunkResult<Promise<ApplicationConfiguration>> => async (dispatch) => {
  dispatch(loadConfigurationInit());

  const api = new ConfigurationApi();

  const response = await api.getConfiguration();

  dispatch(loadConfigurationComplete(response.data.result));

  return response.data.result;
}

