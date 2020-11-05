import { LogoutInitAction } from 'store/security/types';

// State
export interface MapViewerState {
  selected: number[];
}

// Actions
export const TOGGLE_LAYER = 'map/viewer/TOGGLE_LAYER';

export interface ToggleLayerAction {
  type: typeof TOGGLE_LAYER;
  index: number;
}

export type MapViewerActions =
  | LogoutInitAction
  | ToggleLayerAction
  ;
