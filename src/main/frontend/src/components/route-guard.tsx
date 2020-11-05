// See: https://medium.com/@michaelchan_13570/using-react-router-v4-prompt-with-custom-modal-component-ca839f5faf39

import React from 'react';

import { Location } from 'history';
import { Prompt } from 'react-router-dom';

import { FormattedMessage, injectIntl, IntlShape } from 'react-intl';

import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Icon from '@mdi/react';
import { mdiTrashCan, mdiUndoVariant } from '@mdi/js';

import Dialog, { DialogAction, EnumDialogAction } from 'components/dialog';

const styles = (theme: Theme) => createStyles({
});

interface RouteGuardState {
  modalVisible: boolean;
  lastLocation: Location | null;
  confirmedNavigation: boolean;
}

interface RouteGuardProps extends WithStyles<typeof styles> {
  intl: IntlShape,
  shouldBlockNavigation?: (location: Location) => boolean;
  navigate: (location: string) => void;
  when: boolean;
}

export class RouteGuard extends React.Component<RouteGuardProps, RouteGuardState> {

  state: RouteGuardState = {
    modalVisible: false,
    lastLocation: null,
    confirmedNavigation: false,
  }

  showModal(location: Location): void {
    this.setState({
      modalVisible: true,
      lastLocation: location,
    });
  }

  closeModal(callback?: () => void): void {
    this.setState({
      modalVisible: false
    }, callback);
  }

  handleBlockedNavigation(nextLocation: Location): string | boolean {
    const { confirmedNavigation } = this.state;
    const { shouldBlockNavigation } = this.props;

    if (!confirmedNavigation && (!shouldBlockNavigation || shouldBlockNavigation(nextLocation))) {
      this.showModal(nextLocation);
      return false;
    }
    return true;
  }

  handleConfirmNavigationClick() {
    this.closeModal(() => {
      const { navigate } = this.props;
      const { lastLocation } = this.state;

      if (lastLocation) {
        this.setState({
          confirmedNavigation: true
        }, () => {
          // Navigate to the previous blocked location with your navigate function     
          navigate(lastLocation.pathname);
        });
      }
    });
  }

  handleDialogAction(action: DialogAction): void {
    switch (action.key) {
      case EnumDialogAction.Yes:
        this.handleConfirmNavigationClick();
        break;
      default:
        this.closeModal();
        break;
    }
  }

  render() {
    const _t = this.props.intl.formatMessage;

    const { when } = this.props;
    const { modalVisible } = this.state;

    return (
      <>
        <Prompt
          when={when}
          message={(nextLocation: Location) => this.handleBlockedNavigation(nextLocation)}
        />
        <Dialog
          actions={[
            {
              key: EnumDialogAction.Yes,
              label: _t({ id: 'view.shared.action.yes' }),
              iconClass: () => (<Icon path={mdiTrashCan} size="1.5rem" />),
              color: 'primary',
            }, {
              key: EnumDialogAction.No,
              label: _t({ id: 'view.shared.action.no' }),
              iconClass: () => (<Icon path={mdiUndoVariant} size="1.5rem" />)
            }
          ]}
          handleClose={() => this.closeModal()}
          handleAction={(action) => this.handleDialogAction(action)}
          header={
            <span>
              <i className={'mdi mdi-comment-question-outline mr-2'}></i>
              <FormattedMessage id="view.shared.dialog.title" />
            </span>
          }
          open={modalVisible}
        >
          <FormattedMessage id="view.shared.message.cancel-confirm" />
        </Dialog>
      </>
    )
  }
}


// Apply styles
const styledComponent = withStyles(styles)(RouteGuard);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

export default localizedComponent;
