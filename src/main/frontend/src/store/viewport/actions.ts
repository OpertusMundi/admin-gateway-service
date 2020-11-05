import {
  SET_SIZE,
  ViewportTypes,
} from './types';

// Action Creators
export function setSize(width: number | null, height: number | null): ViewportTypes {
  return {
    type: SET_SIZE,
    width,
    height,
  };
}
