import React from 'react';
import ReactDOM from 'react-dom';
import * as ReactRedux from 'react-redux';

import { ConnectedRouter } from 'connected-react-router';

// DnD support
import { DndProvider } from 'react-dnd'
import { HTML5Backend } from 'react-dnd-html5-backend'

import './index.scss';
import App from './App';
import * as serviceWorker from './serviceWorker';

import LoadingBar from 'react-redux-loading-bar'

import store from 'store';
import { setCsrfToken } from 'store/security/actions';
import { changeLocale } from 'store/i18n/thunks';
import { getConfiguration } from 'store/config/thunks';
import { refreshProfile } from 'store/security/thunks';
import { bindActionCreators } from 'redux';
import { history } from 'routing';

// TODO: read from non-HttpOnly "language" cookie
const locale = 'en';

// Get initial value of the CSRF token
const token = document.querySelector("meta[name=_csrf]")?.getAttribute('content') || null;

// Wrap thunk actions with dispatch
const actions = bindActionCreators(
  {
    changeLocale,
    getConfiguration,
    refreshProfile,
  },
  store.dispatch,
);

// Chain preliminary actions before initial rendering
Promise.resolve()
  .then(() => store.dispatch(setCsrfToken(token)))
  .then(() => actions.changeLocale(locale))
  .then(() => actions.getConfiguration())
  .then(() => actions.refreshProfile())
  .catch(() => {
    // Ignore an "Unauthorized" error
  })
  .then(() => {
    ReactDOM.render(
      <ReactRedux.Provider store={store}>
        <ConnectedRouter history={history}>
          <LoadingBar style={{ backgroundColor: '#d50000', height: '3px', zIndex: 10000 }} />
          <DndProvider backend={HTML5Backend}>
            <App />
          </DndProvider>
        </ConnectedRouter>
      </ReactRedux.Provider>,
      document.getElementById('root')
    );
  });



// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
