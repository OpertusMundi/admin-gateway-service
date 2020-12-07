import * as React from 'react';

import { MapOptions } from 'ol/PluggableMap';
import { ViewOptions } from 'ol/View';
import BaseEvent from 'ol/events/Event';
import MapEvent from 'ol/MapEvent';
import MapBrowserEvent from 'ol/MapBrowserEvent';


import OpenLayersMap from 'ol/Map';
import View from 'ol/View';

import { defaults as defaultInteractions } from 'ol/interaction';
import { defaults as defaultControls } from 'ol/control';

interface MapProps {
  altShiftDragRotate?: boolean;
  center?: [number, number];
  className?: string;
  click?: (e: MapBrowserEvent, map: OpenLayersMap) => void;
  defaultCenter?: [number, number];
  defaultZoom?: number;
  doubleClickZoom?: boolean;
  dragPan?: boolean;
  extent?: [number, number, number, number];
  fitToExtent?: boolean;
  height?: number | string;
  keyboard?: boolean;
  maxZoom?: number;
  minZoom?: number;
  moveEnd?: (data: { center: number[], zoom: number | undefined }) => void;
  mouseWheelZoom?: boolean;
  onFocusOnly?: boolean;
  pinchRotate?: boolean;
  pinchZoom?: boolean;
  pointerMove?: (e: MapBrowserEvent, map: OpenLayersMap) => void;
  shiftDragZoom?: boolean;
  width?: number | string;
  zoom?: number;
}

interface MapState {
  map: OpenLayersMap | null,
};

/**
 * A wrapper component for {@link OpenLayers.Map}.
 *
 * @class Map
 * @extends {React.Component}
 */
class Map extends React.Component<MapProps> {

  private elementRef: React.RefObject<HTMLDivElement> | null;

  constructor(props: MapProps) {
    super(props);

    this.elementRef = React.createRef<HTMLDivElement>();
  }

  state: MapState = {
    map: null,
  }

  static defaultProps = {
    altShiftDragRotate: true,
    center: [0, 0],
    doubleClickZoom: true,
    dragPan: true,
    fitToExtent: false,
    height: '600px',
    keyboard: true,
    maxZoom: 19,
    minZoom: 1,
    mouseWheelZoom: true,
    onFocusOnly: false,
    pinchRotate: true,
    pinchZoom: true,
    shiftDragZoom: true,
    width: '100%',
    zoom: 12,
  }

  resize() {
    const { map } = this.state;

    if (map) {
      const container = map.getTargetElement();
      if (container) {
        // Set the map div to scroll so the map does not overflow
        container.style.overflow = 'scroll';
        // Call updateSize -- because the container is no longer overflowing the size is correct
        map.updateSize();
        // Hide the scrollbars
        container.style.overflow = 'hidden';
      }
    }
  }

  componentDidMount() {
    const {
      altShiftDragRotate,
      center,
      defaultCenter,
      defaultZoom,
      doubleClickZoom,
      dragPan,
      extent = null,
      fitToExtent,
      keyboard,
      maxZoom,
      minZoom,
      mouseWheelZoom,
      onFocusOnly,
      pinchRotate,
      pinchZoom,
      shiftDragZoom,
      zoom,
    } = this.props;

    const interactions = defaultInteractions({
      altShiftDragRotate,
      onFocusOnly,
      doubleClickZoom,
      keyboard,
      mouseWheelZoom,
      shiftDragZoom,
      dragPan,
      pinchRotate,
      pinchZoom,
    });

    const controls = defaultControls({
    });

    const mapOptions: MapOptions = {
      controls,
      interactions,
      layers: [],
      target: this.elementRef?.current as HTMLElement,
    };

    const viewOptions: ViewOptions = {
      center: defaultCenter || center || [0, 0],
      maxZoom,
      minZoom,
      zoom: defaultZoom || zoom,
      projection: 'EPSG:3857',
    };

    if (extent) {
      viewOptions.extent = extent;
    }

    const map = new OpenLayersMap({
      ...mapOptions,
      view: new View({
        ...viewOptions
      }),
    });

    if (extent && fitToExtent) {
      map.getView().fit(extent);
    }

    if (typeof this.props.moveEnd === 'function') {
      map.on('moveend', (e: MapEvent) => {
        const data = {
          center: e.map.getView().getCenter() as number[],
          zoom: e.map.getView().getZoom(),
        };
        if (this.props.moveEnd) {
          this.props.moveEnd(data);
        }
      });
    }

    if (typeof this.props.pointerMove === 'function') {
      map.on(['pointermove'], (e: BaseEvent) => {
        if (this.props.pointerMove) {
          this.props.pointerMove(e as MapBrowserEvent, map);
        }
      });
    }

    if (typeof this.props.click === 'function') {
      map.on(['click'], (e: BaseEvent) => {
        if (this.props.click) {
          this.props.click(e as MapBrowserEvent, map);
        }
      });
    }

    this.setState({ map });
    this.resize();
  }

  componentDidUpdate(prevProps: MapProps) {
    const { map } = this.state;
    const { center = [], zoom } = this.props;

    if (map && prevProps.center && (prevProps.center[0] !== center[0] || prevProps.center[1] !== center[1])) {
      map.getView().setCenter(center);
    }
    if (map && prevProps.zoom && prevProps.zoom !== zoom) {
      map.getView().setZoom(zoom || Map.defaultProps.zoom);
    }

    this.resize();
  }

  componentWillUnmount() {
    if (this.state.map) {
      this.state.map.setTarget(undefined);
      this.setState({ map: null });
    }
  }

  moveTo(center: [number, number] | number[], zoom: number | null = null, duration: number = 1500) {
    const { map } = this.state;
    if (map) {
      const view = map.getView();
      view.animate({
        center,
        zoom: zoom ? zoom : view.getZoom(),
        duration,
      });
    }
  }

  get center(): number[] | null {
    const { map } = this.state;
    if (map) {
      return map.getView().getCenter() || null;
    }
    return null;
  }

  get zoom(): number | null {
    const { map } = this.state;
    if (map) {
      return map.getView().getZoom() || null;
    }
    return null;
  }

  render() {
    const { map } = this.state;
    const { className, children, width, height } = this.props;

    return (
      <div className={className || 'app-map-container'} style={{ minWidth: width || '100%', height, }} ref={this.elementRef}>
        {map &&
          React.Children.map(children, (child: any) => {
            return React.cloneElement(child, {
              map,
            });
          })
        }
      </div>
    );
  }
}

export default Map;
