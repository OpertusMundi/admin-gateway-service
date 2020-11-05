import * as React from 'react';

import OpenLayersMap from 'ol/Map';

interface InteractionsProps {
  map?: OpenLayersMap | undefined
}

/**
 * A collection of map interactions
 *
 * @class Interactions
 * @extends {React.Component}
 */
class Interactions extends React.Component<InteractionsProps> {

  render() {
    const { children = null, map = null } = this.props;

    if (map && children) {
      return (
        React.Children.map(children, (child: any) => {
          return React.cloneElement(child, {
            map,
          });
        })
      );
    }

    return null;
  }
}

export default Interactions;
