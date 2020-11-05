import React from 'react';

// Localization
import { injectIntl, IntlShape } from 'react-intl';

// Styles
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

// Material UI
import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';

// Custom components
import PageComponent from 'components/contract/form/page';
import ItemComponent from 'components/contract/form/item';

const styles = (theme: Theme) => createStyles({
  paper: {
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
    borderRadius: 0,
    margin: theme.spacing(2),
    minHeight: '70vh',
    width: '100%',
  },
  toolbox: {
    padding: theme.spacing(2),
  }
});

interface ContractFormComponentProps extends WithStyles<typeof styles> {
  intl: IntlShape;
}

interface ContractFormComponentState {
  sections: number;
}

class ContractFormComponent extends React.Component<ContractFormComponentProps, ContractFormComponentState> {

  constructor(props: ContractFormComponentProps) {
    super(props);

    this.state = { sections: 0 };
  }

  render() {
    const { sections } = this.state;
    const { classes } = this.props;

    return (
      <Grid container spacing={2}>
        <Grid container item xs={6}>
          <PageComponent
            sections={sections}
            addSection={(item) => {
              this.setState((state) => ({ sections: state.sections + 1 }));
            }}
          />
        </Grid>
        <Grid container item xs={6}>
          <Paper className={classes.paper}>
            <Grid container item xs={12} className={classes.toolbox}>
              <ItemComponent />
            </Grid>
          </Paper>
        </Grid>
      </Grid >
    );
  }

}

// Apply styles
const styledComponent = withStyles(styles)(ContractFormComponent);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Export localized component
export default localizedComponent;
