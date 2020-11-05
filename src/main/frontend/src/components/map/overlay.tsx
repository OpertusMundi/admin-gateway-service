import _ from 'lodash';
import * as React from 'react';
import * as ReactDOM from 'react-dom';

import OpenLayersMap from 'ol/Map';
import Overlay from 'ol/Overlay';

interface ReactOverlayProps {
  autoPan: boolean;
  closable: boolean;
  id: string;
  map?: OpenLayersMap;
  position: number[] | undefined;
  close?: (position: number[] | undefined) => void;
  width?: string;
}

class ReactOverlay extends React.Component<ReactOverlayProps> {

  private id: string;

  private element: Element | undefined;

  private elementId: string;

  private overlay: Overlay | undefined;

  constructor(props: ReactOverlayProps) {
    super(props);

    this.id = props.id;
    this.elementId = _.uniqueId('overlay-');

    // Create DOM element for hosting the overlay content
    this.element = document.createElement('div');
    this.element.id = this.elementId;
    document.body.appendChild(this.element);
  }

  static defaultProps = {
    autoPan: false,
    closable: true,
  }

  componentDidMount() {
    const { autoPan, map = null, position } = this.props;

    if (map) {
      this.overlay = new Overlay({
        autoPan,
        autoPanAnimation: {
          duration: 250
        },
        element: this.element as HTMLElement,
        id: this.id,
        position,
      });

      map.addOverlay(this.overlay);
    }
  }

  componentDidUpdate(prevProps: ReactOverlayProps) {
    const { position: currPosition } = this.props;
    const { position: prevPosition } = prevProps;

    if (!_.isEqual(currPosition, prevPosition)) {
      this.overlay?.setPosition(currPosition);
    }
  }

  componentWillUnmount() {
    if ((this.props.map) && (this.overlay)) {
      this.props.map.removeOverlay(this.overlay);

      // TODO: Find workaround
      // document.removeChild(this._element);

      this.overlay = undefined;
      this.element = undefined;
    }
  }

  close(event: React.MouseEvent) {
    event.preventDefault();

    const position = this.overlay?.getPosition();

    this.overlay?.setPosition(undefined);

    if (this.props.close) {
      this.props.close(position);
    }
  }

  render() {
    const { width } = this.props;
    const style: React.CSSProperties = {};

    if (width) {
      style.minWidth = this.props.width;
    }

    return ReactDOM.createPortal(
      <div
        id={`child-${this.elementId}`}
        className="app-map-popup"
        style={style}
      >
        {this.props.closable && this.props.close &&
          <a id={`${this.id}-popup-closer`} className="app-map-popup-closer" onClick={(e: React.MouseEvent) => { this.close(e); e.preventDefault(); }} href="/">
          </a>
        }
        <div id={`${this.id}-popup-content`}>
          {this.props.children || null}
        </div>
      </div>,
      this.element as HTMLElement
    );
  }
}

export default ReactOverlay;
