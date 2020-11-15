import { ThunkAction } from 'redux-thunk'

// Redux
import { RootState } from 'store';
import { AccountActions } from './types';
import { searchInit, searchComplete, searchFailure, setSorting, setPager, saveInit, saveComplete } from './actions';

// Services
import AccountApi from 'service/account';

// Model
import { PageRequest, Sorting, PageResult, ObjectResponse } from 'model/response';
import { Account, AccountCommand } from 'model/account';

// Helper thunk result type
type ThunkResult<R> = ThunkAction<Promise<R>, RootState, unknown, AccountActions>;

export const find = (
  pageRequest?: PageRequest, sorting?: Sorting[]
): ThunkResult<PageResult<Account> | null> => async (dispatch, getState) => {
  // Get query form state (filters are always set synchronously)
  const query = getState().account.explorer.query;

  // Update sorting or use the existing value
  if (sorting) {
    dispatch(setSorting(sorting));
  } else {
    sorting = getState().account.explorer.sorting;
  }

  // Update page or user the existing value (i.e. data page refresh)
  if (pageRequest) {
    const { page, size } = pageRequest;

    dispatch(setPager(page, size));
  } else {
    pageRequest = getState().account.explorer.pagination
  }

  // Initialize search
  dispatch(searchInit());

  // Get response
  const api = new AccountApi();

  const response = await api.find(query, pageRequest, sorting);

  // Update state
  if (response.data.success) {
    dispatch(searchComplete(response.data.result));
    return response.data.result;
  }

  dispatch(searchFailure());
  return null;
}

export const update = (
  id: number | null, command: AccountCommand
): ThunkResult<ObjectResponse<Account>> => async (dispatch, getState) => {
  dispatch(saveInit());

  // Get response
  const api = new AccountApi();

  const response = id ? await api.update(id, command) : await api.create(command);

  // Update state
  dispatch(saveComplete(response.data));

  return response.data;
}
