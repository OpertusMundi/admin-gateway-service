import React from 'react';

// State, routing and localization
import { connect, ConnectedProps } from 'react-redux';
import { injectIntl, IntlShape } from 'react-intl';

import { RootState } from 'store';
import { update } from 'store/account/thunks';

// Material UI
import IconButton from '@material-ui/core/IconButton';

// Icons
import Icon from '@mdi/react';
import { mdiCheck, mdiUndoVariant } from '@mdi/js';

// Model
import { Account, AccountCommand } from 'model/account';

export interface ToolbarProps extends PropsFromRedux {
  intl: IntlShape;
}

export class Toolbar extends React.Component<ToolbarProps> {

  render() {
    return (
      <>
        <IconButton color="inherit">
          <Icon path={mdiCheck} size="1.5rem" />
        </IconButton>
        <IconButton color="inherit">
          <Icon path={mdiUndoVariant} size="1.5rem" />
        </IconButton>
      </>
    );
  }

}

const mapState = (state: RootState) => ({
});

const mapDispatch = {
  update: (id: number | null, command: AccountCommand) => update(id, command),
};

const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>

// Inject i18n resources
const localizedComponent = injectIntl(Toolbar);

// Inject state
export default connector(localizedComponent);

