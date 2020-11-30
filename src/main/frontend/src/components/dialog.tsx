import React from 'react';

// Material UI
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import { iconFunc } from 'model/types';

export const EnumDialogAction = {
  Yes: 'Yes',
  No: 'No',
  Accept: 'Accept',
  Reject: 'Reject',
  Cancel: 'Cancel',
};

interface DialogState {
  modal: boolean;
}

export interface DialogAction {
  key: string;
  label: string;
  iconClass?: iconFunc;
  color?: 'inherit' | 'primary' | 'secondary' | 'default' | undefined;
  disabled?: boolean;
}

interface DialogProps {
  actions?: DialogAction[];
  className?: string;
  handleAction?: (key: DialogAction) => void;
  handleClose: () => void;
  header: React.ReactNode | string;
  open: boolean;
}

class GenericDialog extends React.Component<DialogProps, DialogState>  {

  state: DialogState = {
    modal: false,
  }

  static defaultProps = {
    actions: [],
  }

  render() {
    const { actions, children, handleAction, handleClose, header, open } = this.props;

    return (
      <Dialog
        open={open}
        onClose={() => handleClose()}
      >
        <DialogTitle id="alert-dialog-title">{header}</DialogTitle>
        <DialogContent>
          {children}
        </DialogContent>
        {actions && actions.length !== 0 &&
          <DialogActions>
            {actions.map(a => (
              <Button
                key={a.key}
                onClick={() => handleAction ? handleAction(a) : null}
                color={a.color}
                disabled={a.disabled || false}
              >
                {a.iconClass && a.iconClass()}
                {a.label}
              </Button>
            ))}
          </DialogActions>
        }
      </Dialog>
    );
  }

}

export default GenericDialog;
