import React from 'react';

// Localization
import { FormattedMessage, injectIntl, IntlShape } from 'react-intl';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';
import TextField from '@material-ui/core/TextField';

import Autocomplete from '@material-ui/lab/Autocomplete';

// Icons
import Icon from '@mdi/react';
import { mdiCommentAlertOutline } from '@mdi/js';

// Model
import { PageRequest, PageResult, Sorting } from 'model/response';
import { EnumSortField, EnumDraftStatus, AssetDraft, AssetDraftQuery } from 'model/draft';

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

interface AssetDraftFiltersProps extends WithStyles<typeof styles> {
  intl: IntlShape,
  query: AssetDraftQuery,
  setFilter: (query: Partial<AssetDraftQuery>) => void,
  resetFilter: () => void,
  find: (
    pageRequest?: PageRequest, sorting?: Sorting<EnumSortField>[]
  ) => Promise<PageResult<AssetDraft> | null>,
  disabled: boolean,
}

class AssetDraftFilters extends React.Component<AssetDraftFiltersProps> {

  constructor(props: AssetDraftFiltersProps) {
    super(props);

    this.clear = this.clear.bind(this);
    this.search = this.search.bind(this);

    this.statusOptions = [{
      value: EnumDraftStatus.DRAFT, label: 'Draft'
    }, {
      value: EnumDraftStatus.SUBMITTED, label: 'Submitted'
    }, {
      value: EnumDraftStatus.PENDING_HELPDESK_REVIEW, label: 'Pending HelpDesk Review'
    }, {
      value: EnumDraftStatus.HELPDESK_REJECTED, label: 'HelpDesk Rejected'
    }, {
      value: EnumDraftStatus.PENDING_PROVIDER_REVIEW, label: 'Pending Provider Review'
    }, {
      value: EnumDraftStatus.PROVIDER_REJECTED, label: 'Provider Rejected'
    }, {
      value: EnumDraftStatus.POST_PROCESSING, label: 'Post Processing'
    }, {
      value: EnumDraftStatus.PUBLISHED, label: 'Published'
    }];
  }

  statusOptions: { value: EnumDraftStatus, label: string }[];

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

  render() {
    const { classes, disabled, query, setFilter } = this.props;
    const _t = this.props.intl.formatMessage;

    return (
      <form onSubmit={this.search} noValidate autoComplete="off">
        <Grid container spacing={3} justify={'space-between'}>
          <Grid item sm={8} xs={12}>
            <Autocomplete
              multiple
              options={this.statusOptions}
              getOptionLabel={(option) => option.label}
              value={this.statusOptions.filter(o => query.status.includes(o.value)) || null}
              onChange={(event, value) => {
                setFilter({ status: value.map(v => v.value) })
              }}
              renderInput={(params) => (
                <TextField
                  {...params}
                  variant="standard"
                  label={_t({ id: 'draft.manager.filter.asset-status' })}
                />
              )}
            />
          </Grid>

          <Grid container item sm={4} xs={12} justify={'flex-end'}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              className={classes.button}
              disabled={disabled}
            >
              <FormattedMessage id="view.shared.action.search" />
            </Button>
          </Grid>
        </Grid>
      </form>
    );
  }
}

// Apply styles
const styledComponent = withStyles(styles)(AssetDraftFilters);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

export default localizedComponent;

