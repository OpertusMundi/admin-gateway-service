import { EnumRole } from 'model/role';
import { ErrorPages, Pages } from 'model/routes';
import * as React from 'react';
import { connect, ConnectedProps } from 'react-redux';
import { Redirect, Route, RouteProps } from 'react-router-dom';
import { RootState } from 'store';

interface SecureRouteProps extends PropsFromRedux, RouteProps {
  roles: EnumRole[];
}

class SecureRoute extends React.Component<SecureRouteProps> {

  static defaultProps = {
    roles: [],
  }

  hasAnyRole(roles: EnumRole[] = []) {
    if ((!roles) || (roles.length === 0)) {
      return false;
    }

    const profile = this.props.profile;
    if (!profile) {
      return false;
    }

    for (let role of roles) {
      if (profile.roles.indexOf(role) !== -1) {
        return true;
      }
    }
    return false;
  }

  render() {
    let { roles, profile, ...rest } = this.props;
    let authenticated = (profile != null);

    if (!authenticated) {
      return (
        <Redirect to={Pages.Login} />
      );
    }
    if (this.hasAnyRole(roles)) {
      return (
        <Route {...rest} />
      );
    }
    return (
      <Redirect to={ErrorPages.Forbidden} />
    );
  }
}

//
// Wrap into a connected component
//

const mapState = (state: RootState) => ({
  profile: state.security.profile,
});

const connector = connect(
  mapState,
);

type PropsFromRedux = ConnectedProps<typeof connector>

export default connector(SecureRoute);
