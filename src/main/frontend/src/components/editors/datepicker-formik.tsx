import React from 'react';

import { DatePicker, DatePickerProps } from "@material-ui/pickers";

import { FieldProps } from 'formik';

interface InputProps {
  format?: string,
}

interface DatePickerFormikProps extends FieldProps, Omit<DatePickerProps, 'name' | 'value' | 'error'> {
  inputProps: InputProps,
}

class DatePickerFormik extends React.Component<DatePickerFormikProps> {

  render() {
    const {
      field,
      form,
      inputProps,
      onChange,
      readOnly,
      ...other
    } = this.props;

    const currentError = form.errors[field.name];

    return (
      <DatePicker
        clearable
        name={field.name}
        value={field.value}
        error={Boolean(currentError)}
        format={(inputProps ? inputProps.format : null) || "dd/MM/yyyy"}
        onChange={date => form.setFieldValue(field.name, date, true)}
        readOnly={readOnly}
        {...other}
      />
    );
  }
}

export default DatePickerFormik
