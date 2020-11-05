import React from 'react';

import { connect, ConnectedProps } from 'react-redux';
import { injectIntl, IntlShape } from 'react-intl';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';

// Components
import OpenLayers from 'components/map';

// Store
import { RootState } from 'store';

const styles = (theme: Theme) => createStyles({
  container: {
    height: 'calc(100vh - 64px)',
    width: 'calc(100% + 16px)',
    margin: -8,
  },
});

interface MapViewerComponentProps extends WithStyles<typeof styles>, PropsFromRedux {
  intl: IntlShape;
}

class MapViewerComponent extends React.Component<MapViewerComponentProps> {

  render() {
    const { classes, config } = this.props;

    return (
      <Grid container className={classes.container}>
        <Grid item xs={12}>
          <OpenLayers.Map
            center={[2522457.20, 4743383.34]}
            extent={[2023227.29, 4023104.66, 3569089.78, 5482314.24]}
            maxZoom={19}
            minZoom={6}
            zoom={6}
            height={'100%'}
          >
            <OpenLayers.Layer.BingMaps
              applicationKey={config.bingMaps?.applicationKey}
              imagerySet={'Road'}
            />
          </OpenLayers.Map>
        </Grid>
      </Grid>
    );
  }

}

const mapState = (state: RootState) => ({
  config: state.config,
  map: state.map,
});

const mapDispatch = {
};

const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>

// Apply styles
const styledComponent = withStyles(styles)(MapViewerComponent);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject state
export default connector(localizedComponent);