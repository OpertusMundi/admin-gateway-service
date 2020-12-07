import _ from 'lodash';
import React from 'react';

// i18n
import { FormattedMessage, IntlShape } from 'react-intl';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import TableSortLabel from '@material-ui/core/TableSortLabel';

// Icons
import Icon from '@mdi/react';
import { mdiLoading } from '@mdi/js';

// Model
import { Order } from 'model/response';
import { Sorting } from 'model/response';

const styles = (theme: Theme) => createStyles({
  root: {
    width: '100%',
    overflowX: 'auto',
  },
  table: {
    minWidth: 650,
  },
  cell: {
    padding: '14px 16px 14px 16px',
  },
  sortable: {
    cursor: 'pointer',
  },
  selected: {
    background: '#3F51B5',
    color: '#ffffff',
    '& td': {
      color: '#ffffff',
    }
  },
  visuallyHidden: {
    border: 0,
    clip: 'rect(0 0 0 0)',
    height: 1,
    margin: -1,
    overflow: 'hidden',
    padding: 0,
    position: 'absolute',
    top: 20,
    width: 1,
  },
});

export type cellAccessor<R> = (row: R) => any;

export type cellActionHandler<R, S> = (key: string, index: number, column: Column<R, S>, row: R) => void;

export type cellRenderer<R, S> = (index: number, column: Column<R, S>, row: R, handleAction?: cellActionHandler<R, S>) => React.ReactNode;

export interface Column<R, S> {
  accessor?: string,
  className?: string,
  cell?: cellRenderer<R, S>;
  header: string;
  headerClassName?: string;
  headerStyle?: React.CSSProperties;
  hidden?: boolean;
  id: string,
  selected?: boolean;
  sortable?: boolean;
  sortColumn?: S;
  style?: React.CSSProperties;
  width?: number | string;
}

export interface PaginationOptions {
  rowsPerPageOptions: number[],
  page: number,
  size: number,
  count: number,
}

interface MaterialTableProps<R, S> extends WithStyles<typeof styles> {
  intl: IntlShape;
  columns: Column<R, S>[];
  handleAction?: cellActionHandler<R, S>;
  handleChangePage?: (page: number) => void;
  handleChangeRowsPerPage?: (value: number) => void;
  handleRowClick?: (index: number, row: R) => void;
  handleRowDoubleClick?: (index: number, row: R) => void;
  pagination?: PaginationOptions,
  rows: R[];
  selected: R[];
  sorting?: Sorting<S>[];
  setSorting: (sorting: Sorting<S>[]) => void,
  loading?: boolean;
};

class MaterialTable<R, S> extends React.Component<MaterialTableProps<R, S>> {

  resolveValue(index: number, column: Column<R, S>, row: R, handleAction?: cellActionHandler<R, S>) {
    // Custom rendering function that accepts as arguments 
    if (typeof column.cell === 'function') {
      return column.cell.bind(this)(index, column, row, handleAction);
    }
    // Composite object accessor
    if (column.accessor) {
      return _.get(row, column.accessor);
    }

    // By default use the column id as index
    return row[column.id as keyof R];
  }

  handleRowClick(e: React.MouseEvent, rowIndex: number, row: R) {
    if (typeof this.props.handleRowClick === 'function') {
      this.props.handleRowClick(rowIndex, row);
    }
  }

  handleRowDoubleClick(e: React.MouseEvent, rowIndex: number, row: R) {
    if (typeof this.props.handleRowDoubleClick === 'function') {
      this.props.handleRowDoubleClick(rowIndex, row);
    }
  }

  isRowSelected(row: R) {
    const { selected } = this.props;

    // Compare by reference
    return selected.find(s => s === row);
  }

  render() {
    const { classes, columns, rows, handleAction, handleChangePage, handleChangeRowsPerPage, pagination, sorting, setSorting, loading = false } = this.props;
    const _t = this.props.intl.formatMessage;

    const columnCount = columns.filter(c => !c.hidden).length;

    return (
      <div>
        <Table className={classes.table}>
          <TableHead >
            <TableRow >
              {columns
                .filter(c => !c.hidden)
                .map(c => {
                  const { header, headerClassName, headerStyle, id, sortable, width, sortColumn } = c;
                  const style = headerStyle ? { ...headerStyle } : {};

                  if (width) {
                    style.width = width;
                  }
                  if (sorting && sortable) {
                    const s = sorting.find(s => s.id === sortColumn) || null;

                    if (s) {
                      return (
                        <TableCell
                          key={id}
                          className={headerClassName ? headerClassName + ' ' + classes.cell : classes.cell}
                          style={style}
                          sortDirection={s.order === Order.ASC ? 'asc' : 'desc'}
                        >
                          <TableSortLabel
                            active={true}
                            direction={s.order === Order.ASC ? 'asc' : 'desc'}
                            onClick={() => setSorting([{ id: sortColumn, order: s.order === Order.ASC ? Order.DESC : Order.ASC }])}
                          >
                            {header}
                          </TableSortLabel>
                        </TableCell>
                      );
                    }
                  }

                  return (
                    <TableCell
                      key={id}
                      className={`${headerClassName ? headerClassName : ''} ${classes.cell}  ${sortable ? classes.sortable : ''}`}
                      style={style}
                      sortDirection={false}
                      onClick={() => sortable ? setSorting([{ id: sortColumn, order: Order.ASC }]) : null}
                    >
                      {header}
                    </TableCell>
                  );
                })
              }
            </TableRow>
          </TableHead>
          <TableBody >
            {!loading && rows.map((row: R, index: number) => {
              return (
                <TableRow
                  key={`r-${index}`}
                  onClick={(e) => this.handleRowClick(e, index, row)}
                  onDoubleClick={(e) => this.handleRowDoubleClick(e, index, row)}
                  className={this.isRowSelected(row) ? classes.selected : ''}
                >
                  {columns
                    .filter(c => !c.hidden)
                    .map(c => {
                      const style = c.style ? { ...c.style } : {};
                      if (c.width) {
                        style.width = c.width;
                      }
                      return (
                        <TableCell
                          key={`r-${index}-c-${c.id}`}
                          className={c.className ? c.className + ' ' + classes.cell : classes.cell}
                          style={style}
                        >
                          {this.resolveValue(index, c, row, handleAction)}
                        </TableCell>
                      );
                    })
                  }
                </TableRow>
              );
            })}
            {!loading && rows.length === 0 &&
              <TableRow >
                <TableCell colSpan={columnCount} style={{ textAlign: 'center' }}>
                  <FormattedMessage id="table.no-data"></FormattedMessage>
                </TableCell>
              </TableRow>
            }
            {loading &&
              <TableRow >
                <TableCell colSpan={columnCount} style={{ textAlign: 'center' }}>
                  <Icon path={mdiLoading} spin size="1rem" style={{ marginRight: 10 }} />
                  <FormattedMessage id="table.loading-data"></FormattedMessage>
                </TableCell>
              </TableRow>
            }
          </TableBody>
        </Table>
        {
          pagination && pagination.count !== 0 &&
          <TablePagination
            rowsPerPageOptions={pagination.rowsPerPageOptions}
            component="div"
            count={pagination.count}
            rowsPerPage={pagination.size}
            page={pagination.page}
            backIconButtonProps={{
              'title': _t({ id: 'table.previous-page' }),
            }}
            nextIconButtonProps={{
              'title': _t({ id: 'table.next-page' }),
            }}
            labelRowsPerPage={
              _t({ id: 'table.rows-per-page' })
            }
            labelDisplayedRows={({ from, to, count }) => (
              <FormattedMessage id="table.rows-displayed" values={{ from, to: (to === -1 ? count : to), count }} />
            )}
            onChangePage={(event, newPage) => handleChangePage ? handleChangePage(newPage) : null}
            onChangeRowsPerPage={(e) => handleChangeRowsPerPage ? handleChangeRowsPerPage(+e.target.value) : null}
          />
        }
      </div >
    );
  }
}

// Fix generic parameter
// See: https://stackoverflow.com/questions/52567697/generic-type-arguments-in-jsx-elements-with-withstyles

// Apply styles
type GenericWithStyles = <R, S>(props: Omit<MaterialTableProps<R, S>, 'classes'>) => JSX.Element;

const styledComponent = withStyles(styles)(MaterialTable) as GenericWithStyles;

// Export styled component
export default styledComponent;
