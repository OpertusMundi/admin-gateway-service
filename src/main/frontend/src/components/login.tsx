import { createStyles, WithStyles } from '@material-ui/core';
import Box from '@material-ui/core/Box';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import Grid from '@material-ui/core/Grid';
import Link from '@material-ui/core/Link';
import Paper from '@material-ui/core/Paper';
import { Theme, withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import React from 'react';
import { FormattedMessage, injectIntl, IntlShape } from 'react-intl';
import { connect, ConnectedProps } from 'react-redux';
import { toast } from 'react-toastify';
import { RootState } from 'store';
import { getConfiguration } from 'store/config/thunks';
import { login, refreshProfile } from 'store/security/thunks';

const styles = (theme: Theme) => createStyles({
  root: {
    height: '100vh',
  },
  logoImage: {
    width: '275px',
  },
  image: {
    backgroundImage: 'url(/images/login-background.jpg)',
    backgroundRepeat: 'no-repeat',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  },
  paper: {
    margin: theme.spacing(8, 4),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    maxWidth: '272px',
    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
    borderRadius: 0,
    textTransform: 'none',
  },
});

interface CopyrightProps {
  link: string;
  title: string;
}

const Copyright = (props: CopyrightProps) => {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {'Copyright © '}
      <Link color="inherit" href={props.link} target="_blank">
        {props.title}
      </Link>{' '}
      {new Date().getFullYear()}
    </Typography>
  );
};

interface LoginState {
  username: string;
  password: string;
}

interface LoginProps extends PropsFromRedux, WithStyles<typeof styles> {
  intl: IntlShape,
}

class Login extends React.Component<LoginProps, LoginState> {

  state: LoginState = {
    username: '',
    password: '',
  }

  submit(e: React.FormEvent) {
    e.preventDefault();

    const { username, password } = this.state;

    this.props.login(username, password)
      .then(() => this.props.getConfiguration())
      .then(() => this.props.refreshProfile())
      .then(
        () => toast.dismiss(),
        () => {
          toast.dismiss();
          toast.error(<FormattedMessage id="login.failure" />);
        })
      .catch((err) => null);
  }

  render() {
    const { classes } = this.props;
    const _t = this.props.intl.formatMessage;


    return (
      <Grid container component="main" className={classes.root}>
        <CssBaseline />
        <Grid item xs={false} sm={5} md={8} lg={9} className={classes.image} />
        <Grid item xs={12} sm={7} md={4} lg={3} component={Paper} elevation={6} square>
          <div className={classes.paper}>
            <img className={classes.logoImage} src="/images/logo-black.svg" alt="" />
            <Typography component="h1" variant="h6">
              <FormattedMessage id="login.subtitle" />
            </Typography>
            <form className={classes.form} autoComplete="off" noValidate onSubmit={(e) => this.submit(e)}>
              <TextField
                variant="standard"
                margin="normal"
                fullWidth
                id="username"
                label={_t({ id: 'login.username' })}
                name="username"
                autoFocus
                defaultValue={this.state.username}
                onChange={(e) => this.setState({ username: e.target.value })}
              />
              <TextField
                variant="standard"
                margin="normal"
                fullWidth
                name="password"
                label={_t({ id: 'login.password' })}
                type="password"
                id="password"
                autoComplete="new-password"
                defaultValue={this.state.password}
                onChange={(e) => this.setState({ password: e.target.value })}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                color="primary"
                className={classes.submit}
              >
                <FormattedMessage id="login.login" />
              </Button>
              <Box mt={5}>
                <Copyright title={_t({ id: 'company.title' })} link={_t({ id: 'company.website' })} />
              </Box>
            </form>
          </div>
        </Grid>
      </Grid>
    );
  }
}

//
// Container component
//

const mapState = (state: RootState) => ({
});

const mapDispatch = {
  login: (username: string, password: string) => login(username, password),
  getConfiguration: () => getConfiguration(),
  refreshProfile: () => refreshProfile(),
};

const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>

// Apply styles
const styledComponent = withStyles(styles)(Login);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject state
export default connector(localizedComponent);
