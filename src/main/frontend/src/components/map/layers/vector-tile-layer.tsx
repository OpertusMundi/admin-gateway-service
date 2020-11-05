import * as React from 'react';

import OpenLayersMap from 'ol/Map';

import { StyleLike } from 'ol/style/Style';

import VectorTileLayer from 'ol/layer/VectorTile';
import VectorTileSource from 'ol/source/VectorTile';
import MVT from 'ol/format/MVT';

interface VectorTileLayerProps {
  declutter?: boolean;
  index?: number;
  map?: OpenLayersMap;
  style: StyleLike,
  url: string;
  opacity?: number;
}
class Layer extends React.Component<VectorTileLayerProps> {

  private _layer: VectorTileLayer | undefined;

  static defaultProps = {
    declutter: false,
    opacity: 1,
  }

  get map(): OpenLayersMap | undefined {
    return this.props.map;
  }

  get layer(): VectorTileLayer | undefined {
    return this._layer;
  }

  componentDidMount() {
    const { map = null, url, style, declutter, opacity, index = 0 } = this.props;

    if (map) {
      const source = new VectorTileSource({
        format: new MVT(),
        maxZoom: map.getView().getMaxZoom(),
        minZoom: map.getView().getMinZoom(),
        url,
      });

      this._layer = new VectorTileLayer({
        declutter,
        source,
        style,
        opacity,
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

export default Layer;
