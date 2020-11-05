import {
  SET_SIZE,
  ViewportTypes,
  ViewportState,
} from "./types";

const initialState: ViewportState = {
  width: document?.documentElement?.clientWidth,
  height: document?.documentElement?.clientHeight,
};

export function viewportReducer(
  state = initialState,
  action: ViewportTypes
): ViewportState {

  switch (action.type) {
    case SET_SIZE:
      return {
        ...state,
        width: action.width,
        height: action.height,
      };

    default:
      return state;
  }
}
