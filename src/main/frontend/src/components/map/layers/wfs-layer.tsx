import * as React from 'react';

import OpenLayersMap from 'ol/Map';

import VectorSource, { VectorSourceEvent } from 'ol/source/Vector';
import GeoJSON from 'ol/format/GeoJSON';
import VectorLayer from 'ol/layer/Vector';

import { bbox as bboxLoadingStrategy } from 'ol/loadingstrategy';
import { StyleLike } from 'ol/style/Style';

import URI from 'urijs';

interface WfsLayerProps<E> {
  color: string;
  extra: E,
  icon: string;
  index?: number;
  map?: OpenLayersMap;
  maxZoom?: number;
  outputFormat: string;
  srsName: string;
  typename: string;
  url: string;
  version: string;
  style?: StyleLike;
}

/**
 * WFS layer
 *
 * @class WfsLayer
 * @extends {React.Component}
 */
class WfsLayer<E> extends React.Component<WfsLayerProps<E>> {

  private _layer: VectorLayer | undefined;

  static defaultProps = {
    maxZoom: 15,
    outputFormat: 'application/json',
    srsName: 'EPSG:3857',
    version: '1.1.0',
  }

  get map() {
    return this.props.map;
  }

  get layer() {
    return this._layer;
  }

  buildRequest(extent: [number, number, number, number]) {
    const { outputFormat, url, srsName, typename, version } = this.props;
    const typenameParameter = (version.startsWith('2') ? 'typeNames' : 'typeName');

    return URI(url)
      .query({
        service: 'WFS',
        version,
        request: 'GetFeature',
        [typenameParameter]: typename,
        outputFormat,
        srsName,
        bbox: extent.join(',') + ',' + srsName,
      })
      .toString();
  }

  componentDidMount() {
    const { index = 0, extra, map, maxZoom, style } = this.props;

    if (map) {
      const source = new VectorSource({
        format: new GeoJSON(),
        url: this.buildRequest.bind(this),
        strategy: bboxLoadingStrategy,
      });

      source.on('addfeature', (e: VectorSourceEvent) => {
        if (extra) {
          Object.getOwnPropertyNames(extra).forEach((key) => {
            e.feature.set(key, extra[key as keyof E] || null, true);
          });
        }
      });

      this._layer = new VectorLayer({
        source,
        style,
        maxResolution: maxZoom ? map.getView().getResolutionForZoom(maxZoom) : undefined,
      });

      map.getLayers().insertAt(index, this._layer);
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

export default WfsLayer;
