import React from 'react';
import { AxiosError } from 'axios';

// State, routing and localization
import { connect, ConnectedProps } from 'react-redux';
import { FormattedMessage, injectIntl, IntlShape } from 'react-intl';

// 3rd party components
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';
import clsx from 'clsx';

import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import IconButton from '@material-ui/core/IconButton';
import CloudUploadIcon from '@material-ui/icons/CloudUpload';
import ClearIcon from '@material-ui/icons/Clear';
import AddPhotoAlternateOutlinedIcon from '@material-ui/icons/AddPhotoAlternateOutlined';
import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';

import { Field, Form, Formik } from 'formik';

import { TextField } from 'formik-material-ui';

import Icon from '@mdi/react';
import { mdiCommentAlertOutline } from '@mdi/js';

// Utilities
import { passwordValidator as validator } from 'validation/account';

// Services
import message from 'service/message';

// Store
import { RootState } from 'store';
import { setPassword, setProfile } from 'store/security/thunks';

// Model
import { FieldMapperFunc, localizeErrorCodes } from 'utils/error';
import { Account, SetPasswordCommand, ProfileCommand } from 'model/account';
import { SimpleResponse } from 'model/response';

const MB = 1024 * 1024;

export type DeepPartial<T, V> =
  T extends Array<infer R> ? V :
  T extends object ? { [P in keyof T]?: DeepPartial<T[P], V>; } : V;

const fieldMapper: FieldMapperFunc = (field: string): string | null => {
  switch (field) {
    case 'password':
      return 'account.form.field.password';
    case 'passwordMatch':
      return 'account.form.field.passwordMatch';
  }
  return null;
};

const styles = (theme: Theme) => createStyles({
  container: {
    display: 'flex',
    flexWrap: 'wrap',
  },
  hidden: {
    display: 'none',
  },
  card: {
    width: 345,
    borderRadius: 0,
    padding: theme.spacing(1),
    margin: theme.spacing(1),
  },
  cardActions: {
    justifyContent: 'flex-end',
  },
  avatar: {
    width: theme.spacing(6),
    height: theme.spacing(6),
  },
  avatarImageContainer: {
    textAlign: 'center',
  },
  avatarImage: {
    maxWidth: 298,
  },
  avatarImagePlaceholder: {
    margin: theme.spacing(7, 0, 3, 0),
  },
  button: {
    margin: theme.spacing(3, 1, 2),
    borderRadius: 0,
    textTransform: 'none',
  },
  paper: {
    padding: theme.spacing(1),
    margin: theme.spacing(1),
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
    borderRadius: 0,
    width: '100%'
  },
  item: {
    padding: 8,
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
  }
});

interface ProfileFormState {
  avatar: { image: string, imageMimeType: string } | null;
  password: boolean;
}

interface ProfileFormProps extends PropsFromRedux, WithStyles<typeof styles> {
  intl: IntlShape,
}

/*
 * Notes: 
 *
 * For password autocomplete feature see:
 * https://stackoverflow.com/questions/15738259/disabling-chrome-autofill
 * 
 */

const accountToProfileCommand = (a: Account | null): ProfileCommand => {

  return {
    firstName: a!.firstName,
    image: a!.image,
    imageMimeType: a!.imageMimeType,
    lastName: a!.lastName,
    locale: a!.locale,
    mobile: a!.mobile,
    phone: a!.phone,
  }
};

class Profile extends React.Component<ProfileFormProps, ProfileFormState> {

  private fileEditRef: React.RefObject<HTMLInputElement>;

  private imageRef: React.RefObject<HTMLImageElement>;

  private form: any;

  constructor(props: ProfileFormProps) {
    super(props);

    this.fileEditRef = React.createRef<HTMLInputElement>();
    this.imageRef = React.createRef<HTMLImageElement>();

    const { profile } = props;

    this.state = {
      avatar: !!profile?.image ? { image: profile.image || '', imageMimeType: profile.imageMimeType || '' } : null,
      password: false,
    }
  }

  get userName(): string {
    const { email, firstName, lastName } = this.props.profile as Account;

    if (firstName || lastName) {
      return `${firstName} ${lastName}`;
    }

    return email;
  }

  get avatarDisabled(): boolean {
    return !this.state.avatar;
  }

  onFileSelect(e: React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();

    this.fileEditRef?.current?.click();
  }

  onImageClear(e: React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();

    const { profile } = this.props;

    this.props.setProfile({
      ...accountToProfileCommand(profile),
      image: null,
      imageMimeType: null,
    }).then((response) => {
      if (response.success) {
        this.form.resetForm();
        message.info('message.avatar-changed');

        if (this.imageRef.current) {
          this.imageRef.current.src = '';
        }

        this.setState({
          avatar: null,
        });
      } else {
        const messages = localizeErrorCodes(this.props.intl, response, true, fieldMapper);
        message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />), 10000);
      }
    }).catch((err: AxiosError<SimpleResponse>) => {
      const messages = localizeErrorCodes(this.props.intl, err.response?.data, true, fieldMapper);
      message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />), 10000);
    });
  }

  onImageChange(e: React.ChangeEvent<HTMLInputElement>) {
    e.preventDefault();

    const files = this.fileEditRef?.current?.files || null;

    if (files && files.length === 1) {
      const file = files[0];

      if (file.size > MB) {
        message.errorHtml(
          <FormattedMessage id="error.filesystem.size.less-than" values={{ size: '1MB' }} />,
          () => (<Icon path={mdiCommentAlertOutline} size="3rem" />)
        );
        return;
      }
      if (!file.type.startsWith('image/')) {
        message.errorHtml(
          <FormattedMessage id="error.filesystem.type-not-supported" />,
          () => (<Icon path={mdiCommentAlertOutline} size="3rem" />)
        );
        return;
      }

      this.setImage(file);
    }
  }

  setImage(file: File) {
    const reader = new FileReader();

    reader.addEventListener("load", () => {
      const result = reader.result;

      if (result) {
        if (this.imageRef.current) {
          this.imageRef.current.src = result as string;

          const parts = (result as string).split(/;|:|,/, 4);

          this.setState({
            avatar: {
              image: parts[3],
              imageMimeType: parts[1],
            },
          });
        }
      }
    }, false);

    reader.addEventListener("error", () => {
      message.errorHtml(
        <FormattedMessage id="error.filesystem.read-failed" />,
        () => (<Icon path={mdiCommentAlertOutline} size="3rem" />)
      );
    }, false);

    reader.readAsDataURL(file);
  }

  onChangePassword(e: React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();

    if (this.form && Object.keys(this.form.errors).length === 0) {
      const { password, passwordMatch } = this.form.values as SetPasswordCommand;

      if (password && passwordMatch) {
        this.props.setPassword({
          password,
          passwordMatch,
        }).then((response) => {
          if (response.success) {
            this.form.resetForm();
            message.info('message.password-changed');
          } else {
            const messages = localizeErrorCodes(this.props.intl, response, true, fieldMapper);
            message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />), 10000);
          }
        }).catch((err: AxiosError<SimpleResponse>) => {
          const messages = localizeErrorCodes(this.props.intl, err.response?.data, true, fieldMapper);
          message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />), 10000);
        });
      }
    }
  }

  onSaveProfile(e: React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();

    const { avatar } = this.state;
    const { profile } = this.props;

    if (avatar) {
      this.props.setProfile({
        ...accountToProfileCommand(profile),
        image: avatar.image,
        imageMimeType: avatar.imageMimeType,
      }).then((response) => {
        if (response.success) {
          this.form.resetForm();
          message.info('message.avatar-changed');
        } else {
          const messages = localizeErrorCodes(this.props.intl, response, true, fieldMapper);
          message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />), 10000);
        }
      }).catch((err: AxiosError<SimpleResponse>) => {
        const messages = localizeErrorCodes(this.props.intl, err.response?.data, true, fieldMapper);
        message.errorHtml(messages, () => (<Icon path={mdiCommentAlertOutline} size="3rem" />), 10000);
      });
    }
  }

  componentDidMount() {
    // Refresh image the first time the component is loaded
    if (this.state.avatar) {
      const { profile } = this.props;

      if (this.imageRef.current) {
        this.imageRef.current.src = `data:${profile?.imageMimeType};base64,${profile?.image}`;
      }
    }
  }

  render() {
    const _t = this.props.intl.formatMessage;

    const { classes, profile } = this.props;
    const { avatar } = this.state;

    if (!profile) {
      return null;
    }

    return (
      <>
        <div className={classes.hidden}>
          <input ref={this.fileEditRef} type="file" onChange={(e) => this.onImageChange(e)} name="picture" />
        </div>
        <Grid container item xs={12} justify="center">
          <Card className={classes.card}>
            <CardHeader
              action={
                <div>
                  <IconButton onClick={(e) => this.onFileSelect(e)}>
                    <CloudUploadIcon />
                  </IconButton>
                  {avatar &&
                    <IconButton onClick={(e) => this.onImageClear(e)}>
                      <ClearIcon />
                    </IconButton>
                  }
                </div>
              }
              title={_t({ id: 'profile.form.avatar-image-title' })}
            />
            <CardContent className={classes.avatarImageContainer}>
              {!avatar &&
                <AddPhotoAlternateOutlinedIcon style={{ fontSize: '4rem' }} className={classes.avatarImagePlaceholder} />
              }
              <img
                className={
                  clsx(
                    classes.avatarImage, {
                    [classes.hidden]: !avatar,
                  })
                }
                src={'/image.png'}
                alt={_t({ id: 'profile.form.avatar-image-title' })}
                ref={this.imageRef}
              />
            </CardContent>
            <CardActions disableSpacing className={classes.cardActions}>
              <Button
                size="small"
                color="primary"
                className={classes.button}
                onClick={(e) => this.onSaveProfile(e)}
                disabled={this.avatarDisabled}
              >
                <FormattedMessage id="view.shared.action.save"></FormattedMessage>
              </Button>
            </CardActions>
          </Card>
          <Card className={classes.card}>
            <CardHeader
              title={_t({ id: 'profile.form.set-user-password' })}
            />
            <CardContent className={classes.avatarImageContainer}>

              <Formik
                innerRef={(form) => this.form = form}
                initialValues={{
                  password: '',
                  passwordMatch: '',
                }}
                validate={values => {
                  const errors = validator(this.props.intl, values);

                  if (Object.keys(errors).length !== 0 || !values.password || !values.passwordMatch) {
                    this.setState({ password: false });
                  } else {
                    this.setState({ password: true });
                  }

                  return errors;
                }}
                onSubmit={(values, { setSubmitting }) => {

                }}
              >
                {({ isSubmitting, values, setFieldValue, errors }) => (
                  <Form>
                    <Grid container item xs={12}>
                      <Grid item xs={12} className={classes.item}>
                        <Field
                          component={TextField}
                          name="password"
                          type="password"
                          inputProps={{
                            id: 'password',
                            maxLength: 30,
                            autoComplete: 'new-password',
                          }}
                          label={_t({ id: 'account.form.field.password' })}
                          fullWidth
                        />
                      </Grid>
                      <Grid item xs={12} className={classes.item}>
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
                    </Grid>
                  </Form>
                )}
              </Formik>
            </CardContent>
            <CardActions disableSpacing className={classes.cardActions}>
              <Button
                size="small"
                color="primary"
                className={classes.button}
                onClick={(e) => this.onChangePassword(e)}
                disabled={!this.state.password}
              >
                <FormattedMessage id="view.shared.action.save"></FormattedMessage>
              </Button>
            </CardActions>
          </Card>
        </Grid>
      </>
    );
  }

}

const mapState = (state: RootState) => ({
  config: state.config,
  loading: state.account.explorer.loading,
  lastUpdated: state.account.explorer.lastUpdated,
  profile: state.security.profile,
});

const mapDispatch = {
  setPassword: (command: SetPasswordCommand) => setPassword(command),
  setProfile: (command: ProfileCommand) => setProfile(command),
};

const connector = connect(
  mapState,
  mapDispatch,
);

type PropsFromRedux = ConnectedProps<typeof connector>

// Apply styles
const styledComponent = withStyles(styles)(Profile);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Inject state
export default connector(localizedComponent);
