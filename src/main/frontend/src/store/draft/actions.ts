import { PageResult, Sorting, SimpleResponse } from 'model/response';
import { EnumSortField, AssetDraft, AssetDraftQuery } from 'model/draft';

import {
  DraftActions,
  SET_PAGER,
  RESET_PAGER,
  SET_FILTER,
  RESET_FILTER,
  SEARCH_INIT,
  SEARCH_FAILURE,
  SEARCH_COMPLETE,
  ADD_SELECTED,
  REMOVE_SELECTED,
  SET_SORTING,
  RESET_SELECTED,
  REVIEW_INIT,
  REVIEW_COMPLETE,
} from './types';


// Action Creators
export function setPager(page: number, size: number): DraftActions {
  return {
    type: SET_PAGER,
    page,
    size,
  };
}

export function resetPager(): DraftActions {
  return {
    type: RESET_PAGER,
  };
}

export function setFilter(query: Partial<AssetDraftQuery>): DraftActions {
  return {
    type: SET_FILTER,
    query,
  };
}

export function resetFilter(): DraftActions {
  return {
    type: RESET_FILTER,
  };
}

export function setSorting(sorting: Sorting<EnumSortField>[]): DraftActions {
  return {
    type: SET_SORTING,
    sorting,
  };
}

export function searchInit(): DraftActions {
  return {
    type: SEARCH_INIT,
  };
}

export function searchFailure(): DraftActions {
  return {
    type: SEARCH_FAILURE,
  };
}

export function searchComplete(result: PageResult<AssetDraft>): DraftActions {
  return {
    type: SEARCH_COMPLETE,
    result,
  };
}

export function reviewInit(): DraftActions {
  return {
    type: REVIEW_INIT,
  };
}

export function reviewComplete(response: SimpleResponse): DraftActions {
  return {
    type: REVIEW_COMPLETE,
    response,
  };
}

export function addToSelection(selected: AssetDraft[]): DraftActions {
  return {
    type: ADD_SELECTED,
    selected,
  };
}

export function removeFromSelection(removed: AssetDraft[]): DraftActions {
  return {
    type: REMOVE_SELECTED,
    removed,
  };
}

export function resetSelection(): DraftActions {
  return {
    type: RESET_SELECTED,
  };
}
