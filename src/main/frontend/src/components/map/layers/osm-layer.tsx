import * as React from 'react';

import OpenLayersMap from 'ol/Map';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';

interface OsmLayerProps {
  index?: number;
  map?: OpenLayersMap;
  url: string;
}

/**
 * OSM layer
 *
 * @class OsmLayer
 * @extends {React.Component}
 */
class OsmLayer extends React.Component<OsmLayerProps> {

  private _layer: TileLayer | undefined;

  static defaultProps = {
    url: 'https://{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png',
  }

  get map(): OpenLayersMap | undefined {
    return this.props.map;
  }

  get layer(): TileLayer | undefined {
    return this._layer;
  }

  componentDidMount() {
    const { index = 0, map = null } = this.props;

    if (map) {
      this._layer = new TileLayer({
        source: new OSM({
          url: this.props.url,
        }),
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

export default OsmLayer;
