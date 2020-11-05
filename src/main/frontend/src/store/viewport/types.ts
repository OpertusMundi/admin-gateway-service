// State
export interface ViewportState {
  width: number | null;
  height: number | null;
}

// Actions
export const SET_SIZE = 'viewport/SET_SIZE';

interface SetSizeAction {
  type: typeof SET_SIZE,
  width: number | null,
  height: number | null,
}

export type ViewportTypes =
  | SetSizeAction
  ;
