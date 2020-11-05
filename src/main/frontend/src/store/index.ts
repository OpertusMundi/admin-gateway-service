import * as Redux from 'redux';
import * as ReduxLogger from 'redux-logger';
import * as ReduxThunk from 'redux-thunk';

import { connectRouter, routerMiddleware } from 'connected-react-router';
import { loadingBarReducer } from 'react-redux-loading-bar';

import { history } from 'routing';

import { accountReducer } from './account/reducer';
import { configurationReducer } from './config/reducer';
import { mapViewerReducer } from './map/reducer';
import { messageReducer } from './i18n/reducer';
import { securityReducer } from './security/reducer';
import { viewportReducer } from './viewport/reducer';

// Combine reducers
export const rootReducer = Redux.combineReducers({
  account: Redux.combineReducers({
    explorer: accountReducer,
  }),
  config: configurationReducer,
  i18n: messageReducer,
  map: mapViewerReducer,
  security: securityReducer,
  viewport: viewportReducer,
  // Syncs history and store
  router: connectRouter(history),
  // Syncs loading bar and store
  loadingBar: loadingBarReducer,
});

// Declare root state type
export type RootState = ReturnType<typeof rootReducer>;

// Configure middleware components
const middleware = [
  // Support dispatching of functions
  ReduxThunk.default,
  // Intercept navigation actions
  routerMiddleware(history),
];

if (process.env.REACT_APP_ENABLE_LOGGER === 'true') {
  // The logger middleware should always be last
  middleware.push(ReduxLogger.createLogger({ colors: {} }));
}

const store = Redux.createStore(
  rootReducer,
  {},
  Redux.applyMiddleware(...middleware)
);

export default store;
