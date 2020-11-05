
import * as React from 'react';

import Map from 'ol/Map';
import Feature from 'ol/Feature';
import Collection from 'ol/Collection';
import Translate from 'ol/interaction/Translate';

interface TranslateProps {
  // Enable/Disable interaction
  active?: boolean;
  // Feature to edit
  features: Collection<Feature>;
  // Map instance
  map?: Map;
}

/**
 * Modify interaction
 *
 * @class TranslateInteraction
 * @extends {React.Component}
 */
class TranslateInteraction extends React.Component<TranslateProps> {

  private interaction: Translate | undefined;

  static defaultProps = {
    active: true,
  }

  private createInteraction() {
    const { active = true, features, map = null } = this.props;

    this.removeInteraction();

    this.interaction = new Translate({
      features,
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

  componentDidUpdate(prevProps: TranslateProps) {
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

export default TranslateInteraction;
