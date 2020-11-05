import {
  LOGOUT_INIT,
} from 'store/security/types';

import {
  TOGGLE_LAYER,
  MapViewerActions,
  MapViewerState
} from 'store/map/types';

const initialState: MapViewerState = {
  selected: [],
};

export function mapViewerReducer(
  state = initialState,
  action: MapViewerActions
): MapViewerState {

  switch (action.type) {
    case LOGOUT_INIT:
      return {
        ...initialState
      };

    case TOGGLE_LAYER:
      return {
        ...state,
        selected: state.selected.includes(action.index) ? state.selected.filter(i => i !== action.index) : [...state.selected, action.index],
      };

    default:
      return state;
  }

}
