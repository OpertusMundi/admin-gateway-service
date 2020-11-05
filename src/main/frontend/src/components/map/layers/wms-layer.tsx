import * as React from 'react';

import semver from 'semver';

import OpenLayersMap from 'ol/Map';

import TileWMS from 'ol/source/TileWMS';

import TileLayer from 'ol/layer/Tile';

interface WmsProps {
  crs: string;
  format: string;
  height: number;
  index?: number;
  layers: string;
  map?: OpenLayersMap;
  opacity?: number;
  styles?: string;
  url: string;
  version: string;
  width: number;
  time?: Date | undefined;
  transparent?: boolean;
}
/**
 * WMS layer
 *
 * @class WmsLayer
 * @extends {React.Component}
 */
class WmsLayer extends React.Component<WmsProps> {

  private _layer: TileLayer | undefined;

  static defaultProps = {
    crs: 'EPSG:3857',
    format: 'image/png',
    height: 256,
    opacity: 1,
    version: '1.3.0',
    width: 256,
    transparent: true,
    styles: '',
  }

  get map() {
    return this.props.map;
  }

  get layer(): TileLayer | undefined {
    return this._layer;
  }

  componentDidMount() {
    const {
      crs, format, height, index = 0, layers, opacity, styles = '', time = null, transparent, url, version, width
    } = this.props;

    if (!semver.valid(version)) {
      console.warn(`WMS version ${version} is not valid`);
    }
    const crsParam = semver.lt(version, '1.3.0') ? 'SRS' : 'CRS';


    if (this.props.map) {
      const params = {
        'LAYERS': layers,
        'VERSION': version,
        'TILED': true,
        'STYLES': styles || '',
        'WIDTH': width,
        'HEIGHT': height,
        'FORMAT': format,
        'TRANSPARENT': transparent,
        [crsParam]: crs,
      };

      if (time) {
        params['TIME'] = time.toISOString();
      }

      this._layer = new TileLayer({
        opacity: opacity,
        source: new TileWMS({
          url: url,
          params,
          transition: 0,
        }),
      });

      this.props.map.getLayers().insertAt(index, this._layer);
    }
  }

  componentDidUpdate(prevProps: WmsProps) {
    if (this._layer && prevProps.time && prevProps.time !== this.props.time) {
      (this._layer.getSource() as TileWMS).updateParams({ 'TIME': this.props.time?.toISOString() });
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

export default WmsLayer;