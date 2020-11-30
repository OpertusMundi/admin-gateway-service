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
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';

// Icons
import Icon from '@mdi/react';
import { mdiCancel, mdiCheckOutline, mdiCommentAlertOutline, mdiTrashCan, mdiUndoVariant, mdiWindowClose } from '@mdi/js';

// Services
import message from 'service/message';
import DraftApi from 'service/draft';

// Store
import { RootState } from 'store';
import { addToSelection, removeFromSelection, resetFilter, resetSelection, setFilter, setPager, setSorting } from 'store/draft/actions';
import { find } from 'store/draft/thunks';

// Utilities
import { localizeErrorCodes } from 'utils/error';

// Model
import { DynamicRoutes, buildPath } from 'model/routes';
import { PageRequest, Sorting, SimpleResponse } from 'model/response';
import { AssetDraft } from 'model/draft';

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
    paddingLeft: 0,
    fontSize: '0.7rem',
  }
});

interface AccountManagerState {
  confirm: boolean;
  draft: AssetDraft | null,
  reason: string,
  reasonRequired: boolean,
}

interface AccountManagerProps extends PropsFromRedux, WithStyles<typeof styles>, RouteComponentProps {
  intl: IntlShape,
}

class AssetDraftManager extends React.Component<AccountManagerProps, AccountManagerState> {

  private api: DraftApi;

  constructor(props: AccountManagerProps) {
    super(props);

    this.api = new DraftApi();

    this.reviewDraft = this.reviewDraft.bind(this);
    this.viewDraft = this.viewDraft.bind(this);
  }

  state: AccountManagerState = {
    confirm: false,
    draft: null,
    reason: '',
    reasonRequired: false,
  }

  handleReasonChange(reason: string) {
    this.setState({ reason, reasonRequired: false });
  }

  showConfirmDialog(draft: AssetDraft): void {
    this.setState({
      confirm: true,
      draft,
    });
  }

  hideConfirmDialog(): void {
    this.setState({
      confirm: false,
      draft: null,
    });
  }

  confirmDialogHandler(action: DialogAction): void {
    const { draft, reason, reasonRequired } = this.state;

    switch (action.key) {
      case EnumDialogAction.Accept: {
        if (draft) {
          this.api.accept(draft.publisher.id, draft.key)
            .then((response) => {
              if (response.data.success) {
                this.find();

                message.info('draft.message.accept-success');

                this.hideConfirmDialog();
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
      case EnumDialogAction.Reject: {
        if (!reason) {
          this.setState({ reasonRequired: true });
        }
        if (draft) {
          this.api.reject(draft.publisher.id, draft.key, reason)
            .then((response) => {
              if (response.data.success) {
                this.find();

                message.info('draft.message.accept-success');

                this.hideConfirmDialog();
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
      case EnumDialogAction.Cancel:
        this.hideConfirmDialog();
        break;
    }
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

  reviewDraft(key: string): void {
    const { draft: { result } } = this.props;

    const record = result?.items.find(d => d.key === key);

    if (record) {
      this.showConfirmDialog(record);
    }
  }

  viewDraft(providerKey: string, assetKey: string): void {
    window.open(`https://api.dev.opertusmundi.eu/provider/${providerKey}/asset/${assetKey}`, "_blank");
  }

  setSorting(sorting: Sorting[]): void {
    this.props.setSorting(sorting);
    this.find();
  }

  render() {
    const {
      addToSelection,
      classes,
      draft: { query, result, pagination, loading, lastUpdated, selected, sorting },
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
              disabled={loading}
            />
            {lastUpdated &&
              <Grid container spacing={3}>
                <Grid item xs={12}>
                  <Typography variant="caption" display="block" gutterBottom className={classes.caption}>
                    <FormattedMessage id="draft.manager.last-update" />
                    <FormattedTime value={lastUpdated.toDate()} day='numeric' month='numeric' year='numeric' />
                  </Typography>
                </Grid>
              </Grid>
            }
          </Paper>

          <Paper className={classes.paper}>
            <AccountTable
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
              reviewRow={(key: string) => this.reviewDraft(key)}
              viewRow={(providerKey: string, assetKey: string) => this.viewDraft(providerKey, assetKey)}
              sorting={sorting}
              loading={loading}
            />
          </Paper>
        </div >
        {this.renderReviewDialog()}
      </>
    );
  }

  renderReviewDialog() {
    const _t = this.props.intl.formatMessage;

    const { confirm, draft: record, reason, reasonRequired } = this.state;

    if (!confirm || !record) {
      return null;
    }

    return (
      <Dialog
        actions={[
          {
            key: EnumDialogAction.Accept,
            label: _t({ id: 'view.shared.action.accept' }),
            iconClass: () => (<Icon path={mdiCheckOutline} size="1.5rem" />),
            color: 'primary',
          }, {
            key: EnumDialogAction.Reject,
            label: _t({ id: 'view.shared.action.reject' }),
            iconClass: () => (<Icon path={mdiWindowClose} size="1.5rem" />),
            color: 'secondary',
            disabled: !reason,
          }, {
            key: EnumDialogAction.Cancel,
            label: _t({ id: 'view.shared.action.cancel' }),
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
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <FormattedMessage id="draft.message.review-draft" values={{ title: record.title, version: record.version }} />
          </Grid>
          <Grid item xs={12}>
            <TextField
              id="outlined-multiline-static"
              label="Rejection reason for Provider"
              multiline
              rows={4}
              value={reason}
              fullWidth
              onChange={
                (event: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>): void => this.handleReasonChange(event.target.value)
              }
              error={reasonRequired}
            />
          </Grid>
        </Grid>
      </Dialog>
    );
  }

}

const mapState = (state: RootState) => ({
  config: state.config,
  draft: state.draft,
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
const styledComponent = withStyles(styles)(AssetDraftManager);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject state
export default connector(localizedComponent);
