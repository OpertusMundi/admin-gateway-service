import React from 'react';

import { connect, ConnectedProps } from 'react-redux';

import { FormattedMessage, injectIntl, IntlShape } from 'react-intl';

import { RouteComponentProps, withRouter } from 'react-router-dom';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Typography from '@material-ui/core/Typography';

// Store
import { RootState } from 'store';

// Model
import { matchRoute } from 'model/routes';
import { iconFunc } from 'model/types';
import { EnumRole } from 'model/role';

const MAX_LENGTH = 10; // maximum number of parts for a breadcrumb

const styles = (theme: Theme) => createStyles({
  breadcrumb: {
    flexGrow: 1,
    '& li': {
      color: '#ffffff',
    }
  },
});

interface BreadcrumbProps extends PropsFromRedux, RouteComponentProps, WithStyles<typeof styles> {
  intl: IntlShape;
}

class Breadcrumb extends React.Component<BreadcrumbProps> {

  private getComponent(): (() => React.ReactNode) | null {
    const match = matchRoute(this.props.location.pathname);
    if ((match) && (match.properties.toolbarComponent)) {
      return match.properties.toolbarComponent;
    }
    return null;
  }

  private checkRoles(routeRoles: ((roles: EnumRole[], state: RootState) => boolean) | EnumRole[] | null, userRoles: EnumRole[], state: RootState): boolean {
    if (typeof routeRoles === 'function') {
      return routeRoles(userRoles, state);
    }

    if ((!routeRoles) || (routeRoles.length === 0)) {
      return true;
    }

    if ((!userRoles) || (userRoles.length === 0)) {
      return false;
    }

    for (let role of userRoles) {
      if (routeRoles.indexOf(role) !== -1) {
        return true;
      }
    }
    return false;
  }

  private renderItem(path: string, active: boolean, title: React.ReactNode, locked: boolean, icon?: iconFunc): React.ReactNode {
    return (
      <div key={path} style={{ display: 'flex', alignItems: 'center' }}>
        {icon &&
          <div style={{ display: 'flex', marginRight: 10 }}>
            {icon()}
          </div>
        }
        <Typography key={path} component="h6" variant="h6" color="inherit" noWrap>{title}</Typography>
      </div>
    );
  }

  render() {
    const { classes, location, profile, state } = this.props;

    const toolbarFactory = this.getComponent();

    const roles = profile ? profile.roles : [];

    const paths = location.pathname.split('/')
      .slice(1, 1 + MAX_LENGTH)
      .reduce((res, part) => {
        if (part.length > 0) {
          var prevPath = res.length > 0 ? res[res.length - 1] : "";
          res.push(prevPath + (prevPath.endsWith("/") ? "" : "/") + part);
        }
        return res;
      }, ["/"]);

    return (
      <>
        <Breadcrumbs separator="›" className={classes.breadcrumb}>
          {paths.map((path) => {
            const active = location.pathname === path;
            const match = matchRoute(path);
            if (!match) {
              return null;
            }
            if (match.properties?.breadcrumb === false) {
              return null;
            }

            const title = (
              <FormattedMessage id={match.properties.title} defaultMessage={match.properties.defaultTitle} />
            );
            const icon = match.properties.icon;
            const locked = !this.checkRoles(match.properties.roles || null, roles, state);
            return this.renderItem(path, active, title, locked, icon);
          })}
        </Breadcrumbs>
        {toolbarFactory && toolbarFactory()}
      </>
    );
  }
}

//
// Container component
//

const mapState = (state: RootState) => ({
  profile: state.security.profile,
  state
});

const mapDispatch = {
};

const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>

// Apply styles
const styledComponent = withStyles(styles)(Breadcrumb);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject routing 
const routedComponent = withRouter(localizedComponent);

// Inject state
export default connector(routedComponent);
