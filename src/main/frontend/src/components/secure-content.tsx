import { EnumRole } from 'model/role';
import * as React from 'react';
import { connect, ConnectedProps } from 'react-redux';
import { RootState } from 'store';

interface SecureContentProps extends PropsFromRedux {
  children: React.ReactNode;
  roles: EnumRole[];
}

class SecureContent extends React.Component<SecureContentProps> {

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
    let { roles } = this.props;

    if (!this.hasAnyRole(roles)) {
      return null;
    }

    return this.props.children;
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

export default connector(SecureContent);
