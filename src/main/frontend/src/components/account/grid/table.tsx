import React from 'react';

import { Link } from 'react-router-dom';

import { injectIntl, IntlShape } from 'react-intl';

import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Tooltip from '@material-ui/core/Tooltip';

import Icon from '@mdi/react';
import { mdiPencilOutline, mdiTrashCanOutline } from '@mdi/js';

import MaterialTable, { cellActionHandler, Column } from 'components/material-table';

import { buildPath, DynamicRoutes } from 'model/routes';
import { Account, AccountQuery } from 'model/account';
import { PageRequest, PageResult, Sorting } from 'model/response';

enum EnumAction {
  Delete = 'delete',
  Edit = 'edit',
};

function accountColumns(intl: IntlShape, classes: WithStyles<typeof styles>): Column<Account>[] {
  return (
    [{
      header: intl.formatMessage({ id: 'account.manager.header.actions' }),
      id: 'actions',
      width: 80,
      cell: (
        rowIndex: number, column: Column<Account>, row: Account, handleAction?: cellActionHandler<Account>
      ): React.ReactNode => (
          <div style={{ display: 'flex', justifyContent: 'center' }}>
            <Tooltip title={intl.formatMessage({ id: 'account.manager.tooltip.edit' })}>
              <i
                onClick={() => handleAction ? handleAction(EnumAction.Edit, rowIndex, column, row) : null}
              >
                <Icon path={mdiPencilOutline} className={classes.classes.rowIcon} />
              </i>
            </Tooltip>
            <Tooltip title={intl.formatMessage({ id: 'account.manager.tooltip.delete' })}>
              <i
                onClick={() => handleAction ? handleAction(EnumAction.Delete, rowIndex, column, row) : null}
              >
                <Icon path={mdiTrashCanOutline} className={classes.classes.rowIcon} />
              </i>
            </Tooltip>
          </div>
        ),
    }, {
      header: intl.formatMessage({ id: 'account.manager.header.username' }),
      id: 'username',
      width: 250,
      sortable: true,
      cell: (
        rowIndex: number, column: Column<Account>, row: Account, handleAction?: cellActionHandler<Account>
      ): React.ReactNode => (
          <Link to={buildPath(DynamicRoutes.AccountUpdate, [row.id + ''])} className={classes.classes.link}>
            {row.username}
          </Link>
        ),
    }, {
      header: intl.formatMessage({ id: 'account.manager.header.firstName' }),
      id: 'firstName',
      accessor: 'firstName',
      sortable: true,
    }, {
      header: intl.formatMessage({ id: 'account.manager.header.lastName' }),
      id: 'lastName',
      accessor: 'lastName',
      sortable: true,

    }]);
}

const styles = (theme: Theme) => createStyles({
  root: {
    display: 'flex',
    padding: 0,
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
  deleteRow: (id: number) => void,
  find: (
    pageRequest?: PageRequest, sorting?: Sorting[]
  ) => Promise<PageResult<Account>>,
  query: AccountQuery,
  result: PageResult<Account> | null,
  pagination: PageRequest,
  selected: Account[],
  setPager: (page: number, size: number) => void,
  setSorting: (sorting: Sorting[]) => void,
  addToSelection: (rows: Account[]) => void,
  removeFromSelection: (rows: Account[]) => void,
  resetSelection: () => void;
  sorting: Sorting[];
  updateRow: (id: number) => void;
  loading?: boolean;
}

class AccountTable extends React.Component<FieldTableProps> {

  constructor(props: FieldTableProps) {
    super(props);

    this.handleAction = this.handleAction.bind(this);
  }

  handleAction(action: string, index: number, column: Column<Account>, row: Account): void {
    if (row.id) {
      switch (action) {
        case EnumAction.Delete:
          this.props.deleteRow(row.id);
          break;
        case EnumAction.Edit:
          this.props.updateRow(row.id);
          break;
        default:
          // No action
          break;
      }
    }
  }

  render() {
    const { intl, classes, result, setPager, pagination, find, selected, sorting, setSorting, loading } = this.props;
    console.log(result);
    return (
      <MaterialTable<Account>
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
const styledComponent = withStyles(styles)(AccountTable);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

export default localizedComponent;