import _ from 'lodash';

import moment from 'utils/moment-localized';

import {
  LOGOUT_INIT,
} from 'store/security/types';

import {
  SET_PAGER,
  RESET_PAGER,
  SET_FILTER,
  RESET_FILTER,
  SEARCH_INIT,
  SEARCH_COMPLETE,
  ADD_SELECTED,
  REMOVE_SELECTED,
  RESET_SELECTED,
  SET_SORTING,
  REVIEW_COMPLETE,
  REVIEW_INIT,
  SEARCH_FAILURE,
  DraftActions,
  DraftManagerState,
} from 'store/draft/types';

import { EnumDraftStatus } from 'model/draft';

const initialState: DraftManagerState = {
  loading: false,
  query: {
    status: [EnumDraftStatus.PENDING_HELPDESK_REVIEW],
    provider: null,
  },
  pagination: {
    page: 0,
    size: 10,
  },
  sorting: [{
    id: 'modifiedOn',
    order: 'desc',
  }],
  result: null,
  selected: [],
  lastUpdated: null,
  response: null,
};

export function draftReducer(
  state = initialState,
  action: DraftActions
): DraftManagerState {

  switch (action.type) {
    case LOGOUT_INIT:
      return {
        ...initialState
      };

    case SET_PAGER:
      return {
        ...state,
        pagination: {
          page: action.page,
          size: action.size,
        },
      };

    case RESET_PAGER:
      return {
        ...state,
        pagination: {
          ...initialState.pagination
        },
        selected: [],
      };

    case SET_FILTER: {
      return {
        ...state,
        query: {
          ...state.query,
          ...action.query,
        },
      };
    }

    case RESET_FILTER:
      return {
        ...state,
        query: {
          ...initialState.query
        },
        selected: [],
      };

    case SET_SORTING:
      console.log(action);
      return {
        ...state,
        sorting: action.sorting,
      };

    case SEARCH_INIT:
      return {
        ...state,
        selected: [],
        response: null,
        loading: true,
      };

    case SEARCH_FAILURE:
      return {
        ...state,
        result: null,
        pagination: {
          page: 0,
          size: state.pagination.size,
        },
        lastUpdated: moment(),
        loading: false,
      };

    case SEARCH_COMPLETE:
      return {
        ...state,
        result: action.result,
        pagination: {
          page: action.result.pageRequest.page,
          size: action.result.pageRequest.size,
        },
        lastUpdated: moment(),
        loading: false,
      };

    case ADD_SELECTED:
      return {
        ...state,
        selected: _.uniqBy([...state.selected, ...action.selected], 'title'),
      };

    case REMOVE_SELECTED:
      return {
        ...state,
        selected: state.selected.filter(s => !action.removed.some(r => r.key === s.key)),
      };

    case RESET_SELECTED:
      return {
        ...state,
        selected: [],
      };

    case REVIEW_INIT:
      return {
        ...state,
        loading: true,
      };

    case REVIEW_COMPLETE:
      return {
        ...state,
        response: action.response,
        loading: false,
      }

    default:
      return state;
  }

}
