import * as React from 'react';

import OpenLayersMap from 'ol/Map';

import { StyleLike } from 'ol/style/Style';
import Select from 'ol/interaction/Select';
import Collection from 'ol/Collection';
import Feature from 'ol/Feature';

import { click as clickCondition, noModifierKeys as noModifierKeysCondition } from 'ol/events/condition';

interface SelectProps {
  active?: boolean;
  hitTolerance?: number;
  map?: OpenLayersMap;
  multi?: boolean;
  features: Collection<Feature>;
  onFeatureSelect?: (features: Feature[]) => void;
  style?: StyleLike;
}

/**
 * Select interaction
 *
 * @class SelectInteraction
 * @extends {React.Component}
 */
class SelectInteraction extends React.Component<SelectProps> {

  private interaction: Select | undefined;

  static defaultProps = {
    active: true,
    hitTolerance: 5,
    multi: true,
  }

  private createInteraction() {
    const { active = true, hitTolerance, map, multi, onFeatureSelect, features, style } = this.props;

    this.removeInteraction();

    this.interaction = new Select({
      condition: (e) => {
        return clickCondition(e) && noModifierKeysCondition(e);
      },
      features,
      multi,
      hitTolerance,
      style,
    });

    this.interaction.on('select', () => {
      if (typeof onFeatureSelect === 'function') {
        if (this.interaction) {
          onFeatureSelect([...this.interaction.getFeatures().getArray()]);
        }
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


  componentDidUpdate(prevProps: SelectProps) {
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

  clear() {
    if (this.interaction) {
      this.interaction.getFeatures().clear();
    }
  }

  render() {
    return null;
  }

}

export default SelectInteraction;
