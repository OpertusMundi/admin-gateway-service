import React from 'react';

// Icons
import Icon from '@mdi/react';
import { mdiTrashCanOutline, mdiRestore, mdiMapMarkerOutline } from '@mdi/js';

// OpenLayers
import Collection from 'ol/Collection';
import Style from 'ol/style/Style';
import Stroke from 'ol/style/Stroke';
import Fill from 'ol/style/Fill';
import Circle from 'ol/style/Circle';
import Feature from 'ol/Feature';
import GeometryType from 'ol/geom/GeometryType';
import Geometry from 'ol/geom/Geometry';
import GeoJSON, { GeoJSONGeometry } from 'ol/format/GeoJSON';

// Components
import OpenLayers, { Map } from 'components/map';

// Services
import message from 'service/message';

// Styles
const defaultStyle = new Style({
  image: new Circle({
    radius: 5,
    stroke: new Stroke({
      color: [27, 94, 32, 0.8],
      width: 3
    }),
    fill: new Fill({
      color: [46, 125, 50, 0.5],
    }),
  }),
  stroke: new Stroke({
    color: [27, 94, 32, 0.8],
    width: 3
  }),
  fill: new Fill({
    color: [46, 125, 50, 0.5],
  })
});

const drawStyle = new Style({
  image: new Circle({
    radius: 5,
    stroke: new Stroke({
      color: [33, 33, 33, 0.8],
      width: 1
    }),
    fill: new Fill({
      color: [158, 158, 158, 0.5],
    }),
  }),
  stroke: new Stroke({
    color: [33, 33, 33, 0.8],
    width: 1
  }),
  fill: new Fill({
    color: [158, 158, 158, 0.5],
  })
});

const geometryToFeature = (geometry: Geometry, id = -1) => {
  const feature = new Feature({
    id,
    geometry,
  });

  return feature;
};

interface GeometryEditorState {
  initial: Feature | null;
  features: Collection<Feature>;
}


interface GeometryEditorProps {
  geometry?: GeoJSONGeometry | null;
  onChange?: (geometry: GeoJSONGeometry | null) => void;
  readOnly?: boolean;
}

class GeometryEditor extends React.Component<GeometryEditorProps, GeometryEditorState> {

  mapRef: React.RefObject<Map>;

  state: GeometryEditorState = {
    initial: null,
    features: new Collection<Feature>(),
  }

  constructor(props: GeometryEditorProps) {
    super(props);

    this.mapRef = React.createRef<Map>();
  }

  componentDidMount() {
    const { geometry: geometryObject } = this.props;

    if (geometryObject) {
      const format = new GeoJSON();

      const geometry = format.readGeometry(geometryObject, {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857',
      });

      const feature = geometryToFeature(geometry);

      this.setState({
        initial: feature.clone(),
        features: new Collection<Feature>([feature]),
      });
    }
  }

  onFeatureChange(feature: Feature): void {
    const { onChange } = this.props;

    if (onChange && feature?.getGeometry()) {
      const format = new GeoJSON();

      const geojson = format.writeGeometryObject(feature.getGeometry(), {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857',
      });

      onChange(geojson);
    }
  }

  onDeleteGeometry(): void {
    this.setState({
      features: new Collection<Feature>(),
    });
  }

  onRestoreGeometry(): void {
    const { initial } = this.state;
    if (initial) {
      this.setState({
        features: new Collection<Feature>([initial.clone()]),
      });
    }
  }

  onLocationChange(position: number[] | undefined): void {
  }

  onLocationError(id: string): void {
    message.error(id, () => (<Icon path={mdiMapMarkerOutline} size="3rem" />));
  }

  render() {
    const { features, initial } = this.state;
    const { children, readOnly = false } = this.props;

    return (
      <div style={{ position: 'relative' }}>
        {!readOnly &&
          <>
            <div
              className="map-button"
              style={{
                top: 40,
              }}
              onClick={() => this.onDeleteGeometry()}>
              <Icon path={mdiTrashCanOutline} size="1.2rem" />
            </div>
            {initial &&
              <div
                className="map-button"
                style={{
                  top: 75,
                }}
                onClick={() => this.onRestoreGeometry()}>
                <Icon path={mdiRestore} size="1.2rem" />
              </div>
            }

          </>
        }
        <OpenLayers.Map
          center={[2522457.20, 4743383.34]}
          extent={[2023227.29, 4023104.66, 3569089.78, 5482314.24]}
          maxZoom={19}
          minZoom={6}
          zoom={6}
          height={'500px'}
          ref={this.mapRef}
          mouseWheelZoom={false}
        >
          <OpenLayers.Layers>
            {children}
            <OpenLayers.Layer.GeoJSON
              features={features}
              style={defaultStyle}
              fitToExtent={true}
            />
          </OpenLayers.Layers>
          <OpenLayers.Interactions>
            <OpenLayers.Interaction.Draw
              active={this.state.features.getLength() === 0}
              onDrawStart={(feature: Feature): void => {
                // Single feature
                this.setState({
                  features: new Collection<Feature>(),
                });
              }}
              onDrawEnd={(feature: Feature): void => {
                this.setState((state) => ({
                  features: new Collection<Feature>([...state.features.getArray(), feature]),
                }));
                this.onFeatureChange(feature);
              }}
              style={drawStyle}
              type={GeometryType.POLYGON}
            />
            <OpenLayers.Interaction.Modify
              active={this.state.features.getLength() !== 0}
              features={features}
              style={drawStyle}
              onModifyEnd={(features: Collection<Feature>): void => {
                if (features.getLength() === 1) {
                  this.onFeatureChange(features.item(0));
                }
              }}
            />
            <OpenLayers.Interaction.Snap
              features={features}
              pixelTolerance={5}
            />
          </OpenLayers.Interactions>
          <OpenLayers.GeoLocation
            autoPan={true}
            autoZoom={18}
            positionChange={(position: number[] | undefined) => this.onLocationChange(position)}
            positionError={(id: string) => this.onLocationError(id)}
            style={{
              top: 5,
            }}
          >
          </OpenLayers.GeoLocation>
        </OpenLayers.Map>
      </div >
    );
  }
}

export default GeometryEditor;