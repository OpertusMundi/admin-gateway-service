import React from 'react';

import { connect, ConnectedProps } from 'react-redux';
import { FormattedMessage, injectIntl, IntlShape } from 'react-intl';
import { Redirect, Route, RouteComponentProps, Switch, Link } from 'react-router-dom';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Grid from '@material-ui/core/Grid';
import AppBar from '@material-ui/core/AppBar';
import Avatar from '@material-ui/core/Avatar';
import Badge from '@material-ui/core/Badge';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import Collapse from '@material-ui/core/Collapse';
import Container from '@material-ui/core/Container';
import CssBaseline from '@material-ui/core/CssBaseline';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Menu from '@material-ui/core/Menu';
import MenuIcon from '@material-ui/icons/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';

// Icons
import Icon from '@mdi/react';
import {
  mdiAccountMultiple,
  mdiAccountPlusOutline,
  mdiBadgeAccountOutline,
  mdiLayers,
  mdiBell,
  mdiCogOutline,
  mdiEmail,
  mdiFaceAgent,
  mdiForumOutline,
  mdiHandshakeOutline,
  mdiLogoutVariant,
  mdiMapOutline,
  mdiPackageVariantClosed,
  mdiSignatureFreehand,
  mdiTextBoxCheckOutline,
  mdiTools,
  mdiViewDashboardOutline,
} from '@mdi/js';

// Utilities
import clsx from 'clsx';

// Model
import { EnumRole } from 'model/role';
import { DynamicRoutes, ErrorPages, getRoute, StaticRoutes, buildPath } from 'model/routes';
import { Account } from 'model/account';

// Components
import AccountForm from 'components/account/account-form';
import AccountManager from 'components/account/account-grid';
import Breadcrumb from './breadcrumb';
import DashboardComponent from 'components/dashboard';
import AssetDraftManager from 'components/draft/draft-grid';
import ContractForm from 'components/contract/contract-form';
import MapViewerComponent from 'components/map-viewer';
import MapViewerConfigComponent from 'components/map-viewer-config';
import PlaceHolder from 'components/placeholder';
import Profile from 'components/profile';
import SecureContent from 'components/secure-content';
import SecureRoute from 'components/secure-route';

// Store
import { RootState } from 'store';
import { logout } from 'store/security/thunks';

enum EnumSection {
  Resource = 'Resource',
  Admin = 'Admin',
  Drawer = 'Drawer',
  MapDrawer = 'MapDrawer'
};

const drawerWidth = 280;
const mapDrawerWidth = 280;

const styles = (theme: Theme) => createStyles({
  root: {
    display: 'flex',
    padding: 0,
  },
  userContainer: {
    margin: theme.spacing(2, 0, 2, 0),
  },
  avatar: {
    width: theme.spacing(6),
    height: theme.spacing(6),
  },
  container: {
    padding: theme.spacing(1),
    height: 'calc(100vh - 64px)',
  },
  paper: {
    padding: theme.spacing(0),
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
  },
  content: {
    flexGrow: 1,
    height: '100vh',
    overflow: 'auto',
  },
  toolbar: {
    paddingRight: 24, // keep right padding when drawer closed
  },
  toolbarIcon: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '0 8px',
    ...theme.mixins.toolbar,
  },
  toolbarIconSmall: {
    justifyContent: 'center',
  },
  toolbarImage: {
    maxWidth: '80px',
  },
  menuIcon: {
    marginRight: 8,
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
    width: `calc(100% - ${56}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
  },
  appBarShift: {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  },
  menuButton: {
    marginRight: 36,
    marginLeft: -19,
  },
  nested: {
    paddingLeft: theme.spacing(4),
  },
  menuButtonHidden: {
    display: 'none',
  },
  title: {
    flexGrow: 1,
    paddingLeft: 8,
  },
  drawerPaper: {
    position: 'relative',
    whiteSpace: 'nowrap',
    width: drawerWidth,
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  },
  drawerPaperClose: {
    overflowX: 'hidden',
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    width: theme.spacing(7),
  },
  mapDrawerPaper: {
    width: mapDrawerWidth,
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  },
  appBarSpacer: theme.mixins.toolbar,
  fixedHeight: {
    height: 240,
  },
  childMenu: {
    borderRight: '3px solid #3f51b5',
  }
});

interface HomeState {
  open: {
    [EnumSection.Admin]: boolean;
    [EnumSection.Drawer]: boolean;
    [EnumSection.Resource]: boolean;
    [EnumSection.MapDrawer]: boolean;
  },
  menuAnchor: HTMLElement | null,
  isMenuOpen: boolean,
  drawerInTransition: boolean;
}

interface HomeProps extends PropsFromRedux, WithStyles<typeof styles>, RouteComponentProps {
  intl: IntlShape,
}

const menuId = 'primary-search-account-menu';

class Home extends React.Component<HomeProps, HomeState> {

  private drawerRef: React.RefObject<HTMLDivElement> | null;;

  constructor(props: HomeProps) {
    super(props);

    this.drawerRef = React.createRef<HTMLDivElement>();
  }

  state: HomeState = {
    open: {
      [EnumSection.Admin]: false,
      [EnumSection.Drawer]: true,
      [EnumSection.Resource]: false,
      [EnumSection.MapDrawer]: false,
    },
    menuAnchor: null,
    isMenuOpen: false,
    drawerInTransition: false,
  }

  get userName(): string {
    const { email, firstName, lastName } = this.props.profile as Account;

    if (firstName || lastName) {
      return `${firstName} ${lastName}`;
    }

    return email;
  }

  get avatar(): string {
    const { profile } = this.props;

    if (profile?.image && profile?.imageMimeType) {
      return `data:${profile.imageMimeType};base64,${profile.image}`;
    }

    return '';
  }

  componentDidMount() {
    if (this.drawerRef?.current) {
      this.drawerRef.current.addEventListener('transitionstart', () => {
        this.setState({
          drawerInTransition: true,
        });
      }, false);

      this.drawerRef.current.addEventListener('transitionend', () => {
        this.setState({
          drawerInTransition: false,
        });
      }, false);
    }
  }

  onDrawerOpen(section: EnumSection = EnumSection.Drawer) {
    this.setState({
      open: {
        ...this.state.open,
        [section]: true,
      },
    });
  }

  onDrawerClose(section: EnumSection = EnumSection.Drawer) {
    this.setState({
      open: {
        ...this.state.open,
        [section]: false,
      },
    });
  }

  onSectionToggle(key: EnumSection) {
    this.setState({
      ...this.state,
      open: {
        ...this.state.open,
        [key]: !this.state.open[key],
      }
    });
  }

  onNavigate(e: React.MouseEvent | null, url: string) {
    if (e) {
      e.preventDefault();
    }

    this.props.history.push(url);
  }

  onMenuOpen(event: React.MouseEvent<HTMLElement>) {
    this.setState({
      menuAnchor: event.currentTarget,
      isMenuOpen: true,
    });
  };

  onMenuClose(action: 'Profile' | 'Settings' | 'Logout' | null) {
    this.setState({
      menuAnchor: null,
      isMenuOpen: false,
    });
    switch (action) {
      case 'Profile':
        this.onNavigate(null, StaticRoutes.Profile)
        break;
      case 'Settings':
        this.onNavigate(null, StaticRoutes.Settings)
        break;
      case 'Logout':
        this.props.logout();
        break;
    }
  }

  renderMenu() {
    const { classes } = this.props;

    return (
      <Menu
        anchorEl={this.state.menuAnchor}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        id={menuId}
        keepMounted
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
        open={this.state.isMenuOpen}
        onClose={() => this.onMenuClose(null)}
      >
        <MenuItem onClick={() => this.onMenuClose('Profile')}>
          <Icon path={mdiBadgeAccountOutline} size="1.5rem" className={classes.menuIcon} />
          <FormattedMessage id={`menu.profile`} />
        </MenuItem>
        <MenuItem onClick={() => this.onMenuClose('Settings')}>
          <Icon path={mdiCogOutline} size="1.5rem" className={classes.menuIcon} />
          <FormattedMessage id={`menu.settings`} />
        </MenuItem>
        <Divider light />
        <MenuItem onClick={() => this.onMenuClose('Logout')}>
          <Icon path={mdiLogoutVariant} size="1.5rem" className={classes.menuIcon} />
          <FormattedMessage id={`menu.logout`} />
        </MenuItem>
      </Menu>
    );
  }

  renderToolbar() {
    const { classes, location } = this.props;

    const route = getRoute(location.pathname);
    if (!route) {
      return null;
    }

    // const { toolbarComponent: create = null } = route;
    // if (create !== null) {
    //   return (
    //     <>
    //       {create()}
    //     </>
    //   );
    // }

    return (
      <>
        {route &&
          <>
            {route.icon && route.icon()}
          </>
        }
        <Typography component="h1" variant="h6" color="inherit" noWrap className={classes.title}>
          {route ? <FormattedMessage id={route.title || ''} /> : ''}
        </Typography>
      </>
    );
  }

  createAccount(): void {
    const path = buildPath(DynamicRoutes.AccountCreate, []);
    this.props.history.push(path);
  }

  render() {
    const { open } = this.state;
    const { classes } = this.props;

    const _t = this.props.intl.formatMessage;

    return (
      <div className={classes.root}>
        <CssBaseline />
        <AppBar position="absolute" className={clsx(classes.appBar, open[EnumSection.Drawer] && classes.appBarShift)}>
          <Toolbar className={classes.toolbar}>
            <IconButton
              edge="start"
              color="inherit"
              onClick={() => this.onDrawerOpen()}
              className={clsx(classes.menuButton, open[EnumSection.Drawer] && classes.menuButtonHidden)}
            >
              <MenuIcon />
            </IconButton>
            <Breadcrumb />
            <IconButton title="show 4 new mails" color="inherit">
              <Badge badgeContent={4} color="secondary">
                <Icon path={mdiEmail} size="1.5rem" />
              </Badge>
            </IconButton>
            <IconButton title="show 17 new notifications" color="inherit">
              <Badge badgeContent={17} color="secondary">
                <Icon path={mdiBell} size="1.5rem" />
              </Badge>
            </IconButton>
            {this.props.location.pathname === StaticRoutes.Map &&
              <IconButton
                color="inherit"
                onClick={() => this.onDrawerOpen(EnumSection.MapDrawer)}
              >
                <Icon path={mdiLayers} size="1.5rem" />
              </IconButton>
            }
            {this.props.profile?.roles.includes(EnumRole.ADMIN) &&
              <IconButton
                color="inherit"
                onClick={() => this.createAccount()}
              >
                <Icon path={mdiAccountPlusOutline} size="1.5rem" />
              </IconButton>
            }
            <IconButton
              edge="end"
              aria-controls={menuId}
              aria-haspopup="true"
              onClick={(e) => this.onMenuOpen(e)}
              color="inherit"
            >
              <Icon path={mdiTools} size="1.5rem" />
            </IconButton>
          </Toolbar>
        </AppBar>
        {this.renderMenu()}
        <Drawer
          ref={this.drawerRef}
          variant="permanent"
          classes={{
            paper: clsx(classes.drawerPaper, !open[EnumSection.Drawer] && classes.drawerPaperClose),
          }}
          open={open[EnumSection.Drawer]}
        >
          <div className={clsx(classes.toolbarIcon, !open[EnumSection.Drawer] && classes.toolbarIconSmall)}>
            <img
              className={classes.toolbarImage}
              src={open[EnumSection.Drawer] ? '/images/logo-black.svg' : '/images/logo-black-sm.svg'}
              alt=""
            />
            {open[EnumSection.Drawer] &&
              <IconButton onClick={() => this.onDrawerClose()}>
                <ChevronLeftIcon />
              </IconButton>
            }
          </div>
          <Divider />
          <Grid container direction="column" className={classes.userContainer}>
            <Grid container item justify="center">
              <Link to={StaticRoutes.Profile}>
                <Avatar alt={this.userName} src={this.avatar} variant="circle" className={classes.avatar} />
              </Link>
            </Grid>
            {open[EnumSection.Drawer] &&
              <Grid container item justify="center">
                <Typography component="h6" color="inherit" noWrap className={classes.title} align="center">
                  {this.userName}
                </Typography>
              </Grid>
            }
          </Grid>
          <Divider light />
          <List>
            <div>
              <ListItem button
                onClick={(e) => this.onNavigate(e, StaticRoutes.Dashboard)}>
                <ListItemIcon>
                  <Icon path={mdiViewDashboardOutline} size="1.5rem" />
                </ListItemIcon>
                <ListItemText primary={_t({ id: 'links.dashboard' })} />
              </ListItem>

              <ListItem button
                onClick={(e) => this.onNavigate(e, StaticRoutes.ProviderManager)}>
                <ListItemIcon>
                  <Icon path={mdiHandshakeOutline} size="1.5rem" />
                </ListItemIcon>
                <ListItemText primary={_t({ id: 'links.provider-manager' })} />
              </ListItem>

              <ListItem button
                onClick={(e) => this.onNavigate(e, StaticRoutes.DraftManager)}>
                <ListItemIcon>
                  <Icon path={mdiTextBoxCheckOutline} size="1.5rem" />
                </ListItemIcon>
                <ListItemText primary={_t({ id: 'links.draft-manager' })} />
              </ListItem>

              <ListItem button
                onClick={(e) => this.onNavigate(e, StaticRoutes.ConsumerManager)}>
                <ListItemIcon>
                  <Icon path={mdiFaceAgent} size="1.5rem" />
                </ListItemIcon>
                <ListItemText primary={_t({ id: 'links.consumer-manager' })} />
              </ListItem>

              <ListItem button
                onClick={(e) => this.onNavigate(e, StaticRoutes.OrderManager)}>
                <ListItemIcon>
                  <Icon path={mdiPackageVariantClosed} size="1.5rem" />
                </ListItemIcon>
                <ListItemText primary={_t({ id: 'links.order-manager' })} />
              </ListItem>

              <ListItem button
                onClick={(e) => this.onNavigate(e, StaticRoutes.MessageManager)}>
                <ListItemIcon>
                  <Icon path={mdiForumOutline} size="1.5rem" />
                </ListItemIcon>
                <ListItemText primary={_t({ id: 'links.message-manager' })} />
              </ListItem>

              <ListItem button
                onClick={(e) => this.onNavigate(e, StaticRoutes.ContractMaster)}>
                <ListItemIcon>
                  <Icon path={mdiSignatureFreehand} size="1.5rem" />
                </ListItemIcon>
                <ListItemText primary={_t({ id: 'links.contract.master.title' })} />
              </ListItem>

              <ListItem button
                onClick={(e) => this.onNavigate(e, StaticRoutes.Map)}>
                <ListItemIcon>
                  <Icon path={mdiMapOutline} size="1.5rem" />
                </ListItemIcon>
                <ListItemText primary={_t({ id: 'links.map' })} />
              </ListItem>

              <SecureContent roles={[EnumRole.ADMIN]}>
                <>
                  <ListItem button onClick={() => this.onSectionToggle(EnumSection.Admin)}>
                    <ListItemIcon>
                      <Icon path={mdiTools} size="1.5rem" />
                    </ListItemIcon>
                    <ListItemText primary={_t({ id: 'links.admin' })} />
                    {open[EnumSection.Admin] ? <ExpandLess /> : <ExpandMore />}
                  </ListItem>

                  <Collapse in={open[EnumSection.Admin]} timeout="auto" unmountOnExit>
                    <List component="div" disablePadding className={open[EnumSection.Drawer] ? '' : classes.childMenu}>

                      <SecureContent roles={[EnumRole.ADMIN]}>
                        <ListItem button
                          className={open[EnumSection.Drawer] ? classes.nested : ''}
                          onClick={(e) => this.onNavigate(e, StaticRoutes.AccountManager)}>
                          <ListItemIcon>
                            <Icon path={mdiAccountMultiple} size="1.5rem" />
                          </ListItemIcon>
                          <ListItemText primary={_t({ id: 'links.account.explorer' })} />
                        </ListItem>
                      </SecureContent>

                    </List>
                  </Collapse>

                </>
              </SecureContent>

            </div>
          </List>
        </Drawer>
        <main className={classes.content}>
          <div className={classes.appBarSpacer} />
          <Container maxWidth={false} className={classes.container}>
            <Switch>
              <Redirect from="/" to={StaticRoutes.Dashboard} exact />
              {/* Dynamic */}
              <Route path={DynamicRoutes.AccountUpdate} component={AccountForm} />
              <Route path={DynamicRoutes.AccountCreate} component={AccountForm} />
              {/* Static */}
              <Route path={StaticRoutes.Dashboard} component={DashboardComponent} />
              <Route path={StaticRoutes.ContractMaster} component={ContractForm} />
              <Route path={StaticRoutes.DraftManager} component={AssetDraftManager} />
              <Route path={StaticRoutes.Map} component={MapViewerComponent} />
              <Route path={StaticRoutes.Profile} component={Profile} />
              <Route path={StaticRoutes.Settings} component={PlaceHolder} />
              {/* Secured paths */}
              <SecureRoute path={StaticRoutes.AccountManager} component={AccountManager} roles={[EnumRole.ADMIN]} />
              {/* Default */}
              <Redirect push={true} to={ErrorPages.NotFound} />
            </Switch>
          </Container>
        </main>
        <Drawer
          anchor={'right'}
          open={open[EnumSection.MapDrawer]}
          classes={{
            paper: clsx(classes.mapDrawerPaper, !open[EnumSection.Drawer] && classes.drawerPaperClose),
          }}
          onClose={() => this.onDrawerClose(EnumSection.MapDrawer)}
        >
          <MapViewerConfigComponent />
        </Drawer>
      </div >
    );
  }
}

const mapState = (state: RootState) => ({
  profile: state.security.profile,
});

const mapDispatch = {
  logout: () => logout(),
};

const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>;

// Apply styles
const styledComponent = withStyles(styles)(Home);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject state
export default connector(localizedComponent);
