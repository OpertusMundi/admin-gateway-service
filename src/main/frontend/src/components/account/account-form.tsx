import React from 'react';
import { AxiosError } from 'axios';

// State, routing and localization
import { connect, ConnectedProps } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { FormattedMessage, injectIntl, IntlShape } from 'react-intl';

// 3rd party components
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';
import FormControl from '@material-ui/core/FormControl';
import Chip from '@material-ui/core/Chip';
import FormHelperText from '@material-ui/core/FormHelperText';
import MenuItem from '@material-ui/core/MenuItem';
import FormControlLabel from '@material-ui/core/FormControlLabel';

import { Field, Form, Formik, FormikProps } from 'formik';

import { TextField } from 'formik-material-ui';
import { Checkbox } from 'formik-material-ui';

import Icon from '@mdi/react';
import { mdiTrashCan, mdiUndoVariant, mdiCommentAlertOutline, mdiBadgeAccountOutline } from '@mdi/js';

// Utilities
import { localizeErrorCodes } from 'utils/error';
import { accountValidator as validator } from 'validation/account';

// Services
import message from 'service/message';
import AccountApi from 'service/account';

// Store
import { RootState } from 'store';

// Components
import SecureContent from 'components/secure-content';
import PasswordStrength from 'components/password-strength';
import Dialog, { DialogAction, EnumDialogAction } from 'components/dialog';
import AutoCompleteFormik from 'components/editors/autocomplete-formik';
import RouteGuard from 'components/route-guard';

// Model
import { FieldMapperFunc } from 'utils/error';
import { StaticRoutes } from 'model/routes';
import { EnumRole } from 'model/role';
import { AxiosObjectResponse, SimpleResponse } from 'model/response';
import { AccountFormData, AccountCommand } from 'model/account';
import { KeyValuePair } from 'model/key-value-pair';

export type DeepPartial<T, V> =
  T extends Array<infer R> ? V :
  T extends object ? { [P in keyof T]?: DeepPartial<T[P], V>; } : V;

const fieldMapper: FieldMapperFunc = (field: string): string | null => {
  switch (field) {
    case 'email':
      return 'account.form.field.email';
    case 'firstName':
      return 'account.form.field.firstName';
    case 'lastName':
      return 'account.form.field.lastName';
    case 'locale':
      return 'account.form.field.locale';
    case 'mobile':
      return 'account.form.field.mobile';
    case 'password':
      return 'account.form.field.password';
    case 'passwordMatch':
      return 'account.form.field.passwordMatch';
  }

  return null;
};

type SetFieldValue = (field: string, value: any, shouldValidate?: boolean) => void;

const styles = (theme: Theme) => createStyles({
  container: {
    display: 'flex',
    flexWrap: 'wrap',
    padding: theme.spacing(1),
  },
  card: {
    padding: theme.spacing(0),
    margin: theme.spacing(1),
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
    borderRadius: 0,
  },
  cardHeader: {
    padding: theme.spacing(1, 1),
    fontSize: '1rem',
  },
  cardContent: {
    padding: theme.spacing(1, 1),
  },
  form: {
    width: '100%',
    maxWidth: '1024px',
  },
  item: {
    padding: 8,
  },
  itemReadOnly: {
    padding: 8,
  },
  button: {
    margin: theme.spacing(3, 1, 2),
    borderRadius: 0,
    textTransform: 'none',
  },
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    width: 200,
  },
  chips: {
    display: 'flex',
    flexWrap: 'wrap',
  },
  chip: {
    margin: 2,
  },
  password: {
    position: 'relative',
  },
  passwordStrength: {
    position: 'absolute',
    right: '8px',
    top: '32px',
  }
});

interface AccountState {
  confirm: boolean;
  confirmOnNavigate: boolean;
  account: AccountCommand | null;
}

interface RouteParams {
  id?: string | undefined;
}

interface AccountFormProps extends PropsFromRedux, WithStyles<typeof styles>, RouteComponentProps<RouteParams> {
  intl: IntlShape,
}

/*
 * Notes: 
 *
 * For password autocomplete feature see:
 * https://stackoverflow.com/questions/15738259/disabling-chrome-autofill
 * 
 */

class AccountForm extends React.Component<AccountFormProps, AccountState> {

  private api: AccountApi;

  private roles: KeyValuePair<string, string>[];

  private formRef: React.RefObject<FormikProps<AccountCommand>>;

  constructor(props: AccountFormProps) {
    super(props);

    this.api = new AccountApi();

    const _t = this.props.intl.formatMessage;

    this.roles = Object.keys(EnumRole).map((key: string): KeyValuePair<string, string> => ({
      key,
      value: _t({ id: `enum.role.${key}` })
    }));

    this.formRef = React.createRef();
  }

  state: AccountState = {
    confirm: false,
    confirmOnNavigate: true,
    account: null,
  }

  get id(): number | null {
    const { id } = this.props.match.params;

    if (!id) {
      return null;
    }

    return Number.parseInt(id);
  }

  discardChanges(): void {
    this.setState({
      confirmOnNavigate: false,
    }, () => this.props.history.push(StaticRoutes.AccountManager));
  }

  showConfirmDialog(): void {
    this.setState({
      confirm: true,
    });
  }

  hideConfirmDialog(): void {
    this.setState({
      confirm: false,
    });
  }

  confirmDialogHandler(action: DialogAction): void {
    switch (action.key) {
      case EnumDialogAction.Yes:
        this.discardChanges();
        break;
      default:
        this.hideConfirmDialog();
        break;
    }
  }

  async loadData(): Promise<AccountCommand | null> {
    const id = this.id;

    if (id) {
      return this.api.findOne(id).then((response: AxiosObjectResponse<AccountFormData>) => {
        if (response.data.success) {
          const { account } = response.data.result;

          this.setState({
            account: this.api.accountToCommand(account),
          });
        } else {
          const messages = localizeErrorCodes(this.props.intl, response.data, false);
          message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />));

          this.props.history.push(StaticRoutes.AccountManager);
        }
        return null;
      });
    } else {
      const account = this.api.createNew();

      this.setState({
        account,
        confirm: false,
        confirmOnNavigate: true,
      });

      return Promise.resolve(account);
    }
  }

  componentDidMount() {
    this.loadData();
  }

  componentDidUpdate(prevProps: AccountFormProps) {
    const { id: prevId } = prevProps.match.params;
    const { id: currId } = this.props.match.params;

    if (prevId !== currId) {
      this.loadData().then((account: AccountCommand | null) => {
        if (account != null) {
          this.formRef.current?.setValues(account);
        }
      });
    }
  }

  render() {
    const _t = this.props.intl.formatMessage;

    const { account: o, confirmOnNavigate } = this.state;
    const { classes, loading } = this.props;

    if (!o) {
      return null;
    }

    const id = this.id;

    return (
      <>
        <Grid container justify="center">
          <Formik
            innerRef={this.formRef}
            initialValues={{
              ...o,
            }}
            validate={values => {
              return validator(this.props.intl, values);
            }}
            onSubmit={(values, { setSubmitting }) => {
              (id === null ? this.api.create(values) : this.api.update(id, values))
                .then((response) => {
                  if (response.data.success) {
                    this.discardChanges();
                    message.info('error.insert-success');
                  } else {
                    const messages = localizeErrorCodes(this.props.intl, response.data, true, fieldMapper);
                    message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />), 10000);
                  }
                })
                .catch((err: AxiosError<SimpleResponse>) => {
                  const messages = localizeErrorCodes(this.props.intl, err.response?.data, true, fieldMapper);
                  message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />), 10000);
                })
                .finally(() => {
                  setSubmitting(false);
                });
            }}
          >
            {({ isSubmitting, values, setFieldValue, errors }) => (
              <Form className={classes.form}>
                <Card className={classes.card}>
                  <CardHeader className={classes.cardHeader}
                    avatar={<Icon path={mdiBadgeAccountOutline} size="1.5rem" />}
                    title={_t({ id: 'account.form.section.user' })}
                  >
                  </CardHeader>
                  <CardContent className={classes.cardContent}>
                    <Grid container item xs={12}>
                      <Grid item xs={6} className={classes.item}>
                        <Field
                          component={TextField}
                          name="firstName"
                          type="text"
                          fullWidth
                          inputProps={{
                            id: 'firstName',
                            maxLength: 64,
                          }}
                          label={_t({ id: 'account.form.field.firstName' })}
                        />
                      </Grid>
                      <Grid item xs={6} className={classes.item}>
                        <Field
                          component={TextField}
                          name="lastName"
                          type="text"
                          fullWidth
                          inputProps={{
                            id: 'lastName',
                            maxLength: 64,
                          }}
                          label={_t({ id: 'account.form.field.lastName' })}
                        />
                      </Grid>
                      <Grid item xs={12} className={classes.item}>
                        <Field
                          component={TextField}
                          name="email"
                          type="email"
                          inputProps={{
                            id: 'email',
                            maxLength: 80,
                          }}
                          label={_t({ id: 'account.form.field.email' })}
                          fullWidth
                          disabled={!!id}
                        />
                      </Grid>
                      <Grid item xs={12} className={classes.item}>
                        <FormControl fullWidth error={!!errors.roles}>
                          <Field
                            component={AutoCompleteFormik}
                            name="roles"
                            multiple
                            options={this.roles}
                            getOptionLabel={(option: KeyValuePair<string, string>) => option.value}
                            getOptionKey={(option: KeyValuePair<string, string>) => option.key}
                            noOptionsText={_t({ id: 'view.shared.autocomplete.no-options' })}
                            inputProps={{
                              id: 'roles',
                              multiple: true,
                              label: _t({ id: 'account.form.field.roles' }),
                              error: !!errors.roles,
                              renderValue: (selected: string[]) => (
                                <div className={classes.chips}>
                                  {selected.map((value: string) => (
                                    <Chip key={value} label={_t({ id: `enum.role.${value}` })} className={classes.chip} />
                                  ))}
                                </div>
                              )
                            }}
                          >
                            {this.roles.map(r => (
                              <MenuItem key={r.key} value={r.key}>{r.value}</MenuItem>
                            ))}
                          </Field>
                          {errors.roles &&
                            <FormHelperText>{errors.roles}</FormHelperText>
                          }
                        </FormControl>
                      </Grid>
                      <Grid item xs={6} className={classes.item}>
                        <Field
                          component={TextField}
                          name="phone"
                          inputProps={{
                            id: 'phone',
                            maxLength: 15,
                          }}
                          label={_t({ id: 'account.form.field.phone' })}
                          fullWidth
                        />
                      </Grid>
                      <Grid item xs={6} className={classes.item}>
                        <Field
                          component={TextField}
                          name="mobile"
                          inputProps={{
                            id: 'mobile',
                            maxLength: 15,
                          }}
                          label={_t({ id: 'account.form.field.mobile' })}
                          fullWidth
                        />
                      </Grid>
                      <Grid item xs={6} className={`${classes.item} ${classes.password}`}>
                        <Field
                          component={TextField}
                          name="password"
                          type="password"
                          inputProps={{
                            id: 'password',
                            maxLength: 30,
                            autoComplete: 'new-password'
                          }}
                          label={_t({ id: 'account.form.field.password' })}
                          fullWidth
                        />
                        <PasswordStrength
                          className={classes.passwordStrength}
                          minStrength={3}
                          thresholdLength={8}
                          password={values.password || ''}
                        >
                        </PasswordStrength>
                      </Grid>
                      <Grid item xs={6} className={classes.item}>
                        <Field
                          component={TextField}
                          name="passwordMatch"
                          type="password"
                          inputProps={{
                            id: 'passwordMatch',
                            maxLength: 30,
                          }}
                          label={_t({ id: 'account.form.field.passwordMatch' })}
                          fullWidth
                        />
                      </Grid>
                      <Grid container item xs={12} className={classes.item} alignItems="center" justify="flex-end" >
                        <FormControlLabel
                          control={
                            <Field
                              component={Checkbox}
                              name="active"
                              type="checkbox"
                              color="primary"
                              inputProps={{
                                id: 'active',
                              }}
                            />
                          }
                          label={_t({ id: 'account.form.field.active' })}
                        />
                      </Grid>

                      <Grid container item xs={12} justify="flex-end">
                        <Button
                          type="submit"
                          style={{ marginRight: 10 }}
                          variant="contained"
                          color="primary"
                          className={`${classes.button} mr-2`}
                          disabled={isSubmitting || loading}
                        >
                          <FormattedMessage id="view.shared.action.save" />
                        </Button>
                        <Button
                          type="button"
                          variant="contained"
                          className={classes.button}
                          onClick={() => this.showConfirmDialog()}
                          disabled={isSubmitting || loading}>
                          <FormattedMessage id="view.shared.action.cancel" />
                        </Button>
                      </Grid>
                      <SecureContent roles={[EnumRole.DEVELOPER]}>
                        <pre>{JSON.stringify(values, null, 2)}</pre>
                      </SecureContent>
                    </Grid>
                  </CardContent>
                </Card>
              </Form>
            )}
          </Formik>
        </Grid>
        { this.renderConfirm()}
        <RouteGuard
          when={confirmOnNavigate}
          navigate={(location: string): void => this.props.history.push(location)}
        />
      </>
    );
  }

  renderConfirm() {
    const _t = this.props.intl.formatMessage;

    const { confirm } = this.state;

    if (!confirm) {
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
        <FormattedMessage id="view.shared.message.cancel-confirm" />
      </Dialog>
    );
  }
}

const mapState = (state: RootState) => ({
  config: state.config,
  loading: state.account.explorer.loading,
  lastUpdated: state.account.explorer.lastUpdated,
});

const mapDispatch = {
};

const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>

// Apply styles
const styledComponent = withStyles(styles)(AccountForm);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject state
export default connector(localizedComponent);
