import { ThunkAction } from 'redux-thunk'

// Redux
import { RootState } from 'store';
import { DraftActions } from './types';
import { searchInit, searchComplete, searchFailure, setSorting, setPager, reviewInit, reviewComplete } from './actions';

// Services
import DraftApi from 'service/draft';

// Model
import { PageRequest, Sorting, PageResult, SimpleResponse } from 'model/response';
import { EnumSortField, AssetDraft } from 'model/draft';

// Helper thunk result type
type ThunkResult<R> = ThunkAction<Promise<R>, RootState, unknown, DraftActions>;

export const find = (
  pageRequest?: PageRequest, sorting?: Sorting<EnumSortField>[]
): ThunkResult<PageResult<AssetDraft> | null> => async (dispatch, getState) => {
  // Get query form state (filters are always set synchronously)
  const query = getState().draft.query;

  // Update sorting or use the existing value
  if (sorting) {
    dispatch(setSorting(sorting));
  } else {
    sorting = getState().draft.sorting;
  }

  // Update page or user the existing value (i.e. data page refresh)
  if (pageRequest) {
    const { page, size } = pageRequest;

    dispatch(setPager(page, size));
  } else {
    pageRequest = getState().draft.pagination
  }

  // Initialize search
  dispatch(searchInit());

  // Get response
  const api = new DraftApi();

  const response = await api.find(query, pageRequest, sorting);

  // Update state
  if (response.data.success) {
    dispatch(searchComplete(response.data.result));
    return response.data.result;
  }

  dispatch(searchFailure());
  return null;
}

export const accept = (
  providerKey: string, draftKey: string
): ThunkResult<SimpleResponse> => async (dispatch, getState) => {
  dispatch(reviewInit());

  // Get response
  const api = new DraftApi();

  const response = await api.accept(providerKey, draftKey);

  // Update state
  dispatch(reviewComplete(response.data));

  return response.data;
}

export const reject = (
  providerKey: string, draftKey: string, reason: string
): ThunkResult<SimpleResponse> => async (dispatch, getState) => {
  dispatch(reviewInit());

  // Get response
  const api = new DraftApi();

  const response = await api.reject(providerKey, draftKey, reason);

  // Update state
  dispatch(reviewComplete(response.data));

  return response.data;
}
