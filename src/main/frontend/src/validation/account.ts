import { IntlShape } from 'react-intl';

import { FormikErrors } from 'formik';

import { AccountCommand, SetPasswordCommand } from 'model/account';


//type DeepPartial<T> = T extends Array<infer R> ? string : T extends Function ? T : (T extends object ? { [P in keyof T]?: DeepPartial<T[P]>; } : T extends number ? string : T);

export type AccountErrors = FormikErrors<AccountCommand>;

export type PasswordErrors = FormikErrors<SetPasswordCommand>;

export function accountValidator(intl: IntlShape, values: AccountCommand): AccountErrors {
  const _t = intl.formatMessage;

  const errors: AccountErrors = {};

  // User
  if (!values.username) {
    errors.username = _t({ id: 'validation.required' });
  }
  if (!values.email) {
    errors.email = _t({ id: 'validation.required' });
  }
  if (!values.roles || values.roles.length === 0) {
    errors.roles = _t({ id: 'validation.required' });
  }
  if (!values.firstName) {
    errors.firstName = _t({ id: 'validation.required' });
  }
  if (!values.lastName) {
    errors.lastName = _t({ id: 'validation.required' });
  }
  if (!values.mobile) {
    errors.mobile = _t({ id: 'validation.required' });
  }
  if (values.id) {
    if (values.password) {
      if (!values.passwordMatch) {
        errors.passwordMatch = _t({ id: 'validation.required' });
      } if (values.password !== values.passwordMatch) {
        errors.passwordMatch = _t(
          { id: 'validation.mismatch' }, {
          field1: _t({ id: 'account.form.field.password' }),
          field2: _t({ id: 'account.form.field.passwordMatch' })
        });
      }
    } else if (values.passwordMatch) {
      if (!values.password) {
        errors.password = _t({ id: 'validation.required' });
      } if (values.password !== values.passwordMatch) {
        errors.passwordMatch = _t(
          { id: 'validation.mismatch' }, {
          field1: _t({ id: 'account.form.field.password' }),
          field2: _t({ id: 'account.form.field.passwordMatch' })
        });
      }
    }
  } else {
    if (!values.password) {
      errors.password = _t({ id: 'validation.required' });
    } else if (!values.passwordMatch) {
      errors.passwordMatch = _t({ id: 'validation.required' });
    } if (values.password !== values.passwordMatch) {
      errors.passwordMatch = _t(
        { id: 'validation.mismatch' }, {
        field1: _t({ id: 'account.form.field.password' }),
        field2: _t({ id: 'account.form.field.passwordMatch' })
      });
    }
  }

  return errors;
}

export function passwordValidator(intl: IntlShape, values: SetPasswordCommand): PasswordErrors {
  const _t = intl.formatMessage;

  const errors: FormikErrors<SetPasswordCommand> = {};

  if (values.password) {
    if (!values.passwordMatch) {
      errors.passwordMatch = _t({ id: 'validation.required' });
    } if (values.password !== values.passwordMatch) {
      errors.passwordMatch = _t(
        { id: 'validation.mismatch' }, {
        field1: _t({ id: 'account.form.field.password' }),
        field2: _t({ id: 'account.form.field.passwordMatch' })
      });
    }
  }

  return errors;
}