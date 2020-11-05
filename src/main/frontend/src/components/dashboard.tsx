import React from 'react';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import { injectIntl, IntlShape } from 'react-intl';

import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

const styles = (theme: Theme) => createStyles({
  container: {
    padding: 8,
  },
  paper: {
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
    borderRadius: 0,
    margin: theme.spacing(2),
  },
});

interface DashboardComponentProps extends WithStyles<typeof styles> {
  intl: IntlShape;
}

interface DashboardComponentState {
}

class DashboardComponent extends React.Component<DashboardComponentProps, DashboardComponentState> {

  render() {
    const { classes } = this.props;

    return (
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Paper className={classes.paper}>
          </Paper>
        </Grid>
      </Grid>
    );
  }

}

// Apply styles
const styledComponent = withStyles(styles)(DashboardComponent);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Export localized component
export default localizedComponent;