import React from 'react';

// Localization
import { injectIntl, IntlShape } from 'react-intl';

// Styles
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

// DnD support
import {
  DragSource,
  ConnectDragSource,
  DragSourceConnector,
  DragSourceMonitor,
} from 'react-dnd'

// Icons
import Icon from '@mdi/react';
import { mdiTextBoxOutline } from '@mdi/js';

// Model
import { ContractItemTypes } from 'model/contract';

const styles = (theme: Theme) => createStyles({
});

interface ItemComponentProps extends WithStyles<typeof styles> {
  intl: IntlShape;
  // DnD collected properties
  connectDragSource: ConnectDragSource;
  isDragging?: boolean
}

interface ItemComponentState {
}

class ItemComponent extends React.Component<ItemComponentProps, ItemComponentState> {

  render() {
    const { connectDragSource } = this.props;

    return (
      <div ref={connectDragSource}>
        <Icon path={mdiTextBoxOutline} size="4rem" />
      </div>
    );
  }
}

// Apply styles
const styledComponent = withStyles(styles)(ItemComponent);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Create draggable component
const DraggableComponent = DragSource(
  ContractItemTypes.Section,
  {
    beginDrag: () => ({}),
  },
  (connect: DragSourceConnector, monitor: DragSourceMonitor) => ({
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging(),
  }),
)(localizedComponent);

// Export draggable component
export default DraggableComponent;
