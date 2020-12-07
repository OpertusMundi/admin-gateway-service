import { PageResult, Sorting, ObjectResponse } from 'model/response';
import { EnumSortField, Account, AccountQuery } from 'model/account';

import {
  AccountActions,
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
  SAVE_INIT,
  SAVE_COMPLETE,
} from './types';


// Action Creators
export function setPager(page: number, size: number): AccountActions {
  return {
    type: SET_PAGER,
    page,
    size,
  };
}

export function resetPager(): AccountActions {
  return {
    type: RESET_PAGER,
  };
}

export function setFilter(query: Partial<AccountQuery>): AccountActions {
  return {
    type: SET_FILTER,
    query,
  };
}

export function resetFilter(): AccountActions {
  return {
    type: RESET_FILTER,
  };
}

export function setSorting(sorting: Sorting<EnumSortField>[]): AccountActions {
  return {
    type: SET_SORTING,
    sorting,
  };
}

export function searchInit(): AccountActions {
  return {
    type: SEARCH_INIT,
  };
}

export function searchFailure(): AccountActions {
  return {
    type: SEARCH_FAILURE,
  };
}

export function searchComplete(result: PageResult<Account>): AccountActions {
  return {
    type: SEARCH_COMPLETE,
    result,
  };
}

export function saveInit(): AccountActions {
  return {
    type: SAVE_INIT,
  };
}

export function saveComplete(response: ObjectResponse<Account>): AccountActions {
  return {
    type: SAVE_COMPLETE,
    response,
  };
}

export function addToSelection(selected: Account[]): AccountActions {
  return {
    type: ADD_SELECTED,
    selected,
  };
}

export function removeFromSelection(removed: Account[]): AccountActions {
  return {
    type: REMOVE_SELECTED,
    removed,
  };
}

export function resetSelection(): AccountActions {
  return {
    type: RESET_SELECTED,
  };
}
