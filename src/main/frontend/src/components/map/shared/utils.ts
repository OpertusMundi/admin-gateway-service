import Collection from 'ol/Collection';
import Feature from 'ol/Feature';

import { createEmpty, extend, isEmpty } from 'ol/extent';

const opacity = ['00', '1A', '33', '4D', '66', '80', '99', 'B3', 'CC', 'E6', 'FF'];

export const opacityToHex = (value: number = 100): string => {
  const index = Math.min(100, Math.max(0, value / 10));

  return opacity[index];
};

const toFeatureArray = (features: Feature | Feature[] | Collection<Feature>) => {
  if (features instanceof Feature) {
    return [features];
  }
  if (features instanceof Collection) {
    return features.getArray();
  }
  if (Array.isArray(features)) {
    return features;
  }

  return null;
};

export const mergeExtent = (features: Feature | Feature[] | Collection<Feature>) => {
  const array = toFeatureArray(features);

  if (array) {
    const extent = createEmpty();

    array.forEach((f) => {
      if (f.getGeometry()) {
        extend(extent, f.getGeometry().getExtent());
      }
    });

    return isEmpty(extent) ? null : extent;
  }
  return null;
};
