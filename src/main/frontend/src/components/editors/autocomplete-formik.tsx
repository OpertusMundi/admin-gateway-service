import React from 'react';

import TextField from '@material-ui/core/TextField';

import Autocomplete, { AutocompleteProps, AutocompleteChangeReason, AutocompleteChangeDetails } from '@material-ui/lab/Autocomplete';

import { FieldProps } from 'formik';

interface InputProps<T> {
  label?: string,
  onChange?: (options: T[]) => void,
  error?: boolean,
}

interface AutoCompleteFormikProps<T, K> extends FieldProps, Omit<AutocompleteProps<T, boolean | undefined, undefined, undefined>, 'name' | 'value' | 'error'> {
  getOptionKey: (option: T) => K,
  inputProps: InputProps<T>,
  multiple?: boolean,
}

class AutoCompleteFormik<T, K> extends React.Component<AutoCompleteFormikProps<T, K>> {

  render() {
    const {
      field,
      inputProps,
      options,
      getOptionLabel,
      getOptionKey,
      noOptionsText,
      multiple = false,
    } = this.props;

    if (multiple) {
      return (
        <Autocomplete
          multiple
          options={options}
          getOptionLabel={getOptionLabel}
          value={options.filter(i => field.value.includes(getOptionKey(i))) || null}
          noOptionsText={noOptionsText}
          onChange={(event: React.ChangeEvent<{}>,
            value: T[],
            reason: AutocompleteChangeReason,
            details?: AutocompleteChangeDetails<T>
          ) => {
            this.props.form.setFieldValue(field.name, value.map(r => this.props.getOptionKey(r)))
            if (typeof inputProps.onChange === 'function') {
              inputProps.onChange(options);
            }
          }}
          renderInput={(params) => (
            <TextField
              {...params}
              label={inputProps.label || ''}
              error={inputProps.error}
            />
          )}
        />
      );
    }
    return (
      <Autocomplete
        options={options}
        getOptionLabel={getOptionLabel}
        value={options.find(i => field.value === getOptionKey(i)) || null}
        noOptionsText={noOptionsText}
        onChange={(event: React.ChangeEvent<{}>,
          value: T | null,
          reason: AutocompleteChangeReason,
          details?: AutocompleteChangeDetails<T>
        ) => {
          this.props.form.setFieldValue(field.name, value ? this.props.getOptionKey(value) : null)
          if (typeof inputProps.onChange === 'function') {
            inputProps.onChange(options);
          }
        }}
        renderInput={(params) => (
          <TextField
            {...params}
            label={inputProps.label || ''}
            error={inputProps.error}
          />
        )}
      />
    );
  }
}

export default AutoCompleteFormik
