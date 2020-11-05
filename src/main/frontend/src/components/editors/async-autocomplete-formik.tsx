import _ from 'lodash';
import React from 'react';

import TextField from '@material-ui/core/TextField';

import Autocomplete, { AutocompleteProps } from '@material-ui/lab/Autocomplete';
import CircularProgress from '@material-ui/core/CircularProgress';

import { FieldProps } from 'formik';

interface AsyncAutoCompleteFormikState<T> {
  loading: boolean;
  options: T[];
}

interface InputProps<T> {
  label?: string;
  onChange?: (value: T | null) => void;
  error?: boolean;
}

interface AsyncAutoCompleteFormikProps<T, K> extends FieldProps, Omit<AutocompleteProps<T, boolean | undefined, undefined, undefined>, 'name' | 'value' | 'error'> {
  getOptionKey: (option: T) => K;
  inputProps: InputProps<T>;
  multiple?: boolean;
  loadOptions: (value: string) => Promise<T[]>;
  promptText?: string;
}

class AsyncAutoCompleteFormik<T, K> extends React.Component<AsyncAutoCompleteFormikProps<T, K>, AsyncAutoCompleteFormikState<T>> {

  state: AsyncAutoCompleteFormikState<T> = {
    loading: false,
    options: [],
  }

  constructor(props: AsyncAutoCompleteFormikProps<T, K>) {
    super(props);

    this.state.options = props.options;
  }

  render() {
    const {
      loading,
      options,
    } = this.state;

    const {
      field,
      inputProps,
      getOptionLabel,
      getOptionKey,
      noOptionsText,
      loadOptions,
      loadingText,
      promptText,
    } = this.props;

    const loadOptionsDelayed = _.debounce((value: string): void => {
      this.setState({
        loading: true,
      });

      loadOptions(value).then((options: T[]) => {
        this.setState({
          loading: false,
          options,
        })
      });
    }, 500);

    return (
      <Autocomplete
        options={options}
        loading={loading}
        loadingText={loadingText}
        getOptionLabel={getOptionLabel}
        value={options.find(i => field.value === getOptionKey(i)) || null}
        noOptionsText={!field.value && promptText ? promptText : noOptionsText}
        onChange={(event: React.ChangeEvent<{}>, value: T | null,) => {
          this.props.form.setFieldValue(field.name, value ? this.props.getOptionKey(value) : null)
          if (typeof inputProps.onChange === 'function') {
            inputProps.onChange(value);
          }
        }}
        renderInput={(params) => (
          <TextField
            {...params}
            label={inputProps.label || ''}
            error={inputProps.error}
            InputProps={{
              ...params.InputProps,
              endAdornment: (
                <React.Fragment>
                  {loading ? <CircularProgress color="inherit" size={20} /> : null}
                  {params.InputProps.endAdornment}
                </React.Fragment>
              ),
              onChange: (event: React.ChangeEvent<HTMLInputElement>): void => {
                loadOptionsDelayed(event.target.value);
              }
            }}
          />
        )}
      />
    );
  }
}

export default AsyncAutoCompleteFormik
