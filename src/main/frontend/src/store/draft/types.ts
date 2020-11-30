import { Moment } from 'moment';

import { LogoutInitAction } from 'store/security/types';
import { PageResult, PageRequest, Sorting, SimpleResponse } from 'model/response';
import { AssetDraftQuery, AssetDraft } from 'model/draft';

// State
export interface DraftManagerState {
  loading: boolean;
  lastUpdated: Moment | null;
  pagination: PageRequest;
  query: AssetDraftQuery;
  result: PageResult<AssetDraft> | null;
  selected: AssetDraft[];
  sorting: Sorting[];
  response: SimpleResponse | null;
}

// Actions
export const SET_PAGER = 'draft/manager/SET_PAGER';
export const RESET_PAGER = 'draft/manager/RESET_PAGER';

export const SET_SORTING = 'draft/manager/SET_SORTING';

export const SET_FILTER = 'draft/manager/SET_FILTER';
export const RESET_FILTER = 'draft/manager/RESET_FILTER';

export const SEARCH_INIT = 'draft/manager/SEARCH_INIT';
export const SEARCH_FAILURE = 'draft/manager/SEARCH_FAILURE';
export const SEARCH_COMPLETE = 'draft/manager/SEARCH_COMPLETE';

export const REVIEW_INIT = 'draft/manager/SAVE_INIT';
export const REVIEW_COMPLETE = 'draft/manager/SAVE_COMPLETE';

export const ADD_SELECTED = 'draft/manager/ADD_SELECTED';
export const REMOVE_SELECTED = 'draft/manager/REMOVE_SELECTED';
export const RESET_SELECTED = 'draft/manager/RESET_SELECTED';

export interface SetPagerAction {
  type: typeof SET_PAGER;
  page: number;
  size: number;
}

export interface ResetPagerAction {
  type: typeof RESET_PAGER
}

export interface SetSortingAction {
  type: typeof SET_SORTING;
  sorting: Sorting[];
}

export interface SetFilterAction {
  type: typeof SET_FILTER;
  query: Partial<AssetDraftQuery>;
}

export interface ResetFilterAction {
  type: typeof RESET_FILTER;
}

export interface SearchInitAction {
  type: typeof SEARCH_INIT;
}

export interface SearchFailureAction {
  type: typeof SEARCH_FAILURE;
}

export interface SearchCompleteAction {
  type: typeof SEARCH_COMPLETE;
  result: PageResult<AssetDraft>;
}

export interface ReviewInitAction {
  type: typeof REVIEW_INIT;
}

export interface ReviewCompleteAction {
  type: typeof REVIEW_COMPLETE;
  response: SimpleResponse;
}

export interface SetSelectedAction {
  type: typeof ADD_SELECTED;
  selected: AssetDraft[];
}

export interface RemoveFromSelectionAction {
  type: typeof REMOVE_SELECTED;
  removed: AssetDraft[];
}

export interface ResetSelectionAction {
  type: typeof RESET_SELECTED;
}

export type DraftActions =
  | LogoutInitAction
  | SetPagerAction
  | ResetPagerAction
  | SetSortingAction
  | SetFilterAction
  | ResetFilterAction
  | SearchInitAction
  | SearchFailureAction
  | SearchCompleteAction
  | SetSelectedAction
  | RemoveFromSelectionAction
  | ResetSelectionAction
  | ReviewInitAction
  | ReviewCompleteAction
  ;
