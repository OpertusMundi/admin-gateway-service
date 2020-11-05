import { Point } from 'geojson';

export function toPoint(lon: number, lat: number): Point {
  return {
    type: 'Point',
    coordinates: [lon, lat],
  };
}
