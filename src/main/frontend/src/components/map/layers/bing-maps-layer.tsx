import * as React from 'react';

import OpenLayersMap from 'ol/Map';
import TileLayer from 'ol/layer/Tile';
import BingMaps from 'ol/source/BingMaps';

interface BingMapsLayerProps {
  applicationKey?: string;
  imagerySet: string;
  index?: number;
  map?: OpenLayersMap;
}

/**
 * Microsoft Bing Maps layer
 *
 * @class BingMapsLayer
 * @extends {React.Component}
 */
class BingMapsLayer extends React.Component<BingMapsLayerProps> {

  private _layer: TileLayer | undefined;

  static defaultProps = {
    imagerySet: 'Aerial',
  }

  get map(): OpenLayersMap | undefined {
    return this.props.map;
  }

  get layer(): TileLayer | undefined {
    return this._layer;
  }

  componentDidMount() {
    const { index = 0, map = null, applicationKey, imagerySet } = this.props;

    if (map) {
      if (!applicationKey) {
        return;
      }
      this._layer = new TileLayer({
        source: new BingMaps({
          key: applicationKey,
          imagerySet: imagerySet || 'Aerial',
        })
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

export default BingMapsLayer;
