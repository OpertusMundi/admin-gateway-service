import React from 'react';
import { AxiosError } from 'axios';

// State, routing and localization
import { connect, ConnectedProps } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { FormattedMessage, FormattedTime, injectIntl, IntlShape } from 'react-intl';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';

// Icons
import Icon from '@mdi/react';
import { mdiCommentAlertOutline, mdiTrashCan, mdiUndoVariant } from '@mdi/js';

// Services
import message from 'service/message';
import AccountApi from 'service/account';

// Store
import { RootState } from 'store';
import { addToSelection, removeFromSelection, resetFilter, resetSelection, setFilter, setPager, setSorting } from 'store/account/actions';
import { find } from 'store/account/thunks';

// Utilities
import { localizeErrorCodes } from 'utils/error';

// Model
import { DynamicRoutes, buildPath } from 'model/routes';
import { PageRequest, Sorting, SimpleResponse } from 'model/response';
import { Account } from 'model/account';

// Components
import Dialog, { DialogAction, EnumDialogAction } from 'components/dialog';

import AccountFilters from './grid/filter';
import AccountTable from './grid/table';

const styles = (theme: Theme) => createStyles({
  container: {
    display: 'flex',
    flexWrap: 'wrap',
  },
  paper: {
    padding: theme.spacing(1),
    margin: theme.spacing(1),
    color: theme.palette.text.secondary,
    borderRadius: 0,
  },
  caption: {
    paddingLeft: 8,
    fontSize: '0.7rem',
  }
});

interface AccountManagerState {
  confirm: boolean;
  record: Account | null
}

interface AccountManagerProps extends PropsFromRedux, WithStyles<typeof styles>, RouteComponentProps {
  intl: IntlShape,
}

class AccountManager extends React.Component<AccountManagerProps, AccountManagerState> {

  private api: AccountApi;

  constructor(props: AccountManagerProps) {
    super(props);

    this.api = new AccountApi();

    this.deleteRow = this.deleteRow.bind(this);
  }

  state: AccountManagerState = {
    confirm: false,
    record: null,
  }

  showConfirmDialog(record: Account): void {
    this.setState({
      confirm: true,
      record,
    });
  }

  hideConfirmDialog(): void {
    this.setState({
      confirm: false,
      record: null,
    });
  }

  confirmDialogHandler(action: DialogAction): void {
    switch (action.key) {
      case EnumDialogAction.Yes: {
        const { record } = this.state;

        if (record && record.id) {
          this.api.remove(record.id)
            .then((response) => {
              if (response.data.success) {
                this.find();

                message.info('error.delete-success');
              } else {
                const messages = localizeErrorCodes(this.props.intl, response.data);
                message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />));
              }
            })
            .catch((err: AxiosError<SimpleResponse>) => {
              const messages = localizeErrorCodes(this.props.intl, err.response?.data);
              message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />));
            });
        }
        break;
      }
    }

    this.hideConfirmDialog();
  }

  componentDidMount() {
    this.find();
  }

  find(): void {
    this.props.find().then((result) => {
      if (!result) {
        message.errorHtml("Find operation has failed", () => (<Icon path={mdiCommentAlertOutline} size="3rem" />));
      }
    });
  }

  createRow(): void {
    this.props.history.push(DynamicRoutes.AccountCreate);
  }

  updateRow(id: number): void {
    const path = buildPath(DynamicRoutes.AccountUpdate, [id.toString()]);

    this.props.history.push(path);
  }

  deleteRow(id: number): void {
    const { explorer: { result } } = this.props;

    const record = result?.items.find(o => o.id === id);

    if (record) {
      this.showConfirmDialog(record);
    }
  }

  setSorting(sorting: Sorting[]): void {
    this.props.setSorting(sorting);
    this.find();
  }

  render() {
    const {
      addToSelection,
      classes,
      explorer: { query, result, pagination, loading, lastUpdated, selected, sorting },
      find,
      setPager,
      setFilter,
      removeFromSelection,
      resetFilter,
    } = this.props;

    return (
      <>
        <div>
          <Paper className={classes.paper}>
            <AccountFilters
              query={query}
              setFilter={setFilter}
              resetFilter={resetFilter}
              find={find}
              create={() => this.createRow()}
              disabled={loading}
            />
            {lastUpdated &&
              <Grid container spacing={3}>
                <Grid item xs={12}>
                  <Typography variant="caption" display="block" gutterBottom className={classes.caption}>
                    <FormattedMessage id="account.manager.last-update" />
                    <FormattedTime value={lastUpdated.toDate()} day='numeric' month='numeric' year='numeric' />
                  </Typography>
                </Grid>
              </Grid>
            }
          </Paper>

          <Paper className={classes.paper}>
            <AccountTable
              deleteRow={this.deleteRow}
              find={this.props.find}
              query={query}
              result={result}
              pagination={pagination}
              selected={selected}
              setPager={setPager}
              setSorting={(sorting: Sorting[]) => this.setSorting(sorting)}
              addToSelection={addToSelection}
              removeFromSelection={removeFromSelection}
              resetSelection={resetSelection}
              sorting={sorting}
              updateRow={(id: number) => this.updateRow(id)}
              loading={loading}
            />
          </Paper>
        </div >
        {this.renderConfirm()}
      </>
    );
  }

  renderConfirm() {
    const _t = this.props.intl.formatMessage;

    const { confirm, record } = this.state;

    if (!confirm || !record) {
      return null;
    }

    return (
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
        handleClose={() => this.hideConfirmDialog()}
        handleAction={(action) => this.confirmDialogHandler(action)}
        header={
          <span>
            <i className={'mdi mdi-comment-question-outline mr-2'}></i>
            <FormattedMessage id="view.shared.dialog.title" />
          </span>
        }
        open={confirm}
      >
        <FormattedMessage id="view.shared.message.delete-confirm" values={{ name: record.email }} />
      </Dialog>
    );
  }
}

const mapState = (state: RootState) => ({
  config: state.config,
  explorer: state.account.explorer,
});

const mapDispatch = {
  addToSelection,
  find: (pageRequest?: PageRequest, sorting?: Sorting[]) => find(pageRequest, sorting),
  removeFromSelection,
  resetFilter,
  resetSelection,
  setFilter,
  setPager,
  setSorting,
};

const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>

// Apply styles
const styledComponent = withStyles(styles)(AccountManager);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject state
export default connector(localizedComponent);
