import React from 'react';

// Localization
import { injectIntl, IntlShape } from 'react-intl';

// Styles
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

// Material UI
import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';

// DnD support
import {
  DropTarget,
  DropTargetMonitor,
  DropTargetConnector,
  ConnectDropTarget,
} from 'react-dnd'

// Custom components
import SectionComponent from 'components/contract/form/section';

// Model
import { ContractItemTypes } from 'model/contract';

// Styles
const styles = (theme: Theme) => createStyles({
  paper: {
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
    borderRadius: 0,
    margin: theme.spacing(2),
    minHeight: '70vh',
    width: '40vw',
  },
});

interface PageComponentProps extends WithStyles<typeof styles> {
  intl: IntlShape;
  sections: number;
  addSection: (item: any) => void;
  // DnD collected properties
  isOver: boolean
  canDrop: boolean
  connectDropTarget: ConnectDropTarget
}

interface PageComponentState {
}

class PageComponent extends React.Component<PageComponentProps, PageComponentState> {

  render() {
    const { classes, connectDropTarget, sections: total } = this.props;

    const sections = [];
    for (let i = 0; i < total; i++) {
      sections.push(
        <Grid container item xs={12} key={`section-${i}`}>
          <SectionComponent index={i} />
        </Grid>
      );
    }
    return connectDropTarget(
      <div>
        <Grid container item xs={12}>
          <Paper className={classes.paper}>
            <Grid container item xs={12}>
              {sections}
            </Grid>
          </Paper>
        </Grid>
      </div>
    );
  }

}

// Apply styles
const styledComponent = withStyles(styles)(PageComponent);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Export localized component
const dropTargetComponent = DropTarget(
  [ContractItemTypes.Section],
  {
    canDrop: (props: PageComponentProps) => true,
    drop: (props: PageComponentProps, monitor: DropTargetMonitor, component: PageComponent) => props.addSection(monitor.getItem()),
  },
  (connect: DropTargetConnector, monitor: DropTargetMonitor) => {
    return {
      connectDropTarget: connect.dropTarget(),
      isOver: !!monitor.isOver(),
      canDrop: !!monitor.canDrop(),
    }
  },
)(localizedComponent);

export default dropTargetComponent;