import * as React from 'react';

import OpenLayersMap from 'ol/Map';

interface LayersProps {
  map?: OpenLayersMap | undefined
}

/**
 * A collection of map layers
 *
 * @class Layers
 * @extends {React.Component}
 */
class Layers extends React.Component<LayersProps> {

  render() {
    const { map = null, children = null } = this.props;

    if (!map) {
      return null;
    }

    if (!children) {
      return null;
    }

    return (
      React.Children.map(children, (child: any, index: number) => {
        return React.cloneElement(child, {
          map,
          index,
        });
      })
    );
  }
}

export default Layers;
