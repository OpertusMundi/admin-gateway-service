import React from 'react';

// Routing
import { Link } from 'react-router-dom';

// i18n
import { injectIntl, IntlShape, FormattedMessage } from 'react-intl';

// Components
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import CssBaseline from '@material-ui/core/CssBaseline';
import Container from '@material-ui/core/Container';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';

import Icon from '@mdi/react';
import { mdiHomeOutline } from '@mdi/js';

// Model
import { StaticRoutes } from 'model/routes';

const styles = (theme: Theme) => createStyles({
  container: {
    display: 'flex',
    flexWrap: 'wrap',
    height: '80vh',
  },
  link: {
    textDecoration: 'none',
  },
  button: {
    margin: theme.spacing(3, 0, 2),
    borderRadius: 0,
    textTransform: 'none',
  },
});

interface ErrorPageProps extends WithStyles<typeof styles> {
  intl: IntlShape,
  code: number;
  color: 'primary' | 'secondary' | 'default';
}

class ErrorPage extends React.Component<ErrorPageProps> {

  render() {
    const { code, color, classes } = this.props;

    return (
      <>
        <CssBaseline />
        <Container maxWidth="sm">
          <Grid container justify="center" direction="column" className={classes.container}>
            <Grid item>
              <h1 className='float-left display-3 mr-4'>{code}</h1>
              <h4 className='pt-3'>
                <FormattedMessage id={`error.http-status-code.${code}.title`} />
              </h4>
              <p className='text-muted float-left'>
                <FormattedMessage id={`error.http-status-code.${code}.description`} />
              </p>
            </Grid>
            <Grid item>
              <Link to={StaticRoutes.Dashboard} className={classes.link}>
                <Button
                  variant="contained"
                  color={color}
                  className={classes.button}
                >
                  <Icon path={mdiHomeOutline} size="1rem" style={{ marginRight: 10 }} />
                  <span style={{ fontSize: '1rem' }}>
                    <FormattedMessage id={`error.http-status-code.${code}.button`} />
                  </span> 
                </Button>
              </Link>
            </Grid>
          </Grid>
        </Container>
      </>
    );
  }
}

// Apply styles
const styledComponent = withStyles(styles)(ErrorPage);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

export default localizedComponent;