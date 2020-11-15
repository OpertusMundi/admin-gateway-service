import React from 'react';

// Localization
import { FormattedMessage, injectIntl, IntlShape } from 'react-intl';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';
import TextField from '@material-ui/core/TextField';

// Icons
import Icon from '@mdi/react';
import { mdiCommentAlertOutline } from '@mdi/js';

// Model
import { PageRequest, PageResult, Sorting } from 'model/response';
import { Account, AccountQuery } from 'model/account';

// Services
import message from 'service/message';

const styles = (theme: Theme) => createStyles({
  root: {
    padding: 5,
  },
  container: {
    display: 'flex',
    flexWrap: 'wrap',
  },
  button: {
    margin: theme.spacing(3, 0, 2, 2),
    borderRadius: 0,
    textTransform: 'none',
  },
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    width: 200,
  },
});

interface AccountFiltersProps extends WithStyles<typeof styles> {
  intl: IntlShape,
  query: AccountQuery,
  setFilter: (query: Partial<AccountQuery>) => void,
  resetFilter: () => void,
  find: (
    pageRequest?: PageRequest, sorting?: Sorting[]
  ) => Promise<PageResult<Account> | null>,
  create: () => void,
  disabled: boolean,
}

class AccountFilters extends React.Component<AccountFiltersProps> {

  constructor(props: AccountFiltersProps) {
    super(props);

    this.clear = this.clear.bind(this);
    this.search = this.search.bind(this);
    this.create = this.create.bind(this);
  }

  find(): void {
    this.props.find({ page: 0, size: 10 }).then((result) => {
      if (!result) {
        message.errorHtml("Find operation has failed", () => (<Icon path={mdiCommentAlertOutline} size="3rem" />));
      }
    });
  }

  clear(): void {
    this.props.resetFilter();
    this.find();
  }

  search(e: React.FormEvent): void {
    e.preventDefault();

    this.find();
  }

  create(): void {
    this.props.create();
  }

  render() {
    const { classes, disabled, query, setFilter } = this.props;
    const _t = this.props.intl.formatMessage;

    return (
      <form onSubmit={this.search} noValidate autoComplete="off">
        <Grid container spacing={3} justify={'space-between'}>
          <Grid item sm={4} xs={12}>
            <TextField
              id="name"
              label={_t({ id: 'account.manager.filter.name' })}
              variant="standard"
              margin="normal"
              className={classes.textField}
              value={query.name || ''}
              onChange={e => setFilter({ name: e.target.value })}
            />
          </Grid>

          <Grid container item sm={8} xs={12} justify={'flex-end'}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              className={classes.button}
              disabled={disabled}
            >
              <FormattedMessage id="view.shared.action.search" />
            </Button>
            <Button
              type="submit"
              variant="contained"
              className={classes.button}
              onClick={this.create}
              disabled={disabled}
            >
              <FormattedMessage id="view.shared.action.create" />
            </Button>
          </Grid>
        </Grid>
      </form>
    );
  }
}

// Apply styles
const styledComponent = withStyles(styles)(AccountFilters);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

export default localizedComponent;

