import BingMapsLayer from './layers/bing-maps-layer';
import Draw from './interactions/draw';
import GeoJsonLayer from './layers/geojson-layer';
import GeoLocation from './geolocation';
import Interactions from './interactions/interactions';
import Layers from './layers/layers';
import Map from './map';
import Modify from './interactions/modify';
import OsmLayer from './layers/osm-layer';
import Overlay from './overlay';
import Select from './interactions/select';
import Snap from './interactions/snap';
import Translate from './interactions/translate';
import VectorTileLayer from './layers/vector-tile-layer';
import WfsLayer from './layers/wfs-layer';
import WmsLayer from './layers/wms-layer';

export { default as Map } from './map';

export default {
  Map,
  Layers,
  Layer: {
    BingMaps: BingMapsLayer,
    GeoJSON: GeoJsonLayer,
    OSM: OsmLayer,
    VectorTileLayer: VectorTileLayer,
    WFS: WfsLayer,
    WMS: WmsLayer,
  },
  Interactions,
  Interaction: {
    Draw,
    Modify,
    Select,
    Snap,
    Translate,
  },
  Overlay,
  GeoLocation,
};
