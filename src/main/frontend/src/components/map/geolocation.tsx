import * as React from 'react';

// OpenLayers
import OpenLayersMap from 'ol/Map';
import Geolocation from 'ol/Geolocation';

// Icons
import Icon from '@mdi/react';
import { mdiCrosshairsGps } from '@mdi/js';

interface ReactGeolocationProps {
  animateDuration?: number;
  autoPan?: boolean;
  autoZoom?: number;
  map?: OpenLayersMap;
  positionChange?: (position: number[] | undefined) => void;
  positionError?: (id: string) => void;
  style?: React.CSSProperties;
}

class ReactGeolocation extends React.Component<ReactGeolocationProps> {

  geolocation: Geolocation | null = null;

  static defaultProps = {
    animateDuration: 1500,
    autoPan: true,
    autoZoom: 16,
  }

  findLocation(): void {
    if (this.geolocation) {
      this.geolocation.setTracking(true);
    }
  }

  componentDidMount() {
    const { animateDuration, autoPan, autoZoom, map = null, positionChange, positionError } = this.props;

    if (map) {
      // Initialize geolocation service
      this.geolocation = new Geolocation({
        tracking: false,
        trackingOptions: {
          enableHighAccuracy: true,
        },
        projection: 'EPSG:3857',
      });

      // Add event handlers
      this.geolocation.on('change', (): void => {
        const position = this.geolocation?.getPosition();

        this.geolocation?.setTracking(false);

        if (autoPan && position) {
          setTimeout(() => {
            map.getView().animate({
              center: position,
              zoom: autoZoom,
              duration: animateDuration
            });
          }, 0);
        }

        if (positionChange) {
          positionChange(position);
        }
      });

      this.geolocation.on('error', (error: any): void => {
        this.geolocation?.setTracking(false);

        if (positionError) {
          if (error.message === 'User denied Geolocation') {
            positionError('error.geolocation-cancel');
          } else {
            positionError('error.geolocation-error');
          }
        }
      });
    }
  }

  componentWillUnmount() {
    if (this.geolocation) {
      this.geolocation.dispose();
      this.geolocation = null;
    }
  }

  render() {
    const { style } = this.props;

    return (
      <div
        className="map-button"
        style={style}
        onClick={() => this.findLocation()}>
        <Icon path={mdiCrosshairsGps} size="1.2rem" />
      </div>
    );
  }
}

export default ReactGeolocation;
