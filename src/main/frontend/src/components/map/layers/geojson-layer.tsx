import * as React from 'react';

import OpenLayersMap from 'ol/Map';

import { StyleLike } from 'ol/style/Style';
import VectorSource from 'ol/source/Vector';
import GeoJSON from 'ol/format/GeoJSON';
import Feature from 'ol/Feature';
import Collection from 'ol/Collection';
import VectorLayer from 'ol/layer/Vector';

import { FeatureCollection } from 'geojson';

interface GeoJsonLayerProps {
  features: string | FeatureCollection | Feature | Feature[] | Collection<Feature>;
  fitToExtent?: boolean;
  index?: number;
  map?: OpenLayersMap;
  style?: StyleLike
}

/**
 * Vector layer from GeoJSON data
 *
 * @class GeoJsonLayer
 * @extends {React.Component}
 */
class GeoJsonLayer extends React.Component<GeoJsonLayerProps> {

  private _layer: VectorLayer | undefined;

  static defaultProps = {
    fitToExtent: false,
  }

  get map(): OpenLayersMap | undefined {
    return this.props.map;
  }

  get layer(): VectorLayer | undefined {
    return this._layer;
  }

  parseFeatures(features: string | FeatureCollection | Feature | Feature[] | Collection<Feature>, fitToExtent: boolean = false) {
    if (!this._layer) {
      return;
    }

    const source = this._layer.getSource();
    source.clear();

    if (!features) {
      return;
    }

    if (features instanceof Feature) {
      // Add a single OpenLayers feature
      source.addFeature(features);
    } else if (Array.isArray(features)) {
      // Add an array of OpenLayers features
      source.addFeatures(features);
    } else if (features instanceof Collection) {
      // Add an array of OpenLayers features
      source.addFeatures(features.getArray());
    } else if (typeof features === 'string' || typeof features === 'object') {
      // Parse a JSON string or a GeoJSON object
      const format = new GeoJSON();
      source.addFeatures(format.readFeatures(features, {
        featureProjection: 'EPSG:3857',
      }));
    }

    if ((fitToExtent) && (source.getFeatures().length > 0)) {
      this.props.map?.getView().fit(source.getExtent());
    }
  }

  componentDidMount() {
    const { index = 0, map = null, features, fitToExtent = false, style } = this.props;

    if (map) {
      const source = new VectorSource();

      this._layer = new VectorLayer({
        source,
        style,
      });

      this.parseFeatures(features, fitToExtent);

      map.getLayers().insertAt(index, this._layer);
    }
  }

  componentDidUpdate(prevProps: GeoJsonLayerProps) {
    const currProps = this.props;

    if (currProps.features !== prevProps.features) {
      this.parseFeatures(currProps.features, currProps.fitToExtent);
    }
  }

  componentWillUnmount() {
    if ((this.props.map) && (this._layer)) {
      this.props.map.removeLayer(this._layer);

      delete this._layer;
    }
  }

  render() {
    return null;
  }

}

export default GeoJsonLayer;
