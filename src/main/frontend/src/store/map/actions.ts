import {
  MapViewerActions,
  TOGGLE_LAYER,
} from './types';


// Action Creators
export function toggleLayer(index: number): MapViewerActions {
  return {
    type: TOGGLE_LAYER,
    index,
  };
}
