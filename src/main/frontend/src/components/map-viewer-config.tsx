import React from 'react';

import { connect, ConnectedProps } from 'react-redux';
import { injectIntl, IntlShape } from 'react-intl';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

// Store
import { RootState } from 'store';
import { toggleLayer } from 'store/map/actions';

const styles = (theme: Theme) => createStyles({
  nested: {
    paddingLeft: theme.spacing(4),
  },
  childMenu: {
    borderRight: '3px solid #3f51b5',
  }
});

interface MapViewerConfigState {
  open: {
    [index: string]: boolean;
  },
}

interface MapViewerConfigProps extends PropsFromRedux, WithStyles<typeof styles> {
  intl: IntlShape,
}

class MapViewerConfig extends React.Component<MapViewerConfigProps, MapViewerConfigState> {

  state: MapViewerConfigState = {
    open: {},
  }

  onSectionToggle(name: string) {
    this.setState({
      ...this.state,
      open: {
        ...this.state.open,
        [name]: !this.state.open[name],
      }
    });
  }

  onLayerToggle(index: number): void {
    this.props.toggleLayer(index);
  }

  render() {
    return null;
  }
}

const mapState = (state: RootState) => ({
  config: state.config,
  map: state.map,
  profile: state.security.profile,
});

const mapDispatch = {
  toggleLayer,
};


const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>;

// Apply styles
const styledComponent = withStyles(styles)(MapViewerConfig);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject state
export default connector(localizedComponent);
