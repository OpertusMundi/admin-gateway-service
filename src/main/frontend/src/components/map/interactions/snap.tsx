
import * as React from 'react';

import Map from 'ol/Map';
import Feature from 'ol/Feature';
import Collection from 'ol/Collection';
import Snap from 'ol/interaction/Snap';

interface SnapProps {
  // Enable/Disable interaction
  active?: boolean;
  // Feature to edit
  features: Collection<Feature>;
  // Map instance
  map?: Map;
  // True if Translate interaction is enabled
  translate?: boolean;
  // Pixel tolerance for considering the pointer close enough to a segment or vertex for snapping.
  pixelTolerance?: number;
}

/**
 * Modify interaction
 *
 * @class SnapInteraction
 * @extends {React.Component}
 */
class SnapInteraction extends React.Component<SnapProps> {

  private interaction: Snap | undefined;

  static defaultProps = {
    active: true,
  }

  private createInteraction() {
    const { active = true, features, map = null, pixelTolerance } = this.props;

    this.removeInteraction();

    this.interaction = new Snap({
      features,
      pixelTolerance,
    });

    if (map) {
      this.interaction.setActive(active);
      map.addInteraction(this.interaction);
    }
  }

  private removeInteraction() {
    const { map } = this.props;

    if (map) {
      if (this.interaction) {
        map.removeInteraction(this.interaction);
      }
      this.interaction = undefined;
    }
  }

  componentDidMount() {
    const { map = null } = this.props;

    // Wait for map instance to initialize
    if (!map) {
      return;
    }

    this.createInteraction();
  }

  componentDidUpdate(prevProps: SnapProps) {
    const {
      active: currActive = true,
      features: currFeatures,
    } = this.props;

    const {
      active: prevActive = true,
      features: prevFeatures,
    } = prevProps;

    if (currActive !== prevActive) {
      this.interaction?.setActive(currActive);
    }
    if (currFeatures !== prevFeatures) {
      this.createInteraction()
    }
  }

  componentWillUnmount() {
    this.removeInteraction();
  }

  render() {
    return null;
  }

}

export default SnapInteraction;
