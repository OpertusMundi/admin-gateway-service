
import * as React from 'react';

import Map from 'ol/Map';
import Feature from 'ol/Feature';
import Collection from 'ol/Collection';
import Modify, { ModifyEvent } from 'ol/interaction/Modify';

import { StyleLike } from 'ol/style/Style';

interface ModifyProps {
  // Enable/Disable interaction
  active?: boolean;
  // Feature to edit
  features: Collection<Feature>;
  // Map instance
  map?: Map;
  // Events
  onModifyEnd?: (features: Collection<Feature>) => void;
  // Style
  style?: StyleLike;
}

/**
 * Modify interaction
 *
 * @class ModifyInteraction
 * @extends {React.Component}
 */
class ModifyInteraction extends React.Component<ModifyProps> {

  private interaction: Modify | undefined;

  static defaultProps = {
    active: true,
  }

  private createInteraction() {
    const { active = true, features, map = null, onModifyEnd, style } = this.props;

    this.removeInteraction();

    this.interaction = new Modify({
      features,
      style,
    });

    this.interaction.on('modifyend', (e: ModifyEvent) => {
      if (typeof onModifyEnd === 'function') {
        onModifyEnd(e.features || null);
      }
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

  componentDidUpdate(prevProps: ModifyProps) {
    const {
      active: currActive = true,
      features: currFeatures,
    } = this.props;

    const {
      active: prevActive = true,
      features: prevFeatures,
    } = prevProps;

    if (currActive !== prevActive) {
      this.interaction?.setActive(prevActive);
    }
    if (currFeatures !== prevFeatures) {
      this.createInteraction();
    }
  }

  componentWillUnmount() {
    this.removeInteraction();
  }

  render() {
    return null;
  }

}

export default ModifyInteraction;
