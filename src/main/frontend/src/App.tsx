import React from 'react';

import * as ReactIntl from 'react-intl';

import { Route } from 'react-router-dom';
import { connect, ConnectedProps } from 'react-redux'
import { createMuiTheme } from '@material-ui/core/styles';
import { ThemeProvider } from '@material-ui/styles';

import './App.scss';

// Store
import { RootState } from 'store';
import { setSize } from 'store/viewport/actions';

// Components
import ContentRoot from 'components/content-root';

// Configure material UI theme
const theme = createMuiTheme({
  overrides: {
    MuiDialog: {
      paper: {
        borderRadius: '0px',
      }
    },
  }
});

// Configure localization
require('@formatjs/intl-pluralrules/polyfill');
require('@formatjs/intl-pluralrules/locale-data/el');

require('@formatjs/intl-relativetimeformat/polyfill');
require('@formatjs/intl-relativetimeformat/locale-data/el');

type PropsFromRedux = ConnectedProps<typeof connector>;

class App extends React.Component<PropsFromRedux> {

  private viewportListener?: () => void;

  static defaultProps = {
    locale: 'en',
    messages: {},
  }

  componentDidMount() {
    this.viewportListener = this.setViewport.bind(this);
    window.addEventListener('resize', this.viewportListener);
  }

  componentWillUnmount() {
    if (this.viewportListener) {
      window.removeEventListener('resize', this.viewportListener);
    }
  }

  setViewport() {
    this.props.setSize(
      document.documentElement.clientWidth,
      document.documentElement.clientHeight,
    );
  }

  render() {
    const { locale, messages } = this.props;

    return (
      <ReactIntl.IntlProvider locale={locale} key={locale} messages={messages}>
        <ThemeProvider theme={theme}>
          <Route path="/" component={ContentRoot} />
        </ThemeProvider>
      </ReactIntl.IntlProvider>
    );
  }
}

const mapState = (state: RootState) => {
  const locale = state.i18n.locale;
  const messages = state.i18n.messages[locale];

  return {
    locale,
    messages,
  };
};

const mapDispatch = {
  setSize,
};

const connector = connect(
  mapState,
  mapDispatch,
);

export default connector(App)
