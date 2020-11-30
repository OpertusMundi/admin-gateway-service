import React from 'react';

import { Link } from 'react-router-dom';

import { injectIntl, IntlShape, FormattedTime } from 'react-intl';

import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Avatar from '@material-ui/core/Avatar';
import Chip from '@material-ui/core/Chip';
import Tooltip from '@material-ui/core/Tooltip';

import Icon from '@mdi/react';
import {
  mdiCommentTextOutline,
  mdiCogTransferOutline,
  mdiBookOpenVariant,
  mdiPencilOutline,
  mdiTrashCanOutline,
  mdiNewBox,
  mdiTrayFull,
  mdiCloseOctagon,
  mdiLeadPencil,
  mdiCardSearchOutline,
  mdiMagnify,
} from '@mdi/js';

import MaterialTable, { cellActionHandler, Column } from 'components/material-table';

import { buildPath, DynamicRoutes } from 'model/routes';
import { AssetDraft, AssetDraftQuery, EnumDraftStatus } from 'model/draft';
import { PageRequest, PageResult, Sorting } from 'model/response';

enum EnumAction {
  Review = 'review',
  View = 'view',
};

function statusToChip(value: EnumDraftStatus, classes: WithStyles<typeof styles>, intl: IntlShape): React.ReactElement | undefined {
  let path: string;
  let color: 'default' | 'primary' | 'secondary' = 'default';

  switch (value) {
    case EnumDraftStatus.DRAFT:
      path = mdiLeadPencil;
      break;
    case EnumDraftStatus.SUBMITTED:
      path = mdiTrayFull;
      break;
    case EnumDraftStatus.PENDING_HELPDESK_REVIEW:
      path = mdiCommentTextOutline;
      color = 'primary';
      break;
    case EnumDraftStatus.PENDING_PROVIDER_REVIEW:
      path = mdiCommentTextOutline;
      break;
    case EnumDraftStatus.HELPDESK_REJECTED:
    case EnumDraftStatus.PROVIDER_REJECTED:
      path = mdiCloseOctagon;
      color = 'secondary';
      break;
    case EnumDraftStatus.POST_PROCESSING:
      path = mdiCogTransferOutline;
      break;
    case EnumDraftStatus.PUBLISHED:
      path = mdiBookOpenVariant;
      break;
  }

  return (
    <Chip
      avatar={<Avatar><Icon path={path} className={classes.classes.avatarIcon} /></Avatar>}
      label={intl.formatMessage({ id: `draft.manager.chip.${value}` })}
      variant="outlined"
      color={color}
    />
  );
}

function accountColumns(intl: IntlShape, classes: WithStyles<typeof styles>): Column<AssetDraft>[] {
  return (
    [{
      header: intl.formatMessage({ id: 'account.manager.header.actions' }),
      id: 'actions',
      width: 80,
      cell: (
        rowIndex: number, column: Column<AssetDraft>, row: AssetDraft, handleAction?: cellActionHandler<AssetDraft>
      ): React.ReactNode => (
          <div style={{ display: 'flex', justifyContent: 'center' }}>
            <Tooltip title={intl.formatMessage({ id: 'draft.manager.tooltip.view' })}>
              <i
                onClick={() => handleAction ? handleAction(EnumAction.View, rowIndex, column, row) : null}
              >
                <Icon path={mdiMagnify} className={classes.classes.rowIcon} />
              </i>
            </Tooltip>
            {row.status == EnumDraftStatus.PENDING_HELPDESK_REVIEW &&
              <Tooltip title={intl.formatMessage({ id: 'draft.manager.tooltip.review' })}>
                <i
                  onClick={() => handleAction ? handleAction(EnumAction.Review, rowIndex, column, row) : null}
                >
                  <Icon path={mdiCommentTextOutline} className={classes.classes.rowIcon} />
                </i>
              </Tooltip>
            }
          </div>
        ),
    }, {
      header: intl.formatMessage({ id: 'draft.manager.header.status' }),
      id: 'status',
      width: 250,
      sortable: true,
      cell: (
        rowIndex: number, column: Column<AssetDraft>, row: AssetDraft, handleAction?: cellActionHandler<AssetDraft>
      ): React.ReactNode => statusToChip(row.status, classes, intl)
    }, {
      header: intl.formatMessage({ id: 'draft.manager.header.provider' }),
      id: 'account.profile.provider.name',
      accessor: 'publisher.name',
      sortable: true,
    }, {
      header: intl.formatMessage({ id: 'draft.manager.header.title' }),
      id: 'title',
      accessor: 'title',
      sortable: true,

    }, {
      header: intl.formatMessage({ id: 'draft.manager.header.version' }),
      id: 'version',
      accessor: 'version',
      sortable: true,
    }, {
      header: intl.formatMessage({ id: 'draft.manager.header.modifiedOn' }),
      id: 'modifiedOn',
      accessor: 'modifiedOn',
      sortable: true,
      cell: (
        rowIndex: number, column: Column<AssetDraft>, row: AssetDraft, handleAction?: cellActionHandler<AssetDraft>
      ): React.ReactNode => (
          <FormattedTime value={row.modifiedOn.toDate()} day='numeric' month='numeric' year='numeric' />
        ),
    }]);
}

const styles = (theme: Theme) => createStyles({
  root: {
    display: 'flex',
    padding: 0,
  },
  avatarIcon: {
    width: 16,
    color: '#ffffff',
  },
  rowIcon: {
    width: 18,
    marginRight: 8,
    cursor: 'pointer',
  },
  link: {
    color: 'inherit',
  }
});

interface FieldTableProps extends WithStyles<typeof styles> {
  intl: IntlShape,
  find: (
    pageRequest?: PageRequest, sorting?: Sorting[]
  ) => Promise<PageResult<AssetDraft> | null>,
  query: AssetDraftQuery,
  result: PageResult<AssetDraft> | null,
  pagination: PageRequest,
  selected: AssetDraft[],
  setPager: (page: number, size: number) => void,
  setSorting: (sorting: Sorting[]) => void,
  addToSelection: (rows: AssetDraft[]) => void,
  removeFromSelection: (rows: AssetDraft[]) => void,
  resetSelection: () => void;
  sorting: Sorting[];
  reviewRow: (key: string) => void;
  viewRow: (providerKey: string, assetKey: string) => void;
  loading?: boolean;
}

class AssetDraftTable extends React.Component<FieldTableProps> {

  constructor(props: FieldTableProps) {
    super(props);

    this.handleAction = this.handleAction.bind(this);
  }

  handleAction(action: string, index: number, column: Column<AssetDraft>, row: AssetDraft): void {
    if (row.key) {
      switch (action) {
        case EnumAction.Review:
          this.props.reviewRow(row.key);
          break;
        case EnumAction.View:
          this.props.viewRow(row.publisher.id, row.key);
          break;
        default:
          // No action
          break;
      }
    }
  }

  render() {
    const { intl, classes, result, setPager, pagination, find, selected, sorting, setSorting, loading } = this.props;

    return (
      <MaterialTable<AssetDraft>
        intl={intl}
        columns={accountColumns(intl, { classes })}
        rows={result ? result.items : []}
        pagination={{
          rowsPerPageOptions: [10, 20, 50],
          count: result ? result.count : 0,
          size: result ? result.pageRequest.size : 20,
          page: result ? result.pageRequest.page : 0,
        }}
        handleAction={this.handleAction}
        handleChangePage={(index: number) => {
          setPager(index, pagination.size);

          find();
        }}
        handleChangeRowsPerPage={(size: number) => {
          setPager(0, size);

          this.props.find();
        }}
        selected={selected}
        sorting={sorting}
        setSorting={setSorting}
        loading={loading}
      />
    );
  }
}

// Apply styles
const styledComponent = withStyles(styles)(AssetDraftTable);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

export default localizedComponent;